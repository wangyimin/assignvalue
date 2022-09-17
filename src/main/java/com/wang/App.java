package com.wang;

import java.lang.reflect.Field;
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

    List<List<Data[]>> lst;
    
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Throwable{
        //Value v = 
        //    new CollectionValue(List.class, 
        //        new CollectionValue(List.class, new GenericValue(String.class)));
        Field f = App.class.getDeclaredField("lst");
        Value v = Parser.parse(f);
        
        App app = new App();
        f.set(app, v.set());
        System.out.println("Hello World!");
    }
}
