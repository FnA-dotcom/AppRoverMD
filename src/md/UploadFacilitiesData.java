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
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@SuppressWarnings("Duplicates")
public class UploadFacilitiesData extends HttpServlet {
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
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"PatientsDataUpload\" action=\"/md/md.UploadFacilitiesData?ServiceRequests=SaveData\" >\n");
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
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        Statement stmt1 = null;

        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;
        int i = 0;
        String DOS = "";
        int MRN = 0;
        int tableMRN = 0;
        double Age = 0.0;
        String mi = "";
        String FName = "";
        String LName = "";
        String DOB = "";
        String SSN = "";
        String Gender = "";
        String MStatus = "";
        String Phone = "";
        String Email = "";
        String Address = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String ZIPCode = "";
        String ReasonVisit = "";
        String RegisterBy = "";
        String HomePhone = "";
        String WorkPhone = "";
        String MobilePhone = "";
        int DupRec = 0;
        int PatientRegId = 0;
        int maxVisitIdx = 0;
        int UniqueRec = 0;
        int FoundMRN = 0;
        int foundVisit = 0;
        int DupVisit = 0;
        int VisitNumber = 0;
        int _Age = 0;
        try {
            fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                if (!formatter.formatCellValue(row.getCell(0)).equals("")) {
                    DOS = formatter.formatCellValue(row.getCell(0));
                    if (DOS.length() > 11)
                        DOS = DOS.substring(0, 4) + "-" + DOS.substring(4, 6) + "-" + DOS.substring(6, 8) + " " + DOS.substring(8, 10) + ":" + DOS.substring(10, 12) + ":00".replaceAll("\n", "");
                    else
                        DOS = "0000-00-00";
                } else {
                    DOS = "0000-00-00";
                }

                MRN = Integer.parseInt(formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "").trim());
                FName = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                LName = formatter.formatCellValue(row.getCell(3)).replaceAll("\n", "");
                Phone = formatter.formatCellValue(row.getCell(4)).replaceAll("\n", "").trim();
                Address = formatter.formatCellValue(row.getCell(5)).replaceAll("\n", "").trim();
                Address2 = formatter.formatCellValue(row.getCell(6)).replaceAll("\n", "").trim();
                City = formatter.formatCellValue(row.getCell(7)).replaceAll("\n", "").trim();
                State = formatter.formatCellValue(row.getCell(8)).replaceAll("\n", "").trim();
                ZIPCode = formatter.formatCellValue(row.getCell(9)).replaceAll("\n", "").trim();
                MStatus = formatter.formatCellValue(row.getCell(10)).replaceAll("\n", "").trim();
                Email = formatter.formatCellValue(row.getCell(11)).replaceAll("\n", "").trim();
                Gender = formatter.formatCellValue(row.getCell(12)).replaceAll("\n", "").trim();
                mi = formatter.formatCellValue(row.getCell(13)).replaceAll("\n", "").trim();
                SSN = formatter.formatCellValue(row.getCell(14)).replaceAll("\n", "");
                HomePhone = formatter.formatCellValue(row.getCell(15)).replaceAll("\n", "");
                WorkPhone = formatter.formatCellValue(row.getCell(16)).replaceAll("\n", "");
                MobilePhone = formatter.formatCellValue(row.getCell(17)).replaceAll("\n", "");
                if (!formatter.formatCellValue(row.getCell(18)).equals("")) {
                    DOB = formatter.formatCellValue(row.getCell(18));
                    if (DOB.length() > 7)
                        DOB = DOB.substring(0, 4) + "-" + DOB.substring(4, 6) + "-" + DOB.substring(6, 8).replaceAll("\n", "");
                    else
                        DOB = "0000-00-00";
                } else {
                    DOB = "0000-00-00";
                }
                ReasonVisit = formatter.formatCellValue(row.getCell(19)).replaceAll("\n", "").trim();

                if (!DOB.equals("0000-00-00"))
                    _Age = getAge(LocalDate.parse(DOB));
                else
                    _Age = 0;

