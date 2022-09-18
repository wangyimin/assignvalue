package com.wang.utils;

public class GenericValue implements Value{
    Class<?> clazz;

    public GenericValue(Class<?> clazz){
        this.clazz = clazz;
    }

    public Object set() throws Throwable{
        return values.get(clazz);
    } 

    public String show(){
        return "new " + GenericValue.class.getSimpleName() + "(" + clazz.getSimpleName() + ")";
    }
}
