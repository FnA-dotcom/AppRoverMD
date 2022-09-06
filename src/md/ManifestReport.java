package md;


import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManifestReport extends HttpServlet {


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
        Connection conn = null;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                GetReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("GetFilter")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Add Facility", "Click on the Facility Option", FacilityIndex);
                GetFilter(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);


            } else {
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in AddFacility ** (handleRequest)", context, e, "AddFacility", "handleRequest", conn);
            Services.DumException("AddFacility", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetReport");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in ManifestReport ** (handleRequest -- SqlException)", context, e, "ManifestReport", "handleRequest", conn);
                Services.DumException("ManifestReport", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetReport");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    void GetReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int SNo = 1;
        StringBuffer CDRList = new StringBuffer();
        // DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {

            Query = "SELECT a.ManifestNum,a.CreatedBy, b.Status,d.Location,Count(*)\r\n"
                    + "from "+Database+".ManifestsMaster a \r\n"
                    + "INNER JOIN "+Database+".ManifestDetails b ON a.Id = b.Id \r\n"
                    + "INNER JOIN "+Database+".PatientReg c ON b.PatRegIdx=c.ID \r\n"
                    + "INNER JOIN "+Database+".Locations d ON c.TestingLocation=d.Id\r\n"
                    + "INNER JOIN "+Database+".TestOrder e ON c.ID = e.PatRegIdx\r\n"
                    + "INNER JOIN "+Database+".Tests f ON e.Id=f.OrderId\r\n"
                    + " GROUP BY ManifestNum";
//       	Query="Select ManifestNum, CreatedBy from "+Database+".ManifestsMaster";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println("The Query is " + Query);
            while (rset.next()) {

                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ManifiestReport.html");
        } catch (Exception e) {
            System.out.println("in the catch exception of GetReport Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);
        }
    }

    void GetFilter(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Sno = 0;

        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        try {


            Query = "SELECT a.ManifestNum,a.CreatedBy, b.Status,d.Location,Count(*)\r\n"
                    + "from "+Database+".ManifestsMaster a \r\n"
                    + "INNER JOIN "+Database+".ManifestDetails b ON a.Id = b.Id \r\n"
                    + "INNER JOIN "+Database+".PatientReg c ON b.PatRegIdx=c.ID \r\n"
                    + "INNER JOIN "+Database+".Locations d ON c.TestingLocation=d.Id\r\n"
                    + "INNER JOIN "+Database+".TestOrder e ON c.ID = e.PatRegIdx\r\n"
                    + "INNER JOIN "+Database+".Tests f ON e.Id=f.OrderId\r\n"
                    + " WHERE a.CreatedDate BETWEEN '" + FromDate + "' AND '" + ToDate + "'"
                    + " GROUP BY ManifestNum";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {

                CDRList.append("<tr >");
                CDRList.append("<td align=left>" + Sno + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("</tr>");
                Sno++;

            }

            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ManifiestReport.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

}


