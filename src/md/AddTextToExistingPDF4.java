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
@SuppressWarnings("Duplicates")
public class AddTextToExistingPDF4 {

    public static void main(String[] args) throws Exception {


//        String inputFilePath = "C:/Users/Dev/Desktop/PrimescopeV3.pdf"; // Existing file
//        String outputFilePath = "C:\\Users\\Dev\\Desktop\\PrimescopeV3_NEW.pdf"; // New file


        String inputFilePath = "E:\\COVID_REGNEW.pdf"; // Existing file
        String outputFilePath = "E:\\COVID_REGNEW_new.pdf"; // New file
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
                pdfContentByte.setTextMatrix(70, 710); // set x and y co-ordinates
                pdfContentByte.showText("Tabish"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(190, 710); // set x and y co-ordinates
                pdfContentByte.showText("Hafeez"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(280, 710); // set x and y co-ordinates
                pdfContentByte.showText("S"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(400, 710); // set x and y co-ordinates
                pdfContentByte.showText("Primescope Diagnostics"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(80, 685); // set x and y co-ordinates
                pdfContentByte.showText("17154 butte creek road TX, houston, United States"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(370, 685); // set x and y co-ordinates
                pdfContentByte.showText("Dr, Joel Persall"); // add the text
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(500, 685); // set x and y co-ordinates
                pdfContentByte.showText("123456789"); // NPI
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(80, 665); // set x and y co-ordinates
                pdfContentByte.showText("Houston"); // City
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(170, 665); // set x and y co-ordinates
                pdfContentByte.showText("TX"); // State
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(250, 665); // set x and y co-ordinates
                pdfContentByte.showText("779900"); // ZipCode
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(370, 665); // set x and y co-ordinates
                pdfContentByte.showText("11822 Westheimer Rd, Houston, TX 77077"); // Primescope Address
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(70, 648); // set x and y co-ordinates
                pdfContentByte.showText("4694980033"); // Ph
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(170, 648); // set x and y co-ordinates
                pdfContentByte.showText("01/25/1992"); // DOB
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(265, 648); // set x and y co-ordinates
                pdfContentByte.showText("Male"); // Gender
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(345, 648); // set x and y co-ordinates
                pdfContentByte.showText("Houston"); // City
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(430, 648); // set x and y co-ordinates
                pdfContentByte.showText("Texas"); // State
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(505, 648); // set x and y co-ordinates
                pdfContentByte.showText("698741"); // Zip
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(30, 620); // set x and y co-ordinates
                pdfContentByte.showText("American Indian or Alaska Native"); // Race
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(145, 624); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Ethnicity
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(215, 624); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Ethnicity
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(144, 614); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Ethnicity
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(190, 614); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Ethnicity
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(345, 622); // set x and y co-ordinates
                pdfContentByte.showText("789654123"); // Phone
                pdfContentByte.endText();


                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(460, 622); // set x and y co-ordinates
                pdfContentByte.showText("789654123"); // Fax
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(60, 603); // set x and y co-ordinates
                pdfContentByte.showText("tabish.hafeez@fam-llc.com"); // Email
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(353, 603); // set x and y co-ordinates
                pdfContentByte.showText("tabish.hafeez@fam-llc.com"); // Email
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(229, 570); // set x and y co-ordinates
                pdfContentByte.showText("*"); // have Insurance (YES)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(254, 570); // set x and y co-ordinates
                pdfContentByte.showText("*"); // have Insurance (NO)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(80, 555); // set x and y co-ordinates
                pdfContentByte.showText("BCBS"); // Insurance
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(490, 552); // set x and y co-ordinates
                pdfContentByte.showText("999999999"); // SSN
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(490, 538); // set x and y co-ordinates
                pdfContentByte.showText("999999999"); // DL
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(490, 523); // set x and y co-ordinates
                pdfContentByte.showText("999999999"); // StateID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(70, 530); // set x and y co-ordinates
                pdfContentByte.showText("TABISH HAFEEZ"); // Policy Holder Name
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(170, 530); // set x and y co-ordinates
                pdfContentByte.showText("01/25/1992"); // Policy Holder DOB
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(270, 530); // set x and y co-ordinates
                pdfContentByte.showText("65874"); // Member ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(70, 508); // set x and y co-ordinates
                pdfContentByte.showText("SELF"); // Relationship
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 508); // set x and y co-ordinates
                pdfContentByte.showText("UYTRW"); // Policy Type
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(260, 508); // set x and y co-ordinates
                pdfContentByte.showText("GRP-0987"); // Group
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(36, 452); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Symptomatic
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(63, 452); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Symptomatic
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(87, 452); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Symptomatic
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(366, 480); // set x and y co-ordinates
                pdfContentByte.showText("*"); // First Test(YES)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(328, 469); // set x and y co-ordinates
                pdfContentByte.showText("*"); // First Test(NO)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(350, 469); // set x and y co-ordinates
                pdfContentByte.showText("*"); // First Test(UNKNOWN)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(410, 472); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Employed in Healthcare(YES)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(435, 472); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Employed in Healthcare(No)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(457, 472); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Employed in Healthcare(UNKNOWN)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(548, 482); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Pregnant(Yes)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(512, 472); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Pregnant(No)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(533, 472); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Pregnant(Unknown)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(401, 455); // set x and y co-ordinates
                pdfContentByte.showText("*"); // ICU for COVID-19(Yes)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(426, 455); // set x and y co-ordinates
                pdfContentByte.showText("*"); // ICU for COVID-19(No)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(448, 455); // set x and y co-ordinates
                pdfContentByte.showText("*"); // ICU for COVID-19(Unknown)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(203, 445); // set x and y co-ordinates
                pdfContentByte.showText("1"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(212, 445); // set x and y co-ordinates
                pdfContentByte.showText("2"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(223, 445); // set x and y co-ordinates
                pdfContentByte.showText("0"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(232, 445); // set x and y co-ordinates
                pdfContentByte.showText("2"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(243, 445); // set x and y co-ordinates
                pdfContentByte.showText("2"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(252, 445); // set x and y co-ordinates
                pdfContentByte.showText("2"); // Symptomatic(DATE)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(160, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Resident in congregate(YES)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(185, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Resident in congregate(NO)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(208, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Resident in congregate(Unknown)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(429, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Hospitalized for COVID-19(Yes)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(454, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Hospitalized for COVID-19(No)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(478, 432); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Hospitalized for COVID-19(Unknown)
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(40, 360); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Patient Has Insurance Coverage
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 7); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.RED);
                pdfContentByte.setTextMatrix(40, 297); // set x and y co-ordinates
                pdfContentByte.showText("*"); // Patient Does Not Have Insurance Coverage
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(70, 236); // set x and y co-ordinates
                pdfContentByte.showText("Tabish Hafeez"); // Name
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(300, 236); // set x and y co-ordinates
                pdfContentByte.showText("Tabish Hafeez"); // Signature
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(500, 236); // set x and y co-ordinates
                pdfContentByte.showText("20/05/2022"); // Date
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 107); // set x and y co-ordinates
                pdfContentByte.showText("Test#123"); // Specimen ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // Specimen ID
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(150, 107); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time Collected
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(450, 107); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(50, 68); // set x and y co-ordinates
                pdfContentByte.showText("COVID-19 PCT Alina"); // Test Type
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(215, 68); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // Date & Time Collected
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(315, 68); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // Date of Onset
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(415, 68); // set x and y co-ordinates
                pdfContentByte.showText(" "); // Result
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, BaseFont.EMBEDDED), 9); // set fonts zine and name
                pdfContentByte.setColorFill(BaseColor.BLACK);
                pdfContentByte.setTextMatrix(427, 155); // set x and y co-ordinates
                pdfContentByte.showText("02/25/2022 01:05:58"); // MEDICAL NECESSITY Date
                pdfContentByte.endText();
            }


        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> " + outputFilePath);

    }

}