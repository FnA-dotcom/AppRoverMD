package Testing;

import java.util.Map;
import java.util.Set;

public class Test2 extends Test1 {
    public static void main(String[] args) {

        Set set = myMap.entrySet();
        for (Object aSet : set) {
            Map.Entry mentry = (Map.Entry) aSet;
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }

    }
}
