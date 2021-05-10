
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sahab
 */
public class SimpleClient {
    public final static int REMOTE_PORT = 5000;
    
    public static void main(String[] args) {
        Socket cl = null;
        BufferedReader is = null;
        DataOutputStream os = null;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;
        String output = null;
        boolean exit = false;
        do {
            // Membuka koneksi ke server pada port REMOTE_PORT
            try {
                cl = new Socket();
                cl.connect(new InetSocketAddress("localhost", REMOTE_PORT), 2000);
                is = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                os = new DataOutputStream(cl.getOutputStream());
            }
            catch(UnknownHostException e1) {
                System.out.println("Unknown Host: " + e1);
            }
            catch (IOException e2) {
                System.out.println("Erorr io: " + e2);
            }

            // Menulis ke server
            try {
                System.out.print("Masukkan kata kunci: ");
                userInput = stdin.readLine();
                os.writeBytes(userInput + "\n");
            }
            catch (IOException ex) {
                System.out.println("Error writing to server..." + ex);
            }

            // Menerima tanggapan dari server
            try {
                output = is.readLine();
                System.out.println("Dari server: " + output);
                if (output.equals("bye")) {
                    exit = true;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // close input stream, output stream dan koneksi
            try {
                is.close();
                os.close();
                cl.close();
            }
            catch (IOException x) {
                System.out.println("Error writing...." + x);
            }
        } while (!exit);
    }
}
