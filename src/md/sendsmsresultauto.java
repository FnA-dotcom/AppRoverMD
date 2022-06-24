package md;

import DAL.TwilioSMSConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class sendsmsresultauto {

    public static void main(String[] args) {

        Connection conn = null;
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = null;

        try {
            conn = getConnectionLocal();
            String t_id = "";
            String o_id = "";
            String PhNumber = "";
            String path = "";
            String filename = "";
            String Database = "roverlab";
            String facilityid = "36";
            String mrn = "";
            String ordernum = "";

            Query = " SELECT c.id,b.id, IFNULL(a.PhNumber,''), IFNULL(c.Reportpath,''),IFNULL(c.filename,''),a.MRN,b.OrderNum "
                    + " FROM  " + Database + ".PatientReg a\r\n" +
                    " LEFT JOIN  " + Database + ".TestOrder b ON a.ID=b.PatRegIdx \r\n" +
                    " LEFT JOIN  " + Database + ".Tests c ON b.ID=c.OrderId \r\n" +
                    " where  b.status=6 and b.email=0 limit 50";
            System.out.println(Query);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                String mstats = "2";
                t_id = hrset.getString(1);
                o_id = hrset.getString(2);
                PhNumber = hrset.getString(3);
                path = hrset.getString(4);
                filename = hrset.getString(5);
                mrn = hrset.getString(6);
                ordernum = hrset.getString(7);
                if (PhNumber.length() != 0) {


                    mstats = sendsms(conn, t_id, o_id, PhNumber, ordernum, mrn, Database);


                }

                updatestatus(conn, o_id, mstats, Database);


            }

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println(e.getMessage());
        }


    }

    public static String updatestatus(Connection conn, String o_id, String mstatus, String Database) {
        try {
            Statement hstmt = null;


            hstmt = conn.createStatement();
            System.out.println("UPDATE " + Database + ".TestOrder SET status=7,sms=" + mstatus + ",smstime=now() WHERE id='" + o_id + "'");
            hstmt.executeUpdate("UPDATE " + Database + ".TestOrder SET status=7, sms=" + mstatus + ",smstime=now() WHERE id='" + o_id + "'");
            hstmt.close();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static String sendsms(Connection conn, String t_id, String o_id, String phone, String ordernum, String mrn, String Database) {

        String Status = "2";
        String url = "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + ordernum + "&m=" + mrn;
        String msg = "This message is from Primescope Diagnostics. Your results are in. To access,\r\n" +
                "click the following link and provide your DOB.\r\n" +
                url;
        String PtMRN = "0";
        int f_id = 36;
        TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();


        phone = "4372344164";
        String[] result = smsConfiguration.sendTwilioMessages(null, conn, null, msg, f_id, phone, 67, Database, PtMRN, 0);


        if (result[0].equals("Success")) {
            Status = "1";
        }


        return "1";
    }

    private static Connection getConnectionLocal() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://3.238.87.114:33306/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

}
