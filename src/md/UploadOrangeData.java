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
public class UploadOrangeData extends HttpServlet {
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

            if (ServiceRequests.equals("GetInput")) {
                this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
            } else if (ServiceRequests.equals("UploadFileOnly")) {
                UploadFileOnly(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
            } else if (ServiceRequests.equals("SaveData")) {
                SaveData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, response);
            } else {
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


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadOrangeData.html");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void UploadFileOnly(final HttpServletRequest request, final PrintWriter out, Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
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
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"PatientsDataUpload\" action=\"/md/md.UploadOrangeData?ServiceRequests=SaveData\" >\n");
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

//            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
//            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
//            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
//            final Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("UserId", String.valueOf(UserId));
//            Parser.SetField("Header", String.valueOf(Header));
//            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
//            Parser.SetField("Footer", String.valueOf(Footer));
//            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadDataGraceER.html");
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

    private void SaveData(final HttpServletRequest request, final PrintWriter out, Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;

        String FileName = request.getParameter("FileName").trim();
        out.println("FileName " + FileName + " <br>");
        FileInputStream fileIn = null;
        int i = 0;
        String DOS = "";
        String DOSTime = "";
        int MRN = 0;
        double Age = 0.0;
        String Name = "";
        String FName = "";
        String LName = "";
        String DOB = "";
        String SSN = "";
        String Gender = "";
        String Ethnicity = "";
        String Race = "";
        String Phone = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String ZIPCode = "";
        String EmployerInfo = "";
        String ReasonVisit = "";
        String RegisterBy = "";
        int _Ethnicity = 0;
        int VisitNumber = 0;
        int PatientRegId = 0;
        int DupRec = 0;
        int UniqueRec = 0;
        int maxVisitIdx = 0;
        try {
            fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);
            DataFormatter formatter = new DataFormatter(Locale.US);

            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);
                MRN = Integer.parseInt(formatter.formatCellValue(row.getCell(0)).replaceAll("\n", "").trim());
                if (!formatter.formatCellValue(row.getCell(1)).equals("")) {
                    DOS = formatter.formatCellValue(row.getCell(1));
                    //DOS = DOS.substring(0, 4) + "-" + DOS.substring(4, 6) + "-" + DOS.substring(6, 8) + " " + DOS.substring(8, 10) + ":" + DOS.substring(10, 12) + ":00".replaceAll("\n", "");
                } else {
                    DOS = "0000-00-00";
                }
                DOSTime = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                FName = formatter.formatCellValue(row.getCell(3)).replaceAll("\n", "");
                LName = formatter.formatCellValue(row.getCell(4)).replaceAll("\n", "");
                SSN = formatter.formatCellValue(row.getCell(5)).replaceAll("\n", "");
                DOB = formatter.formatCellValue(row.getCell(6)).replaceAll("\n", "");
                Age = Double.parseDouble(formatter.formatCellValue(row.getCell(7)).replaceAll("\n", "").trim());
                Gender = formatter.formatCellValue(row.getCell(8)).replaceAll("\n", "").trim();
/*                Ethnicity = formatter.formatCellValue(row.getCell(9)).replaceAll("\n", "").trim();
                switch (Ethnicity) {
                    case "Non-Hispanic/Latino":
                        _Ethnicity = 2;
                        break;
                    case "Hispanic/Latino":
                        _Ethnicity = 1;
                        break;
                    default:
                        _Ethnicity = 3;
                        break;
                }
                Race = formatter.formatCellValue(row.getCell(10)).replaceAll("\n", "").trim();*/
                Phone = formatter.formatCellValue(row.getCell(11)).replaceAll("\n", "").trim();
                Email = formatter.formatCellValue(row.getCell(12)).replaceAll("\n", "").trim();
                Address = formatter.formatCellValue(row.getCell(13)).replaceAll("\n", "").trim();
                City = formatter.formatCellValue(row.getCell(14)).replaceAll("\n", "").trim();
                State = formatter.formatCellValue(row.getCell(15)).replaceAll("\n", "").trim();
                ZIPCode = formatter.formatCellValue(row.getCell(16)).replaceAll("\n", "").trim();
                EmployerInfo = formatter.formatCellValue(row.getCell(17)).replaceAll("\n", "").trim();
                ReasonVisit = formatter.formatCellValue(row.getCell(18)).replaceAll("\n", "").trim();
                RegisterBy = formatter.formatCellValue(row.getCell(20)).replaceAll("\n", "").trim();

