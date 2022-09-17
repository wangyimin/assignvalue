package com.wang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

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
    private void test(List<List<Data[]>> lst){
        System.out.println("test is running...");
    }
    List<List<Data[]>> lst;
    //Data lst;
    
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

        Method m = Arrays.asList(App.class.getDeclaredMethods()).stream().filter(el->el.getName().equals("test")).findFirst().get();
        Parameter p = m.getParameters()[0];
        v = Parser.parse(p);
        m.invoke(app, v.set());
        
        System.out.println("Hello World!");
    }
}
