/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class UpdateTrackController {
    FrmMain frmMain = null;
    Timer timer = null;
    int counter = 0;
    JSONObject params = null;
    Response result = null;
    String qrcode = null;
    JSONArray data = null;
    JLabel statusProcess = null;
    JButton nextProcess = null;
    JButton cancelProcess = null;
    String trackName = "";

    public UpdateTrackController(String qrcode, FrmMain frmMain, String trackName,
            JLabel statusProcess, JButton nextProcess, JButton cancelProcess) {
        this.qrcode = qrcode;
        this.frmMain = frmMain;
        this.trackName = trackName;
        this.statusProcess = statusProcess;
        this.nextProcess = nextProcess;
        this.cancelProcess = cancelProcess;
        
        this.createObjects();
        
        this.sendUpdateTrack();
    }
    
    private void createObjects() {
        timer = new Timer();
        this.params = new JSONObject();
        this.data = new JSONArray();
        try {
            counter = Integer.parseInt(frmMain.config.getString("delayOk"));
        } catch (JSONException ex) {
            Logger.getLogger(UpdateTrackController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.statusProcess.setText("Lanjutkan Proses?");
        this.nextProcess.setText("OK");
        this.cancelProcess.setText("Cancel");
        this.nextProcess.setEnabled(true);
        this.cancelProcess.setEnabled(true);
        this.nextProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter = 0;
                try {
                    updateData();
                } catch (Exception ex) {
                    Logger.getLogger(UpdateTrackController.class.getName()).log(Level.SEVERE, null, ex);
                }
                nextProcess.setEnabled(false);
                cancelProcess.setEnabled(false);
            }
        });
        this.cancelProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.cancel();
                nextProcess.setEnabled(false);
                cancelProcess.setEnabled(false);
            }
        });
    }
    
    public void sendUpdateTrack() {
        timer.scheduleAtFixedRate(new TimerTask() {
            Map params = new LinkedHashMap<>();
            @Override
            public void run() {
                nextProcess.setText("OK (" + counter + ")");
                counter--;
                if (counter < 0) {
                    try {
                        updateData();
                    } catch (Exception ex) {
                        Logger.getLogger(UpdateTrackController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }, 0, 1000);
    }
    
    private void updateData() throws JSONException, Exception {
        timer.cancel();
        this.nextProcess.setEnabled(false);
        this.cancelProcess.setEnabled(false);
        params.put("qrcode", qrcode);
        try {
            params.put("wb_id", frmMain.config.getString("kodeTimbangan"));
            params.put("track_name", this.trackName);
            data.put(Double.parseDouble(frmMain.weightValue.getText()));
            data.put(frmMain.weightUnit.getText().toLowerCase());
            params.put("data", data);
        } catch (JSONException ex) {
            Logger.getLogger(UpdateTrackController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.statusProcess.setText("Updating...");
        System.out.println("update track qrcode : " + qrcode);
        System.out.println("qrcode : " + params.get("qrcode").toString());
        result = frmMain.okHttpManager.sendPost(frmMain.config.get("apiUpdateTrack").toString(), params, frmMain.config.get("accessToken").toString());
        if (frmMain.isDebugging.equals("true")) {
            System.out.println("Status : " + result.isSuccessful());
            System.out.println("Return value : " + result.body().string());
        }
        new ProcessSuccess(this.result.isSuccessful(), this.frmMain, statusProcess, nextProcess);
    }
}
