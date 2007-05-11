/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
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
package org.qi4j.api.persistence.modifier;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.qi4j.api.InvocationContext;
import org.qi4j.api.annotation.AppliesTo;
import org.qi4j.api.annotation.Dependency;
import org.qi4j.api.annotation.Modifies;
import org.qi4j.api.annotation.Uses;
import org.qi4j.api.persistence.PersistentRepository;
import org.qi4j.api.persistence.binding.PersistenceBinding;

/**
 * When methods in stateful mixins that modify state have been called
 * this modifier will store it in a repository, if one has been set.
 */
@AppliesTo( Serializable.class )
public final class ReadUpdateModifier
    implements InvocationHandler
{
    @Uses
    PersistenceBinding persistent;
    @Dependency
    InvocationContext context;
    @Modifies
    Object next;

    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
    {
        // Load mixin
        PersistentRepository repository = persistent.getPersistentRepository();
        if( repository != null && isReadMethod( method ) )
        {
            repository.read( persistent );
        }

        Object result = method.invoke( next, args );

        // Store mixin
        if( repository != null && isWriteMethod( method ) )
        {
            repository.update( persistent, context.getMixin() );
        }

        return result;
    }

    protected boolean isReadMethod( Method aMethod )
    {
        String name = aMethod.getName();
        return name.startsWith( "get" ) || name.startsWith( "is" );
    }

    protected boolean isWriteMethod( Method aMethod )
    {
        return !isReadMethod( aMethod );
    }
}