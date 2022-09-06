package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.crypto.Cipher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.*;
import java.sql.*;

public class RSA_testing extends HttpServlet {
    static String plainText = "Plain text which need to be encrypted by Java RSA Encryption in ECB Mode";
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

    public static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        return cipherText;
    }

    public static String decrypt(byte[] cipherTextArray, PrivateKey privateKey) throws Exception {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //Perform Decryption
        byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

        return new String(decryptedTextArray);
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
        String FontColor;
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        int UserIndex = 0;
        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();

        try {

            UtilityHelper helper = new UtilityHelper();

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);

            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
//                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Management Dashboard", "View Management Dashboard", FacilityIndex);
                this.GetInput(request, out, conn, context);
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext) {

        // Get an instance of the RSA key generator
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            out.println(e.getMessage());
        }

        // Generate the KeyPair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get the public and private key
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] cipherTextArray = null;
        if (request.getParameter("p").equals("1")) {
            plainText = "Mouhid";
            out.println("Original Text  : " + plainText);
            // Encryption

            try {
                cipherTextArray = encrypt(plainText, publicKey);

                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.RSA_test (bytes) " +
                                " VALUES (?) ");
                MainReceipt.setBytes(1, cipherTextArray);
                MainReceipt.executeUpdate();
                MainReceipt.close();
                out.println("saved successfully");
            } catch (Exception e) {
                e.printStackTrace();
                out.println(e.getMessage());
            }
        } else {
            cipherTextArray = null;
            try {
                Query = "Select bytes from oe.RSA_test" +
                        " where id=3";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    byte[] cipherTextArray1 = rset.getBytes(1);
                    try {
                        String decryptedText = decrypt(cipherTextArray1, privateKey);
                        out.println("DeCrypted Text : " + decryptedText);
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println(e.getMessage());
//                out.println(e.printStackTrace());
                    }
                }
                rset.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                out.println(e.getMessage());
            }

        }
    }
}
