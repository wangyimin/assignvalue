package com.wang.utils;

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
    public Object set() throws Throwable{
        Map<K, V> map = Map.of((K)extKey.set(), (V)extValue.set());

        return clazz.isInterface() ? map :
             clazz.getConstructor(Map.class).newInstance(map); 
    }

    public String show(){
        return "new " + MapValue.class.getSimpleName() + "(" + clazz.getSimpleName() + "<" +
            key.getSimpleName() + ", " + value.getSimpleName() + ">, " +
            extKey.show() + ", " + extValue.show() + ")";
    }
}
