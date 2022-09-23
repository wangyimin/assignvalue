package com.wang.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MapValue<K, V> extends GenericValue{
    Class<K> key;
    Class<V> value;
    Value extKey;
    Value extValue;

    @SuppressWarnings("rawtypes")
    public MapValue(Class<? extends Map> clazz, Class<K> key, Class<V> value, Value extKey, Value extValue){
        super(clazz);
        this.key = key;
        this.value = value;
        this.extKey = extKey;
        this.extValue = extValue;
    }
    
    @SuppressWarnings("unchecked")
    public Object set(){
        Map<K, V> map = Map.of((K)extKey.set(), (V)extValue.set());
        try{
            return clazz.isInterface() ? map :
                clazz.getConstructor(Map.class).newInstance(map); 
        }catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignore ){}
        return null;
    }

    public String show(){
        return "new " + MapValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "<" +
            key.getSimpleName() + ", " + value.getSimpleName() + ">, " +
            extKey.show() + ", " + extValue.show() + ")";
    }
}
