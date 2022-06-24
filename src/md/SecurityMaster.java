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

public class SecurityMaster
        extends HttpServlet {
    String outbound = "";
    String inbound = "";
    String ob_calling = "";
    private Connection conn = null;

    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
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
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Security Master Get Input", "Get Input Screen", FacilityIndex);
                GetInput(request, response, out, conn, UserId, FacilityIndex, DatabaseName);
            } else if (ActionID.compareTo("SaveData") == 0) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Security Master Save", "Save Data", FacilityIndex);
                SaveData(request, response, out, conn, UserId, FacilityIndex, DatabaseName);
            } else {
                out.println("Under Development ... " + ActionID);
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

    public void GetInput(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String UserId, int ClientId, String Database) {
        Statement hstmt = null;
        ResultSet hrset = null;
        Statement hstmt1 = null;
        ResultSet hrset1 = null;
        String exp_period = "";
        String wrong_pass = "";
        String pass_history = "";
        String enforced_pass = "";
        String app_ppolicy = "";
        String Query = "";
        String exp_periodv = "";
        String enforced_passv = "";
        String app_ppolicyv = "";
        try {
            hstmt = conn.createStatement();
            Query = "select exp_period,wrong_pass,pass_history,enforced_pass,app_ppolicy from oe.SecurityMaster order by entrydate desc limit 1";
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                exp_period = hrset.getString(1);
                wrong_pass = hrset.getString(2);
                pass_history = hrset.getString(3);
                enforced_pass = hrset.getString(4);
                app_ppolicy = hrset.getString(5);
            }
            hrset.close();
            if (app_ppolicy.compareTo("1") == 0) {
                app_ppolicyv = "checked";
            }
            if (enforced_pass.compareTo("1") == 0) {
                enforced_passv = "checked";
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);
            Parser.SetField("exp_period", exp_period);
            Parser.SetField("app_ppolicyv", app_ppolicyv);
            Parser.SetField("wrong_pass", wrong_pass);
            Parser.SetField("pass_history", pass_history);
            Parser.SetField("enforced_pass", enforced_pass);
            Parser.SetField("enforced_passv", enforced_passv);
            Parser.SetField("app_ppolicy", app_ppolicy);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/SecurityMaster.html");
        } catch (Exception e) {
            out.println("Unable to process request ... " + e.getMessage());
        }
    }

    public void SaveData(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String UserId, int ClientId, String Database) {
        String exp_period = request.getParameter("exp_period").trim();
        String wrong_pass = request.getParameter("wrong_pass").trim();
        int pass_history = Integer.parseInt(request.getParameter("pass_history").trim());
        String app_ppolicy = "", enforced_pass = "";
        if (request.getParameter("app_ppolicy") == null) {
            app_ppolicy = "0";
        } else {
            app_ppolicy = request.getParameter("app_ppolicy");
        }

        if (request.getParameter("enforced_pass") == null) {
            enforced_pass = "0";
        } else {
            enforced_pass = request.getParameter("enforced_pass");
        }

        try {
            PreparedStatement MainReceipt = conn.prepareStatement(
                    "Insert into oe.SecurityMaster (app_ppolicy,exp_period,wrong_pass,pass_history,enforced_pass,entrydate) values (?,?,?,?,?,now()) ");
            MainReceipt.setString(1, app_ppolicy);
            MainReceipt.setString(2, exp_period);
            MainReceipt.setString(3, wrong_pass);
            MainReceipt.setInt(4, pass_history);
            MainReceipt.setString(5, enforced_pass);

            MainReceipt.executeUpdate();
            MainReceipt.close();
            out.println("<table><tr><td class=\"fieldm\">");
            out.println("<b> Security setting Sucessfully Saved........</b>");
            out.println("</td></tr></table>");
        } catch (Exception e) {
            out.println("<br>Error No.: 0007");
            out.println("<br>Error Is : Could not save Support Staff Record ...!!! \n\n\n</b>");
            out.println("<input class=\"buttonERP\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
            out.println("</form></body></html>");
            out.flush();
            out.close();
            return;
        }
    }

    private void Dologing(String Status, String UserId, Connection conn, String UserIP, String Option) {
        Statement hstmt1 = null;
        ResultSet hrset1 = null;
        String Query = "";

        try {

            Query = " insert into  oe.loginsyslogs  " +
                    " (userid, entrydate, ipaddress, sourcetype, sourcedesc,status) " +
                    " values ('" + UserId + "',now(),'" + UserIP + "',1,'" + Option + "','" + Status + "')";
            hstmt1 = conn.createStatement();
            hstmt1.executeUpdate(Query);
            hstmt1.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}