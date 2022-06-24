package md;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * How to Add Text To Existing PDF in java
 * Example Using iText library - core java tutorial
 */
public class AddTextToExistingPDF3 {

    public static void main(String[] args) throws Exception {


        String inputFilePath = "C:\\Users\\abid_\\Desktop\\ProCare\\FaceSheet_Frontline.pdf"; // Existing file
        String outputFilePath = "C:\\Users\\abid_\\Desktop\\ProCare\\FaceSheet_Frontline_New.pdf"; // New file
        OutputStream fos = new FileOutputStream(new File(outputFilePath));


        PdfReader pdfReader = new PdfReader(inputFilePath);
        int pages = pdfReader.getNumberOfPages();
        System.out.println("No of Pages: -- " + pages);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

        // loop on all the PDF pages
        // i is the pdfPageNumber
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

            if (i == 1) {
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.showTextAligned(Element.ALIGN_CENTER, "GOLDEN TRIANAGLE EMERGENCY CENTER ORANGE", 280, 740, 0);// add the text//BILLING Provider  facility TAX ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.showTextAligned(Element.ALIGN_CENTER, "17154 Butte CReek Road", 280, 725, 0);// add the text//BILLING Provider  facility TAX ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 12); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.showTextAligned(Element.ALIGN_CENTER, "Address", 280, 710, 0);// add the text//BILLING Provider  facility TAX ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(345, 540); // set x and y co-ordinates
                pdfContentByte.showText(" DR Murad Shaha");//(ER Physician
                pdfContentByte.endText();

            }


        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> " + outputFilePath);

    }

}