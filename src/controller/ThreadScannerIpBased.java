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
import okhttp3.Response;
import org.apache.commons.net.telnet.TelnetClient;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class ThreadScannerIpBased extends Thread {
    private Thread reader;
    private String threadName = "Thread Scanner IP Based";
    private boolean isRunning;
    FrmMain frmMain = null;
    String ip = "";
    int port = 0;
    TelnetClient tc = null;
    InputStream instr = null;
    byte[] buff = new byte[1024];
    int ret_read = 0;
    String input = "";
    String prevInput = "";
    int sleepTime = 0;
    int delay = 0;
    int delayNoScan = 0;
    JSONObject scannerParams = null;
    Response result = null;
    
    public ThreadScannerIpBased(FrmMain frmMain, String ip, int port) {
        this.frmMain = frmMain;
        this.ip = ip;
        this.port = port;
        createObjects();
    }
    
    private void createObjects() {
        this.tc = new TelnetClient();
        try {
            this.tc.connect(this.ip, this.port);
        } catch (IOException ex) {
            Logger.getLogger(ThreadScannerIpBased.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run()
    {
        this.isRunning = true;
        while (this.isRunning) {
            this.instr = this.tc.getInputStream();
            try {
                this.buff = new byte[1024];
                this.ret_read = 0;

                do {
                    this.ret_read = this.instr.read(this.buff);
                    if(this.ret_read > 0)
                    {
                        this.frmMain.scannerInput = new String(this.buff, 0, this.ret_read);
                        
                        if (input.length() >= Integer.parseInt(frmMain.config.get("minLengthQrCode").toString())) {
                            frmMain.txtResultScan.setForeground(Color.BLACK);
                            frmMain.txtResultScan.setText("Scanning...");
                            this.delay--;

                            if (!input.equals(prevInput)) {
                                prevInput = input;
                                resetDelay();
                            }
                            if (delay <= 0) {
                                scannerParams.put("qrcode", input);
                                scannerParams.put("wb_id", frmMain.config.getString("kodeTimbangan"));
                                scannerParams.put("track_name", frmMain.scanner[1][5].toString());
                                try {
                                    result = frmMain.okHttpManager.sendPost(frmMain.config.get("apiQrCode").toString(), scannerParams, frmMain.authManager.readAuth());
                                    showResult(result);
                                    frmMain.scannerInput = "";
                                    frmMain.manualQrCode = "";
                                    resetDelay();
                                } catch (Exception ex) {
                                    Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else {
                            delayNoScan--;
                            if (!input.equals(prevInput)) {
                                prevInput = input;
                                resetDelayNoScan();
                            }
                            if (delayNoScan <= 0) {
                                frmMain.scannerInput = "";
                                resetDelayNoScan();
                            }
                        }
                    }
                }
                while (this.ret_read >= 0);
                
                // Let the thread sleep for a while.
                Thread.sleep(this.sleepTime);
            }

            catch (Exception e) {
                System.err.println("Exception while reading socket:" + e.getMessage());
            }

            try {
                this.tc.disconnect();
            }
            catch (Exception e) {
                System.err.println("Exception while closing telnet:" + e.getMessage());
            }
            
            
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
            delay = (int) (Double.parseDouble(frmMain.config.getString("delayScan")) * (1000 / sleepTime));
        } catch (JSONException ex) {
            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void resetDelayNoScan() {
        delayNoScan = 30;
    }
    
    public void showResult(Response result) throws IOException, JSONException {
//        resultJson = new JSONObject(result.body().string());
//        
//        if (result.isSuccessful()) {
//            frmMain.txtResultScan.setText("Success: QR Code Valid");
//            frmMain.txtResultScan.append("\n\n");
//            frmMain.txtResultScan.append("SO Number: " + resultJson.get("so_number").toString());
//            frmMain.txtResultScan.append("\n");
//            frmMain.txtResultScan.append("DO Number: " + resultJson.get("so_number").toString());
//            frmMain.txtResultScan.append("\n");
//            jsonArray = new JSONObject(resultJson.getJSONArray("trucks").get(0).toString());
//            jsonArray = new JSONObject(jsonArray.getJSONArray("assignments").get(0).toString());
//            jsonTruck = new JSONObject(jsonArray.getJSONArray("trucks").get(0).toString());
//            jsonDriver = new JSONObject(jsonArray.getJSONArray("drivers").get(0).toString());
//            frmMain.txtResultScan.append("Truk: " + String.join(" ", jsonTruck.get("number_array").toString().replace("\"", "").replace("[", "").replace("]", "").split(",")));
//            frmMain.txtResultScan.append("\n");
//            frmMain.txtResultScan.append("Sopir: " + jsonDriver.getString("name"));
//
//            new FrmUpdateTrack(input, frmMain).setVisible(true);
//            
//        } else {
//            frmMain.txtResultScan.setForeground(Color.RED);
//            frmMain.txtResultScan.setText("Gagal: QR Code tidak valid (" + resultJson.get("message").toString() + ")");
//        }
    }
}
