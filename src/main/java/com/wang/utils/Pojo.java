package com.wang.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Pojo {
    default boolean isPojo(Class<?> clazz) {return true;}
    Parser getParser();

    static boolean hasGenericType(Type type){
        if (type instanceof TypeVariable) return true;
        if (type instanceof ParameterizedType){
            for (Type el : ((ParameterizedType)type).getActualTypeArguments())
                if (hasGenericType(el)) return true;
        }
        if(type instanceof GenericArrayType) 
            if (hasGenericType(((GenericArrayType)type).getGenericComponentType()))
                return true;
                
        return false;
    }

    @SuppressWarnings("rawtypes")
    static String getTypeName(Type t){
        return switch(t){
            case TypeVariable c      -> c.getName();
            case ParameterizedType c -> ((Class<?>)c.getRawType()).getSimpleName() + "<"
                                        + Arrays.stream(c.getActualTypeArguments())
                                        .map(el->getTypeName(el)).collect(Collectors.joining(", ")) + ">";
            case GenericArrayType c  -> getTypeName(c.getGenericComponentType()) + "[]";
            default                  -> ((Class<?>)t).getSimpleName();
        };
    }

    static String getConstructorDescriptions(Class<?> clazz){
        record mapper(Constructor<?> c, String s){}

        Function<Integer, String> getModifier = (mod) ->
            Modifier.isPublic(mod)?"public" : (
                Modifier.isProtected(mod)?"protected" : (
                    Modifier.isPrivate(mod)?"private" : "Unknown")
                );
 
        Function<Constructor<?>, String> getParamater = (c)->{
            Type[] ps = c.getGenericParameterTypes();

            return 
                IntStream.range(0, ps.length).mapToObj(i -> getTypeName(ps[i]) + " arg" + i)
                    .collect(Collectors.joining(", ")) + ")\n";
        };

        return clazz.getName() + "'s constructors: \n" 
                + Arrays.stream(clazz.getDeclaredConstructors())
                .map(c -> new mapper(c, "  " +  getModifier.apply(c.getModifiers()) + " " + c.getName() + "("))
                .map(el -> el.s + getParamater.apply(el.c)).collect(Collectors.joining());
    }

    static boolean canEvaluable(Class<?> clazz){
		return !clazz.isPrimitive() && !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers()); 
	}

    default Object initialzie(Class<?> clazz){
        if (!canEvaluable(clazz) || !isPojo(clazz)) return null;
        
        Constructor<?> ctor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(el -> Modifier.isPublic(el.getModifiers()) &&
                    Arrays.stream(el.getGenericParameterTypes()).allMatch(p -> !Pojo.hasGenericType(p)))
                .sorted((el1, el2) -> ((Integer)el1.getParameterCount()).compareTo((Integer)el2.getParameterCount()))
                .findFirst().orElse(null);

        if (ctor == null) return null;
        Object[] values = Arrays.stream(ctor.getParameters()).map(el -> getParser().parse(el).set()).toArray();
        try{
            return ctor.newInstance(values);
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException ignore ){}
        
        return null;
    }
}
