package com.wang.utils;

import java.util.HashMap;
import java.util.Map;

public class MapValue<K, V> extends GenericValue{
    Class<K> key;
    Class<V> value;

    @SuppressWarnings("rawtypes")
    public MapValue(Class<? extends Map> clazz, Class<K> key, Class<V> value){
        super(clazz);
        this.key = key;
        this.value = value;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object set() throws Throwable{
        Map<K, V> map = new HashMap<>();
        map.put((K)Parser.parse(key).set(), (V)Parser.parse(value).set());
        return map;
    }
}
