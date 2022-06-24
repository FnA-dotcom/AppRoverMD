// 
// Decompiled by Procyon v0.5.36
// 

package orange_2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientInfo extends HttpServlet {
    public static String msgfinal;
    public static String msgresponse;

    static {
        PatientInfo.msgfinal = null;
        PatientInfo.msgresponse = null;
    }

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String UserId = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        ResultSet rset = null;
        Statement stmt = null;
        int ClientId = 0;
        String Database = "";
        UserId = "";
        String Query = "";
        try {
            UserId = Services.GetCookie("UserId", request);
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
//            if (ClientId == 8) {
//                Database = "oe_2";
//            }
//            else if (ClientId == 9) {
//                Database = "victoria";
//            }
//            else if (ClientId == 10) {
//                Database = "oddasa";
//            }
        } catch (Exception ex) {
        }
        if (ActionID.equals("GetValues")) {
            this.GetValues(request, out, conn, context, Database);
        }
        try {
            conn.close();
        } catch (Exception ex2) {
        }
        out.flush();
        out.close();
    }

    void GetValues(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String Database) {
        Statement stmt = null;
        ResultSet rset = null;
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        final int ID = Integer.parseInt(request.getParameter("ID").trim());
        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        String DOS = "";
        String msg = "";
        int SelfPayChk = 0;
        try {
            Query = "Select Title, FirstName, MiddleInitial, LastName, MaritalStatus, MRN, DOB, Age, Gender, Email, PhNumber, Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate from " + Database + ".PatientReg where ID = " + ID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Title = rset.getString(1);
                FirstName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                LastName = rset.getString(4);
                MaritalStatus = rset.getString(5);
                MRN = rset.getString(6);
                DOB = rset.getString(7);
                Age = rset.getString(8);
                gender = rset.getString(9);
                Email = rset.getString(10);
                PhNumber = rset.getString(11);
                Address = rset.getString(12);
                City = rset.getString(13);
                State = rset.getString(14);
                Country = rset.getString(15);
                ZipCode = rset.getString(16);
                SSN = rset.getString(17);
                Occupation = rset.getString(18);
                Employer = rset.getString(19);
                EmpContact = rset.getString(20);
                PriCarePhy = rset.getString(21);
                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getInt(23);
                DOS = rset.getString(24);
            }
            rset.close();
            stmt.close();
            final Date dNow = new Date(System.currentTimeMillis() - 7200000L);
            final SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            final String MSH7 = ft.format(dNow);
            final String MRN2 = "3109900";
            DOB = DOB.replace("-", "");
            if (gender.compareTo("female") == 0) {
                gender = "F";
            } else {
                gender = "M";
            }
            MaritalStatus = MaritalStatus.substring(1, 1);
            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^A04|" + MSH7 + "|P|2.3\r\n" + "EVN|A04|20200707020302|||XXX^^^^^^^^488 \r\n" + "PID|1||" + MRN + "||" + FirstName + "^" + LastName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n" + "PV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G|||||||||\r\n" + "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            Query = " insert into oe.request  (msg,mrn, requestdate, RequestType)  values ('" + msg + "','" + MRN + "', now(),'1') ";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();
            out.println("<!DOCTYPE html><html><body><p style=\"color:white;\">Request has been send to ERM , Find Patient MRN " + MRN + "</p>");
            out.println("<br><input type=button class=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
