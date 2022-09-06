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
public class VictoriaRegistration extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;

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

            switch (ActionID) {
                case "Test":
                    Testing(request, out, conn, context);
                    //this.Victoria_2(request, out, conn, context);
                    break;
                case "ShowReport":
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

    void Testing(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Your Payment has been credited!!");
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
        } catch (Exception e) {
            out.println("Exception" + e.getMessage());
        }
    }
    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        try {
/*            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientCount = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                Query = "Select Bundle_FnName, Label_FnName from oe.clients where Id = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    BundleFnName = rset.getString(1);
                    LabelFnName = rset.getString(2);
                }
                rset.close();
                stmt.close();
                Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(DOB,'%m/%d/%Y'), " +
                        " IFNULL(PhNumber,''), IFNULL(MRN,0), IFNULL(ReasonVisit,'-'),  ID, IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T')," +
                        " DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')), " +
                        " CASE WHEN COVIDStatus = 1 THEN 'POSITIVE' WHEN COVIDStatus = 0 THEN 'NEGATIVE'  WHEN COVIDStatus = -1 THEN 'NONE' ELSE 'NONE' END " +
                        " FROM " + Database + ".PatientReg where Status = 0 ORDER BY ID DESC limit 300";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + rset.getInt(6) + ")\">Update</button></td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();
            }*/
/*            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);*/
            Parsehtm Parser = new Parsehtm(request);
/*            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));*/
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ErrorMaintenance.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

}
