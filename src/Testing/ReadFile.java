package Testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile {
    public static void main(String[] args) {
        try {
            File myObj = new File("D:\\EBR_FILES\\EBR-BCBSTX8498-202206241145-001.ebr");
            Scanner myReader = new Scanner(myObj);
            String data = null;
            String fileControllNumber = null;
            String PCN = null;
            String Comment = null;
            String[] dataArr = null;
            int lineCounter=1;
            int claimCounter=0;
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                dataArr = data.split("\\|");
                if(lineCounter==1)
                    fileControllNumber = dataArr[5];

                if(lineCounter==3) {
                    PCN = dataArr[4];
                }

                if (lineCounter == 4) {
                    if (dataArr[0].compareTo("3c") == 0) {
                        Comment = dataArr[4];
                        claimCounter++;
                        System.out.println("fileControllNumber ->  " + fileControllNumber);
                        System.out.println("PCN ->  " + PCN);
                        System.out.println("Comment ->  " + Comment);
                        lineCounter = 0;
                    }else if(dataArr[0].compareTo("3e") == 0 && !myReader.hasNextLine()){
                        Comment = dataArr[4];
                        System.out.println("Comment ->  " + Comment);
                        lineCounter=0;
                        claimCounter++;
                    }else if(dataArr[0].compareTo("3e") == 0){
                        while(myReader.hasNextLine()){
                            data = myReader.nextLine();
                            dataArr = data.split("\\|");
                            Comment = dataArr[4];
                            System.out.println("Comment ->  " + Comment);
                        }
                    }
                }


//                System.out.println(Arrays.toString(dataArr));

                lineCounter++;
            }
            myReader.close();
            System.out.println("claimCounter ->  "+claimCounter);


        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