/*                out.println("******************************************************** <br> ");
                out.println("MRN --> " + MRN + " <br> ");
                out.println("DOS --> " + DOS + " <br> ");
                out.println("F Name --> " + FName + " <br> ");
                out.println("L Name --> " + LName + " <br> ");
                out.println("Middle Name --> " + mi + " <br> ");
                out.println("SSN --> " + SSN + " <br> ");
                out.println("MStatus --> " + MStatus + " <br> ");
                out.println("DOB --> " + DOB + " <br> ");
                out.println("Age --> " + Age + " <br> ");
                out.println("Gender --> " + Gender + " <br> ");
                out.println("Phone --> " + Phone + " <br> ");
                out.println("Email --> " + Email + " <br> ");
                out.println("Address --> " + Address + " <br> ");
                out.println("Address2 --> " + Address2 + " <br> ");
                out.println("City --> " + City + " <br> ");
                out.println("State --> " + State + " <br> ");
                out.println("ZIPCode --> " + ZIPCode + " <br> ");
                out.println("HomePhone --> " + HomePhone + " <br> ");
                out.println("WorkPhone --> " + WorkPhone + " <br> ");
                out.println("MobilePhone --> " + MobilePhone + " <br> ");
                out.println("ReasonVisit --> " + ReasonVisit + " <br> ");
                out.println("******************************************************** <br> ");*/


                try {
//                    Query = "Select COUNT(*), IFNULL(MRN,0) from longview.PatientReg where MRN = " + MRN;//+ " and DATE_FORMAT(DateofService,'%Y-%m-%d') = DATE_FORMAT('" + DOS + "','%Y-%m-%d') ";
/*                    Query = " SELECT COUNT(*), IFNULL(MRN,0) from floresville.PatientReg  " +
                            " WHERE Status = 0 AND " +
                            "ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER('" + FName.trim() + "')))  AND " +
                            "ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER('" + LName.trim() + "'))) AND " +
                            "DOB = '" + DOB + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        FoundMRN = rset.getInt(1);
                        tableMRN = rset.getInt(2);
                    }
                    rset.close();
                    stmt.close();*/
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "SELECT COUNT(*), IFNULL(MRN,0) from hope_er.PatientReg " +
                                    "WHERE " +
                                    "Status = 0 AND " +
                                    "ltrim(rtrim(UPPER(FirstName))) = ltrim(rtrim(UPPER(?)))  AND " +
                                    "ltrim(rtrim(UPPER(LastName))) = ltrim(rtrim(UPPER(?))) AND " +
                                    "DOB = ?");
                    preparedStatement.setString(1, FName.trim());
                    preparedStatement.setString(2, LName.trim());
                    preparedStatement.setString(3, DOB.trim());
                    rset = preparedStatement.executeQuery();
                    if (rset.next()) {
                        FoundMRN = rset.getInt(1);
                        tableMRN = rset.getInt(2);
                    }
                    rset.close();
                    preparedStatement.close();

                } catch (Exception e) {
                    out.println("Error in FoundMRN: " + e.getMessage() + Query);
                    return;
                }
