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

package org.qi4j.runtime.entity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.qi4j.composite.Composite;
import org.qi4j.composite.ConstraintViolationException;
import org.qi4j.composite.State;
import org.qi4j.entity.EntityComposite;
import org.qi4j.entity.EntityCompositeAlreadyExistsException;
import org.qi4j.runtime.composite.BindingException;
import org.qi4j.runtime.composite.qi.CompositeMethodInstance;
import org.qi4j.runtime.composite.qi.CompositeMethodsModel;
import org.qi4j.runtime.composite.qi.CompositeModel;
import org.qi4j.runtime.composite.qi.ConcernsModel;
import org.qi4j.runtime.composite.qi.ConstraintsModel;
import org.qi4j.runtime.composite.qi.MixinsInstance;
import org.qi4j.runtime.composite.qi.Resolution;
import org.qi4j.runtime.composite.qi.SideEffectsModel;
import org.qi4j.runtime.entity.association.AssociationsModel;
import org.qi4j.runtime.injection.DependencyVisitor;
import org.qi4j.runtime.structure.Binder;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.runtime.structure.ModuleModel;
import org.qi4j.spi.composite.CompositeDescriptor;
import org.qi4j.spi.composite.StateDescriptor;
import org.qi4j.spi.entity.EntityAlreadyExistsException;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.EntityStatus;
import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.entity.EntityStoreException;
import org.qi4j.spi.entity.QualifiedIdentity;
import org.qi4j.structure.Visibility;
import org.qi4j.util.MetaInfo;

/**
 * TODO
 */
