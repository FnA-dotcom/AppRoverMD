package DAL;

import md.Services;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class masterDataAPI extends HttpServlet {
    CallableStatement cStmt = null;
    ResultSet rset = null;
    String Query = "";
    Statement stmt = null;
    PreparedStatement pStmt = null;
    ServletContext context = null;
    private Connection conn = null;

    /**
     * Initialize global variables
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Process the HTTP Get request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //For Printing
        PrintWriter out = new PrintWriter(response.getOutputStream());
        //For Printing JSON Objects
        ServletOutputStream SOS = response.getOutputStream();
        try {
            conn = Services.GetConnection(getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                out.flush();
                out.close();
                return;
            }
        } catch (Exception excp) {
            conn = null;
            out.println("Exception excp conn: " + excp.getMessage());
            out.flush();
            out.close();
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            context = getServletContext();
            String RequestName = request.getParameter("RequestName").trim();
            switch (RequestName) {
                case "getTitles":
                    getTitles(request, response, conn, context);
                    break;
                case "getMaritalStatus":
                    getMaritalStatus(request, response, conn, context);
                    break;
                case "getDoctorList":
                    getDoctorList(request, response, conn, context);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception Ex) {
            out.println("Error in Main " + Ex.getMessage());
        }
        out.close();
        out.flush();
    }

    /**
     * Process the HTTP Post request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        out.println("<html>");
        out.println("<head><title>UtilityHelper</title></head>");
        out.println("<body>");
        out.println("</body></html>");
        out.close();
    }

    private void getTitles(HttpServletRequest request, HttpServletResponse response, Connection conn, ServletContext servletContext) throws IOException {
        stmt = null;
        rset = null;
        Query = "";
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        try {
            JSONArray jsonArray = new JSONArray();

            Query = "SELECT Id,Title FROM oe_2.Title WHERE status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("Id", rset.getInt(1));
                jsonObj.put("TitleName", rset.getString(2));
                jsonArray.add(jsonObj);
            }
            rset.close();
            stmt.close();

            out.print(jsonArray.toJSONString());

        } catch (Exception Ex) {
            Services.MobileExceptionDumps("masterDataAPI", "getTitles -- SP -- 001 ", request, Ex, servletContext);
        }
    }

    private void getMaritalStatus(HttpServletRequest request, HttpServletResponse response, Connection conn, ServletContext servletContext) throws IOException {
        stmt = null;
        rset = null;
        Query = "";
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        try {
            JSONArray jsonArray = new JSONArray();

            Query = "SELECT Id,MaritalStatus FROM oe_2.MaritalStatus WHERE status = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("Id", rset.getInt(1));
                jsonObj.put("MaritalStatus", rset.getString(2));
                jsonArray.add(jsonObj);
            }
            rset.close();
            stmt.close();

            out.print(jsonArray.toJSONString());

        } catch (Exception Ex) {
            Services.MobileExceptionDumps("masterDataAPI", "getMaritalStatus -- SP -- 002 ", request, Ex, servletContext);
        }
    }

    private void getDoctorList(HttpServletRequest request, HttpServletResponse response, Connection conn, ServletContext servletContext) throws IOException {
        stmt = null;
        rset = null;
        Query = "";
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        try {
            JSONArray jsonArray = new JSONArray();

            Query = "Select Id, CONCAT(IFNULL(DoctorsLastName,''),', ',IFNULL(DoctorsFirstName,'')) from oe_2.DoctorsList where Status = 1 order by DoctorsLastName";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("Id", rset.getInt(1));
                jsonObj.put("DocName", rset.getString(2));
                jsonArray.add(jsonObj);
            }
            rset.close();
            stmt.close();

            out.print(jsonArray.toJSONString());

        } catch (Exception Ex) {
            Services.MobileExceptionDumps("masterDataAPI", "getDoctorList -- SP -- 003 ", request, Ex, servletContext);
        }
    }
}
