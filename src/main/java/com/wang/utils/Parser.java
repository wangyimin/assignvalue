package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
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
        return f.getType().isArray() ? parse(f.getType()) : parse(f.getGenericType());
    }

    public static Value parse(Parameter p){
        if (p.getParameterizedType() != null) return parse(p.getParameterizedType());
        if (p.getType().isArray()) return parse(p.getType());
        throw new UnsupportedOperationException("Unsupport type[" + p + "].");
    }
}
