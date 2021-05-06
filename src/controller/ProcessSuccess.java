/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.json.JSONException;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class ProcessSuccess {

    FrmMain frmMain = null;
    Timer timer = null;
    int counter = 0;
    JLabel statusProcess = null;
    JButton doneProcess = null;
    
    public ProcessSuccess(boolean isSuccess, FrmMain frmMain, JLabel statusProcess, JButton doneProcess) {
        this.frmMain = frmMain;
        this.statusProcess = statusProcess;
        this.doneProcess = doneProcess;
        
        this.createObjects(isSuccess);
        
        this.showNotification();
    }
    
    private void createObjects(boolean isSuccess) {
        timer = new Timer();
        try {
            counter = Integer.parseInt(frmMain.config.getString("delayOk"));
        } catch (JSONException ex) {
        }
        if (isSuccess) {
            this.statusProcess.setForeground(new Color(0, 204, 51));
            this.statusProcess.setText("Berhasil! Silahkan lanjutkan proses selanjutnya.");
        } else {
            this.statusProcess.setForeground(Color.RED);
            this.statusProcess.setText("Gagal! Silahkan ulangi proses scan.");
        }
        this.doneProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter = 0;
                timeout();
            }
        });
    }
    
    public void showNotification() {
        this.doneProcess.setEnabled(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doneProcess.setText("OK (" + counter + ")");
                counter--;
                if (counter < 0) {
                    timeout();
                }
            }
        }, 0, 1000);
    }
    
    private void timeout() {
        timer.cancel();
        this.doneProcess.setEnabled(false);
        this.statusProcess.setForeground(Color.BLACK);
        this.statusProcess.setText("Status Proses");
//        this.setVisible(false);
    }
}
