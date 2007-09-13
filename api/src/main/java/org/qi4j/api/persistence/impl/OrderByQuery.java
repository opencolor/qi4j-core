package org.qi4j.api.persistence.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.qi4j.api.persistence.Query;

/**
 * TODO
 */
public class OrderByQuery<T> extends QueryDecorator<T>
{
    private OrderBy order = OrderBy.ASCENDING;
    private Method orderBy;

    public OrderByQuery( Query<T> query )
    {
        super( query );
    }

    public <K> K orderBy( Class<K> mixinType )
    {
        InvocationHandler ih = new OrderByInvocationHandler();

        return mixinType.cast(Proxy.newProxyInstance( mixinType.getClassLoader(), new Class[] {mixinType}, ih));
    }

    public <K> K orderBy( Class<K> mixinType, OrderBy ascending )
    {
        this.order = ascending;

        InvocationHandler ih = new OrderByInvocationHandler();

        return mixinType.cast(Proxy.newProxyInstance( mixinType.getClassLoader(), new Class[] {mixinType}, ih));
    }

    public Iterable<T> prepare()
    {
        if (orderBy != null)
            return new OrderedIterable<T>(query.prepare(), orderBy, order );
        else
            return query.prepare();
    }

    public T find()
    {
        return query.find();
    }

    private class OrderByInvocationHandler implements InvocationHandler
    {
        public Object invoke( Object o, Method method, Object[] objects ) throws Throwable
        {
            orderBy = method;

            return null;
        }
    }
}