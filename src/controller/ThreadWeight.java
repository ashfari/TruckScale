/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class ThreadWeight extends Thread {

    private Thread t;
    private String threadName = "Thread Weight";
    private boolean isRunning;
    Date date = new Date();
    FrmMain frmMain = null;
    SerialPortManager serialPortManager = null;
    Response apiResponse = null;
    int refreshRateConfig = 0;
    int refreshRate = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    JSONObject params = null;

    public ThreadWeight(FrmMain frmMain) throws JSONException, FileNotFoundException {
        this.frmMain = frmMain;
        createObjects();
    }
    
    private void createObjects() throws JSONException, FileNotFoundException {
        this.serialPortManager = new SerialPortManager(frmMain);
        this.params = new JSONObject();
    }
    
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
//                Show Date
                date = new Date();
                frmMain.date_time_counter.setText(formatter.format(date));
                
//                Set UI based Config
                try {
                    refreshRateConfig = Integer.parseInt((String) frmMain.config.get("refreshRate"));
                } catch (JSONException ex) {
                    Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
                }

//                Get Data from WeighBridge
                refreshRate++;
                if (refreshRate >= refreshRateConfig) {
                    frmMain.dotLabel.setEnabled(true);
                    
                    try {
                        serialPortManager.getWeight();
                    } catch (Exception e) {
                    }

                    if (frmMain.isDebugging.equals("true")) {
                        System.out.println("current weight : " + frmMain.currentWeight.toString());
                    }
                    
                    try {
                        if (((Double.parseDouble(frmMain.currentWeight.get("value").toString())
                                - Double.parseDouble(frmMain.weightValue.getText().toString())) 
                                > Double.parseDouble(frmMain.config.get("minStepWeight").toString()))
                                ||
                                ((Double.parseDouble(frmMain.weightValue.getText().toString())
                                - Double.parseDouble(frmMain.currentWeight.get("value").toString())) 
                                > Double.parseDouble(frmMain.config.get("minStepWeight").toString()))) {
                            
                            params.put("weigh_bridge_id", frmMain.config.get("kodeTimbangan"));
                            params.put("value_in_kg", Double.parseDouble(frmMain.weightValue.getText()));
                            apiResponse = frmMain.okHttpManager.sendPost(frmMain.config.get("apiTimbangan").toString(), params, null);
                            frmMain.last_sent.setText("Last sent : "
                                    + frmMain.currentWeight.get("value").toString()
                                    + " "
                                    + capitalize(frmMain.currentWeight.get("unit").toString())
                                    + " ~ "
                                    + formatter.format(date));
                            if (frmMain.isDebugging.equals("true")) {
                                System.out.println("send to : " + frmMain.config.get("apiTimbangan").toString());
                                System.out.println("params : " + params);
                                System.out.println("api response : " + apiResponse.body().string());
                            }
                        }
                        if (Integer.parseInt(frmMain.currentWeight.get("value").toString()) == 0) {
                            frmMain.weightValue.setText("0");
                        } else {
                            frmMain.weightValue.setText(frmMain.currentWeight.get("value").toString());
                        }
                        frmMain.weightUnit.setText(capitalize(frmMain.currentWeight.get("unit").toString()));
                    } catch (JSONException ex) {
//                        Logger.getLogger(ThreadWeight.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ThreadWeight.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    refreshRate = 0;
                }
                // Let the thread sleep for a while.
                Thread.sleep(1000);
            }
        }catch (InterruptedException e) {
        }
//            Logger.getLogger(ThreadWeight.class.getName()).log(Level.SEVERE, null, ex);
        
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
