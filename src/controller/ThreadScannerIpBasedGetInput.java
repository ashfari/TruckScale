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
    String ip = "";
    int port = 0;
    TelnetClient tc = null;
    InputStream instr = null;
    byte[] buff = new byte[1024];
    int ret_read = 0;
    int indexScanner = 0;
    ThreadScannerIpBased threadScannerIpBased = null;
    String scannerBuffer = "";
    
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
        while (!this.threadScannerIpBased.frmMain.isRestart) {
            this.instr = this.tc.getInputStream();
            try {
                this.ret_read = this.instr.read(this.buff);
                if (this.ret_read > 0) {
                    this.scannerBuffer = (new String(this.buff, 0, this.ret_read)).replaceAll("\\r\\n", "");
                    if (!this.scannerBuffer.equals("")) {
                        this.threadScannerIpBased.scannerInput += this.scannerBuffer;
                    }
                }
            } catch (Exception e) {
                if (this.threadScannerIpBased.frmMain.isDebugging.equals("true")) {
                    System.err.println("Exception while reading socket:" + e.getMessage());
                }
            }
        }
        try {
            this.tc.disconnect();
        } catch (IOException ex) {
        }
        stop();
    }
}
