package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings("Duplicates")
public class RemoveCMDNacho extends HttpServlet {
    private Connection conn = null;

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
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        UtilityHelper helper = new UtilityHelper();
        String ServiceRequests = request.getParameter("ServiceRequests").trim();
        ServletContext context;
        context = this.getServletContext();

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

            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            switch (ServiceRequests) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "UploadFileOnly":
                    UploadFileOnly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                case "SaveData":
                    SaveData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
                    break;
                default:
                    out.println("Under Development");
                    break;
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadFacilityData.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void UploadFileOnly(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            String key = "";
            String FileName = "";
            String Query = "";

            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();

            boolean FileFound;
            byte Data[];
            try {
                FileFound = false;
                Data = (byte[]) null;
                Query = key = FileName = "";
                Dictionary d = doUpload(request, response, out);
                Enumeration e = d.keys();

                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (key.endsWith(".xls") || key.endsWith(".xlsx") || key.endsWith(".csv")) {
                        FileName = key;
                        FileFound = true;
                        ByteArrayOutputStream baos = null;
                        baos = (ByteArrayOutputStream) d.get(key);
                        Data = baos.toByteArray();
                    }
                }
                if (!FileFound) {
                    throw new Exception("You have Uploaded Incorrect File.");
                }
                File f = new File("/sftpdrive/opt/" + FileName);
                if (f.exists()) {
                    f.delete();
                }
                FileOutputStream fout = new FileOutputStream(f);
                fout.write(Data);
                fout.flush();
                fout.close();
            } catch (Exception e) {
                out.println("EXCEPTION : " + e.getMessage() + "----" + FileName);
                return;
            }

