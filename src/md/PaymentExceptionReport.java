//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("Duplicates")
public class PaymentExceptionReport extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

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
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String FontColor;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        int UserIndex = 0;
        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

        try {

            UtilityHelper helper = new UtilityHelper();


            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);


//            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
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
            if (ActionID.equals("GetInput")) {
//                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.GetInput(request, out, conn, context);
            } else {

                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
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


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        StringBuffer BoltExceptions = new StringBuffer();
        StringBuffer CardExceptions = new StringBuffer();
        StringBuffer TransactionExceptions = new StringBuffer();
        String respTxt = "";


//        String TODAY = "";
//        String Yesterday = "";
//        String WEEK_START = "";
//        String WEEK_END = "";
//
//        String MONTH_START = "";
//        String MONTH_END = "";
//        String LAST_MONTH_START = "";
//        String LAST_MONTH_END = "";


        String FacilityName = "";
        String[] LiveFacilities = {"victoria", "oe_2", "nacogdoches", "longview", "oddasa", "ER_Dallas", "frontlin_er", "richmond"};


//        try {
//
//            /*----------------------------------------------------------------------------------------------
//             *                             Getting Time Variables
//             * ---------------------------------------------------------------------------------------------
//             */
//            Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d'), " +
//                    "DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY), " +
//                    "DATE_ADD(DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
//                    "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
//                    "LAST_DAY(NOW())," +
//                    "DATE_SUB(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
//                    "LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH )," +
//                    " Date_format(NOW(),'%d')," +
//                    "DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS yesterday_date";
//
//
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                TODAY = rset.getString(1);
//                WEEK_START = rset.getString(2);
//                WEEK_END = rset.getString(3);
//                MONTH_START = rset.getString(4);
//                MONTH_END = rset.getString(5);
//                LAST_MONTH_START = rset.getString(6);
//                LAST_MONTH_END = rset.getString(7);
//                presentDayNumber = rset.getInt(8);
//                Yesterday = rset.getString(9);
//            }
//            rset.close();
//            stmt.close();
//


        for (int i = 0; i < LiveFacilities.length; i++) {
            //Getting Name of Facilities
            try {
                Query = "Select Name from oe.clients where dbname='" + LiveFacilities[i] + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FacilityName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Query = "Select SessionResponse,CreatedDate,UserId from " + LiveFacilities[i] + ".BoltDeviceConnectionFailures ORDER BY CreatedDate DESC";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    BoltExceptions.append("<tr>\n");
                    BoltExceptions.append("<td style=\"border: 1px solid black;\" >" + FacilityName + "</td>\n");
                    BoltExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                    BoltExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    BoltExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(3) + "</td>\n");
                    BoltExceptions.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                Query = "Select ResponseText,CreatedDate from " + LiveFacilities[i] + ".CardConnectResponses WHERE ResponseType='ERROR' ORDER BY CreatedDate DESC";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CardExceptions.append("<tr>\n");
                    CardExceptions.append("<td style=\"border: 1px solid black;\" >" + FacilityName + "</td>\n");
                    CardExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                    CardExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    CardExceptions.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                Query = "Select JSON_Response,CreatedDate,CreatedBy from " + LiveFacilities[i] + ".JSON_Response WHERE ResponseType='ERROR' ORDER BY CreatedDate DESC";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    JSONObject obj = new JSONObject(rset.getString(1));
                    respTxt = obj.getString("resptext");
                    TransactionExceptions.append("<tr>\n");
                    TransactionExceptions.append("<td style=\"border: 1px solid black;\" >" + FacilityName + "</td>\n");
                    TransactionExceptions.append("<td style=\"border: 1px solid black;\" >" + respTxt + "</td>\n");
                    TransactionExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    TransactionExceptions.append("<td style=\"border: 1px solid black;\" >" + rset.getString(3) + "</td>\n");
                    TransactionExceptions.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//
//
//


        try {
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("BoltExceptions", String.valueOf(BoltExceptions));
            Parser.SetField("CardExceptions", String.valueOf(CardExceptions));
            Parser.SetField("TransactionExceptions", String.valueOf(TransactionExceptions));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/ExceptionReport.html");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//
    }

    void GetInput_Accordion(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        StringBuffer REPORT_HTML = new StringBuffer();
        String respTxt = "";


//        String TODAY = "";
//        String Yesterday = "";
//        String WEEK_START = "";
//        String WEEK_END = "";
//
//        String MONTH_START = "";
//        String MONTH_END = "";
//        String LAST_MONTH_START = "";
//        String LAST_MONTH_END = "";


        String FacilityName = "";
        String[] LiveFacilities = {"victoria", "oe_2", "nacogdoches", "longview", "oddasa", "ER_Dallas", "frontlin_er", "richmond"};


//        try {
//
//            /*----------------------------------------------------------------------------------------------
//             *                             Getting Time Variables
//             * ---------------------------------------------------------------------------------------------
//             */
//            Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d'), " +
//                    "DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY), " +
//                    "DATE_ADD(DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
//                    "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
//                    "LAST_DAY(NOW())," +
//                    "DATE_SUB(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
//                    "LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH )," +
//                    " Date_format(NOW(),'%d')," +
//                    "DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS yesterday_date";
//
//
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                TODAY = rset.getString(1);
//                WEEK_START = rset.getString(2);
//                WEEK_END = rset.getString(3);
//                MONTH_START = rset.getString(4);
//                MONTH_END = rset.getString(5);
//                LAST_MONTH_START = rset.getString(6);
//                LAST_MONTH_END = rset.getString(7);
//                presentDayNumber = rset.getInt(8);
//                Yesterday = rset.getString(9);
//            }
//            rset.close();
//            stmt.close();
//


        for (int i = 0; i < LiveFacilities.length; i++) {
            //Getting Name of Facilities
            try {
                Query = "Select Name from oe.clients where dbname='" + LiveFacilities[i] + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FacilityName = rset.getString(1);
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            REPORT_HTML.append("<div class=\"card\">\n" +
                    "        <div class=\"card-header\" id=\"heading-1\">\n" +
                    "          <h3 class=\"mb-0\">\n" +
                    "            <a role=\"button\" data-toggle=\"collapse\" href=\"#collapse-" + i + "\" aria-expanded=\"true\" aria-controls=\"collapse-" + i + "\">\n" +
                    "             " + FacilityName + " \n" +
                    "            </a>\n" +
                    "          </h3>\n" +
                    "        </div>\n" +
                    "        <div id=\"collapse-" + i + "\" class=\"collapse \" data-parent=\"#accordion\" aria-labelledby=\"heading-1\">\n" +
                    "          <div class=\"card-body\">\n" +
                    "\n" +
                    "            <div id=\"accordion-" + i + "\">\n" +
                    "\n" +
                    "              <div class=\"card-body\">\n" +
                    "\n" +
                    "                <div id=\"accordion-" + i + "-" + i + "\">\n" +
                    "                  <div class=\"card\">\n" +
                    "                    <div class=\"card-header\" id=\"heading-" + i + "-" + i + "-1\">\n" +
                    "                      <h5 class=\"mb-0\">\n" +
                    "                        <a class=\"collapsed\" role=\"button\" data-toggle=\"collapse\" href=\"#collapse-" + i + "-" + i + "-1\" aria-expanded=\"false\" aria-controls=\"collapse-" + i + "-" + i + "-1\">\n" +
                    "                         Bolt Device Exceptions\n" +
                    "                       </a>\n" +
                    "                     </h5>\n" +
                    "                   </div>\n" +
                    "                   <div id=\"collapse-" + i + "-" + i + "-1\" class=\"collapse\" data-parent=\"#accordion-" + i + "-" + i + "\" aria-labelledby=\"heading-" + i + "-" + i + "-1\">\n" +
                    "                    <div class=\"card-body\">\n" +
                    "                      <div class=\"box\">\n" +
                    "                        <div class=\"box-body p-15\">           \n" +
                    "                          <div class=\"table-responsive\">\n" +
                    "                            <table  class=\"table mt-0 table-hover no-wrap table-borderless\" data-page-size=\"5\">\n" +
                    "                              <thead>\n" +
                    "                                <tr>\n" +
                    "                                  <th scope=\"col\">Exception</th>\n" +
                    "                                  <th scope=\"col\">Date</th>\n" +
                    "                                  <th scope=\"col\">Created By</th>\n" +
                    "                                </tr>\n" +
                    "                              </thead>\n" +
                    "                              <tbody >\n");
            try {
                Query = "Select SessionResponse,CreatedDate,UserId from " + LiveFacilities[i] + ".BoltDeviceConnectionFailures ";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    REPORT_HTML.append("<tr>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(3) + "</td>\n");
                    REPORT_HTML.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            REPORT_HTML.append("                              </tbody>\n" +
                    "                            </table>\n" +
                    "                          </div>\n" +
                    "                        </div>\n" +
                    "                      </div>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                </div>\n" +
                    "                <div class=\"card\">\n" +
                    "                  <div class=\"card-header\" id=\"heading-" + i + "-" + i + "-2\">\n" +
                    "                    <h5 class=\"mb-0\">\n" +
                    "                      <a class=\"collapsed\" role=\"button\" data-toggle=\"collapse\" href=\"#collapse-" + i + "-" + i + "-2\" aria-expanded=\"false\" aria-controls=\"collapse-" + i + "-" + i + "-2\">\n" +
                    "                        Card Connect Exceptions\n" +
                    "                      </a>\n" +
                    "                    </h5>\n" +
                    "                  </div>\n" +
                    "                  <div id=\"collapse-" + i + "-" + i + "-2\" class=\"collapse\" data-parent=\"#accordion-" + i + "-" + i + "\" aria-labelledby=\"heading-" + i + "-" + i + "-2\">\n" +
                    "                    <div class=\"card-body\">\n" +
                    "                      <div class=\"box\">\n" +
                    "                        <div class=\"box-body p-15\">           \n" +
                    "                          <div class=\"table-responsive\">\n" +
                    "                            <table id=\"exceptions00\" class=\"table mt-0 table-hover no-wrap table-borderless\" data-page-size=\"5\">\n" +
                    "                              <thead>\n" +
                    "                                <tr>\n" +
                    "                                  <th scope=\"col\">Exception</th>\n" +
                    "                                  <th scope=\"col\">Date</th>\n" +
                    "                                </tr>\n" +
                    "                              </thead>\n" +
                    "                              <tbody >\n");
            try {
                Query = "Select ResponseText,CreatedDate from " + LiveFacilities[i] + ".CardConnectResponses WHERE ResponseType='ERROR'";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    REPORT_HTML.append("<tr>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(1) + "</td>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    REPORT_HTML.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            REPORT_HTML.append("                              </tbody>\n" +
                    "                            </table>\n" +
                    "                          </div>\n" +
                    "                        </div>\n" +
                    "                      </div>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                </div>\n" +
                    "                <div class=\"card\">\n" +
                    "                  <div class=\"card-header\" id=\"heading-" + i + "-" + i + "-3\">\n" +
                    "                    <h5 class=\"mb-0\">\n" +
                    "                      <a class=\"collapsed\" role=\"button\" data-toggle=\"collapse\" href=\"#collapse-" + i + "-" + i + "-3\" aria-expanded=\"false\" aria-controls=\"collapse-" + i + "-" + i + "-3\">\n" +
                    "                        Transaction Exceptions\n" +
                    "                      </a>\n" +
                    "                    </h5>\n" +
                    "                  </div>\n" +
                    "                  <div id=\"collapse-" + i + "-" + i + "-3\" class=\"collapse\" data-parent=\"#accordion-" + i + "-" + i + "\" aria-labelledby=\"heading-" + i + "-" + i + "-3\">\n" +
                    "                    <div class=\"card-body\">\n" +
                    "                      <div class=\"box\">\n" +
                    "                        <div class=\"box-body p-15\">           \n" +
                    "                          <div class=\"table-responsive\">\n" +
                    "                            <table id=\"exceptions00\" class=\"table mt-0 table-hover no-wrap table-borderless\" data-page-size=\"5\">\n" +
                    "                              <thead>\n" +
                    "                                <tr>\n" +
                    "                                  <th scope=\"col\">Exception</th>\n" +
                    "                                  <th scope=\"col\">Date</th>\n" +
                    "                                  <th scope=\"col\">Created By</th>\n" +
                    "                                </tr>\n" +
                    "                              </thead>\n" +
                    "                              <tbody >\n");
            try {
                Query = "Select JSON_Response,CreatedDate,CreatedBy from " + LiveFacilities[i] + ".JSON_Response WHERE ResponseType='ERROR'";

                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    JSONObject obj = new JSONObject(rset.getString(1));
                    respTxt = obj.getString("resptext");
                    REPORT_HTML.append("<tr>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + respTxt + "</td>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(2) + "</td>\n");
                    REPORT_HTML.append("<td style=\"border: 1px solid black;\" >" + rset.getString(3) + "</td>\n");
                    REPORT_HTML.append("</tr>\n");
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            REPORT_HTML.append("                              </tbody>\n" +
                    "                            </table>\n" +
                    "                          </div>\n" +
                    "                        </div>\n" +
                    "                      </div>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                </div>\n" +
                    "              </div>\n" +
                    "\n" +
                    "            </div>\n" +
                    "          </div>\n" +
                    "        </div>      \n" +
                    "\n" +
                    "      </div>\n" +
                    "    </div>");
        }
//
//
//


        try {
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("REPORT_HTML", String.valueOf(REPORT_HTML));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/ExceptionReport.html");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//
    }

}
