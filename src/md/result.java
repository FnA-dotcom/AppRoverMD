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
import java.io.*;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class result extends HttpServlet {
/*    private Connection conn = null;
    private ResultSet rset = null;
    private String Query = "";
    private Statement stmt = null;
    private PreparedStatement pStmt = null;
    */


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
        Connection conn = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        try {
            Parsehtm Parser;
            ActionID = request.getParameter("ActionID");
            System.out.println("ROVER LAB ACTION " + ActionID);
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    GetInput(request, out, conn, context);
                    break;
                case "VerifyDOB":
                    VerifyDOB(request, out, conn, context);
                    break;
                case "getResultPdf":
                    getResultPdf(request, out, conn, response, "roverlab", "roverlab");
                    break;
                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (handleRequest)", context, e, "PatientRegRoverLab", "handleRequest", conn);
            Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientRegRoverLab ** (handleRequest -- SqlException)", context, e, "PatientRegRoverLab", "handleRequest", conn);
                Services.DumException("PatientRegRoverLab", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext)  {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query1 = "";
        String Query2 = "";
        String BundleFnName = "";
        String LabelFnName = "";
        int PatientCount = 0;
        int VictoriaDetailsFound = 0;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder RoverLab = new StringBuilder();

        StringBuilder locationList = new StringBuilder();
        StringBuilder stageList = new StringBuilder();
        StringBuilder statusList = new StringBuilder();
        String Oid = request.getParameter("oid") != null ? request.getParameter("oid").trim() : null;
        String m = request.getParameter("m") != null ? request.getParameter("m").trim() : null;

        int SNo = 1;
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Oid", Oid);
            Parser.SetField("m", m);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Result/Result_ROVERLAB.html");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void VerifyDOB(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext)  {
        System.out.println("inside VerifyDOB");
        String oid = request.getParameter("oid").trim();
        String mrn = request.getParameter("mrn").trim();
        String dob = request.getParameter("dob".trim());
        ResultSet rset = null;
        boolean matchFound = false;
        StringBuffer Result = new StringBuffer();
        Integer PatRegIdx = 0;

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Count(*),a.Id FROM roverlab.PatientReg a LEFT JOIN roverlab.TestOrder b ON a.Id=b.PatRegIdx WHERE a.MRN=? AND b.OrderNum=? AND a.DOB=?");
            ps.setString(1, mrn);
            ps.setString(2, oid);
            ps.setString(3, dob);
            System.out.println("Query -> "+ps.toString());
            rset = ps.executeQuery();
            if(rset.next()){
                if(rset.getInt(1)==1){
                    matchFound = true;
                    PatRegIdx = rset.getInt(2);
                }
            }
            rset.close();
            ps.close();

            if(matchFound){
                ps = conn.prepareStatement("SELECT IFNULL(b.TestName,''),IFNULL(d.Result,'PENDING'),IFNULL(a.Id,''),IFNULL(a.OrderId,'') FROM roverlab.Tests a " +
                        "  LEFT JOIN roverlab.ListofTests b ON a.TestIdx=b.id" +
                        "  LEFT JOIN roverlab.TestOrder c ON a.OrderId=c.id" +
                        "  LEFT JOIN roverlab.ListofTestResults d ON a.TestStatus=d.id" +
                        "  WHERE c.OrderNum=?");
                ps.setString(1,oid);
                System.out.println("Query -> "+ps.toString());
                rset = ps.executeQuery();
                while(rset.next()){
                    Result.append("<tr>\n" +
                            "<td>"+mrn+"</td>\n" +
                            "<td>"+rset.getString(1)+"</td>\n" +
                            "<td>"+rset.getString(2)+"</td>\n");
                    if(rset.getString(2).equals("PENDING")){
                        Result.append("<td><a class=\"isDisabled\" data-toggle=\"tooltip\" title=\"Result is Pending. Please Wait!\">View PDF</a></td>\n");
                    }else{
                        Result.append("<td><a href=\"/md/md.result?ActionID=getResultPdf&O_ID="+rset.getString(4)+"&T_ID="+rset.getString(3)+"&PatRegIdx="+PatRegIdx+"\">View PDF</a></td>\n");
                    }
                    Result.append("</tr>");
                }
                rset.close();
                ps.close();
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Result", String.valueOf(Result));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Result/ResultList_ROVERLAB.html");
            }
            else{
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Oid", oid);
                Parser.SetField("m", mrn);
                Parser.SetField("errorMsg","Invalid DOB , Please enter valid DOB");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Result/Result_ROVERLAB.html");
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getMessage();
            out.println("0");
        }
    }

    void getResultPdf(final HttpServletRequest request, final PrintWriter out, final Connection conn, final HttpServletResponse response, final String Database, final String DirectoryName) {

        ResultSet rset = null;
        ResultSet rset2 = null;

        String ID = request.getParameter("PatRegIdx").trim();
        String O_ID = request.getParameter("O_ID").trim();
        String T_ID = request.getParameter("T_ID").trim();
        String Name = null;
        String DOB = null;
        String Gender = null;
        String TestingLocation = null;
        String TestName = null;
        String TestComments = null;
        String TestResult = null;
        String CollectionDateTime = null;
        String ReceivedDateTime = null;
        String ReportedDateTime = null;
        String Physician = null;
        String DateTime = null;
        String filepath = null;
        String filename = null;

        try {

            PreparedStatement ps = conn.prepareStatement("SELECT CONCAT(IFNULL(a.FirstName,'') ,' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'') ), " +
                    " IFNULL(a.DOB,'') , IFNULL(a.Gender,'') , IFNULL(b.Location,''),  DATE_FORMAT(NOW(),'%Y-%m-%d')" +
                    " FROM " + Database + ".PatientReg a " +
                    " LEFT JOIN " + Database + ".Locations b  ON b.id = a.TestingLocation  " +
                    " WHERE a.Id='" + ID + "'");
            rset = ps.executeQuery();
            if (rset.next()) {
                Name = rset.getString(1);
                DOB = rset.getString(2);
                Gender = rset.getString(3);
                TestingLocation = rset.getString(4);
                DateTime = rset.getString(5);
            }
            ps.close();
            rset.close();


            ps = conn.prepareStatement("SELECT IFNULL(b.TestName,'') , IFNULL(a.Narration,'') , IFNULL(d.Result,'PENDING')," +
                    " IFNULL(CollectionDateTime,'N/A') , DATE_FORMAT(IFNULL(ReceivedDateTime,'N/A'),'%Y-%m-%d'), IFNULL(ReportedDateTime,'N/A')," +
                    " CONCAT(IFNULL(c.DoctorsFirstName,'') ,' ',IFNULL(c.DoctorsLastName,'')) " +
                    " FROM " + Database + ".Tests a " +
                    " LEFT JOIN " + Database + ".ListofTests b ON a.TestIdx=b.id " +
                    " LEFT JOIN " + Database + ".DoctorsList c ON a.PhysicianIdx=c.id " +
                    " LEFT JOIN " + Database + ".ListofTestResults d ON a.TestStatus=d.id " +
                    " WHERE a.OrderId='" + O_ID + "' AND a.Id='" + T_ID + "'");
            rset = ps.executeQuery();
            while (rset.next()) {
                TestName = rset.getString(1);
                TestComments = rset.getString(2);
                TestResult = rset.getString(3);
                CollectionDateTime = rset.getString(4);
                ReceivedDateTime = rset.getString(5);
                ReportedDateTime = rset.getString(6);
                Physician = rset.getString(7);

            }
            rset.close();
            ps.close();
//            out.println("Before Getting ... ");

            ps = conn.prepareStatement("SELECT IFNULL(filepath,''),IFNULL(filename,'') FROM roverlab.Tests WHERE OrderId='" + O_ID + "' AND Id='" + T_ID + "'");
            rset = ps.executeQuery();
            if (rset.next()) {
                filepath = rset.getString(1);
                filename = rset.getString(2);
            }
            rset.close();
            ps.close();

            if (filepath.compareTo("") != 0 && filename.compareTo("") != 0 && filename.contains(TestResult)) {
                final File pdfFile = new File(filepath);
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + filename);
                response.setContentLength((int) pdfFile.length());
                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
            }else{
                out.println("No File Found !");
            }



        } catch (Exception e) {
            out.println("Hello ->>> "+e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }


}
