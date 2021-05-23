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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sahab
 */
public class LogManager {

    public String getLog() throws FileNotFoundException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String directoryPath = "./logs/";
        String filePath = directoryPath + "log-" + formatter.format(date) + ".log";
        String log = "";
        
        File directory = new File(directoryPath);
        if (! directory.exists()){
            directory.mkdir();
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            log = createLog();
        } else {
            log = readLog();
        }
        return log;
    }

    public String readLog() throws FileNotFoundException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String log = "";
        
        File file = new File("./logs/log-" + formatter.format(date) + ".log");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            log += scanner.nextLine();
        }
        scanner.close();
        return log;
    }

    public String createLog() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String log = "";
        
//        Write file
        try (FileWriter file = new FileWriter("./logs/log-" + formatter.format(date) + ".log")) {
            file.write(log);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return log;
    }

    public String updateLog(String log) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        log = formatter.format(date) + " " + log;
        
        try {
            log = getLog() + log;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (FileWriter file = new FileWriter("./logs/log-" + formatter.format(date) + ".log")) {
            file.write(log);
            file.write(";");
            file.flush();
            
//            BufferedWriter writer = new BufferedWriter(file);
//            
//            writer.write(log);
//            writer.newLine();
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return log;
    }
    
    public String getLogName() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        return "log-" + formatter.format(date) + ".log";
    }
}
