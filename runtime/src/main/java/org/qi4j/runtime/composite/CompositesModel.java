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

package org.qi4j.runtime.composite;

import org.qi4j.api.common.Visibility;
import org.qi4j.api.composite.AmbiguousTypeException;
import org.qi4j.api.composite.Composite;
import org.qi4j.runtime.structure.Binder;
import org.qi4j.runtime.structure.ModelVisitor;

import java.io.Serializable;
import java.util.List;

/**
 * JAVADOC
 */
public class CompositesModel
    implements Binder, Serializable
{
    private final List<? extends CompositeModel> compositeModels;

    public CompositesModel( List<? extends CompositeModel> compositeModels )
    {
        this.compositeModels = compositeModels;
    }


    public void visitModel( ModelVisitor modelVisitor )
    {
        for( CompositeModel compositeModel : compositeModels )
        {
            compositeModel.visitModel( modelVisitor );
        }
    }

    public void bind( Resolution resolution )
        throws BindingException
    {
        for( CompositeModel compositeModel : compositeModels )
        {
            compositeModel.bind( resolution );
        }
    }

    public CompositeModel getCompositeModelFor( Class mixinType, Visibility visibility )
    {
        CompositeModel foundModel = null;
        for( CompositeModel composite : compositeModels )
        {
            if (Composite.class.isAssignableFrom(mixinType))
            {
                if( mixinType.equals( composite.type() ) && composite.visibility() == visibility )
                {
                    if( foundModel != null )
                    {
                        throw new AmbiguousTypeException( mixinType, foundModel.type(), composite.type() );
                    }
                    else
                    {
                        foundModel = composite;
                    }
                }
            } else
            {
                if( mixinType.isAssignableFrom( composite.type() ) && composite.visibility() == visibility )
                {
                    if( foundModel != null )
                    {
                        throw new AmbiguousTypeException( mixinType, foundModel.type(), composite.type() );
                    }
                    else
                    {
                        foundModel = composite;
                    }
                }
            }

        }

        return foundModel;
    }

    public Class getClassForName( String type )
    {
        for( CompositeModel compositeModel : compositeModels )
        {
            if( compositeModel.type().getName().equals( type ) )
            {
                return compositeModel.type();
            }
        }

        return null;
    }
}
