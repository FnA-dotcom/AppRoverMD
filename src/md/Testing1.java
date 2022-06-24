package md;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class Testing1 {
    String a = "Test";
    HashMap<Integer, String> myMap = new HashMap<Integer, String>();

    void myMethod(final HttpServletRequest request, final HttpServletResponse response) {

        System.out.println("INSIDE Testing 1");
        System.out.println("Inserting data....");
        myMap.putIfAbsent(9, "Victoria");
        myMap.put(8, "Orange");
        System.out.println("Inserted in HasMap");

    }
}
