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

package org.qi4j.runtime.injection;

import org.qi4j.api.property.StateHolder;
import org.qi4j.runtime.composite.ProxyReferenceInvocationHandler;
import org.qi4j.runtime.composite.UsesInstance;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.spi.composite.CompositeInstance;

/**
 * JAVADOC
 */
public final class InjectionContext
{
    private final CompositeInstance compositeInstance;
    private final ModuleInstance moduleInstance;
    private UsesInstance uses;
    private final StateHolder state;
    private final Object next;
    private final ProxyReferenceInvocationHandler proxyHandler;

    // For mixins
    public InjectionContext( CompositeInstance compositeInstance, UsesInstance uses, StateHolder state )
    {
        this.compositeInstance = compositeInstance;
        this.moduleInstance = (ModuleInstance) compositeInstance.module();
        this.uses = uses;
        this.state = state;
        this.next = null;
        this.proxyHandler = null;
    }

    // For concerns and side-effects
    public InjectionContext( ModuleInstance moduleInstance, Object next, ProxyReferenceInvocationHandler proxyHandler )
    {
        this.compositeInstance = null;
        this.moduleInstance = moduleInstance;
        this.uses = null;
        this.next = next;
        this.state = null;
        this.proxyHandler = proxyHandler;
    }

    public InjectionContext( ModuleInstance moduleInstance, UsesInstance uses )
    {
        this.compositeInstance = null;
        this.moduleInstance = moduleInstance;
        this.uses = uses;
        this.state = null;
        this.next = null;
        this.proxyHandler = null;
    }

    public ModuleInstance moduleInstance()
    {
        return moduleInstance;
    }

    public CompositeInstance compositeInstance()
    {
        return compositeInstance;
    }

    public UsesInstance uses()
    {
        return uses;
    }

    public StateHolder state()
    {
        return state;
    }

    public Object next()
    {
        return next;
    }

    public ProxyReferenceInvocationHandler proxyHandler()
    {
        return proxyHandler;
    }

    public void setUses( UsesInstance uses )
    {
        this.uses = uses;
    }
}
