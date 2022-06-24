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
public class ERA extends HttpServlet {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";

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
        String UserIndex = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();

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
                UserIndex = session.getAttribute("UserIndex").toString();
                DirectoryName = session.getAttribute("DirectoryName").toString();

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
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.compareTo("GetInput") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "ERA Screen", "Get Input Screen", FacilityIndex);
                GetInput(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else {
                out.println("Under Development");
            }
        } catch (Exception e) {
            System.out.println("Exception in main... " + e.getMessage());
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


    public void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        stmt = null;
        rset = null;
        Query = "";
        StringBuilder ERARecDate = new StringBuilder();
        try {

            Query = "Select Id, IFNULL(ERAReceivedDate,'') from oe.ERAReceivedDate ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ERARecDate.append("<option value='" + rset.getInt(1) + "' > " + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ERARecDate", ERARecDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/ERA.html");
        } catch (Exception e) {
            out.println(e.getMessage());
        }

    }


    private String CheckStringVariable(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName).length() < 1) {
                VariableName = "";
            } else {
                VariableName = request.getParameter(VariableName).trim();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    private String CheckIntegerVariable(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName).length() < 1) {
                VariableName = "0";
            } else {
                VariableName = request.getParameter(String.valueOf(VariableName)).trim();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    private String CheckCheckBoxValue(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName) == null) {
                VariableName = "0";
            } else {
                VariableName = request.getParameter(VariableName).trim();
                if (VariableName.equals("on")) {
                    VariableName = "1";
                } else {
                    VariableName = "0";
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    private String CheckBox(String VariableName) {
        String ChkBox = "";
        try {
            if (VariableName.equals("1")) {
                ChkBox = "<input type=\"checkbox\" id=\"" + VariableName + "\" name=\"" + VariableName + "\" class=\"filled-in\" checked />";
            } else {
                ChkBox = "<input type=\"checkbox\" id=\"" + VariableName + "\" name=\"" + VariableName + "\" class=\"filled-in\" />";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return ChkBox;
    }


}