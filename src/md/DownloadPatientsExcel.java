package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("Duplicates")
public class DownloadPatientsExcel extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";
    private Connection conn = null;

    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = null;

    private PreparedStatement pStmt = null;
    private ResultSet rset1;

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
//        Payments payments = new Payments();
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
            switch (ActionID) {
                case "DownloadPatientInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Admin Report for Patients", "Download Admin Report For Patients", FacilityIndex);
                    DownloadPatientInput(request, out, conn, context, response, UserId, DatabaseName, FacilityIndex);
                    break;
                case "DownloadPatientList":
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-Disposition", "attachment; filename=" + UserId + "_RoverPatientsList.xls");
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "DownloadExcelReport", "Download Admin Report Excel Option", FacilityIndex);
                    DownloadPatientList(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
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

    void DownloadPatientInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientIndex", String.valueOf(ClientIndex));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Forms/DownloadPatientInput.html");
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

    void DownloadPatientList(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        int PatientCount = 0;
        int SNo = 1;
        int ClientIndex = 0;
        String ToDate = "";
        String FromDate = request.getParameter("FromDate").trim();
        ToDate = request.getParameter("ToDate").trim();
        ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
        try {
            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();
            Query = "SELECT COUNT(*) FROM " + Database + ".PatientReg";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientCount = rset.getInt(1);
            rset.close();
            stmt.close();
            if (PatientCount > 0) {
                out.println("<table width=100% cellspacing=0 cellpading=0 border=1>");
                out.println("<tr bgcolor=\"#ff0000\"><td colspan=13 class=\"fieldm\" align=center><font face=\"Arial\" color=\"#FFFFFF\"><b>Registered Patient List</b></font></td></tr>\n");
                out.println("<tr bgcolor=\"#ff0000\">");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>SNo</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>MRN</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Patient Name</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Birth</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Ph Number</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Address</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>City</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>State</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>ZipCode</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Email</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>InsuranceType (PayerName)</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Reason Of Visit</b></font></td>");
                out.println("<td class=\"fieldm\"><font face=\"Arial\" color=\"#FFFFFF\"><b>Date of Service</b></font></td></tr>");

                if(ClientId == 9 || ClientId == 28){
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(a.DOB,'%m/%d/%Y'),IFNULL(a.PhNumber,'')," +
                            " IFNULL(a.MRN, 0),IFNULL(z.ReasonVisit, '-'),a.ID, " +
                            " IFNULL(DATE_FORMAT(z.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(z.CreatedDate,'%m/%d/%Y %T'))," +
                            " CASE WHEN a.COVIDStatus = 1 THEN 'POSITIVE' WHEN a.COVIDStatus = 0 THEN 'NEGATIVE' WHEN a.COVIDStatus = - 1 THEN 'NONE' ELSE 'NONE' END, " +
                            " a.SelfPayChk, IFNULL(a.Email, '-'), IFNULL(a.Address, '-'), IFNULL(b.HIPrimaryInsurance, '-'), IFNULL(a.City, '-'), IFNULL(a.State, '-'), " +
                            " IFNULL(a.ZipCode, '-') " +
                            " FROM "+Database+".PatientReg a " +
                            " LEFT JOIN "+Database+".Patient_HealthInsuranceInfo b ON a.ID = b.PatientRegId\n" +
                            "INNER JOIN "+Database+".PatientVisit z ON z.PatientRegId = a.ID " +
                            " WHERE a. STATUS = 0 "+
                            "AND DATE_FORMAT(z.DateofService,'%Y-%m-%d %T') BETWEEN '"+FromDate+" 00:00:00' AND '"+ToDate+" 23:59:59' "+
//                            " AND z.DateofService >= '" + FromDate + " 00:00:00'\n" +
//                            " AND z.DateofService <= '" + ToDate + " 23:59:59'\n" +
                            " ORDER BY a.ID DESC " ;

                }else {
                    Query = " SELECT CONCAT(IFNULL(Title,''),' ',IFNULL(FirstName,''),' ',IFNULL(MiddleInitial,''),' ',IFNULL(LastName,'')), DATE_FORMAT(a.DOB,'%m/%d/%Y'),\n" +
                            "a.PhNumber,\n" +
                            " IFNULL(a.MRN, 0),\n" +
                            " IFNULL(z.ReasonVisit, '-'),\n" +
                            " a.ID,\n" +
                            " IFNULL(\n" +
                            "\tDATE_FORMAT(\n" +
                            "\t\tz.DateofService,\n" +
                            "\t\t'%m/%d/%Y %T'\n" +
                            "\t),\n" +
                            "\tDATE_FORMAT(\n" +
                            "\t\tz.CreatedDate,\n" +
                            "\t\t'%m/%d/%Y %T'\n" +
                            "\t)\n" +
                            "),\n" +
                            " CASE\n" +
                            "WHEN a.COVIDStatus = 1 THEN\n" +
                            "\t'POSITIVE'\n" +
                            "WHEN a.COVIDStatus = 0 THEN\n" +
                            "\t'NEGATIVE'\n" +
                            "WHEN a.COVIDStatus = - 1 THEN\n" +
                            "\t'SUSPECTED'\n" +
                            "ELSE\n" +
                            "\t'NONE'\n" +
                            "END,\n" +
                            " a.SelfPayChk,\n" +
                            " IFNULL(a.Email, '-'),\n" +
                            " IFNULL(a.Address, '-'),\n" +
                            " IFNULL(c.PayerName, '-'),\n" +
                            " IFNULL(a.City, '-'),\n" +
                            " IFNULL(a.State, '-'),\n" +
                            " IFNULL(a.ZipCode, '-')\n" +
                            "FROM " + Database + ".PatientReg a\n" +
                            "LEFT JOIN " + Database + ".InsuranceInfo b ON a.ID = b.PatientRegId\n" +
                            "LEFT JOIN " + Database + ".ProfessionalPayers c ON b.PriInsuranceName = c.Id\n" +
                            "INNER JOIN " + Database + ".PatientVisit z ON z.MRN = a.MRN\n" +
                            "WHERE\n" +
                            "a.STATUS = 0\n" +
                            "AND DATE_FORMAT(z.DateofService,'%Y-%m-%d %T') BETWEEN '"+FromDate+" 00:00:00' AND '"+ToDate+" 23:59:59' "+
//                            "AND z.DateofService >= '" + FromDate + " 00:00:00'\n" +
//                            "AND z.DateofService <= '" + ToDate + " 23:59:59'\n" +
                            "ORDER BY ID DESC";
                }
//                System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {

                    out.println("<tr><td class=\"fieldm\">" + SNo + "</td>");
                    out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");//MRN
                    out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");//Name
                    out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");//DOB
                    out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");//Phone
                    out.println("<td class=\"fieldm\">" + rset.getString(11) + "</td>");//Address
                    out.println("<td class=\"fieldm\">" + rset.getString(13) + "</td>");//City
                    out.println("<td class=\"fieldm\">" + rset.getString(14) + "</td>");//State
                    out.println("<td class=\"fieldm\">" + rset.getString(15) + "</td>");//Zipcode
                    out.println("<td class=\"fieldm\">" + rset.getString(10) + "</td>");//Email
                    out.println("<td class=\"fieldm\">" + rset.getString(12) + "</td>");//InsuranceType
                    out.println("<td class=\"fieldm\">" + rset.getString(5) + "</td>");//Reason Of visit
                    out.println("<td class=\"fieldm\">" + rset.getString(7) + "</td></tr>");//Date of Service

//                    Query1 = "SELECT ReasonVisit,IFNULL(DATE_FORMAT(DateofService,'%m/%d/%Y %T'),DATE_FORMAT(CreatedDate,'%m/%d/%Y %T')) " +
//                            "FROM  "+ Database + ".PatientVisit WHERE MRN = \'"+ rset.getString(4) + "\' " +
//                            "ORDER BY DateofService DESC";
//                    stmt1 = conn.createStatement();
//                    rset1 = stmt1.executeQuery(Query1);
//                    while(rset1.next()){
//                        if(rset1.isFirst()){
//                            out.println("<td class=\"fieldm\">" + rset1.getString(1) + "</td>");//Reason Of visit
//                            out.println("<td class=\"fieldm\">" + rset1.getString(2) + "</td>");//Date of Service
//                            out.println("</tr>");
//                        }else{
//                            SNo++;
//                            out.println("<tr  bgcolor=\"#FFFFFF\">");
//                            out.println("<td class=\"fieldm\">"+SNo+"</td>");
//                            out.println("<td class=\"fieldm\">" + rset.getString(4) + "</td>");//MRN
//                            out.println("<td class=\"fieldm\">" + rset.getString(1) + "</td>");//Name
//                            out.println("<td class=\"fieldm\">" + rset.getString(2) + "</td>");//DOB
//                            out.println("<td class=\"fieldm\">" + rset.getString(3) + "</td>");//Phone
//                            out.println("<td class=\"fieldm\">" + rset.getString(11) + "</td>");//Address
//                            out.println("<td class=\"fieldm\">" + rset.getString(13) + "</td>");//City
//                            out.println("<td class=\"fieldm\">" + rset.getString(14) + "</td>");//State
//                            out.println("<td class=\"fieldm\">" + rset.getString(15) + "</td>");//Zipcode
//                            out.println("<td class=\"fieldm\">" + rset.getString(10) + "</td>");//Email
//                            out.println("<td class=\"fieldm\">" + rset.getString(12) + "</td>");//InsuranceType
//                            out.println("<td class=\"fieldm\">" + rset1.getString(1) + "</td>");//Reason Of visit
//                            out.println("<td class=\"fieldm\">" + rset1.getString(2) + "</td>");//Date of Service
//                            out.println("</tr>");
//                        }
//                    }
//                    rset1.close();
//                    stmt1.close();
                    SNo++;
                }
                rset.close();
                stmt.close();
                out.println("</table>");
            }
        } catch (Exception var11) {
            out.println("Error in Excel Report: " + var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
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
