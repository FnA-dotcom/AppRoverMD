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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class CopyClaim extends HttpServlet {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";

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
        String UserIndex = "";
        String DirectoryName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();

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
            this.Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = " + FacilityIndex;
            this.stmt = this.conn.createStatement();
            this.rset = this.stmt.executeQuery(this.Query);
            while (this.rset.next()) {
                DirectoryName = this.rset.getString(2);
            }
            this.rset.close();
            this.stmt.close();
            if (ActionID.compareTo("CopyInstoProf") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Copy Claim from Ins to Prof", "Copy Claim", FacilityIndex);
                ConvertInstoProf(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("CopyProftoInst") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Copy Claim from Prof to Ins", "Copy Claim", FacilityIndex);
                ConvertProftoInst(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("ChkClaim") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Copy Claim from Ins to Prof", "Copy Claim", FacilityIndex);
                ChkClaim(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, DirectoryName, response);
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

    public void ConvertInstoProf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

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
        String SpProgCodeAddInfo = "";

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
        StringBuilder _SpProgCodeAddInfo = new StringBuilder();
        StringBuilder _DocumentationMethodAddInfo = new StringBuilder();
        StringBuilder _DocumentationTypeAddInfo = new StringBuilder();
        StringBuilder _ServAuthExcepAddInfo = new StringBuilder();
        StringBuilder _TransportReasonCodeInfoCode = new StringBuilder();
        StringBuilder TimeHours = new StringBuilder();
        StringBuilder AttendingProvider = new StringBuilder();
        StringBuilder RenderingProvider = new StringBuilder();
        StringBuilder FreqList = new StringBuilder();
        StringBuilder SupervisingProvider = new StringBuilder();
        StringBuilder OrderingProvider = new StringBuilder();
        StringBuilder BillingProvider = new StringBuilder();
        StringBuilder ChargeList = new StringBuilder();
        StringBuilder _DescriptionFrom = new StringBuilder();
        StringBuilder _AdmissionHourAddInfo = new StringBuilder();
        StringBuilder _DischargeHourAddInfo = new StringBuilder();
        StringBuilder _EmploymentStatusAddInfo = new StringBuilder();
        StringBuilder _AutoAccidentAddInfo = new StringBuilder();
        StringBuilder _OtherAccidentAddInfo = new StringBuilder();
        StringBuilder _PatientSignOnFile = new StringBuilder();
        StringBuilder _InsuredSignOnFile = new StringBuilder();
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
        StringBuilder _PatHomeboundAddInfo = new StringBuilder();
        StringBuilder _AmbClaimInfoInfo = new StringBuilder();

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
        String _RenderingProvider = "";
        String _SupervisingProvider = "";
        String _OrderingProvider = "";
        String _Frequency = "";
        String _BillingProvider = "";
        String _ChargeOption = "";
        String ICDA = "";
        String ICDB = "";
        String ICDC = "";
        String ICDD = "";
        String ICDE = "";
        String ICDF = "";
        String ICDG = "";
        String ICDH = "";
        String ICDI = "";
        String ICDJ = "";
        String ICDK = "";
        String ICDL = "";
        String _PolicyType = "";
        String _SecondaryInsuranceMemId = "";
        String _SecondaryInsuranceGrpNumber = "";
        String _OperatingProvider = "";
        String _CreatedDate = "";
        String TransportReasonCode = "";
        String DelayReasonCodeAddInfo = "";
        String EmploymentStatusAddInfo = "";
        String AutoAccidentAddInfo = "";
        String OtherAccidentAddInfo = "";
        String AutoAccident_StateAddInfo = "";
        String PatHomeboundAddInfo = "";
        String ProvAccAssigAddInfo = "";
        String AutoAccStateDive = "";
        String POAInfoCodes = "";
        String AmbClaimInfoInfo = "";
        String AccidentIllnesDateAddInfo = "";
        String LastMenstrualPeriodDateAddInfo = "";
        String InitialTreatDateAddInfo = "";
        String LastSeenDateAddInfo = "";
        String UnabletoWorkFromDateAddInfo = "";
        String UnabletoWorkToDateAddInfo = "";
        String ClaimCodesAddinfo = "";
        String OtherClaimIDAddinfo = "";
        String ClaimNoteAddinfo = "";
        String ResubmitReasonCodeAddinfo = "";
        String HospitalizedFromDateAddInfo = "";
        String HospitalizedToDateAddInfo = "";
        String LabChargesAddInfo = "";
        String SpecialProgCodeAddInfo = "";
        String PatientSignOnFileAddInfo = "";
        String InsuredSignOnFileAddInfo = "";
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

        try {

            note = "Open Add info and load pdf";

            Query = "Select IFNULL(ChargeMasterTableName,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ChargeMasterTableName = rset.getString(1);
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
            while (rset.next()) {
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
                        " IFNULL(a.SecondryInsurance,''), IFNULL(c.PayerName,'') " +
                        " from " + Database + ".InsuranceInfo a " +
                        " LEFT JOIN oe_2.ProfessionalPayers b on a.PriInsuranceName = b.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers c on a.SecondryInsurance = c.Id " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    //PriInsuranceName = rset.getString(1);
                    GrpNumber = rset.getString(2);
                    MemId = rset.getString(3);
                    //PriInsuranceNameId = rset.getString(4);
//                    SecondryInsuranceId = rset.getString(5);
//                    SecondryInsurance = rset.getString(6);
                }
                rset.close();
                stmt.close();
            }

            Query1 = "SELECT dbname,IFNULL(FullName,'') FROM oe.clients WHERE id=" + ClientId;
            stmt = conn.createStatement();
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                Facilityname = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                    " and VisitId = " + VisitId + " and ClaimType = 1";// + ClaimType;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundClaim = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundClaim > 0) {
                Query = "Select ClaimNumber from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                        " and VisitId = " + VisitId + " and ClaimType = 1";// + ClaimType;
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

                Query = "Select a.Id,IFNULL(a.RefNumber,''), IFNULL(a.TypeBillText,''),IFNULL(a.UploadDate,''), IFNULL(a.AttendingProvider,''), " +
                        " IFNULL(a.BillingProviders,''), IFNULL(a.PolicyType,''), " +
                        " IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(a.SecondaryInsuranceGrpNumber,''), " +
                        " IFNULL(a.OperatingProvider,''), DATE_FORMAT(a.CreatedDate,'%m%d%y'), IFNULL(a.RenderingProvider,''), " +
                        " IFNULL(a.SupervisingProvider,''), IFNULL(a.OrderingProvider,''), IFNULL(a.Freq,''), IFNULL(a.PriInsuranceNameId,''), " +
                        " IFNULL(a.SecondaryInsuranceId,''), IFNULL(b.PayerName,''), IFNULL(c.PayerName,''), IFNULL(a.GrpNumber,'') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN oe.AvailityClearHousePayerList b on a.PriInsuranceNameId = b.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList c on a.SecondaryInsuranceId= c.Id" +
                        " where a.Status = 0 and a.ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    _RefNum = rset.getString(2);
                    _TypeBillText = rset.getString(3);
                    _UploadDate = rset.getString(4);
                    _AttendingProvider = rset.getString(5);
                    _BillingProvider = rset.getString(6);
                    _PolicyType = rset.getString(7);
                    _SecondaryInsuranceMemId = rset.getString(8);
                    _SecondaryInsuranceGrpNumber = rset.getString(9);
                    _OperatingProvider = rset.getString(10);
                    _CreatedDate = rset.getString(11);
                    _RenderingProvider = rset.getString(12);
                    _SupervisingProvider = rset.getString(13);
                    _OrderingProvider = rset.getString(14);
                    _Frequency = rset.getString(15);
                    PriInsuranceNameId = rset.getString(16);
                    SecondryInsuranceId = rset.getString(17);
                    PriInsuranceName = rset.getString(18);
                    SecondryInsurance = rset.getString(19);
                    if (GrpNumber.equals("")) {
                        GrpNumber = rset.getString(20);
                    }
                }
                rset.close();
                stmt.close();

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                RenderingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (_RenderingProvider.equals(rset.getString(1)))
                        RenderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        RenderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
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


                Query = "Select IFNULL(a.ICDA,''), IFNULL(ICDB,''), IFNULL(a.ICDC,''),IFNULL(a.ICDD,''), IFNULL(a.ICDE,''), IFNULL(a.ICDF,''), IFNULL(a.ICDG,''), " +
                        "IFNULL(a.ICDH,''), IFNULL(a.ICDI,''), IFNULL(a.ICDJ,''), IFNULL(a.ICDK,''), IFNULL(a.ICDL,''), IFNULL(a.ChargeOption,''), " +
                        "IFNULL(a.ServiceFromDate,''),IFNULL(a.ServiceToDate,''), IFNULL(a.HCPCSProcedure,''), IFNULL(a.POS,''), IFNULL(a.TOS,''), IFNULL(a.Mod1,''), " +
                        "IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), IFNULL(a.Mod4,''), IFNULL(a.DXPointer,''), IFNULL(a.UnitPrice,''), IFNULL(a.Units,''), IFNULL(a.Amount,''), " +
                        "IFNULL(a.ChargesStatus,''), IFNULL(b.descname,'') " +
                        "from " + Database + ".ClaimChargesInfo a " +
                        "LEFT JOIN oe.claim_status_list b on a.ChargesStatus = b.Id " +
                        " where a.Status = 0 and a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and a.ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    ICDA = rset.getString(1);
                    ICDB = rset.getString(2);
                    ICDC = rset.getString(3);
                    ICDD = rset.getString(4);
                    ICDE = rset.getString(5);
                    ICDF = rset.getString(6);
                    ICDG = rset.getString(7);
                    ICDH = rset.getString(8);
                    ICDI = rset.getString(9);
                    ICDJ = rset.getString(10);
                    ICDK = rset.getString(11);
                    ICDL = rset.getString(12);
                    _ChargeOption = rset.getString(13);

                    ChargeList.append("<tr>");
                    ChargeList.append("<td>" + rset.getString(14) + "</td>");//serviceFromDAte
                    ChargeList.append("<td>" + rset.getString(15) + "</td>");//ServiceToDate
                    ChargeList.append("<td>" + rset.getString(16) + "</td>");
                    ChargeList.append("<td>" + rset.getString(17) + "</td>");
                    ChargeList.append("<td>" + rset.getString(18) + "</td>");
                    ChargeList.append("<td>" + rset.getString(19) + "</td>");
                    ChargeList.append("<td>" + rset.getString(20) + "</td>");
                    ChargeList.append("<td>" + rset.getString(21) + "</td>");
                    ChargeList.append("<td>" + rset.getString(22) + "</td>");
                    ChargeList.append("<td>" + rset.getString(23) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(24) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(25) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(26) + "</td>");
                    ChargeList.append("<td>" + rset.getString(28) + "</td>");
                    ChargeList.append("<td style=\"display:none;\">" + rset.getString(27) + "</td>");
                    ChargeList.append("<td><button type='button' class='btn btn-danger btn-xs' onclick='deleteCharge(this)'><span class='glyphicon glyphicon-trash'></span></button></td>");
                    ChargeList.append("</tr>");
                    ChargesCount++;
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(OtherAccidentAddInfo,''), " +
                        "IFNULL(AutoAccident_StateAddInfo,''), IFNULL(AccidentIllnesDateAddInfo,''), " +
                        "IFNULL(LastMenstrualPeriodDateAddInfo,''), IFNULL(InitialTreatDateAddInfo,''), IFNULL(LastSeenDateAddInfo,''), IFNULL(UnabletoWorkFromDateAddInfo,'')," +
                        "IFNULL(UnabletoWorkToDateAddInfo,''), IFNULL(PatHomeboundAddInfo,''), IFNULL(ClaimCodesAddinfo,''), IFNULL(OtherClaimIDAddinfo,''), " +
                        "IFNULL(ClaimNoteAddinfo,''), IFNULL(ResubmitReasonCodeAddinfo,''), IFNULL(HospitalizedFromDateAddInfo,''), IFNULL(HospitalizedToDateAddInfo,''), " +
                        "IFNULL(LabChargesAddInfo,''), IFNULL(SpecialProgCodeAddInfo,''), IFNULL(PatientSignOnFileAddInfo,''), IFNULL(InsuredSignOnFileAddInfo,''), " +
                        " IFNULL(ProvAccAssigAddInfo,''), IFNULL(PXCTaxQualiAddInfo,''), IFNULL(DocumentationMethodAddInfo,''), IFNULL(DocumentationTypeAddInfo,''), " +
                        "IFNULL(PatientHeightAddInfo,''), IFNULL(PatientWeightAddInfo,''), IFNULL(ServAuthExcepAddInfo,''), IFNULL(DemoProjectAddInfo,''), " +
                        "IFNULL(MemmoCertAddInfo,''), IFNULL(InvDevExempAddInfo,''), IFNULL(AmbPatGrpAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'') " +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        "and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    EmploymentStatusAddInfo = rset.getString(1);
                    AutoAccidentAddInfo = rset.getString(2);
                    OtherAccidentAddInfo = rset.getString(3);
                    AutoAccident_StateAddInfo = rset.getString(4);
                    AccidentIllnesDateAddInfo = rset.getString(5);
                    LastMenstrualPeriodDateAddInfo = rset.getString(6);
                    InitialTreatDateAddInfo = rset.getString(7);
                    LastSeenDateAddInfo = rset.getString(8);
                    UnabletoWorkFromDateAddInfo = rset.getString(9);
                    UnabletoWorkToDateAddInfo = rset.getString(10);
                    PatHomeboundAddInfo = rset.getString(11);
                    ClaimCodesAddinfo = rset.getString(12);
                    OtherClaimIDAddinfo = rset.getString(13);
                    ClaimNoteAddinfo = rset.getString(14);
                    ResubmitReasonCodeAddinfo = rset.getString(15);
                    HospitalizedFromDateAddInfo = rset.getString(16);
                    HospitalizedToDateAddInfo = rset.getString(17);
                    LabChargesAddInfo = rset.getString(18);
                    SpecialProgCodeAddInfo = rset.getString(19);
                    PatientSignOnFileAddInfo = rset.getString(20);
                    InsuredSignOnFileAddInfo = rset.getString(21);
                    ProvAccAssigAddInfo = rset.getString(22);
                    PXCTaxQualiAddInfo = rset.getString(23);
                    DocumentationMethodAddInfo = rset.getString(24);
                    DocumentationTypeAddInfo = rset.getString(25);
                    PatientHeightAddInfo = rset.getString(26);
                    PatientWeightAddInfo = rset.getString(27);
                    ServAuthExcepAddInfo = rset.getString(28);
                    DemoProjectAddInfo = rset.getString(29);
                    MemmoCertAddInfo = rset.getString(30);
                    InvDevExempAddInfo = rset.getString(31);
                    AmbPatGrpAddInfo = rset.getString(32);
                    DelayReasonCodeAddInfo = rset.getString(33);
                }
                rset.close();
                stmt.close();

                Query = "Select IFNULL(AmbClaimInfoCodes,''),IFNULL(TranReasonInfoCodes,''), IFNULL(TranMilesInfoCodes,''),IFNULL(PatWeightInfoCodes,''), " +
                        "IFNULL(RoundTripReasInfoCodes,''), IFNULL(StretReasonInfoCodes,''), IFNULL(PickUpAddressInfoCode,''), IFNULL(PickUpCityInfoCode,''), " +
                        " IFNULL(PickUpStateInfoCode,''), IFNULL(PickUpZipCodeInfoCode,''), IFNULL(DropoffAddressInfoCode,''), IFNULL(DropoffCityInfoCode,'')," +
                        "IFNULL(DropoffStateInfoCode,''), IFNULL(DropoffZipCodeInfoCode,''), IFNULL(PatAdmitHosChk,''), IFNULL(PatMoveStretChk,''), " +
                        "IFNULL(PatUnconShockChk,''), IFNULL(PatTransEmerSituaChk,''), IFNULL(PatPhyRestrainChk,''), IFNULL(PatvisiblehemorrChk,''), " +
                        "IFNULL(AmbSerNeccChk,''), IFNULL(PatconfbedchairChk,'') " +
                        "from " + Database + ".ClaimAmbulanceCodes where status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    AmbClaimInfoCodes = rset.getString(1);
                    TranReasonInfoCodes = rset.getString(2);
                    TranMilesInfoCodes = rset.getString(3);
                    PatWeightInfoCodes = rset.getString(4);
                    RoundTripReasInfoCodes = rset.getString(5);
                    StretReasonInfoCodes = rset.getString(6);
                    PickUpAddressInfoCode = rset.getString(7);
                    PickUpCityInfoCode = rset.getString(8);
                    PickUpStateInfoCode = rset.getString(9);
                    PickUpZipCodeInfoCode = rset.getString(10);
                    DropoffAddressInfoCode = rset.getString(11);
                    DropoffCityInfoCode = rset.getString(12);
                    DropoffStateInfoCode = rset.getString(13);
                    DropoffZipCodeInfoCode = rset.getString(14);
                    PatAdmitHosChk = rset.getString(15);
                    PatMoveStretChk = rset.getString(16);
                    PatUnconShockChk = rset.getString(17);
                    PatTransEmerSituaChk = rset.getString(18);
                    PatPhyRestrainChk = rset.getString(19);
                    PatvisiblehemorrChk = rset.getString(20);
                    AmbSerNeccChk = rset.getString(21);
                    PatconfbedchairChk = rset.getString(22);
                }
                rset.close();
                stmt.close();

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
            } else {


                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
                stmt = conn.createStatement();
                RenderingProvider.append("<option class=Inner value=\"\"></option>");
                for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                    if (DoctorId == rset.getInt(1))
                        RenderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(2) + "</option>");
                    else
                        RenderingProvider.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
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

            }

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

            Query1 = "SELECT FreqId,Frequency FROM oe.FrequencyList where Status = 1";
            stmt = conn.createStatement();
