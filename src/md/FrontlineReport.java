//
// Decompiled by Procyon v0.5.36
//

package md;

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

public class FrontlineReport extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";

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
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            String UserName = "";
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }
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

            if (ActionID.equals("GetInput_VIPSVIP")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetInput_VIPSVIP(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetReport_VIPSVIP")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetReport_VIPSVIP(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetInput_ReasonLeaving")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetInput_ReasonLeaving(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetReport_ReasonLeaving")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetReport_ReasonLeaving(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetInput_CovidReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetInput_CovidReport(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetReport_CovidReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", ClientId);
                this.GetReport_CovidReport(request, out, conn, context, UserId, Database, ClientId);
            } else {
                out.println("Under Development");
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


    void GetInput_VIPSVIP(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportVipSvip.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }


    void GetReport_VIPSVIP(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String PatientCategory = request.getParameter("PatientCategory").trim();


        try {
            Query = " Select IFNULL(a.ID,''),  IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " +
                    " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
                    " IFNULL(b.RefName,''), CASE WHEN b.PatientCatagory = 1 THEN 'VIP' WHEN b.PatientCatagory = 2 THEN 'SVIP' ELSE 'Not Defined' END, " +
                    " IFNULL(a.PhNumber,'') " +
                    " from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId  " +
                    " where a.Status = 0 and a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' AND b.PatientCatagory = " + PatientCategory;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");//PhNumber
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportVipSvip.html");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }

    }


    void GetInput_ReasonLeaving(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportReasonLeaving.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

    void GetReport_ReasonLeaving(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String ReasonLeaving = request.getParameter("ReasonLeaving").trim();


        try {
            Query = " Select IFNULL(a.ID,''),  IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " +
                    " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
                    " IFNULL(b.RefName,''), CASE WHEN b.ReasonLeaving = 1 THEN 'MSE' WHEN b.ReasonLeaving = 2 THEN 'AMA' WHEN b.ReasonLeaving = 3 THEN 'LWBS' ELSE 'Not Defined' END, " +
                    " IFNULL(a.PhNumber,'') " +
                    " from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId  " +
                    " where a.Status = 0 and a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' AND b.ReasonLeaving = " + ReasonLeaving;
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");//PhNumber
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
//                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportReasonLeaving.html");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }

    }


    void GetInput_CovidReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        try {

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovid.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

    void GetReport_CovidReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String CovidStatus = request.getParameter("CovidStatus").trim();

        try {
            Query = " Select IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " +
                    " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')), " +
                    " CASE WHEN a.COVIDStatus = 0 THEN 'NEGATIVE' WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UNEXAMINED' END, " +
                    " IFNULL(DATE_FORMAT(b.CovidTestDate,'%m/%d/%Y'),''), IFNULL(b.CovidTestNo,''), IFNULL(a.PhNumber,'') " +
                    " from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId  " +
                    " where a.Status = 0 and  a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' AND a.COVIDStatus = '" + CovidStatus + "' ";
            //out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");//PhNumber
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovid.html");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }

    }

}
        
  
