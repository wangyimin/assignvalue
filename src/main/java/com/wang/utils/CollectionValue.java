package com.wang.utils;

import java.lang.reflect.InvocationTargetException;
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
    public Object set(){
        Collection<E> lst = List.of((E)ext.set());
        try{
            return clazz.isInterface() ? lst :
                clazz.getConstructor(Collection.class).newInstance(lst); 
        }catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignore ){}
        return null;
    }

    public String show(){
        return "new " + CollectionValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "<" +
            element.getSimpleName() + ">, " + ext.show() + ")";
    }
}
