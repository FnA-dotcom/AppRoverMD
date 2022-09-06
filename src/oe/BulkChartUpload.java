// 
// Decompiled by Procyon v0.5.36
// 

package oe;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class BulkChartUpload extends HttpServlet {

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
        Connection conn = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String UploadPath = "";
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        final Cookie[] cookies = request.getCookies();
        UserId = (Zone = (Passwd = ""));
        UploadPath = "";
        final int checkCookie = 0;
        for (int coky = 0; coky < cookies.length; ++coky) {
            final String cName = cookies[coky].getName();
            final String cValue = cookies[coky].getValue();
            if (cName.equals("UserId")) {
                UserId = cValue;
            }
        }
        if (ActionID.equals("UploadFiles")) {
            this.UploadFiles(request, out, conn, context, UserId, response, UploadPath);
        } else if (ActionID.equals("BulkUploadInput")) {
            this.BulkUploadInput(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("GetClientPaths")) {
            this.GetClientPaths(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("BulkUploadReportInput")) {
            this.BulkUploadReportInput(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("BulkUploadReport")) {
            this.BulkUploadReport(request, out, conn, context, UserId, response);
        } else {
            out.println("Under Development");
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
        out.flush();
        out.close();
    }

    void BulkUploadInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        StringBuffer Clients = new StringBuffer();
        try {
            Query = "SELECT id,name FROM oe.clients WHERE status = 0";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
//            System.out.println(Query);
            Clients.append("<option value=\"-1\">------ All -----</option>");
            while (hrset.next()) {
                Clients.append("<option value=" + hrset.getInt(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkChartUpload.html");
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }

    void GetClientPaths(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        String UploadPath = "";
        int Clients = Integer.parseInt(request.getParameter("Clients").trim());
        try {
            Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + Clients;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                UploadPath = hrset.getString(1);
            }
            hrset.close();
            hstmt.close();
            if(!UploadPath.endsWith("/")){
                UploadPath = UploadPath+"/";
            }
//            Cookie UploadPathh = new Cookie("UploadPath", UploadPath);
//            response.addCookie(UploadPathh);
            HttpSession session = request.getSession(true);
            session.setAttribute("UploadPath", UploadPath);
            out.println(UploadPath+"|"+Clients);
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }

    private void UploadFiles(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response, String UploadPath) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        boolean FileFound = false;
        String key = "";
        String FileName = "";
        String fName = "";
        String Clients = "0";
        String FileCount = "0";
        String ClientName = "";
        String ClientIndex = "";
        String fSize = "";
        String Stage = "0";
        int i = 1;
        StringBuffer DataTable = new StringBuffer();
        HttpSession session = request.getSession(true);
        UploadPath = session.getAttribute("UploadPath").toString();
        //out.println("UploadPath: "+UploadPath +"<br>");
        Stage = "1 <br>";
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration e = d.keys();
            byte[] Data = (byte[]) null;
            key = Clients = FileCount = fName = "";
            while (e.hasMoreElements()) {
                key = (String) e.nextElement();
                if (key.endsWith(".pdf") || key.endsWith(".PDF")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
//                if (key.startsWith("UploadPath")) {
//                    UploadPath = (String) d.get(key);
//                    if(UploadPath.startsWith("null")){
//                        UploadPath = UploadPath.substring(4);
//                    }
//                }
                if (key.startsWith("ClientIndex")) {
                    ClientIndex = (String) d.get(key);
                }
                if (key.startsWith("FileCount")) {
                    FileCount = (String) d.get(key);
                }
                if (key.startsWith("fName")) {
                    fName = (String) d.get(key);
                }
                if (key.startsWith("fSize")) {
                    fSize = (String) d.get(key);
                }

                if (FileFound) {
                    FileName = FileName.replaceAll("\\s+", "");
                    long unixTime = System.currentTimeMillis() / 1000L;
                    File fe = new File(String.valueOf(UploadPath) +unixTime+"_"+ FileName);
                    if (fe.exists()) {
                        fe.delete();
                    }
                    final FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }

            key = (FileCount = FileCount.substring(4));
            key = (fName = fName.substring(4));
            key = (fSize = fSize.substring(4));
            key = (ClientIndex = ClientIndex.substring(4));

            fName = fName.replaceAll("\\s+", "");
            fName = fName.replaceAll("\\,", "");


            String[] fNameArr = fName.split("\\^");
            String[] fSizeArr = fSize.split("\\^");

//            String[] fNameArr = fName.split("\\,");
            DataTable.append("<table width=\"100%\" id=\"example2\" class=\"table table-bordered table-striped\">\n" +
                    "<thead>\n" +
                    "<tr>\n" +
                    "<th style=\"width:10%\">S.No</th>\n" +
                    "<th style=\"width:30%\">FileName</th>\n" +
                    "</tr>\n" +
                    "</thead>\n" +
                    "<tbody>\n");
            for (String a : fNameArr) {
                DataTable.append("<tr><td align=left>" + i + "</td><td align=left>" + a + "</td></tr>");
                i++;
            }
            DataTable.append("</tbody>\n" +
                    "</table>\n");

            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();


            for (int j = 0; j < fNameArr.length; j++) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.BulkFiles_Upload_logs (ClientId,FileName,FileSize ,CreatedDate,CreatedBy,UserId) " +
                                "VALUES (?,?,?,now(),?,?) ");
                MainReceipt.setString(1, ClientIndex);
                MainReceipt.setString(2, fNameArr[j]);
                MainReceipt.setString(3, fSizeArr[j]);
                MainReceipt.setString(4, UserId);
                MainReceipt.setString(5, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
//                out.println("FileName:"+fNameArr[j]+"<br>");
//                out.println("FileSize:"+fSizeArr[i]+"<br>");
            }




//out.println("*******************************************************************"+"<br>");
//            out.println("UploadPath: "+UploadPath+"<br>");
//            out.println("ClientIndex: "+ClientIndex+"<br>");
//            out.println("FileCount: "+FileCount+"<br>");
//            out.println("fName: "+fName+"<br>");
//            out.println("fSize: "+fSize+"<br>");
//out.println("*******************************************************************"+"<br>");
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClientName", ClientName.toString());
            Parser.SetField("FileCount", FileCount.toString());
            Parser.SetField("DataTable", DataTable.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkUploadDetails.html");

        } catch (Exception e) {
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.println("Error 2: " + e.getMessage());
        }
    }


    void BulkUploadReportInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String CurrDate = "";
        StringBuffer Clients = new StringBuffer();
        StringBuffer Users = new StringBuffer();
        try {
            Query = "SELECT id,name FROM oe.clients WHERE status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Clients.append("<option value=\"-1\">------ All -----</option>");
            while (rset.next()) {
                Clients.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT userid,username FROM oe.sysusers WHERE usertype = 3 and enabled = 'Y'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Users.append("<option value=\"-1\">------ All -----</option>");
            while (rset.next()) {
                Users.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                CurrDate = rset.getString(1).trim();
            rset.close();
            stmt.close();
            CurrDate = CurrDate + " 00:00:00" + " - " + CurrDate + " 23:59:59";
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.SetField("Users", Users.toString());
            Parser.SetField("CurrDate", CurrDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkUploadReport.html");
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }

    void BulkUploadReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String CurrDate = "";
        String DToDate = "";
        String DFromDate = "";
        String UserCondition = "";
        String ClientIndexCondition = "";
        int SNo = 1;
        StringBuffer Clients = new StringBuffer();
        StringBuffer Users = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        String ClientIndex = request.getParameter("Clients").trim();
        String User = request.getParameter("Users").trim();
        String dosdate = request.getParameter("dosdate").trim();
        try {
            DToDate =dosdate.substring(0, 19);
            DFromDate=dosdate.substring(22,41);

            if(User.equals("-1")) {
                UserCondition = " ";
            }else{
                UserCondition = " and a.UserId = '"+User+"'";
            }
            if(ClientIndex.equals("-1")) {
                ClientIndexCondition = " ";
            }else{
                ClientIndexCondition = " and a.ClientId = "+ClientIndex;
            }
            Query = "SELECT id,name FROM oe.clients WHERE status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Clients.append("<option value=\"-1\">------ All -----</option>");
            while (rset.next()) {
                Clients.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT userid,username FROM oe.sysusers WHERE usertype = 3 and enabled = 'Y'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Users.append("<option value=\"-1\">------ All -----</option>");
            while (rset.next()) {
                Users.append("<option value=" + rset.getString(1) + ">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d')";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                CurrDate = rset.getString(1).trim();
            rset.close();
            stmt.close();
            CurrDate = CurrDate + " 00:00:00" + " - " + CurrDate + " 23:59:59";

            Query = "Select b.name, a.FileName, a.FileSize, DATE_FORMAT(a.CreatedDate,'%m/%d/%Y %T'), c.username from " +
                    " oe.BulkFiles_Upload_logs a " +
                    " LEFT JOIN oe.clients b on a.ClientId = b.Id " +
                    " LEFT JOIN oe.sysusers c on a.UserId = c.userid " +
                    " where a.CreatedDate between '"+DToDate+"' and '"+DFromDate+"' "+UserCondition + ClientIndexCondition;
//            out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr class=\"Inner\">\n");
                CDRList.append("<td align=center>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + Math.round(Float.parseFloat(rset.getString(3))/1024) + " KiloByte</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("</tr>\n");
                ++SNo;
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.SetField("Users", Users.toString());
            Parser.SetField("CDRList", CDRList.toString());
            Parser.SetField("CurrDate", CurrDate.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkUploadReport.html");
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
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


    void UploadFiles3(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        try {
            out.println("1 <br>");
            String description = request.getParameter("Clients"); // Retrieves <input type="text" name="description">
            out.println("2 <br>");
            Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
            out.println("2 <br>");
            //   String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
            String fileName = "";
            out.println("3 <br>");
            for (Part part : request.getParts()) {
                fileName = extractFileName(part);
                //details = new UploadDetail();
                //	details.setFileName(fileName);
                //	details.setFileSize(part.getSize() / 1024);

                out.println(fileName);

            }
            out.println("4 <br>");

            //InputStream fileContent = filePart.getInputStream();

            out.println("5 <br>");
            out.println("description " + description + "<br>");
            out.println("fileName " + fileName + "<br>");
            //out.println("fileContent " + fileContent + "<br>");
        } catch (Exception ee) {
            out.println("Exception Message is: " + ee.getMessage());
        }
        // ... (do your job here)
    }

    private String extractFileName(Part part) {
        String fileName = "",
                contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return fileName;
    }
}
