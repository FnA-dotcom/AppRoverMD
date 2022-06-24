package orange_2;

import com.itextpdf.text.BaseColor;
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
public class AddTextToExistingPDF {

    public static void main(String[] args) throws Exception {


        String inputFilePath = "C:\\Users\\abid_\\Desktop\\GeneralForm_Victoria.pdf"; // Existing file
        String outputFilePath = "C:\\Users\\abid_\\Desktop\\GeneralForm_Victoria_Result.pdf"; // New file
        OutputStream fos = new FileOutputStream(new File(outputFilePath));


        PdfReader pdfReader = new PdfReader(inputFilePath);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


        // loop on all the PDF pages
        // i is the pdfPageNumber
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

            //getOverContent() allows you to write content on TOP of existing pdf content.
            //getUnderContent() allows you to write content on BELOW of existing pdf content.

            if (i == 1) {
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(70, 600); // set x and y co-ordinates
                pdfContentByte.showText("Ali"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 600); // set x and y co-ordinates
                pdfContentByte.showText("Muhammad Abid"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(390, 608); // set x and y co-ordinates
                pdfContentByte.showText("Mr."); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(450, 608); // set x and y co-ordinates
                pdfContentByte.showText("Mar"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 570); // set x and y co-ordinates
                pdfContentByte.showText("House no R718 Sector 15B Bufferzone NKhi"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(300, 570); // set x and y co-ordinates
                pdfContentByte.showText("Karachi/SIndh/75850"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(450, 570); // set x and y co-ordinates
                pdfContentByte.showText("4698759668"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 540); // set x and y co-ordinates
                pdfContentByte.showText("132456789"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(230, 540); // set x and y co-ordinates
                pdfContentByte.showText("11/14/1993"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(320, 540); // set x and y co-ordinates
                pdfContentByte.showText("26"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(380, 540); // set x and y co-ordinates
                pdfContentByte.showText("Male"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(435, 540); // set x and y co-ordinates
                pdfContentByte.showText("muhammadabidali3@gmail.com"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 515); // set x and y co-ordinates
                pdfContentByte.showText("Asian"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(230, 515); // set x and y co-ordinates
                pdfContentByte.showText("Employer"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 10); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(400, 515); // set x and y co-ordinates
                pdfContentByte.showText("Occupation"); // add the text
                pdfContentByte.endText();

            }


        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> " + outputFilePath);

    }

}