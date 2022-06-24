package Testing;

import java.util.HashMap;

public class Test1 {
    public static HashMap<Integer, String> myMap = new HashMap<Integer, String>();

    public static void main(String[] args) {
        myMap.putIfAbsent(9, "Victoria");
        myMap.put(8, "Orange");
        System.out.println("Inserted in HasMap");
    }
}
