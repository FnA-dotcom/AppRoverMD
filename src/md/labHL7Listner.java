package md;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class labHL7Listner {
    public static void main(String[] args) {


        String path = "";
        String directory = "D:\\temp\\hl7";
        directory = "/sftpdrive/users/epowerdoc/lab-integration/Integration/Results";
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        try {

            Connection conn = getConnection();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                    path = directory;
                    readfilehl7(path, file.getName(), conn);
                    System.out.println("-----------------------------------------------------------");
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("ERROR in MAIN --> TAB " + e.getMessage());
        }
        //   measureTime("BufferedReader.readLine() into LinkedList", labh7result::bufferReaderToLinkedList, path);
        //   measureTime("BufferedReader.readLine() into ArrayList", labh7result::bufferReaderToArrayList, path);
        //   measureTime("Files.readAllLines()", labh7result::readAllLines, path);
        //   measureTime("Scanner.nextLine() into ArrayList", labh7result::scannerArrayList, path);
        //   measureTime("Scanner.nextLine() into LinkedList", labh7result::scannerLinkedList, path);
        //   measureTime("RandomAccessFile.readLine() into ArrayList", labh7result::randomAccessFileArrayList, path);
        //   measureTime("RandomAccessFile.readLine() into LinkedList", labh7result::randomAccessFileLinkedList, path);
        System.out.println("-----------------------------------------------------------");
    }

    public static void readfilehl7(String path, String filename, Connection conn) {
        try {
            String oid = "";
            String mrn = "";
            String t_result_status = "";
            String t_result_time = "";
            String t_result_lab = "";
            String t_result_note = "";
            String t_info = "";
            String t_id = "";
            String facilityid = "";
            String strCurrentLine = "";
            String pdfbyte = "";
            FileReader in = new FileReader(path + "/" + filename);

            BufferedReader Reader = new BufferedReader(in);

            while ((strCurrentLine = Reader.readLine()) != null) {

                //System.out.println(strCurrentLine.trim());
                if (strCurrentLine.startsWith("MSH")) {
                    facilityid = getvalue("MSH", 4, strCurrentLine.trim()).trim();
                }
                if (strCurrentLine.startsWith("PID")) {
                    mrn = getvalue("PID", 2, strCurrentLine.trim()).trim();
                    //mrn=getvalue("PID", 3,strCurrentLine.trim()).trim();

                }
                if (strCurrentLine.startsWith("OBX")) {
                    t_result_status = getvalue("OBX", 5, strCurrentLine.trim()).trim();
                    t_result_time = getvalue("OBX", 14, strCurrentLine.trim()).trim();
                    t_result_lab = getvalue("OBX", 15, strCurrentLine.trim()).trim();
                    t_result_note = getvalue("OBX", 7, strCurrentLine.trim()).trim();

                    t_info = getvalue("OBX", 3, strCurrentLine.trim()).trim();
                }
                if (strCurrentLine.startsWith("OBR")) {

                    oid = getvalue("OBR", 2, strCurrentLine.trim()).trim();
                }

                if (strCurrentLine.startsWith("ZEF")) {
                    pdfbyte = getvalue("ZEF", 2, strCurrentLine.trim()).trim();
                    writepdf(pdfbyte, filename);
                }
                //   System.out.println(getvalue("OBX", 3,strCurrentLine.trim()).trim());
                //   System.out.println(getvalue("OBX",5,strCurrentLine.trim()).trim());
                // System.out.println(getvalue("ZEF", 11,strCurrentLine.trim()).trim());
                //  String finalstring=facilityid+"|"+mrn+"|"+oid+"|"+t_id+"|"+t_result_status+"|"+t_result_note+"|"+t_result_time+"|"+t_result_lab+"|"+t_info;
                //  System.out.println(finalstring);


            }

/*            System.out.println("1**" + facilityid);
            System.out.println("2**" + oid);
            System.out.println("3**" + mrn);
            System.out.println("4**" + t_result_status);
            System.out.println("5**" + t_result_time);
            System.out.println("6**" + t_result_lab);
            System.out.println("7**" + t_result_note);
            System.out.println("8**" + t_id);
            System.out.println("9**" + t_info);*/
            updateresults(conn, facilityid, oid, t_id, mrn, t_result_status, t_result_time, t_result_lab, t_info, t_result_note, filename, path);

            in.close();

            //System.out.println(strCurrentLine);
            filemove(path, filename);


        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception EE --> " + e.getMessage());
        }


    }


    public static String getresultenam(String teststatus) {
        String _resultenam = "";
        teststatus = teststatus.toUpperCase();
		/* 1	BROKEN	0
		 2	NEGATIVE	0
		 3	POSITIVE	0
		 4	REJECTED	0
		 5	LOST	0
		 6	UNCONCLUSIVE	0*/

        if (teststatus.compareTo("NEGATIVE") == 0) {
            _resultenam = "2";
        }
        if (teststatus.compareTo("POSITIVE") == 0) {
            _resultenam = "3";
        }
        if (teststatus.compareTo("REJECTED") == 0) {
            _resultenam = "4";
        }
        if (teststatus.compareTo("LOST") == 0) {
            _resultenam = "5";
        }
        if (teststatus.compareTo("UNCONCLUSIVE") == 0) {
            _resultenam = "6";
        }


        return _resultenam;
    }

    private static void updateresults(Connection conn, String facilityid, String oid, String t_id, String mrn,
                                      String t_result_status, String t_result_time, String t_result_lab, String t_info, String t_result_note, String Filename, String Filepath) {
        Statement hstmt = null;
        Statement hstmt2 = null;
        ResultSet hrset2 = null;
        String Query2 = "";
        try {
            String Database = "roverlab";


            Query2 = "	select b.id from " + Database + ".TestOrder a," + Database + ".Tests b where a.id=b.OrderId and a.OrderNum='" + oid + "';							 ";
            hstmt2 = conn.createStatement();
            for (hrset2 = hstmt2.executeQuery(Query2); hrset2.next(); ) {
                t_id = hrset2.getString(1);
            }
            hrset2.close();
            hstmt2.close();


            String Reportpath = "/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports_final/";

            hstmt = conn.createStatement();
            String Query = "UPDATE " + Database + ".Tests SET TestStatus='" + getresultenam(t_result_status) + "',ResultDateTime=now(),Filepath='" + Filepath + "',Filename='" + Filename + "',Narration='" + t_result_note + "' WHERE id='" + t_id + "'";
            System.out.println(Query);
            hstmt.executeUpdate("UPDATE " + Database + ".Tests SET TestStatus='" + getresultenam(t_result_status) + "',ResultDateTime=now(),Filepath='" + Filepath + "',Filename='" + Filename + ".pdf',Narration='" + t_result_note + "',Reportpath='" + Reportpath + "' WHERE id='" + t_id + "'");
            hstmt.close();

            hstmt = conn.createStatement();
            System.out.println("UPDATE " + Database + ".TestOrder SET status=6 WHERE OrderNum='" + oid + "'");
            hstmt.executeUpdate("UPDATE " + Database + ".TestOrder SET status=6 WHERE OrderNum='" + oid + "'");
            hstmt.close();


        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

    }

    public static void writepdf(String encodedBytes, String filename) {

        try {

            //System.out.println(encodedBytes);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodedBytes = decoder.decodeBuffer(encodedBytes);

            File file = new File("/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports/" + filename + ".pdf");

            FileOutputStream fop = new FileOutputStream(file);

            fop.write(decodedBytes);
            fop.flush();
            fop.close();
            writelogopdf(filename);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void writelogopdf(String filename) {

        try {


            String inputFilePath = "/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports/" + filename + ".pdf";
            long unixTime = System.currentTimeMillis() / 1000L;
            final String outputFilePath = "/sftpdrive/users/epowerdoc/lab-integration/Integration/Reports_final/" + filename + ".pdf";
            final OutputStream fos3 = new FileOutputStream(new File(outputFilePath));
            final PdfReader pdfReader3 = new PdfReader(inputFilePath);
            final PdfStamper pdfStamper3 = new PdfStamper(pdfReader3, fos3);

            String path = "/sftpdrive/lab/image/logo1.png";

            for (int p = 1; p <= pdfReader3.getNumberOfPages(); ++p) {
                if (p == 1) {
                    // newtamplate.compute(MRN, "11222");
                    type1_02(pdfStamper3, p, path, 50.0f, 700.0f);
//                    type1_02(pdfStamper3, p, path, 340.0f, 700.0f);
//                    type1_01(pdfStamper3, p, "RoverMD.v1", 500.0f, 700.0f);
                }
            }

            pdfStamper3.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
    }

    public static void filemove(String path, String filename) {


        try {
            Files.move(Paths.get(path + "/" + filename), Paths.get("/sftpdrive/users/epowerdoc/lab-integration/Integration/sent/" + filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getvalue(String segment, int indexptr, String data) {

        String _value = "";
        if (data.startsWith(segment)) {

            String[] temp = data.split("\\|");
            int _lenght = getcount(data);

            if (indexptr >= _lenght) {
                _value = "-1";
            } else {

                _value = temp[indexptr];
            }

        }
        return _value.trim();
    }

    public static int getcount(String data) {

        int pipe = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == '|') pipe++;
        }

        return pipe;
    }

    private static void measureTime(String name, Function<String, List<String>> fn, String path) {
        System.out.println("-----------------------------------------------------------");
        System.out.println("run: " + name);
        long startTime = System.nanoTime();
        List<String> l = fn.apply(path);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("lines: " + l.size());
        System.out.println("estimatedTime: " + estimatedTime / 1_000_000_000.);
    }

    private static List<String> bufferReaderToLinkedList(String path) {
        return bufferReaderToList(path, new LinkedList<>());
    }

    private static List<String> bufferReaderToArrayList(String path) {
        return bufferReaderToList(path, new ArrayList<>());
    }

    private static List<String> bufferReaderToList(String path, List<String> list) {
        try {
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> readAllLines(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> randomAccessFileLinkedList(String path) {
        return randomAccessFile(path, new LinkedList<>());
    }

    private static List<String> randomAccessFileArrayList(String path) {
        return randomAccessFile(path, new ArrayList<>());
    }

    private static List<String> randomAccessFile(String path, List<String> list) {
        try {
            RandomAccessFile file = new RandomAccessFile(path, "r");
            String str;
            while ((str = file.readLine()) != null) {
                list.add(str);
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> scannerLinkedList(String path) {
        return scanner(path, new LinkedList<>());
    }

    private static List<String> scannerArrayList(String path) {
        return scanner(path, new ArrayList<>());
    }

    private static List<String> scanner(String path, List<String> list) {
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Connection getConnectionLocal() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://3.238.87.114:33306/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            final Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }

    public static void type1_01(PdfStamper stamper, int pageno, String Data, Float x, Float y) throws DocumentException, IOException {

        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        pdfContentByte.beginText();
        pdfContentByte.setFontAndSize(BaseFont.createFont("Times-Roman", "Cp1257", true), 10.0f);
        pdfContentByte.setColorFill(BaseColor.BLACK);
        pdfContentByte.setTextMatrix(x, y);
        pdfContentByte.showText(Data);
        pdfContentByte.endText();

    }

    public static void type1_02(PdfStamper stamper, int pageno, String Data, Float x, Float y) throws DocumentException, IOException {
        Image image = Image.getInstance(Data);
        PdfContentByte pdfContentByte = stamper.getOverContent(pageno);
        image.setAbsolutePosition(x, y);
        image.scaleToFit(250, 250);
        pdfContentByte.addImage(image);

    }


}
