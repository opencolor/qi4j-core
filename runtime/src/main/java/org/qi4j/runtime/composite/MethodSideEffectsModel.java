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

import org.qi4j.api.util.SerializationUtil;
import org.qi4j.runtime.structure.Binder;
import org.qi4j.runtime.structure.ModelVisitor;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.spi.sideeffect.MethodSideEffectsDescriptor;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JAVADOC
 */
public final class MethodSideEffectsModel
    implements Binder, MethodSideEffectsDescriptor, Serializable
{
    private Method method;
    private List<MethodSideEffectModel> sideEffectModels = null;

    private void writeObject( ObjectOutputStream out )
        throws IOException
    {
        try
        {
            SerializationUtil.writeMethod( out, method );
            out.writeObject( sideEffectModels );
        }
        catch( NotSerializableException e )
        {
            System.err.println( "NotSerializable in " + getClass() );
            throw e;
        }
    }

    private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException
    {
        method = SerializationUtil.readMethod( in );
        sideEffectModels = (List<MethodSideEffectModel>) in.readObject();
    }

    public MethodSideEffectsModel( Method method, List<MethodSideEffectModel> sideEffectModels )
    {
        this.method = method;
        this.sideEffectModels = sideEffectModels;
    }

    public Method method()
    {
        return method;
    }

    public boolean hasSideEffects()
    {
        return !sideEffectModels.isEmpty();
    }

    // Binding
    public void bind( Resolution resolution )
        throws BindingException
    {
        for( MethodSideEffectModel methodSideEffectModel : sideEffectModels )
        {
            methodSideEffectModel.bind( resolution );
        }
    }

    // Context
    public MethodSideEffectsInstance newInstance( ModuleInstance moduleInstance, InvocationHandler invoker )
    {
        ProxyReferenceInvocationHandler proxyHandler = new ProxyReferenceInvocationHandler();
        SideEffectInvocationHandlerResult result = new SideEffectInvocationHandlerResult();
        List<InvocationHandler> sideEffects = new ArrayList<InvocationHandler>( sideEffectModels.size() );
        for( MethodSideEffectModel sideEffectModel : sideEffectModels )
        {
            Object sideEffect = sideEffectModel.newInstance( moduleInstance, result, proxyHandler );
            if( sideEffectModel.isGeneric() )
            {
                sideEffects.add( (InvocationHandler) sideEffect );
            }
            else
            {
                sideEffects.add( new TypedFragmentInvocationHandler( sideEffect ) );
            }
        }
        return new MethodSideEffectsInstance( sideEffects, result, proxyHandler, invoker );
    }


    public void visitModel( ModelVisitor modelVisitor )
    {
        modelVisitor.visit( this );
        for( MethodSideEffectModel methodSideEffectModel : sideEffectModels )
        {
            methodSideEffectModel.visitModel( modelVisitor );
        }
    }

    public MethodSideEffectsModel combineWith( MethodSideEffectsModel mixinMethodSideEffectsModel )
    {
        List<MethodSideEffectModel> combinedModels = new ArrayList<MethodSideEffectModel>( sideEffectModels.size() + mixinMethodSideEffectsModel.sideEffectModels.size() );
        combinedModels.addAll( sideEffectModels );
        combinedModels.addAll( mixinMethodSideEffectsModel.sideEffectModels );
        return new MethodSideEffectsModel( method, combinedModels );
    }

    static MethodSideEffectsModel createForMethod( Method method, Collection<Class> sideEffectClasses )
    {
        List<MethodSideEffectModel> sideEffects = new ArrayList<MethodSideEffectModel>();
        for( Class sideEffectClass : sideEffectClasses )
        {
            sideEffects.add( new MethodSideEffectModel( sideEffectClass ) );
        }

        return new MethodSideEffectsModel( method, sideEffects );
    }
}