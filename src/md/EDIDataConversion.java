package md;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("Duplicates")
public class EDIDataConversion extends HttpServlet {
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
        PrintWriter out = new PrintWriter(response.getOutputStream());
        UtilityHelper helper = new UtilityHelper();
        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context;
        context = this.getServletContext();

        Connection conn = null;

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
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "UploadFileOnly":
                    UploadFileOnly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "SaveData":
                    SaveData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "GetInputCPT":
                    GetInputCPT(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "UploadFileOnlyCPT":
                    UploadFileOnlyCPT(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "SaveDataCPT":
                    SaveDataCPT(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;

                case "GetInputAddOns":
                    GetInputAddOns(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "UploadFileOnlyAddOns":
                    UploadFileOnlyAddOns(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "SaveDataAddOns":
                    SaveDataAddOns(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadEDIData.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void UploadFileOnly(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            String key = "";
            String FileName = "";
            String Query = "";

            boolean FileFound;
            byte Data[];
            try {
                FileFound = false;
                Data = (byte[]) null;
                Query = key = FileName = "";
                Dictionary d = doUpload(request, response, out);
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (key.endsWith(".xls") || key.endsWith(".xlsx") || key.endsWith(".csv")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                }
                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }
                File f = new File("/sftpdrive/opt/EDI/" + FileName);
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

            try {
                out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">File has been Uploaded Successfully </p>");
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"EDIDataUpload\" action=\"/md/md.EDIDataConversion?ServiceRequests=SaveData\" >\n");
                out.println("<div class=\"box-footer\">\n" +
                        "<button type=\"submit\" class=\"btn btn-rounded btn-primary btn-outline\" >\n" +
                        "<i class=\"ti-save-alt\"></i> Save Data\n" +
                        "</button>\n" +
                        "</div>  \n");
                out.println("<input name=\"FileName\" id=\"FileName\" type=\"hidden\" value=" + FileName + ">");
                out.println("</form>");

            } catch (Exception e) {
                try {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Exception/Error.html");
                } catch (Exception localException1) {
                }
                out.flush();
                out.close();
                return;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private Dictionary doUpload(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf(61);
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            final byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            final Dictionary fields = new Hashtable();
            final ServletInputStream in = request.getInputStream();
            for (int i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                final String st = new String(bytes, 0, i);
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
                    final StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        final String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                        } else {
                            if (token.startsWith(" filename")) {
                                filename = tokenizer.nextToken();
                                final StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                                filename = ftokenizer.nextToken();
                                while (ftokenizer.hasMoreTokens()) {
                                    filename = ftokenizer.nextToken();
                                }
                                state = 1;
                                break;
                            }
                            continue;
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

    private void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {

        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;
        int i = 0;
        String ICD = "";
        String Description = "";
        String TerminationDate = "";
        String EffectiveDate = "";
        try {
            fileIn = new FileInputStream("/sftpdrive/opt/EDI/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);
            Workbook workbook = WorkbookFactory.create(fileIn);
            /*Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                ICD = formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "");
                Description = formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "");
                TerminationDate = formatter.formatCellValue(row.getCell(3)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(3)).equals("")) {
                    TerminationDate = formatter.formatCellValue(row.getCell(3));
                    if (TerminationDate.length() > 7)
                        TerminationDate = TerminationDate.substring(0, 4) + "-" + TerminationDate.substring(4, 6) + "-" + TerminationDate.substring(6, 8).replaceAll("\n", "");
                    else
                        TerminationDate = "0000-00-00";
                } else {
                    TerminationDate = "0000-00-00";
                }
                out.println("********************** SHEET # 1 ***************************** <br> ");
                out.println("ICD --> " + ICD + " <br> ");
                out.println("Description --> " + Description + " <br> ");
                out.println("TerminationDate --> " + TerminationDate + " <br> ");
                out.println("********************** SHEET # 1 *********************** <br> ");

                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO ClaimMasterDB.ICDMaster (ICD,Description,TermincationDate," +
                                    "Status,CreatedDate,CreatedBy) VALUES (?,?,?,0,NOW(),'Excel Sheet 1')");
                    MainReceipt.setString(1, ICD);
                    MainReceipt.setString(2, Description);
                    MainReceipt.setString(3, TerminationDate);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error in inserting data in Sheet 1: " + e.getMessage());
                    String str = "";
                    for (i = 0; i < e.getStackTrace().length; ++i) {
                        str = str + e.getStackTrace()[i] + "<br>";
                    }
                    out.println(str);
                    return;
                }
            }*/

            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                ICD = formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "");
                Description = formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "");
                EffectiveDate = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(3)).equals("")) {
                    EffectiveDate = formatter.formatCellValue(row.getCell(3));
                    if (EffectiveDate.length() > 7)
                        EffectiveDate = EffectiveDate.substring(0, 4) + "-" + EffectiveDate.substring(4, 6) + "-" + EffectiveDate.substring(6, 8).replaceAll("\n", "");
                    else
                        EffectiveDate = "0000-00-00";
                } else {
                    EffectiveDate = "0000-00-00";
                }
/*                out.println("********************** SHEET # 2 ***************************** <br> ");
                out.println("ICD --> " + ICD + " <br> ");
                out.println("Description --> " + Description + " <br> ");
                out.println("EffectiveDate --> " + EffectiveDate + " <br> ");
                out.println("********************** SHEET # 2 *********************** <br> ");*/

                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO ClaimMasterDB.ICDMaster (ICD,Description,EffectiveDate," +
                                    "Status,CreatedDate,CreatedBy) VALUES (?,?,?,0,NOW(),'Excel Sheet 2')");
                    MainReceipt.setString(1, ICD);
                    MainReceipt.setString(2, Description);
                    MainReceipt.setString(3, EffectiveDate);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error in inserting data in Sheet 1: " + e.getMessage());
                    String str = "";
                    for (i = 0; i < e.getStackTrace().length; ++i) {
                        str = str + e.getStackTrace()[i] + "<br>";
                    }
                    out.println(str);
                    return;
                }
            }
            out.println("***************** FINISHED ************************");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void GetInputCPT(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadEDIDataCPT.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void UploadFileOnlyCPT(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            String key = "";
            String FileName = "";
            String Query = "";

            boolean FileFound;
            byte Data[];
            try {
                FileFound = false;
                Data = (byte[]) null;
                Query = key = FileName = "";
                Dictionary d = doUpload(request, response, out);
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (key.endsWith(".xls") || key.endsWith(".xlsx") || key.endsWith(".csv")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                }
                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }
                File f = new File("/sftpdrive/opt/EDI/" + FileName);
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

            try {
                out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">File has been Uploaded Successfully </p>");
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"EDIDataUpload\" action=\"/md/md.EDIDataConversion?ServiceRequests=SaveDataCPT\" >\n");
                out.println("<div class=\"box-footer\">\n" +
                        "<button type=\"submit\" class=\"btn btn-rounded btn-primary btn-outline\" >\n" +
                        "<i class=\"ti-save-alt\"></i> Save Data\n" +
                        "</button>\n" +
                        "</div>  \n");
                out.println("<input name=\"FileName\" id=\"FileName\" type=\"hidden\" value=" + FileName + ">");
                out.println("</form>");

            } catch (Exception e) {
                try {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Exception/Error.html");
                } catch (Exception localException1) {
                }
                out.flush();
                out.close();
                return;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void SaveDataCPT(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;
        int i = 0;
        String CPTCode = "";
        String Description = "";
        String TerminationDate = "";
        String EffectiveDate = "";
        try {
            fileIn = new FileInputStream("/sftpdrive/opt/EDI/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                CPTCode = formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "");
                Description = formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "");
                EffectiveDate = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(2)).equals("")) {
                    EffectiveDate = formatter.formatCellValue(row.getCell(2));
                    if (EffectiveDate.length() > 7)
                        EffectiveDate = EffectiveDate.substring(0, 4) + "-" + EffectiveDate.substring(4, 6) + "-" + EffectiveDate.substring(6, 8).replaceAll("\n", "");
                    else
                        EffectiveDate = "0000-00-00";
                } else {
                    EffectiveDate = "0000-00-00";
                }
                out.println("********************** SHEET # 1 ***************************** <br> ");
                out.println("CPTCode --> " + CPTCode + " <br> ");
                out.println("Description --> " + Description + " <br> ");
                out.println("EffectiveDate --> " + EffectiveDate + " <br> ");
                out.println("********************** SHEET # 1 *********************** <br> ");

                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO ClaimMasterDB.CPTMaster (CPTCode,CPTDescription,EffectiveDate," +
                                    "Status,CreatedDate,CreatedBy) VALUES (?,?,?,0,NOW(),'Excel Sheet 2022')");
                    MainReceipt.setString(1, CPTCode);
                    MainReceipt.setString(2, Description);
                    MainReceipt.setString(3, EffectiveDate);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error in inserting data in Sheet 1: " + e.getMessage());
                    String str = "";
                    for (i = 0; i < e.getStackTrace().length; ++i) {
                        str = str + e.getStackTrace()[i] + "<br>";
                    }
                    out.println(str);
                    return;
                }
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }


    void GetInputAddOns(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadEDIDataAddOns.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void UploadFileOnlyAddOns(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            String key = "";
            String FileName = "";
            String Query = "";

            boolean FileFound;
            byte Data[];
            try {
                FileFound = false;
                Data = (byte[]) null;
                Query = key = FileName = "";
                Dictionary d = doUpload(request, response, out);
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (key.endsWith(".xls") || key.endsWith(".xlsx") || key.endsWith(".csv")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                }
                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }
                File f = new File("/sftpdrive/opt/EDI/" + FileName);
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

            try {
                out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">File has been Uploaded Successfully </p>");
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"EDIDataUpload\" action=\"/md/md.EDIDataConversion?ServiceRequests=SaveDataAddOns\" >\n");
                out.println("<div class=\"box-footer\">\n" +
                        "<button type=\"submit\" class=\"btn btn-rounded btn-primary btn-outline\" >\n" +
                        "<i class=\"ti-save-alt\"></i> Save Data\n" +
                        "</button>\n" +
                        "</div>  \n");
                out.println("<input name=\"FileName\" id=\"FileName\" type=\"hidden\" value=" + FileName + ">");
                out.println("</form>");

            } catch (Exception e) {
                try {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Exception/Error.html");
                } catch (Exception localException1) {
                }
                out.flush();
                out.close();
                return;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void SaveDataAddOns(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;
        int i = 0;
        String PrimaryCode = "";
        String AddOnCode = "";
        String TerminationDate = "";
        String EffectiveDate = "";
        String PayerID = "";
        try {
            fileIn = new FileInputStream("/sftpdrive/opt/EDI/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                PrimaryCode = formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "");
                AddOnCode = formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "");
                EffectiveDate = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(2)).equals("")) {
                    EffectiveDate = formatter.formatCellValue(row.getCell(2));
                    if (EffectiveDate.length() > 7)
                        EffectiveDate = EffectiveDate.substring(0, 4) + "-" + EffectiveDate.substring(4, 6) + "-" + EffectiveDate.substring(6, 8).replaceAll("\n", "");
                    else
                        EffectiveDate = "0000-00-00";
                } else {
                    EffectiveDate = "0000-00-00";
                }
                TerminationDate = formatter.formatCellValue(row.getCell(3)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(3)).equals("")) {
                    TerminationDate = formatter.formatCellValue(row.getCell(2));
                    if (TerminationDate.length() > 7)
                        TerminationDate = TerminationDate.substring(0, 4) + "-" + TerminationDate.substring(4, 6) + "-" + TerminationDate.substring(6, 8).replaceAll("\n", "");
                    else
                        TerminationDate = "0000-00-00";
                } else {
                    TerminationDate = "0000-00-00";
                }
                PayerID = formatter.formatCellValue(row.getCell(4)).replaceAll("\n", "");

                out.println("********************** SHEET # 1 ***************************** <br> ");
                out.println("PrimaryCode --> " + PrimaryCode + " <br> ");
                out.println("AddOnCode --> " + AddOnCode + " <br> ");
                out.println("EffectiveDate --> " + EffectiveDate + " <br> ");
                out.println("TerminationDate --> " + TerminationDate + " <br> ");
                out.println("PayerID --> " + PayerID + " <br> ");
                out.println("********************** SHEET # 1 *********************** <br> ");

                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO ClaimMasterDB.AddOnCodes (PrimaryCodes,AddOnCode,EffectiveDate,TerminationDate,PayerID," +
                                    "Status,CreatedDate,CreatedBy) VALUES (?,?,?,?,?,0,NOW(),'Excel Sheet')");
                    MainReceipt.setString(1, PrimaryCode);
                    MainReceipt.setString(2, AddOnCode);
                    MainReceipt.setString(3, EffectiveDate);
                    MainReceipt.setString(4, TerminationDate);
                    MainReceipt.setString(5, PayerID);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println("Error in inserting data in Sheet 1: " + e.getMessage());
                    String str = "";
                    for (i = 0; i < e.getStackTrace().length; ++i) {
                        str = str + e.getStackTrace()[i] + "<br>";
                    }
                    out.println(str);
                    return;
                }
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

}
