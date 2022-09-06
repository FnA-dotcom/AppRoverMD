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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class ClaimReportsManagement extends HttpServlet {


    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
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

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        ServletContext context = null;
        context = getServletContext();


        Connection conn = null;
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            try {
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }
            String ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "ClaimsEntered":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Claims Entered Report ", "Open Claims Entered Report Screen", FacilityIndex);
                    getClaimsEntered(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "ClaimsEnteredReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Claims Entered Report via Filter", "Open Claims Entered Report Screen", FacilityIndex);
                    getClaimsEnteredReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "ClaimsBilled":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Claims Entered Report ", "Open Claims Entered Report Screen", FacilityIndex);
                    getClaimsBilled(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "ClaimsBilledReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Claims Entered Report via Filter", "Open Claims Entered Report Screen", FacilityIndex);
                    getClaimsBilledReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "InsurancePayments":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Insurance Payment Report ", "Open Insurance Payment Report Screen", FacilityIndex);
                    getInsurancePayment(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "InsurancePaymentReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Insurance Payment Report via Filter", "Open Insurance Payment Report Screen", FacilityIndex);
                    getInsurancePaymentReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }

        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }


    void getClaimsEntered(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/ClaimEnteredReport.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }
    void getClaimsEnteredReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer claimsEnteredBody = new StringBuffer();
        StringBuffer claimsEnteredFooter = new StringBuffer();

        StringBuffer claimsPerPatientBody = new StringBuffer();
        StringBuffer claimsPerPatientFooter = new StringBuffer();
        ResultSet rset = null;
        PreparedStatement ps = null;


        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String DateRange = request.getParameter("DateRange").trim();
        try {
            ps = conn.prepareStatement("SELECT IFNULL(PatientMRN,''),IFNULL(a.ClaimNumber,'')" +
                    " ,IFNULL(a.ClaimType,''),IFNULL(PatientName,''),IFNULL(DATE_FORMAT(DOS,'%m/%d/%Y'),''),IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'')," +
                    " FORMAT(IFNULL(TotalCharges,''),2),FORMAT(IFNULL(Balance,IFNULL(TotalCharges,'')),2) ,IFNULL(b.ChargeOption,'')," +
                    " IFNULL(LTRIM(rtrim(REPLACE(d.PayerName,'Servicing States','') )),'No Insurance') " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " LEFT JOIN  " +Database+".ClaimChargesInfo c ON a.Id=c.ClaimInfoMasterId "+
                    " LEFT JOIN oe.ChargeOption b on c.ChargesStatus = b.Id " +
                    " LEFT JOIN ClaimMasterDB.ProfessionalPayersWithFC d on a.PriInsuranceNameId = d.Id " +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND c.Status=0  "+
                    " GROUP BY c.ClaimInfoMasterId ORDER BY a.CreatedDate DESC");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsEnteredBody.append("<tr style='text-align:center;'>");
                claimsEnteredBody.append("<td >" + rset.getString(1) + "</td>\n");//MRN
                claimsEnteredBody.append("<td >" + rset.getString(2) + "</td>\n");//Claim ID
                if (rset.getInt(3) == 1)
                    claimsEnteredBody.append("<td >Institutional</td>\n");
                else
                    claimsEnteredBody.append("<td >Professional</td>\n");
                claimsEnteredBody.append("<td >" + rset.getString(4) + "</td>\n");//Patient Name
                claimsEnteredBody.append("<td >" + rset.getString(5) + "</td>\n");//Claim From Date
                claimsEnteredBody.append("<td >" + rset.getString(5) + "</td>\n");//Claim To Date
                claimsEnteredBody.append("<td >" + rset.getString(6) + "</td>\n");//Claim Date Entered
                claimsEnteredBody.append("<td >$" + rset.getString(7) + "</td>\n");//Claim Total Amount
                claimsEnteredBody.append("<td >$" + rset.getString(8) + "</td>\n");//Claim Balance
                claimsEnteredBody.append("<td >" + rset.getString(9) + "</td>\n");//Claim Status
                claimsEnteredBody.append("<td >" + rset.getString(10) + "</td>\n");//Claim Primary Payer Name
                claimsEnteredBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT " +
                    " COUNT(DISTINCT(a.PatientMRN)),COUNT(a.ClaimNumber),FORMAT(SUM(a.TotalCharges),2),FORMAT(SUM(IFNULL(Balance,IFNULL(TotalCharges,''))),2) " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'  ");

            rset = ps.executeQuery();
            if(rset.next()){
                claimsEnteredFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(2)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(4)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT IFNULL(PatientMRN,''),IFNULL(PatientName,''),COUNT(ClaimNumber)  " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'  "+
                    " GROUP BY PatientName ORDER BY a.CreatedDate DESC");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerPatientBody.append("<tr style='text-align:center;'>");
                claimsPerPatientBody.append("<td >" + rset.getString(1) + "</td>\n");//MRN
                claimsPerPatientBody.append("<td >" + rset.getString(2) + "</td>\n");//Patient Name
                claimsPerPatientBody.append("<td >" + rset.getString(3) + "</td>\n");//Count
                claimsPerPatientBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT " +
                    " COUNT(DISTINCT(a.PatientMRN)),COUNT(a.ClaimNumber) " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'  ");

            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerPatientFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(2)+"</th>\n" +
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);

            Parser.SetField("claimsEnteredBody", String.valueOf(claimsEnteredBody));
            Parser.SetField("claimsEnteredFooter", String.valueOf(claimsEnteredFooter));

            Parser.SetField("claimsPerPatientBody", String.valueOf(claimsPerPatientBody));
            Parser.SetField("claimsPerPatientFooter", String.valueOf(claimsPerPatientFooter));

            Parser.SetField("ToDate", ToDate);
            Parser.SetField("FromDate", FromDate);
            Parser.SetField("DateRange", DateRange);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/ClaimEnteredReport.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }


    void getClaimsBilled(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/ClaimBilledReport.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }
    void getClaimsBilledReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer claimsBilledBody = new StringBuffer();
        StringBuffer claimsBilledFooter = new StringBuffer();

        StringBuffer claimsPerPatientBody = new StringBuffer();
        StringBuffer claimsPerPatientFooter = new StringBuffer();
        ResultSet rset = null;
        PreparedStatement ps = null;


        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String DateRange = request.getParameter("DateRange").trim();
        try {
            ps = conn.prepareStatement("SELECT IFNULL(PatientMRN,''),IFNULL(a.ClaimNumber,'')" +
                    " ,IFNULL(a.ClaimType,''),IFNULL(PatientName,''),IFNULL(DATE_FORMAT(DOS,'%m/%d/%Y'),''),IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'')," +
                    " FORMAT(IFNULL(TotalCharges,''),2),FORMAT(IFNULL(Balance,IFNULL(TotalCharges,'')),2) ,IFNULL(b.ChargeOption,'')," +
                    " IFNULL(LTRIM(rtrim(REPLACE(d.PayerName,'Servicing States','') )),'No Insurance'),isSent " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " LEFT JOIN  " +Database+".ClaimChargesInfo c ON a.Id=c.ClaimInfoMasterId "+
                    " LEFT JOIN oe.ChargeOption b on c.ChargesStatus = b.Id " +
                    " LEFT JOIN ClaimMasterDB.ProfessionalPayersWithFC d on a.PriInsuranceNameId = d.Id " +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND isSent=1  AND c.Status=0 "+
                    " GROUP BY c.ClaimInfoMasterId ORDER BY a.CreatedDate DESC");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsBilledBody.append("<tr style='text-align:center;'>");
                claimsBilledBody.append("<td >" + rset.getString(1) + "</td>\n");//MRN
                claimsBilledBody.append("<td >" + rset.getString(2) + "</td>\n");//Claim ID
                if (rset.getInt(3) == 1)
                    claimsBilledBody.append("<td >Institutional</td>\n");//Claim Type
                else
                    claimsBilledBody.append("<td >Professional</td>\n");//Claim Type
                claimsBilledBody.append("<td >" + rset.getString(11) + "</td>\n");//Times Billed
                claimsBilledBody.append("<td >" + rset.getString(4) + "</td>\n");//Patient Name
                claimsBilledBody.append("<td >" + rset.getString(5) + "</td>\n");//Claim From Date
                claimsBilledBody.append("<td >" + rset.getString(5) + "</td>\n");//Claim To Date
                claimsBilledBody.append("<td >" + rset.getString(6) + "</td>\n");//Claim Date Entered
                claimsBilledBody.append("<td >$" + rset.getString(7) + "</td>\n");//Claim Total Amount
                claimsBilledBody.append("<td >$" + rset.getString(8) + "</td>\n");//Claim Balance
                claimsBilledBody.append("<td >" + rset.getString(10) + "</td>\n");//Claim Primary Payer Name
                claimsBilledBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT " +
                    " COUNT(DISTINCT(a.PatientMRN)),COUNT(a.ClaimNumber),FORMAT(SUM(a.TotalCharges),2),FORMAT(SUM(IFNULL(Balance,IFNULL(TotalCharges,''))),2) " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND isSent=1  ");

            rset = ps.executeQuery();
            if(rset.next()){
                claimsBilledFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(2)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(4)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT IFNULL(PatientMRN,''),IFNULL(PatientName,''),COUNT(ClaimNumber)  " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND isSent=1   "+
                    " GROUP BY PatientName ORDER BY a.CreatedDate DESC");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerPatientBody.append("<tr style='text-align:center;'>");
                claimsPerPatientBody.append("<td >" + rset.getString(1) + "</td>\n");//MRN
                claimsPerPatientBody.append("<td >" + rset.getString(2) + "</td>\n");//Patient Name
                claimsPerPatientBody.append("<td >" + rset.getString(3) + "</td>\n");//Count
                claimsPerPatientBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT " +
                    " COUNT(DISTINCT(a.PatientMRN)),COUNT(a.ClaimNumber) " +
                    " FROM " + Database + ".ClaimInfoMaster a" +
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND isSent=1  ");

            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerPatientFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(2)+"</th>\n" +
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);

            Parser.SetField("claimsBilledBody", String.valueOf(claimsBilledBody));
            Parser.SetField("claimsBilledFooter", String.valueOf(claimsBilledFooter));

            Parser.SetField("claimsPerPatientBody", String.valueOf(claimsPerPatientBody));
            Parser.SetField("claimsPerPatientFooter", String.valueOf(claimsPerPatientFooter));

            Parser.SetField("ToDate", ToDate);
            Parser.SetField("FromDate", FromDate);
            Parser.SetField("DateRange", DateRange);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/ClaimBilledReport.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }


    void getInsurancePayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/InsurancePaymentReport.html");
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
    }
    void getInsurancePaymentReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId) {
        StringBuffer insurancePaymentBody = new StringBuffer();
        StringBuffer insurancePaymentFooter = new StringBuffer();

        StringBuffer claimsPerClaimTypeBody = new StringBuffer();
        StringBuffer claimsPerClaimTypeFooter = new StringBuffer();

        StringBuffer claimsPerProviderBody = new StringBuffer();
        StringBuffer claimsPerProviderFooter = new StringBuffer();

        StringBuffer claimsPerCheckNumberBody = new StringBuffer();
        StringBuffer claimsPerCheckNumberFooter = new StringBuffer();


        StringBuffer claimsPerPayerBody = new StringBuffer();
        StringBuffer claimsPerPayerFooter = new StringBuffer();



        ResultSet rset = null;
        PreparedStatement ps = null;


        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String DateRange = request.getParameter("DateRange").trim();
        try {
            ps = conn.prepareStatement("SELECT IFNULL(DATE_FORMAT(a.ReceivedDate,'%m/%d/%Y'),''),IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'')" +
                    " ,IFNULL(DATE_FORMAT(c.DOS,'%m/%d/%Y'),''),IFNULL(b.Charges,''),IFNULL(c.PatientMRN,''),IFNULL(c.PatientName,''), FORMAT(IFNULL(b.Paid,''),2)," +
                    " FORMAT(IFNULL(b.Adjusted,''),2),IFNULL(a.CheckNumber,''),IFNULL(a.CreatedBy,'') ,IFNULL(b.ClaimNumber,'') " +
                    " FROM " + Database + ".EOB_Master a" +
                    " INNER JOIN " +Database+".Claim_Ledger_Charges_entries b ON a.Id=b.TransactionIdx "+
                    " INNER JOIN " +Database+".ClaimInfoMaster c ON b.ClaimNumber=c.ClaimNumber "+
                    " WHERE DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedDate,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59'  AND b.TransactionType='D' AND b.Deleted IS NULL "+
                    " ORDER BY a.CreatedDate DESC");
            rset = ps.executeQuery();
            while(rset.next()){
                insurancePaymentBody.append("<tr style='text-align:center;'>");
                insurancePaymentBody.append("<td >" + rset.getString(1) + "</td>\n");//Payment Received
                insurancePaymentBody.append("<td >" + rset.getString(2) + "</td>\n");//Payment Entered
                insurancePaymentBody.append("<td >" + rset.getString(3) + "</td>\n");//Charge From Date
                insurancePaymentBody.append("<td >" + rset.getString(11) + "</td>\n");//Claim Number
                insurancePaymentBody.append("<td >" + rset.getString(4) + "</td>\n");//Charge CPT Code
                insurancePaymentBody.append("<td >" + rset.getString(5) + "</td>\n");//MRN
                insurancePaymentBody.append("<td >" + rset.getString(6) + "</td>\n");//Patient Name
                insurancePaymentBody.append("<td >" + rset.getString(7) + "</td>\n");//Paid
                insurancePaymentBody.append("<td >" + rset.getString(8) + "</td>\n");//Adjustment
                insurancePaymentBody.append("<td >" + rset.getString(9) + "</td>\n");//Check #
                insurancePaymentBody.append("<td >" + rset.getString(10) + "</td>\n");//Payment Username
                insurancePaymentBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2) " +
                    " FROM " + Database + ".Claim_Ledger_Charges_entries a" +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '" + FromDate + " 00:00:00'" +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '" + ToDate + " 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL ");

            rset = ps.executeQuery();
            if(rset.next()){
                insurancePaymentFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(1)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(2)+"</th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    "CASE " +
                    " WHEN b.ClaimType = 1 THEN 'Institutional'  " +
                    " WHEN b.ClaimType = 2 THEN 'Professional' " +
                    "END ," +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL " +
                    "GROUP BY b.ClaimType");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerClaimTypeBody.append("<tr style='text-align:center;'>");
                claimsPerClaimTypeBody.append("<td >" + rset.getString(1) + "</td>\n");//Claim Type
                claimsPerClaimTypeBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Payments
                claimsPerClaimTypeBody.append("<td >" + rset.getString(3) + "</td>\n");//Payment (Sum)
                claimsPerClaimTypeBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Adjustments
                claimsPerClaimTypeBody.append("<td >" + rset.getString(4) + "</td>\n");//Adjustment (Sum)
                claimsPerClaimTypeBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL");
            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerClaimTypeFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +//Claim Type
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Payments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(2)+"</th>\n" +//Payment (Sum)
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Adjustments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +//Adjustment (Sum)
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();




            ps = conn.prepareStatement("SELECT  " +
                    "CONCAT(IFNULL(c.DoctorsLastName,''),', ', IFNULL(c.DoctorsFirstName,''))," +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " INNER JOIN "+Database+".DoctorsList c ON c.Id=b.BillingProviders" +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL " +
                    "GROUP BY b.BillingProviders");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerProviderBody.append("<tr style='text-align:center;'>");
                claimsPerProviderBody.append("<td >" + rset.getString(1) + "</td>\n");//Provider Name
                claimsPerProviderBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Payments
                claimsPerProviderBody.append("<td >" + rset.getString(3) + "</td>\n");//Payment (Sum)
                claimsPerProviderBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Adjustments
                claimsPerProviderBody.append("<td >" + rset.getString(4) + "</td>\n");//Adjustment (Sum)
                claimsPerProviderBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL");
            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerProviderFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +//Provider Name
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Payments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(2)+"</th>\n" +//Payment (Sum)
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Adjustments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +//Adjustment (Sum)
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    "c.CheckNumber," +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " INNER JOIN "+Database+".EOB_Master c ON c.Id=a.TransactionIdx" +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL  " +
                    "GROUP BY a.TransactionIdx");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerCheckNumberBody.append("<tr style='text-align:center;'>");
                claimsPerCheckNumberBody.append("<td >" + rset.getString(1) + "</td>\n");//Check Number
                claimsPerCheckNumberBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Payments
                claimsPerCheckNumberBody.append("<td >" + rset.getString(3) + "</td>\n");//Payment (Sum)
                claimsPerCheckNumberBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Adjustments
                claimsPerCheckNumberBody.append("<td >" + rset.getString(4) + "</td>\n");//Adjustment (Sum)
                claimsPerCheckNumberBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL");
            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerCheckNumberFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +//Check Number
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Payments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(2)+"</th>\n" +//Payment (Sum)
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Adjustments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +//Adjustment (Sum)
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    "IFNULL(LTRIM(rtrim(REPLACE(d.PayerName,'Servicing States','') )),'')," +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " INNER JOIN "+Database+".EOB_Master c ON c.Id=a.TransactionIdx" +
                    " INNER JOIN ClaimMasterDB.ProfessionalPayersWithFC d ON d.Id=c.InsuranceIdx" +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL  " +
                    "GROUP BY c.InsuranceIdx");
            rset = ps.executeQuery();
            while(rset.next()){
                claimsPerPayerBody.append("<tr style='text-align:center;'>");
                claimsPerPayerBody.append("<td >" + rset.getString(1) + "</td>\n");//Payer Name
                claimsPerPayerBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Payments
                claimsPerPayerBody.append("<td >" + rset.getString(3) + "</td>\n");//Payment (Sum)
                claimsPerPayerBody.append("<td >" + rset.getString(2) + "</td>\n");//Total # of Adjustments
                claimsPerPayerBody.append("<td >" + rset.getString(4) + "</td>\n");//Adjustment (Sum)
                claimsPerPayerBody.append("</tr>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("SELECT  " +
                    " Count(a.Paid), " +
                    " FORMAT(SUM(a.Paid),2),FORMAT(SUM(a.Adjusted),2)   " +
                    " FROM  "+Database+".Claim_Ledger_Charges_entries a " +
                    " LEFT JOIN "+Database+".ClaimInfoMaster b ON a.ClaimNumber=b.ClaimNumber " +
                    " WHERE DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') >= '"+FromDate+" 00:00:00' " +
                    " AND DATE_FORMAT(a.CreatedAt,'%Y-%m-%d %h:%i:%s') <= '"+ToDate+" 23:59:59' AND a.TransactionType='D' AND a.Deleted IS NULL");
            rset = ps.executeQuery();
            if(rset.next()){
                claimsPerPayerFooter.append("<tfoot style=\"color:white;\">\n" +
                        "<tr bgcolor=\"#249ad5\" >\n" +
                        "<th style='text-align:center;border-top: 6.5px double black;'></th>\n" +//Payer Name
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Payments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(2)+"</th>\n" +//Payment (Sum)
                        "<th style='text-align:center;border-top: 6.5px double black;'>"+rset.getString(1)+"</th>\n" +//Total # of Adjustments
                        "<th style='text-align:center;border-top: 6.5px double black;'>$"+rset.getString(3)+"</th>\n" +//Adjustment (Sum)
                        "</tr>\n" +
                        "</tfoot>");
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", UserId);

            Parser.SetField("insurancePaymentBody", String.valueOf(insurancePaymentBody));
            Parser.SetField("insurancePaymentFooter", String.valueOf(insurancePaymentFooter));

            Parser.SetField("claimsPerClaimTypeBody", String.valueOf(claimsPerClaimTypeBody));
            Parser.SetField("claimsPerClaimTypeFooter", String.valueOf(claimsPerClaimTypeFooter));

            Parser.SetField("claimsPerProviderBody", String.valueOf(claimsPerProviderBody));
            Parser.SetField("claimsPerProviderFooter", String.valueOf(claimsPerProviderFooter));

            Parser.SetField("claimsPerCheckNumberBody", String.valueOf(claimsPerCheckNumberBody));
            Parser.SetField("claimsPerCheckNumberFooter", String.valueOf(claimsPerCheckNumberFooter));

            Parser.SetField("claimsPerPayerBody", String.valueOf(claimsPerPayerBody));
            Parser.SetField("claimsPerPayerFooter", String.valueOf(claimsPerPayerFooter));

            Parser.SetField("ToDate", ToDate);
            Parser.SetField("FromDate", FromDate);
            Parser.SetField("DateRange", DateRange);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "ClaimReports/InsurancePaymentReport.html");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < (e.getStackTrace()).length; i++)
                str = str + e.getStackTrace()[i] + "<br>";
            System.out.println(str);
        }
    }

}