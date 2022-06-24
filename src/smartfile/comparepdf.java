package smartfile;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class comparepdf {

    public static void main(String[] args) throws IOException {

        String file = "450_4424_LOWERY_SARA_18639_202012071031_20201207141153_EDPHYS.PDF";
        String file2 = "LOWERY_2.PDF";


        arePDFFilesEqual(new File("d://" + file), new File("d://" + file2));
      /*  try (PDDocument document = PDDocument.load(new File("d://"+file))) {
        	
            document.getClass();
            String DOB="";

            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);
                //System.out.println("Text:" + pdfFileInText);
                PDDocumentInformation docid=document.getDocumentInformation() ;
                docid.getTitle();
               
            	System.out.println(docid);
				// split by whitespace
                String lines[] = pdfFileInText.split("\\r?\\n");
                for (String line : lines) {
                    System.out.println(line);
                    if(line.startsWith("Patient:")) {
                    	System.out.println(line);
                    }if(line.startsWith("DOS:")) {
                    	System.out.println(line);
                    	 String DOSRaw=line;
                    	  String DOSArr[]=DOSRaw.split("\\s+");
                          String DOS = DOSArr[1];
                          String DOSFormatArr[]=DOS.split("\\/");
                          DOS = DOSFormatArr[2]+"-"+DOSFormatArr[0]+"-"+DOSFormatArr[1] + " " + DOSArr[2] + ":00";
                      	System.out.println("||"+DOS);
                    	
                    	
                    }if(line.startsWith("MRN:")) {
                    	System.out.println(line);
                    	String MRNRaw=line;
                    	String MRNAR[]=MRNRaw.split("\\s+");
                    	
                    	System.out.println(MRNAR[0]);
                    	System.out.println(MRNAR[1]);
                    	System.out.println(MRNAR[2]);
                    	System.out.println(isStringOnlyAlphabet(MRNAR[1].replace(":", "")));
                    	;
                    	
                    }if(line.startsWith("Acct:")) {
                    	System.out.println(line);
                    	  
                    	
                    	
                    }if(line.startsWith("DOB:")) {
                    	System.out.println(line);
                    	 String DOBRaw=line;
                         String DOBArr[]=DOBRaw.split("\\s+");
                         DOB = DOBArr[1];
                         String DOBFormatArr[]=DOB.split("\\/");
                         DOB = DOBFormatArr[2]+"-"+DOBFormatArr[0]+"-"+DOBFormatArr[1];
                         
                         System.out.println("DOB "+DOB);
                       //  System.out.println("YEAR "+DOBArr[2]);
                         System.out.println("YEAR "+DOBArr[2]);
                         String gender[] =DOBArr[3].split("/");
                         System.out.println("gender "+gender[1]);
                    	
                    }if(line.startsWith("PROVIDER:")) {
                    	System.out.println(line);
                    }if(line.startsWith("CHIEF COMPLAINT:")) {
                    	
                    }
                }

            }

        }*/

    }

    private static void arePDFFilesEqual(File pdfFile1, File pdfFile2) throws IOException {
        System.out.print("Comparing PDF files (" + pdfFile1 + "," + pdfFile2 + ")");
        PDDocument pdf1 = PDDocument.load(pdfFile1);
        PDDocument pdf2 = PDDocument.load(pdfFile2);
        PDPageTree pdf1pages = pdf1.getDocumentCatalog().getPages();
        PDPageTree pdf2pages = pdf2.getDocumentCatalog().getPages();
        String name = pdf2.getDocumentCatalog().getNames().toString();
        try {
            System.out.println(name);
            if (pdf1pages.getCount() != pdf2pages.getCount()) {
                String message = "Number of pages in the files (" + pdfFile1 + "," + pdfFile2 + ") do not match. pdfFile1 has " + pdf1pages.getCount() + " no pages, while pdf2pages has " + pdf2pages.getCount() + " no of pages";
                System.out.println(message);
                //throw new TestException(message);
            }
            PDFTextStripper pdfStripper = new PDFTextStripper();
            System.out.println("pdfStripper is :- " + pdfStripper);
            System.out.println("pdf1pages.size() is :- " + pdf1pages.getCount());
            for (int i = 0; i < pdf1pages.getCount(); i++) {
                pdfStripper.setStartPage(i + 1);
                pdfStripper.setEndPage(i + 1);
                String pdf1PageText = pdfStripper.getText(pdf1);
                String pdf2PageText = pdfStripper.getText(pdf2);
                String message = "Contents of the files (" + pdfFile1 + "," + pdfFile2 + ") do not match on Page no: " + (i + 1) + " pdf1PageText is : " + pdf1PageText + " , while pdf2PageText is : " + pdf2PageText;
                System.out.println(message);
                if (!pdf1PageText.equals(pdf2PageText)) {
                    String message1 = "Contents of the files (" + pdfFile1 + "," + pdfFile2 + ") do not match on Page no: " + (i + 1) + " pdf1PageText is : " + pdf1PageText + " , while pdf2PageText is : " + pdf2PageText;
                    System.out.println(message1);
                    System.out.println("fff");
                    System.out.println("pdf1PageText is " + pdf1PageText);
                    System.out.println("pdf2PageText is " + pdf2PageText);
                    //String difference = String.d(pdf1PageText, pdf2PageText);
                    // System.out.println("difference is "+difference);
                    // throw new TestException(message+" [[ Difference is ]] "+difference);
                }
            }
            System.out.println("Returning True , as PDF Files (" + pdfFile1 + "," + pdfFile2 + ") get matched");
        } finally {
            pdf1.close();
            pdf2.close();
        }
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null) && (!str.equals("")) && (str.matches("^[a-zA-Z]*$")));
    }
}