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
 * How to Add Text To Existing PDF in java
 * Example Using iText library - core java tutorial
 */
public class AddTextToExistingPDF {

    public static void main(String[] args) throws Exception {


//        String inputFilePath = "C:/Users/Dev/Desktop/PrimescopeV3.pdf"; // Existing file
//        String outputFilePath = "C:\\Users\\Dev\\Desktop\\PrimescopeV3_NEW.pdf"; // New file


        String inputFilePath = "C:\\Users\\momehmood\\Downloads\\result_template.pdf"; // Existing file
        String outputFilePath = "C:\\Users\\momehmood\\Downloads\\result_template_new.pdf"; // New file
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
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(345, 715); // set x and y co-ordinates
                pdfContentByte.showText("CLIA ID :  45D2212719"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(445, 715); // set x and y co-ordinates
                pdfContentByte.showText("Lab Director : Shiraz Pirali, MD"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20, 685); // set x and y co-ordinates
                pdfContentByte.showText("Member Name "); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(205, 685); // set x and y co-ordinates
                pdfContentByte.showText("DOB"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(275, 685); // set x and y co-ordinates
                pdfContentByte.showText("GENDER"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(345, 685); // set x and y co-ordinates
                pdfContentByte.showText("ACCESSION#"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(409, 685); // set x and y co-ordinates
                pdfContentByte.showText("COLLECTED"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(495, 685); // set x and y co-ordinates
                pdfContentByte.showText("RECEIVED"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20, 655); // set x and y co-ordinates
                pdfContentByte.showText("Date Of Service"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(205, 655); // set x and y co-ordinates
                pdfContentByte.showText("LOCATION"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(495, 655); // set x and y co-ordinates
                pdfContentByte.showText("REPORTED"); // add the text
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20, 620); // set x and y co-ordinates
                pdfContentByte.showText("SARS-CoV-2 RT-PCR (Aries)"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20, 610); // set x and y co-ordinates
                pdfContentByte.showText("Sample Type: Nasopharyngeal Swab"); // add the text
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.WHITE);
                pdfContentByte.setTextMatrix(25, 580); // set x and y co-ordinates
                pdfContentByte.showText("Result Summary: Tests Performed, Non-Detected"); // add the text
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(20, 545); // set x and y co-ordinates
                pdfContentByte.showText("Test Name"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(230, 545); // set x and y co-ordinates
                pdfContentByte.showText("Result"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(315, 545); // set x and y co-ordinates
                pdfContentByte.showText("Comments"); // add the text
                pdfContentByte.endText();
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(495, 545); // set x and y co-ordinates
                pdfContentByte.showText("SEE"); // add the text
                pdfContentByte.endText();

            }


        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> " + outputFilePath);

    }

}