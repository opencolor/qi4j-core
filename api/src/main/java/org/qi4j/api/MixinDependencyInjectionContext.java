package org.qi4j.api;

import java.util.List;
import java.util.Map;
import org.qi4j.api.model.CompositeContext;

/**
 * TODO
 */
public class MixinDependencyInjectionContext
    extends DependencyInjectionContext
{
    Object[] properties;
    List adapt;
    List decorate;

    public MixinDependencyInjectionContext( CompositeContext context, Object thisAs, Object[] properties, List adapt, List decorate )
    {
        super( context, thisAs );
        this.properties = properties;
        this.adapt = adapt;
        this.decorate = decorate;
    }

    public Object[] getProperties()
    {
        return properties;
    }

    public Iterable getAdapt()
    {
        return adapt;
    }

    public Iterable getDecorate()
    {
        return decorate;
    }
}