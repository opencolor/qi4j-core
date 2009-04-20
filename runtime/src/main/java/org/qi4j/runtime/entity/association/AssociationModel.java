/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.entity.association;

import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.common.TypeName;
import org.qi4j.api.composite.Composite;
import org.qi4j.api.constraint.ConstraintViolation;
import org.qi4j.api.constraint.ConstraintViolationException;
import org.qi4j.api.entity.Aggregated;
import org.qi4j.api.entity.Queryable;
import org.qi4j.api.entity.RDF;
import org.qi4j.api.entity.association.AbstractAssociation;
import org.qi4j.api.entity.association.Association;
import org.qi4j.api.entity.association.GenericAssociationInfo;
import org.qi4j.api.entity.association.ManyAssociation;
import org.qi4j.api.property.Immutable;
import org.qi4j.api.util.SerializationUtil;
import org.qi4j.runtime.composite.ValueConstraintsInstance;
import org.qi4j.runtime.structure.ModuleUnitOfWork;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.association.AssociationDescriptor;
import org.qi4j.spi.entity.association.AssociationType;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * JAVADOC
 */
public final class AssociationModel
    implements AssociationDescriptor, Serializable
{
    private MetaInfo metaInfo;
    private Type type;
    private Method accessor;
    private QualifiedName qualifiedName;
    private String rdf;
    private ValueConstraintsInstance constraints;
    private ValueConstraintsInstance associationConstraints;
    private boolean queryable;
    private boolean immutable;
    private boolean aggregated;
    private AssociationType associationType;

    private void writeObject( ObjectOutputStream out )
        throws IOException
    {
        try
        {
            out.writeObject( metaInfo );
            SerializationUtil.writeMethod( out, accessor );
            out.writeObject( constraints );
        }
        catch( NotSerializableException e )
        {
            System.err.println( "NotSerializable in " + getClass() );
            throw e;
        }
    }

    private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException
    {
        metaInfo = (MetaInfo) in.readObject();
        accessor = SerializationUtil.readMethod( in );
        constraints = (ValueConstraintsInstance) in.readObject();
        initialize();
    }


    public AssociationModel( Method accessor, ValueConstraintsInstance valueConstraintsInstance, ValueConstraintsInstance associationConstraintsInstance, MetaInfo metaInfo )
    {
        this.metaInfo = metaInfo;
        this.constraints = valueConstraintsInstance;
        this.associationConstraints = associationConstraintsInstance;
        this.accessor = accessor;
        initialize();
        this.associationType = new AssociationType( qualifiedName, TypeName.nameOf( type ), rdf, queryable );
    }

    private void initialize()
    {
        this.type = GenericAssociationInfo.getAssociationType( accessor );
        this.qualifiedName = QualifiedName.fromMethod( accessor );
        this.immutable = metaInfo.get( Immutable.class ) != null;
        this.aggregated = metaInfo.get( Aggregated.class ) != null;
        RDF uriAnnotation = accessor().getAnnotation( RDF.class );
        this.rdf = uriAnnotation == null ? null : uriAnnotation.value();

        final Queryable queryable = accessor.getAnnotation( Queryable.class );
        this.queryable = queryable == null || queryable.value();
    }

    public <T> T metaInfo( Class<T> infoType )
    {
        return metaInfo.get( infoType );
    }

    public QualifiedName qualifiedName()
    {
        return qualifiedName;
    }

    public Type type()
    {
        return type;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isAggregated()
    {
        return aggregated;
    }

    public Method accessor()
    {
        return accessor;
    }

    public boolean isManyAssociation()
    {
        return ManyAssociation.class.isAssignableFrom( accessor.getReturnType() );
    }

    public boolean isAssociation()
    {
        return Association.class.isAssignableFrom( accessor.getReturnType() );
    }

    public AbstractAssociation newDefaultInstance( ModuleUnitOfWork uow, EntityState entityState )
    {
        AbstractAssociation instance;
        if( isManyAssociation() )
        {
            instance = new ManyAssociationInstance( this, uow, entityState );
        }
        else
        {
            instance = new AssociationInstance<Object>( this, uow, entityState );
        }
        return instance;
    }

    public <T> Association<T> newInstance( ModuleUnitOfWork uow, EntityState state )
    {
        Association<T> associationInstance = new AssociationInstance<T>( this, uow, state );

        if( Composite.class.isAssignableFrom( accessor.getReturnType() ) )
        {
            associationInstance = (Association<T>) uow.module().compositeBuilderFactory().newCompositeBuilder( accessor.getReturnType() ).use( associationInstance ).newInstance();
        }

        return associationInstance;
    }

    public void checkConstraints( Object value )
        throws ConstraintViolationException
    {
        if( constraints != null )
        {
            List<ConstraintViolation> violations = constraints.checkConstraints( value );
            if( !violations.isEmpty() )
            {
                throw new ConstraintViolationException( accessor, violations );
            }
        }
    }

    public void checkConstraints( EntityAssociationsInstance associations )
        throws ConstraintViolationException
    {
        if( constraints != null )
        {
            Object value = associations.associationFor( accessor ).get();
            checkConstraints( value );
        }
    }

    public void checkAssociationConstraints( EntityAssociationsInstance associationsInstance )
        throws ConstraintViolationException
    {
        if( associationConstraints != null )
        {
            Association association = associationsInstance.associationFor( accessor );

            List<ConstraintViolation> violations = associationConstraints.checkConstraints( association );
            if( !violations.isEmpty() )
            {
                throw new ConstraintViolationException( accessor, violations );
            }
        }
    }


    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        AssociationModel that = (AssociationModel) o;

        if( !accessor.equals( that.accessor ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return accessor.hashCode();
    }

    @Override public String toString()
    {
        return accessor.toGenericString();
    }

    public AssociationType associationType()
    {
        return associationType;
    }
}
