/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.ApiManager;
import controller.AveryWeighTronix;
import controller.OkHttpManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
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
    AveryWeighTronix averyWeighTronix = null;
    String apiResponse = "";
    int refreshRateConfig = 0;
    int refreshRate = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Map params = null;

    public ThreadWeight(FrmMain frmMain) throws JSONException, FileNotFoundException {
        this.frmMain = frmMain;
        createObjects();
    }
    
    private void createObjects() throws JSONException, FileNotFoundException {
        this.averyWeighTronix = new AveryWeighTronix(frmMain);
        this.params = new LinkedHashMap<>();
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
                        if ((Double.parseDouble(frmMain.currentWeight.get("value").toString())
                                - Double.parseDouble(frmMain.weightValue.getText().toString())) 
                                > Double.parseDouble(frmMain.config.get("minStepWeight").toString())) {
                            frmMain.weightValue.setText(frmMain.currentWeight.get("value").toString());
                            frmMain.weightUnit.setText(capitalize(frmMain.currentWeight.get("unit").toString()));
                            params.put("weigh_bridge_id", frmMain.config.get("kodeTimbangan"));
                            params.put("value_in_kg", Double.parseDouble(frmMain.weightValue.getText()));
                            apiResponse = frmMain.okHttpManager.sendPost(frmMain.config.get("apiTimbangan").toString(), params, null);
                            System.out.println(apiResponse);
                        }
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
        } catch (InterruptedException e) {
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
