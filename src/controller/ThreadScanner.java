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
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private boolean isRunning;
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

    public ThreadScanner(FrmMain frmMain) {
        this.frmMain = frmMain;
        createObjects();
    }
    
    private void createObjects() {
        scanner = new Scanner(System.in);
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.scannerParams = new JSONObject();
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
//        try {
//            frmMain.averyWeighTronix.getWeight();
//            if (frmMain.isDebugging.equals("true")) {
//                System.out.println("get weight from avery weight tronix");
//            }
//        } catch (JSONException ex) {
//            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
                
                if (frmMain.isDebugging.equals("true") && frmMain.manualQrCode.length() > 0) {
                    frmMain.scannerInput = frmMain.manualQrCode;
                }

                input =  frmMain.scannerInput;
                
                if (frmMain.isDebugging.equals("true") && input.length() > 0) {
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
                        scannerParams.put("wb_id", frmMain.config.getString("kodeTimbangan"));
                        scannerParams.put("track_name", frmMain.config.getString("trackName"));
                        try {
                            result = frmMain.okHttpManager.sendPost(frmMain.config.get("apiQrCode").toString(), scannerParams, frmMain.config.get("accessToken").toString());
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
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    public void showResult(Response result) throws IOException, JSONException {
        resultJson = new JSONObject(result.body().string());
        
        if (result.isSuccessful()) {
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
            
        } else {
            frmMain.txtResultScan.setForeground(Color.RED);
            frmMain.txtResultScan.setText("Gagal: QR Code tidak valid (" + resultJson.get("message").toString() + ")");
        }
    }
}
