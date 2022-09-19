package com.wang.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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

        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isStatic(clazz.getModifiers())) return;

        ctor = Arrays.stream(clazz.getDeclaredConstructors()).filter(el -> Modifier.isPublic(el.getModifiers()))
				.sorted((el1, el2) -> ((Integer)el1.getParameterCount()).compareTo((Integer)el2.getParameterCount()))
                .toArray(Constructor<?>[]::new);

        params = ctor[0].getParameters();

        values = new Object[params.length];
        for (int i = 0; i < values.length; i++){
            values[i] = Parser.parse(params[i]).set();
        }
    }

    public Object newInstance() throws Throwable{
        return this.clazz == null ? null : ctor[0].newInstance(values);
    }

    public int getParameterCount(){
        return this.clazz == null ? -1 : values.length;
    }
}
