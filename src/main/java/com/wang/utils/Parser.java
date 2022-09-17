package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class Parser {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Value parse(Type t){
        Value r = null;
        
        if (t instanceof ParameterizedType){
            Type raw = ((ParameterizedType)t).getRawType();

            if (Collection.class.isAssignableFrom((Class<?>)raw)){
                Type ext = ((ParameterizedType)t).getActualTypeArguments()[0]; 
                r = new CollectionValue((Class<? extends Collection>)raw, parse(ext));
            }else if (Map.class.isAssignableFrom((Class<?>)raw)){
                Type[] param = ((ParameterizedType)t).getActualTypeArguments();
                return new MapValue((Class<?>)raw, (Class<?>)param[0], (Class<?>)param[1]);
            }else
                throw new UnsupportedOperationException("Unsupport type[" + t + "].");
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
