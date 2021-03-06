/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.qi4j.runtime.util;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.qi4j.api.util.CyclicUsageException;
import org.qi4j.api.util.UsageGraph;

import java.util.ArrayList;
import java.util.List;


public class UsageGraphTest
{

    @Before
    public void setUp()
    {
        Thing.counter = 1;
    }

    @Test
    public void verifyRandomDoesNotAffect()
        throws Exception
    {
        for( int i = 0; i < 100; i++ )
        {
            whenGraphIsOpenEndedGivenNotAllowCyclicDependenciesThenNoError();
            whenGraphIsCyclicGivenAllowCyclicDependencyThenNoError();
        }
    }

    @Test
    public void whenGraphIsOpenEndedGivenNotAllowCyclicDependenciesThenNoError()
        throws Exception
    {
        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        Thing thing3 = new Thing();
        Thing thing4 = new Thing();
        Thing thing5 = new Thing();
        Thing thing6 = new Thing();
        Thing thing7 = new Thing();
        thing1.uses.add( thing3 );
        thing2.uses.add( thing3 );
        thing3.uses.add( thing4 );
        thing4.uses.add( thing5 );
        thing1.uses.add( thing6 );
        thing7.uses.add( thing1 );
        thing7.uses.add( thing2 );
        thing7.uses.add( thing4 );
        List<Thing> data = new ArrayList<Thing>();
        data.add( thing7 );
        data.add( thing4 );
        data.add( thing3 );
        data.add( thing1 );
        data.add( thing6 );
        data.add( thing5 );
        data.add( thing2 );
        randomize( data );
        UsageGraph<Thing> deps = new UsageGraph<Thing>( data, new Userator(), false );
        assertFalse( deps.transitiveUse( thing1, thing1 ) );
        assertFalse( deps.transitiveUse( thing1, thing2 ) );
        assertTrue( deps.transitiveUse( thing1, thing3 ) );
        assertTrue( deps.transitiveUse( thing1, thing4 ) );
        assertTrue( deps.transitiveUse( thing1, thing5 ) );
        assertTrue( deps.transitiveUse( thing1, thing6 ) );
        assertFalse( deps.transitiveUse( thing1, thing7 ) );
        assertFalse( deps.transitiveUse( thing2, thing1 ) );
        assertFalse( deps.transitiveUse( thing2, thing2 ) );
        assertTrue( deps.transitiveUse( thing2, thing3 ) );
        assertTrue( deps.transitiveUse( thing2, thing4 ) );
        assertTrue( deps.transitiveUse( thing2, thing5 ) );
        assertFalse( deps.transitiveUse( thing2, thing6 ) );
        assertFalse( deps.transitiveUse( thing2, thing7 ) );
        assertFalse( deps.transitiveUse( thing3, thing1 ) );
        assertFalse( deps.transitiveUse( thing3, thing2 ) );
        assertFalse( deps.transitiveUse( thing3, thing3 ) );
        assertTrue( deps.transitiveUse( thing3, thing4 ) );
        assertTrue( deps.transitiveUse( thing3, thing5 ) );
        assertFalse( deps.transitiveUse( thing3, thing6 ) );
        assertFalse( deps.transitiveUse( thing3, thing7 ) );
        assertFalse( deps.transitiveUse( thing4, thing1 ) );
        assertFalse( deps.transitiveUse( thing4, thing2 ) );
        assertFalse( deps.transitiveUse( thing4, thing3 ) );
        assertFalse( deps.transitiveUse( thing4, thing4 ) );
        assertTrue( deps.transitiveUse( thing4, thing5 ) );
        assertFalse( deps.transitiveUse( thing4, thing6 ) );
        assertFalse( deps.transitiveUse( thing4, thing7 ) );
        assertFalse( deps.transitiveUse( thing5, thing1 ) );
        assertFalse( deps.transitiveUse( thing5, thing2 ) );
        assertFalse( deps.transitiveUse( thing5, thing3 ) );
        assertFalse( deps.transitiveUse( thing5, thing4 ) );
        assertFalse( deps.transitiveUse( thing5, thing5 ) );
        assertFalse( deps.transitiveUse( thing5, thing6 ) );
        assertFalse( deps.transitiveUse( thing5, thing7 ) );
        assertFalse( deps.transitiveUse( thing6, thing1 ) );
        assertFalse( deps.transitiveUse( thing6, thing2 ) );
        assertFalse( deps.transitiveUse( thing6, thing3 ) );
        assertFalse( deps.transitiveUse( thing6, thing4 ) );
        assertFalse( deps.transitiveUse( thing6, thing5 ) );
        assertFalse( deps.transitiveUse( thing6, thing6 ) );
        assertFalse( deps.transitiveUse( thing6, thing7 ) );
        assertTrue( deps.transitiveUse( thing7, thing1 ) );
        assertTrue( deps.transitiveUse( thing7, thing2 ) );
        assertTrue( deps.transitiveUse( thing7, thing3 ) );
        assertTrue( deps.transitiveUse( thing7, thing4 ) );
        assertTrue( deps.transitiveUse( thing7, thing5 ) );
        assertTrue( deps.transitiveUse( thing7, thing6 ) );
        assertFalse( deps.transitiveUse( thing7, thing7 ) );
        List<Thing> resolved = deps.resolveOrder();
        System.out.println( resolved );
        assertTrue( resolved.indexOf( thing1 ) > resolved.indexOf( thing6 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing3 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing1 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing2 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing1 ) > resolved.indexOf( thing3 ) );
        assertTrue( resolved.indexOf( thing1 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing1 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing3 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing3 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing4 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing6 ) );
    }

