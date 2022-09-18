package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class Parser {
    static Function<Type, Type> getRawType = (t) -> {
        return t instanceof ParameterizedType ? ((ParameterizedType)t).getRawType() : t;
    };

    @SuppressWarnings({"rawtypes", "unchecked"})
    static Function<Type, Value> parameterizedType = (t) -> {
        Type raw = getRawType.apply(t);
            
        if (Collection.class.isAssignableFrom((Class<?>)raw)){
            //配列
            Type ext = ((ParameterizedType)t).getActualTypeArguments()[0]; 
            Type element = getRawType.apply(ext);
            
            return new CollectionValue((Class<? extends Collection>)raw, (Class<?>)element, parse(ext));
        }else if (Map.class.isAssignableFrom((Class<?>)raw)){
            //Map
            Type[] param = ((ParameterizedType)t).getActualTypeArguments();
            Type key = getRawType.apply(param[0]);
            Type value = getRawType.apply(param[1]);
                
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
        return parse(f.getGenericType());
    }

    public static Value parse(Parameter p){
        if (p.getParameterizedType() != null) return parse(p.getParameterizedType());
        return parse(p.getType());
    }
}
