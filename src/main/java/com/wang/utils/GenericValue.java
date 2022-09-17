package com.wang.utils;

public class GenericValue implements Value{
    Class<?> clazz;

    public GenericValue(){}

    public GenericValue(Class<?> clazz){
        this.clazz = clazz;
    }

    public Object set() throws Throwable{
        return values.get(clazz);
    } 
}
