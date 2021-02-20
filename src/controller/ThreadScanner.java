/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private boolean isRunning;
    Map scannerParams = null;
    boolean isReading = false;
    int sleepTime = 0;
    int delay = 0;
    int delayNoScan = 0;
    FrmMain frmMain = null;
    Scanner scanner = null;
    BufferedReader bufferedReader = null;
    String input = "";
    String prevInput = "";
    String result = "";
    JSONObject resultJson = null;
    JSONObject jsonArray = null;
    JSONObject jsonTruck = null;
    JSONObject jsonDriver = null;
    public String resultUpdateData = "";

    public ThreadScanner(FrmMain frmMain) {
        this.frmMain = frmMain;
        createObjects();
    }
    
    private void createObjects() {
        scanner = new Scanner(System.in);
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.scannerParams = new LinkedHashMap<>();
        sleepTime = 100;
        resetDelay();
        resetDelayNoScan();
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
        try {
            frmMain.averyWeighTronix.getWeight();
        } catch (Exception e) {
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
        isRunning = true;
        while (isRunning) {
            try {
                frmMain.config = frmMain.configManager.readConfig();
                implementConfig();
                
                input =  frmMain.scannerInput;
                if (frmMain.config.getString("isDebugging").equals("true")) {
                    System.out.println(input);
                }
                
                if (input.length() >= Integer.parseInt(frmMain.config.get("minLengthQrCode").toString())) {
                    frmMain.txtResultScan.setForeground(Color.BLACK);
                    frmMain.txtResultScan.setText("Scanning...");
                    delay--;
                    
                    if (!input.equals(prevInput)) {
                        prevInput = input;
                        resetDelay();
                    }
                    if (delay <= 0) {
                        scannerParams.put("qrcode", input);
                        try {
                            result = frmMain.okHttpManager.sendPost(frmMain.config.get("apiQrCode").toString(), scannerParams, frmMain.config.get("accessToken").toString());
                            showResult(result);
                            frmMain.scannerInput = "";
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
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    public void showResult(String result) throws JSONException {
        resultJson = new JSONObject(result);
        
        try {
            frmMain.txtResultScan.setText("Success: QR Code Valid");
            frmMain.txtResultScan.append("\n\n");
            frmMain.txtResultScan.append("SO Number: " + resultJson.get("so_number").toString());
            frmMain.txtResultScan.append("\n");
            frmMain.txtResultScan.append("DO Number: " + resultJson.get("so_number").toString());
            frmMain.txtResultScan.append("\n");
            jsonArray = new JSONObject(resultJson.getJSONArray("trucks").get(0).toString());
            jsonArray = new JSONObject(jsonArray.getJSONArray("assignments").get(0).toString());
            jsonTruck = new JSONObject(jsonArray.getJSONArray("trucks").get(0).toString());
            jsonDriver = new JSONObject(jsonArray.getJSONArray("drivers").get(0).toString());
            frmMain.txtResultScan.append("Truk: " + String.join(" ", jsonTruck.get("number_array").toString().replace("\"", "").replace("[", "").replace("]", "").split(",")));
            frmMain.txtResultScan.append("\n");
            frmMain.txtResultScan.append("Sopir: " + jsonDriver.getString("name"));

            new FrmUpdateTrack(input, frmMain).setVisible(true);
            
        } catch (Exception e) {
            frmMain.txtResultScan.setForeground(Color.RED);
            frmMain.txtResultScan.setText("Gagal: QR Code tidak valid (" + resultJson.get("message").toString() + ")");
        }
    }
}
