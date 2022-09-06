package md;

//import Handheld.UtilityHelper;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.text.DecimalFormat;

@SuppressWarnings("Duplicates")
public class BoltPayServices extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private PreparedStatement pStmt = null;
    private Connection conn = null;
    private boltmaster bm = new boltmaster();
    private BoltClientProperties props;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        UtilityHelper helper = new UtilityHelper();
        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context;
        context = this.getServletContext();

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

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }

            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ServiceRequests) {
                case "GetDetails":
                    GetDetails(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "GetConnect":
                    GetConnect(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "GetTransaction":
                    GetTransaction(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
                    break;
                case "Disconnect_Stop":
                    Disconnect_Stop(request, conn, context, out, DatabaseName, UserId, FacilityIndex, helper);
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
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (handleRequest)", context, Ex, "BoltPayServices", "handleRequest", conn);
            Services.DumException("BoltPayServices", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
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

    private void GetDetailsold(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId) {
        stmt = null;
        rset = null;
        Query = "";
        int Flag = 0;
        String PatientMRN = "";
        String PatientName = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String ZipCOde = "";
        String PhNumber = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        double AmountToPay = 0.0;
        String SessionKey = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuffer DeviceList = new StringBuffer();


        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;

        String InvoiceNo = request.getParameter("x0Y61008").trim();
        String DeviceS = request.getParameter("DeviceS").trim();

        try {
            //String[] BoltConnect = helper.getBoltCredential(conn, FlagType);
            /*out.println("Site " + BoltConnect[0] + "<br>");
            out.println("URL " + BoltConnect[1] + "<br>");
            out.println("Currency " + BoltConnect[2] + "<br>");*/


            Query = "Select a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    " a.TotalAmount, a.PaidAmount, a.BalAmount, IFNULL(b.Address,''), IFNULL(b.City,''), IFNULL(b.State,''), IFNULL(b.County,''), IFNULL(ZipCOde,'')," +
                    "IFNULL(PhNumber,'')  " +
                    " from " + Database + ".InvoiceMaster a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
                Address = rset.getString(6);
                City = rset.getString(7);
                State = rset.getString(8);
                County = rset.getString(9);
                ZipCOde = rset.getString(10);
                PhNumber = rset.getString(11);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "' and status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InstallmentPlanFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();
            if (InstallmentPlanFound > 0) {
                Query = "Select IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''), " +
                        " CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + PatientName + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();

                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    AmountToPay = rset.getDouble(1);
                }
                rset.close();
                stmt.close();
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            try {
                //CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
                //bm.stop();
            } catch (Exception e) {
            }
            try {
                bm.init(DeviceS, ClientId, conn);
                SessionKey = bm.validateConnection();
            } catch (Exception err) {
            }
            if (SessionKey.equals("") || SessionKey == null) {
                Flag = 10;
            } else if (SessionKey.equals("Error")) {
                Flag = 0;
            } else {
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".ActiveSessionBolt (SessionKey,Status,CreatedDate,ClientId,CreatedBy) " +
                            "VALUES (?,0,now(),?,?) ");
                    MainReceipt.setString(1, SessionKey);
                    MainReceipt.setInt(2, ClientId);
                    MainReceipt.setString(3, UserId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error 1- Insertion ActiveSession Table :" + e.getMessage());
                    return;
                }
                Flag = 1;
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("x0Y61008", InvoiceNo);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("ZipCOde", String.valueOf(ZipCOde));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("AmountToPay", String.valueOf(numFormat.format(AmountToPay)));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
            Parser.SetField("DeviceS", String.valueOf(DeviceS));
            Parser.SetField("Flag", String.valueOf(Flag));
            Parser.SetField("SessionKey", String.valueOf(SessionKey));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BoltFrontEnd.html");
        } catch (Exception Ex) {
            try {
                //CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
                //bm.stop();
            } catch (Exception er) {
            }

            out.println("Error " + Ex.getMessage());
            String str = "";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                str = str + Ex.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }


    }

    private void GetConnectold(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId) {
        Query = "";
        stmt = null;
        rset = null;
        try {
            //CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
            //bm.stop();
        } catch (Exception e) {
        }
        String SessionKey = "";
        int Flag = 0;
        String DeviceS = request.getParameter("DeviceS").trim();
        try {
            bm.init(DeviceS, ClientId, conn);
            SessionKey = bm.validateConnection();
        } catch (Exception err) {
        }
        if (SessionKey.equals("")) {
            Flag = 10;
        } else if (SessionKey.equals("Error")) {
            Flag = 0;
        } else {
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".ActiveSessionBolt (SessionKey,Status,CreatedDate,ClientId,CreatedBy) " +
                        "VALUES (?,0,now(),?,?) ");
                MainReceipt.setString(1, SessionKey);
                MainReceipt.setInt(2, ClientId);
                MainReceipt.setString(3, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 2- Insertion ActiveSession Table :" + e.getMessage());
                return;
            }
            Flag = 1;
        }
        out.println(Flag + "~" + SessionKey);


//        bm.init(DeviceS, ClientId, conn);
//        SessionKey = bm.validateConnection();
//        if (SessionKey.equals("Error")) {
//            Flag = 0;
//        }
    }

    private void GetTransactionold(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId) {

        try {
            String InvoiceNo = "";
            String PatientMRN = "";
            int Paid = 0;
            double TotalAmount = 0.0;
            double PaidAmount = 0.0;
            double BalAmount = 0.0;
            int InstallmentPlanId = 0;
            int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
            PatientMRN = request.getParameter("PatientMRN").trim();
            InvoiceNo = request.getParameter("InvoiceNo").trim();
            TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").replace(",","").trim());
            PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim());
            BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
            String Amount = request.getParameter("Amount").trim();

            if (Amount.contains(".")) {
                String parts[] = Amount.split("\\.");
                if (parts[1].length() == 1) {
                    parts[1] = parts[1] + "0";
                } else if (parts[1].length() > 2) {
                    parts[1] = parts[1].substring(0, 2);
                }
                if (parts[0].equals("0")) {
                    Amount = parts[1];
                } else {
                    Amount = parts[0] + parts[1];
                }
            } else {
                Amount = Amount + "00";
            }
            System.out.println("Amount: " + Amount);
            //Map<String, String> AuthCardRe = new HashMap<String, String>();
            String AuthCardRe = "";
            AuthCardRe = bm.authCard(Amount);
//            AuthCardRe = bm.authCard("11");
            //out.println("AuthCardRe--->"+AuthCardRe.toString());
            System.out.println("Response HERE : " + AuthCardRe);

            //AuthCardRe = StringEscapeUtils.unescapeJava(AuthCardRe);
            System.out.println(Database + "-----:Database");
            try {
                PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".JSON_Response (InvoiceNo,PatientMRN,JSON_Response,"
                        + " CreatedBy,CreatedDate) \nVALUES (?,?,?,?,now()) ");
                MainReceipt.setString(1, InvoiceNo);
                MainReceipt.setString(2, PatientMRN);
                MainReceipt.setString(3, AuthCardRe);
                MainReceipt.setString(4, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                System.out.println("Exception ERROR in Inserting Data in JSON_Response Table: " + e.getMessage());
                return;
            }

            //System.out.println(AuthCardRe);
            //boolean Validate = isValidJSON(AuthCardRe);

            //if (Validate) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
//
            JsonNode jsonNode = objectMapper.readTree(AuthCardRe);
            String resptext = null;
            String respCode = null;
            String DateTime = null;
            String ApprovedAmount = null;
            String CardAddress1 = null;
            String CardAddress2 = null;
            String CardPhone = null;
            String CardName = null;
            JsonNode receiptData = null;
            if (jsonNode.has("resptext")) {
                resptext = jsonNode.get("resptext").toString();
                respCode = jsonNode.get("respcode").toString();
                resptext = resptext.substring(1, resptext.length() - 1);
                respCode = respCode.substring(1, respCode.length() - 1);
                System.out.println(resptext + ": resptext");
                System.out.println(respCode + ": respCode");

                if (resptext.equals("Approval") && respCode.equals("000")) {
                    ApprovedAmount = jsonNode.get("amount").toString();
                    ApprovedAmount = ApprovedAmount.substring(1, ApprovedAmount.length() - 1);
                    System.out.println(ApprovedAmount + ": Approved AMount");
//                        receiptData = jsonNode.get("receiptData");
//                        DateTime = receiptData.get("dateTime").toString().replaceAll("\"", "");
//                        CardAddress1 = receiptData.get("address1").toString().replaceAll("\"", "");
//                        CardAddress2 = receiptData.get("address2").toString().replaceAll("\"", "");
//                        CardPhone = receiptData.get("phone").toString().replaceAll("\"", "");
//                        CardName = receiptData.get("nameOnCard").toString().replaceAll("\"", "");

                    if (BalAmount == Double.parseDouble(ApprovedAmount)) {
                        Paid = 1;
                    }
                    Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Double.parseDouble(ApprovedAmount)) + "', BalAmount = '" + (BalAmount - Double.parseDouble(ApprovedAmount)) + "', Paid = '" + Paid + "', PaymentDateTime = now() " + " where InvoiceNo = '" + InvoiceNo + "'";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," + " PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) \nVALUES (?,?,?,?,?,?,?,?,now(),?) ");
                        MainReceipt.setString(1, PatientMRN);
                        MainReceipt.setString(2, InvoiceNo);
                        MainReceipt.setDouble(3, TotalAmount);
                        MainReceipt.setDouble(4, Double.parseDouble(ApprovedAmount));
                        MainReceipt.setInt(5, Paid);
                        MainReceipt.setString(6, "BOLT");//ref No
                        MainReceipt.setString(7, "BOLT PAYMENT");//remarks
                        MainReceipt.setString(8, String.valueOf("3"));// Pay Method
                        MainReceipt.setDouble(9, BalAmount - Double.parseDouble(ApprovedAmount));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception e) {
                        System.out.println("Error 12- Insertion PaymentReceiptInfo Table :" + e.getMessage());
                        return;
                    }

                    if (InstallmentPlanFound > 0) {
                        try {
                            Query = "Select Id from " + Database + ".InstallmentPlan where Paid = 0 and InvoiceNo = '" + InvoiceNo + "' limit 1";
                            stmt = conn.createStatement();
                            rset = stmt.executeQuery(Query);
                            if (rset.next()) {
                                InstallmentPlanId = rset.getInt(1);
                            }
                            rset.close();
                            stmt.close();
                            if (InstallmentPlanId > 0) {
                                Query = " Update " + Database + ".InstallmentPlan set Paid = " + 1 + " where Id = " + InstallmentPlanId + "";
                                stmt = conn.createStatement();
                                stmt.executeUpdate(Query);
                                stmt.close();
                            }
                        } catch (Exception e) {
                            out.println("Error in Updating Installment plan Table: " + e.getMessage());
                            return;
                        }
                    }
                    out.println("success~" + resptext);
                    try {
//                        CheckActiveSession(conn, Database, UserId, ClientId);
//                        bm.stop();
                    } catch (Exception e) {
                    }

                } else if (resptext.equals("Do not honor") && respCode.equals("100")) {
                    //Error or Declined will treated After This
                    out.println("09~" + resptext);//Transaction Declined.
                    try {
//                        CheckActiveSession(conn, Database, UserId, ClientId);
//                        bm.stop();
                    } catch (Exception e) {
                    }
                }

                //HERE SAVE Complete Response in a Table here.....
            }

//            }
            /*else {
                out.println("jsonvalidatefail");
                try {
                    CheckActiveSession(conn, Database, UserId, ClientId);
                    bm.stop();
                } catch (Exception e) {
                }
            }*/

        } catch (Exception e) {
            try {
                //CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
                //bm.stop();
            } catch (Exception er) {
            }
            System.out.println("Error in Trsaction Method:-- " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);
        }


    }

    private void Disconnect_Stopold(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId) {
        try {
            //CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
            bm.stop();
            out.println("100");
        } catch (Exception e) {
        }

    }

    private void GetDetails(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper) throws IOException {
        stmt = null;
        rset = null;
        Query = "";
        int Flag = 0;
        String PatientMRN = "";
        String PatientName = "";
        String Address = "";
        String City = "";
        String State = "";
        String County = "";
        String ZipCOde = "";
        String PhNumber = "";
        double TotalAmount = 0.0;
        double PaidAmount = 0.0;
        double BalAmount = 0.0;
        double AmountToPay = 0.0;
        String SessionKey = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder CDRList = new StringBuilder();
        StringBuilder DeviceList = new StringBuilder();


        final DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;

        String InvoiceNo = request.getParameter("x0Y61008").trim();
        String DeviceS = request.getParameter("DeviceS").trim();

        try {
            Query = "Select a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    " a.TotalAmount, a.PaidAmount, a.BalAmount, IFNULL(b.Address,''), IFNULL(b.City,''), IFNULL(b.State,''), IFNULL(b.County,''), IFNULL(ZipCOde,'')," +
                    "IFNULL(PhNumber,'')  " +
                    " from " + Database + ".InvoiceMaster a LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN where a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
                Address = rset.getString(6);
                City = rset.getString(7);
                State = rset.getString(8);
                County = rset.getString(9);
                ZipCOde = rset.getString(10);
                PhNumber = rset.getString(11);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "' and status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InstallmentPlanFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (InstallmentPlanFound > 0) {
                Query = "Select IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''), " +
                        " CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and status = 0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + PatientName + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    SNo++;
                }
                rset.close();
                stmt.close();

                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan where MRN = '" + PatientMRN + "' and " +
                        " InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    AmountToPay = rset.getDouble(1);
                }
                rset.close();
                stmt.close();
            }

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            try {
                CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
                //bm.stop();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- Check Active Session)", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
                Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }
            try {
                bm.init(DeviceS, ClientId, conn);
                SessionKey = bm.validateConnection();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- validate Connection)", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
                Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.SetField("Message", "Connection is not validated. Please connect your device!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }

            String UserIP = helper.getClientIp(request);

            if (SessionKey.equals("") || SessionKey == null) {
                Flag = 10;
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".BoltDeviceConnectionFailures (DeviceId,SessionResponse,Status,CreatedDate,UserId,UserIP) " +
                                    "VALUES (?,?,0,NOW(),?,?) ");
                    MainReceipt.setString(1, DeviceS);
                    MainReceipt.setString(2, "SessionKey header is required");
                    MainReceipt.setString(3, UserId);
                    MainReceipt.setString(4, UserIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- Error 01 - Insertion BoltDeviceConnectionFailures Table)", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
                    Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "PayNow");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    out.flush();
                    out.close();
                    return;
                }
            } else if (SessionKey.equals("Error")) {
                Flag = 0;
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".BoltDeviceConnectionFailures (DeviceId,SessionResponse,Status,CreatedDate,UserId,UserIP) " +
                                    "VALUES (?,?,0,NOW(),?,?) ");
                    MainReceipt.setString(1, DeviceS);
                    MainReceipt.setString(2, "SessionKey header is required");
                    MainReceipt.setString(3, UserId);
                    MainReceipt.setString(4, UserIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- Error 02 - Insertion BoltDeviceConnectionFailures Table)", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
                    Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "PayNow");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    out.flush();
                    out.close();
                    return;
                }
            } else {
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".ActiveSessionBolt (SessionKey,Status,CreatedDate,ClientId,CreatedBy) " +
                                    "VALUES (?,0,now(),?,?) ");
                    MainReceipt.setString(1, SessionKey);
                    MainReceipt.setInt(2, ClientId);
                    MainReceipt.setString(3, UserId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- Error 02 - Insertion ActiveSession Table)", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
                    Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "PayNow");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    out.flush();
                    out.close();
                    return;
                }
                Flag = 1;
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("x0Y61008", InvoiceNo);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Address", String.valueOf(Address));
            Parser.SetField("City", String.valueOf(City));
            Parser.SetField("State", String.valueOf(State));
            Parser.SetField("County", String.valueOf(County));
            Parser.SetField("ZipCOde", String.valueOf(ZipCOde));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("AmountToPay", String.valueOf(numFormat.format(AmountToPay)));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
            Parser.SetField("DeviceS", String.valueOf(DeviceS));
            Parser.SetField("Flag", String.valueOf(Flag));
            Parser.SetField("SessionKey", String.valueOf(SessionKey));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BoltFrontEnd.html");
        } catch (Exception Ex) {
            try {
                CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
                //bm.stop();
            } catch (Exception er) {
            }
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetDetails -- Main Catch )", servletContext, Ex, "BoltPayServices", "GetDetails", conn);
            Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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

    private void GetConnect(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper) throws IOException {
        Query = "";
        stmt = null;
        rset = null;
        try {
            CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
            //bm.stop();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetConnect ** CheckActiveSession)",  servletContext, Ex, "BoltPayServices", "GetConnect", conn);
            Services.DumException("BoltPayServices", "Get Connect", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
        String SessionKey = "";
        int Flag = 0;
        String DeviceS = request.getParameter("DeviceS").trim();
        try {
            bm.init(DeviceS, ClientId, conn);
            SessionKey = bm.validateConnection();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetConnect -- validate Connection)",  servletContext, Ex, "BoltPayServices", "GetConnect", conn);
            Services.DumException("BoltPayServices", "Get Connect", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.SetField("Message", "Connection is not validated. Please connect your device!");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
        switch (SessionKey) {
            case "":
                Flag = 10;
                break;
            case "Error":
                Flag = 0;
                break;
            default:
                try {

                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".ActiveSessionBolt (SessionKey,Status,CreatedDate,ClientId,CreatedBy) " +
                                    "VALUES (?,0,now(),?,?) ");
                    MainReceipt.setString(1, SessionKey);
                    MainReceipt.setInt(2, ClientId);
                    MainReceipt.setString(3, UserId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception Ex) {
                    helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetConnect -- Error 01 - Insertion ActiveSession Table)", servletContext, Ex, "BoltPayServices", "GetConnect", conn);
                    Services.DumException("BoltPayServices", "GetConnect", request, Ex, getServletContext());
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "RegisteredPatients");
                    Parser.SetField("ActionID", "PayNow");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                    out.flush();
                    out.close();
                    return;
                }
                Flag = 1;
                break;
        }
        out.println(Flag + "~" + SessionKey);


//        bm.init(DeviceS, ClientId, conn);
//        SessionKey = bm.validateConnection();
//        if (SessionKey.equals("Error")) {
//            Flag = 0;
//        }
    }

    private void GetTransaction(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper) throws IOException {
        try {
            String InvoiceNo = "";
            String PatientMRN = "";
            int Paid = 0;
            double TotalAmount = 0.0;
            double PaidAmount = 0.0;
            double BalAmount = 0.0;
            int InstallmentPlanId = 0;
            int InstallmentPlanFound = Integer.parseInt(request.getParameter("InstallmentPlanFound").trim());
            PatientMRN = request.getParameter("PatientMRN").trim();
            InvoiceNo = request.getParameter("InvoiceNo").trim();
/*            TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim());
            PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim());
            BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim());
            String Amount = request.getParameter("Amount").trim();*/
            TotalAmount = Double.parseDouble(request.getParameter("TotalAmount").trim().replaceAll(",",""));
            PaidAmount = Double.parseDouble(request.getParameter("PaidAmount").trim().replaceAll(",",""));
            BalAmount = Double.parseDouble(request.getParameter("BalAmount").trim().replaceAll(",",""));
            String Amount = request.getParameter("Amount").trim().replaceAll(",","");
            if (Amount.contains(".")) {
                String parts[] = Amount.split("\\.");
                if (parts[1].length() == 1) {
                    parts[1] = parts[1] + "0";
                } else if (parts[1].length() > 2) {
                    parts[1] = parts[1].substring(0, 2);
                }
                if (parts[0].equals("0")) {
                    Amount = parts[1];
                } else {
                    Amount = parts[0] + parts[1];
                }
            } else {
                Amount = Amount + "00";
            }
            String AuthCardRe = "";
            AuthCardRe = bm.authCard(Amount);

            String UserIP = helper.getClientIp(request);
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".JSON_Response (InvoiceNo,PatientMRN,JSON_Response,"
                                + " CreatedBy,CreatedDate,UserIP,ActionID) \nVALUES (?,?,?,?,now(),?,?) ");
                MainReceipt.setString(1, InvoiceNo);
                MainReceipt.setString(2, PatientMRN);
                MainReceipt.setString(3, AuthCardRe);
                MainReceipt.setString(4, UserId);
                MainReceipt.setString(5, UserIP);
                MainReceipt.setString(6, "BoltPayServices ** GetTransaction");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetTransaction -- Error 01 - Exception ERROR in Inserting Data in JSON_Response Table)", servletContext, Ex, "BoltPayServices", "GetTransaction", conn);
                Services.DumException("BoltPayServices", "Get Transaction", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
                return;
            }

            //if (Validate) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
//
            JsonNode jsonNode = objectMapper.readTree(AuthCardRe);
            String resptext = null;
            String respCode = null;
            String DateTime = null;
            String ApprovedAmount = null;
            String CardAddress1 = null;
            String CardAddress2 = null;
            String CardPhone = null;
            String CardName = null;
            JsonNode receiptData = null;
            if (jsonNode.has("resptext")) {
                resptext = jsonNode.get("resptext").toString();
                respCode = jsonNode.get("respcode").toString();
                resptext = resptext.substring(1, resptext.length() - 1);
                respCode = respCode.substring(1, respCode.length() - 1);
//                System.out.println(resptext + ": resptext");
//                System.out.println(respCode + ": respCode");

                if (resptext.equals("Approval") && respCode.equals("000")) {
                    ApprovedAmount = jsonNode.get("amount").toString();
                    ApprovedAmount = ApprovedAmount.substring(1, ApprovedAmount.length() - 1);
                    //System.out.println(ApprovedAmount + ": Approved AMount");
//                        receiptData = jsonNode.get("receiptData");
//                        DateTime = receiptData.get("dateTime").toString().replaceAll("\"", "");
//                        CardAddress1 = receiptData.get("address1").toString().replaceAll("\"", "");
//                        CardAddress2 = receiptData.get("address2").toString().replaceAll("\"", "");
//                        CardPhone = receiptData.get("phone").toString().replaceAll("\"", "");
//                        CardName = receiptData.get("nameOnCard").toString().replaceAll("\"", "");

                    if (BalAmount == Double.parseDouble(ApprovedAmount)) {
                        Paid = 1;
                    }
                    Query = " Update " + Database + ".InvoiceMaster set PaidAmount = '" + (PaidAmount + Double.parseDouble(ApprovedAmount)) + "', " +
                            "BalAmount = '" + (BalAmount - Double.parseDouble(ApprovedAmount)) + "', Paid = '" + Paid + "', PaymentDateTime = now() " +
                            " where InvoiceNo = '" + InvoiceNo + "'";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "INSERT INTO " + Database + ".PaymentReceiptInfo (PatientMRN,InvoiceNo,TotalAmount," +
                                        "PaidAmount,Paid,RefNo,Remarks,PayMethod,CreatedDate, BalAmount) VALUES (?,?,?,?,?,?,?,?,now(),?) ");
                        MainReceipt.setString(1, PatientMRN);
                        MainReceipt.setString(2, InvoiceNo);
                        MainReceipt.setDouble(3, TotalAmount);
                        MainReceipt.setDouble(4, Double.parseDouble(ApprovedAmount));
                        MainReceipt.setInt(5, Paid);
                        MainReceipt.setString(6, "BOLT");//ref No
                        MainReceipt.setString(7, "BOLT PAYMENT");//remarks
                        MainReceipt.setString(8, String.valueOf("3"));// Pay Method
                        MainReceipt.setDouble(9, BalAmount - Double.parseDouble(ApprovedAmount));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception Ex) {
                        helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetTransaction -- Error 02 - Insertion PaymentReceiptInfo Table)", servletContext, Ex, "BoltPayServices", "GetTransaction", conn);
                        Services.DumException("BoltPayServices", "Get Transaction", request, Ex, getServletContext());
                        Parsehtm Parser = new Parsehtm(request);
                        Parser.SetField("FormName", "RegisteredPatients");
                        Parser.SetField("ActionID", "PayNow");
                        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                        out.flush();
                        out.close();
                        return;
                    }

                    if (InstallmentPlanFound > 0) {
                        try {
                            Query = "Select Id from " + Database + ".InstallmentPlan where Paid = 0 and InvoiceNo = '" + InvoiceNo + "' limit 1";
                            stmt = conn.createStatement();
                            rset = stmt.executeQuery(Query);
                            if (rset.next()) {
                                InstallmentPlanId = rset.getInt(1);
                            }
                            rset.close();
                            stmt.close();
                            if (InstallmentPlanId > 0) {
                                Query = " Update " + Database + ".InstallmentPlan set Paid = 1 where Id = " + InstallmentPlanId + "";
                                stmt = conn.createStatement();
                                stmt.executeUpdate(Query);
                                stmt.close();
                            }
                        } catch (Exception Ex) {
                            helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetTransaction -- Error 03 - Error in Updating Installment plan Table)", servletContext, Ex, "BoltPayServices", "GetTransaction", conn);
                            Services.DumException("BoltPayServices", "Get Transaction", request, Ex, getServletContext());
                            Parsehtm Parser = new Parsehtm(request);
                            Parser.SetField("FormName", "RegisteredPatients");
                            Parser.SetField("ActionID", "PayNow");
                            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                            out.flush();
                            out.close();
                            return;
                        }
                    }
                    out.println("success~" + resptext);
                } else if (resptext.equals("Do not honor") && respCode.equals("100")) {
                    //Error or Declined will treated After This
                    out.println("09~" + resptext);//Transaction Declined.
                    try {
//                        CheckActiveSession(conn, Database, UserId, ClientId);
//                        bm.stop();
                    } catch (Exception e) {
                    }
                }

                //HERE SAVE Complete Response in a Table here.....
            }

