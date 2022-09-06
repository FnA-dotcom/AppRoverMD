

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class EditInvoice extends HttpServlet {
    private Connection conn = null;
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
        String ActionID = "";

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
            switch (ActionID) {
                case "AddDiscount":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Input", "Click on Search Old Patient Option", FacilityIndex);
                    this.AddDiscount(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetPatients":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Search Patients Visit Get Details", "Get Data for all Matching Old Patients ", FacilityIndex);
                    this.GetPatients(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;

            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest)", context, e, "PatientVisit", "handleRequest", conn);
            Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in PatientVisit ** (handleRequest -- SqlException)", context, e, "PatientVisit", "handleRequest", conn);
                Services.DumException("PatientVisit", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    private void AddDiscount(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        try {
            String InvoiceNo = request.getParameter("Invoice").trim();
            String Remarks = request.getParameter("Remarks").trim();
            String Discount = request.getParameter("Discount").trim();
            String MRN = request.getParameter("MRN").trim();
            String TotalAmt = request.getParameter("TotalAmt").trim();
            String BalAmt = request.getParameter("BalAmt").trim();


            PreparedStatement MainReceipt = null;
            try {
                MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".InvoiceDiscount (InvoiceNo,Remarks,Discount,MRN,CreatedDate,CreatedBy,TotalAmt)" +
                                " VALUE(?,?,?,?,NOW(),?,?)");

                MainReceipt.setString(1, InvoiceNo);
                MainReceipt.setString(2, Remarks);
                MainReceipt.setString(3, Discount);
                MainReceipt.setString(4, MRN);
                MainReceipt.setString(5, UserId);
                MainReceipt.setString(6, TotalAmt);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

//            double NewTotal = Double.valueOf(TotalAmt) - Double.valueOf(Discount);
            double NewBal = Double.valueOf(BalAmt) - Double.valueOf(Discount);

            try {
                MainReceipt = conn.prepareStatement("UPDATE  " + Database + ".InvoiceMaster  SET Discount = '" + Discount + "'  , BalAmount='" + NewBal + "'  WHERE InvoiceNo = '" + InvoiceNo + "'");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                MainReceipt = conn.prepareStatement("UPDATE  " + Database + ".PaymentReceiptInfo  SET Discount = '" + Discount + "' , BalAmount='" + NewBal + "'  WHERE InvoiceNo = '" + InvoiceNo + "'");
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            out.println("1");
        } catch (Exception e) {
            out.println(e.getMessage());
            helper.SendEmailWithAttachment("Error in AddDiscount ** (AddDiscount^^ MES#001)", servletContext, e, "AddDiscount", "AddDiscount", conn);
            Services.DumException("AddDiscount", "AddDiscount", request, e);
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
                    "WHERE status = 0 and CONCAT(FirstName,LastName,PhNumber,MRN,IFNULL(DATE_FORMAT(DOB,'%m-%d-%Y'),'')) like '%" + Patient + "%' ";
            PatientList.append("<select class=\"form-control select2\" id=\"PatientId\" name=\"PatientId\">");
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            PatientList.append("<option value=''> Please Select Below Patient </option>");
            while (rset.next())
                PatientList.append("<option value=" + rset.getInt(1) + ">" + rset.getString(2) + " | " + rset.getString(3) + " | " + rset.getString(4) + " | " + rset.getString(5) + " | " + rset.getString(6) + " | " + rset.getString(7) + " </option>");
            rset.close();
            stmt.close();
            PatientList.append("</select>");
            out.println(PatientList.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
