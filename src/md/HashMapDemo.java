package md;

import java.util.HashMap;
import java.util.Map;

class HashMapDemo {
    public static void main(String args[]) {
        // Initialization of a HashMap
        Map<Integer, String> hm
                = new HashMap<Integer, String>();

        // Add elements using put method
        hm.put(1, "Geeks");
        hm.put(2, "For");
        hm.put(3, "Geeks");
        hm.put(4, "For");

        // Initial HashMap
        System.out.println("Mappings of HashMap are : "
                + hm);

        System.out.println(hm.containsValue("eeks"));

    }
}
