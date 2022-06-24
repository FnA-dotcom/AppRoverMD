package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class ClaimPost extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceHandling(request, response);
    }


    private void serviceHandling(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID;
        Connection conn = null;
        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
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
//
            switch (ActionID) {
                case "getClaimPostInput":
                    getClaimPostInput(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "searchInsurance":
                    searchInsurance(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "searchPatient":
                    searchPatient(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "SavePatientPayment":
                    SavePatientPayment(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments, response);
                    break;
                case "SaveInsurencePayment":
                    SaveInsurencePayment(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;

                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }

        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
//            helper.SendEmailWithAttachment("Error in Claim Post ** (handleRequest)", context, Ex, "CardConnectServices", "handleRequest", conn);
//            Services.DumException("ClaimPostServices", "Handle Request", request, Ex, getServletContext());
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("FormName", "ManagementDashboard");
//            Parser.SetField("ActionID", "GetInput");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
//            out.flush();
//            out.close();
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

    private void searchInsurance(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String userId, String Database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        try {
            String PaymentBy = request.getParameter("PaymentBy");
            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            StringBuffer PatientList = new StringBuffer();
//            if(searchValue.equals("") || searchValue==null){
//                searchValue=null;
//                Query = "Select MRN,FirstName,MiddleInitial,LastName,DOB,PhNumber from sanmarcos.PatientReg " +
//                        "where FirstName=null or PhNumber=null or MRN=null " +
//                        " or LastName=null or MiddleInitial=null or CONCAT(FirstName,MiddleInitial,LastName)=null ";
//            }else {
//                Query = "Select MRN,CONCAT(FirstName,' ',MiddleInitial,' ',LastName)as name,DOB,PhNumber from sanmarcos.PatientReg " +
//                        "where FirstName='" + searchValue + "' or PhNumber='" + searchValue + "' or MRN='" + searchValue + "'" +
//                        " or LastName='" + searchValue + "' or MiddleInitial='" + searchValue + "' or CONCAT(FirstName,MiddleInitial,LastName)='" + searchValue.trim() + "' ";
//            }
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                System.out.println("joooooooo:");
//                DellAccount.append("<tr>");
//                DellAccount.append("<td align=left>" + rset.getString(1) + "</td>\n");
//                DellAccount.append("<td align=left>" + rset.getString(2) + "</td>\n");
//                DellAccount.append("<td align=left>" + rset.getString(3) + "</td>\n");
//                DellAccount.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                DellAccount.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                DellAccount.append("<td align=left>" + rset.getString(6) + "</td>\n");
//                DellAccount.append("</tr>");
//            }
//            rset.close();
//            stmt.close();


            //old
//            Query = " Select a.ID,b.Id, a.MRN, a.FirstName, a.MiddleInitial, a.LastName, a.DOB, a.PhNumber  " +
//                    "from sanmarcos.PatientReg a Inner join sanmarcos.InsuranceInfo b ON b.PatientRegId = a.ID" +
//                    " where a.status = 0 and CONCAT(a.FirstName,a.LastName,a.PhNumber,a.MiddleInitial,a.MRN) like '%"+searchValue+"%'";
//            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            PatientList.append("<option value=-1> Please Select Below Patient </option>");
//            while (rset.next()) {
//                System.out.println("jooo_1:" + rset.getString(4));
//              //  PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
//                PatientList.append("<tr>");
//                PatientList.append("<td align=left>" + rset.getString(1) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(2) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(3) + "</td>\n");//PhNumber
//                PatientList.append("<td align=left>" + rset.getString(4) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(5) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(6) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(7) + "</td>\n");
//                PatientList.append("<td align=left>" + rset.getString(8) + "</td>\n");
//                PatientList.append("</tr>");
//            }
//            rset.close();
//            stmt.close();
//            PatientList.append("</select>");
//            System.out.println("jooooo:"+PatientList.toString());
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("claimPostSearch", String.valueOf(PatientList));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Claims/post.html");


            Query = " Select id,PayerName, PayerId from oe.ProfessionalPayers where PayerName like '%" + PaymentBy + "%'";
//            System.out.println("Query 1"+Query);
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\" >");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=-1> Please Select Below Patient </option>");
            while (rset.next()) {
//
                //  PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
                PatientList.append("<tr>");
                PatientList.append("<td style=\"display: none;\" align=left>" + rset.getString(1) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(3) + "</td>\n");


                PatientList.append("</tr>");
            }
//            System.out.println("Query 2"+Query);
            rset.close();
            stmt.close();
            PatientList.append("</select>");
//            System.out.println("jooooo:"+PatientList.toString());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("claimPostSearch", String.valueOf(PatientList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Claims/post.html");

        } catch (Exception e) {

//            System.out.println("in the catch exception of search patient Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }
    }


    private void searchPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String userId, String Database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String searchPatient = request.getParameter("searchPatient");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer PatientList = new StringBuffer();
        try {

            PatientList.append("<table id='patientSearchtable' class=\"table table-bordered table-striped\">");
            PatientList.append("<thead>");
            PatientList.append("<tr>");

            PatientList.append("<th style=\"display: none;\">id</th>\n");
            PatientList.append("<th >Patient Name</th>\n");
            PatientList.append("<th >Account Number</th>\n");
            PatientList.append(" <th >Date of Service</th>\n");
            PatientList.append("</tr>");
            PatientList.append("</thead>");
            PatientList.append("<tbody>");


            Query = " Select PatientRegId,PatientName, AcctNo,DOS from " + Database + ".ClaimInfoMaster where PatientName like '%" + searchPatient + "%' ORDER BY CreatedDate ASC";
//            System.out.println("Query 1"+Query);

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            System.out.println("herr is querry " + Query);
            while (rset.next()) {
//
                //  PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
                PatientList.append("<tr onclick='getVal(this);'>");
                PatientList.append("<td style=\"display: none;\" align=left>" + rset.getString(1) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                PatientList.append("</tr>");
                System.out.println("herr is querry " + Query);
            }
//            System.out.println("Query 2"+Query);
            rset.close();
            stmt.close();
            PatientList.append("</tbody>");
            PatientList.append("</table>");

            out.println(PatientList.toString());

//            System.out.println("jooooo:"+PatientList.toString());
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("claimPostSearch", String.valueOf(PatientList));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Claims/post.html");
//            	out.println(PatientList);

        } catch (Exception e) {

//            System.out.println("in the catch exception of search patient Function ");
            System.out.println(e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);

        }
    }

    private void getClaimPostInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(userId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/post.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Claim Post ** (handleRequest)", servletContext, Ex, "CardConnectServices", "handleRequest", conn);
            Services.DumException("ClaimPostServices", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    private void SavePatientPayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments, HttpServletResponse response) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String checkNumber = "";
        String CopayDOS = "";
        String Query = "";
        String CardType = "";
        int SNo = 1;
        System.out.println("out of the try of SavePatientPayment 1");
//        SupportiveMethods suppMethods = new SupportiveMethods();
//        StringBuffer ClientList = new StringBuffer();
        //for patient payment
        String searchPatient = request.getParameter("searchPatient").trim();
        String searchPatientId = request.getParameter("PatientNameId").trim();
        System.out.println("found searchPatient");
        String paymentAmount = request.getParameter("paymentAmount").trim();
        System.out.println("found paymentAmount");
        String ReceivedDate = request.getParameter("ReceivedDate").trim();
        System.out.println("found ReceivedDate");
        checkNumber = request.getParameter("checkNumber");
        if (checkNumber == null || checkNumber == "") {
            checkNumber = "";
        }
        System.out.println("found checkNumber" + checkNumber);
        String paymentType = request.getParameter("paymentType").trim();
        System.out.println("found paymentType");
        String paymentSource = request.getParameter("paymentSource").trim();
        System.out.println("found paymentSource");
        String memo = request.getParameter("memo").trim();
        System.out.println("found memo");
        CopayDOS = request.getParameter("CopayDOS").trim();
        if (CopayDOS == null || CopayDOS == "") {
            CopayDOS = "0000-00-00";
        }

        System.out.println("found CopayDOS----->" + CopayDOS);
        CardType = request.getParameter("CardType").trim();
        if (CardType == null || CardType == "") {
            CardType = "";
        }

        System.out.println("found CardType");

        String UserId = request.getParameter("UserId").trim();
        System.out.println("found UserId" + UserId);
        System.out.println("out of the try of SavePatientPayment");
        try {
//            PreparedStatement MainReceipt = conn.prepareStatement(
//                    "INSERT INTO primescope.EOB_Master (PatientIdx,PaymentAmount,ReceivedDate,CheckNumber,PaymentType,PaymentSource,Memo,CopayDOS,CardType,CreatedBy,Status,CreatedDate) VALUES (?,?,?,?,?,?,?,?,?,?,0,now()) ");
//            MainReceipt.setString(1, searchPatient);
//            MainReceipt.setString(2, paymentAmount);
//            MainReceipt.setString(3, ReceivedDate);
//            MainReceipt.setString(4, checkNumber);
//            MainReceipt.setString(5, paymentType);
//            MainReceipt.setString(6, paymentSource);
//            MainReceipt.setString(7, memo);
//            MainReceipt.setString(8, CopayDOS);
//            MainReceipt.setString(9, CardType);
//            MainReceipt.setString(10, UserId);
////            MainReceipt.setString(11, Status);
//
//
//            MainReceipt.executeUpdate();
//            MainReceipt.close();
//            System.out.println("in try of SavePatientPayment"+MainReceipt);


//            Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check.html");


            response.sendRedirect("md.ViewClaimPayment?ActionID=getClaims_WRT_PatientPayment_inside&PatRegIdx=" + searchPatientId + "&Amt=" + paymentAmount + "&checkNumber=" + checkNumber + "&ReceivedDate=" + ReceivedDate + "&paymentType=" + paymentType + "&paymentSource=" + paymentSource + "&memo=" + memo + "&CopayDOS=" + CopayDOS + "&CardType=" + CardType);
        } catch (Exception Ex) {

            helper.SendEmailWithAttachment("Error in Claim Post ** (handleRequest)", servletContext, Ex, "CardConnectServices", "handleRequest", conn);
            Services.DumException("SavePatientPayment", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "SavePatientPayment");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
//        getClaimPostInput(request, out, conn, servletContext, UserId, UserId, helper, SNo, payments);
    }


    //
    private void SaveInsurencePayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        System.out.println("INSIDE SaveInsurencePayment");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String InsuranceName = "";
        int SNo = 1;
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer ClientList = new StringBuffer();
        //for patient payment


        //for insurance
        String PaymentBy = request.getParameter("PaymentById").trim();
        System.out.println("found PaymentBy");
        String PaymentFrom = request.getParameter("PaymentFrom").trim();
        System.out.println("found PaymentFrom");
        String InpaymentAmount = request.getParameter("InpaymentAmount").replaceAll("\\$", "").trim();
//        InpaymentAmount=InpaymentAmount.replaceAll(",", "m");
        System.out.println("found InpaymentAmount");
        String InRecievedChkDate = request.getParameter("InRecievedChkDate").trim();
        System.out.println("found InRecievedChkDate");
        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();
        System.out.println("found OtherRefrenceNo");
        String InChekNo = request.getParameter("InChekNo").trim();
        System.out.println("found InChekNo -> " + InChekNo);
        String insurancePaymentSource = request.getParameter("isurancePaymentSource").trim();
        System.out.println("found isurancePaymentSource");
        String InCardType = request.getParameter("InCardType").trim();

        if (InCardType == null || InCardType == "") {
            InCardType = "";
        }


        System.out.println("found InCardType");
//        String Status = request.getParameter("Status").trim();
//        System.out.println("found Status");
        String UserId = request.getParameter("UserId").trim();
        System.out.println("found UserId");

        try {
//            PreparedStatement MainReceipt = conn.prepareStatement(
//                    "INSERT INTO primescope.EOB_Master (InsuranceIdx,PaymentFrom,PaymentAmount,ReceivedDate,CheckNumber,OtherRefrenceNo,PaymentSource,CardType,"
//                            +" CreatedBy,Status,CreatedDate) VALUES (?,?,?,?,?,?,?,?,?,0,now())");
//            MainReceipt.setString(1, PaymentBy);
//            MainReceipt.setString(2, PaymentFrom);
//            MainReceipt.setString(3, InpaymentAmount);
//            MainReceipt.setString(4, InRecievedChkDate);
//            MainReceipt.setString(5, InChekNo);
//            MainReceipt.setString(6, OtherRefrenceNo);
//            MainReceipt.setString(7, isurancePaymentSource);
//            MainReceipt.setString(8, InCardType);
//            MainReceipt.setString(9, UserId);
////            MainReceipt.setString(10, Status);
//
////            MainReceipt.setString(13, UserId);
//            MainReceipt.executeUpdate();
//            MainReceipt.close();
//            System.out.println("found Querry---->"+MainReceipt);


            PreparedStatement ps = conn.prepareStatement("SELECT PayerName FROM oe.ProfessionalPayers WHERE Id=?");
            ps.setString(1, PaymentBy);
            rset = ps.executeQuery();
            if (rset.next()) {
                InsuranceName = rset.getString(1);
            }
            rset.close();
            ps.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PaymentBy", PaymentBy);
            Parser.SetField("PaymentFrom", PaymentFrom);
            Parser.SetField("CheckAmt", String.valueOf(String.format("%,.2f", Double.parseDouble(InpaymentAmount))));
            Parser.SetField("UnappliedAmt", String.valueOf(String.format("%,.2f", Double.parseDouble(InpaymentAmount))));
            Parser.SetField("AppliedAmt", String.valueOf(String.format("%,.2f", Double.parseDouble("0.00"))));
            Parser.SetField("receivingDate", InRecievedChkDate);
            Parser.SetField("OtherRefrenceNo", OtherRefrenceNo);
            Parser.SetField("checkNumber", InChekNo);
            Parser.SetField("insurancePaymentSource", insurancePaymentSource);
            Parser.SetField("InCardType", InCardType);
            Parser.SetField("InsuranceName", InsuranceName);
            Parser.SetField("InsuranceIdx", PaymentBy);
            Parser.SetField("cancelLink", "md.ClaimPost?ActionID=getClaimPostInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in Claim Post ** (handleRequest)", servletContext, Ex, "CardConnectServices", "handleRequest", conn);
            Services.DumException("SaveInsurencePayment", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "SaveInsurencePayment");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
//        getClaimPostInput(request, out, conn, servletContext, UserId, UserId, helper, SNo, payments);
    }


}
