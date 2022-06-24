package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class PatientDemographics extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequestOLD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        rset = null;
        stmt = null;
        Query = "";
        String UserId = "";
        int ClientId = 0;
        String Database = "";

        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");

        try {
            conn = Services.GetConnection(this.getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                out.flush();
                out.close();
                return;
            }
        } catch (Exception excp) {
            conn = null;
            System.out.println("Exception excp conn: " + excp.getMessage());
        }
        ServletContext context = null;
        context = this.getServletContext();

        try {
            final Cookie[] cookies = request.getCookies();
            String UserName = "";
            final int checkCookie = 0;
            for (Cookie cooky : cookies) {
                final String cName = cooky.getName();
                final String cValue = cooky.getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }

            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            if (conn != null) {
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClientId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            }

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            if (conn != null) {
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    Database = rset.getString(1);
                }
                rset.close();
                stmt.close();
            }

            String ActionID = request.getParameter("ActionID").trim();

            switch (ActionID) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, Database, ClientId);
                    break;
                case "ShowReport":
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-Disposition", "attachment; filename=" + UserId + "_RoverPatientsDemographics.xls");
                    ShowReport(request, out, conn, context, UserId, Database, ClientId);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println("Error in Main " + e.getMessage());
        }
    }

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
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

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "ShowReport":
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-Disposition", "attachment; filename=" + UserId + "_RoverPatientsDemographics.xls");
                    ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }

    }

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, final String Database, final int ClientId) {
        rset = null;
        stmt = null;
        Query = "";

        int FacilityIndex = Integer.parseInt(request.getParameter("FacilityIndex"));

        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/PatientDemo.html");
        } catch (Exception e) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FacilityIndex", String.valueOf(FacilityIndex));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exceptions/Exception6.html");
            } catch (Exception ex) {
            }
            Services.doLogMethodMessage(servletContext, "GetInput Method", e.getMessage(), request);
        }
    }

    private void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, final String Database, final int ClientId) {
        int FacilityIndex = Integer.parseInt(request.getParameter("FacilityIndex"));
        String FromDate = request.getParameter("FromDate");
        String ToDate = request.getParameter("ToDate");
        int SNo = 1;
        String DatabaseName = "";
        try {
            Query = "Select dbname from oe.clients where Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DatabaseName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            out.println("<table width=100% cellspacing=0 cellpading=0 border=1>");
            out.println("<tr bgcolor=\"#ff0000\"><td colspan=13 class=\"fieldm\" align=center><font face=\"Arial\" color=\"#FFFFFF\"><b>Registered Patient List</b></font></td></tr>\n");
            out.println("<tr bgcolor=\"#ff0000\">");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>SNo</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Patient Name</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>MRN</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Birth</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Ph Number</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Reason Of Visit</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Service</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Covid Status</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Email</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Address</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>City</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>State</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Zip Code</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Primary Insurance</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Health Insurance Name</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Insurance DOB</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Insurance GroupNo</b></font></td>");
            out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Insurance Policy Info</b></font></td>");

            Query = "SELECT  CONCAT(a.Title,' ',a.FirstName,' ',a.MiddleInitial,' ',a.LastName) As Name,IFNULL(a.MRN,0) AS MRN, DATE_FORMAT(a.DOB,'%m/%d/%Y') AS DOB, a.PhNumber, \n" +
                    "IFNULL(a.ReasonVisit,'-') AS ReasonVisit,\n" +
                    "IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')) AS DOS,\n" +
                    "CASE WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = 0 THEN 'NEGATIVE'  WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'NONE' END AS CovidStatus, \n" +
                    "IFNULL(a.Email,'-') AS Email, IFNULL(a.Address,'-') AS Address,IFNULL(a.City,'-') AS City, IFNULL(a.State, '-') AS State, IFNULL(a.ZipCode, '-') AS ZipCode, \n" +
                    "IFNULL(c.HIPrimaryInsurance,'-') AS PrimaryInsurance, CONCAT(IFNULL(c.HISubscriberFirstName,''),\" \",IFNULL(HISubscriberLastName,'')) AS HealthInsuranceName, \n" +
                    "IFNULL(HISubscriberDOB,'') AS InsuranceDOB, IFNULL(HISubscriberGroupNo,'') AS InsuranceGroupNo, IFNULL(HISubscriberPolicyNo,'') AS InsurancePolicyInfo,a.ID AS PatientRegId \n" +
                    "FROM " + DatabaseName + ".PatientReg a\n" +
                    "LEFT JOIN  " + DatabaseName + ".PatientReg_Details b on a.ID = b.PatientRegId \n" +
                    "LEFT JOIN  " + DatabaseName + ".Patient_HealthInsuranceInfo c on a.ID = c.PatientRegId \n" +
                    "where a.Status = 0 and a.CreatedDate >= '" + FromDate + " 00:00:00' and a.CreatedDate <= '" + ToDate + " 23:59:59' " +
                    "ORDER BY a.ID DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                out.println("<tr  bgcolor=\"#FFFFFF\">");
                out.println("<td class=\"fieldm\">" + SNo + "</td>");
                out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");//Name
                out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");//MRN
                out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");//DOB
                out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");//phnumber
                out.println("<td class=\"fieldm\">" + rset.getString(5) + "</td>");//ReasonVisit
                out.println("<td class=\"fieldm\">" + rset.getString(6) + "</td>");//DOS
                out.println("<td class=\"fieldm\">" + rset.getString(7) + "</td>");//CovidStatus
                out.println("<td class=\"fieldm\">" + rset.getString(8) + "</td>");//Email
                out.println("<td class=\"fieldm\">" + rset.getString(9) + "</td>");//Address
                out.println("<td class=\"fieldm\">" + rset.getString(10) + "</td>");//City
                out.println("<td class=\"fieldm\">" + rset.getString(11) + "</td>");//State
                out.println("<td class=\"fieldm\">" + rset.getString(12) + "</td>");//ZipCode
                out.println("<td class=\"fieldm\">" + rset.getString(13) + "</td>");//PrimaryInsurance
                out.println("<td class=\"fieldm\">" + rset.getString(14) + "</td>");//HealthInsuranceName
                out.println("<td class=\"fieldm\">" + rset.getString(15) + "</td>");//InsuranceDOB
                out.println("<td class=\"fieldm\">" + rset.getString(16) + "</td>");//InsuranceGroupNo
                out.println("<td class=\"fieldm\">" + rset.getString(17) + "</td>");//InsurancePolicyInfo
                out.println("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();
            out.println("</table>");

        } catch (Exception e) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exceptions/Exception6.html");
            } catch (Exception ex) {
            }
            Services.doLogMethodMessage(servletContext, "ShowReport Method", e.getMessage(), request);
        }
    }
}
