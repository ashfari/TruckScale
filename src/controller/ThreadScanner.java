/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmMain;
import view.FrmUpdateTrack;

/**
 *
 * @author Sahab
 */
public class ThreadScanner extends Thread {

    private Thread t;
    private String threadName = "Thread Scanner";
    JSONObject scannerParams = null;
    boolean isReading = false;
    int sleepTime = 0;
    int delay = 0;
    int delayNoScan = 0;
    FrmMain frmMain = null;
    Scanner scanner = null;
    BufferedReader bufferedReader = null;
    String input = "";
    String prevInput = "";
    Response result = null;
    JSONObject resultJson = null;
    JSONObject jsonArray = null;
    JSONObject jsonTruck = null;
    JSONObject jsonDriver = null;
    public String resultUpdateData = "";
    JLabel titleText = null;
    JTextArea resultTextArea = null;
    int indexScanner = 0;
    int indexScannerShow = 0;

    public ThreadScanner(FrmMain frmMain, JLabel titleText, 
            JTextArea resultTextArea, int indexScanner, int indexScannerShow) {
        this.frmMain = frmMain;
        this.titleText = titleText;
        this.resultTextArea = resultTextArea;
        this.indexScanner = indexScanner;
        
        createObjects();
    }
    
    private void createObjects() {
        scanner = new Scanner(System.in);
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.scannerParams = new JSONObject();
        sleepTime = 100;
        resetDelay();
        resetDelayNoScan();
        this.resultTextArea.setText("Ready...");
    }
    
    private void implementConfig() {
        frmMain.setDebugging();
        frmMain.dotLabel.setEnabled(false);
        try {
            frmMain.main_title.setText(frmMain.config.getString("title"));
            frmMain.refreshRateLabel.setText(frmMain.config.get("refreshRate") + "s");
            frmMain.setTitle(frmMain.config.getString("title"));
        } catch (JSONException ex) {
            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
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

    public void run() {
        while (!this.frmMain.isRestart) {
            try {
                frmMain.config = frmMain.configManager.readConfig();
                frmMain.scanner = frmMain.scannerManager.readScanner();
                
                if (frmMain.isDebugging.equals("true") && frmMain.manualQrCode.length() > 0) {
                    frmMain.scannerInput = frmMain.manualQrCode;
                }

                input = frmMain.scannerInput;
                
                if (frmMain.isDebugging.equals("true") && input.length() > 0) {
                    System.out.println(input);
                }
                
                if (input.length() >= Integer.parseInt(frmMain.config.get("minLengthQrCode").toString())) {
                    this.resultTextArea.setForeground(Color.BLACK);
                    this.resultTextArea.setText("Scanning...");
                    this.frmMain.tabNotify(this.indexScannerShow);
                    this.delay--;
                    
                    if (!input.equals(prevInput)) {
                        prevInput = input;
                        resetDelay();
                    }
                    if (delay <= 0) {
                        scannerParams.put("qrcode", input);
                        scannerParams.put("wb_id", frmMain.config.getString("kodeTimbangan"));
                        scannerParams.put("track_name", frmMain.scanner[this.indexScanner][5].toString());
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
                
                // Let the thread sleep for a while.
                Thread.sleep(sleepTime);
            } catch (JSONException ex) {
                Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        stop();
    }
    
    private void showResult(Response result) throws IOException, JSONException {
        resultJson = new JSONObject(result.body().string());
        
        if (result.isSuccessful()) {
            this.resultTextArea.setText("Success: QR Code Valid");
            this.resultTextArea.append("\n\n");
            this.resultTextArea.append("SO Number: " + resultJson.get("so_number").toString());
            this.resultTextArea.append("\n");
            this.resultTextArea.append("DO Number: " + resultJson.get("so_number").toString());
            this.resultTextArea.append("\n");
            jsonArray = new JSONObject(resultJson.getJSONArray("trucks").get(0).toString());
            jsonArray = new JSONObject(jsonArray.getJSONArray("assignments").get(0).toString());
            jsonTruck = new JSONObject(jsonArray.getJSONArray("trucks").get(0).toString());
            jsonDriver = new JSONObject(jsonArray.getJSONArray("drivers").get(0).toString());
            this.resultTextArea.append("Truk: " + String.join(" ", jsonTruck.get("number_array").toString().replace("\"", "").replace("[", "").replace("]", "").split(",")));
            this.resultTextArea.append("\n");
            this.resultTextArea.append("Sopir: " + jsonDriver.getString("name"));

            new FrmUpdateTrack(this.input, this.frmMain).setVisible(true);
            
        } else {
            this.resultTextArea.setForeground(Color.RED);
            this.resultTextArea.setText("Gagal: QR Code tidak valid (" + resultJson.get("message").toString() + ")");
        }
    }
    
    private void resetResultScan() {
        this.resultTextArea.setForeground(Color.BLACK);
        this.resultTextArea.setText("Ready...");
    }
}
