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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class LabDocsUpload extends HttpServlet {
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
        String ActionID = "";
        String UserId = "";
        String DatabaseName = "";
        String DirectoryName = "";
        HttpSession session = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        Connection conn = null;
        try {
            Parsehtm Parser;
            session = request.getSession(false);
            validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            DirectoryName = session.getAttribute("DirectoryName").toString();
            if (UserId.equals("")) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (ActionID.equals("PatientsDocUpload")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Patient Docs Upload LAB ", "Click on Upload Docs Option", FacilityIndex);
                PatientsDocUpload(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            }
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(context);
            }
            helper.SendEmailWithAttachment("Error in PatientReg ** (handleRequest)", context, e, "PatientReg", "handleRequest", conn);
            Services.DumException("PatientReg", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in LabDocsUpload ** (handleRequest -- SqlException)", context, e, "LabDocsUpload", "handleRequest", conn);
                Services.DumException("LabDocsUpload", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void PatientsDocUpload(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String DocumentPath = "";
            String DirectoryName = "";
            int SNo = 1;
            StringBuffer PatientList = new StringBuffer();
            StringBuffer CDRList = new StringBuffer();
            StringBuilder DOS = new StringBuilder();
            String IDFront = "";
            String InsuranceFront = "";
            String InsuranceBack = "";
            String MRN = "";
            String Name = "";
            String DateOfService = "";

            Query = "Select DirectoryName from oe.clients where Id = 36";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();


            String PatientId = request.getParameter("PatientId");
            Query = " Select Id, IFNULL(MRN,''), IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), " +
                    " IFNULL(PhNumber,''),  CONCAT(ID,',',IFNULL(MRN,''),',',IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' '," +
                    " IFNULL(LastName,''))  " +
                    "from " + Database + ".PatientReg  where status = 0 and ID = " + PatientId;
//            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option class=Inner value=\"" + rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7) + "\">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();


            Query = "SELECT Id, ReasonVisit,IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')) " +
                    "FROM " + Database + ".PatientVisit WHERE PatientRegId = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                DOS.append("<option style=\"color:white\" value=" + rset.getInt(1) + "> " + rset.getString(2) + " | " + rset.getString(3) + " </option>");
                DateOfService = rset.getString(3);
            }
            stmt.close();
            rset.close();

            if (ClientId == 9) {
                PreparedStatement ps = conn.prepareStatement(" Select " +
                        "CASE WHEN IDFront_Status = 0 THEN IDFront ELSE NULL END,\n" +
                        "CASE WHEN InsuranceFront_Status = 0 THEN InsuranceFront ELSE NULL END,\n" +
                        "CASE WHEN InsuranceBack_Status = 0 THEN InsuranceBack ELSE NULL END, " +
                        " MRN , FirstName, CreatedDate " +
                        " from " + Database + ".PatientReg where ID = '" + PatientId + "' and status = 0");
                rset = ps.executeQuery();
                if (rset.next()) {
                    if (rset.getString(1) != null) {
                        CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                        CDRList.append("<td align=left> ID Front </td>\n");
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                        CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(1) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">ID_Front.png</a></td>\n");
                        CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IS'," + PatientId + ")\"></i></td>\n");
                        CDRList.append("</tr>");
                        SNo++;
                    }
                    if (rset.getString(2) != null) {
                        CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                        CDRList.append("<td align=left> Insurance Front </td>\n");
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                        CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(2) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">Insurance_Front.png</a></td>\n");
                        CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IF'," + PatientId + ")\"></i></td>\n");
                        CDRList.append("</tr>");
                        SNo++;
                    }
                    if (rset.getString(3) != null) {

                        CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                        CDRList.append("<td align=left> Insurance Back </td>\n");
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                        CDRList.append("<td align=left><a href=\"md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(3) + "&path=/sftpdrive/AdmissionBundlePdf/Attachment/Victoria/Uploads/\" target=\"_blank\">Insurance_Back.png</a></td>\n");
                        CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteIDs('IB'," + PatientId + ")\"></i></td>\n");
                        CDRList.append("</tr>");
                        SNo++;
                    }
                }
                rset.close();
                ps.close();
            }


            Query = "SELECT b.MRN, b.FirstName, a.UploadDocumentName, a.FileName, IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T')," +
                    " DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), a.Id FROM " + Database + ".PatientDocUpload a  " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientRegId = b.ID WHERE a.Status = 0 and b.status=0 and a.PatientRegId = " + PatientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                File tmpFile = new File("/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "/" + rset.getString(4));
                boolean exists = tmpFile.exists();
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
                CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                //                if (hostname.trim().equals("romver-01")) {
                //                    CDRList.append("<td align=left><a href=https://rovermd.com:8443/md/md.RegisteredPatients?ActionID=download_direct&fname=" + rset.getString(4) + "&path=" + DocumentPath + " target=\"_blank\">" + rset.getString(4) + "</a></td>\n");
                //                } else {
                //
                //                }
                CDRList.append("<td align=left ><i class=\"fa fa-trash\" onclick=\"DeleteDocument(" + rset.getInt(6) + ")\"></i></td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();


            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientList", String.valueOf(PatientList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("DOS", DOS.toString());
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/UploadPatientsDocsLabs.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
