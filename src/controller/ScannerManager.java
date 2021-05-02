/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONException;

/**
 *
 * @author Sahab
 */
public class ScannerManager {

    public Object[][] getScanner() throws JSONException, FileNotFoundException {
        String path = "./scanner";
        Object[][] object = null;
        File file = new File(path);
        if (!file.exists()) {
            object = createScanner();
        } else {
            object = readScanner();
        }
        return object;
    }

    public Object[][] readScanner() throws FileNotFoundException {
        String data = "";
        File file = new File("./scanner");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }
        scanner.close();
        
        Object[][] object = this.stringToObjectConverter(data);
        return object;
    }

    public Object[][] createScanner() {
//        Write file
        String content = "1,Scanner Serial 1,Serial Based,,0,weigh-in,1,true;"
                + "2,Scanner IP 1,IP Based,127.0.0.1,23,weigh-in,1,true;";
        try (FileWriter file = new FileWriter("./scanner")) {
            file.write(content);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.stringToObjectConverter(content);
    }

    public Object[][] updateScanner(Object[][] object) {
//        Convert object to string
        String scanner = this.objectToStringConverter(object);
        
//        Write file
        try (FileWriter file = new FileWriter("./scanner")) {
            file.write(scanner);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;
    }
    
//    Converter Object to String
    private String objectToStringConverter(Object[][] object) {
        String scanner = "";
        
        for (int i = 0; i < object.length; i++) {
            for (int j = 0; j < object[0].length; j++) {
                scanner += object[i][j];
                if (j < object[0].length-1) {
                    scanner += ",";
                }
            }
            scanner += ";";
        }
        
        return scanner;
    }
    
    //    Converter String to Object
    private Object[][] stringToObjectConverter(String scanner) {
        String[] scannerPerRow = scanner.split(";");
        Object[][] object = new Object[scannerPerRow.length][8];
        
        for (int i = 0; i < scannerPerRow.length; i++) {
            String[] scannerPerColumns = scannerPerRow[i].split(",");
            for (int j = 0; j < 8; j++) {
                object[i][j] = (j == 7) ? Boolean.parseBoolean(scannerPerColumns[j]) : scannerPerColumns[j];
            }
        }
        
        return object;
    }
}
