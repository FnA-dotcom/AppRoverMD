// 
// Decompiled by Procyon v0.5.36
// 

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("Duplicates")
public class ChartsUpload extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";
    static String MRN = "";
    static String ChiefComplaint = "";
    static String DOB = "";
    static String firstname = "";
    static String lastname = "";
    static String FileName = "";
    private Connection conn = null;

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
        String ActionID;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        Connection NewConn = null;
        Services supp = new Services();
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

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

            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            String ConnString = "jdbc:mysql://54.167.174.84/oe?user=909090XXXZZZ1&password=990909090909ABC1";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            NewConn = DriverManager.getConnection(ConnString);
            if (NewConn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "Click on Chart Upload Option", FacilityIndex);
                    GetInput(request, out, conn, context, response, DatabaseName, FacilityIndex, UserId);
                    break;
                case "GetChartData":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "View the information of the chart data", FacilityIndex);
                    GetChartData(request, out, conn, context, response, DatabaseName, FacilityIndex, UserId, NewConn);
                    break;
                case "SaveData":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "Save the data From Upload Chart", FacilityIndex);
                    SaveData(request, out, conn, context, FacilityIndex, UserId, NewConn, DatabaseName);
                    break;
                case "MoveDir":
                    MoveDir(request, out);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
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

    public void handleRequestold(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Statement stmt = null;
        ResultSet rset = null;
        Connection conn = null;
        Connection NewConn = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        int ClientId = 0;
        String Database = "";
        String Query = "";
        String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);

        try {
            Cookie[] cookies = request.getCookies();
            UserId = "";

            for (Cookie cooky : cookies) {
                String cName = cooky.getName();
                String cValue = cooky.getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }

            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                ClientId = rset.getInt(1);
            rset.close();
            stmt.close();


            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();

            String ConnString = "jdbc:mysql://54.167.174.84/oe?user=909090XXXZZZ1&password=990909090909ABC1";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            NewConn = DriverManager.getConnection(ConnString);
        } catch (Exception var23) {
            out.println(var23.getMessage());
        }

        switch (ActionID) {
            case "GetInput":
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "Click on Chart Upload Option", ClientId);
                this.GetInput(request, out, conn, context, response, Database, ClientId, UserId);
                break;
            case "GetChartData":
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "View the information of the chart data", ClientId);
                this.GetChartData(request, out, conn, context, response, Database, ClientId, UserId, NewConn);
                break;
            case "SaveData":
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Charts Upload", "Save the data From Upload Chart", ClientId);
                this.SaveData(request, out, conn, context, ClientId, UserId, NewConn, Database);
                break;
        }

        try {
            conn.close();
        } catch (Exception var22) {
            ;
        }

        out.flush();
        out.close();
    }

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String Database, int ClientId, String UserId) {
        try {
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Date = "";
            int ClientIndex = 0;
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientIndex);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientIndex);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientIndex);

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Forms/ChartsUploadInput.html");
        } catch (Exception ex) {
            System.out.println("Error-1 : " + ex.getMessage());
        }
    }

    private void GetChartData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, HttpServletResponse response, String Database, int ClientId, String UserId, Connection newConn) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String DirectoryName = "";
        int FoundData = 0;

        String myQuery = "";
        if (ClientId == 8) {
            DirectoryName = "Orange";
        } else if (ClientId == 9) {
            DirectoryName = "Victoria";
        } else if (ClientId == 10) {
            DirectoryName = "Odessa";
        } else if (ClientId == 12) {
            DirectoryName = "SAustin";
        } else if (ClientId == 15) {
            DirectoryName = "Sublime";
        }
        final String Path = "/sftpdrive/AdmissionBundlePdf/Attachment/" + DirectoryName + "";
        final String UploadPath = String.valueOf(String.valueOf(Path)) + "/";
        boolean FileFound = false;
        byte[] Data = null;
        String key = "";

        StringBuilder DataDisplay = new StringBuilder();
        StringBuilder PatientDisplay = new StringBuilder();
        StringBuilder VisitDataDisplay = new StringBuilder();
        StringBuilder ChartAlertDisplay = new StringBuilder();
        StringBuilder PatientAlertDisplay = new StringBuilder();
        StringBuilder VisitAlertDisplay = new StringBuilder();

        try {
            final Dictionary d = this.doUpload(request, response, out);
            final Enumeration en = d.keys();
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                if (key.endsWith(".pdf") || key.endsWith(".PDF")) {
                    FileName = key;
                    FileFound = true;
                    ByteArrayOutputStream baos = null;
                    baos = (ByteArrayOutputStream) d.get(key);
                    Data = baos.toByteArray();
                }
                if (FileFound) {
                    //out.println(FileName);
                    FileName = FileName.replaceAll("\\s+", "");
                    File fe = new File(String.valueOf("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/TempChartsUpload/") + FileName);
                    if (fe.exists()) {
                        fe.delete();
                    }
                    final FileOutputStream fouts = new FileOutputStream(fe);
                    fouts.write(Data);
                    fouts.flush();
                    fouts.close();
                }
                //out.println("File Uploaded Successfully");
            }

            boolean readable = false;
            readable = ReadPdfGetData(FileName, String.valueOf("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/TempChartsUpload/"));
            if (readable) {
/*                out.println("epowerdocdate: " + printabledate + "<br>");
                out.println("Acct: " + Acct + "<br>");
                out.println("DOS: " + DOS + "<br>");
                out.println("MRN: " + MRN + "<br>");
                out.println("chiefComplaint: " + ChiefComplaint + "<br>");
                out.println("firstname: " + firstname + "<br>");
                out.println("lastname:  " + lastname + "<br>");*/

                if (!isNumber(MRN)) {
/*                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "MRN is not present in the given Chart. Please provide a valid MRN!. Current value is MRN : " + MRN + " ");
                    Parser.SetField("FormName", "ChartsUpload");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
                    return;*/
                    int _MRN = 0;
                    Query = "Select MRN from " + Database + ".PatientReg order by ID desc limit 1 ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        _MRN = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    if (String.valueOf(_MRN).length() == 0) {
                        _MRN = 310001;
                    } else if (String.valueOf(_MRN).length() == 4) {
                        _MRN = 310001;
                    } else if (String.valueOf(_MRN).length() == 8) {
                        _MRN = 310001;
                    } else if (String.valueOf(_MRN).length() == 6) {
                        ++_MRN;
                    }
                    MRN = String.valueOf(_MRN);
                }

                //File Already exist on the basis of MRN and DOS and Acct#
                Query = "Select count(*) from oe.filelogs_sftp where MRN = '" + MRN + "' AND dosdate = '" + DOS + "' AND acc = '" + Acct + "' ";
                stmt = newConn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    FoundData = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (FoundData > 0)
                    ChartAlertDisplay.append("<div class=\"alert alert-primary\"> Chart has already been uploaded </div>");


                //Data Table me populate///
                DataDisplay.append("<div class=\"col-12 col-lg-12\">");
                DataDisplay.append("<div class=\"box\">");
                DataDisplay.append("<div class=\"box-header with-border\">");
                DataDisplay.append("<h4 class=\"box-title\">Chart Data</h4>");
                DataDisplay.append("</div><!-- /.box-header -->");
                DataDisplay.append("<div class=\"box-body\">");
                DataDisplay.append("<div class=\"table-responsive\">");
                DataDisplay.append("<table class=\"table mb-0\">");
                DataDisplay.append("<thead class=\"thead-dark\">");
                DataDisplay.append("<tr>");
                DataDisplay.append("<th scope=\"col\">#</th>");
                DataDisplay.append("<th scope=\"col\">First Name</th>");
                DataDisplay.append("<th scope=\"col\">Last Name</th>");
                DataDisplay.append("<th scope=\"col\">MRN</th>");
                DataDisplay.append("<th scope=\"col\">EPower Doc Date</th>");
                DataDisplay.append("<th scope=\"col\">Account#</th>");
                DataDisplay.append("<th scope=\"col\">DOS</th>");


                DataDisplay.append("</tr>");
                DataDisplay.append("</thead>");
                DataDisplay.append("<tbody>");
                DataDisplay.append("<tr>");
                DataDisplay.append("<th scope=\"row\">1</th>");
                DataDisplay.append("<td>" + firstname + "</td>");
                DataDisplay.append("<td>" + lastname + "</td>");
                DataDisplay.append("<td>" + MRN + "</td>");
                DataDisplay.append("<td>" + printabledate + "</td>");
                DataDisplay.append("<td>" + Acct + "</td>");
                DataDisplay.append("<td>" + DOS + "</td>");
                DataDisplay.append("</tr>");
                DataDisplay.append("</tbody>");
                DataDisplay.append("</table>");
                DataDisplay.append("</div>");
                DataDisplay.append("</div><!-- /.box-body -->");
                DataDisplay.append("</div><!-- /.box -->");
                DataDisplay.append("</div>");

                int PatientCount = 0;
                String mFirstName = "";
                String mLastName = "";
                String mMRN = "";
                String mDOB = "";
                String mDateofService = "";
                Query = "SELECT FirstName,LastName,MRN,DOB,DateofService FROM " + Database + ".PatientReg WHERE MRN = " + MRN + " AND " +
                        "FirstName = '" + firstname + "' AND LastName = '" + lastname + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    out.println("Patient Name  " + rset.getString("FirstName") + "<br>");
                    out.println("Patient Name  " + rset.getString("LastName") + "<br>");
                    out.println("Patient Name  " + rset.getString("MRN") + "<br>");
                    out.println("Patient Name  " + rset.getString("DOB") + "<br>");
                    out.println("Patient Name  " + rset.getString("DateofService") + "<br>");
                    mFirstName = rset.getString(1);
                    mLastName = rset.getString(2);
                    mMRN = rset.getString(3);
                    mDOB = rset.getString(4);
                    mDateofService = rset.getString(5);

                    PatientDisplay.append("<div class=\"col-12 col-lg-12\">");
                    PatientDisplay.append("<div class=\"box\">");
                    PatientDisplay.append("<div class=\"box-header with-border\">");
                    PatientDisplay.append("<h4 class=\"box-title\">Patient Data</h4>");
                    PatientDisplay.append("</div><!-- /.box-header -->");
                    PatientDisplay.append("<div class=\"box-body\">");
                    PatientDisplay.append("<div class=\"table-responsive\">");
                    PatientDisplay.append("<table class=\"table mb-0\">");
                    PatientDisplay.append("<thead class=\"thead-dark\">");
                    PatientDisplay.append("<tr>");
                    PatientDisplay.append("<th scope=\"col\">#</th>");
                    PatientDisplay.append("<th scope=\"col\">First Name</th>");
                    PatientDisplay.append("<th scope=\"col\">Last Name</th>");
                    PatientDisplay.append("<th scope=\"col\">MRN</th>");
                    PatientDisplay.append("<th scope=\"col\">DOB</th>");
                    PatientDisplay.append("<th scope=\"col\">DOS</th>");


                    PatientDisplay.append("</tr>");
                    PatientDisplay.append("</thead>");
                    PatientDisplay.append("<tbody>");
                    PatientDisplay.append("<tr>");
                    PatientDisplay.append("<th scope=\"row\">1</th>");
                    PatientDisplay.append("<td>" + mFirstName + "</td>");
                    PatientDisplay.append("<td>" + mLastName + "</td>");
                    PatientDisplay.append("<td>" + mMRN + "</td>");
                    PatientDisplay.append("<td>" + mDOB + "</td>");
                    PatientDisplay.append("<td>" + mDateofService + "</td>");
                    PatientDisplay.append("</tr>");
                    PatientDisplay.append("</tbody>");
                    PatientDisplay.append("</table>");
                    PatientDisplay.append("</div>");
                    PatientDisplay.append("</div><!-- /.box-body -->");
                    PatientDisplay.append("</div><!-- /.box -->");
                    PatientDisplay.append("</div>");
                    PatientCount++;
                }
                rset.close();
                stmt.close();

                if (PatientCount <= 0)
                    PatientAlertDisplay.append("<div class=\"alert alert-warning\">Patient data does not exist. Data will be saved after you click save</div>");

                int VisitCount = 0;
                String vMRN = "";
                String vReasonVisit = "";
                String vVisitNumber = "";
                String vDateofService = "";
                Query = "SELECT MRN,ReasonVisit,VisitNumber,DateofService FROM " + Database + ".PatientVisit WHERE MRN = " + MRN + " ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    vMRN = rset.getString(1);
                    vReasonVisit = rset.getString(2);
                    vVisitNumber = rset.getString(3);
                    vDateofService = rset.getString(4);
                    VisitDataDisplay.append("<div class=\"col-12 col-lg-12\">");
                    VisitDataDisplay.append("<div class=\"box\">");
                    VisitDataDisplay.append("<div class=\"box-header with-border\">");
                    VisitDataDisplay.append("<h4 class=\"box-title\">Visit Data</h4>");
                    VisitDataDisplay.append("</div><!-- /.box-header -->");
                    VisitDataDisplay.append("<div class=\"box-body\">");
                    VisitDataDisplay.append("<div class=\"table-responsive\">");
                    VisitDataDisplay.append("<table class=\"table mb-0\">");
                    VisitDataDisplay.append("<thead class=\"thead-dark\">");
                    VisitDataDisplay.append("<tr>");
                    VisitDataDisplay.append("<th scope=\"col\">#</th>");
                    VisitDataDisplay.append("<th scope=\"col\">MRN</th>");
                    VisitDataDisplay.append("<th scope=\"col\">Reason Visit</th>");
                    VisitDataDisplay.append("<th scope=\"col\">Visit Number</th>");
                    VisitDataDisplay.append("<th scope=\"col\">DOS</th>");


                    VisitDataDisplay.append("</tr>");
                    VisitDataDisplay.append("</thead>");
                    VisitDataDisplay.append("<tbody>");
                    VisitDataDisplay.append("<tr>");
                    VisitDataDisplay.append("<th scope=\"row\">1</th>");
                    VisitDataDisplay.append("<td>" + vMRN + "</td>");
                    VisitDataDisplay.append("<td>" + vReasonVisit + "</td>");
                    VisitDataDisplay.append("<td>" + vVisitNumber + "</td>");
                    VisitDataDisplay.append("<td>" + vDateofService + "</td>");
                    VisitDataDisplay.append("</tr>");
                    VisitDataDisplay.append("</tbody>");
                    VisitDataDisplay.append("</table>");
                    VisitDataDisplay.append("</div>");
                    VisitDataDisplay.append("</div><!-- /.box-body -->");
                    VisitDataDisplay.append("</div><!-- /.box -->");
                    VisitDataDisplay.append("</div>");

                    VisitCount++;
                }
                rset.close();
                stmt.close();

                if (VisitCount == 0)
                    VisitAlertDisplay.append("<div class=\"alert alert-warning\">Visit data does not exist. Data will be saved after you click save</div>");


                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("DataDisplay", DataDisplay.toString());
                Parser.SetField("AlertDisplay", ChartAlertDisplay.toString());

                Parser.SetField("PatientDisplay", PatientDisplay.toString());
                Parser.SetField("PatientAlertDisplay", PatientAlertDisplay.toString());

                Parser.SetField("VisitDataDisplay", VisitDataDisplay.toString());
                Parser.SetField("VisitAlertDisplay", VisitAlertDisplay.toString());


                Parser.SetField("FirstName", firstname);
                Parser.SetField("LastName", lastname);
                Parser.SetField("MRN", MRN);
                Parser.SetField("EPowerDocDate", printabledate);
                Parser.SetField("Account", Acct);
                Parser.SetField("DOS", DOS);
                Parser.SetField("FileName", FileName);
                Parser.SetField("ChiefComplaint", ChiefComplaint);
                Parser.SetField("DOB", DOB);

                Parser.SetField("PatientCount", String.valueOf(PatientCount));
                Parser.SetField("VisitCount", String.valueOf(VisitCount));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/NextChartsUpload.html");
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ChartsUpload");
                Parser.SetField("ActionID", "GetInfo");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exceptions/Error.html");
            }


        } catch (Exception e) {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Cannot Process Chart at the moment. Please try again later!");
            Parser.SetField("FormName", "ChartsUpload");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Error.html");
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            Services.DumException(myQuery, "Upload Chart  001 ", request, e, servletContext);
            //out.println("Error in Upload Chart Function: " + e.getMessage());
            out.close();
            out.flush();
        }
    }

    private void SaveData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, int ClientId, String UserId, Connection NewConn, String database) {
        Statement stmt = null;
        ResultSet rset = null;
        PreparedStatement pStmt = null;

        String Query = "";
        String DirectoryName = "";
        int FoundData = 0;
        String oldDOSDate = "";

        String FirstName = request.getParameter("FirstName").trim();
        String LastName = request.getParameter("LastName").trim();
        String MRN = request.getParameter("MRN").trim();
        String EPowerDocDate = request.getParameter("EPowerDocDate").trim();
        String Account = request.getParameter("Account").trim();
        String DOS = request.getParameter("DOS").trim();
        String FileName = request.getParameter("FileName").trim();
        String ChiefComplaint = request.getParameter("ChiefComplaint").trim();
        String DOB = request.getParameter("DOB").trim();

        int PatientCount = Integer.parseInt(request.getParameter("PatientCount"));
        int VisitCount = Integer.parseInt(request.getParameter("VisitCount"));

        if (ClientId == 8) {
            DirectoryName = "Orange";
        } else if (ClientId == 9) {
            DirectoryName = "Victoria";
        } else if (ClientId == 10) {
            DirectoryName = "Odessa";
        } else if (ClientId == 12) {
            DirectoryName = "SAustin";
        } else if (ClientId == 15) {
            DirectoryName = "Sublime";
        }

        String ExtendedMRN = "";
        if (String.valueOf(ClientId).length() == 1) {
            ExtendedMRN = "100" + ClientId + MRN;
        } else if (String.valueOf(ClientId).length() == 2) {
            ExtendedMRN = "10" + ClientId + MRN;
        } else if (String.valueOf(ClientId).length() == 3) {
            ExtendedMRN = "1" + ClientId + MRN;
        }

        try {

            Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + ClientId;
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();

//            Query = "Select count(*) from oe.filelogs_sftp where MRN = '" + MRN + "' AND acc = '" + Account + "' ";
            //out.println("First Query " + Query + "<br> ");
            Query = "Select count(*) from oe.filelogs_sftp where MRN = '" + MRN + "' AND dosdate = '" + DOS + "' AND acc = '" + Account + "' ";
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundData = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (PatientCount == 0) {
                int CalculatedAge = 0;
                Query = "SELECT (CASE WHEN date_format(DOB,'%m-%d') = date_format(curdate(),'%m-%d')  " +
                        "THEN (floor(DATEDIFF(date_format(now(),'%Y-%m-%d'), STR_TO_DATE(DOB, '%Y-%m-%d'))/365.25)) + 1  " +
                        "ELSE floor(DATEDIFF(date_format(now(),'%Y-%m-%d'), STR_TO_DATE(DOB, '%Y-%m-%d'))/365.25) END) AS Age " +
                        "FROM " + database + ".PatientReg   WHERE MRN=" + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    CalculatedAge = rset.getInt(1);
                }

                rset.close();
                stmt.close();


                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + database + ".PatientReg (ClientIndex,FirstName,LastName ,DOB,Age,ReasonVisit," +
                                "CreatedDate,CreatedBy, MRN, Status, DateofService, ExtendedMRN, MiddleInitial,PhNumber) " +
                                "VALUES (?,?,?,?,?,?,NOW(),?,?,0,?,?,'','') ");
                MainReceipt.setInt(1, ClientId);
                MainReceipt.setString(2, FirstName);
                MainReceipt.setString(3, LastName);
                MainReceipt.setString(4, DOB);
                MainReceipt.setInt(5, CalculatedAge);
                MainReceipt.setString(6, ChiefComplaint);
                MainReceipt.setString(7, UserId);
                MainReceipt.setString(8, MRN);
                MainReceipt.setString(9, DOS);
                MainReceipt.setString(10, ExtendedMRN);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }

            if (VisitCount == 0) {
                int PatientRegId = 0;
                try {
                    Query = "Select max(ID) from " + database + ".PatientReg ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + database + ".PatientVisit(MRN,PatientRegId,ReasonVisit,VisitNumber,DoctorId,DateofService,CreatedDate," +
                                    "CreatedBy) VALUES (?,?,?,1,NULL,now(),now(),?) ");
                    MainReceipt.setString(1, MRN);
                    MainReceipt.setInt(2, PatientRegId);
                    MainReceipt.setString(3, ChiefComplaint);
                    MainReceipt.setString(4, "OutPatient");
                    MainReceipt.executeUpdate();
                    MainReceipt.close();
                } catch (Exception e) {
                    out.println("Error 3- :" + e.getMessage());
                }
            } else {
                Query = "UPDATE " + database + ".PatientVisit SET VisitNumber = VisitNumber+1 " +
                        " WHERE MRN = '" + MRN + "' AND DateofService = '" + DOS + "' ";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }

            Query = "SELECT ifnull(substr(MAX(dosdate),1,16),'') AS DOSDate, ifnull(target,'') AS Target, ifnull(filename,'') AS FileName," +
                    "IFNULL(DATE_FORMAT(dosdate,'%Y%m%d%h%i'),'29000101') AS MyDate  " +
                    "FROM oe.filelogs_sftp " +
                    "WHERE MRN = '" + MRN + "' ORDER BY dosdate";
            //out.println("Sec Query " + Query + "<br> ");
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                oldDOSDate = rset.getString(1).trim() + ":00";
            }
            rset.close();
            stmt.close();

