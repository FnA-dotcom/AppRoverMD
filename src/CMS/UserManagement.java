package CMS;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import md.FacilityLogin;
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
public class UserManagement extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;
    private ResultSet rset1;

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
//        Payments payments = new Payments();
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
                case "CreateUser":
                    CreateUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "EditUser":
                    EditUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UpdateUser":
                    UpdateUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DeactivateUser":
                    DeactivateUser(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
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
            StringBuffer CDRList = new StringBuffer();
            StringBuffer MDRList = new StringBuffer();
            StringBuffer NDRList = new StringBuffer();

            MDRList.append("<option value=''    disabled selected >Select Company</option>");
            Query = "SELECT Id, name FROM oe.clients where status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                MDRList.append("<option value=" + rset.getString(1) + " >" + rset.getString(2) + "</option>");
                NDRList.append("'" + rset.getString(2) + "' , ");
            }
            rset.close();
            stmt.close();


            int SNo = 1;
            Query = "SELECT CONCAT(firstname,' ',lastname),userid, companyname,indexptr FROM CMS.sysusers where status = 0 " +
                    "ORDER BY create_date DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left><span><button class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick='EditUser(" + rset.getString(4) + ")' >Edit</button></span><span style='margin-left:5px'><button class=\"waves-effect waves-light btn btn-outline btn-rounded btn-danger mb-5\" onclick='DeactivateUser(" + rset.getString(4) + ")' >Deactivate</button></span></td></tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("MDRList", String.valueOf(MDRList));
            Parser.SetField("NDRList", String.valueOf(NDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "CMS/CreateUser.html");
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

    void CreateUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        try {
            String firstname = request.getParameter("firstname");
            String lastname = request.getParameter("lastname");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String pwd = request.getParameter("pwd");
            String Company = "";
            String clientId = "";
            String user_type = request.getParameter("user_type");
            String userId = request.getParameter("userId");

            String passwordEnc = FacilityLogin.encrypt(pwd);


            Company = request.getParameter("Company");
            clientId = request.getParameter("clientId");
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO CMS.sysusers (userid,firstname,lastname,password,username,companyname,usertype,create_date,enabled,email,clientid,status) VALUE(?,?,?,?,?,?,?,now(),'Y',?,?,0)");

            MainReceipt.setString(1, userId);
            MainReceipt.setString(2, firstname);
            MainReceipt.setString(3, lastname);
            MainReceipt.setString(4, passwordEnc);
            MainReceipt.setString(5, username);
            MainReceipt.setString(6, Company);
            MainReceipt.setString(7, user_type);
            MainReceipt.setString(8, email);
            MainReceipt.setString(9, clientId);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            out.println("1");
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

    void EditUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        try {
            StringBuffer CDRList = new StringBuffer();
            String idxptr = request.getParameter("str");

            Query = "SELECT firstname,lastname,userid,email,usertype,password,clientid,indexptr FROM CMS.sysusers where indexptr = " + idxptr + " and status = 0 ORDER BY create_date DESC";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);


            while (rset.next()) {
                String passdec = FacilityLogin.decrypt(rset.getString(6));
                CDRList.append(rset.getString(1) + "~" + rset.getString(2) + "~" + rset.getString(3) + "~" +
                        "" + rset.getString(4) + "~" + rset.getString(5) + "~" + passdec + "~" + rset.getString(7) + "~" + rset.getString(8));
            }
            rset.close();
            stmt.close();

            out.println(CDRList);
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

    void UpdateUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            String firstname = request.getParameter("firstname");
            String lastname = request.getParameter("lastname");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String pwd = request.getParameter("pwd");
            String Company = "";
            String clientId = "";
            String user_type = request.getParameter("user_type");
            String userId = request.getParameter("userId");
            String idxptr = request.getParameter("idxptr");


            String passwordEnc = FacilityLogin.encrypt(pwd);

//            if (user_type.equals("4")) {
            Company = request.getParameter("Company");
            clientId = request.getParameter("clientId");
            Query = "UPDATE CMS.sysusers SET userid='" + userId + "',firstname='" + firstname + "',lastname='" + lastname + "'," +
                    "password='" + passwordEnc + "',username='" + username + "',companyname='" + Company + "',usertype='" + user_type + "'," +
                    "create_date=now(),email='" + email + "',clientid='" + clientId + "' WHERE indexptr='" + idxptr + "'";

            stmt = conn.createStatement();
            stmt.executeUpdate(Query);

            stmt.close();
//            } else {
//                Company = request.getParameter("Company");
//                clientId = request.getParameter("clientId");
//                Query = "UPDATE CMS.sysusers SET userid='" + userId + "',firstname='" + firstname + "',lastname='" + lastname + "'," +
//                        "password='" + passwordEnc + "',username='" + username + "',companyname='" + Company + "',usertype='" + user_type + "'," +
//                        "create_date=now(),email='" + email + "' WHERE indexptr='" + idxptr + "'";
//
//                stmt = conn.createStatement();
//                stmt.executeUpdate(Query);
//
//                stmt.close();
//            }
            out.println("1");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            System.out.println(str);
            out.flush();
            out.close();
        }
    }

    void DeactivateUser(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            String idxptr = request.getParameter("idxptr");
            Query = "UPDATE CMS.sysusers SET status='1' WHERE indexptr='" + idxptr + "'";

            stmt = conn.createStatement();
            stmt.executeUpdate(Query);

            stmt.close();
            out.println("1");
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

}
