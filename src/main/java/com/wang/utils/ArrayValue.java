package com.wang.utils;

import java.lang.reflect.Array;

public class ArrayValue extends GenericValue{
    Value ext;

    public ArrayValue(Class<?> clazz, Value ext){
        super(clazz);
        this.ext = ext;
    }
    
    public Object set(){
        Object obj = Array.newInstance(clazz, 1);
		
        Object el = ext.set();
		if (el != null) Array.set(obj, 0, el);
	
		return obj;
    }

    public String show(){
        return "new " + ArrayValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "[])";
  }
}