    private void randomize( List<Thing> data )
    {
        int n = (int) ( Math.random() * 100 );
        for( int i = 0; i < n; i++ )
        {
            int pos1 = 0;
            int pos2 = 0;
            while( pos1 == pos2 )
            {
                pos1 = (int) ( Math.floor( Math.random() * data.size() ) );
                pos2 = (int) ( Math.floor( Math.random() * data.size() ) );
            }
            if( pos1 < pos2 )
            {
                int temp = pos2;
                pos2 = pos1;
                pos1 = temp;
            }
            Thing thing1 = data.remove( pos1 );
            Thing thing2 = data.remove( pos2 );
            data.add( pos2, thing1 );
            data.add( pos1, thing2 );
        }
    }

    @Test
    public void whenAskingForDependencyGivenThatGraphContainsCyclicDepThenDetectTheError()
        throws Exception
    {
        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        Thing thing3 = new Thing();
        Thing thing4 = new Thing();
        Thing thing5 = new Thing();
        Thing thing6 = new Thing();
        Thing thing7 = new Thing();
        thing1.uses.add( thing3 );
        thing2.uses.add( thing3 );
        thing3.uses.add( thing4 );
        thing4.uses.add( thing5 );
        thing5.uses.add( thing1 );      // <-- Cyclic
        thing1.uses.add( thing6 );
        thing7.uses.add( thing1 );
        thing7.uses.add( thing2 );
        thing7.uses.add( thing4 );
        List<Thing> data = new ArrayList<Thing>();
        data.add( thing7 );
        data.add( thing4 );
        data.add( thing1 );
        data.add( thing3 );
        data.add( thing6 );
        data.add( thing5 );
        data.add( thing2 );
        randomize( data );
        UsageGraph<Thing> deps = new UsageGraph<Thing>( data, new Userator(), false );
        try
        {
            List<Thing> resolved = deps.resolveOrder();
            Assert.fail( "Cyclic Dependency Not Detected." );
        }
        catch( CyclicUsageException e )
        {
            // Expected!
        }
    }

