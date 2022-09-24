package com.wang.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PojoValue extends GenericValue implements Pojo{
	static Map<String, Object> pojos = new HashMap<>();
	
    public PojoValue(Class<?> clazz){
        super(clazz);
    }

	public Parser getParser(){
		return Parser.getDefaultParser();
	}

    public Object set(){
        @SuppressWarnings("rawtypes")
		final BiFunction<Class<?>, Object, Boolean> isNull = (c, v) -> {
			if (v == null)                            return true;
			if (Collection.class.isAssignableFrom(c)) return ((Collection)v).size() == 0;
			if (Map.class.isAssignableFrom(c))        return ((Map)v).entrySet().size() == 0;
			if (c.isArray())                          return Array.getLength(v) == 0;

			return false;
		};

		Object obj = initialzie(clazz);
		if (obj == null) return null;

		pojos.put(clazz.getName(), obj);

		Field[] fs = clazz.getDeclaredFields();
		for (Field f : fs) {
			if (Modifier.isStatic(f.getModifiers())) continue;
			if (Pojo.hasGenericType(f.getGenericType())) continue;
			
			f.setAccessible(true);

			try{
            	if (!isNull.apply(f.getType(), f.get(obj))) continue;
				f.set(obj, pojos.containsKey(f.getType().getName())?pojos.get(f.getType().getName()):getParser().parse(f).set());
			}catch(IllegalAccessException ignore){}
		}
		return obj;
    }

	public String show(){
		return "new " + PojoValue.class.getSimpleName() + "(" + clazz.getSimpleName() + ")";
	}
}
