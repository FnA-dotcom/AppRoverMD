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
import java.text.ParseException;
import java.text.SimpleDateFormat;


@SuppressWarnings("Duplicates")
public class ViewClaimPayment extends HttpServlet {
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
        String Action;
        String UserIndex = "";


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
            UserIndex = session.getAttribute("UserIndex").toString();

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "getViewClaimPaymentInput":
                    getViewClaimPaymentInput(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "viewActivity":
                    viewActivity(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "viewActivity_Ins":
                    viewActivity_Ins(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "getClaims_WRT_Check":
                    getClaims_WRT_Check(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "getClaims_WRT_PatientPayment_inside":
                    getClaims_WRT_PatientPayment_inside(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "getClaims_WRT_PatientPayment":
                    getClaims_WRT_PatientPayment(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "getClaims_WRT_Check_inside":
                    getClaims_WRT_Check_inside(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "getCharges_WRT_Claims":
                    getCharges_WRT_Claims(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "setCharges_WRT_Claims":
                    setCharges_WRT_Claims(request, out, conn, context, UserIndex, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "setClaims_WRT_Checks":
                    setClaims_WRT_Checks(request, out, conn, context, UserIndex, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "setClaims_WRT_PatientPayment":
                    setClaims_WRT_PatientPayment(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
                    break;
                case "ShowReport":
                    ShowReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "doEmpty":
                    doEmpty(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "searchPatient":
                    searchPatient(request, out, conn, context, UserId, DatabaseName, helper, FacilityIndex, payments);
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
            helper.SendEmailWithAttachment("Error in ViewClaimPayment ** (handleRequest)", context, Ex, "ViewClaimPayment", "handleRequest", conn);
            Services.DumException("ViewClaimPayment", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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

    private void getViewClaimPaymentInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        PreparedStatement ps = null;
        ResultSet rset = null;
        StringBuilder EOBList = new StringBuilder();
        StringBuilder PayerList = new StringBuilder();
        String Source = null;
        String Link = null;
        String FilterKey = request.getParameter("FilterKey") == null ? "" : request.getParameter("FilterKey").trim();
        String PatIdx = request.getParameter("PatIdx") == null ? "" : request.getParameter("PatIdx").trim();
        String Payers = request.getParameter("PayerList") == null ? "" : request.getParameter("PayerList").trim();
        String FromDate = request.getParameter("FromDate") == null ? "" : request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate") == null ? "" : request.getParameter("ToDate").trim();
        String Filter = "";

//        System.out.println("Payers -> "+Payers);

        if (FilterKey.compareTo("") != 0) {
            Filter = " WHERE (" +
                    " (REPLACE(f.PayerName,'Servicing States','') LIKE '%" + FilterKey + "%') " +
                    " OR (e.PatientName LIKE '%" + FilterKey + "%') " +
                    " OR (CheckNumber LIKE '%" + FilterKey + "%') " +
                    " OR (d.ClaimNumber LIKE '%" + FilterKey + "%') " +
                    " OR (PaymentAmount LIKE '%" + FilterKey + "%')" +
                    ") ";
            if (PatIdx.compareTo("") != 0) {
                Filter += " AND (PatientIdx LIKE '%" + PatIdx + "%') ";
            }

            if (Payers.compareTo("") != 0) {
                Filter += " AND (a.InsuranceIdx in (" + Payers + ")) ";
            }


        } else if (PatIdx.compareTo("") != 0) {
            Filter += " WHERE (PatientIdx LIKE '%" + PatIdx + "%') ";
        } else if (Payers.compareTo("") != 0) {
            Filter += " WHERE (a.InsuranceIdx in (" + Payers + ")) ";
        }

        if (FromDate.compareTo("") != 0 && ToDate.compareTo("") != 0) {
            if (FromDate.equals(ToDate))
                if (Filter.equals(""))
                    Filter += " WHERE (ReceivedDate = '" + FromDate + "')";
                else
                    Filter += " AND (ReceivedDate = '" + FromDate + "')";
            else if (Filter.equals(""))
                Filter += " WHERE (ReceivedDate >= '" + FromDate + "') AND (ReceivedDate <= '" + ToDate + "')";
            else
                Filter += " AND (ReceivedDate >= '" + FromDate + "') AND (ReceivedDate <= '" + ToDate + "')";
        }


        try {
            ps = conn.prepareStatement("SELECT IFNULL(LTRIM(rtrim(REPLACE(f.PayerName,'Servicing States','') )),IFNULL(e.PatientName,'')) ,DATE_FORMAT(ReceivedDate,'%m/%d/%Y'),IFNULL(CheckNumber,'')" +
                    ",IFNULL(FORMAT(PaymentAmount, 2),''),IFNULL(FORMAT(AppliedAmount, 2),'0.00')," +
                    "IFNULL(FORMAT(UnappliedAmount, 2),'0.00') , a.InsuranceIdx , a.Id , a.PatientIdx" +
                    " FROM " + database + ".EOB_Master a " +
                    " LEFT JOIN oe_2.ProfessionalPayers f on a.InsuranceIdx = f.Id " +
                    " LEFT JOIN " + database + ".ClaimInfoMaster e on a.PatientIdx = e.PatientRegId " +
                    " INNER JOIN " + database + ".Claim_Ledger_Charges_entries d on a.Id = d.TransactionIdx "
                    + Filter +
                    "GROUP BY d.ClaimNumber  ORDER BY a.ViewDate DESC ");
            rset = ps.executeQuery();
            while (rset.next()) {
                if (rset.getString(7) != null) {
                    Source = "Insurance";
                    Link = "md.ViewClaimPayment?ActionID=getClaims_WRT_Check&checkNumber=" + rset.getString(3) + "&InsuranceIdx=" + rset.getString(7) + "&Flag=0&EOB_MasterIdx=" + rset.getString(8);
                } else {
                    Source = "Patient";
                    Link = "md.ViewClaimPayment?ActionID=getClaims_WRT_PatientPayment&PatRegIdx=" + rset.getString(9) + "&ReceivedDate=" + rset.getString(2) + "&Amt=" + rset.getString(4) + "&TransactionType=D&Flag=0&EOB_MasterIdx=" + rset.getString(8);
                }

                EOBList.append(" <tr onclick='openScreen(`" + Link + "`)'>\n");
                EOBList.append("<td>" + Source + "</td>\n");
                EOBList.append("<td>" + rset.getString(1) + "</td>\n" +
                        "<td>" + rset.getString(2) + "</td>\n" +
                        "<td>" + rset.getString(3) + "</td>\n" +
                        "<td>$" + rset.getString(4) + "</td>\n" +
                        "<td>$" + rset.getString(5) + "</td>\n" +
                        "<td>$" + rset.getString(6) + "</td>\n" +
                        "</tr>");
            }
            rset.close();
            ps.close();

            ps = conn.prepareStatement("Select a.Id, a.PayerId, LTRIM(rtrim(REPLACE(a.PayerName,'Servicing States','') )), Count(*) from oe_2.ProfessionalPayers a\n" +
                    "LEFT JOIN " + database + ".InsuranceInfo b ON  a.Id=b.PriInsuranceName\n" +
                    "where a.Id  in (902,8289,8297,123,127,5800,1259,2700,5978,389,2337,1460,2348,3901,2583,2588,2393,955,64,1545,3646,8589,200,201,202,203,204,205,206,207,3649,5978,8206,4763,3465,3466,3467,3468,41,387,388,389,697,698,4757, 8605, 8606, 8254, 2560, 1663,8176,1049) \n" +
                    "AND Status != 100 GROUP BY b.PriInsuranceName ORDER BY Count(*) DESC");
//            System.out.println("QUERY - >> " + ps.toString());
            rset = ps.executeQuery();
            while (rset.next()) {
                PayerList.append("<option value='" + rset.getString(1) + "'>" + rset.getString(3) + "</option>");
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("getEOBs", String.valueOf(EOBList));
            Parser.SetField("PayerList", String.valueOf(PayerList));
            Parser.SetField("Payers", Payers);
            Parser.SetField("FilterKey", String.valueOf(FilterKey));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in ViewClaimPayment Payment ** (getViewClaimPaymentInput)", servletContext, Ex, "ViewClaimPayment", "getViewClaimPaymentInput", conn);
            Services.DumException("ViewClaimPayment", "getViewClaimPaymentInput", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }


    private void viewActivity(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        try {
            String claimNumber = request.getParameter("claimNumber").trim();
            ResultSet rset1 = null;
            ResultSet rset2 = null;
            ResultSet rset3 = null;
            String chargeStatus = "ON HOLD";

            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            PreparedStatement ps3 = null;

            double TotalPayment = 0.0f;
            double TotalAdjustment = 0.0f;
            double TotalBalance = 0.0f;
            double CLAIM_TotalAmount = 0.0f;
            double CLAIM_TotalPayment = 0.0f;
            double CLAIM_TotalAdjustment = 0.0f;
            double CLAIM_TotalBalance = 0.0f;

            StringBuilder ActivityList = new StringBuilder();

            ps1 = conn.prepareStatement("SELECT a.HCPCSProcedure , a.ServiceFromDate , b.CPTDescription , a.Amount  FROM " + database + ".ClaimChargesInfo a " +
                    "INNER JOIN ClaimMasterDB.CPTMaster b ON a.HCPCSProcedure=b.CPTCode  " +
                    "WHERE a.ClaimNumber=? AND b.EffectiveDate < NOW()");
            ps1.setString(1, claimNumber);

            //system.out.println("viewActivity ->> " + ps1.toString());

            rset1 = ps1.executeQuery();
            while (rset1.next()) {
                ActivityList.append("<tr>\n" +
                        "<td scope=\"row\" style='border-bottom: none !important;'>" + rset1.getString(1) + "</td> " +//Charges
                        "<td>" + rset1.getString(2) + "</td>\n" +//DOS
                        "<td>" + rset1.getString(3) + "</td>\n" +//Desc
                        "<td>$" + rset1.getString(4) + "</td>\n" +//Amount
                        "<td></td>" +
                        "<td></td>" +
                        "<td></td>" +
                        "</tr>");
                ps2 = conn.prepareStatement("SELECT  DATE_FORMAT(a.ReceivedDate,'%m/%d/%Y') , a.CheckNumber , " +
                        " UPPER(b.PayerName) , IFNULL(c.Payment,'0.00') , IFNULL(c.Adjustment,'0.00')  , IFNULL(c.Balance,'0.00') , IFNULL(d.ChargeOption,'')" +
                        "  FROM  " + database + ".EOB_Master a" +
                        " INNER JOIN " + database + ".ProfessionalPayers b ON a.InsuranceIdx = b.Id" +
                        " INNER JOIN " + database + ".Claim_Ledger_Charges_entries c ON a.Id = c.TransactionIdx" +
                        " LEFT JOIN oe.ChargeOption d on c.Status = d.Id " +
                        " WHERE c.ClaimNumber=? AND c.Charges=? ORDER BY a.ReceivedDate ASC");
                ps2.setString(1, claimNumber);
                ps2.setString(2, rset1.getString(1));
                //system.out.println("QUERY ACTIVITY ->> " + ps2.toString());
                rset2 = ps2.executeQuery();
                TotalPayment = TotalAdjustment = TotalBalance = 0.0f;
                while (rset2.next()) {
                    ActivityList.append("<tr >\n" +
                            "<td scope=\"row\"class=\"border-less\" ></td> " +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td class='eli' data-toggle=\"tooltip\" title=\"[EOB] - Chk#" + rset2.getString(2) + " - PAYMENT FROM " + rset2.getString(3) + "\"> [EOB] - Chk#" + rset2.getString(2) + " - PAYMENT FROM " + rset2.getString(3) + "</td>\n" +
                            "<td></td>\n" +
                            "<td>$" + String.valueOf(String.format(rset2.getString(4))) + "</td>\n" +//Payment
                            "<td></td>\n" +
                            "<td></td>\n" +
                            "</tr>");
                    ActivityList.append("<tr >\n" +
                            "<td scope=\"row\"class=\"border-less\" ></td> " +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td class='eli' data-toggle=\"tooltip\" title=\"[EOB] - Chk#" + rset2.getString(2) + " - ADJUSTMENT BY " + rset2.getString(3) + "\"> [EOB] - Chk#" + rset2.getString(2) + " - ADJUSTMENT BY " + rset2.getString(3) + "</td>\n" +
                            "<td></td>\n" +
                            "<td></td>\n" +
                            "<td>$" + String.valueOf(String.format(rset2.getString(5))) + "</td>\n" +//Adjustment
                            "<td></td>\n" +
                            "</tr>");

                    //system.out.println("BALANCE ->> " + rset2.getString(6));
                    TotalPayment += Double.parseDouble(rset2.getString(4));
                    TotalAdjustment += Double.parseDouble(rset2.getString(5));
                    TotalBalance += Double.parseDouble(rset2.getString(6));
                    chargeStatus = rset2.getString(7);
                }

                ActivityList.append("<tr class=\"font-weight-bold\">\n" +
                        "<td scope=\"row\"class=\"border-less\" ></td> " +
                        "<td colspan='3' >" + chargeStatus + " as of " + rset1.getString(2) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalPayment)) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalAdjustment)) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalBalance)) + "</td>" +
                        "</tr>");
                rset2.close();
                ps2.close();
                ActivityList.append("</tr>");


                CLAIM_TotalAmount += Double.parseDouble(rset1.getString(4));
                CLAIM_TotalPayment += TotalPayment;
                CLAIM_TotalAdjustment += TotalAdjustment;
                CLAIM_TotalBalance += TotalBalance;
            }
            rset1.close();
            ps1.close();

            ActivityList.append("<tr class=\"font-weight-bold\" style='border-top: 2px solid black !important;'>\n" +
                    "<td scope=\"row\" colspan='3'>Claim Totals</td> " +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalAmount)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalPayment)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalAdjustment)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalBalance)) + "</td>" +
                    "</tr>");

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ActivityList", String.valueOf(ActivityList));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view.html");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/apply_credit_patient.html");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check_NEW.html");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_Activity.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in viewActivity ** (handleRequest)", servletContext, Ex, "viewActivity", "handleRequest", conn);
            Services.DumException("viewActivity", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    private void viewActivity_Ins(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        try {
            String claimNumber = request.getParameter("claimNumber").trim();
            ResultSet rset1 = null;
            ResultSet rset2 = null;
            ResultSet rset3 = null;
            String chargeStatus = "ON HOLD";

            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            PreparedStatement ps3 = null;

            double TotalPayment = 0.0f;
            double TotalAdjustment = 0.0f;
            double TotalBalance = 0.0f;
            double CLAIM_TotalAmount = 0.0f;
            double CLAIM_TotalPayment = 0.0f;
            double CLAIM_TotalAdjustment = 0.0f;
            double CLAIM_TotalBalance = 0.0f;

            StringBuilder ActivityList = new StringBuilder();

            ps1 = conn.prepareStatement("SELECT a.HCPCSProcedure , a.ServiceDate , b.CPTDescription , a.Amount  FROM " + database + ".ClaimChargesInfo a " +
                    "INNER JOIN ClaimMasterDB.CPTMaster b ON a.HCPCSProcedure=b.CPTCode  " +
                    "WHERE a.ClaimNumber=? AND b.EffectiveDate < NOW()");
            ps1.setString(1, claimNumber);

            //system.out.println("viewActivity ->> " + ps1.toString());

            rset1 = ps1.executeQuery();
            while (rset1.next()) {
                ActivityList.append("<tr>\n" +
                        "<td scope=\"row\" style='border-bottom: none !important;'>" + rset1.getString(1) + "</td> " +//Charges
                        "<td>" + rset1.getString(2) + "</td>\n" +//DOS
                        "<td>" + rset1.getString(3) + "</td>\n" +//Desc
                        "<td>$" + rset1.getString(4) + "</td>\n" +//Amount
                        "<td></td>" +
                        "<td></td>" +
                        "<td></td>" +
                        "</tr>");
                ps2 = conn.prepareStatement("SELECT  DATE_FORMAT(a.ReceivedDate,'%m/%d/%Y') , a.CheckNumber , " +
                        " UPPER(b.PayerName) , IFNULL(c.Payment,'0.00') , IFNULL(c.Adjustment,'0.00')  , IFNULL(c.Balance,'0.00') , IFNULL(d.ChargeOption,'')" +
                        "  FROM  " + database + ".EOB_Master a" +
                        " INNER JOIN " + database + ".ProfessionalPayers b ON a.InsuranceIdx = b.Id" +
                        " INNER JOIN " + database + ".Claim_Ledger_Charges_entries c ON a.Id = c.TransactionIdx" +
                        " LEFT JOIN oe.ChargeOption d on c.Status = d.Id " +
                        " WHERE c.ClaimNumber=? AND c.Charges=? ORDER BY a.ReceivedDate ASC");
                ps2.setString(1, claimNumber);
                ps2.setString(2, rset1.getString(1));
                //system.out.println("QUERY ACTIVITY ->> " + ps2.toString());
                rset2 = ps2.executeQuery();
                TotalPayment = TotalAdjustment = TotalBalance = 0.0f;
                while (rset2.next()) {
                    ActivityList.append("<tr >\n" +
                            "<td scope=\"row\"class=\"border-less\" ></td> " +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td class='eli' data-toggle=\"tooltip\" title=\"[EOB] - Chk#" + rset2.getString(2) + " - PAYMENT FROM " + rset2.getString(3) + "\"> [EOB] - Chk#" + rset2.getString(2) + " - PAYMENT FROM " + rset2.getString(3) + "</td>\n" +
                            "<td></td>\n" +
                            "<td>$" + String.valueOf(String.format(rset2.getString(4))) + "</td>\n" +//Payment
                            "<td></td>\n" +
                            "<td></td>\n" +
                            "</tr>");
                    ActivityList.append("<tr >\n" +
                            "<td scope=\"row\"class=\"border-less\" ></td> " +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td class='eli' data-toggle=\"tooltip\" title=\"[EOB] - Chk#" + rset2.getString(2) + " - ADJUSTMENT BY " + rset2.getString(3) + "\"> [EOB] - Chk#" + rset2.getString(2) + " - ADJUSTMENT BY " + rset2.getString(3) + "</td>\n" +
                            "<td></td>\n" +
                            "<td></td>\n" +
                            "<td>$" + String.valueOf(String.format(rset2.getString(5))) + "</td>\n" +//Adjustment
                            "<td></td>\n" +
                            "</tr>");

                    //system.out.println("BALANCE ->> " + rset2.getString(6));
                    TotalPayment += Double.parseDouble(rset2.getString(4));
                    TotalAdjustment += Double.parseDouble(rset2.getString(5));
                    TotalBalance += Double.parseDouble(rset2.getString(6));
                    chargeStatus = rset2.getString(7);
                }

                ActivityList.append("<tr class=\"font-weight-bold\">\n" +
                        "<td scope=\"row\"class=\"border-less\" ></td> " +
                        "<td colspan='3' >" + chargeStatus + " as of " + rset1.getString(2) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalPayment)) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalAdjustment)) + "</td>\n" +
                        "<td>$" + String.valueOf(String.format("%,.2f", TotalBalance)) + "</td>" +
                        "</tr>");
                rset2.close();
                ps2.close();
                ActivityList.append("</tr>");


                CLAIM_TotalAmount += Double.parseDouble(rset1.getString(4));
                CLAIM_TotalPayment += TotalPayment;
                CLAIM_TotalAdjustment += TotalAdjustment;
                CLAIM_TotalBalance += TotalBalance;
            }
            rset1.close();
            ps1.close();

            ActivityList.append("<tr class=\"font-weight-bold\" style='border-top: 2px solid black !important;'>\n" +
                    "<td scope=\"row\" colspan='3'>Claim Totals</td> " +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalAmount)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalPayment)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalAdjustment)) + "</td>\n" +
                    "<td>$" + String.valueOf(String.format("%,.2f", CLAIM_TotalBalance)) + "</td>" +
                    "</tr>");

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ActivityList", String.valueOf(ActivityList));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view.html");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/apply_credit_patient.html");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check_NEW.html");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_Activity.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in viewActivity ** (handleRequest)", servletContext, Ex, "viewActivity", "handleRequest", conn);
            Services.DumException("viewActivity", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }


    private void getClaims_WRT_PatientPayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException, SQLException {

        String PatRegIdx = request.getParameter("PatRegIdx").trim();
        String Flag = request.getParameter("Flag");
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx") == null ? null : request.getParameter("EOB_MasterIdx").trim();
        String ReceivedDate = request.getParameter("ReceivedDate").trim();
        Double PaymentAmount = Double.parseDouble(request.getParameter("Amt").trim().replace(",", ""));
        String AppliedAmount = null;
        String UnappliedAmount = null;
        String TransactionType = request.getParameter("TransactionType") == null ? "Cr" : "D";

        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rset1 = null;
        ResultSet rset2 = null;

        String ClaimNumber = null;
        String DOS = null;
        String PatientName = null;
        String _ChargeOption = null;
        String filter = "";

        StringBuilder ClaimList = new StringBuilder();
        StringBuilder ChargeOption = new StringBuilder();
        int count = 0;

        if (Flag.equals("0")) {
            filter = " AND TransactionIdx=?";
        }


        ps1 = conn.prepareStatement("SELECT FORMAT(IFNULL(AppliedAmount,'0.00'),2), FORMAT(IFNULL(UnappliedAmount,'0.00'),2) FROM " + database + ".EOB_Master WHERE Id=?");
        ps1.setString(1, EOB_MasterIdx);
        rset1 = ps1.executeQuery();
        while (rset1.next()) {
            AppliedAmount = rset1.getString(1);
            UnappliedAmount = rset1.getString(2);
        }
        rset1.close();
        ps1.close();


        ps1 = conn.prepareStatement("SELECT IFNULL(ClaimNumber,''), DATE_FORMAT(DOS,'%m/%d/%Y') ,IFNULL(PatientName,'') FROM " + database + ".ClaimInfoMaster WHERE PatientRegId=?");
        ps1.setString(1, PatRegIdx);
        rset1 = ps1.executeQuery();
        while (rset1.next()) {
            ClaimNumber = rset1.getString(1);
            DOS = rset1.getString(2);
            PatientName = rset1.getString(3);


            ps2 = conn.prepareStatement("SELECT  IFNULL(b.Charges,''), IFNULL(b.Amount,'0.00') " +
                    " , IFNULL(b.StartBalance,IFNULL(b.Amount,'0.00')) , IFNULL(b.Paid,'0.00')  " +
                    " , IFNULL(e.ChargeOption,''), IFNULL(b.OtherCredits,'0.00'), IFNULL(b.Adjusted,'0.00'),IFNULL(b.EndBalance,IFNULL(b.Amount,'0.00')) " +
                    " , IFNULL(e.Id,'4'), IFNULL(b.ChargeIdx,''), IFNULL(b.ClaimIdx,'') " +
                    " from  " + database + ".Claim_Ledger_Charges_entries b " +
                    " LEFT JOIN oe.ChargeOption e on b.Status = e.Id " +
                    " WHERE b.ClaimNumber=? and b.TransactionType=? " + filter);
            ps2.setString(1, ClaimNumber);
            ps2.setString(2, TransactionType);
            if (Flag.equals("0")) {
//                ps.setString(3, checkNumber);
                ps2.setString(3, EOB_MasterIdx);
            }

            //system.out.println("QUERY ->> " + ps2.toString());

            rset2 = ps2.executeQuery();
            while (rset2.next()) {
                ClaimList.append("<tr>\n" +
                        "     <td class=\"edit-disabled\" >" + ClaimNumber + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + DOS + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + rset2.getString(1) + "</td>\n" + //Charge
                        "     <td  class=\"edit-disabled\" >" + rset2.getString(2) + "</td>\n" + //Amount
                        "     <td class=\"edit-disabled\" id='StartBal_" + count + "'>" + rset2.getString(3) + "</td>\n" + //Start Balance
                        "     <td id='Paid_" + count + "'>" + rset2.getString(4) + "</td>\n" + //Paid
                        "     <td class=\"edit-disabled\" id='ChargeStatus_" + count + "'>" + rset2.getString(5) + "</td>\n" + //Status
                        "     <td style='display:none' >" + rset2.getString(9) + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + rset2.getString(6) + "</td>\n" +//OtherCred
                        "     <td class=\"edit-disabled\" >" + rset2.getString(7) + "</td>\n" +//Adjusted
                        "     <td class=\"edit-disabled\" id='EndBal_" + count + "'>" + rset2.getString(8) + "</td>\n" +//End Balance
                        "     <td style='display:none'>" + rset2.getString(10) + "</td>\n" +
                        "     <td style='display:none'>" + rset2.getString(11) + "</td>\n" +
                        "     <td  id='FixedStatus_" + count + "'style='display:none'>" + rset2.getString(9) + "</td>\n" +
                        " </tr>");
                count++;

                _ChargeOption = rset2.getString(5);

            }
            rset2.close();
            ps2.close();

        }
        rset1.close();
        ps1.close();


        ps1 = conn.prepareStatement("Select Id, IFNULL(UPPER(ChargeOption),'') from oe.ChargeOption where status = 1");
        rset1 = ps1.executeQuery();
        while (rset1.next()) {
            if (_ChargeOption.equals(rset1.getString(2)))
                ChargeOption.append("<option class=Inner value=\"" + rset1.getString(1) + "\" selected>" + rset1.getString(2) + "</option>");
            else
                ChargeOption.append("<option class=Inner value=\"" + rset1.getString(1) + "\">" + rset1.getString(2) + "</option>");
        }
        rset1.close();
        ps1.close();

        ps1 = conn.prepareStatement("UPDATE " + database + ".EOB_Master" +
                " SET ViewDate = NOW()" +
                " where id=?");
        ps1.setString(1, EOB_MasterIdx);
        ps1.executeUpdate();


        Parsehtm Parser = new Parsehtm(request);
        Parser.SetField("ClaimList", String.valueOf(ClaimList));
        Parser.SetField("ChargeOption", String.valueOf(ChargeOption));
        Parser.SetField("PatientName", PatientName);
        Parser.SetField("receivingDate", ReceivedDate);
        Parser.SetField("PatRegIdx", PatRegIdx);
        Parser.SetField("Flag", Flag);
        Parser.SetField("EOB_MasterIdx", EOB_MasterIdx);
        Parser.SetField("tableCount", String.valueOf(count));
        Parser.SetField("PaymentAmount", String.valueOf(String.format("%,.2f", PaymentAmount)));

        Parser.SetField("StartBal", "0.00");
        Parser.SetField("EndBal", "0.00");
        Parser.SetField("StartCred", "0.00");
        Parser.SetField("EndCredit", String.valueOf(UnappliedAmount));
        Parser.SetField("AppliedAmt", String.valueOf(AppliedAmount));
        Parser.SetField("UnAppliedAmt", String.valueOf(UnappliedAmount));
        Parser.SetField("cancelLink", "md.ViewClaimPayment?ActionID=getViewClaimPaymentInput");

        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_PatientPayment.html");
    }

    private void getClaims_WRT_PatientPayment_inside(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException, SQLException {

        String PatRegIdx = request.getParameter("PatRegIdx").trim();
        String checkNumber = request.getParameter("checkNumber").trim();
        String ReceivedDate = request.getParameter("ReceivedDate").trim();
        String paymentType = request.getParameter("paymentType").trim();
        String paymentSource = request.getParameter("paymentSource").trim();
        String memo = request.getParameter("memo").trim();
        String CopayDOS = request.getParameter("CopayDOS").trim();
        String CardType = request.getParameter("CardType").trim();
        Double PaymentAmount = Double.parseDouble(request.getParameter("Amt").trim());
        String TransactionType = request.getParameter("TransactionType") == null ? "Cr" : "D";

        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rset1 = null;
        ResultSet rset2 = null;

        String ClaimNumber = null;
        String DOS = null;
        String PatientName = null;
        String _ChargeOption = null;

        StringBuilder ClaimList = new StringBuilder();
        StringBuilder ChargeOption = new StringBuilder();
        int count = 0;

        ps1 = conn.prepareStatement("SELECT IFNULL(ClaimNumber,''), DATE_FORMAT(DOS,'%m/%d/%Y') ,IFNULL(PatientName,'') FROM " + database + ".ClaimInfoMaster WHERE PatientRegId=?");
        ps1.setString(1, PatRegIdx);
        rset1 = ps1.executeQuery();
        while (rset1.next()) {
            ClaimNumber = rset1.getString(1);
            DOS = rset1.getString(2);
            PatientName = rset1.getString(3);


            ps2 = conn.prepareStatement("SELECT  IFNULL(b.Charges,''), FORMAT(IFNULL(b.Amount,'0.00'),2) " +
                    " , FORMAT(IFNULL(b.StartBalance,IFNULL(b.Amount,'0.00')),2) , FORMAT(IFNULL(b.Paid,'0.00'),2)  " +
                    " , IFNULL(e.ChargeOption,''), FORMAT(IFNULL(b.OtherCredits,'0.00'),2), FORMAT(IFNULL(b.Adjusted,'0.00'),2), FORMAT(IFNULL(b.EndBalance,IFNULL(b.Amount,'0.00')),2) " +
                    " , IFNULL(e.Id,'4'), IFNULL(b.ChargeIdx,''), IFNULL(b.ClaimIdx,'') " +
                    " from  " + database + ".Claim_Ledger_Charges_entries b " +
                    " LEFT JOIN oe.ChargeOption e on b.Status = e.Id " +
                    " WHERE b.ClaimNumber=? and b.TransactionType=? AND b.Deleted is NULL ");
            ps2.setString(1, ClaimNumber);
            ps2.setString(2, TransactionType);

            System.out.println(" getClaims_WRT_PatientPayment_inside Query ->> " + ps2.toString());
            rset2 = ps2.executeQuery();
            while (rset2.next()) {
                ClaimList.append("<tr>\n" +
                        "     <td class=\"edit-disabled\" >" + ClaimNumber + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + DOS + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + rset2.getString(1) + "</td>\n" + //Charge
                        "     <td  class=\"edit-disabled\" >" + rset2.getString(2) + "</td>\n" + //Amount
                        "     <td class=\"edit-disabled\" id='StartBal_" + count + "'>" + rset2.getString(3) + "</td>\n" + //Start Balance
                        "     <td id='Paid_" + count + "'>" + rset2.getString(4) + "</td>\n" + //Paid
                        "     <td class=\"edit-disabled\" id='ChargeStatus_" + count + "'>" + rset2.getString(5) + "</td>\n" + //Status
                        "     <td style='display:none' >" + rset2.getString(9) + "</td>\n" +
                        "     <td class=\"edit-disabled\" >" + rset2.getString(6) + "</td>\n" +//OtherCred
                        "     <td class=\"edit-disabled\" >" + rset2.getString(7) + "</td>\n" +//Adjusted
                        "     <td class=\"edit-disabled\" id='EndBal_" + count + "'>" + rset2.getString(8) + "</td>\n" +//End Balance
                        "     <td style='display:none'>" + rset2.getString(10) + "</td>\n" +
                        "     <td style='display:none'>" + rset2.getString(11) + "</td>\n" +
                        "     <td  id='FixedStatus_" + count + "'style='display:none'>" + rset2.getString(9) + "</td>\n" +
                        " </tr>");
                count++;

                _ChargeOption = rset2.getString(5);

            }
            rset2.close();
            ps2.close();

        }
        rset1.close();
        ps1.close();


        ps1 = conn.prepareStatement("Select Id, IFNULL(UPPER(ChargeOption),'') from oe.ChargeOption where status = 1");
        rset1 = ps1.executeQuery();
        while (rset1.next()) {
            if (_ChargeOption.equals(rset1.getString(2)))
                ChargeOption.append("<option class=Inner value=\"" + rset1.getString(1) + "\" selected>" + rset1.getString(2) + "</option>");
            else
                ChargeOption.append("<option class=Inner value=\"" + rset1.getString(1) + "\">" + rset1.getString(2) + "</option>");
        }
        rset1.close();
        ps1.close();


        Parsehtm Parser = new Parsehtm(request);
        Parser.SetField("ClaimList", String.valueOf(ClaimList));
        Parser.SetField("ChargeOption", String.valueOf(ChargeOption));
        Parser.SetField("PatientName", PatientName);
        Parser.SetField("Source", paymentSource);
        Parser.SetField("receivingDate", ReceivedDate);
        Parser.SetField("checkNumber", checkNumber);
        Parser.SetField("paymentType", paymentType);
        Parser.SetField("memo", memo);
        Parser.SetField("CopayDOS", CopayDOS);
        Parser.SetField("CardType", CardType);
        Parser.SetField("PatRegIdx", PatRegIdx);
        Parser.SetField("tableCount", String.valueOf(count));
        Parser.SetField("PaymentAmount", String.valueOf(String.format("%,.2f", PaymentAmount)));

        Parser.SetField("StartBal", "0.00");
        Parser.SetField("EndBal", "0.00");
        Parser.SetField("StartCred", "0.00");
        Parser.SetField("EndCredit", String.valueOf(String.format("%,.2f", PaymentAmount)));//Payment Amount
        Parser.SetField("AppliedAmt", "0.00");
        Parser.SetField("UnAppliedAmt", String.valueOf(String.format("%,.2f", PaymentAmount)));//Payment Amount

        Parser.SetField("Flag", "1");
        Parser.SetField("cancelLink", "md.ClaimPost?ActionID=getClaimPostInput");

        Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_PatientPayment.html");
    }

    private void getClaims_WRT_Check(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String checkNumber = request.getParameter("checkNumber").trim();
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rset3 = null;
        ResultSet rset2 = null;
        ResultSet rset = null;
        StringBuilder ClaimList = new StringBuilder();


        String receivingDate = "";
        String InsuranceName = "";
        double CheckAmt = 0.0f;
        double AppliedAmt = 0.0f;
        double UnappliedAmt = 0.0f;
        String OtherRefrenceNo = "";
//        String EOB_MasterIdx = "";

        double Total_Billed = 0;
        double Total_Allowed = 0;
        double Total_Paid = 0;
        double Total_Adjusted = 0;
        double Total_Unpaid = 0;
        double Total_AdditionalActions = 0;
        double Total_Balance = 0;
        try {

            ps = conn.prepareStatement("SELECT DATE_FORMAT(ReceivedDate,'%Y-%m-%d') , IFNULL(PaymentAmount,'0.00') , IFNULL(AppliedAmount,'0.00'), IFNULL(UnappliedAmount,IFNULL(PaymentAmount,'0.00')),IFNULL(OtherRefrenceNo,''),Id  from " + database + ".EOB_Master WHERE Id=?");
            ps.setString(1, EOB_MasterIdx);
//            ps.setString(1, checkNumber);
            rset = ps.executeQuery();
            if (rset.next()) {
                receivingDate = rset.getString(1);
                CheckAmt = rset.getDouble(2);
                AppliedAmt = rset.getDouble(3);
                UnappliedAmt = rset.getDouble(4);
                OtherRefrenceNo = rset.getString(5);
                EOB_MasterIdx = rset.getString(6);
            }
            rset.close();
            ps.close();


            //system.out.println("CheckAmt ->> " + CheckAmt);

            ps = conn.prepareStatement("SELECT DISTINCT(ClaimNumber) from " + database + ".Claim_Ledger_Charges_entries where TransactionIdx=?");
            ps.setString(1, EOB_MasterIdx);
//            ps.setString(1, checkNumber);
            rset = ps.executeQuery();
            while (rset.next()) {
                ps2 = conn.prepareStatement("SELECT IFNULL(a.PatientName,'') ,IFNULL(a.AcctNo,'') , IFNULL(a.PCN,''), IFNULL(a.DOS ,'') ,IFNULL(a.ClaimNumber,''),FORMAT(IFNULL(a.TotalCharges,'0.00'),2) FROM  " + database + ".ClaimInfoMaster a" +
                        " LEFT JOIN oe_2.ProfessionalPayers f on a.PriInsuranceNameId = f.Id " +
                        " WHERE a.ClaimNumber=?");
                ps2.setString(1, rset.getString(1));
                rset2 = ps2.executeQuery();
                if (rset2.next()) {
                    ClaimList.append("<tr onclick=openWindow(\"/md/md.ViewClaimPayment?ActionID=getCharges_WRT_Claims&claimNumber=" + rset2.getString(5) + "&checkNumber=" + checkNumber + "&EOB_MasterIdx=" + EOB_MasterIdx + "&UnappliedAmt=" + UnappliedAmt + "&AppliedAmt=" + AppliedAmt + "&CheckAmt=" + CheckAmt + "&receivingDate=" + receivingDate + "&OtherRefrenceNo=" + OtherRefrenceNo + "&InsuranceIdx=" + InsuranceIdx + "&TransactionType=D&Flag=0\") >\n" +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td>" + rset2.getString(2) + "</td>\n" +
                            "<td>" + rset2.getString(5) + "</td>\n" +
                            "<td>" + rset2.getString(3) + "</td>\n" +
                            "<td>" + rset2.getString(4) + "</td>\n");

                    ps3 = conn.prepareStatement("SELECT FORMAT(SUM(IFNULL(b.Allowed, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Paid, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Adjusted, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Unpaid, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.OtherCredits, '0')),2)," +
                            " FORMAT(SUM(b.EndBalance),2)\n" +
                            " from " + database + ".Claim_Ledger_Charges_entries b" +
                            " WHERE b.TransactionIdx = ? and b.ClaimNumber=?");
                    ps3.setString(1, EOB_MasterIdx);
//                    ps3.setString(1, checkNumber);
                    ps3.setString(2, rset.getString(1));
                    rset3 = ps3.executeQuery();
                    if (rset3.next()) {
                        ClaimList.append("<td>$" + rset2.getString(6) + "</td>\n" +
                                "<td>$" + rset3.getString(1) + "</td>\n" +
                                "<td>$" + rset3.getString(2) + "</td>\n" +
                                "<td>$" + rset3.getString(3) + "</td>\n" +
                                "<td>$" + rset3.getString(4) + "</td>\n" +
                                "<td>$" + rset3.getString(5) + "</td>\n" +
                                "<td>$" + rset3.getString(6) + "</td>\n" +
                                "</tr>");


                        Total_Billed += Double.parseDouble(rset2.getString(6).replace(",", ""));
                        Total_Allowed += Double.parseDouble(rset3.getString(1).replace(",", ""));
                        Total_Paid += Double.parseDouble(rset3.getString(2).replace(",", ""));
                        Total_Adjusted += Double.parseDouble(rset3.getString(3).replace(",", ""));
                        Total_Unpaid += Double.parseDouble(rset3.getString(4).replace(",", ""));
                        Total_AdditionalActions += Double.parseDouble(rset3.getString(5).replace(",", ""));
                        Total_Balance += Double.parseDouble(rset3.getString(6).replace(",", ""));
                    }
                    rset3.close();
                    ps3.close();

                }
                rset2.close();
                ps2.close();
            }
            rset.close();
            ps.close();


            ClaimList.append("<tr  style='font-weight: 900;'>\n" +
                    "<td>Total</td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td>$" + Total_Billed + "</td>\n" +
                    "<td>$" + Total_Allowed + "</td>\n" +
                    "<td>$" + Total_Paid + "</td>\n" +
                    "<td>$" + Total_Adjusted + "</td>\n" +
                    "<td>$" + Total_Unpaid + "</td>\n" +
                    "<td>$" + Total_AdditionalActions + "</td>\n" +
                    "<td>$" + Total_Balance + "</td>\n" +
                    "</tr>");

            AppliedAmt = Total_Paid;
            UnappliedAmt = CheckAmt - Total_Paid;

            ps = conn.prepareStatement("SELECT PayerName FROM " + database + ".ProfessionalPayers WHERE Id=?");
            ps.setString(1, InsuranceIdx);
            rset = ps.executeQuery();
            if (rset.next()) {
                InsuranceName = rset.getString(1);
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("UPDATE " + database + ".EOB_Master" +
                    " SET ViewDate = NOW()" +
                    " where id=?");
            ps.setString(1, EOB_MasterIdx);
            ps.executeUpdate();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClaimList", String.valueOf(ClaimList));
            Parser.SetField("receivingDate", receivingDate);
            Parser.SetField("checkNumber", checkNumber);
            Parser.SetField("EOB_MasterIdx", EOB_MasterIdx);
            Parser.SetField("CheckAmt", String.valueOf(String.format("%,.2f", CheckAmt)));
            Parser.SetField("AppliedAmt", String.valueOf(String.format("%,.2f", AppliedAmt)));
            Parser.SetField("UnappliedAmt", String.valueOf(String.format("%,.2f", UnappliedAmt)));
            Parser.SetField("InsuranceName", InsuranceName);
            Parser.SetField("InsuranceIdx", InsuranceIdx);
            Parser.SetField("cancelLink", "md.ViewClaimPayment?ActionID=getViewClaimPaymentInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check.html");

        } catch (Exception Ex) {

            //system.out.println("Error in : " + Ex.getMessage());
            String str = "";
            for (int i = 0; i < Ex.getStackTrace().length; ++i) {
                str = str + Ex.getStackTrace()[i] + "<br>";
            }
            //system.out.println(str);

            helper.SendEmailWithAttachment("Error in getClaims_WRT_Check ** (handleRequest)", servletContext, Ex, "getClaims_WRT_Check", "handleRequest", conn);
            Services.DumException("getClaims_WRT_Check", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    private void getClaims_WRT_Check_inside(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String checkNumber = request.getParameter("checkNumber");
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx") == null ? null : request.getParameter("EOB_MasterIdx");
//        String claimNumber = request.getParameter("claimNumber");
        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();


        //system.out.println("getClaims_WRT_Check_inside");
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rset3 = null;
        ResultSet rset2 = null;
        ResultSet rset = null;
        StringBuilder ClaimList = new StringBuilder();


        String RenderingProviderName = "";
//        String receivingDate = "";
        String InsuranceName = "";
        double CheckAmt = Double.parseDouble(request.getParameter("CheckAmt").trim().replace(",", ""));
        double AppliedAmt = Double.parseDouble(request.getParameter("AppliedAmt").trim().replace(",", ""));
        double UnappliedAmt = Double.parseDouble(request.getParameter("UnappliedAmt").trim().replace(",", ""));
        String receivingDate = request.getParameter("receivingDate").trim();
        String _ChargeOption = "";

        double Total_Billed = 0;
        double Total_Allowed = 0;
        double Total_Paid = 0;
        double Total_Adjusted = 0;
        double Total_Unpaid = 0;
        double Total_AdditionalActions = 0;
        double Total_Balance = 0;
        try {
//            ps = conn.prepareStatement("SELECT ClaimNumber from " + database + ".Claim_Payments_View WHERE CheckNumber=?");
//            ps.setString(1, checkNumber);
//            rset = ps.executeQuery();
//            while (rset.next()) {
            ps = conn.prepareStatement("SELECT DISTINCT(ClaimNumber) from " + database + ".Claim_Ledger_Charges_entries_TEMP where TransactionIdx=?");
            ps.setString(1, checkNumber);
            rset = ps.executeQuery();
            while (rset.next()) {
                ps2 = conn.prepareStatement("SELECT IFNULL(a.PatientName,'') ,IFNULL(a.AcctNo,'') , IFNULL(a.PCN,''), IFNULL(a.DOS ,'') ,IFNULL(a.ClaimNumber,''),FORMAT(IFNULL(a.TotalCharges,'0.00'),2) FROM  " + database + ".ClaimInfoMaster a" +
                        " LEFT JOIN oe_2.ProfessionalPayers f on a.PriInsuranceNameId = f.Id " +
                        " WHERE a.ClaimNumber=?");
                ps2.setString(1, rset.getString(1));
                rset2 = ps2.executeQuery();
                if (rset2.next()) {
                    ClaimList.append("<tr onclick=openWindow(\"/md/md.ViewClaimPayment?ActionID=getCharges_WRT_Claims&claimNumber=" + rset2.getString(5) + "&checkNumber=" + checkNumber + "&EOB_MasterIdx=" + EOB_MasterIdx + "&UnappliedAmt=" + UnappliedAmt + "&AppliedAmt=" + AppliedAmt + "&CheckAmt=" + CheckAmt + "&receivingDate=" + receivingDate + "&OtherRefrenceNo=" + OtherRefrenceNo + "&InsuranceIdx=" + InsuranceIdx + "\") >\n" +
                            "<td>" + rset2.getString(1) + "</td>\n" +
                            "<td>" + rset2.getString(2) + "</td>\n" +
                            "<td>" + rset2.getString(5) + "</td>\n" +
                            "<td>" + rset2.getString(3) + "</td>\n" +
                            "<td>" + rset2.getString(4) + "</td>\n");

                    ps3 = conn.prepareStatement("SELECT FORMAT(SUM(IFNULL(b.Allowed, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Paid, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Adjusted, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.Unpaid, '0')),2), " +
                            " FORMAT(SUM(IFNULL(b.OtherCredits, '0')),2)," +
                            " FORMAT(SUM(b.EndBalance),2)\n" +
                            " from " + database + ".Claim_Ledger_Charges_entries_TEMP b" +
                            " WHERE b.TransactionIdx = ? and b.ClaimNumber=?");
                    ps3.setString(1, checkNumber);
                    ps3.setString(2, rset.getString(1));
                    rset3 = ps3.executeQuery();
                    if (rset3.next()) {
                        ClaimList.append("<td>$" + rset2.getString(6) + "</td>\n" +
                                "<td>$" + rset3.getString(1) + "</td>\n" +
                                "<td>$" + rset3.getString(2) + "</td>\n" +
                                "<td>$" + rset3.getString(3) + "</td>\n" +
                                "<td>$" + rset3.getString(4) + "</td>\n" +
                                "<td>$" + rset3.getString(5) + "</td>\n" +
                                "<td>$" + rset3.getString(6) + "</td>\n" +
                                "</tr>");


                        Total_Billed += Double.parseDouble(rset2.getString(6).replace(",", ""));
                        Total_Allowed += Double.parseDouble(rset3.getString(1).replace(",", ""));
                        Total_Paid += Double.parseDouble(rset3.getString(2).replace(",", ""));
                        Total_Adjusted += Double.parseDouble(rset3.getString(3).replace(",", ""));
                        Total_Unpaid += Double.parseDouble(rset3.getString(4).replace(",", ""));
                        Total_AdditionalActions += Double.parseDouble(rset3.getString(5).replace(",", ""));
                        Total_Balance += Double.parseDouble(rset3.getString(6).replace(",", ""));
                    }
                    rset3.close();
                    ps3.close();

                }
                rset2.close();
                ps2.close();
            }
            rset.close();
            ps.close();
//            ps2 = conn.prepareStatement("SELECT IFNULL(a.PatientName,'') ,IFNULL(a.AcctNo,'') , IFNULL(a.PCN,''), IFNULL(a.DOS ,'')  IFNULL(LTRIM(rtrim(REPLACE(f.PayerName,'Servicing States','') )),''),IFNULL(b.ClaimNumber,'') FROM  " + database + ".ClaimInfoMaster a" +
//                    " LEFT JOIN oe_2.ProfessionalPayers f on a.PriInsuranceNameId = f.Id " +
//                    " WHERE a.ClaimNumber=?");
//            ps2.setString(1, checkNumber);
//
//
//            //system.out.println("Query -> "+ps2.toString());

//            }
//            rset.close();
//            ps.close();

            ClaimList.append("<tr onclick='open();' style='font-weight: 900;'>\n" +
                    "<td>Total</td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td></td>\n" +
                    "<td>$" + Total_Billed + "</td>\n" +
                    "<td>$" + Total_Allowed + "</td>\n" +
                    "<td>$" + Total_Paid + "</td>\n" +
                    "<td>$" + Total_Adjusted + "</td>\n" +
                    "<td>$" + Total_Unpaid + "</td>\n" +
                    "<td>$" + Total_AdditionalActions + "</td>\n" +
                    "<td>$" + Total_Balance + "</td>\n" +
                    "</tr>");

            AppliedAmt = Total_Paid;
            UnappliedAmt = CheckAmt - Total_Paid;

            ps = conn.prepareStatement("SELECT PayerName FROM " + database + ".ProfessionalPayers WHERE Id=?");
            ps.setString(1, InsuranceIdx);
            rset = ps.executeQuery();
            if (rset.next()) {
                InsuranceName = rset.getString(1);
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClaimList", String.valueOf(ClaimList));
            Parser.SetField("receivingDate", receivingDate);
            Parser.SetField("checkNumber", checkNumber);
            Parser.SetField("CheckAmt", String.valueOf(String.format("%,.2f", CheckAmt)));
            Parser.SetField("AppliedAmt", String.valueOf(String.format("%,.2f", AppliedAmt)));
            Parser.SetField("UnappliedAmt", String.valueOf(String.format("%,.2f", UnappliedAmt)));
            Parser.SetField("InsuranceName", InsuranceName);
            Parser.SetField("cancelLink", "md.ClaimPost?ActionID=getClaimPostInput");

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Claims_WRT_Check.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getClaims_WRT_Check ** (handleRequest)", servletContext, Ex, "getClaims_WRT_Check", "handleRequest", conn);
            Services.DumException("getClaims_WRT_Check", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }


    private void getCharges_WRT_Claims(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {

        //system.out.println("getCharges_WRT_Claims**********");

        String claimNumber = request.getParameter("claimNumber").trim();
        String checkNumber = request.getParameter("checkNumber").trim();
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx") == null ? null : request.getParameter("EOB_MasterIdx").trim();
        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();
        String TransactionType = request.getParameter("TransactionType") == null ? "Cr" : "D";
        String Flag = request.getParameter("Flag") == null ? "1" : "0";
//        String EOB_MasterIdx = request.getParameter("Idx").trim();
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rset2 = null;
        ResultSet rset = null;

        String Query = "";
        Statement stmt = null;


        StringBuilder ClaimList = new StringBuilder();
        double Total_Amount = 0.0f;
        double Total_StartBalance = 0.0f;
        double Total_Allowed = 0.0f;
        double Total_Paid = 0.0f;
        double Total_Adjusted = 0.0f;
        double Total_SequestrationAmt = 0.0f;
        double Total_Unpaid = 0.0f;
        double Total_Deductible = 0.0f;
        double Total_OtherCredits = 0.0f;
        double Total_EndBalance = 0.0f;

        String RenderingProviderName = "";
        String ClaimType = "";
        String receivingDate = request.getParameter("receivingDate").trim();
        String InsuranceName = "";
        double CheckAmt = Double.parseDouble(request.getParameter("CheckAmt").trim().replace(",", ""));
        double AppliedAmt = Double.parseDouble(request.getParameter("AppliedAmt").trim().replace(",", ""));
        //system.out.println(request.getParameter("UnappliedAmt").trim().replace(",", ""));
        //system.out.println(request.getParameter("UnappliedAmt").trim());
        double UnappliedAmt = Double.parseDouble(request.getParameter("UnappliedAmt").trim().replace(",", ""));
        String _ChargeOption = "";
        String _AdjReasons = "";
        String _UnpaidReasons = "";
        String filter = "";

        int count = 0;

        StringBuilder ChargeOption = new StringBuilder();
        StringBuilder AdjReasons = new StringBuilder();
        StringBuilder UnpaidReasons = new StringBuilder();


        StringBuffer AlertList = new StringBuffer();
        int Alertscount = 0;

        String TotalCharges = "";
        String Adjustments = "";
        String Paid = "";
        String Balance = "";

        String _CreatedDate = "";
        String DOS = "";

        String PatientName = "";
        String DOB = "";
        String AcctNo = "";


        if (Flag.equals("0")) {
            filter = " AND TransactionIdx=?";
        }

        try {
            ps = conn.prepareStatement("SELECT IFNULL(DATE_FORMAT(a.DOS,'%m-%d-%Y') ,'') , IFNULL(b.Charges,''), IFNULL(b.Amount,'0.00') " +
                    " , IFNULL(b.StartBalance,IFNULL(b.Amount,'0.00')), IFNULL(b.Allowed,'0.00'), IFNULL(b.Paid,'0.00'), IFNULL(b.Remarks,''), IFNULL(b.AdjReasons,''), IFNULL(b.Adjusted,'0.00') " +
                    " , IFNULL(b.UnpaidReasons,''), IFNULL(b.Unpaid,'0.00'), IFNULL(b.Deductible,'0.00'), IFNULL(e.ChargeOption,''), IFNULL(b.OtherCredits,'0.00'), IFNULL(b.EndBalance,IFNULL(b.Amount,'0.00'))" +
                    " , IFNULL(LTRIM(rtrim(REPLACE(f.PayerName,'Servicing States','') )),'') , CONCAT(IFNULL(g.DoctorsLastName,''),', ', IFNULL(g.DoctorsFirstName,'')), IFNULL(e.Id,'4'), IFNULL(b.ChargeIdx,''), IFNULL(b.ClaimIdx,''),IFNULL(ClaimType,''),IFNULL(b.SequestrationAmt,'0.00') " +
                    " from " + database + ".ClaimInfoMaster a " +
                    " INNER JOIN " + database + ".Claim_Ledger_Charges_entries b ON a.ClaimNumber=b.ClaimNumber " +
//                    " LEFT JOIN " + database + ".EOB_Detail c ON a.ClaimNumber=c.ClaimNumber " +
//                    " LEFT JOIN oe.ChargeOption d on b.ChargesStatus = d.Id " +
                    " LEFT JOIN oe.ChargeOption e on b.Status = e.Id " +
                    " LEFT JOIN oe_2.ProfessionalPayers f on a.PriInsuranceNameId = f.Id " +
                    " LEFT JOIN " + database + ".DoctorsList g on a.RenderingProvider = g.Id " +
                    " WHERE a.ClaimNumber=? and b.TransactionType=? and b.Deleted is NULL " + filter);
            ps.setString(1, claimNumber);
            ps.setString(2, TransactionType);
            if (Flag.equals("0")) {
//                ps.setString(3, checkNumber);
                ps.setString(3, EOB_MasterIdx);
            }


            //system.out.println("QUERY - >>> " + ps.toString());
            rset = ps.executeQuery();
            while (rset.next()) {
                ClaimList.append("<tr >\n" +
                        "<td class=\"edit-disabled\" id='DOS_" + count + "'>" + rset.getString(1) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='Proc_" + count + "'>" + rset.getString(2) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='Amt_" + count + "'>" + rset.getString(3) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='StartBal_" + count + "'>" + rset.getString(4) + "</td>\n" +
                        "<td id='Allowed_" + count + "'>" + rset.getString(5) + "</td>\n" +
                        "<td id='Paid_" + count + "'>" + rset.getString(6) + "</td>\n" +
                        "<td id='Remarks_" + count + "'>" + rset.getString(7) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='Adjreasons_" + count + "'>" + rset.getString(8) + "</td>\n" +
                        "<td id='Adjusted_" + count + "'>" + rset.getString(9) + "</td>\n" +
                        "<td id='SequestrationAmt_" + count + "'>" + rset.getString(22) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='Unpaidreasons_" + count + "'>" + rset.getString(10) + "</td>\n" +
                        "<td id='Unpaid_" + count + "'>" + rset.getString(11) + "</td>\n" +
                        "<td id='Deductible_" + count + "'>" + rset.getString(12) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='ChargeStatus_" + count + "'>" + rset.getString(13) + "</td>\n" +
                        "<td id='ChargeStatusIdx_" + count + "'style='display:none;'>" + rset.getString(18) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='OtherCred_" + count + "'>" + rset.getString(14) + "</td>\n" +
                        "<td class=\"edit-disabled\" id='EndBal_" + count + "'>" + rset.getString(15) + "</td>\n" +
                        "<td id='ChargeIdx_" + count + "' style='display:none;'>" + rset.getString(19) + "</td>\n" +
                        "<td id='ClaimIdx_" + count + "' style='display:none;'>" + rset.getString(20) + "</td>\n" +
                        "</tr>");

                Total_Amount += Double.parseDouble(rset.getString(3));
                Total_StartBalance += Double.parseDouble(rset.getString(4));
                Total_Allowed += Double.parseDouble(rset.getString(5));
                Total_Paid += Double.parseDouble(rset.getString(6));
                Total_Adjusted += Double.parseDouble(rset.getString(9));
                Total_SequestrationAmt += Double.parseDouble(rset.getString(22));
                Total_Unpaid += Double.parseDouble(rset.getString(11));
                Total_Deductible += Double.parseDouble(rset.getString(12));
                Total_Deductible += Double.parseDouble(rset.getString(14));
                Total_EndBalance += Double.parseDouble(rset.getString(15));


//                InsuranceName = rset.getString(16);
                RenderingProviderName = rset.getString(17);
                _ChargeOption = rset.getString(13);
                count++;

                ClaimType = rset.getString(21);
            }
            rset.close();
            ps.close();


            if (ClaimList.length() > 0 && Flag.equals("0")) {
                ps = conn.prepareStatement("SELECT IFNULL(DATE_FORMAT(a.DOS,'%m-%d-%Y') ,'') , IFNULL(b.Charges,''), IFNULL(b.Amount,'0.00') " +
                        " , IFNULL(b.StartBalance,IFNULL(b.Amount,'0.00')), IFNULL(b.Allowed,'0.00'), IFNULL(b.Paid,'0.00'), IFNULL(b.Remarks,''), IFNULL(b.AdjReasons,''), IFNULL(b.Adjusted,'0.00') " +
                        " , IFNULL(b.UnpaidReasons,''), IFNULL(b.Unpaid,'0.00'), IFNULL(b.Deductible,'0.00'), IFNULL(e.ChargeOption,''), IFNULL(b.OtherCredits,'0.00'), IFNULL(b.EndBalance,IFNULL(b.Amount,'0.00'))" +
                        " , IFNULL(LTRIM(rtrim(REPLACE(f.PayerName,'Servicing States','') )),'') , CONCAT(IFNULL(g.DoctorsLastName,''),', ', IFNULL(g.DoctorsFirstName,'')), IFNULL(e.Id,'4'), IFNULL(b.ChargeIdx,''), IFNULL(b.ClaimIdx,''),IFNULL(ClaimType,''),IFNULL(b.SequestrationAmt,'0.00') " +
                        " from " + database + ".ClaimInfoMaster a " +
                        " INNER JOIN " + database + ".Claim_Ledger_Charges_entries b ON a.ClaimNumber=b.ClaimNumber " +
                        //                    " LEFT JOIN " + database + ".EOB_Detail c ON a.ClaimNumber=c.ClaimNumber " +
                        //                    " LEFT JOIN oe.ChargeOption d on b.ChargesStatus = d.Id " +
                        " LEFT JOIN oe.ChargeOption e on b.Status = e.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers f on a.PriInsuranceNameId = f.Id " +
                        " LEFT JOIN " + database + ".DoctorsList g on a.RenderingProvider = g.Id " +
                        " WHERE a.ClaimNumber=?  and b.Deleted is NULL AND b.TransactionType='Cr'  AND " +
                        "b.ChargeIdx NOT IN ( SELECT b.ChargeIdx FROM  " + database + ".Claim_Ledger_Charges_entries b WHERE b.TransactionIdx=?)");
                ps.setString(1, claimNumber);
                ps.setString(2, EOB_MasterIdx);


                //system.out.println("QUERY - >>> " + ps.toString());
                rset = ps.executeQuery();
                while (rset.next()) {
                    ClaimList.append("<tr >\n" +
                            "<td class=\"edit-disabled\" id='DOS_" + count + "'>" + rset.getString(1) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='Proc_" + count + "'>" + rset.getString(2) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='Amt_" + count + "'>" + rset.getString(3) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='StartBal_" + count + "'>" + rset.getString(4) + "</td>\n" +
                            "<td id='Allowed_" + count + "'>" + rset.getString(5) + "</td>\n" +
                            "<td id='Paid_" + count + "'>" + rset.getString(6) + "</td>\n" +
                            "<td id='Remarks_" + count + "'>" + rset.getString(7) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='Adjreasons_" + count + "'>" + rset.getString(8) + "</td>\n" +
                            "<td id='Adjusted_" + count + "'>" + rset.getString(9) + "</td>\n" +
                            "<td id='SequestrationAmt_" + count + "'>" + rset.getString(22) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='Unpaidreasons_" + count + "'>" + rset.getString(10) + "</td>\n" +
                            "<td id='Unpaid_" + count + "'>" + rset.getString(11) + "</td>\n" +
                            "<td id='Deductible_" + count + "'>" + rset.getString(12) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='ChargeStatus_" + count + "'>" + rset.getString(13) + "</td>\n" +
                            "<td id='ChargeStatusIdx_" + count + "'style='display:none;'>" + rset.getString(18) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='OtherCred_" + count + "'>" + rset.getString(14) + "</td>\n" +
                            "<td class=\"edit-disabled\" id='EndBal_" + count + "'>" + rset.getString(15) + "</td>\n" +
                            "<td id='ChargeIdx_" + count + "' style='display:none;'>" + rset.getString(19) + "</td>\n" +
                            "<td id='ClaimIdx_" + count + "' style='display:none;'>" + rset.getString(20) + "</td>\n" +
                            "</tr>");

                    Total_Amount += Double.parseDouble(rset.getString(3));
                    Total_StartBalance += Double.parseDouble(rset.getString(4));
                    Total_Allowed += Double.parseDouble(rset.getString(5));
                    Total_Paid += Double.parseDouble(rset.getString(6));
                    Total_Adjusted += Double.parseDouble(rset.getString(9));
                    Total_SequestrationAmt += Double.parseDouble(rset.getString(22));
                    Total_Unpaid += Double.parseDouble(rset.getString(11));
                    Total_Deductible += Double.parseDouble(rset.getString(12));
                    Total_Deductible += Double.parseDouble(rset.getString(14));
                    Total_EndBalance += Double.parseDouble(rset.getString(15));


                    //                InsuranceName = rset.getString(16);
                    RenderingProviderName = rset.getString(17);
                    _ChargeOption = rset.getString(13);
                    count++;

                    ClaimType = rset.getString(21);
                }
                rset.close();
                ps.close();
            }


            ClaimList.append("<tr  style='font-weight: 900;'>\n" +
                    "<td class=\"edit-disabled\" >Total</td>\n" +
                    "<td class=\"edit-disabled\" ></td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Amt_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Amount)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_StartBalance_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_StartBalance)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Allowed_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Allowed)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Paid_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Paid)) + "</td>\n" +
                    "<td class=\"edit-disabled\" ></td>\n" +
                    "<td class=\"edit-disabled\" ></td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Adjusted_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Adjusted)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_SequestrationAmt_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_SequestrationAmt)) + "</td>\n" +
                    "<td class=\"edit-disabled\" ></td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Unpaid_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Unpaid)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_Deductible_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_Deductible)) + "</td>\n" +
                    "<td class=\"edit-disabled\" ></td>\n" +
                    "<td class=\"edit-disabled\" id='Total_OtherCredits_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_OtherCredits)) + "</td>\n" +
                    "<td class=\"edit-disabled\" id='Total_EndBalance_" + count + "'>$" + String.valueOf(String.format("%,.2f", Total_EndBalance)) + "</td>\n" +
                    "</tr>");


//            ps = conn.prepareStatement("SELECT DATE_FORMAT(ReceivedDate,'%m/%d/%Y') , IFNULL(PaymentAmount,'0.00') , IFNULL(AppliedAmount,'0.00'), IFNULL(UnappliedAmount,IFNULL(PaymentAmount,'0.00'))  from " + database + ".EOB_Master WHERE CheckNumber=?");
//            ps.setString(1, checkNumber);
//            rset = ps.executeQuery();
//            if (rset.next()) {
//                receivingDate = rset.getString(1);
//                CheckAmt = rset.getDouble(2);
//                AppliedAmt = rset.getDouble(3);
//                UnappliedAmt = rset.getDouble(4);
//            }
//            rset.close();
//            ps.close();


            ps = conn.prepareStatement("Select Id, IFNULL(UPPER(ChargeOption),'') from oe.ChargeOption where status = 1");
            rset = ps.executeQuery();
            while (rset.next()) {
                ChargeOption.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            ps.close();


            ps = conn.prepareStatement("Select Id, IFNULL(UPPER(AdjOption),'') from ClaimMasterDB.AdjReasons where status = 1");
            rset = ps.executeQuery();
            while (rset.next()) {
                AdjReasons.append("<option class=Inner value=\"" + rset.getString(2) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            ps.close();

            ps = conn.prepareStatement("Select Id, IFNULL(UPPER(UnpaidOption),'') from ClaimMasterDB.UnpaidReasons where status = 1");
            rset = ps.executeQuery();
            while (rset.next()) {
                if (_UnpaidReasons.equals(rset.getString(2)))
                    UnpaidReasons.append("<option class=Inner value=\"" + rset.getString(2) + "\" selected>" + rset.getString(2) + "</option>");
                else
                    UnpaidReasons.append("<option class=Inner value=\"" + rset.getString(2) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            ps.close();

            ps = conn.prepareStatement("SELECT PayerName FROM " + database + ".ProfessionalPayers WHERE Id=?");
            ps.setString(1, InsuranceIdx);
            rset = ps.executeQuery();
            if (rset.next()) {
                InsuranceName = rset.getString(1);
            }
            rset.close();
            ps.close();


            Query = " Select IFNULL(ClaimNumber,''), IFNULL(Alerts,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),''), IFNULL(CreatedBy,''), Id  " +
                    " from " + database + ".Claim_Alerts where ClaimNumber = '" + claimNumber + "' and Status = 0 order by Id desc ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            while (rset.next()) {
                AlertList.append("<div class=\"box\" >\n" +
                        "\t\t\t\t  <div class=\"box-header\" style='height: 50%;'>\n" +
                        "\t\t\t\t\t<h5 class=\"box-title\" style='margin-right: 10%;'>" + rset.getString(2) + "" +
                        "<br><sub>Added by <u>" + rset.getString(4) + "</u> at <u>" + rset.getString(3) + "</u></sub></h5>\n" +
                        "\t\t\t\t\t<div class=\"box-controls pull-right\">\n" +
                        "\t\t\t\t\t  <button class=\"btn btn-xs btn-info\" onclick=\"DeleteAlert(" + rset.getInt(5) + ")\"><i class=\"fa fa-trash\"></i></button>\n" +
                        "\t\t\t\t\t</div>                \n" +
                        "\t\t\t\t  </div>\t\t\t\t  \n" +
                        "\t\t\t\t</div>");
                Alertscount++;
            }
            rset.close();
            stmt.close();


            ps = conn.prepareStatement("SELECT FORMAT(IFNULL(TotalCharges,'0.00'),2),IFNULL(FORMAT(Balance,2),FORMAT(IFNULL(TotalCharges,'0.00'),2))" +
                    " ,FORMAT(IFNULL(Adjusted,'0.00'),2),FORMAT(IFNULL(Paid,'0.00'),2),DATE_FORMAT(CreatedDate,'%m/%d/%Y')," +
                    " IFNULL(DATE_FORMAT(DOS,'%m/%d/%Y'),''),IFNULL(PatientName,''),IFNULL(AcctNo,'') " +
                    " FROM " + database + ".ClaimInfoMaster WHERE ClaimNumber=?");
            ps.setString(1, claimNumber);
            rset = ps.executeQuery();
            if (rset.next()) {
                TotalCharges = rset.getString(1);
                Balance = rset.getString(2);
                Adjustments = rset.getString(3);
                Paid = rset.getString(4);
                _CreatedDate = rset.getString(5);
                DOS = rset.getString(6);
                PatientName = rset.getString(7);
                AcctNo = rset.getString(8);
            }
            rset.close();
            ps.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("ClaimList", String.valueOf(ClaimList));
            Parser.SetField("InsuranceName", InsuranceName);
            Parser.SetField("Rendering", RenderingProviderName);
            Parser.SetField("receivingDate", receivingDate);
            Parser.SetField("ClaimNumber", claimNumber);
            Parser.SetField("checkNumber", checkNumber);
            Parser.SetField("EOB_MasterIdx", EOB_MasterIdx);
            Parser.SetField("CheckAmt", String.valueOf(String.format("%,.2f", CheckAmt)));
            Parser.SetField("AppliedAmt", String.valueOf(String.format("%,.2f", AppliedAmt)));
            //system.out.println(String.valueOf(String.format("%,.2f", UnappliedAmt)));
            Parser.SetField("UnappliedAmt", String.valueOf(String.format("%,.2f", UnappliedAmt)));
//            Parser.SetField("EOB_MasterIdx", EOB_MasterIdx);
            Parser.SetField("ChargeOption", String.valueOf(ChargeOption));
            Parser.SetField("UnpaidReasons", String.valueOf(UnpaidReasons));
            Parser.SetField("AdjReasons", String.valueOf(AdjReasons));
            Parser.SetField("tableCount", String.valueOf(count));
            Parser.SetField("OtherRefrenceNo", String.valueOf(OtherRefrenceNo));
            Parser.SetField("InsuranceIdx", String.valueOf(InsuranceIdx));
            Parser.SetField("Flag", String.valueOf(Flag));

//            System.out.println("ClaimType -> "+ClaimType);
            if (ClaimType.equals("1"))
                Parser.SetField("ActionName", "viewActivity_Ins");
            else
                Parser.SetField("ActionName", "viewActivity");


            Parser.SetField("AlertList", String.valueOf(AlertList));
            Parser.SetField("Alertscount", String.valueOf(Alertscount));


            Parser.SetField("TotalCharges", TotalCharges);
            Parser.SetField("Adjustments", Adjustments);
            Parser.SetField("Paid", Paid);
            Parser.SetField("Balance", Balance);

            Parser.SetField("DOS_CS", DOS);
            Parser.SetField("DateEntered", String.valueOf(_CreatedDate));

            Parser.SetField("AcctNo", AcctNo.toString());
            Parser.SetField("PatientName", PatientName.toString());


            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Claims/view_Charges_WRT_Claim.html");

        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in getClaims_WRT_Check ** (handleRequest)", servletContext, Ex, "getClaims_WRT_Check", "handleRequest", conn);
            Services.DumException("getClaims_WRT_Check", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }


    private void setCharges_WRT_Claims(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String claimNumber = request.getParameter("claimNumber").trim();
        String checkNumber = request.getParameter("checkNumber").trim();
        String ChargesString = request.getParameter("ChargesString").trim();
        String ChargesTableCount = request.getParameter("ChargesTableCount").trim();
        String Action = request.getParameter("Action").trim();
        String TCN = request.getParameter("TCN").trim();
        String Status = request.getParameter("Status").trim();
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx") == null ? null : request.getParameter("EOB_MasterIdx");
        String claimControlNumber = request.getParameter("claimControlNumber").trim();


        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();
        String receivingDate = request.getParameter("receivingDate").trim();
        String flag = request.getParameter("Flag").trim();

        String ClientIP = helper.getClientIp(request);

        int i = 0, j = 0, k = 0;
        String[] myInfoCharges;
        myInfoCharges = new String[0];
        myInfoCharges = ChargesString.split("\\^");
        String ChargesInput[][] = new String[Integer.parseInt(ChargesTableCount)][18];
        i = j = k = 0;
        for (i = 1; i < myInfoCharges.length; i++) {
            if (myInfoCharges[i].length() <= 0)
                continue;


            if (myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1).equals("^"))
                ChargesInput[k][j] = "-";
            else {
                ChargesInput[k][j] = myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1);
                //system.out.println("ChargesInput [" + k + "][" + j + "] -> " + ChargesInput[k][j]);
            }

            j++;
            if (j > 17) {
                j = 0;
                k++;
            }


        }


        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rset2 = null;
        ResultSet rset = null;
        StringBuilder ClaimList = new StringBuilder();
        double Total_Amount = 0.0f;
        double Total_StartBalance = 0.0f;
        double Total_Allowed = 0.0f;
        double Total_Paid = 0.0f;
        double Total_Adjusted = 0.0f;
        double Total_Unpaid = 0.0f;
        double Total_Deductible = 0.0f;
        double Total_OtherCredits = 0.0f;
        double Total_EndBalance = 0.0f;

        String RenderingProviderName = "";
//        String receivingDate = "";
        String InsuranceName = "";
        double CheckAmt = Double.parseDouble(request.getParameter("CheckAmt").trim().replace(",", ""));
        double AppliedAmt = Double.parseDouble(request.getParameter("AppliedAmt").trim().replace(",", ""));
        double UnappliedAmt = Double.parseDouble(request.getParameter("UnappliedAmt").trim().replace(",", ""));
        String _ChargeOption = "";
        int count = 0;

        String ClaimNumber = "";
        int ClaimType = 0;
        String ClaimIdx = "";
        String ChargeIdx = "";
        String Charges = "";
        String Amount = "";
        String StartBalance = "";
        String Allowed = "";
        Double TOTAL_wrt_Claim_Allowed = 0.0;

        String Paid = "";
        Double TOTAL_wrt_Claim_Paid = 0.0;

        String Remarks = "";
        String AdjReasons = "";
        String Adjusted = "";
        Double TOTAL_wrt_Claim_Adjusted = 0.0;


        String SequestrationAmt = "";
        Double TOTAL_wrt_Claim_SequestrationAmt = 0.0;

        String UnpaidReasons = "";
        String Unpaid = "";
        Double TOTAL_wrt_Claim_Unpaid = 0.0;

        String Deductible = "";
//        String Status = "";
        String OtherCredits = "";
        String EndBalance = "";
        Double TOTAL_wrt_Claim_Balance = 0.0;

        String CreatedAt = "";
        String CreatedBy = "";
        String UserIP = "";
        String TransactionIdx = "";
        String TransactionType = "";
        String Payment = "";
        String Adjustment = "";
        String Balance = "";


        String PatientIdx = null;
        String PaymentAmount = null;
        String ReceivedDate = null;
        String CheckNumber = null;
        String PaymentType = null;
        String PaymentSource = null;
        String Memo = null;
        String CopayDOS = null;
        String CardType = null;
        String CreatedDate = null;
        String ModifyBy = null;
        String ModifyDate = null;
        String PaymentFrom = null;
        String isPaymentOnly = null;
        String isCreditAccount = null;
        double AppliedAmount = 0.0f;
        double UnappliedAmount = 0.0f;


        StringBuilder ChargeOption = new StringBuilder();

        try {
            if (flag.equals("1")) {
                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {

                    if (
                            ChargesInput[i][4].replaceAll("\\$", "").compareTo("0.00") != 0 || //Allowed
                                    ChargesInput[i][5].replaceAll("\\$", "").compareTo("0.00") != 0 || //Paid
                                    ChargesInput[i][8].replaceAll("\\$", "").compareTo("0.00") != 0 || //Adjusted
                                    ChargesInput[i][9].replaceAll("\\$", "").compareTo("0.00") != 0 || //SequestrationAmt
                                    ChargesInput[i][11].replaceAll("\\$", "").compareTo("0.00") != 0 || //Unpaid
                                    ChargesInput[i][12].replaceAll("\\$", "").compareTo("0.00") != 0 || //Deductible
                                    ChargesInput[i][14].replaceAll("\\$", "").compareTo("0.00") != 0  //OtherCredits
                    ) {
                        ps = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries_TEMP` \n" +
                                "(`ClaimNumber`, `Charges`, `Amount`, `StartBalance`, " +
                                "`Allowed`, `Paid`, `Remarks`, `AdjReasons`, `Adjusted`, `SequestrationAmt`, `UnpaidReasons`, `Unpaid`, `Deductible`, `Status`," +
                                " `OtherCredits`, `EndBalance`, `CreatedAt`, `CreatedBy`, `UserIP`,`TransactionType`,`Payment`,`Adjustment` ,`ChargeIdx`,`ClaimIdx`, `TransactionIdx`) " +
                                "VALUES (? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,? ,NOW() ,? ,?,'D',?,?,?,?,?)");
                        ps.setString(1, claimNumber);
                        ps.setString(2, ChargesInput[i][1]);//Procedure
                        ps.setString(3, ChargesInput[i][2].replaceAll("\\$", ""));//Amount
                        ps.setString(4, ChargesInput[i][3].replaceAll("\\$", ""));//StartBalance
                        ps.setString(5, ChargesInput[i][4].replaceAll("\\$", ""));//Allowed
                        ps.setString(6, ChargesInput[i][5].replaceAll("\\$", ""));//Paid
                        ps.setString(7, ChargesInput[i][6]);//Remarks
                        ps.setString(8, ChargesInput[i][7]);//AdjReasons
                        ps.setString(9, ChargesInput[i][8].replaceAll("\\$", ""));//Adjusted
                        ps.setString(10, ChargesInput[i][9].replaceAll("\\$", ""));//SequestrationAmt
                        ps.setString(11, ChargesInput[i][10]);//UnpaidReasons
                        ps.setString(12, ChargesInput[i][11].replaceAll("\\$", ""));//Unpaid
                        ps.setString(13, ChargesInput[i][12].replaceAll("\\$", ""));//Deductible
                        ps.setString(14, ChargesInput[i][13]);//Status
                        ps.setString(15, ChargesInput[i][14].replaceAll("\\$", ""));//OtherCredits
                        ps.setString(16, ChargesInput[i][15].replaceAll("\\$", ""));//EndBalance
                        ps.setString(17, userId);
                        ps.setString(18, ClientIP);
                        ps.setString(19, ChargesInput[i][5].replaceAll("\\$", ""));//Payment
                        ps.setString(20, ChargesInput[i][8].replaceAll("\\$", ""));//Adjustment
                        ps.setString(21, ChargesInput[i][16].replaceAll("\\$", ""));//ChargeIdx
                        ps.setString(22, ChargesInput[i][17].replaceAll("\\$", ""));//ClaimIdx
                        ps.setString(23, checkNumber);

                        //system.out.println("QUERY ->> " + ps.toString());
                        ps.executeUpdate();
                        ps.close();
                    }


                }
                out.println("1~md.ViewClaimPayment?ActionID=getClaims_WRT_Check_inside&claimNumber=" + claimNumber + "&checkNumber=" + checkNumber + "&UnappliedAmt=" + String.valueOf(String.format("%,.2f", UnappliedAmt)) + "&AppliedAmt=" + String.valueOf(String.format("%,.2f", AppliedAmt)) + "&CheckAmt=" + String.valueOf(String.format("%,.2f", CheckAmt)) + "&receivingDate=" + receivingDate + "&OtherRefrenceNo=" + OtherRefrenceNo + "&InsuranceIdx=" + InsuranceIdx);
            } else {

                double Existing_Total_Paid = 0.0f;
                ps = conn.prepareStatement("SELECT ClaimNumber,ClaimIdx,ChargeIdx,Charges,Amount,StartBalance " +
                        ",Allowed ,Paid ,Remarks ,AdjReasons ," +
                        "Adjusted ,SequestrationAmt ,UnpaidReasons ,Unpaid ,Deductible ,Status ,OtherCredits ,EndBalance, CreatedAt,CreatedBy,UserIP,TransactionIdx,TransactionType, Payment ,Adjustment ,Balance " +
                        " FROM " + database + ".Claim_Ledger_Charges_entries WHERE ClaimNumber=? AND TransactionIdx=? AND Deleted is NULL");
                ps.setString(1, claimNumber);
                ps.setString(2, EOB_MasterIdx);
                rset = ps.executeQuery();
                while (rset.next()) {
                    ClaimNumber = rset.getString(1);
                    ClaimIdx = rset.getString(2);
                    ChargeIdx = rset.getString(3);
                    Charges = rset.getString(4);
                    Amount = rset.getString(5);
                    StartBalance = rset.getString(6);
                    Allowed = rset.getString(7);
                    Paid = rset.getString(8);
                    Remarks = rset.getString(9);
                    AdjReasons = rset.getString(10);
                    Adjusted = rset.getString(11);
                    SequestrationAmt = rset.getString(12);
                    UnpaidReasons = rset.getString(13);
                    Unpaid = rset.getString(14);
                    Deductible = rset.getString(15);
                    Status = rset.getString(16);
                    OtherCredits = rset.getString(17);
                    EndBalance = rset.getString(18);
                    CreatedAt = rset.getString(19);
                    CreatedBy = rset.getString(20);
                    UserIP = rset.getString(21);
                    TransactionIdx = rset.getString(22);
                    TransactionType = rset.getString(23);
                    Payment = rset.getString(24);
                    Adjustment = rset.getString(25);
                    Balance = rset.getString(26);


                    ps2 = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries_HISTORY` (ClaimNumber, ClaimIdx, ChargeIdx, Charges," +
                            " Amount, StartBalance, Allowed, Paid, Remarks, AdjReasons, Adjusted, SequestrationAmt," +
                            " UnpaidReasons, Unpaid, Deductible, Status, OtherCredits, EndBalance," +
                            " CreatedAt, CreatedBy, UserIP, TransactionIdx,TransactionType,Payment ,Adjustment ,Balance)" +
                            " VALUES ( ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?,? ,?,?,?)");
                    ps2.setString(1, ClaimNumber);
                    ps2.setString(2, ClaimIdx);
                    ps2.setString(3, ChargeIdx);
                    ps2.setString(4, Charges);
                    ps2.setString(5, Amount);
                    ps2.setString(6, StartBalance);
                    ps2.setString(7, Allowed);
                    ps2.setString(8, Paid);
                    ps2.setString(9, Remarks);
                    ps2.setString(10, AdjReasons);
                    ps2.setString(11, Adjusted);
                    ps2.setString(12, SequestrationAmt);
                    ps2.setString(13, UnpaidReasons);
                    ps2.setString(14, Unpaid);
                    ps2.setString(15, Deductible);
                    ps2.setString(16, Status);
                    ps2.setString(17, OtherCredits);
                    ps2.setString(18, EndBalance);
                    ps2.setString(19, CreatedAt);
                    ps2.setString(20, CreatedBy);
                    ps2.setString(21, UserIP);
                    ps2.setString(22, TransactionIdx);
                    ps2.setString(23, TransactionType);
                    ps2.setString(24, Payment);
                    ps2.setString(25, Adjustment);
                    ps2.setString(26, Balance);
                    ps2.executeUpdate();
                    ps2.close();

                    Existing_Total_Paid += Double.parseDouble(Paid);
                }
                ps.close();
                rset.close();


                double NEW_Total_Paid = 0.0f;
                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {

                    if (isDebitTransactionExists(conn,ChargesInput[i][16].replaceAll("\\$", "")/*ChargeIdx*/,EOB_MasterIdx,
                            ChargesInput[i][17].replaceAll("\\$", "")/*ClaimIdx*/,database)) {
                        if (
                                ChargesInput[i][4].replaceAll("\\$", "").compareTo("0.00") != 0 || //Allowed
                                        ChargesInput[i][5].replaceAll("\\$", "").compareTo("0.00") != 0 || //Paid
                                        ChargesInput[i][8].replaceAll("\\$", "").compareTo("0.00") != 0 || //Adjusted
                                        ChargesInput[i][9].replaceAll("\\$", "").compareTo("0.00") != 0 || //SequestrationAmt
                                        ChargesInput[i][11].replaceAll("\\$", "").compareTo("0.00") != 0 || //Unpaid
                                        ChargesInput[i][12].replaceAll("\\$", "").compareTo("0.00") != 0 || //Deductible
                                        ChargesInput[i][14].replaceAll("\\$", "").compareTo("0.00") != 0  //OtherCredits
                        ) {
                            ps = conn.prepareStatement("UPDATE " + database + ".`Claim_Ledger_Charges_entries` SET " +
                                    " `StartBalance`=?," +
                                    " `Allowed`=?," +
                                    " `Paid`=?," +
                                    " `Remarks`=?," +
                                    " `AdjReasons`=?," +
                                    " `Adjusted`=?," +
                                    " `SequestrationAmt`=?," +
                                    " `UnpaidReasons`=?," +
                                    " `Unpaid`=?," +
                                    " `Deductible`=?," +
                                    " `Status`=?," +
                                    " `OtherCredits`=?," +
                                    " `EndBalance`=?," +
                                    " `UpdatedAt`=NOW() ," +
                                    " `UpdatedBy`=?," +
                                    " `UserIP`=?," +
                                    " `Payment`=?," +
                                    " `Adjustment`=? " +
                                    " WHERE ChargeIdx = ? AND TransactionIdx=? AND TransactionType='D' AND ClaimIdx=?");

                            ps.setString(1, ChargesInput[i][3].replaceAll("\\$", ""));//StartBalance
                            ps.setString(2, ChargesInput[i][4].replaceAll("\\$", ""));//Allowed
                            TOTAL_wrt_Claim_Allowed += Double.parseDouble(ChargesInput[i][4].replaceAll("\\$", ""));
                            ps.setString(3, ChargesInput[i][5].replaceAll("\\$", ""));//Paid
                            TOTAL_wrt_Claim_Paid += Double.parseDouble(ChargesInput[i][5].replaceAll("\\$", ""));

                            ps.setString(4, ChargesInput[i][6]);//Remarks
                            ps.setString(5, ChargesInput[i][7]);//AdjReasons
                            ps.setString(6, ChargesInput[i][8].replaceAll("\\$", ""));//Adjusted
                            TOTAL_wrt_Claim_Adjusted += Double.parseDouble(ChargesInput[i][8].replaceAll("\\$", ""));

                            ps.setString(7, ChargesInput[i][9].replaceAll("\\$", ""));//SequestrationAmt
                            TOTAL_wrt_Claim_SequestrationAmt += Double.parseDouble(ChargesInput[i][9].replaceAll("\\$", ""));

                            ps.setString(8, ChargesInput[i][10]);//UnpaidReasons
                            ps.setString(9, ChargesInput[i][11].replaceAll("\\$", ""));//Unpaid
                            TOTAL_wrt_Claim_Unpaid += Double.parseDouble(ChargesInput[i][11].replaceAll("\\$", ""));

                            ps.setString(10, ChargesInput[i][12].replaceAll("\\$", ""));//Deductible
                            ps.setString(11, ChargesInput[i][13]);//Status
                            ps.setString(12, ChargesInput[i][14].replaceAll("\\$", ""));//OtherCredits
                            ps.setString(13, ChargesInput[i][15].replaceAll("\\$", ""));//EndBalance
                            TOTAL_wrt_Claim_Balance += Double.parseDouble(ChargesInput[i][15].replaceAll("\\$", ""));

                            ps.setString(14, userId);
                            ps.setString(15, ClientIP);
                            ps.setString(16, ChargesInput[i][5].replaceAll("\\$", ""));//Payment
                            ps.setString(17, ChargesInput[i][8].replaceAll("\\$", ""));//Adjustment
                            ps.setString(18, ChargesInput[i][16].replaceAll("\\$", ""));//ChargeIdx
                            ps.setString(19, EOB_MasterIdx);
    //                    ps.setString(18, checkNumber);
                            ps.setString(20, ChargesInput[i][17].replaceAll("\\$", ""));//ClaimIdx
                            ps.executeUpdate();
                            ps.close();
                            NEW_Total_Paid += Double.parseDouble(ChargesInput[i][5].replaceAll("\\$", ""));

                            if (ClaimNumber.split("-")[0].equals("CP"))
                                ClaimType = 2;
                            else
                                ClaimType = 1;

                            insertIntoClaim_AuditTrails(conn, "Payment Updated Against "+ChargesInput[i][1] , ClaimNumber, ClaimType, userId, facilityIndex, ClientIP, database, "PAYMENT UPDATED");

                        }
                    }else{
                        if (
                                ChargesInput[i][4].replaceAll("\\$", "").compareTo("0.00") != 0 || //Allowed
                                        ChargesInput[i][5].replaceAll("\\$", "").compareTo("0.00") != 0 || //Paid
                                        ChargesInput[i][8].replaceAll("\\$", "").compareTo("0.00") != 0 || //Adjusted
                                        ChargesInput[i][9].replaceAll("\\$", "").compareTo("0.00") != 0 || //SequestrationAmt
                                        ChargesInput[i][11].replaceAll("\\$", "").compareTo("0.00") != 0 || //Unpaid
                                        ChargesInput[i][12].replaceAll("\\$", "").compareTo("0.00") != 0 || //Deductible
                                        ChargesInput[i][14].replaceAll("\\$", "").compareTo("0.00") != 0  //OtherCredits
                        ) {
                            ps = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries` \n" +
                                    "(`ClaimNumber`, `Charges`, `Amount`, `StartBalance`, " +
                                    "`Allowed`, `Paid`, `Remarks`, `AdjReasons`, `Adjusted`, `SequestrationAmt`, `UnpaidReasons`, `Unpaid`, `Deductible`, `Status`," +
                                    " `OtherCredits`, `EndBalance`, `CreatedAt`, `CreatedBy`, `UserIP`,`TransactionType`,`Payment`,`Adjustment` ,`ChargeIdx`,`ClaimIdx`, `TransactionIdx`) " +
                                    "VALUES (? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,? ,NOW() ,? ,?,'D',?,?,?,?,?)");
                            ps.setString(1, claimNumber);
                            ps.setString(2, ChargesInput[i][1]);//Procedure
                            ps.setString(3, ChargesInput[i][2].replaceAll("\\$", ""));//Amount
                            ps.setString(4, ChargesInput[i][3].replaceAll("\\$", ""));//StartBalance
                            ps.setString(5, ChargesInput[i][4].replaceAll("\\$", ""));//Allowed
                            ps.setString(6, ChargesInput[i][5].replaceAll("\\$", ""));//Paid
                            ps.setString(7, ChargesInput[i][6]);//Remarks
                            ps.setString(8, ChargesInput[i][7]);//AdjReasons
                            ps.setString(9, ChargesInput[i][8].replaceAll("\\$", ""));//Adjusted
                            ps.setString(10, ChargesInput[i][9].replaceAll("\\$", ""));//SequestrationAmt
                            ps.setString(11, ChargesInput[i][10]);//UnpaidReasons
                            ps.setString(12, ChargesInput[i][11].replaceAll("\\$", ""));//Unpaid
                            ps.setString(13, ChargesInput[i][12].replaceAll("\\$", ""));//Deductible
                            ps.setString(14, ChargesInput[i][13]);//Status
                            ps.setString(15, ChargesInput[i][14].replaceAll("\\$", ""));//OtherCredits
                            ps.setString(16, ChargesInput[i][15].replaceAll("\\$", ""));//EndBalance
                            ps.setString(17, userId);
                            ps.setString(18, ClientIP);
                            ps.setString(19, ChargesInput[i][5].replaceAll("\\$", ""));//Payment
                            ps.setString(20, ChargesInput[i][8].replaceAll("\\$", ""));//Adjustment
                            ps.setString(21, ChargesInput[i][16].replaceAll("\\$", ""));//ChargeIdx
                            ps.setString(22, ChargesInput[i][17].replaceAll("\\$", ""));//ClaimIdx
                            ps.setString(23, EOB_MasterIdx);

                            //system.out.println("QUERY ->> " + ps.toString());
                            ps.executeUpdate();
                            ps.close();


                            insertIntoClaim_AuditTrails(conn, "Payment Posted Against "+ChargesInput[i][1] , ClaimNumber, ClaimType, userId, facilityIndex, ClientIP, database, "PAYMENT POSTED");

                        }
                    }
                }


                ps = conn.prepareStatement("UPDATE " + database + ".ClaimInfoMaster " +
                        "Set Allowed=? ," +
                        " Paid=? ," +
                        " Adjusted=? ," +
                        " Unpaid=? ," +
                        " Balance=? " +
                        "WHERE ClaimNumber=?");
                ps.setDouble(1, TOTAL_wrt_Claim_Allowed);
                ps.setDouble(2, TOTAL_wrt_Claim_Paid);
                ps.setDouble(3, TOTAL_wrt_Claim_Adjusted);
                ps.setDouble(4, TOTAL_wrt_Claim_Unpaid);
                ps.setDouble(5, TOTAL_wrt_Claim_Balance);
                ps.setString(6, ClaimNumber);
                ps.executeUpdate();
                ps.close();


                ps = conn.prepareStatement("SELECT PatientIdx ,PaymentAmount ,ReceivedDate ,CheckNumber ,PaymentType ," +
                        "PaymentSource ,Memo ,Status ,CopayDOS ,CardType ,CreatedDate ,CreatedBy ,ModifyBy ,ModifyDate " +
                        ",InsuranceIdx ,OtherRefrenceNo ,PaymentFrom ,isPaymentOnly ,isCreditAccount ,AppliedAmount ,UnappliedAmount" +
                        " FROM " + database + ".EOB_Master WHERE Id=?");
//                ps.setString(1, checkNumber);
                ps.setString(1, EOB_MasterIdx);
                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientIdx = rset.getString(1);
                    PaymentAmount = rset.getString(2);
                    ReceivedDate = rset.getString(3);
                    CheckNumber = rset.getString(4);
                    PaymentType = rset.getString(5);
                    PaymentSource = rset.getString(6);
                    Memo = rset.getString(7);
                    Status = rset.getString(8);
                    CopayDOS = rset.getString(9);
                    CardType = rset.getString(10);
                    CreatedDate = rset.getString(11);
                    CreatedBy = rset.getString(12);
                    ModifyBy = rset.getString(13);
                    ModifyDate = rset.getString(14);
                    InsuranceIdx = rset.getString(15);
                    OtherRefrenceNo = rset.getString(16);
                    PaymentFrom = rset.getString(17);
                    isPaymentOnly = rset.getString(18);
                    isCreditAccount = rset.getString(19);
                    AppliedAmount = rset.getDouble(20);
                    UnappliedAmount = rset.getDouble(21);

                    ps2 = conn.prepareStatement("INSERT INTO " + database + ".EOB_Master_HISTORY (PatientIdx ,PaymentAmount ,ReceivedDate ,CheckNumber " +
                            ",PaymentType ,PaymentSource ,Memo ,Status ,CopayDOS ,CardType ,CreatedDate ,CreatedBy ,ModifyBy ,ModifyDate ," +
                            "InsuranceIdx ,OtherRefrenceNo ,PaymentFrom ,isPaymentOnly ,isCreditAccount ,AppliedAmount ,UnappliedAmount,Id)" +
                            " VALUES (? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?)");
                    ps2.setString(1, PatientIdx);
                    ps2.setString(2, PaymentAmount);
                    ps2.setString(3, ReceivedDate);
                    ps2.setString(4, CheckNumber);
                    ps2.setString(5, PaymentType);
                    ps2.setString(6, PaymentSource);
                    ps2.setString(7, Memo);
                    ps2.setString(8, Status);
                    ps2.setString(9, CopayDOS);
                    ps2.setString(10, CardType);
                    ps2.setString(11, CreatedDate);
                    ps2.setString(12, CreatedBy);
                    ps2.setString(13, ModifyBy);
                    ps2.setString(14, ModifyDate);
                    ps2.setString(15, InsuranceIdx);
                    ps2.setString(16, OtherRefrenceNo);
                    ps2.setString(17, PaymentFrom);
                    ps2.setString(18, isPaymentOnly);
                    ps2.setString(19, isCreditAccount);
                    ps2.setDouble(20, AppliedAmount);
                    ps2.setDouble(21, UnappliedAmount);
                    ps2.setString(22, EOB_MasterIdx);
                    ps2.executeUpdate();
                    ps2.close();

                }
                rset.close();
                ps.close();

                //system.out.println("Existing Applied Amt -->> " + AppliedAmount);
                //system.out.println("Existing_Total_Paid -->> " + Existing_Total_Paid);
                //system.out.println("NEW_Total_Paid -->> " + NEW_Total_Paid);

                AppliedAmount += (NEW_Total_Paid - Existing_Total_Paid);
                UnappliedAmount = Double.parseDouble(PaymentAmount) - AppliedAmount;

                //system.out.println("New Applied Amt -->> " + AppliedAmount);


                ps = conn.prepareStatement("UPDATE " + database + ".`EOB_Master` SET " +
                        "  `AppliedAmount`= ?" +
                        " ,`UnappliedAmount`= ?" +
                        " ,`UpdatedAt`= NOW()" +
                        " ,`UpdatedBy`= ?" +
                        " ,`ViewDate`= NOW()" +
                        " WHERE `Id`= ?");
                ps.setDouble(1, AppliedAmount);
                ps.setDouble(2, UnappliedAmount);
                ps.setString(3, userId);
                ps.setString(4, EOB_MasterIdx);
//                ps.setString(4, CheckNumber);
                ps.executeUpdate();
                ps.close();


                out.println("1~md.ViewClaimPayment?ActionID=getClaims_WRT_Check&EOB_MasterIdx=" + EOB_MasterIdx + "&checkNumber=" + CheckNumber + "&InsuranceIdx=" + InsuranceIdx + "&Flag=0");
            }


//            getClaims_WRT_Check_inside(request, out, conn, servletContext, userId, database, helper, facilityIndex, payments,claimNumber,checkNumber);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);


//            helper.SendEmailWithAttachment("Error in setCharges_WRT_Claims ** (handleRequest)", servletContext, e, "setClaims_WRT_Check", "handleRequest", conn);
//            Services.DumException("setClaims_WRT_Check", "Handle Request", request, e, getServletContext());
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("FormName", "ManagementDashboard");
//            Parser.SetField("ActionID", "GetInput");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//            out.flush();
//            out.close();
        }
    }

    private boolean isDebitTransactionExists(Connection conn, String ChargeIdx, String EOB_MasterIdx, String ClaimIdx, String database) {
        boolean isExist = false;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + database + ".Claim_Ledger_Charges_entries " +
                    "WHERE ClaimIdx=? AND Deleted is NULL AND TransactionIdx=? AND ChargeIdx=? ");
            ps.setString(1, ClaimIdx);
            ps.setString(2, EOB_MasterIdx);
            ps.setString(3, ChargeIdx);
            ResultSet rset = ps.executeQuery();
            if(rset.next() && rset.getInt(1) > 0){
                isExist=true;
            }
            rset.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isExist;
    }

    private void setClaims_WRT_Checks(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String[] claimNumber = request.getParameter("claims").trim().split("~");
        String[] billed = request.getParameter("billed").trim().split("~");
        String checkNumber = request.getParameter("checkNumber").trim();
        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();
        String receivingDate = request.getParameter("receivingDate").trim();
        double CheckAmt = Double.parseDouble(request.getParameter("CheckAmt").trim().replace(",", ""));
        double AppliedAmt = Double.parseDouble(request.getParameter("AppliedAmt").trim().replace(",", ""));
        double UnappliedAmt = Double.parseDouble(request.getParameter("UnappliedAmt").trim().replace(",", ""));

        String ClientIP = helper.getClientIp(request);

        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rset = null;

        String ClaimNumber = "";
        int ClaimType = 0;
        String ClaimIdx = "";
        String ChargeIdx = "";
        String Charges = "";
        String Amount = "";
        String StartBalance = "";
        String Allowed = "";
        Double TOTAL_wrt_Claim_Allowed = 0.0;
        String Paid = "";
        Double TOTAL_wrt_Claim_Paid = 0.0;
        String Remarks = "";
        String AdjReasons = "";
        String Adjusted = "";
        Double TOTAL_wrt_Claim_Adjusted = 0.0;

        String SequestrationAmt = "";
        Double TOTAL_wrt_Claim_SequestrationAmt = 0.0;

        String UnpaidReasons = "";
        String Unpaid = "";
        Double TOTAL_wrt_Claim_Unpaid = 0.0;

        String Deductible = "";
        String Status = "";
        String OtherCredits = "";
        String EndBalance = "";
        Double TOTAL_wrt_Claim_Balance = 0.0;


        String CreatedAt = "";
        String CreatedBy = "";
        String UserIP = "";
        String TransactionIdx = "";
        String TransactionType = "";
        String Payment = "";
        String Adjustment = "";
        String Balance = "";
        String EOB_MasterIdx = "";

        try {
//            String pattern = "yyyy-MM-dd";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//            Date date1=  new SimpleDateFormat("dd/MM/yyyy").parse(receivingDate);
//            String date = simpleDateFormat.format(date1);
//
//            //system.out.println(date);
//            receivingDate = date;
//            //system.out.println(receivingDate);

            ps = conn.prepareStatement("INSERT INTO " + database + ".`EOB_Master` \n" +
                    "(PaymentAmount,ReceivedDate, CheckNumber, CreatedDate, " +
                    "CreatedBy, InsuranceIdx, OtherRefrenceNo,AppliedAmount,UnappliedAmount) " +
                    " VALUES (? ,? ,? ,NOW() ,? ,?,?,?,?)");
//                ps.setString(1,claimNumber[i]);
            ps.setDouble(1, CheckAmt);
            ps.setString(2, receivingDate);
            ps.setString(3, checkNumber);
            ps.setString(4, userId);
            ps.setString(5, InsuranceIdx);
            ps.setString(6, OtherRefrenceNo);
            ps.setDouble(7, AppliedAmt);
            ps.setDouble(8, UnappliedAmt);

//            //system.out.println("QUERY ->> " + ps.toString());
            ps.executeUpdate();
            ps.close();


            ps = conn.prepareStatement("SELECT Id FROM " + database + ".EOB_Master ORDER BY CreatedDate DESC  LIMIT 1");
            rset = ps.executeQuery();
            while (rset.next()) {
                EOB_MasterIdx = rset.getString(1);
            }
            rset.close();
            ps.close();


            for (int i = 0; i < claimNumber.length; i++) {
                ps = conn.prepareStatement("INSERT INTO " + database + ".Claim_Ledger_entries (TransactionIdx,ClaimNumber,Amount," +
                        "CreatedAt,CreatedBy,UserIP,TransactionType)" +
                        "VALUES (?,?,?,NOW(),?,?,'D')");

                ps.setString(1, EOB_MasterIdx);
//                ps.setString(1, checkNumber);
                ps.setString(2, claimNumber[i]);
                ps.setString(3, billed[i]);
                ps.setString(4, userId);
                ps.setString(5, ClientIP);
                //system.out.println("QUERY ->> " + ps.toString());
                ps.executeUpdate();
                ps.close();
            }

            for (int i = 0; i < claimNumber.length; i++) {
                ps = conn.prepareStatement("SELECT ClaimNumber,ClaimIdx,ChargeIdx,Charges,Amount,StartBalance " +
                        ",Allowed ,Paid ,Remarks ,AdjReasons ," +
                        "Adjusted ,SequestrationAmt ,UnpaidReasons ,Unpaid ,Deductible ,Status ,OtherCredits ,EndBalance, CreatedAt,CreatedBy,UserIP,TransactionIdx,TransactionType, Payment ,Adjustment ,Balance " +
                        " FROM " + database + ".Claim_Ledger_Charges_entries_TEMP WHERE ClaimNumber=? AND TransactionIdx=?");
                ps.setString(1, claimNumber[i]);
                ps.setString(2, checkNumber);
                rset = ps.executeQuery();
                while (rset.next()) {
                    ClaimNumber = rset.getString(1);
                    ClaimIdx = rset.getString(2);
                    ChargeIdx = rset.getString(3);
                    Charges = rset.getString(4);
                    Amount = rset.getString(5);
                    StartBalance = rset.getString(6);
                    Allowed = rset.getString(7);
                    TOTAL_wrt_Claim_Allowed += Double.parseDouble(Allowed);

                    Paid = rset.getString(8);
                    TOTAL_wrt_Claim_Paid += Double.parseDouble(Paid);

                    Remarks = rset.getString(9);
                    AdjReasons = rset.getString(10);

                    Adjusted = rset.getString(11);
                    TOTAL_wrt_Claim_Adjusted += Double.parseDouble(Adjusted);

                    SequestrationAmt = rset.getString(12);
                    TOTAL_wrt_Claim_SequestrationAmt += Double.parseDouble(SequestrationAmt);

                    UnpaidReasons = rset.getString(13);

                    Unpaid = rset.getString(14);
                    TOTAL_wrt_Claim_Unpaid += Double.parseDouble(Unpaid);

                    Deductible = rset.getString(15);
                    Status = rset.getString(16);
                    OtherCredits = rset.getString(17);
                    EndBalance = rset.getString(18);
                    TOTAL_wrt_Claim_Balance += Double.parseDouble(EndBalance);

                    CreatedAt = rset.getString(19);
                    CreatedBy = rset.getString(20);
                    UserIP = rset.getString(21);
                    TransactionIdx = rset.getString(22);
                    TransactionType = rset.getString(23);
                    Payment = rset.getString(24);
                    Adjustment = rset.getString(25);
                    Balance = rset.getString(26);


                    ps2 = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries` (ClaimNumber, ClaimIdx, ChargeIdx, Charges," +
                            " Amount, StartBalance, Allowed, Paid, Remarks, AdjReasons, Adjusted,SequestrationAmt," +
                            " UnpaidReasons, Unpaid, Deductible, Status, OtherCredits, EndBalance," +
                            " CreatedAt, CreatedBy, UserIP, TransactionIdx,TransactionType,Payment ,Adjustment ,Balance)" +
                            " VALUES ( ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?,? ,?,?,?)");
                    ps2.setString(1, ClaimNumber);
                    ps2.setString(2, ClaimIdx);
                    ps2.setString(3, ChargeIdx);
                    ps2.setString(4, Charges);
                    ps2.setString(5, Amount);
                    ps2.setString(6, StartBalance);
                    ps2.setString(7, Allowed);
                    ps2.setString(8, Paid);
                    ps2.setString(9, Remarks);
                    ps2.setString(10, AdjReasons);
                    ps2.setString(11, Adjusted);
                    ps2.setString(12, SequestrationAmt);
                    ps2.setString(13, UnpaidReasons);
                    ps2.setString(14, Unpaid);
                    ps2.setString(15, Deductible);
                    ps2.setString(16, Status);
                    ps2.setString(17, OtherCredits);
                    ps2.setString(18, EndBalance);
                    ps2.setString(19, CreatedAt);
                    ps2.setString(20, CreatedBy);
                    ps2.setString(21, UserIP);
//                    ps2.setString(21, TransactionIdx);
                    ps2.setString(22, EOB_MasterIdx);
                    ps2.setString(23, TransactionType);
                    ps2.setString(24, Payment);
                    ps2.setString(25, Adjustment);
                    ps2.setString(26, Balance);
                    ps2.executeUpdate();
                    ps2.close();

                    if (claimNumber[i].split("-")[0].equals("CP"))
                        ClaimType = 2;
                    else
                        ClaimType = 1;

                    insertIntoClaim_AuditTrails(conn, "Payment Posted Against "+Charges , claimNumber[i], ClaimType, userId, facilityIndex, ClientIP, database, "PAYMENT POSTED");
                }
                rset.close();
                ps.close();

                ps = conn.prepareStatement("UPDATE " + database + ".ClaimInfoMaster " +
                        "Set Allowed=? ," +
                        " Paid=? ," +
                        " Adjusted=? ," +
                        " Unpaid=?, " +
                        " Balance=? " +
                        "WHERE ClaimNumber=?");
                ps.setDouble(1, TOTAL_wrt_Claim_Allowed);
                ps.setDouble(2, TOTAL_wrt_Claim_Paid);
                ps.setDouble(3, TOTAL_wrt_Claim_Adjusted);
                ps.setDouble(4, TOTAL_wrt_Claim_Unpaid);
                ps.setDouble(5, TOTAL_wrt_Claim_Balance);
                ps.setString(6, claimNumber[i]);
                ps.executeUpdate();
                ps.close();

                TOTAL_wrt_Claim_Allowed = 0.0;
                TOTAL_wrt_Claim_Paid = 0.0;
                TOTAL_wrt_Claim_Adjusted = 0.0;
                TOTAL_wrt_Claim_Unpaid = 0.0;
                TOTAL_wrt_Claim_Balance = 0.0;


            }


            for (int i = 0; i < claimNumber.length; i++) {
                ps = conn.prepareStatement("Delete from " + database + ".Claim_Ledger_Charges_entries_TEMP WHERE ClaimNumber=? AND TransactionIdx=?");
                ps.setString(1, claimNumber[i]);
                ps.setString(2, TransactionIdx);
                ps.executeUpdate();
                ps.close();
            }


            out.println("1");
//            getClaims_WRT_Check_inside(request, out, conn, servletContext, userId, database, helper, facilityIndex, payments,claimNumber,checkNumber);

        } catch (Exception e) {
            e.printStackTrace();
            //system.out.println("Error in : " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //system.out.println(str);


            helper.SendEmailWithAttachment("Error in setClaims_WRT_Check ** (handleRequest)", servletContext, e, "setClaims_WRT_Check", "handleRequest", conn);
            Services.DumException("setClaims_WRT_Check", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//            out.flush();
//            out.close();
        }
    }

    private void setClaims_WRT_PatientPayment(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String userId, String database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException, ParseException {


        String CardType = request.getParameter("CardType") == null ? null : request.getParameter("CardType").trim();
        String CopayDOS = request.getParameter("CopayDOS") == null ? null : request.getParameter("CopayDOS").trim();
        CopayDOS = CopayDOS.equals("") ? null : CopayDOS;

        String memo = request.getParameter("memo") == null ? null : request.getParameter("memo").trim();
        String paymentType = request.getParameter("paymentType") == null ? null : request.getParameter("paymentType").trim();
        String checkNumber = request.getParameter("checkNumber") == null ? null : request.getParameter("checkNumber").trim();
        String receivingDate = request.getParameter("receivingDate") == null ? null : request.getParameter("receivingDate").trim();
        String Source = request.getParameter("Source") == null ? null : request.getParameter("Source").trim();
        String PatRegIdx = request.getParameter("PatRegIdx") == null ? null : request.getParameter("PatRegIdx").trim();
        String ChargesString = request.getParameter("ChargesString") == null ? null : request.getParameter("ChargesString").trim();
        String ChargesTableCount = request.getParameter("ChargesTableCount") == null ? null : request.getParameter("ChargesTableCount").trim();
        String Flag = request.getParameter("Flag").trim();
        String EOB_MasterIdx = request.getParameter("EOB_MasterIdx") == null ? null : request.getParameter("EOB_MasterIdx");

        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        //system.out.println("receivingDate ->> " + receivingDate);
        receivingDate = myFormat.format(fromUser.parse(receivingDate));
        //system.out.println("receivingDate ->> " + receivingDate);

        double PaymentAmount = Double.parseDouble(request.getParameter("PaymentAmount").trim().replace(",", ""));
        double AppliedAmt = Double.parseDouble(request.getParameter("AppliedAmt").trim().replace(",", ""));
        double UnappliedAmt = Double.parseDouble(request.getParameter("UnappliedAmt").trim().replace(",", ""));

        //system.out.println("AppliedAmt ->> " + AppliedAmt);
        //system.out.println("UnappliedAmt ->> " + UnappliedAmt);
        //system.out.println("CopayDOS ->> " + CopayDOS);
        //system.out.println(" request.getParameter(\"Flag\") == null ->> " + request.getParameter("Flag") == null);


        String ClientIP = helper.getClientIp(request);

        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rset = null;

        String ClaimNumber = "";
        String ClaimIdx = "";
        String ChargeIdx = "";
        String Charges = "";
        String Amount = "";
        String StartBalance = "";
        String Allowed = "";
        String Paid = "";
        String Remarks = "";
        String AdjReasons = "";
        String Adjusted = "";
        String UnpaidReasons = "";
        String Unpaid = "";
        String Deductible = "";
        String Status = "";
        String OtherCredits = "";
        String EndBalance = "";
        String CreatedAt = "";
        String CreatedBy = "";
        String UserIP = "";
        String TransactionIdx = "";
        String TransactionType = "";
        String Payment = "";
        String Adjustment = "";
        String Balance = "";
//        String EOB_MasterIdx = "";


        String PatientIdx = null;
        String ReceivedDate = null;
        String CheckNumber = null;
        String PaymentType = null;
        String PaymentSource = null;
        String Memo = null;
        String CreatedDate = null;
        String ModifyBy = null;
        String ModifyDate = null;
        String PaymentFrom = null;
        String isPaymentOnly = null;
        String isCreditAccount = null;
        String InsuranceIdx = null;
        String OtherRefrenceNo = null;
        double AppliedAmount = 0.0f;
        double UnappliedAmount = 0.0f;


        int i = 0, j = 0, k = 0;
        String[] myInfoCharges;
        myInfoCharges = new String[0];
        myInfoCharges = ChargesString.split("\\^");
        String ChargesInput[][] = new String[Integer.parseInt(ChargesTableCount)][12];
        i = j = k = 0;
        for (i = 1; i < myInfoCharges.length; i++) {
            if (myInfoCharges[i].length() <= 0)
                continue;


            if (myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1).equals("^"))
                ChargesInput[k][j] = "-";
            else {
                ChargesInput[k][j] = myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1);
                //system.out.println("ChargesInput [" + k + "][" + j + "] -> " + ChargesInput[k][j]);
            }

            j++;
            if (j > 11) {
                j = 0;
                k++;
            }


        }

        try {
//            String pattern = "yyyy-MM-dd";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//            Date date1=  new SimpleDateFormat("dd/MM/yyyy").parse(receivingDate);
//            String date = simpleDateFormat.format(date1);
//
//            //system.out.println(date);
//            receivingDate = date;
//            //system.out.println(receivingDate);

            if (Flag.equals("1")) {


                //system.out.println("setClaims_WRT_PatientPayment Flag ->> " + Flag);
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + database + ".EOB_Master (PatientIdx,PaymentAmount,ReceivedDate,CheckNumber," +
                                "PaymentType,PaymentSource,Memo,CopayDOS,CardType,CreatedBy,Status,CreatedDate,AppliedAmount,UnappliedAmount) " +
                                " VALUES (?,?,?,?,?,?,?,?,?,?,0,now(),?,?) ");
                MainReceipt.setString(1, PatRegIdx);
                MainReceipt.setDouble(2, PaymentAmount);
                MainReceipt.setString(3, receivingDate);
                MainReceipt.setString(4, checkNumber);
                MainReceipt.setString(5, paymentType);
                MainReceipt.setString(6, Source);
                MainReceipt.setString(7, memo);
                MainReceipt.setString(8, CopayDOS);
                MainReceipt.setString(9, CardType);
                MainReceipt.setString(10, userId);
                MainReceipt.setDouble(11, AppliedAmt);
                MainReceipt.setDouble(12, UnappliedAmt);
                MainReceipt.executeUpdate();
                MainReceipt.close();


                ps = conn.prepareStatement("SELECT Id FROM " + database + ".EOB_Master ORDER BY CreatedDate DESC  LIMIT 1");
                rset = ps.executeQuery();
                while (rset.next()) {
                    EOB_MasterIdx = rset.getString(1);
                }
                rset.close();
                ps.close();


                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
                    ps = conn.prepareStatement("INSERT INTO " + database + ".Claim_Ledger_entries (TransactionIdx,ClaimNumber,Amount," +
                            "CreatedAt,CreatedBy,UserIP,TransactionType)" +
                            "VALUES (?,?,?,NOW(),?,?,'D')");

                    ps.setString(1, EOB_MasterIdx);
//                ps.setString(1, checkNumber);
                    ps.setString(2, ChargesInput[i][0]);//ClaimNumber
                    ps.setString(3, ChargesInput[i][3]);//Amt
                    ps.setString(4, userId);
                    ps.setString(5, ClientIP);
                    //system.out.println("QUERY ->> " + ps.toString());
                    ps.executeUpdate();
                    ps.close();
                }

                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {

                    ps2 = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries` (ClaimNumber, ClaimIdx, ChargeIdx, Charges," +
                            " Amount, StartBalance, Paid,Status, OtherCredits, Adjusted," +
                            " EndBalance," +
                            " CreatedAt, CreatedBy, UserIP, TransactionIdx,TransactionType,Payment ,Adjustment ,Balance)" +
                            " VALUES ( ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,NOW() ,? ,? ,? ,'D' ,? ,? ,? )");
                    ps2.setString(1, ChargesInput[i][0]);//ClaimNumber
                    ps2.setString(2, ChargesInput[i][11]);//ClaimIdx
                    ps2.setString(3, ChargesInput[i][10]);//ChargeIdx
                    ps2.setString(4, ChargesInput[i][2]);//Procedures
                    ps2.setString(5, ChargesInput[i][3]);//Amt
                    ps2.setString(6, ChargesInput[i][4]);//StartBal
                    ps2.setString(7, ChargesInput[i][5]);//ApplyPayment
                    ps2.setString(8, ChargesInput[i][6]);//Status
                    ps2.setString(9, ChargesInput[i][7]);//ApplyCredits
                    ps2.setString(10, ChargesInput[i][8]);//Adjustments
                    ps2.setString(11, ChargesInput[i][9]);//EndBal
                    ps2.setString(12, userId);
                    ps2.setString(13, ClientIP);
                    ps2.setString(14, EOB_MasterIdx);
                    ps2.setString(15, ChargesInput[i][5]);
                    ps2.setString(16, ChargesInput[i][8]);
                    ps2.setString(17, ChargesInput[i][9]);
                    ps2.executeUpdate();
                    ps2.close();
                }
                out.println("1");
            } else {

                //system.out.println(" setClaims_WRT_PatientPayment Flag ->> " + Flag);


                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
                    ps = conn.prepareStatement("SELECT ClaimNumber,ClaimIdx,ChargeIdx,Charges,Amount,StartBalance " +
                            ",Allowed ,Paid ,Remarks ,AdjReasons ," +
                            " Adjusted ,UnpaidReasons ,Unpaid ,Deductible ,Status ,OtherCredits ,EndBalance, CreatedAt,CreatedBy,UserIP,TransactionIdx,TransactionType, Payment ,Adjustment ,Balance " +
                            " FROM " + database + ".Claim_Ledger_Charges_entries WHERE ClaimNumber=? AND TransactionIdx=?");
                    ps.setString(1, ChargesInput[i][0]);//ClaimNumber
                    ps.setString(2, EOB_MasterIdx);
                    rset = ps.executeQuery();
                    while (rset.next()) {
                        ClaimNumber = rset.getString(1);
                        ClaimIdx = rset.getString(2);
                        ChargeIdx = rset.getString(3);
                        Charges = rset.getString(4);
                        Amount = rset.getString(5);
                        StartBalance = rset.getString(6);
                        Allowed = rset.getString(7);
                        Paid = rset.getString(8);
                        Remarks = rset.getString(9);
                        AdjReasons = rset.getString(10);
                        Adjusted = rset.getString(11);
                        UnpaidReasons = rset.getString(12);
                        Unpaid = rset.getString(13);
                        Deductible = rset.getString(14);
                        Status = rset.getString(15);
                        OtherCredits = rset.getString(16);
                        EndBalance = rset.getString(17);
                        CreatedAt = rset.getString(18);
                        CreatedBy = rset.getString(19);
                        UserIP = rset.getString(20);
                        TransactionIdx = rset.getString(21);
                        TransactionType = rset.getString(22);
                        Payment = rset.getString(23);
                        Adjustment = rset.getString(24);
                        Balance = rset.getString(25);


                        ps2 = conn.prepareStatement("INSERT INTO " + database + ".`Claim_Ledger_Charges_entries_HISTORY` (ClaimNumber, ClaimIdx, ChargeIdx, Charges," +
                                " Amount, StartBalance, Allowed, Paid, Remarks, AdjReasons, Adjusted," +
                                " UnpaidReasons, Unpaid, Deductible, Status, OtherCredits, EndBalance," +
                                " CreatedAt, CreatedBy, UserIP, TransactionIdx,TransactionType,Payment ,Adjustment ,Balance)" +
                                " VALUES ( ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?,? ,?,?)");
                        ps2.setString(1, ClaimNumber);
                        ps2.setString(2, ClaimIdx);
                        ps2.setString(3, ChargeIdx);
                        ps2.setString(4, Charges);
                        ps2.setString(5, Amount);
                        ps2.setString(6, StartBalance);
                        ps2.setString(7, Allowed);
                        ps2.setString(8, Paid);
                        ps2.setString(9, Remarks);
                        ps2.setString(10, AdjReasons);
                        ps2.setString(11, Adjusted);
                        ps2.setString(12, UnpaidReasons);
                        ps2.setString(13, Unpaid);
                        ps2.setString(14, Deductible);
                        ps2.setString(15, Status);
                        ps2.setString(16, OtherCredits);
                        ps2.setString(17, EndBalance);
                        ps2.setString(18, CreatedAt);
                        ps2.setString(19, CreatedBy);
                        ps2.setString(20, UserIP);
                        ps2.setString(21, TransactionIdx);
                        ps2.setString(22, TransactionType);
                        ps2.setString(23, Payment);
                        ps2.setString(24, Adjustment);
                        ps2.setString(25, Balance);
                        ps2.executeUpdate();
                        ps2.close();


                    }
                    ps.close();
                    rset.close();
                }


                double NEW_Total_Paid = 0.0f;
                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
//                    Amount, StartBalance, Paid,Status, OtherCredits, Adjusted," +
//                    " EndBalance
                    ps = conn.prepareStatement("UPDATE " + database + ".`Claim_Ledger_Charges_entries` SET " +
                            " `StartBalance`=?," +
                            " `Paid`=?," +
                            " `Status`=?," +
                            " `OtherCredits`=?," +
                            " `Adjusted`=?," +
                            " `EndBalance`=?," +
                            " `UpdatedAt`=NOW() ," +
                            " `UpdatedBy`=?," +
                            " `UserIP`=?," +
                            " `Payment`=?," +
                            " `Adjustment`=? " +
                            " WHERE ChargeIdx = ? AND TransactionIdx=? AND TransactionType='D' AND ClaimIdx=?");

                    ps.setString(1, ChargesInput[i][4]);//StartBal
                    ps.setString(2, ChargesInput[i][5]);//ApplyPayment
                    ps.setString(3, ChargesInput[i][6]);//Status
                    ps.setString(4, ChargesInput[i][7]);//ApplyCredits
                    ps.setString(5, ChargesInput[i][8]);//Adjustments
                    ps.setString(6, ChargesInput[i][9]);//EndBal
                    ps.setString(7, userId);
                    ps.setString(8, ClientIP);
                    ps.setString(9, ChargesInput[i][5].replaceAll("\\$", ""));//Payment
                    ps.setString(10, ChargesInput[i][8].replaceAll("\\$", ""));//Adjustment
                    ps.setString(11, ChargesInput[i][10]);//ChargeIdx
                    ps.setString(12, EOB_MasterIdx);
//                    ps.setString(18, checkNumber);
                    ps.setString(13, ChargesInput[i][11]);//ClaimIdx

                    //system.out.println("UPDATE ** QUERY -->> " + ps.toString());
                    ps.executeUpdate();
                    ps.close();


                }


                ps = conn.prepareStatement("SELECT PatientIdx ,PaymentAmount ,ReceivedDate ,CheckNumber ,PaymentType ," +
                        "PaymentSource ,Memo ,Status ,CopayDOS ,CardType ,CreatedDate ,CreatedBy ,ModifyBy ,ModifyDate " +
                        ",InsuranceIdx ,OtherRefrenceNo ,PaymentFrom ,isPaymentOnly ,isCreditAccount ,AppliedAmount ,UnappliedAmount" +
                        " FROM " + database + ".EOB_Master WHERE Id=?");
//                ps.setString(1, checkNumber);
                ps.setString(1, EOB_MasterIdx);
                rset = ps.executeQuery();
                if (rset.next()) {
                    PatientIdx = rset.getString(1);
                    PaymentAmount = rset.getDouble(2);
                    ReceivedDate = rset.getString(3);
                    CheckNumber = rset.getString(4);
                    PaymentType = rset.getString(5);
                    PaymentSource = rset.getString(6);
                    Memo = rset.getString(7);
                    Status = rset.getString(8);
                    CopayDOS = rset.getString(9);
                    CardType = rset.getString(10);
                    CreatedDate = rset.getString(11);
                    CreatedBy = rset.getString(12);
                    ModifyBy = rset.getString(13);
                    ModifyDate = rset.getString(14);
                    InsuranceIdx = rset.getString(15);
                    OtherRefrenceNo = rset.getString(16);
                    PaymentFrom = rset.getString(17);
                    isPaymentOnly = rset.getString(18);
                    isCreditAccount = rset.getString(19);
                    AppliedAmount = rset.getDouble(20);
                    UnappliedAmount = rset.getDouble(21);

                    ps2 = conn.prepareStatement("INSERT INTO " + database + ".EOB_Master_HISTORY (PatientIdx ,PaymentAmount ,ReceivedDate ,CheckNumber " +
                            ",PaymentType ,PaymentSource ,Memo ,Status ,CopayDOS ,CardType ,CreatedDate ,CreatedBy ,ModifyBy ,ModifyDate ," +
                            "InsuranceIdx ,OtherRefrenceNo ,PaymentFrom ,isPaymentOnly ,isCreditAccount ,AppliedAmount ,UnappliedAmount,Id)" +
                            " VALUES (? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?)");
                    ps2.setString(1, PatientIdx);
                    ps2.setDouble(2, PaymentAmount);
                    ps2.setString(3, ReceivedDate);
                    ps2.setString(4, CheckNumber);
                    ps2.setString(5, PaymentType);
                    ps2.setString(6, PaymentSource);
                    ps2.setString(7, Memo);
                    ps2.setString(8, Status);
                    ps2.setString(9, CopayDOS);
                    ps2.setString(10, CardType);
                    ps2.setString(11, CreatedDate);
                    ps2.setString(12, CreatedBy);
                    ps2.setString(13, ModifyBy);
                    ps2.setString(14, ModifyDate);
                    ps2.setString(15, InsuranceIdx);
                    ps2.setString(16, OtherRefrenceNo);
                    ps2.setString(17, PaymentFrom);
                    ps2.setString(18, isPaymentOnly);
                    ps2.setString(19, isCreditAccount);
                    ps2.setDouble(20, AppliedAmount);
                    ps2.setDouble(21, UnappliedAmount);
                    ps2.setString(22, EOB_MasterIdx);
                    ps2.executeUpdate();
                    ps2.close();

                }
                rset.close();
                ps.close();


                ps = conn.prepareStatement("UPDATE " + database + ".`EOB_Master` SET " +
                        "  `AppliedAmount`= ?" +
                        " ,`UnappliedAmount`= ?" +
                        " ,`UpdatedAt`= NOW()" +
                        " ,`UpdatedBy`= ?" +
                        " ,`ViewDate`= NOW() " +
                        " WHERE `Id`= ?");
                ps.setDouble(1, AppliedAmt);
                ps.setDouble(2, UnappliedAmt);
                ps.setString(3, userId);
                ps.setString(4, EOB_MasterIdx);

                //system.out.println("UPDATE ** QUERY -->> " + ps.toString());
//                ps.setString(4, CheckNumber);
                ps.executeUpdate();
                ps.close();

                out.println("1");
            }


//            getClaims_WRT_Check_inside(request, out, conn, servletContext, userId, database, helper, facilityIndex, payments,claimNumber,checkNumber);

        } catch (Exception e) {
            e.printStackTrace();
            //system.out.println("Error in : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //system.out.println(str);

            //system.out.println("Error in : " + e.getMessage());

            helper.SendEmailWithAttachment("Error in setClaims_WRT_Check ** (handleRequest)", servletContext, e, "setClaims_WRT_Check", "handleRequest", conn);
            Services.DumException("setClaims_WRT_Check", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
//            out.flush();
//            out.close();
        }
    }

    void ShowReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
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

        String CheckNumber = request.getParameter("CheckNumber").trim();
        String UnappliedAmt = request.getParameter("UnappliedAmt").trim();
        String AppliedAmt = request.getParameter("AppliedAmt").trim();
        String CheckAmt = request.getParameter("CheckAmt").trim();
        String receivingDate = request.getParameter("receivingDate").trim();
        String InsuranceIdx = request.getParameter("InsuranceIdx").trim();
        String OtherRefrenceNo = request.getParameter("OtherRefrenceNo").trim();


        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT IFNULL(a.VisitId,''),IFNULL(a.PatientRegId,''),IFNULL(a.AcctNo,''),IFNULL(a.ClaimType,'')" +
                    " ,IFNULL(a.ClaimNumber,''),IFNULL(a.PatientName,''),IFNULL(a.DOS,''),IFNULL(a.TotalCharges,''),IFNULL(a.Balance,''),IFNULL(b.Desc,'') " +
                    " FROM " + Database + ".ClaimInfoMaster a " +
                    " LEFT JOIN oe.ClaimStatus_NEW b on a.Status = b.Id ORDER BY CreatedDate DESC");
            rset = ps.executeQuery();
            while (rset.next()) {

                CDRList.append("<tr onclick=openClaim(\"/md/md.ViewClaimPayment?ActionID=getCharges_WRT_Claims&claimNumber=" + rset.getString(5) + "&checkNumber=" + CheckNumber + "&UnappliedAmt=" + UnappliedAmt + "&AppliedAmt=" + AppliedAmt + "&CheckAmt=" + CheckAmt + "&receivingDate=" + receivingDate + "&OtherRefrenceNo=" + OtherRefrenceNo + "&InsuranceIdx=" + InsuranceIdx + "\") >");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left> $" + String.format("%,.2f", rset.getDouble(8)) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                if (rset.getInt(4) == 1)
                    CDRList.append("<td align=left>Institutional</td>\n");
                else
                    CDRList.append("<td align=left>Professional</td>\n");

                CDRList.append("</tr>");
            }
            rset.close();
            ps.close();


            out.println(String.valueOf(CDRList));
//            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowClaims.html");
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in ViewClaimPayment ** (ShowReport)", servletContext, e, "ViewClaimPayment", "ShowReport", conn);
            Services.DumException("ShowReport", "ViewClaimPayment ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    void doEmpty(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {

        String CheckNumber = request.getParameter("checkNumber").trim();
        String[] claimNumber = request.getParameter("claims").trim().split("~");

        StringBuffer CDRList = new StringBuffer();
        int SNo = 0;
        PreparedStatement ps = null;
        try {

            for (int i = 0; i < claimNumber.length; i++) {
                ps = conn.prepareStatement("Delete from " + Database + ".Claim_Ledger_Charges_entries_TEMP WHERE ClaimNumber=? AND TransactionIdx=?");
                ps.setString(1, claimNumber[i]);
                ps.setString(2, CheckNumber);
                ps.executeUpdate();
                ps.close();
            }

//            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowClaims.html");
        } catch (Exception e) {
            if (conn == null) {
                conn = Services.getMysqlConn(servletContext);
            }
            helper.SendEmailWithAttachment("Error in ViewClaimPayment ** (ShowReport)", servletContext, e, "ViewClaimPayment", "ShowReport", conn);
            Services.DumException("ShowReport", "ViewClaimPayment ", request, e, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void searchPatient(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String userId, String Database, UtilityHelper helper, int facilityIndex, Payments payments) throws FileNotFoundException {
        String searchPatient = request.getParameter("searchPatient");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer PatientList = new StringBuffer();
        try {
            Query = " Select PatientRegId,PatientName, AcctNo,DOS from " + Database + ".ClaimInfoMaster a " +
                    " INNER JOIN " + Database + ".EOB_Master b ON b.PatientIdx = a.PatientRegId  " +
                    " where PatientName like '%" + searchPatient + "%' ORDER BY b.CreatedDate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                PatientList.append("<tr onclick='getVal(this);'>");
                PatientList.append("<td style=\"display: none;\" align=left>" + rset.getString(1) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                PatientList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                PatientList.append("</tr>");
            }
            rset.close();
            stmt.close();

            out.println(PatientList.toString());
        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            //system.out.println(str);

        }
    }

    private void insertIntoClaim_AuditTrails(Connection conn, String Desc, String ClaimNumber, int ClaimType, String UserId, int ClientId, String ClientIP, String Database, String Action) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO " + Database + ".Claim_AuditTrails ( `RuleText`, `ClaimNo`, `ClaimType`, `UserID`, `ClientID`, `CreatedAt`,`UserIP` ,`Action`) VALUES (?,?,?,?,?,NOW(),?,?)");
            ps.setString(1, Desc);
            ps.setString(2, ClaimNumber);
            ps.setInt(3, ClaimType);
            ps.setString(4, UserId);
            ps.setInt(5, ClientId);
            ps.setString(6, ClientIP);
            ps.setString(7, Action);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
