package oe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class reporttest {

    public static void main(String[] args) {

        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement hstmt2 = null;
        ResultSet hrset2 = null;
        String Query2 = "";
        int requestedMonthInt =10;
        String clientid="";
        String clientname="";
        String requestedYearString="2020";
        String requestedMonthString="10";


        int requestedMonthLength = 0;
        int broughtMonthLength = 0;
        if ((requestedMonthInt == 1) || (requestedMonthInt == 3) || (requestedMonthInt == 5) || (requestedMonthInt == 7) || (requestedMonthInt == 8) || (requestedMonthInt == 10) || (requestedMonthInt == 12)) {
            requestedMonthLength = 31;
        } else if (requestedMonthInt == 2) {
            requestedMonthLength = 28;
        } else {
            requestedMonthLength = 30;
        }

        Connection conn=getConnectionlocal();
        Query="select id,name,directory_1,remotedirectory,tablename from clients where id not in (1,9,10,12)";
        try {
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);

            while (hrset.next())
            {
                clientid = hrset.getString(1);
                clientname = hrset.getString(2);
                System.out.print(clientid+"|"+clientname+"|");
                String monthcount="";
                for (int i = 1; i <= requestedMonthLength; i++)
                {
                    Query2 = "SELECT COUNT(*) FROM filelogs_sftp WHERE clientdirectory=" + clientid + " AND substr(dosdate,1,10)='" + requestedYearString + "-" + requestedMonthString + "-" + String.format("%02d", new Object[] { Integer.valueOf(i) }) + "' ";
                    // System.out.println(Query2);
                    hstmt2 = conn.createStatement();
                    hrset2 = hstmt2.executeQuery(Query2);
                    hrset2.next();
                    System.out.print(hrset2.getString(1));// + "," + monthcount;
                    //monthcount = hrset2.getString(1);// + "," + monthcount;
                    hrset2.close();
                    hstmt2.close();
//                    System.out.print(monthcount);
                }
                System.out.println(",");



            }
        }catch(Exception ee) {

            System.out.println(ee.getMessage());

        }

    }
    private static Connection getConnectionlocal()
    {
        try{
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Connection connection = DriverManager.getConnection("jdbc:mysql://54.167.174.84/oe?user=abdf890092&password=980293339jjjj");

            //Connection connection = DriverManager.getConnection("jdbc:mysql://54.80.137.178/oe?user=abdf890092&password=980293339jjjj");
            return connection;
        }catch (Exception e)
        {
            System.out.println("PL"+e.getMessage());
            return null;
        }
    }

}