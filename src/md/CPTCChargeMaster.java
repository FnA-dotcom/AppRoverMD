package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPTCChargeMaster extends HttpServlet {


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
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();

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
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
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
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Showing FrontEnd", "Input the Data", FacilityIndex);
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("InsertData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insert CPTC and Price", "Click on Insert button Option", FacilityIndex);
                InsertData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("FacilityData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insert CPTC and Price", "Click on Insert button Option", FacilityIndex);
                FacilityData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("UploadFileOnly")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Uplaod File Only", "Uplaod Bulk SMS File", FacilityIndex);
                UploadFileOnly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, response, UserIndex);
            } else if (ActionID.equals("SendFile")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insert CPTC and Price", "Click on Insert button Option", FacilityIndex);
                SendFile(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("UpdateData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insert CPTC and Price", "Click on Insert button Option", FacilityIndex);
                UpdateData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("EditData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Insert CPTC and Price", "Click on Insert button Option", FacilityIndex);
                EditData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else {
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            //System.out.println("in the catch exception of handle request Function ");
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (handleRequest -- SqlException)", context, e, "CPTCChargeMaster", "handleRequest", conn);
                Services.DumException("CPTCChargeMaster", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer CPTCList = new StringBuffer();
        StringBuffer CPTCDataList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "Select Id, ifnull(CPTC,''),ifnull(Inst_Price,''),ifnull(Prof_Price,'') from " + Database + ".CPTCChargeMaster  where Status = 0 order by CreatedDate DESC ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {

                CPTCDataList.append("<tr>");
                CPTCDataList.append("<td align=left>" + SNo + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(4) + "</td>\n");

                CPTCDataList.append("<td align=left><i data-toggle=\"modal\" data-target=\"#exampleModalCenter\"  id=\"edit\" class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                CPTCDataList.append("</tr>");
                SNo++;
            }
            //System.out.println("hee is the Query1----->" + Query);
            rset.close();
            stmt.close();


            Query = "Select dbname,name from oe.clients where status = 0 ";
            //System.out.println("hee is the Query2------->" + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            CPTCList.append("<option value='0' selected>Please Select Facility</option>");
            while (rset.next()) {

                CPTCList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            //System.out.println("hee is the Query2------->" + Query);
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CPTCList", String.valueOf(CPTCList));
            Parser.SetField("CPTCDataList", String.valueOf(CPTCDataList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/CPTCChargeMaster.html");

        } catch (Exception e) {
            //System.out.println("in the catch exception of GetInput Function ");
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
        }
    }

    void InsertData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer CPTCList = new StringBuffer();

        String CPT = request.getParameter("CPTC").trim();

        String Inst_Price = request.getParameter("Inst_Price").trim();
        String Prof_Price = request.getParameter("Prof_Price").trim();

//    String DBName = request.getParameter("FacilityName").trim();


        UserId = request.getParameter("UserId").trim();

        try {

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "INSERT INTO " + Database + ".CPTCChargeMaster (CPTC,Inst_Price,Prof_Price,CreatedBy,Status,CreatedDate) VALUES (?,?,?,?,0,now())");
            MainReceipt.setString(1, CPT);
            MainReceipt.setString(2, Inst_Price);
            MainReceipt.setString(3, Prof_Price);
            MainReceipt.setString(4, UserId);

            MainReceipt.executeUpdate();
            MainReceipt.close();
            //System.out.println("hee is the querry" + MainReceipt);
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (ShowReport)", servletContext, e, "CPTCChargeMaster", "ShowReport", conn);
            Services.DumException("ShowReport", "CPTCChargeMaster ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "InsertData");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

        GetInput(request, out, conn, servletContext, UserId, Database, ClientId, helper);


    }


    private void UploadFileOnly(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, HttpServletResponse response, int UserIndex) throws FileNotFoundException {
        //System.out.println("inside function----->");
        Statement stmt = null;
        ResultSet rset = null;
        try {
            String Facility = "";
            String key = "";
            String FileName = "";
            int dbFound = 0;
            boolean FileFound;
            int EmptyFlag = 0;
            int PatternFlag = 0;
            String regex = "[0-9., ]+";
            byte Data[];
            String[] FacIdx;
            String Query = "";
            String CPT = "";
            String Inst_Price = "";
            String Prof_Price = "";
            Pattern p1 = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
            Pattern p2 = Pattern.compile("[0-9.]+", Pattern.CASE_INSENSITIVE);



            try {
                FileFound = false;
                Data = null;
                Query = FileName = "";
                Dictionary d = doUpload(request, response, out);
                //System.out.println("Dictionary----->");
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    //System.out.println("Key----->" + key);
                    if (key.endsWith(".xls") || key.endsWith(".xlsx")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                        //System.out.println("FileName----->" + FileName);
                    }


                }

//                Facility = Facility.substring(4);
//
//                FacIdx = Facility.split("\\~");


                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }

                File f = new File("/sftpdrive/opt/" + FileName);
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

            //System.out.println("FileName is----->" + FileName);
//            //System.out.println("Facility is----->"+Facility);
//            //System.out.println("FacIdx is----->"+FacIdx);
            //System.out.println("UserIndex is----->" + UserIndex);
            //System.out.println("UserId is----->" + UserId);

            int i;
            StringBuilder SMSList = new StringBuilder();


            DataFormatter formatter = new DataFormatter(Locale.US);
            FileInputStream fileIn = null;
            fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);


            int _bIndex_ = 0;
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 0; i <= totalrows; i++) {


                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                CPT = formatter.formatCellValue(row.getCell(0));
                Inst_Price = formatter.formatCellValue(row.getCell(1));
                Prof_Price = formatter.formatCellValue(row.getCell(2));

                if (CPT == "" || CPT == null) {
                    EmptyFlag = 1;

                }
                if (Prof_Price == "" || Prof_Price == null) {
                    EmptyFlag = 1;

                }
                if (Inst_Price == "" || Inst_Price == null) {
                    EmptyFlag = 1;

                }
                Matcher cptmatcher = p1.matcher(CPT);
                boolean CptSpecialCharacterFound = cptmatcher.find();
                //System.out.println("CptSpecialCharacterFound" + cptmatcher);
                if (CptSpecialCharacterFound == false) {
                    PatternFlag = 1;
                }
                Matcher Inst_PriceMatcher = p2.matcher(Inst_Price);
                boolean Inst_PriceSpecialCharacterFound = Inst_PriceMatcher.find();
                if (Inst_PriceSpecialCharacterFound == false) {
                    //System.out.println("PriceSpecialCharacterFound" + Inst_PriceMatcher);
                    PatternFlag = 1;
                }

                Matcher Prof_PricePriceMatcher = p2.matcher(Prof_Price);
                boolean Prof_PriceSpecialCharacterFound = Prof_PricePriceMatcher.find();
                if (Prof_PriceSpecialCharacterFound == false) {
                    //System.out.println("PriceSpecialCharacterFound" + Prof_PricePriceMatcher);
                    PatternFlag = 1;
                }
                SMSList.append("<tr>");
                SMSList.append("<td align=left>" + CPT + "</td>");
                SMSList.append("<td align=left>" + Inst_Price + "</td>");
                SMSList.append("<td align=left>" + Prof_Price + "</td>");
                SMSList.append("</tr>");
//CPTList.push(CPT);
//PriceList.push(Price);


            }

            final Parsehtm Parser = new Parsehtm(request);

            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("EmptyFlag", String.valueOf(EmptyFlag));
            Parser.SetField("PatternFlag", String.valueOf(PatternFlag));
            Parser.SetField("FileName", String.valueOf(FileName));
//            Parser.SetField("Facility", String.valueOf(Facility));
            Parser.SetField("SMSList", String.valueOf(SMSList));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/sendCPTFile.html");

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (ShowReport)", servletContext, e, "CPTCChargeMaster", "ShowReport", conn);
            Services.DumException("ShowReport", "CPTCChargeMaster ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "UploadFileOnly");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }


    }

    void SendFile(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws InvalidFormatException, IOException {

        //System.out.println("in function send file ");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String CPTValue = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer CPTCList = new StringBuffer();

//        //System.out.println("her is CPT"+request.getParameter("CPT"));
//        String CPT[] = {request.getParameter("CPT").trim()};
        String CPT = null;//{request.getParameter("CPT").trim()};

        String FileName = request.getParameter("FileName").trim();
//        String CPT = request.getParameter("CPT").trim();
        String Inst_Price = null;
        String Prof_Price = null;

        UserId = request.getParameter("UserId").trim();

        DataFormatter formatter = new DataFormatter(Locale.US);
        FileInputStream fileIn = null;
        fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);


        int _bIndex_ = 0;
        Workbook workbook = WorkbookFactory.create(fileIn);
        Sheet sheet = workbook.getSheetAt(0);
        int totalrows = sheet.getLastRowNum();

            try {
            for (int i = 0; i <= totalrows; i++) {

                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                CPT = formatter.formatCellValue(row.getCell(0));
                Inst_Price = formatter.formatCellValue(row.getCell(1));
                Prof_Price = formatter.formatCellValue(row.getCell(2));
                //System.out.println("her is CPT array" + CPT);

                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".CPTCChargeMaster (FileName,CreatedBy,CPTC,Inst_Price,Prof_Price,Status,CreatedDate) VALUES (?,?,?,?,?,0,now())");
                MainReceipt.setString(1, FileName);

                MainReceipt.setString(2, UserId);

                MainReceipt.setString(3, CPT);
                MainReceipt.setString(4, Inst_Price);
                MainReceipt.setString(5, Prof_Price);
                MainReceipt.executeUpdate();
                MainReceipt.close();
                //System.out.println("hee is the querry" + MainReceipt);
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (ShowReport)", servletContext, e, "CPTCChargeMaster", "ShowReport", conn);
            Services.DumException("ShowReport", "CPTCChargeMaster ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "SendFile");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

        GetInput(request, out, conn, servletContext, UserId, Database, ClientId, helper);


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


    void UpdateData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        //System.out.println("in the Update Function");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        SupportiveMethods suppMethods = new SupportiveMethods();


        String CPT = request.getParameter("CPTC").trim();
        //System.out.println("CPT--->" + CPT);
        String Inst_Price = request.getParameter("Inst_Price").trim();
        String Prof_Price = request.getParameter("Prof_Price").trim();
//        //System.out.println("CPT--->" + Price);

        UserId = request.getParameter("UserId").trim();
        //System.out.println("UserId--->" + UserId);
        String Status = request.getParameter("Status").trim();
        //System.out.println("Status--->" + Status);
        //System.out.println("hee is the id" + request.getParameter("Id"));
        int Id = Integer.parseInt(request.getParameter("Id").trim());

        try {

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "Update " + Database + ".CPTCChargeMaster set CPTC =?,Inst_Price =?,Prof_Price=?,UpdatedBy=?,Status=?,UpdatedDate =now()  where Id =" + Id);
            MainReceipt.setString(1, CPT);
            MainReceipt.setString(2, Inst_Price);
            MainReceipt.setString(3, Prof_Price);
            MainReceipt.setString(4, UserId);
            MainReceipt.setString(5, Status);
            MainReceipt.executeUpdate();
            MainReceipt.close();
            //System.out.println("hee is the querry" + MainReceipt);
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (ShowReport)", servletContext, e, "CPTCChargeMaster", "ShowReport", conn);
            Services.DumException("ShowReport", "CPTCChargeMaster ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "UpdateData");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

        GetInput(request, out, conn, servletContext, UserId, Database, ClientId, helper);


    }


    void EditData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Id = Integer.parseInt(request.getParameter("Id").trim());
        String CPT = "";
        String Inst_Price = "";
        String Prof_Price = "";
        int Status = 0;


        try {
            Query = "Select  IFNULL(CPTC,''),IFNULL(Inst_Price,''),IFNULL(Prof_Price,''),Status From " + Database + ".CPTCChargeMaster where Id = " + Id;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CPT = rset.getString(1);
                Inst_Price = rset.getString(2);
                Prof_Price = rset.getString(3);
                Status = rset.getInt(4);


            }
            rset.close();
            stmt.close();
            out.println(CPT + "|" + Inst_Price + "|" + Prof_Price + "|" + String.valueOf(Status) + "|" + String.valueOf(Id));

        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in CPTCChargeMaster ** (ShowReport)", servletContext, e, "CPTCChargeMaster", "ShowReport", conn);
            Services.DumException("ShowReport", "CPTCChargeMaster ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "EditData");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");


        }

    }


    void FacilityData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DBName = request.getParameter("DBName");
        StringBuffer CPTCList = new StringBuffer();
        StringBuffer CPTCDataList = new StringBuffer();
        int SNo = 1;
        try {
            Query = "Select a.Id, ifnull(a.CPTC,''),ifnull(a.Price,''),ifnull(b.name,'') from " + DBName + ".CPTCChargeMaster a left join oe.clients b on a.FacilityName = b.dbname  where a.Status = 0 order by a.CreatedDate DESC ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {

                CPTCDataList.append("<tr>");
                CPTCDataList.append("<td align=left>" + SNo + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CPTCDataList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CPTCDataList.append("<td align=left><i data-toggle=\"modal\" data-target=\"#exampleModalCenter\"  id=\"edit\" class=\"fa fa-edit\" onClick=\"editRow(" + rset.getInt(1) + ")\"></i></td>\n");
                CPTCDataList.append("</tr>");
                SNo++;
            }
            //System.out.println("hee is the Query1----->" + Query);
            rset.close();
            stmt.close();


//        	Query="Select dbname,name from oe.clients where status = 0 ";
//        	  //System.out.println("hee is the Query2------->"+Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            CPTCList.append("<option value='0' selected>Please Select Facility</option>");
//            while (rset.next()) {
//
//            	CPTCList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
//            }
//            //System.out.println("hee is the Query2------->"+Query);
//            rset.close();
//            stmt.close();
//             Parsehtm Parser = new Parsehtm(request); 
//             Parser.SetField("CPTCList", String.valueOf(CPTCList));
//             Parser.SetField("CPTCDataList", String.valueOf(CPTCDataList));
//             Parser.SetField("UserId", String.valueOf(UserId));
//             Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/CPTCChargeMaster.html");
            out.println(CPTCDataList);
        } catch (Exception e) {
            //System.out.println("in the catch exception of GetInput Function ");
            //System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //System.out.println(str);
        }
    }


}
