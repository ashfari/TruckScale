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
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

/**
 *
 * @author Sahab
 */
public class AuthManager {
    
    private static SecretKeySpec secretKey;
    private static byte[] key;
    final String secretCode = "chromeExpert!!!";
    
    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public String readAuth() throws FileNotFoundException {
        String data = "";
        File file = new File("./auth");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }
        scanner.close();
        String decryptedToken = decrypt(data, secretCode) ;
        return decryptedToken;
    }

    public void createAuth(String token) {
        String encryptedToken = encrypt(token, secretCode) ;
        String path = "./auth";
        File file = new File(path);
        if (!file.exists()) {
            //        Write file
            try (FileWriter newFile = new FileWriter("./auth")) {
                newFile.write(encryptedToken);
                newFile.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean deleteAuth() {
        String path = "./auth";
        File file = new File(path);
        if (file.delete()) {
            return true;
        }
        return false;
    }
}
