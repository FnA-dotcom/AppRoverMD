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
public class RefundVoidReceipt extends HttpServlet {

/*    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;*/

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
        Connection conn = null;

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

            if (ActionID.equals("CardConnectRVReceipt")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Card Connect Refund Void Receipt", "Printing Refund and Void Receipt", FacilityIndex);
                CardConnectRVReceipt(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
            } else if (ActionID.equals("BoltRVReceipt")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "BOLT Refund Void Receipt", "Printing Refund and Void Receipt", FacilityIndex);
                BoltRVReceipt(request, out, conn, context, UserId, helper, FacilityIndex, DatabaseName);
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


    private void CardConnectRVReceipt(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String DatabaseName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String headName = "";
        String FullName = "";
        String Address = "";
        String Phone = "";
        String RetRef = "";
        String Name = "";
        String Date = "";
        String Time = "";
        String receipt = "";
        String MRN = request.getParameter("xy89op").trim();
        String InvoiceNo = request.getParameter("yt78r3").trim();
        int CCId = Integer.parseInt(request.getParameter("er44u7").trim());
        String flag = request.getParameter("flag").trim();
        String Amount = request.getParameter("he667I").trim();

        try {
            String[] RecieptCD = helper.receiptClientData(request, conn, servletContext, facilityIndex);
            FullName = RecieptCD[0];
            Address = RecieptCD[1];
            Phone = RecieptCD[2];

            if (flag.equals("V")) {
                headName = "VOID";
                Query = " Select IFNULL(a.ReturnReference,''), IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),''), IFNULL(DATE_FORMAT(a.CreatedDate,'%T'),'')," +
                        " CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.LastName,''), ' ', IFNULL(b.MiddleInitial,''))" +
                        " from " + DatabaseName + ".VoidTransactions a " +
                        " LEFT JOIN " + DatabaseName + ".PatientReg b on a.PatientMRN = b.MRN " +
                        " Where a.PatientMRN = '" + MRN + "' and a.InvoiceNo = '" + InvoiceNo + "' and a.CardConnectIndex = " + CCId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    RetRef = rset.getString(1).trim();
                    Date = rset.getString(2).trim();
                    Time = rset.getString(3).trim();
                    Name = rset.getString(4).trim();
                }
                rset.close();
                stmt.close();


            } else if (flag.equals("R")) {
                headName = "REFUND";
                Query = " Select IFNULL(a.ReturnReference,''), IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),''), IFNULL(DATE_FORMAT(a.CreatedDate,'%T'),'')," +
                        " CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.LastName,''), ' ', IFNULL(b.MiddleInitial,''))" +
                        " from " + DatabaseName + ".RefundTransactions a " +
                        " LEFT JOIN " + DatabaseName + ".PatientReg b on a.PatientMRN = b.MRN " +
                        " Where a.PatientMRN = '" + MRN + "' and a.InvoiceNo = '" + InvoiceNo + "' and a.CardConnectResponseIndex = " + CCId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    RetRef = rset.getString(1).trim();
                    Date = rset.getString(2).trim();
                    Time = rset.getString(3).trim();
                    Name = rset.getString(4).trim();
                }
                rset.close();
                stmt.close();
            }

            receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'>" +
                    "<div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p><br><p style='margin:0px;'><u><b>" + headName + " TRANSACTION</b></u></p></div>" +
                    "<div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Invoice #: " + InvoiceNo + "</p> <p style='margin:0px;'>Return Ref #: " + RetRef + "</p>   <p style='margin:0px;'>Status: Approval </p></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + Date + "</span> <span style='margin-left: 166px'>" + Time + "</span></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + Amount + "</span></div>" +
                    "<div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Credit Card </p><p style='margin:0px;'>" + Name + "</p></div>" +
                    "<div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div>  <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>    </div></div>";


            out.println(receipt);


        } catch (Exception e) {
            out.println("Somthing Went WRONG!! Please contact System adminitrator ");
            System.out.println(e.getMessage());
        }
    }

    private void BoltRVReceipt(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, String DatabaseName) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String headName = "";
        String FullName = "";
        String Address = "";
        String Phone = "";
        String RetRef = "";
        String Name = "";
        String Date = "";
        String Time = "";
        String receipt = "";
        String MRN = request.getParameter("xy89op").trim();
        String InvoiceNo = request.getParameter("yt78r3").trim();
        int BoltIdx = Integer.parseInt(request.getParameter("er44u7").trim());
        String flag = request.getParameter("flag").trim();
        String Amount = request.getParameter("he667I").trim();

        try {
            String[] RecieptCD = helper.receiptClientData(request, conn, servletContext, facilityIndex);
            FullName = RecieptCD[0];
            Address = RecieptCD[1];
            Phone = RecieptCD[2];

            if (flag.equals("V")) {
                headName = "VOID";
                Query = " Select IFNULL(a.ReturnReference,''), IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),''), IFNULL(DATE_FORMAT(a.CreatedDate,'%T'),'')," +
                        " CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.LastName,''), ' ', IFNULL(b.MiddleInitial,''))" +
                        " from " + DatabaseName + ".VoidTransactions a " +
                        " LEFT JOIN " + DatabaseName + ".PatientReg b on a.PatientMRN = b.MRN " +
                        " Where a.PatientMRN = '" + MRN + "' and a.InvoiceNo = '" + InvoiceNo + "' and a.BoltIdx = " + BoltIdx;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    RetRef = rset.getString(1).trim();
                    Date = rset.getString(2).trim();
                    Time = rset.getString(3).trim();
                    Name = rset.getString(4).trim();
                }
                rset.close();
                stmt.close();


            } else if (flag.equals("R")) {
                headName = "REFUND";
                Query = " Select IFNULL(a.ReturnReference,''), IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),''), IFNULL(DATE_FORMAT(a.CreatedDate,'%T'),'')," +
                        " CONCAT(IFNULL(b.FirstName,''),' ',IFNULL(b.LastName,''), ' ', IFNULL(b.MiddleInitial,''))" +
                        " from " + DatabaseName + ".RefundTransactions a " +
                        " LEFT JOIN " + DatabaseName + ".PatientReg b on a.PatientMRN = b.MRN " +
                        " Where a.PatientMRN = '" + MRN + "' and a.InvoiceNo = '" + InvoiceNo + "' and a.BoltIdx = " + BoltIdx;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    RetRef = rset.getString(1).trim();
                    Date = rset.getString(2).trim();
                    Time = rset.getString(3).trim();
                    Name = rset.getString(4).trim();
                }
                rset.close();
                stmt.close();
            }

            receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'>" +
                    "<div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p><br><p style='margin:0px;'><u><b>" + headName + " TRANSACTION</b></u></p></div>" +
                    "<div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Invoice #: " + InvoiceNo + "</p> <p style='margin:0px;'>Return Ref #: " + RetRef + "</p>   <p style='margin:0px;'>Status: Approval </p></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + Date + "</span> <span style='margin-left: 166px'>" + Time + "</span></div>" +
                    "<div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + Amount + "</span></div>" +
                    "<div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: BOLT </p><p style='margin:0px;'>" + Name + "</p></div>" +
                    "<div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div>  <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>    </div></div>";


            out.println(receipt);


        } catch (Exception e) {
            out.println("Somthing Went WRONG!! Please contact System adminitrator ");
            System.out.println(e.getMessage());
        }
    }


}