package PaymentIntegrations;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAEncryption {
    static String plainText = "Plain text which need to be encrypted by Java RSA Encryption in ECB Mode";

    public static void main(String[] args) throws Exception
    {
        plainText ="Mouhid";
        // Get an instance of the RSA key generator
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        // Generate the KeyPair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get the public and private key
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println("Original Text  : "+plainText);

//        // Encryption
        byte[] cipherTextArray = encrypt(plainText, publicKey);
        String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
//        String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
//        String encryptedText = "V0eAM3enQWvLbUHuKbMjYZzA3G616ewRqQcGJZAFeqwBLytmiSTD2vpqGh3kkb346Y4nt+PKbOQaIFOOT2i8Kxj6FrKkX4o+9bX8tpmSYGOs7nLX4NQpZFjWXYY2g44Ltz34IxbsA1Ca2cmkceskdIfL4EcuspYHEOijN60BB6TPjwr4lhxiVm/k44j7AuSAM2Q04xITiEXpV9sjp+joJ09OSR6rjDD6icgXR/JZMRGOIJnio6LJwh7iUgcp5/wMVKKD2y6ADBPtCcpJZXMn6SOQjEq73CNja/whWs2sh+4boOUDnAb02qLfrKdbLniBEboIA3Qu/+IvFje6ChvgUuPMixvzhG3pXKRRngIvIXs7zJ7f/90AMmH16zQKyEoHyiJUEGvDlkmzL/PyLC1WkFb7nWReoIrx1K3CUnwLPt5dlKr5bgpfTLoRN1WTaIg4DIluti85QQpTYHjNXpcx7TDnfHvy9sUXfHByrLqIE60wioQVPJ/+TDyK1LNhJc1yO5LzGdEno1xx53kaLvjRhQChC82jsiaB2yasfZx6fHX9s7840m3XitobtOE21QAZuiaV4P/1fZTccMj1VFUiPEsVOV2pTSPIKdCMaqTdtlpRfSwCPUM5408Q0spCm0jb2iVHtxXSC2tymvcnkDg5czhOMvOWZTE+b5dyt/z0gcQ=";


//        System.out.println("cipherTextArray : "+cipherTextArray);
        System.out.println("Encrypted Text : "+encryptedText);
        System.out.println("Encrypted Text : "+Base64.getEncoder().encodeToString(cipherTextArray));
//        System.out.println("Encrypted Text : "+Base64.getEncoder().encodeToString(cipherTextArray));
//        System.out.println("cipherTextArray : "+ Base64.getDecoder().decode(encryptedText.getBytes()));


        // Decryption
        String decryptedText = decrypt(Base64.getDecoder().decode(encryptedText.getBytes()), privateKey);
//        String decryptedText = decrypt(encryptedText.getBytes(), privateKey);
        System.out.println("DeCrypted Text : "+decryptedText);
    }

    public static byte[] encrypt (String plainText,PublicKey publicKey ) throws Exception
    {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plainText.getBytes()) ;

        return cipherText;
    }

    public static String decrypt (byte[] cipherTextArray, PrivateKey privateKey) throws Exception
    {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //Perform Decryption
        byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

        return new String(decryptedTextArray);
    }
}
