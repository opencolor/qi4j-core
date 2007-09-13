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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.qi4j.api.Composite;
import org.qi4j.api.CompositeBuilder;
import org.qi4j.api.CompositeBuilderFactory;
import org.qi4j.api.CompositeModelFactory;
import org.qi4j.api.FragmentFactory;
import org.qi4j.api.annotation.Adapt;
import org.qi4j.api.annotation.Decorate;
import org.qi4j.api.annotation.Modifies;
import org.qi4j.api.annotation.ThisAs;
import org.qi4j.api.model.CompositeModel;
import org.qi4j.api.model.CompositeResolution;

/**
 * Default implementation of CompositeBuilderFactory
 */
public final class CompositeBuilderFactoryImpl
    implements CompositeBuilderFactory
{
    private Map<Class<? extends Composite>, CompositeContextImpl> objectContexts;
    private CompositeModelFactory modelFactory;
    private FragmentFactory fragmentFactory;
    private CompositeModelResolver compositeModelResolver;
    private CompositeModelBuilder compositeModelBuilder;

    public CompositeBuilderFactoryImpl()
    {
        DependencyResolverDelegator dependencyResolverDelegator = new DependencyResolverDelegator();

        dependencyResolverDelegator.setDependencyResolver( ThisAs.class, new ThisAsDependencyResolver());
        dependencyResolverDelegator.setDependencyResolver( Modifies.class, new ModifiesDependencyResolver());
        dependencyResolverDelegator.setDependencyResolver( Adapt.class, new AdaptDependencyResolver());
        dependencyResolverDelegator.setDependencyResolver( Decorate.class, new DecorateDependencyResolver());

        ModifierModelBuilder modifierModelBuilder = new ModifierModelBuilder();
        MixinModelBuilder mixinModelBuilder = new MixinModelBuilder(modifierModelBuilder);
        compositeModelBuilder = new CompositeModelBuilder( modifierModelBuilder, mixinModelBuilder );

        ModifierModelResolver modifierModelResolver = new ModifierModelResolver( dependencyResolverDelegator);
        MixinModelResolver mixinModelResolver = new MixinModelResolver( dependencyResolverDelegator);
        compositeModelResolver = new CompositeModelResolver( modifierModelResolver, mixinModelResolver);

        modelFactory = new CompositeModelFactoryImpl(compositeModelBuilder);
        objectContexts = new ConcurrentHashMap<Class<? extends Composite>, CompositeContextImpl>();
        fragmentFactory = new FragmentFactoryImpl();
   }

    public <T extends Composite> CompositeBuilder<T> newCompositeBuilder( Class<T> compositeType )
    {
        CompositeContextImpl<T> context = getCompositeContext( compositeType);
        CompositeBuilder<T> builder = new CompositeBuilderImpl<T>( context, fragmentFactory );
        return builder;
    }

    // Private ------------------------------------------------------
    private <T extends Composite> CompositeContextImpl<T> getCompositeContext( Class<T> compositeType )
    {
        CompositeContextImpl<T> context = objectContexts.get( compositeType );
        if( context == null )
        {
            CompositeModel<T> model = modelFactory.newCompositeModel( compositeType);
            CompositeResolution<T> resolution = compositeModelResolver.resolveCompositeModel( model );
            context = new CompositeContextImpl<T>( resolution, modelFactory, this, fragmentFactory );
            objectContexts.put( compositeType, context );
        }
        return context;
    }
}