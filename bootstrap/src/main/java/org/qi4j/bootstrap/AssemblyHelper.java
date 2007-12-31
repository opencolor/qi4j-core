/*
 * Copyright (c) 2007, Rickard �berg. All Rights Reserved.
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

package org.qi4j.bootstrap;

import java.lang.reflect.Method;
import org.qi4j.entity.association.AbstractAssociation;
import org.qi4j.entity.property.ReadableProperty;

/**
 * TODO
 */
public class AssemblyHelper
{
    ModuleAssembly module;

    public AssemblyHelper( ModuleAssembly module )
    {
        this.module = module;
    }

    public AssemblyHelper add( Class interfaceClass )
    {
        try
        {
            for( Method method : interfaceClass.getMethods() )
            {
                if( ReadableProperty.class.isAssignableFrom( method.getReturnType() ) )
                {
                    // Register property
                    PropertyBuilder builder = module.addProperty();
                    method.invoke( builder.withAccessor( interfaceClass ) );
                    addProperty( builder, method );
                }
                else if( method.getReturnType().equals( AbstractAssociation.class ) )
                {
                    // Register association
                    AssociationBuilder builder = module.addAssociation();
                    method.invoke( builder.withAccessor( interfaceClass ) );
                    addAssociation( builder, method );
                }

            }
        }
        catch( Exception e )
        {
            // Should never happen...
            e.printStackTrace();
        }

        return this;
    }

    protected void addProperty( PropertyBuilder builder, Method accessor )
    {
        // Override this method if you want to add custom initialization
    }

    protected void addAssociation( AssociationBuilder builder, Method accessor )
    {
        // Override this method if you want to add custom initialization
    }
}