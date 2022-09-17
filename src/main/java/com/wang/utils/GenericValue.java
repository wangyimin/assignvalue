package com.wang.utils;

public class GenericValue implements Value{
    Class<?> clazz;
    Value ext;

    public GenericValue(){}

    public GenericValue(Class<?> clazz){
        this.clazz = clazz;
    }

    public GenericValue(Class<?> clazz, Value ext){
        this(clazz);
        this.ext = ext;
    }

    public Object set() throws Throwable{
        return values.get(clazz);
    } 
}
