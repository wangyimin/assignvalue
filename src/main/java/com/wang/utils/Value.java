package com.wang.utils;

import java.lang.reflect.Type;
import java.util.Map;

public interface Value {
    static Map<Type, Object> values = new java.util.HashMap<Type, Object>(){{
            put(String.class, new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
            put(Long.class, 1L);
            put(int.class, 1);
    }};

    Object set();
    default String show(){return "unknown implementation.";};
}
