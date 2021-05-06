/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.telnet.TelnetClient;

/**
 *
 * @author Sahab
 */
public class ThreadScannerIpBasedGetInput extends Thread {
    private Thread reader;
    private String threadName = "Thread Scanner IP Based";
    boolean isRunning;
    String ip = "";
    int port = 0;
    TelnetClient tc = null;
    InputStream instr = null;
    byte[] buff = new byte[1024];
    int ret_read = 0;
    int indexScanner = 0;
    ThreadScannerIpBased threadScannerIpBased = null;
    
    public ThreadScannerIpBasedGetInput(ThreadScannerIpBased threadScannerIpBased, String ip, int port) {
        this.threadScannerIpBased = threadScannerIpBased;
        this.ip = ip;
        this.port = port;
        
        createObjects();
    }
    
    private void createObjects() {
        this.tc = new TelnetClient();
        try {
            this.tc.connect(this.ip, this.port);
        } catch (IOException ex) {
            Logger.getLogger(ThreadScannerIpBasedGetInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run()
    {
        this.buff = new byte[1024];
        this.ret_read = 0;
        this.isRunning = true;
        while (this.isRunning) {
            this.instr = this.tc.getInputStream();
            try {
                this.ret_read = this.instr.read(this.buff);
                if (this.ret_read > 0) {
                    this.threadScannerIpBased.scannerInput += new String(this.buff, 0, this.ret_read);
                }
            } catch (Exception e) {
                System.err.println("Exception while reading socket:" + e.getMessage());
            }

//            try {
//                this.tc.disconnect();
//            }
//            catch (Exception e) {
//                System.err.println("Exception while closing telnet:" + e.getMessage());
//            }
        }
    }
    
    public void start() {
        if (this.reader == null) {
            this.reader = new Thread(this, this.threadName);
            this.reader.start();
        }
    }
}
