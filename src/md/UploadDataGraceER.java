package md;

import Parsehtm.Parsehtm;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@SuppressWarnings("Duplicates")
public class UploadDataGraceER extends HttpServlet {
    static String DOS = "";
    static String Acct = "";
    static String printabledate = "";

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
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            String UserName = "";
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            //System.out.println(Database);

//      if (ClientId == 8) {
//        Database = "oe_2";
//      }
//      else if (ClientId == 9) {
//        Database = "victoria";
//      }
//      else if (ClientId == 10) {
//        Database = "oddasa";
//      }
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Installment Plan Input", "Open Installment Plan Input Screem", ClientId);
                this.GetInput(request, out, conn, context, UserId, Database, ClientId, response);
            } else if (ActionID.equals("UploadFileOnly")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Installment Plan Input", "Open Installment Plan Input Screem", ClientId);
                this.UploadFileOnly(request, out, conn, context, UserId, Database, ClientId, response);
            } else if (ActionID.equals("SaveData")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Save Plan Data", "Save Plan Details", ClientId);
                this.SaveData(request, out, conn, context, UserId, Database, ClientId, response);
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
            SupportiveMethods suppMethods = new SupportiveMethods();
            StringBuffer LeftSideBarMenu = new StringBuffer();
            StringBuffer Header = new StringBuffer();
            StringBuffer Footer = new StringBuffer();

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("Header", String.valueOf(Header));
            Parser.SetField("LeftSideBarMenu", String.valueOf(LeftSideBarMenu));
            Parser.SetField("Footer", String.valueOf(Footer));
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Forms/UploadDataGraceER.html");
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
                out.println("<br><form role=\"form\" method=\"post\" name=\"frm\" id=\"PatientsDataUpload\" action=\"/md/md.UploadDataGraceER?ActionID=SaveData\" >\n");
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


    void SaveData(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, HttpServletResponse response) {
        try {
            Services supp = new Services();
            DataFormatter formatter = new DataFormatter(Locale.US);
            Statement stmt = null;
            ResultSet rset = null;
            int i = 0;
            int _bIndex_ = 0;
            int _instNo_ = 0;
            double db = 0.0D;
            String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String Query = "";
            String FirstName = "";
            String MiddleInitial = "";
            String LastName = "";
            String DOS = "";
            String DOB = "";
            int ClientIndex = 0;
            String gender = "";
            String Address = "";
            String StreetAddress2 = "";
            String City = "";
            String State = "";
            String ZipCode = "";
            String PhNumber = "";
            String Email = "";
            String Ethnicity = "";
            int MRN = 0;
            int PatientRegId = 0;
            String ExtendedMRN = "";
            String SelfPayChk = "0";
            String Status = "";
            String PriInsurance = "";
            String WorkCompPolicy = "";
            String MotorVehAccident = "";
            NumberFormat nf = new DecimalFormat("###,###,###.00");
            NumberFormat nf1 = new DecimalFormat("#####");
            String UniqueCode = "";
            String FileName = request.getParameter("FileName").trim();
            FileInputStream fileIn = null;
            fileIn = new FileInputStream("/sftpdrive/opt/" + FileName);

            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            for (i = 1; i <= totalrows; i++) {
                Row row = sheet.getRow(i);

                String dayDOS = "";
                String dayDOB = "";
                String monthDOS = "";
                String monthDOB = "";
                String yearDOS = "";
                String yearDOB = "";
                if (!formatter.formatCellValue(row.getCell(0)).equals("")) {
                    DOS = formatter.formatCellValue(row.getCell(0));
                    DOS = DOS.substring(0, 4) + "-" + DOS.substring(4, 6) + "-" + DOS.substring(6, 8) + " " + DOS.substring(8, 10) + ":" + DOS.substring(10, 12) + ":00".replaceAll("\n", "");
                } else {
                    DOS = "0000-00-00";
                }
                MRN = Integer.parseInt(formatter.formatCellValue(row.getCell(1)).replaceAll("\n", "").trim());

                LastName = formatter.formatCellValue(row.getCell(2)).replaceAll("\n", "");
                FirstName = formatter.formatCellValue(row.getCell(3)).replaceAll("\n", "");
                MiddleInitial = formatter.formatCellValue(row.getCell(4)).replaceAll("\n", "");

                if (!formatter.formatCellValue(row.getCell(5)).equals("")) {
                    DOB = formatter.formatCellValue(row.getCell(5));
                    DOB = DOB.substring(0, 4) + "-" + DOB.substring(4, 6) + "-" + DOB.substring(6, 8).replaceAll("\n", "");
                } else {
                    DOB = "0000-00-00";
                }
                gender = formatter.formatCellValue(row.getCell(6)).replaceAll("\n", "");
                if (gender.toUpperCase().equals("M")) {
                    gender = "MALE";
                } else if (gender.toUpperCase().equals("F")) {
                    gender = "FEMALE";
                } else {
                    gender = "MALE";
                }
                Address = formatter.formatCellValue(row.getCell(7)).replaceAll("\n", "");
                StreetAddress2 = formatter.formatCellValue(row.getCell(8)).replaceAll("\n", "");
                City = formatter.formatCellValue(row.getCell(9)).replaceAll("\n", "");
                State = formatter.formatCellValue(row.getCell(10)).replaceAll("\n", "");
                ZipCode = formatter.formatCellValue(row.getCell(11)).replaceAll("\n", "");
                PhNumber = formatter.formatCellValue(row.getCell(12)).replaceAll("\n", "");
                if (PhNumber.contains(".") || PhNumber.contains("-")) {
                    PhNumber = PhNumber.replaceAll(".", "").replace("-", "").replaceAll("\n", "");
                }
                Email = formatter.formatCellValue(row.getCell(15)).replaceAll("\n", "");

//                DOS = formatter.formatCellValue(row.getCell(2));
//                DOB = formatter.formatCellValue(row.getCell(3));

                int FoundMRN = 0;
                try {
                    Query = "Select COUNT(*) from oddasa.PatientReg where MRN = " + MRN;//+ " and DATE_FORMAT(DateofService,'%Y-%m-%d') = DATE_FORMAT('" + DOS + "','%Y-%m-%d') ";
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

//                out.println("FoundMRN " + FoundMRN + " <br>");
//                out.println(" i VAL --> " + i +" <br> ");
                if (FoundMRN > 0) {
                    out.println("******************************************************** <br> ");
                    String NAddress = "";
                    String NStreetAddress2 = "";
                    String NCity = "";
                    String NState = "";
                    String NZipCode = "";
                    Query = "SELECT IFNULL(Address,''), IFNULL(Address2,''),IFNULL(City,''),IFNULL(State,''),IFNULL(ZipCode,'') " +
                            " FROM oddasa.PatientReg WHERE MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        NAddress = rset.getString(1);
                        NStreetAddress2 = rset.getString(2);
                        NCity = rset.getString(3);
                        NState = rset.getString(4);
                        NZipCode = rset.getString(5);
                    }
                    rset.close();
                    stmt.close();

                    if (NAddress.equals("") || NStreetAddress2.equals("") || NCity.equals("") || NZipCode.equals("") || NState.equals("")) {
                        out.println("MRN " + MRN + " <br>");
                        out.println("Address " + Address + " <br>");
                        out.println("StreetAddress2 " + StreetAddress2 + " <br>");
                        out.println("City " + City + " <br>");
                        out.println("State " + State + " <br>");
                        out.println("ZipCode " + ZipCode + " <br>");
                        out.println("PhNumber " + PhNumber + " <br>");
                        out.println("Email " + Email + " <br>");

                        Query = "UPDATE oddasa.PatientReg SET Address = '" + Address + "', Address2 = '" + StreetAddress2 + "', City = '" + City + "', State = '" + State + "'," +
                                "ZipCode='" + ZipCode + "' WHERE MRN = " + MRN;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();
                        out.println("UPDATED %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% <br>");
                    }

                    out.println("******************************************************** <br> ");
                }
                /*if (FoundMRN > 0) {

                    if (String.valueOf(MRN).length() == 6 && String.valueOf(MRN).startsWith("3")) {
                        continue;
                    }

                    int VisitNumber = 0;
                    Query = "Select Id from oddasa.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        Query = "Select MAX(VisitNumber) + 1 from oddasa.PatientVisit where PatientRegId = " + PatientRegId;
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
                                "Insert into oddasa.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, CreatedDate, CreatedBy) \n "
                                        + "values (?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, MRN);
                        MainReceipt.setInt(2, PatientRegId);
                        MainReceipt.setInt(3, VisitNumber);
                        MainReceipt.setString(4, DOS);
                        MainReceipt.setString(5, DOS);
                        MainReceipt.setString(6, "excel.longview");
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
                        Query = "Update oddasa.PatientReg set DateofService = '" + DOS + "' where ID = " + PatientRegId;
                        stmt = conn.createStatement();
                        stmt.executeUpdate(Query);
                        stmt.close();

                    } catch (Exception e) {
                        out.println("Error in Update PatientReg DOS: " + e.getMessage());
                        return;
                    }
                } else {


                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oddasa.PatientReg (DateofService, CreatedDate, MRN, FirstName, LastName, MiddleInitial, DOB, Gender, Address, " +
                                        "StreetAddress2, City, State, ZipCode, PhNumber, Email, ClientIndex, Status, SelfPayChk) \n "
                                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
                        MainReceipt.setString(1, DOS);
                        MainReceipt.setString(2, DOS);
                        MainReceipt.setInt(3, MRN);
                        MainReceipt.setString(4, FirstName);
                        MainReceipt.setString(5, LastName);
                        MainReceipt.setString(6, MiddleInitial);
                        MainReceipt.setString(7, DOB);
                        MainReceipt.setString(8, gender);
                        MainReceipt.setString(9, Address);
                        MainReceipt.setString(10, StreetAddress2);
                        MainReceipt.setString(11, City);
                        MainReceipt.setString(12, State);
                        MainReceipt.setString(13, ZipCode);
                        MainReceipt.setString(14, PhNumber);
                        MainReceipt.setString(15, Email);
                        MainReceipt.setInt(16, 1);
                        MainReceipt.setInt(17, 0);
                        MainReceipt.setInt(18, 0);
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

                    Query = "Select Id from oddasa.PatientReg where MRN = " + MRN;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        PatientRegId = rset.getInt(1);
                    }
                    rset.close();
                    stmt.close();

                    try {
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oddasa.PatientVisit (MRN, PatientRegId, VisitNumber, DateofService, CreatedDate, CreatedBy) \n "
                                        + "values (?,?,?,?,?,?) ");
                        MainReceipt.setInt(1, MRN);
                        MainReceipt.setInt(2, PatientRegId);
                        MainReceipt.setInt(3, 1);
                        MainReceipt.setString(4, DOS);
                        MainReceipt.setString(5, DOS);
                        MainReceipt.setString(6, "excel.longview");
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
                        PreparedStatement MainReceipt = conn.prepareStatement(
                                "Insert into oddasa.PatientReg_Details (PatientRegId, MRN) \n "
                                        + "values (?,?) ");
                        MainReceipt.setInt(1, PatientRegId);
                        MainReceipt.setInt(2, MRN);
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
                                "Insert into oddasa.EmergencyInfo (PatientRegId) \n "
                                        + "values (?) ");
                        MainReceipt.setInt(1, PatientRegId);
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
                                "Insert into oddasa.RandomCheckInfo (PatientRegId) \n "
                                        + "values (?) ");
                        MainReceipt.setInt(1, PatientRegId);
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

                out.println(FirstName + "|" + LastName + "|" + DOS + "|" + DOB + "|" + ClientIndex + "|" + gender + "|" + MRN + "|" + ExtendedMRN + "|" + SelfPayChk + "|" + Status + "|" + PriInsurance + "|" + WorkCompPolicy + "|" + MotorVehAccident + "<br>");
*/
            }

            /*try
            {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "Insert into grace_er.PatientReg_test (FirstName, LastName,DOB,Gender,"
                                + " SelfPayChk,MRN ,ClientIndex ,DateofService,"
                                + "ExtendedMRN, Status ) \n "
                                + "values (?,?,?,?,?,?,?,?,?,?) ");
                MainReceipt.setString(1, FirstName);//Organization_MSISDN
                MainReceipt.setString(2, LastName);//Organization_Name
                MainReceipt.setString(3, DOB);//Organization_DOB
                MainReceipt.setString(4, gender);//Organization_CNIC
                MainReceipt.setInt(5, SelfPayChk);//Organization_CNIC_Expiry
                MainReceipt.setInt(6, MRN);//Organization_Product_Name
                MainReceipt.setInt(7, ClientIndex);//Organization_Address
                MainReceipt.setString(8,  DOS);//Organization_City
                MainReceipt.setString(9,  ExtendedMRN);//Organization_Region
                MainReceipt.setInt(10, Status);//Organizaiton_Registration_Date
                MainReceipt.executeUpdate();
                MainReceipt.close();
            }
            catch (Exception e)
            {
                out.println("<br>Error No.: 0014");
                out.println("<br>Error Is : Could not Upload Records From file ...!!! \n\n\n</b>");
                out.println(e);
                out.println("<input class=\"buttonERP\" type=button name=Back Value=\"  Back  \" onclick=history.back()>");
                out.println("</form></body></html>");
                out.flush();
                out.close();
                return;
            }*/


        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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


    public String GetMonth(String month) {

        System.out.println("Initial val " + month);
        String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (int i = 1; i <= 12; ++i) {
            if (month.equals(Months[i - 1])) {
                month = String.valueOf(i);
                System.out.println("Month VAl" + month);
            }
        }
        return month;
    }
}
