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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class MarketingReport extends HttpServlet {

    /*
        private Statement stmt = null;
        private ResultSet rset = null;
        private String Query = "";
        private Statement stmt2 = null;
        private ResultSet rset2 = null;
        private String Query2 = "";
        private Connection conn = null;
        */
    Integer ScreenIndex = 2;


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
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        int UserIndex = 0;
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
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

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

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);


//            if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, this.ScreenIndex)) {
////                out.println("You are not Authorized to access this page");
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "You are not Authorized to access this page");
//                Parser.SetField("FormName", "ManagementDashboard");
//                Parser.SetField("ActionID", "GetInput");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
//                return;
//            }
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "MarketingReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Marketing Report", "Open Marketing Report Input", FacilityIndex);
                    MarketingReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Report Marketing ", "Get Report Marketing", FacilityIndex);
                    GetReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "MarketingReportDetailInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Marketing Report Details", "Open Marketing Report Details", FacilityIndex);
                    MarketingReportDetailInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "MarketingDetailReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Marketing Report Details Open", "OPEN Marketing Report Details ", FacilityIndex);
                    MarketingDetailReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "MarketingReportDashboard":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Marketing Report Details Open", "OPEN Marketing Report Details ", FacilityIndex);
                    MarketingReportDashboard(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DashBoardDateWise":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Marketing Report Details Open", "OPEN Marketing Report Details ", FacilityIndex);
                    DashBoardDateWise(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
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


    void MarketingReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();

            if (ClientId == 9 || ClientId == 28) {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReport.html");
            } else if (ClientId == 27 || ClientId == 29) {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport_frontline.html");
            } else {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport_others.html");
            }
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void GetReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        String DayDate = "";
        StringBuilder MRList = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();


        try {
            if (ClientId == 9 || ClientId == 28) {
                //TotalCountVariables
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;

                Query = "select * from  (select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v \n" +
                        "where selected_date >= '" + FromDate + "' and selected_date <= '" + ToDate + "' order by selected_date";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DayDate = "";
                    DayDate = rset.getString(1);


                    Query2 = " Select SUM(MFFirstVisit),SUM(MFReturnPat),SUM(MFInternetFind),SUM(Facebook),SUM(MapSearch),SUM(GoogleSearch),SUM(VERWebsite),SUM(WebsiteAds)," +
                            " SUM(OnlineReviews),SUM(Twitter),SUM(LinkedIn),SUM(EmailBlast),SUM(YouTube),SUM(TV),SUM(Billboard),SUM(Radio),SUM(Brochure),SUM(DirectMail)," +
                            " SUM(CitizensDeTar),SUM(LiveWorkNearby),SUM(FamilyFriend),SUM(UrgentCare),SUM(NewspaperMagazine),SUM(School),SUM(Hotel) " +//25
                            " from " + Database + ".MarketingInfo where CreatedDate between '" + DayDate + " 00:00:00' and '" + DayDate + " 23:59:59' ";
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        DayDate = DayDate.substring(5, 7) + "/" + DayDate.substring(8, 10) + "/" + DayDate.substring(0, 4);
                        MRList.append("<tr>");
                        MRList.append("<td align=left>" + DayDate + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(1) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(2) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(3) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(4) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(5) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(6) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(7) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(8) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(9) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(10) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(11) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(12) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(13) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(14) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(15) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(16) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(17) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(18) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(19) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(20) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(21) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(22) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(23) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(24) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(25) + "</td>\n");
                        MRList.append("</tr>");
                        TotalMFFirstVisit += rset2.getLong(1);
                        TotalMFReturnPat += rset2.getLong(2);
                        TotalMFInternetFind += rset2.getLong(3);
                        TotalFacebook += rset2.getLong(4);
                        TotalMapSearch += rset2.getLong(5);
                        TotalGoogleSearch += rset2.getLong(6);
                        TotalVERWebsite += rset2.getLong(7);
                        TotalWebsiteAds += rset2.getLong(8);
                        TotalOnlineReviews += rset2.getLong(9);
                        TotalTwitter += rset2.getLong(10);
                        TotalLinkedIn += rset2.getLong(11);
                        TotalEmailBlast += rset2.getLong(12);
                        TotalYouTube += rset2.getLong(13);
                        TotalTV += rset2.getLong(14);
                        TotalBillboard += rset2.getLong(15);
                        TotalRadio += rset2.getLong(16);
                        TotalBrochure += rset2.getLong(17);
                        TotalDirectMail += rset2.getLong(18);
                        TotalCitizensDeTar += rset2.getLong(19);
                        TotalLiveWorkNearby += rset2.getLong(20);
                        TotalFamilyFriend += rset2.getLong(21);
                        TotalUrgentCare += rset2.getLong(22);
                        TotalNewspaperMagazine += rset2.getLong(23);
                        TotalSchool += rset2.getLong(24);
                        TotalHotel += rset2.getLong(25);
                    }
                    rset2.close();
                    stmt2.close();
                }
                rset.close();
                stmt.close();
                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left>" + "TOTAL: " + "</td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMFFirstVisit + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMFReturnPat + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMFInternetFind + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFacebook + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMapSearch + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalGoogleSearch + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalVERWebsite + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalWebsiteAds + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalOnlineReviews + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTwitter + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalLinkedIn + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalEmailBlast + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalYouTube + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTV + "</b></font></a></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBillboard + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalRadio + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBrochure + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalDirectMail + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalCitizensDeTar + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalLiveWorkNearby + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFamilyFriend + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalUrgentCare + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalNewspaperMagazine + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalSchool + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalHotel + "</b></font></td>\n");
                MRList.append("</tr>");


                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReport.html");
            } else if (ClientId == 27 || ClientId == 29) {
                long TotalVisitedBefore = 0;
                long TotalFamilyVisitedBefore = 0;
                long TotalInternet = 0;
                long TotalBillboard = 0;
                long TotalGoogle = 0;
                long TotalBuildingSignage = 0;
                long TotalFacebook = 0;
                long TotalLivesNear = 0;
                long TotalTwitter = 0;
                long TotalTv = 0;
                long TotalMapSearch = 0;
                long TotalEvent = 0;

                Query = "select * from  (select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v \n" +
                        "where selected_date >= '" + FromDate + "' and selected_date <= '" + ToDate + "' order by selected_date";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);


                while (rset.next()) {
                    DayDate = "";
                    DayDate = rset.getString(1);


                    Query2 = " Select SUM(FrVisitedBefore),SUM(FrFamiliyVisitedBefore),SUM(FrInternet),SUM(FrBillboard),SUM(FrGoogle),SUM(FrBuildingSignage),SUM(FrFacebook),SUM(FrLivesNear)," +
                            " SUM(FrTwitter),SUM(FrTV),SUM(FrMapSearch),SUM(FrEvent) " +//12
                            " from " + Database + ".RandomCheckInfo where CreatedDate between '" + DayDate + " 00:00:00' and '" + DayDate + " 23:59:59' ";
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        DayDate = DayDate.substring(5, 7) + "/" + DayDate.substring(8, 10) + "/" + DayDate.substring(0, 4);
                        MRList.append("<tr>");
                        MRList.append("<td align=left>" + DayDate + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(1) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(2) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(3) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(4) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(5) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(6) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(7) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(8) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(9) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(10) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(11) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(12) + "</td>\n");
                        MRList.append("</tr>");
                        TotalVisitedBefore += rset2.getLong(1);
                        TotalFamilyVisitedBefore += rset2.getLong(2);
                        TotalInternet += rset2.getLong(3);
                        TotalBillboard += rset2.getLong(4);
                        TotalGoogle += rset2.getLong(5);
                        TotalBuildingSignage += rset2.getLong(6);
                        TotalFacebook += rset2.getLong(7);
                        TotalLivesNear += rset2.getLong(8);
                        TotalTwitter += rset2.getLong(9);
                        TotalTv += rset2.getLong(10);
                        TotalMapSearch += rset2.getLong(11);
                        TotalEvent += rset2.getLong(12);

                    }
                    rset2.close();
                    stmt2.close();
                }
                rset.close();
                stmt.close();
                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left>" + "TOTAL: " + "</td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalVisitedBefore + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFamilyVisitedBefore + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalInternet + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBillboard + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalGoogle + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBuildingSignage + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFacebook + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalLivesNear + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTwitter + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTv + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMapSearch + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalEvent + "</b></font></td>\n");
                MRList.append("</tr>");


                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport_frontline.html");

            } else {
                //TotalCountVariables
                long TotalReturnPatient = 0;
                long TotalGoogle = 0;
                long TotalMapSearch = 0;
                long TotalBillboard = 0;
                long TotalOnlineReview = 0;
                long TotalTV = 0;
                long TotalWebsite = 0;
                long TotalBuildingSignDriveBy = 0;
                long TotalFacebook = 0;
                long TotalSchool = 0;
                long TotalTwitter = 0;
                long TotalMagazine = 0;
                long TotalNewspaper = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalCommunityEvent = 0;

                Query = "select * from  (select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, \n" +
                        "(select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v \n" +
                        "where selected_date >= '" + FromDate + "' and selected_date <= '" + ToDate + "' order by selected_date";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DayDate = "";
                    DayDate = rset.getString(1);


                    Query2 = " Select SUM(ReturnPatient),SUM(Google),SUM(MapSearch),SUM(Billboard),SUM(OnlineReview),SUM(TV),SUM(Website),SUM(BuildingSignDriveBy)," +
                            " SUM(Facebook),SUM(School),SUM(Twitter),SUM(Magazine),SUM(Newspaper),SUM(FamilyFriend),SUM(UrgentCare),SUM(CommunityEvent) " +//16
                            " from " + Database + ".RandomCheckInfo where CreatedDate between '" + DayDate + " 00:00:00' and '" + DayDate + " 23:59:59' ";
                    stmt2 = conn.createStatement();
                    rset2 = stmt2.executeQuery(Query2);
                    if (rset2.next()) {
                        DayDate = DayDate.substring(5, 7) + "/" + DayDate.substring(8, 10) + "/" + DayDate.substring(0, 4);
                        MRList.append("<tr>");
                        MRList.append("<td align=left>" + DayDate + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(1) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(2) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(3) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(4) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(5) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(6) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(7) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(8) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(9) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(10) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(11) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(12) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(13) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(14) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(15) + "</td>\n");
                        MRList.append("<td align=left>" + rset2.getLong(16) + "</td>\n");

                        MRList.append("</tr>");
                        TotalReturnPatient += rset2.getLong(1);
                        TotalGoogle += rset2.getLong(2);
                        TotalMapSearch += rset2.getLong(3);
                        TotalBillboard += rset2.getLong(4);
                        TotalOnlineReview += rset2.getLong(5);
                        TotalTV += rset2.getLong(6);
                        TotalWebsite += rset2.getLong(7);
                        TotalBuildingSignDriveBy += rset2.getLong(8);
                        TotalFacebook += rset2.getLong(9);
                        TotalSchool += rset2.getLong(10);
                        TotalTwitter += rset2.getLong(11);
                        TotalMagazine += rset2.getLong(12);
                        TotalNewspaper += rset2.getLong(13);
                        TotalFamilyFriend += rset2.getLong(14);
                        TotalUrgentCare += rset2.getLong(15);
                        TotalCommunityEvent += rset2.getLong(16);
                    }
                    rset2.close();
                    stmt2.close();
                }
                rset.close();
                stmt.close();
                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left>" + "TOTAL: " + "</td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalReturnPatient + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalGoogle + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMapSearch + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBillboard + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalOnlineReview + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTV + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalWebsite + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalBuildingSignDriveBy + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFacebook + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalSchool + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalTwitter + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalMagazine + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalNewspaper + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalFamilyFriend + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalUrgentCare + "</b></font></td>\n");
                MRList.append("<td align=left><font color=\"white\"><b>" + TotalCommunityEvent + "</b></font></td>\n");
                MRList.append("</tr>");


                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport_others.html");
            }


        } catch (Exception e) {
            out.println("Error: Insertion Notes Table " + e.getMessage());
            Services.DumException("MarketingReport", "Marketing Report Error: ", request, e, this.getServletContext());
            return;
        }
    }


    void MarketingReportDetailInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();


            if (ClientId == 9 || ClientId == 28) {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReport2.html");
            } else if (ClientId == 27 || ClientId == 29) {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport2_frontline.html");
            } else {
                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("ClientId", String.valueOf(ClientId));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport2_others.html");
            }
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void MarketingDetailReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder MRList = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        StringBuilder reportHeaders = new StringBuilder();
        int SNo = 1;

        try {
            if (ClientId == 9 || ClientId == 28) {
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;
                long TotalOnlineAdvertisements = 0;
                long TotalEmployerSentMe = 0;
                long TotalMFPhysicianRefChk = 0;

                Query = " Select IFNULL(a.MRN,''), CONCAT(IFNULL(a.Title,''),' ', IFNULL(a.FirstName,''),' ', IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //2
                        " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Address,''), IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.County,''), IFNULL(a.ZipCode,''), " +
                        " IFNULL(a.PhNumber,''), IFNULL(a.Email,''), IFNULL(a.ReasonVisit,''), " +
                        " CASE WHEN b.MFFirstVisit = 1 THEN 'Yes' WHEN b.MFFirstVisit = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.MFReturnPat = 1 THEN 'Yes' WHEN b.MFReturnPat = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.MFInternetFind = 1 THEN 'Yes' WHEN b.MFInternetFind = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Facebook = 1 THEN 'Yes' WHEN b.Facebook = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.MapSearch = 1 THEN 'Yes' WHEN b.MapSearch = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.GoogleSearch = 1 THEN 'Yes' WHEN b.GoogleSearch = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.VERWebsite = 1 THEN 'Yes' WHEN b.VERWebsite = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.WebsiteAds = 1 THEN 'Yes' WHEN b.WebsiteAds = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.OnlineReviews = 1 THEN 'Yes' WHEN b.OnlineReviews = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Twitter = 1 THEN 'Yes' WHEN b.Twitter = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.LinkedIn = 1 THEN 'Yes' WHEN b.LinkedIn = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.EmailBlast = 1 THEN 'Yes' WHEN b.EmailBlast = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.YouTube = 1 THEN 'Yes' WHEN b.YouTube = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.TV = 1 THEN 'Yes' WHEN b.TV = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Billboard = 1 THEN 'Yes' WHEN b.Billboard = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Radio = 1 THEN 'Yes' WHEN b.Radio = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Brochure = 1 THEN 'Yes' WHEN b.Brochure = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.DirectMail = 1 THEN 'Yes' WHEN b.DirectMail = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.CitizensDeTar = 1 THEN 'Yes' WHEN b.CitizensDeTar = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.LiveWorkNearby = 1 THEN 'Yes' WHEN b.LiveWorkNearby = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FamilyFriend = 1 THEN IFNULL(b.FamilyFriend_text,'') WHEN b.FamilyFriend = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.UrgentCare = 1 THEN IFNULL(b.UrgentCare_text,'') WHEN b.UrgentCare = 0 THEN 'No' ELSE '' END, " +
                        " CASE WHEN b.NewspaperMagazine = 1 THEN IFNULL(b.NewspaperMagazine_text,'') WHEN b.NewspaperMagazine = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.School = 1 THEN IFNULL(b.School_text,'') WHEN b.School = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.Hotel = 1 THEN IFNULL(b.Hotel_text,'') WHEN b.Hotel = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.MFPhysicianRefChk = 1 THEN IFNULL(b.MFPhysician,'') WHEN b.MFPhysicianRefChk = 0 THEN 'No' ELSE '' END, \n" +
                        " IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'),DATE_FORMAT(a.DateofService,'%m/%d/%Y %T')), " +
                        " CASE WHEN EmployerSentMe = 1 THEN IFNULL(EmployerSentMe_text,'') WHEN EmployerSentMe = 0 THEN 'No' ELSE '' END, " +
                        " IFNULL(b.MFFirstVisit,0),IFNULL(b.MFReturnPat,0),IFNULL(b.MFInternetFind,0),IFNULL(b.Facebook,0),IFNULL(b.MapSearch,0),\n" +
                        " IFNULL(b.GoogleSearch,0),IFNULL(b.VERWebsite,0),IFNULL(b.WebsiteAds,0),IFNULL(b.OnlineReviews,0),IFNULL(b.Twitter,0),\n" +
                        " IFNULL(b.LinkedIn,0),IFNULL(b.EmailBlast,0),IFNULL(b.YouTube,0),IFNULL(b.TV,0),IFNULL(b.Billboard,0),IFNULL(b.Radio,0),\n" +
                        " IFNULL(b.Brochure,0),IFNULL(b.DirectMail,0),IFNULL(b.CitizensDeTar,0),IFNULL(b.LiveWorkNearby,0),IFNULL(b.FamilyFriend,0),\n" +
                        " IFNULL(b.UrgentCare,0),IFNULL(b.NewspaperMagazine,0),IFNULL(b.School,0),IFNULL(b.Hotel,0),IFNULL(b.OnlineAdvertisements,0),\n" +
                        " IFNULL(b.EmployerSentMe,0),IFNULL(b.MFPhysicianRefChk,0), " +
                        " CASE WHEN b.OnlineAdvertisements = 1 THEN 'Yes' WHEN b.OnlineAdvertisements = 0 THEN 'No' ELSE '' END,\n" +
                        " CONCAT(IFNULL(c.DoctorsLastName,''),' ',IFNULL(c.DoctorsFirstName,'')) " +
                        " from " + Database + ".PatientReg a " +
                        " STRAIGHT_JOIN " + Database + ".PatientVisit d on d.PatientRegId = a.ID " +
                        " LEFT JOIN " + Database + ".MarketingInfo b on a.ID = b.PatientRegId " +
                        " LEFT JOIN " + Database + ".DoctorsList c ON a.DoctorsName = c.Id " +
                        " WHERE " +
                        " DATE_FORMAT(d.DateofService,'%Y-%m-%d %T') between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' " +
                        " AND a.Status = 0 GROUP BY a.MRN ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MRList.append("<tr>");
//                    MRList.append("<td align=left>" + SNo + "</td>\n");//SNO
//                    MRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//MRN
                    MRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//NAME PATINET
//                    MRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//DOB
//                    MRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//address
//                    MRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//City
//                    MRList.append("<td align=left>" + rset.getString(6) + "</td>\n");//State
//                    MRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//County
                    MRList.append("<td align=left>" + rset.getString(8) + "</td>\n");//ZipCode
//                    MRList.append("<td align=left>" + rset.getString(9) + "</td>\n");//Number
                    MRList.append("<td align=left>" + rset.getString(10) + "</td>\n");//Email
                    MRList.append("<td align=left>" + rset.getString(11) + "</td>\n");//ReasonV
                    MRList.append("<td align=left>" + rset.getString(69) + "</td>\n");//Doctors Name
                    MRList.append("<td align=left>" + rset.getString(12) + "</td>\n");//firstVisit
                    MRList.append("<td align=left>" + rset.getString(13) + "</td>\n");//Return Visit
                    MRList.append("<td align=left>" + rset.getString(20) + "</td>\n");//Online Review//internetfind
                    MRList.append("<td align=left>" + rset.getString(15) + "</td>\n");//Facebook
                    MRList.append("<td align=left>" + rset.getString(16) + "</td>\n");//MapSearch
                    MRList.append("<td align=left>" + rset.getString(17) + "</td>\n");//Google Search
                    MRList.append("<td align=left>" + rset.getString(18) + "</td>\n");//VER.com
                    MRList.append("<td align=left>" + rset.getString(19) + "</td>\n");//Website adds
                    MRList.append("<td align=left>" + rset.getString(68) + "</td>\n");//Online Adds//online reviews
                    MRList.append("<td align=left>" + rset.getString(21) + "</td>\n");//Twioyter
                    MRList.append("<td align=left>" + rset.getString(22) + "</td>\n");//Linkedin
                    MRList.append("<td align=left>" + rset.getString(23) + "</td>\n");//Email blast
                    MRList.append("<td align=left>" + rset.getString(24) + "</td>\n");//Youtube
                    MRList.append("<td align=left>" + rset.getString(25) + "</td>\n");//TV
                    MRList.append("<td align=left>" + rset.getString(26) + "</td>\n");//Billboard
                    MRList.append("<td align=left>" + rset.getString(27) + "</td>\n");//Radion
                    MRList.append("<td align=left>" + rset.getString(28) + "</td>\n");//Brochure
                    MRList.append("<td align=left>" + rset.getString(29) + "</td>\n");//Directmail
                    MRList.append("<td align=left>" + rset.getString(30) + "</td>\n");//Citizendetar
                    MRList.append("<td align=left>" + rset.getString(31) + "</td>\n");//Live work near by
                    MRList.append("<td align=left>" + rset.getString(32) + "</td>\n");// Frond familyh
                    MRList.append("<td align=left>" + rset.getString(33) + "</td>\n");//urgeny care
                    MRList.append("<td align=left>" + rset.getString(34) + "</td>\n");//news magzsine
                    MRList.append("<td align=left>" + rset.getString(35) + "</td>\n");//school
                    MRList.append("<td align=left>" + rset.getString(36) + "</td>\n");//hotel
                    MRList.append("<td align=left>" + rset.getString(37) + "</td>\n");//Physician Ref
                    MRList.append("<td align=left>" + rset.getString(39) + "</td>\n");//EmpSent
                    MRList.append("<td align=left>" + rset.getString(38) + "</td>\n");//DOS
//                    MRList.append("<td align=left>" + rset.getString(69) + "</td>\n");//Doctors Name
                    MRList.append("</tr>");

                    TotalMFFirstVisit += rset.getLong(40);
                    TotalMFReturnPat += rset.getLong(41);
                    TotalMFInternetFind += rset.getLong(42);
                    TotalFacebook += rset.getLong(43);
                    TotalMapSearch += rset.getLong(44);
                    TotalGoogleSearch += rset.getLong(45);
                    TotalVERWebsite += rset.getLong(46);
                    TotalWebsiteAds += rset.getLong(47);
                    TotalOnlineReviews += rset.getLong(48);
                    TotalTwitter += rset.getLong(49);
                    TotalLinkedIn += rset.getLong(50);
                    TotalEmailBlast += rset.getLong(51);
                    TotalYouTube += rset.getLong(52);
                    TotalTV += rset.getLong(53);
                    TotalBillboard += rset.getLong(54);
                    TotalRadio += rset.getLong(55);
                    TotalBrochure += rset.getLong(56);
                    TotalDirectMail += rset.getLong(57);
                    TotalCitizensDeTar += rset.getLong(58);
                    TotalLiveWorkNearby += rset.getLong(59);
                    TotalFamilyFriend += rset.getLong(60);
                    TotalUrgentCare += rset.getLong(61);
                    TotalNewspaperMagazine += rset.getLong(62);
                    TotalSchool += rset.getLong(63);
                    TotalHotel += rset.getLong(64);
                    TotalOnlineAdvertisements += rset.getLong(65);
                    TotalEmployerSentMe += rset.getLong(66);
                    TotalMFPhysicianRefChk += rset.getLong(67);

                }
                rset.close();
                stmt.close();
                MRList.append("<tr style=\"background-color:#00FF00\">");
                MRList.append("<td align=left ></td>\n");//TOTAL
                MRList.append("<td align=left ></td>\n");//TOTAL
                MRList.append("<td align=left ></td>\n");//TOTAL
                MRList.append("<td align=left ></td>\n");//TOTAL
                MRList.append("<td align=left >TOTAL</td>\n");//TOTAL
                MRList.append("<td align=left>" + TotalMFFirstVisit + "</td>\n");
                MRList.append("<td align=left>" + TotalMFReturnPat + "</td>\n");
                MRList.append("<td align=left>" + TotalOnlineReviews + "</td>\n");
                MRList.append("<td align=left>" + TotalFacebook + "</td>\n");
                MRList.append("<td align=left>" + TotalMapSearch + "</td>\n");
                MRList.append("<td align=left>" + TotalGoogleSearch + "</td>\n");
                MRList.append("<td align=left>" + TotalVERWebsite + "</td>\n");
                MRList.append("<td align=left>" + TotalWebsiteAds + "</td>\n");
                MRList.append("<td align=left>" + TotalOnlineAdvertisements + "</td>\n");
                MRList.append("<td align=left>" + TotalTwitter + "</td>\n");
                MRList.append("<td align=left>" + TotalLinkedIn + "</td>\n");
                MRList.append("<td align=left>" + TotalEmailBlast + "</td>\n");
                MRList.append("<td align=left>" + TotalYouTube + "</td>\n");
                MRList.append("<td align=left>" + TotalTV + "</td>\n");
                MRList.append("<td align=left>" + TotalBillboard + "</td>\n");
                MRList.append("<td align=left>" + TotalRadio + "</td>\n");
                MRList.append("<td align=left>" + TotalBrochure + "</td>\n");
                MRList.append("<td align=left>" + TotalDirectMail + "</td>\n");
                MRList.append("<td align=left>" + TotalCitizensDeTar + "</td>\n");
                MRList.append("<td align=left>" + TotalLiveWorkNearby + "</td>\n");
                MRList.append("<td align=left>" + TotalFamilyFriend + "</td>\n");
                MRList.append("<td align=left>" + TotalUrgentCare + "</td>\n");
                MRList.append("<td align=left>" + TotalNewspaperMagazine + "</td>\n");
                MRList.append("<td align=left>" + TotalSchool + "</td>\n");
                MRList.append("<td align=left>" + TotalHotel + "</td>\n");
                MRList.append("<td align=left>" + TotalMFPhysicianRefChk + "</td>\n");
                MRList.append("<td align=left>" + TotalEmployerSentMe + "</td>\n");
                MRList.append("<td align=left>" + "" + "</td>\n");

                MRList.append("</tr>");

                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReport2.html");
            } else if (ClientId == 27 || ClientId == 29) {
                Query = " Select IFNULL(a.MRN,''), CONCAT(IFNULL(a.Title,''),' ', IFNULL(a.FirstName,''),' ', IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " +
                        " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Address,''), IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.County,''), IFNULL(a.ZipCode,''), " +
                        " IFNULL(a.PhNumber,''), IFNULL(a.Email,''), IFNULL(a.ReasonVisit,''), " +
                        " CASE WHEN b.FrVisitedBefore = 1 THEN 'Yes' WHEN b.FrVisitedBefore = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrFamiliyVisitedBefore = 1 THEN 'Yes' WHEN b.FrFamiliyVisitedBefore = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrInternet = 1 THEN 'Yes' WHEN b.FrInternet = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrBillboard = 1 THEN 'Yes' WHEN b.FrBillboard = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrGoogle = 1 THEN 'Yes' WHEN b.FrGoogle = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrBuildingSignage = 1 THEN 'Yes' WHEN b.FrBuildingSignage = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrFacebook = 1 THEN 'Yes' WHEN b.FrFacebook = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrLivesNear = 1 THEN 'Yes' WHEN b.FrLivesNear = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrTwitter = 1 THEN 'Yes' WHEN b.FrTwitter = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrTV = 1 THEN 'Yes' WHEN b.FrTV = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrMapSearch = 1 THEN 'Yes' WHEN b.FrMapSearch = 0 THEN 'No' ELSE '' END,\n" +
                        " CASE WHEN b.FrEvent = 1 THEN 'Yes' WHEN b.FrEvent = 0 THEN 'No' ELSE '' END,\n" +
                        " IFNULL(b.FrPhysicianReferral,''),\n" +
                        " IFNULL(b.FrNeurologyReferral,''),\n " +
                        " IFNULL(b.FrUrgentCareReferral,''), \n " +
                        " IFNULL(b.FrOrganizationReferral,''), \n " +
                        " IFNULL(b.FrFriendFamily,''), \n " +
                        " DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),CONCAT(IFNULL(c.DoctorsLastName,''),' ',IFNULL(c.DoctorsFirstName,''))" +
                        " FROM " + Database + ".PatientReg a " +
                        " STRAIGHT_JOIN " + Database + ".PatientVisit d on d.PatientRegId = a.ID " +
                        " LEFT JOIN " + Database + ".RandomCheckInfo b on a.ID = b.PatientRegId " +
                        " LEFT JOIN " + Database + ".DoctorsList c ON a.DoctorsName = c.Id where" +
                        " DATE_FORMAT(d.DateofService,'%Y-%m-%d %T') between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' " +
                        " AND a.Status = 0 GROUP BY a.MRN ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MRList.append("<tr>");
                    MRList.append("<td align=left>" + SNo + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(30) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(17) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(18) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(19) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(20) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(21) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(22) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(23) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(24) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(25) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(26) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(27) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(28) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(29) + "</td>\n");

                    MRList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();


                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport2_frontline.html");
            } else {

                String Attorney = "";

                if (ClientId == 41 || ClientId == 42 || ClientId == 43) {
                    reportHeaders.append("<th>SrNo</th>");
                    reportHeaders.append("<th>MRN</th>");
                    reportHeaders.append("<th>Patient Name</th>");
                    reportHeaders.append("<th>DOB</th>");
                    reportHeaders.append("<th>Address</th>");
                    reportHeaders.append("<th>City</th>");
                    reportHeaders.append("<th>State</th>");
                    reportHeaders.append("<th>County</th>");
                    reportHeaders.append("<th>ZipCode</th>");
                    reportHeaders.append("<th>Number</th>");
                    reportHeaders.append("<th>Email</th>");
                    reportHeaders.append("<th>ReasonVisit</th>");
                    reportHeaders.append("<th>ER Physician</th>");
                    reportHeaders.append("<th>Attorney</th>");
                    reportHeaders.append("<th>Return Patient</th>");
                    reportHeaders.append("<th>Google</th>");
                    reportHeaders.append("<th>Map Search</th>");
                    reportHeaders.append("<th>Billboard</th>");
                    reportHeaders.append("<th>Online Review</th>");
                    reportHeaders.append("<th>TV</th>");
                    reportHeaders.append("<th>Website</th>");
                    reportHeaders.append("<th>Building Sign Drive By</th>");
                    reportHeaders.append("<th>Facebook</th>");
                    reportHeaders.append("<th>School</th>");
                    reportHeaders.append("<th>Twitter</th>");
                    reportHeaders.append("<th>Magazine</th>");
                    reportHeaders.append("<th>Newspaper</th>");
                    reportHeaders.append("<th>Family Friend</th>");
                    reportHeaders.append("<th>Urgent Care</th>");
                    reportHeaders.append("<th>Community Event</th>");
                    reportHeaders.append("<th>Work Referral</th>");
                    reportHeaders.append("<th>Physician Referral</th>");
                    reportHeaders.append("<th>Other Referral</th>");
                    reportHeaders.append("<th>DOS</th>");

                    Attorney = ",IFNULL(b.Attorney,''),IFNULL(b.Instagram_text,''),IFNULL(b.Youtube_text,''),IFNULL(b.Spotify_text,'')";


                } else {
                    reportHeaders.append("<th>SrNo</th>");
                    reportHeaders.append("<th>MRN</th>");
                    reportHeaders.append("<th>Patient Name</th>");
                    reportHeaders.append("<th>DOB</th>");
                    reportHeaders.append("<th>Address</th>");
                    reportHeaders.append("<th>City</th>");
                    reportHeaders.append("<th>State</th>");
                    reportHeaders.append("<th>County</th>");
                    reportHeaders.append("<th>ZipCode</th>");
                    reportHeaders.append("<th>Number</th>");
                    reportHeaders.append("<th>Email</th>");
                    reportHeaders.append("<th>ReasonVisit</th>");
                    reportHeaders.append("<th>ER Physician</th>");
                    reportHeaders.append("<th>Return Patient</th>");
                    reportHeaders.append("<th>Google</th>");
                    reportHeaders.append("<th>Map Search</th>");
                    reportHeaders.append("<th>Billboard</th>");
                    reportHeaders.append("<th>Online Review</th>");
                    reportHeaders.append("<th>TV</th>");
                    reportHeaders.append("<th>Website</th>");
                    reportHeaders.append("<th>Building Sign Drive By</th>");
                    reportHeaders.append("<th>Facebook</th>");
                    reportHeaders.append("<th>School</th>");
                    reportHeaders.append("<th>Twitter</th>");
                    reportHeaders.append("<th>Magazine</th>");
                    reportHeaders.append("<th>Newspaper</th>");
                    reportHeaders.append("<th>Family Friend</th>");
                    reportHeaders.append("<th>Urgent Care</th>");
                    reportHeaders.append("<th>Community Event</th>");
                    reportHeaders.append("<th>Work Referral</th>");
                    reportHeaders.append("<th>Physician Referral</th>");
                    reportHeaders.append("<th>Other Referral</th>");
                    reportHeaders.append("<th>DOS</th>");
                    Attorney = "";
                }


                Query = " Select IFNULL(a.MRN,''), CONCAT(IFNULL(a.Title,''),' ', IFNULL(a.FirstName,''),' ', IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), " + //2
                        " IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Address,''), IFNULL(a.City,''), IFNULL(a.State,''), IFNULL(a.County,''), IFNULL(a.ZipCode,''), " + //8
                        " IFNULL(a.PhNumber,''), IFNULL(a.Email,''), IFNULL(a.ReasonVisit,''), " + //11
                        " CASE WHEN b.ReturnPatient = 1 THEN 'Yes' WHEN b.ReturnPatient = 0 THEN 'No' ELSE '' END,\n" + //12
                        " CASE WHEN b.Google = 1 THEN 'Yes' WHEN b.Google = 0 THEN 'No' ELSE '' END,\n" +//13
                        " CASE WHEN b.MapSearch = 1 THEN 'Yes' WHEN b.MapSearch = 0 THEN 'No' ELSE '' END,\n" +//14
                        " CASE WHEN b.Billboard = 1 THEN 'Yes' WHEN b.Billboard = 0 THEN 'No' ELSE '' END,\n" +//15
                        " CASE WHEN b.OnlineReview = 1 THEN 'Yes' WHEN b.OnlineReview = 0 THEN 'No' ELSE '' END,\n" +//16
                        " CASE WHEN b.TV = 1 THEN 'Yes' WHEN b.TV = 0 THEN 'No' ELSE '' END,\n" +//17
                        " CASE WHEN b.Website = 1 THEN 'Yes' WHEN b.Website = 0 THEN 'No' ELSE '' END,\n" +//18
                        " CASE WHEN b.BuildingSignDriveBy = 1 THEN 'Yes' WHEN b.BuildingSignDriveBy = 0 THEN 'No' ELSE '' END,\n" + //19
                        " CASE WHEN b.Facebook = 1 THEN 'Yes' WHEN b.Facebook = 0 THEN 'No' ELSE '' END,\n" +//20
                        " CASE WHEN b.School = 1 THEN IFNULL(b.School_text,'') WHEN b.School = 0 THEN 'No' ELSE '' END,\n" +//21
                        " CASE WHEN b.Twitter = 1 THEN 'Yes' WHEN b.Twitter = 0 THEN 'No' ELSE '' END,\n" +//22
                        " CASE WHEN b.Magazine = 1 THEN IFNULL(b.Magazine_text,'') WHEN b.Magazine = 0 THEN 'No' ELSE '' END, " +//23
                        " CASE WHEN b.Newspaper = 1 THEN IFNULL(b.Newspaper_text,'') WHEN b.Newspaper = 0 THEN 'No' ELSE '' END,\n" +//24
                        " CASE WHEN b.FamilyFriend = 1 THEN IFNULL(b.FamilyFriend_text,'') WHEN b.FamilyFriend = 0 THEN 'No' ELSE '' END,\n" +//25
                        " CASE WHEN b.UrgentCare = 1 THEN IFNULL(b.UrgentCare_text,'') WHEN b.UrgentCare = 0 THEN 'No' ELSE '' END,\n" + //26
                        " CASE WHEN b.CommunityEvent = 1 THEN IFNULL(b.CommunityEvent_text,'') WHEN b.CommunityEvent = 0 THEN 'No' ELSE '' END, \n" +//27
                        " IFNULL(b.Work_text,''), IFNULL(b.Physician_text,''), IFNULL(b.Other_text,''), \n " + //30
                        " DATE_FORMAT(a.DateofService,'%m/%d/%Y %T')," + //31
                        " CONCAT(IFNULL(c.DoctorsLastName,''),' ',IFNULL(c.DoctorsFirstName,''))" + Attorney + "" + //32
                        " FROM " + Database + ".PatientReg a " +
                        " STRAIGHT_JOIN " + Database + ".PatientVisit d on d.PatientRegId = a.ID " +
                        " LEFT JOIN " + Database + ".RandomCheckInfo b on a.ID = b.PatientRegId " +
                        " LEFT JOIN " + Database + ".DoctorsList c ON a.DoctorsName = c.Id " +
                        " WHERE " +
                        " DATE_FORMAT(d.DateofService,'%Y-%m-%d %T') between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' " +
                        " AND a.Status = 0 GROUP BY a.MRN ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    MRList.append("<tr>");
                    MRList.append("<td align=left>" + SNo + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(32) + "</td>\n");

                    if (ClientId == 41 || ClientId == 42 || ClientId == 43)
                        MRList.append("<td align=left>" + rset.getString(33) + "</td>\n");


                    MRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(17) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(18) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(19) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(20) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(21) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(22) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(23) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(24) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(25) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(26) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(27) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(28) + "</td>\n");
                    MRList.append("<td align=left>" + rset.getString(29) + "</td>\n");

                    MRList.append("<td align=left>" + rset.getString(30) + "</td>\n");
                    if (ClientId == 41 || ClientId == 42 || ClientId == 43) {
                        MRList.append("<td align=left>" + rset.getString(34) + "</td>\n");
                        MRList.append("<td align=left>" + rset.getString(35) + "</td>\n");
                        MRList.append("<td align=left>" + rset.getString(36) + "</td>\n");
                    }
                    MRList.append("<td align=left>" + rset.getString(31) + "</td>\n");

