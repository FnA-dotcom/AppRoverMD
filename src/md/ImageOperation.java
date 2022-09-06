package md;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

public class ImageOperation {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', '#', '3', 'B', '3', '$', 'T',
                    '$', '3', 'C', 'r', '3', 't', 'K', '3', 'Q'};

    public static void operate(int key)
    {
        try
        {
//            FileInputStream fis=new FileInputStream("C:\\Users\\Dev\\Desktop\\image_test.png");
//
//            byte []data=new byte[fis.available()];
//            fis.read(data);
//
//            //AES
////          data=encrypt(data);
//            data=decrypt(data);
//
//            //XOR
////            int i=0;
////            for(byte b:data)
////            {
////                System.out.println(b);
////                data[i]=(byte)(b^key);
////                i++;
////            }
//
//            FileOutputStream fos=new FileOutputStream("C:\\Users\\Dev\\Desktop\\image_test.png");
//            fos.write(data);
//            fos.close();
//            fis.close();

            FileInputStream fis=new FileInputStream("/sftpdrive/AdmissionBundlePdf/SignImg/erDallas/");
            byte []data=new byte[fis.available()];
            fis.read(data);
            data=encrypt(data);
            FileOutputStream foss=new FileOutputStream("/sftpdrive/AdmissionBundlePdf/SignImg/erDallas/");
            foss.write(data);
            foss.close();
            fis.close();


        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        int key=951753;
        operate(key);
    }

    public static byte[] encrypt(byte[] Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data);
//        byte[] encryptedValue = new BASE64Encoder().encode(Data);
        return encVal;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(encryptedData);
//        String decryptedValue = new String(decValue);
        return decValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }
}