/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.spi.query;

/**
 * JAVADOC Add JavaDoc.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, April 02, 2008
 */
public class EntityFinderException
    extends Exception
{
    public EntityFinderException( final String message )
    {
        super( message );
    }

    public EntityFinderException( final String message,
                                  final Throwable throwable )
    {
        super( message, throwable );
    }

    public EntityFinderException( final Throwable throwable )
    {
        super( throwable );
    }
}