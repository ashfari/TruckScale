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
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 *
 * @author Sahab
 */
public class PasswordManager {

    public boolean checkPassword(String password) throws FileNotFoundException {
        if (BCrypt.checkpw(password, readPassword())) {
            return true;
        }
        return false;
    }

    public String readPassword() throws FileNotFoundException {
        String data = "";
        File file = new File("./key");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }
        scanner.close();
        return data;
    }

    public void createPassword() {
        String path = "./key";
        File file = new File(path);
        if (!file.exists()) {
            //        Write file
            try (FileWriter newFile = new FileWriter("./key")) {
                newFile.write("$2y$12$jQes5VWSX5sHgozwluVTIOMQMcczv1XXwDQMfVir6F.bhOHw.wHMq");
                newFile.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
