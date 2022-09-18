package com.wang.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PojoValue extends GenericValue{
	static Map<String, Object> pojos = new HashMap<>();
	
    public PojoValue(Class<?> clazz){
        super(clazz);
    }

    @Override
    public Object set() throws Throwable{
		final Function<Class<?>, Boolean> isPOJO = (clazz) ->{
			return Modifier.isAbstract(clazz.getModifiers()) || Modifier.isStatic(clazz.getModifiers()) ? false :
						Arrays.asList(clazz.getDeclaredConstructors()).stream()
							.allMatch(el -> Modifier.isPublic(el.getModifiers()) && el.getParameterCount() == 0);
		};

        @SuppressWarnings("rawtypes")
		final BiFunction<Class<?>, Object, Boolean> isNull = (c, v) -> {
			if (v == null)                            return true;
			if (Collection.class.isAssignableFrom(c)) return ((Collection)v).size() == 0;
			if (Map.class.isAssignableFrom(c))        return ((Map)v).entrySet().size() == 0;
			if (c.isArray())                          return Array.getLength(v) == 0;

			return false;
		};

		if (!isPOJO.apply(clazz)) return null;		
        Object obj = clazz.getConstructor().newInstance();
		pojos.put(clazz.getName(), obj);

		Field[] fs = clazz.getDeclaredFields();
		for (Field f : fs) {
			if (Modifier.isStatic(f.getModifiers())) continue;
			
			f.setAccessible(true);
            if (!isNull.apply(f.getType(), f.get(obj))) continue;

			f.set(obj, pojos.containsKey(f.getType().getName())?pojos.get(f.getType().getName()):Parser.parse(f).set());
		}
		return obj;
    }

	public String show(){
		return "new " + PojoValue.class.getSimpleName() + "(" + clazz.getSimpleName() + ")";
	}
}