//            FreqList.append("<option class=Inner value=\"\">Select Any</option>");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                if (_Frequency.equals(rset.getString(1)))
                    FreqList.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(1) + " - " + rset.getString(2) + "</option>");
                else
                    FreqList.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(1) + " - " + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

            Query = "Select Id, IFNULL(UPPER(ChargeOption),'') from oe.ChargeOption where status = 1";
            stmt = conn.createStatement();
            for (rset = stmt.executeQuery(Query); rset.next(); ) {
                if (_ChargeOption.equals(rset.getString(1)))
                    ChargeOption.append("<option class=Inner value=\"" + rset.getString(1) + "\" selected>" + rset.getString(1) + " - " + rset.getString(2) + "</option>");
                else
                    ChargeOption.append("<option class=Inner value=\"" + rset.getString(1) + "\">" + rset.getString(1) + " - " + rset.getString(2) + "</option>");
            }
            rset.close();
            stmt.close();

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


            Query = "Select SpProgCode, Description from oe.SpecialProgCode where Status = 1";
            hstmt = conn.createStatement();
            _SpProgCodeAddInfo.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (SpecialProgCodeAddInfo.equals(hrset.getString(1)))
                    _SpProgCodeAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    _SpProgCodeAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            Query = "Select Id, DocumentationMethod from oe.DocumentationMethod where Status = 1";
            hstmt = conn.createStatement();
            _DocumentationMethodAddInfo.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (DocumentationMethodAddInfo.equals(hrset.getString(1)))
                    _DocumentationMethodAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    _DocumentationMethodAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            Query = "Select DocTypeId, Description from oe.DocumentationType where Status = 1";
            hstmt = conn.createStatement();
            _DocumentationTypeAddInfo.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (DocumentationTypeAddInfo.equals(hrset.getString(1)))
                    _DocumentationTypeAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    _DocumentationTypeAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            Query = "Select ServAuthExcepCode, Description from oe.ServiceAuthExcepCode where Status = 1";
            hstmt = conn.createStatement();
            _ServAuthExcepAddInfo.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (ServAuthExcepAddInfo.equals(hrset.getString(1)))
                    _ServAuthExcepAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    _ServAuthExcepAddInfo.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");

            }
            hrset.close();
            hstmt.close();

            Query = "Select TransportReasCode, Description from oe.TransportReasonCode where Status = 1";
            hstmt = conn.createStatement();
            _TransportReasonCodeInfoCode.append("<option class=Inner value=\"\">Select Any</option>");
            for (hrset = hstmt.executeQuery(Query); hrset.next(); ) {
                if (TranReasonInfoCodes.equals(hrset.getString(1)))
                    _TransportReasonCodeInfoCode.append("<option class=Inner value=\"" + hrset.getString(1) + "\" selected>" + hrset.getString(2) + "</option>");
                else
                    _TransportReasonCodeInfoCode.append("<option class=Inner value=\"" + hrset.getString(1) + "\">" + hrset.getString(2) + "</option>");
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

            Set SetPatHomebound = HMYesNo.entrySet();
            Iterator ItrPatHomebound = SetPatHomebound.iterator();
            _PatHomeboundAddInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrPatHomebound.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrPatHomebound.next();
                if (entry.getKey().equals((PatHomeboundAddInfo))) {
                    _PatHomeboundAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _PatHomeboundAddInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }


            HashMap<String, String> HMRelInfo = new HashMap<String, String>();
            HMRelInfo.put("IC", "Informed Consent ");
            HMRelInfo.put("Y", "YES");
            Set SetRelInfo = HMRelInfo.entrySet();
            Iterator ItrRelInfo = SetRelInfo.iterator();
            _PatientSignOnFile.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrRelInfo.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrRelInfo.next();
                if (entry.getKey().equals(PatientSignOnFileAddInfo)) {
                    _PatientSignOnFile.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _PatientSignOnFile.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            HashMap<String, String> HMAssBen = new HashMap<String, String>();
            HMAssBen.put("Y", "YES");
            HMAssBen.put("N", "NO");
            HMAssBen.put("PR", "Patient Refuses");
            Set SetAssBen = HMAssBen.entrySet();
            Iterator ItrSetAssBen = SetAssBen.iterator();
            _InsuredSignOnFile.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrSetAssBen.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrSetAssBen.next();
                if (entry.getKey().equals(InsuredSignOnFileAddInfo)) {
                    _InsuredSignOnFile.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _InsuredSignOnFile.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
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


            Set SetAmbClaim = HMYesNo.entrySet();
            Iterator ItrAmbClaim = SetAmbClaim.iterator();
            _AmbClaimInfoInfo.append("<option class=Inner value=''>SELECT ANY</option>");
            while (ItrAmbClaim.hasNext()) {
                Map.Entry entry = (Map.Entry) ItrAmbClaim.next();
                if (entry.getKey().equals((AmbClaimInfoCodes))) {
                    _AmbClaimInfoInfo.append("<option class=Inner value=\"" + entry.getKey() + "\" selected >" + entry.getValue().toString() + "</option>");
                } else {
                    _AmbClaimInfoInfo.append("<option class=Inner value=\"" + entry.getKey() + "\"  >" + entry.getValue().toString() + "</option>");
                }
            }

            PatAdmitHosChk = CheckBox(PatAdmitHosChk);
            PatMoveStretChk = CheckBox(PatMoveStretChk);
            PatUnconShockChk = CheckBox(PatUnconShockChk);
            PatTransEmerSituaChk = CheckBox(PatTransEmerSituaChk);
            PatPhyRestrainChk = CheckBox(PatPhyRestrainChk);
            PatvisiblehemorrChk = CheckBox(PatvisiblehemorrChk);
            AmbSerNeccChk = CheckBox(AmbSerNeccChk);
            PatconfbedchairChk = CheckBox(PatconfbedchairChk);

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
            Parser.SetField("_UploadDate", String.valueOf(_UploadDate));
            Parser.SetField("RenderingProvider", String.valueOf(RenderingProvider));
            Parser.SetField("BillingProvider", String.valueOf(BillingProvider));
            Parser.SetField("SupervisingProvider", String.valueOf(SupervisingProvider));
            Parser.SetField("OrderingProvider", String.valueOf(OrderingProvider));
            Parser.SetField("FreqList", String.valueOf(FreqList));
            Parser.SetField("_PolicyType", String.valueOf(_PolicyType));
            Parser.SetField("_SecondaryInsuranceMemId", String.valueOf(_SecondaryInsuranceMemId));
            Parser.SetField("_SecondaryInsuranceGrpNumber", String.valueOf(_SecondaryInsuranceGrpNumber));
            Parser.SetField("_OperatingProvider", String.valueOf(_OperatingProvider));
            Parser.SetField("_CreatedDate", String.valueOf(_CreatedDate));
            Parser.SetField("ChargeOption", String.valueOf(ChargeOption));
            Parser.SetField("ICDA", String.valueOf(ICDA));
            Parser.SetField("ICDB", String.valueOf(ICDB));
            Parser.SetField("ICDC", String.valueOf(ICDC));
            Parser.SetField("ICDD", String.valueOf(ICDD));
            Parser.SetField("ICDE", String.valueOf(ICDE));
            Parser.SetField("ICDF", String.valueOf(ICDF));
            Parser.SetField("ICDG", String.valueOf(ICDG));
            Parser.SetField("ICDH", String.valueOf(ICDH));
            Parser.SetField("ICDI", String.valueOf(ICDI));
            Parser.SetField("ICDJ", String.valueOf(ICDJ));
            Parser.SetField("ICDK", String.valueOf(ICDK));
            Parser.SetField("ICDL", String.valueOf(ICDL));
            Parser.SetField("ChargeList", String.valueOf(ChargeList));
            Parser.SetField("_DescriptionFrom", String.valueOf(_DescriptionFrom));
            Parser.SetField("ChargesCount", String.valueOf(ChargesCount + " Charges"));
            Parser.SetField("AutoAccident_StateAddInfo", String.valueOf(AutoAccident_StateAddInfo));
            Parser.SetField("_OtherAccidentAddInfo", String.valueOf(_OtherAccidentAddInfo));
            Parser.SetField("_EmploymentStatusAddInfo", String.valueOf(_EmploymentStatusAddInfo));
            Parser.SetField("_AutoAccidentAddInfo", String.valueOf(_AutoAccidentAddInfo));
            Parser.SetField("AutoAccStateDive", String.valueOf(AutoAccStateDive));
            Parser.SetField("_ProvAccAssigAddInfo", String.valueOf(_ProvAccAssigAddInfo));
            Parser.SetField("_AssofBenifitAddInfo", String.valueOf(_AssofBenifitAddInfo));
            Parser.SetField("_PatHomeboundAddInfo", String.valueOf(_PatHomeboundAddInfo));
            Parser.SetField("_SpProgCodeAddInfo", String.valueOf(_SpProgCodeAddInfo));
            Parser.SetField("_PatientSignOnFile", String.valueOf(_PatientSignOnFile));
            Parser.SetField("_InsuredSignOnFile", String.valueOf(_InsuredSignOnFile));
            Parser.SetField("_DocumentationMethodAddInfo", String.valueOf(_DocumentationMethodAddInfo));
            Parser.SetField("_DocumentationTypeAddInfo", String.valueOf(_DocumentationTypeAddInfo));
            Parser.SetField("_ServAuthExcepAddInfo", String.valueOf(_ServAuthExcepAddInfo));
            Parser.SetField("AccidentIllnesDateAddInfo", String.valueOf(AccidentIllnesDateAddInfo));
            Parser.SetField("LastMenstrualPeriodDateAddInfo", String.valueOf(LastMenstrualPeriodDateAddInfo));
            Parser.SetField("InitialTreatDateAddInfo", String.valueOf(InitialTreatDateAddInfo));
            Parser.SetField("LastSeenDateAddInfo", String.valueOf(LastSeenDateAddInfo));
            Parser.SetField("UnabletoWorkFromDateAddInfo", String.valueOf(UnabletoWorkFromDateAddInfo));
            Parser.SetField("UnabletoWorkToDateAddInfo", String.valueOf(UnabletoWorkToDateAddInfo));
            Parser.SetField("ClaimCodesAddinfo", String.valueOf(ClaimCodesAddinfo));
            Parser.SetField("OtherClaimIDAddinfo", String.valueOf(OtherClaimIDAddinfo));
            Parser.SetField("ClaimNoteAddinfo", String.valueOf(ClaimNoteAddinfo));
            Parser.SetField("ResubmitReasonCodeAddinfo", String.valueOf(ResubmitReasonCodeAddinfo));
            Parser.SetField("HospitalizedFromDateAddInfo", String.valueOf(HospitalizedFromDateAddInfo));
            Parser.SetField("HospitalizedToDateAddInfo", String.valueOf(HospitalizedToDateAddInfo));
            Parser.SetField("LabChargesAddInfo", String.valueOf(LabChargesAddInfo));
            Parser.SetField("PXCTaxQualiAddInfo", String.valueOf(PXCTaxQualiAddInfo));
            Parser.SetField("PatientHeightAddInfo", String.valueOf(PatientHeightAddInfo));
            Parser.SetField("PatientWeightAddInfo", String.valueOf(PatientWeightAddInfo));
            Parser.SetField("DemoProjectAddInfo", String.valueOf(DemoProjectAddInfo));
            Parser.SetField("MemmoCertAddInfo", String.valueOf(MemmoCertAddInfo));
            Parser.SetField("InvDevExempAddInfo", String.valueOf(InvDevExempAddInfo));
            Parser.SetField("AmbPatGrpAddInfo", String.valueOf(AmbPatGrpAddInfo));
            Parser.SetField("TranMilesInfoCodes", String.valueOf(TranMilesInfoCodes));
            Parser.SetField("PatWeightInfoCodes", String.valueOf(PatWeightInfoCodes));
            Parser.SetField("RoundTripReasInfoCodes", String.valueOf(RoundTripReasInfoCodes));
            Parser.SetField("StretReasonInfoCodes", String.valueOf(StretReasonInfoCodes));
            Parser.SetField("PickUpAddressInfoCode", String.valueOf(PickUpAddressInfoCode));
            Parser.SetField("PickUpCityInfoCode", String.valueOf(PickUpCityInfoCode));
            Parser.SetField("PickUpStateInfoCode", String.valueOf(PickUpStateInfoCode));
            Parser.SetField("PickUpZipCodeInfoCode", String.valueOf(PickUpZipCodeInfoCode));
            Parser.SetField("DropoffAddressInfoCode", String.valueOf(DropoffAddressInfoCode));
            Parser.SetField("DropoffCityInfoCode", String.valueOf(DropoffCityInfoCode));
            Parser.SetField("DropoffStateInfoCode", String.valueOf(DropoffStateInfoCode));
            Parser.SetField("DropoffZipCodeInfoCode", String.valueOf(DropoffZipCodeInfoCode));
            Parser.SetField("PatAdmitHosChk", String.valueOf(PatAdmitHosChk));
            Parser.SetField("PatMoveStretChk", String.valueOf(PatMoveStretChk));
            Parser.SetField("PatUnconShockChk", String.valueOf(PatUnconShockChk));
            Parser.SetField("PatTransEmerSituaChk", String.valueOf(PatTransEmerSituaChk));
            Parser.SetField("PatPhyRestrainChk", String.valueOf(PatPhyRestrainChk));
            Parser.SetField("PatvisiblehemorrChk", String.valueOf(PatvisiblehemorrChk));
            Parser.SetField("AmbSerNeccChk", String.valueOf(AmbSerNeccChk));
            Parser.SetField("PatconfbedchairChk", String.valueOf(PatconfbedchairChk));
            Parser.SetField("_TransportReasonCodeInfoCode", String.valueOf(_TransportReasonCodeInfoCode));
            Parser.SetField("_AmbClaimInfoInfo", String.valueOf(_AmbClaimInfoInfo));
            Parser.SetField("FoundClaim", String.valueOf(0));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));

            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Reports/AddinfoProf.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage() + Query);
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