            try {
                out.println("<!DOCTYPE html><html><body><p style=\"color:black;\">File has been Uploaded Successfully </p>");
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"PatientsDataUpload\" action=\"/md/md.RemoveCMDNacho?ServiceRequests=SaveData\" >\n");
                out.println("<div class=\"box-footer\">\n" +
                        "<button type=\"submit\" class=\"btn btn-rounded btn-primary btn-outline\" >\n" +
                        "<i class=\"ti-save-alt\"></i> Save Data\n" +
                        "</button>\n" +
                        "</div>  \n");
                out.println("<input name=\"FileName\" id=\"FileName\" type=\"hidden\" value=" + FileName + ">");
                out.println("</form>");

            } catch (Exception e) {
                try {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Exception/Error.html");
                } catch (Exception localException1) {
                }
                out.flush();
                out.close();
                return;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private Dictionary doUpload(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf(61);
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            final byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            final Dictionary fields = new Hashtable();
            final ServletInputStream in = request.getInputStream();
            for (int i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                final String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {
                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            fields.put(filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        final String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                        } else {
                            if (token.startsWith(" filename")) {
                                filename = tokenizer.nextToken();
                                final StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                                filename = ftokenizer.nextToken();
                                while (ftokenizer.hasMoreTokens()) {
                                    filename = ftokenizer.nextToken();
                                }
                                state = 1;
                                break;
                            }
                            continue;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = String.valueOf(String.valueOf(value)) + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

    private void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        String Query = "";
        String Query0 = "";
        Statement stmt = null;
        Statement stmt0 = null;
        ResultSet rset = null;
        ResultSet rset0 = null;
        int i = 0;
        int MRN = 0;
        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;

        try {
            fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);

            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                MRN = Integer.parseInt(formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "").trim());
                int alertCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.Alerts WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    alertCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int CardConnectCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.CardConnectResponses WHERE PatientMRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    CardConnectCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int CashPaymentCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.CashPayments WHERE PatientMRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    CashPaymentCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int InsuranceInfoCount = 0;
                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "SELECT COUNT(*) FROM nacogdoches.InsuranceInfo WHERE PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        InsuranceInfoCount = rset.getInt(1);
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();

                int InstallmentPlanCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.InstallmentPlan WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InstallmentPlanCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int InvoiceMasterCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.InvoiceMaster WHERE PatientMRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    InvoiceMasterCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int JSON_ResponseCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.JSON_Response WHERE PatientMRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    JSON_ResponseCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int NotesCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.Notes WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    NotesCount = rset.getInt(1);
                rset.close();
                stmt.close();

                int AdditionalInfoCount = 0;
                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "SELECT COUNT(*) FROM nacogdoches.Patient_AdditionalInfo WHERE PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next())
                        AdditionalInfoCount = rset.getInt(1);
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();

                int PaymentReceiptInfoCount = 0;
                Query = "SELECT COUNT(*) FROM nacogdoches.PaymentReceiptInfo WHERE PatientMRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    PaymentReceiptInfoCount = rset.getInt(1);
                rset.close();
                stmt.close();

                if (alertCount > 0 || CardConnectCount > 0 || CashPaymentCount > 0 || InsuranceInfoCount > 0 ||
                        NotesCount > 0 || InstallmentPlanCount > 0 || InvoiceMasterCount > 0 || JSON_ResponseCount > 0 ||
                        AdditionalInfoCount > 0 || PaymentReceiptInfoCount > 0) {

                    out.println("******************************************************** <br> ");
                    out.println("MRN " + MRN + " <br> ");
                    out.println("alertCount " + alertCount + " <br> ");
                    out.println("CardConnectCount " + CardConnectCount + " <br> ");
                    out.println("CashPaymentCount " + CashPaymentCount + " <br> ");
                    out.println("InsuranceInfoCount " + InsuranceInfoCount + " <br> ");
                    out.println("NotesCount " + NotesCount + " <br> ");
                    out.println("InstallmentPlanCount " + InstallmentPlanCount + " <br> ");
                    out.println("InvoiceMasterCount " + InvoiceMasterCount + " <br> ");
                    out.println("JSON_ResponseCount " + JSON_ResponseCount + " <br> ");
                    out.println("AdditionalInfoCount " + AdditionalInfoCount + " <br> ");
                    out.println("PaymentReceiptInfoCount " + PaymentReceiptInfoCount + " <br> ");
                    out.println("******************************************************** <br> ");
                    out.println();
                    out.println();
                    continue;
                }

                Query = "SELECT ID, ClientIndex, FirstName, LastName, MiddleInitial, DOB, Age, Gender, Email, PhNumber, Address, City, " +
                        "State, Country, ZipCode, SSN, Occupation, Employer, EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate, " +
                        "Title, MaritalStatus, CreatedBy, MRN, COVIDStatus, Status, DoctorsName, sync, DateofService, ExtendedMRN, County, " +
                        "Ethnicity, Address2, StreetAddress2, EnterBy, EnterType, EnterIP, ViewDate, RegisterFrom " +
                        "FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into nacogdoches.PatientRegCMD (ID, ClientIndex, FirstName, LastName, MiddleInitial, DOB, Age, " +
                                        "Gender, Email, PhNumber, Address, City, State, Country, ZipCode, SSN, Occupation, Employer, " +
                                        "EmpContact, PriCarePhy, ReasonVisit, SelfPayChk, CreatedDate, Title, MaritalStatus, CreatedBy, MRN, " +
                                        "COVIDStatus, Status, DoctorsName, sync, DateofService, ExtendedMRN, County, Ethnicity, Address2, " +
                                        "StreetAddress2, EnterBy, EnterType, EnterIP, ViewDate, RegisterFrom) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, rset.getInt("ID"));
                        MainReceipt.setInt(2, rset.getInt("ClientIndex"));
                        MainReceipt.setString(3, rset.getString("FirstName"));
                        MainReceipt.setString(4, rset.getString("LastName"));
                        MainReceipt.setString(5, rset.getString("MiddleInitial"));
                        MainReceipt.setString(6, rset.getString("DOB"));
                        MainReceipt.setInt(7, rset.getInt("Age"));
                        MainReceipt.setString(8, rset.getString("Gender"));
                        MainReceipt.setString(9, rset.getString("Email"));
                        MainReceipt.setString(10, rset.getString("PhNumber"));
                        MainReceipt.setString(11, rset.getString("Address"));
                        MainReceipt.setString(12, rset.getString("City"));
                        MainReceipt.setString(13, rset.getString("State"));
                        MainReceipt.setString(14, rset.getString("Country"));
                        MainReceipt.setString(15, rset.getString("ZipCode"));
                        MainReceipt.setString(16, rset.getString("SSN"));
                        MainReceipt.setString(17, rset.getString("Occupation"));
                        MainReceipt.setString(18, rset.getString("Employer"));
                        MainReceipt.setString(19, rset.getString("EmpContact"));
                        MainReceipt.setString(20, rset.getString("PriCarePhy"));
                        MainReceipt.setString(21, rset.getString("ReasonVisit"));
                        MainReceipt.setInt(22, rset.getInt("SelfPayChk"));
                        MainReceipt.setString(23, rset.getString("CreatedDate"));
                        MainReceipt.setString(24, rset.getString("Title"));
                        MainReceipt.setString(25, rset.getString("MaritalStatus"));
                        MainReceipt.setString(26, rset.getString("CreatedBy"));
                        MainReceipt.setInt(27, rset.getInt("MRN"));
                        MainReceipt.setString(28, rset.getString("COVIDStatus"));
                        MainReceipt.setInt(29, rset.getInt("Status"));
                        MainReceipt.setString(30, rset.getString("DoctorsName"));
                        MainReceipt.setString(31, rset.getString("sync"));
                        MainReceipt.setString(32, rset.getString("DateofService"));
                        MainReceipt.setString(33, rset.getString("ExtendedMRN"));
                        MainReceipt.setString(34, rset.getString("County"));
                        MainReceipt.setString(35, rset.getString("Ethnicity"));
                        MainReceipt.setString(36, rset.getString("Address2"));
                        MainReceipt.setString(37, rset.getString("StreetAddress2"));
                        MainReceipt.setString(38, rset.getString("EnterBy"));
                        MainReceipt.setString(39, rset.getString("EnterType"));
                        MainReceipt.setString(40, rset.getString("EnterIP"));
                        MainReceipt.setString(41, rset.getString("ViewDate"));
                        MainReceipt.setString(42, rset.getString("RegisterFrom"));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception e) {
                        out.println("Error in inserting data in PatientReg_test Table: " + e.getMessage());
                        String str = "";
                        for (i = 0; i < e.getStackTrace().length; ++i) {
                            str = str + e.getStackTrace()[i] + "<br>";
                        }
                        out.println(str);
                        return;
                    }
                }
                rset.close();
                stmt.close();

                Query = "Select Id, MRN, PatientRegId, ReasonVisit, VisitNumber, DoctorId, DateofService, CreatedDate, CreatedBy " +
                        "from nacogdoches.PatientVisit where MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into nacogdoches.PatientVisitCMD (Id, MRN, PatientRegId, ReasonVisit, VisitNumber, DoctorId, " +
                                        "DateofService, CreatedDate, CreatedBy) \n "
                                        + "values (?,?,?,?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, rset.getInt("Id"));
                        MainReceipt.setInt(2, rset.getInt("MRN"));
                        MainReceipt.setInt(3, rset.getInt("PatientRegId"));
                        MainReceipt.setString(4, rset.getString("ReasonVisit"));
                        MainReceipt.setInt(5, rset.getInt("VisitNumber"));
                        MainReceipt.setInt(6, rset.getInt("DoctorId"));
                        MainReceipt.setString(7, rset.getString("DateofService"));
                        MainReceipt.setString(8, rset.getString("CreatedDate"));
                        MainReceipt.setString(9, rset.getString("CreatedBy"));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception e) {
                        out.println("Error in inserting data in PatientVisit_test Table: " + e.getMessage());
                        String str = "";
                        for (i = 0; i < e.getStackTrace().length; ++i) {
                            str = str + e.getStackTrace()[i] + "<br>";
                        }
                        out.println(str);
                        return;
                    }

                }
                rset.close();
                stmt.close();

                Query = "Select ID, PatientRegId, MRN, TravellingChk, TravelWhen, TravelWhere, TravelHowLong, COVIDExposedChk, SympFever, " +
                        "SympBodyAches, SympSoreThroat, SympFatigue, SympRash, SympVomiting, SympDiarrhea, SympCough, SympRunnyNose, " +
                        "SympNausea, SympFluSymptoms, SympEyeConjunctivitis, Race, CovidExpWhen, SpCarePhy, SympHeadache, SympLossTaste, " +
                        "SympShortBreath, AddInfoTextArea, SympCongestion, Ethnicity, GuarantorName, GuarantorDOB, GuarantorNumber, " +
                        "GuarantorSSN, VisitId, COVIDPositveChk, CovidPositiveDate, GuarantorLastName " +
                        "from nacogdoches.PatientReg_Details where MRN = " + MRN;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into nacogdoches.PatientReg_DetailsCMD (ID, PatientRegId, MRN, TravellingChk, TravelWhen, " +
                                        "TravelWhere, TravelHowLong, COVIDExposedChk, SympFever, SympBodyAches, SympSoreThroat, SympFatigue, " +
                                        "SympRash, SympVomiting, SympDiarrhea, SympCough, SympRunnyNose, SympNausea, SympFluSymptoms, " +
                                        "SympEyeConjunctivitis, Race, CovidExpWhen, SpCarePhy, SympHeadache, SympLossTaste, SympShortBreath, " +
                                        "AddInfoTextArea, SympCongestion, Ethnicity, GuarantorName, GuarantorDOB, GuarantorNumber, " +
                                        "GuarantorSSN, VisitId, COVIDPositveChk, CovidPositiveDate, GuarantorLastName) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, rset.getInt("ID"));
                        MainReceipt.setInt(2, rset.getInt("PatientRegId"));
                        MainReceipt.setInt(3, rset.getInt("MRN"));
                        MainReceipt.setInt(4, rset.getInt("TravellingChk"));
                        MainReceipt.setString(5, rset.getString("TravelWhen"));
                        MainReceipt.setString(6, rset.getString("TravelWhere"));
                        MainReceipt.setString(7, rset.getString("TravelHowLong"));
                        MainReceipt.setInt(8, rset.getInt("COVIDExposedChk"));
                        MainReceipt.setString(9, rset.getString("SympFever"));
                        MainReceipt.setString(10, rset.getString("SympBodyAches"));
                        MainReceipt.setString(11, rset.getString("SympSoreThroat"));
                        MainReceipt.setString(12, rset.getString("SympFatigue"));
                        MainReceipt.setString(13, rset.getString("SympRash"));
                        MainReceipt.setString(14, rset.getString("SympVomiting"));
                        MainReceipt.setString(15, rset.getString("SympDiarrhea"));
                        MainReceipt.setString(16, rset.getString("SympCough"));
                        MainReceipt.setString(17, rset.getString("SympRunnyNose"));
                        MainReceipt.setString(18, rset.getString("SympNausea"));
                        MainReceipt.setString(19, rset.getString("SympFluSymptoms"));
                        MainReceipt.setString(20, rset.getString("SympEyeConjunctivitis"));
                        MainReceipt.setString(21, rset.getString("Race"));
                        MainReceipt.setString(22, rset.getString("CovidExpWhen"));
                        MainReceipt.setString(23, rset.getString("SpCarePhy"));
                        MainReceipt.setString(24, rset.getString("SympHeadache"));
                        MainReceipt.setString(25, rset.getString("SympLossTaste"));
                        MainReceipt.setString(26, rset.getString("SympShortBreath"));
                        MainReceipt.setString(27, rset.getString("AddInfoTextArea"));
                        MainReceipt.setString(28, rset.getString("SympCongestion"));
                        MainReceipt.setString(29, rset.getString("Ethnicity"));
                        MainReceipt.setString(30, rset.getString("GuarantorName"));
                        MainReceipt.setString(31, rset.getString("GuarantorDOB"));
                        MainReceipt.setString(32, rset.getString("GuarantorNumber"));
                        MainReceipt.setString(33, rset.getString("GuarantorSSN"));
                        MainReceipt.setString(34, rset.getString("VisitId"));
                        MainReceipt.setInt(35, rset.getInt("COVIDPositveChk"));
                        MainReceipt.setString(36, rset.getString("CovidPositiveDate"));
                        MainReceipt.setString(37, rset.getString("GuarantorLastName"));
                        MainReceipt.executeUpdate();
                        MainReceipt.close();
                    } catch (Exception e) {
                        out.println("Error in inserting data in PatientReg_Details_TEST Table: " + e.getMessage());
                        String str = "";
                        for (i = 0; i < e.getStackTrace().length; ++i) {
                            str = str + e.getStackTrace()[i] + "<br>";
                        }
                        out.println(str);
                        return;
                    }
                }
                rset.close();
                stmt.close();

                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "Select ID, PatientRegId, NextofKinName, RelationToPatient, PhoneNumber, LeaveMessage, Address, City, " +
                            "State, Country, ZipCode, CreatedDate, VisitId " +
                            "from nacogdoches.EmergencyInfo where PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        try {
                            PreparedStatement MainReceipt = conn.prepareStatement(
                                    "Insert into nacogdoches.EmergencyInfoCMD (ID, PatientRegId, NextofKinName, RelationToPatient, " +
                                            "PhoneNumber, LeaveMessage, Address, City, State, Country, ZipCode, CreatedDate, VisitId) \n "
                                            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                            MainReceipt.setInt(1, rset.getInt("ID"));
                            MainReceipt.setInt(2, rset.getInt("PatientRegId"));
                            MainReceipt.setString(3, rset.getString("NextofKinName"));
                            MainReceipt.setString(4, rset.getString("RelationToPatient"));
                            MainReceipt.setString(5, rset.getString("PhoneNumber"));
                            MainReceipt.setString(6, rset.getString("LeaveMessage"));
                            MainReceipt.setString(7, rset.getString("Address"));
                            MainReceipt.setString(8, rset.getString("City"));
                            MainReceipt.setString(9, rset.getString("State"));
                            MainReceipt.setString(10, rset.getString("Country"));
                            MainReceipt.setString(11, rset.getString("ZipCode"));
                            MainReceipt.setString(12, rset.getString("CreatedDate"));
                            MainReceipt.setString(13, rset.getString("VisitId"));
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            out.println("Error in inserting data in EmergencyInfo_TEST Table: " + e.getMessage());
                            String str = "";
                            for (i = 0; i < e.getStackTrace().length; ++i) {
                                str = str + e.getStackTrace()[i] + "<br>";
                            }
                            out.println(str);
                            return;
                        }
                    }
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();


                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "Select ID, PatientRegId, Google, MapSearch, Billboard, OnlineReview, TV, Website, " +
                            "BuildingSignDriveBy, Facebook, School, School_text, Twitter, Magazine, Magazine_text, Newspaper, Newspaper_text, " +
                            "FamilyFriend, FamilyFriend_text, UrgentCare, UrgentCare_text, CommunityEvent, CommunityEvent_text, Work_text, " +
                            "Physician_text, Other_text, CreatedDate, FrVisitedBefore, FrFamiliyVisitedBefore, FrInternet, FrBillboard, " +
                            "FrGoogle, FrBuildingSignage, FrFacebook, FrLivesNear, FrTwitter, FrTV, FrMapSearch, FrEvent, FrPhysicianReferral, " +
                            "FrNeurologyReferral, FrUrgentCareReferral, FrOrganizationReferral, FrFriendFamily, VisitId,ReturnPatient " +
                            "from nacogdoches.RandomCheckInfo where PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        try {
                            PreparedStatement MainReceipt = conn.prepareStatement(
                                    "Insert into nacogdoches.RandomCheckInfoCMD (ID, PatientRegId, Google, MapSearch, " +
                                            "Billboard, OnlineReview, TV, Website, BuildingSignDriveBy, Facebook, School, School_text, " +
                                            "Twitter, Magazine, Magazine_text, Newspaper, Newspaper_text, FamilyFriend, FamilyFriend_text, " +
                                            "UrgentCare, UrgentCare_text, CommunityEvent, CommunityEvent_text, Work_text, Physician_text, " +
                                            "Other_text, CreatedDate, FrVisitedBefore, FrFamiliyVisitedBefore, FrInternet, FrBillboard, " +
                                            "FrGoogle, FrBuildingSignage, FrFacebook, FrLivesNear, FrTwitter, FrTV, FrMapSearch, FrEvent, " +
                                            "FrPhysicianReferral, FrNeurologyReferral, FrUrgentCareReferral, FrOrganizationReferral, " +
                                            "FrFriendFamily, VisitId,ReturnPatient) \n "
                                            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                            MainReceipt.setInt(1, rset.getInt("ID"));
                            MainReceipt.setInt(2, rset.getInt("PatientRegId"));
                            MainReceipt.setInt(3, rset.getInt("Google"));
                            MainReceipt.setInt(4, rset.getInt("MapSearch"));
                            MainReceipt.setInt(5, rset.getInt("Billboard"));
                            MainReceipt.setInt(6, rset.getInt("OnlineReview"));
                            MainReceipt.setInt(7, rset.getInt("TV"));
                            MainReceipt.setInt(8, rset.getInt("Website"));
                            MainReceipt.setInt(9, rset.getInt("BuildingSignDriveBy"));
                            MainReceipt.setInt(10, rset.getInt("Facebook"));
                            MainReceipt.setInt(11, rset.getInt("School"));
                            MainReceipt.setString(12, rset.getString("School_text"));
                            MainReceipt.setInt(13, rset.getInt("Twitter"));
                            MainReceipt.setInt(14, rset.getInt("Magazine"));
                            MainReceipt.setString(15, rset.getString("Magazine_text"));
                            MainReceipt.setInt(16, rset.getInt("Newspaper"));
                            MainReceipt.setString(17, rset.getString("Newspaper_text"));
                            MainReceipt.setInt(18, rset.getInt("FamilyFriend"));
                            MainReceipt.setString(19, rset.getString("FamilyFriend_text"));
                            MainReceipt.setInt(20, rset.getInt("UrgentCare"));
                            MainReceipt.setString(21, rset.getString("UrgentCare_text"));
                            MainReceipt.setInt(22, rset.getInt("CommunityEvent"));
                            MainReceipt.setString(23, rset.getString("CommunityEvent_text"));
                            MainReceipt.setString(24, rset.getString("Work_text"));
                            MainReceipt.setString(25, rset.getString("Physician_text"));
                            MainReceipt.setString(26, rset.getString("Other_text"));
                            MainReceipt.setString(27, rset.getString("CreatedDate"));
                            MainReceipt.setInt(28, rset.getInt("FrVisitedBefore"));
                            MainReceipt.setInt(29, rset.getInt("FrFamiliyVisitedBefore"));
                            MainReceipt.setInt(30, rset.getInt("FrInternet"));
                            MainReceipt.setInt(31, rset.getInt("FrBillboard"));
                            MainReceipt.setInt(32, rset.getInt("FrGoogle"));
                            MainReceipt.setInt(33, rset.getInt("FrBuildingSignage"));
                            MainReceipt.setInt(34, rset.getInt("FrFacebook"));
                            MainReceipt.setInt(35, rset.getInt("FrLivesNear"));
                            MainReceipt.setInt(36, rset.getInt("FrTwitter"));
                            MainReceipt.setInt(37, rset.getInt("FrTV"));
                            MainReceipt.setInt(38, rset.getInt("FrMapSearch"));
                            MainReceipt.setInt(39, rset.getInt("FrEvent"));
                            MainReceipt.setString(40, rset.getString("FrPhysicianReferral"));
                            MainReceipt.setString(41, rset.getString("FrNeurologyReferral"));
                            MainReceipt.setString(42, rset.getString("FrUrgentCareReferral"));
                            MainReceipt.setString(43, rset.getString("FrOrganizationReferral"));
                            MainReceipt.setString(44, rset.getString("FrFriendFamily"));
                            MainReceipt.setString(45, rset.getString("VisitId"));
                            MainReceipt.setInt(46, rset.getInt("ReturnPatient"));
                            MainReceipt.executeUpdate();
                            MainReceipt.close();
                        } catch (Exception e) {
                            out.println("Error in inserting data in RandomCheckInfo_TEST Table: " + e.getMessage());
                            String str = "";
                            for (i = 0; i < e.getStackTrace().length; ++i) {
                                str = str + e.getStackTrace()[i] + "<br>";
                            }
                            out.println(str);
                            return;
                        }
                    }
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();

                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "DELETE FROM nacogdoches.EmergencyInfo WHERE PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                rset0.close();
                stmt0.close();

                Query0 = "SELECT ID FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);
                if (rset0.next()) {
                    Query = "DELETE FROM nacogdoches.RandomCheckInfo WHERE PatientRegId = " + rset0.getInt(1);
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                rset0.close();
                stmt0.close();

                Query = "DELETE FROM nacogdoches.PatientReg WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Query = "DELETE FROM nacogdoches.PatientVisit WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                Query = "DELETE FROM nacogdoches.PatientReg_Details WHERE MRN = " + MRN;
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }
}
