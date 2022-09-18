package com.wang.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.BiFunction;

public class PojoValue extends GenericValue{
    public PojoValue(Class<?> clazz){
        super(clazz);
    }

    @Override
    public Object set() throws Throwable{
        @SuppressWarnings("rawtypes")
		final BiFunction<Field, Object, Boolean> isNull = (f, v) -> {
			if (v == null)                                      return true;
			if (Collection.class.isAssignableFrom(f.getType())) return ((Collection)v).size() == 0;
			if (f.getType().isArray())                          return Array.getLength(v) == 0;

			return false;
		};
        
        Object obj = clazz.getConstructor().newInstance();

		Field[] fs = clazz.getDeclaredFields();
		for (Field f : fs) {
			if (Modifier.isStatic(f.getModifiers())) continue;
			
			f.setAccessible(true);
            if (!isNull.apply(f, f.get(obj))) continue;

			Value value = Parser.parse(f);
            f.set(obj, value.set());
		}
		return obj;
    }
}
