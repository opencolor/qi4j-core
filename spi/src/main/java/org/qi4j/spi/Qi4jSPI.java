/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.spi;

import org.qi4j.api.Qi4j;
import org.qi4j.api.composite.Composite;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.association.EntityStateHolder;
import org.qi4j.api.property.StateHolder;
import org.qi4j.api.structure.Module;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.spi.composite.CompositeDescriptor;
import org.qi4j.spi.entity.EntityDescriptor;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.StateFactory;
import org.qi4j.spi.object.ObjectDescriptor;
import org.qi4j.spi.value.ValueDescriptor;

/**
 * Encapsulation of the Qi4j SPI. This is implemented by the runtime.
 */
public interface Qi4jSPI
    extends Qi4j
{
    // Composites
    CompositeDescriptor getCompositeDescriptor( Composite composite );

    CompositeDescriptor getCompositeDescriptor( Class<? extends Composite> compositeType, Module module );

    StateHolder getState( Composite composite );

    // Entities
    EntityDescriptor getEntityDescriptor( EntityComposite composite );

    EntityDescriptor getEntityDescriptor( Class<? extends EntityComposite> entityType, Module module );

    EntityStateHolder getState( EntityComposite composite );

    EntityState getEntityState( EntityComposite composite );

    // Values
    ValueDescriptor getValueDescriptor( ValueComposite value );

    ValueDescriptor getValueDescriptor( Class<? extends ValueComposite> entityType, Module module );

    StateHolder getState( ValueComposite composite );

    // Objects
    ObjectDescriptor getObjectDescriptor( Class objectType, Module module );

    StateFactory getDefaultStateFactory();
}
