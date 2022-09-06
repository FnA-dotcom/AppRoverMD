

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class PatientVisit extends HttpServlet {
    //    private Connection conn = null;
    Integer ScreenIndex = 11;

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
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
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
//            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);


/*            if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, this.ScreenIndex)) {
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }*/

            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "SearchPatient":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                    this.SearchPatient(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetPatients":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Get Details", "Get Data for all Matching Old Patients ", FacilityIndex);
                    this.GetPatients(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "GetData":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Get Details", "Get Data for all Matching Old Patients ", FacilityIndex);
                    this.GetData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetData2":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Get Details", "Get Data for all Matching Old Patients ", FacilityIndex);
                    this.GetData2(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "AddNewVisit":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Add New Visit Option", "Click on Add New Visit for Searched Old Patients", FacilityIndex);
                    this.AddNewVisit(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "SaveVisit":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Save Data", "Save the new Information and Create Log for Old Information", FacilityIndex);
                    this.SaveVisit(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, response);
                    break;
                case "ReasonVisits":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Reason Visit", "Get Reason Visit ACcording to CLientindex", FacilityIndex);
                    this.ReasonVisits(request, out, conn, context, DatabaseName, helper);
                    break;
            }
            try {
                conn.close();
            } catch (SQLException e) {
                helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest -- SqlException)", context, e, "PatientVisit", "handleRequest", conn);
                Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest)", context, e, "PatientVisit", "handleRequest", conn);
            Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
/*        finally {
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
        }*/
    }


    private void SearchPatient(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();

        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SearchPatient.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (SearchPatient^^ MES#001)", servletContext, e, "PatientVisit", "SearchPatient", conn);
            Services.DumException("SearchPatient", "PatientVisit ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void GetPatients(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            String Patient = request.getParameter("Patient").trim();
            StringBuffer PatientList = new StringBuffer();
/*            Query = "Select Id, MRN, FirstName, MiddleInitial, LastName, DOB, PhNumber  from " + Database + ".PatientReg " +
                    "where status = 0 and FirstName like '%" + Patient + "%' OR LastName like '%" + Patient + "%'  OR MRN like '%" + Patient + "%' OR PhNumber like '%" + Patient + "%'  OR MiddleInitial like '%" + Patient + "%'";*/
            Query = "Select Id, MRN, IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(DOB,''), IFNULL(PhNumber,''),`Status`\n" +
                    "FROM " + Database + ".PatientReg \n" +
                    "WHERE CONCAT(FirstName,LastName,PhNumber,MRN,IFNULL(DATE_FORMAT(DOB,'%m-%d-%Y'),'')) like '%" + Patient + "%' ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=''> Please Select Below Patient </option>");
            while (rset.next()) {
                if (rset.getInt(8) == 0)
                    PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
                else
                    PatientList.append("<option style=\"background-color:red\" value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            }
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void GetData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int PatientId = Integer.parseInt(request.getParameter("PatientId").trim());
//    String PatientFirstName = null;
//    String PatientLastName = null;
//    String PatientMRN = null;
//    String PhNumber = null;
//    String DOB = null;
//    String SSN = null;
        int SNo = 1;
        try {

//      if(SearchBy == 1){
//        PatientFirstName = request.getParameter("FirstName").trim();
//        Query = "Select Id from "+Database+".PatientReg where FirstName like '%"+PatientFirstName+"%'";
//      }else if( SearchBy == 2){
//        PatientLastName = request.getParameter("LastName").trim();
//        Query = "Select Id from "+Database+".PatientReg where LastName like '%"+PatientLastName+"%'";
//      }else if ( SearchBy == 3){
//        PatientMRN = request.getParameter("MRN").trim();
//        Query = "Select Id from "+Database+".PatientReg where MRN = '"+PatientMRN+"'";
//      }else if(SearchBy == 4){
//        PhNumber = request.getParameter("PhNumber").trim();
//        Query = "Select Id from "+Database+".PatientReg where PhNumber = '"+PhNumber+"'";
//      }else if(SearchBy == 5) {
//        DOB = request.getParameter("DOB").trim();
//        Query = "Select Id from " + Database + ".PatientReg where DOB = '" + DOB + "'";
//      }else if(SearchBy == 6) {
//        SSN = request.getParameter("SSN").trim();
//        Query = "Select Id from " + Database + ".PatientReg where SSN = '" + SSN + "'";
//      }
//      stmt = conn.createStatement();
//      rset = stmt.executeQuery(Query);
////      out.println(Query);
//      while (rset.next()) {
            Query2 = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''), " +
                    " IFNULL(a.ReasonVisit,'-'), IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T'))," +
                    " CASE WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE' " +
                    " WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status" +
                    " from " + Database + ".PatientVisit a " +
                    " Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id where b.ID =  " + PatientId + " order by a.DateofService desc";
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
            while (rset2.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(6) + "</td>\n");
//          if(rset2.getInt(8) == 1){
//            CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-success mb-5\" onclick=\"AddNewVisit("+PatientId+", "+rset2.getInt(8)+")\">Add New Visit</button></td>");
//            //CDRList.append("<td align=left disabled><a href=/md/md.PatientVisit?ActionID=AddNewVisit&ID="+PatientId+">Add New Visit</a></td>\n");
//          }else{
//            CDRList.append("<td align=left><a href=/md/md.PatientVisit?ActionID=AddNewVisit&ID="+PatientId+">Add New Visit</a></td>\n");
//          }

                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-success mb-5\" onclick=\"AddNewVisit(" + PatientId + ", " + rset2.getInt(8) + ")\">AddVisit</button></td>");

                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + PatientId + "," + rset2.getInt(8) + ")\">Update</button></td>\n");

//          CDRList.append("<td align=left><a href=/md/md.PrintLabel4?ActionID=GETINPUT&ID="+rset.getInt(1)+">Print Label</a></td>\n");
//          if(ClientId == 8) {
//            CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUT&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
//          }else if(ClientId == 9){
//            CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTVictoria&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
//          }else if (ClientId == 10){
//            CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTOddasa&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
//          }else if(ClientId == 12) {
//        	  CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTSAustin&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
//          }else if(ClientId == 15) {
//        	  CDRList.append("<td align=left><a href=/md/md.DownloadBundle_OLD_15042021?ActionID=GETINPUTSublime&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
//          }
//          CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=ShowHistory&ID=" + rset.getInt(1) + ">Show History</a></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.PatientReg?ActionID=EditValues&MRN='" + rset2.getString(7).trim() + "'&ClientId="+ClientId+">View/Edit</a></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.PatientInfo?ActionID=GetValues&ID=" + rset.getInt(1) + ">Send to E-Doc</a></td>\n");
                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-warning mb-5\" onclick=\"ReactivePatient(" + PatientId + "," + rset2.getInt(8) + ")\">Reactive</button></td>\n");
//          CDRList.append("<td align=left><a href=/md/md.RegisteredPatients?ActionID=ReActivePatient&ID=" + rset.getInt(1) + ">Re-Activate Patient</a></td>\n");
                SNo++;
            }
            rset2.close();
            stmt2.close();

