package md;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionDecryption {
    private static final String ALGO = "AES";
    /*    private static final byte[] keyValue =
                new byte[] {  '(' , 'T', '@', 'b', '!' , 's', 'H',
                            '!' , '$' , 'T' , '#' ,'E' , 'b' , '3' , '$' , 'T' , ')' };*/
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};

    private static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.trim().getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    private static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData.trim());
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public static void main(String[] args) {
        try {
            //String Enc = EncryptionDecryption.encrypt("3787-5085-7421-618_");//Card 1
            //String Enc = EncryptionDecryption.encrypt("4264-2879-0450-8997");//Card 2
            //String Enc = EncryptionDecryption.encrypt("1901");//Card 1 CVV
            String Enc = EncryptionDecryption.encrypt("Login2020!");//Card 2 CVV
            //String DEC = EncryptionDecryption.decrypt("NYwrb/Exfglgcp/76YDy0Q==");
            String DECShayaPwd = EncryptionDecryption.decrypt("IAlaEyf7v5Kcuxie2EUWIQ==");

            String DEC = EncryptionDecryption.decrypt("dxAwprj1yuipUg8mGlaMSw==");
            System.out.println("Encrypted Val ---- " + Enc);
            System.out.println("Decrypted Val ---- " + DECShayaPwd);
            System.out.println("Decrypted Val ---- " + DEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