    @Test
    public void whenAskingForResolveOrderGivenThatGraphContainsCyclicDepThenDetectTheError()
        throws Exception
    {
        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        Thing thing3 = new Thing();
        Thing thing4 = new Thing();
        Thing thing5 = new Thing();
        Thing thing6 = new Thing();
        Thing thing7 = new Thing();
        thing1.uses.add( thing3 );
        thing2.uses.add( thing3 );
        thing3.uses.add( thing4 );
        thing4.uses.add( thing5 );
        thing5.uses.add( thing1 );      // <-- Cyclic
        thing1.uses.add( thing6 );
        thing7.uses.add( thing1 );
        thing7.uses.add( thing2 );
        thing7.uses.add( thing4 );
        List<Thing> data = new ArrayList<Thing>();
        data.add( thing7 );
        data.add( thing4 );
        data.add( thing1 );
        data.add( thing3 );
        data.add( thing6 );
        data.add( thing5 );
        data.add( thing2 );
        randomize( data );
        UsageGraph<Thing> deps = new UsageGraph<Thing>( data, new Userator(), false );
        try
        {
            assertTrue( deps.transitiveUse( thing1, thing3 ) );
            Assert.fail( "Cyclic Dependency Not Detected." );
        }
        catch( CyclicUsageException e )
        {
            // Expected!
        }
    }

    @Test
    public void whenGraphIsCyclicGivenAllowCyclicDependencyThenNoError()
        throws Exception
    {
        Thing thing1 = new Thing();
        Thing thing2 = new Thing();
        Thing thing3 = new Thing();
        Thing thing4 = new Thing();
        Thing thing5 = new Thing();
        Thing thing6 = new Thing();
        Thing thing7 = new Thing();
        thing1.uses.add( thing3 );
        thing2.uses.add( thing3 );
        thing3.uses.add( thing4 );
        thing4.uses.add( thing5 );
        thing1.uses.add( thing6 );
        thing5.uses.add( thing1 );      // <-- Cyclic
        thing7.uses.add( thing1 );
        thing7.uses.add( thing2 );
        thing7.uses.add( thing4 );
        List<Thing> data = new ArrayList<Thing>();
        data.add( thing7 );
        data.add( thing4 );
        data.add( thing1 );
        data.add( thing3 );
        data.add( thing6 );
        data.add( thing5 );
        data.add( thing2 );
        randomize( data );
        UsageGraph<Thing> deps = new UsageGraph<Thing>( data, new Userator(), true );
        assertTrue( deps.transitiveUse( thing1, thing1 ) );
        assertFalse( deps.transitiveUse( thing1, thing2 ) );
        assertTrue( deps.transitiveUse( thing1, thing3 ) );
        assertTrue( deps.transitiveUse( thing1, thing4 ) );
        assertTrue( deps.transitiveUse( thing1, thing5 ) );
        assertTrue( deps.transitiveUse( thing1, thing6 ) );
        assertFalse( deps.transitiveUse( thing1, thing7 ) );
        assertTrue( deps.transitiveUse( thing2, thing1 ) );
        assertFalse( deps.transitiveUse( thing2, thing2 ) );
        assertTrue( deps.transitiveUse( thing2, thing3 ) );
        assertTrue( deps.transitiveUse( thing2, thing4 ) );
        assertTrue( deps.transitiveUse( thing2, thing5 ) );
        assertTrue( deps.transitiveUse( thing2, thing6 ) );
        assertFalse( deps.transitiveUse( thing2, thing7 ) );
        assertTrue( deps.transitiveUse( thing3, thing1 ) );
        assertFalse( deps.transitiveUse( thing3, thing2 ) );
        assertTrue( deps.transitiveUse( thing3, thing3 ) );
        assertTrue( deps.transitiveUse( thing3, thing4 ) );
        assertTrue( deps.transitiveUse( thing3, thing5 ) );
        assertTrue( deps.transitiveUse( thing3, thing6 ) );
        assertFalse( deps.transitiveUse( thing3, thing7 ) );
        assertTrue( deps.transitiveUse( thing4, thing1 ) );
        assertFalse( deps.transitiveUse( thing4, thing2 ) );
        assertTrue( deps.transitiveUse( thing4, thing3 ) );
        assertTrue( deps.transitiveUse( thing4, thing4 ) );
        assertTrue( deps.transitiveUse( thing4, thing5 ) );
        assertTrue( deps.transitiveUse( thing4, thing6 ) );
        assertFalse( deps.transitiveUse( thing4, thing7 ) );
        assertTrue( deps.transitiveUse( thing5, thing1 ) );
        assertFalse( deps.transitiveUse( thing5, thing2 ) );
        assertTrue( deps.transitiveUse( thing5, thing3 ) );
        assertTrue( deps.transitiveUse( thing5, thing4 ) );
        assertTrue( deps.transitiveUse( thing5, thing5 ) );
        assertTrue( deps.transitiveUse( thing5, thing6 ) );
        assertFalse( deps.transitiveUse( thing5, thing7 ) );
        assertFalse( deps.transitiveUse( thing6, thing1 ) );
        assertFalse( deps.transitiveUse( thing6, thing2 ) );
        assertFalse( deps.transitiveUse( thing6, thing3 ) );
        assertFalse( deps.transitiveUse( thing6, thing4 ) );
        assertFalse( deps.transitiveUse( thing6, thing5 ) );
        assertFalse( deps.transitiveUse( thing6, thing6 ) );
        assertFalse( deps.transitiveUse( thing6, thing7 ) );
        assertTrue( deps.transitiveUse( thing7, thing1 ) );
        assertTrue( deps.transitiveUse( thing7, thing2 ) );
        assertTrue( deps.transitiveUse( thing7, thing3 ) );
        assertTrue( deps.transitiveUse( thing7, thing4 ) );
        assertTrue( deps.transitiveUse( thing7, thing5 ) );
        assertTrue( deps.transitiveUse( thing7, thing6 ) );
        assertFalse( deps.transitiveUse( thing7, thing7 ) );
        List<Thing> resolved = deps.resolveOrder();
        System.out.println( resolved );
        assertTrue( resolved.indexOf( thing1 ) > resolved.indexOf( thing6 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing3 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing4 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing5 ) );
        assertTrue( resolved.indexOf( thing2 ) > resolved.indexOf( thing1 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing1 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing2 ) );
        assertTrue( resolved.indexOf( thing7 ) > resolved.indexOf( thing4 ) );

        // The cyclic nodes can not be determine which one is before the other
    }