//      }
//      rset.close();
//      stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SearchPatient.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (GetData^^ MES#002)", servletContext, e, "PatientVisit", "GetData", conn);
            Services.DumException("GetData", "PatientVisit ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void GetData2(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        int PatientId = Integer.parseInt(request.getParameter("PatientId").trim());
        int SNo = 1;
        try {

            Query2 = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''), " +
                    " IFNULL(a.ReasonVisit,'-'), IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T'))," +
                    " CASE WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE' " +
                    " WHEN b.COVIDStatus = -1 THEN 'NONE' ELSE 'UN-EXAMINED' END , a.MRN, b.Status" +
                    " from " + Database + ".PatientVisit a " +
                    " Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id where b.ID =  " + PatientId + " order by a.DateofService desc";
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
            while (rset2.next()) {
                CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(1) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset2.getString(6) + "</td>\n");

                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-success mb-5\" onclick=\"AddNewVisit(" + PatientId + ", " + rset2.getInt(8) + ")\">AddVisit</button></td>");

                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info mb-5\" onclick=\"UpdateInfoPatient(" + PatientId + "," + rset2.getInt(8) + ")\">Update</button></td>\n");
                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-warning mb-5\" onclick=\"ReactivePatient(" + PatientId + "," + rset2.getInt(8) + ")\">Reactive</button></td>\n");
                SNo++;

            }
            rset2.close();
            stmt2.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PatientSearch.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (GetData2^^ MES#003)", servletContext, e, "PatientVisit", "GetData2", conn);
            Services.DumException("GetData2", "PatientVisit ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#003");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }


    private void AddNewVisit(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int MaxVisitNumber = 0;
        String MRN = "";
        String PatientName = "";
        String DOB = "";
        String PhNumber = "";
        String LastReasonVisit = "";
        String LastDateofService = "";
        String LastDoctorName = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer DoctorList = new StringBuffer();
        int PatientRegId = Integer.parseInt(request.getParameter("ID").trim());
        try {

            Query = "Select Id, CONCAT(DoctorsLastName, ' , ', DoctorsFirstName) from " + Database + ".DoctorsList";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DoctorList.append("<option value='-1'>Select Physician</option>");
            while (rset.next()) {
                DoctorList.append("<option value=" + rset.getString(1) + " selected>" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();


            Query = "Select MAX(VisitNumber) from " + Database + ".PatientVisit where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MaxVisitNumber = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = " Select a.MRN, CONCAT(IFNULL(a.Title,''),' ',IFNULL(a.FirstName,''),' ',IFNULL(a.MiddleInitial,''),' ',IFNULL(a.LastName,'')), IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.PhNumber,''), IFNULL(a.ReasonVisit,''), IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),''), " +
                    " CONCAT(IFNULL(c.DoctorsFirstName,''), ' ', IFNULL(c.DoctorsLastName,'')) " +
                    " from " + Database + ".PatientReg a  " +
                    " LEFT JOIN " + Database + ".DoctorsList c on a.DoctorsName = c.Id where a.Id = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                MRN = rset.getString(1);
                PatientName = rset.getString(2);
                DOB = rset.getString(3);
                PhNumber = rset.getString(4);
                LastReasonVisit = rset.getString(5);
                LastDateofService = rset.getString(6);
                LastDoctorName = rset.getString(7);
            }
            rset.close();
            stmt.close();
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("DoctorList", String.valueOf(DoctorList));
            Parser.SetField("MRN", String.valueOf(MRN));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("DOB", String.valueOf(DOB));
            Parser.SetField("PhNumber", String.valueOf(PhNumber));
            Parser.SetField("LastReasonVisit", String.valueOf(LastReasonVisit));
            Parser.SetField("LastDateofService", String.valueOf(LastDateofService));
            Parser.SetField("LastDoctorName", String.valueOf(LastDoctorName));
            Parser.SetField("MaxVisitNumber", String.valueOf(MaxVisitNumber));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AddNewVisit.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (GetData2^^ MES#004)", servletContext, e, "PatientVisit", "GetData2", conn);
            Services.DumException("GetData2", "PatientVisit ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#004");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void SaveVisit(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, HttpServletResponse response) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int VisitNumber = 0;
        String WEB = "";
        String MRN = "";
        int PatientRegId = 0;
        String NewDateofService = "";
        int NewDoctorId = 0;
        String NewReasonVisit = "";
        int VisitId = 0;
        try {
            MRN = request.getParameter("MRN").trim();
            String RequestType = request.getParameter("RequestType").trim();
            PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
            NewDateofService = request.getParameter("NewDateofService").trim();
//            System.out.println("Patient DOS : " + NewDateofService);
            NewDoctorId = Integer.parseInt(request.getParameter("NewDoctorId").trim());
            NewReasonVisit = request.getParameter("ReasonVisit").trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.US);
            LocalDateTime NDOS = LocalDateTime.parse(NewDateofService, formatter);
//            System.out.println("NDOS " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(NDOS));

            if (ClientId == 27 || ClientId == 29) {
                Query = "Select ReasonVisit from " + Database + ".ReasonVisits where Id = " + NewReasonVisit;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    NewReasonVisit = rset.getString(1);
                rset.close();
                stmt.close();
            }

            try {
                Query = "Select MAX(VisitNumber) + 1 from " + Database + ".PatientVisit where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    VisitNumber = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientVisit ** (SaveVisit^^ MES#005)", servletContext, e, "PatientVisit", "SaveVisit", conn);
                Services.DumException("SaveVisit", "PatientVisit ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientVisit");
                Parser.SetField("ActionID", "SearchPatient");
                Parser.SetField("Message", "MES#005");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".PatientVisit (MRN,PatientRegId,ReasonVisit ,VisitNumber,DoctorId,DateofService," +
                                "CreatedDate,CreatedBy) VALUES (?,?,?,?,?,?,now(),?) ");
                MainReceipt.setString(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, NewReasonVisit);
                MainReceipt.setInt(4, VisitNumber);
                MainReceipt.setInt(5, NewDoctorId);
                MainReceipt.setString(6, String.valueOf(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(NDOS)));
                MainReceipt.setString(7, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientVisit ** (SaveVisit^^ MES#006)", servletContext, e, "PatientVisit", "SaveVisit", conn);
                Services.DumException("SaveVisit", "PatientVisit ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientVisit");
                Parser.SetField("ActionID", "SearchPatient");
                Parser.SetField("Message", "MES#006");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }

            try {
                Query = "Select MAX(Id) from " + Database + ".PatientVisit";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    VisitId = rset.getInt(1);
                }
                rset.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "UPDATE " + Database + ".PatientReg SET ReasonVisit = ?, DateofService = ?, DoctorsName = ?, sync = 0,ViewDate = NOW()  WHERE ID = " + PatientRegId);
                MainReceipt.setString(1, NewReasonVisit);
                MainReceipt.setString(2, String.valueOf(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(NDOS)));
                MainReceipt.setInt(3, NewDoctorId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
//                Query = "UPDATE " + Database + ".PatientReg SET ReasonVisit ='" + NewReasonVisit + "', " +
//                        "DateofService = '" + NewDateofService + "', DoctorsName = '" + NewDoctorId + "', sync = 0,ViewDate = NOW() " +
//                        "WHERE ID = " + PatientRegId;
//                stmt = conn.createStatement();
//                stmt.executeUpdate(Query);
//                stmt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in PatientVisit ** (SaveVisit^^ MES#007)", servletContext, e, "PatientVisit", "SaveVisit", conn);
                Services.DumException("SaveVisit", "PatientVisit ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientVisit");
                Parser.SetField("ActionID", "SearchPatient");
                Parser.SetField("Message", "MES#007");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
                return;
            }

            String InsertCOVIDRegReply;
            String Message = "";
            String CDCFlag = "0";
            if (ClientId == 9 && NewReasonVisit != null) {
                NewReasonVisit = NewReasonVisit.replaceAll(" ", "");
                if (NewReasonVisit.toUpperCase().equals("COVIDTESTING")) {
                    InsertCOVIDRegReply = this.InsertCOVIDReg(request, response, out, conn, String.valueOf(PatientRegId));
                    if (Integer.parseInt(InsertCOVIDRegReply) > 0) {
                        Message = " and COVID Form Also Registered Successfully.";
                        CDCFlag = "1";
                    } else {
                        Message = " and COVID Form Not Registered. ";
                        CDCFlag = "0";
                    }
                }
                Query = "Update victoria.PatientVisit set CDCFlag = '" + CDCFlag + "' where Id = " + VisitId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            }

            if (ClientId == 8 || ClientId == 9 || ClientId == 19 || ClientId == 25 || ClientId == 27 || ClientId == 28 || ClientId == 29 || ClientId == 39 || ClientId == 40 ||
                    ClientId == 41 || ClientId == 42 || ClientId == 43) {
                int found = 0;
                Query = "Select Count(*) from " + Database + ".SignRequest where PatientRegId = " + PatientRegId + "";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    found = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (found > 0) {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "INSERT INTO " + Database + ".SignRequest_History " +
                                    "SELECT * FROM " + Database + ".SignRequest " +
                                    "where PatientRegId = " + PatientRegId);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    Query = "Update " + Database + ".SignRequest set isSign = 0 , SignedFrom='VISIT' where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                Query = "Select DirectoryName from oe.clients where ltrim(rtrim(UPPER(Id))) = ltrim(rtrim(UPPER('" + ClientId + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                String DirectoryName = null;
                if (rset.next()) {
                    DirectoryName = rset.getString(1);
                }
                rset.close();
                stmt.close();

                PatientReg2 ptr2 = new PatientReg2();
                PatientReg ptr = new PatientReg();
                DownloadBundle dbundle = new DownloadBundle();
                String temp = "";

                switch (ClientId) {
                    case 8:
                        DownloadBundle orangebndle = new DownloadBundle();
                        temp = orangebndle.GETINPUT_Inside(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT",helper);
                        break;
                    case 9:
                        temp = ptr2.SaveBundle_Victoria(request, out, conn, response, Database, ClientId, DirectoryName, PatientRegId, "VISIT");
                        break;
                    case 19:
                        temp = ptr.SaveBundle_HopeER(request, out, conn, response, Database, ClientId, DirectoryName, PatientRegId, "REGISTRATION", helper);
                        break;
                    case 39:
                        temp = ptr.GETINPUTSchertz(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT");
                        break;
                    case 25:
                        SanMarcosBundle smbundle = new SanMarcosBundle();
                        temp = smbundle.GETINPUTSanMarcos_Inside(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT", helper);
                        break;
                    case 27:
                    case 29:
                        FrontlineBundle obj1 = new FrontlineBundle();
                        temp = obj1.GETINPUTFrontLine_Inside(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT", helper);
                        break;

                    case 28:
                        temp = dbundle.GETINPUTERDallas_BundleCreate(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName,"VISIT",PatientRegId,helper);
                        break;
                    case 40:
                        temp = ptr.GETINPUTfloresville(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT");
                        break;
                    case 41:
                        temp = ptr.GETINPUTwillowbrook(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT", helper);
                        break;
                    case 42:
                        temp = ptr.GETINPUTsummerwood(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT", helper);
                        break;
                    case 43:
                        temp = ptr.GETINPUTheights(request, out, conn, servletContext, response, UserId, Database, ClientId, DirectoryName, PatientRegId, "VISIT", helper);
                        break;
                    default:
                        out.println("Under Development!!!");
                        break;
                }
                String[] arr = temp.split("~");
                String FileName = arr[2];
                String outputFilePath = arr[1];
                String pageCount = arr[0];

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "New Visit has been Created For the MRN : " + MRN + Message + " Please wait for further processing.");
                Parser.SetField("MRN", "DONE");
                Parser.SetField("FormName", "PatientReg");
                Parser.SetField("ActionID", "GetValues&ClientIndex=" + ClientId);
                Parser.SetField("pageCount", String.valueOf(pageCount));
                Parser.SetField("FileName", String.valueOf(FileName));
                Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
                Parser.SetField("outputFilePath", String.valueOf(outputFilePath));
                Parser.SetField("ClientIndex", String.valueOf(ClientId));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/MessageVictoria.html");
            } else {
                if (RequestType.equals("GetValues")) {
                    //redirection in case of external patient
                    Parsehtm Parser = new Parsehtm(request);
                    PreparedStatement ps = conn.prepareStatement("SELECT website from oe.ClientsWebsite where clientID=?");
                    ps.setInt(1, ClientId);

                    rset = ps.executeQuery();
                    if (rset.next()) {
                        WEB = rset.getString(1);
                    }
                    ps.close();
                    rset.close();

                    Parser.SetField("Message", "New Visit has been Created For the MRN : " + MRN);
                    Parser.SetField("WEB", WEB);
                    Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message_SumWill.html");
                    return;
                }

                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserId", String.valueOf(UserId));
                Parser.SetField("Message", "New Visit has been Created For the MRN : " + MRN + Message);
                Parser.SetField("FormName", String.valueOf("PatientUpdateInfo"));
                Parser.SetField("ActionID", String.valueOf("GetInput&ID=" + PatientRegId));
                Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(this.getServletContext())) + "Exception/Message.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** MAIN CATCH (SaveVisit^^ MES#006 - PatRegId --> " + PatientRegId + ")", servletContext, e, "PatientVisit", "SaveVisit", conn);
            Services.DumException("SaveVisit", "PatientVisit ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "PatientVisit");
            Parser.SetField("ActionID", "SearchPatient");
            Parser.SetField("Message", "MES#006");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    private void ReasonVisits(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer ReasonVisitS = new StringBuffer();
        try {
            int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex").trim());
            String ReasonVisit = request.getParameter("ReasonVisitSelect").trim();
            Query = "Select dbname from oe.clients where Id = " + ClientIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Database = rset.getString(1);
            rset.close();
            stmt.close();
            if (ClientIndex == 27 || ClientIndex == 29) {
                ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitS.append("<select class=\"form-control\" id=\"ReasonVisit\" name=\"ReasonVisit\" style=\"color:black;\" >");
                ReasonVisitS.append("<option value=\"\">Select Reason of Visit</option>\n");
                Query = "Select Id,ReasonVisit from " + Database + ".ReasonVisits where ltrim(rtrim(UPPER(Catagory))) = ltrim(rtrim(UPPER('" + ReasonVisit + "')))";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next())
                    ReasonVisitS.append("<option value=\"" + rset.getString(1).trim() + "\">" + rset.getString(2).trim() + "</option>\n");
                rset.close();
                stmt.close();
                ReasonVisitS.append("</select>");
            } else if (ReasonVisit.toUpperCase().trim().equals("COVID")) {
                ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitS.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" value=\"COVID Testing\" readonly>");
            } else {
                ReasonVisitS.append("<label><font color=\"black\">Reason For Visit </font></label>");
                ReasonVisitS.append("<input type=\"text\" placeholder=\"\" class=\"form-control\"id=\"ReasonVisit\" name=\"ReasonVisit\" >");
            }
            out.println(ReasonVisitS);
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** MAIN CATCH (ReasonVisits^^ MES#000-0001)", servletContext, e, "PatientVisit", "ReasonVisits", conn);
            Services.DumException("ReasonVisits", "PatientVisit ", request, e);
        }
    }


    private String InsertCOVIDReg(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final String PatientRegId) throws JsonProcessingException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Request = "";
        final HashMap<String, Object> responseJSON = new HashMap<String, Object>();
        final ObjectMapper jsonMapper = new ObjectMapper();
        String FirstName = "";
        String MiddleInitial = "";
        String LastName = "";
        String Email = "";
        String PhNumber = "";
        String DOB = "";
        String Gender = "";
        String Ethnicity = "";
        String Race = "";
        String City = "";
        String Address = "";
        String State = "";
        String ZipCode = "";
        String County = "";
        String reply = "";
        try {
            Query = "Select IFNULL(FirstName,''), IFNULL(MiddleInitial,''), IFNULL(LastName,''), IFNULL(Email, ''), " +
                    "IFNULL(PhNumber,0),  IFNULL(DOB,'0000-00-00'), IFNULL(Gender,'M'), IFNULL(City,''),IFNULL(Address,''), " +
                    "IFNULL(State,'TX'), IFNULL(ZipCode,''), IFNULL(County,'')  " +
                    "from victoria.PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FirstName = rset.getString(1).trim();
                MiddleInitial = rset.getString(2).trim();
                LastName = rset.getString(3).trim();
                Email = rset.getString(4).trim();
                PhNumber = rset.getString(5).trim();
                DOB = rset.getString(6).trim();
                Gender = rset.getString(7).trim();
                City = rset.getString(8).trim();
                Address = rset.getString(9).trim();
                State = rset.getString(10).trim();
                ZipCode = rset.getString(11).trim();
                County = rset.getString(12);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("PatientReg2", "InsertCOVIDRegError in Getting Data from PatientReg table", request, e, this.getServletContext());
        }
        try {
            Query = "Select IFNULL(Ethnicity,''), IFNULL(Race,'') from victoria.PatientReg_Details " +
                    "where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Ethnicity = rset.getString(1);
                Race = rset.getString(2);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            Services.DumException("PatientReg2", "InsertCOVIDRegError in Getting Data from PatientReg_Details table", request, e, this.getServletContext());
        }
        if (Gender.toUpperCase().equals("MALE")) {
            Gender = "M";
        } else {
            Gender = "F";
        }
        switch (Ethnicity) {
            case "1":
                Ethnicity = "H";
                break;
            case "2":
                Ethnicity = "NH";
                break;
            case "3":
                Ethnicity = "U";
                break;
            default:
                Ethnicity = "U";
                break;
        }
        switch (Race) {
            case "1":
                Race = "A";
                break;
            case "2":
                Race = "B";
                break;
            case "3":
                Race = "W";
                break;
            case "4":
                Race = "O";
                break;
            case "5":
                Race = "U";
                break;
            default:
                Race = "U";
                break;
        }
        if (MiddleInitial.length() > 1) {
            MiddleInitial = MiddleInitial.substring(0, 1);
        }
        if (PhNumber.contains("-")) {
            PhNumber = PhNumber.replaceAll("-", "");
        }
        if (PhNumber.length() < 10) {
            PhNumber += "0";
        }
        try {
            responseJSON.put("FirstName", FirstName);
            responseJSON.put("MiddleName", MiddleInitial);
            responseJSON.put("LastName", LastName);
            responseJSON.put("Email", Email);
            responseJSON.put("ConfirmEmail", Email);
            responseJSON.put("Mobile", PhNumber);
            responseJSON.put("DOB", DOB);
            responseJSON.put("Sex", Gender);
            responseJSON.put("Ethnicity", Ethnicity);
            responseJSON.put("Race", Race);
            responseJSON.put("City", City);
            responseJSON.put("County", County);
            responseJSON.put("Street", Address);
            responseJSON.put("StateCode", State);
            responseJSON.put("IsValidDOB", true);
            responseJSON.put("Zipcode", ZipCode);
            Request = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJSON);

            final String BaseURL = "https://victoriacovid.com/api/CovidPatient/CreatePatient/?UserId=1";
            final String Mask = "";
            final URL url = new URL("https://victoriacovid.com/api/CovidPatient/CreatePatient/?UserId=1");
            final URLConnection uc = url.openConnection();
            uc.setReadTimeout(15000);
            uc.setConnectTimeout(17000);
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestProperty("Accept", "application/json");
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            uc.setAllowUserInteraction(false);
            uc.setDoOutput(true);
            final OutputStream os = uc.getOutputStream();
            os.write(Request.getBytes(StandardCharsets.UTF_8));
            os.close();
            uc.connect();
            final InputStream is = uc.getInputStream();
            final int size = is.available();
            final byte[] response2 = new byte[size];
            is.read(response2);
            reply = new String(response2);
            reply = reply.trim();
            System.out.println("Reply: " + reply);
        } catch (Exception e) {
            final String Message = "0";
            Services.DumException("PatientReg2", "InsertCOVIDReg 0", request, e, this.getServletContext());
            return "0";
        }
        return reply;
    }

}
