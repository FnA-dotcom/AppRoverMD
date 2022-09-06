package UnityLab;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import md.Services;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class Providers extends HttpServlet {
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
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Connection conn = null;
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
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);

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
                case "GetInput":
                    GetInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "InsertProvider":
                    InsertProvider(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;

//                case "EditUser":
//                    EditUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
//                    break;
//                case "UpdateUser":
//                    UpdateUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
//                    break;
//                case "DeactivateUser":
//                    DeactivateUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
//                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception e) {
//            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest)", context, e, "RegisteredPatients", "handleRequest", conn);
            Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
//                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest -- SqlException)", context, e, "RegisteredPatients", "handleRequest", conn);
                Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        try {

            Statement stmt = null;
            ResultSet rset = null;
            String Query = null;
            StringBuffer Providers = new StringBuffer();


            Query = "SELECT id ,FirstName ,LastName ,NPI ,Department ,Category ,Abbreviation ,Mobile ,Email\n" +
                    "FROM UnityLab.Providers " +
                    "ORDER BY CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Providers.append(" <tr data-toggle=\"modal\" data-target=\"#exampleModal\">\n" +
                        "<td class=\"eli\"><span data-toggle=\"tooltip\" title=\"" + rset.getString(1) + "\" >" + rset.getString(1) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(2) + "\" >" + rset.getString(2) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(3) + "\">" + rset.getString(3) + "</span></td>\n" +
                        "<td class=\"eli\"  style=\"width:1% !important\"><span data-toggle=\"tooltip\" title=\"" + rset.getString(4) + "\">" + rset.getString(4) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(5) + "\">" + rset.getString(5) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(6) + "\">" + rset.getString(6) + "</span></td>\n" +
                        "<td class=\"eli\" data-toggle=\"tooltip\" title=\"" + rset.getString(7) + "\">" + rset.getString(7) + "</td>\n" +
                        "<td class=\"eli\" style=\"width:4% !important\" ><span  data-toggle=\"tooltip\" title=\"" + rset.getString(8) + "\" >" + rset.getString(8) + "</span></td>\n" +
                        "<td class=\"eli\" style=\"width:4% !important\" ><span  data-toggle=\"tooltip\" title=\"" + rset.getString(9) + "\" >" + rset.getString(9) + "</span></td>\n" +
                        "</tr>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Providers", String.valueOf(Providers));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "UnityLab/Providers.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }


    void InsertProvider(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {

        String FirstName = request.getParameter("FirstName").trim();
        String LastName = request.getParameter("LastName").trim();
        String NPI = request.getParameter("NPI").trim();
        String Department = request.getParameter("Department").trim();
        String Category = request.getParameter("Category").trim();
        String Abbreviation = request.getParameter("Abbreviation").trim();
        String Mobile = request.getParameter("Mobile").trim();
        String Email = request.getParameter("Email").trim();

        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuffer Providers = new StringBuffer();


        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO UnityLab.Providers " +
                            "( FirstName ,LastName ,NPI ,Department ,Category ,Abbreviation ,Mobile ,Email ,CreatedDate ,CreatedBy ,Status )" +
                            " VALUE(?,?,?,?,?,?,?,?, now(),?,0)");
            MainReceipt.setString(1, FirstName);
            MainReceipt.setString(2, LastName);
            MainReceipt.setString(3, NPI);
            MainReceipt.setString(4, Department);
            MainReceipt.setString(5, Category);
            MainReceipt.setString(6, Abbreviation);
            MainReceipt.setString(7, Mobile);
            MainReceipt.setString(8, Email);
            MainReceipt.setString(9, UserId);
            MainReceipt.executeUpdate();
            MainReceipt.close();


            Query = "SELECT id ,FirstName ,LastName ,NPI ,Department ,Category ,Abbreviation ,Mobile ,Email\n" +
                    "FROM UnityLab.Providers " +
                    "ORDER BY CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Providers.append(" <tr data-toggle=\"modal\" data-target=\"#exampleModal\">\n" +
                        "<td class=\"eli\"><span data-toggle=\"tooltip\" title=\"" + rset.getString(1) + "\" >" + rset.getString(1) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(2) + "\" >" + rset.getString(2) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(3) + "\">" + rset.getString(3) + "</span></td>\n" +
                        "<td class=\"eli\"  style=\"width:1% !important\"><span data-toggle=\"tooltip\" title=\"" + rset.getString(4) + "\">" + rset.getString(4) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(5) + "\">" + rset.getString(5) + "</span></td>\n" +
                        "<td class=\"eli\" ><span data-toggle=\"tooltip\" title=\"" + rset.getString(6) + "\">" + rset.getString(6) + "</span></td>\n" +
                        "<td class=\"eli\" data-toggle=\"tooltip\" title=\"" + rset.getString(7) + "\">" + rset.getString(7) + "</td>\n" +
                        "<td class=\"eli\" style=\"width:4% !important\" ><span  data-toggle=\"tooltip\" title=\"" + rset.getString(8) + "\" >" + rset.getString(8) + "</span></td>\n" +
                        "<td class=\"eli\" style=\"width:4% !important\" ><span  data-toggle=\"tooltip\" title=\"" + rset.getString(9) + "\" >" + rset.getString(9) + "</span></td>\n" +
                        "</tr>");
            }
            rset.close();
            stmt.close();


            out.println("1~" + Providers);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}