package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class AddInfo31JAN2022 extends HttpServlet {

/*    private Connection conn = null;
    private   Statement stmt = null;
    private   ResultSet rset = null;
    private   String Query = "";
    */

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
            while (rset.next()) {
                DirectoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            if (ActionID.equals("Addinfo")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Add Info Insititutional Claim Screen", "Open Insititutional Claim Screen", FacilityIndex);
                Addinfo(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("AddinfoProf")) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Add Info Professional Claim Screen", "Open Professional Claim Screen", FacilityIndex);
                AddinfoProf(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetInsuranceDetails") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Insurance Details", "Getting Professional Payer List", FacilityIndex);
                GetInsuranceDetails(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetCPTCodesTable") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get CPT Codes table", "Getting CPT CODE List", FacilityIndex);
                GetCPTCodesTable(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetModTable") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Modifier Codes table", "Getting Modifier CODE List", FacilityIndex);
                GetModTable(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetRevCodeTable") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Revenue Codes table", "Getting Revenue CODE List", FacilityIndex);
                GetRevCodeTable(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetCodeDetails") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get CPT Codes Details ", "Getting CPT Code Details When Types or Input in text fiels", FacilityIndex);
                GetCodeDetails(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetDiagnosisCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Diagnosis Codes Details ", "Getting Diagnosis Code Details When Types or Input in text fiels", FacilityIndex);
                GetDiagnosisCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetProcedureCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Procedure Codes Details ", "Getting Procedure Code Details When Types or Input in text fiels", FacilityIndex);
                GetProcedureCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetOccuranceSpanCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Occurance Span Codes Details ", "Getting Occurance Span Code Details When Types or Input in text fiels", FacilityIndex);
                GetOccuranceSpanCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetOccuranceCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get OCcurance Codes Details ", "Getting OCcurance Code Details When Types or Input in text fiels", FacilityIndex);
                GetOccuranceCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetValueCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Value Codes Details ", "Getting Value Code Details When Types or Input in text fiels", FacilityIndex);
                GetValueCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetConditionCode") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get Condition Codes Details ", "Getting Condition Code Details When Types or Input in text fiels", FacilityIndex);
                GetConditionCode(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetPOSCodeTable") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get POS Codes Details ", "Getting POS Code Details When Types or Input in text fiels", FacilityIndex);
                GetPOSCodeTable(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("GetTOSCodeTable") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Get TOS Codes Details ", "Getting TOS Code Details When Types or Input in text fiels", FacilityIndex);
                GetTOSCodeTable(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("SaveClaim") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Save Claim", "Saving all fields in the claim section", FacilityIndex);
                SaveClaim(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("SaveClaimProf") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Save Claim Professional", "Saving all fields in the claim section", FacilityIndex);
                SaveClaimProf(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.compareTo("OpenUB04") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Open UB04", "OpenUB04 Form and Filling", FacilityIndex);
                OpenUB04(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else if (ActionID.compareTo("OpenCMS1500") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "Open UB04", "OpenUB04 Form and Filling", FacilityIndex);
                OpenCMS1500(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else if (ActionID.compareTo("Institutional_EDIFile") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "EDI File Formation", "EDI File Making", FacilityIndex);
                Institutional_EDIFile(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
            } else if (ActionID.compareTo("Professional_EDIFile") == 0) {
                supp.Dologing(UserIndex, conn, request.getRemoteAddr(), ActionID, "EDI File Formation", "EDI File Making", FacilityIndex);
                Professional_EDIFile(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, helper, DirectoryName, response);
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

    /*public void GetClaims(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

        int Found = 0;
        String Link = "";
        StringBuilder ClaimList = new StringBuilder();
        int VisitId = Integer.parseInt(request.getParameter("VisitId").trim());
        int PatientRegId = Integer.parseInt(request.getParameter("PatientRegId").trim());
        String AcctNo = request.getParameter("AcctNo").trim();
        try {
            Query = "Select COUNT(*) from " + Database + ".ClaimInfoMaster where VisitId = " + VisitId + " and PatientRegId = " + PatientRegId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if(Found > 0){
                Query = "Select IFNULL(a.ClaimNumber,''), IFNULL(a.PatientName,''), IFNULL(a.DOS,''), IFNULL(b.ClaimProgress,''), " +
                        "CASE WHEN a.ReadytoSubmit = 1 THEN 'READY TO SUBMIT' ELSE 'INCOMPLETE' END, " +
                        "CASE WHEN a.ClaimType = 1 THEN 'Institutional' WHEN a.ClaimType = 2 THEN 'Professional' ELSE 'CLAIM' END, " +
                        " IFNULL(a.ClaimType,''), IFNULL(DATE_FORMAT(CreatedDate,'%m/%d/%Y %T'),'') from "+Database+".ClaimInfoMaster a " +
                        " LEFT JOIN ClaimProgress b on a.ClaimProgress = b.Id " +
                        " where a.VisitId = "+VisitId +" and a.PatientRegId = "+PatientRegId;
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()){
                    if(rset.getString(7).equals("1")){
                        Link = "/md/md.AddInfo?ActionID=Addinfo&VisitId="+VisitId+"&PatientRegId="+PatientRegId+"&AcctNo="+AcctNo+"&ClaimType=1";
                    }else{
                        Link = "/md/md.AddInfo?ActionID=AddinfoProf&VisitId="+VisitId+"&PatientRegId="+PatientRegId+"&AcctNo="+AcctNo+"&ClaimType=2";
                    }
                    ClaimList.append("<tr data-href='"+Link+"' >");
                    ClaimList.append("<td>"+rset.getString(1)+"</td>");
                    ClaimList.append("<td>"+rset.getString(2)+"</td>");
                    ClaimList.append("<td>"+rset.getString(3)+"</td>");
                    ClaimList.append("<td>"+rset.getString(5)+"</td>");
                    ClaimList.append("<td>"+rset.getString(4)+"</td>");
                    ClaimList.append("<td>"+rset.getString(6)+"</td>");
                    ClaimList.append("<td>"+rset.getString(8)+"</td>");
                    ClaimList.append("</tr>");
                }
                rset.close();
                stmt.close();

            }

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("VisitId", String.valueOf(VisitId));
            Parser.SetField("PatientRegId", String.valueOf(PatientRegId));
            Parser.SetField("AcctNo", String.valueOf(AcctNo));
            Parser.SetField("ClaimList", String.valueOf(ClaimList));
            Parser.SetField("Found", String.valueOf(Found));
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/ClaimsInfo.html");

        }catch(Exception e){
            out.println(e.getMessage());
        }


    }*/

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
            Parser.SetField("FoundClaim", String.valueOf(FoundClaim));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));


            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage() + Query);
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    public void AddinfoProf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {

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
                        " and VisitId = " + VisitId + " and ClaimType = " + ClaimType;
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
            Parser.SetField("FoundClaim", String.valueOf(FoundClaim));
            Parser.SetField("ClaimInfoMasterId", String.valueOf(ClaimInfoMasterId));

            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/AddinfoProf.html");
        } catch (Exception e) {
            out.println(aa + " Unable to process the request..." + e.getMessage() + Query);
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            out.println(str);
        }
    }

    public void GetInsuranceDetails(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer Insurancelist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String PayerSearchBox = request.getParameter("PayerSearchBox").trim();
        String InsuranceTxtBoxId = request.getParameter("InsuranceTxtBoxId").trim();
        try {


            Query1 = "SELECT PayerName,PayerID,Id FROM oe.AvailityClearHousePayerList where Status = 0 and PayerName like '%" + PayerSearchBox + "%' OR PayerID like '%" + PayerSearchBox + "%' ";
            stmt = conn.createStatement();
            Insurancelist.append("<table id=\"PrimaryInsuranceTable\" class=\"table table-bordered table-striped\">");
            Insurancelist.append("<thead>");
            Insurancelist.append("<tr>");
            Insurancelist.append("<th>Payer Name</th>");
            Insurancelist.append("<th>Payer ID</th>");
            Insurancelist.append("</tr>");
            Insurancelist.append("</thead>");
            Insurancelist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                Insurancelist.append("<tr onclick=\"GetInsuranceDetails(`" + rset.getString(1) + "`, `" + InsuranceTxtBoxId + "`, `" + rset.getInt(3) + "`)\">");
                Insurancelist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                Insurancelist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                Insurancelist.append("</tr>");
            }
            rset.close();
            stmt.close();
            Insurancelist.append("</tbody>");
            Insurancelist.append("</table>");

            out.println(Insurancelist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetCPTCodesTable(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer CPTCodelist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Query1 = "";
        String ChargeTableName = "";

        String txtBoxId = request.getParameter("txtId").trim();
        try {

            Query = "Select ChargeMasterTableName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ChargeTableName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query1 = "Select CPTCode , ShortDescription , Price from oe." + ChargeTableName;
            stmt = conn.createStatement();
            CPTCodelist.append("<table id=\"CPTCodeTable\" class=\"table table-bordered table-striped\">");
            CPTCodelist.append("<thead>");
            CPTCodelist.append("<tr>");
            CPTCodelist.append("<th>CPT Code</th>");
            CPTCodelist.append("<th>Description</th>");
            CPTCodelist.append("<th>Amount</th>");
            CPTCodelist.append("</tr>");
            CPTCodelist.append("</thead>");
            CPTCodelist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                CPTCodelist.append("<tr onclick=\"GetCode(`" + rset.getString(1) + "`, `" + txtBoxId + "`, `" + rset.getString(2) + "`, `" + rset.getDouble(3) + "`)\">");
                CPTCodelist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                CPTCodelist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                CPTCodelist.append("<td align='left' >" + rset.getString(3) + "</td>\n");
                CPTCodelist.append("</tr>");
            }
            rset.close();
            stmt.close();
            CPTCodelist.append("</tbody>");
            CPTCodelist.append("</table>");

            out.println(CPTCodelist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetModTable(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer Modlist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String ChargeTableName = "";
        String Query = "";
        String txtBoxId = request.getParameter("txtId").trim();
        try {

            Query1 = "Select Code , Description from oe.ModifierCodes where Status = 1";
            stmt = conn.createStatement();
            Modlist.append("<table id=\"ModCodeTable\" class=\"table table-bordered table-striped\">");
            Modlist.append("<thead>");
            Modlist.append("<tr>");
            Modlist.append("<th>Mod Code</th>");
            Modlist.append("<th>Description</th>");
            Modlist.append("</tr>");
            Modlist.append("</thead>");
            Modlist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                Modlist.append("<tr onclick=\"GetMod(`" + rset.getString(1) + "`, `" + txtBoxId + "` )\">");
                Modlist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                Modlist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                Modlist.append("</tr>");
            }
            rset.close();
            stmt.close();
            Modlist.append("</tbody>");
            Modlist.append("</table>");

            out.println(Modlist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetRevCodeTable(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer RevCodelist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String ChargeTableName = "";
        String Query = "";
        String txtBoxId = request.getParameter("txtId").trim();
        try {

            Query1 = "Select IFNULL(Codes,'') , IFNULL(Price,''), IFNULL(Category,''), IFNULL(Description,'') " +
                    "from oe.RevenueCode where Status = 1";
            stmt = conn.createStatement();
            RevCodelist.append("<table id=\"RevCodeTable\" class=\"table table-bordered table-striped\">");
            RevCodelist.append("<thead>");
            RevCodelist.append("<tr>");
            RevCodelist.append("<th>Rev Code</th>");
            RevCodelist.append("<th>Price</th>");
            RevCodelist.append("<th>Category</th>");
            RevCodelist.append("<th>Description</th>");
            RevCodelist.append("</tr>");
            RevCodelist.append("</thead>");
            RevCodelist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                RevCodelist.append("<tr onclick=\"GetRevCode(`" + rset.getString(1) + "`, `" + txtBoxId + "`, `" + rset.getString(4) + "` )\">");
                RevCodelist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                RevCodelist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                RevCodelist.append("<td align='left' >" + rset.getString(3) + "</td>\n");
                RevCodelist.append("<td align='left' >" + rset.getString(4) + "</td>\n");
                RevCodelist.append("</tr>");
            }
            rset.close();
            stmt.close();
            RevCodelist.append("</tbody>");
            RevCodelist.append("</table>");

            out.println(RevCodelist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetCodeDetails(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String ChargeTableName = "";
        String CPTCodeDescription = "";
        double CPTCodePrice = 0.0;
        String Query = "";
        String CPTCodeText = request.getParameter("CPTCodeText").trim();
        System.out.println(CPTCodeText + "-----" + ClientId);
        try {

            Query = "Select ChargeMasterTableName from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ChargeTableName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "Select ShortDescription , Price from oe." + ChargeTableName + " where CPTCode = '" + CPTCodeText + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CPTCodeDescription = rset.getString(1);
                CPTCodePrice = rset.getDouble(2);
            }
            rset.close();
            stmt.close();

            if (CPTCodeDescription == null) {
                out.println("99|99");
            } else {
                out.println(CPTCodeDescription + "|" + CPTCodePrice);
            }

        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetDiagnosisCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        try {
            String DiagnosisCodesSearchBox = request.getParameter("DiagnosisCodesSearchBox").trim();
            String tabletxtboxId = request.getParameter("id").trim();
            StringBuffer DiagnosisCodesList = new StringBuffer();

            Query = "Select Id, Code, Description, Case WHEN Status = 1 THEN 'Active' ELSE 'InActive' END from oe.DiagnosisCodes where Code like '%" + DiagnosisCodesSearchBox + "%' OR Description like '%" + DiagnosisCodesSearchBox + "%'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DiagnosisCodesList.append("<table id=\"DiagnosisCodesTable\" class=\"table table-bordered table-striped\">");
            DiagnosisCodesList.append("<thead>");
            DiagnosisCodesList.append("<tr>");
            DiagnosisCodesList.append("<th>Code</th>");
            DiagnosisCodesList.append("<th>Description</th>");
            DiagnosisCodesList.append("<th>Status</th>");
            DiagnosisCodesList.append("</tr>");
            DiagnosisCodesList.append("</thead>");
            DiagnosisCodesList.append("<tbody >");
            while (rset.next()) {
                DiagnosisCodesList.append("<tr onclick=\"GetDiagnosisCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tabletxtboxId + "`)\">");
                DiagnosisCodesList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                DiagnosisCodesList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                DiagnosisCodesList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                DiagnosisCodesList.append("</tr>");
            }
            rset.close();
            stmt.close();
            DiagnosisCodesList.append("</tbody>");
            DiagnosisCodesList.append("</table>");

            out.println(DiagnosisCodesList.toString());
        } catch (Exception e) {
            out.println("Error in getting Diagnosis Codes: " + e.getMessage());
        }
    }

    public void GetProcedureCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Found = 0;
        try {
            String ProcedureCodeSearchBox = request.getParameter("ProcedureCodeSearchBox").trim();
            String tabletxtboxId = request.getParameter("id").trim();
            StringBuffer ProcedureCodesList = new StringBuffer();

            Query = "Select COUNT(*) from oe.ProcedureCodes ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found > 0) {
                Query = "Select Id, ProcedureCode, Description from oe.ProcedureCodes where ProcedureCode like '%" + ProcedureCodeSearchBox + "%' OR Description like '%" + ProcedureCodeSearchBox + "%'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                ProcedureCodesList.append("<table id=\"ProcedureCodesTable\" class=\"table table-bordered table-striped\">");
                ProcedureCodesList.append("<thead>");
                ProcedureCodesList.append("<tr>");
                ProcedureCodesList.append("<th>Code</th>");
                ProcedureCodesList.append("<th>Description</th>");
                ProcedureCodesList.append("</tr>");
                ProcedureCodesList.append("</thead>");
                ProcedureCodesList.append("<tbody >");
                while (rset.next()) {
                    ProcedureCodesList.append("<tr onclick=\"GetProcedureCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tabletxtboxId + "`)\">");
                    ProcedureCodesList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                    ProcedureCodesList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                    ProcedureCodesList.append("</tr>");
                }
                rset.close();
                stmt.close();
                ProcedureCodesList.append("</tbody>");
                ProcedureCodesList.append("</table>");

                out.println(ProcedureCodesList.toString());
            } else {
                out.println("99");
            }
        } catch (Exception e) {
            out.println("Error in getting Procedure Codes: " + e.getMessage());
        }
    }

    public void GetOccuranceSpanCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Found = 0;
        try {
            String tableId = request.getParameter("tableId").trim();
            StringBuffer OccuranceSpanCodeList = new StringBuffer();

            Query = "Select COUNT(*) from oe.OccurenceSpanCodes ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found > 0) {
                Query = "Select Id, OccurenceSpanCode, Description from oe.OccurenceSpanCodes where Status = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                OccuranceSpanCodeList.append("<table id=\"OccuranceSpanCodeTable\" class=\"table table-bordered table-striped\">");
                OccuranceSpanCodeList.append("<thead>");
                OccuranceSpanCodeList.append("<tr>");
                OccuranceSpanCodeList.append("<th>Code</th>");
                OccuranceSpanCodeList.append("<th>Description</th>");
                OccuranceSpanCodeList.append("</tr>");
                OccuranceSpanCodeList.append("</thead>");
                OccuranceSpanCodeList.append("<tbody >");
                while (rset.next()) {
                    OccuranceSpanCodeList.append("<tr onclick=\"GetOccuranceSpanCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tableId + "`)\">");
                    OccuranceSpanCodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                    OccuranceSpanCodeList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                    OccuranceSpanCodeList.append("</tr>");
                }
                rset.close();
                stmt.close();
                OccuranceSpanCodeList.append("</tbody>");
                OccuranceSpanCodeList.append("</table>");

                out.println(OccuranceSpanCodeList.toString());
            } else {
                out.println("99");
            }
        } catch (Exception e) {
            out.println("Error in getting Occurance Span Codes: " + e.getMessage());
        }
    }

    public void GetOccuranceCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Found = 0;
        try {
            String tableId = request.getParameter("tableId").trim();
            StringBuffer OccuranceCodeList = new StringBuffer();

            Query = "Select COUNT(*) from oe.OcurrenceCodes ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found > 0) {
                Query = "Select Id, OccuranceCode, Description from oe.OcurrenceCodes where Status = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                OccuranceCodeList.append("<table id=\"OccuranceCodeTable\" class=\"table table-bordered table-striped\">");
                OccuranceCodeList.append("<thead>");
                OccuranceCodeList.append("<tr>");
                OccuranceCodeList.append("<th>Code</th>");
                OccuranceCodeList.append("<th>Description</th>");
                OccuranceCodeList.append("</tr>");
                OccuranceCodeList.append("</thead>");
                OccuranceCodeList.append("<tbody >");
                while (rset.next()) {
                    OccuranceCodeList.append("<tr onclick=\"GetOccuranceCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tableId + "`)\">");
                    OccuranceCodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                    OccuranceCodeList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                    OccuranceCodeList.append("</tr>");
                }
                rset.close();
                stmt.close();
                OccuranceCodeList.append("</tbody>");
                OccuranceCodeList.append("</table>");

                out.println(OccuranceCodeList.toString());
            } else {
                out.println("99");
            }
        } catch (Exception e) {
            out.println("Error in getting Occurance Codes: " + e.getMessage());
        }
    }

    public void GetValueCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Found = 0;
        try {
            String tableId = request.getParameter("tableId").trim();
            StringBuffer ValueCodeList = new StringBuffer();

            Query = "Select COUNT(*) from oe.ValueCodes ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found > 0) {
                Query = "Select Id, ValueCode, Description from oe.ValueCodes where Status = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                ValueCodeList.append("<table id=\"ValueCodesTable\" class=\"table table-bordered table-striped\">");
                ValueCodeList.append("<thead>");
                ValueCodeList.append("<tr>");
                ValueCodeList.append("<th>Code</th>");
                ValueCodeList.append("<th>Description</th>");
                ValueCodeList.append("</tr>");
                ValueCodeList.append("</thead>");
                ValueCodeList.append("<tbody >");
                while (rset.next()) {
                    ValueCodeList.append("<tr onclick=\"GetValueCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tableId + "`)\">");
                    ValueCodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                    ValueCodeList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                    ValueCodeList.append("</tr>");
                }
                rset.close();
                stmt.close();
                ValueCodeList.append("</tbody>");
                ValueCodeList.append("</table>");

                out.println(ValueCodeList.toString());
            } else {
                out.println("99");
            }
        } catch (Exception e) {
            out.println("Error in getting Occurance Codes: " + e.getMessage());
        }
    }

    public void GetConditionCode(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int Found = 0;
        try {
            String tableId = request.getParameter("tableId").trim();
            StringBuffer ConditionCodeList = new StringBuffer();

            Query = "Select COUNT(*) from oe.ConditionCodes ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Found = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Found > 0) {
                Query = "Select Id, ConditionCode, Description from oe.ConditionCodes where Status = 1";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                ConditionCodeList.append("<table id=\"ConditionCodesTable\" class=\"table table-bordered table-striped\">");
                ConditionCodeList.append("<thead>");
                ConditionCodeList.append("<tr>");
                ConditionCodeList.append("<th>Code</th>");
                ConditionCodeList.append("<th>Description</th>");
                ConditionCodeList.append("</tr>");
                ConditionCodeList.append("</thead>");
                ConditionCodeList.append("<tbody >");
                while (rset.next()) {
                    ConditionCodeList.append("<tr onclick=\"GetConditionCode(`" + rset.getString(2) + "`,`" + rset.getString(3) + "`,`" + tableId + "`)\">");
                    ConditionCodeList.append("<td align=left >" + rset.getString(2) + "</td>\n");
                    ConditionCodeList.append("<td align=left >" + rset.getString(3) + "</td>\n");
                    ConditionCodeList.append("</tr>");
                }
                rset.close();
                stmt.close();
                ConditionCodeList.append("</tbody>");
                ConditionCodeList.append("</table>");

                out.println(ConditionCodeList.toString());
            } else {
                out.println("99");
            }
        } catch (Exception e) {
            out.println("Error in getting Occurance Codes: " + e.getMessage());
        }
    }

    public void GetPOSCodeTable(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer POSCodelist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String ChargeTableName = "";
        String Query = "";
        String txtBoxId = request.getParameter("txtId").trim();
        try {

            Query1 = "Select IFNULL(Code,'') , IFNULL(Description,'') from oe.POSCodes where Status = 1";
            stmt = conn.createStatement();
            POSCodelist.append("<table id=\"POSCodeTable\" class=\"table table-bordered table-striped\">");
            POSCodelist.append("<thead>");
            POSCodelist.append("<tr>");
            POSCodelist.append("<th>Rev Code</th>");
            POSCodelist.append("<th>Description</th>");
            POSCodelist.append("</tr>");
            POSCodelist.append("</thead>");
            POSCodelist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                POSCodelist.append("<tr onclick=\"GetPOSCode(`" + rset.getString(1) + "`, `" + txtBoxId + "`, `" + rset.getString(2) + "` )\">");
                POSCodelist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                POSCodelist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                POSCodelist.append("</tr>");
            }
            rset.close();
            stmt.close();
            POSCodelist.append("</tbody>");
            POSCodelist.append("</table>");

            out.println(POSCodelist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
        }
    }

    public void GetTOSCodeTable(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String DatabaseName, int ClientId, UtilityHelper helper) {

        StringBuffer TOSCodelist = new StringBuffer();
        Statement stmt = null;
        ResultSet rset = null;
        String Query1 = "";
        String ChargeTableName = "";
        String Query = "";
        String txtBoxId = request.getParameter("txtId").trim();
        try {

            Query1 = "Select IFNULL(Code,'') , IFNULL(Description,'') from oe.TOSCodes where Status = 1";
            stmt = conn.createStatement();
            TOSCodelist.append("<table id=\"TOSCodeTable\" class=\"table table-bordered table-striped\">");
            TOSCodelist.append("<thead>");
            TOSCodelist.append("<tr>");
            TOSCodelist.append("<th>Rev Code</th>");
            TOSCodelist.append("<th>Description</th>");
            TOSCodelist.append("</tr>");
            TOSCodelist.append("</thead>");
            TOSCodelist.append("<tbody >");
            for (rset = stmt.executeQuery(Query1); rset.next(); ) {
                TOSCodelist.append("<tr onclick=\"GetTOSCode(`" + rset.getString(1) + "`, `" + txtBoxId + "`, `" + rset.getString(2) + "` )\">");
                TOSCodelist.append("<td align='left' >" + rset.getString(1) + "</td>\n");
                TOSCodelist.append("<td align='left' >" + rset.getString(2) + "</td>\n");
                TOSCodelist.append("</tr>");
            }
            rset.close();
            stmt.close();
            TOSCodelist.append("</tbody>");
            TOSCodelist.append("</table>");

            out.println(TOSCodelist.toString());
        } catch (Exception e) {
            out.println(" Unable to process the request..." + e.getMessage());
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
            myInfoExternalCauseInjury = ExternalCauseInjuryString.split("\\^");
            String ExternalCauseInjuryInput[][] = new String[Integer.parseInt(ExternalCauseInjuryTableCount)][2];
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

            ReasonVisitTableCount = CheckStringVariable(request, "ReasonVisitTableCount");
            ReasonVisitString = CheckStringVariable(request, "ReasonVisitString");

            String[] myInfoReasonVisit;
            myInfoReasonVisit = new String[0];
            myInfoReasonVisit = ReasonVisitString.split("\\^");
            String ReasonVisitInput[][] = new String[Integer.parseInt(ReasonVisitTableCount)][2];
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

            OtherDiagnosisTableCount = CheckStringVariable(request, "OtherDiagnosisTableCount");
            OtherDiagnosisString = CheckStringVariable(request, "OtherDiagnosisString");

            String[] myInfoOtherDiagnosis;
            myInfoOtherDiagnosis = new String[0];
            myInfoOtherDiagnosis = OtherDiagnosisString.split("\\^");
            String OtherDiagnosisInput[][] = new String[Integer.parseInt(OtherDiagnosisTableCount)][3];
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

            OtherProcedureTableCount = CheckStringVariable(request, "OtherProcedureTableCount");
            OtherProcedureString = CheckStringVariable(request, "OtherProcedureString");

            String[] myInfoOtherProcedure;
            myInfoOtherProcedure = new String[0];
            myInfoOtherProcedure = OtherProcedureString.split("\\^");
            String OtherProcedureInput[][] = new String[Integer.parseInt(OtherProcedureTableCount)][2];
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

            OccurrenceSpanTableCount = CheckStringVariable(request, "OccurrenceSpanTableCount");
            OccurrenceSpanString = CheckStringVariable(request, "OccurrenceSpanString");

            String[] myInfoOccuranceSpan;
            myInfoOccuranceSpan = new String[0];
            myInfoOccuranceSpan = OccurrenceSpanString.split("\\^");
            String OccuranceSpanInput[][] = new String[Integer.parseInt(OccurrenceSpanTableCount)][3];
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

            OccurrenceTableCount = CheckStringVariable(request, "OccurrenceTableCount");
            OccurrenceString = CheckStringVariable(request, "OccurrenceString");

            String[] myInfoOccurance;
            myInfoOccurance = new String[0];
            myInfoOccurance = OccurrenceString.split("\\^");
            String OccuranceInput[][] = new String[Integer.parseInt(OccurrenceTableCount)][2];
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


            ValueCodeTableCount = CheckStringVariable(request, "ValueCodeTableCount");
            ValueCodeString = CheckStringVariable(request, "ValueCodeString");

            String[] myInfoValueCode;
            myInfoValueCode = new String[0];
            myInfoValueCode = ValueCodeString.split("\\^");
            String ValueCodeInput[][] = new String[Integer.parseInt(ValueCodeTableCount)][2];
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


            ConditionCodeTableCount = CheckStringVariable(request, "ConditionCodeTableCount");
            ConditionCodeString = CheckStringVariable(request, "ConditionCodeString");

            String[] myInfoConditionCode;
            myInfoConditionCode = new String[0];
            myInfoConditionCode = ConditionCodeString.split("\\^");
            String ConditionCodeInput[][] = new String[Integer.parseInt(ConditionCodeTableCount)][1];
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

            Query = "Select Id from " + Database + ".ClaimInfoMaster where ClaimType = '" + ClaimType + "' and VisitId = '" + VisitId + "' and PatientRegId = '" + PatientRegId + "'";
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
                    MainReceipt.setString(14, ChargesInput[i][11]);//ChargesStatus
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
            Parser.SetField("FormName", "AddInfo");
            Parser.SetField("ActionID", "OpenUB04&PatientRegId=" + PatientRegId + "&ClaimNumber=" + ClaimNumber + "&VisitId=" + VisitId);
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");


        } catch (Exception e) {
            System.out.println("Error in : " + e.getMessage());
//            String str = "";
//            for (i = 0; i < e.getStackTrace().length; ++i) {
//                str = str + e.getStackTrace()[i] + "<br>";
//            }
//            out.println(str);
            Services.DumException("AddInfo", "Error in Save Claim Method --NO SP", request, e, getServletContext());
            try {
                helper.SendEmailWithAttachment("Error in Claim Saving ** (AddInfo Main Catch^^" + ClientName, context, e, "AddInfo", "SaveClaim", conn);
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

    public void SaveClaimProf(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        boolean Found = false;
        int PatientRegId = 0;
        int VisitId = 0;
        int ClaimInfoMasterId = 0;
        int ClaimType = 0;
        int ClaimID = 0;
        //Claim Basic Info Variable
        String Freq = "";
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
        String RenderingProvider = "";
        String BillingProviders = "";
        String SupervisingProvider = "";
        String OrderingProvider = "";
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
        String ChargeOptionProf = "";
        String ChargesTableCount = "0";
        String ChargesString = "";

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

        try {
            PatientRegId = Integer.parseInt(CheckIntegerVariable(request, "PatientRegId"));
            VisitId = Integer.parseInt(CheckIntegerVariable(request, "VisitId"));
            ClaimType = Integer.parseInt(CheckIntegerVariable(request, "ClaimType"));

            //claim basic Info
            ClaimNumber = CheckStringVariable(request, "ClaimNumber");
            RefNumber = CheckStringVariable(request, "RefNumber");
            Freq = CheckStringVariable(request, "Freq");
            PatientName = CheckStringVariable(request, "PatientName");
            PatientMRN = CheckStringVariable(request, "PatientMRN");
            AcctNo = CheckStringVariable(request, "AcctNo");
            PhNumber = CheckStringVariable(request, "PhNumber");
            Email = CheckStringVariable(request, "Email");
            Address = CheckStringVariable(request, "Address");
            DOS = CheckStringVariable(request, "DOS");
            UploadDate = CheckStringVariable(request, "UploadDate");
            RenderingProvider = CheckStringVariable(request, "RenderingProvider");
            BillingProviders = CheckStringVariable(request, "BillingProviders");
            SupervisingProvider = CheckStringVariable(request, "SupervisingProvider");
            OrderingProvider = CheckStringVariable(request, "OrderingProvider");
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
            ICDA = CheckStringVariable(request, "ICDA");
            ICDB = CheckStringVariable(request, "ICDB");
            ICDC = CheckStringVariable(request, "ICDC");
            ICDD = CheckStringVariable(request, "ICDD");
            ICDE = CheckStringVariable(request, "ICDE");
            ICDF = CheckStringVariable(request, "ICDF");
            ICDG = CheckStringVariable(request, "ICDG");
            ICDH = CheckStringVariable(request, "ICDH");
            ICDI = CheckStringVariable(request, "ICDI");
            ICDJ = CheckStringVariable(request, "ICDJ");
            ICDK = CheckStringVariable(request, "ICDK");
            ICDL = CheckStringVariable(request, "ICDL");
            ChargeOptionProf = CheckStringVariable(request, "ChargeOptionProf");
            ChargesTableCount = CheckStringVariable(request, "ChargesTableCount");
            ChargesString = CheckStringVariable(request, "ChargesString");

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
            AccidentIllnesDateAddInfo = CheckStringVariable(request, "AccidentIllnesDateAddInfo");
            LastMenstrualPeriodDateAddInfo = CheckStringVariable(request, "LastMenstrualPeriodDateAddInfo");
            InitialTreatDateAddInfo = CheckStringVariable(request, "InitialTreatDateAddInfo");
            LastSeenDateAddInfo = CheckStringVariable(request, "LastSeenDateAddInfo");
            UnabletoWorkFromDateAddInfo = CheckStringVariable(request, "UnabletoWorkFromDateAddInfo");
            UnabletoWorkToDateAddInfo = CheckStringVariable(request, "UnabletoWorkToDateAddInfo");
            PatHomeboundAddInfo = CheckStringVariable(request, "PatHomeboundAddInfo");
            ClaimCodesAddinfo = CheckStringVariable(request, "ClaimCodesAddinfo");
            OtherClaimIDAddinfo = CheckStringVariable(request, "OtherClaimIDAddinfo");
            AdditionalClaimInfoAddinfo = CheckStringVariable(request, "AdditionalClaimInfoAddinfo");
            ClaimNoteAddinfo = CheckStringVariable(request, "ClaimNoteAddinfo");
            ResubmitReasonCodeAddinfo = CheckStringVariable(request, "ResubmitReasonCodeAddinfo");
            DelayReasonCodeAddInfo = CheckStringVariable(request, "DelayReasonCodeAddInfo");
            HospitalizedFromDateAddInfo = CheckStringVariable(request, "HospitalizedFromDateAddInfo");
            HospitalizedToDateAddInfo = CheckStringVariable(request, "HospitalizedToDateAddInfo");
            LabChargesAddInfo = CheckStringVariable(request, "LabChargesAddInfo");
            SpecialProgCodeAddInfo = CheckStringVariable(request, "SpecialProgCodeAddInfo");
            PatientSignOnFileAddInfo = CheckStringVariable(request, "PatientSignOnFileAddInfo");
            InsuredSignOnFileAddInfo = CheckStringVariable(request, "InsuredSignOnFileAddInfo");
            ProvAccAssigAddInfo = CheckStringVariable(request, "ProvAccAssigAddInfo");
            PXCTaxQualiAddInfo = CheckStringVariable(request, "PXCTaxQualiAddInfo");
            DocumentationMethodAddInfo = CheckStringVariable(request, "DocumentationMethodAddInfo");
            DocumentationTypeAddInfo = CheckStringVariable(request, "DocumentationTypeAddInfo");
            PatientHeightAddInfo = CheckStringVariable(request, "PatientHeightAddInfo");
            PatientWeightAddInfo = CheckStringVariable(request, "PatientWeightAddInfo");
            ServAuthExcepAddInfo = CheckStringVariable(request, "ServAuthExcepAddInfo");
            DemoProjectAddInfo = CheckStringVariable(request, "DemoProjectAddInfo");
            MemmoCertAddInfo = CheckStringVariable(request, "MemmoCertAddInfo");
            InvDevExempAddInfo = CheckStringVariable(request, "InvDevExempAddInfo");
            AmbPatGrpAddInfo = CheckStringVariable(request, "AmbPatGrpAddInfo");

            //Ambulance Info
            AmbClaimInfoCodes = CheckStringVariable(request, "AmbClaimInfoCodes");
            TranReasonInfoCodes = CheckStringVariable(request, "TranReasonInfoCodes");
            TranMilesInfoCodes = CheckStringVariable(request, "TranMilesInfoCodes");
            PatWeightInfoCodes = CheckStringVariable(request, "PatWeightInfoCodes");
            RoundTripReasInfoCodes = CheckStringVariable(request, "RoundTripReasInfoCodes");
            StretReasonInfoCodes = CheckStringVariable(request, "StretReasonInfoCodes");
            PickUpAddressInfoCode = CheckStringVariable(request, "PickUpAddressInfoCode");
            PickUpCityInfoCode = CheckStringVariable(request, "PickUpCityInfoCode");
            PickUpStateInfoCode = CheckStringVariable(request, "PickUpStateInfoCode");
            PickUpZipCodeInfoCode = CheckStringVariable(request, "PickUpZipCodeInfoCode");
            DropoffAddressInfoCode = CheckStringVariable(request, "DropoffAddressInfoCode");
            DropoffCityInfoCode = CheckStringVariable(request, "DropoffCityInfoCode");
            DropoffStateInfoCode = CheckStringVariable(request, "DropoffStateInfoCode");
            DropoffZipCodeInfoCode = CheckStringVariable(request, "DropoffZipCodeInfoCode");
            PatAdmitHosChk = CheckCheckBoxValue(request, "PatAdmitHosChk");
            PatMoveStretChk = CheckCheckBoxValue(request, "PatMoveStretChk");
            PatUnconShockChk = CheckCheckBoxValue(request, "PatUnconShockChk");
            PatTransEmerSituaChk = CheckCheckBoxValue(request, "PatTransEmerSituaChk");
            PatPhyRestrainChk = CheckCheckBoxValue(request, "PatPhyRestrainChk");
            PatvisiblehemorrChk = CheckCheckBoxValue(request, "PatvisiblehemorrChk");
            AmbSerNeccChk = CheckCheckBoxValue(request, "AmbSerNeccChk");
            PatconfbedchairChk = CheckCheckBoxValue(request, "PatconfbedchairChk");

//            out.println(PatAdmitHosChk);

            Query = "Select Id from " + Database + ".ClaimInfoMaster where ClaimType = '" + ClaimType + "' and VisitId = '" + VisitId + "' and PatientRegId = '" + PatientRegId + "'";
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
                        + " ClaimNumber,RefNumber,Freq,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,RenderingProvider,BillingProviders," +
                        " SupervisingProvider,OrderingProvider," +
                        " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
                        " CreatedBy,CreatedIP,Status,ClaimType) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?) ");
                MainReceipt.setInt(1, ClientId);
                MainReceipt.setInt(2, PatientRegId);
                MainReceipt.setInt(3, VisitId);
                MainReceipt.setString(4, ClaimNumber);
                MainReceipt.setString(5, RefNumber);
                MainReceipt.setString(6, Freq);
                MainReceipt.setString(7, PatientName);
                MainReceipt.setString(8, PatientMRN);
                MainReceipt.setString(9, AcctNo);
                MainReceipt.setString(10, PhNumber);
                MainReceipt.setString(11, Email);
                MainReceipt.setString(12, Address);
                MainReceipt.setString(13, DOS);
                MainReceipt.setString(14, UploadDate);
                MainReceipt.setString(15, RenderingProvider);
                MainReceipt.setString(16, BillingProviders);
                MainReceipt.setString(17, SupervisingProvider);
                MainReceipt.setString(18, OrderingProvider);
                MainReceipt.setString(19, ClientName);
                MainReceipt.setString(20, PriInsuranceNameId);
                MainReceipt.setString(21, MemId);
                MainReceipt.setString(22, PolicyType);
                MainReceipt.setString(23, GrpNumber);
                MainReceipt.setString(24, SecondaryInsuranceId);
                MainReceipt.setString(25, SecondaryInsuranceMemId);
                MainReceipt.setString(26, SecondaryInsuranceGrpNumber);
                MainReceipt.setString(27, UserId);
                MainReceipt.setString(28, ClientIP);
                MainReceipt.setInt(29, ClaimType);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimInfoMaster_history (ClientId,PatientRegId,VisitId ,"
                        + " ClaimNumber,RefNumber,Freq,PatientName,PatientMRN,AcctNo,PhNumber,Email,Address,DOS,UploadDate,RenderingProvider,BillingProviders," +
                        " SupervisingProvider,OrderingProvider," +
                        " ClientName,PriInsuranceNameId,MemId,PolicyType,GrpNumber,SecondaryInsuranceId,SecondaryInsuranceMemId,SecondaryInsuranceGrpNumber,CreatedDate," +
                        " CreatedBy,CreatedIP,Status,ClaimType) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,0,?) ");
                MainReceipt2.setInt(1, ClientId);
                MainReceipt2.setInt(2, PatientRegId);
                MainReceipt2.setInt(3, VisitId);
                MainReceipt2.setString(4, ClaimNumber);
                MainReceipt2.setString(5, RefNumber);
                MainReceipt2.setString(6, Freq);
                MainReceipt2.setString(7, PatientName);
                MainReceipt2.setString(8, PatientMRN);
                MainReceipt2.setString(9, AcctNo);
                MainReceipt2.setString(10, PhNumber);
                MainReceipt2.setString(11, Email);
                MainReceipt2.setString(12, Address);
                MainReceipt2.setString(13, DOS);
                MainReceipt2.setString(14, UploadDate);
                MainReceipt2.setString(15, RenderingProvider);
                MainReceipt2.setString(16, BillingProviders);
                MainReceipt2.setString(17, SupervisingProvider);
                MainReceipt2.setString(18, OrderingProvider);
                MainReceipt2.setString(19, ClientName);
                MainReceipt2.setString(20, PriInsuranceNameId);
                MainReceipt2.setString(21, MemId);
                MainReceipt2.setString(22, PolicyType);
                MainReceipt2.setString(23, GrpNumber);
                MainReceipt2.setString(24, SecondaryInsuranceId);
                MainReceipt2.setString(25, SecondaryInsuranceMemId);
                MainReceipt2.setString(26, SecondaryInsuranceGrpNumber);
                MainReceipt2.setString(27, UserId);
                MainReceipt2.setString(28, ClientIP);
                MainReceipt2.setInt(29, ClaimType);
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

                for (i = 0; i < Integer.parseInt(ChargesTableCount); i++) {
                    PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimChargesInfo (ClaimInfoMasterId,ClaimNumber,ChargeOption," +//3
                            " ICDA,ICDB,ICDC,ICDD,ICDE,ICDF,ICDG,ICDH,ICDI,ICDJ,ICDK,ICDL,ServiceFromDate,ServiceToDate,HCPCSProcedure,POS,TOS,Mod1,Mod2,Mod3,Mod4,DXPointer," +//25
                            " UnitPrice,Units,Amount,ChargesStatus,Status,CreatedDate,CreatedBy,CreatedIP) \n"//33
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt.setInt(1, ClaimInfoMasterId);
                    MainReceipt.setString(2, ClaimNumber);
                    MainReceipt.setString(3, ChargeOptionProf);
                    MainReceipt.setString(4, ICDA);
                    MainReceipt.setString(5, ICDB);
                    MainReceipt.setString(6, ICDC);
                    MainReceipt.setString(7, ICDD);
                    MainReceipt.setString(8, ICDE);
                    MainReceipt.setString(9, ICDF);
                    MainReceipt.setString(10, ICDG);
                    MainReceipt.setString(11, ICDH);
                    MainReceipt.setString(12, ICDI);
                    MainReceipt.setString(13, ICDJ);
                    MainReceipt.setString(14, ICDK);
                    MainReceipt.setString(15, ICDL);
                    MainReceipt.setString(16, ChargesInput[i][0]);//ServiceFromDate
                    MainReceipt.setString(17, ChargesInput[i][1]);//ServiceTODAte
                    MainReceipt.setString(18, ChargesInput[i][2]);//Procedure
                    MainReceipt.setString(19, ChargesInput[i][3]);//POS
                    MainReceipt.setString(20, ChargesInput[i][4]);//TOS
                    MainReceipt.setString(21, ChargesInput[i][5]);//Mod1
                    MainReceipt.setString(22, ChargesInput[i][6]);//Mod2
                    MainReceipt.setString(23, ChargesInput[i][7]);//Mod3
                    MainReceipt.setString(24, ChargesInput[i][8]);//Mod4
                    MainReceipt.setString(25, ChargesInput[i][9]);//DXPOinter
                    MainReceipt.setDouble(26, Double.parseDouble(ChargesInput[i][10]));//UnitPrice
                    MainReceipt.setDouble(27, Double.parseDouble(ChargesInput[i][11]));//Units
                    MainReceipt.setDouble(28, Double.parseDouble(ChargesInput[i][12]));//Amount
                    MainReceipt.setString(29, ChargesInput[i][13]);//ChargeStatus
                    MainReceipt.setString(30, UserId);//UserID
                    MainReceipt.setString(31, ClientIP);//ClientIP
                    MainReceipt.executeUpdate();
                    MainReceipt.close();

                    PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimChargesInfo_history (ClaimInfoMasterId,ClaimNumber,ChargeOption," +//3
                            " ICDA,ICDB,ICDC,ICDD,ICDE,ICDF,ICDG,ICDH,ICDI,ICDJ,ICDK,ICDL,ServiceFromDate,ServiceToDate,HCPCSProcedure,POS,TOS,Mod1,Mod2,Mod3,Mod4,DXPointer," +//25
                            " UnitPrice,Units,Amount,ChargesStatus,Status,CreatedDate,CreatedBy,CreatedIP) \n"//33
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                    MainReceipt2.setInt(1, ClaimInfoMasterId);
                    MainReceipt2.setString(2, ClaimNumber);
                    MainReceipt2.setString(3, ChargeOptionProf);
                    MainReceipt2.setString(4, ICDA);
                    MainReceipt2.setString(5, ICDB);
                    MainReceipt2.setString(6, ICDC);
                    MainReceipt2.setString(7, ICDD);
                    MainReceipt2.setString(8, ICDE);
                    MainReceipt2.setString(9, ICDF);
                    MainReceipt2.setString(10, ICDG);
                    MainReceipt2.setString(11, ICDH);
                    MainReceipt2.setString(12, ICDI);
                    MainReceipt2.setString(13, ICDJ);
                    MainReceipt2.setString(14, ICDK);
                    MainReceipt2.setString(15, ICDL);
                    MainReceipt2.setString(16, ChargesInput[i][0]);//ServiceFromDate
                    MainReceipt2.setString(17, ChargesInput[i][1]);//ServiceTODAte
                    MainReceipt2.setString(18, ChargesInput[i][2]);//Procedure
                    MainReceipt2.setString(19, ChargesInput[i][3]);//POS
                    MainReceipt2.setString(20, ChargesInput[i][4]);//TOS
                    MainReceipt2.setString(21, ChargesInput[i][5]);//Mod1
                    MainReceipt2.setString(22, ChargesInput[i][6]);//Mod2
                    MainReceipt2.setString(23, ChargesInput[i][7]);//Mod3
                    MainReceipt2.setString(24, ChargesInput[i][8]);//Mod4
                    MainReceipt2.setString(25, ChargesInput[i][9]);//DXPOinter
                    MainReceipt2.setDouble(26, Double.parseDouble(ChargesInput[i][10]));//UnitPrice
                    MainReceipt2.setDouble(27, Double.parseDouble(ChargesInput[i][11]));//Units
                    MainReceipt2.setDouble(28, Double.parseDouble(ChargesInput[i][12]));//Amount
                    MainReceipt2.setString(29, ChargesInput[i][13]);//ChargeStatus
                    MainReceipt2.setString(30, UserId);//UserID
                    MainReceipt2.setString(31, ClientIP);//ClientIP
                    MainReceipt2.executeUpdate();
                    MainReceipt2.close();
                }
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

                PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAdditionalInfo (ClaimInfoMasterId,ClaimNumber,EmploymentStatusAddInfo ,"
                        + " AutoAccidentAddInfo,OtherAccidentAddInfo,AutoAccident_StateAddInfo,AccidentIllnesDateAddInfo,LastMenstrualPeriodDateAddInfo,InitialTreatDateAddInfo,LastSeenDateAddInfo," +
                        " UnabletoWorkFromDateAddInfo,UnabletoWorkToDateAddInfo,PatHomeboundAddInfo,ClaimCodesAddinfo,OtherClaimIDAddinfo,ClaimNoteAddinfo," +
                        " ResubmitReasonCodeAddinfo,HospitalizedFromDateAddInfo,HospitalizedToDateAddInfo,LabChargesAddInfo,SpecialProgCodeAddInfo,PatientSignOnFileAddInfo,InsuredSignOnFileAddInfo,ProvAccAssigAddInfo, " +
                        " PXCTaxQualiAddInfo,DocumentationMethodAddInfo,DocumentationTypeAddInfo,PatientHeightAddInfo,PatientWeightAddInfo,ServAuthExcepAddInfo,DemoProjectAddInfo," +
                        " MemmoCertAddInfo,InvDevExempAddInfo,AmbPatGrpAddInfo,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                MainReceipt.setInt(1, ClaimInfoMasterId);
                MainReceipt.setString(2, ClaimNumber);
                MainReceipt.setString(3, EmploymentStatusAddInfo);
                MainReceipt.setString(4, AutoAccidentAddInfo);
                MainReceipt.setString(5, OtherAccidentAddInfo);
                MainReceipt.setString(6, AutoAccident_StateAddInfo);
                MainReceipt.setString(7, AccidentIllnesDateAddInfo);
                MainReceipt.setString(8, LastMenstrualPeriodDateAddInfo);
                MainReceipt.setString(9, InitialTreatDateAddInfo);
                MainReceipt.setString(10, LastSeenDateAddInfo);
                MainReceipt.setString(11, UnabletoWorkFromDateAddInfo);
                MainReceipt.setString(12, UnabletoWorkToDateAddInfo);
                MainReceipt.setString(13, PatHomeboundAddInfo);
                MainReceipt.setString(14, ClaimCodesAddinfo);
                MainReceipt.setString(15, OtherClaimIDAddinfo);
                MainReceipt.setString(16, ClaimNoteAddinfo);
                MainReceipt.setString(17, ResubmitReasonCodeAddinfo);
                MainReceipt.setString(18, HospitalizedFromDateAddInfo);
                MainReceipt.setString(19, HospitalizedToDateAddInfo);
                MainReceipt.setString(20, LabChargesAddInfo);
                MainReceipt.setString(21, SpecialProgCodeAddInfo);
                MainReceipt.setString(22, PatientSignOnFileAddInfo);
                MainReceipt.setString(23, InsuredSignOnFileAddInfo);
                MainReceipt.setString(24, ProvAccAssigAddInfo);
                MainReceipt.setString(25, PXCTaxQualiAddInfo);
                MainReceipt.setString(26, DocumentationMethodAddInfo);
                MainReceipt.setString(27, DocumentationTypeAddInfo);
                MainReceipt.setString(28, PatientHeightAddInfo);
                MainReceipt.setString(29, PatientWeightAddInfo);
                MainReceipt.setString(30, ServAuthExcepAddInfo);
                MainReceipt.setString(31, DemoProjectAddInfo);
                MainReceipt.setString(32, MemmoCertAddInfo);
                MainReceipt.setString(33, InvDevExempAddInfo);
                MainReceipt.setString(34, AmbPatGrpAddInfo);
                MainReceipt.setString(35, UserId);
                MainReceipt.setString(36, ClientIP);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAdditionalInfo_history (ClaimInfoMasterId,ClaimNumber,EmploymentStatusAddInfo ,"
                        + " AutoAccidentAddInfo,OtherAccidentAddInfo,AutoAccident_StateAddInfo,AccidentIllnesDateAddInfo,LastMenstrualPeriodDateAddInfo,InitialTreatDateAddInfo,LastSeenDateAddInfo," +
                        " UnabletoWorkFromDateAddInfo,UnabletoWorkToDateAddInfo,PatHomeboundAddInfo,ClaimCodesAddinfo,OtherClaimIDAddinfo,ClaimNoteAddinfo," +
                        " ResubmitReasonCodeAddinfo,HospitalizedFromDateAddInfo,HospitalizedToDateAddInfo,LabChargesAddInfo,SpecialProgCodeAddInfo,PatientSignOnFileAddInfo,InsuredSignOnFileAddInfo,ProvAccAssigAddInfo, " +
                        " PXCTaxQualiAddInfo,DocumentationMethodAddInfo,DocumentationTypeAddInfo,PatientHeightAddInfo,PatientWeightAddInfo,ServAuthExcepAddInfo,DemoProjectAddInfo," +
                        " MemmoCertAddInfo,InvDevExempAddInfo,AmbPatGrpAddInfo,Status,CreatedDate,CreatedBy,CreatedIP) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                MainReceipt2.setInt(1, ClaimInfoMasterId);
                MainReceipt2.setString(2, ClaimNumber);
                MainReceipt2.setString(3, EmploymentStatusAddInfo);
                MainReceipt2.setString(4, AutoAccidentAddInfo);
                MainReceipt2.setString(5, OtherAccidentAddInfo);
                MainReceipt2.setString(6, AutoAccident_StateAddInfo);
                MainReceipt2.setString(7, AccidentIllnesDateAddInfo);
                MainReceipt2.setString(8, LastMenstrualPeriodDateAddInfo);
                MainReceipt2.setString(9, InitialTreatDateAddInfo);
                MainReceipt2.setString(10, LastSeenDateAddInfo);
                MainReceipt2.setString(11, UnabletoWorkFromDateAddInfo);
                MainReceipt2.setString(12, UnabletoWorkToDateAddInfo);
                MainReceipt2.setString(13, PatHomeboundAddInfo);
                MainReceipt2.setString(14, ClaimCodesAddinfo);
                MainReceipt2.setString(15, OtherClaimIDAddinfo);
                MainReceipt2.setString(16, ClaimNoteAddinfo);
                MainReceipt2.setString(17, ResubmitReasonCodeAddinfo);
                MainReceipt2.setString(18, HospitalizedFromDateAddInfo);
                MainReceipt2.setString(19, HospitalizedToDateAddInfo);
                MainReceipt2.setString(20, LabChargesAddInfo);
                MainReceipt2.setString(21, SpecialProgCodeAddInfo);
                MainReceipt2.setString(22, PatientSignOnFileAddInfo);
                MainReceipt2.setString(23, InsuredSignOnFileAddInfo);
                MainReceipt2.setString(24, ProvAccAssigAddInfo);
                MainReceipt2.setString(25, PXCTaxQualiAddInfo);
                MainReceipt2.setString(26, DocumentationMethodAddInfo);
                MainReceipt2.setString(27, DocumentationTypeAddInfo);
                MainReceipt2.setString(28, PatientHeightAddInfo);
                MainReceipt2.setString(29, PatientWeightAddInfo);
                MainReceipt2.setString(30, ServAuthExcepAddInfo);
                MainReceipt2.setString(31, DemoProjectAddInfo);
                MainReceipt2.setString(32, MemmoCertAddInfo);
                MainReceipt2.setString(33, InvDevExempAddInfo);
                MainReceipt2.setString(34, AmbPatGrpAddInfo);
                MainReceipt2.setString(35, UserId);
                MainReceipt2.setString(36, ClientIP);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
            } catch (Exception Ex) {
                out.println("Error Insertion in ClaimAdditionalInfo --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimAdditionalInfo --NO SP", request, Ex, getServletContext());
            }

            try {
                if (ClaimID != 0) {
                    Query = "Delete from " + Database + ".ClaimAmbulanceCodes where ClaimInfoMasterId = " + ClaimID;
                    stmt = conn.createStatement();
                    stmt.executeUpdate(Query);
                    stmt.close();
                }

                PreparedStatement MainReceipt = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAmbulanceCodes (ClaimInfoMasterId,ClaimNumber," +
                        " AmbClaimInfoCodes,TranReasonInfoCodes,TranMilesInfoCodes,PatWeightInfoCodes,RoundTripReasInfoCodes,StretReasonInfoCodes,PickUpAddressInfoCode" +
                        " ,PickUpCityInfoCode,PickUpStateInfoCode,PickUpZipCodeInfoCode,DropoffAddressInfoCode,DropoffCityInfoCode,DropoffStateInfoCode,DropoffZipCodeInfoCode," +
                        " PatAdmitHosChk,PatMoveStretChk,PatUnconShockChk,PatTransEmerSituaChk,PatPhyRestrainChk,PatvisiblehemorrChk,AmbSerNeccChk,PatconfbedchairChk," +
                        " Status,CreatedDate,CreatedBy,CreatedIP) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                MainReceipt.setInt(1, ClaimInfoMasterId);
                MainReceipt.setString(2, ClaimNumber);
                MainReceipt.setString(3, AmbClaimInfoCodes);
                MainReceipt.setString(4, TranReasonInfoCodes);
                MainReceipt.setString(5, TranMilesInfoCodes);
                MainReceipt.setString(6, PatWeightInfoCodes);
                MainReceipt.setString(7, RoundTripReasInfoCodes);
                MainReceipt.setString(8, StretReasonInfoCodes);
                MainReceipt.setString(9, PickUpAddressInfoCode);
                MainReceipt.setString(10, PickUpCityInfoCode);
                MainReceipt.setString(11, PickUpStateInfoCode);
                MainReceipt.setString(12, PickUpZipCodeInfoCode);
                MainReceipt.setString(13, DropoffAddressInfoCode);
                MainReceipt.setString(14, DropoffCityInfoCode);
                MainReceipt.setString(15, DropoffStateInfoCode);
                MainReceipt.setString(16, DropoffZipCodeInfoCode);
                MainReceipt.setString(17, PatAdmitHosChk);
                MainReceipt.setString(18, PatMoveStretChk);
                MainReceipt.setString(19, PatUnconShockChk);
                MainReceipt.setString(20, PatTransEmerSituaChk);
                MainReceipt.setString(21, PatPhyRestrainChk);
                MainReceipt.setString(22, PatvisiblehemorrChk);
                MainReceipt.setString(23, AmbSerNeccChk);
                MainReceipt.setString(24, PatconfbedchairChk);
                MainReceipt.setString(25, UserId);
                MainReceipt.setString(26, ClientIP);
                MainReceipt.executeUpdate();
                MainReceipt.close();

                PreparedStatement MainReceipt2 = conn.prepareStatement(" INSERT INTO " + Database + ".ClaimAmbulanceCodes_history (ClaimInfoMasterId,ClaimNumber," +
                        " AmbClaimInfoCodes,TranReasonInfoCodes,TranMilesInfoCodes,PatWeightInfoCodes,RoundTripReasInfoCodes,StretReasonInfoCodes,PickUpAddressInfoCode" +
                        " ,PickUpCityInfoCode,PickUpStateInfoCode,PickUpZipCodeInfoCode,DropoffAddressInfoCode,DropoffCityInfoCode,DropoffStateInfoCode,DropoffZipCodeInfoCode," +
                        " PatAdmitHosChk,PatMoveStretChk,PatUnconShockChk,PatTransEmerSituaChk,PatPhyRestrainChk,PatvisiblehemorrChk,AmbSerNeccChk,PatconfbedchairChk," +
                        " Status,CreatedDate,CreatedBy,CreatedIP) \n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,NOW(),?,?) ");
                MainReceipt2.setInt(1, ClaimInfoMasterId);
                MainReceipt2.setString(2, ClaimNumber);
                MainReceipt2.setString(3, AmbClaimInfoCodes);
                MainReceipt2.setString(4, TranReasonInfoCodes);
                MainReceipt2.setString(5, TranMilesInfoCodes);
                MainReceipt2.setString(6, PatWeightInfoCodes);
                MainReceipt2.setString(7, RoundTripReasInfoCodes);
                MainReceipt2.setString(8, StretReasonInfoCodes);
                MainReceipt2.setString(9, PickUpAddressInfoCode);
                MainReceipt2.setString(10, PickUpCityInfoCode);
                MainReceipt2.setString(11, PickUpStateInfoCode);
                MainReceipt2.setString(12, PickUpZipCodeInfoCode);
                MainReceipt2.setString(13, DropoffAddressInfoCode);
                MainReceipt2.setString(14, DropoffCityInfoCode);
                MainReceipt2.setString(15, DropoffStateInfoCode);
                MainReceipt2.setString(16, DropoffZipCodeInfoCode);
                MainReceipt2.setString(17, PatAdmitHosChk);
                MainReceipt2.setString(18, PatMoveStretChk);
                MainReceipt2.setString(19, PatUnconShockChk);
                MainReceipt2.setString(20, PatTransEmerSituaChk);
                MainReceipt2.setString(21, PatPhyRestrainChk);
                MainReceipt2.setString(22, PatvisiblehemorrChk);
                MainReceipt2.setString(23, AmbSerNeccChk);
                MainReceipt2.setString(24, PatconfbedchairChk);
                MainReceipt2.setString(25, UserId);
                MainReceipt2.setString(26, ClientIP);
                MainReceipt2.executeUpdate();
                MainReceipt2.close();
            } catch (Exception Ex) {
                out.println("Error Insertion in ClaimInformationCode Table --NO SP" + Ex.getMessage());
                Services.DumException("AddInfo", "Insertion in ClaimInformationCode Table --NO SP", request, Ex, getServletContext());
            }

            //out.println("Claim Saved Successfully");
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Claim Saved Successfully");
            Parser.SetField("MRN", "ClaimNumber: " + ClaimNumber);
            Parser.SetField("FormName", "AddInfo");
            Parser.SetField("ActionID", "OpenCMS1500&PatientRegId=" + PatientRegId + "&ClaimNumber=" + ClaimNumber + "&VisitId=" + VisitId);
            Parser.SetField("ClientIndex", String.valueOf(ClientId));
            Parser.GenerateHtml(out, String.valueOf(Services.GetHtmlPath(getServletContext())) + "Exception/Message.html");


        } catch (Exception e) {
            out.println("Error in : " + e.getMessage());
            String str = "";
            for (i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
//            Services.DumException("AddInfo", "Error in Save Claim Method --NO SP", request, e, getServletContext());
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

    public void OpenUB04(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
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

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,'') from oe.clients" +
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
            }
            rset.close();
            stmt.close();

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
                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        " where a.Status = 0 and a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
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
                        pdfContentByte.showText(ClientName.substring(0, 28)); // add the text
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
                        pdfContentByte.showText(ClientName.substring(0, 28)); // add the text
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

    public void OpenCMS1500(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
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
        String PatRelationtoPri = "";
        String AddressifDifferent = "";
        int ClaimInfoMasterId = 0;
        int SelfPayChk = 0;
        String ClientAddress = "";
        String ClientCity = "";
        String ClientState = "";
        String ClientZipCode = "";
        String ClientPhone = "";
        String ClientNPI = "";
        String ClientTaxanomySpecialty = "";

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
        String RenderingProviderLastName = "";
        String RenderingProviderFirstName = "";
        String RenderingProviderNPI = "";
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
        String AccidentIllnesDateAddInfo = "";
        String AccidentIllnesDateAddInfoDD = "";
        String AccidentIllnesDateAddInfoMM = "";
        String AccidentIllnesDateAddInfoYYYY = "";
        String HospitalizedFromDateAddInfo = "";
        String HospitalizedFromDateAddInfoDD = "";
        String HospitalizedFromDateAddInfoMM = "";
        String HospitalizedFromDateAddInfoYYYY = "";
        String HospitalizedToDateAddInfo = "";
        String HospitalizedToDateAddInfoDD = "";
        String HospitalizedToDateAddInfoMM = "";
        String HospitalizedToDateAddInfoYYYY = "";
        String LabChargesAddInfo = "";
        String PatientSignOnFileAddInfo = "";
        String InsuredSignOnFileAddInfo = "";
//
        //Information Codes variable
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

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            Query = " Select IFNULL(Address,''), IFNULL(City,''), IFNULL(State,''), IFNULL(ZipCode,''), IFNULL(Phone,''),IFNULL(NPI,''), IFNULL(TaxanomySpecialty,'') from oe.clients" +
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
                ClientTaxanomySpecialty = rset.getString(7);
            }
            rset.close();
            stmt.close();

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
                        "IFNULL(SelfPayChk,'0'), IFNULL(PhNumber,'')" +
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
                    DOB = rset.getString(7).trim();
                    DateTime = rset.getString(8);
                    Gender = rset.getString(9).trim();
                    DateNow = rset.getString(10);
                    SelfPayChk = rset.getInt(11);
                    PhNumber = rset.getString(12).trim().replaceAll("-", "");
                }
                rset.close();
                stmt.close();
                String DOBMonth = "";
                String DOBDay = "";
                String DOBYear = "";
                if (!DOB.equals("")) {
                    DOBYear = DOB.substring(4, 8);
                    DOBMonth = DOB.substring(0, 2);
                    DOBDay = DOB.substring(2, 4);
                }


                if (SelfPayChk == 1) {
                    Query = "Select IFNULL(PriInsurerName,''), IFNULL(PatientRelationtoPrimary,''), IFNULL(AddressIfDifferent,'') from " + Database + ".InsuranceInfo where PatientRegId = " + PatientRegId;
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    if (rset.next()) {
                        PriInsuredName = rset.getString(1);
                        PatRelationtoPri = rset.getString(2);
                        AddressifDifferent = rset.getString(3);
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
                        " DATE_FORMAT(a.CreatedDate,'%m/%d/%y') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN " + Database + ".DoctorsList b on a.RenderingProvider = b.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList c on a.BillingProviders = c.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        " where a.Status = 0 and a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
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
                    RenderingProviderLastName = rset.getString(11);
                    RenderingProviderFirstName = rset.getString(12);
                    RenderingProviderNPI = rset.getString(13);
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

                Query = "Select IFNULL(EmploymentStatusAddInfo,''), IFNULL(AutoAccidentAddInfo,''), " +
                        " IFNULL(OtherAccidentAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), " +
                        " IFNULL(AccidentIllnesDateAddInfo,''), IFNULL(HospitalizedFromDateAddInfo,''), IFNULL(HospitalizedToDateAddInfo,''), IFNULL(LabChargesAddInfo,'')," +
                        " IFNULL(PatientSignOnFileAddInfo,''), IFNULL(InsuredSignOnFileAddInfo,''), IFNULL(ProvAccAssigAddInfo,'')" +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    EmploymentStatusAddInfo = rset.getString(1);
                    AutoAccidentAddInfo = rset.getString(2);
                    OtherAccidentAddInfo = rset.getString(3);
                    AutoAccident_StateAddInfo = rset.getString(4);
                    AccidentIllnesDateAddInfo = rset.getString(5);
                    HospitalizedFromDateAddInfo = rset.getString(6);
                    HospitalizedToDateAddInfo = rset.getString(7);
                    LabChargesAddInfo = rset.getString(8);
                    PatientSignOnFileAddInfo = rset.getString(9);
                    InsuredSignOnFileAddInfo = rset.getString(10);
                    ProvAccAssigAddInfo = rset.getString(11);
                }
                rset.close();
                stmt.close();


                if (!AccidentIllnesDateAddInfo.equals("")) {
                    AccidentIllnesDateAddInfo = AccidentIllnesDateAddInfo.replaceAll("/", "");
                    AccidentIllnesDateAddInfoMM = AccidentIllnesDateAddInfo.substring(0, 2);
                    AccidentIllnesDateAddInfoDD = AccidentIllnesDateAddInfo.substring(2, 4);
                    AccidentIllnesDateAddInfoYYYY = AccidentIllnesDateAddInfo.substring(4, 8);
                }
                if (!HospitalizedFromDateAddInfo.equals("")) {
                    HospitalizedFromDateAddInfo = HospitalizedFromDateAddInfo.replaceAll("/", "");
                    HospitalizedFromDateAddInfoMM = HospitalizedFromDateAddInfo.substring(0, 2);
                    HospitalizedFromDateAddInfoDD = HospitalizedFromDateAddInfo.substring(2, 4);
                    HospitalizedFromDateAddInfoYYYY = HospitalizedFromDateAddInfo.substring(4, 8);
                }
                if (!HospitalizedToDateAddInfo.equals("")) {
                    HospitalizedToDateAddInfo = HospitalizedToDateAddInfo.replaceAll("/", "");
                    HospitalizedToDateAddInfoMM = HospitalizedToDateAddInfo.substring(0, 2);
                    HospitalizedToDateAddInfoDD = HospitalizedToDateAddInfo.substring(2, 4);
                    HospitalizedToDateAddInfoYYYY = HospitalizedToDateAddInfo.substring(4, 8);
                }

                Query = " Select IFNULL(ICDA,''), IFNULL(ICDB,''), IFNULL(ICDC,''), IFNULL(ICDD,''), IFNULL(ICDE,''), IFNULL(ICDF,''), " +
                        " IFNULL(ICDG,''), IFNULL(ICDH,''), IFNULL(ICDI,''), IFNULL(ICDJ,''), IFNULL(ICDK,''), IFNULL(ICDL,'') " +
                        " from " + Database + ".ClaimChargesInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                        " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
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
                }
                rset.close();
                stmt.close();


                String inputFilePath = "";
                inputFilePath = "/sftpdrive/opt/apache-tomcat-8.5.61/webapps/oe/TemplatePdf/BillingPdf/CMS1500.pdf";
                String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + DirectoryName + "/CMS1500/" + PatientName + PatientRegId + "_" + ".pdf";
                OutputStream fos = new FileOutputStream(new File(outputFilePath));
                PdfReader pdfReader = new PdfReader(inputFilePath);
                PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);
                for (i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                    if (i == 1) {
                        PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(325, 757); // set x and y co-ordinates
                        pdfContentByte.showText(PriInsuranceName); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(325, 743); // set x and y co-ordinates
                        pdfContentByte.showText("Insurance Address"); // add the text
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(325, 730); // set x and y co-ordinates
                        pdfContentByte.showText("Insurance City, STATE ZIPCODE"); // add the text
                        pdfContentByte.endText();

                        if (PriInsuranceName.toUpperCase().contains("MEDICARE")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(25, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//medicare
                            pdfContentByte.endText();
                        } else if (PriInsuranceName.toUpperCase().contains("MEDICAID")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(75, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//medicaid
                            pdfContentByte.endText();
                        } else if (PriInsuranceName.toUpperCase().contains("TRICARE")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(125, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//TRICARE
                            pdfContentByte.endText();
                        } else if (PriInsuranceName.toUpperCase().contains("CHAMPVA")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(190, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//CHAMPVA
                            pdfContentByte.endText();
                        } else if (PriInsuranceName.toUpperCase().contains("GROUP HEALTH PLAN")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(240, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//GROUP HEALTH PLAN
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(340, 680); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//OTEHRS
                            pdfContentByte.endText();
                        }

//                        pdfContentByte.beginText();
//                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
//                        pdfContentByte.setColorFill(BaseColor.BLACK);
//                        pdfContentByte.setTextMatrix(298, 680); // set x and y co-ordinates
//                        pdfContentByte.showText("X"); // add the text//FECA BLK LUNG
//                        pdfContentByte.endText();


                        //box 1a
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(390, 680); // set x and y co-ordinates
                        pdfContentByte.showText(MemId); // add the text//medicaid
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 657); // set x and y co-ordinates
                        pdfContentByte.showText(PatientLastName + ", " + PatientFirstName); // add the text//medicaid
                        pdfContentByte.endText();

                        //box 3
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(240, 655); // set x and y co-ordinates
                        pdfContentByte.showText(DOBMonth); // add the text//MM
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(260, 655); // set x and y co-ordinates
                        pdfContentByte.showText(DOBDay); // add the text//DD
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280, 655); // set x and y co-ordinates
                        pdfContentByte.showText(DOBYear); // add the text//YYYY
                        pdfContentByte.endText();

                        if (Gender.toUpperCase().equals("MALE")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(318, 655); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//MALE
                            pdfContentByte.endText();
                        } else if (Gender.toUpperCase().equals("FEMALE")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(355, 655); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//FEMALE
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(318, 655); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//MALE
                            pdfContentByte.endText();
                        }

                        //box 4
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380, 655); // set x and y co-ordinates
                        pdfContentByte.showText(PriInsuredName); // add the text//FEMALE
                        pdfContentByte.endText();

                        //box 5
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 632); // set x and y co-ordinates
                        pdfContentByte.showText(Address); // add the text//address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 610); // set x and y co-ordinates
                        pdfContentByte.showText(City); // add the text//Others
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(210, 610); // set x and y co-ordinates
                        pdfContentByte.showText(State); // add the text//Others
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 585); // set x and y co-ordinates
                        pdfContentByte.showText(ZipCode); // add the text//Others
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(130, 585); // set x and y co-ordinates
                        pdfContentByte.showText(PhNumber); // add the text//Others
                        pdfContentByte.endText();

                        if (PatRelationtoPri.toUpperCase().equals("SELF")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(255, 632); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//SELF
                            pdfContentByte.endText();
                        } else if (PatRelationtoPri.toUpperCase().equals("SPOUSE")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(290, 632); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Spouse
                            pdfContentByte.endText();
                        } else if (PatRelationtoPri.toUpperCase().equals("CHILD")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(318, 632); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Child
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(353, 632); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Others
                            pdfContentByte.endText();
                        }

                        if (PatRelationtoPri.toUpperCase().equals("SELF")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 632); // set x and y co-ordinates
                            pdfContentByte.showText(Address); // add the text//Others
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 610); // set x and y co-ordinates
                            pdfContentByte.showText(City); // add the text//Others
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(550, 610); // set x and y co-ordinates
                            pdfContentByte.showText(State); // add the text//Others
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 585); // set x and y co-ordinates
                            pdfContentByte.showText(ZipCode); // add the text//Others
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(480, 585); // set x and y co-ordinates
                            pdfContentByte.showText(PhNumber); // add the text//Others
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 632); // set x and y co-ordinates
                            pdfContentByte.showText(AddressifDifferent); // add the text//Others
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 610); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//CITY
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(550, 610); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//STATE
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 585); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//ZIPCODE
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(480, 585); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//PHONE NUMBER
                            pdfContentByte.endText();
                        }

                        //box 9
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 560); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//OthersOther Insureds name Last, First
                        pdfContentByte.endText();

                        //box 9--a
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 540); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//OthersOther Insureds policy or grp num
                        pdfContentByte.endText();

                        //box 9--d
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 465); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//OthersOtherInsurancePLannameorprogname
                        pdfContentByte.endText();


                        if (EmploymentStatusAddInfo.equals("1")) {
                            //box 10--a
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(268, 535); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Employement yes
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(310, 535); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Employement NO
                            pdfContentByte.endText();
                        }

                        if (AutoAccidentAddInfo.equals("1")) {
                            //box 10--b
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(268, 510); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Auto Accident yes
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(340, 510); // set x and y co-ordinates
                            pdfContentByte.showText(AutoAccident_StateAddInfo); // add the text//Auto Accident STATE
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(310, 510); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Auto Accident NO
                            pdfContentByte.endText();
                        }
                        if (OtherAccidentAddInfo.equals("1")) {
                            //box 10--C
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(268, 488); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//Other Accident yes
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(310, 488); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//other Accident NO
                            pdfContentByte.endText();
                        }

                        if (PatRelationtoPri.toUpperCase().equals("SELF")) {
                            //box 11
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 560); // set x and y co-ordinates
                            pdfContentByte.showText(PriInsuranceName); // add the text//Insured policy grp or FECA number
                            pdfContentByte.endText();

                            //box 11--a
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(398, 532); // set x and y co-ordinates
                            pdfContentByte.showText(DOBMonth); // add the text//MM //INsureds DOB
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(418, 532); // set x and y co-ordinates
                            pdfContentByte.showText(DOBDay); // add the text//DD //INsureds DOB
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(438, 532); // set x and y co-ordinates
                            pdfContentByte.showText(DOBYear); // add the text//YYYY //INsureds DOB
                            pdfContentByte.endText();

                            if (Gender.toUpperCase().equals("MALE")) {
                                pdfContentByte.beginText();
                                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                                pdfContentByte.setColorFill(BaseColor.BLACK);
                                pdfContentByte.setTextMatrix(505, 535); // set x and y co-ordinates
                                pdfContentByte.showText("X"); // add the text//MALE //INsureds SEX
                                pdfContentByte.endText();
                            } else if (Gender.toUpperCase().equals("FEMALE")) {

                                pdfContentByte.beginText();
                                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                                pdfContentByte.setColorFill(BaseColor.BLACK);
                                pdfContentByte.setTextMatrix(555, 535); // set x and y co-ordinates
                                pdfContentByte.showText("X"); // add the text//FEMALE //INsureds SEX
                                pdfContentByte.endText();
                            } else {
                                pdfContentByte.beginText();
                                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                                pdfContentByte.setColorFill(BaseColor.BLACK);
                                pdfContentByte.setTextMatrix(505, 535); // set x and y co-ordinates
                                pdfContentByte.showText("X"); // add the text//MALE //INsureds SEX
                                pdfContentByte.endText();
                            }
                        } else {
                            //box 11
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(375, 560); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//Insured policy grp or FECA number
                            pdfContentByte.endText();

                            //box 11--a
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(398, 532); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//MM //INsureds DOB
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(418, 532); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//DD //INsureds DOB
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(438, 532); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//YYYY //INsureds DOB
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(505, 535); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//MALE //INsureds SEX
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(555, 535); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//FEMALE //INsureds SEX
                            pdfContentByte.endText();

                        }

                        //box 11--c
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(375, 490); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//Other plan Name or prog
                        pdfContentByte.endText();

                        //box 11--d
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(390, 462); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//YES //is ther any other health plan
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(425, 462); // set x and y co-ordinates
                        pdfContentByte.showText("X"); // add the text//NO //is ther any other health plan
                        pdfContentByte.endText();


                        if (PatientSignOnFileAddInfo.equals("Y")) {
                            //box 12
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(70, 420); // set x and y co-ordinates
                            pdfContentByte.showText("SIGNATURE ON FILE"); // add the text//Signatre Pat
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(70, 420); // set x and y co-ordinates
                            pdfContentByte.showText("INFORMED CONSENT"); // add the text//Signatre Pat
                            pdfContentByte.endText();
                        }

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(280, 420); // set x and y co-ordinates
                        pdfContentByte.showText(CreationDate); // add the text//DATE
                        pdfContentByte.endText();

                        if (InsuredSignOnFileAddInfo.equals("Y")) {
                            //box 13
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(420, 420); // set x and y co-ordinates
                            pdfContentByte.showText("SIGNATURE ON FILE"); // add the text//Insureds or auth person signature
                            pdfContentByte.endText();
                        } else if (InsuredSignOnFileAddInfo.equals("PR")) {
                            //box 13
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(420, 420); // set x and y co-ordinates
                            pdfContentByte.showText("PATIENT REFUSES"); // add the text//Insureds or auth person signature
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(420, 420); // set x and y co-ordinates
                            pdfContentByte.showText(""); // add the text//NO
                            pdfContentByte.endText();
                        }

                        //box 14
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 390); // set x and y co-ordinates
                        pdfContentByte.showText(AccidentIllnesDateAddInfoMM); // add the text//MM // Date of current illness
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(50, 390); // set x and y co-ordinates
                        pdfContentByte.showText(AccidentIllnesDateAddInfoDD); // add the text//DD // Date of current illness
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(75, 390); // set x and y co-ordinates
                        pdfContentByte.showText(AccidentIllnesDateAddInfoYYYY); // add the text//YYYY // Date of current illness
                        pdfContentByte.endText();

                        //box 17
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 372); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//name of reffering provider
                        pdfContentByte.endText();

                        //box 17--b
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(250, 368); // set x and y co-ordinates
                        pdfContentByte.showText(""); // add the text//name of referring/ordering provider NPI
                        pdfContentByte.endText();

                        //box 18
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(405, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedFromDateAddInfoMM); // add the text//MM  //Hospitalization Dates from
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(425, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedFromDateAddInfoDD); // add the text//DD  //Hospitalization Dates from
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(445, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedFromDateAddInfoYYYY); // add the text//YYYY  //Hospitalization Dates from
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(500, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedToDateAddInfoMM); // add the text//MM  //Hospitalization Dates TO
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(525, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedToDateAddInfoDD); // add the text//DD  //Hospitalization Dates TO
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(545, 368); // set x and y co-ordinates
                        pdfContentByte.showText(HospitalizedToDateAddInfoYYYY); // add the text//YYYY  //Hospitalization Dates TO
                        pdfContentByte.endText();

                        if (!LabChargesAddInfo.equals("0") || !LabChargesAddInfo.equals("0.00") || !LabChargesAddInfo.equals("0.0")) {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(390, 345); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//YES  // OUTSIDE LAB if lab chrges is 0 the no else yes
                            pdfContentByte.endText();

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(470, 345); // set x and y co-ordinates
                            pdfContentByte.showText(LabChargesAddInfo); // add the text//LAB CHARGES  // OUTSIDE LAB if lab chrges is 0 the no else yes
                            pdfContentByte.endText();
                        } else {
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(425, 345); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//NO  // OUTSIDE LAB if lab chrges is 0 the no else yes
                            pdfContentByte.endText();
                        }

                        //box 21---- A
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(40, 320); // set x and y co-ordinates
                        pdfContentByte.showText(ICDA); // add the text//ICD A
                        pdfContentByte.endText();

                        //box 21---- B
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(130, 320); // set x and y co-ordinates
                        pdfContentByte.showText(ICDB); // add the text//ICD B
                        pdfContentByte.endText();

                        //box 21---- C
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(225, 320); // set x and y co-ordinates
                        pdfContentByte.showText(ICDC); // add the text//ICD C
                        pdfContentByte.endText();

                        //box 21---- D
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(315, 320); // set x and y co-ordinates
                        pdfContentByte.showText(ICDD); // add the text//ICD D
                        pdfContentByte.endText();

                        //box 21---- E
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(40, 308); // set x and y co-ordinates
                        pdfContentByte.showText(ICDE); // add the text//ICD E
                        pdfContentByte.endText();

                        //box 21---- F
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(130, 308); // set x and y co-ordinates
                        pdfContentByte.showText(ICDF); // add the text//ICD F
                        pdfContentByte.endText();

                        //box 21---- G
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(225, 308); // set x and y co-ordinates
                        pdfContentByte.showText(ICDG); // add the text//ICD G
                        pdfContentByte.endText();

                        //box 21---- H
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(315, 306); // set x and y co-ordinates
                        pdfContentByte.showText(ICDH); // add the text//ICD H
                        pdfContentByte.endText();

                        //box 21---- I
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(40, 296); // set x and y co-ordinates
                        pdfContentByte.showText(ICDI); // add the text//ICD I
                        pdfContentByte.endText();

                        //box 21---- J
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(130, 296); // set x and y co-ordinates
                        pdfContentByte.showText(ICDJ); // add the text//ICD J
                        pdfContentByte.endText();

                        //box 21---- K
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(225, 296); // set x and y co-ordinates
                        pdfContentByte.showText(ICDK); // add the text//ICD K
                        pdfContentByte.endText();

                        //box 21---- L
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(315, 295); // set x and y co-ordinates
                        pdfContentByte.showText(ICDL); // add the text//ICD L
                        pdfContentByte.endText();

                        int y = 0;
                        int Count = 1;
                        String ServiceDateFrom = "";
                        String ServiceDateFromMM = "";
                        String ServiceDateFromDD = "";
                        String ServiceDateFromYY = "";
                        String ServiceDateTo = "";
                        String ServiceDateToMM = "";
                        String ServiceDateToDD = "";
                        String ServiceDateToYY = "";
                        Query = "Select IFNULL(ServiceFromDate,''), IFNULL(ServiceToDate,''), IFNULL(POS,''), IFNULL(TOS,''), IFNULL(HCPCSProcedure,''), " +
                                "IFNULL(Mod1,''), IFNULL(Mod2,''), IFNULL(Mod3,''), IFNULL(Mod4,''), IFNULL(DXPointer,''), IFNULL(Amount,''), " +
                                "IFNULL(Units,'') from " + Database + ".ClaimChargesInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " " +
                                "and ClaimNumber = '" + ClaimNumber + "'";
                        stmt = conn.createStatement();
                        rset = stmt.executeQuery(Query);
                        while (rset.next()) {
                            if (Count == 1) {
                                y = 250;
                            } else if (Count == 2) {
                                y = 225;
                            } else if (Count == 3) {
                                y = 200;
                            } else if (Count == 4) {
                                y = 175;
                            } else if (Count == 5) {
                                y = 153;
                            } else if (Count == 6) {
                                y = 130;
                            } else {
                                break;
                            }

                            ServiceDateFrom = rset.getString(1);
                            ServiceDateTo = rset.getString(2);
                            if (!ServiceDateFrom.equals("")) {
                                ServiceDateFromYY = ServiceDateFrom.substring(2, 4);
                                ServiceDateFromMM = ServiceDateFrom.substring(5, 7);
                                ServiceDateFromDD = ServiceDateFrom.substring(8, 10);
                            }
                            if (!ServiceDateTo.equals("")) {
                                ServiceDateToYY = ServiceDateTo.substring(2, 4);
                                ServiceDateToMM = ServiceDateTo.substring(5, 7);
                                ServiceDateToDD = ServiceDateTo.substring(8, 10);
                            }

                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(25, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateFromMM); // add the text//MM  //DATE OF SERVICES FROM
                            pdfContentByte.endText();

                            //box 24---A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(45, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateFromDD); // add the text//DD  //DATE OF SERVICES FROM
                            pdfContentByte.endText();

                            //box 24---A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(65, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateFromYY); // add the text//YY  //DATE OF SERVICES FROM
                            pdfContentByte.endText();

                            //box 24---A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(88, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateToMM); // add the text//MM  //DATE OF SERVICES TO
                            pdfContentByte.endText();

                            //box 24---A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(108, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateToDD); // add the text//DD  //DATE OF SERVICES TO
                            pdfContentByte.endText();

                            //box 24---A
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(130, y); // set x and y co-ordinates
                            pdfContentByte.showText(ServiceDateToYY); // add the text//YY  //DATE OF SERVICES TO
                            pdfContentByte.endText();

                            //box 24---B
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(150, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(3)); // add the text//POS Place of service
                            pdfContentByte.endText();

                            //box 24---C
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(175, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(4)); // add the text//EMG//TOS
                            pdfContentByte.endText();

                            //box 24---D
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(195, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(5)); // add the text//CPT PRocedure
                            pdfContentByte.endText();

                            //box 24---D--MOD 1
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(248, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(6)); // add the text//Modifer MOD-1
                            pdfContentByte.endText();

                            //box 24---D--MOD 2
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(273, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(7)); // add the text//Modifer MOD-2
                            pdfContentByte.endText();

                            //box 24---D--MOD 3
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(295, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(8)); // add the text//Modifer MOD-3
                            pdfContentByte.endText();

                            //box 24---D--MOD 4
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(315, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(9)); // add the text//Modifer MOD-4
                            pdfContentByte.endText();

                            //box 24---E---DXPOINTER
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(335, y); // set x and y co-ordinates
                            pdfContentByte.showText(rset.getString(10)); // add the text//DXPOINTER
                            pdfContentByte.endText();

                            //box 24---F---Charges
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", rset.getDouble(11)), 430, y, 0);//Charges
                            pdfContentByte.endText();

                            //box 24---G-- UNITS
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", rset.getDouble(12)), 460, y, 0);//UNITS
                            pdfContentByte.endText();

                            //box 24---E---REdnering Provider NPI
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(500, y); // set x and y co-ordinates
                            pdfContentByte.showText(RenderingProviderNPI); // add the text//REnderring Provider NPI
                            pdfContentByte.endText();

                            Count++;
                            TotalChargeAmount += rset.getDouble(11);
                        }
                        rset.close();
                        stmt.close();

                        //box 25 Federal TAx ID
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(30, 105); // set x and y co-ordinates
                        pdfContentByte.showText("FED TAX ID"); // add the text//Federal TAx ID
                        pdfContentByte.endText();

                        //box 26
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(190, 105); // set x and y co-ordinates
                        pdfContentByte.showText("COPY" + ClaimNumber); // add the text/Pat Account No
                        pdfContentByte.endText();

                        if (ProvAccAssigAddInfo.equals("Y")) {
                            //box 27
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(288, 105); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//YES  // ACCEPT Assignment
                            pdfContentByte.endText();
                        } else {
                            //box 27
                            pdfContentByte.beginText();
                            pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                            pdfContentByte.setColorFill(BaseColor.BLACK);
                            pdfContentByte.setTextMatrix(325, 105); // set x and y co-ordinates
                            pdfContentByte.showText("X"); // add the text//NO  // ACCEPT Assignment
                            pdfContentByte.endText();
                        }

                        //box 28
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", TotalChargeAmount), 430, 105, 0);//Total Chrges
                        pdfContentByte.endText();

                        //box 29
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", 0.00), 515, 105, 0);//amount paid
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(20, 65); // set x and y co-ordinates
                        pdfContentByte.showText(BillingProvidersLastName + ", " + BillingProvidersFirstName); // add the text//NO  // Signature of PHysician NAME HERE
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(110, 50); // set x and y co-ordinates
                        pdfContentByte.showText(CreationDate); // add the text//NO  // Signature of PHysician DATE HERE
                        pdfContentByte.endText();

                        //box 32
                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(178, 85); // set x and y co-ordinates
                        pdfContentByte.showText(ClientName); // add the text//Service facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(178, 75); // set x and y co-ordinates
                        pdfContentByte.showText(ClientAddress); // add the text//Service facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(178, 65); // set x and y co-ordinates
                        pdfContentByte.showText(ClientCity + " " + ClientState + " " + ClientZipCode); // add the text//Service facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(185, 45); // set x and y co-ordinates
                        pdfContentByte.showText(ClientNPI); // add the text//Service facility NPI
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(265, 45); // set x and y co-ordinates
                        pdfContentByte.showText(ClientTaxanomySpecialty); // add the text//Service facility TAX ID
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(370, 85); // set x and y co-ordinates
                        pdfContentByte.showText(ClientName); // add the text//Service facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(370, 75); // set x and y co-ordinates
                        pdfContentByte.showText(ClientAddress); // add the text//BILLING Provider facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(370, 65); // set x and y co-ordinates
                        pdfContentByte.showText(ClientCity + " " + ClientState + " " + ClientZipCode);  // add the text//BILLING Provider  facility location name and address
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(380, 45); // set x and y co-ordinates
                        pdfContentByte.showText(ClientNPI); // add the text//BILLING Provider  facility NPI
                        pdfContentByte.endText();

                        pdfContentByte.beginText();
                        pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                        pdfContentByte.setColorFill(BaseColor.BLACK);
                        pdfContentByte.setTextMatrix(460, 45); // set x and y co-ordinates
                        pdfContentByte.showText(ClientTaxanomySpecialty); // add the text//BILLING Provider  facility TAX ID
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

    public void Institutional_EDIFile(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        String InterControlNo = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
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
                out.println("Please Save Claim First and then Print UB04");

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
                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        " where a.Status = 0 and a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
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
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
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

    public void Professional_EDIFile(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String UserId, String Database, int ClientId, UtilityHelper helper, String DirectoryName, HttpServletResponse response) {
        String ClientIP = helper.getClientIp(request);
        int i = 0, j = 0, k = 0;
        int Found = 0;
        String ChargeMasterTableName = "";
        String InterControlNo = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
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
        String BillingProviderLastName = "";
        String BillingProviderFirstName = "";
        String BillingProviderNPI = "";
        String RenderingProvidersLastName = "";
        String RenderingProvidersFirstName = "";
        String RenderingProvidersNPI = "";
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
        String InitialTreatDateAddInfo = "";
        String AccidentIllnesDateAddInfo = "";
        String LastMenstrualPeriodDateAddInfo = "";
        String UnabletoWorkFromDateAddInfo = "";
        String UnabletoWorkToDateAddInfo = "";
        String ClaimNoteAddinfo = "";
        String SpecialProgCodeAddInfo = "";
        String PatientSignOnFileAddInfo = "";
        String InsuredSignOnFileAddInfo = "";
        String LastSeenDateAddInfo = "";
//
        //Information Codes variable
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
        String ServiceFromDate = "";
        String ServiceToDate = "";
        String POS = "";

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
        StringBuilder PER_1 = new StringBuilder();
        StringBuilder NM1_4 = new StringBuilder();
        StringBuilder N3_2 = new StringBuilder();
        StringBuilder N4_2 = new StringBuilder();
        StringBuilder HL_2 = new StringBuilder();
        StringBuilder SBR = new StringBuilder();
        StringBuilder NM1_5 = new StringBuilder();
        StringBuilder N3_3 = new StringBuilder();
        StringBuilder N4_3 = new StringBuilder();
        StringBuilder DMG = new StringBuilder();
        StringBuilder REF_2 = new StringBuilder();
        StringBuilder NM1_6 = new StringBuilder();
        StringBuilder N3_4 = new StringBuilder();
        StringBuilder N4_4 = new StringBuilder();
        StringBuilder CLM = new StringBuilder();
        StringBuilder DTP_1 = new StringBuilder();
        StringBuilder DTP_2 = new StringBuilder();
        StringBuilder DTP_3 = new StringBuilder();
        StringBuilder DTP_4 = new StringBuilder();
        StringBuilder DTP_5 = new StringBuilder();
        StringBuilder DTP_6 = new StringBuilder();
        StringBuilder REF_3 = new StringBuilder();
        StringBuilder NTE = new StringBuilder();
        StringBuilder HI = new StringBuilder();
        StringBuilder NM1_7 = new StringBuilder();
        StringBuilder PRV_2 = new StringBuilder();
        StringBuilder NM1_8 = new StringBuilder();
        StringBuilder N3_5 = new StringBuilder();
        StringBuilder N4_5 = new StringBuilder();
        StringBuilder LX_SV1 = new StringBuilder();
        StringBuilder DTP_7 = new StringBuilder();
        StringBuilder REF_4 = new StringBuilder();
        StringBuilder SE = new StringBuilder();
        StringBuilder GE = new StringBuilder();
        StringBuilder IEA = new StringBuilder();


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
                out.println("Please Save Claim First and then Print UB04");

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
                        " DATE_FORMAT(a.CreatedDate,'%Y%m%d'), CASE WHEN a.ClaimType = 1 THEN 'CI' WHEN a.ClaimType = 2 THEN 'CP' ELSE 'CL' END, " +
                        " IFNULL(DATE_FORMAT(a.CreatedDate,'%y%m%d'),''),IFNULL(DATE_FORMAT(a.CreatedDate,'%H%i'),''), IFNULL(d.PayerID,''), IFNULL(e.PayerID,'') " +
                        " from " + Database + ".ClaimInfoMaster a " +
                        " LEFT JOIN " + Database + ".DoctorsList b on a.RenderingProvider = b.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList c on a.BillingProviders = c.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList d on a.PriInsuranceNameId = d.Id " +
                        " LEFT JOIN oe.AvailityClearHousePayerList e on a.SecondaryInsuranceId = e.Id " +
                        " LEFT JOIN " + Database + ".DoctorsList f on a.OperatingProvider = f.Id " +
                        " where a.Status = 0 and a.PatientRegId = " + PatientRegId + " and a.VisitId = " + VisitId + " and a.ClaimNumber = '" + ClaimNumber + "'";
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
                    RenderingProvidersLastName = rset.getString(11);
                    RenderingProvidersFirstName = rset.getString(12);
                    RenderingProvidersNPI = rset.getString(13);
                    BillingProviderLastName = rset.getString(14);
                    BillingProviderFirstName = rset.getString(15);
                    BillingProviderNPI = rset.getString(16);
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

                PriInsuranceName = PriInsuranceName.replaceAll("\n", " ");

                Query = "Select IFNULL(ProvAccAssigAddInfo,''), IFNULL(PatientSignOnFileAddInfo,''), " +
                        " IFNULL(InsuredSignOnFileAddInfo,''), IFNULL(AutoAccidentAddInfo,''), IFNULL(EmploymentStatusAddInfo,''), " +
                        " IFNULL(OtherAccidentAddInfo,''), IFNULL(AutoAccident_StateAddInfo,''), IFNULL(SpecialProgCodeAddInfo,''), IFNULL(DelayReasonCodeAddInfo,'')," +
                        " IFNULL(InitialTreatDateAddInfo,''), IFNULL(AccidentIllnesDateAddInfo,''), IFNULL(LastMenstrualPeriodDateAddInfo,''), IFNULL(UnabletoWorkFromDateAddInfo,''), " +
                        " IFNULL(UnabletoWorkToDateAddInfo,''), IFNULL(ClaimNoteAddinfo,''), IFNULL(LastSeenDateAddInfo,'') " +
                        " from " + Database + ".ClaimAdditionalInfo where Status = 0 and ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    ProvAccAssigAddInfo = rset.getString(1);
                    PatientSignOnFileAddInfo = rset.getString(2);
                    InsuredSignOnFileAddInfo = rset.getString(3);
                    AutoAccidentAddInfo = rset.getString(4);
                    EmploymentStatusAddInfo = rset.getString(5);
                    OtherAccidentAddInfo = rset.getString(6);
                    AutoAccident_StateAddInfo = rset.getString(7);
                    SpecialProgCodeAddInfo = rset.getString(8);
                    DelayReasonCodeAddInfo = rset.getString(9);
                    InitialTreatDateAddInfo = rset.getString(10);
                    AccidentIllnesDateAddInfo = rset.getString(11);
                    LastMenstrualPeriodDateAddInfo = rset.getString(12);
                    UnabletoWorkFromDateAddInfo = rset.getString(13);
                    UnabletoWorkToDateAddInfo = rset.getString(14);
                    ClaimNoteAddinfo = rset.getString(15);
                    LastSeenDateAddInfo = rset.getString(16);
                }
                rset.close();
                stmt.close();

                if (!InitialTreatDateAddInfo.equals("")) {
                    InitialTreatDateAddInfo = InitialTreatDateAddInfo.replaceAll("/", "");
                    InitialTreatDateAddInfo = InitialTreatDateAddInfo.substring(4, 8) + InitialTreatDateAddInfo.substring(0, 2) + InitialTreatDateAddInfo.substring(2, 4);
                }
                if (!AccidentIllnesDateAddInfo.equals("")) {
                    AccidentIllnesDateAddInfo = AccidentIllnesDateAddInfo.replaceAll("/", "");
                    AccidentIllnesDateAddInfo = AccidentIllnesDateAddInfo.substring(4, 8) + AccidentIllnesDateAddInfo.substring(0, 2) + AccidentIllnesDateAddInfo.substring(2, 4);
                }
                if (!LastMenstrualPeriodDateAddInfo.equals("")) {
                    LastMenstrualPeriodDateAddInfo = LastMenstrualPeriodDateAddInfo.replaceAll("/", "");
                    LastMenstrualPeriodDateAddInfo = LastMenstrualPeriodDateAddInfo.substring(4, 8) + LastMenstrualPeriodDateAddInfo.substring(0, 2) + LastMenstrualPeriodDateAddInfo.substring(2, 4);
                }
                if (!UnabletoWorkFromDateAddInfo.equals("")) {
                    UnabletoWorkFromDateAddInfo = UnabletoWorkFromDateAddInfo.replaceAll("/", "");
                    UnabletoWorkFromDateAddInfo = UnabletoWorkFromDateAddInfo.substring(4, 8) + UnabletoWorkFromDateAddInfo.substring(0, 2) + UnabletoWorkFromDateAddInfo.substring(2, 4);
                }
                if (!UnabletoWorkToDateAddInfo.equals("")) {
                    UnabletoWorkToDateAddInfo = UnabletoWorkToDateAddInfo.replaceAll("/", "");
                    UnabletoWorkToDateAddInfo = UnabletoWorkToDateAddInfo.substring(4, 8) + UnabletoWorkToDateAddInfo.substring(0, 2) + UnabletoWorkToDateAddInfo.substring(2, 4);
                }
                if (!LastSeenDateAddInfo.equals("")) {
                    LastSeenDateAddInfo = LastSeenDateAddInfo.replaceAll("/", "");
                    LastSeenDateAddInfo = LastSeenDateAddInfo.substring(4, 8) + LastSeenDateAddInfo.substring(0, 2) + LastSeenDateAddInfo.substring(2, 4);
                }


                int Count = 1;
                String DXPointer = "";
                int TagCount = 1;
                Query = "Select IFNULL(HCPCSProcedure,''), IFNULL(Amount,''), IFNULL(Units,''), IFNULL(DXPointer,''), IFNULL(ServiceFromDate,''), IFNULL(POS,''), " +
                        " IFNULL(ICDA,''), IFNULL(ICDB,''), IFNULL(ICDC,''), IFNULL(ICDD,''), IFNULL(ICDE,''), IFNULL(ICDF,''), IFNULL(ICDG,''), IFNULL(ICDH,'')," +
                        " IFNULL(ICDI,''), IFNULL(ICDJ,''), IFNULL(ICDK,''), IFNULL(ICDL,''), IFNULL(ServiceToDate,'') " +
                        " from " + Database + ".ClaimChargesInfo " +
                        " where ClaimInfoMasterId = " + ClaimInfoMasterId + " and ClaimNumber = '" + ClaimNumber + "'";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    String Amount = "";
                    if (rset.getString(2).contains(".")) {
                        Amount = rset.getString(2).substring(0, rset.getString(2).indexOf("."));
                    }
                    ServiceFromDate = rset.getString(5);
                    ServiceToDate = rset.getString(19);
                    if (!ServiceFromDate.equals("")) {
                        ServiceFromDate = ServiceFromDate.replaceAll("-", "");
                        //ServiceFromDate = ServiceFromDate.substring(4,8)+ServiceFromDate.substring(0,2)+ServiceFromDate.substring(2,4);
                    }
                    if (!ServiceToDate.equals("")) {
                        ServiceToDate = ServiceToDate.replaceAll("-", "");
                        //ServiceFromDate = ServiceFromDate.substring(4,8)+ServiceFromDate.substring(0,2)+ServiceFromDate.substring(2,4);
                    }
                    POS = rset.getString(6);
                    ICDA = rset.getString(7);
                    ICDB = rset.getString(8);
                    ICDC = rset.getString(9);
                    ICDD = rset.getString(10);
                    ICDE = rset.getString(11);
                    ICDF = rset.getString(12);
                    ICDG = rset.getString(13);
                    ICDH = rset.getString(14);
                    ICDI = rset.getString(15);
                    ICDJ = rset.getString(16);
                    ICDK = rset.getString(17);
                    ICDL = rset.getString(18);
                    if (!ICDA.equals("")) {
                        DXPointer += "1:";
                    }
                    if (!ICDB.equals("")) {
                        DXPointer += "2:";
                    }
                    if (!ICDC.equals("")) {
                        DXPointer += "3:";
                    }
                    if (!ICDD.equals("")) {
                        DXPointer += "4:";
                    }
//                    LX_SV1.append("LX*"+Count+"~\nSV1*HC:"+rset.getString(1)+"*"+Amount+"*UN*"+rset.getString(3).substring(0, rset.getString(3).indexOf("."))+"***"+DXPointer+"~\nDTP*472*D8*"+ServiceFromDate+"~\n");
                    LX_SV1.append("LX*" + Count + "~\nSV1*HC:" + rset.getString(1) + "*" + Amount + "*UN*" + rset.getString(3).substring(0, rset.getString(3).indexOf(".")) + "***" + DXPointer + "~\nDTP*472*RD8*" + ServiceFromDate + "-" + ServiceToDate + "~\n");
                    TotalChargeAmount += rset.getDouble(2);
                    DXPointer = "";
                    Count++;
                    TagCount += 3;
                }
                rset.close();
                stmt.close();


                String TotalChargeAmountStr = String.valueOf(TotalChargeAmount);

                if (!TotalChargeAmountStr.equals("")) {
                    if (TotalChargeAmountStr.contains("."))
                        TotalChargeAmountStr = TotalChargeAmountStr.substring(0, TotalChargeAmountStr.indexOf("."));
                }


                String ls = "~";
                String segS = "*";
                String AutoAccidentInd = "";
                String EmpInd = "";
                String OtherAccidentInd = "";
                if (AutoAccidentAddInfo.equals("1")) {
                    AutoAccidentInd = "AA";
                }
                if (EmploymentStatusAddInfo.equals("1")) {
                    EmpInd = "EM";
                }
                if (OtherAccidentAddInfo.equals("1")) {
                    OtherAccidentInd = "OA";
                }

                if (!ICDA.equals("")) {
                    ICDA = ICDA.replace(".", "");
                    ICDA = "ABK:" + ICDA + "*";
                }
                if (!ICDB.equals("")) {
                    ICDB = ICDB.replace(".", "");
                    ICDB = "ABF:" + ICDB + "*";
                }
                if (!ICDC.equals("")) {
                    ICDC = ICDC.replace(".", "");
                    ICDC = "ABF:" + ICDC + "*";
                }
                if (!ICDD.equals("")) {
                    ICDD = ICDD.replace(".", "");
                    ICDD = "ABF:" + ICDD + "*";
                }
                if (!ICDE.equals("")) {
                    ICDE = ICDE.replace(".", "");
                    ICDE = "ABF:" + ICDE + "*";
                }
                if (!ICDF.equals("")) {
                    ICDF = ICDF.replace(".", "");
                    ICDF = "ABF:" + ICDF + "*";
                }
                if (!ICDG.equals("")) {
                    ICDG = ICDG.replace(".", "");
                    ICDG = "ABF:" + ICDG + "*";
                }
                if (!ICDH.equals("")) {
                    ICDH = ICDH.replace(".", "");
                    ICDH = "ABF:" + ICDH + "*";
                }
                if (!ICDI.equals("")) {
                    ICDI = ICDI.replace(".", "");
                    ICDI = "ABF:" + ICDI + "*";
                }
                if (!ICDJ.equals("")) {
                    ICDJ = ICDJ.replace(".", "");
                    ICDJ = "ABF:" + ICDJ + "*";
                }
                if (!ICDK.equals("")) {
                    ICDK = ICDK.replace(".", "");
                    ICDK = "ABF:" + ICDK + "*";
                }
                if (!ICDL.equals("")) {
                    ICDL = ICDL.replace(".", "");
                    ICDL = "ABF:" + ICDL + "*";
                }


                ISA.append("ISA*00*          *00*          *ZZ*AV09311993     *01*030240928      *" + ClaimCreateDate + "*" + ClaimCreateTime + "*^*00501*" + InterControlNo + "*1*T*:~\n");
                //GS.append("GS"+segS+"HC"+segS+"p920700"+segS+"ECGCLAIMS"+segS+"20210713"+segS+"174417"+segS+"000000001"+segS+"X"+segS+"005010X222A1"+ls+"\n");
                GS.append("GS*HC*AV09311993*030240928*" + CreationDate + "*" + ClaimCreateTime + "*" + InterControlNo + "*X*005010X222~\n");
                ST.append("ST" + segS + "837" + segS + "1159" + segS + "005010X222" + ls + "\n");
                BHT.append("BHT" + segS + "0019" + segS + "00" + segS + "1" + segS + "" + CreationDate + "" + segS + "" + ClaimCreateTime + "" + segS + "CH" + ls + "\n");
                NM1.append("NM1*41*2*" + RenderingProvidersFirstName + " " + "" + " " + RenderingProvidersLastName + "" + segS + "" + segS + "" + segS + "" + segS + "" + segS + "46" + segS + "009207634633~\n");
                PER.append("PER*IC*" + BillingProviderFirstName + " " + "" + " " + BillingProviderLastName + "*TE*" + "4694980033" + "~\n");//BillingProviderPhNumber
                NM1_2.append("NM1*40*2*MCKHBOC*****46*0001~\n");
                HL_1.append("HL*1**20*1~\n");
                PRV_BI.append("PRV*BI*PXC*" + TaxanomySpecialty + "~\n");
                NM1_3.append("NM1*85*2*" + ClientName + "*****XX*" + ClientNPI + "~\n");
                N3_1.append("N3*" + ClientAddress + "~\n");
                N4_1.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");
                REF_1.append("REF*EI*" + ClientTaxID + "~\n");
                PER_1.append("PER*IC*TEST*TE*4694980033~\n");
                NM1_4.append("NM1*87*2~\n");
                N3_2.append("N3*" + ClientAddress + "~\n");
                N4_2.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");
                HL_2.append("HL*2*1*22*0~\n");
                SBR.append("SBR*P*18**" + PriInsuranceName + "*****CI~\n");
                NM1_5.append("NM1*IL*1*" + PatientLastName + "*" + PatientFirstName + "*" + "" + "***MI*" + MemId + "~\n");
                N3_3.append("N3*" + Address + "~\n");
                N4_3.append("N4*" + City + "*" + State + "*" + ZipCode + "~\n");
                DMG.append("DMG*D8*" + DOB + "*" + Gender + "~\n");
                REF_2.append("REF*Y4*OTHER CLAIM ID~\n");
                NM1_6.append("NM1*PR*2*" + PriInsuranceName + "*****PI*" + PriInsuranceNameId + "~\n");
                N3_4.append("N3*InsuranceAdddress~\n");
                N4_4.append("N4*DALLAS*TX*752660044~\n");
                CLM.append("CLM*" + ClaimNumber + "*" + TotalChargeAmountStr + "***" + POS + ":B:1*" + ProvAccAssigAddInfo + "*A*" + PatientSignOnFileAddInfo + "*" + InsuredSignOnFileAddInfo + "~\n");
                TagCount = TagCount + 25;

                DTP_1.append("DTP*454*D8*" + InitialTreatDateAddInfo + "~\n");
                DTP_2.append("DTP*304*D8*" + LastSeenDateAddInfo + "~\n");
                //DTP_3.append("DTP*439*D8*"+AccidentIllnesDateAddInfo+"~\n");
                DTP_4.append("DTP*484*D8*" + LastMenstrualPeriodDateAddInfo + "~\n");
                //DTP_5.append("DTP*314*RD8*"+UnabletoWorkFromDateAddInfo+"-"+UnabletoWorkToDateAddInfo+"~\n");
                DTP_6.append("DTP*296*D8*" + UnabletoWorkToDateAddInfo + "~\n");

                if (!InitialTreatDateAddInfo.equals("")) {
                    TagCount++;
                }
                if (!LastSeenDateAddInfo.equals("")) {
                    TagCount++;
                }
//                if(!AccidentIllnesDateAddInfo.equals("")){
//                    TagCount++;
//                }
                if (!LastMenstrualPeriodDateAddInfo.equals("")) {
                    TagCount++;
                }
                if (!UnabletoWorkFromDateAddInfo.equals("")) {
                    TagCount++;
                }

                REF_3.append("REF*EA*" + PatientMRN + "~\n");
                TagCount++;

                if (!ClaimNoteAddinfo.equals("")) {
                    TagCount++;
                }
                NTE.append("NTE*ADD*" + ClaimNoteAddinfo + "~\n");

                HI.append("HI*" + ICDA + "" + ICDB + "" + ICDC + "" + ICDD + "" + ICDE + "" + ICDF + "" + ICDG + "" + ICDH + "" + ICDI + "" + ICDJ + "" + ICDK + "" + ICDL + "~\n");
                NM1_7.append("NM1*82*1*" + RenderingProvidersLastName + " " + RenderingProvidersFirstName + "*" + "" + "***XX*" + "" + "~\n");
                PRV_2.append("PRV*PE*PXC*" + TaxanomySpecialty + "~\n");
                NM1_8.append("NM1*77*2*" + ClientName + "*****XX*" + ClientNPI + "~\n");
                N3_5.append("N3*" + ClientAddress + "~\n");
                N4_5.append("N4*" + ClientCity + "*" + ClientState + "*" + ClientZipCode + "~\n");


                //DTP_7.append("DTP*472*D8*"+ServiceFromDate+"~\n");

                TagCount = TagCount + 8;
                REF_4.append("REF*6R*353201581~\n");
                SE.append("SE*" + TagCount + "*1159~\n");
                GE.append("GE*1*" + InterControlNo + "~\n");
                IEA.append("IEA*1*" + InterControlNo + "~\n");

                File file = new File("/sftpdrive/opt/EDI_Prof_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (file.exists()) {
                    file.delete();
                }

                File myObj = new File("/sftpdrive/opt/EDI_Prof_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
                if (myObj.createNewFile()) {

                    FileWriter myWriter = new FileWriter("/sftpdrive/opt/EDI_Prof_" + PatientName + "_" + PatientRegId + VisitId + "_" + ClaimNumber + ".txt");
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
                    myWriter.write(PER_1.toString());
                    myWriter.write(NM1_4.toString());
                    myWriter.write(N3_2.toString());
                    myWriter.write(N4_2.toString());
                    myWriter.write(HL_2.toString());
                    myWriter.write(SBR.toString());
                    myWriter.write(NM1_5.toString());
                    myWriter.write(N3_3.toString());
                    myWriter.write(N4_3.toString());
                    myWriter.write(DMG.toString());
                    myWriter.write(REF_2.toString());
                    myWriter.write(NM1_6.toString());
                    myWriter.write(N3_4.toString());
                    myWriter.write(N4_4.toString());
                    myWriter.write(CLM.toString());

                    if (!InitialTreatDateAddInfo.equals("")) {
                        myWriter.write(DTP_1.toString());
                    }
                    if (!LastSeenDateAddInfo.equals("")) {
                        myWriter.write(DTP_2.toString());
                    }
//                    if(!AccidentIllnesDateAddInfo.equals("")){
//                        myWriter.write(DTP_3.toString());
//                    }
                    if (!LastMenstrualPeriodDateAddInfo.equals("")) {
                        myWriter.write(DTP_4.toString());
                    }
                    if (!UnabletoWorkToDateAddInfo.equals("")) {
                        //myWriter.write(DTP_5.toString());
                        myWriter.write(DTP_6.toString());
                    }
                    myWriter.write(REF_3.toString());

                    if (!ClaimNoteAddinfo.equals("")) {
                        myWriter.write(NTE.toString());
                    }
                    myWriter.write(HI.toString());
                    myWriter.write(NM1_7.toString());
                    myWriter.write(PRV_2.toString());
                    myWriter.write(NM1_8.toString());
                    myWriter.write(N3_5.toString());
                    myWriter.write(N4_5.toString());
                    myWriter.write(LX_SV1.toString());
                    myWriter.write(DTP_7.toString());
                    myWriter.write(REF_4.toString());
                    myWriter.write(SE.toString());
                    myWriter.write(GE.toString());
                    myWriter.write(IEA.toString());

                    myWriter.close();
                }

            }
//            Parsehtm Parser = new Parsehtm(request);
//            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Reports/Addinfo.html");
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