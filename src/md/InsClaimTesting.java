package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.pb.x12.Context;
import org.pb.x12.Loop;
import org.pb.x12.Segment;
import org.pb.x12.X12;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class InsClaimTesting extends HttpServlet {
    private static boolean isValid(final String npi) {
//Picked last digit for Matching check digit at the end
        final char lastDigit = npi.charAt(9);
//contains doubled digits and unaffected digits
        final List<Integer> alternateDigits = alternativeDigitsDoubled(npi.substring(0, 9));
        int sum = 0;
//Adding all numerals
        for (final Integer num : alternateDigits) sum += sumOfDigits(num);
//Add constant 24 as mentioned in algo
        final int total = sum + 24;
//Picked unitPlaceDigit of total
        final int unitPlaceDigit = total % 10;
//Subtract from next higher number ending in zero
        final int checkDigit = (unitPlaceDigit != 0) ? (10 - unitPlaceDigit) : unitPlaceDigit;
        return Character.getNumericValue(lastDigit) == checkDigit;
    }

    private static List<Integer> alternativeDigitsDoubled(final String str) {
        final List<Integer> numerals = new ArrayList<>();
        for (int i = 0; i < str.length(); ++i)
            //doubled every alternate digit
            if (i % 2 == 0) numerals.add(2 * Character.getNumericValue(str.charAt(i)));
                //added unaffected digits
            else numerals.add(Character.getNumericValue(str.charAt(i)));
        return numerals;
    }

    private static int sumOfDigits(int num) {
        int sum = 0;
//Breaking number into single Digits and Adding them
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9.]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    static boolean isEmpty(final String str) {
        return (str == null) || (str.length() <= 0);
    }

    public static int getAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears();
    }

    public static HashMap<Integer, String> claim_status_list(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_status_list where status=0";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static HashMap<Integer, String> claim_ppt_list(String aa, Connection Conn) {

        HashMap<Integer, String> hm = new HashMap<Integer, String>();


        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int Days = 0;
        int id = 0;
        String name = null;
        try {
            Query = "select id,descname from claim_ppt_list where status=0";
            hstmt = Conn.createStatement();

            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                id = hrset.getInt(1);
                name = hrset.getString(2);
                hm.put(id, name);

            }


            hrset.close();
            hstmt.close();
            return hm;
        } catch (Exception e) {
            return hm;
        }
    }

    public static String logging(int userindex, int filestatus, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into fileactivity(fileindex,created,userindex,filestatus) " +
                    " values('" + indexptr + "',now()," + userindex + ",'" + filestatus + "') ";
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public static String createnote(int userindex, String note, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " insert into claim_note(note,userindex,createddate) " +
                    " values('" + note + "','" + userindex + "',now()) ";
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public static String markuser(int userindex, int filestatus, int indexptr, Connection conn) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        try {
            hstmt = conn.createStatement();
            Query = " update  filelogs_sftp set processby=" + userindex + " where processby=0 and id=" + indexptr;
            hstmt.execute(Query);
        } catch (Exception ee) {


        }

        return null;
    }

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        String UserIndex = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String ActionID;
        ServletContext context = null;
        context = getServletContext();
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
                UserIndex = session.getAttribute("UserIndex").toString();

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
            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + FacilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();
            System.out.println("ACTION ID " + ActionID);
            if (ActionID.equals("Addinfo")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Add Info Institutional Claim Screen", "Open Institutional Claim Screen", FacilityIndex);
                Addinfo(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("Institutional_EDIFile") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "EDI Institutional File Formation", "Institutional EDI File Making", FacilityIndex);
                Institutional_EDIFile(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else if (ActionID.compareTo("Scrubber") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Save Claim Institutional", "Saving all fields in the claim section", FacilityIndex);
                ScrubberIns(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("SaveClaim") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Save Institutional Claim", "Saving all fields in the Institutional claim section", FacilityIndex);
                SaveClaim(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("OpenUB04") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Open Institutional UB04", "OpenUB04 Institutional Form and Filling", FacilityIndex);
                OpenUB04(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else {
                out.println("Under Development");
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

    public void Addinfo(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

        String Head = "";
        int VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String AcctNo = request.getParameter("AcctNo").trim();
        String ClaimType = request.getParameter("ClaimType").trim();
        int FoundClaim = 0;
        StringBuffer Day = new StringBuffer();
        StringBuffer Month = new StringBuffer();
        StringBuffer Year = new StringBuffer();
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String dbname = "";
        String Facilityname = "";
        String ClaimNo = "";

        String ChargeMasterTableName = "";
        StringBuffer Insurancelist = new StringBuffer();
        StringBuffer Providerlist = new StringBuffer();
        StringBuffer OperatingProvider = new StringBuffer();
        StringBuffer claimstatuslist = new StringBuffer();
        StringBuffer claimpptlist = new StringBuffer();
        StringBuffer PatientStatus = new StringBuffer();
        StringBuilder AdmissionType = new StringBuilder();
        StringBuilder AdmissionSource = new StringBuilder();
        StringBuilder DelayReason = new StringBuilder();
        StringBuilder TimeHours = new StringBuilder();
        StringBuilder AttendingProvider = new StringBuilder();
        StringBuilder BillingProvider = new StringBuilder();
        StringBuilder SupervisingProvider = new StringBuilder();
        StringBuilder OrderingProvider = new StringBuilder();
        StringBuilder ReferringProvider = new StringBuilder();
        StringBuilder ChargeList = new StringBuilder();
        StringBuilder _DescriptionFrom = new StringBuilder();
        StringBuilder _AdmissionHourAddInfo = new StringBuilder();
        StringBuilder _DischargeHourAddInfo = new StringBuilder();
        StringBuilder _EmploymentStatusAddInfo = new StringBuilder();
        StringBuilder _AutoAccidentAddInfo = new StringBuilder();
        StringBuilder _OtherAccidentAddInfo = new StringBuilder();
        StringBuilder _ReleaseInfoAddInfo = new StringBuilder();
        StringBuilder _ProvAccAssigAddInfo = new StringBuilder();
        StringBuilder _AssofBenifitAddInfo = new StringBuilder();
        StringBuilder _POAInfoCodes = new StringBuilder();
        StringBuilder InfoCodeExtCauseInj = new StringBuilder();
        StringBuilder InfoCodeReasVisit = new StringBuilder();
        StringBuilder InfoCodeOthDiag = new StringBuilder();
        StringBuilder InfoCodeOthProcedure = new StringBuilder();
        StringBuilder InfoCodeOccSpan = new StringBuilder();
        StringBuilder InfoCodeOcc = new StringBuilder();
        StringBuilder InfoCodeValueCode = new StringBuilder();
        StringBuilder InfoCodeCondCode = new StringBuilder();
        StringBuilder ChargeOption = new StringBuilder();

        String note = "";
        String PatientName = "";
        String DOB = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String DOS = "";
        String MRN = "";
        int DoctorId = 0;
        int SelfPayChk = 0;
        String PriInsuranceName = "";
        String DOSFormat = "";
        String GrpNumber = "";
        String MemId = "";
        String PriInsuranceNameId = "";
        String SecondryInsuranceId = "";
        String SecondryInsurance = "";
        String aa = "0";
        int ClaimInfoMasterId = 0;
        int ChargesCount = 0;
        String _RefNum = "";
        String _TypeBillText = "131";
        String _UploadDate = "";
        String _AttendingProvider = "";
        String _BillingProvider = "";
        String _OrderingProvider = "";
        String _SupervisingProvider = "";
        String _ReferringProvider = "";
        String _PolicyType = "";
        String _SecondaryInsuranceMemId = "";
        String _SecondaryInsuranceGrpNumber = "";
        String _OperatingProvider = "";
        String _CreatedDate = "";
        String DescriptionFrom = "";
        String StatmentCoverFromDateAddInfo = "";
        String StatmentCoverToDateAddInfo = "";
        String AdmissionDateAddInfo = "";
        String AdmissionHourAddInfo = "";
        String AdmissionTypeAddInfo = "";
        String AdmissionSourceAddInfo = "";
        String DischargeHourAddInfo = "";
        String PatientStatusAddInfo = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String PPSAddInfo = "";
        String RemarksAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String ReleaseInfoAddInfo = "";
        String AssofBenifitAddInfo = "";
        String ProvAccAssigAddInfo = "";
        String AutoAccStateDive = "";
        String PrincipalDiagInfoCodes = "";
        String POAInfoCodes = "";
        String AdmittingDiagInfoCodes = "";
        String PrincipalProcedureInfoCodes = "";
        String PrincipalProcedureDateInfoCodes = "";
        String EDI_GENERATOR = "";
        String PatientRelationtoPrimary = "";
        String PatientRelationtoSec = "";
        try {

            Query = "Select Id, IFNULL(UPPER(ChargeOption),'') " +
                    "from oe.ChargeOption where status = 1";
            stmt = conn.createStatement();
            for (rset = stmt.executeQuery(Query); rset.next(); ) {
                ChargeOption.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            note = "Open Add info and load pdf";

            Query = "Select IFNULL(ChargeMasterTableName,''),IFNULL(EDI_GENERATOR,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ChargeMasterTableName = rset.getString(1);
                EDI_GENERATOR = rset.getString(2);
            }
            rset.close();
            stmt.close();

            Query = " Select CONCAT(IFNULL(b.Title,''),' ',IFNULL(b.FirstName,''),' ',IFNULL(b.MiddleInitial,''),' ',IFNULL(b.LastName,'')), " +
                    " IFNULL(DATE_FORMAT(b.DOB,'%m/%d/%Y'),''), IFNULL(b.PhNumber,''),  IFNULL(b.Email,'-'), " +
                    " CONCAT(IFNULL(b.Address,''),', ',IFNULL(b.City,''),',',IFNULL(b.State,''),',',IFNULL(b.County,''),',',IFNULL(b.Country,''),',',IFNULL(b.ZipCode,''))," +
                    " IFNULL(DATE_FORMAT(a.DateofService,'%m/%d/%Y %T'),DATE_FORMAT(b.CreatedDate,'%m/%d/%Y %T')), " +
                    " b.MRN, b.Status, IFNULL(b.SelfPayChk,0), DATE_FORMAT(a.DateofService,'%Y-%m-%d'), IFNULL(a.DoctorId,b.DoctorsName)" +
                    " from " + Database + ".PatientVisit a  " +
                    " Left Join " + Database + ".PatientReg b on a.PatientRegId = b.Id " +
                    " where b.ID = " + PatientRegId + " and a.Id = " + VisitId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientName = rset.getString(1);
                DOB = rset.getString(2);
                PhNumber = rset.getString(3);
                Email = rset.getString(4);
                Address = rset.getString(5);
                DOS = rset.getString(6);
                MRN = rset.getString(7);
                SelfPayChk = rset.getInt(9);
                DOSFormat = rset.getString(10);
                DoctorId = rset.getInt(11);

            }
            rset.close();
            stmt.close();

            if (SelfPayChk == 1) {
                Query = " Select IFNULL(b.PayerName,''), IFNULL(a.GrpNumber,''), IFNULL(a.MemId,''), IFNULL(a.PriInsuranceName,''), " +
                        " IFNULL(a.SecondryInsurance,''), IFNULL(c.PayerName,'')," +
                        "IFNULL(PatientRelationtoPrimary,''),IFNULL(PatientRelationshiptoSecondry,'')  " +
                        " from " + Database + ".InsuranceInfo a " +
                        " LEFT JOIN oe_2.ProfessionalPayers b on a.PriInsuranceName = b.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers c on a.SecondryInsurance = c.Id " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PriInsuranceName = rset.getString(1);
                    GrpNumber = rset.getString(2);
                    MemId = rset.getString(3);
                    PriInsuranceNameId = rset.getString(4);
                    SecondryInsuranceId = rset.getString(5);
                    SecondryInsurance = rset.getString(6);
                    PatientRelationtoPrimary = rset.getString(7);
                    PatientRelationtoSec = rset.getString(8);
                }
                rset.close();
                stmt.close();

                if (PatientRelationtoSec.equals("-1"))
                    PatientRelationtoSec = "";
            }

            Query1 = "SELECT dbname,IFNULL(FullName,'') FROM oe.clients WHERE id=" + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query1);
            if (rset.next()) {
                Facilityname = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                    " and VisitId = " + VisitId + " and ClaimType = " + ClaimType;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundClaim = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundClaim > 0) {
                Query = "Select ClaimNumber from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                        " and VisitId = " + VisitId + " and ClaimType = " + ClaimType + " order by Id desc ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimNo = rset.getString(1);
                }
                rset.close();
                stmt.close();

                if (ClaimType.equals("1")) {
                    Head = "Institutional";
                } else if (ClaimType.equals("2")) {
                    Head = "Professional";
                } else {
                    Head = "Claim";
                }


                Query = " Select a.Id,IFNULL(a.RefNumber,''), IFNULL(a.TypeBillText,''),IFNULL(a.UploadDate,''), IFNULL(a.AttendingProvider,''), " +
                        " IFNULL(a.BillingProviders,''), IFNULL(a.PolicyType,''), " +
                        " IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(a.SecondaryInsuranceGrpNumber,''), " +
                        " IFNULL(a.OperatingProvider,''), DATE_FORMAT(a.CreatedDate,'%m%d%y'),IFNULL(a.PriInsuranceNameId,''), IFNULL(a.SecondaryInsuranceId,''), " +
                        " IFNULL(b.PayerName,''), IFNULL(c.PayerName,''), IFNULL(a.GrpNumber,''),IFNULL(a.ReferringProvider,''),  " +
                        " IFNULL(a.SupervisingProvider,''), IFNULL(a.OrderingProvider,'') " +
                        "from " + Database + ".ClaimInfoMaster a " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList b on a.PriInsuranceNameId = b.Id " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList c on a.SecondaryInsuranceId = c.Id  " +
                        " LEFT JOIN oe_2.ProfessionalPayers b on a.PriInsuranceNameId = b.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers c on a.SecondaryInsuranceId = c.Id  " +
                        "  where a.ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    _RefNum = rset.getString(2);
                    _TypeBillText = rset.getString(3);
                    _UploadDate = rset.getString(4);
                    _AttendingProvider = rset.getString(5);
                    _BillingProvider = rset.getString(6);
                    _ReferringProvider = rset.getString(17);
                    _SupervisingProvider = rset.getString(18);
                    _OrderingProvider = rset.getString(19);
                    _PolicyType = rset.getString(7);
                    _SecondaryInsuranceMemId = rset.getString(8);
                    _SecondaryInsuranceGrpNumber = rset.getString(9);
                    _OperatingProvider = rset.getString(10);
                    _CreatedDate = rset.getString(11);
                    PriInsuranceNameId = rset.getString(12);
                    SecondryInsuranceId = rset.getString(13);
                    PriInsuranceName = rset.getString(14);
                    SecondryInsurance = rset.getString(15);
                    if (GrpNumber.equals("")) {
                        GrpNumber = rset.getString(16);
                    }

                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) " +
                        "FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                AttendingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_AttendingProvider.equals(rset.getString(1)))
                        AttendingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        AttendingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                BillingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_BillingProvider.equals(rset.getString(1)))
                        BillingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        BillingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                OperatingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_OperatingProvider.equals(rset.getString(1)))
                        OperatingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        OperatingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                SupervisingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_SupervisingProvider.equals(rset.getString(1)))
                        SupervisingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        SupervisingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                OrderingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_OrderingProvider.equals(rset.getString(1)))
                        OrderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        OrderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                ReferringProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_ReferringProvider.equals(rset.getString(1)))
                        ReferringProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        ReferringProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.DescriptionFrom,''), IFNULL(a.ServiceDate,''), IFNULL(a.HCPCS,''), " + //3
                        "IFNULL(a.Mod1,''), IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), IFNULL(a.Mod4,''), IFNULL(a.RevCode,''), " + //8
                        "CASE WHEN a.DescriptionFrom = 1 THEN b.ShortDescription WHEN a.DescriptionFrom = 0 THEN c.Description ELSE c.Description END, " + //9
                        "IFNULL(a.UnitPrice,'0'), IFNULL(a.Units,'0'), IFNULL(a.Amount,'0.0'), IFNULL(d.descname,''), " + //13
                        "IFNULL(a.ChargesStatus,''), " +//14
                        "IFNULL(e.ChargeOption,'') " +//15
                        "from " + Database + ".ClaimChargesInfo a " +
                        "LEFT JOIN oe." + ChargeMasterTableName + " b on a.HCPCS = b.CPTCode " +
                        "LEFT JOIN oe.RevenueCode c on a.RevCode = c.Codes " +
                        "LEFT JOIN oe.claim_status_list d on a.ChargesStatus = d.Id " +
                        "LEFT JOIN oe.ChargeOption e on a.ChargesStatus = e.Id " +
                        "where a.Status = 0 and a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and a.ClaimNumber = '" + ClaimNo + "'";
                System.out.println("EXIS QUERY " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DescriptionFrom = rset.getString(1);
                    ChargeList.append("<tr>");
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='ServDate_" + ChargesCount + "'>" + rset.getString(2) + "</td>"); // ServiceDate
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='CPT_" + ChargesCount + "'>" + rset.getString(3) + "</td>"); //HCPCS
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Mod1_" + ChargesCount + "'>" + rset.getString(4) + "</td>"); // Mod1
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Mod2_" + ChargesCount + "'>" + rset.getString(5) + "</td>");// Mod2
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Mod3_" + ChargesCount + "'>" + rset.getString(6) + "</td>");//Mod3
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Mod4_" + ChargesCount + "'>" + rset.getString(7) + "</td>");//Mod4
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='RevCod_" + ChargesCount + "'>" + rset.getString(8) + "</td>");//RevCode
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='CPTDescrip_" + ChargesCount + "'>" + rset.getString(9) + "</td>");//Description
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='UnitPrice_" + ChargesCount + "' >" + rset.getDouble(10) + "</td>");//UnitPrice
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Units_" + ChargesCount + "'>" + rset.getDouble(11) + "</td>");//Units
                    ChargeList.append("<td contentEditable='true' data-toggle='tooltip' title='' data-original-title='' id='Amount_" + ChargesCount + "'>" + rset.getDouble(12) + "</td>");//Amount
                    ChargeList.append("<td id='ChargeStatus_" + ChargesCount + "'>" + rset.getString(15) + "</td>");
                    ChargeList.append("<td style=\"display:none;\">" + rset.getString(14) + "</td>");
                    ChargeList.append("<td><button type='button' class='btn btn-danger btn-xs' onclick='deleteCharge(this)'><span class='glyphicon glyphicon-trash'></span></button></td>");
                    ChargeList.append("</tr>");
                    ChargesCount++;
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(StatmentCoverFromDateAddInfo,''), IFNULL(StatmentCoverToDateAddInfo,''), IFNULL(AdmissionDateAddInfo,''), " +
                        "IFNULL(AdmissionHourAddInfo,''), IFNULL(AdmissionTypeAddInfo,''), " +
                        "IFNULL(AdmissionSourceAddInfo,''), IFNULL(DischargeHourAddInfo,''), IFNULL(PatientStatusAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'')," +
                        "IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(OtherAccidentAddInfo,''), IFNULL(PPSAddInfo,''), " +
                        "IFNULL(RemarksAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), IFNULL(ReleaseInfoAddInfo,''), IFNULL(AssofBenifitAddInfo,''), " +
                        "IFNULL(ProvAccAssigAddInfo,'') from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        "and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    StatmentCoverFromDateAddInfo = rset.getString(1);
                    StatmentCoverToDateAddInfo = rset.getString(2);
                    AdmissionDateAddInfo = rset.getString(3);
                    AdmissionHourAddInfo = rset.getString(4);
                    AdmissionTypeAddInfo = rset.getString(5);
                    AdmissionSourceAddInfo = rset.getString(6);
                    DischargeHourAddInfo = rset.getString(7);
                    PatientStatusAddInfo = rset.getString(8);
                    DelayReasonCodeAddInfo = rset.getString(9);
                    EmploymentStatusAddInfo = rset.getString(10);
                    AutoAccidentAddInfo = rset.getString(11);
                    OtherAccidentAddInfo = rset.getString(12);
                    PPSAddInfo = rset.getString(13);
                    RemarksAddInfo = rset.getString(14);
                    AutoAccident_StateAddInfo = rset.getString(15);
                    ReleaseInfoAddInfo = rset.getString(16);
                    AssofBenifitAddInfo = rset.getString(17);
                    ProvAccAssigAddInfo = rset.getString(18);
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(PrincipalDiagInfoCodes,''), IFNULL(POAInfoCodes,''), IFNULL(AdmittingDiagInfoCodes,''), " +
                        "IFNULL(PrincipalProcedureInfoCodes,''), IFNULL(PrincipalProcedureDateInfoCodes,'') " +
                        "from " + Database + ".ClaimInformationCode where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PrincipalDiagInfoCodes = rset.getString(1);
                    POAInfoCodes = rset.getString(2);
                    AdmittingDiagInfoCodes = rset.getString(3);
                    PrincipalProcedureInfoCodes = rset.getString(4);
                    PrincipalProcedureDateInfoCodes = rset.getString(5);

                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(b.Description,'') from " + Database + ".ClaimInfoCodeExtCauseInj a " +
                        " LEFT JOIN oe.DiagnosisCodes b on a.Code = b.Code where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeExtCauseInj.append("<tr>");
                    InfoCodeExtCauseInj.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeExtCauseInj.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeExtCauseInj.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteExternalCause(this)'></i></td>");
                    InfoCodeExtCauseInj.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(b.Description,'') from " + Database + ".ClaimInfoCodeReasVisit a " +
                        " LEFT JOIN oe.DiagnosisCodes b on a.Code = b.Code where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeReasVisit.append("<tr>");
                    InfoCodeReasVisit.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeReasVisit.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeReasVisit.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteReasonVisit(this)'></i></td>");
                    InfoCodeReasVisit.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(b.Description,''), " +
                        "CASE WHEN a.PQA = 'Y' THEN 'Y - Yes' WHEN a.PQA = '1' THEN '1 - Unreported' WHEN a.PQA = 'N' THEN 'N - No'" +
                        " WHEN a.PQA = 'U' THEN 'U - Unknown' WHEN a.PQA = 'W' THEN 'W - Undetermined' ELSE '' END from " + Database + ".ClaimInfoCodeOthDiag a " +
                        " LEFT JOIN oe.DiagnosisCodes b on a.Code = b.Code where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeOthDiag.append("<tr>");
                    InfoCodeOthDiag.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeOthDiag.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeOthDiag.append("<td>" + rset.getString(3) + "</td>");
                    InfoCodeOthDiag.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteOtherDiagnosis(this)'></i></td>");
                    InfoCodeOthDiag.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(a.Date,''), IFNULL(b.Description,'') from " + Database + ".ClaimInfoCodeOthProcedure a " +
                        " LEFT JOIN oe.ProcedureCodes b on a.Code = b.ProcedureCode where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeOthProcedure.append("<tr>");
                    InfoCodeOthProcedure.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeOthProcedure.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeOthProcedure.append("<td>" + rset.getString(3) + "</td>");
                    InfoCodeOthProcedure.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteOtherProcedure(this)'></i></td>");
                    InfoCodeOthProcedure.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(a.FromDate,''), IFNULL(a.ToDate,''), IFNULL(b.Description,'') " +
                        " from " + Database + ".ClaimInfoCodeOccSpan a " +
                        " LEFT JOIN oe.OccurenceSpanCodes b on a.Code = b.OccurenceSpanCode where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeOccSpan.append("<tr>");
                    InfoCodeOccSpan.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeOccSpan.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeOccSpan.append("<td>" + rset.getString(3) + "</td>");
                    InfoCodeOccSpan.append("<td>" + rset.getString(4) + "</td>");
                    InfoCodeOccSpan.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteOccuranceSpanCode(this)'></i></td>");
                    InfoCodeOccSpan.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(a.Date,''), IFNULL(b.Description,'') " +
                        " from " + Database + ".ClaimInfoOccurance a " +
                        " LEFT JOIN oe.OcurrenceCodes b on a.Code = b.OccuranceCode where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeOcc.append("<tr>");
                    InfoCodeOcc.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeOcc.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeOcc.append("<td>" + rset.getString(3) + "</td>");
                    InfoCodeOcc.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteOccuranceCode(this)'></i></td>");
                    InfoCodeOcc.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(a.Amount,''), IFNULL(b.Description,'') " +
                        " from " + Database + ".ClaimInfoCodeValueCode a " +
                        " LEFT JOIN oe.ValueCodes b on a.Code = b.ValueCode where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeValueCode.append("<tr>");
                    InfoCodeValueCode.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeValueCode.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeValueCode.append("<td>" + rset.getString(3) + "</td>");
                    InfoCodeValueCode.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteValueCode(this)'></i></td>");
                    InfoCodeValueCode.append("</tr>");
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(a.Code,''), IFNULL(b.Description,'') " +
                        " from " + Database + ".ClaimInfoCodeConditionCode a " +
                        " LEFT JOIN oe.ConditionCodes b on a.Code = b.ConditionCode where a.Status = 0 and " +
                        " a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    InfoCodeCondCode.append("<tr>");
                    InfoCodeCondCode.append("<td>" + rset.getString(1) + "</td>");
                    InfoCodeCondCode.append("<td>" + rset.getString(2) + "</td>");
                    InfoCodeCondCode.append("<td><i class='fa fa-trash-o fa-fw' aria-hidden='true' onClick='DeleteConditionCode(this)'></i></td>");
                    InfoCodeCondCode.append("</tr>");
                }
                rset.close();
                stmt.close();


            } else {

                Query = "SELECT SUBSTRING(IFNULL(MAX(Convert(Substring(ClaimNumber,4,8) ,UNSIGNED INTEGER)),0)+10000001,2,7) " +
                        "FROM " + Database + ".ClaimInfoMaster";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimNo = rset.getString(1);
                }
                rset.close();
                stmt.close();

                if (ClaimNo.equals("0000001")) {
                    ClaimNo = "0001081";
                }
                if (ClaimType.equals("1")) {
                    Head = "Institutional";
                    ClaimNo = "CI-" + ClaimNo;
                } else if (ClaimType.equals("2")) {
                    Head = "Professional";
                    ClaimNo = "CP-" + ClaimNo;
                } else {
                    Head = "Claim";
                    ClaimNo = "C-" + ClaimNo;
                }

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                AttendingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (DoctorId == rset.getInt(1))
                        AttendingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        AttendingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                BillingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (DoctorId == rset.getInt(1))
                        BillingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        BillingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                OperatingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    OperatingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                SupervisingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_SupervisingProvider.equals(rset.getString(1)))
                        SupervisingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        SupervisingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                OrderingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_OrderingProvider.equals(rset.getString(1)))
                        OrderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        OrderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                ReferringProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_ReferringProvider.equals(rset.getString(1)))
                        ReferringProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        ReferringProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
                }
                rset.close();
                stmt.close();
            }
            if (DescriptionFrom.equals("1")) {
                _DescriptionFrom.append("<option value=''>Select Any </option> <option value='1' selected> CPT/HCPCS </option><option value='0' >REVCODE </option> ");
            } else if (DescriptionFrom.equals("0")) {
                _DescriptionFrom.append("<option value=''>Select Any </option> <option value='1' > CPT/HCPCS </option><option value='0' selected>REVCODE </option> ");
            } else {
                _DescriptionFrom.append("<option value=''>Select Any </option> <option value='1' selected > CPT/HCPCS </option><option value='0'>REVCODE </option> ");
            }
            if (AutoAccidentAddInfo.equals("1")) {
                AutoAccStateDive = "#AutoAccStateDive{display:block;}";
            } else {
                AutoAccStateDive = "#AutoAccStateDive{display:none;}";
            }

            HashMap<Integer, String> hm2 = new HashMap<Integer, String>();
            hm2 = claim_status_list("a", conn);
            Set set2 = hm2.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();
                claimstatuslist.append("<option class=Inner value=\"" + entry2.getKey() + "\"  >" + entry2.getValue().toString() + "</option>");

            }

            Query = "Select 24Hour, TimeLimit from oe.TimeHours where Status = 1";
            hstmt = conn.createStatement();
            _AdmissionHourAddInfo.append("<option class=Inner value=\"\"></option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (AdmissionHourAddInfo.equals(hrset.getString(1)))
                    _AdmissionHourAddInfo.append("<option class=Inner value=\"" + hrset.getString(1).trim() + "\" selected>[" + hrset.getString(1) + "] " + hrset.getString(2) + "</option>");
                else
                    _AdmissionHourAddInfo.append("<option class=Inner value=\"" + hrset.getString(1).trim() + "\">[" + hrset.getString(1) + "] " + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();

            Query = "Select AdmissionTypeCode, AdmissionType from oe.AdmissionType where Status = 1";
            hstmt = conn.createStatement();
            AdmissionType.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (AdmissionTypeAddInfo.equals(hrset.getString(1)))
                    AdmissionType.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
                else
                    AdmissionType.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();


            Query = "Select AdmissionSourceCode, AdmissionSource from oe.AdmissionSource where Status = 1";
            hstmt = conn.createStatement();
            AdmissionSource.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (AdmissionSourceAddInfo.equals(hrset.getString(1)))
                    AdmissionSource.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
                else
                    AdmissionSource.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();

            Query = "Select 24Hour, TimeLimit from oe.TimeHours where Status = 1";
            hstmt = conn.createStatement();
            _DischargeHourAddInfo.append("<option class=Inner value=\"\"></option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (DischargeHourAddInfo.equals(hrset.getString(1)))
                    _DischargeHourAddInfo.append("<option class=Inner value=\"" + hrset.getString(1).trim() + "\" selected>[" + hrset.getString(1) + "] " + hrset.getString(2) + "</option>");
                else
                    _DischargeHourAddInfo.append("<option class=Inner value=\"" + hrset.getString(1).trim() + "\">[" + hrset.getString(1) + "] " + hrset.getString(2) + "</option>");
            }
            hrset.close();
            hstmt.close();

            Query = "Select Code, Description from oe.PatientDischargeStatus where Status = 1";
            hstmt = conn.createStatement();
            PatientStatus.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (PatientStatusAddInfo.equals(hrset.getString(1)))
                    PatientStatus.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");
                else
                    PatientStatus.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(1) + " - " + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            Query = "Select DelayReasonCode, DelayReason from oe.DelayReason where Status = 1";
            hstmt = conn.createStatement();
            DelayReason.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (DelayReasonCodeAddInfo.equals(hrset.getString(1)))
                    DelayReason.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    DelayReason.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            HashMap<String, String> HMYesNo = new HashMap<String, String>();
            HMYesNo.put("1", "YES");
            HMYesNo.put("0", "NO");

            Set SetEmpSt = HMYesNo.entrySet();
            Iterator ItrEmpSt = SetEmpSt.iterator();
            _EmploymentStatusAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrEmpSt.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrEmpSt.next();
                if (entry.getKey().equals((EmploymentStatusAddInfo))) {
                    _EmploymentStatusAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _EmploymentStatusAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }
            Set SetAuAcc = HMYesNo.entrySet();
            Iterator ItrAuAcc = SetAuAcc.iterator();
            _AutoAccidentAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrAuAcc.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrAuAcc.next();
                if (entry.getKey().equals((AutoAccidentAddInfo))) {
                    _AutoAccidentAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _AutoAccidentAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            Set SetOthAcc = HMYesNo.entrySet();
            Iterator ItrOthAcc = SetOthAcc.iterator();
            _OtherAccidentAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrOthAcc.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrOthAcc.next();
                if (entry.getKey().equals((OtherAccidentAddInfo))) {
                    _OtherAccidentAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _OtherAccidentAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            HashMap<String, String> HMRelInfo = new HashMap<String, String>();
            HMRelInfo.put("IC", "Informed Consent ");
            HMRelInfo.put("Y", "YES");
            Set SetRelInfo = HMRelInfo.entrySet();
            Iterator ItrRelInfo = SetRelInfo.iterator();
            _ReleaseInfoAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrRelInfo.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrRelInfo.next();
                if (entry.getKey().equals(ReleaseInfoAddInfo)) {
                    _ReleaseInfoAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _ReleaseInfoAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            HashMap<String, String> HMAssBen = new HashMap<String, String>();
            HMAssBen.put("Y", "YES");
            HMAssBen.put("N", "NO");
            HMAssBen.put("PR", "Patient Refuses");
            Set SetAssBen = HMAssBen.entrySet();
            Iterator ItrSetAssBen = SetAssBen.iterator();
            _AssofBenifitAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrSetAssBen.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrSetAssBen.next();
                if (entry.getKey().equals(AssofBenifitAddInfo)) {
                    _AssofBenifitAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _AssofBenifitAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }


            HashMap<String, String> HMAccAss = new HashMap<String, String>();
            HMAccAss.put("Y", "YES");
            HMAccAss.put("N", "NO");
            HMAccAss.put("D", "Default ");
            Set SetAccAss = HMAccAss.entrySet();
            Iterator ItrAccAss = SetAccAss.iterator();
            _ProvAccAssigAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrAccAss.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrAccAss.next();
                if (entry.getKey().equals(ProvAccAssigAddInfo)) {
                    _ProvAccAssigAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _ProvAccAssigAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            HashMap<String, String> HMPOA = new HashMap<String, String>();
            HMPOA.put("1", "1 - Unreported");
            HMPOA.put("Y", "Y - Yes");
            HMPOA.put("N", "N - No");
            HMPOA.put("U", "U - Unknown");
            HMPOA.put("W", "W - Undetermined");
            Set SetPOA = HMPOA.entrySet();
            Iterator ItrPOA = SetPOA.iterator();
            _POAInfoCodes.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrPOA.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrPOA.next();
                if (entry.getKey().equals(POAInfoCodes)) {
                    _POAInfoCodes.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _POAInfoCodes.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            Services.GetCalendar(Day, Month, Year);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientName", PatientName.toString());
            Parser.SetField("PhNumber", PhNumber.toString());
            Parser.SetField("Email", Email.toString());
            Parser.SetField("Address", Address.toString());
            Parser.SetField("DOS", DOS.toString());
            Parser.SetField("MRN", MRN.toString());
            Parser.SetField("AcctNo", AcctNo.toString());
            Parser.SetField("PriInsuranceName", PriInsuranceName.toString());
            Parser.SetField("GrpNumber", GrpNumber.toString());
            Parser.SetField("MemId", MemId.toString());
            Parser.SetField("Facilityname", Facilityname.toString());
            Parser.SetField("Providerlist", Providerlist.toString());
            Parser.SetField("OperatingProvider", OperatingProvider.toString());
            Parser.SetField("PatientStatus", PatientStatus.toString());
            Parser.SetField("AdmissionType", AdmissionType.toString());
            Parser.SetField("TimeHours", TimeHours.toString());
            Parser.SetField("AdmissionSource", AdmissionSource.toString());
            Parser.SetField("DelayReason", DelayReason.toString());
            Parser.SetField("ClaimNo", ClaimNo);
            Parser.SetField("claimstatuslist", claimstatuslist.toString());
            Parser.SetField("DOSFormat", DOSFormat);
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("PriInsuranceNameId", String.valueOf(PriInsuranceNameId));
            Parser.SetField("SecondryInsuranceId", String.valueOf(SecondryInsuranceId));
            Parser.SetField("SecondryInsurance", String.valueOf(SecondryInsurance));
            Parser.SetField("ClaimType", String.valueOf(ClaimType));
            Parser.SetField("Head", String.valueOf(Head));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));
            Parser.SetField("_RefNum", String.valueOf(_RefNum));
            Parser.SetField("_TypeBillText", String.valueOf(_TypeBillText));
            Parser.SetField("_UploadDate", String.valueOf(_UploadDate));
            Parser.SetField("AttendingProvider", String.valueOf(AttendingProvider));
            Parser.SetField("SupervisingProvider", String.valueOf(SupervisingProvider));
            Parser.SetField("OrderingProvider", String.valueOf(OrderingProvider));
            Parser.SetField("BillingProvider", String.valueOf(BillingProvider));
            Parser.SetField("ReferringProvider", String.valueOf(ReferringProvider));
            Parser.SetField("_PolicyType", String.valueOf(_PolicyType));
            Parser.SetField("_SecondaryInsuranceMemId", String.valueOf(_SecondaryInsuranceMemId));
            Parser.SetField("_SecondaryInsuranceGrpNumber", String.valueOf(_SecondaryInsuranceGrpNumber));
            Parser.SetField("_OperatingProvider", String.valueOf(_OperatingProvider));
            Parser.SetField("_CreatedDate", String.valueOf(_CreatedDate));
            Parser.SetField("ChargeList", String.valueOf(ChargeList));
            Parser.SetField("_DescriptionFrom", String.valueOf(_DescriptionFrom));
            Parser.SetField("ChargesCount", String.valueOf(ChargesCount + " Charges"));
            Parser.SetField("_DischargeHourAddInfo", String.valueOf(_DischargeHourAddInfo));
            Parser.SetField("_AdmissionHourAddInfo", String.valueOf(_AdmissionHourAddInfo));
            Parser.SetField("StatmentCoverFromDateAddInfo", String.valueOf(StatmentCoverFromDateAddInfo));
            Parser.SetField("StatmentCoverToDateAddInfo", String.valueOf(StatmentCoverToDateAddInfo));
            Parser.SetField("AdmissionDateAddInfo", String.valueOf(AdmissionDateAddInfo));
            Parser.SetField("PPSAddInfo", String.valueOf(PPSAddInfo));
            Parser.SetField("RemarksAddInfo", String.valueOf(RemarksAddInfo));
            Parser.SetField("AutoAccident_StateAddInfo", String.valueOf(AutoAccident_StateAddInfo));
            Parser.SetField("_OtherAccidentAddInfo", String.valueOf(_OtherAccidentAddInfo));
            Parser.SetField("_EmploymentStatusAddInfo", String.valueOf(_EmploymentStatusAddInfo));
            Parser.SetField("_AutoAccidentAddInfo", String.valueOf(_AutoAccidentAddInfo));
            Parser.SetField("AutoAccStateDive", String.valueOf(AutoAccStateDive));
            Parser.SetField("_ProvAccAssigAddInfo", String.valueOf(_ProvAccAssigAddInfo));
            Parser.SetField("_AssofBenifitAddInfo", String.valueOf(_AssofBenifitAddInfo));
            Parser.SetField("_ReleaseInfoAddInfo", String.valueOf(_ReleaseInfoAddInfo));
            Parser.SetField("PrincipalDiagInfoCodes", String.valueOf(PrincipalDiagInfoCodes));
            Parser.SetField("AdmittingDiagInfoCodes", String.valueOf(AdmittingDiagInfoCodes));
            Parser.SetField("PrincipalProcedureInfoCodes", String.valueOf(PrincipalProcedureInfoCodes));
            Parser.SetField("PrincipalProcedureDateInfoCodes", String.valueOf(PrincipalProcedureDateInfoCodes));
            Parser.SetField("_POAInfoCodes", String.valueOf(_POAInfoCodes));
            Parser.SetField("InfoCodeExtCauseInj", String.valueOf(InfoCodeExtCauseInj));
            Parser.SetField("InfoCodeReasVisit", String.valueOf(InfoCodeReasVisit));
            Parser.SetField("InfoCodeOthDiag", String.valueOf(InfoCodeOthDiag));
            Parser.SetField("InfoCodeOthProcedure", String.valueOf(InfoCodeOthProcedure));
            Parser.SetField("InfoCodeOccSpan", String.valueOf(InfoCodeOccSpan));
            Parser.SetField("InfoCodeOcc", String.valueOf(InfoCodeOcc));
            Parser.SetField("InfoCodeValueCode", String.valueOf(InfoCodeValueCode));
            Parser.SetField("InfoCodeCondCode", String.valueOf(InfoCodeCondCode));
            Parser.SetField("FoundClaim", String.valueOf(FoundClaim));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));
            Parser.SetField("ChargeOption", String.valueOf(ChargeOption));
            Parser.SetField("PatientRelationtoPrimary", PatientRelationtoPrimary);
            Parser.SetField("PatientRelationtoSec", PatientRelationtoSec);

            Parser.SetField("EDI_GEN", EDI_GENERATOR);

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage() + Query);
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    public void SaveClaim(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        boolean Found = false;
        int ClaimID = 0;
        int PatientRegId = 0;
        int VisitId = 0;
        int ClaimInfoMasterId = 0;
        int ClaimType = 0;
//Claim Basic Info Variable
        String ClaimNumber = "";
        String RefNumber = "";
        String TypeBillText = "";
        String PatientName = "";
        String PatientMRN = "";
        String AcctNo = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String DOS = "";
        String UploadDate = "";
        String AttendingProvider = "";
        String BillingProviders = "";
        String OperatingProvider = "";
        String ClientName = "";
        String PriInsuranceName = "";
        String PriInsuranceNameId = "";
        String MemId = "";
        String PolicyType = "";
        String GrpNumber = "";
        String SecondaryInsurance = "";
        String SecondaryInsuranceId = "";
        String SecondaryInsuranceMemId = "";
        String SecondaryInsuranceGrpNumber = "";

//Charges Variables
        String DescriptionFrom = "";
        String ChargeOptionIns = "";
        String ChargesTableCount = "0";
        String ChargesString = "";

//Additional Info Variable
        String StatmentCoverFromDateAddInfo = "";
        String StatmentCoverToDateAddInfo = "";
        String AdmissionDateAddInfo = "";
        String AdmissionHourAddInfo = "";
        String AdmissionTypeAddInfo = "";
        String AdmissionSourceAddInfo = "";
        String DischargeHourAddInfo = "";
        String PatientStatusAddInfo = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String PPSAddInfo = "";
        String RemarksAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String ReleaseInfoAddInfo = "";
        String AssofBenifitAddInfo = "";
        String ProvAccAssigAddInfo = "";

//Information Codes variable
        String PrincipalDiagInfoCodes = "";
        String POAInfoCodes = "";
        String AdmittingDiagInfoCodes = "";
        String PrincipalProcedureInfoCodes = "";
        String PrincipalProcedureDateInfoCodes = "";
        String ExternalCauseInjuryTableCount = "0";
        String ExternalCauseInjuryString = "";
        String ReasonVisitTableCount = "0";
        String ReasonVisitString = "";
        String OtherDiagnosisString = "";
        String OtherDiagnosisTableCount = "0";
        String OtherProcedureString = "";
        String OtherProcedureTableCount = "0";
        String OccurrenceSpanString = "";
        String OccurrenceSpanTableCount = "0";
        String OccurrenceString = "";
        String OccurrenceTableCount = "0";
        String ValueCodeString = "";
        String ValueCodeTableCount = "0";
        String ConditionCodeString = "";
        String ConditionCodeTableCount = "0";

        try {

//            System.out.println("PatientRegID -> "+request.getParameter("PatientRegId"));
            PatientRegId = Integer.parseInt(CheckIntegerVariable(request, "PatientRegId"));
            VisitId = Integer.parseInt(CheckIntegerVariable(request, "VisitId"));
            ClaimType = Integer.parseInt(CheckIntegerVariable(request, "ClaimType"));

            //claim basic Info
            ClaimNumber = CheckStringVariable(request, "ClaimNumber");
            RefNumber = CheckStringVariable(request, "RefNumber");
            TypeBillText = CheckStringVariable(request, "TypeBillText");
            PatientName = CheckStringVariable(request, "PatientName");
            PatientMRN = CheckStringVariable(request, "PatientMRN");
            AcctNo = CheckStringVariable(request, "AcctNo");
            PhNumber = CheckStringVariable(request, "PhNumber");
            Email = CheckStringVariable(request, "Email");
            Address = CheckStringVariable(request, "Address");
            DOS = CheckStringVariable(request, "DOS");
            UploadDate = CheckStringVariable(request, "UploadDate");
            AttendingProvider = CheckStringVariable(request, "AttendingProvider");
            BillingProviders = CheckStringVariable(request, "BillingProviders");
            OperatingProvider = CheckStringVariable(request, "OperatingProvider");
            ClientName = CheckStringVariable(request, "ClientName");
            PriInsuranceName = CheckStringVariable(request, "PriInsuranceName");
            PriInsuranceNameId = CheckStringVariable(request, "PriInsuranceNameId");
            MemId = CheckStringVariable(request, "MemId");
            PolicyType = CheckStringVariable(request, "PolicyType");
            GrpNumber = CheckStringVariable(request, "GrpNumber");
            SecondaryInsurance = CheckStringVariable(request, "SecondaryInsurance");
            SecondaryInsuranceId = CheckStringVariable(request, "SecondaryInsuranceId");
            SecondaryInsuranceMemId = CheckStringVariable(request, "SecondaryInsuranceMemId");
            SecondaryInsuranceGrpNumber = CheckStringVariable(request, "SecondaryInsuranceGrpNumber");

            //Charges Info
            DescriptionFrom = CheckStringVariable(request, "DescriptionFrom");
            ChargeOptionIns = CheckStringVariable(request, "ChargeOptionIns");
            ChargesTableCount = CheckStringVariable(request, "ChargesTableCount");
            ChargesString = CheckStringVariable(request, "ChargesString");

            String[] myInfoCharges;
            myInfoCharges = new String[0];
            myInfoCharges = ChargesString.split("\\^");
            String ChargesInput[][] = new String[Integer.parseInt(ChargesTableCount)][12];
            i = j = k = 0;
            for (i = 1; i < myInfoCharges.length; i++) {
                if (myInfoCharges[i].length() <= 0)
                    continue;

//                FlightInput[k][j] = myInfoFlight[i].substring(myInfoFlight[i].indexOf("=") + 1);
                if (myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1).equals("^"))
                    ChargesInput[k][j] = "-";
                else
                    ChargesInput[k][j] = myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1);
                j++;
                if (j > 11) {
                    j = 0;
                    k++;
                }
            }


            //Additional Info
            StatmentCoverFromDateAddInfo = CheckStringVariable(request, "StatmentCoverFromDateAddInfo");
            StatmentCoverToDateAddInfo = CheckStringVariable(request, "StatmentCoverToDateAddInfo");
            AdmissionDateAddInfo = CheckStringVariable(request, "AdmissionDateAddInfo");
            AdmissionHourAddInfo = CheckStringVariable(request, "AdmissionHourAddInfo");
            AdmissionTypeAddInfo = CheckStringVariable(request, "AdmissionTypeAddInfo");
            AdmissionSourceAddInfo = CheckStringVariable(request, "AdmissionSourceAddInfo");
            DischargeHourAddInfo = CheckStringVariable(request, "DischargeHourAddInfo");
            PatientStatusAddInfo = CheckStringVariable(request, "PatientStatusAddInfo");
            DelayReasonCodeAddInfo = CheckStringVariable(request, "DelayReasonCodeAddInfo");
            EmploymentStatusAddInfo = CheckStringVariable(request, "EmploymentStatusAddInfo");
            AutoAccidentAddInfo = CheckStringVariable(request, "AutoAccidentAddInfo");
            AutoAccident_StateAddInfo = CheckStringVariable(request, "AutoAccident_StateAddInfo");
            OtherAccidentAddInfo = CheckStringVariable(request, "OtherAccidentAddInfo");
            PPSAddInfo = CheckStringVariable(request, "PPSAddInfo");
            RemarksAddInfo = CheckStringVariable(request, "RemarksAddInfo");
            ReleaseInfoAddInfo = CheckStringVariable(request, "ReleaseInfoAddInfo");
            AssofBenifitAddInfo = CheckStringVariable(request, "AssofBenifitAddInfo");
            ProvAccAssigAddInfo = CheckStringVariable(request, "ProvAccAssigAddInfo");

            //Information Codes
            PrincipalDiagInfoCodes = CheckStringVariable(request, "PrincipalDiagInfoCodes");
            POAInfoCodes = CheckStringVariable(request, "POAInfoCodes");
            AdmittingDiagInfoCodes = CheckStringVariable(request, "AdmittingDiagInfoCodes");
            PrincipalProcedureInfoCodes = CheckStringVariable(request, "PrincipalProcedureInfoCodes");
            PrincipalProcedureDateInfoCodes = CheckStringVariable(request, "PrincipalProcedureDateInfoCodes");

            ExternalCauseInjuryTableCount = CheckStringVariable(request, "ExternalCauseInjuryTableCount");
            ExternalCauseInjuryString = CheckStringVariable(request, "ExternalCauseInjuryString");

            String[] myInfoExternalCauseInjury;
            myInfoExternalCauseInjury = new String[0];
            String ExternalCauseInjuryInput[][] = null;

            if (Integer.parseInt(ExternalCauseInjuryTableCount) > 0) {
                myInfoExternalCauseInjury = ExternalCauseInjuryString.split("\\^");

                ExternalCauseInjuryInput = new String[Integer.parseInt(ExternalCauseInjuryTableCount)][2];
                i = j = k = 0;
                for (i = 1; i < myInfoExternalCauseInjury.length; i++) {
                    if (myInfoExternalCauseInjury[i].length() <= 0)
                        continue;

                    if (myInfoExternalCauseInjury[i].substring(myInfoExternalCauseInjury[i].indexOf("=") + 1).equals("^"))
                        ExternalCauseInjuryInput[k][j] = "-";
                    else
                        ExternalCauseInjuryInput[k][j] = myInfoExternalCauseInjury[i].substring(myInfoExternalCauseInjury[i].indexOf("=") + 1);
                    j++;
                    if (j > 1) {
                        j = 0;
                        k++;
                    }
                }
            }

            ReasonVisitTableCount = CheckStringVariable(request, "ReasonVisitTableCount");
            ReasonVisitString = CheckStringVariable(request, "ReasonVisitString");

            String[] myInfoReasonVisit;
            String ReasonVisitInput[][] = null;
            myInfoReasonVisit = new String[0];
            if (Integer.parseInt(ReasonVisitTableCount) > 0) {
                myInfoReasonVisit = ReasonVisitString.split("\\^");
                ReasonVisitInput = new String[Integer.parseInt(ReasonVisitTableCount)][2];
                i = j = k = 0;
                for (i = 1; i < myInfoReasonVisit.length; i++) {
                    if (myInfoReasonVisit[i].length() <= 0)
                        continue;

                    if (myInfoReasonVisit[i].substring(myInfoReasonVisit[i].indexOf("=") + 1).equals("^"))
                        ReasonVisitInput[k][j] = "-";
                    else
                        ReasonVisitInput[k][j] = myInfoReasonVisit[i].substring(myInfoReasonVisit[i].indexOf("=") + 1);
                    j++;
                    if (j > 1) {
                        j = 0;
                        k++;
                    }
                }
            }


            OtherDiagnosisTableCount = CheckStringVariable(request, "OtherDiagnosisTableCount");
            OtherDiagnosisString = CheckStringVariable(request, "OtherDiagnosisString");

            String[] myInfoOtherDiagnosis;
            myInfoOtherDiagnosis = new String[0];
            String OtherDiagnosisInput[][] = null;
            if (Integer.parseInt(OtherDiagnosisTableCount) > 0) {
                myInfoOtherDiagnosis = OtherDiagnosisString.split("\\^");
                OtherDiagnosisInput = new String[Integer.parseInt(OtherDiagnosisTableCount)][3];
                i = j = k = 0;
                for (i = 1; i < myInfoOtherDiagnosis.length; i++) {
                    if (myInfoOtherDiagnosis[i].length() <= 0)
                        continue;

                    if (myInfoOtherDiagnosis[i].substring(myInfoOtherDiagnosis[i].indexOf("=") + 1).equals("^"))
                        OtherDiagnosisInput[k][j] = "-";
                    else
                        OtherDiagnosisInput[k][j] = myInfoOtherDiagnosis[i].substring(myInfoOtherDiagnosis[i].indexOf("=") + 1);
                    j++;
                    if (j > 2) {
                        j = 0;
                        k++;
                    }
                }
            }


            OtherProcedureTableCount = CheckStringVariable(request, "OtherProcedureTableCount");
            OtherProcedureString = CheckStringVariable(request, "OtherProcedureString");

            String[] myInfoOtherProcedure;
            myInfoOtherProcedure = new String[0];
            String OtherProcedureInput[][] = null;
            if (Integer.parseInt(OtherProcedureTableCount) > 0) {
                myInfoOtherProcedure = OtherProcedureString.split("\\^");
                OtherProcedureInput = new String[Integer.parseInt(OtherProcedureTableCount)][2];
                i = j = k = 0;
                for (i = 1; i < myInfoOtherProcedure.length; i++) {
                    if (myInfoOtherProcedure[i].length() <= 0)
                        continue;

                    if (myInfoOtherProcedure[i].substring(myInfoOtherProcedure[i].indexOf("=") + 1).equals("^"))
                        OtherProcedureInput[k][j] = "-";
                    else
                        OtherProcedureInput[k][j] = myInfoOtherProcedure[i].substring(myInfoOtherProcedure[i].indexOf("=") + 1);
                    j++;
                    if (j > 1) {
                        j = 0;
                        k++;
                    }
                }
            }


            OccurrenceSpanTableCount = CheckStringVariable(request, "OccurrenceSpanTableCount");
            OccurrenceSpanString = CheckStringVariable(request, "OccurrenceSpanString");

            String[] myInfoOccuranceSpan;
            myInfoOccuranceSpan = new String[0];
            String OccuranceSpanInput[][] = null;
            if (Integer.parseInt(OccurrenceSpanTableCount) > 0) {
                myInfoOccuranceSpan = OccurrenceSpanString.split("\\^");
                OccuranceSpanInput = new String[Integer.parseInt(OccurrenceSpanTableCount)][3];
                i = j = k = 0;
                for (i = 1; i < myInfoOccuranceSpan.length; i++) {
                    if (myInfoOccuranceSpan[i].length() <= 0)
                        continue;

                    if (myInfoOccuranceSpan[i].substring(myInfoOccuranceSpan[i].indexOf("=") + 1).equals("^"))
                        OccuranceSpanInput[k][j] = "-";
                    else
                        OccuranceSpanInput[k][j] = myInfoOccuranceSpan[i].substring(myInfoOccuranceSpan[i].indexOf("=") + 1);
                    j++;
                    if (j > 2) {
                        j = 0;
                        k++;
                    }
                }
            }


            OccurrenceTableCount = CheckStringVariable(request, "OccurrenceTableCount");
            OccurrenceString = CheckStringVariable(request, "OccurrenceString");

            String[] myInfoOccurance;
            myInfoOccurance = new String[0];
            String OccuranceInput[][] = null;
            if (Integer.parseInt(OccurrenceTableCount) > 0) {
                myInfoOccurance = OccurrenceString.split("\\^");
                OccuranceInput = new String[Integer.parseInt(OccurrenceTableCount)][2];
                i = j = k = 0;
                for (i = 1; i < myInfoOccurance.length; i++) {
                    if (myInfoOccurance[i].length() <= 0)
                        continue;

                    if (myInfoOccurance[i].substring(myInfoOccurance[i].indexOf("=") + 1).equals("^"))
                        OccuranceInput[k][j] = "-";
                    else
                        OccuranceInput[k][j] = myInfoOccurance[i].substring(myInfoOccurance[i].indexOf("=") + 1);
                    j++;
                    if (j > 1) {
                        j = 0;
                        k++;
                    }
                }

            }


            ValueCodeTableCount = CheckStringVariable(request, "ValueCodeTableCount");
            ValueCodeString = CheckStringVariable(request, "ValueCodeString");

            String[] myInfoValueCode;
            myInfoValueCode = new String[0];
            String ValueCodeInput[][] = null;
            if (Integer.parseInt(ValueCodeTableCount) > 0) {
                myInfoValueCode = ValueCodeString.split("\\^");
                ValueCodeInput = new String[Integer.parseInt(ValueCodeTableCount)][2];
                i = j = k = 0;
                for (i = 1; i < myInfoValueCode.length; i++) {
                    if (myInfoValueCode[i].length() <= 0)
                        continue;

                    if (myInfoValueCode[i].substring(myInfoValueCode[i].indexOf("=") + 1).equals("^"))
                        ValueCodeInput[k][j] = "-";
                    else
                        ValueCodeInput[k][j] = myInfoValueCode[i].substring(myInfoValueCode[i].indexOf("=") + 1);
                    j++;
                    if (j > 1) {
                        j = 0;
                        k++;
                    }
                }
            }


            ConditionCodeTableCount = CheckStringVariable(request, "ConditionCodeTableCount");
            ConditionCodeString = CheckStringVariable(request, "ConditionCodeString");

            String[] myInfoConditionCode;
            myInfoConditionCode = new String[0];
            String ConditionCodeInput[][] = null;
            if (Integer.parseInt(ConditionCodeTableCount) > 0) {
                myInfoConditionCode = ConditionCodeString.split("\\^");
                ConditionCodeInput = new String[Integer.parseInt(ConditionCodeTableCount)][1];
                i = j = k = 0;
                for (i = 1; i < myInfoConditionCode.length; i++) {
                    if (myInfoConditionCode[i].length() <= 0)
                        continue;

                    if (myInfoConditionCode[i].substring(myInfoConditionCode[i].indexOf("=") + 1).equals("^"))
                        ConditionCodeInput[k][j] = "-";
                    else
                        ConditionCodeInput[k][j] = myInfoConditionCode[i].substring(myInfoConditionCode[i].indexOf("=") + 1);
                    j++;
                    if (j > 0) {
                        j = 0;
                        k++;
                    }
                }
            }


            Query = "Select Id from " + Database + ".ClaimInfoMaster " +
                    " where ClaimType = '" + ClaimType + "' and " +
                    " VisitId = '" + VisitId + "' and " +
                    " PatientRegId = '" + PatientRegId + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClaimID = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            try {

                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoMaster where Id = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoMaster (ClientId,PatientRegId,VisitId ,"
                        + " ClaimNumber,RefNumber,TypeBillText,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,AttendingProvider,BillingProviders," +
                        " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
                        " CreatedBy,CreatedIP,Status,ClaimType,OperatingProvider, ClaimProgress, ReadytoSubmit) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?,?,1,1) ");
                MainReceipt.setInt(1, ClientId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setInt(3, VisitId);
                MainReceipt.setString(4, ClaimNumber);
                MainReceipt.setString(5, RefNumber);
                MainReceipt.setString(6, TypeBillText);
                MainReceipt.setString(7, PatientName);
                MainReceipt.setString(8, PatientMRN);
                MainReceipt.setString(9, AcctNo);
                MainReceipt.setString(10, PhNumber);
                MainReceipt.setString(11, Email);
                MainReceipt.setString(12, Address);
                MainReceipt.setString(13, DOS);
                MainReceipt.setString(14, UploadDate);
                MainReceipt.setString(15, AttendingProvider);
                MainReceipt.setString(16, BillingProviders);
                MainReceipt.setString(17, ClientName);
                MainReceipt.setString(18, PriInsuranceNameId);
                MainReceipt.setString(19, MemId);
                MainReceipt.setString(20, PolicyType);
                MainReceipt.setString(21, GrpNumber);
                MainReceipt.setString(22, SecondaryInsuranceId);
                MainReceipt.setString(23, SecondaryInsuranceMemId);
                MainReceipt.setString(24, SecondaryInsuranceGrpNumber);
                MainReceipt.setString(25, UserId);
                MainReceipt.setString(26, ClientIP);
                MainReceipt.setInt(27, ClaimType);
                MainReceipt.setString(28, OperatingProvider);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoMaster_history (ClientId,PatientRegId,VisitId ,"
                        + " ClaimNumber,RefNumber,TypeBillText,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,AttendingProvider,BillingProviders," +
                        " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
                        " CreatedBy,CreatedIP,Status,ClaimType,OperatingProvider, ClaimProgress, ReadytoSubmit) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?,?,1,1) ");
                MainReceipt2.setInt(1, ClientId);
                MainReceipt2.setInt(2, PatientRegId);
                MainReceipt2.setInt(3, VisitId);
                MainReceipt2.setString(4, ClaimNumber);
                MainReceipt2.setString(5, RefNumber);
                MainReceipt2.setString(6, TypeBillText);
                MainReceipt2.setString(7, PatientName);
                MainReceipt2.setString(8, PatientMRN);
                MainReceipt2.setString(9, AcctNo);
                MainReceipt2.setString(10, PhNumber);
                MainReceipt2.setString(11, Email);
                MainReceipt2.setString(12, Address);
                MainReceipt2.setString(13, DOS);
                MainReceipt2.setString(14, UploadDate);
                MainReceipt2.setString(15, AttendingProvider);
                MainReceipt2.setString(16, BillingProviders);
                MainReceipt2.setString(17, ClientName);
                MainReceipt2.setString(18, PriInsuranceNameId);
                MainReceipt2.setString(19, MemId);
                MainReceipt2.setString(20, PolicyType);
                MainReceipt2.setString(21, GrpNumber);
                MainReceipt2.setString(22, SecondaryInsuranceId);
                MainReceipt2.setString(23, SecondaryInsuranceMemId);
                MainReceipt2.setString(24, SecondaryInsuranceGrpNumber);
                MainReceipt2.setString(25, UserId);
                MainReceipt2.setString(26, ClientIP);
                MainReceipt2.setInt(27, ClaimType);
                MainReceipt2.setString(28, OperatingProvider);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
            } catch (Exception Ex) {
                out.println("Error Insertion in ClaimInfoMasterTable --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoMasterTable --NO SP", request, Ex, getServletContext());
            }

            try {
                Query = "Select max(Id) from " + Database + ".ClaimInfoMaster ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next())
                    ClaimInfoMasterId = rset.getInt(1);
                rset.close();
                stmt.close();

            } catch (Exception Ex) {
                out.println("Error Getting Info from ClaimInfoMasterTable --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Error Getting Info from ClaimInfoMasterTable --NO SP", request, Ex, getServletContext());
            }

            try {

                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimChargesInfo where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                Double totalAmount = 0.0;
                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimChargesInfo (ClaimInfoMasterId,ClaimNumber,DescriptionFrom ,"
                            + " ServiceDate,HCPCS,Mod1,Mod2,Mod3,Mod4,RevCode,UnitPrice,Units,Amount,ChargesStatus,Status,CreatedDate," +
                            " CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, DescriptionFrom);
                    MainReceipt.setString(4, ChargesInput[i][0]); //ServiceDate
                    MainReceipt.setString(5, ChargesInput[i][1]); //HCPCS
                    MainReceipt.setString(6, ChargesInput[i][2]); //Mod1
                    MainReceipt.setString(7, ChargesInput[i][3]); //Mod2
                    MainReceipt.setString(8, ChargesInput[i][4]); //Mod3
                    MainReceipt.setString(9, ChargesInput[i][5]); //Mod4
                    MainReceipt.setString(10, ChargesInput[i][6]); //RevCode
                    MainReceipt.setDouble(11, Double.parseDouble(ChargesInput[i][8])); //UnitPrice
                    MainReceipt.setDouble(12, Double.parseDouble(ChargesInput[i][9])); //Units
                    MainReceipt.setDouble(13, Double.parseDouble(ChargesInput[i][10])); //Amount

                    totalAmount += Double.parseDouble(ChargesInput[i][10]);


                    //MainReceipt.setString(14, ChargesInput[i][11]);//ChargesStatus
                    MainReceipt.setString(14, ChargeOptionIns);//ChargesStatus
                    MainReceipt.setString(15, UserId);//CreatedBy
                    MainReceipt.setString(16, ClientIP);//CreatedIP
                    MainReceipt.executeUpdate();
                    MainReceipt.close();


                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimChargesInfo_history (ClaimInfoMasterId,ClaimNumber,DescriptionFrom ,"
                            + " ServiceDate,HCPCS,Mod1,Mod2,Mod3,Mod4,RevCode,UnitPrice,Units,Amount,ChargesStatus,Status,CreatedDate," +
                            " CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, DescriptionFrom);
                    MainReceipt2.setString(4, ChargesInput[i][0]); //ServiceDate
                    MainReceipt2.setString(5, ChargesInput[i][1]); //HCPCS
                    MainReceipt2.setString(6, ChargesInput[i][2]); //Mod1
                    MainReceipt2.setString(7, ChargesInput[i][3]); //Mod2
                    MainReceipt2.setString(8, ChargesInput[i][4]); //Mod3
                    MainReceipt2.setString(9, ChargesInput[i][5]); //Mod4
                    MainReceipt2.setString(10, ChargesInput[i][6]); //RevCode
                    MainReceipt2.setDouble(11, Double.parseDouble(ChargesInput[i][8])); //UnitPrice
                    MainReceipt2.setDouble(12, Double.parseDouble(ChargesInput[i][9])); //Units
                    MainReceipt2.setDouble(13, Double.parseDouble(ChargesInput[i][10])); //Amount
                    MainReceipt2.setString(14, ChargesInput[i][11]);//ChargesStatus
                    MainReceipt2.setString(15, UserId);//CreatedBy
                    MainReceipt2.setString(16, ClientIP);//CreatedIP
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }


                PreparedStatement ps = conn.prepareStatement("UPDATE " + Database + ".ClaimInfoMaster SET TotalCharges='" + totalAmount + "' WHERE Id=" + ClaimInfoMasterId);
                ps.executeUpdate();
                ps.close();


            } catch (Exception Ex) {
                out.println("Insertion in ClaimChargesInfoTable --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimChargesInfoTable --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimAdditionalInfo where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAdditionalInfo (ClaimInfoMasterId,ClaimNumber,StatmentCoverFromDateAddInfo ,"
                        + " StatmentCoverToDateAddInfo,AdmissionDateAddInfo,AdmissionHourAddInfo,AdmissionTypeAddInfo,AdmissionSourceAddInfo,DischargeHourAddInfo,PatientStatusAddInfo," +
                        " DelayReasonCodeAddInfo,EmploymentStatusAddInfo,AutoAccidentAddInfo,OtherAccidentAddInfo,Status,CreatedDate," +
                        " CreatedBy,CreatedIP,PPSAddInfo,RemarksAddInfo,AutoAccident_StateAddInfo,ReleaseInfoAddInfo,AssofBenifitAddInfo,ProvAccAssigAddInfo) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                MainReceipt.setInt(1, ClaimInfoMasterId);
                MainReceipt.setString(2, ClaimNumber);
                MainReceipt.setString(3, StatmentCoverFromDateAddInfo);
                MainReceipt.setString(4, StatmentCoverToDateAddInfo);
                MainReceipt.setString(5, AdmissionDateAddInfo);
                MainReceipt.setString(6, AdmissionHourAddInfo);
                MainReceipt.setString(7, AdmissionTypeAddInfo);
                MainReceipt.setString(8, AdmissionSourceAddInfo);
                MainReceipt.setString(9, DischargeHourAddInfo);
                MainReceipt.setString(10, PatientStatusAddInfo);
                MainReceipt.setString(11, DelayReasonCodeAddInfo);
                MainReceipt.setString(12, EmploymentStatusAddInfo);
                MainReceipt.setString(13, AutoAccidentAddInfo);
                MainReceipt.setString(14, OtherAccidentAddInfo);
                MainReceipt.setString(15, UserId);
                MainReceipt.setString(16, ClientIP);
                MainReceipt.setString(17, PPSAddInfo);
                MainReceipt.setString(18, RemarksAddInfo);
                MainReceipt.setString(19, AutoAccident_StateAddInfo);
                MainReceipt.setString(20, ReleaseInfoAddInfo);
                MainReceipt.setString(21, AssofBenifitAddInfo);
                MainReceipt.setString(22, ProvAccAssigAddInfo);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAdditionalInfo_history (ClaimInfoMasterId,ClaimNumber,StatmentCoverFromDateAddInfo ,"
                        + " StatmentCoverToDateAddInfo,AdmissionDateAddInfo,AdmissionHourAddInfo,AdmissionTypeAddInfo,AdmissionSourceAddInfo,DischargeHourAddInfo,PatientStatusAddInfo," +
                        " DelayReasonCodeAddInfo,EmploymentStatusAddInfo,AutoAccidentAddInfo,OtherAccidentAddInfo,Status,CreatedDate," +
                        " CreatedBy,CreatedIP,PPSAddInfo,RemarksAddInfo,AutoAccident_StateAddInfo,ReleaseInfoAddInfo,AssofBenifitAddInfo,ProvAccAssigAddInfo) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?,?,?,?,?,?,?) ");
                MainReceipt2.setInt(1, ClaimInfoMasterId);
                MainReceipt2.setString(2, ClaimNumber);
                MainReceipt2.setString(3, StatmentCoverFromDateAddInfo);
                MainReceipt2.setString(4, StatmentCoverToDateAddInfo);
                MainReceipt2.setString(5, AdmissionDateAddInfo);
                MainReceipt2.setString(6, AdmissionHourAddInfo);
                MainReceipt2.setString(7, AdmissionTypeAddInfo);
                MainReceipt2.setString(8, AdmissionSourceAddInfo);
                MainReceipt2.setString(9, DischargeHourAddInfo);
                MainReceipt2.setString(10, PatientStatusAddInfo);
                MainReceipt2.setString(11, DelayReasonCodeAddInfo);
                MainReceipt2.setString(12, EmploymentStatusAddInfo);
                MainReceipt2.setString(13, AutoAccidentAddInfo);
                MainReceipt2.setString(14, OtherAccidentAddInfo);
                MainReceipt2.setString(15, UserId);
                MainReceipt2.setString(16, ClientIP);
                MainReceipt2.setString(17, PPSAddInfo);
                MainReceipt2.setString(18, RemarksAddInfo);
                MainReceipt2.setString(19, AutoAccident_StateAddInfo);
                MainReceipt2.setString(20, ReleaseInfoAddInfo);
                MainReceipt2.setString(21, AssofBenifitAddInfo);
                MainReceipt2.setString(22, ProvAccAssigAddInfo);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
            } catch (Exception Ex) {
                out.println("Error Insertion in ClaimAdditionalInfo --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimAdditionalInfo --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInformationCode where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInformationCode (ClaimInfoMasterId,ClaimNumber," +
                        " PrincipalDiagInfoCodes,POAInfoCodes,AdmittingDiagInfoCodes,Status,CreatedDate,CreatedBy,CreatedIP,PrincipalProcedureInfoCodes,PrincipalProcedureDateInfoCodes) \n"
                        + " VALUES (?,?,?,?,?,0,NOW(),?,?,?,?) ");
                MainReceipt.setInt(1, ClaimInfoMasterId);
                MainReceipt.setString(2, ClaimNumber);
                MainReceipt.setString(3, PrincipalDiagInfoCodes);
                MainReceipt.setString(4, POAInfoCodes);
                MainReceipt.setString(5, AdmittingDiagInfoCodes);
                MainReceipt.setString(6, UserId);
                MainReceipt.setString(7, ClientIP);
                MainReceipt.setString(8, PrincipalProcedureInfoCodes);
                MainReceipt.setString(9, PrincipalProcedureDateInfoCodes);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInformationCode_history (ClaimInfoMasterId,ClaimNumber," +
                        " PrincipalDiagInfoCodes,POAInfoCodes,AdmittingDiagInfoCodes,Status,CreatedDate,CreatedBy,CreatedIP,PrincipalProcedureInfoCodes,PrincipalProcedureDateInfoCodes) \n"
                        + " VALUES (?,?,?,?,?,0,NOW(),?,?,?,?) ");
                MainReceipt2.setInt(1, ClaimInfoMasterId);
                MainReceipt2.setString(2, ClaimNumber);
                MainReceipt2.setString(3, PrincipalDiagInfoCodes);
                MainReceipt2.setString(4, POAInfoCodes);
                MainReceipt2.setString(5, AdmittingDiagInfoCodes);
                MainReceipt2.setString(6, UserId);
                MainReceipt2.setString(7, ClientIP);
                MainReceipt2.setString(8, PrincipalProcedureInfoCodes);
                MainReceipt2.setString(9, PrincipalProcedureDateInfoCodes);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
            } catch (Exception Ex) {
                out.println("Error Insertion in ClaimInformationCode Table --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInformationCode Table --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeExtCauseInj where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                for (i = 0; i < Integer.parseInt(ExternalCauseInjuryTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeExtCauseInj (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Status,CreatedBy,CreatedDate,CreatedIP) \n"
                            + " VALUES (?,?,?,0,?,NOW(),?) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, ExternalCauseInjuryInput[i][0]);//Code
                    MainReceipt.setString(4, UserId);
                    MainReceipt.setString(5, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeExtCauseInj_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Status,CreatedBy,CreatedDate,CreatedIP) \n"
                            + " VALUES (?,?,?,0,?,NOW(),?) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, ExternalCauseInjuryInput[i][0]);//Code
                    MainReceipt2.setString(4, UserId);
                    MainReceipt2.setString(5, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeExtCauseInjTable --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeExtCauseInjTable --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeReasVisit where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                for (i = 0; i < Integer.parseInt(ReasonVisitTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeReasVisit (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,0,NOW(),?,?) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, ReasonVisitInput[i][0]);//Code
                    MainReceipt.setString(4, UserId);
                    MainReceipt.setString(5, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeReasVisit_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,0,NOW(),?,?) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, ReasonVisitInput[i][0]);//Code
                    MainReceipt2.setString(4, UserId);
                    MainReceipt2.setString(5, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeReasVisit --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeReasVisit --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeOthDiag where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                for (i = 0; i < Integer.parseInt(OtherDiagnosisTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOthDiag (ClaimInfoMasterId,ClaimNumber," +
                            " Code,PQA,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, OtherDiagnosisInput[i][0]);//Code
                    MainReceipt.setString(4, OtherDiagnosisInput[i][2]);//PQA
                    MainReceipt.setString(5, UserId);
                    MainReceipt.setString(6, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOthDiag_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,PQA,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                            + " VALUES (?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, OtherDiagnosisInput[i][0]);//Code
                    MainReceipt2.setString(4, OtherDiagnosisInput[i][2]);//PQA
                    MainReceipt2.setString(5, UserId);
                    MainReceipt2.setString(6, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeOthDiag --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeOthDiag --NO SP", request, Ex, getServletContext());
            }

            try {

                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeOthProcedure where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                for (i = 0; i < Integer.parseInt(OtherProcedureTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOthProcedure (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Date,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, OtherProcedureInput[i][0]);//Code
                    MainReceipt.setString(4, OtherDiagnosisInput[i][1]);//Date
                    MainReceipt.setString(5, UserId);
                    MainReceipt.setString(6, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOthProcedure_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Date,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, OtherProcedureInput[i][0]);//Code
                    MainReceipt2.setString(4, OtherDiagnosisInput[i][1]);//Date
                    MainReceipt2.setString(5, UserId);
                    MainReceipt2.setString(6, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeOthProcedure --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeOthProcedure --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeOccSpan where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                for (i = 0; i < Integer.parseInt(OccurrenceSpanTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOccSpan (ClaimInfoMasterId,ClaimNumber," +
                            " Code,FromDate,ToDate,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, OccuranceSpanInput[i][0]);//Code
                    MainReceipt.setString(4, OccuranceSpanInput[i][1]);//FromDate
                    MainReceipt.setString(5, OccuranceSpanInput[i][2]);//ToDate
                    MainReceipt.setString(6, UserId);
                    MainReceipt.setString(7, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeOccSpan_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,FromDate,ToDate,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, OccuranceSpanInput[i][0]);//Code
                    MainReceipt2.setString(4, OccuranceSpanInput[i][1]);//FromDate
                    MainReceipt2.setString(5, OccuranceSpanInput[i][2]);//ToDate
                    MainReceipt2.setString(6, UserId);
                    MainReceipt2.setString(7, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeOccSpan --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeOccSpan --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                for (i = 0; i < Integer.parseInt(OccurrenceTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoOccurance (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Date,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, OccuranceInput[i][0]);//Code
                    MainReceipt.setString(4, OccuranceInput[i][1]);//Date
                    MainReceipt.setString(5, UserId);
                    MainReceipt.setString(6, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoOccurance_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Date,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, OccuranceInput[i][0]);//Code
                    MainReceipt2.setString(4, OccuranceInput[i][1]);//Date
                    MainReceipt2.setString(5, UserId);
                    MainReceipt2.setString(6, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoOccurance --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoOccurance --NO SP", request, Ex, getServletContext());
            }

            try {

                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeValueCode where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                for (i = 0; i < Integer.parseInt(ValueCodeTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeValueCode (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Amount,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, ValueCodeInput[i][0]);//Code
                    MainReceipt.setDouble(4, Double.parseDouble(ValueCodeInput[i][1]));//Amount
                    MainReceipt.setString(5, UserId);
                    MainReceipt.setString(6, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeValueCode_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,Amount,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,?,NOW(),?,?,0) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, ValueCodeInput[i][0]);//Code
                    MainReceipt2.setDouble(4, Double.parseDouble(ValueCodeInput[i][1]));//Amount
                    MainReceipt2.setString(5, UserId);
                    MainReceipt2.setString(6, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeValueCode --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeValueCode --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }
                for (i = 0; i < Integer.parseInt(ConditionCodeTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeConditionCode (ClaimInfoMasterId,ClaimNumber," +
                            " Code,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,NOW(),?,?,0) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, ConditionCodeInput[i][0]);//Code
                    MainReceipt.setString(4, UserId);
                    MainReceipt.setString(5, ClientIP);
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoCodeConditionCode_history (ClaimInfoMasterId,ClaimNumber," +
                            " Code,CreatedDate,CreatedBy,CreatedIP,Status) \n"
                            + " VALUES (?,?,?,NOW(),?,?,0) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, ConditionCodeInput[i][0]);//Code
                    MainReceipt2.setString(4, UserId);
                    MainReceipt2.setString(5, ClientIP);
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
            } catch (Exception Ex) {
                out.println("Insertion in ClaimInfoCodeConditionCode --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInfoCodeConditionCode --NO SP", request, Ex, getServletContext());
            }

            //out.println("Claim Saved Successfully");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Claim Saved Successfully");
            Parser.SetField("MRN", "ClaimNumber: " + ClaimNumber);
            Parser.SetField("FormName", "InsClaimTesting");
            Parser.SetField("ActionID", "OpenUB04&PatientRegId=" + PatientRegId + "&ClaimNumber=" + ClaimNumber + "&VisitId=" + VisitId);
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");


        } catch (Exception e) {
            System.out.println("Error in : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            System.out.println(str);
//            String str = "";
//            for (i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
            Services.DumException("AddInfo", "Error in Save Claim Method --NO SP", request, e, getServletContext());
            try {
//                helper.SendEmailWithAttachment("Error in Claim Saving ** (AddInfo Main Catch^^" + ClientName, context, e, "AddInfo", "SaveClaim", conn);
                Services.DumException("SaveClaim^^" + ClientName + " Error in Save Claim Method", "AddInfo ", request, e);
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "PatientUpdateInfo");
                Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
                Parser.SetField("Message", "MES#014");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            } catch (Exception ex) {
            }
        }
    }

    public void ScrubberIns(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        Instant start = Instant.now();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        PreparedStatement ps = null;
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        boolean Found = false;
        int PatientRegId = 0;
        int VisitId = 0;
        int ClaimInfoMasterId = 0;
        int ClaimType = 0;
        int ClaimID = 0;
//Claim Basic Info Variable
        String ClaimNumber = "";
        String RefNumber = "";
        String TypeBillText = "";
        char[] _TOB = new char[3];
        String PatientName = "";
        String PatientMRN = "";
        String AcctNo = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";

        String ReasonVisit = "";
        String PatientFirstName = "";
        String SSN = "";
        String PatientLastName = "";
        String DOB = "";
        String _DOB = "";
        String Gender = "";
        String City = "";
        String State = "";
        String StateCode = "";
        String ZipCode = "";
        int SelfPayChk = 0;
        String ClaimCreateDate = "";

        String DOS = "";
        String UploadDate = "";
        String AttendingProvider = "";
        String BillingProviders = "";
        String SupervisingProvider = "";
        String OrderingProvider = "";
        String ReferringProvider = "";
        String ClientName = "";
        String PriInsuranceName = "";
        String PriInsuranceNameId = "";
        String MemId = "";
        String PolicyType = "";
        String GrpNumber = "";
        String SecondaryInsurance = "";
        String SecondaryInsuranceId = "";
        String SecondaryInsuranceMemId = "";
        String SecondaryInsuranceGrpNumber = "";


//Charges Variables
        String ChargeOptionIns = "";
        String ChargesTableCount = "0";
        String ChargesString = "";
        String DescriptionFrom = "";

//Additional Info Variable
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String OtherAccidentAddInfo = "";
        String AccidentIllnesDateAddInfo = "";
        String LastMenstrualPeriodDateAddInfo = "";
        String InitialTreatDateAddInfo = "";
        String LastSeenDateAddInfo = "";
        String UnabletoWorkFromDateAddInfo = "";
        String UnabletoWorkToDateAddInfo = "";
        String PatHomeboundAddInfo = "";
        String ClaimCodesAddinfo = "";
        String OtherClaimIDAddinfo = "";
        String AdditionalClaimInfoAddinfo = "";
        String ClaimNoteAddinfo = "";
        String ResubmitReasonCodeAddinfo = "";
        String DelayReasonCodeAddInfo = "";
        String HospitalizedFromDateAddInfo = "";
        String HospitalizedToDateAddInfo = "";
        String LabChargesAddInfo = "";
        String SpecialProgCodeAddInfo = "";
        String PatientSignOnFileAddInfo = "";
        String InsuredSignOnFileAddInfo = "";
        String ProvAccAssigAddInfo = "";
        String PXCTaxQualiAddInfo = "";
        String DocumentationMethodAddInfo = "";
        String DocumentationTypeAddInfo = "";
        String PatientHeightAddInfo = "";
        String PatientWeightAddInfo = "";
        String ServAuthExcepAddInfo = "";
        String DemoProjectAddInfo = "";
        String MemmoCertAddInfo = "";
        String InvDevExempAddInfo = "";
        String AmbPatGrpAddInfo = "";

//AmbulanceCodes Codes variable
        String AmbClaimInfoCodes = "";
        String TranReasonInfoCodes = "";
        String TranMilesInfoCodes = "";
        String PatWeightInfoCodes = "";
        String RoundTripReasInfoCodes = "";
        String StretReasonInfoCodes = "";
        String PickUpAddressInfoCode = "";
        String PickUpCityInfoCode = "";
        String PickUpStateInfoCode = "";
        String PickUpZipCodeInfoCode = "";
        String DropoffAddressInfoCode = "";
        String DropoffCityInfoCode = "";
        String DropoffStateInfoCode = "";
        String DropoffZipCodeInfoCode = "";
        String PatAdmitHosChk = "";
        String PatMoveStretChk = "";
        String PatUnconShockChk = "";
        String PatTransEmerSituaChk = "";
        String PatPhyRestrainChk = "";
        String PatvisiblehemorrChk = "";
        String AmbSerNeccChk = "";
        String PatconfbedchairChk = "";

        StringBuilder ErrorMsgs = new StringBuilder();
        StringBuilder SuccessMsgs = new StringBuilder();

        try {
            PatientRegId = Integer.parseInt(CheckIntegerVariable(request, "PatientRegId"));
            VisitId = Integer.parseInt(CheckIntegerVariable(request, "VisitId"));
            ClaimType = Integer.parseInt(CheckIntegerVariable(request, "ClaimType"));

            //claim basic Info
            ClaimNumber = CheckStringVariable(request, "ClaimNumber");
            RefNumber = CheckStringVariable(request, "RefNumber");
            TypeBillText = CheckStringVariable(request, "TypeBillText");
            _TOB = TypeBillText.toCharArray();
//            FrequencyTypeBillDiv = CheckStringVariable(request, FrequencyTypeBillDiv);
//            System.out.println("FrequencyTypeBillDiv" +FrequencyTypeBillDiv);
            PatientName = CheckStringVariable(request, "PatientName");
            PatientMRN = CheckStringVariable(request, "PatientMRN");
            AcctNo = CheckStringVariable(request, "AcctNo");
            PhNumber = CheckStringVariable(request, "PhNumber");
            Email = CheckStringVariable(request, "Email");
            Address = CheckStringVariable(request, "Address");
            DOS = CheckStringVariable(request, "DOS");
            UploadDate = CheckStringVariable(request, "UploadDate");
            AttendingProvider = CheckStringVariable(request, "AttendingProvider");
            BillingProviders = CheckStringVariable(request, "BillingProviders");
            SupervisingProvider = CheckStringVariable(request, "SupervisingProvider");
            OrderingProvider = CheckStringVariable(request, "OrderingProvider");
            ReferringProvider = CheckStringVariable(request, "ReferringProvider");
            ClientName = CheckStringVariable(request, "ClientName");
            PriInsuranceName = CheckStringVariable(request, "PriInsuranceName");
            PriInsuranceNameId = CheckStringVariable(request, "PriInsuranceNameId");
            MemId = CheckStringVariable(request, "MemId");
            PolicyType = CheckStringVariable(request, "PolicyType");
            GrpNumber = CheckStringVariable(request, "GrpNumber");
            SecondaryInsurance = CheckStringVariable(request, "SecondaryInsurance");
            SecondaryInsuranceId = CheckStringVariable(request, "SecondaryInsuranceId");
            SecondaryInsuranceMemId = CheckStringVariable(request, "SecondaryInsuranceMemId");
            SecondaryInsuranceGrpNumber = CheckStringVariable(request, "SecondaryInsuranceGrpNumber");

            ChargeOptionIns = CheckStringVariable(request, "ChargeOptionIns");
            ChargesTableCount = CheckStringVariable(request, "ChargesTableCount");
            ChargesString = CheckStringVariable(request, "ChargesString");
            DescriptionFrom = CheckStringVariable(request, "DescriptionFrom");


//            out.println(ChargesTableCount);
//            out.println(ChargesString);
            String[] myInfoCharges;
            myInfoCharges = new String[0];
            myInfoCharges = ChargesString.split("\\^");
            String ChargesInput[][] = new String[Integer.parseInt(ChargesTableCount)][14];
            i = j = k = 0;
            for (i = 1; i < myInfoCharges.length; i++) {
                if (myInfoCharges[i].length() <= 0)
                    continue;

                if (myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1).equals("^"))
                    ChargesInput[k][j] = "-";
                else
                    ChargesInput[k][j] = myInfoCharges[i].substring(myInfoCharges[i].indexOf("=") + 1);
                j++;
                if (j > 13) {
                    j = 0;
                    k++;
                }
            }


            //Additional Info Variables
            EmploymentStatusAddInfo = CheckStringVariable(request, "EmploymentStatusAddInfo");
            AutoAccidentAddInfo = CheckStringVariable(request, "AutoAccidentAddInfo");
            AutoAccident_StateAddInfo = CheckStringVariable(request, "AutoAccident_StateAddInfo");
            OtherAccidentAddInfo = CheckStringVariable(request, "OtherAccidentAddInfo");
            AccidentIllnesDateAddInfo = formatDate(request, "AccidentIllnesDateAddInfo");
            LastMenstrualPeriodDateAddInfo = formatDate(request, "LastMenstrualPeriodDateAddInfo");
            InitialTreatDateAddInfo = formatDate(request, "InitialTreatDateAddInfo");
            LastSeenDateAddInfo = formatDate(request, "LastSeenDateAddInfo");
            UnabletoWorkFromDateAddInfo = formatDate(request, "UnabletoWorkFromDateAddInfo");
            UnabletoWorkToDateAddInfo = formatDate(request, "UnabletoWorkToDateAddInfo");
//            PatHomeboundAddInfo = CheckStringVariable(request, "PatHomeboundAddInfo");
//            ClaimCodesAddinfo = CheckStringVariable(request, "ClaimCodesAddinfo");
//            OtherClaimIDAddinfo = CheckStringVariable(request, "OtherClaimIDAddinfo");
//            AdditionalClaimInfoAddinfo = CheckStringVariable(request, "AdditionalClaimInfoAddinfo");
//            ClaimNoteAddinfo = CheckStringVariable(request, "ClaimNoteAddinfo");
//            ResubmitReasonCodeAddinfo = CheckStringVariable(request, "ResubmitReasonCodeAddinfo");
            DelayReasonCodeAddInfo = CheckStringVariable(request, "DelayReasonCodeAddInfo");
            HospitalizedFromDateAddInfo = formatDate(request, "HospitalizedFromDateAddInfo");
            HospitalizedToDateAddInfo = formatDate(request, "HospitalizedToDateAddInfo");
//            LabChargesAddInfo = CheckStringVariable(request, "LabChargesAddInfo");
//            SpecialProgCodeAddInfo = CheckStringVariable(request, "SpecialProgCodeAddInfo");
//            PatientSignOnFileAddInfo = CheckStringVariable(request, "PatientSignOnFileAddInfo");
//            InsuredSignOnFileAddInfo = CheckStringVariable(request, "InsuredSignOnFileAddInfo");
            ProvAccAssigAddInfo = CheckStringVariable(request, "ProvAccAssigAddInfo");
//            PXCTaxQualiAddInfo = CheckStringVariable(request, "PXCTaxQualiAddInfo");
//            DocumentationMethodAddInfo = CheckStringVariable(request, "DocumentationMethodAddInfo");
//            DocumentationTypeAddInfo = CheckStringVariable(request, "DocumentationTypeAddInfo");
//            PatientHeightAddInfo = CheckStringVariable(request, "PatientHeightAddInfo");
//            PatientWeightAddInfo = CheckStringVariable(request, "PatientWeightAddInfo");
//            ServAuthExcepAddInfo = CheckStringVariable(request, "ServAuthExcepAddInfo");
//            DemoProjectAddInfo = CheckStringVariable(request, "DemoProjectAddInfo");
//            MemmoCertAddInfo = CheckStringVariable(request, "MemmoCertAddInfo");
//            InvDevExempAddInfo = CheckStringVariable(request, "InvDevExempAddInfo");
//            AmbPatGrpAddInfo = CheckStringVariable(request, "AmbPatGrpAddInfo");

            //Ambulance Info
//            AmbClaimInfoCodes = CheckStringVariable(request, "AmbClaimInfoCodes");
//            TranReasonInfoCodes = CheckStringVariable(request, "TranReasonInfoCodes");
//            TranMilesInfoCodes = CheckStringVariable(request, "TranMilesInfoCodes");
//            PatWeightInfoCodes = CheckStringVariable(request, "PatWeightInfoCodes");
//            RoundTripReasInfoCodes = CheckStringVariable(request, "RoundTripReasInfoCodes");
//            StretReasonInfoCodes = CheckStringVariable(request, "StretReasonInfoCodes");
//            PickUpAddressInfoCode = CheckStringVariable(request, "PickUpAddressInfoCode");
//            PickUpCityInfoCode = CheckStringVariable(request, "PickUpCityInfoCode");
//            PickUpStateInfoCode = CheckStringVariable(request, "PickUpStateInfoCode");
//            PickUpZipCodeInfoCode = CheckStringVariable(request, "PickUpZipCodeInfoCode");
//            DropoffAddressInfoCode = CheckStringVariable(request, "DropoffAddressInfoCode");
//            DropoffCityInfoCode = CheckStringVariable(request, "DropoffCityInfoCode");
//            DropoffStateInfoCode = CheckStringVariable(request, "DropoffStateInfoCode");
//            DropoffZipCodeInfoCode = CheckStringVariable(request, "DropoffZipCodeInfoCode");
            PatAdmitHosChk = CheckCheckBoxValue(request, "PatAdmitHosChk");
            PatMoveStretChk = CheckCheckBoxValue(request, "PatMoveStretChk");
            PatUnconShockChk = CheckCheckBoxValue(request, "PatUnconShockChk");
            PatTransEmerSituaChk = CheckCheckBoxValue(request, "PatTransEmerSituaChk");
            PatPhyRestrainChk = CheckCheckBoxValue(request, "PatPhyRestrainChk");
            PatvisiblehemorrChk = CheckCheckBoxValue(request, "PatvisiblehemorrChk");
            AmbSerNeccChk = CheckCheckBoxValue(request, "AmbSerNeccChk");
            PatconfbedchairChk = CheckCheckBoxValue(request, "PatconfbedchairChk");

            String BillingProviderLastName = "";
            String BillingProviderFirstName = "";
            String BillingProviderNPI = "";
            String BillingProvider_Taxonomy = "";

            String ReferringProviderLastName = "";
            String ReferringProviderFirstName = "";
            String ReferringProviderNPI = "";
            String ReferringProvider_Taxonomy = "";

            String SupervisingProviderLastName = "";
            String SupervisingProviderFirstName = "";
            String SupervisingProviderNPI = "";
            String SupervisingProvider_Taxonomy = "";

            String RenderingProvidersLastName = "";
            String RenderingProvidersFirstName = "";
            String RenderingProvidersNPI = "";
            String RenderingProviders_Taxonomy = "";

            String OrderingProvidersLastName = "";
            String OrderingProvidersFirstName = "";
            String OrderingProvidersNPI = "";
            String OrderingProviders_Taxonomy = "";


            String OperatingProviderLastName = "";
            String OperatingProviderFirstName = "";
            String OperatingProviderNPI = "";
            String OperatingProvider_Taxonomy = "";


            String ClientAddress = "";
            String ClientCity = "";
            String ClientTaxID = "";
            String ClientState = "";
            String ClientZipCode = "";
            String ClientPhone = "";
            String ClientNPI = "";
            String CLIA_number = "";
            String TaxanomySpecialty = "";

            String PriFillingIndicator = "";
            String SecFillingIndicator = "";

            String Payer_Address = "";
            String Payer_City = "";
            String Payer_State = "";
            String Payer_Zip = "";


            String PatientRelationtoPrimary = "";
            String PatientRelationtoSec = "";


            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), " +
                    "IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,''), " +
                    "IFNULL(TaxanomySpecialty,''), IFNULL(TaxID,'') ,IFNULL(CLIA,'')  " +
                    "from oe.clients" +
                    " where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientAddress = rset.getString(1).toUpperCase();
                ClientCity = rset.getString(2).toUpperCase();
                ClientState = rset.getString(3).toUpperCase();
                ClientZipCode = rset.getString(4).toUpperCase();
                ClientPhone = rset.getString(5).toUpperCase();
                ClientNPI = rset.getString(6).toUpperCase();
                TaxanomySpecialty = rset.getString(7).toUpperCase();
                ClientTaxID = rset.getString(8).toUpperCase();
                CLIA_number = rset.getString(9).toUpperCase();
            }
            rset.close();
            stmt.close();

            if (ClientId == 32) {
                final String CLIA_REGEX = "^([0-9]{2}[A-Z]{1}[0-9]{7})*$";
                if (!isEmpty(CLIA_number)) {
                    if (!CLIA_number.matches(CLIA_REGEX))
                        ErrorMsgs.append("<p style=\"color:black;\"> <b>CLIA</b> is <b>InValid</b> Must be in <b>NNDNNNNNNN</b> format. where <b>N</b> is <b>numeric</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\"> <b>CLIA</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");

                }
            }


            if (!isEmpty(BillingProviders)) {
                ps = conn.prepareStatement(
                        "SELECT IFNULL(DoctorsLastName,''), IFNULL(DoctorsFirstName,''), " +
                                "IFNULL(NPI,'') , IFNULL(TaxonomySpecialty,'') " +
                                "From " + Database + ".DoctorsList  " +
                                "WHERE id=" + BillingProviders);
                rset = ps.executeQuery();
                if (rset.next()) {
                    BillingProviderLastName = rset.getString(1).toUpperCase();
                    BillingProviderFirstName = rset.getString(2).toUpperCase();
                    BillingProviderNPI = rset.getString(3);
                    BillingProvider_Taxonomy = rset.getString(4).toUpperCase();
                }
                rset.close();
                ps.close();
            }

            if (!isEmpty(ReferringProvider)) {
                ps = conn.prepareStatement("SELECT IFNULL(DoctorsLastName,''), IFNULL(DoctorsFirstName,''), IFNULL(NPI,'') , IFNULL(TaxonomySpecialty,'') From " + Database + ".DoctorsList WHERE id=" + ReferringProvider);
                rset = ps.executeQuery();
                if (rset.next()) {
                    ReferringProviderLastName = rset.getString(1).toUpperCase();
                    ReferringProviderFirstName = rset.getString(2).toUpperCase();
                    ReferringProviderNPI = rset.getString(3);
                    ReferringProvider_Taxonomy = rset.getString(4).toUpperCase();
                }
                rset.close();
                ps.close();
            }

            if (!isEmpty(OrderingProvider)) {
                ps = conn.prepareStatement("SELECT IFNULL(DoctorsLastName,''), IFNULL(DoctorsFirstName,''), IFNULL(NPI,'') , IFNULL(TaxonomySpecialty,'') From " + Database + ".DoctorsList WHERE id=" + OrderingProvider);
                rset = ps.executeQuery();
                if (rset.next()) {
                    OrderingProvidersLastName = rset.getString(1).toUpperCase();
                    OrderingProvidersFirstName = rset.getString(2).toUpperCase();
                    OrderingProvidersNPI = rset.getString(3);
                    OrderingProviders_Taxonomy = rset.getString(4).toUpperCase();
                }
                rset.close();
                ps.close();
            }


            Query = "Select IFNULL(PriInsurerName,''),IFNULL(PatientRelationtoPrimary,''),IFNULL(PatientRelationshiptoSecondry,'') " +
                    "from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientRelationtoPrimary = rset.getString(2);
                PatientRelationtoSec = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = "Select IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                    "IFNULL(DATE_FORMAT(DOB,'%Y%m%d'),''), DATE_FORMAT(NOW(),'%d%m%y%k%i%s'), CASE WHEN Gender='male' then 'M' else 'F' END, DATE_FORMAT(NOW(),'%m%d%y'), " +
                    "IFNULL(SelfPayChk,'0'),IFNULL(DATE_FORMAT(DOB,'%Y-%m-%d'),''), IFNULL(ReasonVisit,'') , IFNULL(SSN,'') , IFNULL(DATE_FORMAT(NOW(),'%Y%m%d'),'') " +
                    " from " + Database + ".PatientReg where ID = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                PatientFirstName = rset.getString(1).toUpperCase();
                PatientLastName = rset.getString(2).toUpperCase();
                Address = rset.getString(3);
                City = rset.getString(4);
                State = rset.getString(5);
                ZipCode = rset.getString(6);
                DOB = rset.getString(7);
                _DOB = rset.getString(12);
//                DateTime = rset.getString(8);
                Gender = rset.getString(9).trim();
//                DateNow = rset.getString(10);
                SelfPayChk = rset.getInt(11);
                ReasonVisit = rset.getString(13);
                SSN = rset.getString(14);
                ClaimCreateDate = rset.getString(15);
            }
            rset.close();
            stmt.close();


            String[] Taxonomy = {"213ES0131X", "213EG0000X", "213EP1101X", "213EP0504X", "213ER0200X", "213ES0000X"};
            List<String> TaxonomyList = Arrays.asList(Taxonomy);
            String[] Taxonomy_for_InitialTreatment = {"111N00000X", "111NI0013X", "111NI0900X", "111NN0400X", "111NN1001X",
                    "111NP0017X", "111NR0200X", "111NR0400X", "111NS0005X", "111NT0100X", "111NX0100X", "111NX0800X"};
            List<String> Taxonomy_for_InitialTreatment_List = Arrays.asList(Taxonomy_for_InitialTreatment);

            if (!isEmpty(BillingProvider_Taxonomy)) {
                if (isInValidTaxonomy(conn, BillingProvider_Taxonomy)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Taxonomy Code</b> is  <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Taxonomy Code</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }
            final String NPI_REGEX = "[0-9]{10}";

            if (!isEmpty(BillingProviderNPI)) {
                if (BillingProviderNPI.matches(NPI_REGEX)) {
                    if (isValid(BillingProviderNPI))
                        SuccessMsgs.append("<p style=\"color:black;\">Billing Provider<b> NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
                    else
                        ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                            "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>NPI</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            final String Name_REGEX = "^([A-Z0-9]{2,100})$";
            if (!isEmpty(BillingProviderLastName)) {
                if (!BillingProviderLastName.matches(Name_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>LastName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>LastName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (!isEmpty(BillingProviderFirstName)) {
                if (!BillingProviderFirstName.matches(Name_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>FirstName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>FirstName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

//            String BillingProvider_Address = "225 MAIN STREET BARKLEY BUILDING";
            if (!isEmpty(ClientAddress)) {
                if (isInValidAddress(ClientAddress)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Address/b> Cannot be <b>PO BOX</b> Address. Only Physical Address Is <b>Allowed</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Address</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (!isEmpty(ClientZipCode)) {
                if (ClientZipCode.contains("-")) {
                    String[] ClientZipCodes = ClientZipCode.split("-");
                    if (isInValidZipCode(conn, ClientState, ClientZipCodes[0])) {
                        ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    if (isInValidZipCode(conn, ClientState, ClientZipCode)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>ZipCode</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            final String CITY_REGEX = "^([0-9, A-Z, a-z]{2,30})$";
            if (!isEmpty(ClientCity)) {
                if (!ClientCity.matches(CITY_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>City</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>City</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }


            final String BillingProvider_TaxID_LENGTH_REGEX = "([0-9]{9})";
            final String BillingProvider_TaxID_SEQUENCE_REGEX = "(?=012345678|987654321|098765432|000000000|111111111|222222222|333333333|444444444|555555555|666666666|777777777|888888888|999999999|123456789).{9}";
            if (!isEmpty(ClientTaxID)) {
                if (ClientTaxID.matches(BillingProvider_TaxID_LENGTH_REGEX)) {
                    if (ClientTaxID.matches(BillingProvider_TaxID_SEQUENCE_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Tax ID</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Tax ID</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Billing Provider <b>Tax ID</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            ps = conn.prepareStatement("SELECT IFNULL(ClaimIndicator_I,''),IFNULL(Address,''),IFNULL(City,''),IFNULL(State,''),IFNULL(Zip,'') " +
                    "from ClaimMasterDB.ProfessionalPayersWithFC where Id=" + PriInsuranceNameId);
            rset = ps.executeQuery();
            if (rset.next()) {
                PriFillingIndicator = rset.getString(1);
                Payer_Address = rset.getString(2);
                Payer_City = rset.getString(3);
                Payer_State = rset.getString(4);
                Payer_Zip = rset.getString(5);
            }
            rset.close();
            ps.close();

            System.out.println("PriFillingIndicator " + PriFillingIndicator);
            ps = conn.prepareStatement("SELECT IFNULL(ClaimIndicator_P,'') from ClaimMasterDB.ProfessionalPayersWithFC where Id=" + SecondaryInsuranceId);
            rset = ps.executeQuery();
            if (rset.next()) {
                SecFillingIndicator = rset.getString(1);
            }
            rset.close();
            ps.close();

            if (isEmpty(PriInsuranceNameId) && isEmpty(SecondaryInsuranceId)) {
                ErrorMsgs.append("<p style=\"color:black;\"><b>Insurance </b> is <b>Missing</b> <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (PriFillingIndicator == null && (PriFillingIndicator.equals("MB") || PriFillingIndicator.equals("MA"))) {

//                if (PatientRelationshipCode.compareTo("18") != 0) {
//                    ErrorMsgs.append("<p style=\"color:black;\"><b>Subscriber Relationship</b>  must be   <b>Self</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
//                }

                if (!isEmpty(ReferringProviderNPI) && !isEmpty(BillingProviderNPI)) {
//                            we will verify NPI of referring provider with our ordering and referring provider library
                    ps = conn.prepareStatement("SELECT Count(*) from ClaimMasterDB.OrderReferring where LastName = ? AND NPI=?");
                    ps.setString(1, ReferringProviderLastName);
                    ps.setString(2, ReferringProviderNPI);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        if (rset.getInt(1) == 0) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Referring provider </b> is <b>Invalid</b> <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    rset.close();
                    ps.close();
                }


                if (!isEmpty(OrderingProvidersNPI) && !isEmpty(BillingProviderNPI)) {
//                            we will verify NPI of referring provider with our ordering and referring provider library
                    ps = conn.prepareStatement("SELECT Count(*) from ClaimMasterDB.OrderReferring where LastName = ? AND NPI=?");
                    ps.setString(1, OrderingProvidersLastName);
                    ps.setString(2, OrderingProvidersNPI);
                    rset = ps.executeQuery();
                    if (rset.next()) {
                        if (rset.getInt(1) == 0) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Ordering provider </b> is <b>Invalid</b> <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    rset.close();
                    ps.close();
                }
            }

            if (!isEmpty(PriInsuranceNameId) && (PriInsuranceNameId.equals("84146") || PriInsuranceNameId.equals("31114"))) {
//                            Patient Entity should be IL NOT QC -> (LOOP 2010CA)  NOT DONE
                if (PatientRelationtoPrimary.compareTo("Self") != 0 || PatientRelationtoSec.compareTo("Self") != 0) {
                    ErrorMsgs.append("<p style=\"color:black;\"><b>Subscriber Relationship</b>  must be   <b>Self</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            }

            if (!isEmpty(PriInsuranceNameId) && (PriInsuranceNameId.equals("MB") || PriFillingIndicator.equals("MA") || PriFillingIndicator.equals("MC"))) {
                if (PriInsuranceNameId.equals("DNC00")) {
                    if (!isEmpty(GrpNumber)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>group number</b> not allowed for <b>Medicare</b> and <b>Medicaid</b> <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            }

            if (!isEmpty(PatientLastName)) {
                if (!PatientLastName.matches(Name_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber  <b>LastName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (PatientLastName.toUpperCase().equals("TEST") || PatientLastName.toUpperCase().equals("DEMO")) {
                    ErrorMsgs.append("<p style=\"color:black;\">THE PATIENT SEEMS TO BE A TEST PATIENT . DO NOT SUBMIT CLAIM WITHOUT DUE VERIFICATION <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>LastName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }
            if (!isEmpty(PatientFirstName)) {
                if (!PatientFirstName.matches(Name_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>FirstName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>FirstName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            final String MemberID_REGEX = "^(123456789|^(TEST)|^[0]{2,}|^[1]{2,}|^[2]{2,}|^[3]{2,}|^[4]{2,}|^[5]{2,}|^[6]{2,}|^[7]{2,}|^[8]{2,}|^[9]{2,})$";
//                    MemId
            if (!isEmpty(MemId)) {
                if (MemId.matches(MemberID_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>Member-ID</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>Member-ID</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (!isEmpty(ZipCode)) {
                if (isInValidZipCode(conn, State, ZipCode)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>ZipCode</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (!isEmpty(City)) {
                if (!City.matches(CITY_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>City</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>City</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (Integer.parseInt(DOB) > Integer.parseInt(ClaimCreateDate)) {
                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>DOB</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }


//            final String SSN_REGEX = "^([0-9]{9})$";
////            if (!isEmpty(MemId)) {
////                if (SSN.matches(SSN_REGEX)) {
////                    ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>SSN</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
////                }
////            } else {
////                ErrorMsgs.append("<p style=\"color:black;\">Subscriber <b>SSN</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
////            }


            if (!isEmpty(PriInsuranceNameId) && PriInsuranceNameId.equals("66006") && !isEmpty(MemId) && Integer.parseInt(DOS) > 20170101) {
                if (MemId.startsWith("YUB") || MemId.startsWith("YUX") || MemId.startsWith("XOJ") || MemId.startsWith("XOD") || MemId.startsWith("ZGJ") ||
                        MemId.startsWith("ZGD") || MemId.startsWith("YIJ") || MemId.startsWith("YID") || MemId.startsWith("YDJ") || MemId.startsWith("YDL")) {
                    ErrorMsgs.append("<p style=\"color:black;\">Medicare Advantage Claim need to be resubmitter with <b>PayerID : 66006</b> when <b>secondary</b> insurance is <b>Medicare</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            }


//            String Payer_State = "OH";
//            String Payer_ZipCode = "44306";
//            String Payer_City = "AKRON";
            if (!isEmpty(Payer_Zip)) {
                if (isInValidZipCode(conn, Payer_State, Payer_Zip)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Payer <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Payer <b>ZipCode</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if (!isEmpty(Payer_City)) {
                if (!Payer_City.matches(CITY_REGEX)) {
                    ErrorMsgs.append("<p style=\"color:black;\">Payer <b>City</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Payer <b>City</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }

            if ((PriFillingIndicator.equals("MA") || PriFillingIndicator.equals("MB")) && !String.valueOf(_TOB[2]).equals("1")) {
                ErrorMsgs.append("<p style=\"color:black;\">Medicare always accept the claim as <b>ORIGINAL/NEW CLAIM </b>. rejected due to claim <b>frequency code</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }
            String Related_Causes_Code = null;

            if (!isEmpty(AutoAccidentAddInfo) && AutoAccidentAddInfo.equals("1")) {
                Related_Causes_Code = "AA";
            } else if (!isEmpty(EmploymentStatusAddInfo) && EmploymentStatusAddInfo.equals("1")) {
                Related_Causes_Code = "EM";
            } else if (!isEmpty(OtherAccidentAddInfo) && OtherAccidentAddInfo.equals("1")) {
                Related_Causes_Code = "OA";
            }

//            String PatientControlNumber = PatientMRN + "FAM" + ClientId + timesSubmitted + "E" + ClaimNumber.replace("-", "");

            if (isEmpty(String.valueOf(_TOB[2]))) {
                ErrorMsgs.append("<p style=\"color:black;\"> <b>Frequency Code</b> is  <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }


            if (!isEmpty(AccidentIllnesDateAddInfo) && !isEmpty(LastMenstrualPeriodDateAddInfo)) {
                if (!isInValidDate(AccidentIllnesDateAddInfo, ClaimCreateDate)
                        && !isInValidDate(LastMenstrualPeriodDateAddInfo, ClaimCreateDate))
                    if (AccidentIllnesDateAddInfo.equals(LastMenstrualPeriodDateAddInfo))
                        ErrorMsgs.append("<p style=\"color:black;\"> <b>Illness Date</b> and <b>Last Menstrual Period Date</b>  cannot be <b>Same</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }


            if ((!isEmpty(Related_Causes_Code) && Related_Causes_Code.equals("AA")) || (!isEmpty(Related_Causes_Code) && Related_Causes_Code.equals("OA"))) {
                if (isEmpty(AccidentIllnesDateAddInfo)) {
                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Accident Date</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                } else if (isInValidDate(AccidentIllnesDateAddInfo, ClaimCreateDate)) {
                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Accident Date</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
            }


            if (!isEmpty(BillingProvider_Taxonomy)) {

                if (Taxonomy_for_InitialTreatment_List.contains(BillingProvider_Taxonomy)) {
                    System.out.println("BillingProvider_Taxonomy ->> " + BillingProvider_Taxonomy);
                    if (PriFillingIndicator.equals("MB") || PriFillingIndicator.equals("MA")) {
                        if (isEmpty(InitialTreatDateAddInfo)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Initial Treatment Date</b> is  <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        } else if (isInValidDate(InitialTreatDateAddInfo, ClaimCreateDate)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Initial Treatment Date</b> is  <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                }


                if (TaxonomyList.contains(BillingProvider_Taxonomy)) {
                    //Last Seen Date of Claim should Not be Equal to Date Time Min Value
                    if (isEmpty(LastSeenDateAddInfo)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Last Seen Date</b> is  <b>Missing</b>, Last Seen Date is required for this speciality code <b>[" + BillingProvider_Taxonomy + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    } else if (isInValidDate(LastSeenDateAddInfo, ClaimCreateDate)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Last Seen Date</b> is  <b>InValid</b>,  Last Seen Date is required for this speciality code <b>[" + BillingProvider_Taxonomy + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            }


            if (!isEmpty(ReferringProviderFirstName)) {
                if (!isEmpty(ReferringProviderNPI)) {
                    if (ReferringProviderNPI.matches(NPI_REGEX)) {
                        if (isValid(ReferringProviderNPI))
                            SuccessMsgs.append("<p style=\"color:black;\">Referring Provider<b> NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
                        else
                            ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                    "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                    } else {
                        ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>NPI</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


                if (!isEmpty(ReferringProviderLastName)) {
                    if (!ReferringProviderLastName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>LastName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>LastName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (!isEmpty(ReferringProviderFirstName)) {
                    if (!ReferringProviderFirstName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>FirstName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Referring Provider <b>FirstName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


                if (PriInsuranceNameId.equals("11315") || PriInsuranceNameId.equals("87726")) {
                    if (ReferringProviderNPI.equals(BillingProviderNPI) && ReferringProviderNPI.equals(BillingProviderNPI)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Referring Provider  is <b>Missing/InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            }


            if (!isEmpty(RenderingProvidersFirstName)) {
                // 2310B RENDERING PROVIDER NAME
//                Loop loop_2310B = loop_2300.addChild("2300");

                if (!isEmpty(RenderingProvidersNPI)) {
                    if (RenderingProvidersNPI.matches(NPI_REGEX)) {
                        if (isValid(RenderingProvidersNPI))
                            SuccessMsgs.append("<p style=\"color:black;\">Rendering Provider<b> NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
                        else
                            ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                    "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");


                        if (!isEmpty(ClientNPI)) {
                            if (ClientNPI.equals(RenderingProvidersNPI)) {
                                ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider and Service Location <b> NPI</b> cannot be  <b>Same</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        }
                    } else {
                        ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>NPI</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (!isEmpty(RenderingProvidersLastName)) {
                    if (!RenderingProvidersLastName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>LastName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>LastName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (!isEmpty(RenderingProvidersFirstName)) {
                    if (!RenderingProvidersFirstName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>FirstName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>FirstName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


//                loop_2310B.addSegment("NM1*82*1*" + RenderingProvidersLastName + "*" + RenderingProvidersFirstName + "****XX*" + RenderingProvidersNPI);//NM1 Rendering PROVIDER
//                String RenderingProviders_Taxonomy = "225100000X";

                if (!isEmpty(RenderingProviders_Taxonomy)) {
                    if (isInValidTaxonomy(conn, RenderingProviders_Taxonomy)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>Taxonomy Code</b> is  <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Rendering Provider <b>Taxonomy Code</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }
//                loop_2310B.addSegment("PRV*PE*PXC*" + RenderingProviders_Taxonomy); //PRV Rendering Provider SPECIALTY


            }

            if (!isEmpty(ClientNPI)) {
                if (ClientNPI.matches(NPI_REGEX)) {
                    if (isValid(ClientNPI))
                        SuccessMsgs.append("<p style=\"color:black;\">Client <b> NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
                    else
                        ErrorMsgs.append("<p style=\"color:black;\">Client <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Client <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                            "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                }

                if (!isEmpty(BillingProviderNPI)) {
                    if (ClientNPI.equals(BillingProviderNPI)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Billing Provider NPI </b> and <b>Client NPI</b> cannot be <b>same</b> <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            } else {
                ErrorMsgs.append("<p style=\"color:black;\">Client  <b>NPI</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
            }


            if (!isEmpty(SupervisingProviderFirstName)) {
                if (!isEmpty(SupervisingProviderNPI)) {
                    if (SupervisingProviderNPI.matches(NPI_REGEX)) {
                        if (isValid(SupervisingProviderNPI))
                            SuccessMsgs.append("<p style=\"color:black;\">Supervising Provider<b> NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
                        else
                            ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                    "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                    } else {
                        ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
                                "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>NPI</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


                if (!isEmpty(SupervisingProviderLastName)) {
                    if (!SupervisingProviderLastName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>LastName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>LastName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (!isEmpty(SupervisingProviderFirstName)) {
                    if (!SupervisingProviderFirstName.matches(Name_REGEX)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>FirstName</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\">Supervising Provider <b>FirstName</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

//                Loop loop_2310D = loop_2300.addChild("2300");
//                loop_2310D.addSegment("NM1*DQ*1*" + SupervisingProviderLastName + "*" + SupervisingProviderFirstName + "****XX*" + SupervisingProviderNPI);//NM1 Supervising PROVIDER
//                        loop_2310D.addSegment("REF*G2*B99937");//REF Referring PROVIDER SECONDARY IDENTIFICATION
            }


            Double totalcharges = 0.0;
            ArrayList<String> ProcedureCodes_List = new ArrayList<String>(Arrays.asList("99221", "99222", "99223"));
            ArrayList<String> ProcedureCodes_List_A_S_DATES_r1 = new ArrayList<String>(Arrays.asList("99221", "99222", "99223"));
            ArrayList<String> ProcedureCodes_List_A_S_DATES_r2 = new ArrayList<String>(Arrays.asList("99231", "99232", "99233"));
            ArrayList<String> Charge_ProcedureCodes = new ArrayList<String>();
            ArrayList<String> Charge_VaccineCodes = new ArrayList<String>();
            ArrayList<String> COVID_CPTS = new ArrayList<String>(Arrays.asList("91300", "91301", "0001A", "0002A", "0011A", "0012A", "91302", "91303", "0021A", "0022A", "0031A", "0003A", "0013A", "M0201", "99401", "0041A", "0042A", "0051A", "0052A", "0053A", "0054A", "0071A",
                    "0072A", "91304", "91305", "91307", "91306", "0064A", "0004A", "0034A"));

            ArrayList<String> COVID_CPTS_TJ = new ArrayList<String>(Arrays.asList("90471", "0001A", "0002A", "0003A", "0004A", "0011A", "0012A", "0013A", "0021A", "0022A", "0031A", "0034A", "0041A", "0042A", "0051A", "0052A", "0053A", "0054A", "0064A", "0071A", "0072A", "90662", "90672", "90674", "90682", "90685", "90686", "90687", "90688", "90694", "90756",
                    "90653", "91300", "91301", "91302", "91303", "91304", "91305", "91306", "91307"));

            String modifier = "";
            String MeasurementCode = null;
            String ProcedureCode = null;
            String Units = null;
            String Amount = null;
            String ServiceFromDate = null;
            String ServiceToDate = null;
            String mod1 = null;
            String mod2 = null;
            String mod3 = null;
            String mod4 = null;
            String revCode = "";
            boolean isENMProcedureCode = false;
            boolean addCovidMsg = false;
            boolean addAdminCodeMsg = false;
            boolean is_91303 = false;
            boolean errorMsgAdded = false;
            boolean isENM_SurgeryProcedureCode = false;

            int units_Of_90472 = -1;
            int units_Of_90460 = -1;
            int units_Of_90461 = -1;
            int sum_Of_units_Of_allVaccines = 0;


            for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
                modifier = "";
                ServiceFromDate = ChargesInput[i][0];
                ServiceToDate = ChargesInput[i][1];
                revCode = ChargesInput[i][6];
//                POS = ChargesInput[i][3];
//                if (POS_list_AMBULANCE_CLAIMS.contains(POS)) {
//                    if (isEmpty(HospitalizedFromDateAddInfo)) {
//                        ErrorMsgs.append("<p style=\"color:black;\"> <b>Admission DATE</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
//
//                    } else if (isInValidDate(HospitalizedFromDateAddInfo, ClaimCreateDate)) {
//                        ErrorMsgs.append("<p style=\"color:black;\"> <b>Admission DATE </b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
//
//                    }
//                }



/*                if (POS_list.contains(POS)) {
                    if (!isEmpty(PickUpAddressInfoCode) && !isEmpty(PickUpZipCodeInfoCode)
                            && !isEmpty(PickUpCityInfoCode) && !isEmpty(PickUpStateInfoCode)) {
                        if (!isEmpty(PickUpZipCodeInfoCode)) {
                            if (isInValidZipCode(conn, PickUpStateInfoCode, PickUpZipCodeInfoCode)) {
                                ErrorMsgs.append("<p style=\"color:black;\">Pick Up <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        } else {
                            ErrorMsgs.append("<p style=\"color:black;\">Pick Up <b>ZipCode</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                        if (!isEmpty(PickUpCityInfoCode)) {
                            if (!PickUpCityInfoCode.matches(CITY_REGEX)) {
                                ErrorMsgs.append("<p style=\"color:black;\">Pick Up <b>City</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        } else {
                            ErrorMsgs.append("<p style=\"color:black;\">Pick Up <b>City</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

//                    Loop loop_2310E = loop_2300.addChild("2300"); //AMBULANCE PICK-UP LOCATION
//                    loop_2310E.addSegment("NM1*PW*2");
//                    loop_2310E.addSegment("N3*" + PickUpAddressInfoCode);
//                    loop_2310E.addSegment("N4*" + PickUpCityInfoCode + "*" + PickUpStateInfoCode + "*" + PickUpZipCodeInfoCode);
                    }

                    if (!isEmpty(DropoffAddressInfoCode) && !isEmpty(DropoffCityInfoCode)
                            && !isEmpty(DropoffStateInfoCode) && !isEmpty(DropoffZipCodeInfoCode)) {
                        if (!isEmpty(DropoffZipCodeInfoCode)) {
                            if (isInValidZipCode(conn, DropoffStateInfoCode, DropoffZipCodeInfoCode)) {
                                ErrorMsgs.append("<p style=\"color:black;\">Drop off <b>ZipCode</b> does not match with <b>State</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        } else {
                            ErrorMsgs.append("<p style=\"color:black;\">Drop off <b>ZipCode</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                        if (!isEmpty(DropoffCityInfoCode)) {
                            if (!DropoffCityInfoCode.matches(CITY_REGEX)) {
                                ErrorMsgs.append("<p style=\"color:black;\">Drop off <b>City</b> is <b>InValid</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        } else {
                            ErrorMsgs.append("<p style=\"color:black;\">Drop off <b>City</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

//                    Loop loop_2310F = loop_2300.addChild("2300"); //AMBULANCE DROP-OFF LOCATION
//                    loop_2310F.addSegment("NM1*45*2");
//                    loop_2310F.addSegment("N3*" + DropoffAddressInfoCode);
//                    loop_2310F.addSegment("N4*" + DropoffCityInfoCode + "*" + DropoffStateInfoCode + "*" + DropoffZipCodeInfoCode);
                    }
                }*/


                Units = ChargesInput[i][11].contains(".") ? ChargesInput[i][11].substring(0, ChargesInput[i][11].indexOf(".")) : ChargesInput[i][11];
                Amount = ChargesInput[i][12];
                ProcedureCode = ChargesInput[i][2];
                mod1 = ChargesInput[i][5];
                mod2 = ChargesInput[i][6];
                mod3 = ChargesInput[i][7];
                mod4 = ChargesInput[i][8];
                //System.out.println("****ProcedureCode");
                if (isValidAnesthesiaCodes(conn, ProcedureCode)) {
                    MeasurementCode = "MJ";
                    if (!isEmpty(mod1)) {//mod1
                        modifier += ":" + mod1; //SV101-3
                        if (Units.equals("1") && mod1.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                        if (!isValidAnesthesiaModifier(conn, mod1)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Anesthesia Modifier</b>  is only allowed with  <b>Anesthesia Procedures [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                    }
                    if (!isEmpty(mod2)) {//mod2
                        modifier += ":" + mod2;
                        if (Units.equals("1") && mod2.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                        if (!isValidAnesthesiaModifier(conn, mod2)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Anesthesia Modifier</b>  is only allowed with  <b>Anesthesia Procedures [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (!mod3.equals("")) {//mod3
                        modifier += ":" + mod3;
                        if (Units.equals("1") && mod3.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }


                        if (!isValidAnesthesiaModifier(conn, mod3)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Anesthesia Modifier</b>  is only allowed with  <b>Anesthesia Procedures " + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (!mod4.equals("")) {//mod4
                        modifier += ":" + mod4;
                        if (Units.equals("1") && mod4.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }


                        if (!isValidAnesthesiaModifier(conn, mod4)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Anesthesia Modifier</b>  is only allowed with  <b>Anesthesia Procedures [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }


                } else {
                    MeasurementCode = "UN";
                    if (!isEmpty(mod1)) {//mod1
                        modifier += ":" + mod1; //SV101-3
                        if (ClientId == 32) {
                            if (Units.equals("1") && mod1.equals("50")) {
                                ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        }
                    }
                    if (!isEmpty(mod2)) {//mod2
                        modifier += ":" + 2;
                        if (Units.equals("1") && mod2.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (!isEmpty(mod3)) {//mod3
                        modifier += ":" + mod3;
                        if (Units.equals("1") && mod3.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (!isEmpty(mod4)) {//mod4
                        modifier += ":" + mod4;
                        if (Units.equals("1") && mod4.equals("50")) {
                            ErrorMsgs.append("<p style=\"color:black;\">Units must be greater than <b>ONE</b> when a modifier of <b>50</b> is used.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                }

                if (!isValidCLIACodes(conn, ProcedureCode)) {
                    if (modifier.contains("QW")) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>QW Modifier</b>  is not allowed with  <b>Procedure : [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }


                if (isValidConsultantProceduresCodes(conn, ProcedureCode)) {
                    if (isEmpty(ReferringProviderFirstName) && isEmpty(ReferringProviderLastName)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Referring provider</b>  is required with  <b>Consultation Procedure : [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }


                if (isValid_E_N_M_ProceduresCodes(conn, ProcedureCode) && !errorMsgAdded) {
                    //System.out.println(" isValid_E_N_M_ProceduresCodes ProcedureCode -> " + ProcedureCode);

                    if (isENM_SurgeryProcedureCode) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b> E&M Procedure </b> and  <b> E&M Surgery Codes </b> cannot be billed together on same DOS<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        isENM_SurgeryProcedureCode = false;
                        isENMProcedureCode = false;
                        errorMsgAdded = true;
                    } else
                        isENMProcedureCode = true;
                }


                if (isValid_E_N_M_Surgery_ProceduresCodes(conn, ProcedureCode) && !errorMsgAdded) {
                    //System.out.println("isValid_E_N_M_Surgery_ProceduresCodes ProcedureCode -> " + ProcedureCode);
                    if (isENMProcedureCode) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b> E&M Procedure </b> and  <b> E&M Surgery Codes </b> cannot be billed together on same DOS<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        isENM_SurgeryProcedureCode = false;
                        isENMProcedureCode = false;
                        errorMsgAdded = true;
                    } else
                        isENM_SurgeryProcedureCode = true;
                }


                if (isValid_EPSDT_ProceduresCodes(conn, ProcedureCode) && (PriFillingIndicator.equals("MC") || SecFillingIndicator.equals("MC"))
                        && (getAge(LocalDate.parse(_DOB)) < 21) && modifier.contains("EP")) {
                    ErrorMsgs.append("<p style=\"color:black;\">eligible medicaid recipient for <b>EPSDT</b> services  are less than <b>21 year</b> of age. patient  age is not appropriate to bill this service please remove modifier <b>EP</b>.<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


                if ((modifier.contains("GV") || modifier.contains("GW")) && (!PriFillingIndicator.equals("MB") || !SecFillingIndicator.equals("MB"))) {
                    ErrorMsgs.append("<p style=\"color:black;\">modifier <b> GW/GV </b> indicates hospice services, please file claim to <b>medicare</b> or remove the modifier<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                if (ProcedureCodes_List.contains(ProcedureCode)) {
                    if (Integer.parseInt(Units) > 1) {
                        ErrorMsgs.append("<p style=\"color:black;\">Hospital Admission Service For Cpt <b> [" + ProcedureCode + "] </b> Should Always Billed As <b>One</b> Unit <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }

                    if (!ServiceFromDate.equals(ServiceToDate)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Hospital Admission Service For Cpt <b> [" + ProcedureCode + "] </b> Start/End Dos Should Be Same <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }

                if (ProcedureCode.equals("G0447")) {
//                            if()

                }

                if (ProcedureCodes_List_A_S_DATES_r1.contains(ProcedureCode) || ProcedureCodes_List_A_S_DATES_r2.contains(ProcedureCode)) {
                    if (HospitalizedFromDateAddInfo.equals(HospitalizedToDateAddInfo)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Procedure Code <b>[" + ProcedureCode + "]</b> is inconsistent  <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }

                if ((PriFillingIndicator.equals("MA") || SecFillingIndicator.equals("MA")) ||
                        ((PriFillingIndicator.equals("MB") || SecFillingIndicator.equals("MB"))) ||
                        ((PriFillingIndicator.equals("16") || SecFillingIndicator.equals("16")))) {

                    if (isValidCLIACodes(conn, ProcedureCode) && !modifier.contains("QW")) {
                        ErrorMsgs.append("<p style=\"color:black;\">CLIA required procedure <b>[" + ProcedureCode + "]</b> found, Procedure may require <b>QW</b> modifier  <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    } else if (modifier.contains("QW") && !isValidCLIACodes(conn, ProcedureCode)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>QW</b> modifier found, Modifier may require <b>CLIA</b>procedure code  <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }


                    if (isValidDMEProceduresCodes(conn, ProcedureCode)) {
                        if (isEmpty(OrderingProvidersFirstName) && isEmpty(OrderingProvidersLastName)) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Ordering provider</b>  is required with  <b>DME Procedure : [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
//                            if ((ICDA.equals("Z23") || ICDB.equals("Z23") || ICDC.equals("Z23") ||
//                                    ICDD.equals("Z23") || ICDE.equals("Z23") || ICDF.equals("Z23")
//                                    || ICDG.equals("Z23") || ICDH.equals("Z23") || ICDI.equals("Z23")
//                                    || ICDJ.equals("Z23") || ICDK.equals("Z23") || ICDL.equals("Z23"))) {
//                                if (ProcedureCode.equals("G0008") || ProcedureCode.equals("G0009") || ProcedureCode.equals("G0010")) {
//                                    if(!containsValidRelativeAdminCode(conn,ProcedureCode,Charge_ProcedureCodes,"Z23")){
//                                        ErrorMsgs.append("<p style=\"color:black;\"><b>Bill Vaccine Code : "+getRespectiveVaccineCode(conn,ProcedureCode,"Z23").toString()+"</b>  is required to bill  <b>Relative Admin Code : [" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
//                                    }
//                                }
//
//
//                            }
                }

/*                else {
                    if (((!isEmpty(ICDA) && ICDA.equals("Z23"))
                            || (!isEmpty(ICDB) && ICDB.equals("Z23"))
                            || (!isEmpty(ICDC) && ICDC.equals("Z23"))
                            || (!isEmpty(ICDD) && ICDD.equals("Z23"))
                            || (!isEmpty(ICDE) && ICDE.equals("Z23"))
                            || (!isEmpty(ICDF) && ICDF.equals("Z23"))
                            || (!isEmpty(ICDG) && ICDG.equals("Z23"))
                            || (!isEmpty(ICDH) && ICDH.equals("Z23"))
                            || (!isEmpty(ICDI) && ICDI.equals("Z23"))
                            || (!isEmpty(ICDJ) && ICDJ.equals("Z23"))
                            || (!isEmpty(ICDK) && ICDK.equals("Z23"))
                            || (!isEmpty(ICDL) && ICDL.equals("Z23"))
                    )) {

                        if (isValidVaccineCode(conn, ProcedureCode)) {
                            Charge_VaccineCodes.add(ProcedureCode);
                            sum_Of_units_Of_allVaccines += Integer.parseInt(Units);
                        }

                        if (ProcedureCode.equals("90472"))
                            units_Of_90472 = Integer.parseInt(Units);

                        if (ProcedureCode.equals("90460"))
                            units_Of_90460 = Integer.parseInt(Units);

                        if (ProcedureCode.equals("90461"))
                            units_Of_90461 = Integer.parseInt(Units);
                    }
                }*/


                if (isValidPQRSCodes(conn, ProcedureCode) && Integer.parseInt(Amount) != 0) {
                    ErrorMsgs.append("<p style=\"color:black;\"><b>PQRS</b> Procedure found, <b>Amount</b> should be equal to 0 <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }


 /*               if (isValid_OFFICE_E_N_M(conn, ProcedureCode) && (WELL_VISIT_ICDs.contains(ICDA) || WELL_VISIT_ICDs.contains(ICDB) || WELL_VISIT_ICDs.contains(ICDC) ||
                        WELL_VISIT_ICDs.contains(ICDD) || WELL_VISIT_ICDs.contains(ICDE) || WELL_VISIT_ICDs.contains(ICDF)
                        || WELL_VISIT_ICDs.contains(ICDG) || WELL_VISIT_ICDs.contains(ICDH) || WELL_VISIT_ICDs.contains(ICDI)
                        || WELL_VISIT_ICDs.contains(ICDJ) || WELL_VISIT_ICDs.contains(ICDK) || WELL_VISIT_ICDs.contains(ICDL))) {
                    ErrorMsgs.append("<p style=\"color:black;\">Procedure Code <b>[" + ProcedureCode + "]</b>  is not <b>billable</b> with wellness ICDs <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }*/

                if ((PriFillingIndicator.equals("MA") || SecFillingIndicator.equals("MA"))
                        || ((PriFillingIndicator.equals("MB") || SecFillingIndicator.equals("MB")))) {
                    if (isValid_I_Codes(conn, ProcedureCode)) {
                        ErrorMsgs.append("<p style=\"color:black;\">Procedure Code <b>[" + ProcedureCode + "]</b>  is  <b>I HCPCS Code</b> which is not payable by <b>Medicare</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }


                if (ReasonVisit.toUpperCase().contains("CORONA VIRUS") || ReasonVisit.toUpperCase().contains("COVID")) {
                    if (!COVID_CPTS.contains(ProcedureCode) && !addCovidMsg) {
                        ErrorMsgs.append("<p style=\"color:black;\">Please Bill COVID-19 codes <b>" + COVID_CPTS.toString() + "</b>  and  create separate charge for rest of the CPT Codes<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        addCovidMsg = true;
                    }

                    if (ProcedureCode.equals("91303") && !is_91303) {
                        is_91303 = true;
                    }

                    if (!Arrays.asList("91300", "91301", "91302", "0031A").contains(ProcedureCode) && is_91303 && !addAdminCodeMsg) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Admin Code " + Arrays.asList("91300", "91301", "91302", "0031A").toString() + " </b>  is  missing with  <b>Vaccine [91303]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        addAdminCodeMsg = true;
                    }

                    if (COVID_CPTS_TJ.contains(ProcedureCode) && !modifier.contains("TJ")) {
                        ErrorMsgs.append("<p style=\"color:black;\">Per COVID Guidelines,  <b>Modifier : TJ </b>  is  required with CPT <b>[" + ProcedureCode + "]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }


                if (ValidMammographyCPT(conn, ProcedureCode) && (!modifier.contains("26") || isEmpty(modifier))) {
                    if (isEmpty(MemmoCertAddInfo)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>FDA Certification Number</b> is Missing with Mammography services [<b>" + ProcedureCode + "</b>] Please check the location settings and update FDA Certification Number <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }

/*
                if (((!isEmpty(ICDA) && ICDA.startsWith("F"))
                        || (!isEmpty(ICDB) && ICDB.startsWith("F"))
                        || (!isEmpty(ICDC) && ICDC.startsWith("F"))
                        || (!isEmpty(ICDD) && ICDD.startsWith("F"))
                        || (!isEmpty(ICDE) && ICDE.startsWith("F"))
                        || (!isEmpty(ICDF) && ICDF.startsWith("F"))
                        || (!isEmpty(ICDG) && ICDG.startsWith("F"))
                        || (!isEmpty(ICDH) && ICDH.startsWith("F"))
                        || (!isEmpty(ICDI) && ICDI.startsWith("F"))
                        || (!isEmpty(ICDJ) && ICDJ.startsWith("F"))
                        || (!isEmpty(ICDK) && ICDK.startsWith("F"))
                        || (!isEmpty(ICDL) && ICDL.startsWith("F"))
                )) {
                    if (isValid_E_N_M_ProceduresCodes(conn, ProcedureCode)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>Mental Disorder</b> diagnosis may not be billed with E&Ms <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
*/
                if (!isEmpty(revCode)) {
                    if (!isValidRevCode(conn, revCode)) {
                        ErrorMsgs.append("<p style=\"color:black;\"><b>REVENUE CODE</b> USE DOESNOT MATCH WITH NATIONAL UNIFORM BILLING COMMITTEE (NUBC). PLEASE USE THIS LINK FOR CORRECT REVENUE CODE: https://med.noridianmedicare.com/web/jfa/topics/claim-submission/revenue-codes <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                } else {
                    ErrorMsgs.append("<p style=\"color:black;\"><b>Revenue Code </b> is Missing. It cannot be null or empty <span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                }

                Charge_ProcedureCodes.add(ProcedureCode);
            }

/*            if ((PriFillingIndicator.equals("MA") || SecFillingIndicator.equals("MA")) || ((PriFillingIndicator.equals("MB") || SecFillingIndicator.equals("MB"))) || ((PriFillingIndicator.equals("16") || SecFillingIndicator.equals("16")))) {
                if (((!isEmpty(ICDA) && ICDA.equals("Z23"))
                        || (!isEmpty(ICDB) && ICDB.equals("Z23"))
                        || (!isEmpty(ICDC) && ICDC.equals("Z23"))
                        || (!isEmpty(ICDD) && ICDD.equals("Z23"))
                        || (!isEmpty(ICDE) && ICDE.equals("Z23"))
                        || (!isEmpty(ICDF) && ICDF.equals("Z23"))
                        || (!isEmpty(ICDG) && ICDG.equals("Z23"))
                        || (!isEmpty(ICDH) && ICDH.equals("Z23"))
                        || (!isEmpty(ICDI) && ICDI.equals("Z23"))
                        || (!isEmpty(ICDJ) && ICDJ.equals("Z23"))
                        || (!isEmpty(ICDK) && ICDK.equals("Z23"))
                        || (!isEmpty(ICDL) && ICDL.equals("Z23"))
                )) {
                    if (Charge_ProcedureCodes.contains("G0009")) {
                        if (!containsValidRelativeAdminCode(conn, "G0009", Charge_ProcedureCodes, "Z23")) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Bill Vaccine Code : " + getRespectiveVaccineCode(conn, "G0009", "Z23").toString() + "</b>  is required to bill  <b>Relative Admin Code : [G0009]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (Charge_ProcedureCodes.contains("G0010")) {
                        if (!containsValidRelativeAdminCode(conn, "G0010", Charge_ProcedureCodes, "Z23")) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Bill Vaccine Code : " + getRespectiveVaccineCode(conn, "G0010", "Z23").toString() + "</b>  is required to bill  <b>Relative Admin Code : [G0010]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }
                    if (Charge_ProcedureCodes.contains("G0008")) {
                        if (!containsValidRelativeAdminCode(conn, "G0008", Charge_ProcedureCodes, "Z23")) {
                            ErrorMsgs.append("<p style=\"color:black;\"><b>Bill Vaccine Code : " + getRespectiveVaccineCode(conn, "G0008", "Z23").toString() + "</b>  is required to bill  <b>Relative Admin Code : [G0008]</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    }

                }
            }
            else {
                if (((!isEmpty(ICDA) && ICDA.equals("Z23"))
                        || (!isEmpty(ICDB) && ICDB.equals("Z23"))
                        || (!isEmpty(ICDC) && ICDC.equals("Z23"))
                        || (!isEmpty(ICDD) && ICDD.equals("Z23"))
                        || (!isEmpty(ICDE) && ICDE.equals("Z23"))
                        || (!isEmpty(ICDF) && ICDF.equals("Z23"))
                        || (!isEmpty(ICDG) && ICDG.equals("Z23"))
                        || (!isEmpty(ICDH) && ICDH.equals("Z23"))
                        || (!isEmpty(ICDI) && ICDI.equals("Z23"))
                        || (!isEmpty(ICDJ) && ICDJ.equals("Z23"))
                        || (!isEmpty(ICDK) && ICDK.equals("Z23"))
                        || (!isEmpty(ICDL) && ICDL.equals("Z23"))
                )) {
                    if (Charge_ProcedureCodes.contains("90471")) {
                        if (Charge_VaccineCodes.size() > 1) {
                            if (!Charge_ProcedureCodes.contains("90472")) {
                                if ((Charge_VaccineCodes.size() - 1) == 1)
                                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Additional Vaccine of " + (Charge_VaccineCodes.size() - 1) + "</b> Administration Code is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                                else
                                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Additional Vaccine of " + (Charge_VaccineCodes.size() - 1) + "</b> Administration Codes are <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            } else if (Charge_ProcedureCodes.contains("90472") && (units_Of_90472 != (Charge_VaccineCodes.size() - 1))) {
                                ErrorMsgs.append("<p style=\"color:black;\"> <b>Units</b> of <b>90472</b> must be equal to number of additional vaccine codes<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        }
                    } else {
                        ErrorMsgs.append("<p style=\"color:black;\"> <b>Primary Vaccine admin 90471</b> is <b>Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }


                if (((!isEmpty(ICDA) && ICDA.equals("Z23"))
                        || (!isEmpty(ICDB) && ICDB.equals("Z23"))
                        || (!isEmpty(ICDC) && ICDC.equals("Z23"))
                        || (!isEmpty(ICDD) && ICDD.equals("Z23"))
                        || (!isEmpty(ICDE) && ICDE.equals("Z23"))
                        || (!isEmpty(ICDF) && ICDF.equals("Z23"))
                        || (!isEmpty(ICDG) && ICDG.equals("Z23"))
                        || (!isEmpty(ICDH) && ICDH.equals("Z23"))
                        || (!isEmpty(ICDI) && ICDI.equals("Z23"))
                        || (!isEmpty(ICDJ) && ICDJ.equals("Z23"))
                        || (!isEmpty(ICDK) && ICDK.equals("Z23"))
                        || (!isEmpty(ICDL) && ICDL.equals("Z23"))
                )) {


                    System.out.println("No. of Vaccines -> " + Charge_VaccineCodes.size());
                    System.out.println("Units units_Of_90460  -> " + units_Of_90460);
                    if (Charge_ProcedureCodes.contains("90460")) {
                        if (Charge_VaccineCodes.size() > 1) {
                            if (!Charge_ProcedureCodes.contains("90461")) {
                                if (Charge_VaccineCodes.size() == 1)
                                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Vaccine Toxoid of " + Charge_VaccineCodes.size() + "</b> Administration Code is missing<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                                else
                                    ErrorMsgs.append("<p style=\"color:black;\"> <b>Vaccine Toxoid of " + Charge_VaccineCodes.size() + "</b> Administration Codes are missing<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            } else if (Charge_ProcedureCodes.contains("90461") && (units_Of_90461 != sum_Of_units_Of_allVaccines)) {
                                ErrorMsgs.append("<p style=\"color:black;\"> <b>Units</b> of <b>90461</b> must be equal to number of additional vaccine codes<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                            }
                        }


                        if (units_Of_90460 != Charge_VaccineCodes.size()) {
                            ErrorMsgs.append("<p style=\"color:black;\"> <b>Units</b> of <b>90460</b> must be equal to number of additional vaccine codes<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }

                        if ((getAge(LocalDate.parse(_DOB)) > 18)) {
                            ErrorMsgs.append("<p style=\"color:black;\"> <b>Age</b> of patient must be less then or equal to <b> 18 Yrs </b> for <b>90460</b> & <b>90461</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                        }
                    } else {
                        ErrorMsgs.append("<p style=\"color:black;\"> <b>Primary Vaccine admin 90460</b> is <b> Missing</b><span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    }
                }
            }*/


            String vaccGrp1 = null, vaccGrp2 = null;


            for (String CPT :
                    Charge_ProcedureCodes) {
                if (CPT.equals("G0009") || CPT.equals("G0010") || CPT.equals("G0008")) {
                    vaccGrp1 = CPT;
                }

                if (CPT.equals("90471") || CPT.equals("90472")) {
                    vaccGrp2 = CPT;
                }

                if (vaccGrp1 != null && vaccGrp2 != null) {
                    ErrorMsgs.append("<p style=\"color:black;\">System identifies medicare and commercial vaccine admins <b>[" + vaccGrp1 + "]</b> & <b>[" + vaccGrp2 + "]</b> alongside which is incorrect by coding point of view. please review coding before filing the claim<span> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>\n");
                    break;
                }

            }

            Instant end = Instant.now();
            Duration ExecutionTime = Duration.between(start, end);
            if (ErrorMsgs.length() != 0)
                out.println(ErrorMsgs + "~" + ExecutionTime);
            else {
                out.println("1");
            }
        } catch (Exception e) {
            out.println("Error in : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
//            Services.DumException("AddInfo", "Error in Save Claim Method --NO SP", request, e,getServletContext());
//            try {
//                helper.SendEmailWithAttachment("Error in Claim Saving ** (AddInfo Main Catch^^" + ClientName, context, e, "AddInfo", "SaveClaim", conn);
//                Services.DumException("SaveClaim^^" + ClientName + " Error in Save Claim Method", "AddInfo ", request, e);
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("FormName", "PatientUpdateInfo");
//                Parser.SetField("ActionID", "GetInput&ID=" + PatientRegId);
//                Parser.SetField("Message", "MES#014");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
//            } catch (Exception ex) {
//            }
        }
    }

    public void Institutional_EDIFile(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        Instant start = Instant.now();
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        String InterControlNo = "";
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String ClaimNumber = request.getParameter("ClaimNumber").trim();
//int ClaimType = Integer.parseInt(request.getParameter("ClaimType").trim());
        int VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        String DateTime = "";
        String DateNow = "";
        String PatientFirstName = "";
        String PatientLastName = "";
        String DOB = "";
        String Gender = "";
        String PriInsuredName = "";
        String TaxanomySpecialty = "";
        int ClaimInfoMasterId = 0;
        int SelfPayChk = 0;
        String ClientAddress = "";
        String ClientCity = "";
        String ClientTaxID = "";
        String ClientState = "";
        String ClientZipCode = "";
        String ClientPhone = "";
        String ClientNPI = "";

//Claim Basic Info Variable
        String RefNumber = "";
        String TypeBillText = "";
        String PatientName = "";
        String PatientMRN = "";
        String AcctNo = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String StateCode = "";
        String ZipCode = "";
        String DOS = "";
        String UploadDate = "";
        String AttendingProviderLastName = "";
        String AttendingProviderFirstName = "";
        String AttendingProviderNPI = "";
        String AttendingProvider_Taxonomy = "";
        String BillingProvidersLastName = "";
        String BillingProvidersFirstName = "";
        String BillingProvidersNPI = "";
        String BillingProvider_Taxonomy = "";
        String OperatingProviderLastName = "";
        String OperatingProviderFirstName = "";
        String OperatingProviderNPI = "";
        String ClientName = "";
        String PriInsuranceName = "";
        String PriInsuranceNameId = "";
        String MemId = "";
        String PolicyType = "";
        String GrpNumber = "";
        String SecondaryInsurance = "";
        String SecondaryInsuranceId = "";
        String SecondaryInsuranceMemId = "";
        String SecondaryInsuranceGrpNumber = "";
        String CreationDate = "";
        String ClaimType = "";
        String ClaimCreateDate = "";
        String ClaimCreateTime = "";
//
//        //Charges Variables
        double TotalChargeAmount = 0.00;
//        String ChargesTableCount = "0";
//        String ChargesString = "";
//
//Additional Info Variable
        String StatmentCoverFromDateAddInfo = "";
        String StatmentCoverToDateAddInfo = "";
        String AdmissionDateAddInfo = "";
        String AdmissionHourAddInfo = "";
        String AdmissionTypeAddInfo = "";
        String AdmissionSourceAddInfo = "";
        String DischargeHourAddInfo = "";
        String PatientStatusAddInfo = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String PPSAddInfo = "";
        String RemarksAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String ReleaseInfoAddInfo = "";
        String AssofBenifitAddInfo = "";
        String ProvAccAssigAddInfo = "";
//
//Information Codes variable
        String PrincipalDiagInfoCodes = "";
        String POAInfoCodes = "";
        String AdmittingDiagInfoCodes = "";
        String PrincipalProcedureInfoCodes = "";
        String PrincipalProcedureDateInfoCodes = "";
        String ExternalCauseInjuryTableCount = "0";
        String ExternalCauseInjuryString = "";
        String ReasonVisitTableCount = "0";
        String ReasonVisitString = "";
        String OtherDiagnosisString = "";
        String OtherDiagnosisTableCount = "0";
        StringBuffer ErrorMsgs = new StringBuffer();
        final String NPI_REGEX = "[0-9]{10}";
        String PatientRelationtoPrimary = "";
        String PriFillingIndicator = "";

        String Payer_Address = "";
        String Payer_City = "";
        String Payer_State = "";
        String Payer_Zip = "";
        String timesSubmitted = "";


        String PatientControlNumber = "";


        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,''), IFNULL(TaxanomySpecialty,''), IFNULL(TaxID,'') from oe.clients" +
                    " where Id = " + ClientId;
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientAddress = rset.getString(1);
                ClientCity = rset.getString(2);
                ClientState = rset.getString(3);
                ClientZipCode = rset.getString(4);
                ClientPhone = rset.getString(5);
                ClientNPI = rset.getString(6);
                TaxanomySpecialty = rset.getString(7);
                ClientTaxID = rset.getString(8);
            }
            rset.close();
            stmt.close();

            //Validating NPI
//            if (ClientNPI.matches(NPI_REGEX)) {
//                if(isValid(ClientNPI)) ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
//                else ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
//                        "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
//            } else {
//                ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
//                        "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
//            }


            Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster " +
                    " where Status = 0 and PatientRegId = " + PatientRegId + " and VisitId = " + VisitId +
                    " and ClaimNumber = '" + ClaimNumber + "'";
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found == 0) {
                out.println("Please Save Claim First and then Create EDI");
                return;
            } else {

                Query = "Select IFNULL(MAX(InterControlNo),'') from " + Database + ".ClaimInfoMaster ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InterControlNo = rset.getString(1);
                }
                rset.close();
                stmt.close();
//out.println("InterControlNo;---1-----"+InterControlNo);
                if (InterControlNo.equals("")) {
                    //out.println("inside 1 icn");
                    InterControlNo = "100000001";
                } else if (InterControlNo.length() > 9) {
                    //out.println("inside 2 icn");
                    InterControlNo = "100000001";
                } else {
                    //out.println("inside 3 icn");
                    InterControlNo = String.valueOf((Integer.parseInt(InterControlNo) + 1));
                }
//out.println("InterControlNo;---2----"+InterControlNo);
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "Update " + Database + ".ClaimInfoMaster Set InterControlNo = ? " +
                                    " where PatientRegId = ? and VisitId = ? and ClaimNumber = ? ");
                    MainReceipt.setString(1, (InterControlNo));
                    MainReceipt.setInt(2, PatientRegId);
                    MainReceipt.setInt(3, VisitId);
                    MainReceipt.setString(4, ClaimNumber);
                    //out.println("MainReceipt:--"+MainReceipt.toString());
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println(e.getMessage());
                    helper.SendEmailWithAttachment("Error in UpdatingtInstitionalEDI ** (AddInfo^^ MES#ICN)", context, e, "AddInfo", "InstitutuionalEDI", conn);
                    Services.DumException("AddInfo", "InstitionalEDI", request, e);
                }


                Query = "Select ChargeMasterTableName from oe.clients where ID = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ChargeMasterTableName = rset.getString(1).trim();
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(DATE_FORMAT(DOB,'%Y%m%d'),''), DATE_FORMAT(NOW(),'%d%m%y%k%i%s'), IFNULL(Gender,''), DATE_FORMAT(NOW(),'%m%d%y'), " +
                        "IFNULL(SelfPayChk,'0')" +
                        " from " + Database + ".PatientReg where ID = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientFirstName = rset.getString(1);
                    PatientLastName = rset.getString(2);
                    Address = rset.getString(3);
                    City = rset.getString(4);
                    State = rset.getString(5);
                    ZipCode = rset.getString(6);
                    DOB = rset.getString(7);
                    DateTime = rset.getString(8);
                    Gender = rset.getString(9).trim();
                    DateNow = rset.getString(10);
                    SelfPayChk = rset.getInt(11);
                }
                rset.close();
                stmt.close();
                if (!Gender.equals("")) {
                    Gender = Gender.substring(0, 1).toUpperCase();
                } else {
                    Gender = "";
                }

                if (SelfPayChk == 1) {
                    Query = "Select IFNULL(PriInsurerName,''),IFNULL(PatientRelationtoPrimary,'') from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuredName = rset.getString(1);
                        PatientRelationtoPrimary = rset.getString(2);
                    }
                    rset.close();
                    stmt.close();

                    if (PriInsuredName.equals("")) {
                        PriInsuredName = PatientLastName + ", " + PatientFirstName;
                    }

                } else {
                    PriInsuredName = PatientLastName + ", " + PatientFirstName;
                }


                if (!State.equals("")) {
                    if (State.length() > 2) {
                        Query = "Select IFNULL(StateCode,'') from " + Database + ".StateList where ltrim(rtrim(Upper(StateName))) = ltrim(rtrim(UPPER('" + State + "')))";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            StateCode = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    } else {
                        StateCode = State.toUpperCase();
                    }

                } else {
                    StateCode = "";
                }

                Query = "Select a.Id,IFNULL(RefNumber,''), IFNULL(a.TypeBillText,''), IFNULL(a.PatientName,''), IFNULL(a.PatientMRN,''), " +
                        " IFNULL(a.AcctNo,''), IFNULL(a.PhNumber,''), " +
                        " IFNULL(a.Email,''), IFNULL(a.Address,''), IFNULL(a.DOS,''), " +
                        " IFNULL(b.DoctorsLastName,''), IFNULL(b.DoctorsFirstName,'') , IFNULL(b.NPI,'') as AttendingNPI, " +
                        " IFNULL(c.DoctorsLastName,''), IFNULL(c.DoctorsFirstName,''), IFNULL(c.NPI,'') as BillingNPI, " +
                        " IFNULL(LTRIM(rtrim(REPLACE(d.PayerName,'Servicing States','') )),'') as PriInsuranceName, IFNULL(a.MEMId,''), IFNULL(a.PolicyType,''), IFNULL(a.GrpNumber,''), " +
                        " IFNULL(LTRIM(rtrim(REPLACE(e.PayerName,'Servicing States','') )),'') as SecondaryInsurance, " +
                        " IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(a.SecondaryInsuranceGrpNumber,''), IFNULL(ClientName,''), " +
                        " IFNULL(f.DoctorsLastName,''), IFNULL(f.DoctorsFirstName,''), IFNULL(f.NPI,'') as OperatingProvider, " +
                        " DATE_FORMAT(a.CreatedDate,'%Y%m%d'), CASE WHEN a.ClaimType = 1 THEN 'CI' WHEN a.ClaimType = 2 THEN 'CP' ELSE 'CL' END, " +
                        " DATE_FORMAT(a.CreatedDate,'%y%m%d'), DATE_FORMAT(a.CreatedDate,'%H%i'), IFNULL(d.PayerID,''), IFNULL(e.PayerID,''),IFNULL(c.TaxonomySpecialty,''),IFNULL(e.ClaimIndicator_I,'')," +
                        " IFNULL(d.Address,''),IFNULL(d.City,''),IFNULL(d.State,''),IFNULL(d.Zip,''),IFNULL(timesSubmitted,0), IFNULL(b.TaxonomySpecialty,'') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN " + Database + ".DoctorsList b on a.AttendingProvider = b.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList c on a.BillingProviders = c.Id " +
                        " LEFT JOIN ClaimMasterDB.ProfessionalPayersWithFC d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN ClaimMasterDB.ProfessionalPayersWithFC e on a.SecondaryInsuranceId = e.Id " +
//                        " LEFT JOIN oe_2.ProfessionalPayers d on a.PriInsuranceNameId = d.Id " +
//                        " LEFT JOIN oe_2.ProfessionalPayers e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        "  where a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
                //System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    RefNumber = rset.getString(2);
                    TypeBillText = rset.getString(3);
                    PatientName = rset.getString(4).replaceAll(" ", "").replaceAll("'", "");
                    PatientMRN = rset.getString(5);
                    AcctNo = rset.getString(6);
                    PhNumber = rset.getString(7);
                    Email = rset.getString(8);
                    //Address = rset.getString(9);
                    DOS = rset.getString(10).replaceAll("/", "");
                    AttendingProviderLastName = rset.getString(11);
                    AttendingProviderFirstName = rset.getString(12);
                    AttendingProviderNPI = rset.getString(13);
                    BillingProvidersLastName = rset.getString(14);
                    BillingProvidersFirstName = rset.getString(15);
                    BillingProvidersNPI = rset.getString(16);
                    PriInsuranceName = (rset.getString(17).replaceAll(":", " ")).trim().replaceAll("&", "");
                    MemId = rset.getString(18);
                    PolicyType = rset.getString(19);
                    GrpNumber = rset.getString(20);
                    SecondaryInsurance = rset.getString(21);
                    SecondaryInsuranceMemId = rset.getString(22);
                    SecondaryInsuranceGrpNumber = rset.getString(23);
                    ClientName = rset.getString(24);
                    OperatingProviderLastName = rset.getString(25);
                    OperatingProviderFirstName = rset.getString(26);
                    OperatingProviderNPI = rset.getString(27);
                    CreationDate = rset.getString(28);
                    ClaimType = rset.getString(29);
                    ClaimCreateDate = rset.getString(30);
                    ClaimCreateTime = rset.getString(31);
                    PriInsuranceNameId = rset.getString(32);
                    SecondaryInsuranceId = rset.getString(33);
                    BillingProvider_Taxonomy = rset.getString(34);
                    PriFillingIndicator = rset.getString(35);
                    Payer_Address = rset.getString(36);
                    Payer_City = rset.getString(37);
                    Payer_State = rset.getString(38);
                    Payer_Zip = rset.getString(39);
                    timesSubmitted = rset.getString(40) + 1;
                    AttendingProvider_Taxonomy = rset.getString(41);
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(StatmentCoverFromDateAddInfo,''), IFNULL(StatmentCoverToDateAddInfo,''), " +
                        " IFNULL(AdmissionDateAddInfo,''), IFNULL(AdmissionHourAddInfo,''), IFNULL(AdmissionTypeAddInfo,''), " +
                        " IFNULL(AdmissionSourceAddInfo,''), IFNULL(DischargeHourAddInfo,''), IFNULL(PatientStatusAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'')," +
                        " IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(OtherAccidentAddInfo,''), IFNULL(PPSAddInfo,''), " +
                        " IFNULL(RemarksAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), IFNULL(ReleaseInfoAddInfo,''), IFNULL(AssofBenifitAddInfo,''), " +
                        " IFNULL(ProvAccAssigAddInfo,'') " +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    StatmentCoverFromDateAddInfo = rset.getString(1);
                    StatmentCoverToDateAddInfo = rset.getString(2);
                    AdmissionDateAddInfo = rset.getString(3);
                    AdmissionHourAddInfo = rset.getString(4);
                    AdmissionTypeAddInfo = rset.getString(5);
                    AdmissionSourceAddInfo = rset.getString(6);
                    DischargeHourAddInfo = rset.getString(7);
                    PatientStatusAddInfo = rset.getString(8);
                    DelayReasonCodeAddInfo = rset.getString(9);
                    EmploymentStatusAddInfo = rset.getString(10);
                    AutoAccidentAddInfo = rset.getString(11);
                    OtherAccidentAddInfo = rset.getString(12);
                    PPSAddInfo = rset.getString(13);
                    RemarksAddInfo = rset.getString(14);
                    AutoAccident_StateAddInfo = rset.getString(15);
                    ReleaseInfoAddInfo = rset.getString(16);
                    AssofBenifitAddInfo = rset.getString(17);
                    ProvAccAssigAddInfo = rset.getString(18);
                }
                rset.close();
                stmt.close();

                if (!StatmentCoverFromDateAddInfo.equals("")) {
                    StatmentCoverFromDateAddInfo = StatmentCoverFromDateAddInfo.replaceAll("/", "");
                    StatmentCoverFromDateAddInfo = StatmentCoverFromDateAddInfo.substring(4, 8) + StatmentCoverFromDateAddInfo.substring(0, 2) + StatmentCoverFromDateAddInfo.substring(2, 4);
                }
                if (!StatmentCoverToDateAddInfo.equals("")) {
                    StatmentCoverToDateAddInfo = StatmentCoverToDateAddInfo.replaceAll("/", "");
                    StatmentCoverToDateAddInfo = StatmentCoverToDateAddInfo.substring(4, 8) + StatmentCoverToDateAddInfo.substring(0, 2) + StatmentCoverToDateAddInfo.substring(2, 4);
                }
                if (!AdmissionDateAddInfo.equals("")) {
                    AdmissionDateAddInfo = AdmissionDateAddInfo.replaceAll("/", "");
                    AdmissionDateAddInfo = AdmissionDateAddInfo.substring(4, 8) + AdmissionDateAddInfo.substring(0, 2) + AdmissionDateAddInfo.substring(2, 4);
                }

                Query = "Select IFNULL(PrincipalDiagInfoCodes,''), IFNULL(POAInfoCodes,''), IFNULL(AdmittingDiagInfoCodes,''), " +
                        " IFNULL(PrincipalProcedureInfoCodes,''), IFNULL(PrincipalProcedureDateInfoCodes,'') " +
                        "from " + Database + ".ClaimInformationCode where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PrincipalDiagInfoCodes = rset.getString(1);
                    POAInfoCodes = rset.getString(2);
                    AdmittingDiagInfoCodes = rset.getString(3);
                    PrincipalProcedureInfoCodes = rset.getString(4);
                    PrincipalProcedureDateInfoCodes = rset.getString(5);
                }
                rset.close();
                stmt.close();

                if (!PrincipalDiagInfoCodes.equals("")) {
                    if (PrincipalDiagInfoCodes.contains(".")) {
                        PrincipalDiagInfoCodes = PrincipalDiagInfoCodes.replace(".", "");
                    }
                }
                if (!AdmittingDiagInfoCodes.equals("")) {
                    if (AdmittingDiagInfoCodes.contains(".")) {
                        AdmittingDiagInfoCodes = AdmittingDiagInfoCodes.replace(".", "");
                    }
                }
                if (!PrincipalProcedureInfoCodes.equals("")) {
                    if (PrincipalProcedureInfoCodes.contains(".")) {
                        PrincipalProcedureInfoCodes = PrincipalProcedureInfoCodes.replace(".", "");
                    }
                }

                String ST02 = "";

                if (ClaimNumber.length() > 4) {
                    ST02 = ClaimNumber.substring(ClaimNumber.length() - 4);
                } else {
                    ST02 = "0222";
                }

                StringBuilder ISA = new StringBuilder();
                StringBuilder GS = new StringBuilder();
                StringBuilder ST = new StringBuilder();
                StringBuilder BHT = new StringBuilder();
                StringBuilder NM1 = new StringBuilder();
                StringBuilder PER = new StringBuilder();
                StringBuilder NM1_2 = new StringBuilder();
                StringBuilder HL_1 = new StringBuilder();
                StringBuilder PRV_BI = new StringBuilder();
                StringBuilder NM1_3 = new StringBuilder();
                StringBuilder N3_1 = new StringBuilder();
                StringBuilder N4_1 = new StringBuilder();
                StringBuilder REF_1 = new StringBuilder();
                StringBuilder HL_2 = new StringBuilder();
                StringBuilder SBR = new StringBuilder();
                StringBuilder NM1_4 = new StringBuilder();
                StringBuilder N3_2 = new StringBuilder();
                StringBuilder N4_2 = new StringBuilder();
                StringBuilder DMG = new StringBuilder();
                StringBuilder NM1_5 = new StringBuilder();
                StringBuilder N3_3 = new StringBuilder();
                StringBuilder N4_3 = new StringBuilder();
                StringBuilder CLM = new StringBuilder();
                StringBuilder DTP_1 = new StringBuilder();
                StringBuilder DTP_2 = new StringBuilder();
                StringBuilder DTP_3 = new StringBuilder();
                StringBuilder CL1 = new StringBuilder();
                StringBuilder REF_2 = new StringBuilder();
                StringBuilder HI_1_ABK = new StringBuilder();
                StringBuilder HI_1_ABJ = new StringBuilder();
                StringBuilder HI_1_ABN = new StringBuilder();
                StringBuilder HI_1_APR = new StringBuilder();
                StringBuilder HI_1_ABF = new StringBuilder();
                StringBuilder HI_1_BBR = new StringBuilder();
                StringBuilder HI_1_BBQ = new StringBuilder();
                StringBuilder HI_1_BI = new StringBuilder();
                StringBuilder HI_1_BH = new StringBuilder();
                StringBuilder HI_1_BE = new StringBuilder();
                StringBuilder HI_1_BG = new StringBuilder();
                StringBuilder NM1_6 = new StringBuilder();
                StringBuilder PRV_AT = new StringBuilder();
                StringBuilder NM1_7 = new StringBuilder();
                StringBuilder N3_4 = new StringBuilder();
                StringBuilder N4_4 = new StringBuilder();
                StringBuilder LX1 = new StringBuilder();
                StringBuilder SV2_1 = new StringBuilder();
                StringBuilder SE = new StringBuilder();
                StringBuilder GE = new StringBuilder();
                StringBuilder IEA = new StringBuilder();


                int TagCount = 0;
/*
                ISA.append("ISA*00*          *00*          *ZZ*AV09311993     *01*030240928      *" + ClaimCreateDate + "*" + ClaimCreateTime + "*^*00501*" + InterControlNo + "*1*T*:~\n");
                //GS.append("GS*HC*p920700*ECGCLAIMS*20210625*155544*000000001*X*005010X223A2~\n");
                GS.append("GS*HC*AV09311993*030240928*" + CreationDate + "*" + ClaimCreateTime + "*" + InterControlNo + "*X*005010X223A2~\n");
                ST.append("ST*837*" + ST02 + "*005010X223A2~\n");
                BHT.append("BHT*0019*00*1*" + CreationDate + "*" + ClaimCreateTime + "*CH~\n");
                NM1.append("NM1*41*2*" + AttendingProviderLastName + " " + AttendingProviderFirstName + "*****46*009207651653~\n");
                PER.append("PER*IC*" + BillingProvidersLastName + " " + BillingProvidersFirstName + "*TE*2812800911*FX*2812800041~\n");
                NM1_2.append("NM1*40*2*MCKHBOC*****46*0001~\n");
                HL_1.append("HL*1**20*1~\n");
                PRV_BI.append("PRV*BI*PXC*" + TaxanomySpecialty + "~\n");
                NM1_3.append("NM1*85*2*" + ClientName + ",*****XX*" + ClientNPI + "~\n");
                N3_1.append("N3*" + ClientAddress + "~\n");
                N4_1.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");
                REF_1.append("REF*EI*" + ClientTaxID + "~\n");
                HL_2.append("HL*2*1*22*0~\n");
                SBR.append("SBR*P*18*" + GrpNumber + "******" + ClaimType + "~\n");
                NM1_4.append("NM1*IL*1*" + PatientLastName + "*" + PatientFirstName + "*" + "" + "***MI*" + MemId + "~\n");
                N3_2.append("N3*" + Address + "~\n");
                N4_2.append("N4*" + City + "*" + State + "*" + ZipCode + "~\n");
                DMG.append("DMG*D8*" + DOB + "*" + Gender + "~\n");
                NM1_5.append("NM1*PR*2*" + (PriInsuranceName.contains("\n") ? PriInsuranceName.replaceAll("\n", "") : PriInsuranceName) + "*****PI*" + PriInsuranceNameId + "~\n");
                N3_3.append("N3*" + "17154 butte creek road" + "~\n");//Insurance Street Address
                N4_3.append("N4*" + "HOUSTON" + "*" + "TX" + "*" + "77090" + "~\n");//Insurance City, Insurance State, Insurance ZipCode

*/

                TagCount = 20;
//out.println("TagCount--1--"+TagCount);
                int foundExtCauseInj = 0;
                int foundReasVisit = 0;
                int foundOthDiag = 0;
                int foundOthProc = 0;
                int foundOccSpan = 0;
                int foundOcc = 0;
                int foundValCode = 0;
                int foundConCode = 0;
                Query = "Select Count(*) from " + Database + ".ClaimInfoCodeExtCauseInj where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    foundExtCauseInj = rset.getInt(1);
                }
                rset.close();
                stmt.close();
                if (foundExtCauseInj > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeExtCauseInj where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_ABN.append("HI*ABN:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--2--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeReasVisit where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    foundReasVisit = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundReasVisit > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeReasVisit where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_APR.append("HI*APR:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--3--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeOthDiag where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOthDiag = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOthDiag > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(PQA,'') from " + Database + ".ClaimInfoCodeOthDiag where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_ABF.append("HI*ABF:" + Code + ":::::::" + rset.getString(2) + "~\n");
                        TagCount++;
                        //out.println("TagCount--4--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                if (PrincipalProcedureDateInfoCodes.equals("")) {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes;
                } else if (PrincipalProcedureDateInfoCodes.contains("/")) {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes.replaceAll("/", "");
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes.substring(4, 8) + PrincipalProcedureDateInfoCodes.substring(0, 2) + PrincipalProcedureDateInfoCodes.substring(2, 4);
                } else {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes;
                }
                if (!PrincipalProcedureInfoCodes.equals("")) {
                    HI_1_BBR.append("HI*BBR:" + PrincipalProcedureInfoCodes + "*" + PrincipalProcedureDateInfoCodes + "~\n");
                    TagCount++;
                    //out.println("TagCount--5--" + TagCount);
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeOthProcedure where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOthProc = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOthProc > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Date,'') from " + Database + ".ClaimInfoCodeOthProcedure where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Date = "";
                        if (rset.getString(2).equals("")) {
                            Date = rset.getString(2);
                        } else if (rset.getString(2).contains("/")) {
                            Date = rset.getString(2).replaceAll("/", "");
                            Date = Date.substring(4, 8) + Date.substring(0, 2) + Date.substring(2, 4);
                        } else {
                            Date = rset.getString(2);
                        }
                        HI_1_BBQ.append("HI*BBQ:" + Code + ":" + Date + "~\n");
                        TagCount++;
                        //out.println("TagCount--6--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

//                Query = "Select COUNT(*) from "+Database+".ClaimInfoCodeOccSpan where ClaimInfoMasterId = "+ClaimInfoMasterId + " " +
//                        " and ClaimNumber = '"+ClaimNumber+"'";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while(rset.next()) {
//                    foundOccSpan = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//
//                if(foundOccSpan > 0 ){
//                    Query = "Select IFNULL(Code,''), IFNULL(FromDate,''), IFNULL(ToDate,'') from " + Database + ".ClaimInfoCodeOccSpan where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
//                            " and ClaimNumber = '" + ClaimNumber + "'";
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    while (rset.next()) {
//                        String Code = "";
//                        if(rset.getString(1).contains(".")){
//                            Code = rset.getString(1).replace(".","");
//                        }else{
//                            Code = rset.getString(1);
//                        }
//                        String FromDate = "";String ToDate = "";
//                        if(rset.getString(2).equals("")){
//                            FromDate = rset.getString(2);
//                        }else if(rset.getString(2).contains("/")){
//                            FromDate = rset.getString(2).replaceAll("/","");
//                            FromDate = FromDate.substring(4,8)+FromDate.substring(0,2)+FromDate.substring(2,4);
//                        }else {
//                            FromDate = rset.getString(2);
//                        }
//                        if(rset.getString(3).equals("")){
//                            ToDate = rset.getString(3);
//                        }else if(rset.getString(3).contains("/")){
//                            ToDate = rset.getString(3).replaceAll("/","");
//                            ToDate = ToDate.substring(4,8)+ToDate.substring(0,2)+ToDate.substring(2,4);
//                        }else {
//                            ToDate = rset.getString(3);
//                        }
//                        HI_1_BI.append("HI*BI:" + Code+ "*" + FromDate + "-"+ ToDate+"~\n");
//                    }
//                    rset.close();
//                    stmt.close();
//                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOcc = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOcc > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Date,'') from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Date = "";
                        if (rset.getString(2).equals("")) {
                            Date = rset.getString(2);
                        } else if (rset.getString(2).contains("/")) {
                            Date = rset.getString(2).replaceAll("/", "");
                            Date = Date.substring(4, 8) + Date.substring(0, 2) + Date.substring(2, 4);
                        } else {
                            Date = rset.getString(2);
                        }
                        HI_1_BH.append("HI*BH:" + Code + "*" + Date + "~\n");
                        TagCount++;
                        //out.println("TagCount--7--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeValueCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundValCode = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundValCode > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Amount,'') from " + Database + ".ClaimInfoCodeValueCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Amount = "";
                        if (rset.getString(2).contains(".")) {
                            Amount = rset.getString(2).replace(".", "");
                        } else {
                            Amount = rset.getString(2);
                        }
//                        HI_1_BE.append("HI*BE:" + rset.getString(1) + ":" + (rset.getString(2).equals("") ? rset.getString(2) : rset.getString(2).replaceAll(".","")) + "^\n");
                        HI_1_BE.append("HI*BE:" + Code + "*" + Amount + "~\n");
                        TagCount++;
                        //out.println("TagCount--8--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundConCode = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundConCode > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_BG.append("HI*BG:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--9--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }


                //out.println("TagCount---10--"+TagCount);

                int iCount = 1;
                String TotalChrgeAmt = "";
                Query = "Select IFNULL(a.RevCode,''), IFNULL(a.HCPCS,''), IFNULL(a.Mod1,''), IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), " +
                        "IFNULL(a.Mod4,''), CASE WHEN a.DescriptionFrom = 0 THEN b.ShortDescription WHEN a.DescriptionFrom = 1 THEN c.Description ELSE c.Description END, " +
                        "IFNULL(a.Amount,''), IFNULL(a.Units,'') from " + Database + ".ClaimChargesInfo a " +
                        " LEFT JOIN oe." + ChargeMasterTableName + " b on a.HCPCS = b.CPTCode " +
                        " LEFT JOIN oe.RevenueCode c on a.RevCode = c.Codes " +
                        "where a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and a.ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    String HCPCS = "";
                    String Units = "";
                    if (rset.getString(2).contains(".")) {
                        HCPCS = rset.getString(2).replace(".", "");
                    } else {
                        HCPCS = rset.getString(2);
                    }
                    String Amount = "";
                    if (rset.getString(8).contains(".")) {
                        //Amount = rset.getString(8).replace(".","");
                        Amount = rset.getString(8).substring(0, rset.getString(8).indexOf("."));
                    } else {
                        Amount = rset.getString(8);
                    }
                    Units = rset.getString(9);
                    if (Units.contains(".")) {
                        //Units = Units.replace(".","");
                        Units = Units.substring(0, Units.indexOf("."));
                    }
                    SV2_1.append("LX*" + iCount + "~\n" + "SV2*" + rset.getString(1) + "*HC:" + HCPCS + ":" + rset.getString(3) + ":" + rset.getString(4) + ":" + rset.getString(5) + ":" + rset.getString(6) + ":" + rset.getString(7) + "*" + Amount + "*UN*" + Units + "~\n");
                    TotalChargeAmount += Double.parseDouble(Amount);
                    TagCount = TagCount + 2;
                    //out.println("TagCount---11--"+TagCount);
                    iCount++;
                }
                rset.close();
                stmt.close();
                TotalChrgeAmt = String.valueOf(TotalChargeAmount);

                if (TotalChrgeAmt.contains(".")) {
                    TotalChrgeAmt = (TotalChrgeAmt.substring(0, TotalChrgeAmt.indexOf(".")));
                }
                System.out.println(TotalChrgeAmt);

                CLM.append("CLM*915243155*" + TotalChrgeAmt + "***22:A:1*" + ProvAccAssigAddInfo + "*" + AssofBenifitAddInfo + "*" + ReleaseInfoAddInfo + "*" + ReleaseInfoAddInfo + "~\n");
                //DTP_1.append("DTP*096*TM*" + DischargeHourAddInfo + "00~\n");
                DTP_2.append("DTP*434*RD8*" + StatmentCoverFromDateAddInfo + "-" + StatmentCoverToDateAddInfo + "~\n");
                //DTP_3.append("DTP*435*DT*" + AdmissionDateAddInfo + "" + AdmissionHourAddInfo + "00~\n");
                CL1.append("CL1*1*9*" + AdmissionTypeAddInfo + "~\n");
                REF_2.append("REF*EA*" + PatientMRN + "~\n");
                HI_1_ABK.append("HI*ABK:" + PrincipalDiagInfoCodes + ":::::::" + POAInfoCodes + "~\n");
                //HI_1_ABJ.append("HI*ABJ:" + AdmittingDiagInfoCodes + "~\n");
                NM1_6.append("NM1*71*1*" + AttendingProviderLastName + "*" + AttendingProviderFirstName + "****XX*" + AttendingProviderNPI + "~\n");
                PRV_AT.append("PRV*AT*PXC*TaxonomySpeciality_Provider~\n");
                NM1_7.append("NM1*77*2*" + ClientName + "*****XX*" + ClientNPI + "~\n");
                N3_4.append("N3*" + ClientAddress + "~\n");
                N4_4.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");

                TagCount = TagCount + 11;
                //out.println("TagCount---12--"+TagCount);
                SE.append("SE*" + TagCount + "*" + ST02 + "~\n");
                GE.append("GE*1*" + InterControlNo + "~\n");
                IEA.append("IEA*1*" + InterControlNo + "~\n");


                String Organization = null;
                String Organization_Tel = null;
                String Organization_Email = null;

                PreparedStatement ps = conn.prepareStatement("SELECT Organization , Tel , Email FROM oe.CompanyCredentials where Id = 1");
                rset = ps.executeQuery();
                if (rset.next()) {
                    Organization = rset.getString(1);
                    Organization_Tel = rset.getString(2);
                    Organization_Email = rset.getString(3);
                }
                rset.close();
                ps.close();

                Context c = new Context('~', '*', ':');
                HashMap<String, Integer> ISATAG = new HashMap<String, Integer>();
                int ClaimTypes = 1; //1 prof , 2 inst
                int HLCounter = 1;
                String ClaimTypeIdentifier = "005010X223A2";// 005010X222A1 Prof , 005010X223A2 Inst

                ISATAG.put("0000-ISA01-AuthorizationInfoQualifier-00", 2);
                ISATAG.put("0000-ISA02-AuthorizationInformation-", 10);
                ISATAG.put("0000-ISA03-SecurityInformationQualifier-00", 2);
                ISATAG.put("0000-ISA04-SecurityInformation-          ", 10);
                ISATAG.put("0000-ISA05-Interchange ID Qualifier-ZZ", 2);
                ISATAG.put("0000-ISA06-Interchange Sender ID (*)-BS01834        ", 15);
                ISATAG.put("0000-ISA07-Interchange ID Qualifier-ZZ", 2);
                ISATAG.put("0000-ISA08-Interchange Receiver ID-33477          ", 15);
                ISATAG.put("0000-ISA09-InterchangeDate-140205", 6);
                ISATAG.put("0000-ISA10-InterchangeTime-1452", 4);
                ISATAG.put("0000-ISA11-RepetitionSeparator-^", 1);
                ISATAG.put("0000-ISA12-InterchangeControlVersionNumber-00501", 5);
                ISATAG.put("0000-ISA13-InterchangeControlNumber-100000467", 9);
                ISATAG.put("0000-ISA14-AcknowledgmentRequested-0", 1);
                ISATAG.put("0000-ISA15-Usage Indicator-T", 1);
                ISATAG.put("0000-ISA16-ComponentElementSeparator-:~", 1);


                HashMap<Integer, String> ISAValue = new HashMap<Integer, String>();

                ISAValue.put(0, "ISA");
                ISAValue.put(1, "00");
                ISAValue.put(2, " ");
                ISAValue.put(3, "00");
                ISAValue.put(4, " ");
                ISAValue.put(5, "ZZ");
                ISAValue.put(6, "BS01834");
                ISAValue.put(7, "ZZ");
                ISAValue.put(8, "33477");
                ISAValue.put(9, "140205");
                ISAValue.put(10, "1452");
                ISAValue.put(11, "^");
                ISAValue.put(12, "00501");
                ISAValue.put(13, "000987654");
                ISAValue.put(14, "0");
                ISAValue.put(15, "T");
                ISAValue.put(16, ":");

                X12 x12 = new X12(c);
                Loop loop_isa = x12.addChild("ISA");

                // add segment
                loop_isa.addSegment("ISA*00*          *00*          *ZZ*SENDERID       *ZZ*RECEIVERID    *" + CreationDate + "*" + ClaimCreateTime + "*U*00401*" + InterControlNo + "*0*T*:");

                for (String key : ISATAG.keySet()) {
                    //   System.out.println(key);
                    String temp[] = key.split("-");
                    int elementid = Integer.parseInt(getOnlyDigits(temp[1]));
                    int len = ISATAG.get(key);
                    // System.out.println(elementid);
                    // System.out.println(len);
                    loop_isa.getSegment(0).setElement(elementid, ISAValue.get(elementid), len);
                }

                //Transaction Set Header (ST)
                // Add GS child loop to ISA loop
                Loop loop_gs = loop_isa.addChild("GS");
                // Add GS segment directly as a string
                loop_gs.addSegment("GS*HC*AZ08260*0048*" + ClaimCreateDate + "*" + ClaimCreateTime + "*" + InterControlNo + "*X*" + ClaimTypeIdentifier);

                Loop loop_st = loop_gs.addChild("ST");
                loop_st.addSegment("ST*837*" + InterControlNo + "*" + ClaimTypeIdentifier);
                loop_st.addSegment("BHT*0019*00*" + ClaimCreateDate + ClaimCreateTime + "*" + ClaimCreateDate + "*" + ClaimCreateTime + "*CH~");
                //loop_st.addSegment("TRN*1*0000000000*1999999999");
                //loop_st.addSegment("DTM*111*20090915");


                //1000A SUBMITTER NAME

                Loop loop_1000A = loop_st.addChild("1000A");
                loop_1000A.addSegment("NM1*41*2*" + Organization + "*****46*12345");
                loop_1000A.addSegment("PER*IC*" + Organization + "*TE*" + Organization_Tel + "*EM*" + Organization_Email);


                String RecieverInsurance = "MEDICARE";
                String RecieverInsurance_IdentificationCode = "00120";

                //1000B RECEIVER NAME
                Loop loop_1000B = loop_st.addChild("1000B");
                loop_1000B.addSegment("NM1*40*2*" + RecieverInsurance + "*****46*" + RecieverInsurance_IdentificationCode);
                loop_1000B.addSegment("N1*PE*FI*888888888*P.O.BOX 456*SHINE CITY*GREEN STATE*ZIP*EARTH");


                //2000A BILLING PROVIDER
                Loop loop_2000 = loop_st.addChild("2000");
                loop_2000.addSegment("HL*" + HLCounter + "**20*1"); //HL BILLING PROVIDER HIERARCHICAL LEVEL
                loop_2000.addSegment("PRV*BI*PXC*" + BillingProvider_Taxonomy); //PRV BILLING PROVIDER SPECIALTY

                String BillingProvider_Address = "225 MAIN STREET BARKLEY BUILDING";
                String BillingProvider_State = "PA";
                String BillingProvider_ZipCode = "17111";
                String BillingProvider_City = "CENTERVILLE";
                String BillingProvider_TaxID = "567891234";
                String BillingProvider_Tel = "5678912342";

                //2010AA BILLING PROVIDER NAME
                Loop loop_2010_1 = loop_2000.addChild("2010");
                loop_2010_1.addSegment("NM1*85**" + BillingProvidersLastName + "*" + BillingProvidersFirstName + "****XX*9876540809");//NM1 BILLING PROVIDER NAME INCLUDING NATIONAL PROVIDER ID
                loop_2010_1.addSegment("N3*" + BillingProvider_Address); //N3 BILLING PROVIDER ADDRESS
                loop_2010_1.addSegment("N4*" + BillingProvider_City + "*" + BillingProvider_State + "*" + BillingProvider_ZipCode);//N4 BILLING PROVIDER LOCATION
                loop_2010_1.addSegment("REF*EI*" + ClientTaxID); //REF BILLING PROVIDER TAX IDENTIFICATION NUMBER
                loop_2010_1.addSegment("PER*IC*" + BillingProvidersLastName + "*TE*" + BillingProvider_Tel);//PER BILLING PROVIDER CONTACT INFORMATION


                String PayerResponsibilityCode = "P";
                String InsuranceTypeCode = "";//required when sbr02 is S and FC is MA or MB
                String PatientRelationshipCode = null;


                if (PatientRelationtoPrimary.equals("Self")) {
                    PatientRelationshipCode = "18";
                } else {
                    PatientRelationshipCode = "";
                }

                HLCounter++;
                //2000B SUBSCRIBER HL LOOP
                Loop loop_2000B = loop_2000.addChild("2000");
                loop_2000B.addSegment("HL*" + HLCounter + "*1*22*0");//HL SUBSCRIBER HIERARCHICAL LEVEL
                loop_2000B.addSegment("SBR*" + PayerResponsibilityCode + "*" + PatientRelationshipCode + "*" + GrpNumber + "******" + PriFillingIndicator);//SBR SUBSCRIBER INFORMATION

                //2010BA SUBSCRIBER NAME LOOP
                Loop loop_2010BA = loop_2000.addChild("2000");
                loop_2010BA.addSegment("NM1*IL*1*" + PatientLastName + "*" + PatientFirstName + "*T***MI*" + MemId);//NM1 SUBSCRIBER NAME
                loop_2010BA.addSegment("N3*" + Address);//N3 SUBSCRIBER ADDRESS
                loop_2010BA.addSegment("N4*" + City + "*" + State + "*" + ZipCode);//N4 SUBSCRIBER LOCATION
                loop_2010BA.addSegment("DMG*D8*" + DOB + "*" + Gender); //DMG SUBSCRIBER DEMOGRAPHIC INFORMATION

                //2010BB PAYER NAME LOOP
                Loop loop_2010BB = loop_2000.addChild("2000");
                loop_2010BB.addSegment("NM1*PR*2*" + PriInsuranceName + "*****PI*" + PriInsuranceNameId);//NM1 PAYER NAME
                loop_2010BB.addSegment("N3*" + Payer_Address);//N3 PAYER ADDRESS ** ADDED
                loop_2010BB.addSegment("N4*" + Payer_City + "*" + Payer_State + "*" + Payer_Zip);//N4 PAYER ADDRESS ** ADDED
//                loop_2010BB.addSegment("REF*G2*330127");//REF BILLING PROVIDER SECONDARY IDENTIFICATION

                //	2300 CLAIM INFORMATION
                PatientControlNumber = PatientMRN + "FAM" + ClientId + timesSubmitted + "E" + ClaimNumber.replace("-", "");
                String Related_Causes_Code = null;

                if (AutoAccidentAddInfo.equals("1")) {
                    Related_Causes_Code = "AA";
                } else if (EmploymentStatusAddInfo.equals("1")) {
                    Related_Causes_Code = "EM";
                } else if (OtherAccidentAddInfo.equals("1")) {
                    Related_Causes_Code = "OA";
                }


                Loop loop_2300 = loop_st.addChild("2300");
                loop_2300.addSegment("CLM*" + PatientControlNumber + "*" + TotalChrgeAmt + "***" + TypeBillText.substring(0, 1) + TypeBillText.substring(1, 2) + ":A:" + TypeBillText.substring(2, 3) + "*Y*A*Y*Y***"); //CLM CLAIM LEVEL INFORMATION

//                loop_2300.addSegment("CLM*756048Q*89.93**14:A:1*A*Y*Y"); //CLM CLAIM LEVEL INFORMATION
//                loop_2300.addSegment("DTP*434*RD8*19960911"); // DTP STATEMENT DATES
//                loop_2300.addSegment("CL1*3**01"); // CL1 INSTITUTIONAL CLAIM CODE
//                loop_2300.addSegment("HI*BK:3669"); //HI PRINCIPAL DIAGNOSIS CODES
//                loop_2300.addSegment("HI*BF:4019*BF:79431"); //HI OTHER DIAGNOSIS INFORMATION
//                loop_2300.addSegment("HI*BH:A1:D8:19261111*BH:A2:D8:19911101*BH:B1:D8:19261111*BH:B2:D8:19870101"); //HI OCCURRENCE INFORMATION
//                loop_2300.addSegment("HI*BE:A2:::15.31"); //HI VALUE INFORMATION
//                loop_2300.addSegment("HI*BG:09"); //HI CONDITION INFORMATION

                //2310A ATTENDING PROVIDER NAME

                Loop loop_2310A = loop_2300.addChild("2310");
                loop_2310A.addSegment("NM1*71*1*" + AttendingProviderLastName + "*" + AttendingProviderFirstName + "*J");//NM1 ATTENDING PROVIDER
                loop_2310A.addSegment("PRV*AT*PXC*" + AttendingProvider_Taxonomy);//NM1 ATTENDING PROVIDER
//                loop_2310A.addSegment("REF*1G*B99937");//REF ATTENDING PROVIDER SECONDARY IDENTIFICATION


                //2400 SERVICE LINE
                Loop loop_2400 = loop_st.addChild("2400");
                loop_2400.addSegment("LX*1");//LX SERVICE LINE COUNTER
                loop_2400.addSegment("SV2*0305*HC:85025*13.39*UN*1"); //SV2 INSTITUTIONAL SERVICE
                loop_2400.addSegment("DTP*472*D8*19960911");//DTP DATE - SERVICE DATES


                Loop loop_se = loop_gs.addChild("SE");
                loop_se.addSegment("SE*XX*000987654");

                Loop loop_ge = loop_isa.addChild("GE");
                loop_ge.addSegment("GE*1*000987654");

                Loop loop_iea = x12.addChild("IEA");
                loop_iea.addSegment("IEA*1*000987654");

                // Since the SE loop has the incorrect segment count let us fix that.
                Integer count = loop_st.size();
                count += 1; // In the loop hierarchy SE is not a child loop of ST. So
                // when we get the rows in ST loop it does not have the count of SE.
                // so add 1.

                // We can set the count directly, like
                // loop_se.getSegment(0).setElement(1, count.toString());
                // this is just to show how to use the findLoop()

                //TRAILER
                List<Loop> trailer = x12.findLoop("SE");
                trailer.get(0).getSegment(0).setElement(1, count.toString(), 2);

                //another way
                List<Segment> se = x12.findSegment("SE");
                se.get(0).setElement(1, count.toString(), 2);

                //another way
                loop_se.getSegment(0).setElement(1, count.toString(), 2);


                //System.out.println(loop_st.size());
                System.out.println(x12.toString());
                //System.out.println(x12.toXML());

                //out.println("TagCount--ALL--"+TagCount);

                File file = new File("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (file.exists()) {
                    file.delete();
                }

                File myObj = new File("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (myObj.createNewFile()) {

                    FileWriter myWriter = new FileWriter("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                    myWriter.write(ISA.toString());
                    myWriter.write(GS.toString());
                    myWriter.write(ST.toString());
                    myWriter.write(BHT.toString());
                    myWriter.write(NM1.toString());
                    myWriter.write(PER.toString());
                    myWriter.write(NM1_2.toString());
                    myWriter.write(HL_1.toString());
                    myWriter.write(PRV_BI.toString());
                    myWriter.write(NM1_3.toString());
                    myWriter.write(N3_1.toString());
                    myWriter.write(N4_1.toString());
                    myWriter.write(REF_1.toString());
                    myWriter.write(HL_2.toString());
                    myWriter.write(SBR.toString());
                    myWriter.write(NM1_4.toString());
                    myWriter.write(N3_2.toString());
                    myWriter.write(N4_2.toString());
                    myWriter.write(DMG.toString());
                    myWriter.write(NM1_5.toString());
                    myWriter.write(N3_3.toString());
                    myWriter.write(N4_3.toString());
                    myWriter.write(CLM.toString());
                    myWriter.write(DTP_1.toString());
                    myWriter.write(DTP_2.toString());
                    myWriter.write(DTP_3.toString());
                    myWriter.write(CL1.toString());
                    myWriter.write(REF_2.toString());
                    myWriter.write(HI_1_ABK.toString());
                    myWriter.write(HI_1_ABJ.toString());
                    myWriter.write(HI_1_ABN.toString());
                    myWriter.write(HI_1_APR.toString());
                    myWriter.write(HI_1_ABF.toString());
                    myWriter.write(HI_1_BBR.toString());
                    myWriter.write(HI_1_BBQ.toString());
                    //myWriter.write(HI_1_BI.toString());
                    myWriter.write(HI_1_BH.toString());
                    myWriter.write(HI_1_BE.toString());
                    myWriter.write(HI_1_BG.toString());
                    myWriter.write(NM1_6.toString());
                    myWriter.write(PRV_AT.toString());
                    myWriter.write(NM1_7.toString());
                    myWriter.write(N3_4.toString());
                    myWriter.write(N4_4.toString());
                    myWriter.write(SV2_1.toString());
                    myWriter.write(SE.toString());
                    myWriter.write(GE.toString());
                    myWriter.write(IEA.toString());


                    myWriter.close();
                }

            }
            Instant end = Instant.now();
            Duration ExecutionTime = Duration.between(start, end);
//            out.println(ErrorMsgs + "~" + ExecutionTime);
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            out.println("An error occurred.");
            out.println(e.getMessage());
            out.println(e.getMessage() + Query);
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    private String CheckStringVariable(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName).length() < 1) {
                VariableName = null;
            } else {
                VariableName = request.getParameter(VariableName).trim();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "-- VariableName -- " + VariableName);
        }
        return VariableName;
    }

    private String CheckIntegerVariable(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName).length() < 1) {
                VariableName = "0";
            } else {
                VariableName = request.getParameter(String.valueOf(VariableName)).trim();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    private String CheckCheckBoxValue(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName) == null) {
                VariableName = "0";
            } else {
                VariableName = request.getParameter(VariableName).trim();
                if (VariableName.equals("on")) {
                    VariableName = "1";
                } else {
                    VariableName = "0";
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    private String CheckBox(String VariableName) {
        String ChkBox = "";
        try {
            if (VariableName.equals("1")) {
                ChkBox = "<input type=\"checkbox\" id=\"" + VariableName + "\" name=\"" + VariableName + "\" class=\"filled-in\" checked />";
            } else {
                ChkBox = "<input type=\"checkbox\" id=\"" + VariableName + "\" name=\"" + VariableName + "\" class=\"filled-in\" />";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return ChkBox;
    }

    public void validateCPT(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) throws SQLException {
        boolean isValid = false;
        String Price = null;
        ResultSet rset = null;
        String code = request.getParameter("code");
        PreparedStatement ps = conn.prepareStatement("SELECT IFNULL(Price,'0.0') FROM ClaimMasterDB.CPTMaster WHERE CPTCode=?");
        ps.setString(1, code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getString(1) != null) {
                Price = rset.getString(1);
                isValid = true;
            }
        }
        rset.close();
        ps.close();

        if (isValid) {
            out.println("1~" + Price);
        } else {
            out.println("0");
        }
    }

    public void validatePOS(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        String code = request.getParameter("code");
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.POS WHERE Codes=?");
        ps.setString(1, code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();

        if (isValid) {
            out.println("1");
        } else {
            out.println("0");
        }
    }

    public void validateTOS(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        String code = request.getParameter("code");
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM oe.TOSCodes WHERE Code=?");
        ps.setString(1, code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();

        if (isValid) {
            out.println("1");
        } else {
            out.println("0");
        }
    }

    public void validateMod(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        String code = request.getParameter("code");
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM oe.ModifierCodes WHERE Code=?");
        ps.setString(1, code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();

        if (isValid) {
            out.println("1");
        } else {
            out.println("0");
        }
    }

    public void validateICDs(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        String code = request.getParameter("code");
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ICDMaster WHERE ICD=?");
        ps.setString(1, code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();

        if (isValid) {
            out.println("1");
        } else {
            out.println("0");
        }
    }

    private boolean containsValidRelativeAdminCode(Connection conn, String Code, ArrayList<String> charge_procedureCodes, String ICD) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;

        for (String chargeProcedure : charge_procedureCodes) {
            PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.MCRVaccineRuleCodes" +
                    " WHERE VaccineCodes=? AND ICD10Code=? AND Administration=?");
            ps.setString(1, chargeProcedure);
            ps.setString(2, ICD);
            ps.setString(3, Code);
            rset = ps.executeQuery();
            if (rset.next()) {
                if (rset.getInt(1) > 0) {
                    isValid = true;
                    break;
                }
            }
            rset.close();
            ps.close();
        }

        return isValid;
    }

    private ArrayList getRespectiveVaccineCode(Connection conn, String Code, String ICD) throws SQLException {
        ResultSet rset = null;
        ArrayList<String> VaccineCodes = new ArrayList<>();

        PreparedStatement ps = conn.prepareStatement("SELECT VaccineCodes FROM ClaimMasterDB.MCRVaccineRuleCodes" +
                " WHERE  ICD10Code=? AND Administration=?");
        ps.setString(1, ICD);
        ps.setString(2, Code);
        rset = ps.executeQuery();
        while (rset.next()) {
            VaccineCodes.add(rset.getString(1));
        }
        rset.close();
        ps.close();


        return VaccineCodes;
    }

    private boolean isInValidZipCode(Connection conn, String state, String zipCode) throws SQLException {
        boolean isInValid = true;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ZipCodeFacility WHERE Zip=? AND State=?");
        ps.setString(1, zipCode);
        ps.setString(2, state);
//        System.out.println("ZipCodeFacility -> " + ps.toString());
        rset = ps.executeQuery();
        if (rset.next()) {
//            System.out.println("ZipCodeFacility -> " + rset.getInt(1));
            if (rset.getInt(1) > 0)
                isInValid = false;
        }
        rset.close();
        ps.close();
//        System.out.println("ZipCodeFacility isInValid -> " + isInValid);

        if (isInValid) {
            ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ZipCodeLibrary WHERE ZIPCode=? AND State=?");
            ps.setString(1, zipCode);
            ps.setString(2, state);
//            System.out.println("ZipCodeLibrary -> " + ps.toString());
            rset = ps.executeQuery();
            if (rset.next()) {
//                System.out.println("ZipCodeLibrary -> " + rset.getInt(1));
                if (rset.getInt(1) > 0)
                    isInValid = false;
            }
            rset.close();
            ps.close();
        }

        return isInValid;
    }

    private boolean isInValidAddress(String address) {
        return address.startsWith("PO BOX") ||
                address.startsWith("POST") ||
                address.startsWith("BOX");
    }

    public boolean isInValidDate(String date, String ClaimCreateDate) {
        return date.equals("00000000") || date.equals("19000101") || Integer.parseInt(date) > Integer.parseInt(ClaimCreateDate);
    }

    private boolean isInValidTaxonomy(Connection conn, String taxonomy) throws SQLException {
        boolean isInValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.WPCTaxonomy WHERE Codes=?");
        ps.setString(1, taxonomy);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 0) {
                isInValid = true;
            }
        }
        rset.close();
        ps.close();
        return isInValid;
    }

    private boolean isValidAnesthesiaCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.AnesthesiaCodes WHERE Code=?");
        ps.setString(1, Code);
        //System.out.println("Anesthesia Codes ->>>> QUERY    " + ps.toString());
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidCLIACodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.CLIA WHERE HCPCS=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidConsultantProceduresCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ConsultantProcedures WHERE CPTCode=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidPQRSCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.PQRSCodes WHERE ProcedureCodes=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidDMEProceduresCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.DME_HCPCS WHERE HCPCS=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValid_E_N_M_Surgery_ProceduresCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.EnMSurgeryCodes WHERE SurgeryCodes=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValid_E_N_M_ProceduresCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.EnMProcedures WHERE CPT=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValid_I_Codes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ICodes WHERE HCPCS=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidVaccineCode(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.VaccineCodesComponents WHERE VaccineCode=?");
        ps.setString(1, Code);
//        System.out.println("QUERY -> " + ps.toString());
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValid_OFFICE_E_N_M(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.`CPTMaster` where (CPTCode BETWEEN '99201' AND '99357' OR  CPTCode BETWEEN '92002' AND '92014')  AND CPTCode=?");
        ps.setString(1, Code);

//        "Query ->> " + ps.toString());
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 2) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean ValidMammographyCPT(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.`CPTMaster` where (CPTCode BETWEEN '77053' AND '77067')  AND CPTCode=?");
        ps.setString(1, Code);

//        "Query ->> " + ps.toString());
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValid_EPSDT_ProceduresCodes(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ESPDTProcedures WHERE ESPDT=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isBMI_ICD(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
//        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM oe.DiagnosisCodes WHERE Code LIKE 'Z68%' AND Code=?");
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.ICDMaster WHERE ICD LIKE 'Z68%' AND ICD=?");
        ps.setString(1, Code);
        //System.out.println("QUERY ->>  " + ps.toString());

        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) > 0) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private boolean isValidAnesthesiaModifier(Connection conn, String Modifier) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.AnesthesiaModifiers WHERE Modifier=?");
        ps.setString(1, Modifier);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }

    private String formatDate(HttpServletRequest request, String VariableName) {
        try {
            VariableName = request.getParameter(VariableName) == null ? null : request.getParameter(VariableName).replaceAll("-", "");

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
        }
        return VariableName;
    }

    public void Institutional_EDIFileOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        Instant start = Instant.now();
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        String EDI_GENERATOR = "";
        String InterControlNo = "";
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String ClaimNumber = request.getParameter("ClaimNumber").trim();
//int ClaimType = Integer.parseInt(request.getParameter("ClaimType").trim());
        int VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        String DateTime = "";
        String DateNow = "";
        String PatientFirstName = "";
        String PatientLastName = "";
        String DOB = "";
        String Gender = "";
        String PriInsuredName = "";
        String TaxanomySpecialty = "";
        int ClaimInfoMasterId = 0;
        int SelfPayChk = 0;
        String ClientAddress = "";
        String ClientCity = "";
        String ClientTaxID = "";
        String ClientState = "";
        String ClientZipCode = "";
        String ClientPhone = "";
        String ClientNPI = "";

//Claim Basic Info Variable
        String RefNumber = "";
        String TypeBillText = "";
        String PatientName = "";
        String PatientMRN = "";
        String AcctNo = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String StateCode = "";
        String ZipCode = "";
        String DOS = "";
        String UploadDate = "";
        String AttendingProviderLastName = "";
        String AttendingProviderFirstName = "";
        String AttendingProviderNPI = "";
        String BillingProvidersLastName = "";
        String BillingProvidersFirstName = "";
        String BillingProvidersNPI = "";
        String OperatingProviderLastName = "";
        String OperatingProviderFirstName = "";
        String OperatingProviderNPI = "";
        String ClientName = "";
        String PriInsuranceName = "";
        String PriInsuranceNameId = "";
        String MemId = "";
        String PolicyType = "";
        String GrpNumber = "";
        String SecondaryInsurance = "";
        String SecondaryInsuranceId = "";
        String SecondaryInsuranceMemId = "";
        String SecondaryInsuranceGrpNumber = "";
        String CreationDate = "";
        String ClaimType = "";
        String ClaimCreateDate = "";
        String ClaimCreateTime = "";
//
//        //Charges Variables
        double TotalChargeAmount = 0.00;
//        String ChargesTableCount = "0";
//        String ChargesString = "";
//
//Additional Info Variable
        String StatmentCoverFromDateAddInfo = "";
        String StatmentCoverToDateAddInfo = "";
        String AdmissionDateAddInfo = "";
        String AdmissionHourAddInfo = "";
        String AdmissionTypeAddInfo = "";
        String AdmissionSourceAddInfo = "";
        String DischargeHourAddInfo = "";
        String PatientStatusAddInfo = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String PPSAddInfo = "";
        String RemarksAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String ReleaseInfoAddInfo = "";
        String AssofBenifitAddInfo = "";
        String ProvAccAssigAddInfo = "";
//
//Information Codes variable
        String PrincipalDiagInfoCodes = "";
        String POAInfoCodes = "";
        String AdmittingDiagInfoCodes = "";
        String PrincipalProcedureInfoCodes = "";
        String PrincipalProcedureDateInfoCodes = "";
        String ExternalCauseInjuryTableCount = "0";
        String ExternalCauseInjuryString = "";
        String ReasonVisitTableCount = "0";
        String ReasonVisitString = "";
        String OtherDiagnosisString = "";
        String OtherDiagnosisTableCount = "0";
        StringBuffer ErrorMsgs = new StringBuffer();
        final String NPI_REGEX = "[0-9]{10}";

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,''), IFNULL(TaxanomySpecialty,''), IFNULL(TaxID,'') from oe.clients" +
                    " where Id = " + ClientId;
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientAddress = rset.getString(1);
                ClientCity = rset.getString(2);
                ClientState = rset.getString(3);
                ClientZipCode = rset.getString(4);
                ClientPhone = rset.getString(5);
                ClientNPI = rset.getString(6);
                TaxanomySpecialty = rset.getString(7);
                ClientTaxID = rset.getString(8);
            }
            rset.close();
            stmt.close();

            //Validating NPI
//            if (ClientNPI.matches(NPI_REGEX)) {
//                if(isValid(ClientNPI)) ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>Valid</b><span> <i class=\"fa fa-check-circle\" style=\"color: green;font-size: 20px;\"></i></span></p>\n");
//                else ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
//                        "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
//            } else {
//                ErrorMsgs.append("<p style=\"color:black;\"><b>NPI</b> is <b>InValid</b><span data-toggle=\"tooltip\" title=\"Mentioned below might be the causes\n* NPI should contain Numeric Characters \n" +
//                        "* Length of the NPI should be 10 digits\"> <i class=\"fa fa-times-circle-o\" style=\"color: red;font-size: 20px;\"></i></span></p>");
//            }


            Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster " +
                    " where Status = 0 and PatientRegId = " + PatientRegId + " and VisitId = " + VisitId +
                    " and ClaimNumber = '" + ClaimNumber + "'";
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found == 0) {
                out.println("Please Save Claim First and then Create EDI");
                return;
            } else {

                Query = "Select IFNULL(MAX(InterControlNo),'') from " + Database + ".ClaimInfoMaster ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    InterControlNo = rset.getString(1);
                }
                rset.close();
                stmt.close();
//out.println("InterControlNo;---1-----"+InterControlNo);
/*                if (InterControlNo.equals("")) {
                    //out.println("inside 1 icn");
                    InterControlNo = "100000001";
                } else if (InterControlNo.length() > 9) {
                    //out.println("inside 2 icn");
                    InterControlNo = "100000001";
                } else {
                    //out.println("inside 3 icn");
                    InterControlNo = String.valueOf((Integer.parseInt(InterControlNo) + 1));
                }*/
                if (InterControlNo.equals("")) {
                    //out.println("inside 1 icn");
                    InterControlNo = "000000001";
                } else {
                    InterControlNo = String.format("%09d", Integer.parseInt(InterControlNo) + 1);
                }
//out.println("InterControlNo;---2----"+InterControlNo);
                try {
                    PreparedStatement MainReceipt = conn.prepareStatement(
                            "Update " + Database + ".ClaimInfoMaster Set InterControlNo = ? " +
                                    " where PatientRegId = ? and VisitId = ? and ClaimNumber = ? ");
                    MainReceipt.setString(1, (InterControlNo));
                    MainReceipt.setInt(2, PatientRegId);
                    MainReceipt.setInt(3, VisitId);
                    MainReceipt.setString(4, ClaimNumber);
                    //out.println("MainReceipt:--"+MainReceipt.toString());
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                } catch (Exception e) {
                    out.println(e.getMessage());
                    helper.SendEmailWithAttachment("Error in UpdatingtInstitionalEDI ** (AddInfo^^ MES#ICN)", context, e, "AddInfo", "InstitutuionalEDI", conn);
                    Services.DumException("AddInfo", "Instit", request, e);
                }


                Query = "Select ChargeMasterTableName,IFNULL(EDI_GENERATOR,'') from oe.clients where ID = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ChargeMasterTableName = rset.getString(1).trim();
                    EDI_GENERATOR = rset.getString(2).trim();
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(DATE_FORMAT(DOB,'%Y%m%d'),''), DATE_FORMAT(NOW(),'%d%m%y%k%i%s'), IFNULL(Gender,''), DATE_FORMAT(NOW(),'%m%d%y'), " +
                        "IFNULL(SelfPayChk,'0')" +
                        " from " + Database + ".PatientReg where ID = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientFirstName = rset.getString(1);
                    PatientLastName = rset.getString(2);
                    Address = rset.getString(3);
                    City = rset.getString(4);
                    State = rset.getString(5);
                    ZipCode = rset.getString(6);
                    DOB = rset.getString(7);
                    DateTime = rset.getString(8);
                    Gender = rset.getString(9).trim();
                    DateNow = rset.getString(10);
                    SelfPayChk = rset.getInt(11);
                }
                rset.close();
                stmt.close();
                if (!Gender.equals("")) {
                    Gender = Gender.substring(0, 1).toUpperCase();
                } else {
                    Gender = "";
                }

                if (SelfPayChk == 1) {
                    Query = "Select IFNULL(PriInsurerName,'') from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuredName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();

                    if (PriInsuredName.equals("")) {
                        PriInsuredName = PatientLastName + ", " + PatientFirstName;
                    }

                } else {
                    PriInsuredName = PatientLastName + ", " + PatientFirstName;
                }


                if (!State.equals("")) {
                    if (State.length() > 2) {
                        Query = "Select IFNULL(StateCode,'') from " + Database + ".StateList where ltrim(rtrim(Upper(StateName))) = ltrim(rtrim(UPPER('" + State + "')))";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            StateCode = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    } else {
                        StateCode = State.toUpperCase();
                    }

                } else {
                    StateCode = "";
                }

                Query = "Select a.Id,IFNULL(RefNumber,''), IFNULL(a.TypeBillText,''), IFNULL(a.PatientName,''), IFNULL(a.PatientMRN,''), " +
                        " IFNULL(a.AcctNo,''), IFNULL(a.PhNumber,''), " +
                        " IFNULL(a.Email,''), IFNULL(a.Address,''), IFNULL(a.DOS,''), " +
                        " IFNULL(b.DoctorsLastName,''), IFNULL(b.DoctorsFirstName,'') , IFNULL(b.NPI,'') as AttendingNPI, " +
                        " IFNULL(c.DoctorsLastName,''), IFNULL(c.DoctorsFirstName,''), IFNULL(c.NPI,'') as BillingNPI, " +
                        " IFNULL(d.PayerName,'') as PriInsuranceName, IFNULL(a.MEMId,''), IFNULL(a.PolicyType,''), IFNULL(a.GrpNumber,''), " +
                        " IFNULL(e.PayerName,'') as SecondaryInsurance, " +
                        " IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(a.SecondaryInsuranceGrpNumber,''), IFNULL(ClientName,''), " +
                        " IFNULL(f.DoctorsLastName,''), IFNULL(f.DoctorsFirstName,''), IFNULL(f.NPI,'') as OperatingProvider, " +
                        " DATE_FORMAT(a.CreatedDate,'%Y%m%d'), CASE WHEN a.ClaimType = 1 THEN 'CI' WHEN a.ClaimType = 2 THEN 'CP' ELSE 'CL' END, " +
                        " DATE_FORMAT(a.CreatedDate,'%y%m%d'), DATE_FORMAT(a.CreatedDate,'%H%i'), IFNULL(d.PayerID,''), IFNULL(e.PayerID,'') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN " + Database + ".DoctorsList b on a.AttendingProvider = b.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList c on a.BillingProviders = c.Id " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        "  where a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
                //System.out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    RefNumber = rset.getString(2);
                    TypeBillText = rset.getString(3);
                    PatientName = rset.getString(4).replaceAll(" ", "").replaceAll("'", "");
                    PatientMRN = rset.getString(5);
                    AcctNo = rset.getString(6);
                    PhNumber = rset.getString(7);
                    Email = rset.getString(8);
                    //Address = rset.getString(9);
                    DOS = rset.getString(10).replaceAll("/", "");
                    AttendingProviderLastName = rset.getString(11);
                    AttendingProviderFirstName = rset.getString(12);
                    AttendingProviderNPI = rset.getString(13);
                    BillingProvidersLastName = rset.getString(14);
                    BillingProvidersFirstName = rset.getString(15);
                    BillingProvidersNPI = rset.getString(16);
                    PriInsuranceName = rset.getString(17);
                    MemId = rset.getString(18);
                    PolicyType = rset.getString(19);
                    GrpNumber = rset.getString(20);
                    SecondaryInsurance = rset.getString(21);
                    SecondaryInsuranceMemId = rset.getString(22);
                    SecondaryInsuranceGrpNumber = rset.getString(23);
                    ClientName = rset.getString(24);
                    OperatingProviderLastName = rset.getString(25);
                    OperatingProviderFirstName = rset.getString(26);
                    OperatingProviderNPI = rset.getString(27);
                    CreationDate = rset.getString(28);
                    ClaimType = rset.getString(29);
                    ClaimCreateDate = rset.getString(30);
                    ClaimCreateTime = rset.getString(31);
                    PriInsuranceNameId = rset.getString(32);
                    SecondaryInsuranceId = rset.getString(33);

                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(StatmentCoverFromDateAddInfo,''), IFNULL(StatmentCoverToDateAddInfo,''), " +
                        " IFNULL(AdmissionDateAddInfo,''), IFNULL(AdmissionHourAddInfo,''), IFNULL(AdmissionTypeAddInfo,''), " +
                        " IFNULL(AdmissionSourceAddInfo,''), IFNULL(DischargeHourAddInfo,''), IFNULL(PatientStatusAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'')," +
                        " IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(OtherAccidentAddInfo,''), IFNULL(PPSAddInfo,''), " +
                        " IFNULL(RemarksAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), IFNULL(ReleaseInfoAddInfo,''), IFNULL(AssofBenifitAddInfo,''), " +
                        " IFNULL(ProvAccAssigAddInfo,'') " +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    StatmentCoverFromDateAddInfo = rset.getString(1);
                    StatmentCoverToDateAddInfo = rset.getString(2);
                    AdmissionDateAddInfo = rset.getString(3);
                    AdmissionHourAddInfo = rset.getString(4);
                    AdmissionTypeAddInfo = rset.getString(5);
                    AdmissionSourceAddInfo = rset.getString(6);
                    DischargeHourAddInfo = rset.getString(7);
                    PatientStatusAddInfo = rset.getString(8);
                    DelayReasonCodeAddInfo = rset.getString(9);
                    EmploymentStatusAddInfo = rset.getString(10);
                    AutoAccidentAddInfo = rset.getString(11);
                    OtherAccidentAddInfo = rset.getString(12);
                    PPSAddInfo = rset.getString(13);
                    RemarksAddInfo = rset.getString(14);
                    AutoAccident_StateAddInfo = rset.getString(15);
                    ReleaseInfoAddInfo = rset.getString(16);
                    AssofBenifitAddInfo = rset.getString(17);
                    ProvAccAssigAddInfo = rset.getString(18);
                }
                rset.close();
                stmt.close();

                if (!StatmentCoverFromDateAddInfo.equals("")) {
                    StatmentCoverFromDateAddInfo = StatmentCoverFromDateAddInfo.replaceAll("/", "");
                    StatmentCoverFromDateAddInfo = StatmentCoverFromDateAddInfo.substring(4, 8) + StatmentCoverFromDateAddInfo.substring(0, 2) + StatmentCoverFromDateAddInfo.substring(2, 4);
                }
                if (!StatmentCoverToDateAddInfo.equals("")) {
                    StatmentCoverToDateAddInfo = StatmentCoverToDateAddInfo.replaceAll("/", "");
                    StatmentCoverToDateAddInfo = StatmentCoverToDateAddInfo.substring(4, 8) + StatmentCoverToDateAddInfo.substring(0, 2) + StatmentCoverToDateAddInfo.substring(2, 4);
                }
                if (!AdmissionDateAddInfo.equals("")) {
                    AdmissionDateAddInfo = AdmissionDateAddInfo.replaceAll("/", "");
                    AdmissionDateAddInfo = AdmissionDateAddInfo.substring(4, 8) + AdmissionDateAddInfo.substring(0, 2) + AdmissionDateAddInfo.substring(2, 4);
                }

                Query = "Select IFNULL(PrincipalDiagInfoCodes,''), IFNULL(POAInfoCodes,''), IFNULL(AdmittingDiagInfoCodes,''), " +
                        " IFNULL(PrincipalProcedureInfoCodes,''), IFNULL(PrincipalProcedureDateInfoCodes,'') " +
                        "from " + Database + ".ClaimInformationCode where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PrincipalDiagInfoCodes = rset.getString(1);
                    POAInfoCodes = rset.getString(2);
                    AdmittingDiagInfoCodes = rset.getString(3);
                    PrincipalProcedureInfoCodes = rset.getString(4);
                    PrincipalProcedureDateInfoCodes = rset.getString(5);
                }
                rset.close();
                stmt.close();

                if (!PrincipalDiagInfoCodes.equals("")) {
                    if (PrincipalDiagInfoCodes.contains(".")) {
                        PrincipalDiagInfoCodes = PrincipalDiagInfoCodes.replace(".", "");
                    }
                }
                if (!AdmittingDiagInfoCodes.equals("")) {
                    if (AdmittingDiagInfoCodes.contains(".")) {
                        AdmittingDiagInfoCodes = AdmittingDiagInfoCodes.replace(".", "");
                    }
                }
                if (!PrincipalProcedureInfoCodes.equals("")) {
                    if (PrincipalProcedureInfoCodes.contains(".")) {
                        PrincipalProcedureInfoCodes = PrincipalProcedureInfoCodes.replace(".", "");
                    }
                }

                String ST02 = "";

                if (ClaimNumber.length() > 4) {
                    ST02 = ClaimNumber.substring(ClaimNumber.length() - 4);
                } else {
                    ST02 = "0222";
                }

                StringBuilder ISA = new StringBuilder();
                StringBuilder GS = new StringBuilder();
                StringBuilder ST = new StringBuilder();
                StringBuilder BHT = new StringBuilder();
                StringBuilder NM1 = new StringBuilder();
                StringBuilder PER = new StringBuilder();
                StringBuilder NM1_2 = new StringBuilder();
                StringBuilder HL_1 = new StringBuilder();
                StringBuilder PRV_BI = new StringBuilder();
                StringBuilder NM1_3 = new StringBuilder();
                StringBuilder N3_1 = new StringBuilder();
                StringBuilder N4_1 = new StringBuilder();
                StringBuilder REF_1 = new StringBuilder();
                StringBuilder HL_2 = new StringBuilder();
                StringBuilder SBR = new StringBuilder();
                StringBuilder NM1_4 = new StringBuilder();
                StringBuilder N3_2 = new StringBuilder();
                StringBuilder N4_2 = new StringBuilder();
                StringBuilder DMG = new StringBuilder();
                StringBuilder NM1_5 = new StringBuilder();
                StringBuilder N3_3 = new StringBuilder();
                StringBuilder N4_3 = new StringBuilder();
                StringBuilder CLM = new StringBuilder();
                StringBuilder DTP_1 = new StringBuilder();
                StringBuilder DTP_2 = new StringBuilder();
                StringBuilder DTP_3 = new StringBuilder();
                StringBuilder CL1 = new StringBuilder();
                StringBuilder REF_2 = new StringBuilder();
                StringBuilder HI_1_ABK = new StringBuilder();
                StringBuilder HI_1_ABJ = new StringBuilder();
                StringBuilder HI_1_ABN = new StringBuilder();
                StringBuilder HI_1_APR = new StringBuilder();
                StringBuilder HI_1_ABF = new StringBuilder();
                StringBuilder HI_1_BBR = new StringBuilder();
                StringBuilder HI_1_BBQ = new StringBuilder();
                StringBuilder HI_1_BI = new StringBuilder();
                StringBuilder HI_1_BH = new StringBuilder();
                StringBuilder HI_1_BE = new StringBuilder();
                StringBuilder HI_1_BG = new StringBuilder();
                StringBuilder NM1_6 = new StringBuilder();
                StringBuilder PRV_AT = new StringBuilder();
                StringBuilder NM1_7 = new StringBuilder();
                StringBuilder N3_4 = new StringBuilder();
                StringBuilder N4_4 = new StringBuilder();
                StringBuilder LX1 = new StringBuilder();
                StringBuilder SV2_1 = new StringBuilder();
                StringBuilder SE = new StringBuilder();
                StringBuilder GE = new StringBuilder();
                StringBuilder IEA = new StringBuilder();


                int TagCount = 0;
                ISA.append("ISA*00*          *00*          *ZZ*AV09311993     *01*030240928      *" + ClaimCreateDate + "*" + ClaimCreateTime + "*^*00501*" + InterControlNo + "*1*T*:~\n");
                //GS.append("GS*HC*p920700*ECGCLAIMS*20210625*155544*000000001*X*005010X223A2~\n");
                GS.append("GS*HC*AV09311993*030240928*" + CreationDate + "*" + ClaimCreateTime + "*" + InterControlNo + "*X*005010X223A2~\n");
                ST.append("ST*837*" + ST02 + "*005010X223A2~\n");
                BHT.append("BHT*0019*00*1*" + CreationDate + "*" + ClaimCreateTime + "*CH~\n");
                NM1.append("NM1*41*2*" + AttendingProviderLastName + " " + AttendingProviderFirstName + "*****46*009207651653~\n");
                PER.append("PER*IC*" + BillingProvidersLastName + " " + BillingProvidersFirstName + "*TE*2812800911*FX*2812800041~\n");
                NM1_2.append("NM1*40*2*MCKHBOC*****46*0001~\n");
                HL_1.append("HL*1**20*1~\n");
                PRV_BI.append("PRV*BI*PXC*" + TaxanomySpecialty + "~\n");
                NM1_3.append("NM1*85*2*" + ClientName + ",*****XX*" + ClientNPI + "~\n");
                N3_1.append("N3*" + ClientAddress + "~\n");
                N4_1.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");
                REF_1.append("REF*EI*" + ClientTaxID + "~\n");
                HL_2.append("HL*2*1*22*0~\n");
                SBR.append("SBR*P*18*" + GrpNumber + "******" + ClaimType + "~\n");
                NM1_4.append("NM1*IL*1*" + PatientLastName + "*" + PatientFirstName + "*" + "" + "***MI*" + MemId + "~\n");
                N3_2.append("N3*" + Address + "~\n");
                N4_2.append("N4*" + City + "*" + State + "*" + ZipCode + "~\n");
                DMG.append("DMG*D8*" + DOB + "*" + Gender + "~\n");
                NM1_5.append("NM1*PR*2*" + (PriInsuranceName.contains("\n") ? PriInsuranceName.replaceAll("\n", "") : PriInsuranceName) + "*****PI*" + PriInsuranceNameId + "~\n");
                N3_3.append("N3*" + "17154 butte creek road" + "~\n");//Insurance Street Address
                N4_3.append("N4*" + "HOUSTON" + "*" + "TX" + "*" + "77090" + "~\n");//Insurance City, Insurance State, Insurance ZipCode


                TagCount = 20;
//out.println("TagCount--1--"+TagCount);
                int foundExtCauseInj = 0;
                int foundReasVisit = 0;
                int foundOthDiag = 0;
                int foundOthProc = 0;
                int foundOccSpan = 0;
                int foundOcc = 0;
                int foundValCode = 0;
                int foundConCode = 0;
                Query = "Select Count(*) from " + Database + ".ClaimInfoCodeExtCauseInj where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    foundExtCauseInj = rset.getInt(1);
                }
                rset.close();
                stmt.close();
                if (foundExtCauseInj > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeExtCauseInj where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_ABN.append("HI*ABN:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--2--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeReasVisit where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    foundReasVisit = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundReasVisit > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeReasVisit where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_APR.append("HI*APR:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--3--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeOthDiag where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOthDiag = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOthDiag > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(PQA,'') from " + Database + ".ClaimInfoCodeOthDiag where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_ABF.append("HI*ABF:" + Code + ":::::::" + rset.getString(2) + "~\n");
                        TagCount++;
                        //out.println("TagCount--4--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

                if (PrincipalProcedureDateInfoCodes.equals("")) {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes;
                } else if (PrincipalProcedureDateInfoCodes.contains("/")) {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes.replaceAll("/", "");
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes.substring(4, 8) + PrincipalProcedureDateInfoCodes.substring(0, 2) + PrincipalProcedureDateInfoCodes.substring(2, 4);
                } else {
                    PrincipalProcedureDateInfoCodes = PrincipalProcedureDateInfoCodes;
                }
                if (!PrincipalProcedureInfoCodes.equals("")) {
                    HI_1_BBR.append("HI*BBR:" + PrincipalProcedureInfoCodes + "*" + PrincipalProcedureDateInfoCodes + "~\n");
                    TagCount++;
                    //out.println("TagCount--5--" + TagCount);
                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeOthProcedure where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOthProc = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOthProc > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Date,'') from " + Database + ".ClaimInfoCodeOthProcedure where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Date = "";
                        if (rset.getString(2).equals("")) {
                            Date = rset.getString(2);
                        } else if (rset.getString(2).contains("/")) {
                            Date = rset.getString(2).replaceAll("/", "");
                            Date = Date.substring(4, 8) + Date.substring(0, 2) + Date.substring(2, 4);
                        } else {
                            Date = rset.getString(2);
                        }
                        HI_1_BBQ.append("HI*BBQ:" + Code + ":" + Date + "~\n");
                        TagCount++;
                        //out.println("TagCount--6--"+TagCount);
                    }
                    rset.close();
                    stmt.close();
                }

//                Query = "Select COUNT(*) from "+Database+".ClaimInfoCodeOccSpan where ClaimInfoMasterId = "+ClaimInfoMasterId + " " +
//                        " and ClaimNumber = '"+ClaimNumber+"'";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                while(rset.next()) {
//                    foundOccSpan = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//
//                if(foundOccSpan > 0 ){
//                    Query = "Select IFNULL(Code,''), IFNULL(FromDate,''), IFNULL(ToDate,'') from " + Database + ".ClaimInfoCodeOccSpan where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
//                            " and ClaimNumber = '" + ClaimNumber + "'";
//                    stmt = conn.createStatement();
//                    rset = stmt.executeQuery(Query);
//                    while (rset.next()) {
//                        String Code = "";
//                        if(rset.getString(1).contains(".")){
//                            Code = rset.getString(1).replace(".","");
//                        }else{
//                            Code = rset.getString(1);
//                        }
//                        String FromDate = "";String ToDate = "";
//                        if(rset.getString(2).equals("")){
//                            FromDate = rset.getString(2);
//                        }else if(rset.getString(2).contains("/")){
//                            FromDate = rset.getString(2).replaceAll("/","");
//                            FromDate = FromDate.substring(4,8)+FromDate.substring(0,2)+FromDate.substring(2,4);
//                        }else {
//                            FromDate = rset.getString(2);
//                        }
//                        if(rset.getString(3).equals("")){
//                            ToDate = rset.getString(3);
//                        }else if(rset.getString(3).contains("/")){
//                            ToDate = rset.getString(3).replaceAll("/","");
//                            ToDate = ToDate.substring(4,8)+ToDate.substring(0,2)+ToDate.substring(2,4);
//                        }else {
//                            ToDate = rset.getString(3);
//                        }
//                        HI_1_BI.append("HI*BI:" + Code+ "*" + FromDate + "-"+ ToDate+"~\n");
//                    }
//                    rset.close();
//                    stmt.close();
//                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundOcc = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundOcc > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Date,'') from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Date = "";
                        if (rset.getString(2).equals("")) {
                            Date = rset.getString(2);
                        } else if (rset.getString(2).contains("/")) {
                            Date = rset.getString(2).replaceAll("/", "");
                            Date = Date.substring(4, 8) + Date.substring(0, 2) + Date.substring(2, 4);
                        } else {
                            Date = rset.getString(2);
                        }
                        HI_1_BH.append("HI*BH:" + Code + "*" + Date + "~\n");
                        TagCount++;
                        //out.println("TagCount--7--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeValueCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundValCode = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundValCode > 0) {
                    Query = "Select IFNULL(Code,''), IFNULL(Amount,'') from " + Database + ".ClaimInfoCodeValueCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        String Amount = "";
                        if (rset.getString(2).contains(".")) {
                            Amount = rset.getString(2).replace(".", "");
                        } else {
                            Amount = rset.getString(2);
                        }
//                        HI_1_BE.append("HI*BE:" + rset.getString(1) + ":" + (rset.getString(2).equals("") ? rset.getString(2) : rset.getString(2).replaceAll(".","")) + "^\n");
                        HI_1_BE.append("HI*BE:" + Code + "*" + Amount + "~\n");
                        TagCount++;
                        //out.println("TagCount--8--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }

                Query = "Select COUNT(*) from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    foundConCode = rset.getInt(1);
                }
                rset.close();
                stmt.close();

                if (foundConCode > 0) {
                    Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                            " and ClaimNumber = '" + ClaimNumber + "'";
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        String Code = "";
                        if (rset.getString(1).contains(".")) {
                            Code = rset.getString(1).replace(".", "");
                        } else {
                            Code = rset.getString(1);
                        }
                        HI_1_BG.append("HI*BG:" + Code + "~\n");
                        TagCount++;
                        //out.println("TagCount--9--"+TagCount);
                    }
                    rset.close();
                    stmt.close();

                }


                //out.println("TagCount---10--"+TagCount);

                int iCount = 1;
                String TotalChrgeAmt = "";
                Query = "Select IFNULL(a.RevCode,''), IFNULL(a.HCPCS,''), IFNULL(a.Mod1,''), IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), " +
                        "IFNULL(a.Mod4,''), CASE WHEN a.DescriptionFrom = 0 THEN b.ShortDescription WHEN a.DescriptionFrom = 1 THEN c.Description ELSE c.Description END, " +
                        "IFNULL(a.Amount,''), IFNULL(a.Units,'') from " + Database + ".ClaimChargesInfo a " +
                        " LEFT JOIN oe." + ChargeMasterTableName + " b on a.HCPCS = b.CPTCode " +
                        " LEFT JOIN oe.RevenueCode c on a.RevCode = c.Codes " +
                        "where a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and a.ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    String HCPCS = "";
                    String Units = "";
                    if (rset.getString(2).contains(".")) {
                        HCPCS = rset.getString(2).replace(".", "");
                    } else {
                        HCPCS = rset.getString(2);
                    }
                    String Amount = "";
                    if (rset.getString(8).contains(".")) {
                        //Amount = rset.getString(8).replace(".","");
                        Amount = rset.getString(8).substring(0, rset.getString(8).indexOf("."));
                    } else {
                        Amount = rset.getString(8);
                    }
                    Units = rset.getString(9);
                    if (Units.contains(".")) {
                        //Units = Units.replace(".","");
                        Units = Units.substring(0, Units.indexOf("."));
                    }
                    SV2_1.append("LX*" + iCount + "~\n" + "SV2*" + rset.getString(1) + "*HC:" + HCPCS + ":" + rset.getString(3) + ":" + rset.getString(4) + ":" + rset.getString(5) + ":" + rset.getString(6) + ":" + rset.getString(7) + "*" + Amount + "*UN*" + Units + "~\n");
                    TotalChargeAmount += Double.parseDouble(Amount);
                    TagCount = TagCount + 2;
                    //out.println("TagCount---11--"+TagCount);
                    iCount++;
                }
                rset.close();
                stmt.close();
                TotalChrgeAmt = String.valueOf(TotalChargeAmount);

                if (TotalChrgeAmt.contains(".")) {
                    TotalChrgeAmt = (TotalChrgeAmt.substring(0, TotalChrgeAmt.indexOf(".")));
                }
                System.out.println(TotalChrgeAmt);

                CLM.append("CLM*915243155*" + TotalChrgeAmt + "***22:A:1*" + ProvAccAssigAddInfo + "*" + AssofBenifitAddInfo + "*" + ReleaseInfoAddInfo + "*" + ReleaseInfoAddInfo + "~\n");
                //DTP_1.append("DTP*096*TM*" + DischargeHourAddInfo + "00~\n");
                DTP_2.append("DTP*434*RD8*" + StatmentCoverFromDateAddInfo + "-" + StatmentCoverToDateAddInfo + "~\n");
                //DTP_3.append("DTP*435*DT*" + AdmissionDateAddInfo + "" + AdmissionHourAddInfo + "00~\n");
                CL1.append("CL1*1*9*" + AdmissionTypeAddInfo + "~\n");
                REF_2.append("REF*EA*" + PatientMRN + "~\n");
                HI_1_ABK.append("HI*ABK:" + PrincipalDiagInfoCodes + ":::::::" + POAInfoCodes + "~\n");
                //HI_1_ABJ.append("HI*ABJ:" + AdmittingDiagInfoCodes + "~\n");
                NM1_6.append("NM1*71*1*" + AttendingProviderLastName + "*" + AttendingProviderFirstName + "****XX*" + AttendingProviderNPI + "~\n");
                PRV_AT.append("PRV*AT*PXC*TaxonomySpeciality_Provider~\n");
                NM1_7.append("NM1*77*2*" + ClientName + "*****XX*" + ClientNPI + "~\n");
                N3_4.append("N3*" + ClientAddress + "~\n");
                N4_4.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");

                TagCount = TagCount + 11;
                //out.println("TagCount---12--"+TagCount);
                SE.append("SE*" + TagCount + "*" + ST02 + "~\n");
                GE.append("GE*1*" + InterControlNo + "~\n");
                IEA.append("IEA*1*" + InterControlNo + "~\n");

                //out.println("TagCount--ALL--"+TagCount);

                File file = new File("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (file.exists()) {
                    file.delete();
                }

                File myObj = new File("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (myObj.createNewFile()) {

                    FileWriter myWriter = new FileWriter("/sftpdrive/opt/EDI_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                    myWriter.write(ISA.toString());
                    myWriter.write(GS.toString());
                    myWriter.write(ST.toString());
                    myWriter.write(BHT.toString());
                    myWriter.write(NM1.toString());
                    myWriter.write(PER.toString());
                    myWriter.write(NM1_2.toString());
                    myWriter.write(HL_1.toString());
                    myWriter.write(PRV_BI.toString());
                    myWriter.write(NM1_3.toString());
                    myWriter.write(N3_1.toString());
                    myWriter.write(N4_1.toString());
                    myWriter.write(REF_1.toString());
                    myWriter.write(HL_2.toString());
                    myWriter.write(SBR.toString());
                    myWriter.write(NM1_4.toString());
                    myWriter.write(N3_2.toString());
                    myWriter.write(N4_2.toString());
                    myWriter.write(DMG.toString());
                    myWriter.write(NM1_5.toString());
                    myWriter.write(N3_3.toString());
                    myWriter.write(N4_3.toString());
                    myWriter.write(CLM.toString());
                    myWriter.write(DTP_1.toString());
                    myWriter.write(DTP_2.toString());
                    myWriter.write(DTP_3.toString());
                    myWriter.write(CL1.toString());
                    myWriter.write(REF_2.toString());
                    myWriter.write(HI_1_ABK.toString());
                    myWriter.write(HI_1_ABJ.toString());
                    myWriter.write(HI_1_ABN.toString());
                    myWriter.write(HI_1_APR.toString());
                    myWriter.write(HI_1_ABF.toString());
                    myWriter.write(HI_1_BBR.toString());
                    myWriter.write(HI_1_BBQ.toString());
                    //myWriter.write(HI_1_BI.toString());
                    myWriter.write(HI_1_BH.toString());
                    myWriter.write(HI_1_BE.toString());
                    myWriter.write(HI_1_BG.toString());
                    myWriter.write(NM1_6.toString());
                    myWriter.write(PRV_AT.toString());
                    myWriter.write(NM1_7.toString());
                    myWriter.write(N3_4.toString());
                    myWriter.write(N4_4.toString());
                    myWriter.write(SV2_1.toString());
                    myWriter.write(SE.toString());
                    myWriter.write(GE.toString());
                    myWriter.write(IEA.toString());


                    myWriter.close();
                }

            }
            Instant end = Instant.now();
            Duration ExecutionTime = Duration.between(start, end);
//            out.println(ErrorMsgs + "~" + ExecutionTime);
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            out.println("An error occurred.");
            out.println(e.getMessage());
            out.println(e.getMessage() + Query);
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
        }
    }

    public void OpenUB04(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        String Query = "";
        Statement stmt = null;
        ResultSet rset = null;

        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String ClaimNumber = request.getParameter("ClaimNumber").trim();
//int ClaimType = Integer.parseInt(request.getParameter("ClaimType").trim());
        int VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        String DateTime = "";
        String DateNow = "";
        String PatientFirstName = "";
        String PatientLastName = "";
        String DOB = "";
        String Gender = "";
        String PriInsuredName = "";
        int ClaimInfoMasterId = 0;
        int SelfPayChk = 0;
        String ClientAddress = "";
        String ClientCity = "";
        String ClientState = "";
        String ClientZipCode = "";
        String ClientPhone = "";
        String ClientNPI = "";

//Claim Basic Info Variable
        String RefNumber = "";
        String TypeBillText = "";
        String PatientName = "";
        String PatientMRN = "";
        String AcctNo = "";
        String PhNumber = "";
        String Email = "";
        String Address = "";
        String City = "";
        String State = "";
        String StateCode = "";
        String ZipCode = "";
        String DOS = "";
        String UploadDate = "";
        String AttendingProviderLastName = "";
        String AttendingProviderFirstName = "";
        String AttendingProviderNPI = "";
        String BillingProvidersLastName = "";
        String BillingProvidersFirstName = "";
        String BillingProvidersNPI = "";
        String OperatingProviderLastName = "";
        String OperatingProviderFirstName = "";
        String OperatingProviderNPI = "";
        String ClientName = "";
        String PriInsuranceName = "";
        String PriInsuranceNameId = "";
        String MemId = "";
        String PolicyType = "";
        String GrpNumber = "";
        String SecondaryInsurance = "";
        String SecondaryInsuranceId = "";
        String SecondaryInsuranceMemId = "";
        String SecondaryInsuranceGrpNumber = "";
        String CreationDate = "";
//
//        //Charges Variables
        double TotalChargeAmount = 0.00;
//        String ChargesTableCount = "0";
//        String ChargesString = "";
//
//Additional Info Variable
        String StatmentCoverFromDateAddInfo = "";
        String StatmentCoverToDateAddInfo = "";
        String AdmissionDateAddInfo = "";
        String AdmissionHourAddInfo = "";
        String AdmissionTypeAddInfo = "";
        String AdmissionSourceAddInfo = "";
        String DischargeHourAddInfo = "";
        String PatientStatusAddInfo = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String PPSAddInfo = "";
        String RemarksAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String ReleaseInfoAddInfo = "";
        String AssofBenifitAddInfo = "";
        String ProvAccAssigAddInfo = "";
//
//Information Codes variable
        String PrincipalDiagInfoCodes = "";
        String POAInfoCodes = "";
        String AdmittingDiagInfoCodes = "";
        String PrincipalProcedureInfoCodes = "";
        String PrincipalProcedureDateInfoCodes = "";
        String ExternalCauseInjuryTableCount = "0";
        String ExternalCauseInjuryString = "";
        String ReasonVisitTableCount = "0";
        String ReasonVisitString = "";
        String OtherDiagnosisString = "";
        String OtherDiagnosisTableCount = "0";
        String ClientTaxanomySpecialty = "";
        String ClientTaxID = "";

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), " +
                    "IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,''), " +
                    "IFNULL(TaxanomySpecialty,''), IFNULL(TaxID,'') " +
                    " from oe.clients" +
                    " where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientAddress = rset.getString(1);
                ClientCity = rset.getString(2);
                ClientState = rset.getString(3);
                ClientZipCode = rset.getString(4);
                ClientPhone = rset.getString(5);
                ClientNPI = rset.getString(6);
                ClientTaxanomySpecialty = rset.getString(7);
                ClientTaxID = rset.getString(8);
            }
            rset.close();
            stmt.close();

            Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster " +
                    " where  PatientRegId = " + PatientRegId + " and VisitId = " + VisitId +
                    " and ClaimNumber = '" + ClaimNumber + "'";
            //System.out.println(Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found == 0) {
                out.println("Please Save Claim First and then Print UB04");

            } else {
                Query = "Select ChargeMasterTableName from oe.clients where ID = " + ClientId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ChargeMasterTableName = rset.getString(1).trim();
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(FirstName,''), IFNULL(LastName,''), IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), " +
                        "IFNULL(DATE_FORMAT(DOB,'%m%d%Y'),''), DATE_FORMAT(NOW(),'%d%m%y%k%i%s'), IFNULL(Gender,''), DATE_FORMAT(NOW(),'%m%d%y'), " +
                        "IFNULL(SelfPayChk,'0')" +
                        " from " + Database + ".PatientReg where ID = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientFirstName = rset.getString(1);
                    PatientLastName = rset.getString(2);
                    Address = rset.getString(3);
                    City = rset.getString(4);
                    State = rset.getString(5);
                    ZipCode = rset.getString(6);
                    DOB = rset.getString(7);
                    DateTime = rset.getString(8);
                    Gender = rset.getString(9).trim();
                    DateNow = rset.getString(10);
                    SelfPayChk = rset.getInt(11);
                }
                rset.close();
                stmt.close();
                if (!Gender.equals("")) {
                    Gender = Gender.substring(0, 1).toUpperCase();
                } else {
                    Gender = "";
                }

                if (SelfPayChk == 1) {
                    Query = "Select IFNULL(PriInsurerName,'') from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuredName = rset.getString(1);
                    }
                    rset.close();
                    stmt.close();

                    if (PriInsuredName.equals("")) {
                        PriInsuredName = PatientLastName + ", " + PatientFirstName;
                    }

                } else {
                    PriInsuredName = PatientLastName + ", " + PatientFirstName;
                }


                if (!State.equals("")) {
                    if (State.length() > 2) {
                        Query = "Select IFNULL(StateCode,'') from " + Database + ".StateList where ltrim(rtrim(Upper(StateName))) = ltrim(rtrim(UPPER('" + State + "')))";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        if (rset.next()) {
                            StateCode = rset.getString(1);
                        }
                        rset.close();
                        stmt.close();
                    } else {
                        StateCode = State.toUpperCase();
                    }

                } else {
                    StateCode = "";
                }

                Query = "Select a.Id,IFNULL(RefNumber,''), IFNULL(a.TypeBillText,''), IFNULL(a.PatientName,''), IFNULL(a.PatientMRN,''), " +
                        " IFNULL(a.AcctNo,''), IFNULL(a.PhNumber,''), " +
                        " IFNULL(a.Email,''), IFNULL(a.Address,''), IFNULL(a.DOS,''), " +
                        " IFNULL(b.DoctorsLastName,''), IFNULL(b.DoctorsFirstName,'') , IFNULL(b.NPI,'') as AttendingNPI, " +
                        " IFNULL(c.DoctorsLastName,''), IFNULL(c.DoctorsFirstName,''), IFNULL(c.NPI,'') as BillingNPI, " +
                        " IFNULL(d.PayerName,'') as PriInsuranceName, IFNULL(a.MEMId,''), IFNULL(a.PolicyType,''), IFNULL(a.GrpNumber,''), " +
                        " IFNULL(e.PayerName,'') as SecondaryInsurance, " +
                        " IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(a.SecondaryInsuranceGrpNumber,''), IFNULL(ClientName,''), " +
                        " IFNULL(f.DoctorsLastName,''), IFNULL(f.DoctorsFirstName,''), IFNULL(f.NPI,'') as OperatingProvider, " +
                        " DATE_FORMAT(a.CreatedDate,'%m%d%y') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN " + Database + ".DoctorsList b on a.AttendingProvider = b.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList c on a.BillingProviders = c.Id " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
//                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        " where a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
                System.out.println("AA " + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    RefNumber = rset.getString(2);
                    TypeBillText = rset.getString(3);
                    PatientName = rset.getString(4).replaceAll(" ", "").replaceAll("'", "");
                    PatientMRN = rset.getString(5);
                    AcctNo = rset.getString(6);
                    PhNumber = rset.getString(7);
                    Email = rset.getString(8);
                    //Address = rset.getString(9);
                    DOS = rset.getString(10).replaceAll("/", "");
                    AttendingProviderLastName = rset.getString(11);
                    AttendingProviderFirstName = rset.getString(12);
                    AttendingProviderNPI = rset.getString(13);
                    BillingProvidersLastName = rset.getString(14);
                    BillingProvidersFirstName = rset.getString(15);
                    BillingProvidersNPI = rset.getString(16);
                    PriInsuranceName = rset.getString(17);
                    MemId = rset.getString(18);
                    PolicyType = rset.getString(19);
                    GrpNumber = rset.getString(20);
                    SecondaryInsurance = rset.getString(21);
                    SecondaryInsuranceMemId = rset.getString(22);
                    SecondaryInsuranceGrpNumber = rset.getString(23);
                    ClientName = rset.getString(24);
                    OperatingProviderLastName = rset.getString(25);
                    OperatingProviderFirstName = rset.getString(26);
                    OperatingProviderNPI = rset.getString(27);
                    CreationDate = rset.getString(28);

                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(StatmentCoverFromDateAddInfo,''), IFNULL(StatmentCoverToDateAddInfo,''), " +
                        " IFNULL(AdmissionDateAddInfo,''), IFNULL(AdmissionHourAddInfo,''), IFNULL(AdmissionTypeAddInfo,''), " +
                        " IFNULL(AdmissionSourceAddInfo,''), IFNULL(DischargeHourAddInfo,''), IFNULL(PatientStatusAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'')," +
                        " IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(OtherAccidentAddInfo,''), IFNULL(PPSAddInfo,''), " +
                        " IFNULL(RemarksAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), IFNULL(ReleaseInfoAddInfo,''), IFNULL(AssofBenifitAddInfo,''), " +
                        " IFNULL(ProvAccAssigAddInfo,'') " +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    StatmentCoverFromDateAddInfo = rset.getString(1);
                    StatmentCoverToDateAddInfo = rset.getString(2);
                    AdmissionDateAddInfo = rset.getString(3);
                    AdmissionHourAddInfo = rset.getString(4);
                    AdmissionTypeAddInfo = rset.getString(5);
                    AdmissionSourceAddInfo = rset.getString(6);
                    DischargeHourAddInfo = rset.getString(7);
                    PatientStatusAddInfo = rset.getString(8);
                    DelayReasonCodeAddInfo = rset.getString(9);
                    EmploymentStatusAddInfo = rset.getString(10);
                    AutoAccidentAddInfo = rset.getString(11);
                    OtherAccidentAddInfo = rset.getString(12);
                    PPSAddInfo = rset.getString(13);
                    RemarksAddInfo = rset.getString(14);
                    AutoAccident_StateAddInfo = rset.getString(15);
                    ReleaseInfoAddInfo = rset.getString(16);
                    AssofBenifitAddInfo = rset.getString(17);
                    ProvAccAssigAddInfo = rset.getString(18);
                }
                rset.close();
                stmt.close();

                if (!StatmentCoverFromDateAddInfo.equals("")) {
                    StatmentCoverFromDateAddInfo = StatmentCoverToDateAddInfo.replaceAll("/", "");
                }
                if (!StatmentCoverToDateAddInfo.equals("")) {
                    StatmentCoverToDateAddInfo = StatmentCoverToDateAddInfo.replaceAll("/", "");
                }
                if (!AdmissionDateAddInfo.equals("")) {
                    AdmissionDateAddInfo = AdmissionDateAddInfo.replaceAll("/", "");
                }

                Query = "Select IFNULL(PrincipalDiagInfoCodes,''), IFNULL(POAInfoCodes,''), IFNULL(AdmittingDiagInfoCodes,''), " +
                        " IFNULL(PrincipalProcedureInfoCodes,''), IFNULL(PrincipalProcedureDateInfoCodes,'') " +
                        "from " + Database + ".ClaimInformationCode where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PrincipalDiagInfoCodes = rset.getString(1);
                    POAInfoCodes = rset.getString(2);
                    AdmittingDiagInfoCodes = rset.getString(3);
                    PrincipalProcedureInfoCodes = rset.getString(4);
                    PrincipalProcedureDateInfoCodes = rset.getString(5);
                }
                rset.close();
                stmt.close();

                String inputFilePath = "";
                inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/BillingPdf/ub-40-P.pdf";
                String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/UB04/" + PatientName + PatientRegId + "_" + ".pdf";
                OutputStream fos = new FileOutputStream(new File(outputFilePath));
                PdfReader pdfReader = new PdfReader(inputFilePath);
                PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
                for (i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                    if (i == 1) {
                        PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                        //box 1
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 770); // set x and y co-ordinates
                        pdfContentByte.showText(ClientName); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 757); // set x and y co-ordinates
                        pdfContentByte.showText(ClientAddress); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 745); // set x and y co-ordinates
                        pdfContentByte.showText(ClientCity + " " + ClientState + " " + ClientZipCode); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 735); // set x and y co-ordinates
                        pdfContentByte.showText(ClientPhone); // add the text
                        pdfContentByte.endText();

                        //box 2
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(195, 770); // set x and y co-ordinates
                        pdfContentByte.showText(ClientName); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(195, 757); // set x and y co-ordinates
                        pdfContentByte.showText(ClientAddress); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(195, 745); // set x and y co-ordinates
                        pdfContentByte.showText(ClientCity + " " + ClientState + " " + ClientZipCode); // add the text
                        pdfContentByte.endText();

                        //box 3a
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(395, 770); // set x and y co-ordinates
                        pdfContentByte.showText("COPY" + ClaimNumber); // add the text
                        pdfContentByte.endText();

                        //box 4
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(570, 757); // set x and y co-ordinates
                        pdfContentByte.showText(TypeBillText); // add the text
                        pdfContentByte.endText();

                        //box 5
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(372, 735); // set x and y co-ordinates
                        pdfContentByte.showText("FED. TAX NO"); // add the text
                        pdfContentByte.endText();

                        //box 6
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(445, 735); // set x and y co-ordinates
                        pdfContentByte.showText(StatmentCoverFromDateAddInfo); // STATEMENT COVERS PERIOD-- FROMDate
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(495, 735); // set x and y co-ordinates
                        pdfContentByte.showText(StatmentCoverToDateAddInfo); // STATEMENT COVERS PERIOD-- TO Date
                        pdfContentByte.endText();

                        //box 8a
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(100, 723); // set x and y co-ordinates
                        pdfContentByte.showText(PatientMRN); // MRN
                        pdfContentByte.endText();
                        //box 8b
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 710); // set x and y co-ordinates
                        pdfContentByte.showText(PatientLastName + ", " + PatientFirstName); // MRN
                        pdfContentByte.endText();

                        //box 9a
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(320, 723); // set x and y co-ordinates
                        pdfContentByte.showText(Address); //
                        pdfContentByte.endText();
                        //box 9b
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(240, 710); // set x and y co-ordinates
                        pdfContentByte.showText(City); //
                        pdfContentByte.endText();
                        //box 9c
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475, 710); // set x and y co-ordinates
                        pdfContentByte.showText(StateCode); //
                        pdfContentByte.endText();
                        //box 9d
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(505, 710); // set x and y co-ordinates
                        pdfContentByte.showText(ZipCode); //
                        pdfContentByte.endText();

                        //box 10
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 685); // set x and y co-ordinates
                        pdfContentByte.showText(DOB); // MRN
                        pdfContentByte.endText();

                        //box 11
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(80, 685); // set x and y co-ordinates
                        pdfContentByte.showText(Gender); // sex
                        pdfContentByte.endText();

                        //box 12
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(100, 685); // set x and y co-ordinates
                        pdfContentByte.showText(AdmissionDateAddInfo); // Admission Date
                        pdfContentByte.endText();

                        //box 13
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(145, 685); // set x and y co-ordinates
                        pdfContentByte.showText(AdmissionHourAddInfo); // Admission Hour
                        pdfContentByte.endText();

                        //box 14
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(170, 685); // set x and y co-ordinates
                        pdfContentByte.showText(AdmissionTypeAddInfo); // Admission Type
                        pdfContentByte.endText();

                        //box 15
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(190, 685); // set x and y co-ordinates
                        pdfContentByte.showText(AdmissionSourceAddInfo); // Admission Source
                        pdfContentByte.endText();

                        //box 16
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(210, 685); // set x and y co-ordinates
                        pdfContentByte.showText(DischargeHourAddInfo); // Discharge Hour
                        pdfContentByte.endText();

                        //box 17
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(230, 685); // set x and y co-ordinates
                        pdfContentByte.showText(PatientStatusAddInfo); // Patient Discharge Status
                        pdfContentByte.endText();

//***************box 18 to  box 28 COndition Codes*************
                        //box 18
                        int xCon = 0;
                        int CondCodeCount = 1;
                        Query = "Select Code from " + Database + ".ClaimInfoCodeConditionCode where ClaimInfoMasterId = " + ClaimInfoMasterId +
                                " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (CondCodeCount == 1) {
                                xCon = 250;
                            } else if (CondCodeCount == 2) {
                                xCon = 272;
                            } else if (CondCodeCount == 3) {
                                xCon = 293;
                            } else if (CondCodeCount == 4) {
                                xCon = 315;
                            } else if (CondCodeCount == 5) {
                                xCon = 336;
                            } else if (CondCodeCount == 6) {
                                xCon = 359;
                            } else if (CondCodeCount == 7) {
                                xCon = 380;
                            } else if (CondCodeCount == 8) {
                                xCon = 403;
                            } else if (CondCodeCount == 9) {
                                xCon = 425;
                            } else if (CondCodeCount == 10) {
                                xCon = 445;
                            } else if (CondCodeCount == 11) {
                                xCon = 467;
                            } else {
                                break;
                            }
                            //box 18 to box 28 Condition Codes
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xCon, 685); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // Condition Codes
                            pdfContentByte.endText();
                            CondCodeCount++;
                        }
                        rset.close();
                        stmt.close();
//*************************COndition Codes End*********************************

                        //box 29
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(490, 685); // set x and y co-ordinates
                        pdfContentByte.showText(AutoAccident_StateAddInfo); // Accident State
                        pdfContentByte.endText();

                        //box 31 to 34 Occurrence Codes and Dates
                        int xOccD = 0;
                        int yOccD = 0;
                        int xOccC = 0;
                        int yOccC = 0;
                        int OccCodeCount = 1;
                        Query = "Select IFNULL(Code,''), IFNULL(Date,'') from " + Database + ".ClaimInfoOccurance where ClaimInfoMasterId = " + ClaimInfoMasterId +
                                " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (OccCodeCount == 1) {
                                xOccC = 13;
                                yOccC = 660;
                                xOccD = 35;
                                yOccD = 660;
                            } else if (OccCodeCount == 2) {
                                xOccC = 13;
                                yOccC = 650;
                                xOccD = 35;
                                yOccD = 650;
                            } else if (OccCodeCount == 3) {
                                xOccC = 87;
                                yOccC = 660;
                                xOccD = 105;
                                yOccD = 660;
                            } else if (OccCodeCount == 4) {
                                xOccC = 87;
                                yOccC = 650;
                                xOccD = 105;
                                yOccD = 650;
                            } else if (OccCodeCount == 5) {
                                xOccC = 157;
                                yOccC = 660;
                                xOccD = 178;
                                yOccD = 660;
                            } else if (OccCodeCount == 6) {
                                xOccC = 157;
                                yOccC = 650;
                                xOccD = 178;
                                yOccD = 650;
                            } else if (OccCodeCount == 7) {
                                xOccC = 230;
                                yOccC = 660;
                                xOccD = 250;
                                yOccD = 660;
                            } else if (OccCodeCount == 8) {
                                xOccC = 230;
                                yOccC = 650;
                                xOccD = 250;
                                yOccD = 650;
                            } else {
                                break;
                            }
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xOccC, yOccC); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // OccuranceCode--31
                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xOccD, yOccD); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(2) == null ? "" : rset.getString(2).contains("/") ? rset.getString(2).replaceAll("/", "") : rset.getString(2)); // OccuranceDAte---31
                            pdfContentByte.endText();
                            OccCodeCount++;
                        }
                        rset.close();
                        stmt.close();

                        //box 35 to 36  Occurrence Span Codes and Dates
                        //box 35
                        int xOccSp = 0;
                        int x1OccSp = 0;
                        int x2OccSp = 0;
                        int yOccSp = 0;
                        int OccSpCount = 1;
                        Query = "Select IFNULL(Code,''), IFNULL(FromDate,''), IFNULL(ToDate,'') from " + Database + ".ClaimInfoCodeOccSpan where " +
                                " ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (OccSpCount == 1) {
                                xOccSp = 300;
                                x1OccSp = 321;
                                x2OccSp = 372;
                                yOccSp = 660;
                            } else if (OccSpCount == 2) {
                                xOccSp = 300;
                                x1OccSp = 321;
                                x2OccSp = 372;
                                yOccSp = 650;
                            } else if (OccSpCount == 3) {
                                xOccSp = 422;
                                x1OccSp = 445;
                                x2OccSp = 496;
                                yOccSp = 660;
                            } else if (OccSpCount == 4) {
                                xOccSp = 422;
                                x1OccSp = 445;
                                x2OccSp = 496;
                                yOccSp = 650;
                            } else {
                                break;
                            }
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xOccSp, yOccSp); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // OccuranceSPANCode--35
                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(x1OccSp, yOccSp); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(2) == null ? "" : rset.getString(2).contains("/") ? rset.getString(2).replaceAll("/", "") : rset.getString(2)); // Occurance FROM DAte---35
                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(x2OccSp, yOccSp); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(3) == null ? "" : rset.getString(3).contains("/") ? rset.getString(3).replaceAll("/", "") : rset.getString(3)); // Occurance to DAte---35
                            pdfContentByte.endText();
                            OccSpCount++;
                        }
                        rset.close();
                        stmt.close();

                        //box 38  Insurance Name and Address COmplete here //Responsible Party Name and Address
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 635); // set x and y co-ordinates

                        pdfContentByte.showText(PriInsuranceName.length() < 35 ? PriInsuranceName : PriInsuranceName.substring(0, 35)); //Insurance Name and Address COmplete here // Responsible Party Name and Address
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 625); // set x and y co-ordinates
                        pdfContentByte.showText("Insuracnce Address");//Insurance Name and Address COmplete here  // Responsible Party Name and Address
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 615); // set x and y co-ordinates
                        pdfContentByte.showText("Insuracnce Address"); //Insurance Name and Address COmplete here // city sATE ZIPCODEResponsible Party Name and Address
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 605); // set x and y co-ordinates
                        pdfContentByte.showText("Insuracnce Address");//Insurance Name and Address COmplete here  // NUMBER PHONEResponsible Party Name and Address
                        pdfContentByte.endText();

                        //BOX 39 TO 41 Value Codes and Amounts
                        //box 39

                        int xValC = 0;
                        int xAmt = 0;
                        int yValC = 0;
                        int ValCodCount = 1;
                        Query = "Select IFNULL(Code,''), IFNULL(Amount,0.0) from " + Database + ".ClaimInfoCodeValueCode where  " +
                                "ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (ValCodCount == 1) {
                                xValC = 322;
                                xAmt = 398;
                                yValC = 625;
                            } else if (ValCodCount == 2) {
                                xValC = 322;
                                xAmt = 398;
                                yValC = 613;
                            } else if (ValCodCount == 3) {
                                xValC = 322;
                                xAmt = 398;
                                yValC = 601;
                            } else if (ValCodCount == 4) {
                                xValC = 322;
                                xAmt = 398;
                                yValC = 590;
                            } else if (ValCodCount == 5) {
                                xValC = 415;
                                xAmt = 492;
                                yValC = 625;
                            } else if (ValCodCount == 6) {
                                xValC = 415;
                                xAmt = 492;
                                yValC = 613;
                            } else if (ValCodCount == 7) {
                                xValC = 415;
                                xAmt = 492;
                                yValC = 601;
                            } else if (ValCodCount == 8) {
                                xValC = 415;
                                xAmt = 492;
                                yValC = 590;
                            } else if (ValCodCount == 9) {
                                xValC = 510;
                                xAmt = 585;
                                yValC = 625;
                            } else if (ValCodCount == 10) {
                                xValC = 510;
                                xAmt = 585;
                                yValC = 613;
                            } else if (ValCodCount == 11) {
                                xValC = 510;
                                xAmt = 585;
                                yValC = 601;
                            } else if (ValCodCount == 12) {
                                xValC = 510;
                                xAmt = 585;
                                yValC = 590;
                            } else {
                                break;
                            }
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xValC, yValC); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // Value Code 39--a
                            pdfContentByte.endText();
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", rset.getDouble(2)), xAmt, yValC, 0);//Value Amount 39--a
//                pdfContentByte.setTextMatrix(345, 625); // set x and y co-ordinates
//                pdfContentByte.showText("200.00"); // Value Amount 39--a
                            pdfContentByte.endText();
                            ValCodCount++;
                        }
                        rset.close();
                        stmt.close();

                        int ChrgesCount = 22;
                        int y = 565;
                        Query = "Select IFNULL(a.DescriptionFrom,'1'),IFNULL(DATE_FORMAT(a.ServiceDate,'%m%d%y'),''), IFNULL(a.HCPCS,''), " +
                                " IFNULL(a.Units,''), IFNULL(a.Amount,''), IFNULL(a.RevCode,''), IFNULL(b.ShortDescription,'') " +
                                //"CASE WHEN  a.DescrptionFrom = 1 THEN b.ShortDescription WHEN a.DescriptionFrom = 2 THEN c.Category" +
                                " from " + Database + ".ClaimChargesInfo a " +
                                " LEFT JOIN oe." + ChargeMasterTableName + " b on a.HCPCS = b.CPTCode " +
                                " where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                                " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(15, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(6)); // RevCode--a
                            pdfContentByte.endText();

                            //box 43---a Description
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(48, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(7).length() < 27 ? rset.getString(7) : rset.getString(7).substring(0, 27)); // DEscription--a //length should be 27 substring 0,27
                            pdfContentByte.endText();

                            //box 44---a HCPCS CPT CODE
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(230, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(3)); // HCPCS CPT Code--a
                            pdfContentByte.endText();

                            //box 45---a Service Date
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(335, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(2)); // Service Date--a
                            pdfContentByte.endText();

                            //box 46---a Service UNITS
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.valueOf(rset.getDouble(4)), 438, y, 0);
                            pdfContentByte.endText();

                            //box 47---a Total Charges
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", rset.getDouble(5)), 490, y, 0);
                            pdfContentByte.endText();

                            y = y - 12;
                            TotalChargeAmount += rset.getDouble(5);

                        }


                        //box 40----23
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 301); // set x and y co-ordinates
                        pdfContentByte.showText(""); // REV CODE END--a total
                        pdfContentByte.endText();

                        //Pages no 1 of 1
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(80, 301); // set x and y co-ordinates
                        pdfContentByte.showText("1"); //
                        pdfContentByte.endText();

                        //Pages no 1 of 1
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(125, 301); // set x and y co-ordinates
                        pdfContentByte.showText("1"); //
                        pdfContentByte.endText();

                        //box Creatioon Date
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(335, 301); // set x and y co-ordinates
                        pdfContentByte.showText(CreationDate); //CreationDate
                        pdfContentByte.endText();

                        //box TOTAL SUM of ALL CHARGES
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", TotalChargeAmount), 490, 301, 0);
                        pdfContentByte.endText();

                        //box 48 LAST ROW TOTAL SUM OF non covered Charges
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, "0.0", 565, 301, 0);
                        pdfContentByte.endText();

                        //box 50
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(13, 278); // set x and y co-ordinates
                        pdfContentByte.showText(PriInsuranceName.length() < 26 ? PriInsuranceName : PriInsuranceName.substring(0, 26)); // PAYER NAME
                        pdfContentByte.endText();

                        //box 51
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(180, 278); // set x and y co-ordinates
                        pdfContentByte.showText("FED TAX NO"); // health plan ID or FED TAX NO
                        pdfContentByte.endText();

                        //box 52
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(288, 278); // set x and y co-ordinates
                        pdfContentByte.showText(ReleaseInfoAddInfo); // RELEASE INFO Y or N
                        pdfContentByte.endText();

                        //box 53
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(310, 278); // set x and y co-ordinates
                        pdfContentByte.showText(AssofBenifitAddInfo); // Assignet of benifits Y or N
                        pdfContentByte.endText();

                        //box 54
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, "", 378, 278, 0); //PRIOR PAYMENT DONT KNOW
                        pdfContentByte.endText();

                        //box 55
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", TotalChargeAmount), 457, 278, 0); //ESTimated Amout due
                        pdfContentByte.endText();

                        //box 56
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(500, 290); // set x and y co-ordinates
                        pdfContentByte.showText(ClientNPI); // NPI Hospital or may be DRs here
                        pdfContentByte.endText();

                        //box 57
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(500, 278); // set x and y co-ordinates
                        pdfContentByte.showText("FED TAX No"); // Other provider IDs// FED TAX NO
                        pdfContentByte.endText();

                        //box 58
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(15, 230); // set x and y co-ordinates
                        pdfContentByte.showText(PriInsuredName); // Inserer NAME Insured's Name
                        pdfContentByte.endText();

                        //box 59
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(200, 230); // set x and y co-ordinates
                        pdfContentByte.showText("19"); // Patient’s Relation to the Insured
                        pdfContentByte.endText();

                        //box 60
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(225, 230); // set x and y co-ordinates
                        pdfContentByte.showText(MemId); // INSURED's Policy No Mem ID
                        pdfContentByte.endText();

                        //box 61
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(369, 230); // set x and y co-ordinates
                        pdfContentByte.showText(""); // INSURANCE Group Name
                        pdfContentByte.endText();

                        //box 62
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(475, 230); // set x and y co-ordinates
                        pdfContentByte.showText(GrpNumber); // Insurance Group No
                        pdfContentByte.endText();

                        //box 64
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(235, 180); // set x and y co-ordinates
                        pdfContentByte.showText("020211255008D340X0"); // Document COntrol Number
                        pdfContentByte.endText();

                        //box 66
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(12, 135); // set x and y co-ordinates
                        pdfContentByte.showText("0"); // Diagnosis/Procedure Code Qualifier
                        pdfContentByte.endText();

                        //box 67
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(25, 145); // set x and y co-ordinates
                        pdfContentByte.showText(PrincipalDiagInfoCodes); // Principal Diagnosis Code/Other Diagnosis
                        pdfContentByte.endText();

                        int xohtDia = 0;
                        int yothDia = 0;
                        int OthDiaCount = 1;
                        Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeOthDiag where " +
                                " ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (OthDiaCount == 1) {
                                xohtDia = 75;
                                yothDia = 145;
                            } else if (OthDiaCount == 2) {
                                xohtDia = 130;
                                yothDia = 145;
                            } else if (OthDiaCount == 3) {
                                xohtDia = 180;
                                yothDia = 145;
                            } else if (OthDiaCount == 4) {
                                xohtDia = 245;
                                yothDia = 145;
                            } else if (OthDiaCount == 5) {
                                xohtDia = 305;
                                yothDia = 145;
                            } else if (OthDiaCount == 6) {
                                xohtDia = 360;
                                yothDia = 145;
                            } else if (OthDiaCount == 7) {
                                xohtDia = 425;
                                yothDia = 145;
                            } else if (OthDiaCount == 8) {
                                xohtDia = 470;
                                yothDia = 145;
                            } else if (OthDiaCount == 9) {
                                xohtDia = 25;
                                yothDia = 133;
                            } else if (OthDiaCount == 10) {
                                xohtDia = 75;
                                yothDia = 133;
                            } else if (OthDiaCount == 11) {
                                xohtDia = 130;
                                yothDia = 133;
                            } else if (OthDiaCount == 12) {
                                xohtDia = 180;
                                yothDia = 133;
                            } else if (OthDiaCount == 13) {
                                xohtDia = 245;
                                yothDia = 133;
                            } else if (OthDiaCount == 14) {
                                xohtDia = 305;
                                yothDia = 133;
                            } else if (OthDiaCount == 15) {
                                xohtDia = 360;
                                yothDia = 133;
                            } else if (OthDiaCount == 16) {
                                xohtDia = 425;
                                yothDia = 133;
                            } else if (OthDiaCount == 17) {
                                xohtDia = 470;
                                yothDia = 133;
                            } else {
                                break;
                            }

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xohtDia, yothDia); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // 67-----A---Principal Diagnosis Code/Other Diagnosis
                            pdfContentByte.endText();

                            OthDiaCount++;

                        }
                        rset.close();
                        stmt.close();

                        //box 69
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(40, 122); // set x and y co-ordinates
                        pdfContentByte.showText(AdmittingDiagInfoCodes); // Admitting Diagnosis
                        pdfContentByte.endText();

                        int CountR = 1;
                        int xR = 135;
                        Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeReasVisit where Status = 0 " +
                                " and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (CountR == 1)
                                xR = 135;
                            else if (CountR == 2)
                                xR = 180;
                            else if (CountR == 3)
                                xR = 240;
                            //box 70-----A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xR, 122); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // 70----A Reason visit Code
                            pdfContentByte.endText();

//                            //box 70-----B
//                            pdfContentByte.beginText();
//                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                            pdfContentByte.setColorFill(BaseColor.BLACK);
//                            pdfContentByte.setTextMatrix(x, 122); // set x and y co-ordinates
//                            pdfContentByte.showText("A0.00"); // 70----B Reason visit Code
//                            pdfContentByte.endText();
//
//                            //box 70-----C
//                            pdfContentByte.beginText();
//                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                            pdfContentByte.setColorFill(BaseColor.BLACK);
//                            pdfContentByte.setTextMatrix(x, 122); // set x and y co-ordinates
//                            pdfContentByte.showText("A0.00"); // 70----C Reason visit Code
//                            pdfContentByte.endText();
                            CountR++;
                            if (CountR > 3)
                                break;
                        }
                        rset.close();
                        stmt.close();

                        int CountE = 1;
                        int xE = 360;
                        Query = "Select IFNULL(Code,'') from " + Database + ".ClaimInfoCodeExtCauseInj where Status = 0 " +
                                " and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (CountE == 1)
                                xE = 360;
                            else if (CountE == 2)
                                xE = 415;
                            else if (CountE == 3)
                                xE = 470;
                            //box 72-----A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(xE, 122); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(1)); // 72----A External Cause Injury COdes
                            pdfContentByte.endText();

//                            //box 72-----B
//                            pdfContentByte.beginText();
//                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                            pdfContentByte.setColorFill(BaseColor.BLACK);
//                            pdfContentByte.setTextMatrix(415, 122); // set x and y co-ordinates
//                            pdfContentByte.showText(""); // 72----B External Cause Injury COdes
//                            pdfContentByte.endText();
//
//                            //box 72-----C
//                            pdfContentByte.beginText();
//                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                            pdfContentByte.setColorFill(BaseColor.BLACK);
//                            pdfContentByte.setTextMatrix(470, 122); // set x and y co-ordinates
//                            pdfContentByte.showText(""); // 72---C External Cause Injury COdes
//                            pdfContentByte.endText();
                            CountE++;
                            if (CountE > 3)
                                break;
                        }
                        rset.close();
                        stmt.close();


                        //box 76 ATTNDING PROVIDER
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(435, 110); // set x and y co-ordinates
                        pdfContentByte.showText(AttendingProviderNPI); // 76 NPI Attending Provider
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(390, 98); // set x and y co-ordinates
                        pdfContentByte.showText(AttendingProviderLastName); // 76 LAst Name Attending Provider
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(520, 98); // set x and y co-ordinates
                        pdfContentByte.showText(AttendingProviderFirstName); // 76 First Name Attending Provider
                        pdfContentByte.endText();

                        //box 77 OPERATING PROVIDER
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(435, 85); // set x and y co-ordinates
                        pdfContentByte.showText(OperatingProviderNPI); // 77 NPI OPERATING Provider
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(390, 73); // set x and y co-ordinates
                        pdfContentByte.showText(OperatingProviderLastName); // 77 LAst Name OPERATING Provider
                        pdfContentByte.endText();
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(520, 73); // set x and y co-ordinates
                        pdfContentByte.showText(OperatingProviderFirstName); // 77 First Name OPERATING Provider
                        pdfContentByte.endText();

                    }

                }
                pdfStamper.close(); //close pdfStamper
                final File pdfFile = new File(outputFilePath);
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + PatientName + PatientRegId + "_" + ".pdf");
                response.setContentLength((int) pdfFile.length());
                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
                final OutputStream responseOutputStream = (OutputStream) response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
            }


//            Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            try {
                out.println(e.getMessage() + Query);
                String str = "";
                for (i = 0; i < e.getStackTrace().length; ++i) {
                    str = str + e.getStackTrace()[i] + "<br>";
                }
                out.println(str);
//                helper.SendEmailWithAttachment("Error in Claim Saving ** (AddInfo Main Catch^^" + ClientId , context, e, "AddInfo", "SaveClaim", conn);
//                Services.DumException("SaveClaim^^" + ClientId + " Error in Save Claim Method", "AddInfo ", request, e);
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("FormName", "PatientUpdateInfo");
//                Parser.SetField("ActionID", "GetInput&ID="+PatientRegId);
//                Parser.SetField("Message", "MES#014");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            } catch (Exception ex) {
            }
        }
    }

    private boolean isValidRevCode(Connection conn, String Code) throws SQLException {
        boolean isValid = false;
        ResultSet rset = null;
        PreparedStatement ps = conn.prepareStatement("SELECT Count(*) FROM ClaimMasterDB.RevenueCode WHERE RevCode=?");
        ps.setString(1, Code);
        rset = ps.executeQuery();
        if (rset.next()) {
            if (rset.getInt(1) == 1) {
                isValid = true;
            }
        }
        rset.close();
        ps.close();
        return isValid;
    }
}
