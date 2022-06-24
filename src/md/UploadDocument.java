
package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class UploadDocument extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";
    private Connection conn = null;

    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;
    private PreparedStatement pStmt = null;

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    private static int copyFileUsingJava7Files(File source, File dest) throws IOException {
        File fe = new File(String.valueOf(dest));
        if (!fe.exists()) {
            Files.copy(source.toPath(), dest.toPath(), new java.nio.file.CopyOption[0]);
            return 1;
        }
        return 0;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    void GetValues(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Dashboards/DashBoardInput.html");
        } catch (Exception exception) {
        }
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
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            this.conn = Services.GetConnection(context, 1);
            if (this.conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetValues")) {
                GetValues(request, out, this.conn, context);
            } else if (ActionID.equals("PatientsDocUpload")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload", "Click on Upload Docs Option", FacilityIndex);
                PatientsDocUpload(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("PatientsDocUpload_Save") && request.getContentType().startsWith("multipart/form-data")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload Save", "Save the Documents for the selected patients", FacilityIndex);
                PatientsDocUpload_Save(request, out, this.conn, context, response, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments_Input")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Click on View Documents Options", FacilityIndex);
                ViewDocuments_Input(request, out, this.conn, context, response, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("ViewDocuments")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                ViewDocuments(request, out, this.conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("DeleteDocument")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                DeleteDocument(request, out, this.conn, context, DatabaseName, helper);
            } else if (ActionID.equals("GetPatientsUploadDocs")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                GetPatientsUploadDocs(request, response, out, this.conn, context, UserId, DatabaseName, helper);
            } else if (ActionID.equals("download_direct")) {
                supp.Dologing(UserId, this.conn, request.getRemoteAddr(), ActionID, "Patient Docs View", "Docmuents Uploaded List", FacilityIndex);
                download_direct(request, response, out, this.conn);
            } else {
                helper.deleteUserSession(request, this.conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in UplaodDocument ** (handleRequest)", context, e, "UplaodDocument", "handleRequest", this.conn);
            Services.DumException("UplaodDocument", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (this.conn != null)
                    this.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in UplaodDocument ** (handleRequest -- SqlException)", context, e, "UplaodDocument", "handleRequest", this.conn);
                Services.DumException("UplaodDocument", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void PatientsDocUpload(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        try {
            StringBuffer PatientList = new StringBuffer();

            Query = "SELECT CONCAT(ID,',',MRN,',',Title,' ',FirstName,' ',MiddleInitial,' ',LastName), CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option class=Inner value=-1>Select Patient</option>");
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));

            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/UploadPatientsDocs.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in UploadDocument ** (PatientsDocUpload)", servletContext, ex, "UploadDocument", "PatientsDocUpload", conn);
            Services.DumException("PatientsDocUpload", "UploadDocument ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#003");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            //System.out.println(ex.getMessage());
        }
    }

    void PatientsDocUpload_Save(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        String PatientId = "";
        String DocumentName = "";
        String UserId = "";
        String Client = "";
        int PremisisId = 0;
        int PatientRegId = 0;
        String PatientMRN = "";
        String PatientName = "";
        String DirectoryName = "";
        String DocumentType = "";
        String VisitNo = "";

        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("UploadDocument", "PatientDocUplaodSave", request, e, servletContext);
        }
        String Path = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "";
        String UploadPath = String.valueOf(String.valueOf(Path)) + "/";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";
        String FileName = "";
        try {
            Dictionary d = doUpload(request, response, out);
            Enumeration<String> en = d.keys();
            while (en.hasMoreElements()) {
                key = en.nextElement();
                if (key.endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".pdf") || key.endsWith(".txt") || key.endsWith(".csv") || key.endsWith(".doc") || key.endsWith(".xlsx")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (key.toUpperCase().endsWith(".jpg") || key.endsWith(".png") || key.endsWith(".PDF") || key.endsWith(".txt") || key.endsWith(".csv") || key.endsWith(".doc") || key.endsWith(".xlsx")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                } else if (key.startsWith("PatientId")) {
                    PatientId = (String) d.get(key);
                } else if (key.startsWith("DocumentName")) {
                    DocumentName = (String) d.get(key);
                } else if (key.startsWith("UserId")) {
                    UserId = (String) d.get(key);
                } else if (key.startsWith("Client")) {
                    Client = (String) d.get(key);
                } else if (key.startsWith("DocumentType")) {
                    DocumentType = (String) d.get(key);
                } else if (key.startsWith("visitDos")) {
                    VisitNo = (String) d.get(key);
                }
                if (FileFound) {
                    System.out.println(FileName);
                    FileName = FileName.replaceAll("\\s+", "");
                    File fe = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                    if (fe.exists())
                        fe.delete();
                    FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
            }
            PatientId = PatientId.substring(4);
            DocumentName = DocumentName.substring(4);
            UserId = UserId.substring(4);
            VisitNo = VisitNo.substring(4);


//            out.println("Visit No "+VisitNo);
            String[] PatientInfo = PatientId.split("\\,");
            PatientRegId = Integer.parseInt(PatientInfo[0]);
            PatientMRN = PatientInfo[1];
            PatientName = PatientInfo[2];
            try {
                Query = "Select clientid from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    PremisisId = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Client Id get (PremisisID)" + e.getMessage());
            }
            DocumentType = DocumentType.substring(4);
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "Insert into " + Database + ".PatientDocUpload (PremisisId, PatientRegId, PatientMRN, PatientName, " +
                                "UploadDocumentName, FileName, CreatedBy, CreatedDate, DocumentType,VisitIdx) values (?,?,?,?,?,?,?,now(),?,?) ");
                MainReceipt.setInt(1, PremisisId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, PatientMRN);
                MainReceipt.setString(4, PatientName);
                MainReceipt.setString(5, DocumentName);
                MainReceipt.setString(6, FileName);
                MainReceipt.setString(7, UserId);
                MainReceipt.setString(8, DocumentType);
                MainReceipt.setString(9, VisitNo);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in UploadDocument ** (PatientsDocUpload_Save)", servletContext, e, "UploadDocument", "PatientsDocUpload_Save", conn);
                Services.DumException("PatientsDocUpload_Save", "UploadDocument ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "UploadDocument");
                Parser.SetField("ActionID", "PatientsDocUpload");
                Parser.SetField("Message", "MES#004");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                //out.println("Error in Insertion:-" + e.getMessage());
            }
            String target = "";
            String firstname = "";
            String lastname = "";
            String Message = "";
            if (DocumentType.compareTo("1") == 0) {
                Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + PremisisId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    target = rset.getString(1);
                rset.close();
                stmt.close();
                File source = new File(String.valueOf(String.valueOf(UploadPath)) + FileName);
                File dest = new File(String.valueOf("/opt/" + FileName));
                int i = copyFileUsingJava7Files(source, dest);
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", String.valueOf("File Has been Uploaded Successfully" + Message));
            Parser.SetField("FormName", String.valueOf("UploadDocument"));
            Parser.SetField("ActionID", String.valueOf("PatientsDocUpload"));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Success.html");
        } catch (Exception e2) {
            helper.SendEmailWithAttachment("Error in UploadDocument ** (PatientsDocUpload_Save MES#005)", servletContext, e2, "UploadDocument", "PatientsDocUpload_Save MES#005", conn);
            Services.DumException("PatientsDocUpload_Save", "UploadDocument MES#005", request, e2);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "UploadDocument");
            Parser.SetField("ActionID", "PatientsDocUpload");
            Parser.SetField("Message", "MES#005");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error in Upload DOcuments!!" + e2.getMessage());
            String str = "";
            for (int i = 0; i < (e2.getStackTrace()).length; i++)
                str = str + e2.getStackTrace()[i] + "<br>";
            out.println(str);*/
        }
    }

    void ViewDocuments_Input(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        StringBuffer PatientList = new StringBuffer();
        try {
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/ViewDocuments.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in UploadDocument ** (ViewDocuments_Input)", servletContext, e, "UploadDocument", "ViewDocuments_Input", conn);
            Services.DumException("ViewDocuments_Input", "UploadDocument ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#006");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void ViewDocuments(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        int PatientRegId = 0;
        final String PatientName = "";
        final String PatientMRN = "";
        final StringBuffer CDRList = new StringBuffer();
        final StringBuffer PatientList = new StringBuffer();
        int SNo = 1;
        String DirectoryName = "";
        String DocumentPath = "";
        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("UplaodDocument2", "PatientDocUplaodSave", request, e, servletContext);
        }
        try {
            /*final InetAddress ip = InetAddress.getLocalHost();
            final String hostname = ip.getHostName();*/
            PatientRegId = Integer.parseInt(request.getParameter("PatientId").trim());
            Query = "SELECT ID, CONCAT(Title,' ',FirstName,' ',MiddleInitial,' ',LastName), MRN FROM " + Database + ".PatientReg where Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(3) + ")</option>");
            }
            rset.close();
            stmt.close();
            Query = "SELECT a.PatientMRN, a.PatientName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and a.PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                final File tmpFile = new File("/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/" + rset.getString(4));
                final boolean exists = tmpFile.exists();
                if (exists) {
                    DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/";
                } else {
                    DocumentPath = "/sftpdrive/AdmissionBundlePdf/Attachment/";
                }
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left><a href=/md/md.UploadDocument?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                /*if (hostname.trim().equals("romver-01")) {
                    CDRList.append("<td align=left><a href=/md/md.UploadDocument?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                } else {
                    CDRList.append("<td align=left><a href=/md/md.UploadDocument?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                }*/
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
                CDRList.append("</tr>");
                ++SNo;
            }
            rset.close();
            stmt.close();

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ViewDocuments.html");
        } catch (Exception ex) {
            Services.DumException("ViewDocuments", "UploadDocument ", request, ex);
            Parsehtm Parser2 = new Parsehtm(request);
            Parser2.SetField("FormName", "UploadDocument");
            Parser2.SetField("ActionID", "ViewDocuments_Input");
            Parser2.SetField("Message", "MES#007");
            Parser2.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void DeleteDocument(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int DocId = Integer.parseInt(request.getParameter("ID").trim());
        try {
            Query = "Update " + Database + ".PatientDocUpload Set Status = 1 where ID = " + DocId;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
            out.println("1");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in UploadDocument ** (DeleteDocument)", servletContext, ex, "UploadDocument", "DeleteDocument", conn);
            Services.DumException("DeleteDocument", "UploadDocument ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "UplaodDocument");
            Parser.SetField("ActionID", "ViewDocuments_Input");
            Parser.SetField("Message", "MES#008");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error: Updating PatientDocUpload Table " + e.getMessage());
            Services.DumException("UplaodDocument", "Delete DOC - Updating PatientDocUpload table :", request, e, getServletContext());
            return;*/
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

    public void download_direct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn) {
        String FileName = request.getParameter("fname");
        String path = request.getParameter("path");
        String RecordingPath = path + FileName;
        if (FileName.endsWith("docx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (FileName.endsWith("doc")) {
            response.setContentType("application/msword");
        } else if (FileName.endsWith("csv")) {
            response.setContentType("text/csv");
        } else if (FileName.endsWith("jpg")) {
            response.setContentType("image/jpeg");
        } else if (FileName.endsWith("png")) {
            response.setContentType("image/png");
        } else if (FileName.endsWith("pdf")) {
            response.setContentType("application/pdf");
        } else if (FileName.endsWith("txt")) {
            response.setContentType("text/plain");
        } else if (FileName.endsWith("xlsx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        try {
            response.setHeader("Content-Disposition", "Inline; filename=\"" + FileName + "\";");
            FileInputStream fin = new FileInputStream(RecordingPath);
            byte[] content = new byte[fin.available()];
            fin.read(content);
            fin.close();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(content);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            out.println("Unable to process request ..." + e.getMessage());
        }
    }

    void GetPatientsUploadDocs(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper) throws FileNotFoundException {
        try {
            String Patient = request.getParameter("Patient").trim();
            StringBuilder PatientList = new StringBuilder();

            Query = " Select Id, IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), " +
                    "IFNULL(PhNumber,''),  CONCAT(ID,',',IFNULL(MRN,''),',',IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,''))  " +
                    "from " + Database + ".PatientReg  where status = 0 and CONCAT(FirstName,LastName,PhNumber,MiddleInitial,MRN) like '%" + Patient + "%'  ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" onchange=\"GetDOS(this.value);\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            Services.DumException("GetPatientsUploadDocs", "UploadDocument ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "UploadDocument");
            Parser.SetField("ActionID", "GetPatientsUploadDocs");
            Parser.SetField("Message", "MES#043");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }
}