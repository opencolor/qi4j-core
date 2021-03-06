/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
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

import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.entity.Queryable;
import org.qi4j.api.property.GenericPropertyInfo;
import org.qi4j.api.property.Property;
import org.qi4j.api.property.PropertyInfo;
import org.qi4j.runtime.composite.ValueConstraintsInstance;
import org.qi4j.runtime.property.PersistentPropertyModel;
import org.qi4j.spi.property.PropertyType;
import org.qi4j.spi.property.PropertyTypeDescriptor;
import org.qi4j.spi.value.ValueType;

import java.lang.reflect.Method;

/**
 * Property model for values
 */
public final class ValuePropertyModel extends PersistentPropertyModel
    implements PropertyTypeDescriptor
{
    private final PropertyType propertyType;
    private PropertyInfo propertyInfo;

    public ValuePropertyModel(Method anAccessor,
                              Class compositeType, ValueConstraintsInstance constraints,
                              MetaInfo metaInfo,
                              Object defaultValue)
    {
        super( anAccessor, compositeType, true, constraints, metaInfo, defaultValue );
        final Queryable queryable = anAccessor.getAnnotation( Queryable.class );
        boolean isQueryable = queryable == null || queryable.value();

        PropertyType.PropertyTypeEnum type;
        type = PropertyType.PropertyTypeEnum.IMMUTABLE;

        ValueType valueType = ValueType.newValueType( type(), anAccessor.getDeclaringClass(), compositeType );

        propertyType = new PropertyType( qualifiedName(), valueType, toRDF(), isQueryable, type );
        propertyInfo = new GenericPropertyInfo( metaInfo, isImmutable(), isComputed(), qualifiedName(), type() );
    }

    public PropertyType propertyType()
    {
        return propertyType;
    }

    public Property<?> newInstance( Object value )
    {
        // Property was constructed using a builder

        Property property;
        if( isComputed() )
        {
            property = new ComputedPropertyInfo<Object>( propertyInfo );
        }
        else
        {
            property = new ValuePropertyInstance<Object>( propertyInfo, value );
        }
        return wrapProperty( property );
    }
}