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

package org.qi4j.spi.value;

import java.lang.reflect.Type;
import static org.qi4j.api.common.TypeName.*;
import org.qi4j.api.structure.Module;
import org.qi4j.spi.util.PeekableStringTokenizer;

/**
 * Boolean type
 */
public final class BooleanType
    extends ValueType
{
    public static boolean isBoolean( Type type )
    {
        if( type instanceof Class )
        {
            Class typeClass = (Class) type;
            return ( typeClass.equals( Boolean.class ) );
        }
        return false;
    }

    public BooleanType()
    {
        super( nameOf( Boolean.class ) );
    }

    public void toJSON( Object value, StringBuilder json )
    {
        json.append( ( (Boolean) value ).booleanValue() );
    }

    public Object fromJSON( PeekableStringTokenizer json, Module module )
    {
        String string = json.nextToken();
        return Boolean.valueOf( string );
    }
}