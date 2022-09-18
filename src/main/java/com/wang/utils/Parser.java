package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class Parser {
    @SuppressWarnings({"rawtypes", "unchecked"})
    static Function<Type, Value> parameterizedType = (t) -> {
        Type raw = ((ParameterizedType)t).getRawType();
            
        if (Collection.class.isAssignableFrom((Class<?>)raw)){
            //配列
            Type ext = ((ParameterizedType)t).getActualTypeArguments()[0]; 

            return new CollectionValue((Class<? extends Collection>)raw, parse(ext));
        }else if (Map.class.isAssignableFrom((Class<?>)raw)){
            //Map
            Type[] param = ((ParameterizedType)t).getActualTypeArguments();
            Type key = param[0] instanceof ParameterizedType ? ((ParameterizedType)param[0]).getActualTypeArguments()[0] : param[0];
            Type value = param[1] instanceof ParameterizedType ? ((ParameterizedType)param[1]).getActualTypeArguments()[0] : param[1];
                
            return new MapValue((Class<? extends Map>)raw, (Class<?>)key, (Class<?>)value, parse(param[0]), parse(param[1]));
        }else
            throw new UnsupportedOperationException("Unsupport type[" + t + "].");
    };
    
    static Function<Type, Value> genericType = (t) -> {
        if (((Class<?>)t).isArray()){
            return new ArrayValue(((Class<?>)t).getComponentType());
        }else{
            return Value.values.containsKey(t) ? 
                new GenericValue((Class<?>)t) : new PojoValue((Class<?>)t);
        }
    };
    
    public static Value parse(Type t){
        if (t instanceof ParameterizedType)
            return parameterizedType.apply(t);
        else
            return genericType.apply(t);
    }

    public static Value parse(Field f){
        return f.getType().isArray() ? parse(f.getType()) : parse(f.getGenericType());
    }

    public static Value parse(Parameter p){
        if (p.getParameterizedType() != null) return parse(p.getParameterizedType());
        return parse(p.getType());
    }
}
