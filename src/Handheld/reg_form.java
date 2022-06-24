package Handheld;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class reg_form {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer Htmlstart = new StringBuffer();
        StringBuffer Htmlend = new StringBuffer();
        StringBuffer Htmlfrom = new StringBuffer();

        Htmlstart.append("<!DOCTYPE html><html lang=\"en\">\n<body>\n");
        Htmlstart.append("<head></head>\n");
        Htmlstart.append("<style></style>");
        Htmlstart.append("<body>\n");
        Htmlstart.append("<form novalidate method=\"post\" action=\"/md/md.PatientReg\" id=\"PatientRegForm\" onsubmit=\"return CheckDateofBirth();\">\n");
        Htmlend.append("<form>\n");
        Htmlend.append("<script></script>\n");
        Htmlend.append("</body></html>\n");
        Writer writer = null;
        String id, field_id, field_name, field_seq_no, field_is_required, field_type, field_status, field_lenght, field_size, field_col, custom, attribute;


        Query = "select id,field_id,field_name,field_seq_no,field_is_required,field_type,field_status,field_lenght,field_size,field_col,custom,attribute " +
                " from oe.t_form order by field_seq_no asc";
        try {
            String datafile = "f:/reg.html";
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile), "utf-8"));


            Connection conn = getConnectionlocal();
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            while (hrset.next()) {

                id = hrset.getString(1);
                field_id = hrset.getString(2);
                field_name = hrset.getString(3);
                field_seq_no = hrset.getString(4);
                field_is_required = hrset.getString(5);
                field_type = hrset.getString(6);
                field_status = hrset.getString(7);
                field_lenght = hrset.getString(8);
                field_size = hrset.getString(9);
                field_col = hrset.getString(10);
                custom = hrset.getString(11);
                attribute = hrset.getString(12);
                System.out.println(field_id);
                //   String aa=	gettype(field_type, custom, field_id, field_name, field_is_required,attribute);
                //  System.out.println(aa);
                Htmlfrom.append("<div class=\"" + field_col + "\">\n");
                Htmlfrom.append("<div class=\"form-group\">\n");
                Htmlfrom.append(gettype(field_type, custom, field_id, field_name, field_is_required, attribute) + "\n");
                Htmlfrom.append("<div>\n");
                Htmlfrom.append("<div>\n");
            }

            writer.write(Htmlstart.toString());
            writer.write(Htmlfrom.toString());
            writer.write(Htmlend.toString());
            writer.close();

        } catch (Exception ee) {
            ee.getMessage();
        }


    }


    public static String gettype(String field_type, String Custom, String id, String name, String isrequired, String attribute) {
        String field_r = "";
        if (isrequired.compareTo("0") == 0) {
            isrequired = "";
        } else {
            isrequired = "required";
        }

        System.out.println(field_type);
        if (field_type.compareTo("select") == 0) {
            field_r = "<select class=\"form-control\" id=\"" + id + "\" name=\"" + name + "\" style=\"color:black;\" " + isrequired + " " + attribute + ">" + Custom + "</select>";
        }

        if (field_type.compareTo("label") == 0) {

            field_r = "<label " + attribute + "><font color=\"black\">" + name + " </font></label>";

        }


        if (field_type.compareTo("input-text") == 0) {

            field_r = "<input type=\"text\" placeholder=\" \" class=\"form-control\"  id=\"" + name + "\" name=\"" + name + "\" " + isrequired + " " + attribute + ">";

        }

        if (field_type.compareTo("input-radio") == 0) {

            field_r = "<input type=\"radio\" placeholder=\" \" class=\"form-control\"  id=\"" + name + "\" name=\"" + name + "\" " + isrequired + " " + attribute + " >";

        }
        if (field_type.compareTo("button") == 0) {

            field_r = "<button type=\"submit\" class=\"btn btn-rounded btn-success btn-lg\" name=\"ActionID\" value=\"" + id + "\" " + attribute + " >" + name + "</button>";

        }
        if (field_type.compareTo("h5") == 0) {

            field_r = "<" + field_type + "><font " + attribute + ">" + name + "</font></" + field_type + ">";

        }

        return field_r;
    }

    private static Connection getConnectionlocal() {
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
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
        } catch (Exception e) {
            System.out.println("PL" + e.getMessage());
            return null;
        }
    }
}

