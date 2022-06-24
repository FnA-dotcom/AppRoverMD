package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

@SuppressWarnings("Duplicates")
public class TransactionReport extends HttpServlet {
    Integer ScreenIndex = 14;

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
        Connection conn = null;
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();
        UtilityHelper helper = new UtilityHelper();
        String ActionID = "";
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            //int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

/*            if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, this.ScreenIndex)) {
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }*/

            switch (ActionID) {
                case "TransactionReport_Input":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Open Transaction Report Input ", FacilityIndex);
                    TransactionReport_Input(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "TransactionReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Get Transaction Report", FacilityIndex);
                    showReport(request, response, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "PatientTransaction":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Get Transaction Report", FacilityIndex);
                    PatientTransaction(request, response, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                case "ShowReceipt":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Transaction Report", "Get Transaction Report", FacilityIndex);
                    ShowReceipt(request, response, out, conn, context, UserId, DatabaseName, helper, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (handleRequest)", context, Ex, "TransactionReport", "handleRequest", conn);
            Services.DumException("TransactionReport", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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

    void TransactionReport_Input(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer PatientInvoiceList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "a.InvoiceNo FROM " + Database + ".InvoiceMaster a  " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN " +
                    " where b.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport.html");
        } catch (Exception var11) {
            out.println(Query);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void showReport(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query2 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";

        int Found = 0;
        int SNo = 1;
        String PatientId = "";
        String FromDate = "";
        String ToDate = "";
        StringBuffer PatientInvoiceList = new StringBuffer();
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SearchBy = Integer.parseInt(request.getParameter("SearchByVal").trim());
        String RetRef = "N/A";
        String ResponseText = "N/A";
        String AccountNo = "N/A";
        try {
            if (SearchBy == 1) {
                PatientId = request.getParameter("PatientId").trim();
                String[] parts = PatientId.split("\\,");
                String MRN = parts[0];
                String InvoiceNo = parts[1];
                Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo  " +
                        " FROM " + Database + ".InvoiceMaster a  " +
                        " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                        " WHERE b.Status = 0 GROUP BY a.PatientMRN";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    if (rset.getString(1).equals(MRN)) {
                        PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\" selected>" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                        continue;
                    }
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                }
                rset.close();
                stmt.close();

                Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , " +
                        "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                        "CASE " +
                        "WHEN PayMethod = 1 THEN 'Credit Card' " +
                        "WHEN PayMethod = 2 THEN 'Cash' " +
                        "WHEN PayMethod = 3 THEN 'BOLT Device' " +
                        "WHEN PayMethod = 4 THEN 'Ingenico' " +
                        "ELSE '' END, IFNULL(PayMethod,0),IFNULL(a.Id,'N/A')  " +
                        "FROM " + Database + ".PaymentReceiptInfo a " +
                        "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                        "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo  " +
                        "WHERE a.PatientMRN = '" + MRN + "' AND c.Status = 0 AND b.status=0 AND c.InvoiceCreatedBy='" + UserId + "' ORDER BY a.CreatedDate DESC ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    RetRef = "N/A";
                    ResponseText = "N/A";
                    AccountNo = "N/A";
                    if (rset.getInt(12) == 1) {
                        Query2 = "SELECT RetRef,ResponseText,AccountNo FROM " + Database + ".CardConnectResponses " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            RetRef = rset2.getString(1);
                            ResponseText = rset2.getString(2);
                            AccountNo = rset2.getString(3);
                        }
                        rset2.close();
                        stmt2.close();

                    }
                    if (rset.getInt(12) == 3) {
                        Query2 = "SELECT JSON_Response FROM " + Database + ".JSON_Response " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse("[" + rset2.getString(1) + "]");
                            JSONArray array = (JSONArray) obj;
                            JSONObject obj2 = (JSONObject) array.get(0);
                            RetRef = (String) obj2.get("retref");
                            ResponseText = (String) obj2.get("resptext");
                        }
                        rset2.close();
                        stmt2.close();
                    }

                    TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                    TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    if (rset.getString(11).trim().equals("Cash")) {
                        if (ClientId == 27 || ClientId == 29) {
                            TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                        } else {
                            TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                        }
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                    TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    TransactionList.append("<td align=left>" + RetRef + "</td>\n");
                    TransactionList.append("<td align=left>" + ResponseText + "</td>\n");
                    TransactionList.append("<td align=left>" + AccountNo + "</td>\n");
                    if (rset.getInt(12) != 3) {
                        TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + ")\">Show Receipt</button></td>\n");
                    } else
                        TransactionList.append("<td align=center>No Receipt</td>\n");

                    TransactionList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();
            } else {
                FromDate = request.getParameter("FromDate").trim();
                ToDate = request.getParameter("ToDate").trim();

                Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo  " +
                        "FROM " + Database + ".InvoiceMaster a   " +
                        "LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                        "WHERE a.Status = 0 AND b.Status = 0 GROUP BY a.PatientMRN";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1).trim() + "," + rset.getString(3).trim() + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                rset.close();
                stmt.close();

                Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                        "a.InvoiceNo, a.TotalAmount, a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , " +
                        "DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                        "CASE " +
                        "WHEN PayMethod = 1 THEN 'Credit Card' " +
                        "WHEN PayMethod = 2 THEN 'Cash' " +
                        "WHEN PayMethod = 3 THEN 'BOLT Device' " +
                        "WHEN PayMethod = 4 THEN 'Ingenico' " +
                        "ELSE '' END, IFNULL(PayMethod,0),IFNULL(a.Id,'N/A')  " +
                        "FROM " + Database + ".PaymentReceiptInfo a " +
                        "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                        "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo " +
                        "WHERE a.CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'  AND c.Status = 0 AND b.status=0  AND c.InvoiceCreatedBy='" + UserId + "'" +
                        " ORDER BY a.CreatedDate DESC ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {

                    RetRef = "N/A";
                    ResponseText = "N/A";
                    AccountNo = "N/A";
                    if (rset.getInt(12) == 1) {
                        Query2 = "SELECT RetRef,ResponseText,AccountNo FROM " + Database + ".CardConnectResponses " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            RetRef = rset2.getString(1);
                            ResponseText = rset2.getString(2);
                            AccountNo = rset2.getString(3);
                        }
                        rset2.close();
                        stmt2.close();

                    }
                    if (rset.getInt(12) == 3) {
                        Query2 = "SELECT JSON_Response FROM " + Database + ".JSON_Response " +
                                "WHERE PatientMRN = " + rset.getInt(1) + " AND InvoiceNo = '" + rset.getString(3) + "' ";
                        stmt2 = conn.createStatement();
                        rset2 = stmt2.executeQuery(Query2);
                        if (rset2.next()) {
                            if (rset2.getString(1) != null) {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse("[" + rset2.getString(1) + "]");
                                JSONArray array = (JSONArray) obj;
                                JSONObject obj2 = (JSONObject) array.get(0);
                                RetRef = (String) obj2.get("retref");
                                ResponseText = (String) obj2.get("resptext");
                            }
                        }
                        rset2.close();
                        stmt2.close();

                    }

                    TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                    TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                    TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    if (rset.getString(11).trim().equals("Cash")) {
                        if (ClientId == 27 || ClientId == 29) {
                            TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                        } else {
                            TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                        }
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                    TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    TransactionList.append("<td align=left>" + RetRef + "</td>\n");
                    TransactionList.append("<td align=left>" + ResponseText + "</td>\n");
                    TransactionList.append("<td align=left>" + AccountNo + "</td>\n");
                    //TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + ")\">Show Receipt</button></td>\n");
                    if (rset.getInt(12) != 3) {
                        TransactionList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"ShowReceipt(" + rset.getInt(13) + ")\">Show Receipt</button></td>\n");
                    } else
                        TransactionList.append("<td align=center>No Receipt</td>\n");
                    TransactionList.append("</tr>");
                    SNo++;
                }
                rset.close();
                stmt.close();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("TransactionList", String.valueOf(TransactionList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/TransactionReport.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (showReport)", servletContext, Ex, "TransactionReport", "showReport", conn);
            Services.DumException("TransactionReport", "showReport ", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "TransactionReport");
            Parser.SetField("ActionID", "TransactionReport_Input");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    void PatientTransaction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, int ClientId) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        int Found = 0;
        int SNo = 1;
        String PatientId = "";
        String FromDate = "";
        String ToDate = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");

        int MRN = Integer.parseInt(request.getParameter("MRN").trim());
        try {

            Query = " Select a.PatientMRN , CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), a.InvoiceNo, a.TotalAmount, " +
                    "a.PaidAmount,  a.BalAmount, IFNULL(a.RefNo,'-'), IFNULL(a.Remarks,'-') , DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T') as PaymentDate, " +
                    "DATE_FORMAT(c.CreatedDate, '%m/%d/%Y %T') as InvoiceDate,  " +
                    "CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT Device' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END, " +
                    "IFNULL(PayMethod,0),IFNULL(c.InvoiceCreatedBy,0) " +
                    " FROM " + Database + ".PaymentReceiptInfo a " +
                    "LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN " +
                    "LEFT JOIN " + Database + ".InvoiceMaster c on a.InvoiceNo = c.InvoiceNo " +
                    " WHERE a.PatientMRN = '" + MRN + "' and b.status=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                TransactionList.append("<tr id=\"Tran_" + SNo + "\">");
                TransactionList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(4)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(5)) + "</td>\n");
                TransactionList.append("<td align=left>" + numFormat.format(rset.getDouble(6)) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                if (rset.getString(11).trim().equals("Cash")) {
                    if (ClientId == 27 || ClientId == 29) {
                        TransactionList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=CashReceipt&MRN=" + rset.getInt(1) + "&InvoiceNo=" + rset.getString(3) + ">" + rset.getString(11) + "</a></td>\n");
                    } else {
                        TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    }
                } else {
                    TransactionList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                }
                TransactionList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                TransactionList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                TransactionList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();


            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("TransactionList", String.valueOf(TransactionList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientTransactions.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (PatientTransaction)", servletContext, Ex, "TransactionReport", "PatientTransaction", conn);
            Services.DumException("TransactionReport", "Patient Transaction", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    void ShowReceipt(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, int ClientId) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String receipt = null;
        StringBuffer TransactionList = new StringBuffer();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");

        int id = Integer.parseInt(request.getParameter("id").trim());
        try {

            Query = "SELECT IFNULL(CONVERT(Receipt USING utf8),'No Receipt Found') " +
                    "FROM " + Database + ".PaymentReceiptInfo WHERE id=" + id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                receipt = rset.getString(1);
            }
            rset.close();
            stmt.close();

            out.println(receipt);
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Transaction Report ** (ShowReceipt)", servletContext, Ex, "TransactionReport", "ShowReceipt", conn);
            Services.DumException("TransactionReport", "Show Receipt", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();

        }
    }


}

