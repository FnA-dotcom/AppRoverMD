//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestReport extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";


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
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();
        conn = Services.getMysqlConn(context);
        try {
            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.GetReport(request, out, conn, context, UserId, Database, ClientId,helper);
            }
            if (ActionID.equals("sendToEPDfromRequest")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.sendToEPDFromRequestReport(request, out, conn, context,helper);
            }
            if (ActionID.equals("skipEPDrequest")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.skipEPDrequest(request, out, conn, context,helper);
            }else {
                out.println("Under Development");
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


    void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        Statement stmt1 = null;
        String Query1 = "";

        try {


            CDRList.append("<div class=\"table-responsive\">");
            CDRList.append("<table class=\"table table-striped mb-0\" style=\"width:100%\">");
            CDRList.append("<thead style=\"color:black;\">");
            CDRList.append("<tr>");
            CDRList.append("<th >Action</th>");
            CDRList.append("<th >MRN</th>");
            CDRList.append("<th >ClientIndex</th>");
            CDRList.append("<th >MSG</th>");
            CDRList.append("<th >RequestDate</th>");
            CDRList.append("<th >PostTime</th>");
            CDRList.append("<th >Status</th>");
            CDRList.append("<th >RequestType</th>");
            CDRList.append("<th >Flag</th>");
            CDRList.append("<th >Response</th>");
            CDRList.append("<th >ResponseCode</th>");
            CDRList.append("<th >MSCID</th>");
            CDRList.append("</tr>");
            CDRList.append("<tbody style=\"color:black;\">");

            Query = "Select a.Id, a.msg, a.requestdate, a.posttime, a.status, a.RequestType, a.mrn, a.flag, a.ClientIndex,  a.Response, " +
                    "a.ResponseCode, a.MSCID,b.name,a.EmailSent \n" +
                    " from oe.request a " +
                    " STRAIGHT_JOIN oe.clients b ON a.ClientIndex = b.Id " +
                    " order by Id desc limit 50";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(10) == null) {
                    CDRList.append("<tr style=\"background-color: #FF7C60;\"><td> <button  onclick=\"sendEPD("+rset.getInt(7)+", "+rset.getInt(9)+")\"  class=\"btn btn-info btn-md\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"Resend To EPowerDoc\" > <font color=\"FFFFFF\" > RESEND </font></button>" +
                            " <br><br><br> <button  onclick=\"SkipEPD("+rset.getInt(1)+")\"  class=\"btn btn-info btn-md\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"Skip this Patient\" > <font color=\"FFFFFF\" > SKIP </font></button></td>");
                    //CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getInt(7) + "</td>");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");


                    if(Is5minutesCrosed(rset.getInt(1),conn,out) && (rset.getInt(14)==0)){
//                        helper.SendEmail("",,"The Patient with Following details are not crossing over:\n" +
//                                "\t<table style='border-style: solid;'>\n" +
//                                "\t\t<thead>\n" +
//                                "\t\t\t<th>Mrn</th>\n" +
//                                "\t\t\t<th>Client Name</th>\n" +
//                                "\t\t\t<th>Request Date</th>\n" +
//                                "\t\t\t<th>MSCID</th>\n" +
//                                "\t\t</thead>\n" +
//                                "\t\t<tbody>\n" +
//                                "\t\t\t<tr style=\"background-color: #FF7C60;\" ><td>"+rset.getString(7)+"</td>\n" +
//                                "\t\t\t<td>"+rset.getString(13)+"</td>\n" +
//                                "\t\t\t<td>"+rset.getString(3)+"</td>\n" +
//                                "\t\t\t<td>"+rset.getString(12)+"</td></tr>\n" +
//                                "\t\t</tbody>\n" +
//                                "\t</table>");
                        String Subject="Patient Not crossing over to EPD";
                        String Body = "The Patient with Following details are not crossing over:\n " +
                                "\t<table style='border-style: solid;'>\n" +
                                "\t\t<thead>\n" +
                                "\t\t\t<th>Mrn</th>\n" +
                                "\t\t\t<th>Client Name</th>\n" +
                                "\t\t\t<th>Request Date</th>\n" +
                                "\t\t\t<th>MSCID</th>\n" +
                                "\t\t</thead>\n" +
                                "\t\t<tbody>\n" +
                                "\t\t\t<tr style=\"background-color: #FF7C60;\" ><td>"+rset.getString(7)+"</td>\n" +
                                "\t\t\t<td>"+rset.getString(13)+"</td>\n" +
                                "\t\t\t<td>"+rset.getString(3)+"</td>\n" +
                                "\t\t\t<td>"+rset.getString(12)+"</td></tr>\n" +
                                "\t\t</tbody>\\n\" +\n" +
                                "\t</table>";
                        helper.SendEmail_RequestReport(conn,Subject,Body);
                        Query1= "UPDATE oe.request SET EmailSent=1 WHERE id='"+rset.getInt(1)+"'";

                        stmt1 = conn.createStatement();
                        stmt1.executeUpdate(Query1);
                        stmt1.close();
                    }

                } else {
                    CDRList.append("<tr style=\"background-color: 60FF66;\"><td> <button  onclick=\"sendEPD("+rset.getInt(7)+", "+rset.getInt(9)+")\"  class=\"btn btn-info btn-md\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"Resend To EPowerDoc\" disabled> <font color=\"FFFFFF\"> RESEND </font></button> " +
                            " <br><br><br> <button  onclick=\"SkipEPD("+rset.getInt(1)+")\"  class=\"btn btn-info btn-md\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"Skip this Patient\" disabled> <font color=\"FFFFFF\"> SKIP </font></button></td>");
//                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getInt(7) + "</td>");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");


                }
            }
            rset.close();
            stmt.close();

            CDRList.append("</tbody>");
            CDRList.append("</table>");
            CDRList.append("</div>");

            conn.close();
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/RequestTableReport.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

    private void sendToEPDFromRequestReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        Statement hstmt = null;
        final ResultSet hrset = null;
        String Query = "";
        int ID = Integer.parseInt(request.getParameter("ID").trim());
        int ClientID = Integer.parseInt(request.getParameter("CID").trim());
        String DATABASE = null;

        String Title = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String MaritalStatus = "";
        String DOB = "";
        String Age = "";
        String gender = "";
        String Email = "";
        String PhNumber = "";
        String Address = "";
        String City = "";
        String State = "";
        String Country = "";
        String ZipCode = "";
        String SSN = "";
        String Occupation = "";
        String Employer = "";
        String EmpContact = "";
        String PriCarePhy = "";
        String ReasonVisit = "";
        String MRN = "";
        String DOS = "";
        String msg = "";
        int SelfPayChk = 0;
        StringBuilder StatusReport = new StringBuilder();

        try {
            Query = "SELECT dbname from oe.clients where Id="+ClientID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if(rset.next()){
                DATABASE = rset.getString(1);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Query = "Select Title, FirstName, MiddleInitial, LastName, MaritalStatus, MRN, DOB, Age, Gender, Email, PhNumber, " +
                    "Address, City, State, Country,  ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, " +
                    "CreatedDate, Address2 from " + DATABASE + ".PatientReg where MRN = " + ID;
//            out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Title = rset.getString(1);
                FirstName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                LastName = rset.getString(4);
                MaritalStatus = rset.getString(5);
                MRN = rset.getString(6);
                DOB = rset.getString(7);
                Age = rset.getString(8);
                gender = rset.getString(9);
                Email = rset.getString(10);
                PhNumber = rset.getString(11);
                Address = rset.getString(12) + " " + rset.getString(24);
                City = rset.getString(13);
                State = rset.getString(14);
                Country = rset.getString(15);
                ZipCode = rset.getString(16);
                SSN = rset.getString(17);
                Occupation = rset.getString(18);
                Employer = rset.getString(19);
                EmpContact = rset.getString(20);
                PriCarePhy = rset.getString(21);
                ReasonVisit = rset.getString(22);
                SelfPayChk = rset.getInt(23);
                DOS = rset.getString(24);
            }
            rset.close();
            stmt.close();
//            out.println("ID :"+FirstName);


            final Date dNow = new Date(System.currentTimeMillis() - 7200000L);
            final SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            final String MSH7 = ft.format(dNow);
            final String MRN2 = "3109900";
            DOB = DOB.replace("-", "");
            if (gender.toUpperCase().compareTo("FEMALE") == 0) {
                gender = "F";
            } else {
                gender = "M";
            }

            msg = "MSH|^~\\&||665|ADT|3344|" + MSH7 + "||ADT^A04|" + MSH7 + "|P|2.3\r\n" + "EVN|A04|20200707020302|||XXX^^^^^^^^488 \r\n" + "PID|1||" + MRN + "||" + FirstName + "^" + LastName + "^" + MiddleInitial + "||" + DOB + "|" + gender + "||W|" + Address + "^^" + City + "^" + State + "^" + ZipCode + "|" + PhNumber + "|" + PhNumber + "||ENGLISH|M|001||" + SSN + "|||||||||||N \r\n" + "PV1||3^E/R^02|||||194501^TOWNSHEND^PETE|194502^DALTREY^ROGER|194506^ROGERS^PAUL|E|||||||194501^TOWNSHEND^PETE|3||BB1||||||||||||||||G|||||||||\r\n" + "IN1|1|SELF-PAY^SELF PAY|SELF PAY|SELF PAY||PFO Sequence 99 Self P|||||||||4";
            String CurrDate = helper.getCurrDate(request,conn);
            int Result = helper.saveRequestEPD(request, msg, Integer.parseInt(MRN), CurrDate, ClientID, conn, servletContext);
//            out.println("ID :"+Result);

            if (Result == 1)
                out.println("1|");
            else
                out.println("0|");
        } catch (Exception e) {
            try {
                helper.SendEmailWithAttachment("Error in PatientInfo ** (sendToEPDFromRequestReport)", servletContext, e, "PatientInfo", "sendToEPDFromRequestReport", conn);
                Services.DumException("PatientInfo", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + ID);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e2) {
            }
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", e.getMessage());
//            Parser.SetField("FormName", String.valueOf("RegisteredPatients"));
//            Parser.SetField("ActionID", String.valueOf("ShowReport"));
//            out.println(e.getMessage());
//            out.println(Query);
//            String str = "";
//            for (int i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
        }
    }

    private void skipEPDrequest(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext,UtilityHelper helper) {
        Statement stmt = null;
        String Query = "";
        int ID = Integer.parseInt(request.getParameter("ID").trim());
//        out.println("ID"+ID);
        try {
            stmt = conn.createStatement();
            Query = "Update oe.request set status = 2  where id="+ID;
            stmt.execute(Query);
            out.println("1|");
        } catch (Exception e) {
            out.println("0|");
            try {
                helper.SendEmailWithAttachment("Error in PatientInfo ** (skipEPDrequest)", servletContext, e, "PatientInfo", "skipEPDrequest", conn);
                Services.DumException("PatientInfo", "GetInput ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + ID);
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (Exception e2) {
            }
//
        }
    }

    private boolean Is5minutesCrosed(int ID,final Connection conn , final PrintWriter out){
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query2 = "";
        try {
            Query2 = "SELECT requestdate FROM oe.request WHERE id="+ID;
            stmt2 = conn.createStatement();
            rset2 = stmt2.executeQuery(Query2);
//            System.out.println("Query : "+Query2);
            if(rset2.next()){
                Query1 = "SELECT TIMESTAMPDIFF(SECOND ,'"+rset2.getString(1)+"',NOW())/60";
                stmt1= conn.createStatement();
                rset1= stmt1.executeQuery(Query1);
//                System.out.println("Query1 : "+Query1);
                if(rset1.next()){
//                    System.out.println("time diff : "+rset1.getDouble(1));
                    if( 6 >= rset1.getDouble(1) && rset1.getDouble(1) >= 5){
//                        System.out.println("Yes!");
                        return true;
                    }
                }
                stmt1.close();
                rset1.close();
            }
            stmt2.close();
            rset2.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}


