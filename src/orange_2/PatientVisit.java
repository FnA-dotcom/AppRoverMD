

package orange_2;

import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

public class PatientVisit extends HttpServlet {
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
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = supp.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = supp.getMysqlConn(context);

        try {
            Cookie[] cookies = request.getCookies();
            Zone = UserId = Passwd = "";
            int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; coky++) {
                String cName = cookies[coky].getName();
                String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }

            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
//      if(ClientId == 8){
//        Database = "oe_2";
//      }else if(ClientId == 9){
//        Database = "victoria";
//      }else if(ClientId == 10){
//        Database = "oddasa";
//      }


            if (ActionID.equals("SearchPatient")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", ClientId);
                this.SearchPatient(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("GetData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Get Details", "Get Data for all Matching Old Patients ", ClientId);
                this.GetData(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("AddNewVisit")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Add New Visit Option", "Click on Add New Visit for Searched Old Patients", ClientId);
                this.AddNewVisit(request, out, conn, context, UserId, Database, ClientId);
            } else if (ActionID.equals("SaveVisit")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Save Data", "Save the new Information and Create Log for Old Information", ClientId);
                this.SaveVisit(request, out, conn, context, UserId, Database, ClientId);
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


    void SearchPatient(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();

        try {
            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SearchPatient.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void GetData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
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
        int SearchBy = Integer.parseInt(request.getParameter("SearchBy").trim());
        String PatientFirstName = null;
        String PatientLastName = null;
        String PatientMRN = null;
        String PhNumber = null;
        String DOB = null;
        String SSN = null;
        int SNo = 1;
        try {

            if (SearchBy == 1) {
                PatientFirstName = request.getParameter("FirstName").trim();
                Query = "Select Id from " + Database + ".PatientReg where FirstName like '%" + PatientFirstName + "%'";
            } else if (SearchBy == 2) {
                PatientLastName = request.getParameter("LastName").trim();
                Query = "Select Id from " + Database + ".PatientReg where LastName like '%" + PatientLastName + "%'";
            } else if (SearchBy == 3) {
                PatientMRN = request.getParameter("MRN").trim();
                Query = "Select Id from " + Database + ".PatientReg where MRN = '" + PatientMRN + "'";
            } else if (SearchBy == 4) {
                PhNumber = request.getParameter("PhNumber").trim();
                Query = "Select Id from " + Database + ".PatientReg where PhNumber = '" + PhNumber + "'";
            } else if (SearchBy == 5) {
                DOB = request.getParameter("DOB").trim();
                Query = "Select Id from " + Database + ".PatientReg where DOB = '" + DOB + "'";
            } else if (SearchBy == 6) {
                SSN = request.getParameter("SSN").trim();
                Query = "Select Id from " + Database + ".PatientReg where SSN = '" + SSN + "'";
            }
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
//      out.println(Query);
            while (rset.next()) {
                Query2 = " Select CONCAT(IFNULL(b.Title,''),' ',b.FirstName,' ',b.MiddleInitial,' ',b.LastName), DATE_FORMAT(b.DOB,'%m/%d/%Y'), b.PhNumber, a.ReasonVisit, IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T'))," +
                        " CASE WHEN b.COVIDStatus = 1 THEN 'POSITIVE' WHEN b.COVIDStatus = 0 THEN 'NEGATIVE' " +
                        " WHEN b.COVIDStatus = -1 THEN 'SUSPECTED' ELSE 'UN-EXAMINED' END , a.MRN" +
                        " from " + Database + ".PatientVisit a " +
                        " Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id where b.ID =  " + rset.getInt(1);
                stmt2 = conn.createStatement();
                rset2 = stmt2.executeQuery(Query2);
//        System.out.println(Query2);
                while (rset2.next()) {
                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset2.getString(6) + "</td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.PatientVisit?ActionID=AddNewVisit&ID=" + rset.getInt(1) + ">Add New Visit</a></td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.PrintLabel4?ActionID=GETINPUT&ID=" + rset.getInt(1) + ">Print Label</a></td>\n");
                    if (ClientId == 8) {
                        CDRList.append("<td align=left><a href=/orange_2/orange_2.DownloadBundle?ActionID=GETINPUT&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
                    } else if (ClientId == 9) {
                        CDRList.append("<td align=left><a href=/orange_2/orange_2.DownloadBundle?ActionID=GETINPUTVictoria&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
                    } else if (ClientId == 10) {
                        CDRList.append("<td align=left><a href=/orange_2/orange_2.DownloadBundle?ActionID=GETINPUTOddasa&ID=" + rset.getInt(1) + ">Downlaod Admission Bundle</a></td>\n");
                    }
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.RegisteredPatients_old?ActionID=ShowHistory&ID=" + rset.getInt(1) + ">Show History</a></td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.PatientReg?ActionID=EditValues&MRN='" + rset2.getString(7).trim() + "'&ClientId=" + ClientId + ">View/Edit</a></td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.PatientInfo?ActionID=GetValues&ID=" + rset.getInt(1) + ">Send to E-Doc</a></td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.RegisteredPatients_old?ActionID=DeActivePatient&ID=" + rset.getInt(1) + ">De-Activate Patient</a></td>\n");
                    CDRList.append("<td align=left><a href=/orange_2/orange_2.RegisteredPatients_old?ActionID=ReActivePatient&ID=" + rset.getInt(1) + ">Re-Activate Patient</a></td>\n");

                    SNo++;

                }
                rset2.close();
                stmt2.close();

            }
            rset.close();
            stmt.close();
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
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }


    void AddNewVisit(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
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

            Query = " Select a.MRN, CONCAT(a.Title,' ',a.FirstName,' ',a.MiddleInitial,' ',a.LastName), DATE_FORMAT(a.DOB,'%m/%d/%Y'), a.PhNumber, a.ReasonVisit, DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'), " +
                    " CONCAT(c.DoctorsFirstName, ' ', c.DoctorsLastName) " +
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
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/AddNewVisit.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }

    void SaveVisit(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int VisitNumber = 0;
        String MRN = request.getParameter("MRN").trim();
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String NewReasonVisit = request.getParameter("NewReasonVisit").trim();
        String NewDateofService = request.getParameter("NewDateofService").trim();
        int NewDoctorId = Integer.parseInt(request.getParameter("NewDoctorId").trim());

        try {

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
                out.println("Error in getting Visit Number + 1" + e.getMessage());
            }
            try {
                final PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".PatientVisit (MRN,PatientRegId,ReasonVisit ,VisitNumber,DoctorId,DateofService,CreatedDate,CreatedBy) \nVALUES (?,?,?,?,?,?,now(),?) ");
                MainReceipt.setString(1, MRN);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setString(3, NewReasonVisit);
                MainReceipt.setInt(4, VisitNumber);
                MainReceipt.setInt(5, NewDoctorId);
                MainReceipt.setString(6, NewDateofService);
                MainReceipt.setString(7, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                out.println("Error 2- Insertion PatinetVisit Table :" + e.getMessage());
                return;
            }

            try {
                Query = "UPDATE " + Database + ".PatientReg SET ReasonVisit ='" + NewReasonVisit + "', DateofService = '" + NewDateofService + "', DoctorsName = '" + NewDoctorId + "' WHERE ID = " + PatientRegId;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();
            } catch (Exception e) {
                out.println("Error in Updating PatientReg Table:-" + e.getMessage());
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Message", "New Visit has been Created For the MRN : " + MRN);
            Parser.SetField("FormName", String.valueOf("PatientVisit"));
            Parser.SetField("ActionID", String.valueOf("GetData&SearchBy=3&MRN=" + MRN));
//      Parser.SetField("SearchBy", String.valueOf("3"));
//      Parser.SetField("MRN", String.valueOf(MRN));
            Parser.GenerateHtml(out, "/opt/Htmls/orange_2/Exception/Success.html");

//      out.println("<!DOCTYPE html><html><body><p style=\"color:gray;\">New Visit has been Created For the MRN : "+MRN+"</p>");
//      //  out.println("<br>Request has been send to ERM , Find Patient MRN "+MRN);
//      out.println("<br><p style=\"color:gray;\"> Please press Back and do the necessary Updates </p>");
//      //out.println("<br><p style=\"color:gray;\"> If you can not found Patient in ViewPatient Option Please Search Here again and do the necessary Updates <br> Thank You!</p>");
//      out.println("<br><input class=\"btn btn-primary\" type=button name=Back Value=\"  Back  \" onclick=history.back()></body></html>");
//      final Parsehtm Parser = new Parsehtm(request);
//      Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/SearchPatient.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }


}
