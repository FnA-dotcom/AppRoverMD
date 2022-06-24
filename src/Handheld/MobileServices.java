package Handheld;

import md.FacilityLogin;
import md.Services;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class MobileServices extends HttpServlet {
/*    private Statement stmt = null;
    private   ResultSet rset = null;
    private   String Query = "";
    private PreparedStatement pStmt = null;
    private String LogString = null;
    private Connection conn = null;
    private static final String ALGO = "AES";
    ServletContext context = null;
    HashMap<Integer, String> myMap = new HashMap<Integer, String>();

    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};
    private String userId;*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = null;
        ServletContext context = null;
        //For Printing
        PrintWriter out = new PrintWriter(response.getOutputStream());
        //For Printing JSON Objects
        ServletOutputStream SOS = response.getOutputStream();
        try {
            conn = Services.GetConnection(getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                out.flush();
                out.close();
                return;
            }
        } catch (Exception excp) {
            conn = null;
            out.println("Exception excp conn: " + excp.getMessage());
            out.flush();
            out.close();
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            UtilityHelper helper = new UtilityHelper();
            context = this.getServletContext();
            String RequestName = request.getParameter("RequestName").trim();
            switch (RequestName) {
                case "Test":
                    BFunction(response, request);
                    break;
                case "LoginHelper":
                    LoginHelper(response, request, conn, helper, context);
                    break;
                case "MobileLogOut":
                    MobileLogOut(response, request, conn);
                    break;
                case "PasswordChange":
                    PasswordChange(response, request, conn, helper, context);
                    break;
                case "signPDFData":
                    signPDFData(response, request, SOS, conn, helper, context);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception Ex) {
            out.println("Error in Main " + Ex.getMessage());
        }
        out.close();
        out.flush();
    }

    private void BFunction(HttpServletResponse response, HttpServletRequest req) throws IOException {

        String myVar = req.getParameter("AA");
        ServletOutputStream out = response.getOutputStream();

        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < 2; i++) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("srcOfPhoto", "Element 1");
            jsonObj.put("username", "name" + i);
            jsonObj.put("userid", "userid" + i);

            jsonArray.add(jsonObj);
        }
        out.print("ELEMENTS ARE " + jsonArray.toJSONString());
        out.print("myVar " + myVar);
    }

    private String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec("encryptionIntVec".getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec("aesEncryptionKey".getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void LoginHelper(HttpServletResponse response, HttpServletRequest request, Connection conn, UtilityHelper helper, ServletContext context) throws IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String userId = "";
        String appVersion = "";
        String passwordEnc = "";
        int Found = 0;
        int ClientIndex = 0;
        String UserName = "";
        String RegFormName = "";
        String QRegFormName = "";

        //JSON Variables
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        HashMap<Integer, String> myMap = new HashMap<Integer, String>();
        try {
            String UserIP = request.getRemoteAddr();
            String value = request.getParameter("value");
            String[] splitValues = decrypt(value).split("'");

            userId = splitValues[0];
            passwordEnc = FacilityLogin.encrypt(splitValues[1]);
            String[] userInfo = helper.getMobileUserInfo(request, splitValues[0].trim(), passwordEnc, conn, context);
            Found = Integer.parseInt(userInfo[0]);
            if (Found > 0) {
                appVersion = helper.AppVersion(request, conn, context);
                if (splitValues[2].compareToIgnoreCase(appVersion) != 0) {
                    UtilityHelper.doLogMethodMessage(this.getServletContext(), "Login Method ", "Version Exception!!");
                    jsonObj.put("Result", "false");
                    jsonObj.put("Message", "You are using an outdated Mobile App. Current Version is  " + appVersion + " ");
                    jsonArray.add(jsonObj);
                    out.print(jsonArray.toJSONString());
                    return;
                } else {
                    UserName = userInfo[1];
                    RegFormName = userInfo[2];
                    QRegFormName = userInfo[3];
                    ClientIndex = Integer.parseInt(userInfo[4]);

                    //Trail Activity
                    MobUserActivity(conn, splitValues[0].trim(), UserIP, "Logged-In");

                    jsonObj.put("Result", "true");
                    jsonObj.put("Message", UserName);
                    jsonObj.put("RegFormName", RegFormName);
                    jsonObj.put("QRegFormName", QRegFormName);
                    jsonObj.put("ClientIndex", ClientIndex);

                    myMap.putIfAbsent(ClientIndex, UserName);
                    //URLEncoder.encode(Description, "UTF-8");

                    if (ClientIndex == 9) //Victoria
                        jsonObj.put("URL", "https://app.rovermd.com:8443/md/md.PatientReg2_Mobile?ActionID=Victoria_2&ClientId=Victoria-ER");
                        //jsonObj.put("URL", "https://dev1.rovermd.com:8443/md/md.PatientReg2_Mobile?ActionID=Victoria_2&ClientId=Victoria-ER");
/*                    else if (ClientIndex == 8) //Orange
                        jsonObj.put("URL", "https://app.rovermd.com:8443/md/md.PatientReg_Mobile?ActionID=GetValues&ClientIndex=Orange");
                        //jsonObj.put("URL", "https://dev1.rovermd.com:8443/md/md.PatientReg_Mobile?ActionID=GetValues&ClientId=Orange");*/
                    else if (ClientIndex == 28) //ER-Dallas
                        jsonObj.put("URL", "https://app.rovermd.com:8443/md/md.PatientReg2_Mobile?ActionID=Victoria_2&ClientId=ER-Dallas");
                        //jsonObj.put("URL", "https://dev1.rovermd.com:8443/md/md.PatientReg2_Mobile?ActionID=Victoria_2&ClientId=ER-Dallas");
                    else if (ClientIndex == 36) //Rover Lab
                        jsonObj.put("URL", "https://app.rovermd.com:8443/md/md.RoverLabMobile?RequestName=GetValues&userId=" + userId + "");
                        //jsonObj.put("URL", "https://dev1.rovermd.com:8443/md/md.PatientReg2_Mobile?ActionID=Victoria_2&ClientId=ER-Dallas");
                    else
                        jsonObj.put("URL", "https://app.rovermd.com:8443/md/md.PatientReg_Mobile?ActionID=GetValues&ClientIndex=" + ClientIndex + "");
                    //jsonObj.put("URL", "https://dev1.rovermd.com:8443/md/md.PatientReg_Mobile?ActionID=GetValues&ClientIndex=" + ClientIndex + "");

                    jsonArray.add(jsonObj);
                    out.print(jsonArray.toJSONString());
                }

            } else {
                UtilityHelper.doLogMethodMessage(this.getServletContext(), "Login Method - Credential Error", "InCorrect Credentials. Please Enter Correct Credentials!!");
                jsonObj.put("Result", "false");
                jsonObj.put("Message", "InCorrect Credentials. Please Enter Correct Credentials!! ");
                jsonArray.add(jsonObj);
                out.print(jsonArray.toJSONString());
                return;
            }


        } catch (Exception e) {
            Services.doLogMethodMessage(this.getServletContext(), "Login Method - User Not Exist", e.getMessage(), request);
            jsonObj.put("Result", "false");
            jsonObj.put("Message", "Invalid UserId or password " + e.getMessage() + " ");
            jsonArray.add(jsonObj);
            out.print(jsonArray.toJSONString());
            return;
        }

    }

    private void MobileLogOut(HttpServletResponse response, HttpServletRequest request, Connection conn) throws IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String UserId = request.getParameter("UserId").trim();

        //JSON Variables
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        try {
            String UserIP = request.getRemoteAddr();

            jsonObj.put("Result", "true");
            jsonArray.add(jsonObj);
            out.print(jsonArray.toJSONString());

            //Trail Activity
            MobUserActivity(conn, UserId, UserIP, "Logged-Out");
            return;

        } catch (Exception Ex) {
            Services.doLogMethodMessage(this.getServletContext(), "LogOut Method - User Not Exist", "Exception :  Unable to LogOut", request);
            jsonObj.put("Result", "false");
            jsonObj.put("Message", "LogOut Procedure Failure" + Ex.getMessage() + " ");
            jsonArray.add(jsonObj);
            out.print(jsonArray.toJSONString());
            return;
        }
    }

    private String MobUserActivity(Connection conn, String UserId, String UserIP, String UserFlag) {
        PreparedStatement pStmt = null;
        String Query = "";
        String Message = "";
        try {
            pStmt = conn.prepareStatement(
                    "INSERT INTO MobileUserActivity(UserId ,LoggedTime, Status, CreatedDate,UserIP, UserFlag)" +
                            " VALUES(?,NOW(),0,now(),?,?)");

            pStmt.setString(1, UserId.trim());
            pStmt.setString(2, UserIP.trim());
            pStmt.setString(3, UserFlag.trim());
            pStmt.executeUpdate();
            pStmt.close();

            Message = "Success";
        } catch (Exception Ex) {
            Message = Ex.getMessage();
        }
        return Message;
    }

    private void PasswordChange(HttpServletResponse response, HttpServletRequest request, Connection conn, UtilityHelper helper, ServletContext context) throws IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        PreparedStatement pStmt = null;
        String passwordEnc = "";

        //JSON Variables
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();

        try {
            String UserIP = request.getRemoteAddr();
            String UserId = request.getParameter("UserId");
            String Password = request.getParameter("Password");
            String NewPassword = request.getParameter("NewPassword");

            passwordEnc = FacilityLogin.encrypt(Password);
            NewPassword = FacilityLogin.encrypt(NewPassword);

            int found = 0;
            Query = "SELECT COUNT(*) FROM oe.MobileUsers WHERE UserId = '" + UserId + "' AND Password = '" + passwordEnc + "' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                found = rset.getInt(1);
            rset.close();
            stmt.close();

            if (found > 0) {
                Query = "SELECT UserName, UserId, Password, Status, CreatedDate, ClientIndex,Id " +
                        " FROM oe.MobileUsers WHERE UserId = '" + UserId + "' ";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    pStmt = conn.prepareStatement(
                            "INSERT INTO MobileUsersHistory (UserName, UserId, Password, Status, CreatedDate, ClientIndex,OldId, UserIP) VALUES " +
                                    "(?,?,?,?,?,?,?,?) ");
                    pStmt.setString(1, rset.getString(1));
                    pStmt.setString(2, rset.getString(2));
                    pStmt.setString(3, rset.getString(3));
                    pStmt.setInt(4, rset.getInt(4));
                    pStmt.setString(5, rset.getString(5));
                    pStmt.setInt(6, rset.getInt(6));
                    pStmt.setInt(7, rset.getInt(7));
                    pStmt.setString(8, UserIP);
                    pStmt.executeUpdate();
                    pStmt.close();
                }
                rset.close();
                stmt.close();

                Query = "UPDATE oe.MobileUsers SET Password = '" + NewPassword + "', UpdatedDate = NOW(), UpdatedBy = '" + UserId + "' " +
                        " WHERE UserId = '" + UserId + "' ";
                stmt = conn.createStatement();
                stmt.executeUpdate(Query);
                stmt.close();

                jsonObj.put("Result", "true");
                jsonObj.put("Message", " ");
                jsonArray.add(jsonObj);
                out.print(jsonArray.toJSONString());
            } else {
                jsonObj.put("Result", "false");
                jsonObj.put("Message", "Your Password current is incorrect!");
                jsonArray.add(jsonObj);
                out.print(jsonArray.toJSONString());
            }
        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + " <br> ";
            }
            out.println(str);
            Services.doLogMethodMessage(this.getServletContext(), "Login Method - User Not Exist", e.getMessage(), request);
            jsonObj.put("Result", "false");
            jsonObj.put("Message", "Invalid UserId or password " + e.getMessage() + " ");
            jsonArray.add(jsonObj);
            out.print(jsonArray.toJSONString());
            return;
        }

    }

    private void signPDFData(HttpServletResponse response, HttpServletRequest request, ServletOutputStream SOS, Connection conn, UtilityHelper helper, ServletContext context) throws IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        PreparedStatement pStmt = null;
        String passwordEnc = "";

        //JSON Variables
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject();
        //For Printing JSON Objects
        ServletOutputStream out = response.getOutputStream();
        String dbName = "";
        try {
            String UserIP = request.getRemoteAddr();
            String UserId = request.getParameter("UserId");
            int facilityIdx = 0;
            Query = "Select ClientIndex from oe.MobileUsers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                facilityIdx = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + facilityIdx;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                dbName = rset.getString(1);
            }
            rset.close();
            stmt.close();

            Query = "SELECT a.UID,a.MRN,a.FacilityIndex,a.OutputPath,a.pageCount,a.FileName,a.PatientRegIdx,b.name," +
                    "CONCAT(IFNULL(c.Title,''),' ',IFNULL(c.FirstName,''),' ',IFNULL(c.MiddleInitial,''),' ',IFNULL(c.LastName,'')) " +
                    "FROM " + dbName + ".RequestToMobile a " +
                    " STRAIGHT_JOIN oe.clients b ON a.FacilityIndex = b.Id " +
                    " STRAIGHT_JOIN " + dbName + ".PatientReg c ON a.PatientRegIdx = c.ID " +
                    " WHERE a.Status = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("UID", rset.getString(1));
                jsonObj.put("MRN", rset.getInt(2));
                jsonObj.put("FacilityIndex", rset.getInt(3));
                jsonObj.put("OutputPath", rset.getString(4));
                jsonObj.put("pageCount", rset.getInt(5));
                jsonObj.put("FileName", rset.getString(6));
                jsonObj.put("PatientRegIdx", rset.getInt(7));
                jsonObj.put("FacilityName", rset.getString(8));
                jsonObj.put("PatientName", rset.getString(9));

                jsonArray.add(jsonObj);
            }
            rset.close();
            stmt.close();

            //Printing
            SOS.print(jsonArray.toJSONString());
        } catch (Exception e) {
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + " <br> ";
            }
            out.println(str);
            Services.doLogMethodMessage(this.getServletContext(), "Login Method - User Not Exist", e.getMessage(), request);
            jsonObj1.put("Result", "false");
            jsonObj1.put("Message", "Invalid UserId or password " + e.getMessage() + " ");
            jsonArray.add(jsonObj1);
            out.print(jsonArray.toJSONString());
        }

    }
}
