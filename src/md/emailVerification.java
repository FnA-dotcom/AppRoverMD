package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
@WebServlet(name = "emailVerification", urlPatterns = {"/emailVerification/*"})
public class emailVerification extends HttpServlet {
    public static String decode(String url) {
        try {
            String prevURL = "";
            String decodeURL = url;
            while (!prevURL.equals(decodeURL)) {
                prevURL = decodeURL;
                decodeURL = URLDecoder.decode(decodeURL, "UTF-8");
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while decoding" + e.getMessage();
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out = new PrintWriter(response.getOutputStream());
        String MRN = "";
        int clientIdx = 0;
        if (request.getRequestURI().contains("emailVerification")) {
            MRN = decode(request.getRequestURI().substring("/md/emailVerification/".length()));
            try {
                updatePatientReg(request, response, out, MRN, clientIdx);
            } catch (SQLException e) {
                System.out.println("EXCEPTION " + e.getMessage());
            }
            out.flush();
            out.close();
        }
    }

    private void updatePatientReg(HttpServletRequest request, HttpServletResponse response,
                                  PrintWriter out, String mrn, int clientIdx) throws SQLException {

        Connection conn = null;
        String isVerifiedCheck = "";
        ResultSet rset = null;
        String Query = "";
        Statement stmt = null;
        ServletContext context = getServletContext();
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        String _mrn = "";
        int _facilityIdx = 0;
        try {
            conn = Services.GetConnection(context, 1);
            String[] splitMrn = mrn.split(":");
            _mrn = splitMrn[0];
            _facilityIdx = Integer.parseInt(splitMrn[1]);

            String DatabaseName = "";
            Query = "SELECT dbname FROM oe.clients WHERE Id = " + _facilityIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DatabaseName = rset.getString(1).trim();
            }
            rset.close();
            stmt.close();

            Query = "select isVerified from " + DatabaseName + ".PatientReg where MRN='" + _mrn + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                isVerifiedCheck = rset.getString(1);
            }
            if (isVerifiedCheck.equals("0")) {
                Query = "update " + DatabaseName + ".PatientReg set isVerified=1 where MRN='" + _mrn + "'";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("EmailVerified", "Verified");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/EmailVerified.html");
            } else if (isVerifiedCheck.equals("1")) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("EmailVerified", "Already Verified");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Forms/EmailVerified.html");

            }

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in emailVerification ** (handleRequest)", context, e, "emailVerification", "handleRequest", conn);
            Services.DumException("email Verfication", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            out.flush();
            out.close();
        } finally {
            out.flush();
            out.close();
            conn.close();
        }
    }

}
