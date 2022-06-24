//
// Decompiled by Procyon v0.5.36
//

package md;

import DAL.Payments;
import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;

@SuppressWarnings("Duplicates")
public class LabInvoice extends HttpServlet {
    public ServletContext context = null;
    private Connection conn = null;

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

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
//            out.println("OUTSIDE SWITCH");
//            out.println("ActionID -> "+ActionID);
            switch (ActionID) {
                case "GETINPUT":
                    GETINPUT(request, out, conn, context);
                    break;
                case "GetCost":
                    GetCost(request, out, conn, context, DatabaseName, helper);
                    break;
                case "SaveInvoice":
                    SaveInvoice(request, out, conn, context, UserId, DatabaseName, helper);
                    break;
                case "CollectPayment":
                    CollectPayment(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "CollectPayment_View":
                    CollectPayment_View(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "PayNow":
                    PayNow(request, response, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, payments);
                    break;
                case "InvoicePdf":
                    InvoicePdf(request, response, out, conn, UserId, DatabaseName, FacilityIndex, context, helper);
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

    private void GETINPUT(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer LabTestsList = new StringBuffer();
        try {

            Query = "SELECT Id, CONCAT('(',CATEGORY, ') ', TEST) FROM oe.MedicalTests order by Id desc";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            LabTestsList.append("<option class=Inner value='' selected disabled>Select TEST </option>");
            while (rset.next())
                LabTestsList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("LabTestsList", String.valueOf(LabTestsList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/LabInvoice.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void GetCost(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Cost = "";

        try {
            int TestID = Integer.parseInt(request.getParameter("TestID").trim());
            Query = "Select RATE from oe.MedicalTests where Id = " + TestID;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                Cost = rset.getString(1);
            rset.close();
            stmt.close();
            out.println(Cost);
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabInvoice ** (GetCost)", servletContext, ex, "LabInvoice", "GetCost", conn);
            Services.DumException("GetCost", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#010");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void SaveInvoice(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        double TotalAmount = 0.0D;
        int i = 0;
        int j = 0;
        int k = 0;
        String InvoiceNo = "";
        int InvoiceMasterId = 0;
        String PatientString = request.getParameter("PatientString").trim();
        String PatientMRN = request.getParameter("PatientMRN").trim();
        try {
            String[] myInfo1 = new String[0];
            myInfo1 = PatientString.split("\\~");
            int RowCount = Integer.parseInt(myInfo1[1].substring(myInfo1[1].indexOf("=") + 1));
            String[][] InvoiceTableInput = new String[RowCount][5];
            for (i = j = k = 0, i = 2; i < myInfo1.length; i++) {
                if (myInfo1[i].length() > 0) {
                    InvoiceTableInput[k][j] = myInfo1[i].substring(myInfo1[i].indexOf("=") + 1);
                    if (++j > 4) {
                        j = 0;
                        k++;
                    }
                }
            }
            for (i = 0; i < RowCount; i++)
                TotalAmount += Double.parseDouble(InvoiceTableInput[i][2]);
            try {
                Query = "SELECT IFNULL(MAX(Id),0) + 1, DATE_FORMAT(now(),'%y-%m-%d') FROM " + Database + ".InvoiceMaster_LAB";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InvoiceNo = "Inv_" + rset.getString(2) + "_" + rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in LabInvoice ** (SaveInvoice -- 01)", servletContext, e, "LabInvoice", "SaveInvoice -- 01", conn);
                Services.DumException("SaveInvoice -- 01", "LabInvoice ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#011");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO " + Database + ".InvoiceMaster_LAB (PatientMRN,InvoiceNo,TotalAmount ,PaidAmount,Paid,PaymentDateTime," +
                                "InvoiceCreatedBy ,CreatedDate, Status, BalAmount,InstallmentApplied) " +
                                "VALUES (?,?,?,?,?,?,?,now(),0,?,0) ");
                MainReceipt.setString(1, PatientMRN);
                MainReceipt.setString(2, InvoiceNo);
                MainReceipt.setDouble(3, TotalAmount);
                MainReceipt.setDouble(4, 0.0D);
                MainReceipt.setInt(5, 0);
                MainReceipt.setString(6, "0000-00-00");
                MainReceipt.setString(7, UserId);
                MainReceipt.setDouble(8, TotalAmount);
                MainReceipt.executeUpdate();
                MainReceipt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in LabInvoice ** (SaveInvoice -- 02)", servletContext, e, "LabInvoice", "SaveInvoice -- 02", conn);
                Services.DumException("SaveInvoice -- 02", "LabInvoice ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#012");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                Query = "SELECT max(Id) FROM " + Database + ".InvoiceMaster_LAB";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InvoiceMasterId = rset.getInt(1);
                rset.close();
                stmt.close();
            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in LabInvoice ** (SaveInvoice -- 03)", servletContext, e, "LabInvoice", "SaveInvoice -- 03", conn);
                Services.DumException("SaveInvoice -- 03", "LabInvoice ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#013");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            try {
                System.out.println("<======================== Sending Request =====================>");
                String status = this.SendRequest(request, out, conn, context, UserId, Database, helper, PatientMRN);
                if (status.equals("OK")) {

                    for (i = 0; i < RowCount; i++) {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "INSERT INTO " + Database + ".InvoiceDetail_LAB (InvoiceMasterId,PatientMRN,InvoiceNo ,TestID,CostPerTest," +
                                        "Quantity,CreatedDate ,CreatedBy) VALUES (?,?,?,?,?,?,now(),?) ");
                        MainReceipt.setInt(1, InvoiceMasterId);
                        MainReceipt.setString(2, InvoiceTableInput[i][0]);
                        MainReceipt.setString(3, InvoiceNo);
                        MainReceipt.setString(4, InvoiceTableInput[i][4]);
                        MainReceipt.setDouble(5, Double.parseDouble(InvoiceTableInput[i][2]));
                        MainReceipt.setInt(6, Integer.parseInt(InvoiceTableInput[i][3]));
                        MainReceipt.setString(7, UserId);
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    }
                    out.println("1|" + PatientMRN + "|" + InvoiceNo);
                    return;
                } else if (status.equals("unauthorized")) {
                    out.println("Your Request's Status is " + status + "\n Please Contact System Administrator");
                    return;
                } else {
                    out.println(status);
                    return;
                }


            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in LabInvoice ** (SaveInvoice -- 04)", servletContext, e, "LabInvoice", "SaveInvoice -- 04", conn);
                Services.DumException("SaveInvoice -- 04", "LabInvoice ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#014");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }

        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabInvoice ** (SaveInvoice -- 05)", servletContext, ex, "LabInvoice", "SaveInvoice -- 05", conn);
            Services.DumException("SaveInvoice -- 05", "LabInvoice ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatient");
            Parser.SetField("ActionID", "CreateInvoiceInput");
            Parser.SetField("Message", "MES#015");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            out.println("Error: " + var11.getMessage());
            String str = "";
            for (i = 0; i < (var11.getStackTrace()).length; i++)
                str = str + var11.getStackTrace()[i] + "<br>";
            out.println(str);
            out.flush();
            out.close();*/
        }
    }

    private void InvoicePdf(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, String UserId, String Database, int ClientId, ServletContext servletContext, UtilityHelper helper) throws FileNotFoundException {
        String PatientMRN = request.getParameter("PatientMRN");
        String InvoiceNo = request.getParameter("InvoiceNo");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientName = "";
        String PatientName = "";
        String DOB = "";
        String DOBForAge = "";
        String Age = "";
        String Sex = "";
        String DOS = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        String InvoiceCreatedDate = "";
        String PayMethod = "";
        int Sno = 0;
        String DirectoryName = "";
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        try {
            Query = "Select DirectoryName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                DirectoryName = rset.getString(1);
            rset.close();
            stmt.close();
            Font MainHeading = new Font(2, 12.0F, 1, new Color(0, 0, 0));
            Font normfont = new Font(2, 8.0F, 0, new Color(0, 0, 0));
            Font normfont2 = new Font(2, 10.0F, 0, new Color(0, 0, 0));
            Font normfont3 = new Font(2, 12.0F, 0, new Color(0, 0, 0));
            Font UnderLine = new Font(2, 12.0F, 4, new Color(0, 0, 0));
            try {
                Query = "SELECT IFNULL(InvoiceNo,''), IFNULL(TotalAmount,''), IFNULL(PaidAmount,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y'),'') " +
                        "FROM " + Database + ".InvoiceMaster_LAB where PatientMRN = '" + PatientMRN + "' AND InvoiceNo='" + InvoiceNo + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    TotalAmount = rset.getDouble(2);
                    PaidAmount = rset.getDouble(3);
                    InvoiceCreatedDate = rset.getString(4);
                }
                rset.close();
                stmt.close();

                Query = " Select CASE WHEN PayMethod = 1 THEN 'Credit Card' WHEN PayMethod = 2 THEN 'Cash' WHEN PayMethod = 3 THEN 'BOLT' WHEN PayMethod = 4 THEN 'Ingenico' ELSE '' END " +
                        " from " + Database + ".PaymentReceiptInfo where PatientMRN = '" + PatientMRN + "' and InvoiceNo = '" + InvoiceNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    PayMethod = rset.getString(1);
                }
                rset.close();
                stmt.close();

            } catch (Exception e) {
                helper.SendEmailWithAttachment("Error in LabInvoice ** (InvoicePdf -- 01)", servletContext, e, "LabInvoice", "InvoicePdf -- 01", conn);
                Services.DumException("InvoicePdf -- 01", "LabInvoice ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "LabInvoice");
                Parser.SetField("ActionID", "GETINPUT");
                Parser.SetField("Message", "MES#016");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            Document document = new Document(PageSize.A4, 0.0F, 0.0F, 70.0F, 30.0F);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            PdfWriter.getInstance(document, new FileOutputStream("/sftpdrive/AdmissionBundlePdf/LAB_Invoices/" + DirectoryName + "/" + InvoiceNo + "_" + PatientMRN + ".pdf"));
            document.addAuthor(DirectoryName);
            document.addSubject("Customer Lab Invoice");
            document.addCreationDate();
            Paragraph p = new Paragraph();
            com.lowagie.text.Image jpeg = null;
            if (Database.equals("oe_2")) {
                jpeg = com.lowagie.text.Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/orange/images/logorange.jpg");
            } else {
                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/images_/jpg/" + ClientId + ".jpg");
//                jpeg = Image.getInstance("/sftpdrive/opt/apache-tomcat-8.5.61/webapps/md/images_/jpg/9.jpg");
            }
            if (jpeg != null) {
                jpeg.setAlignment(3);
                jpeg.setAbsolutePosition(210.0F, 730.0F);
                jpeg.scaleToFit(1200.0F, 95.0F);
                p.add(jpeg);
            }
            HeaderFooter header = new HeaderFooter((Phrase) p, false);
            header.setBorder(0);
            header.setAlignment(1);
            document.setHeader(header);
            document.open();
            document.add((Element) new Paragraph("\n"));
            document.add((Element) new Paragraph("\n"));
            try {
                Query = " Select CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.MiddleInitial,''), ' ', IFNULL(a.LastName,'')), IFNULL(b.name,''), IFNULL(a.Gender,''), " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%m/%d/%Y'),''), IFNULL(a.Age,''),  IFNULL(DATE_FORMAT(a.CreatedDate,'%m/%d/%Y'),'') as DOS, " +
                        "IFNULL(DATE_FORMAT(a.DOB,'%Y-%m-%d'),'')" +
                        "from " + Database + ".PatientReg a " +
                        "LEFT JOIN oe.clients b on a.ClientIndex = b.Id  where a.MRN = '" + PatientMRN + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientName = rset.getString(1);
                    ClientName = rset.getString(2);
                    Sex = rset.getString(3);
                    DOB = rset.getString(4);
                    Age = rset.getString(5);
                    DOS = rset.getString(6);
                    DOBForAge = rset.getString(7);
                }
                rset.close();
                stmt.close();

                if (!DOB.equals("")) {
                    Age = String.valueOf(getAge(LocalDate.parse(DOBForAge)));
                } else {
                    Age = "0";
                }

            } catch (Exception e2) {
                helper.SendEmailWithAttachment("Error in RegisteredPatients ** (InvoicePdf -- 02)", servletContext, e2, "RegisteredPatients", "InvoicePdf -- 02", conn);
                Services.DumException("InvoicePdf -- 02", "RegisteredPatients ", request, e2);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "RegisteredPatient");
                Parser.SetField("ActionID", "CreateInvoiceInput");
                Parser.SetField("Message", "MES#017");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            }
            Table datatable1 = new Table(6);
            datatable1.setWidth(100.0F);
            int[] widths1 = {5, 15, 25, 25, 25, 5};
            datatable1.setWidths(widths1);
            datatable1.setBorder(0);
            datatable1.setCellpadding(1.0F);
            datatable1.setCellspacing(0.0F);
            datatable1.setDefaultCellBorder(0);
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("INVOICE DATE: ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(InvoiceCreatedDate, UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(13);
            datatable1.addCell((Phrase) new Paragraph(PatientName, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("INVOICE NO: ", normfont2));
            datatable1.addCell((Phrase) new Paragraph(InvoiceNo, UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell((Phrase) new Paragraph(ClientName + "         Sex:" + Sex, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(12);
            datatable1.addCell((Phrase) new Paragraph("DOB: " + DOB + "         Age: " + Age, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultColspan(1);
            datatable1.setDefaultHorizontalAlignment(0);
            datatable1.addCell((Phrase) new Paragraph(" ", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.addCell((Phrase) new Paragraph("", UnderLine));
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            datatable1.setDefaultCellBorder(14);
            datatable1.addCell((Phrase) new Paragraph("MRN: " + PatientMRN + "         DOS: " + DOS, normfont));
            datatable1.setDefaultCellBorder(0);
            datatable1.addCell((Phrase) new Paragraph("", normfont2));
            document.add((Element) datatable1);
            Table datatable2 = new Table(5);
            datatable2.setWidth(100.0F);
            int[] widths2 = {10, 15, 5, 65, 5};
            datatable2.setWidths(widths2);
            datatable2.setBorder(0);
            datatable2.setCellpadding(1.0F);
            datatable2.setCellspacing(0.0F);
            datatable2.setDefaultCellBorder(0);
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Name: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(PatientName, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Age: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(Age, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Date of Birth: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(DOB, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Sex: ", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(Sex, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("Payment Method:", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph(PayMethod, normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.setDefaultColspan(1);
            datatable2.setDefaultHorizontalAlignment(0);
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            datatable2.addCell((Phrase) new Paragraph("", normfont3));
            document.add((Element) datatable2);
            Table datatable3 = new Table(7);
            datatable3.setWidth(100.0F);
            int[] widths3 = {5, 5, 50, 10, 10, 20, 5};
            datatable3.setWidths(widths3);
            datatable3.setBorder(0);
            datatable3.setCellpadding(1.0F);
            datatable3.setCellspacing(0.0F);
            datatable3.setDefaultCellBorder(0);
            datatable3.setDefaultColspan(1);
            datatable3.setDefaultHorizontalAlignment(0);
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(15);
            datatable3.addCell((Phrase) new Paragraph(" # ", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph(" Test Name ", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("Cost Per Test", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("QTY", MainHeading));
            datatable3.setDefaultCellBorder(11);
            datatable3.addCell((Phrase) new Paragraph("Amount", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            Query = "Select CONCAT('(',b.CATEGORY,')', ' ', b.TEST), b.RATE, a.Quantity " +
                    "from " + Database + ".InvoiceDetail_LAB a  " +
                    "LEFT JOIN oe.MedicalTests b on a.Id = b.Id " +
                    "where PatientMRN = " + PatientMRN + " and ltrim(rtrim(a.InvoiceNo)) = ltrim(rtrim('" + InvoiceNo.trim() + "'))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Sno++;
                datatable3.setDefaultColspan(1);
                datatable3.setDefaultHorizontalAlignment(0);
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph(" ", normfont3));
                datatable3.setDefaultCellBorder(12);
                datatable3.addCell((Phrase) new Paragraph(String.valueOf(Sno), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(1), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(2), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(rset.getString(3), normfont3));
                datatable3.setDefaultCellBorder(8);
                datatable3.addCell((Phrase) new Paragraph(String.valueOf(rset.getDouble(2) * rset.getDouble(3)), normfont3));
                datatable3.setDefaultCellBorder(0);
                datatable3.addCell((Phrase) new Paragraph("", normfont3));
            }
            rset.close();
            stmt.close();
            datatable3.setDefaultColspan(1);
            datatable3.setDefaultHorizontalAlignment(0);
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable3.setDefaultCellBorder(14);
            datatable3.addCell((Phrase) new Paragraph("", normfont2));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(10);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            datatable3.setDefaultCellBorder(0);
            datatable3.addCell((Phrase) new Paragraph("", MainHeading));
            document.add((Element) datatable3);
            document.add((Element) new Paragraph("\n"));
            Table datatable4 = new Table(7);
            datatable4.setWidth(100.0F);
            int[] widths4 = {10, 5, 40, 0, 20, 20, 5};
            datatable4.setWidths(widths4);
            datatable4.setBorder(0);
            datatable4.setCellpadding(1.0F);
            datatable4.setCellspacing(0.0F);
            datatable4.setDefaultCellBorder(0);
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Total ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(TotalAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Paid Amount ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(PaidAmount), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultColspan(1);
            datatable4.setDefaultHorizontalAlignment(0);
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph(" ", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("Balance ", MainHeading));
            datatable4.setDefaultCellBorder(15);
            datatable4.addCell((Phrase) new Paragraph(String.valueOf(numFormat.format(TotalAmount - PaidAmount)), normfont3));
            datatable4.setDefaultCellBorder(0);
            datatable4.addCell((Phrase) new Paragraph("", MainHeading));
            document.add((Element) datatable4);
            document.add((Element) new Paragraph("\n"));
            Table datatable5 = new Table(2);
            datatable5.setWidth(100.0F);
            int[] widths5 = {10, 90};
            datatable5.setWidths(widths5);
            datatable5.setBorder(0);
            datatable5.setCellpadding(1.0F);
            datatable5.setCellspacing(0.0F);
            datatable5.setDefaultCellBorder(0);
            datatable5.setDefaultColspan(1);
            datatable5.setDefaultHorizontalAlignment(0);
            datatable5.addCell((Phrase) new Paragraph(" ", normfont3));
            datatable5.addCell((Phrase) new Paragraph("Patient Signature: ___________________", normfont3));
            document.add((Element) datatable5);
            document.close();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Progma", "no-cache");
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            ServletOutputStream out2 = response.getOutputStream();
            baos.writeTo((OutputStream) out2);
            out2.flush();
            try {
                conn.close();
            } catch (Exception ex) {
                out.println(ex.getMessage());
            }
        } catch (Exception e3) {
//            helper.SendEmailWithAttachment("Error in LabInvoice ** (InvoicePdf -- 03)", servletContext, e3, "LabInvoice", "InvoicePdf -- 03", conn);
//            Services.DumException("InvoicePdf -- 03", "RegisteredPatients ", request, e3);
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("FormName", "LabInvoice");
//            Parser.SetField("ActionID", "GETINPUT");
//            Parser.SetField("Message", "MES#018");
//            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.println(e3.getMessage());
            String str = "";
            for (int i = 0; i < (e3.getStackTrace()).length; i++)
                str = str + e3.getStackTrace()[i] + "<br>";
            out.println(str);
        }
    }

    void CollectPayment(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder PatientInvoiceList = new StringBuilder();
        try {
            Query = "SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "DATE_FORMAT(b.DOB,'%m/%d/%Y')  " +
                    "FROM " + Database + ".InvoiceMaster_LAB a   " +
                    "LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                    "WHERE b.Status = 0 AND a.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next())
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ") (" + rset.getString(3) + ")</option>");
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice_LAB.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in LabInvoice ** (CollectPayment -- 01)", servletContext, ex, "LabInvoice", "CollectPayment -- 01", conn);
            Services.DumException("CollectPayment", "LabInvoice ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#019");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CollectPayment)", servletContext, var11, "RegisteredPatients", "CollectPayment", conn);
            Services.DumException("RegisteredPatients", "CollectPayment", request, var11, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    void CollectPayment_View(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmt2 = null;
        ResultSet rset2 = null;
        String Query = "";
        String Query2 = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        int PatientRegId = 0;
        int InstallmentPlanFound = 0;
        String PatientName = "";
        String PatientInvoiceMRN = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuilder CDRList = new StringBuilder();
        StringBuilder PatientInvoiceList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        try {
            PatientInvoiceMRN = request.getParameter("PatientInvoice").trim();
            Query = "SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    "DATE_FORMAT(b.DOB,'%m/%d/%Y')  " +
                    "FROM " + Database + ".InvoiceMaster_LAB a   " +
                    "LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN  " +
                    "WHERE b.Status = 0 AND a.Status = 0 GROUP BY a.PatientMRN";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(1).equals(PatientInvoiceMRN)) {
                    PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
                    continue;
                }
                PatientInvoiceList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + " (" + rset.getString(1) + ")</option>");
            }
            rset.close();
            stmt.close();

            Query = " SELECT a.PatientMRN, CONCAT(IFNULL(b.Title,'-'),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')),DATE_FORMAT(b.DOB,'%d-%m-%Y')," +
                    "IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS,  " +
                    "a.InvoiceNo, DATE_FORMAT(a.CreatedDate, '%m/%d/%Y %T'), a.TotalAmount, a.PaidAmount, a.BalAmount, a.Id   " +
                    " FROM " + Database + ".InvoiceMaster_LAB a   " +
                    " LEFT JOIN " + Database + ".PatientReg b ON a.PatientMRN = b.MRN    " +
                    " WHERE a.PatientMRN = '" + PatientInvoiceMRN + "' AND a.Status = 0 AND b.Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
//                Query2 = "Select COUNT(*) FROM " + Database + ".InstallmentPlan WHERE MRN = '" + rset.getString(1) + "' AND " +
//                        " InvoiceNo = '" + rset.getString(5) + "'";
//                stmt2 = conn.createStatement();
//                rset2 = stmt2.executeQuery(Query2);
//                if (rset2.next())
//                    InstallmentPlanFound = rset2.getInt(1);
//                rset2.close();
//                stmt2.close();
                TotalAmount = rset.getDouble(7);
                PaidAmount = rset.getDouble(7) - rset.getDouble(9);
                PatientName = rset.getString(2) + "(" + rset.getString(1) + ")";
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");
                CDRList.append("<td align=left width=15%>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(TotalAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(PaidAmount) + "</td>\n");
                CDRList.append("<td align=left>" + numFormat.format(rset.getDouble(9)) + "</td>\n");
                CDRList.append("<td align=left width=35%> ");
                CDRList.append("<a class='btn-sm btn btn-primary' href=/md/md.LabInvoice?ActionID=InvoicePdf&PatientMRN=" + rset.getString(1) + "&InvoiceNo=" + rset.getString(5) + ">View</a>\n");
                CDRList.append("<a class='btn-sm btn btn-success' href=/md/md.LabInvoice?ActionID=PayNow&InvoiceNo=" + rset.getString(5) + " >PayNow</a>\n");
//                if (InstallmentPlanFound > 0) {
//                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + " >Installments (Plan Applied) </a>\n");
//                } else {
//                    CDRList.append("<a class='btn-sm btn btn-warning' href=/md/md.InstallmentPlan?ActionID=GetInput&InvoiceNo=" + rset.getString(5) + " >Installments</a>\n");
//                }
                CDRList.append("<a class='btn-sm btn btn-danger' href=/md/md.LabInvoice?ActionID=DeActiveInvoice&InvoiceMasterID=" + rset.getString(10) + "&InvoiceNo=" + rset.getString(5).trim() + " >De-Activate</a>\n");
                CDRList.append("</td>");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("PatientInvoiceList", String.valueOf(PatientInvoiceList));
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ShowInvoice_LAB.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (CollectPayment_View -- 01)", servletContext, ex, "RegisteredPatients", "CollectPayment_View -- 01", conn);
            Services.DumException("CollectPayment_View", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#020");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void PayNow(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, Payments payments) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String InvoiceNo = request.getParameter("InvoiceNo").trim();
        String PatientMRN = "";
        String PatientName = "";
        double TotalAmount = 0.0D;
        double PaidAmount = 0.0D;
        double BalAmount = 0.0D;
        double AmountToPay = 0.0D;
        String DOS = "";
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        StringBuilder DeviceList = new StringBuilder();
        DecimalFormat numFormat = new DecimalFormat("#,###,###.00");
        int SNo = 1;
        int InstallmentPlanFound = 0;
        StringBuilder installmentPlan = new StringBuilder();
        try {
            Query = "SELECT a.PatientMRN,  CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,''))," +
                    "a.TotalAmount,a.PaidAmount,a.BalAmount,IFNULL(DATE_FORMAT(b.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')) AS DOS " +
                    " FROM " + Database + ".InvoiceMaster_LAB a  " +
                    " LEFT JOIN " + Database + ".PatientReg b on a.PatientMRN = b.MRN  " +
                    "WHERE a.InvoiceNo = '" + InvoiceNo + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientMRN = rset.getString(1);
                PatientName = rset.getString(2);
                TotalAmount = rset.getDouble(3);
                PaidAmount = rset.getDouble(3) - rset.getDouble(5);
                BalAmount = rset.getDouble(5);
                DOS = rset.getString(6).trim();
            }
            rset.close();
            stmt.close();

            if (BalAmount == 0.0D) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Message", "Payment has already been paid!");
                parsehtm.SetField("FormName", "RegisteredPatients");
                parsehtm.SetField("ActionID", "CollectPayment");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }
/*            StringBuilder checkAmountDisplay = new StringBuilder();
            double checkAmount = 0.0;
            int checkInfoCHK = 0;
            Query = "SELECT COUNT(*),Amount FROM " + Database + ".CheckInfo WHERE InvoiceNo =  '" + InvoiceNo + "' AND isPaid = 0 AND Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                checkInfoCHK = rset.getInt(1);
                checkAmount += rset.getDouble(2);
            }
            rset.close();
            stmt.close();

            if (checkInfoCHK > 0) {
                checkAmountDisplay.append("<tr>");
                checkAmountDisplay.append("<td>Check</td>");
                checkAmountDisplay.append("<td class=\"text-right font-weight-700\"><span class=\"text-warning mr-15\"><b>($" + checkAmount + ")</b></span>");
                checkAmountDisplay.append("</td>");
                checkAmountDisplay.append("</tr>");
                //checkAmountDisplay.append("<p><span class='text-warning'> <b>(" + checkAmount + ")</b></span></p>");
            }*/


//            Query = "SELECT COUNT(*) FROM " + Database + ".InstallmentPlan " +
//                    "WHERE MRN = '" + PatientMRN + "' AND InvoiceNo = '" + InvoiceNo + "' AND status = 0";
//            stmt = conn.createStatement();
//            rset = this.stmt.executeQuery(this.Query);
//            if (rset.next())
//                InstallmentPlanFound = this.rset.getInt(1);
//            rset.close();
//            stmt.close();
//
//            if (InstallmentPlanFound > 0) {
//                installmentPlan.append("<a class='btn-sm btn btn-primary' data-toggle=\"modal\" data-target=\"#installmentModal\">View</a>");
//                Query = "SELECT IFNULL(MRN,''), IFNULL(InvoiceNo,''), IFNULL(PaymentAmount,0), IFNULL(DATE_FORMAT(PaymentDate,'%m/%d/%Y'),''),  CASE WHEN PAID = 0 THEN 'Pending' WHEN Paid = 1 THEN 'Paid' ELSE 'Pending' END FROM " + Database + ".InstallmentPlan WHERE MRN = '" + PatientMRN + "' AND  InvoiceNo = '" + InvoiceNo + "' AND status = 0";
//                stmt = conn.createStatement();
//                rset = this.stmt.executeQuery(this.Query);
//                while (this.rset.next()) {
//                    CDRList.append("<tr><td align=left>" + SNo + "</td>\n");
//                    CDRList.append("<td align=left>" + this.rset.getString(1) + "</td>\n");
//                    CDRList.append("<td align=left>" + PatientName + "</td>\n");
//                    CDRList.append("<td align=left>" + this.rset.getString(2) + "</td>\n");
//                    CDRList.append("<td align=left>" + this.rset.getString(3) + "</td>\n");
//                    CDRList.append("<td align=left>" + this.rset.getString(4) + "</td>\n");
//                    CDRList.append("<td align=left>" + this.rset.getString(5) + "</td>\n");
//                    SNo++;
//                }
//                rset.close();
//                stmt.close();
//
//                Query = " Select IFNULL(PaymentAmount,0) from " + Database + ".InstallmentPlan " +
//                        "where MRN = '" + PatientMRN + "' and  InvoiceNo = '" + InvoiceNo + "' and Paid = 0 limit 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next())
//                    AmountToPay = rset.getDouble(1);
//                rset.close();
//                stmt.close();
//            } else {
//                installmentPlan.append("No Installment Applied");
//            }


            DeviceList = payments.getDeviceList(request, conn, servletContext, 32);

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", String.valueOf(PatientName));
            Parser.SetField("PatientMRN", String.valueOf(PatientMRN));
            Parser.SetField("TotalAmount", String.valueOf(numFormat.format(TotalAmount)));
            Parser.SetField("PaidAmount", String.valueOf(numFormat.format(PaidAmount)));
            Parser.SetField("BalAmount", String.valueOf(numFormat.format(BalAmount)));
            Parser.SetField("InvoiceNo", String.valueOf(InvoiceNo));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("AmountToPay", String.valueOf(numFormat.format(AmountToPay)));
            Parser.SetField("InstallmentPlanFound", String.valueOf(InstallmentPlanFound));
            Parser.SetField("DeviceList", String.valueOf(DeviceList));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.SetField("DOS", DOS);
            Parser.SetField("installmentPlan", installmentPlan.toString());
            //Parser.SetField("checkAmountDisplay", checkAmountDisplay.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/PayNow_LAB.html");
        } catch (Exception ex) {
            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNow -- 01)", servletContext, ex, "RegisteredPatients", "PayNow -- 01", conn);
            Services.DumException("PayNow", "RegisteredPatients ", request, ex);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            Parser.SetField("Message", "MES#021");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
/*            helper.SendEmailWithAttachment("Error in RegisteredPatients ** (PayNow)", servletContext, var11, "RegisteredPatients", "PayNow", conn);
            Services.DumException("Registered Patients", "PayNow", request, var11, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "RegisteredPatients");
            Parser.SetField("ActionID", "CollectPayment");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }


    String SendRequest(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, UtilityHelper helper, String MRN) throws IOException {


        System.out.println("<==============Inside Send request=====================>");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FirstName = "";
        String LastName = "";
        String MiddleInitial = "";
        String PhNumber = "";
        String Email = "";
        String SelfPayChk = "";
        String InsuranceType = "";
        String DOB = "";
        String Gender = "";
        String SSN = "";
        String Address = "";
        String State = "";
        String City = "";
        String Country = "";
        String ZipCode = "";
        String County = "";
        String MaritalStaus = "";


        String username = "";
        String pwd = "";
        String labName = "";


        String Status = null;


        try {
            Query = "SELECT FirstName,LastName,MiddleInitial,PhNumber, Email,SelfPayChk,DATE_FORMAT(DOB,'%m/%d/%Y'),SSN,Gender,Address,State,City,Country,ZipCode,County,MaritalStatus from " + Database + ".PatientReg Where MRN='" + MRN + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FirstName = rset.getString(1);
                LastName = rset.getString(2);
                MiddleInitial = rset.getString(3);
                PhNumber = rset.getString(4);
                Email = rset.getString(5);
                SelfPayChk = rset.getString(6);
                DOB = rset.getString(7);
                SSN = rset.getString(8);
                Gender = rset.getString(9);
                Address = rset.getString(10);
                State = rset.getString(11);
                City = rset.getString(12);
                Country = rset.getString(13);
                ZipCode = rset.getString(14);
                County = rset.getString(15);
                MaritalStaus = rset.getString(16);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("PatientReg error +>  " + e.getMessage());
        }


        System.out.println("<==============Patient Details Fetched =====================>");

        if (SelfPayChk.equals("0")) {
            InsuranceType = "";
        } else {
            InsuranceType = "Primary";
        }


        try {
            Query = "SELECT username,pwd,labName from " + Database + ".LabCredentials where id=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                username = rset.getString(1);
                pwd = rset.getString(2);
                labName = rset.getString(3);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("LabCredentials error +>  " + e.getMessage());
        }

        System.out.println("<==============Lab Credentials Fetched =====================>");

        String LabOrderID = "";
        try {
            Query = "SELECT IFNULL(MAX(indexPtr),0) + 1, DATE_FORMAT(now(),'%y-%m-%d') FROM " + Database + ".LabOrderDetails";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                LabOrderID = "ORD_" + rset.getString(2) + "_" + rset.getInt(1);
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String jsonInputString = " {\n" +
                "            \"username\": \"" + username + "\",\n" +
                "            \"password\": \"" + pwd + "\",\n" +
                "            \"labName\": \"" + labName + "\",\n" +
                "            \"order\": {\n" +
                "                \"laborderid\": \"" + LabOrderID + "\",\n" +
                "                \"physician\": {\n" +
                "                    \"firstName\": \"" + FirstName + "\",\n" +
                "                    \"middleIntial\": \"" + MiddleInitial + "\",\n" +
                "                    \"lastName\": \"" + LastName + "\",\n" +
                "                    \"location\": \"Victoria ER\",\n" +
                "                    \"locationCode\": \"\",\n" +
                "                    \"labclient\": \"Victoria ER\",\n" +
                "                    \"npi\": \"\",\n" +
                "                    \"salutation\": \"\",\n" +
                "                    \"medicareNumber\": \"\",\n" +
                "                    \"medicaidNumber\": \"\",\n" +
                "                    \"upinNumber\": \"\",\n" +
                "                    \"stateLicense\": \"\",\n" +
                "                    \"email\": \"" + Email + "\",\n" +
                "                    \"contact\": \"" + PhNumber + "\",\n" +
                "                    \"medicalDegree\": \"\"\n" +
                "                },\n" +
                "                \"secondaryPhysician\": {\n" +
                "                    \"firstName\": \"\",\n" +
                "                    \"lastName\": \"\"\n" +
                "                },\n" +
                "                \"ccPhysician\": {\n" +
                "                    \"firstName\": \"\",\n" +
                "                    \"lastName\": \"\"\n" +
                "                },\n" +
                "                \"pathologist\": {\n" +
                "                    \"firstName\": null,\n" +
                "                    \"lastName\": null\n" +
                "                },\n" +
                "                \"caseType\": \"\",\n" +
                "                \"dateReceived\": \"\",\n" +
                "                \"dateCollected\": \"\",\n" +
                "                \"symptomaticDefinedByCDC\": \"Unknown\",\n" +
                "                \"testingCovidFristtime\": \"Unknown\",\n" +
                "                \"employedInHealthcare\": \"Unknown\",\n" +
                "                \"hospitalizedAtTimeofCollection\": \"Unknown\",\n" +
                "                \"inIcuAtTimeOfCollection\": \"Unknown\",\n" +
                "                \"residentInCongregateCare\": \"Unknown\",\n" +
                "                \"currentlyPregnant\": \"Unknown\",\n" +
                "                \"profiles\": [\n" +
                "                {\n" +
                "                    \"code\": \"\",\n" +
                "                    \"name\": \"SARS-CoV-2\"\n" +
                "            } // profile code only works for toxicology\n" +
                "            ],\n" +
                "        \"insuranceType\": \"" + InsuranceType + "\", //use either Primary/Secondary/Tertiary/Worksmen/Client Bill or if self pay leave empty\n" +
                "        \"drugs\": [\n" +
                "        {\n" +
                "            \"drugName\": \"\"\n" +
                "        }\n" +
                "        ],\n" +
                "        \"icd10Codes\": [\n" +
                "        {\n" +
                "            \"name\": \"\"\n" +
                "        }\n" +
                "        ],\n" +
                "        \"comments\": \"\",\n" +
                "        \"patient\": {\n" +
                "            \"firstname\": \"" + FirstName + "\",\n" +
                "            \"lastname\": \"" + LastName + "\",\n" +
                "            \"middleInitial\": \"" + MiddleInitial + "\",\n" +
                "            \"gender\": \"" + Gender + "\",\n" +
                "            \"dateOfBirth\": \"" + DOB + "\",\n" +
                "            \"medRecNo\": \"\",\n" +
                "            \"socialSecurityNumber\": \"" + SSN + "\",\n" +
                "            \"address\": {\n" +
                "                \"address1\": \"" + Address + "\",\n" +
                "                \"address2\": \"\",\n" +
                "                \"city\": \"" + City + "\",\n" +
                "                \"state\": \"" + State + "\",\n" +
                "                \"zip\": \"" + ZipCode + "\",\n" +
                "                \"race\": \"\",\n" +
                "                \"ethnicity\": \"\",\n" +
                "                \"county\": \"" + County + "\"\n" +
                "            },\n" +
                "            \"contactNumber\": \"" + PhNumber + "\",\n" +
                "            \"workNumber\": \"\",\n" +
                "            \"email\": \"" + Email + "\",\n" +
                "            \"maritalStatus\": \"" + MaritalStaus + "\",\n" +
                "            \"emergencyContactName\": \"\",\n" +
                "            \"emergencyContactNumber\": \"\",\n" +
                "            \"emergencyContactRelationship\": \"\",\n" +
                "            \"guarantor\": {\n" +
                "                \"firstName\": \"\",\n" +
                "                \"lastName\": \"\",\n" +
                "                \"middleIntial\": \"\",\n" +
                "                \"relationshipToPatient\": \"\",\n" +
                "                \"ssn\": \"\",\n" +
                "                \"address1\": \"\",\n" +
                "                \"address2\": \"\",\n" +
                "                \"city\": \"\",\n" +
                "                \"state\": \"\",\n" +
                "                \"zip\": \"\",\n" +
                "                \"contact\": \"\",\n" +
                "                \"email\": \"\",\n" +
                "                \"maritalStatus\": \"\"\n" +
                "            },\n" +
                "            \"insurances\": [\n" +
                "            {\n" +
                "                \"insuranceType\": \"\",\n" +
                "                \"insuranceCompanyCode\": \"\",\n" +
                "                \"insuranceCompanyName\": \"\",\n" +
                "                \"insuranceAddress1\": \"\",\n" +
                "                \"insuranceAddress2\": \"\",\n" +
                "                \"insuranceCity\": \"\",\n" +
                "                \"insuranceState\": \"\",\n" +
                "                \"insuranceZip\": \"\",\n" +
                "                \"insuranceCompanyPayerID\": \"\",\n" +
                "                \"insuranceCompanyAdjuster\": \"\",\n" +
                "                \"policyID\": \"\",\n" +
                "                \"groupName\": \"\",\n" +
                "                \"groupNumber\": \"\",\n" +
                "                    \"validityOfPolicy\": \"\", // mm/dd/yyyy format\n" +
                "                    \"insuredName\": \"\",\n" +
                "                    \"effectiveDate\": \"\",\n" +
                "                    \"insuredLastName\": \"\",\n" +
                "                    \"insuredFirstName\": \"\",\n" +
                "                    \"insuredMiddleInitial\": \"\",\n" +
                "                    \"insuredDateOfBirth\": \"\",\n" +
                "                    \"insuredAddress1\": \"\",\n" +
                "                    \"insuredAddress2\": \"\",\n" +
                "                    \"insuredCity\": \"\",\n" +
                "                    \"insuredState\": \"\",\n" +
                "                    \"insuredZip\": \"\",\n" +
                "                    \"insuredPriority\": \"\",\n" +
                "                    \"insuredPhoneNumber\": \"\",\n" +
                "                    \"insuredGender\": \"\",\n" +
                "                    \"relation\": \"\"\n" +
                "                }\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    }";

        System.out.println("<==============Request is in Processing =====================>");


        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            System.out.println("<==============Setting Headers =====================>");

            RequestBody body = RequestBody.create(mediaType, jsonInputString);//"{\r\n    \"username\": \"Victoria_API\",\r\n    \"password\": \"Welcome@123\",\r\n    \"labName\": \"easytox\",\r\n    \"order\": {\r\n        \"laborderid\": \"1234567812\",\r\n        \"physician\": {\r\n            \"firstName\": \"phys\",\r\n            \"middleIntial\": \"\",\r\n            \"lastName\": \"lname\",\r\n            \"location\": \"Victoria ER\",\r\n            \"locationCode\": \"\",\r\n            \"labclient\": \"Victoria ER\",\r\n            \"npi\": \"\",\r\n            \"salutation\": \"\",\r\n            \"medicareNumber\": \"\",\r\n            \"medicaidNumber\": \"\",\r\n            \"upinNumber\": \"\",\r\n            \"stateLicense\": \"\",\r\n            \"email\": \"\",\r\n            \"contact\": \"\",\r\n            \"medicalDegree\": \"\"\r\n        },\r\n        \"secondaryPhysician\": {\r\n            \"firstName\": \"\",\r\n            \"lastName\": \"\"\r\n        },\r\n        \"ccPhysician\": {\r\n            \"firstName\": \"\",\r\n            \"lastName\": \"\"\r\n        },\r\n        \"pathologist\": {\r\n            \"firstName\": null,\r\n            \"lastName\": null\r\n        },\r\n        \"caseType\": \"\",\r\n        \"dateReceived\": \"\",\r\n        \"dateCollected\": \"\",\r\n        \"molecularSampleType\": \"Nasal Swab\",\r\n        \"symptomaticDefinedByCDC\": \"Unknown\",\r\n        \"testingCovidFristtime\": \"Unknown\",\r\n        \"employedInHealthcare\": \"Unknown\",\r\n        \"hospitalizedAtTimeofCollection\": \"Unknown\",\r\n        \"inIcuAtTimeOfCollection\": \"Unknown\",\r\n        \"residentInCongregateCare\": \"Unknown\",\r\n        \"currentlyPregnant\": \"Unknown\",\r\n        \"profiles\": [\r\n            {\r\n                \"code\": \"\",\r\n                \"name\": \"SARS-CoV-2\"\r\n            } // profile code only works for toxicology\r\n        ],\r\n        \"insuranceType\": \"Primary\", //use either Primary/Secondary/Tertiary/Worksmen/Client Bill or if self pay leave empty\r\n        \"drugs\": [\r\n            {\r\n                \"drugName\": \"\"\r\n            }\r\n        ],\r\n        \"icd10Codes\": [\r\n            {\r\n                \"name\": \"\"\r\n            }\r\n        ],\r\n        \"comments\": \"\",\r\n        \"patient\": {\r\n            \"firstname\": \"patient\",\r\n            \"lastname\": \"test\",\r\n            \"middleInitial\": \"\",\r\n            \"gender\": \"Male\",\r\n            \"dateOfBirth\": \"29/12/2008\",\r\n            \"medRecNo\": \"\",\r\n            \"socialSecurityNumber\": \"\",\r\n            \"address\": {\r\n                \"address1\": \"address1\",\r\n                \"address2\": \"\",\r\n                \"city\": \"city\",\r\n                \"state\": \"state\",\r\n                \"zip\": \"12345\",\r\n                \"race\": \"White\",\r\n                \"ethnicity\": \"Not Hispanic or Latino\",\r\n                \"county\": \"test\"\r\n            },\r\n            \"contactNumber\": \"\",\r\n            \"workNumber\": \"\",\r\n            \"email\": \"\",\r\n            \"maritalStatus\": \"\",\r\n            \"emergencyContactName\": \"\",\r\n            \"emergencyContactNumber\": \"\",\r\n            \"emergencyContactRelationship\": \"\",\r\n            \"guarantor\": {\r\n                \"firstName\": \"\",\r\n                \"lastName\": \"\",\r\n                \"middleIntial\": \"\",\r\n                \"relationshipToPatient\": \"\",\r\n                \"ssn\": \"\",\r\n                \"address1\": \"\",\r\n                \"address2\": \"\",\r\n                \"city\": \"\",\r\n                \"state\": \"\",\r\n                \"zip\": \"\",\r\n                \"contact\": \"\",\r\n                \"email\": \"\",\r\n                \"maritalStatus\": \"\"\r\n            },\r\n            \"insurances\": [\r\n                {\r\n                    \"insuranceType\": \"Primary\",\r\n                    \"insuranceCompanyCode\": \"BCBS\",\r\n                    \"insuranceCompanyName\": \"Blue Cross Blue Sheild\",\r\n                    \"insuranceAddress1\": \"\",\r\n                    \"insuranceAddress2\": \"\",\r\n                    \"insuranceCity\": \"\",\r\n                    \"insuranceState\": \"\",\r\n                    \"insuranceZip\": \"\",\r\n                    \"insuranceCompanyPayerID\": \"\",\r\n                    \"insuranceCompanyAdjuster\": \"\",\r\n                    \"policyID\": \"\",\r\n                    \"groupName\": \"\",\r\n                    \"groupNumber\": \"\",\r\n                    \"validityOfPolicy\": \"\", // mm/dd/yyyy format\r\n                    \"insuredName\": \"\",\r\n                    \"effectiveDate\": \"\",\r\n                    \"insuredLastName\": \"\",\r\n                    \"insuredFirstName\": \"\",\r\n                    \"insuredMiddleInitial\": \"\",\r\n                    \"insuredDateOfBirth\": \"\",\r\n                    \"insuredAddress1\": \"\",\r\n                    \"insuredAddress2\": \"\",\r\n                    \"insuredCity\": \"\",\r\n                    \"insuredState\": \"\",\r\n                    \"insuredZip\": \"\",\r\n                    \"insuredPriority\": \"\",\r\n                    \"insuredPhoneNumber\": \"\",\r\n                    \"insuredGender\": \"\",\r\n                    \"relation\": \"\"\r\n                }\r\n            ]\r\n        }\r\n    }\r\n}\r\n");
            Request requestOKKIE = new Request.Builder()
                    .url("https://easytox.apeasycloud.com/easytox/orderFrom/createOrder")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();
            System.out.println("<==============Setting Headers Completed =====================>");

            Response response = client.newCall(requestOKKIE).execute();
            System.out.println("<==============REQUEST EXCECUTED =====================>");

            String jsonResponse = response.body().string();

            System.out.println("Request : " + jsonInputString);
            System.out.println("Response : " + jsonResponse);
            try {
                JSONObject obj = new JSONObject(jsonResponse);
                if (obj.has("status")) {
                    Status = obj.getString("status");
                    System.out.println("status : " + Status);
                    return Status;
                } else if (obj.has("orderNo")) {
                    String orderNo = obj.getString("orderNo");
                    String id = obj.getString("id");
                    String laborderid = obj.getString("laborderid");
                    String dateCollected = obj.getString("dateCollected");
                    String collectedTime = obj.getString("collectedTime");

                    System.out.println("orderNo : " + orderNo);
                    System.out.println("id : " + id);
                    System.out.println("laborderid : " + laborderid);
                    System.out.println("dateCollected : " + dateCollected);
                    System.out.println("collectedTime : " + collectedTime);


                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement("INSERT INTO " + Database + ".LabOrderDetails " +
                                "(IdFromApi ,OrderNo ,LabOrderID ,FirstName ,LastName ,DateCollected ,CollectedTime  ,JSONResponse,CreatedDate)" +
                                "VALUES (?,?,?,?,?,?,?,?,NOW())");
                        MainReceipt.setString(1, id);
                        MainReceipt.setString(2, orderNo);
                        MainReceipt.setString(3, laborderid);
                        MainReceipt.setString(4, FirstName);
                        MainReceipt.setString(5, LastName);
                        MainReceipt.setString(6, dateCollected);
                        MainReceipt.setString(7, collectedTime);
                        MainReceipt.setString(8, jsonResponse);
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Status = "OK";
                    return Status;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("JSON ERRRROR   +>>>>>> " + e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("<==============ERRORS =====================>" + e.getMessage());
        }

        return Status;
    }
}
