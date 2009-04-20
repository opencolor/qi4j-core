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

package org.qi4j.test;

import org.junit.After;
import org.junit.Before;
import org.qi4j.api.Qi4j;
import org.qi4j.api.composite.CompositeBuilderFactory;
import org.qi4j.api.object.ObjectBuilderFactory;
import org.qi4j.api.service.ServiceFinder;
import org.qi4j.api.structure.Application;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.api.value.ValueBuilderFactory;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.Energy4Java;
import org.qi4j.spi.Qi4jSPI;
import org.qi4j.spi.structure.ApplicationModelSPI;
import org.qi4j.spi.structure.ApplicationSPI;

/**
 * Base class for Composite tests.
 */
public abstract class AbstractQi4jTest
    implements Assembler
{
    protected Qi4j api;
    protected Qi4jSPI spi;

    protected Energy4Java qi4j;
    protected ApplicationModelSPI applicationModel;
    protected ApplicationSPI application;

    protected CompositeBuilderFactory compositeBuilderFactory;
    protected ObjectBuilderFactory objectBuilderFactory;
    protected ValueBuilderFactory valueBuilderFactory;
    protected UnitOfWorkFactory unitOfWorkFactory;
    protected ServiceFinder serviceLocator;

    protected Module moduleInstance;

    @Before public void setUp() throws Exception
    {
        qi4j = new Energy4Java();
        applicationModel = newApplication();
        application = applicationModel.newInstance( qi4j.spi() );
        initApplication( application );
        api = spi = qi4j.spi();
        application.activate();

        // Assume only one module
        moduleInstance = application.findModule( "Layer 1", "Module 1" );
        compositeBuilderFactory = moduleInstance.compositeBuilderFactory();
        objectBuilderFactory = moduleInstance.objectBuilderFactory();
        valueBuilderFactory = moduleInstance.valueBuilderFactory();
        unitOfWorkFactory = moduleInstance.unitOfWorkFactory();
        serviceLocator = moduleInstance.serviceFinder();
    }

    protected ApplicationModelSPI newApplication()
        throws AssemblyException
    {
        return qi4j.newApplicationModel( new ApplicationAssembler()
        {
            public ApplicationAssembly assemble( ApplicationAssemblyFactory applicationFactory ) throws AssemblyException
            {
                return applicationFactory.newApplicationAssembly( AbstractQi4jTest.this );
            }
        } );
    }

    protected void initApplication( Application app ) throws Exception
    {
    }

    @After public void tearDown() throws Exception
    {
        if( unitOfWorkFactory != null && unitOfWorkFactory.currentUnitOfWork() != null )
        {
            UnitOfWork current;
            while( ( current = unitOfWorkFactory.currentUnitOfWork() ) != null )
            {
                if( current.isOpen() )
                {
                    current.discard();
                }
                else
                {
                    throw new InternalError( "I have seen a case where a UoW is on the stack, but not opened." );
                }
            }

            new Exception( "UnitOfWork not properly cleaned up" ).printStackTrace();
        }

        if( application != null )
        {
            application.passivate();
        }
    }

}