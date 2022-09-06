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
import java.sql.*;

public class ChangeFacilityInfo extends HttpServlet {

    private Connection conn = null;
    Integer ScreenIndex = 31;


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
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();
        int UserIndex=0;
        try {
            HttpSession session = request.getSession(false);


            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);

            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }

            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            }else if (ActionID.equals("SaveChangeFacilityInfo")) {
                SaveChangeFacilityInfo(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                out.println("Under Development");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;

        String FacilityName = null;
        String Address = null;
        String NPI = null;
        String Phone = null;
        String Id = null;

        try {

            Query = "Select Id, FullName, Address, NPI, Phone from oe.clients " +
                    "where Id="+ClientId ;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()){
                Id = rset.getString(1);
                FacilityName = rset.getString(2);
                Address = rset.getString(3);
                NPI = rset.getString(4);
                Phone = rset.getString(5);
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Id", String.valueOf(Id));
            Parser.SetField("FacilityName", String.valueOf(FacilityName));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("NPI", String.valueOf(NPI));
            Parser.SetField("Phone", String.valueOf(Phone));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangeFacilityInfo.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
    void SaveChangeFacilityInfo(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String FacilityName = request.getParameter("FacilityName").trim();
            String Address = request.getParameter("Address").trim();
            String Phone = request.getParameter("Phone").trim();
            String NPI = request.getParameter("NPI").trim();



            PreparedStatement MainReceipt = conn.prepareStatement("UPDATE  oe.clients  SET NPI = '"+ NPI +"', FullName = '"+FacilityName+"' , Address='"+Address+"' , Phone='"+Phone+"' WHERE Id="+ClientId);
            MainReceipt.executeUpdate();
            MainReceipt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("success", "1");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangeFacilityInfo.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}