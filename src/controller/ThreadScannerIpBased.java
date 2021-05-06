/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import okhttp3.Response;
import org.apache.commons.net.telnet.TelnetClient;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmMain;
import view.FrmUpdateTrack;

/**
 *
 * @author Sahab
 */
public class ThreadScannerIpBased extends Thread {
    private Thread reader;
    private String threadName = "Thread Scanner IP Based";
    boolean isRunning;
    FrmMain frmMain = null;
    String ip = "";
    int port = 0;
    String input = "";
    String prevInput = "";
    int sleepTime = 0;
    int delay = 0;
    int delayNoScan = 0;
    JSONObject scannerParams = null;
    Response result = null;
    JSONObject resultJson = null;
    JSONObject jsonArray = null;
    JSONObject jsonTruck = null;
    JSONObject jsonDriver = null;
    JLabel titleText = null;
    JTextArea resultTextArea = null;
    JLabel statusProcess = null;
    JButton nextProcess = null; 
    JButton cancelProcess = null;
    int indexScanner = 0;
    public String scannerInput = "";
    ThreadScannerIpBasedGetInput threadScannerIpBasedGetInput = null;
    
    public ThreadScannerIpBased(FrmMain frmMain, JLabel titleText, 
            JTextArea resultTextArea, JLabel statusProcess, JButton nextProcess, 
            JButton cancelProcess, int indexScanner) {
        this.frmMain = frmMain;
        this.titleText = titleText;
        this.statusProcess = statusProcess;
        this.nextProcess = nextProcess;
        this.cancelProcess = cancelProcess;
        this.resultTextArea = resultTextArea;
        this.indexScanner = indexScanner;
        
        createObjects();
    }
    
    private void createObjects() {
        this.scannerParams = new JSONObject();
        this.ip = frmMain.scanner[this.indexScanner][3].toString();
        this.port = Integer.parseInt(frmMain.scanner[this.indexScanner][4].toString());
        this.threadScannerIpBasedGetInput = new ThreadScannerIpBasedGetInput(this, this.ip, this.port);
        this.threadScannerIpBasedGetInput.start();
        sleepTime = 100;
        resetDelay();
        resetDelayNoScan();
        this.resultTextArea.setText("Ready...");
    }
    
    public void run()
    {
        this.isRunning = true;
        while (this.isRunning) {
            try {
                this.input = this.scannerInput;
                
                if (this.frmMain.isDebugging.equals("true") && this.input.length() > 0) {
                    System.out.println(this.input);
                }

                if (this.input.length() >= Integer.parseInt(this.frmMain.config.get("minLengthQrCode").toString())) {
                    this.resultTextArea.setForeground(Color.BLACK);
                    this.resultTextArea.setText("Scanning...");
                    this.frmMain.tabNotify(this.indexScanner);
                    this.delay--;

                    if (!this.input.equals(this.prevInput)) {
                        this.prevInput = this.input;
                        resetDelay();
                    }

                    if (delay <= 0) {
                        this.scannerParams.put("qrcode", this.input);
                        this.scannerParams.put("wb_id", this.frmMain.config.getString("kodeTimbangan"));
                        this.scannerParams.put("track_name", this.frmMain.scanner[this.indexScanner][5].toString());
                        try {
                            this.result = this.frmMain.okHttpManager.sendPost(this.frmMain.config.get("apiQrCode").toString(), scannerParams, frmMain.authManager.readAuth());
                            showResult(result);
                            this.scannerInput = "";
                            resetDelay();
                        } catch (Exception ex) {
                            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    this.delayNoScan--;
                    if (!this.input.equals(this.prevInput)) {
                        this.prevInput = this.input;
                        resetDelayNoScan();
                    }
                    if (this.delayNoScan <= 0) {
                        this.frmMain.scannerInput = "";
                        resetDelayNoScan();
                    }
                }

                // Let the thread sleep for a while.
                Thread.sleep(this.sleepTime);
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
    
    private void resetDelay() {
        try {
            this.delay = (int) (Double.parseDouble(this.frmMain.config.getString("delayScan")) * (1000 / this.sleepTime));
        } catch (JSONException ex) {
            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void resetDelayNoScan() {
        this.delayNoScan = 30;
    }
    
    public void showResult(Response result) throws IOException, JSONException {
        this.resultJson = new JSONObject(result.body().string());
        
        if (result.isSuccessful()) {
            this.resultTextArea.setText("Success: QR Code Valid");
            this.resultTextArea.append("\n\n");
            this.resultTextArea.append("SO Number: " + resultJson.get("so_number").toString());
            this.resultTextArea.append("\n");
            this.resultTextArea.append("DO Number: " + resultJson.get("so_number").toString());
            this.resultTextArea.append("\n");
            this.jsonArray = new JSONObject(resultJson.getJSONArray("trucks").get(0).toString());
            this.jsonArray = new JSONObject(this.jsonArray.getJSONArray("assignments").get(0).toString());
            this.jsonTruck = new JSONObject(this.jsonArray.getJSONArray("trucks").get(0).toString());
            this.jsonDriver = new JSONObject(this.jsonArray.getJSONArray("drivers").get(0).toString());
            this.resultTextArea.append("Truk: " + String.join(" ", this.jsonTruck.get("number_array").toString().replace("\"", "").replace("[", "").replace("]", "").split(",")));
            this.resultTextArea.append("\n");
            this.resultTextArea.append("Sopir: " + this.jsonDriver.getString("name"));

            new UpdateTrackController(this.input, 
                this.frmMain, this.frmMain.scanner[indexScanner][5].toString(), 
                statusProcess, nextProcess, cancelProcess);
            
        } else {
            this.resultTextArea.setForeground(Color.RED);
            this.resultTextArea.setText("Gagal: QR Code tidak valid (" + this.resultJson.get("message").toString() + ")");
        }
    }
}
