package com.wang.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

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
        String name = clazz.getName();
        String r = name + "'s constructors: \n";
        for (Constructor<?> c : clazz.getDeclaredConstructors()){
            r = r + " " + (params != null && params.length == c.getParameterCount() ? "*" : " ") + 
                (Modifier.isPublic(c.getModifiers()) ? "public  " : "private ") + c.getName() + "(";
            
            int i = 0;
            for (Type p : c.getGenericParameterTypes()){
                r = r + getTypeName(p) + " arg" + i + ", ";
                i = i + 1;
            }
            
            if (c.getGenericParameterTypes().length > 0) r = r.substring(0, r.length()-2);
            r = r + ")\n";
        }
        return r;
    }

    @SuppressWarnings("rawtypes")
    private String getTypeName(Type t){
        String r = "";
        if (t instanceof TypeVariable) r = ((TypeVariable)t).getName();
        if (t instanceof ParameterizedType){
            r = ((Class<?>)((ParameterizedType)t).getRawType()).getSimpleName() + "<"; 
            Type[] els = ((ParameterizedType)t).getActualTypeArguments();
            for (Type el : els){
                r = r + getTypeName(el) + ", ";
            }
            if (els.length > 0) r = r.substring(0, r.length()-2) + ">";
        }
        return r;
    }

    public static boolean hasGenericType(Type t){
        if (t instanceof TypeVariable) return true;
        if (t instanceof ParameterizedType){
            Type[] els = ((ParameterizedType)t).getActualTypeArguments();
            for (Type el : els)
                if (hasGenericType(el)) return true;
        }
        return false;
    }
}