//                    if(ClientId == 41 || ClientId == 42 || ClientId == 43)
//                        MRList.append("<td align=left>" + rset.getString(33) + "</td>\n");


//                    MRList.append("<td align=left>" + rset.getString(32) + "</td>\n");
                    MRList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();


                Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
                LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
                Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("ClientId", String.valueOf(ClientId));
                Parser.SetField("MRList", String.valueOf(MRList));
                Parser.SetField("reportHeaders", String.valueOf(reportHeaders));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("Header", String.valueOf(Header));
                Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
                Parser.SetField("Footer", String.valueOf(Footer));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/MarketingReport2_others.html");

            }
        } catch (Exception e) {
            out.println(e.getMessage() + Query);
        }

    }


    void MarketingReportDashboard(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {

            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            if (ClientId == 27) {
                String MSE = "";
                String AMA = "";
                String LWBS = "";
                String Eloped = "";
                System.out.println("joooo req prams:" + request.getParameter("FromDate") + " : " + request.getParameter("ToDate"));
                Query = "select ReasonLeaving,COUNT(*) from frontlin_er.Patient_AdditionalInfo where ReasonLeaving!='' and " +
                        " ReasonLeaving!='0' GROUP BY ReasonLeaving ORDER BY ReasonLeaving";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(1).equals("1")) {
                        MSE = rset.getString(2);
                    } else if (rset.getString(1).equals("2")) {
                        AMA = rset.getString(2);
                    } else if (rset.getString(1).equals("3")) {
                        LWBS = rset.getString(2);
                    } else if (rset.getString(1).equals("4")) {
                        Eloped = rset.getString(2);
                    } else {
                        System.out.println("joooo: Done!");
                    }
                }
                System.out.println("joooo: " + MSE + " : " + AMA + " : " + LWBS + " : " + Eloped);
                rset.close();
                stmt.close();
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
//                Parser.SetField("FromDate", String.valueOf(FromDate));
//                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("MSE", MSE);
                Parser.SetField("AMA", AMA);
                Parser.SetField("LWBS", LWBS);
                Parser.SetField("Eloped", Eloped);

                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_frontline.html");
                return;
            }
            if (ClientId == 27 || ClientId == 29) {
                long TotalVisitedBefore = 0;
                long TotalFamilyVisitedBefore = 0;
                long TotalInternet = 0;
                long TotalBillboard = 0;
                long TotalGoogle = 0;
                long TotalBuildingSignage = 0;
                long TotalFacebook = 0;
                long TotalLivesNear = 0;
                long TotalTwitter = 0;
                long TotalTv = 0;
                long TotalMapSearch = 0;
                long TotalEvent = 0;

                Query = " Select SUM(FrVisitedBefore),SUM(FrFamiliyVisitedBefore),SUM(FrInternet),SUM(FrBillboard),SUM(FrGoogle),SUM(FrBuildingSignage),SUM(FrFacebook),SUM(FrLivesNear)," +
                        " SUM(FrTwitter),SUM(FrTV),SUM(FrMapSearch),SUM(FrEvent) " +//12
                        " from " + Database + ".RandomCheckInfo";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    TotalVisitedBefore += rset.getLong(1);
                    TotalFamilyVisitedBefore += rset.getLong(2);
                    TotalInternet += rset.getLong(3);
                    TotalBillboard += rset.getLong(4);
                    TotalGoogle += rset.getLong(5);
                    TotalBuildingSignage += rset.getLong(6);
                    TotalFacebook += rset.getLong(7);
                    TotalLivesNear += rset.getLong(8);
                    TotalTwitter += rset.getLong(9);
                    TotalTv += rset.getLong(10);
                    TotalMapSearch += rset.getLong(11);
                    TotalEvent += rset.getLong(12);

                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
//                Parser.SetField("FromDate", String.valueOf(FromDate));
//                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("TotalVisitedBefore", String.valueOf(TotalVisitedBefore));
                Parser.SetField("TotalFamilyVisitedBefore", String.valueOf(TotalFamilyVisitedBefore));
                Parser.SetField("TotalInternet", String.valueOf(TotalInternet));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalGoogle", String.valueOf(TotalGoogle));
                Parser.SetField("TotalBuildingSignage", String.valueOf(TotalBuildingSignage));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalTv", String.valueOf(TotalTv));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalEvent", String.valueOf(TotalEvent));
                Parser.SetField("TotalLivesNear", String.valueOf(TotalLivesNear));

                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_frontline.html");

            } else if (ClientId == 9 || ClientId == 28) {
                //TotalCountVariables
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;


                Query = " Select SUM(MFFirstVisit),SUM(MFReturnPat),SUM(MFInternetFind),SUM(Facebook),SUM(MapSearch),SUM(GoogleSearch),SUM(VERWebsite),SUM(WebsiteAds)," +
                        " SUM(OnlineReviews),SUM(Twitter),SUM(LinkedIn),SUM(EmailBlast),SUM(YouTube),SUM(TV),SUM(Billboard),SUM(Radio),SUM(Brochure),SUM(DirectMail)," +
                        " SUM(CitizensDeTar),SUM(LiveWorkNearby),SUM(FamilyFriend),SUM(UrgentCare),SUM(NewspaperMagazine),SUM(School),SUM(Hotel) " +//25
                        " from " + Database + ".MarketingInfo";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {

                    TotalMFFirstVisit += rset.getLong(1);
                    TotalMFReturnPat += rset.getLong(2);
                    TotalMFInternetFind += rset.getLong(3);
                    TotalFacebook += rset.getLong(4);
                    TotalMapSearch += rset.getLong(5);
                    TotalGoogleSearch += rset.getLong(6);
                    TotalVERWebsite += rset.getLong(7);
                    TotalWebsiteAds += rset.getLong(8);
                    TotalOnlineReviews += rset.getLong(9);
                    TotalTwitter += rset.getLong(10);
                    TotalLinkedIn += rset.getLong(11);
                    TotalEmailBlast += rset.getLong(12);
                    TotalYouTube += rset.getLong(13);
                    TotalTV += rset.getLong(14);
                    TotalBillboard += rset.getLong(15);
                    TotalRadio += rset.getLong(16);
                    TotalBrochure += rset.getLong(17);
                    TotalDirectMail += rset.getLong(18);
                    TotalCitizensDeTar += rset.getLong(19);
                    TotalLiveWorkNearby += rset.getLong(20);
                    TotalFamilyFriend += rset.getLong(21);
                    TotalUrgentCare += rset.getLong(22);
                    TotalNewspaperMagazine += rset.getLong(23);
                    TotalSchool += rset.getLong(24);
                    TotalHotel += rset.getLong(25);
                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("TotalMFFirstVisit", String.valueOf(TotalMFFirstVisit));
                Parser.SetField("TotalMFReturnPat", String.valueOf(TotalMFReturnPat));
                Parser.SetField("TotalMFInternetFind", String.valueOf(TotalMFInternetFind));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalGoogleSearch", String.valueOf(TotalGoogleSearch));
                Parser.SetField("TotalVERWebsite", String.valueOf(TotalVERWebsite));
                Parser.SetField("TotalWebsiteAds", String.valueOf(TotalWebsiteAds));
                Parser.SetField("TotalOnlineReviews", String.valueOf(TotalOnlineReviews));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalLinkedIn", String.valueOf(TotalLinkedIn));
                Parser.SetField("TotalEmailBlast", String.valueOf(TotalEmailBlast));

                Parser.SetField("TotalYouTube", String.valueOf(TotalYouTube));
                Parser.SetField("TotalTV", String.valueOf(TotalTV));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalRadio", String.valueOf(TotalRadio));
                Parser.SetField("TotalBrochure", String.valueOf(TotalBrochure));
                Parser.SetField("TotalDirectMail", String.valueOf(TotalDirectMail));
                Parser.SetField("TotalCitizensDeTar", String.valueOf(TotalCitizensDeTar));


                Parser.SetField("TotalLiveWorkNearby", String.valueOf(TotalLiveWorkNearby));
                Parser.SetField("TotalFamilyFriend", String.valueOf(TotalFamilyFriend));
                Parser.SetField("TotalUrgentCare", String.valueOf(TotalUrgentCare));
                Parser.SetField("TotalNewspaperMagazine", String.valueOf(TotalNewspaperMagazine));

                Parser.SetField("TotalSchool", String.valueOf(TotalSchool));
                Parser.SetField("TotalHotel", String.valueOf(TotalHotel));


                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_Victoria.html");
            } else {

                long TotalReturnPatient = 0;
                long TotalGoogle = 0;
                long TotalMapSearch = 0;
                long TotalBillboard = 0;
                long TotalOnlineReview = 0;
                long TotalTV = 0;
                long TotalWebsite = 0;
                long TotalBuildingSignDriveBy = 0;
                long TotalFacebook = 0;
                long TotalSchool = 0;
                long TotalTwitter = 0;
                long TotalMagazine = 0;
                long TotalNewspaper = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalCommunityEvent = 0;


                Query = " Select SUM(ReturnPatient),SUM(Google),SUM(MapSearch),SUM(Billboard),SUM(OnlineReview),SUM(TV),SUM(Website),SUM(BuildingSignDriveBy)," +
                        " SUM(Facebook),SUM(School),SUM(Twitter),SUM(Magazine),SUM(Newspaper),SUM(FamilyFriend),SUM(UrgentCare),SUM(CommunityEvent) " +//16
                        " from " + Database + ".RandomCheckInfo";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalReturnPatient += rset.getLong(1);
                    TotalGoogle += rset.getLong(2);
                    TotalMapSearch += rset.getLong(3);
                    TotalBillboard += rset.getLong(4);
                    TotalOnlineReview += rset.getLong(5);
                    TotalTV += rset.getLong(6);
                    TotalWebsite += rset.getLong(7);
                    TotalBuildingSignDriveBy += rset.getLong(8);
                    TotalFacebook += rset.getLong(9);
                    TotalSchool += rset.getLong(10);
                    TotalTwitter += rset.getLong(11);
                    TotalMagazine += rset.getLong(12);
                    TotalNewspaper += rset.getLong(13);
                    TotalFamilyFriend += rset.getLong(14);
                    TotalUrgentCare += rset.getLong(15);
                    TotalCommunityEvent += rset.getLong(16);
                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));

                Parser.SetField("TotalReturnPatient", String.valueOf(TotalReturnPatient));
                Parser.SetField("TotalGoogle", String.valueOf(TotalGoogle));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalOnlineReview", String.valueOf(TotalOnlineReview));
                Parser.SetField("TotalTV", String.valueOf(TotalTV));
                Parser.SetField("TotalWebsite", String.valueOf(TotalWebsite));
                Parser.SetField("TotalBuildingSignDriveBy", String.valueOf(TotalBuildingSignDriveBy));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalSchool", String.valueOf(TotalSchool));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalMagazine", String.valueOf(TotalMagazine));

                Parser.SetField("TotalNewspaper", String.valueOf(TotalNewspaper));
                Parser.SetField("TotalFamilyFriend", String.valueOf(TotalFamilyFriend));
                Parser.SetField("TotalUrgentCare", String.valueOf(TotalUrgentCare));
                Parser.SetField("TotalCommunityEvent", String.valueOf(TotalCommunityEvent));


                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_Others.html");
            }
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    void DashBoardDateWise(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String FromDate = request.getParameter("FromDate").trim();
            String ToDate = request.getParameter("ToDate").trim();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            if (ClientId == 27) {
                Query = "select ReasonLeaving,COUNT(*) from frontlin_er.Patient_AdditionalInfo where CreatedDate BETWEEN '" + FromDate + "' and '" + ToDate + "' and ReasonLeaving!='' and " +
                        " ReasonLeaving!='0' GROUP BY ReasonLeaving ORDER BY ReasonLeaving";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                String MSE = "";
                String AMA = "";
                String LWBS = "";
                String Eloped = "";
                while (rset.next()) {
                    if (rset.getString(1).equals("1")) {
                        MSE = rset.getString(2);
                    } else if (rset.getString(1).equals("2")) {
                        AMA = rset.getString(2);
                    } else if (rset.getString(1).equals("3")) {
                        LWBS = rset.getString(2);
                    } else if (rset.getString(1).equals("4")) {
                        Eloped = rset.getString(2);
                    }
                }
                rset.close();
                stmt.close();
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("MSE", MSE);
                Parser.SetField("AMA", AMA);
                Parser.SetField("LWBS", LWBS);
                Parser.SetField("Eloped", Eloped);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_frontline.html");
                return;
            }
            Query = "";

            if (ClientId == 27 || ClientId == 29) {
                long TotalVisitedBefore = 0;
                long TotalFamilyVisitedBefore = 0;
                long TotalInternet = 0;
                long TotalBillboard = 0;
                long TotalGoogle = 0;
                long TotalBuildingSignage = 0;
                long TotalFacebook = 0;
                long TotalLivesNear = 0;
                long TotalTwitter = 0;
                long TotalTv = 0;
                long TotalMapSearch = 0;
                long TotalEvent = 0;

                Query = " Select SUM(FrVisitedBefore),SUM(FrFamiliyVisitedBefore),SUM(FrInternet),SUM(FrBillboard),SUM(FrGoogle),SUM(FrBuildingSignage),SUM(FrFacebook),SUM(FrLivesNear)," +
                        " SUM(FrTwitter),SUM(FrTV),SUM(FrMapSearch),SUM(FrEvent) " +//12
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' ";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalVisitedBefore += rset.getLong(1);
                    TotalFamilyVisitedBefore += rset.getLong(2);
                    TotalInternet += rset.getLong(3);
                    TotalBillboard += rset.getLong(4);
                    TotalGoogle += rset.getLong(5);
                    TotalBuildingSignage += rset.getLong(6);
                    TotalFacebook += rset.getLong(7);
                    TotalLivesNear += rset.getLong(8);
                    TotalTwitter += rset.getLong(9);
                    TotalTv += rset.getLong(10);
                    TotalMapSearch += rset.getLong(11);
                    TotalEvent += rset.getLong(12);

                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
//                Parser.SetField("FromDate", String.valueOf(FromDate));
//                Parser.SetField("ToDate", String.valueOf(ToDate));
                Parser.SetField("TotalVisitedBefore", String.valueOf(TotalVisitedBefore));
                Parser.SetField("TotalFamilyVisitedBefore", String.valueOf(TotalFamilyVisitedBefore));
                Parser.SetField("TotalInternet", String.valueOf(TotalInternet));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalGoogle", String.valueOf(TotalGoogle));
                Parser.SetField("TotalBuildingSignage", String.valueOf(TotalBuildingSignage));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalTv", String.valueOf(TotalTv));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalEvent", String.valueOf(TotalEvent));
                Parser.SetField("TotalLivesNear", String.valueOf(TotalLivesNear));
                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));

                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_frontline.html");

            } else if (ClientId == 9 || ClientId == 28) {
                //TotalCountVariables
                long TotalMFFirstVisit = 0;
                long TotalMFReturnPat = 0;
                long TotalMFInternetFind = 0;
                long TotalFacebook = 0;
                long TotalMapSearch = 0;
                long TotalGoogleSearch = 0;
                long TotalVERWebsite = 0;
                long TotalWebsiteAds = 0;
                long TotalOnlineReviews = 0;
                long TotalTwitter = 0;
                long TotalLinkedIn = 0;
                long TotalEmailBlast = 0;
                long TotalYouTube = 0;
                long TotalTV = 0;
                long TotalBillboard = 0;
                long TotalRadio = 0;
                long TotalBrochure = 0;
                long TotalDirectMail = 0;
                long TotalCitizensDeTar = 0;
                long TotalLiveWorkNearby = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalNewspaperMagazine = 0;
                long TotalSchool = 0;
                long TotalHotel = 0;


                Query = " Select SUM(MFFirstVisit),SUM(MFReturnPat),SUM(MFInternetFind),SUM(Facebook),SUM(MapSearch),SUM(GoogleSearch),SUM(VERWebsite),SUM(WebsiteAds)," +
                        " SUM(OnlineReviews),SUM(Twitter),SUM(LinkedIn),SUM(EmailBlast),SUM(YouTube),SUM(TV),SUM(Billboard),SUM(Radio),SUM(Brochure),SUM(DirectMail)," +
                        " SUM(CitizensDeTar),SUM(LiveWorkNearby),SUM(FamilyFriend),SUM(UrgentCare),SUM(NewspaperMagazine),SUM(School),SUM(Hotel) " +//25
                        " from " + Database + ".MarketingInfo where CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {

                    TotalMFFirstVisit += rset.getLong(1);
                    TotalMFReturnPat += rset.getLong(2);
                    TotalMFInternetFind += rset.getLong(3);
                    TotalFacebook += rset.getLong(4);
                    TotalMapSearch += rset.getLong(5);
                    TotalGoogleSearch += rset.getLong(6);
                    TotalVERWebsite += rset.getLong(7);
                    TotalWebsiteAds += rset.getLong(8);
                    TotalOnlineReviews += rset.getLong(9);
                    TotalTwitter += rset.getLong(10);
                    TotalLinkedIn += rset.getLong(11);
                    TotalEmailBlast += rset.getLong(12);
                    TotalYouTube += rset.getLong(13);
                    TotalTV += rset.getLong(14);
                    TotalBillboard += rset.getLong(15);
                    TotalRadio += rset.getLong(16);
                    TotalBrochure += rset.getLong(17);
                    TotalDirectMail += rset.getLong(18);
                    TotalCitizensDeTar += rset.getLong(19);
                    TotalLiveWorkNearby += rset.getLong(20);
                    TotalFamilyFriend += rset.getLong(21);
                    TotalUrgentCare += rset.getLong(22);
                    TotalNewspaperMagazine += rset.getLong(23);
                    TotalSchool += rset.getLong(24);
                    TotalHotel += rset.getLong(25);
                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("TotalMFFirstVisit", String.valueOf(TotalMFFirstVisit));
                Parser.SetField("TotalMFReturnPat", String.valueOf(TotalMFReturnPat));
                Parser.SetField("TotalMFInternetFind", String.valueOf(TotalMFInternetFind));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalGoogleSearch", String.valueOf(TotalGoogleSearch));
                Parser.SetField("TotalVERWebsite", String.valueOf(TotalVERWebsite));
                Parser.SetField("TotalWebsiteAds", String.valueOf(TotalWebsiteAds));
                Parser.SetField("TotalOnlineReviews", String.valueOf(TotalOnlineReviews));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalLinkedIn", String.valueOf(TotalLinkedIn));
                Parser.SetField("TotalEmailBlast", String.valueOf(TotalEmailBlast));

                Parser.SetField("TotalYouTube", String.valueOf(TotalYouTube));
                Parser.SetField("TotalTV", String.valueOf(TotalTV));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalRadio", String.valueOf(TotalRadio));
                Parser.SetField("TotalBrochure", String.valueOf(TotalBrochure));
                Parser.SetField("TotalDirectMail", String.valueOf(TotalDirectMail));
                Parser.SetField("TotalCitizensDeTar", String.valueOf(TotalCitizensDeTar));


                Parser.SetField("TotalLiveWorkNearby", String.valueOf(TotalLiveWorkNearby));
                Parser.SetField("TotalFamilyFriend", String.valueOf(TotalFamilyFriend));
                Parser.SetField("TotalUrgentCare", String.valueOf(TotalUrgentCare));
                Parser.SetField("TotalNewspaperMagazine", String.valueOf(TotalNewspaperMagazine));

                Parser.SetField("TotalSchool", String.valueOf(TotalSchool));
                Parser.SetField("TotalHotel", String.valueOf(TotalHotel));

                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));

                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_Victoria.html");
            } else {

                long TotalReturnPatient = 0;
                long TotalGoogle = 0;
                long TotalMapSearch = 0;
                long TotalBillboard = 0;
                long TotalOnlineReview = 0;
                long TotalTV = 0;
                long TotalWebsite = 0;
                long TotalBuildingSignDriveBy = 0;
                long TotalFacebook = 0;
                long TotalSchool = 0;
                long TotalTwitter = 0;
                long TotalMagazine = 0;
                long TotalNewspaper = 0;
                long TotalFamilyFriend = 0;
                long TotalUrgentCare = 0;
                long TotalCommunityEvent = 0;


                Query = " Select SUM(ReturnPatient),SUM(Google),SUM(MapSearch),SUM(Billboard),SUM(OnlineReview),SUM(TV),SUM(Website),SUM(BuildingSignDriveBy)," +
                        " SUM(Facebook),SUM(School),SUM(Twitter),SUM(Magazine),SUM(Newspaper),SUM(FamilyFriend),SUM(UrgentCare),SUM(CommunityEvent) " +//16
                        " from " + Database + ".RandomCheckInfo where CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalReturnPatient += rset.getLong(1);
                    TotalGoogle += rset.getLong(2);
                    TotalMapSearch += rset.getLong(3);
                    TotalBillboard += rset.getLong(4);
                    TotalOnlineReview += rset.getLong(5);
                    TotalTV += rset.getLong(6);
                    TotalWebsite += rset.getLong(7);
                    TotalBuildingSignDriveBy += rset.getLong(8);
                    TotalFacebook += rset.getLong(9);
                    TotalSchool += rset.getLong(10);
                    TotalTwitter += rset.getLong(11);
                    TotalMagazine += rset.getLong(12);
                    TotalNewspaper += rset.getLong(13);
                    TotalFamilyFriend += rset.getLong(14);
                    TotalUrgentCare += rset.getLong(15);
                    TotalCommunityEvent += rset.getLong(16);
                }
                rset.close();
                stmt.close();


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));

                Parser.SetField("TotalReturnPatient", String.valueOf(TotalReturnPatient));
                Parser.SetField("TotalGoogle", String.valueOf(TotalGoogle));
                Parser.SetField("TotalMapSearch", String.valueOf(TotalMapSearch));
                Parser.SetField("TotalBillboard", String.valueOf(TotalBillboard));
                Parser.SetField("TotalOnlineReview", String.valueOf(TotalOnlineReview));
                Parser.SetField("TotalTV", String.valueOf(TotalTV));
                Parser.SetField("TotalWebsite", String.valueOf(TotalWebsite));
                Parser.SetField("TotalBuildingSignDriveBy", String.valueOf(TotalBuildingSignDriveBy));
                Parser.SetField("TotalFacebook", String.valueOf(TotalFacebook));
                Parser.SetField("TotalSchool", String.valueOf(TotalSchool));
                Parser.SetField("TotalTwitter", String.valueOf(TotalTwitter));
                Parser.SetField("TotalMagazine", String.valueOf(TotalMagazine));

                Parser.SetField("TotalNewspaper", String.valueOf(TotalNewspaper));
                Parser.SetField("TotalFamilyFriend", String.valueOf(TotalFamilyFriend));
                Parser.SetField("TotalUrgentCare", String.valueOf(TotalUrgentCare));
                Parser.SetField("TotalCommunityEvent", String.valueOf(TotalCommunityEvent));

                Parser.SetField("FromDate", String.valueOf(FromDate));
                Parser.SetField("ToDate", String.valueOf(ToDate));

                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_Others.html");
            }
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

    private void transactionDashboard(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        long ERInsured = 0;
        long ERSelfPay = 0;
        long MVA = 0;
        long KAVA = 0;
        long WorkersComp = 0;
        long MSE = 0;
        long LWBS = 0;
        long AMA = 0;
        long Eloped = 0;
        long Comped = 0;
        long CovidSelfPay = 0;

        try {
            if (ClientId == 27) {

                Query = "select ReasonLeaving,COUNT(*) from frontlin_er.Patient_AdditionalInfo where CreatedDate BETWEEN '" + FromDate + "' and '" + ToDate + "' and ReasonLeaving!='' and " +
                        " ReasonLeaving!='0' GROUP BY ReasonLeaving ORDER BY ReasonLeaving";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(1).equals("1")) {
                        ERInsured += rset.getLong(1);
                    } else if (rset.getString(1).equals("2")) {
                        ERSelfPay += rset.getLong(1);
                    } else if (rset.getString(1).equals("3")) {
                        MVA += rset.getLong(1);
                    } else if (rset.getString(1).equals("4")) {
                        KAVA += rset.getLong(1);
                    }
                }
                rset.close();
                stmt.close();
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
//                Parser.SetField("MSE", MSE);
//                Parser.SetField("AMA", AMA);
//                Parser.SetField("LWBS", LWBS);
//                Parser.SetField("Eloped", Eloped);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MarketingReportDashboard_frontline.html");
                return;
            }
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

}
