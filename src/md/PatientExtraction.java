package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class PatientExtraction extends HttpServlet {


    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();


        Connection conn = null;
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            try {
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }
            String ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patient Extraction Report Input", "Open Patient Extraction Report Screen", FacilityIndex);
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patient Extraction Report Input", "Open Patient Extraction Report Screen", FacilityIndex);
                GetReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetVisitDetails")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Patient Extraction Report Input", "Open Patient Extraction Report Screen", FacilityIndex);
                GetVisitDetails(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            }  else {
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }


    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/PatientExtractionReport.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer overAllPatients = new StringBuffer();
        StringBuffer covidPatients = new StringBuffer();
        StringBuffer insuredPatients = new StringBuffer();
        StringBuffer selfPayPatients = new StringBuffer();
        Statement stmt = null;
        ResultSet rset1 = null;
        ResultSet rset2 = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        int VisitCount = 0;
        int patientCount = 0;
        int covidPatientCount = 0;
        int insuredPatientCount = 0;
        int selfPayPatientCount = 0;

        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String Month = request.getParameter("Month").trim();
        try {
            ps1 = conn.prepareStatement("SELECT IFNULL(MRN,''), CONCAT(IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')),IFNULL(DOB,''),IFNULL(Age,''),IFNULL(Gender,'')" +
                    "FROM "+Database+".PatientReg " +
                    "WHERE DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND Status = 0 ORDER BY DateOfService DESC ");
            rset1 = ps1.executeQuery();
            while(rset1.next()){
                ps2 = conn.prepareStatement("SELECT COUNT(*) FROM "+Database+".PatientVisit WHERE MRN=? AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00' " +
                       " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'");
                ps2.setString(1,rset1.getString(1));
                rset2 = ps2.executeQuery();
                if(rset2.next()){
                    VisitCount = rset2.getInt(1);
                }
                rset2.close();
                ps2.close();

                overAllPatients.append("<tr onclick='openVisits("+rset1.getString(1)+",this)'>");
                overAllPatients.append("<td >" + rset1.getString(1) + "</td>\n");
                overAllPatients.append("<td >" + rset1.getString(2) + "</td>\n");
                overAllPatients.append("<td >" + rset1.getString(3) + "</td>\n");
                overAllPatients.append("<td >" + rset1.getString(4) + "</td>\n");
                overAllPatients.append("<td >" + rset1.getString(5) + "</td>\n");
                overAllPatients.append("<td >" + VisitCount + "</td>\n");
                overAllPatients.append("</tr>");
                VisitCount=0;
                patientCount++;
            }
            rset1.close();
            ps1.close();


            ps1 = conn.prepareStatement("SELECT IFNULL(MRN,''), CONCAT(IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')),IFNULL(DOB,''),IFNULL(Age,''),IFNULL(Gender,'')" +
                    "FROM "+Database+".PatientReg " +
                    "WHERE DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND (COVIDStatus = 1 OR COVIDStatus = 0) AND Status = 0 ORDER BY DateOfService DESC ");
            rset1 = ps1.executeQuery();
            while(rset1.next()){
                ps2 = conn.prepareStatement("SELECT COUNT(*) FROM "+Database+".PatientVisit WHERE MRN=? AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00' " +
                        " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'");
                ps2.setString(1,rset1.getString(1));
                rset2 = ps2.executeQuery();
                if(rset2.next()){
                    VisitCount = rset2.getInt(1);
                }
                rset2.close();
                ps2.close();

                covidPatients.append("<tr onclick='openVisits("+rset1.getString(1)+",this)'>");
                covidPatients.append("<td >" + rset1.getString(1) + "</td>\n");
                covidPatients.append("<td >" + rset1.getString(2) + "</td>\n");
                covidPatients.append("<td >" + rset1.getString(3) + "</td>\n");
                covidPatients.append("<td >" + rset1.getString(4) + "</td>\n");
                covidPatients.append("<td >" + rset1.getString(5) + "</td>\n");
                covidPatients.append("<td >" + VisitCount + "</td>\n");
                covidPatients.append("</tr>");
                VisitCount=0;
                covidPatientCount++;
            }
            rset1.close();
            ps1.close();


            ps1 = conn.prepareStatement("SELECT IFNULL(MRN,''), CONCAT(IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')),IFNULL(DOB,''),IFNULL(Age,''),IFNULL(Gender,'')" +
                    "FROM "+Database+".PatientReg" +
                    " WHERE DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND SelfPayChk = 1 AND Status = 0 ORDER BY DateOfService DESC ");
            rset1 = ps1.executeQuery();
            while(rset1.next()){
                ps2 = conn.prepareStatement("SELECT COUNT(*) FROM "+Database+".PatientVisit WHERE MRN=? AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00' " +
                        " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'");
                ps2.setString(1,rset1.getString(1));
                rset2 = ps2.executeQuery();
                if(rset2.next()){
                    VisitCount = rset2.getInt(1);
                }
                rset2.close();
                ps2.close();

                insuredPatients.append("<tr onclick='openVisits("+rset1.getString(1)+",this)'>");
                insuredPatients.append("<td >" + rset1.getString(1) + "</td>\n");
                insuredPatients.append("<td >" + rset1.getString(2) + "</td>\n");
                insuredPatients.append("<td >" + rset1.getString(3) + "</td>\n");
                insuredPatients.append("<td >" + rset1.getString(4) + "</td>\n");
                insuredPatients.append("<td >" + rset1.getString(5) + "</td>\n");
                insuredPatients.append("<td >" + VisitCount + "</td>\n");
                insuredPatients.append("</tr>");
                VisitCount=0;
                insuredPatientCount++;
            }
            rset1.close();
            ps1.close();


            ps1 = conn.prepareStatement("SELECT IFNULL(MRN,''), CONCAT(IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')),IFNULL(DOB,''),IFNULL(Age,''),IFNULL(Gender,'')" +
                    "FROM "+Database+".PatientReg WHERE DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND SelfPayChk = 0 AND Status = 0 ORDER BY DateOfService DESC ");
            rset1 = ps1.executeQuery();
            while(rset1.next()){
                ps2 = conn.prepareStatement("SELECT COUNT(*) FROM "+Database+".PatientVisit WHERE MRN=? AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00' " +
                        " AND DATE_FORMAT(DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'");
                ps2.setString(1,rset1.getString(1));
                rset2 = ps2.executeQuery();
                if(rset2.next()){
                    VisitCount = rset2.getInt(1);
                }
                rset2.close();
                ps2.close();

                selfPayPatients.append("<tr onclick='openVisits("+rset1.getString(1)+",this)'>");
                selfPayPatients.append("<td >" + rset1.getString(1) + "</td>\n");
                selfPayPatients.append("<td >" + rset1.getString(2) + "</td>\n");
                selfPayPatients.append("<td >" + rset1.getString(3) + "</td>\n");
                selfPayPatients.append("<td >" + rset1.getString(4) + "</td>\n");
                selfPayPatients.append("<td >" + rset1.getString(5) + "</td>\n");
                selfPayPatients.append("<td >" + VisitCount + "</td>\n");
                selfPayPatients.append("</tr>");
                VisitCount=0;
                selfPayPatientCount++;
            }
            rset1.close();
            ps1.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);
            Parser.SetField("overAllPatients", String.valueOf(overAllPatients));
            Parser.SetField("selfPayPatients", String.valueOf(selfPayPatients));
            Parser.SetField("insuredPatients", String.valueOf(insuredPatients));
            Parser.SetField("covidPatients", String.valueOf(covidPatients));

            Parser.SetField("patientCount", "<span class=\"badge badge-danger\">"+String.valueOf(patientCount)+"</span>");
            Parser.SetField("covidPatientCount", "<span class=\"badge badge-danger\">"+String.valueOf(covidPatientCount)+"</span>");
            Parser.SetField("insuredPatientCount", "<span class=\"badge badge-danger\">"+String.valueOf(insuredPatientCount)+"</span>");
            Parser.SetField("selfPayPatientCount", "<span class=\"badge badge-danger\">"+String.valueOf(selfPayPatientCount)+"</span>");


            Parser.SetField("ToDate", ToDate);
            Parser.SetField("FromDate", FromDate);
            Parser.SetField("DateRange", Month);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/PatientExtractionReport.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void GetVisitDetails(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId){
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String MRN = request.getParameter("MRN").trim();
        StringBuffer visitDetails = new StringBuffer();
        try {
            PreparedStatement ps2 = conn.prepareStatement("SELECT CONCAT('VN-',IFNULL(a.MRN,''),'-',IFNULL(a.VisitNumber,'')),IFNULL(a.ReasonVisit,''),CONCAT(IFNULL(b.DoctorsFirstName,''), ' ',IFNULL(b.DoctorsLastName,'')),DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') " +
                    " FROM "+Database+".PatientVisit a " +
                    " LEFT JOIN "+Database+".DoctorsList b ON a.DoctorId = b.Id " +
                    " WHERE a.MRN=? AND DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00' " +
                    " AND DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' ORDER BY DateOfService DESC ");
            ps2.setString(1,MRN);
            ResultSet rset1 = ps2.executeQuery();
            while(rset1.next()){
                visitDetails.append("<tr >");
                visitDetails.append("<td >" + rset1.getString(1) + "</td>\n");
                visitDetails.append("<td >" + rset1.getString(2) + "</td>\n");
                visitDetails.append("<td >" + rset1.getString(4) + "</td>\n");
                visitDetails.append("<td >" + ((rset1.getString(3).trim().equals("")) ? "N/A" : rset1.getString(3)) + "</td>\n");
                visitDetails.append("</tr>");
            }
            rset1.close();
            ps2.close();
            out.println(visitDetails);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}