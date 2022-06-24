package oe;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static oe.PatientChart.Clientlist;

@SuppressWarnings("Duplicates")
public class FollowUpReport extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    private void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action = "";
        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId = "";
        int FacilityIndex;
        String DatabaseName;
        final Services supp = new Services();

        try {
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                return;
            }
            Cookie[] cookies = request.getCookies();
            int checkCookie = 0;
            for (Cookie cooky : cookies) {
                String cName = cooky.getName();
                String cValue = cooky.getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }
            Action = request.getParameter("ActionID").trim();
            switch (Action) {
                case "FollowupInputReport":
                    FollowupInputReport(request, out, conn, context, UserId);
                    break;
                case "showFollowupReport":
                    showReportFollowup(request, out, conn, context, UserId);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception Ex) {
            out.println("Exception in main..");
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

    private void FollowupInputReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId) {
        try {
            String UserId = request.getParameter("User").trim();
            StringBuffer ClientList = new StringBuffer();
            ClientList.append("<option class=Inner value=\"-1\"> All </option>");
            HashMap<Integer, String> hm = new HashMap<>();
            hm = Clientlist("a", conn);
            Set<Map.Entry<Integer, String>> set = hm.entrySet();
            for (Map.Entry<Integer, String> entry : set) {
                ClientList.append("<option class=Inner value=\"" + entry.getKey() + "\">" + entry.getValue().toString() + "</option>");
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);
            Parser.SetField("ClientList", ClientList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/getFollowupInput.html");

        } catch (Exception Ex) {
            out.println("0~Error while fetching report");
            String Message = Ex.getMessage() + " <br> ";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                Message = Message + Ex.getStackTrace()[i] + " ******* <br>";
            }
            out.println(Message);
        }
    }

    private void showReportFollowup(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId) {
        stmt = null;
        rset = null;
        pStmt = null;
        Query = "";
        int SNo = 1;
        StringBuilder ClaimFollowup = new StringBuilder();
        try {
            String FDate = request.getParameter("FDate").trim();
            int Facility = Integer.parseInt(request.getParameter("ClientList").trim());
            String UserId = request.getParameter("UserId").trim();

            String condition = "";
            if (Facility < 0)
                condition = "";
            else
                condition = " AND a.FacilityIdx = " + Facility;
            String DToDate = FDate.substring(0, 19);
            String DFromDate = FDate.substring(22, 41);
            Query = "SELECT a.FollowupRemarks,DATE_FORMAT(a.FollowupDateTime,'%d-%b-%Y'),a.FollowupEnterBy, \n" +
                    "a.MRN,b.`name`, a.ClaimIdx,c.firstname,c.lastname,c.dosdate,c.id\n" +
                    "FROM oe.ClaimFollowups a \n" +
                    "INNER JOIN oe.clients b ON a.FacilityIdx = b.Id " +
                    "INNER JOIN filelogs_sftp c ON a.ClaimIdx = c.Id " +
                    "WHERE a.FollowupDateTime BETWEEN '" + DToDate + "' AND '" + DFromDate + "' AND " +
                    "UPPER(a.CreatedBy) = UPPER('" + UserId + "') " + condition + " ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClaimFollowup.append("<tr><td align=left>" + SNo + "</td>\n");
                ClaimFollowup.append("<td align=left>" + rset.getString(7) + "</td>\n");//firstname
                ClaimFollowup.append("<td align=left>" + rset.getString(8) + "</td>\n");//lastname
                ClaimFollowup.append("<td align=left>" + rset.getString(9) + "</td>\n");//dosdate
                ClaimFollowup.append("<td align=left>" + rset.getInt(4) + "</td>\n");//MRN
                ClaimFollowup.append("<td align=left>" + rset.getString(2) + "</td>\n");//FollowupDateTime
                ClaimFollowup.append("<td align=left>" + rset.getString(1) + "</td>\n");//FollowupRemarks
                ClaimFollowup.append("<td align=left>" + rset.getString(3) + "</td>\n");//FollowupEnterBy
                ClaimFollowup.append("<td align=left>" + rset.getString(5) + "</td>\n");//Facility
                ClaimFollowup.append("<td><a class=\"btn btn-sm btn-primary\" href=/oe/oe.PatientChart?Action=Addinfo&indexptr=" + rset.getString(10) + "&mrn=" + rset.getString(4) + " target='_blank'>Details</a></td>");
                ClaimFollowup.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClaimFollowup", ClaimFollowup.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/FollowUpReport.html");

        } catch (Exception Ex) {
            out.println("0~Error while fetching report");
            String Message = Ex.getMessage() + " <br> ";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                Message = Message + Ex.getStackTrace()[i] + " ******* <br>";
            }
            out.println(Message);
        }
    }
}
