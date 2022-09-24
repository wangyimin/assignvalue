package com.wang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface Parser {
    Value parse(Type t);

    default Value parse(Field f){
        return parse(f.getGenericType());
    }
    
    default Value parse(Parameter p){
        return p.getParameterizedType() != null ? parse(p.getParameterizedType()) : parse(p.getType());
    }

    static Parser getDefaultParser(){
        return ParserImpl.getParserImpl();
    }

    class ParserImpl implements Parser{
        //Collection、Map等
        Function<Type, Value> parameterizedTypeFunc;
        //Array、Primitive・Wrapper、Class等
        Function<Type, Value> genericTypeFunc;

        static Parser parserImpl;

        Function<Type, Type> getRawType = (t) -> {
            return t instanceof ParameterizedType ? ((ParameterizedType)t).getRawType() : t;
        };
    
        @SuppressWarnings({"rawtypes", "unchecked"})
        Function<Type, Value> parameterizedType = (t) -> {
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
    
        Function<Type, Value> genericType = (t) -> {
            Class<?> c = (Class<?>)t;
            return (c).isArray() ? new ArrayValue(c.getComponentType(), parse(c.getComponentType())) :
                (Value.values.containsKey(t) ? new GenericValue(c) : new PojoValue(c));
        };
        
        ParserImpl(){
            this.parameterizedTypeFunc = parameterizedType;
            this.genericTypeFunc = genericType;
            parserImpl = this;
        }

        public static Parser getParserImpl(){
            return parserImpl == null ? new ParserImpl() : parserImpl;
        }

        public Value parse(Type t){
            return t instanceof ParameterizedType ? parameterizedTypeFunc.apply(t) : genericTypeFunc.apply(t);
        }
    }
}
