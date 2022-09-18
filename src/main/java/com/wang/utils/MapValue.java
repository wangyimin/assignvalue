package com.wang.utils;

import java.util.HashMap;
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
    
    @Override
    @SuppressWarnings("unchecked")
    public Object set() throws Throwable{
        Map<K, V> map = new HashMap<>();
        map.put((K)extKey.set(), (V)extValue.set());

        if (!clazz.isInterface()) 
            return clazz.getConstructor(Map.class).newInstance(map); 
        
        return map;
    }
}
