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

package org.qi4j.runtime.value;

import org.qi4j.api.property.StateHolder;
import org.qi4j.api.value.Value;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.runtime.composite.AbstractMixinsModel;
import org.qi4j.runtime.composite.MixinDeclaration;
import org.qi4j.runtime.composite.MixinModel;

import java.util.List;

/**
 * Model for mixins in ValueComposites
 */
public final class ValueMixinsModel extends AbstractMixinsModel
{
    public ValueMixinsModel( Class<? extends ValueComposite> compositeType, List<Class<?>> assemblyMixins )
    {
        super( compositeType, assemblyMixins );
        mixins.add( new MixinDeclaration( ValueMixin.class, Value.class ) );
    }

    public void newMixins( ValueInstance compositeInstance, StateHolder state, Object[] mixins )
    {
        int i = 0;
        for( MixinModel mixinModel : mixinModels )
        {
            mixins[ i++ ] = mixinModel.newInstance( compositeInstance, state );
        }
    }
}