    @Test
    @Ignore( "What is this supposed to do anyway???" )
    public void when100LevelsOfNodesWith100NodesOnEachLevelThenTakeLessThanOneMilliSecond()
        throws Exception
    {
        long timeSpent = createThingGraph( 1 );
        assertTrue( timeSpent < 10 );
        timeSpent = createThingGraph( 2 );
        assertTrue( timeSpent < 10 );
        timeSpent = createThingGraph( 3 );
        assertTrue( timeSpent < 10 );
        timeSpent = createThingGraph( 4 );
        assertTrue( timeSpent < 100 );
        timeSpent = createThingGraph( 5 );
        assertTrue( timeSpent < 100 );
        timeSpent = createThingGraph( 6 );
        assertTrue( timeSpent < 100 );
        timeSpent = createThingGraph( 7 );
        assertTrue( timeSpent < 1000 );
        timeSpent = createThingGraph( 8 );
        assertTrue( timeSpent < 1000 );
        timeSpent = createThingGraph( 9 );
        assertTrue( timeSpent < 2000 );
    }

    private long createThingGraph( int maxLevels )
    {
        ArrayList<Thing> all = new ArrayList<Thing>();
        long t0 = System.currentTimeMillis();
        Thing root = createThing( all, 0, maxLevels );
        long t1 = System.currentTimeMillis();
        long timeSpent = t1 - t0;
        String countText = "" + ( Thing.counter - 1 );
        System.out.print( countText );
        int len = countText.length();
        for( int i = 20 - ( len < 20 ? len : 20 ); i > 0; i-- )
        {
            System.out.print( " " );
        }
        System.out.println( timeSpent + "ms" );
        Thing.counter = 1;
        return timeSpent;
    }

    private Thing createThing( ArrayList<Thing> all, int level, int maxLevels )
    {
        Thing thing = new Thing();
        if( level < maxLevels )
        {
            for( int i = 0; i < 4; i++ )
            {
                Thing child = createThing( all, level + 1, maxLevels );
                thing.uses.add( child );
            }
        }
        return thing;
    }

    public class Userator
        implements UsageGraph.Use<Thing>
    {

        public List<Thing> uses( Thing source )
        {
            return source.uses;
        }
    }

    public static class Thing
    {
        private static int counter = 1;

        private List<Thing> uses = new ArrayList<Thing>();
        private String name = "Thing" + counter++;

        public String toString()
        {
            return name;
        }
    }
}
