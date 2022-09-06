package md;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 *
 *  How to Add Text To Existing PDF in java
 *  Example Using iText library - core java tutorial
 *
 */
public class AddTextToExistingPDF {

    public static void main(String[] args) throws Exception {


//        String inputFilePath = "C:/Users/Dev/Desktop/PrimescopeV3.pdf"; // Existing file
//        String outputFilePath = "C:\\Users\\Dev\\Desktop\\PrimescopeV3_NEW.pdf"; // New file


        String inputFilePath = "C:\\Users\\momehmood\\Desktop\\ORIGINALS\\manifestFormat.pdf"; // Existing file
        String outputFilePath = "C:\\Users\\momehmood\\Desktop\\ORIGINALS\\manifestFormat-NEW.pdf"; // New file
        OutputStream fos = new FileOutputStream(new File(outputFilePath));


        PdfReader pdfReader = new PdfReader(inputFilePath);
        int pages = pdfReader.getNumberOfPages();
        System.out.println("No of Pages: -- "+pages);
        PdfStamper pdfStamper3 = new PdfStamper(pdfReader, fos);


        // loop on all the PDF pages
        // i is the pdfPageNumber
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

            if (i == 1) {
                final PdfContentByte pdfContentByte3 = pdfStamper3.getOverContent(i);
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(120.0f, 590.0f);
                pdfContentByte3.showText("NAME");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(300.0f, 590.0f);
                pdfContentByte3.showText("DOB");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(485.0f, 590.0f);
                pdfContentByte3.showText("HISubscriberRelationtoPatient");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(150.0f, 565.0f);
                pdfContentByte3.showText("HIPrimaryInsurance");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(150.0f, 540.0f);
                pdfContentByte3.showText("HISubscriberPolicyNo");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(150.0f, 520.0f);
                pdfContentByte3.showText("DOS");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(150.0f, 420.0f);
                pdfContentByte3.showText("Name");
                pdfContentByte3.endText();
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(380.0f, 420.0f);
                pdfContentByte3.showText("Victoria ER");
                pdfContentByte3.endText();


//                if (SignImages != null) {
//                    SignImages.setAbsolutePosition(100.0f, 160.0f);
//                    pdfContentByte3.addImage(SignImages);
//                }
                pdfContentByte3.beginText();
                pdfContentByte3.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
                pdfContentByte3.setColorFill(BaseColor.BLACK);
                pdfContentByte3.setTextMatrix(440.0f, 160.0f);
                pdfContentByte3.showText("Date");
                pdfContentByte3.endText();
            }


        }

        pdfStamper3.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> "+ outputFilePath);

    }

}