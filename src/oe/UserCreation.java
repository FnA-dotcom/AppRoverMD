package oe;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class UserCreation extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String ActionID = null;

        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        ServletContext context = null;
        context = getServletContext();
        try {

            String UserId = Services.GetCookie("UserId", request);
            if (UserId == "") {
                out.println("<font size=\"3\" face=\"Calibri\">Your session has been expired, please login again.</font>");
                out.flush();
                out.close();
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            conn = Services.getMysqlConn(context);

            if (request.getParameter("ActionID") == null) {
                ActionID = "Home";
                return;
            }
            ActionID = request.getParameter("ActionID");

            if (ActionID.equals("GetInput"))
                InitialScreen(request, out, conn, context);
            else if (ActionID.equals("SaveUser"))
                SaveUser(request, out, conn, context);
            else {
                out.println("Under Development ... " + ActionID);
            }

            conn.close();
        } catch (Exception Ex) {
            conn = null;
            out.println("Main Function Error : " + Ex.getMessage());
        }
        out.close();
        out.flush();
    }

    private void InitialScreen(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/UserCreation.html");
        } catch (Exception e) {
            out.println("Exception Message : " + e.getMessage());
            out.flush();
            out.close();
        }
    }

    private void SaveUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        boolean found = false;

        int selectedOption = Integer.parseInt(request.getParameter("Flag").trim());
        String firstName = request.getParameter("firstName").trim();
        String lastName = request.getParameter("lastName").trim();
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();
        String UserId = request.getParameter("UserId").trim();
        String UserName = request.getParameter("UserName").trim();
        String Company = request.getParameter("Company").trim();
        int Access = Integer.parseInt(request.getParameter("Access").trim());
        int gender = Integer.parseInt(request.getParameter("gender").trim());
        int isAdminCheck = Integer.parseInt(String.valueOf(request.getParameter("isadmincheck") == null ? 0 : 1));

        try {

            Query = " select count(*) from sysusers where ltrim(rtrim(upper(UserId)))='" + UserId + "'" + " and status=0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                found = rset.getInt(1) > 0;
            rset.close();
            stmt.close();

        } catch (Exception e) {
            out.println("Exception : " + e.getMessage());
            try {
                Parsehtm Parser;
                Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exceptions/Exception6.html");
            } catch (Exception var25) {
            }
            out.flush();
            out.close();
            return;
        }

        if (found) {
            out.println("Exception :  Already Registered !! ");
            out.flush();
            out.close();
            return;
        }

        try {
            PreparedStatement MainReceipt = conn.prepareStatement("Insert into sysusers (UserID, Password,Status,create_date," +
                    "Gender,Email,firstname,lastname,usertype,username,companyname,enabled,Access) " +
                    "values (?,?,0,NOW(),?,?,?,?,?,?,?,'Y',?) ");
            MainReceipt.setString(1, UserId);
            MainReceipt.setString(2, password);
            MainReceipt.setInt(3, gender);
            MainReceipt.setString(4, email);
            MainReceipt.setString(5, firstName);
            MainReceipt.setString(6, lastName);
            MainReceipt.setInt(7, selectedOption);
            MainReceipt.setString(8, UserName);
            MainReceipt.setString(9, Company);
            MainReceipt.setInt(10, Access);

            MainReceipt.executeUpdate();
            MainReceipt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "User has been created!!");
            Parser.SetField("FormName", "UserCreation");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
        } catch (Exception var27) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exceptions/Exception6.html");
            } catch (Exception var26) {
            }
            out.flush();
            out.close();
            return;
        }
    }
}
