//
// Decompiled by Procyon v0.5.36
//

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

public class Reports_New extends HttpServlet {
    static String DOS = "";

    static String Acct = "";

    static String printabledate = "";

    private Connection conn = null;

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
                this.conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }
            String ActionID = request.getParameter("ActionID").trim();
            this.conn = Services.getMysqlConn(context);
            if (this.conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput_VIPSVIP")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetInput_VIPSVIP(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport_VIPSVIP")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetReport_VIPSVIP(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetInput_ReasonLeaving")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetInput_ReasonLeaving(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport_ReasonLeaving")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetReport_ReasonLeaving(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetInput_CovidReport")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetInput_CovidReport(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport_CovidReport")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetReport_CovidReport(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetInput_PatientLog")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetInput_PatientLog(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport_PatientLog")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetReport_PatientLog(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetInput_PatientStatus")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetInput_PatientStatus(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetReport_PatientStatus")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Get Eligibility Inquiry Report Input", "Open Eligibility Inquiry Report Screen", FacilityIndex);
                GetReport_PatientStatus(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                helper.deleteUserSession(request, this.conn, session.getId());
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
                if (this.conn != null)
                    this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    void GetInput_VIPSVIP(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportVipSvip.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport_VIPSVIP(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Condition = "";
        String Query = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String PatientCategory = request.getParameter("PatientCategory").trim();
        if (PatientCategory.equals("-1")) {
            Condition = " AND b.PatientCatagory in (1,2)";
        } else {
            Condition = " AND b.PatientCatagory = " + PatientCategory;
        }
        try {
            Query = " Select IFNULL(a.ID,''),  IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')),  IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')),  IFNULL(b.RefName,''), CASE WHEN b.PatientCatagory = 1 THEN 'VIP' WHEN b.PatientCatagory = 2 THEN 'SVIP' ELSE 'Not Defined' END,  IFNULL(a.PhNumber,'')  from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId   where a.Status = 0 and a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  " + Condition;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
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
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportVipSvip.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void GetInput_ReasonLeaving(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportReasonLeaving.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport_ReasonLeaving(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Condition = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String ReasonLeaving = request.getParameter("ReasonLeaving").trim();
        if (ReasonLeaving.equals("-1")) {
            Condition = " AND b.ReasonLeaving in (1,2,3,4)";
        } else {
            Condition = " AND b.ReasonLeaving = " + ReasonLeaving;
        }
        try {
            Query = " Select IFNULL(a.ID,''),  IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')),  IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')),  IFNULL(b.RefName,''), CASE WHEN b.ReasonLeaving = 1 THEN 'MSE' WHEN b.ReasonLeaving = 2 THEN 'AMA' WHEN b.ReasonLeaving = 3 THEN 'LWBS'  WHEN b.ReasonLeaving = 4 THEN 'Eloped' ELSE 'Not Defined' END,  IFNULL(a.PhNumber,'')  from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId   where a.Status = 0 and a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  " + Condition;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportReasonLeaving.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void GetInput_CovidReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovid.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport_CovidReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
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
            Query = " Select IFNULL(a.MRN,''),CONCAT(IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')),  IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''),  IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T')),  CASE WHEN a.COVIDStatus = 0 THEN 'NEGATIVE' WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UNEXAMINED' END,  IFNULL(DATE_FORMAT(b.CovidTestDate,'%m/%d/%Y'),''), IFNULL(b.CovidTestNo,''), IFNULL(a.PhNumber,'')  from  " + Database + ".PatientReg a LEFT JOIN " + Database + ".Patient_AdditionalInfo b on a.ID = b.PatientRegId   where a.Status = 0 and  a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' AND a.COVIDStatus = '" + CovidStatus + "' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
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
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportCovid.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void GetInput_PatientLog(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportPatientLog.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport_PatientLog(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        try {
            Query = " Select " +
                    "CASE " +
                    " WHEN a.DateofService = 'now()' THEN DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T') " +
                    " WHEN a.DateofService = NULL THEN DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T') " +
                    " ELSE DATE_FORMAT(a.DateofService,'%m/%d/%Y %T') END, IFNULL(b.LastName,''), " +
                    " IFNULL(b.FirstName,''), IFNULL(b.PhNumber,''), IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''),  " +
                    " CASE " +
                    " WHEN d.Race = 1 THEN 'Black or African American' " +
                    " WHEN d.Race = 2 THEN 'American Indian' " +
                    " WHEN d.Race = 3 THEN 'Asian' " +
                    " WHEN d.Race = 4 THEN 'Native Hawaiian or Other Specific Islander' " +
                    " WHEN d.Race = 5 THEN 'White' " +
                    " WHEN d.Race = 6 THEN 'Others' ELSE 'Others' END, " +
                    " IFNULL(b.Gender,''), IFNULL(b.ZipCode,''), IFNULL(b.County,''),IFNULL(a.ReasonVisit,''), " +
                    " CASE " +
                    " WHEN b.SelfPayChk = 1 THEN 'Insured' " +
                    " WHEN b.SelfPayChk = 0 THEN 'SelfPay' ELSE 'SelfPay' END," +
                    "  b.ID, b.COVIDStatus, a.Id,  " +
                    " CASE WHEN b.Ethnicity = 1 THEN 'Hispanic or Latino' " +
                    " WHEN b.Ethnicity = 2 THEN 'Non Hispanic or Latino'  " +
                    " WHEN b.Ethnicity = 3 THEN 'Others' ELSE 'Others' END, b.MRN, IFNULL(b.DOB,'') " +
                    " from " + Database + ".PatientReg b " +
                    " STRAIGHT_JOIN " + Database + ".PatientVisit a on a.PatientRegId = b.ID  " +
                    " STRAIGHT_JOIN " + Database + ".PatientReg_Details d on a.PatientRegId = d.PatientRegId  " +
                    " WHERE b.Status = 0 and " +
                    " DATE_FORMAT(a.DateofService,'%Y-%m-%d %T') between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                String PriInsuranceName = "";
                if (rset.getString(11).equals("Insured")) {
                    Query2 = "Select IFNULL(b.PayerName,'')  from " + Database + ".InsuranceInfo a  " +
                            " LEFT JOIN oe_2.ProfessionalPayers b on a.PriInsuranceName = b.Id " +
                            " where a.PatientRegId = " + rset.getInt(12);
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next())
                        PriInsuranceName = rset2.getString(1);
                    rset2.close();
                    stmt2.close();
                } else {
                    PriInsuranceName = "Self Pay";
                }
                Query2 = "Select COUNT(*)  from " + Database + ".Patient_AdditionalInfo  " +
                        "where PatientRegId = " + rset.getInt(12) + " and " +
                        " VisitId='" + rset.getInt(14) + "'";
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
                if (rset2.next())
                    if (rset2.getInt(1) > 0) {
                        Query1 = "Select CASE WHEN a.CovidTestNo = NULL THEN 'N/A' WHEN a.CovidTestNo = '' THEN 'N/A' ELSE IFNULL(a.CovidTestNo,'') END,  " +
                                "CASE WHEN a.CovidStatus = 1 THEN 'POSITIVE' WHEN a.CovidStatus = 0 THEN 'NEGATIVE'   WHEN a.CovidStatus = -1 THEN 'NONE' ELSE 'N/A' END,  " +
                                "CASE WHEN a.PatientCatagory = 1 THEN 'VIP' WHEN a.PatientCatagory = 2 THEN 'SVIP' ELSE 'N/A' END,  " +
                                "CASE WHEN a.PatientStatus = 1 THEN 'Transferred' WHEN a.PatientStatus = 2 THEN 'OBS' WHEN a.PatientStatus = 3 THEN 'OBS/Transferred' ELSE 'N/A' END," +
                                "IFNULL(b.TransactionType,'N/A')  " +
                                "from " + Database + ".Patient_AdditionalInfo a " +
                                " LEFT JOIN " + Database + ".TransactionTypes b ON a.ReasonLeaving = b.Id " +
                                " where PatientRegId = " + rset.getInt(12) + " and " +
                                " VisitId='" + rset.getInt(14) + "'";
                        stmt1 = conn.createStatement();
                        rset1 = stmt1.executeQuery(Query1);
                        while (rset1.next()) {
                            CDRList.append("<tr>");
                            CDRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                            CDRList.append("<td align=left>" + rset1.getString(1) + "</td>\n");
                            CDRList.append("<td align=left>" + rset1.getString(2) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                            if (rset.getString(5).equals("")) {
                                CDRList.append("<td align=left></td>\n");
                            } else {
                                CDRList.append("<td align=left>" + getAge(LocalDate.parse(rset.getString(17))) + "</td>\n");
                            }
                            CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                            CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                            CDRList.append("<td align=left>" + PriInsuranceName + "</td>\n");
                            CDRList.append("<td align=left>" + rset1.getString(3) + "</td>\n");
                            CDRList.append("<td align=left>" + rset1.getString(4) + "</td>\n");
                            CDRList.append("<td align=left>" + rset1.getString(5) + "</td>\n");
                            CDRList.append("</tr>");
                        }
                        rset1.close();
                        stmt1.close();
                    } else {
                        CDRList.append("<tr>");
                        CDRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                        CDRList.append("<td align=left> N/A </td>\n");
                        CDRList.append("<td align=left> N/A </td>\n");
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                        if (rset.getString(5).equals("")) {
                            CDRList.append("<td align=left></td>\n");
                        } else {
                            CDRList.append("<td align=left>" + getAge(LocalDate.parse(rset.getString(17))) + "</td>\n");
                        }
                        CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                        CDRList.append("<td align=left>" + PriInsuranceName + "</td>\n");
                        CDRList.append("<td align=left>N/A</td>\n");
                        CDRList.append("<td align=left>N/A</td>\n");
                        CDRList.append("<td align=left>N/A</td>\n");
                        CDRList.append("</tr>");
                    }
                rset2.close();
                stmt2.close();
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportPatientLog.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

    void GetInput_PatientStatus(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        StringBuilder RefPhyBuilder = new StringBuilder();
        StringBuilder PatientCatagoryBuilder = new StringBuilder();
        StringBuilder ReasonLeavingBuilder = new StringBuilder();
        StringBuilder PatStatusBuilder = new StringBuilder();
        try {
            Query = "Select Id, IFNULL(RefPhysicianName,'') from " + Database + ".RefPhysicianName where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            RefPhyBuilder.append("<select class=\"form-control\" id=\"RefPhy\" name=\"RefPhy\" style=\"color:black;\" >");
            RefPhyBuilder.append("<option value=''>Select One</option>");
            RefPhyBuilder.append("<option value='-1' selected>ALL</option>");
            while (rset.next())
                RefPhyBuilder.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();
            RefPhyBuilder.append("</select>");
            PatientCatagoryBuilder.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"1\">VIP</option>\n<option value=\"2\">SVIP</option>\n</select>\n");
            ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"1\">MSE</option>\n<option value=\"2\">AMA</option>\n<option value=\"3\">LWBS</option>\n<option value=\"4\">Eloped</option>\n</select>");
            PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"2\">OBS</option>\n<option value=\"1\">Transferred</option>\n<option value=\"3\">OBS/Transferred</option>\n</select>");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("RefPhyBuilder", String.valueOf(RefPhyBuilder));
            Parser.SetField("PatientCatagoryBuilder", String.valueOf(PatientCatagoryBuilder));
            Parser.SetField("ReasonLeavingBuilder", String.valueOf(ReasonLeavingBuilder));
            Parser.SetField("PatStatusBuilder", String.valueOf(PatStatusBuilder));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportPatientStatus.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }

    void GetReport_PatientStatus(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        StringBuilder RefPhyBuilder = new StringBuilder();
        StringBuilder PatientCatagoryBuilder = new StringBuilder();
        StringBuilder ReasonLeavingBuilder = new StringBuilder();
        StringBuilder PatStatusBuilder = new StringBuilder();
        String PatientCategoryCondition = "";
        String ReasonLeavingCondition = "";
        String RefPhyCondition = "";
        String PatStatusCondition = "";
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String PatientCategory = request.getParameter("PatientCatagory").trim();
        String ReasonLeaving = request.getParameter("ReasonLeaving").trim();
        String RefPhy = request.getParameter("RefPhy").trim();
        String PatStatus = request.getParameter("PatStatus").trim();
        if (PatientCategory.equals("-1")) {
            PatientCategoryCondition = "  ";
        } else {
            PatientCategoryCondition = " and c.PatientCatagory = " + PatientCategory;
        }
        if (ReasonLeaving.equals("-1")) {
            ReasonLeavingCondition = "  ";
        } else {
            ReasonLeavingCondition = " and c.ReasonLeaving = " + ReasonLeaving;
        }
        if (RefPhy.equals("-1")) {
            RefPhyCondition = " ";
        } else {
            RefPhyCondition = " and c.RefPhysician = " + RefPhy;
        }
        if (PatStatus.equals("-1")) {
            PatStatusCondition = " ";
        } else {
            PatStatusCondition = " and c.PatientStatus = " + PatStatus;
        }
        try {
            if (PatientCategory.equals("-1")) {
                PatientCatagoryBuilder.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"1\">VIP</option>\n<option value=\"2\">SVIP</option>\n</select>\n");
            } else if (PatientCategory.equals("1")) {
                PatientCatagoryBuilder.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" selected>VIP</option>\n<option value=\"2\">SVIP</option>\n</select>\n");
            } else if (PatientCategory.equals("2")) {
                PatientCatagoryBuilder.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" >VIP</option>\n<option value=\"2\" selected>SVIP</option>\n</select>\n");
            } else {
                PatientCatagoryBuilder.append("<select class=\"form-control\" id=\"PatientCatagory\" name=\"PatientCatagory\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected >ALL</option>\n<option value=\"1\" >VIP</option>\n<option value=\"2\" >SVIP</option>\n</select>\n");
            }
            if (ReasonLeaving.equals("-1")) {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"1\">MSE</option>\n<option value=\"2\">AMA</option>\n<option value=\"3\">LWBS</option>\n<option value=\"4\">Eloped</option>\n</select>");
            } else if (ReasonLeaving.equals("1")) {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" selected>MSE</option>\n<option value=\"2\">AMA</option>\n<option value=\"3\">LWBS</option>\n<option value=\"4\">Eloped</option>\n</select>");
            } else if (ReasonLeaving.equals("2")) {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" >MSE</option>\n<option value=\"2\" selected>AMA</option>\n<option value=\"3\">LWBS</option>\n<option value=\"4\">Eloped</option>\n</select>");
            } else if (ReasonLeaving.equals("3")) {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" >MSE</option>\n<option value=\"2\" >AMA</option>\n<option value=\"3\" selected>LWBS</option>\n<option value=\"4\">Eloped</option>\n</select>");
            } else if (ReasonLeaving.equals("4")) {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"1\" >MSE</option>\n<option value=\"2\" >AMA</option>\n<option value=\"3\" >LWBS</option>\n<option value=\"4\" selected>Eloped</option>\n</select>");
            } else {
                ReasonLeavingBuilder.append("<select class=\"form-control\" id=\"ReasonLeaving\" name=\"ReasonLeaving\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"1\" >MSE</option>\n<option value=\"2\" >AMA</option>\n<option value=\"3\" >LWBS</option>\n<option value=\"4\" >Eloped</option>\n</select>");
            }
            if (RefPhy.equals("-1")) {
                Query = "Select Id, IFNULL(RefPhysicianName,'') from " + Database + ".RefPhysicianName where Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                RefPhyBuilder.append("<select class=\"form-control\" id=\"RefPhy\" name=\"RefPhy\" style=\"color:black;\" >");
                RefPhyBuilder.append("<option value=''>Select One</option>");
                RefPhyBuilder.append("<option value='-1' selected>ALL</option>");
                while (rset.next())
                    RefPhyBuilder.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                rset.close();
                stmt.close();
                RefPhyBuilder.append("</select>");
            } else {
                Query = "Select Id, IFNULL(RefPhysicianName,'') from " + Database + ".RefPhysicianName where Status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                RefPhyBuilder.append("<select class=\"form-control\" id=\"RefPhy\" name=\"RefPhy\" style=\"color:black;\" >");
                RefPhyBuilder.append("<option value=''>Select One</option>");
                RefPhyBuilder.append("<option value='-1' >ALL</option>");
                while (rset.next()) {
                    if (RefPhy.equals(rset.getString(1))) {
                        RefPhyBuilder.append("<option value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                        continue;
                    }
                    RefPhyBuilder.append("<option value=\"" + rset.getString(1) + "\" >" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();
                RefPhyBuilder.append("</select>");
            }
            if (PatStatus.equals("-1")) {
                PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"2\">OBS</option>\n<option value=\"1\">Transferred</option>\n<option value=\"3\">OBS/Transferred</option>\n</select>");
            } else if (PatStatus.equals("1")) {
                PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"2\">OBS</option>\n<option value=\"1\" selected>Transferred</option>\n<option value=\"3\">OBS/Transferred</option>\n</select>");
            } else if (PatStatus.equals("2")) {
                PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"2\" selected>OBS</option>\n<option value=\"1\" >Transferred</option>\n<option value=\"3\">OBS/Transferred</option>\n</select>");
            } else if (PatStatus.equals("3")) {
                PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" >ALL</option>\n<option value=\"2\" >OBS</option>\n<option value=\"1\" >Transferred</option>\n<option value=\"3\" selected>OBS/Transferred</option>\n</select>");
            } else {
                PatStatusBuilder.append("<select class=\"form-control\" id=\"PatStatus\" name=\"PatStatus\" style=\"color:black;\" >\n<option value=\"\">Select any</option>\n<option value=\"-1\" selected>ALL</option>\n<option value=\"2\" >OBS</option>\n<option value=\"1\" >Transferred</option>\n<option value=\"3\" >OBS/Transferred</option>\n</select>");
            }
            Query = " Select IFNULL(b.FirstName,''), IFNULL(b.LastName,''),  IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''), CASE WHEN a.DateofService = 'now()' THEN DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T') WHEN a.DateofService = NULL THEN DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T') ELSE DATE_FORMAT(a.DateofService,'%m/%d/%Y %T') END, a.Id as VisitId, b.ID as PatientRegId, IFNULL(b.MRN,''),  CASE WHEN c.PatientCatagory = 1 THEN 'VIP' WHEN c.PatientCatagory = 2 THEN 'SVIP' ELSE 'N/A' END,  IFNULL(c.RefName,'N/A'),  CASE WHEN c.ReasonLeaving = 1 THEN 'MSE' WHEN c.ReasonLeaving = 2 THEN 'AWA' WHEN c.ReasonLeaving = 3 THEN 'LWBS'  WHEN c.ReasonLeaving = 4 THEN 'ELoped' ELSE 'N/A' END ,  IFNULL(d.RefPhysicianName,'N/A'),  CASE WHEN c.PatientStatus = 1 THEN 'Transferred' WHEN c.PatientStatus = 2 THEN 'OBS' WHEN c.PatientStatus = 3 THEN 'OBS/Transferred' ELSE 'N/A' END from " + Database + ".PatientReg b  STRAIGHT_JOIN " + Database + ".PatientVisit a on a.PatientRegId = b.ID  LEFT JOIN " + Database + ".Patient_AdditionalInfo c on c.PatientRegId = b.ID and c.VisitId = a.Id  LEFT JOIN " + Database + ".RefPhysicianName d on c.RefPhysician = d.Id  where b.Status = 0 and  DATE_FORMAT(a.DateofService,'%Y-%m-%d %T') between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'" + PatientCategoryCondition + ReasonLeavingCondition + RefPhyCondition + PatStatusCondition;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + " " + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("RefPhyBuilder", String.valueOf(RefPhyBuilder));
            Parser.SetField("PatientCatagoryBuilder", String.valueOf(PatientCatagoryBuilder));
            Parser.SetField("ReasonLeavingBuilder", String.valueOf(ReasonLeavingBuilder));
            Parser.SetField("PatStatusBuilder", String.valueOf(PatStatusBuilder));
            Parser.SetField("FromDate", String.valueOf(FromDate));
            Parser.SetField("ToDate", String.valueOf(ToDate));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ReportPatientStatus.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

}
