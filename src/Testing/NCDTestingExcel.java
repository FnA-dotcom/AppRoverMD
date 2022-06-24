package Testing;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class NCDTestingExcel {
    public static void main(String[] args) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String icdTERMDate = "";
        String icdEFFDate = "";
        String cptEFFDate = "";
        int i = 0;
        int CPT = 0;
        int resCode = 0;
        float _bIndex_ = 0;
        DecimalFormat df = new DecimalFormat("#");
        try {
            FileInputStream fileIn = null;
            fileIn = new FileInputStream("E:\\NCD.xlsx");
            Workbook workbook = WorkbookFactory.create(fileIn);
            Sheet sheet = workbook.getSheetAt(0);
            int totalrows = sheet.getLastRowNum();
            System.out.println("TOTAL ROWS --> " + totalrows);
            for (i = 0; i <= 5; i++) {
                Row row = sheet.getRow(i);

                _bIndex_ = row.getCell(0).getCellType();
                if (_bIndex_ == 1) {
                    Query = row.getCell(0).getStringCellValue();
                    if ((Query.length() <= 0) || (Query.equals("NCD Number"))) {
                        continue;
                    }
                    _bIndex_ = Integer.parseInt(Query);
                } else {
                    _bIndex_ = (float) row.getCell(0).getNumericCellValue();
                }

                System.out.println(" NCD Number " + _bIndex_);
                System.out.println("NCD Descr" + row.getCell(1).getStringCellValue());
                CPT = row.getCell(2).getCellType();
                if (CPT == 1) {
                    System.out.println("CPT STR " + row.getCell(2).getStringCellValue());
                } else {
                    System.out.println("CPT INT " + df.format(row.getCell(2).getNumericCellValue()));
                }

                if (row.getCell(3).getStringCellValue().length() > 1) {
                    if (row.getCell(3).getDateCellValue().getDate() < 10) {
                        cptEFFDate = "0" + row.getCell(3).getDateCellValue().getDate();
                    } else {
                        cptEFFDate = "" + row.getCell(3).getDateCellValue().getDate();
                    }

                    if (row.getCell(3).getDateCellValue().getMonth() + 1 < 10) {
                        cptEFFDate = cptEFFDate + "-0" + (row.getCell(3).getDateCellValue().getMonth() + 1);
                    } else {
                        cptEFFDate = cptEFFDate + "-" + (row.getCell(3).getDateCellValue().getMonth() + 1);
                    }

                    if (row.getCell(3).getDateCellValue().getYear() + 1 < 10) {
                        cptEFFDate = cptEFFDate + cptEFFDate + "-0" + (row.getCell(3).getDateCellValue().getYear() + 1);
                    } else {
                        cptEFFDate = cptEFFDate + cptEFFDate + "-" + (row.getCell(3).getDateCellValue().getYear() + 1);
                    }
                }

                System.out.println(" cptEFFDate " + cptEFFDate);

                System.out.println("ICD " + row.getCell(5).getStringCellValue());

                if (row.getCell(6).getStringCellValue().length() > 1) {
                    if (row.getCell(6).getDateCellValue().getDate() < 10) {
                        icdEFFDate = "0" + row.getCell(6).getDateCellValue().getDate();
                    } else {
                        icdEFFDate = "" + row.getCell(6).getDateCellValue().getDate();
                    }
                    if (row.getCell(6).getDateCellValue().getMonth() + 1 < 10) {
                        icdEFFDate = icdEFFDate + "-0" + (row.getCell(6).getDateCellValue().getMonth() + 1);
                    } else {
                        icdEFFDate = icdEFFDate + "-" + (row.getCell(6).getDateCellValue().getMonth() + 1);
                    }

                    if (row.getCell(6).getDateCellValue().getYear() + 1 < 10) {
                        icdEFFDate = icdEFFDate + icdEFFDate + "-0" + (row.getCell(6).getDateCellValue().getYear() + 1);
                    } else {
                        icdEFFDate = icdEFFDate + icdEFFDate + "-" + (row.getCell(6).getDateCellValue().getYear() + 1);
                    }
                }

                System.out.println(" icdEFFDate " + icdEFFDate);

                if (row.getCell(7).getStringCellValue().length() > 1) {
                    if (row.getCell(7).getDateCellValue().getDate() < 10) {
                        icdTERMDate = "0" + row.getCell(7).getDateCellValue().getDate();
                    } else {
                        icdTERMDate = "" + row.getCell(7).getDateCellValue().getDate();
                    }
                    if (row.getCell(7).getDateCellValue().getMonth() + 1 < 10) {
                        icdTERMDate = icdTERMDate + "-0" + (row.getCell(7).getDateCellValue().getMonth() + 1);
                    } else {
                        icdTERMDate = icdTERMDate + "-" + (row.getCell(7).getDateCellValue().getMonth() + 1);
                    }

                    if (row.getCell(6).getDateCellValue().getYear() + 1 < 10) {
                        icdTERMDate = icdTERMDate + icdTERMDate + "-0" + (row.getCell(7).getDateCellValue().getYear() + 1);
                    } else {
                        icdTERMDate = icdTERMDate + icdTERMDate + "-" + (row.getCell(7).getDateCellValue().getYear() + 1);
                    }
                }
                System.out.println(" icdTERMDate " + icdTERMDate);
                resCode = row.getCell(8).getCellType();
                if (resCode == 1) {
                    System.out.println("resCode STR " + row.getCell(8).getStringCellValue());
                } else {
                    System.out.println("resCode INT " + df.format(row.getCell(8).getNumericCellValue()));
                }

            }
























            /*
            File file = new File("E:\\NCD.xlsx");   //creating a new file instance
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
//creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:    //field that represents string cell type
                            System.out.print("STRING" + cell.getStringCellValue() + "\t\t\t");
                            break;
                        case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type
                            System.out.print("NUMBER " + cell.getNumericCellValue() + "\t\t\t");
                            break;
                        default:
                    }
                }
                System.out.println("");

            }*/
        } catch (Exception Ex) {
            System.out.println("Exception occurred " + Ex.getMessage());
        }

    }

    private Dictionary doUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        try {
            String boundary = request.getHeader("Content-Type");
            int pos = boundary.indexOf('=');
            boundary = boundary.substring(pos + 1);
            boundary = "--" + boundary;
            byte[] bytes = new byte[512];
            int state = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            String name = null;
            String value = null;
            String filename = null;
            String contentType = null;
            Dictionary<Object, Object> fields = new Hashtable<>();
            ServletInputStream in = request.getInputStream();
            int i;
            for (i = in.readLine(bytes, 0, 512); -1 != i; i = in.readLine(bytes, 0, 512)) {
                String st = new String(bytes, 0, i);
                if (st.startsWith(boundary)) {
                    state = 0;
                    if (name != null) {
                        if (value != null) {
                            fields.put(name, value.substring(0, value.length() - 2));
                        } else if (buffer.size() > 2) {
                            fields.put(filename, buffer);
                        }
                        name = null;
                        value = null;
                        filename = null;
                        contentType = null;
                        buffer = new ByteArrayOutputStream();
                    }
                } else if (st.startsWith("Content-Disposition: form-data") && state == 0) {
                    StringTokenizer tokenizer = new StringTokenizer(st, ";=\"");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.startsWith(" name")) {
                            name = tokenizer.nextToken();
                            state = 2;
                            continue;
                        }
                        if (token.startsWith(" filename")) {
                            filename = tokenizer.nextToken();
                            StringTokenizer ftokenizer = new StringTokenizer(filename, "\\/:");
                            filename = ftokenizer.nextToken();
                            while (ftokenizer.hasMoreTokens())
                                filename = ftokenizer.nextToken();
                            state = 1;
                            break;
                        }
                    }
                } else if (st.startsWith("Content-Type") && state == 1) {
                    pos = st.indexOf(":");
                    st.substring(pos + 2, st.length() - 2);
                } else if (st.equals("\r\n") && state == 1) {
                    state = 3;
                } else if (st.equals("\r\n") && state == 2) {
                    state = 4;
                } else if (state == 4) {
                    value = value + st;
                } else if (state == 3) {
                    buffer.write(bytes, 0, i);
                }
            }
            return fields;
        } catch (Exception var20) {
            throw new Exception("Error In Do Upload " + var20.getMessage());
        }
    }

}
