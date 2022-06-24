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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class BulkChartUpload2 extends HttpServlet {
    public static final String UPLOAD_DIR = "uploadedFiles";
    private static final long serialVersionUID = 1L;
    static String ClientIndex = "0";

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
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        final Cookie[] cookies = request.getCookies();
        UserId = (Zone = (Passwd = ""));
        final int checkCookie = 0;
        for (int coky = 0; coky < cookies.length; ++coky) {
            final String cName = cookies[coky].getName();
            final String cValue = cookies[coky].getValue();
            if (cName.equals("UserId")) {
                UserId = cValue;
            }
        }
        if (ActionID.equals("UploadFiles")) {
            this.UploadFiles(request, out, conn, context, UserId, response);
        } else if (ActionID.equals("BulkUploadInput")) {
            this.BulkUploadInput(request, out, conn, context, UserId, response);
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
        final StringBuffer Clients = new StringBuffer();
        try {
            Query = "SELECT id,name FROM oe.clients WHERE status = 0";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            System.out.println(Query);
            Clients.append("<option value=\"-1\">------ All -----</option>");
            while (hrset.next()) {
                Clients.append("<option value=" + hrset.getInt(1) + ">" + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Clients", Clients.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkChartUpload2.html");
        } catch (Exception e) {
            out.println("Error in getting Report: " + e.getMessage());
        }
    }


    //void UploadFiles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    void UploadFiles1(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        try {
            String description = request.getParameter("Clients"); // Retrieves <input type="text" name="description">

            String applicationPath = getServletContext().getRealPath(""),
                    uploadPath = applicationPath + File.separator + UPLOAD_DIR;
            //	 Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
            //   String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
            String fileName = "";
            out.println(description);
            for (Part part : request.getParts()) {
                fileName = extractFileName(part);
                //details = new UploadDetail();
                //	details.setFileName(fileName);
                //	details.setFileSize(part.getSize() / 1024);

                out.println(fileName);
                // InputStream fileContent = part.getInputStream();

                part.write(uploadPath + File.separator + fileName);
            }


            //  InputStream fileContent = filePart.getInputStream();


            out.println(description);
            out.println(fileName);
        } catch (Exception ee) {
            out.println(ee.getMessage());
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


    void UploadFiles(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String UploadPath = "";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String FileName = "";
        String fName = "";
        String Clients = "0";
        String FileCount = "0";
        String ClientName = "";
        int i = 1;
        StringBuffer DataTable = new StringBuffer();


        try {
            final Dictionary d = this.doUpload(request, response, out);
            // out.println("||||||||||||"+d);
            final Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                if (key.startsWith("Clients")) {
                    out.println("Key Val : " + key + "<br>");

                    Clients = (String) d.get(key);
                    out.println("Key Val : " + Clients + "<br>");
/*                    if (Clients.startsWith("null"))
                        Clients = Clients.substring(4);

                    out.println("final client: " + Clients+ "<br>");

                    if (UploadPath.equals("")) {
                        Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + Clients;
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            UploadPath = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    }
                    break;*/
                    if (Clients.length() > 4) {

                        out.println("final client: " + Clients + "<br>");
                        if (Clients.startsWith("null"))
                            Clients = Clients.substring(4);
                        out.println("final client2: " + Clients + "<br>");

                        if (UploadPath.equals("")) {
                            Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + Clients;
                            out.println("Query " + Query + "<br>");
                            stmt = conn.createStatement();
                            rset = stmt.executeQuery(Query);
                            if (rset.next()) {
                                UploadPath = rset.getString(1);
                            }
                            rset.close();
                            stmt.close();

                            out.println("Path " + UploadPath + "<br>");
                        }
                        //break;
                    }
                }

            }
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                out.println(key + "<br>");
                if (key.endsWith(".pdf") || key.endsWith(".PDF")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }


                out.println("Second Loop path " + UploadPath + "<br>");
                if (FileFound) {
                    FileName = FileName.replaceAll("\\s+", "");
                    out.println("FileName: " + FileName + "<br>");
/*                    File fe = new File(String.valueOf(UploadPath) + FileName);
                    if (fe.exists()) {
                        fe.delete();
                    }
                    final FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();*/
                }
            }
            //key = (FileCount = FileCount.substring(4));
            //key = (fName = fName.substring(4));

            String[] fNameArr = fName.split("\\,");

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
//out.println(Clients);
//out.println(Query);
//out.println("UploadPath: "+UploadPath + "<br>");
/*            Query = "Select name from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClientName", ClientName.toString());
            Parser.SetField("FileCount", FileCount.toString());
            Parser.SetField("DataTable", DataTable.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/BulkUploadDetails.html");*/

//            out.println("Clients: "+ Clients);
//            out.println("FileCount: "+ FileCount);
//            out.println("fName: "+ fName);
//            out.println("DataTable: "+ DataTable);
//            out.println("fNameArr: "+ fNameArr.length);
        } catch (Exception e) {
            out.println("Error 2: " + e.getMessage());
//            String str = "";
//            for (i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
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
}