//            }
            /*else {
                out.println("jsonvalidatefail");
                try {
                    CheckActiveSession(conn, Database, UserId, ClientId);
                    bm.stop();
                } catch (Exception e) {
                }
            }*/

        } catch (Exception Ex) {
            CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (GetTransaction ** Main Exceptions)", servletContext, Ex, "BoltPayServices", "GetTransaction", conn);
            Services.DumException("BoltPayServices", "Get Transaction", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
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

    private void Disconnect_Stop(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper) throws IOException {
        try {
            CheckActiveSession(conn, Database, UserId, ClientId, helper, request, out, servletContext);
            bm.stop();
            out.println("100");
        } catch (Exception e) {
        }

    }

    /*public static boolean isValidJSON(String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory()
                    .createJsonParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (Exception jpe) {
            jpe.printStackTrace();
        }
        return valid;
    }*/

    private void CheckActiveSession(Connection conn, String Database, String UserId, int ClientId, UtilityHelper helper, HttpServletRequest request, PrintWriter out, ServletContext servletContext) throws IOException {
        int FoundSession = 0;
        Query = "";
        stmt = null;
        rset = null;
        try {
            Query = "Select COUNT(*) from " + Database + ".ActiveSessionBolt where ClientId = " + ClientId
                    + " and ltrim(rtrim(UPPER(CreatedBy))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundSession = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in BoltPayServices ** (CheckActiveSession --Error 1- Getting Data from Active Session Bolt Table)", servletContext, Ex, "BoltPayServices", "CheckActiveSession", conn);
            Services.DumException("BoltPayServices", "Check Active Session", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "PayNow");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
            return;
        }
        if (FoundSession > 0) {
            try {
                Query = "TRUNCATE TABLE " + Database + ".ActiveSessionBolt";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception Ex) {
                helper.SendEmailWithAttachment("Error in BoltPayServices ** (CheckActiveSession -- Error 2- Updating/Truncate Table Active Session Bolt Table)", servletContext, Ex, "BoltPayServices", "CheckActiveSession", conn);
                Services.DumException("BoltPayServices", "Get Details", request, Ex, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatients");
                Parser.SetField("ActionID", "PayNow");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                out.flush();
                out.close();
            }
        }
    }

    private void CheckActiveSessionold(Connection conn, String Database, String UserId, int ClientId) {
        int FoundSession = 0;
        Query = "";
        stmt = null;
        rset = null;
        try {
            Query = "Select COUNT(*) from " + Database + ".ActiveSessionBolt where ClientId = " + ClientId
                    + " and ltrim(rtrim(UPPER(CreatedBy))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundSession = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error 1- Getting Data from Active Session Bolt Table: " + e.getMessage());
        }
        if (FoundSession > 0) {
            try {
                Query = "TRUNCATE TABLE " + Database + ".ActiveSessionBolt";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                System.out.println("Error 2- Updating/Truncate Table Active Session Bolt Table: " + e.getMessage());
            }
        }
    }

    private void establishSession(HttpServletRequest request, Connection conn, ServletContext servletContext, PrintWriter out, String Database, String UserId, int ClientId, UtilityHelper helper){

    }
}
