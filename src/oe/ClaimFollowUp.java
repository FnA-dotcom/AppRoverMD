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

@SuppressWarnings("Duplicates")
public class ClaimFollowUp extends HttpServlet {
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
                case "saveClaimFollowUp":
                    saveClaimFollowUp(request, out, conn, context, UserId);
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

    private void saveClaimFollowUp(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId) {
        stmt = null;
        rset = null;
        pStmt = null;
        Query = "";

        int mrn = Integer.parseInt(request.getParameter("mrn").trim());
        int FacilityIdx = Integer.parseInt(request.getParameter("ClientId").trim());
        int ClaimId = Integer.parseInt(request.getParameter("ClaimId").trim());
        String FollowupDate = request.getParameter("FollowupDate").trim();
        String FollowupRemarks = request.getParameter("FollowupRemarks").trim();
        try {
            if (mrn == 0 || FacilityIdx == 0 || ClaimId == 0) {
                String msg = mrn == 0 ? "MRN" : FacilityIdx == 0 ? "Facility Index " : "Claim Id";
                out.println("0|" + msg + " cannot be zero!");
                return;
            }
            if (FollowupDate.equals("") || FollowupDate.isEmpty() || FollowupRemarks.equals("") || FollowupRemarks.isEmpty()) {
                String msg = FollowupDate.equals("") ? "Followup Date " : "Remarks ";
                out.println("0|" + msg + " cannot be empty or null!");
                return;
            }

            pStmt = conn.prepareStatement(
                    "INSERT INTO oe.ClaimFollowups (FollowupRemarks, FollowupDateTime, FollowupEnterBy, " +
                            "Status, CreatedDate,CreatedBy,ClaimIdx,FacilityIdx,MRN) " +
                            "VALUES (?,?,?,0,NOW(),?,?,?,?)");
            pStmt.setString(1, FollowupRemarks);
            pStmt.setString(2, FollowupDate);
            pStmt.setString(3, userId);
            pStmt.setString(4, userId);
            pStmt.setInt(5, ClaimId);
            pStmt.setInt(6, FacilityIdx);
            pStmt.setInt(7, mrn);
            pStmt.executeUpdate();
            pStmt.close();

            out.println("1");
        } catch (Exception Ex) {
            out.println("0~Error while saving followup!!");
            String Message = Ex.getMessage() + " <br> ";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                Message = Message + Ex.getStackTrace()[i] + " ******* <br>";
            }
            out.println(Message);
        }
    }
}
