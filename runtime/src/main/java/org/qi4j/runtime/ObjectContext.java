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
package org.qi4j.runtime;

import org.qi4j.api.ObjectFactory;
import org.qi4j.api.MixinFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * TODO
 *
 */
public final class ObjectContext
{
    private Class bindingType;
    private ObjectFactory objectFactory;
    private MixinFactory mixinFactory;
    private InvocationInstancePool pool;
    private IdentityHashMap<Method, ArrayList<InvocationInstance>> invocationInstancePool;

    public ObjectContext( Class aBindingType, ObjectFactory aObjectFactory, MixinFactory aMixinFactory, InvocationInstancePool instancePool)
    {
        bindingType = aBindingType;
        objectFactory = aObjectFactory;
        mixinFactory = aMixinFactory;
        pool = instancePool;
        invocationInstancePool = instancePool.getPool( aBindingType);
    }

    public Class getBindingType()
    {
        return bindingType;
    }

    public ObjectFactory getObjectFactory()
    {
        return objectFactory;
    }

    public MixinFactory getMixinFactory()
    {
        return mixinFactory;
    }

    public InvocationInstancePool getPool()
    {
        return pool;
    }

    public IdentityHashMap<Method, ArrayList<InvocationInstance>> getInvocationInstancePool()
    {
        return invocationInstancePool;
    }
}
