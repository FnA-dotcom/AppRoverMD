package smartfile;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;

public class ReadPdf {

    public static void main(String[] args) throws IOException {

        String PatientName = "";String MRN = ""; String DOB = ""; String DOS = "";String Acct = "";String ChComplaint ="";String printabledate = "";
        String firstname = "";
        String lastname = "";
        try (PDDocument document = PDDocument.load(new File("C:/Users/abid_/Desktop/328_32434022_HUGHES_KIESHA_21079_202009051641_20201013152324_EDRN.PDF"))) {
            //document.getPage(0);
            document.getClass();

            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();
                tStripper.getStartPage();

                String pdfFileInText = tStripper.getText(document);
//                System.out.println("pdfFileInText: " + pdfFileInText);
                int ii=0;
                int iChComplaint=0;
                // split by whitespace
                String lines[] = pdfFileInText.split("\\r?\\n");
                ChComplaint = lines[12];
                for (String line : lines) {

                    if(line.startsWith("Patient:")) {
                        String PatientNameRaw=line;
                        String PtArr[]=PatientNameRaw.split("\\s+");
                        lastname = PtArr[1];
                        lastname = StringUtils.chop(lastname);
                        firstname = PtArr[2];
                    }
                    if(line.startsWith("DOB:")) {
                        String DOBRaw=line;
                        String DOBArr[]=DOBRaw.split("\\s+");
                        DOB = DOBArr[1];
                        String DOBFormatArr[]=DOB.split("\\/");
                        DOB = DOBFormatArr[2]+"-"+DOBFormatArr[0]+"-"+DOBFormatArr[1];
                    }
                    if(line.startsWith("MRN:")) {
                        String MRNRaw=line;
                        String MRNAR[]=MRNRaw.split("\\s+");
                        MRN = MRNAR[1];
//                        System.out.println(MRNAR[0]);
//                        System.out.println(MRNAR[1]);
//                        System.out.println(MRNAR[2]);
                    }
                    if(line.startsWith("Acct #:")) {
                        String AcctRaw=line;
                        String AcctArr[]=AcctRaw.split("\\s+");
                        Acct = AcctArr[2]; //+ AcctArr[5];
                        String printabledateRaw = AcctArr[4] ;
                        String printabledateArr[] = printabledateRaw.split("\\/");
                        printabledate = printabledateArr[2]+"-"+printabledateArr[0]+"-"+printabledateArr[1] + " " + AcctArr[5] + ":00";
                    }
                    if(line.startsWith("DOS:")) {
                        String DOSRaw=line;
                        String DOSArr[]=DOSRaw.split("\\s+");
                        DOS = DOSArr[1];
                        String DOSFormatArr[]=DOS.split("\\/");
                        DOS = DOSFormatArr[2]+"-"+DOSFormatArr[0]+"-"+DOSFormatArr[1] + " " + DOSArr[2] + ":00";
                    }
                    if(line.startsWith("CHIEF COMPLAINT:")) {

                        iChComplaint=ii+1;
                    }
                    if(iChComplaint==ii) {
                        ChComplaint=line;
                    }

                    ii++;
                }
            }
        }
        System.out.println(firstname);
        System.out.println(lastname);
        System.out.println(MRN);
        System.out.println(DOS);
        System.out.println(Acct);
        System.out.println(DOB);
        System.out.println(ChComplaint);
        System.out.println(printabledate);

    }
}