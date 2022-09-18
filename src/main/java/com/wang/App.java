package com.wang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wang.demo.Data;
import com.wang.utils.Parser;
import com.wang.utils.Value;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    @SuppressWarnings("unused")
    private void test(List<Map<String, List<Data[]>>> lst){
        System.out.println("test is running...");
        //lst.get(0).get("20220917").get(0)[0].map.toString()
    }
    //List<List<Map<String, Data[]>>> lst;
    HashMap<ArrayList<Set<Map<String, String>>>, String[]> lst;
    
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Throwable{
        //Value v = 
        //    new CollectionValue(List.class, 
        //        new CollectionValue(List.class, new GenericValue(String.class)));

        App app = new App();

        Field f = App.class.getDeclaredField("lst");
        Value v = Parser.parse(f);
        f.set(app, v.set());
        //app.lst.get(0).get(0).get("20220917")[0].map.toString()

        Method m = Arrays.asList(App.class.getDeclaredMethods()).stream().filter(el->el.getName().equals("test")).findFirst().get();
        Parameter p = m.getParameters()[0];
        v = Parser.parse(p);
        m.invoke(app, v.set());
        
        System.out.println("Hello World!");
    }
}