//YES Condition
            if (FoundData > 0) {
                if (!oldDOSDate.equals(DOS)) {
                    //out.println("In First IF <br>");
                    //insert6 data here new row here
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.filelogs_sftp (target,entrydate,clientdirectory,filename,acc,dosdate,epowertime," +
                                    "processed,processby,filestatus,liststatus,firstname,lastname,MRN) " +
                                    " VALUES (?,NOW(),?,?,?,?,?,0,0,0,0,?,?,?)");
                    pStmt.setString(1, DirectoryName);
                    pStmt.setInt(2, ClientId);
                    pStmt.setString(3, FileName);
                    pStmt.setString(4, Account);
                    pStmt.setString(5, DOS);
                    pStmt.setString(6, EPowerDocDate);
                    pStmt.setString(7, FirstName);
                    pStmt.setString(8, LastName);
                    pStmt.setString(9, MRN);

                    pStmt.executeUpdate();
                    pStmt.close();

                    //My Log Insertion Table
                    //10-Dec-2020
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                    "Status,Account,ClientId, OldDosDate, DOSDate) " +
                                    " VALUES (?,?,?,?,NOW(),0,?,?,?,?)");
                    pStmt.setString(1, FileName);
                    pStmt.setString(2, MRN);
                    pStmt.setString(3, EPowerDocDate);
                    pStmt.setString(4, UserId);
                    pStmt.setString(5, Account);
                    pStmt.setInt(6, ClientId);
                    pStmt.setString(7, oldDOSDate);
                    pStmt.setString(8, DOS);

                    pStmt.executeUpdate();
                    pStmt.close();

                    NewConn.close();
                }
                //Noting to do
                else {
//                    out.println("In First ELSE <br>");
                    //Just copy the file on the current server

                    //My Log Insertion Table
                    //10-Dec-2020
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                    "Status,Account,ClientId,OldDosDate,DOSDate) " +
                                    " VALUES (?,?,?,?,NOW(),0,?,?,?,?)");
                    pStmt.setString(1, FileName);
                    pStmt.setString(2, MRN);
                    pStmt.setString(3, EPowerDocDate);
                    pStmt.setString(4, UserId);
                    pStmt.setString(5, Account);
                    pStmt.setInt(6, ClientId);
                    pStmt.setString(7, oldDOSDate);
                    pStmt.setString(8, DOS);

                    pStmt.executeUpdate();
                    pStmt.close();
                }
            }
            //No Condition
            else {
//                out.println("In Second ELSE <br>");
                //Insert into sptf table
                pStmt = NewConn.prepareStatement(
                        "INSERT INTO oe.filelogs_sftp (target,entrydate,clientdirectory,filename,acc,dosdate,epowertime," +
                                "processed,processby,filestatus,liststatus,firstname,lastname,MRN) " +
                                " VALUES (?,NOW(),?,?,?,?,?,0,0,0,0,?,?,?)");
                pStmt.setString(1, DirectoryName);
                pStmt.setInt(2, ClientId);
                pStmt.setString(3, FileName);
                pStmt.setString(4, Account);
                pStmt.setString(5, DOS);
                pStmt.setString(6, EPowerDocDate);
                pStmt.setString(7, FirstName);
                pStmt.setString(8, LastName);
                pStmt.setString(9, MRN);

                pStmt.executeUpdate();
                pStmt.close();

                //My Log Insertion Table
                //10-Dec-2020
/*                pStmt = NewConn.prepareStatement(
                        "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                "Status,Account,ClientId,OldDosDate,DOSDate) " +
                                " VALUES (?,?,?,?,NOW(),0,?,?,?,?,?)");
                pStmt.setString(1, FileName);
                pStmt.setString(2, MRN);
                pStmt.setString(3, EPowerDocDate);
                pStmt.setString(4, UserId);
                pStmt.setString(5, Account);
                pStmt.setInt(6, ClientId);
                pStmt.setString(7, oldDOSDate);
                pStmt.setString(8, DOS);

                pStmt.executeUpdate();
                pStmt.close();*/
                pStmt = NewConn.prepareStatement(
                        "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                "Status,Account,ClientId, OldDosDate, DOSDate) " +
                                " VALUES (?,?,?,?,NOW(),0,?,?,?,?)");
                pStmt.setString(1, FileName);
                pStmt.setString(2, MRN);
                pStmt.setString(3, EPowerDocDate);
                pStmt.setString(4, UserId);
                pStmt.setString(5, Account);
                pStmt.setInt(6, ClientId);
                pStmt.setString(7, oldDOSDate);
                pStmt.setString(8, DOS);

                pStmt.executeUpdate();
                pStmt.close();
                //Copy From App.Rover -- 18.207.31.10 to
                //Rover -- 54.167.174.84
                NewConn.close();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Information has been updated!!");
            Parser.SetField("FormName", "ChartsUpload");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");

        } catch (Exception Ex) {
            out.println("Error in Saving Data: " + Ex.getMessage());
        }

    }


    private void SaveDataOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, int ClientId, String UserId, Connection NewConn) {

        Statement stmt = null;
        ResultSet rset = null;
        PreparedStatement pStmt = null;
        String DirectoryName = "";
        String Query = "";
        int FoundData = 0;
        String oldDOSDate = "";
        String NoldDOSDate = "";
        String target = "";
        String filename = "";

        String FirstName = request.getParameter("FirstName").trim();
        String LastName = request.getParameter("LastName").trim();
        String MRN = request.getParameter("MRN").trim();
        String EPowerDocDate = request.getParameter("EPowerDocDate").trim();
        String Account = request.getParameter("Account").trim();
        String DOS = request.getParameter("DOS").trim();
        String FileName = request.getParameter("FileName").trim();
        String ChiefComplaint = request.getParameter("ChiefComplaint").trim();


        if (ClientId == 8) {
            DirectoryName = "Orange";
        } else if (ClientId == 9) {
            DirectoryName = "Victoria";
        } else if (ClientId == 10) {
            DirectoryName = "Odessa";
        } else if (ClientId == 12) {
            DirectoryName = "SAustin";
        } else if (ClientId == 15) {
            DirectoryName = "Sublime";
        }

        try {


            //jdbc:mysql://54.167.174.84/oe?user=oe&password=abc1234oe

            //************ First Check if data is present in the table "filelogs_sftp" on "oe" database or not for this chart if No
//            Then just need to add data in this table and copy file on any location on this server then Copy that file from this server to
//            this server 54.167.174.84 (Billing Server) to the specified chart directory with specified client directory name folder.
//
//            +++++++++IF yes data is present in "filelog_sftp" table then just get the proper data from the table then here comes the checks++++++
//            ******************CHECKS**********
//            If DOSdate date hh:mm only is same then dont add any row just leave the file on the old server another
//            if the DOSDATE date hh:mm is  different from present dos date then add the row and add the file on the server and
//            add the new row with same information but different DOSDATE
//
//            +++++++IF the data is not present in the table then add the row with the target from table and client direcrtory and also copy that file
//            on another server that is 54.167.174.84

            Query = "SELECT directory_1 FROM oe.clients WHERE Id = " + ClientId;
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();

/*            out.println("EPowerDocDate: " + EPowerDocDate + "<br>");
            out.println("Account: " + Account + "<br>");
            out.println("DOS: " + DOS + "<br>");
            out.println("MRN: " + MRN + "<br>");
            out.println("FirstName: " + FirstName + "<br>");
            out.println("LastName:  " + LastName + "<br>");*/

            Query = "Select count(*) from oe.filelogs_sftp where MRN = '" + MRN + "'";
            //out.println("First Query " + Query + "<br> ");
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundData = rset.getInt(1);
            }
            rset.close();
            stmt.close();

//            out.println("FoundData " + FoundData + "<Br>");

            Query = "SELECT ifnull(substr(MAX(dosdate),1,16),'') AS DOSDate, ifnull(target,'') AS Target, ifnull(filename,'') AS FileName," +
                    "IFNULL(DATE_FORMAT(dosdate,'%Y%m%d%h%i'),'29000101') AS MyDate  " +
                    "FROM oe.filelogs_sftp " +
                    "WHERE MRN = '" + MRN + "' ORDER BY dosdate";
            //out.println("Sec Query " + Query + "<br> ");
            stmt = NewConn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                oldDOSDate = rset.getString(1).trim() + ":00";
                target = rset.getString(2).trim();
                filename = rset.getString(3).trim();
                NoldDOSDate = rset.getString(4).trim();
            }
            rset.close();
            stmt.close();

