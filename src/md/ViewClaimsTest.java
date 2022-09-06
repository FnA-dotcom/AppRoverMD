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
import java.io.PrintWriter;
import java.sql.*;

public class ViewClaimsTest extends HttpServlet {


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }


    private void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID;
        Connection conn = null;
        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
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

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
//
            switch (ActionID) {
//        	System.out.println("into the serviceHandling function switch");
                case "ShowReport":

                    ShowReport(request, out, conn, context, DatabaseName, DatabaseName, FacilityIndex, helper);
                    break;
                case "ShowFilteredReport":

                    ShowFilteredReport(request, out, conn, context, DatabaseName, DatabaseName, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }

        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
//            helper.SendEmailWithAttachment("Error in Claim Post ** (handleRequest)", context, Ex, "CardConnectServices", "handleRequest", conn);
//            Services.DumException("ClaimPostServices", "Handle Request", request, Ex, getServletContext());
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("FormName", "ManagementDashboard");
//            Parser.SetField("ActionID", "GetInput");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
//            out.flush();
//            out.close();
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


    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query1 = "";
        String Query2 = "";
        String BundleFnName = "";
        String LabelFnName = "";
        String Title = "Recent Claims";//byDefault
        int PatientCount = 0;
        int VictoriaDetailsFound = 0;
        String Status = request.getParameter("Status") == null ? "0" : request.getParameter("Status").trim();
        if (Status.equals("QA")) {
            Status = " a.Status= 1 ";
            Title = "QA Claims";
        } else if (Status.equals("AR")) {
            Status = " a.Status in (2,3) ";
            Title = "AR & Denial Claims";
        } else {
            Status = " a.Status = 0";
        }
        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT IFNULL(VisitId,''),IFNULL(PatientRegId,''),IFNULL(AcctNo,''),IFNULL(ClaimType,'')" +
                    " ,IFNULL(a.ClaimNumber,''),IFNULL(PatientName,''),IFNULL(DOS,''),FORMAT(IFNULL(TotalCharges,''),2),FORMAT(IFNULL(Balance,IFNULL(TotalCharges,'')),2) ,IFNULL(b.ChargeOption,'') " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " LEFT JOIN  " +Database+".ClaimChargesInfo c ON a.Id=c.ClaimInfoMasterId "+
                    " LEFT JOIN oe.ChargeOption b on c.ChargesStatus = b.Id " +
                    " GROUP BY c.ClaimInfoMasterId ORDER BY a.CreatedDate DESC");
            // ps.setString(1, Status);
            rset = ps.executeQuery();
            while (rset.next()) {
                if (rset.getInt(4) == 1)
                    CDRList.append("<tr onclick=openWindow(\"/md/md.InsClaimTesting?ActionID=Addinfo&VisitId=" + rset.getInt(1) + "&PatientRegId=" + rset.getInt(2) + "&AcctNo=" + rset.getString(3) + "&ClaimType=" + rset.getInt(4) + "\") >");
                else
                    CDRList.append("<tr onclick=openWindow(\"/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(1) + "&PatientRegId=" + rset.getInt(2) + "&AcctNo=" + rset.getString(3) + "&ClaimType=" + rset.getInt(4) + "\") >");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left> $" + rset.getString(8) + "</td>\n");//Paid
                CDRList.append("<td align=left> $" + rset.getString(9) + "</td>\n");//Balance
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");//Status
                if (rset.getInt(4) == 1)
                    CDRList.append("<td align=left>Institutional</td>\n");
                else
                    CDRList.append("<td align=left>Professional</td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            ps.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Title", Title);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowClaimsTest.html");
        } catch (Exception e) {
            System.out.println("into the ShowReport function exception");
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in AddInfo ** (ShowReport) ** FacIdx : " + ClientId, servletContext, e, "AddInfo", "ShowReport", conn);
            Services.DumException("ShowReport", "AddInfo ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }


    void ShowFilteredReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
//        System.out.println("into the ShowReport function");

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query1 = "";
        String Query2 = "";
        String BundleFnName = "";
        String LabelFnName = "";
        String Title = "Recent Claims";//byDefault
        int PatientCount = 0;
        int VictoriaDetailsFound = 0;
        String Status = request.getParameter("Status") == null ? "0" : request.getParameter("Status").trim();
        if (Status.equals("QA")) {
            Status = " a.Status= 1 ";
            Title = "QA Claims";
        } else if (Status.equals("AR")) {
            Status = " a.Status in (2,3) ";
            Title = "AR & Denial Claims";
        } else {
            Status = " a.Status = 0";
        }
        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
        PreparedStatement ps = null;
        String FilterKey = request.getParameter("FilterKey");
        try {
            ps = conn.prepareStatement("SELECT IFNULL(VisitId,''),IFNULL(PatientRegId,''),IFNULL(AcctNo,''),IFNULL(ClaimType,'')" +
                    " ,IFNULL(ClaimNumber,''),IFNULL(PatientName,''),IFNULL(DOS,''),FORMAT(IFNULL(TotalCharges,''),2),FORMAT(IFNULL(Balance,IFNULL(TotalCharges,'')),2) ,IFNULL(b.Desc,'') " +
                    " FROM " + Database + ".ClaimInfoMaster a " +
                    "LEFT JOIN oe.ClaimStatus_NEW b on a.Status = b.Id " +
                    " WHERE " +
                    " IFNULL(PatientName,'') LIKE '%" + FilterKey + "%' " +
                    " OR IFNULL(ClaimNumber,'') LIKE '%" + FilterKey + "%' " +
                    " OR IFNULL(MemId,'') LIKE '%" + FilterKey + "%' " +
                    " OR IFNULL(PCN,'') LIKE '%" + FilterKey + "%' " +
//       			" OR DATE_FORMAT(DOS,'%m-%d-%Y') LIKE '%"+FilterKey+"%' "+
                    " ORDER BY CreatedDate DESC");
            // ps.setString(1, Status);

            rset = ps.executeQuery();


            CDRList.append("<table id=\"FilterReport\" class=\"table table-bordered table-striped\">");
            CDRList.append("<thead style=\"color:white;\">");
            CDRList.append("<tr bgcolor=\"#249ad5\">");
            CDRList.append("<th>Claim ID</th>");
            CDRList.append("<th>Patient Name</th>");
            CDRList.append("<th>Date of Service</th>");
            CDRList.append("<th>Total Charges</th>");
            CDRList.append("<th>Balance</th>");
            CDRList.append("<th>Status</th>");
            CDRList.append("<th>Type</th>");

            CDRList.append("</tr>");
            CDRList.append("</thead>");
            CDRList.append("<tbody >");


            while (rset.next()) {
                if (rset.getInt(4) == 1)
                    CDRList.append("<tr onclick=openWindow(\"/md/md.InsClaimTesting?ActionID=Addinfo&VisitId=" + rset.getInt(1) + "&PatientRegId=" + rset.getInt(2) + "&AcctNo=" + rset.getString(3) + "&ClaimType=" + rset.getInt(4) + "\") >");
                else
                    CDRList.append("<tr onclick=openWindow(\"/md/md.AddInfo?ActionID=AddinfoProf&VisitId=" + rset.getInt(1) + "&PatientRegId=" + rset.getInt(2) + "&AcctNo=" + rset.getString(3) + "&ClaimType=" + rset.getInt(4) + "\") >");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left> $" + rset.getString(8) + "</td>\n");//Paid
                CDRList.append("<td align=left> $" + rset.getString(9) + "</td>\n");//Balance
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");//Status
                if (rset.getInt(4) == 1)
                    CDRList.append("<td align=left>Institutional</td>\n");
                else
                    CDRList.append("<td align=left>Professional</td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            ps.close();
            CDRList.append("</tbody>");
            CDRList.append("</table>");
            out.println(CDRList);

        } catch (Exception e) {
            System.out.println("into the ShowFilteredReport function exception");
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in AddInfo ** (ShowFilteredReport) ** FacIdx : " + ClientId, servletContext, e, "AddInfo", "ShowFilteredReport", conn);
            Services.DumException("ShowFilteredReport", "AddInfo ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }


}
