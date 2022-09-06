package md;


public class MainTest {

    public static void main(String[] args) throws Exception {

    	String s = "select * from  (select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from";

    	System.out.println(s.length());

    }
    
    

}