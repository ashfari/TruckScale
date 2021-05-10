
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sahab
 */
public class SimpleServer {
    public final static int TESTPORT = 5000;
    
    public static void main(String args[]) {
        ServerSocket checkServer = null;
        String line;
        BufferedReader is = null;
        DataOutputStream os = null;
        Socket clientSocket = null;
        boolean exit = false;
        
        try {
            checkServer = new ServerSocket(TESTPORT);
            System.out.println("Aplikasi Server hidup ...");
        }
        catch (IOException e) {
            System.out.println(e);
        }
        do {
            try {
                clientSocket = checkServer.accept();
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                os = new DataOutputStream(clientSocket.getOutputStream());
            }
            catch (Exception ei) {
                ei.printStackTrace();
            }
            try {
                line = is.readLine();
                System.out.println("Terima : " + line);
                if (line.compareTo("salam") == 0) {
                    os.writeBytes(line + " juga");
                }
                else if (line.compareTo("exit") == 0) {
                    os.writeBytes("bye");
                    exit = true;
                }
                else {
                    os.writeBytes("Maaf, saya tidak mengerti");
                }
            }
            catch (IOException e) {
                System.out.println(e);
            }
            try {
                os.close();
                is.close();
                clientSocket.close();
            }
            catch (IOException ic) {
                ic.printStackTrace();
            }
        } while (!exit);
    }
}