                if (DOSTime.length() == 7)
                    DOSTime = "0" + DOSTime;

                DOS = DOS + " " + DOSTime;

/*                out.println("******************************************************** <br> ");
                out.println("MRN --> " + MRN + " <br> ");
                out.println("DOS --> " + DOS + " <br> ");
                out.println("DOSTime --> " + DOSTime + " <br> ");
                out.println("Name --> " + Name + " <br> ");
                out.println("F Name --> " + FName + " <br> ");
                out.println("L Name --> " + LName + " <br> ");
                out.println("SSN --> " + SSN + " <br> ");
                out.println("DOB --> " + DOB + " <br> ");
                out.println("Age --> " + Age + " <br> ");
                out.println("Gender --> " + Gender + " <br> ");
                out.println("Phone --> " + Phone + " <br> ");
                out.println("Email --> " + Email + " <br> ");
                out.println("Address --> " + Address + " <br> ");
                out.println("City --> " + City + " <br> ");
                out.println("State --> " + State + " <br> ");
                out.println("ZIPCode --> " + ZIPCode + " <br> ");
                out.println("EmployerInfo --> " + EmployerInfo + " <br> ");
                out.println("ReasonVisit --> " + ReasonVisit + " <br> ");
                out.println("RegisterBy --> " + RegisterBy + " <br> ");
                out.println("******************************************************** <br> ");*/

                int FoundMRN = 0;
                try {
                    Query = "Select COUNT(*) from oe_2.PatientReg where MRN = " + MRN;//+ " and DATE_FORMAT(DateofService,'%Y-%m-%d') = DATE_FORMAT('" + DOS + "','%Y-%m-%d') ";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        FoundMRN = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();
                } catch (Exception e) {
                    out.println("Error in FoundMRN: " + e.getMessage() + Query);
                    return;
                }

                if (FoundMRN > 0) {
                    Query = "Select Id from oe_2.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        Query = "Select MAX(VisitNumber) + 1 from oe_2.PatientVisit where PatientRegId = " + PatientRegId;
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
                                "Insert into oe_2.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, CreatedDate, " +
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
                    try {
                        Query = "Update oe_2.PatientReg set DateofService = '" + DOS + "' where ID = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();

                    } catch (Exception e) {
                        out.println("Error in Update PatientReg DOS: " + e.getMessage());
                        return;
                    }
                    DupRec++;
                } else {
                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oe_2.PatientReg (DateofService, CreatedDate, MRN, FirstName, LastName," +
                                        " ClientIndex, Status, SelfPayChk,SSN, DOB,PhNumber,Address, City,State,Country,ZipCode," +
                                        "CreatedBy,ReasonVisit,Email,Gender) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'US',?,?,?,?,?) ");
                        MainReceipt.setString(1, DOS);
                        MainReceipt.setString(2, DOS);
                        MainReceipt.setInt(3, MRN);
                        MainReceipt.setString(4, FName);
                        MainReceipt.setString(5, LName);
                        MainReceipt.setInt(6, 8);
                        MainReceipt.setInt(7, 0);
                        MainReceipt.setInt(8, 0);
                        MainReceipt.setString(9, SSN);
                        MainReceipt.setString(10, DOB);
                        MainReceipt.setString(11, Phone);
                        MainReceipt.setString(12, Address);
                        MainReceipt.setString(13, City);
                        MainReceipt.setString(14, State);
                        MainReceipt.setString(15, ZIPCode);
                        MainReceipt.setString(16, RegisterBy);
                        MainReceipt.setString(17, ReasonVisit);
                        MainReceipt.setString(18, Email);
                        MainReceipt.setString(19, Gender);
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

                    Query = "Select Id from oe_2.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oe_2.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, CreatedDate, CreatedBy, ReasonVisit) \n "
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

                    Query = "Select MAX(Id) from oe_2.PatientVisit where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        maxVisitIdx = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oe_2.PatientReg_Details (PatientRegId, MRN,VisitId) \n "
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
                                "Insert into oe_2.EmergencyInfo (PatientRegId,VisitId) \n "
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
                                "Insert into oe_2.RandomCheckInfo (PatientRegId,VisitId) \n "
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
