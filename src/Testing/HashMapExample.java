package Testing;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HashMapExample {
    static ResultSet rst = null;
    static Statement stmt = null;
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;

    public static void main(String[] args) {
        String Query1 = "";
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);
            HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            Query1 = "SELECT Id,dbname FROM oe.clients WHERE status = 0";
            stmt = conn.createStatement();
            rst = stmt.executeQuery(Query1);
            while (rst.next()) {
                hashMap.put(rst.getInt(1), rst.getString(2));
            }
            rst.close();
            stmt.close();
            System.out.println("Iterating Hashmap...");
            for (Map.Entry m : hashMap.entrySet()) {
                System.out.println(m.getKey() + " " + m.getValue());
            }

            String var = hashMap.get(8);
            String Database = "";
            //hashMap.putIfAbsent(7,"Ascent");

            if (var == null)
                Database = "oe";
            else
                Database = var;
            System.out.println("Value  is: " + Database);
        } catch (Exception e) {
            e.printStackTrace();
        }

/*        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("1", "11");
        multimap.put("1", "14");
        multimap.put("1", "12");
        multimap.put("1", "13");
        multimap.put("11", "111");
        multimap.put("12", "121");*/
//        System.out.println(multimap);
//        System.out.println(multimap.get("11"));

/*        Multimap<String, Integer> multimap1 = ArrayListMultimap.create();
        multimap1.put("monica", 8);
        multimap1.put("tiffany", 10);
        multimap1.put("nelda", 8);
        multimap1.put("nelda", 34);
        multimap1.put("nelda", 36);
        System.out.println(multimap1);
        System.out.println(multimap1.get("nelda"));*/
        /*        *//* This is how to declare HashMap *//*
        HashMap<ArrayList<String>, Integer> hmap = new HashMap<ArrayList<String>, Integer>();

        ArrayList<String> alist=new ArrayList<String>();
        alist.add("monica");
        alist.add("tiffany");
        alist.add("nelda");

        hmap.put(alist, 8);
        *//*Adding elements to HashMap
        hmap.put("monica", 8);
        hmap.put("tiffany", 10);
        hmap.put("nelda", 8);
        hmap.put("nelda", 34);
*//*

         *//* Display content using Iterator*//*
        Set set = hmap.entrySet();
        for (Object aSet : set) {
            Map.Entry mentry = (Map.Entry) aSet;
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }*/
/*
        Enumeration names;
        String key;

        // Creating a Hashtable
        Hashtable<String, String> hashtable =
                new Hashtable<String, String>();

        // Adding Key and Value pairs to Hashtable
        hashtable.put("Key1","Chaitanya");
        hashtable.put("Key2","Ajeet");
        hashtable.put("Key3","Peter");
        hashtable.put("Key4","Ricky");
        hashtable.put("Key5","Mona");
        hashtable.put("Key5","Mona11");

        names = hashtable.keys();
        while(names.hasMoreElements()) {
            key = (String) names.nextElement();
            System.out.println("Key: " +key+ " & Value: " + hashtable.get(key));
        }*/
/*//Define a TreeMap with a custom Comparator
        Map<Integer, String> map = new TreeMap<>((a, b) -> 1); // See notes 1 and 2

//Populate the map
        map.put(1, "One");
        map.put(3, "Three");
        map.put(1, "One One");
        map.put(7, "Seven");
        map.put(2, "Two");
        map.put(1, "One One One");

//Display the map entries:
        map.entrySet().forEach(System.out::println);

//See note number 3 for the following:
        Map<Integer, String> sortedTreeMap = map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (x, y) -> x, () -> new TreeMap<>((a, b) -> 1)
                ));
//Display the entries of this sorted TreeMap:
        sortedTreeMap.entrySet().forEach(System.out::println);*/

    }
}