//            out.println("oldDOSDate " + oldDOSDate + "<br>");
//            out.println(" DOS " + DOS + "<br>");

            //YES Condition
            if (FoundData > 0) {
                if (!oldDOSDate.equals(DOS)) {
                    out.println("In First IF <br>");
                    //insert6 data here new row here
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.filelogs_sftp (target,entrydate,clientdirectory,filename,acc,dosdate,epowertime," +
                                    "processed,processby,filestatus,liststatus,firstname,lastname,MRN) " +
                                    " VALUES (?,NOW(),?,?,?,?,?,0,0,0,0,?,?,?)");
                    pStmt.setString(1, DirectoryName);
                    pStmt.setInt(2, ClientId);
                    pStmt.setString(3, FileName);
                    pStmt.setString(4, Account);
                    pStmt.setString(5, DOS);
                    pStmt.setString(6, EPowerDocDate);
                    pStmt.setString(7, FirstName);
                    pStmt.setString(8, LastName);
                    pStmt.setString(9, MRN);

                    pStmt.executeUpdate();
                    pStmt.close();

                    //My Log Insertion Table
                    //10-Dec-2020
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                    "Status,Account,ClientId, OldDosDate, DOSDate) " +
                                    " VALUES (?,?,?,?,NOW(),0,?,?,?,?)");
                    pStmt.setString(1, FileName);
                    pStmt.setString(2, MRN);
                    pStmt.setString(3, EPowerDocDate);
                    pStmt.setString(4, UserId);
                    pStmt.setString(5, Account);
                    pStmt.setInt(6, ClientId);
                    pStmt.setString(7, oldDOSDate);
                    pStmt.setString(8, DOS);

                    pStmt.executeUpdate();
                    pStmt.close();

                    NewConn.close();
                }
                //Noting to do
                else {
//                    out.println("In First ELSE <br>");
                    //Just copy the file on the current server

                    //My Log Insertion Table
                    //10-Dec-2020
                    pStmt = NewConn.prepareStatement(
                            "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                    "Status,Account,ClientId,OldDosDate,DOSDate) " +
                                    " VALUES (?,?,?,?,NOW(),0,?,?,?,?)");
                    pStmt.setString(1, FileName);
                    pStmt.setString(2, MRN);
                    pStmt.setString(3, EPowerDocDate);
                    pStmt.setString(4, UserId);
                    pStmt.setString(5, Account);
                    pStmt.setInt(6, ClientId);
                    pStmt.setString(7, oldDOSDate);
                    pStmt.setString(8, DOS);

                    pStmt.executeUpdate();
                    pStmt.close();
                }
            }
            //No Condition
            else {
//                out.println("In Second ELSE <br>");
                //Insert into sptf table
                pStmt = NewConn.prepareStatement(
                        "INSERT INTO oe.filelogs_sftp (target,entrydate,clientdirectory,filename,acc,dosdate,epowertime," +
                                "processed,processby,filestatus,liststatus,firstname,lastname,MRN) " +
                                " VALUES (?,NOW(),?,?,?,?,?,0,0,0,0,?,?,?)");
                pStmt.setString(1, DirectoryName);
                pStmt.setInt(2, ClientId);
                pStmt.setString(3, FileName);
                pStmt.setString(4, Account);
                pStmt.setString(5, DOS);
                pStmt.setString(6, EPowerDocDate);
                pStmt.setString(7, FirstName);
                pStmt.setString(8, LastName);
                pStmt.setString(9, MRN);

                pStmt.executeUpdate();
                pStmt.close();

                //My Log Insertion Table
                //10-Dec-2020
                pStmt = NewConn.prepareStatement(
                        "INSERT INTO oe.TabishChartUpload (FileName,MRN,EPowerDocDate,UploadedBy,UploadedDate," +
                                "Status,Account,ClientId,OldDosDate,DOSDate) " +
                                " VALUES (?,?,?,?,NOW(),0,?,?,?,?,?)");
                pStmt.setString(1, FileName);
                pStmt.setString(2, MRN);
                pStmt.setString(3, EPowerDocDate);
                pStmt.setString(4, UserId);
                pStmt.setString(5, Account);
                pStmt.setInt(6, ClientId);
                pStmt.setString(7, oldDOSDate);
                pStmt.setString(8, DOS);

                pStmt.executeUpdate();
                pStmt.close();

                //Copy From App.Rover -- 18.207.31.10 to
                //Rover -- 54.167.174.84
                NewConn.close();
            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Your Chart has been uploaded!");
            Parser.SetField("FormName", "ChartsUpload");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");

        } catch (Exception Ex) {
            out.println("Error in Saving Data: " + Ex.getMessage());
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


    private static boolean ReadPdfGetData(String FileName, String Path) {

        try {
            DOS = "";
            Acct = "";
            printabledate = "";
            MRN = "";
            ChiefComplaint = "";
            firstname = "";
            lastname = "";

            try (PDDocument document = PDDocument.load(new File(Path + "" + FileName))) {
                document.getPage(0);
                document.getClass();

                if (!document.isEncrypted()) {

                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    stripper.setSortByPosition(true);

                    PDFTextStripper tStripper = new PDFTextStripper();
                    tStripper.getStartPage();

                    String pdfFileInText = tStripper.getText(document);
//                System.out.println("pdfFileInText: " + pdfFileInText);
                    int ii = 0;
                    int iChComplaint = 0;
                    int count = 1;   //Just to indicate line number
                    // split by whitespace
                    String lines[] = pdfFileInText.split("\\r?\\n");

                    for (String line : lines) {

                        if (line.startsWith("Acct #:")) {
                            String AcctRaw = line;
                            String AcctArr[] = AcctRaw.split("\\s+");
                            Acct = AcctArr[2];
                            String printabledateRaw = AcctArr[4];
                            String printabledateArr[] = printabledateRaw.split("\\/");
                            printabledate = printabledateArr[2] + "-" + printabledateArr[0] + "-" + printabledateArr[1] + " " + AcctArr[5] + ":00";
                        }
                        if (line.startsWith("DOS:")) {
                            String DOSRaw = line;
                            String DOSArr[] = DOSRaw.split("\\s+");
                            DOS = DOSArr[1];
                            String DOSFormatArr[] = DOS.split("\\/");
                            DOS = DOSFormatArr[2] + "-" + DOSFormatArr[0] + "-" + DOSFormatArr[1] + " " + DOSArr[2] + ":00";
                        }
                        if (line.startsWith("MRN:")) {
                            String MRNRaw = line;
                            String MRNArr[] = MRNRaw.split("\\s+");
                            MRN = MRNArr[1];
                        }
                        if (line.startsWith("Patient:")) {
                            String PatientRaw = line;
                            String PatientArr[] = PatientRaw.split("\\:");
                            String Name = PatientArr[1];
                            String NameArr[] = Name.split("\\,");
                            firstname = NameArr[1];
                            lastname = NameArr[0];
                        }
                        if (line.startsWith("DOB:")) {
                            String DOBArr[] = line.split("\\s+");
                            String dOB = DOBArr[1];
                            String SMonth = dOB.substring(0, 2);
                            String SDay = dOB.substring(3, 5);
                            String SYear = dOB.substring(6, 10);
                            DOB = SYear + "-" + SMonth + "-" + SDay;
                        }
                        if (count == 11) {
                            if (!line.startsWith("CHIEF COMPLAINT:") && !line.startsWith("TRIAGE")) {
                                ChiefComplaint = line;
//                                System.out.println("%%%%%%%%% COUNT VAL " + count + "%%%%%%%%%%%%%");
//                                System.out.println("ChiefComplaint : " + ChiefComplaint);
//                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                break;
                            }
                        }
                        if (count == 12) {
                            if (!line.startsWith("CHIEF COMPLAINT:")) {
                                ChiefComplaint = line;
//                                System.out.println("%%%%%%%%% COUNT VAL " + count + "%%%%%%%%%%%%%");
//                                System.out.println("ChiefComplaint : " + ChiefComplaint);
//                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                break;
                            }
                        }
                        if (count == 13) {
                            if (!line.startsWith("CHIEF COMPLAINT:")) {
                                ChiefComplaint = line;
//                                System.out.println("%%%%%%%%% COUNT VAL " + count + "%%%%%%%%%%%%%");
//                                System.out.println("ChiefComplaint : " + ChiefComplaint);
//                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                break;
                            }
                        }
/*                        if (line.startsWith("CHIEF COMPLAINT:")) {
                            System.out.println("^^^^^^^^^^^^^^^^^^^^");
                            System.out.println("ChiefComplaint : " + line);
                            System.out.println("^^^^^^^^^^^^^^^^^^^^");
                            if (count == 11) {
                                System.out.println("$$$FIRST $$$$$COUNT VAL " + count + "$$$$$$$$$$");
                                System.out.println("ChiefComplaint : " + line);
                                System.out.println("$$$$$$$$$$$$$$$$$");
                                break;
                            }
                            if (count == 12) {
                                System.out.println("%%%SECOND%%%%%% COUNT VAL " + count + "%%%%%%%%%%%%%");
                                System.out.println("ChiefComplaint : " + line);
                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                break;
                            }
                            if (count == 13) {
                                System.out.println("******THIRD*** COUNT VAL " + count + "***************");
//                                ChiefComplaint = line;
                                System.out.println("ChiefComplaint : " + line);
                                System.out.println("********* ***************");
                                break;
                            }
                        }*/
/*                        if (count == 11) {
                            System.out.println("$$$$$$$$$$$$$$$$$$");
                            System.out.println("ChiefComplaint : " + line);
                            System.out.println("$$$$$$$$$$$$$$$$$");
                        }*/
/*                        if (count == 12) {
                            if (!line.startsWith("CHIEF")) {
                                ChiefComplaint = line;
*//*                                System.out.println("%%%%%%%%% COUNT VAL " + count + "%%%%%%%%%%%%%");
                                System.out.println("ChiefComplaint : " + ChiefComplaint);
                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");*//*
                                break;
                            } else {
                                System.out.println("$$$$$$$$ IN ELSE COUNT VAL " + count + "$$$$$$$$$$");
                                System.out.println("ChiefComplaint : " + line);
                                System.out.println("$$$$$$$$$$$$$$$$$");
                            }
                        }
                        if (count == 13) {
//                            System.out.println("********* COUNT VAL " + count + "***************");
                            ChiefComplaint = line;
//                            System.out.println("ChiefComplaint : " + ChiefComplaint);
//                            System.out.println("********* ***************");
                            break;
                        }*/

                        count++;
                    }
                } else {
                    return false;

                }
            }

        } catch (Exception ee) {
            System.out.println(ee.getLocalizedMessage());
            return false;

        }
        return true;
    }

    private void MoveDir(HttpServletRequest request, PrintWriter out) {
        try {
            out.println("In Class <br> ");
/*            String cmd = "scp -i /opt/dam1.pem -r DENTON, DIONNE.pdf centos@roverlocal:/opt";
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                out.println(line);
            }*/
            String SSHStartup = "centos@18.207.31.10";
            out.println("Running command:  ssh " + SSHStartup);
//            Process p = Runtime.getRuntime().exec(new String[]{"scp -i /opt/dam1.pem -r /opt/File1.pdf centos@roverlocal:/opt ssh " + SSHStartup});
//            Process p1 = Runtime.getRuntime().exec(new String[]{"sudo scp -i /opt/dam1.pem -r /opt/adminOddasa_AetnaInsurance.pdf centos@roverlocal:/opt/filetest/"});


            //String[] command = {"sudo, -c , ssh " + SSHStartup};
            //String[] command1 = {"sudo", "scp", "-i", "/opt/dam1.pem", "-r", "/opt/adminOddasa_AetnaInsurance.pdf", "centos@roverlocal:/opt/filetest", "ssh " + SSHStartup};
//            String[] command1 = {"sudo", "scp", "-i", "/opt/dam1.pem", "-r", "/opt/adminOddasa_AetnaInsurance.pdf", "centos@roverlocal:/opt/filetest", "ssh "};
            String[] command1 = {"sudo su root scp -i /opt/dam1.pem -r /opt/adminOddasa_AetnaInsurance.pdf centos@roverlocal:/opt/filetest ssh "};
//            out.println(Arrays.toString(command1));
            Process p = Runtime.getRuntime().exec(command1);
            p.waitFor();

/*            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            StringBuffer output = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }*/

        } catch (Exception e) {
            out.println("Unable to process request ... " + e.getMessage());
        }
    }

    // Returns true if s is
    // a number else false
    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }

/*    public void MoveDir(HttpServletRequest request, PrintWriter out) {

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect("54.167.174.84", "22");
            ftpClient.login("centos");
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File sourceFile = new File("/opt/adminOddasa_AetnaInsurance.pdf");
            InputStream inputStream = new FileInputStream(sourceFile);

            boolean done = ftpClient.storeFile("filename which receiver get", inputStream);
            inputStream.close();
            if (done) {
               out.println("file is uploaded successfully..............");
            }

        } catch (IOException e) {
            out.println("Exception IO " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                out.println("Exception occured while ftp logout/disconnect : " + e.getMessage());
            }
        }

    }*/
}
