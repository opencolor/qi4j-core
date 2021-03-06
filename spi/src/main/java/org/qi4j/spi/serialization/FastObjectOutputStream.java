package org.qi4j.spi.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * JAVADOC
 */
public final class FastObjectOutputStream
    extends ObjectOutputStream
{
    private final boolean usesWriteObjectMethod;

    public FastObjectOutputStream( OutputStream outputStream, boolean usesWriteObjectMethod )
        throws IOException
    {
        super( outputStream );
        this.usesWriteObjectMethod = usesWriteObjectMethod;
    }

    @Override protected void writeClassDescriptor( ObjectStreamClass objectStreamClass )
        throws IOException
    {
        if( usesWriteObjectMethod )
        {
            super.writeClassDescriptor( objectStreamClass );
        }
        else
        {
            writeUTF( objectStreamClass.getName() );
        }
    }
}
