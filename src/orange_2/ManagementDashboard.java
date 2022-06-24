//
// Decompiled by Procyon v0.5.36
//

package orange_2;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

public class ManagementDashboard extends HttpServlet {
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
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();

        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

        try {
            Cookie[] cookies = request.getCookies();
            Zone = UserId = Passwd = "";
            int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; coky++) {
                String cName = cookies[coky].getName();
                String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }

            Query = "SELECT ClientId FROM oe.sysusers WHERE ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
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
//      if (ClientId == 8) {
//        Database = "oe_2";
//      } else if (ClientId == 9) {
//        Database = "victoria";
//      } else if (ClientId == 10) {
//        Database = "oddasa";
//      }

            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", ClientId);
                this.GetInput(request, out, conn, context, UserId, Database, ClientId);
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }

    }


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        int PatientCount = 0;
        String FromDate = "";
        String ToDate = "";
        String _FromEndDate = "";
        String _ToEndDate = "";
        StringBuffer CDRList = new StringBuffer();
        StringBuffer DoctorsList = new StringBuffer();
        String PatientsCurrentMonthDaily = "";
        String PatientCountAgeWise = "";
        String CurrentYear = "";
        String PatientCountMonthly = "";
        int PatientCountCurrentMonth = 0;
        int PatientCountOverAll = 0;
        int PatientCountMale = 0;
        int PatientCountFemale = 0;
        int PatientCountSelfPay = 0;
        int PatientCountInsured = 0;
        int PatientCountWC = 0;
        int PatientCountMVA = 0;
        int PatientCountCOVID = 0;
        int SNo = 1;
        try {

            Query = "Select Id,Concat(DoctorsFirstName, ' ', DoctorsLastName) from " + Database + ".DoctorsList where Status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DoctorsList.append("<div class=\"d-flex align-items-center justify-content-between my-15 pr-20\">");
                DoctorsList.append("<h5 class=\"my-0\"><i class=\"mr-50 w-20 fa fa-user-md\"></i>" + rset.getString(2) + "</h5>");

                Query1 = "Select COUNT(*) from " + Database + ".PatientReg where DoctorsName = " + rset.getInt(1);
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                if (rset1.next()) {
                    DoctorsList.append("<p class=\"mb-0\">" + rset1.getInt(1) + "</p>");
                }
                rset1.close();
                stmt1.close();

                DoctorsList.append("</div>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', LAST_DAY( now() ), YEAR(NOW());";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FromDate = rset.getString(1);
                ToDate = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + FromDate + "' AND selected_date <= '" + ToDate + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
                Query1 = "Select COUNT(*) from " + Database + ".PatientReg where CreatedDate >= '" + _FromEndDate + " 00:00:00' and CreatedDate <= '" + _ToEndDate + " 23:59:59'";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    PatientsCurrentMonthDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (PatientsCurrentMonthDaily.endsWith(",")) {
                PatientsCurrentMonthDaily = PatientsCurrentMonthDaily.substring(0, PatientsCurrentMonthDaily.length() - 1);
            }

            Query = "Select COUNT(*) from " + Database + ".PatientReg where CreatedDate >= '" + FromDate + " 00:00:00' and CreatedDate <= '" + ToDate + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCurrentMonth = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountOverAll = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where ltrim(rtrim(UPPER(Gender))) = ltrim(rtrim(UPPER('male')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountMale = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where ltrim(rtrim(UPPER(Gender))) = ltrim(rtrim(UPPER('female')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountFemale = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where SelfPayChk = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountSelfPay = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where SelfPayChk = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountInsured = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 0 and Age <= 5";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 6 and Age <= 10";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 11 and Age <= 15";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 16 and Age <= 20";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 21 and Age <= 25";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 26 and Age <= 30";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 31 and Age <= 35";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age >= 36 and Age <= 40";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where Age > 40 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountAgeWise += rset.getInt(1) + " , ";
            }
            rset.close();
            stmt.close();


            if (PatientCountAgeWise.endsWith(",")) {
                PatientCountAgeWise = PatientCountAgeWise.substring(0, PatientCountAgeWise.length() - 1);
            }

            Query = "SELECT YEAR(NOW())";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentYear = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String[] Months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (int i = 0; i <= 11; i++) {
                Query = "Select COUNT(*) from " + Database + ".PatientReg where DATE_FORMAT(CreatedDate,'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMonthly += rset.getString(1) + ",";
                }
                rset.close();
                stmt.close();
            }
            if (PatientCountMonthly.endsWith(",")) {
                PatientCountMonthly = PatientCountMonthly.substring(0, PatientCountMonthly.length() - 1);
            }

            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where WorkersCompPolicy = 1 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountWC = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where MotorVehAccident = 1 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountMVA = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".PatientReg where COVIDStatus = 1 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientCountCOVID = rset.getInt(1);
            }
            rset.close();
            stmt.close();
//      System.out.println(PatientCountMonthly);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DoctorsList", String.valueOf(DoctorsList));
            Parser.SetField("PatientsCurrentMonthDaily", String.valueOf(PatientsCurrentMonthDaily));
            Parser.SetField("PatientCountCurrentMonth", String.valueOf(PatientCountCurrentMonth));
            Parser.SetField("PatientCountOverAll", String.valueOf(PatientCountOverAll));
            Parser.SetField("PatientCountMale", String.valueOf(PatientCountMale));
            Parser.SetField("PatientCountFemale", String.valueOf(PatientCountFemale));
            Parser.SetField("PatientCountSelfPay", String.valueOf(PatientCountSelfPay));
            Parser.SetField("PatientCountInsured", String.valueOf(PatientCountInsured));
            Parser.SetField("PatientCountAgeWise", String.valueOf(PatientCountAgeWise));
            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
            Parser.SetField("PatientCountWC", String.valueOf(PatientCountWC));
            Parser.SetField("PatientCountMVA", String.valueOf(PatientCountMVA));
            Parser.SetField("PatientCountCOVID", String.valueOf(PatientCountCOVID));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ManagementDashboard.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

}
