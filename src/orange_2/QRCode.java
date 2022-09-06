package orange_2;


import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.WriterException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRCode {

    public static void main(String[] args) throws WriterException, IOException{
        String qrCodeData = "123456789012";
        String filePath = "d://QRCode.png";
        String charset = "UTF-8"; // or "ISO-8859-1"
        Map hintMap = new HashMap();
        //hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintMap.put(EncodeHintType.CHARACTER_SET, 0);
//		createQRCode(qrCodeData, filePath, charset, hintMap, 100, 200);

        //	generateEAN13BarcodeImage();
        System.out.println("QR Code image created successfully!");

//		System.out.println("Data read from QR Code: "+ readQRCode(filePath, charset, hintMap));


    }

    // QRCode.genbarcode("/opt/logs/barcode/"+mrn,mrn);
    public static String genbarcode(String filePath, String qrCodeData, int x, int y) {
//		//String qrCodeData = "123456789012";
//		//String filePath = "d://QRCode.png";
//		String charset = "UTF-8"; // or "ISO-8859-1"
//		Map hintMap = new HashMap();
//		String done="0";
//		//hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//		hintMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
//		hintMap.put(EncodeHintType.MARGIN,0);
//		try {
////			createQRCode(qrCodeData, filePath, charset, hintMap, 100, 200);
//			done="1";
//			return done;
//		}
//		catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return done;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return done;
//		}
        return "";
    }
//	public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
//	    EAN13Writer barcodeWriter = new EAN13Writer();
//	    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);
//
//	    return MatrixToImageWriter.toBufferedImage(bitMatrix);
//	}
//
//	public static void createQRCode(String qrCodeData, String filePath,
//			String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
//			throws WriterException, IOException {
//		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
//				BarcodeFormat.UPC_A, qrCodewidth, qrCodeheight, hintMap);
//		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
//	}

//	public static String readQRCode(String filePath, String charset, Map hintMap)
//			throws FileNotFoundException, IOException, NotFoundException {
//		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
//				new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
//		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,hintMap);
//		return qrCodeResult.getText();
//	}
}