//    public void ConvertInstoProf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response){
//        int FoundClaim = 0;
//        int PatientRegId = 0;
//        int VisitId = 0;
//        int OldClaimInfoMasterId = 0;
//        int ClaimInfoMasterId = 0;
//        String ClaimNo = "";
//        String ClaimType = "2";
//        String Head = "";
//        String ClientIP = helper.getClientIp(request);
//        OldClaimInfoMasterId = Integer.parseInt(request.getParameter("ClaimInfoMasterId").trim());
//        PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
//        VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
//
//        try {
//            Query = "SELECT SUBSTRING(IFNULL(MAX(Convert(Substring(ClaimNumber,4,8) ,UNSIGNED INTEGER)),0)+10000001,2,7) " +
//                    "FROM " + Database + ".ClaimInfoMaster";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                ClaimNo = rset.getString(1);
//            }
//            rset.close();
//            stmt.close();
//
//            if (ClaimNo.equals("0000001")) {
//                ClaimNo = "0001081";
//            }
//            if (ClaimType.equals("1")) {
//                Head = "Institutional";
//                ClaimNo = "CI-" + ClaimNo;
//            } else if (ClaimType.equals("2")) {
//                Head = "Professional";
//                ClaimNo = "CP-" + ClaimNo;
//            } else {
//                Head = "Claim";
//                ClaimNo = "C-" + ClaimNo;
//            }
//
//            try {
//                Query = "Select IFNULL(a.RefNumber,''), IFNULL(a.Freq,''), IFNULL(a.PatientName,''), IFNULL(a.PatientMRN,''), IFNULL(a.AcctNo,''), IFNULL(a.PhNumber,''), " +
//                        "IFNULL(a.Email,''), IFNULL(a.Address,''), IFNULL(a.DOS,''), IFNULL(a.UplaodDate,''), IFNULL(a.RenderingProvider,''), IFNULL(a.BillingProvider,''), " +
//                        "IFNULL(a.SupervisingProvider,''), IFNULL(a.OrderingProvider,''), IFNULL(a.ClientName,''), IFNULL(a.PriInsuranceNameId,''), IFNULL(a.MemId,''), " +
//                        "IFNULL(a.Policytype,''), IFNULL(a.GrpNumber,''), IFNULL(a.SecondaryInsuranceId,''), IFNULL(a.SecondaryInsuranceMemId,''), IFNULL(SecondaryInsuranceGrpNumber,'') " +
//                        " FROM " + Database + ".ClaimInfoMaster a " +
//                        " WHERE a.Status = 0 and a.Id = " + OldClaimInfoMasterId;
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoMaster (ClientId,PatientRegId,VisitId ,"
//                            + " ClaimNumber,RefNumber,Freq,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,RenderingProvider,BillingProviders," +
//                            " SupervisingProvider,OrderingProvider," +
//                            " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
//                            " CreatedBy,CreatedIP,Status,ClaimType) \n"
//                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?) ");
//                    MainReceipt.setInt(1, ClientId);
//                    MainReceipt.setInt(2, PatientRegId);
//                    MainReceipt.setInt(3, VisitId);
//                    MainReceipt.setString(4, ClaimNo);
//                    MainReceipt.setString(5, rset.getString(1));
//                    MainReceipt.setString(6, rset.getString(2));
//                    MainReceipt.setString(7, rset.getString(3));
//                    MainReceipt.setString(8, rset.getString(4));
//                    MainReceipt.setString(9, rset.getString(5));
//                    MainReceipt.setString(10, rset.getString(6));
//                    MainReceipt.setString(11, rset.getString(7));
//                    MainReceipt.setString(12, rset.getString(8));
//                    MainReceipt.setString(13, rset.getString(9));
//                    MainReceipt.setString(14, rset.getString(10));
//                    MainReceipt.setString(15, rset.getString(11));
//                    MainReceipt.setString(16, rset.getString(12));
//                    MainReceipt.setString(17, rset.getString(13));
//                    MainReceipt.setString(18, rset.getString(14));
//                    MainReceipt.setString(19, rset.getString(15));
//                    MainReceipt.setString(20, rset.getString(16));
//                    MainReceipt.setString(21, rset.getString(17));
//                    MainReceipt.setString(22, rset.getString(18));
//                    MainReceipt.setString(23, rset.getString(19));
//                    MainReceipt.setString(24, rset.getString(20));
//                    MainReceipt.setString(25, rset.getString(21));
//                    MainReceipt.setString(26, rset.getString(22));
//                    MainReceipt.setString(27, UserId);
//                    MainReceipt.setString(28, ClientIP);
//                    MainReceipt.setInt(29, Integer.parseInt(ClaimType));
//                    MainReceipt.executeUpdate();
//                    MainReceipt.close();
//
//                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoMaster_history (ClientId,PatientRegId,VisitId ,"
//                            + " ClaimNumber,RefNumber,Freq,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,RenderingProvider,BillingProviders," +
//                            " SupervisingProvider,OrderingProvider," +
//                            " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
//                            " CreatedBy,CreatedIP,Status,ClaimType) \n"
//                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?) ");
//                    MainReceipt2.setInt(1, ClientId);
//                    MainReceipt2.setInt(2, PatientRegId);
//                    MainReceipt2.setInt(3, VisitId);
//                    MainReceipt2.setString(4, ClaimNo);
//                    MainReceipt2.setString(5, rset.getString(1));
//                    MainReceipt2.setString(6, rset.getString(2));
//                    MainReceipt2.setString(7, rset.getString(3));
//                    MainReceipt2.setString(8, rset.getString(4));
//                    MainReceipt2.setString(9, rset.getString(5));
//                    MainReceipt2.setString(10, rset.getString(6));
//                    MainReceipt2.setString(11, rset.getString(7));
//                    MainReceipt2.setString(12, rset.getString(8));
//                    MainReceipt2.setString(13, rset.getString(9));
//                    MainReceipt2.setString(14, rset.getString(10));
//                    MainReceipt2.setString(15, rset.getString(11));
//                    MainReceipt2.setString(16, rset.getString(12));
//                    MainReceipt2.setString(17, rset.getString(13));
//                    MainReceipt2.setString(18, rset.getString(14));
//                    MainReceipt2.setString(19, rset.getString(15));
//                    MainReceipt2.setString(20, rset.getString(16));
//                    MainReceipt2.setString(21, rset.getString(17));
//                    MainReceipt2.setString(22, rset.getString(18));
//                    MainReceipt2.setString(23, rset.getString(19));
//                    MainReceipt2.setString(24, rset.getString(20));
//                    MainReceipt2.setString(25, rset.getString(21));
//                    MainReceipt2.setString(26, rset.getString(22));
//                    MainReceipt2.setString(27, UserId);
//                    MainReceipt2.setString(28, ClientIP);
//                    MainReceipt2.setInt(29, Integer.parseInt(ClaimType));
//                    MainReceipt2.executeUpdate();
//                    MainReceipt2.close();
//                }
//                rset.close();
//                stmt.close();
//            }catch(Exception e){
//                out.println("Error Insertion in ClaimInfoMasterTable --NO SP" + e.getMessage());
//                Services.DumException("CopyClaim", "Insertion in ClaimInfoMasterTable --NO SP", request, e, this.getServletContext());
////            }
//
//            try {
//                Query = "Select max(Id) from " + Database + ".ClaimInfoMaster ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next())
//                    ClaimInfoMasterId = rset.getInt(1);
//                rset.close();
//                stmt.close();
//
//            } catch (Exception Ex) {
//                out.println("Error Getting Info from ClaimInfoMasterTable --NO SP" + Ex.getMessage());
//                Services.DumException("CopyClaim", "Error Getting Info from ClaimInfoMasterTable --NO SP", request, Ex, this.getServletContext());
//            }
//
//
//        }catch(Exception e) {
//            e.getMessage();
//        }
//    }

    public void ConvertProftoInst(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

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
        try {

            note = "Open Add info and load pdf";

            Query = "Select IFNULL(ChargeMasterTableName,'') from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ChargeMasterTableName = rset.getString(1);
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
            while (rset.next()) {
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
                        " IFNULL(a.SecondryInsurance,''), IFNULL(c.PayerName,'') " +
                        " from " + Database + ".InsuranceInfo a " +
                        " LEFT JOIN oe_2.ProfessionalPayers b on a.PriInsuranceName = b.Id " +
                        " LEFT JOIN oe_2.ProfessionalPayers c on a.SecondryInsurance = c.Id " +
                        " where PatientRegId = " + PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    //PriInsuranceName = rset.getString(1);
                    GrpNumber = rset.getString(2);
                    MemId = rset.getString(3);
                    //PriInsuranceNameId = rset.getString(4);
                    //SecondryInsuranceId = rset.getString(5);
                    //SecondryInsurance = rset.getString(6);
                }
                rset.close();
                stmt.close();
            }

            Query1 = "SELECT dbname,IFNULL(FullName,'') FROM oe.clients WHERE id=" + ClientId;
            stmt = conn.createStatement();
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                Facilityname = rset.getString(2);
            }
            rset.close();
            stmt.close();


            Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                    " and VisitId = " + VisitId + " and ClaimType = 2";// + ClaimType;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                FoundClaim = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (FoundClaim > 0) {
                Query = "Select ClaimNumber from " + Database + ".ClaimInfoMaster where Status = 0 and PatientRegId = " + PatientRegId + " " +
                        " and VisitId = " + VisitId + " and ClaimType = 2 order by Id desc ";
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
                        " IFNULL(b.PayerName,''), IFNULL(c.PayerName,''), IFNULL(a.GrpNumber,'') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN oe.AvailityClearHousePayerList b on a.PriInsuranceNameId = b.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList c on a.SecondaryInsuranceId = c.Id  " +
                        " where a.Status = 0 and a.ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ClaimInfoMasterId = rset.getInt(1);
                    _RefNum = rset.getString(2);
                    _TypeBillText = rset.getString(3);
                    _UploadDate = rset.getString(4);
                    _AttendingProvider = rset.getString(5);
                    _BillingProvider = rset.getString(6);
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

                Query1 = "SELECT id,CONCAT(DoctorsLastName,', ',DoctorsFirstName) FROM " + Database + ".DoctorsList";
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

                Query = "Select IFNULL(a.DescriptionFrom,''), IFNULL(a.ServiceDate,''), IFNULL(a.HCPCS,''), " +
                        "IFNULL(a.Mod1,''), IFNULL(a.Mod2,''), IFNULL(a.Mod3,''), IFNULL(a.Mod4,''), IFNULL(a.RevCode,''), " +
                        "CASE WHEN a.DescriptionFrom = 1 THEN b.ShortDescription WHEN a.DescriptionFrom = 0 THEN c.Description ELSE c.Description END, " +
                        "IFNULL(a.UnitPrice,'0'), IFNULL(a.Units,'0'), IFNULL(a.Amount,'0.0'), IFNULL(d.descname,'') " +
                        "from " + Database + ".ClaimChargesInfo a " +
                        "LEFT JOIN oe." + ChargeMasterTableName + " b on a.HCPCS = b.CPTCode " +
                        "LEFT JOIN oe.RevenueCode c on a.RevCode = c.Codes " +
                        "LEFT JOIN oe.claim_status_list d on a.ChargesStatus = d.Id " +
                        "where a.Status = 0 and a.ClaimInfoMasterId = " + ClaimInfoMasterId + " and a.ClaimNumber = '" + ClaimNo + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    DescriptionFrom = rset.getString(1);
                    ChargeList.append("<tr>");
                    ChargeList.append("<td>" + rset.getString(2) + "</td>");
                    ChargeList.append("<td>" + rset.getString(3) + "</td>");
                    ChargeList.append("<td>" + rset.getString(4) + "</td>");
                    ChargeList.append("<td>" + rset.getString(5) + "</td>");
                    ChargeList.append("<td>" + rset.getString(6) + "</td>");
                    ChargeList.append("<td>" + rset.getString(7) + "</td>");
                    ChargeList.append("<td>" + rset.getString(8) + "</td>");
                    ChargeList.append("<td>" + rset.getString(9) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(10) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(11) + "</td>");
                    ChargeList.append("<td>" + rset.getDouble(12) + "</td>");
                    ChargeList.append("<td>" + rset.getString(13) + "</td>");
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

            } else {


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
            //hrset.close();
            //hstmt.close();

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
            Parser.SetField("BillingProvider", String.valueOf(BillingProvider));
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
            Parser.SetField("FoundClaim", String.valueOf(0));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));


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

    public void ChkClaim(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        int FoundClaim = 0;
        int PatientRegId = 0;
        int VisitId = 0;
        int ClaimType = 0;
        String ClaimNumber = "";
        PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        ClaimType = Integer.parseInt(request.getParameter("ClaimType").trim());
        try {
            this.Query = " Select COUNT(*) from " + Database + ".ClaimInfoMaster  where Status = 0 and PatientRegId = " + PatientRegId + " and VisitId = " + VisitId + " and ClaimType = " + ClaimType;
            this.stmt = conn.createStatement();
            this.rset = this.stmt.executeQuery(this.Query);
            if (this.rset.next())
                FoundClaim = this.rset.getInt(1);
            this.rset.close();
            this.stmt.close();
            if (FoundClaim > 0) {
                out.println("1");
            } else {
                out.println("404");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private String CheckStringVariable(HttpServletRequest request, String VariableName) {
        try {
            if (request.getParameter(VariableName).length() < 1) {
                VariableName = "";
            } else {
                VariableName = request.getParameter(VariableName).trim();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "VariableName" + VariableName);
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


}