

package md;

import DAL.TwilioSMSConfiguration;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;
import java.util.*;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class SendSMSBulk extends HttpServlet {
/*    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = ""; */

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
        String ActionID = "";
        Connection conn = null;
        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        int UserIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();
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
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

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
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "SendSMSBulk", "GetInput Funtion", FacilityIndex);
                    this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, smsConfiguration, UserIndex);
                    break;

                case "UploadFileOnly":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Uplaod File Only", "Uplaod Bulk SMS File", FacilityIndex);
                    this.UploadFileOnly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, response, UserIndex);
                    break;

                case "SendSMS":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View Date on File", "ReView Bulk SMS Excel File", FacilityIndex);
                    this.SendSMS(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, UserIndex, smsConfiguration);
                    break;

            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest)", context, e, "PatientVisit", "handleRequest", conn);
            Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest -- SqlException)", context, e, "PatientVisit", "handleRequest", conn);
                Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    private void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, TwilioSMSConfiguration smsConfiguration, int userIndex) throws FileNotFoundException {
        StringBuilder FacilityList;
        try {
/*            Query = "Select Id, IFNULL(name,'') from oe.clients where Id not in (23,32,33) and status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            FacilityList.append("<option value=\"\" > Select Facility </option>");
            while (rset.next()) {
                FacilityList.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();*/
            FacilityList = smsConfiguration.getFacilityList(request, conn, servletContext, userIndex);

/*            Query = "";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()){

            }
            rset.close();
            stmt.close();*/

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("FacilityList", String.valueOf(FacilityList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SendSMSBulk.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in GetInput ** (SendSMSBulk^^ MES#001)", servletContext, e, "SendSMSBulk", "GetInput", conn);
            Services.DumException("SendSMSBulk", "GetInput", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void UploadFileOnly(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, HttpServletResponse response, int UserIndex) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Facility = "";
        String FileName = "";
        try {

            String key = "";

            int dbFound = 0;
            boolean FileFound;
            byte Data[];
            String[] FacIdx;

            try {
                FileFound = false;
                Data = null;
                Query = FileName = "";
                Dictionary d = doUpload(request, response, out);
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (key.endsWith(".xls") || key.endsWith(".xlsx")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                    if (key.startsWith("Facility")) {
                        Facility = (String) d.get(key);
                    }
                }
                Facility = Facility.substring(4);

                FacIdx = Facility.split("\\~");


                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }

                File f = new File("/sftpdrive/opt/BulkSMSFiles/" + FileName);
                if (f.exists()) {
                    f.delete();
                }
                FileOutputStream fout = new FileOutputStream(f);
                fout.write(Data);
                fout.flush();
                fout.close();
            } catch (Exception e) {
                out.println("EXCEPTION : " + e.getMessage() + "----" + FileName);
                return;
            }

            StringBuilder SMSList = new StringBuilder();
            int i = 0;
            int ErrorCode = 0;
            String FacilityName = "";
            String UserName = "";
            String MRN = "0";
            String PatientName = "";
            String Priority = "";
            String PhNumber = "";
            String TempType = "";
            String Template = "";
            DataFormatter formatter = new DataFormatter(Locale.US);
            FileInputStream fileIn = null;
            fileIn = new FileInputStream("/sftpdrive/opt/BulkSMSFiles/" + FileName);

            Query = "Select IFNULL(name,''), IFNULL(dbname ,'') " +
                    "FROM oe.clients WHERE Id=" + FacIdx[0] + "";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println(Query);
            if (rset.next()) {
                FacilityName = rset.getString(1);
                Database = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = "Select IFNULL(UserName,'') FROM oe.sysusers WHERE indexptr=" + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                UserName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String AdvocatePhNumber = "";
            Query = "Select IFNULL(AdvocatePhNumber,'') from oe.AdvocateSMSNumber " +
                    "where AdvocateIdx = " + UserIndex + " AND FacilityIdx=" + FacIdx[0];
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                AdvocatePhNumber = rset.getString(1);
            }
            rset.close();
            stmt.close();

            int _bIndex_ = 0;
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
//                System.out.println("Cell Type " + row.getCell(0).getCellType() + "<br>");
/*                if (row.getCell(0) != null) {
                    //Integer
                    if (row.getCell(0).getCellType() == 1)
                        MRN = (int) row.getCell(0).getNumericCellValue();
                    else
                        MRN = Integer.parseInt(String.valueOf(row.getCell(0)));
                } else
                    MRN = 0;*/

                //System.out.println(MRN);


                if (row.getCell(0) != null) {
                    MRN = formatter.formatCellValue(row.getCell(0));
                } else {
                    MRN = "0";
                }

//                if (MRN.equals("0") || MRN.equals(""))
//                    continue;

                PatientName = formatter.formatCellValue(row.getCell(1));
                Priority = formatter.formatCellValue(row.getCell(2));
                PhNumber = formatter.formatCellValue(row.getCell(3));
                TempType = formatter.formatCellValue(row.getCell(4));
/*                _bIndex_ = row.getCell(0).getCellType();
                out.println("_bIndex_:--- " + _bIndex_ + "| <br>");
                if (_bIndex_ == 1) {
                    Query = row.getCell(0).getStringCellValue();
                    out.println("Query:--- " + Query + "| <br> ");
                    if ((Query.length() <= 0) || (Query.equals("MRN"))) {
                        continue;
                    }
                    MRN = Integer.parseInt(Query);
                } else {
                    MRN = (int) row.getCell(0).getNumericCellValue();
                }*/
/*                if (!Facility.equals("999"))
                    MRN = formatter.formatCellValue(row.getCell(0));
                else
                    MRN = "";
*/

/*                 ");
                out.println("PatientName:--- "+PatientName+"|");
                out.println("Priority:--- "+Priority+"|");
                out.println("PhNumber:--- "+PhNumber+"|");
               */
//                out.println("<br>");
//                System.out.println("TempType:--- "+TempType+"|");

                if ((PatientName.equals("") || PatientName.equals(null) || PatientName.isEmpty()) &&
                        (PhNumber.equals("") || PhNumber.equals(null) || PhNumber.isEmpty()))
                    continue;

                if (TempType.equals("") || TempType.equals(null) || TempType.isEmpty()) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "Please enter Template Type in the Excel Sheet!");
                    Parser.SetField("FormName", "SendSMSBulk");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
                    return;
                }

                switch (Priority) {
                    case "1":
                        Priority = "LOW";
                        break;
                    case "2":
                        Priority = "MEDIUM";
                        break;
                    case "3":
                        Priority = "HIGH";
                        break;
                }


                Query = "Select IFNULL(Body ,'') FROM oe.SmsTemplates WHERE Id = " + TempType;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    Template = rset.getString(1);
                else
                    Template = "999";
                rset.close();
                stmt.close();

                if (!Template.equals("999")) {
                    Template = Template.replaceAll("\\bName\\b", PatientName);
                    Template = Template.replaceAll("\\bUserName\\b", UserName);
                    Template = Template.replaceAll("\\bClientName\\b", FacilityName);
                    Template = Template.replaceAll("\\bAdvocatePhNumber\\b", AdvocatePhNumber);
                }

                SMSList.append("<tr>");
                SMSList.append("<td align=left>" + MRN + "</td>");
                SMSList.append("<td align=left>" + PatientName + "</td>");
                SMSList.append("<td align=left>" + Priority + "</td>");
                SMSList.append("<td align=left>" + PhNumber + "</td>");
                SMSList.append("<td align=left>" + Template + "</td>");
                SMSList.append("</tr>");


            }

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("SMSList", String.valueOf(SMSList));
            Parser.SetField("ErrorCode", String.valueOf(ErrorCode));
            Parser.SetField("FileName", String.valueOf(FileName));
            Parser.SetField("Facility", String.valueOf(Facility));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ViewDataSendSMSBulk.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in UploadFileOnly ** (FacilityName: " + Facility + "^^ MES#002^^ Perform BY: " + UserId + " File Name : " + FileName + ")", servletContext, e, "SendSMSBulk", "UploadFileOnly", conn);
            Services.DumException("SendSMSBulk", "UploadFileOnly", request, e);
/*            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println("ERROR " + str);
            System.out.println("ERROR " + e);*/
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(e.getMessage());

            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void SendSMS(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, int UserIndex, TwilioSMSConfiguration smsConfiguration) throws FileNotFoundException {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";

            String FileName = "";
            String Facility = "";
            String FacilityName = "";
            String UserName = "";
            String MRN = "";
            String PatientName = "";
            String Priority = "";
            String PhNumber = "";
            String TempType = "";
            String Template = "";
            String[] result;
            int ClientStatus = 0;
            String[] FacIdx;
            int i = 0;
            FileName = request.getParameter("FileName").trim();
            Facility = request.getParameter("Facility").trim();

            FacIdx = Facility.split("\\~");

            DataFormatter formatter = new DataFormatter(Locale.US);
            FileInputStream fileIn = null;
            fileIn = new FileInputStream("/sftpdrive/opt/BulkSMSFiles/" + FileName);
//            System.out.println(FacIdx[0]);
            Query = "Select IFNULL(name,''), IFNULL(dbname ,''), status " +
                    "FROM oe.clients WHERE Id='" + FacIdx[0] + "'";
//            System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FacilityName = rset.getString(1);
                Database = rset.getString(2);
                ClientStatus = rset.getInt(3);
            }
            rset.close();
            stmt.close();

            System.out.println(FacilityName);

            if (ClientStatus == 1) {
                Database = "oe";
            }

            Query = "Select IFNULL(UserName,'') FROM oe.sysusers WHERE indexptr=" + UserIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                UserName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String AdvocatePhNumber = "";
            Query = "Select IFNULL(AdvocatePhNumber,'') from oe.AdvocateSMSNumber " +
                    "where AdvocateIdx = " + UserIndex + " AND FacilityIdx=" + FacIdx[0];
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                AdvocatePhNumber = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                //MRN = formatter.formatCellValue(row.getCell(0));
                if (row.getCell(0) != null) {
                    MRN = formatter.formatCellValue(row.getCell(0));
                } else
                    MRN = "0";
                PatientName = formatter.formatCellValue(row.getCell(1));
                Priority = formatter.formatCellValue(row.getCell(2));
                PhNumber = formatter.formatCellValue(row.getCell(3));
                TempType = formatter.formatCellValue(row.getCell(4));

                if ((PatientName.equals("") || PatientName.equals(null) || PatientName.isEmpty()) &&
                        (PhNumber.equals("") || PhNumber.equals(null) || PhNumber.isEmpty()))
                    break;

                if (PhNumber.contains("-")) {
                    PhNumber = PhNumber.replace("-", "");
                }

//                Query = "Select IFNULL(Body ,'') FROM oe.SmsTemplates WHERE Id='" + TempType + "'";
                Query = "Select IFNULL(Body ,'') FROM oe.SmsTemplates WHERE id = " + TempType;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    Template = rset.getString(1);
                else
                    Template = "999";
                rset.close();
                stmt.close();
//                System.out.println("FIRST TEMPLATE " + Template);
                if (!Template.equals("999")) {
                    Template = Template.replaceAll("\\bName\\b", PatientName);
                    Template = Template.replaceAll("\\bUserName\\b", UserName);
                    Template = Template.replaceAll("\\bClientName\\b", FacilityName);
                    Template = Template.replaceAll("\\bAdvocatePhNumber\\b", AdvocatePhNumber);
                }
//                System.out.println("SECOND TEMPLATE " + Template);

                try {
/*                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "Insert into " + Database + ".SMS_Info (FacilityIdx, SentBy, SentAt, Priority, PatientName, PatientMRN, PatientPhNumber, Status, Sms) \n "
                                    + "values (?,?,NOW(),?,?,?,?,0,?) ");
                    MainReceipt.setInt(1, Integer.parseInt(Facility));
                    MainReceipt.setString(2, String.valueOf(UserIndex));
                    MainReceipt.setInt(3, Integer.parseInt(Priority));
                    MainReceipt.setString(4, PatientName);
                    MainReceipt.setString(5, MRN);
                    MainReceipt.setString(6, PhNumber);
                    MainReceipt.setString(7, Template);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();*/

                    insertionSMSInfo(Database, Integer.parseInt(FacIdx[0]), UserIndex, Integer.parseInt(Priority), PatientName, MRN, PhNumber, Template, "Bulk Upload", 0, conn);
                    int maxId = smsConfiguration.getMaxSMSIndex(request, conn, servletContext, Integer.parseInt(FacIdx[0]), Database);

                    if (Priority.equals("3")) {
                        result = smsConfiguration.sendTwilioMessages(request, conn, servletContext, Template, Integer.parseInt(FacIdx[0]), PhNumber, UserIndex, Database, MRN, maxId);
                        if (result[0].equals("Success")) {
                            smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, result[1], Database, "0");
                        } else {
                            smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, result[1], Database, "999");
                        }
                    } else {
                        //System.out.println("Here in ELSE");
                        smsConfiguration.updateSMSInfoTable(request, conn, servletContext, maxId, "", Database, "0");
                    }

                } catch (Exception e) {
                    System.out.println("Error in insertion data in SMS Table:" + e.getMessage());
                    //out.println(e.getMessage());
//                    String str = "";
//                    for (i = 0; i < e.getStackTrace().length; ++i) {
//                        str = str + e.getStackTrace()[i] + "<br>";
//                    }
//                    out.println(str);
//                    out.flush();
//                    out.close();
                }

            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Data has been Uploaded Successfully.");
            Parser.SetField("MRN", "");
            Parser.SetField("FormName", "SendSMSBulk");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("ClientIndex", "");
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Send SMS ** (SendSMSBulk^^ MES#001)", servletContext, e, "SendSMSBulk", "SendSMS", conn);
            Services.DumException("SendSMSBulk", "GetInput", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//            out.println(e.getMessage());
//            String str = "";
//            for (int i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
            out.flush();
            out.close();
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
                    value = value + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    private void insertionSMSInfo(String Database, int facilityIndex, int advocateIdx, int Priority, String PtName, String PtMRN, String PtPhNumber, String Sms, String Username, int status, Connection conn) {
        PreparedStatement MainReceipt = null;
        try {
            MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".SMS_Info (FacilityIdx,SentBy,SentAt ,Priority,PatientName," +
                            "PatientMRN,PatientPhNumber,Status,Sms,Username) " +
                            " VALUES (?,?,now(),?,?,?,?,?,?,?) ");
            MainReceipt.setInt(1, facilityIndex);
            MainReceipt.setInt(2, advocateIdx);
            MainReceipt.setInt(3, Priority);
            MainReceipt.setString(4, PtName);
            MainReceipt.setString(5, PtMRN);
            MainReceipt.setString(6, PtPhNumber);
            MainReceipt.setInt(7, status);
            MainReceipt.setString(8, Sms);
            MainReceipt.setString(9, Username);
            MainReceipt.executeUpdate();
            MainReceipt.close();
        } catch (Exception ex) {
            System.out.println("EXCEPTION in Saving Record insertionSMSInfo " + ex.getMessage());
        }
    }

}
