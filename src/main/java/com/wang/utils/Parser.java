package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class Parser {
    public static Value parse(Type t){
        Value r = null;
        
        if (t instanceof ParameterizedType){
            @SuppressWarnings({"rawtypes", "unchecked"})
            Class<? extends Collection> clazz = (Class<? extends Collection>)((ParameterizedType)t).getRawType();
            Type ext = ((ParameterizedType)t).getActualTypeArguments()[0]; 

            r = new CollectionValue(clazz, parse(ext));
        }else if (((Class<?>)t).isArray()){
            return new ArrayValue(((Class<?>)t).getComponentType());
        }else{
            return Value.values.containsKey(t) ? 
                new GenericValue((Class<?>)t) : new PojoValue((Class<?>)t);
        }
    	return r;
    }

    public static Value parse(Field f){
        Type t = f.getType();
        return ((Class<?>)t).isArray() ? parse(t) : parse(f.getGenericType());
    }
    
}
