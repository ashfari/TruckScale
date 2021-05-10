/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import okhttp3.Response;
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
    int indexScannerShow = 0;
    public String scannerInput = "";
    ThreadScannerIpBasedGetInput threadScannerIpBasedGetInput = null;
    
    public ThreadScannerIpBased(FrmMain frmMain, JLabel titleText, 
            JTextArea resultTextArea, JLabel statusProcess, JButton nextProcess, 
            JButton cancelProcess, int indexScanner, int indexScannerShow) {
        this.frmMain = frmMain;
        this.titleText = titleText;
        this.statusProcess = statusProcess;
        this.nextProcess = nextProcess;
        this.cancelProcess = cancelProcess;
        this.resultTextArea = resultTextArea;
        this.indexScanner = indexScanner;
        this.indexScannerShow = indexScannerShow;
        
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
//        this.updateSwing();
    }
    
    public void run()
    {
        while (!this.frmMain.isRestart) {
            try {
                this.input = this.scannerInput;
                
                System.out.println("inputan : " + this.input);
                
                if (this.frmMain.isDebugging.equals("true") && this.input.length() > 0) {
                    if (this.input.length() >= Integer.parseInt(this.frmMain.config.get("minLengthQrCode").toString())) {
                        
                    } else {
                        System.out.println("input scanner tidak melebihi minimal qrcode character length");
                    }
                }

                if (this.input.length() >= Integer.parseInt(this.frmMain.config.get("minLengthQrCode").toString())) {
                    this.resultTextArea.setForeground(Color.BLACK);
                    this.resultTextArea.setText("Scanning...");
                    System.out.println(this.resultTextArea.getText());
                    this.frmMain.tabNotify(this.indexScannerShow);
                    this.delay--;

                    if (!this.input.equals(this.prevInput)) {
                        this.prevInput = this.input;
                        resetDelay();
                    }

                    if (delay == 0) {
                        System.out.println("delay done : " + delay);
                        this.scannerParams.put("qrcode", this.input.replaceAll("\\r\\n", ""));
                        this.scannerParams.put("wb_id", this.frmMain.config.getString("kodeTimbangan"));
                        this.scannerParams.put("track_name", this.frmMain.scanner[this.indexScanner][5].toString());
                        try {
                            System.out.println("send post request" + this.input);
                            System.out.println(this.scannerParams);
                            System.out.println(this.frmMain.config.get("apiQrCode").toString());
                            System.out.println(this.frmMain.config.get("accessToken").toString());
                            this.result = this.frmMain.okHttpManager.sendPost(this.frmMain.config.get("apiQrCode").toString(), this.scannerParams, this.frmMain.authManager.readAuth());
                            showResult(this.result);
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
            } catch (JSONException ex) {
                Logger.getLogger(ThreadScannerIpBased.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadScannerIpBased.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        stop();
    }
    
    public void updateSwing() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                resultTextArea.setText("Scanning...");
            }
        });
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
            System.out.println("Gagal: QR Code tidak valid (" + this.resultJson.get("message").toString() + ")");
            this.resultTextArea.setText("Gagal: QR Code tidak valid (" + this.resultJson.get("message").toString() + ")");
        }
    }
}
