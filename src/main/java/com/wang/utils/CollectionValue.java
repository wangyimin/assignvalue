package com.wang.utils;

import java.util.Collection;
import java.util.List;

public class CollectionValue<E> extends GenericValue{
    Class<E> element;
    Value ext;

    @SuppressWarnings("rawtypes")
    public CollectionValue(Class<? extends Collection> clazz, Class<E> element, Value ext){
        super(clazz);
        this.element = element;
        this.ext = ext;
    }
    
    @SuppressWarnings("unchecked")
    public Object set() throws Throwable{
        Collection<E> lst = List.of((E)ext.set());

        return clazz.isInterface() ? lst :
            clazz.getConstructor(Collection.class).newInstance(lst); 
    }

    public String show(){
        return "new " + CollectionValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "<" +
            element.getSimpleName() + ">, " + ext.show() + ")";
    }
}
