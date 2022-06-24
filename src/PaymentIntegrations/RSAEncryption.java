package PaymentIntegrations;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class RSAEncryption {
    static String plainText = "Tabish";

    public static void main(String[] args) throws Exception {
        plainText = "Tabish";
        // Get an instance of the RSA key generator
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        // Generate the KeyPair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get the public and private key
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println("Original Text  : " + plainText);

        // Encryption
//        byte[] cipherTextArray = encrypt(plainText, publicKey);
//        String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
//        System.out.println("Encrypted Text : "+encryptedText);

        // Decryption
        String encryptedText = "O5jdPpymAujV32egYh6kRhlxeGxH7pttNW4FYXcu5hX/2XtDAjKN3/HkzWopm8fw9gTh2zPn7+EePBNmvn2TdItPbxhQo5b8vLDYvsK+Q05yBNtkk9HnQ+d9sboOsJMUMj6M28685pSYfAsai7npA8YCZOp0JS1TEwaY5UGfGrqgtmQTvqyvUSblqWHddXzREyEEaxCIpPOGZxUyEcDjHR3StYzBcFs4cclh8NAnzmgd0E2E/IepPP7BpbdTzLfgslk8FGgoMGYJJcxh0wxyTvY/nQxBRcrTsEEXF96MXwXkXmbI5ZAHv0KOLBkyKeT+t23JITFmbz4DOJ6mXP88/VS6Nyz9MLYFdVwFE3qoLXL67zP9OVER6LTFqoGe9c8rrVZdVjZAZHBQrrPz0uJzmVGfLL6k7tbs8n0DXK5RZSsNh+LHGPySxensIkIbNl+S4TGvtGruhvJpB9mfpOlYjcRQ758X0SkDYnx18EDd00c0j+vJX+fUYxVcKrmTUXkF1zVYQYAb/3QfD5WZyEgHVU+Po2EMWuibAkRVHpCTnO+xuVuVQMS7Y2d7XvDSpCXKSV22yjl0Wtc5481muymFnsO/RUWnu1ErJxVduyx4PKtr17WivU1L7A/Rti8jN2AcQAhuinULxIZt2QPRzVDekdkj3n64uDaawHwqeFuI1HA=\n";
        System.out.println("BYTES " + Arrays.toString(encryptedText.getBytes()));
        byte[] cipherTextArray = encryptedText.getBytes();
        String decryptedText = decrypt(cipherTextArray, privateKey);
        System.out.println("DeCrypted Text : " + decryptedText);
    }

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
}
