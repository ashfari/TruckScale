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
public class ThreadMain extends Thread {

    private Thread t;
    private String threadName = "Thread Main";
    private boolean isRunning;
    boolean isReading = false;
    int sleepTime = 0;
    FrmMain frmMain = null;
    String input = "";
    String prevInput = "";

    public ThreadMain(FrmMain frmMain) {
        this.frmMain = frmMain;
        createObjects();
    }
    
    private void createObjects() {
        this.sleepTime = 100;
        this.frmMain.setTabResults();
    }
    
    private void implementConfig() {
        this.frmMain.setDebugging();
        this.frmMain.dotLabel.setEnabled(false);
        try {
            this.frmMain.main_title.setText(this.frmMain.config.getString("title"));
            this.frmMain.refreshRateLabel.setText(this.frmMain.config.get("refreshRate") + "s");
            this.frmMain.setTitle(this.frmMain.config.getString("title"));
        } catch (JSONException ex) {
            Logger.getLogger(ThreadMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                this.frmMain.config = this.frmMain.configManager.readConfig();
                implementConfig();
                if (this.frmMain.config.getString("isActiveWeighBridge").equals("false")) {
                    frmMain.leftPanel.setVisible(false);
                    frmMain.splitPaneMain.setDividerSize(0);
                } else {
                    frmMain.leftPanel.setVisible(true);
                    frmMain.splitPaneMain.setDividerLocation(390);
                    frmMain.splitPaneMain.setDividerSize(5);
                }
                // Let the thread sleep for a while.
                Thread.sleep(sleepTime);
            } catch (JSONException ex) {
                Logger.getLogger(ThreadMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ThreadMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
