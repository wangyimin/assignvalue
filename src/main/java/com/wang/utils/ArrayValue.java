package com.wang.utils;

import java.lang.reflect.Array;

public class ArrayValue extends GenericValue{
    public ArrayValue(Class<?> clazz){
        super(clazz);
    }
    
    @Override
    public Object set() throws Throwable{
		Object obj = Array.newInstance(clazz, 1);
		
		Object el = Parser.parse(clazz).set();
		if (el != null) Array.set(obj, 0, el);
	
		return obj;
    }

    public String show(){
      return "new " + ArrayValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "[])";
  }
}
