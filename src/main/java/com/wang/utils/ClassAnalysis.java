package com.wang.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassAnalysis {
    Class<?> clazz;
    Constructor<?>[] ctor;
    Parameter[] params;
    Object[] values;

    @SuppressWarnings("unused")
    private ClassAnalysis(){}

    public ClassAnalysis(Class<?> clazz) throws Throwable{
        this.clazz = clazz;

        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isStatic(clazz.getModifiers()) ||
            Modifier.isInterface(clazz.getModifiers())) 
            return;

        ctor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(el -> Modifier.isPublic(el.getModifiers()) &&
                    Arrays.stream(el.getGenericParameterTypes()).allMatch(p -> !hasGenericType(p)))
				.sorted((el1, el2) -> ((Integer)el1.getParameterCount()).compareTo((Integer)el2.getParameterCount()))
                .toArray(Constructor<?>[]::new);
        if (ctor.length == 0) return;

        params = ctor[0].getParameters();

        values = new Object[params.length];
        for (int i = 0; i < values.length; i++){
            values[i] = Parser.parse(params[i]).set();
        }
    }

    public Object newInstance() throws Throwable{
        return ctor.length == 0 ? null : ctor[0].newInstance(values);
    }

    public int getParameterCount(){
        return ctor.length == 0 ? -1 : params.length;
    }

    public Method[] getMethods(){
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(el -> Modifier.isPublic(el.getModifiers()) &&
                    Arrays.stream(el.getGenericParameterTypes()).allMatch(p -> !hasGenericType(p)))
                .toArray(Method[]::new);
    }

    public String toString(){
        record mapper(Constructor<?> c, String s){}

        Function<Constructor<?>, String> get = (c)->{
            Type[] ps = c.getGenericParameterTypes();
            return IntStream.range(0, ps.length).mapToObj(i -> getTypeName(ps[i]) + " arg" + i)
                    .collect(Collectors.joining(", ")) + ")\n";
        };

        return clazz.getName() + "'s constructors: \n" + Arrays.stream(clazz.getDeclaredConstructors())
            .map(c ->new mapper(c, " " + (params != null && params.length == c.getParameterCount() ? "*" : " ") + 
                (Modifier.isPublic(c.getModifiers()) ? "public  " : "private ") + c.getName() + "("))
            .map(el -> el.s + get.apply(el.c)).collect(Collectors.joining());
    }

    @SuppressWarnings("rawtypes")
    private String getTypeName(Type t){
        return switch(t){
            case TypeVariable c      -> c.getName();
            case ParameterizedType c -> ((Class<?>)c.getRawType()).getSimpleName() + "<"
                                        + Arrays.stream(c.getActualTypeArguments())
                                        .map(el->getTypeName(el)).collect(Collectors.joining(", ")) + ">";
            case GenericArrayType c  -> getTypeName(c.getGenericComponentType()) + "[]";
            default                  -> ((Class<?>)t).getSimpleName();
        };
    }

    public static boolean hasGenericType(Type t){
        if (t instanceof TypeVariable) return true;
        if (t instanceof ParameterizedType){
            for (Type el : ((ParameterizedType)t).getActualTypeArguments())
                if (hasGenericType(el)) return true;
        }
        if(t instanceof GenericArrayType) 
            if (hasGenericType(((GenericArrayType)t).getGenericComponentType()))
                return true;
                
        return false;
    }
}
