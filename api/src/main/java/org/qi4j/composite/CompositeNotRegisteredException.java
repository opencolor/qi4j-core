/*
 * Copyright (c) 2008, Niclas Hedhman. All Rights Reserved.
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
package org.qi4j.composite;

public class CompositeNotRegisteredException extends InvalidApplicationException
{
    private static final long serialVersionUID = 1L;

    private Class compositeType;
    private String moduleName;

    public CompositeNotRegisteredException( Class compositeType, String moduleName )
    {
        super( "Trying to find unregistered composite of type [" + compositeType.getName() + "] in module [" +
               moduleName + "]." );
        this.compositeType = compositeType;
        this.moduleName = moduleName;
    }

    public Class compositeType()
    {
        return compositeType;
    }

    public String moduleName()
    {
        return moduleName;
    }
}