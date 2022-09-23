package com.wang.utils;

public class GenericValue implements Value{
    Class<?> clazz;
    Parser parser;

    public GenericValue(Class<?> clazz, Parser parser){
        this.clazz = clazz;
        this.parser = parser;
    }

    public GenericValue(Class<?> clazz){
        this.clazz = clazz;
        this.parser = new Parser.ParserImpl();
    }

    public Object set(){
        return values.get(clazz);
    } 

    public String show(){
        return "new " + GenericValue.class.getSimpleName() + "(" + clazz.getSimpleName() + ")";
    }
}
