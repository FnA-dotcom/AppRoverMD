package Testing;

public class Test {
    public static void main(String[] args) {
        String str = "3adebaf66c714797b7114a8ce8d899b4;expires=2021-08-31T19:45:35.704118Z";
        String[] arrOfStr = str.split(";", 2);
/*

        for (String a : arrOfStr)
            System.out.println(a);

        String s = "quick,brown,fox,jumps,over,the,lazy,dog";
        int from = s.indexOf(',');
        int to = s.indexOf(',', from+1);
        String brown = s.substring(from+1, to);
        System.out.println(brown);*/
        System.out.println(arrOfStr[1]);

    }
}
