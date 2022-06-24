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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")
public class MergeMRN extends HttpServlet {
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
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Connection conn = null;
        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();

        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
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
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "MrnMerge":
                    MrnMerge(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/MergeMrn.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    void MrnMerge(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) throws SQLException {
        final String oldMrn = request.getParameter("oldMrn").trim().equals("") ? null : request.getParameter("oldMrn").trim();
        final String newMrn = request.getParameter("newMrn").trim().equals("") ? null : request.getParameter("newMrn").trim();
        //Flag = true if both MRN exist in a system
        final boolean Flag = request.getParameter("Flag") != null;
        int MRN = -1;
        ResultSet rset;
        String patientRegId = null;


        //by-pass checks in case of NewMRN and OldMRN are in systems
        if (!Flag) {
            if (MrnExist(conn, Database, newMrn) && MrnExist(conn, Database, oldMrn)) {
                StringBuilder NewData = getBasicData(conn, Database, newMrn);
                StringBuilder OldData = getBasicData(conn, Database, oldMrn);

                out.println("999~" + String.valueOf(NewData) + "~" + String.valueOf(OldData));
                return;
            }

            if (!MrnExist(conn, Database, oldMrn)) {
                out.println("Old MRN Does not Exist");
                return;
            }

            if (MrnExist(conn, Database, newMrn)) {
                out.println("New MRN exist already");
                return;
            }


        }


        if (Flag) {
            PreparedStatement ps = conn.prepareStatement("SELECT Id FROM " + Database + ".PatientReg WHERE MRN='" + oldMrn + "'  AND status=0");
            rset = ps.executeQuery();
            if (rset.next()) {
                patientRegId = rset.getString(1);
            }
            rset.close();
            ps.close();
        }


        if (insertHistoryPatientReg(conn, Database, oldMrn)) {
            updatePatientReg(conn, Database, oldMrn, newMrn, Flag);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryPatientVisit(conn, Database, oldMrn)) {
            if (Flag) updatePatientVisit(conn, Database, oldMrn, newMrn, patientRegId);
            else updatePatientVisit(conn, Database, oldMrn, newMrn, null);


        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }


        if (insertHistoryNotes(conn, Database, oldMrn)) {
            updateNotes(conn, Database, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryAlerts(conn, Database, oldMrn)) {
            updateAlerts(conn, Database, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryInvoiceMaster(conn, Database, oldMrn)) {
            updateInvoiceMaster(conn, Database, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryInvoiceDetail(conn, Database, oldMrn)) {
            updateInvoiceDetail(conn, Database, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryPaymentReceiptInfo(conn, Database, oldMrn)) {
            updatePaymentReceiptInfo(conn, Database, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }

        if (insertHistoryPatientDocUpload(conn, Database, oldMrn)) {
            if (Flag) updatePatientDocUpload(conn, Database, oldMrn, newMrn, patientRegId);
            else updatePatientDocUpload(conn, Database, oldMrn, newMrn, null);

        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }


        if (insertHistoryEligibilityInquiry(conn, ClientId, oldMrn)) {
            updateEligibilityInquiry(conn, ClientId, oldMrn, newMrn);
        } else {
            out.println("Oops.. Something Goes Wrong!");
            return;
        }


        //if old mrn is last patient then add dummy patient with status 1
        if (isLast(conn, Database, newMrn)) {
            PreparedStatement ps = conn.prepareStatement("Select MRN from " + Database + ".PatientReg where MRN like '31%' order by ID desc limit 1 ");
            rset = ps.executeQuery();
            if (rset.next())
                MRN = rset.getInt(1);
            rset.close();
            ps.close();

            //insert dummy patient with status 1
            try {
                MRN++;
                ps = conn.prepareStatement("INSERT INTO " + Database + ".PatientReg(MRN,FirstName,LastName,Status) " +
                        "VALUES (?,?,?,1)");
                ps.setInt(1, MRN);
                ps.setString(2, "DummyMRN");
                ps.setString(3, "Test");
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                System.out.println("Error in inserting dummy data ->" + e.getMessage());
                e.printStackTrace();
            }
        }


        PreparedStatement ps = conn.prepareStatement("INSERT INTO " + Database + ".SuccessfulMerge (OldMRN ,NewMRN ,MergedAt ,MergedBy)" +
                " VALUES (?,?,NOW(),?)");
        ps.setString(1, oldMrn);
        ps.setString(2, newMrn);
        ps.setString(3, UserId);
        ps.executeUpdate();
        ps.close();
        //Success
        out.println("1");
    }

    private StringBuilder getBasicData(Connection conn, String database, String _MRN) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rset;
        StringBuilder Data = new StringBuilder();
        try {
            ps = conn.prepareStatement(" SELECT FirstName,LastName,DOB FROM " + database + ".PatientReg " +
                    " WHERE MRN = '" + _MRN + "' AND status=0");
            rset = ps.executeQuery();
            if (rset.next()) {
                Data.append(
                        "<tr><td align=\"right\"><b>MRN</b></td><td>" + _MRN + "</td></tr>\n" +
                                "<tr><td align=\"right\"><b>FirstName</b></td><td>" + rset.getString(1) + "</td></tr>\n" +
                                "<tr><td align=\"right\"><b>LastName</b></td><td>" + rset.getString(2) + "</td></tr>\n" +
                                "<tr><td align=\"right\"><b>DOB</b></td><td>" + rset.getString(3) + "</td></tr>"
                );
            }
        } catch (SQLException e) {
            System.out.println("insertHistoryEligibilityInquiry ERROR -> " + e.getMessage());
            e.printStackTrace();
        } finally {
            ps.close();
        }
        return Data;
    }

    private boolean updateEligibilityInquiry(Connection conn, int clientId, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE EligibilityInquiry " +
                    " SET PatientMRN ='" + newMrn + "' " +
                    " WHERE PatientMRN = '" + oldMRN + "' AND FacilityIndex=" + clientId);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updateEligibilityInquiry ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryEligibilityInquiry(Connection conn, int clientId, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO EligibilityInquiry_History" +
                    " (Id ,PatientMRN ,DateofService ,TraceId ,PolicyStatus ,strmsg ,Name ,DateofBirth ,Gender ,InsuranceNum ,GediPayerId ,CreatedBy ,CreatedDate ,ResponseType ,FacilityIndex ,EProvider) " +
                    " SELECT Id ,PatientMRN ,DateofService ,TraceId ,PolicyStatus ,strmsg ,Name ,DateofBirth ,Gender ,InsuranceNum ,GediPayerId ,CreatedBy ,CreatedDate ,ResponseType ,FacilityIndex ,EProvider " +
                    " FROM EligibilityInquiry " +
                    " WHERE PatientMRN = '" + oldMRN + "' AND FacilityIndex=" + clientId);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryEligibilityInquiry ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePatientDocUpload(Connection conn, String database, String oldMRN, String newMrn, String patientRegId) throws SQLException {
        boolean success;
        try {
            if (patientRegId != null) {
                success = updatePatientRegId_DocUpload(conn, database, newMrn, patientRegId) &&
                        updateMRN_DocUpload(conn, database, newMrn, oldMRN);
            } else {
                success = updateMRN_DocUpload(conn, database, newMrn, oldMRN);
            }
        } catch (SQLException e) {
            System.out.println("updatePatientDocUpload ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private boolean updateMRN_DocUpload(Connection conn, String database, String newMrn, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".PatientDocUpload " +
                    " SET PatientMRN ='" + newMrn + "' " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePatientRegId_DocUpload(Connection conn, String database, String newMrn, String patientRegId) throws SQLException {
        PreparedStatement ps = null;
        boolean success;

        try {
            ps = conn.prepareStatement("UPDATE " + database + ".PatientDocUpload " +
                    " SET PatientRegId ='" + patientRegId + "' " +
                    " WHERE PatientMRN = " + newMrn);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryPatientDocUpload(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".PatientDocUpload_History " +
                    " (Id ,PremisisId ,PatientRegId ,PatientMRN ,PatientName ,UploadDocumentName ,FileName ,CreatedBy ,CreatedDate ,DocumentType ,Status ,DateofService ,VisitIdx) " +
                    " SELECT Id ,PremisisId ,PatientRegId ,PatientMRN ,PatientName ,UploadDocumentName ,FileName ,CreatedBy ,CreatedDate ,DocumentType ,Status ,DateofService ,VisitIdx " +
                    " FROM " + database + ".PatientDocUpload " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryPatientDocUpload ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePaymentReceiptInfo(Connection conn, String database, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".PaymentReceiptInfo " +
                    " SET PatientMRN ='" + newMrn + "' " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updatePaymentReceiptInfo ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryPaymentReceiptInfo(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".PaymentReceiptInfo_History " +
                    " (Id ,PatientMRN ,InvoiceNo ,TotalAmount ,PaidAmount ,BalAmount ,Paid ,RefNo ,Remarks ,PayMethod ,CreatedDate ,CreatedBy ,UserIP ,ActionID ,ReceiptNo ,Receipt ,Discount)" +
                    " SELECT Id ,PatientMRN ,InvoiceNo ,TotalAmount ,PaidAmount ,BalAmount ,Paid ,RefNo ,Remarks ,PayMethod ,CreatedDate ,CreatedBy ,UserIP ,ActionID ,ReceiptNo ,Receipt ,Discount " +
                    " FROM " + database + ".PaymentReceiptInfo " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryPaymentReceiptInfo ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updateInvoiceDetail(Connection conn, String database, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".InvoiceDetail " +
                    " SET PatientMRN ='" + newMrn + "' " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updateInvoiceDetail ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryInvoiceDetail(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".InvoiceDetail_History " +
                    " (Id ,InvoiceMasterId ,PatientMRN ,InvoiceNo ,DiseaseId ,CostPerDisease ,Quantity ,CreatedDate ,CreatedBy) " +
                    " SELECT Id ,InvoiceMasterId ,PatientMRN ,InvoiceNo ,DiseaseId ,CostPerDisease ,Quantity ,CreatedDate ,CreatedBy " +
                    " FROM " + database + ".InvoiceDetail " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryInvoiceDetail ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updateInvoiceMaster(Connection conn, String database, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".InvoiceMaster " +
                    " SET PatientMRN ='" + newMrn + "' " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryInvoiceMaster(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".InvoiceMaster_MRN_History " +
                    " (Id ,PatientMRN ,InvoiceNo ,TotalAmount ,PaidAmount ,BalAmount ,Paid ,PaymentDateTime ,InvoiceCreatedBy ,CreatedDate ,Status ,InstallmentApplied ,refundFlag ," +
                    " RefundDateTime ,VoidFlag ,VoidDateTime ,CreatedBy ,VisitID ,Discount) " +
                    " SELECT Id ,PatientMRN ,InvoiceNo ,TotalAmount ,PaidAmount ,BalAmount ,Paid ,PaymentDateTime ,InvoiceCreatedBy ,CreatedDate ,Status ,InstallmentApplied ,refundFlag ," +
                    " RefundDateTime ,VoidFlag ,VoidDateTime ,CreatedBy ,VisitID ,Discount " +
                    " FROM " + database + ".InvoiceMaster " +
                    " WHERE PatientMRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryInvoiceMaster ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updateAlerts(Connection conn, String database, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".Alerts " +
                    " SET MRN ='" + newMrn + "' " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updateAlerts ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryAlerts(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".Alerts_History " +
                    " (Id ,ClientIndex ,PatientRegId ,MRN ,Alerts ,CreatedDate ,CreatedBy ,Status)" +
                    " SELECT Id ,ClientIndex ,PatientRegId ,MRN ,Alerts ,CreatedDate ,CreatedBy ,Status " +
                    " FROM " + database + ".Alerts " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryAlerts ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updateNotes(Connection conn, String database, String oldMRN, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("UPDATE " + database + ".Notes " +
                    " SET MRN ='" + newMrn + "' " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updateNotes ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryNotes(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".Notes_History " +
                    "(Id ,ClientIndex ,PatientRegId ,MRN ,Notes ,CreatedDate ,CreatedBy ,Status)" +
                    " SELECT Id ,ClientIndex ,PatientRegId ,MRN ,Notes ,CreatedDate ,CreatedBy ,Status" +
                    " FROM " + database + ".Notes " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryNotes ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePatientVisit(Connection conn, String database, String oldMRN, String newMrn, String patientRegId) throws SQLException {
        boolean success;
        if (patientRegId != null) {
            success = updatePatientRegId(conn, database, newMrn, patientRegId) &&
                    updateMRN(conn, database, newMrn, oldMRN) &&
                    orderingVisitNumbers(conn, database, newMrn);

        } else {
            success = updateMRN(conn, database, newMrn, oldMRN);
        }
        return success;
    }

    private boolean orderingVisitNumbers(Connection conn, String database, String newMrn) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement ps1;
        int VisitNumber = 0;
        ResultSet rset = null;
        boolean success;
        try {
            ps = conn.prepareStatement("SELECT Id FROM " + database + ".PatientVisit " +
                    "WHERE MRN = '" + newMrn + "' ORDER BY DateofService ASC");
            rset = ps.executeQuery();
            while (rset.next()) {
                ps1 = conn.prepareStatement("UPDATE " + database + ".PatientVisit " +
                        " SET VisitNumber =  " + ++VisitNumber +
                        " WHERE MRN = '" + newMrn + "' AND Id =" + rset.getString(1));
                ps1.executeUpdate();
                ps1.close();
            }
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            rset.close();
            ps.close();
        }
        return success;
    }

    private boolean updateMRN(Connection conn, String database, String newMrn, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;

        try {
            ps = conn.prepareStatement("UPDATE " + database + ".PatientVisit " +
                    " SET MRN ='" + newMrn + "'  " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePatientRegId(Connection conn, String database, String newMrn, String patientRegId) throws SQLException {
        PreparedStatement ps = null;
        boolean success;

        try {
            ps = conn.prepareStatement("UPDATE " + database + ".PatientVisit " +
                    " SET PatientRegId ='" + patientRegId + "' " +
                    " WHERE MRN = " + newMrn);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryPatientVisit(Connection conn, String database, String oldMRN) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ps = conn.prepareStatement("INSERT INTO " + database + ".PatientVisit_History " +
                    "(Id ,MRN ,PatientRegId ,ReasonVisit ,VisitNumber ,DoctorId ,DateofService ,CreatedDate ,CreatedBy)" +
                    " SELECT Id ,MRN ,PatientRegId ,ReasonVisit ,VisitNumber ,DoctorId ,DateofService ,CreatedDate ,CreatedBy " +
                    " FROM " + database + ".PatientVisit " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryPatientVisit ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean updatePatientReg(Connection conn, String database, String oldMRN, String newMrn, boolean flag) throws SQLException {
        PreparedStatement ps = null;
        boolean success;
        try {
            ////Flag = true if both MRN exist in a system
            if (flag) {
                ps = conn.prepareStatement("UPDATE " + database + ".PatientReg " +
                        " SET Status = 1 " +
                        " WHERE MRN = " + newMrn);
                ps.executeUpdate();
                ps.close();
            }
            ps = conn.prepareStatement("UPDATE " + database + ".PatientReg " +
                    " SET MRN ='" + newMrn + "' " +
                    " WHERE MRN = " + oldMRN);
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("updatePatientReg ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            ps.close();
        }
        return success;
    }

    private boolean insertHistoryPatientReg(Connection conn, String database, String oldMRN) throws SQLException {
        boolean success;
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO " + database + ".PatientReg_History " +
                "(ID ,ClientIndex ,FirstName ,LastName ,MiddleInitial ,DOB ,Age ,Gender ,Email ,PhNumber ,Address ,City ,State ,Country ,ZipCode ,SSN ,Occupation ,Employer ,EmpContact ," +
                "PriCarePhy ,ReasonVisit ,SelfPayChk ,CreatedDate ,Title ,MaritalStatus ,CreatedBy ,MRN ,COVIDStatus ,Status ,DoctorsName ,sync ,DateofService ,ExtendedMRN ,Ethnicity ," +
                "County ,Address2 ,StreetAddress2 ,EnterBy ,EnterType ,EnterIP ,Race ,RegisterFrom ,ViewDate ,ReasonVisitOthers ,EditBy ,Edittime) " +

                " SELECT ID ,ClientIndex ,FirstName ,LastName ,MiddleInitial ,DOB ,Age ,Gender ,Email ,PhNumber ,Address ,City ,State ,Country ,ZipCode ,SSN ,Occupation ,Employer ,EmpContact ," +
                "PriCarePhy ,ReasonVisit ,SelfPayChk ,CreatedDate ,Title ,MaritalStatus ,CreatedBy ,MRN ,COVIDStatus ,Status ,DoctorsName ,sync ,DateofService ,ExtendedMRN ,Ethnicity " +
                ",County ,Address2 ,StreetAddress2 ,EnterBy ,EnterType ,EnterIP ,Race ,RegisterFrom ,ViewDate ,ReasonVisitOthers ,EditBy ,Edittime" +
                " FROM " + database + ".PatientReg " +
                " WHERE MRN = " + oldMRN)) {
            ps.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.out.println("insertHistoryPatientReg ERROR -> " + e.getMessage());
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private boolean MrnExist(Connection conn, String Database, String _MRN) throws SQLException {
        boolean MrnExist = false;

        try (PreparedStatement ps = conn.prepareStatement(
                "Select MRN from " + Database + ".PatientReg where MRN =" + _MRN);
             ResultSet rset = ps.executeQuery()) {
            //if new mrn exist already
            MrnExist = rset.next();
        } catch (SQLException e) {
            System.out.println("MrnExist ERROR -> " + e.getMessage());
            e.printStackTrace();
        }

        return MrnExist;
    }

    private boolean isLast(Connection conn, String Database, String _MRN) throws SQLException {
        boolean isLast = false;

        try (PreparedStatement ps = conn.prepareStatement(
                "Select MRN from " + Database + ".PatientReg ORDER BY Id DESC LIMIT 1"); ResultSet rset = ps.executeQuery()) {
            if (rset.next()) {
                isLast = rset.getString(1).equals(_MRN);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isLast;
    }

}