public class EntityModel
    implements Binder, CompositeDescriptor
{
    public static EntityModel newModel( Class<? extends EntityComposite> type, Visibility visibility, MetaInfo info, ModuleModel moduleModel )
    {
        ConstraintsModel constraintsModel = new ConstraintsModel( type );
        EntityMixinsModel mixinsModel = new EntityMixinsModel( type );
        EntityPropertiesModel entityPropertiesModel = new EntityPropertiesModel( constraintsModel );
        AssociationsModel associationsModel = new AssociationsModel( constraintsModel );
        EntityStateModel stateModel = new EntityStateModel( entityPropertiesModel, associationsModel );
        ConcernsModel concernsModel = new ConcernsModel( type );
        SideEffectsModel sideEffectsModel = new SideEffectsModel( type );
        CompositeMethodsModel compositeMethodsModel = new CompositeMethodsModel( type,
                                                                                 constraintsModel,
                                                                                 concernsModel,
                                                                                 sideEffectsModel,
                                                                                 mixinsModel );

        return new EntityModel( type,
                                visibility,
                                info,
                                moduleModel,
                                constraintsModel,
                                concernsModel,
                                sideEffectsModel,
                                mixinsModel,
                                stateModel,
                                compositeMethodsModel );
    }

    private Class<? extends EntityComposite> type;
    private Visibility visibility;
    private MetaInfo info;
    private ModuleModel moduleModel;
    private ConstraintsModel constraintsModel;
    private ConcernsModel concernsModel;
    private SideEffectsModel sideEffectsModel;
    private EntityMixinsModel mixinsModel;
    private EntityStateModel stateModel;
    private CompositeMethodsModel compositeMethodsModel;

    private Class<? extends Composite> proxyClass;

    public EntityModel( Class<? extends EntityComposite> type,
                        Visibility visibility,
                        MetaInfo info,
                        ModuleModel moduleModel,
                        ConstraintsModel constraintsModel,
                        ConcernsModel concernsModel,
                        SideEffectsModel sideEffectsModel,
                        EntityMixinsModel mixinsModel,
                        EntityStateModel stateModel,
                        CompositeMethodsModel compositeMethodsModel
    )
    {
        this.type = type;
        this.visibility = visibility;
        this.info = info;
        this.moduleModel = moduleModel;
        this.constraintsModel = constraintsModel;
        this.concernsModel = concernsModel;
        this.sideEffectsModel = sideEffectsModel;
        this.mixinsModel = mixinsModel;
        this.stateModel = stateModel;
        this.compositeMethodsModel = compositeMethodsModel;

        this.proxyClass = createProxyClass( type );
    }

    public Class<? extends EntityComposite> type()
    {
        return type;
    }

    public Visibility visibility()
    {
        return visibility;
    }

    public MetaInfo metaInfo()
    {
        return info;
    }

    public StateDescriptor state()
    {
        return stateModel;
    }

    public void visitDependencies( DependencyVisitor visitor )
    {
        concernsModel.visitDependencies( visitor );
        sideEffectsModel.visitDependencies( visitor );
        mixinsModel.visitDependencies( visitor );
    }

    public void bind( Resolution resolution ) throws BindingException
    {
        resolution = new Resolution( resolution.application(), resolution.layer(), resolution.module(), this, null, null );
        compositeMethodsModel.bind( resolution );
        mixinsModel.bind( resolution );
    }

    public QualifiedIdentity newQualifiedIdentity( String identity )
    {
        return new QualifiedIdentity( identity, type.getName() );
    }


    public EntityInstance getInstance( UnitOfWorkInstance unitOfWorkInstance, EntityStore store, QualifiedIdentity qid, ModuleInstance moduleInstance )
    {
        return loadInstance( unitOfWorkInstance, store, qid, moduleInstance, null );
    }

    public EntityInstance loadInstance( UnitOfWorkInstance uow, EntityStore entityStore, QualifiedIdentity identity, ModuleInstance moduleInstance, EntityState state )
    {
        EntityInstance instance = new EntityInstance( uow, entityStore, this, moduleInstance, identity, EntityStatus.NEW, state );
        return instance;
    }

    public Object invoke( MixinsInstance mixins, Object proxy, Method method, Object[] args, ModuleInstance moduleInstance )
        throws Throwable
    {
        return compositeMethodsModel.invoke( mixins, proxy, method, args, moduleInstance );
    }

    public Object invoke( Object composite, Object[] params, Object[] mixins, CompositeMethodInstance methodInstance )
        throws Throwable
    {
        return mixinsModel.invoke( composite, params, mixins, methodInstance );
    }

    public Object[] newMixins( UnitOfWorkInstance uow, EntityState entityState, EntityInstance entityInstance )
    {
        Object[] mixins = mixinsModel.newMixinHolder();

        State state = stateModel.newInstance( uow, entityState );

        mixinsModel.newMixins( entityInstance, state, mixins );
        return mixins;
    }

    public EntityComposite newProxy( EntityInstance entityInstance )
    {
        // Instantiate proxy for given composite interface
        try
        {
            return EntityComposite.class.cast( proxyClass.getConstructor( InvocationHandler.class ).newInstance( entityInstance ) );
        }
        catch( Exception e )
        {
            throw new org.qi4j.composite.InstantiationException( e );
        }
    }

    public State newDefaultState()
    {
        return stateModel.newDefaultInstance();
    }

    private Class<? extends Composite> createProxyClass( Class<? extends Composite> compositeType )
    {
        ClassLoader proxyClassloader = compositeType.getClassLoader();
        Class[] interfaces = new Class[]{ compositeType };
        return (Class<? extends Composite>) Proxy.getProxyClass( proxyClassloader, interfaces );
    }

    public EntityState newEntityState( EntityStore store, String identity, State state )
        throws ConstraintViolationException
    {
        QualifiedIdentity qid = newQualifiedIdentity( identity );
        try
        {
            EntityState entityState = store.newEntityState( this, qid );

            stateModel.setState( state, entityState );
            return entityState;
        }
        catch( EntityAlreadyExistsException e )
        {
            throw new EntityCompositeAlreadyExistsException( identity, qid.type() );
        }
        catch( EntityStoreException e )
        {
            throw new org.qi4j.composite.InstantiationException( "Could not create new entity in store", e );
        }
    }

    public String toURI()
    {
        return CompositeModel.toURI( type );
    }
}