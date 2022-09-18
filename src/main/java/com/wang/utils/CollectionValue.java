package com.wang.utils;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionValue<E> extends GenericValue{
    Class<E> element;
    Value ext;

    @SuppressWarnings("rawtypes")
    public CollectionValue(Class<? extends Collection> clazz, Class<E> element, Value ext){
        super(clazz);
        this.element = element;
        this.ext = ext;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object set() throws Throwable{
        Collection<E> lst = (Collection<E>)new ArrayList<>();
        lst.add((E)ext.set());

        if (!clazz.isInterface()) 
            return clazz.getConstructor(Collection.class).newInstance(lst); 
        return lst;
    }

    public String show(){
        return "new " + CollectionValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "<" +
            element.getSimpleName() + ">, " + ext.show() + ")";
    }
}
