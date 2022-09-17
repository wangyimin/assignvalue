package com.wang.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class CollectionValue extends GenericValue{
    public CollectionValue(Class<? extends Collection> clazz, Value ext){
        super(clazz, ext);
    }
    
    @Override
    public Object set() throws Throwable{
        final Function<Type, Class<?>> convert = (t) ->{
            if (List.class.equals(t))  return ArrayList.class;
            if (Set.class.equals(t))   return HashSet.class;
            throw new UnsupportedOperationException("Unsupport type[" + t + "].");
        };

        @SuppressWarnings("unchecked")
        java.util.Collection<Object> lst = 
            (Collection<Object>)convert.apply(clazz).getDeclaredConstructor().newInstance();
        lst.add(ext.set());
        return lst;
    }
}
