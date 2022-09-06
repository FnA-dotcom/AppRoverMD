package oe_2;

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

        String inputFilePath = "C:\\Users\\abid_\\Desktop\\label01.pdf"; // Existing file
        String outputFilePath = "C:\\Users\\abid_\\Desktop\\label01_abid.pdf"; // New file
        String CheckMark = "\u2688";
        String Lastname="Ali";
        String firstname="Muhammad Abid";
        OutputStream fos = new FileOutputStream(new File(outputFilePath));

        PdfReader pdfReader = new PdfReader(inputFilePath);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


        // loop on all the PDF pages
        // i is the pdfPageNumber
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

            //getOverContent() allows you to write content on TOP of existing pdf content.
            //getUnderContent() allows you to write content on BELOW of existing pdf content.

            if( i == 1){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, //Font name
                        BaseFont.CP1257, //Font encoding
                        BaseFont.EMBEDDED //Font embedded
                        )
                        , 10); // set font and size
                //  String bColor="";
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 520); // set x and y co-ordinates
                //0, 800 will write text on TOP LEFT of pdf page
                //0, 0 will write text on BOTTOM LEFT of pdf page
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 500); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 485); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 450); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 435); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 415); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 398); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 380); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(190, 380); // set x and y co-ordinates
                pdfContentByte.showText("YSchool textES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 365); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 348); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(200, 348); // set x and y co-ordinates
                pdfContentByte.showText("Magzine text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 330); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(220, 330); // set x and y co-ordinates
                pdfContentByte.showText("Newspaer text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 312); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(220, 312); // set x and y co-ordinates
                pdfContentByte.showText("Friend Faimily text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 295); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(220, 295); // set x and y co-ordinates
                pdfContentByte.showText("Urgent Care text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 278); // set x and y co-ordinates
                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(250, 278); // set x and y co-ordinates
                pdfContentByte.showText("Community Event text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 225); // set x and y co-ordinates
                pdfContentByte.showText("Work text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(180, 210); // set x and y co-ordinates
                pdfContentByte.showText("Physicain text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 195); // set x and y co-ordinates
                pdfContentByte.showText("Other text"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(80, 85); // set x and y co-ordinates
                pdfContentByte.showText("2020-04-20"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(480, 85); // set x and y co-ordinates
                pdfContentByte.showText("123445"); // add the text
                pdfContentByte.endText();
            }


            if(i==2) {
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                // Add text in existing PDF
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size
                pdfContentByte.setTextMatrix(105, 640); // set x and y co-ordinates

                pdfContentByte.showText(Lastname); // add the text
                pdfContentByte.endText();
                System.out.println("Text added in "+outputFilePath);
                BaseColor bColor = new BaseColor(0x00, 0xFF, 0x00);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(350, 640); // set x and y co-ordinates

                pdfContentByte.showText(firstname + CheckMark); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(500, 640); // set x and y co-ordinates

                pdfContentByte.showText("Middle Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 605); // set x and y co-ordinates

                pdfContentByte.showText("Title: Mr."); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(200, 600); // set x and y co-ordinates

                pdfContentByte.showText("Single"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(340, 600); // set x and y co-ordinates

                pdfContentByte.showText("14-11-1993"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(440, 600); // set x and y co-ordinates

                pdfContentByte.showText("26"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(510, 600); // set x and y co-ordinates

                pdfContentByte.showText("Male"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 8); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 570); // set x and y co-ordinates

                pdfContentByte.showText("House No R718 Sector 15-B Bufferzone North Karachi"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(280, 570); // set x and y co-ordinates

                pdfContentByte.showText("Karachi / Sindh / QW3RJG"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(460, 570); // set x and y co-ordinates

                pdfContentByte.showText("+923472063941"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 540); // set x and y co-ordinates

                pdfContentByte.showText("+1234567890"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(170, 540); // set x and y co-ordinates

                pdfContentByte.showText("Software Developer"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(350, 540); // set x and y co-ordinates

                pdfContentByte.showText("FAM"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(470, 540); // set x and y co-ordinates

                pdfContentByte.showText("+923472063941"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 510); // set x and y co-ordinates

                pdfContentByte.showText("Primary Care Physicaian"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(200, 510); // set x and y co-ordinates

                pdfContentByte.showText("muhammadabidali3@gmail.com"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(400, 510); // set x and y co-ordinates

                pdfContentByte.showText("Chest and Throat Infection"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 445); // set x and y co-ordinates

                pdfContentByte.showText("Is this a worker’s comp policy: YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(330, 445); // set x and y co-ordinates

                pdfContentByte.showText("Is this a Motor Vehicle Accident : NO"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 415); // set x and y co-ordinates

                pdfContentByte.showText("Primary Insurance"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(280, 415); // set x and y co-ordinates

                pdfContentByte.showText("Member ID"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(420, 415); // set x and y co-ordinates

                pdfContentByte.showText("Group Number"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 375); // set x and y co-ordinates

                pdfContentByte.showText("Primary Insurance Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 8); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 375); // set x and y co-ordinates

                pdfContentByte.showText("Address if Diffrent"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(460, 375); // set x and y co-ordinates

                pdfContentByte.showText("Karachi / Sindh / QW3ERJ"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 335); // set x and y co-ordinates

                pdfContentByte.showText("14-11-1993"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(160, 335); // set x and y co-ordinates

                pdfContentByte.showText("Primary SS"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(270, 335); // set x and y co-ordinates

                pdfContentByte.showText("Self"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(460, 335); // set x and y co-ordinates

                pdfContentByte.showText("+923472063941"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 300); // set x and y co-ordinates

                pdfContentByte.showText("Software Developer"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(160, 300); // set x and y co-ordinates

                pdfContentByte.showText("FAM"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 300); // set x and y co-ordinates

                pdfContentByte.showText("House No R718 Sector 15 B"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(460, 300); // set x and y co-ordinates

                pdfContentByte.showText("+923472063941"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 275); // set x and y co-ordinates

                pdfContentByte.showText("Secondry Insurance"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 275); // set x and y co-ordinates

                pdfContentByte.showText("Subscriber Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(420, 275); // set x and y co-ordinates

                pdfContentByte.showText("Subscriber Date of birth"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(60, 240); // set x and y co-ordinates

                pdfContentByte.showText("Self"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 240); // set x and y co-ordinates

                pdfContentByte.showText("Mem ID"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(420, 240); // set x and y co-ordinates

                pdfContentByte.showText("Grp Number"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 190); // set x and y co-ordinates

                pdfContentByte.showText("Next of kin"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(200, 190); // set x and y co-ordinates

                pdfContentByte.showText("Relationship to patient"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(350, 190); // set x and y co-ordinates

                pdfContentByte.showText("+923472063941"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(510, 190); // set x and y co-ordinates

                pdfContentByte.showText("YES"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 8); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(40, 150); // set x and y co-ordinates

                pdfContentByte.showText("Address if Diffrent from patient"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(350, 150); // set x and y co-ordinates

                pdfContentByte.showText("Karachi / Sindh / QWERJH"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(440, 70); // set x and y co-ordinates

                pdfContentByte.showText("DATE NOW()"); // add the text
                pdfContentByte.endText();



                System.out.println("Text added in "+outputFilePath);
            }

            if( i == 3){

                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(320, 230); // set x and y co-ordinates

                pdfContentByte.showText("24-06-2020"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(330, 150); // set x and y co-ordinates

                pdfContentByte.showText("24-06-2020"); // add the text
                pdfContentByte.endText();

            }

            if( i == 4){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(285, 70); // set x and y co-ordinates

                pdfContentByte.showText("24-06-2020"); // add the text
                pdfContentByte.endText();
            }

            if( i == 5){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(440, 395); // set x and y co-ordinates

                pdfContentByte.showText("24-06-2020"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 12); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(330, 250); // set x and y co-ordinates

                pdfContentByte.showText("Printed Name of Qualified Personal"); // add the text
                pdfContentByte.endText();
            }

            if( i == 6){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(95, 585); // set x and y co-ordinates

                pdfContentByte.showText("Patient Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(300, 585); // set x and y co-ordinates

                pdfContentByte.showText("Patient DOB"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(490, 585); // set x and y co-ordinates

                pdfContentByte.showText("Relation to memeber"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(120, 560); // set x and y co-ordinates

                pdfContentByte.showText("Insurance provider"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(180, 535); // set x and y co-ordinates

                pdfContentByte.showText("Member Identification Number"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 510); // set x and y co-ordinates

                pdfContentByte.showText("Date of Service"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 385); // set x and y co-ordinates

                pdfContentByte.showText("Patients Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(330, 210); // set x and y co-ordinates

                pdfContentByte.showText("24-06-2020"); // add the text
                pdfContentByte.endText();
            }

            if( i == 7){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(130, 490); // set x and y co-ordinates

                pdfContentByte.showText("Health Insurance"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 465); // set x and y co-ordinates

                pdfContentByte.showText("Subscriber"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(340, 465); // set x and y co-ordinates

                pdfContentByte.showText("Subscriber DOB"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(110, 440); // set x and y co-ordinates

                pdfContentByte.showText("MEMBER ID"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 440); // set x and y co-ordinates

                pdfContentByte.showText("GROUP ID"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(120, 415); // set x and y co-ordinates

                pdfContentByte.showText("Effective Date"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 240); // set x and y co-ordinates

                pdfContentByte.showText("Printed Name of the person completing form"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(430, 170); // set x and y co-ordinates

                pdfContentByte.showText("DATE"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_BOLD,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(430, 130); // set x and y co-ordinates

                pdfContentByte.showText("DATE"); // add the text
                pdfContentByte.endText();
            }

            if( i == 8){
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 650); // set x and y co-ordinates

                pdfContentByte.showText("PAtient Name"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(390, 650); // set x and y co-ordinates

                pdfContentByte.showText("Phone Number"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 610); // set x and y co-ordinates

                pdfContentByte.showText("DOB"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(390, 610); // set x and y co-ordinates

                pdfContentByte.showText("Socail Security"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(130, 480); // set x and y co-ordinates

                pdfContentByte.showText("Facility or Physician to receive information"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(130, 440); // set x and y co-ordinates

                pdfContentByte.showText("PHONE NUMBER"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(340, 440); // set x and y co-ordinates

                pdfContentByte.showText("FAX NUMBER"); // add the text
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(130, 400); // set x and y co-ordinates

                pdfContentByte.showText("House nio R718 sector 15 b bufferzone north karachi"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1257,BaseFont.EMBEDDED), 10); // set font and size

                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(130, 360); // set x and y co-ordinates

                pdfContentByte.showText("Karachi / Sindh / QWERTH"); // add the text
                pdfContentByte.endText();


            }


        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> "+ outputFilePath);

    }
}