//                out.println("MRN " + MRN + "<br>");
//                out.println("******************************* <br> ");
//                out.println("* Found " + MRN + " --> " + FoundMRN + " ** <br> ");
//                out.println("******************************* <br>");

                /*if (FoundMRN < 1) {
                    RegisterBy = "ManualUpload";
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into floresville.PatientReg (DateofService, CreatedDate, MRN, FirstName, LastName," +
                                        " ClientIndex, Status, SelfPayChk,SSN,PhNumber,Address, City,State,Country,ZipCode," +
                                        "CreatedBy,ReasonVisit,Email,Gender,MaritalStatus,MiddleInitial,Address2,ViewDate,DOB," +
                                        "RegisterFrom) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,'US',?,?,?,?,?,?,?,?,?,?,'Excel Sheet') ");
                        MainReceipt.setString(1, DOS);
                        MainReceipt.setString(2, DOS);
                        MainReceipt.setInt(3, MRN);
                        MainReceipt.setString(4, FName);
                        MainReceipt.setString(5, LName);
                        MainReceipt.setInt(6, 1);
                        MainReceipt.setInt(7, 0);
                        MainReceipt.setInt(8, 0);
                        MainReceipt.setString(9, SSN);
                        MainReceipt.setString(10, Phone);
                        MainReceipt.setString(11, Address);
                        MainReceipt.setString(12, City);
                        MainReceipt.setString(13, State);
                        MainReceipt.setString(14, ZIPCode);
                        MainReceipt.setString(15, RegisterBy);
                        MainReceipt.setString(16, ReasonVisit);
                        MainReceipt.setString(17, Email);
                        MainReceipt.setString(18, Gender);
                        MainReceipt.setString(19, MStatus);
                        MainReceipt.setString(20, mi);
                        MainReceipt.setString(21, Address2);
                        MainReceipt.setString(22, DOS);
                        MainReceipt.setString(23, DOB);
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

                    Query = "Select Id from floresville.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into floresville.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, " +
                                        "CreatedDate, CreatedBy, ReasonVisit) \n "
                                        + "values (?,?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, MRN);
                        MainReceipt.setInt(2, PatientRegId);
                        MainReceipt.setInt(3, 1);
                        MainReceipt.setString(4, DOS);
                        MainReceipt.setString(5, DOS);
                        MainReceipt.setString(6, "ManualUpload");
                        MainReceipt.setString(7, ReasonVisit);
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

                    Query = "Select MAX(Id) from floresville.PatientVisit where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        maxVisitIdx = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into floresville.PatientReg_Details (PatientRegId, MRN,VisitId) \n "
                                        + "values (?,?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, MRN);
                        MainReceipt.setInt(3, maxVisitIdx);
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

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into floresville.EmergencyInfo (PatientRegId,VisitId) \n "
                                        + "values (?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, maxVisitIdx);
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

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into floresville.RandomCheckInfo (PatientRegId,VisitId) \n "
                                        + "values (?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, maxVisitIdx);
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
                    UniqueRec++;
                }*/

                if (FoundMRN > 0) {
                    Query = "Select Id from hope_er.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    Query = "SELECT COUNT(*) FROM hope_er.PatientVisit WHERE MRN = " + MRN + " AND DateofService = '" + DOS + "' ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        foundVisit = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    if (foundVisit > 0) {
                        DupVisit++;
                    } else {
                        try {
                            Query = "Select IFNULL(MAX(VisitNumber),0) + 1 from hope_er.PatientVisit where MRN = " + MRN;
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
                            PreparedStatement MainReceipt = conn.prepareStatement(
                                    "Insert into hope_er.PatientVisit (MRN, PatientRegId, VisitNumber,DateofService, CreatedDate, " +
                                            "CreatedBy, ReasonVisit) VALUES (?,?,?,?,?,?,?) ");
                            MainReceipt.setInt(1, MRN);
                            MainReceipt.setInt(2, PatientRegId);
                            MainReceipt.setInt(3, VisitNumber);
                            MainReceipt.setString(4, DOS);
                            MainReceipt.setString(5, DOS);
                            MainReceipt.setString(6, "ManualUpload");
                            MainReceipt.setString(7, ReasonVisit);
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
/*                        try {
                            Query = "Select Id,IFNULL(VisitNumber,0) from schertz.PatientVisit where PatientRegId = " + PatientRegId+ " ORDER BY DateofService";
                            stmt = conn.createStatement();
                            rset = stmt.executeQuery(Query);
                            if (rset.next()) {
                                Query1 = "Update schertz.PatientVisit set VisitNumber = "+rset.getInt(2)+"+1 where ID = " + rset.getInt(1);
                                stmt1 = conn.createStatement();
                                stmt1.executeUpdate(Query1);
                                stmt1.close();
                            }
                            rset.close();
                            stmt.close();
                        } catch (Exception e) {
                            out.println("Error in getting Visit Number + 1" + e.getMessage());
                        }*/
                        try {
                            Query = "Update hope_er.PatientReg set DateofService = '" + DOS + "' where MRN = " + MRN;
                            stmt = conn.createStatement();
                            stmt.executeUpdate(Query);
                            stmt.close();

                        } catch (Exception e) {
                            out.println("Error in Update PatientReg DOS: " + e.getMessage());
                            return;
                        }
                    }
                    DupRec++;
                } else {
                    RegisterBy = "ManualUpload";
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into hope_er.PatientReg (DateofService, CreatedDate, MRN, FirstName, LastName," +
                                        " ClientIndex, Status, SelfPayChk,SSN,PhNumber,Address, City,State,Country,ZipCode," +
                                        "CreatedBy,ReasonVisit,Email,Gender,MaritalStatus,MiddleInitial,Address2,ViewDate,DOB,Age) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,'US',?,?,?,?,?,?,?,?,?,?,?) ");
                        MainReceipt.setString(1, DOS);
                        MainReceipt.setString(2, DOS);
                        MainReceipt.setInt(3, MRN);
                        MainReceipt.setString(4, FName);
                        MainReceipt.setString(5, LName);
                        MainReceipt.setInt(6, 19);
                        MainReceipt.setInt(7, 0);
                        MainReceipt.setInt(8, 0);
                        MainReceipt.setString(9, SSN);
                        MainReceipt.setString(10, Phone);
                        MainReceipt.setString(11, Address);
                        MainReceipt.setString(12, City);
                        MainReceipt.setString(13, State);
                        MainReceipt.setString(14, ZIPCode);
                        MainReceipt.setString(15, RegisterBy);
                        MainReceipt.setString(16, ReasonVisit);
                        MainReceipt.setString(17, Email);
                        MainReceipt.setString(18, Gender);
                        MainReceipt.setString(19, MStatus);
                        MainReceipt.setString(20, mi);
                        MainReceipt.setString(21, Address2);
                        MainReceipt.setString(22, DOS);
                        MainReceipt.setString(23, DOB);
                        MainReceipt.setInt(24, _Age);
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

                    Query = "Select Id from hope_er.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into hope_er.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, CreatedDate, CreatedBy, ReasonVisit) \n "
                                        + "values (?,?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, MRN);
                        MainReceipt.setInt(2, PatientRegId);
                        MainReceipt.setInt(3, 1);
                        MainReceipt.setString(4, DOS);
                        MainReceipt.setString(5, DOS);
                        MainReceipt.setString(6, "ManualUpload");
                        MainReceipt.setString(7, ReasonVisit);
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

                    Query = "Select MAX(Id) from hope_er.PatientVisit where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        maxVisitIdx = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into hope_er.PatientReg_Details (PatientRegId, MRN,VisitId) \n "
                                        + "values (?,?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, MRN);
                        MainReceipt.setInt(3, maxVisitIdx);
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

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into hope_er.EmergencyInfo (PatientRegId,VisitId) \n "
                                        + "values (?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, maxVisitIdx);
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

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into hope_er.RandomCheckInfo (PatientRegId,VisitId) \n "
                                        + "values (?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, maxVisitIdx);
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
                    UniqueRec++;
                }
            }
            out.println("Duplicate Records : " + DupRec + "<br>");
//            out.println("tableMRN : " + tableMRN + "<br>");
            out.println("Duplicate VISITS : " + DupVisit + "<br>");
            out.println("Unique Records : " + UniqueRec + "<br>");
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
