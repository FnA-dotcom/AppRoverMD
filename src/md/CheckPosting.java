package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import PaymentIntegrations.CardConnectPayment;
import PaymentIntegrations.CheckPayment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class CheckPosting extends HttpServlet {
    Integer ScreenIndex = 15;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;

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
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            String DirectoryName = session.getAttribute("DirectoryName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
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

            if (ActionID.equals("CheckPostInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CheckPostInput", "CheckPostInput", FacilityIndex);
                CheckPostInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("GetPatients")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CheckPostInput", "CheckPostInput", FacilityIndex);
                GetPatients(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("CheckPostSave") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CheckPostSave", "Saving Check Data", FacilityIndex);
                CheckPostSave(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, payments, DirectoryName);
            } else if (ActionID.compareTo("ViewImage") == 0) {
                ViewImage(request, response, out, DirectoryName);
            } else if (ActionID.equals("CCInsurancePayment") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "CCInsurancePayment", "Saving CC Insurance Data", FacilityIndex);
                CCInsurancePayment(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, payments, DirectoryName);

            } else {
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
/*            switch (ActionID) {
                case "CheckPostInput":
                    CheckPostInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetPatients":
                    GetPatients(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
*//*                case "CheckPostSave":
                    CheckPostSave(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex, helper, payments, DirectoryName);
                    break;*//*
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }*/
        } catch (Exception e) {
//            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest)", context, e, "RegisteredPatients", "handleRequest", conn);
            Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
//                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (handleRequest -- SqlException)", context, e, "RegisteredPatients", "handleRequest", conn);
                Services.DumException("RegisteredPatients", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    private void CheckPostInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {

        StringBuffer ProfessionalPayersList = new StringBuffer();
        StringBuffer PatientList = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuilder CCTableList = new StringBuilder();
        try {

            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers " +
                    "where id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757) " +
                    " AND Status != 100 group by PayerId";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            ProfessionalPayersList.append("<option value=''>Select Insurance</option>");
            while (rset.next())
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            rset.close();
            stmt.close();

            //Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers where id not in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955) group by PayerId ";//where PayerName not like '%Texas%'";
            Query = "Select Id, PayerId, LTRIM(rtrim(REPLACE(PayerName,'Servicing States','') )) from oe_2.ProfessionalPayers " +
                    "where PayerName like  '%Texas%' OR PayerName like '%TX%' AND Status != 100";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                ProfessionalPayersList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(3) + "</option>");
            rset.close();
            stmt.close();

            Query = "SELECT Routing, Account, CheckNo, Amount, Description,InsuranceName,CASE " +
                    "WHEN Status=9 THEN 'Not Verified' END, IFNULL(FileName,'0') " +
                    "FROM " + Database + ".CheckInfo where status = 9 ORDER BY CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                //CDRList.append("<td align=left><a href=\"/md/md.CheckPosting?ActionID=ViewImage&FileName=" + rset.getString(8) + "\">  [View] </a></td>\n");

                //CDRList.append("<td ><a href=\"/md/md.CheckPosting?ActionID=ViewImage&SurveyIndex=" + rset.getInt(1) + "&TSMIndex=" + rset.getString(30) + "&FirstPicName=" + rset.getString(31) + "&ShopName=" + rset.getString(27) + " \" target=\"_self\"><i class=\"fa fa-share\"></i>[View]</a></td>\n");
                if (!rset.getString(8).equals("0"))
                    CDRList.append("<td align=left><a href=\"JavaScript:newPopup('/md/md.CheckPosting?ActionID=ViewImage&FileName=" + rset.getString(8) + "'&FolderType=2);\">[" + rset.getString(8) + "]</a></td>\n");// Shop Pic
                else
                    CDRList.append("<td align=left> [ No Image ] </a></td>\n");// Shop Pic
                CDRList.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Query = "SELECT ResponseText,RetRef,DATE_FORMAT(CreatedDate,'%m-%d-%Y'),AccountNo,NameOnCard," +
                    "Remarks,Amount,InsuranceName,IFNULL(FileName,'-') " +
                    "FROM oe_2.CardConnectResponses WHERE TransactionFrom = 1 AND Status = 0 AND refundFlag = 0 AND voidFlag = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CCTableList.append("<tr>\n");
                CCTableList.append("<td align=left>" + rset.getString(1) + "</td>\n");//ResponseText
                CCTableList.append("<td align=left>" + rset.getString(2) + "</td>\n");//RetRef
                CCTableList.append("<td align=left>" + rset.getString(3) + "</td>\n");//CreatedDate
                CCTableList.append("<td align=left>" + rset.getString(4) + "</td>\n");//AccountNo
                CCTableList.append("<td align=left>" + rset.getString(5) + "</td>\n");//NameOnCard
                CCTableList.append("<td align=left>" + rset.getString(6) + "</td>\n");//Remarks
                CCTableList.append("<td align=left>" + rset.getString(7) + "</td>\n");//Amount
                CCTableList.append("<td align=left>" + rset.getString(8) + "</td>\n");//Insurance
                if (!rset.getString(9).equals("-"))
                    CCTableList.append("<td align=left><a href=\"JavaScript:newPopup('/md/md.CheckPosting?ActionID=ViewImage&FileName=" + rset.getString(9) + "'&FolderType=2);\">[" + rset.getString(9) + "]</a></td>\n");// Shop Pic
                else
                    CCTableList.append("<td align=left> [ No Image ] </a></td>\n");// FileName
                CCTableList.append("</tr>\n");
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ProfessionalPayersList", String.valueOf(ProfessionalPayersList));
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("CCTableList", CCTableList.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/CheckPosting.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();
        }
    }

    private void GetPatients(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
            //Query = "Select Id, MRN, IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), DOB, IFNULL(PhNumber ,'') from " + Database + ".PatientReg where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";
            Query = "Select Id, MRN, CONCAT(FirstName,' ', MiddleInitial,' ', LastName), DOB, PhNumber \n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE status = 0 and CONCAT(FirstName,LastName,PhNumber,MRN) like '%" + Patient + "%' ";
//            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber,`Status`\n" +
//                    "FROM PatientReg\n" +
//                    "WHERE status = 0 and CONCAT(MRN,FirstName,LastName,PhNumber) like '%39277709%'";out.println("INSIDE GetPatients TRY ");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            PatientList.append("<option value='' selected disabled>Select Patient</option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(1) + " onclick=\"Getvalue();\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " </option>");
            rset.close();
            stmt.close();

            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void CheckPostSave(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper, Payments payments, String directoryName) throws FileNotFoundException {
        /*String routing = request.getParameter("routing").trim();
        String accountNo = request.getParameter("accountNo").trim();
        //String accountNo = accountNo1;
        String checkNumber = request.getParameter("checkNumber").trim();
        double checkAmount = Double.parseDouble(request.getParameter("checkAmount").trim().replaceAll(",", "").replaceAll("$", ""));
        String checkDescription = request.getParameter("checkDescription").trim();
        String InsuranceName = request.getParameter("InsuranceName").trim();
        double PaidAmount = 0.0D;
        double TotalAmount = 0.0D;
        double BalAmount = 0.0D;
*/
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String routing = "";
        String accountNo = "";
        String checkNumber = "";
        String checkDescription = "";
        String FileName = "";
        String InsuranceName = "";
        String _checkAmount = "";
        double checkAmount = 0.0;
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (key.toUpperCase().endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if (key.startsWith("Routing")) {
                    routing = (String) d.get(key);
                } else if (key.startsWith("AccountNo")) {
                    accountNo = (String) d.get(key);
                } else if (key.startsWith("CheckNumber")) {
                    checkNumber = (String) d.get(key);
                } else if (key.startsWith("checkAmount")) {
                    _checkAmount = (String) d.get(key);
                } else if (key.startsWith("checkDescription")) {
                    checkDescription = (String) d.get(key);
                } else if (key.startsWith("InsuranceName")) {
                    InsuranceName = (String) d.get(key);
                }

                if (FileFound) {
                    FileName = FileName.replaceAll("\\s+", "");
                    File fe = new File("/sftpdrive/AdmissionBundlePdf/CheckImages/" + directoryName + "/" + FileName);
                    if (fe.exists())
                        fe.delete();

                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            routing = routing.substring(4);
            accountNo = accountNo.substring(4);
            checkNumber = checkNumber.substring(4);
            checkDescription = checkDescription.substring(4);
            InsuranceName = InsuranceName.substring(4);
            checkAmount = Double.parseDouble(_checkAmount.substring(4));

//            out.println("routing :" + routing);
//            out.println("accountNo :" + accountNo);
//            out.println("checkNumber :" + checkNumber);
//            out.println("checkAmount :" + checkAmount);
//            out.println("checkDescription :" + checkDescription);
//            out.println("InsuranceName :" + InsuranceName);

        } catch (Exception e2) {
/*            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);*/
            helper.SendEmailWithAttachment("Error in CheckPosting ** (CheckPostSave MES#001)", servletContext, e2, "CheckPosting", "CheckPostSave MES#001", conn);
            Services.DumException("CheckPostSave", "CheckPosting MES#001", request, e2);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "CheckPosting");
            Parser.SetField("ActionID", "CheckPostInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
        String facilityName = helper.getFacilityName(request, conn, servletContext, ClientId);
        try {
            Query = "Select PayerName from " + Database + ".ProfessionalPayers where Id=" + InsuranceName;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                InsuranceName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String accountNo2 = routing + "/" + accountNo;
            CheckPayment checkPayment = new CheckPayment();
            String token = checkPayment.generateToken(ClientId, conn, accountNo2);

            String[] PatientInfo = helper.getPatientInfo(request, conn, servletContext, Database, String.valueOf(310495));
            String FName = PatientInfo[0];
            String LName = PatientInfo[1];
            String Name = FName + " " + LName;
            String PatientAddress = PatientInfo[8];
            String City = PatientInfo[3];
            String State = PatientInfo[4];
            String Country = PatientInfo[6];
            String ZipCode = PatientInfo[5];

            String Response[] = checkPayment.performCheckPaymentAuth(ClientId, conn, token, checkAmount, 310495, Name, PatientAddress, City, State, Country, ZipCode, "WEB");

//            System.out.println("Amount " + Response[1] + "<br> ");
//            System.out.println("CardProc " + Response[2] + "<br> ");
//            System.out.println("Commcard " + Response[3] + "<br> ");
//            System.out.println("ResponseCode " + Response[4] + "<br> ");
//            System.out.println("EntryMode " + Response[5] + "<br> ");
//            System.out.println("Merchant " + Response[6] + "<br> ");
//            System.out.println("ResponseToken " + Response[7] + "<br> ");
//            System.out.println("RespProc " + Response[8] + "<br> ");
//            System.out.println("BinType " + Response[9] + "<br> ");
//            System.out.println("Expiry " + Response[10] + "<br> ");
//            System.out.println("RetRef " + Response[11] + "<br> ");
//            System.out.println("RespStat " + Response[12] + "<br>");
//            System.out.println("Account " + Response[13] + "<br>");

            if (Response[0].equals("Approved") || Response[0].equals("APPROVED") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {
                String ResponseText = Response[0];
                String Amount = Response[1];
                String CardProc = Response[2];
                String Commcard = Response[3];
                String ResponseCode = Response[4];
                String EntryMode = Response[5];
                String Merchant = Response[6];
                String ResponseToken = Response[7];
                String RespProc = Response[8];
                String BinType = Response[9];
                String Expiry = Response[10];
                String RetRef = Response[11];
                String RespStat = Response[12];
                String Account = Response[13];

                String UserIP = helper.getClientIp(request);
/*                payments.insertCheckInfo(request, conn, servletContext, Database, routing,
                        accountNo, checkNumber, checkDescription, checkAmount, 5, "CheckPostSave",
                        UserId, UserIP, ClientId, RetRef, InsuranceName, FileName);*/
                payments.insertCheckInfo(request, conn, servletContext, Database, 0, "", 0, routing,
                        accountNo, checkNumber, checkDescription, checkAmount, 5, "CheckPostSave",
                        UserId, UserIP, ClientId, RetRef, Response, FileName, InsuranceName, 9);

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "CheckPosting");
                Parser.SetField("ActionID", "CheckPostInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMsg.html");
            } else if (Response.length > 1) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "CheckPosting");
                Parser.SetField("ActionID", "CheckPostInput");
                Parser.SetField("Message", Response[0]);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentExp.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "CheckPosting");
                Parser.SetField("ActionID", "CheckPostInput");
                Parser.SetField("Message", "Something went wrong. Please contact System Administrator.");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
            }

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CheckPostSave ^^ (Occurred At : " + facilityName + ") ** (CheckPostSave)", servletContext, e, "CheckPosting", "CheckPostSave", conn);
            Services.DumException("CheckPosting", "CheckPostSave", request, e, getServletContext());

        }
    }

    private Dictionary doUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf('=');
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            Dictionary<Object, Object> fields = new Hashtable<>();
            ServletInputStream in = request.getInputStream();
            int i;
            for (i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {
                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            fields.put(filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                            continue;
                        }
                        if (token.startsWith(" filename")) {
                            filename = tokenizer.nextToken();
                            StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                            filename = ftokenizer.nextToken();
                            while (ftokenizer.hasMoreTokens())
                                filename = ftokenizer.nextToken();
                            state = 1;
                            break;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = String.valueOf(String.valueOf(value)) + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    private void ViewImage(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String directoryName) {
        String FileName = request.getParameter("FileName");
        int folderType = Integer.parseInt(request.getParameter("FolderType"));
        try {
            String path = "";
            if (folderType == 1)
                path = "/sftpdrive/AdmissionBundlePdf/CheckImages/" + directoryName + "/" + FileName;
            else if (folderType == 2)
                path = "/sftpdrive/AdmissionBundlePdf/CCImages/" + directoryName + "/" + FileName;

            FileInputStream fin = new FileInputStream(new File(path));
            byte content[] = new byte[fin.available()];
            fin.read(content);
            fin.close();

            OutputStream os = response.getOutputStream();
            if (FileName.indexOf("jpg") > 0) {
                response.setContentType("image/jpg");
            } else if (FileName.indexOf("jpeg") > 0) {
                response.setContentType("image/jpeg");
            } else if (FileName.indexOf("png") > 0) {
                response.setContentType("image/png");
            } else if (FileName.indexOf("pdf") > 0) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + FileName + "\"");
            }
/*             else if (FileName.indexOf("html") > 0) {
                res.setContentType("text/html");
            } else if (FileName.indexOf("zip") > 0) {
                res.setContentType("application/zip");
                res.setHeader("Content-Disposition", "attachment; filename=\"" + pictureName + "\"");
            }*/
            //response.setContentType("image/jpeg");
            os.write(content);
            os.flush();
            os.close();
        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            out.println(e.getMessage() + " <br> " + str);
            out.flush();
            out.close();
        }
    }

    private void CCInsurancePayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int facilityIndex, UtilityHelper helper, Payments payments, String directoryName) throws FileNotFoundException {
        boolean FileFound = false;
        byte[] Data = null;
        String ResponseType = "";
        String key = "";
        String CCExpiry = "";
        String CCCVC = "";
        String CCnameCard = "";
        String CCDescription = "";
        String mytoken = "";
        String FileName = "";
        String CCInsurance = "";
        String _CCAmount = "";
        double CCAmount = 0.0;
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (key.toUpperCase().endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if (key.startsWith("CCAmount")) {
                    _CCAmount = (String) d.get(key);
                } else if (key.startsWith("CCInsurance")) {
                    CCInsurance = (String) d.get(key);
                } else if (key.startsWith("CCExpiry")) {
                    CCExpiry = (String) d.get(key);
                } else if (key.startsWith("CCCVC")) {
                    CCCVC = (String) d.get(key);
                } else if (key.startsWith("CCnameCard")) {
                    CCnameCard = (String) d.get(key);
                } else if (key.startsWith("CCDescription")) {
                    CCDescription = (String) d.get(key);
                } else if (key.startsWith("mytoken")) {
                    mytoken = (String) d.get(key);
                }

                if (FileFound) {
                    FileName = FileName.replaceAll("\\s+", "");
                    File fe = new File("/sftpdrive/AdmissionBundlePdf/CCImages/" + directoryName + "/" + FileName);
                    if (fe.exists())
                        fe.delete();

                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            CCDescription = CCDescription.substring(4);
            mytoken = mytoken.substring(4);
            CCInsurance = CCInsurance.substring(4);
            CCExpiry = CCExpiry.substring(4);
            CCCVC = CCCVC.substring(4);
            CCnameCard = CCnameCard.substring(4);
            CCAmount = Double.parseDouble(_CCAmount.substring(4));

/*            out.println("CCDescription :" + CCDescription);
            out.println("mytoken :" + mytoken);
            out.println("CCInsurance :" + CCInsurance);
            out.println("CCExpiry :" + CCExpiry);
            out.println("CCCVC :" + CCCVC);
            out.println("CCnameCard :" + CCnameCard);
            out.println("CCAmount :" + CCAmount);*/

        } catch (Exception e2) {
/*            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);*/
            helper.SendEmailWithAttachment("Error in CCInsurancePosting ** (CCInsurancePayment MES#001)", servletContext, e2, "CheckPosting", "CCInsurancePayment MES#002", conn);
            Services.DumException("CheckPostSave", "CheckPosting MES#002", request, e2);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "CheckPosting");
            Parser.SetField("ActionID", "CheckPostInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {
            int checkCredentials = payments.checkCCCredentials(request, conn, facilityIndex, servletContext);
            if (checkCredentials == 0) {
                out.println("11~No account found. Please contact System Administrator.");
                return;
            }
            String printDate = "";
            String printTime = "";
            printDate = helper.printDateTime(request, conn, servletContext)[0];
            printTime = helper.printDateTime(request, conn, servletContext)[1];

            System.out.println("GOING FOR PAYMENT....");
            CardConnectPayment cardConnectPayment = new CardConnectPayment();
            String[] Response = cardConnectPayment.InquireTransaction(facilityIndex, conn, mytoken, CCExpiry, CCAmount, CCnameCard, CCCVC);
/*            System.out.println("ResponseText " + Response[0] + "<br> ");
            System.out.println("cvvresp " + Response[1] + "<br> ");
            System.out.println("respcode " + Response[2] + "<br> ");
            System.out.println("entrymode " + Response[3] + "<br> ");
            System.out.println("authcode " + Response[4] + "<br> ");
            System.out.println("respproc " + Response[5] + "<br> ");
            System.out.println("respstat " + Response[6] + "<br> ");
            System.out.println("retref " + Response[7] + "<br> ");
            System.out.println("expiry " + Response[8] + "<br> ");
            System.out.println("AVS " + Response[9] + "<br> ");
            System.out.println("Receipt " + Response[10] + "<br> ");
            System.out.println("BinType " + Response[11] + "<br> ");
            System.out.println("Amount " + Response[12] + "<br>");
            System.out.println("AccountNo " + Response[13] + "<br>");
            System.out.println("orderId " + Response[14] + "<br>");
            System.out.println("commCard " + Response[15] + "<br>");*/

            JSONParser parser = new JSONParser();
            Object obj = parser.parse("[" + Response[10] + "]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String dateTime = (String) obj2.get("dateTime");
            String dba = (String) obj2.get("dba");
            String address2 = (String) obj2.get("address2");
            String phone = (String) obj2.get("phone");
            String footer = (String) obj2.get("footer");
            String nameOnCard = (String) obj2.get("nameOnCard");
            String address1 = (String) obj2.get("address1");
            String orderNote = (String) obj2.get("orderNote");
            String header = (String) obj2.get("header");
            String items = (String) obj2.get("items");


            String FullName = "";
            String Address = "";
            String Phone = "";
            String receipt = "";
            FullName = helper.receiptClientData(request, conn, servletContext, facilityIndex)[0];
            Address = helper.receiptClientData(request, conn, servletContext, facilityIndex)[1];
            Phone = helper.receiptClientData(request, conn, servletContext, facilityIndex)[2];

            String UserIP = helper.getClientIp(request);
            String CurrDate = helper.getCurrDate(request, conn);
            if (Response[0].equals("Approval") || Response[0].equals("APPROVAL") ||
                    Response[0].equals("Success") || Response[0].equals("SUCCESS")) {

                receipt = "<div id='printThis'><div id='mystyle' style='width: 377.68px;border: 1px; border-style: solid; border-color: black;'><div class='reciept' style='margin-top:20px;text-align: center;'><p style='margin:0px;'>" + FullName + "</p><p style='margin:0px;'>" + Address + "</p><p style='margin:0px;'>" + Phone + "</p></div><div class='reciept' style='margin-left: 5px; text-align:left !important'><p style='margin:0px;'>Ref #: " + Response[7] + "</p><p style='margin:0px;'>Status: " + Response[0] + "</p><p style='margin:0px;'>Auth #: " + Response[4] + "</p><p style='margin:0px;'>MID: " + Response[13] + "</p></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>" + printDate + "</span> <span style='margin-left: 166px'>" + printTime + "</span></div><div class='reciept' style='text-align:left !important'><span style='margin-left: 5px'>Amount</span> <span style='margin-left: 199px'>$" + CCAmount + "</span></div><div class='reciept' style='margin-left: 5px;text-align:left !important'><p style='margin:0px;'>Method: Card</p><p style='margin:0px;'>" + CCnameCard + "</p></div><div class='reciept' style='text-align: center;'>Approved</div><div class='reciept' style='margin-bottom: 15px; text-align: center;'>Thank you. Please come again</div> <div class='reciept' style='text-align: right;font-size:12px;'>Printed By Rover</div>  </div></div>";

                ResponseType = "SUCCESS";
                payments.insertionCardConnectResponses(request, conn, servletContext, Database, "No Invoice", "No MRN", Response[0], Response[1], Response[2], Response[3], Response[4], Response[5], Response[6], Response[7], Response[8], Response[9], CurrDate, 0, facilityIndex, mytoken, CurrDate, CCCVC, Response[11], Response[10], ResponseType, CCDescription, CCAmount, Response[13], Response[14], Response[15], UserIP, "CheckPosting--CC", CCnameCard, 0, "1", CCInsurance, receipt, FileName, UserId);
                //out.println(receipt);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Your Payment has been credited!!");
                Parser.SetField("FormName", "CheckPosting");
                Parser.SetField("ActionID", "CheckPostInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
/*                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("printReceipt", receipt);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/InsuranceMessage.html");*/
            } else {
                ResponseType = "ERROR";
                //payments.insertionCardConnectResponses(request, conn, servletContext, Database, "No Invoice", "No MRN", Response[0], Response[1], Response[2], Response[3], Response[4], Response[5], Response[6], Response[7], Response[8], Response[9], CurrDate, 0, facilityIndex, mytoken, CurrDate, CCCVC, Response[11], Response[10], ResponseType, CCDescription, CCAmount, Response[13], Response[14], Response[15], UserIP, "CheckPosting--CC", CCnameCard, 0, "1", CCInsurance, "No Slip", "No File");
                payments.insertionCardConnectResponses(request, conn, servletContext, Database, "No Invoice", "No MRN", Response[0], Response[1] == null ? "" : Response[1], Response[2] == null ? "" : Response[2], Response[3] == null ? "" : Response[3], Response[4] == null ? "" : Response[4], Response[5] == null ? "" : Response[5], Response[6] == null ? "" : Response[6], Response[7] == null ? "" : Response[7], Response[8] == null ? "" : Response[8], Response[9] == null ? "" : Response[9], CurrDate, 0, facilityIndex, mytoken, CurrDate, CCCVC, Response[11] == null ? "" : Response[11], Response[10] == null ? "" : Response[10], ResponseType, CCDescription, CCAmount, Response[13] == null ? "" : Response[13], Response[14] == null ? "" : Response[14], Response[15] == null ? "" : Response[15], UserIP, "cardConnectPaymentSave", CCnameCard, 0, "1", CCInsurance, "No Slip", "No File", UserId);

                //out.println(Response[0]);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", Response[0]);
                Parser.SetField("FormName", "CheckPosting");
                Parser.SetField("ActionID", "CheckPostInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/PaymentMessge.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CheckPostSave ^^ (Occurred At : " + facilityName + ") ** (CheckPostSave)", servletContext, e, "CheckPosting", "CheckPostSave", conn);
            Services.DumException("CheckPosting", "CheckPostSave", request, e, getServletContext());

        }
    }
}
