/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import okhttp3.Response;
import org.apache.commons.net.telnet.TelnetClient;
import org.json.JSONException;
import org.json.JSONObject;
import sun.audio.AudioStream;
import view.FrmLogs;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class ScannerIpBasedManager {
    FrmMain frmMain = null;
    LogManager logManager = null;
    JPanel panel = null;
    String ip = "";
    int port = 0;
    int indexScanner = 0, indexScannerShow = 0; 
    String scannerInput = "", prevScannerInput = "", scannerBuffer = "";
    byte[] buff = new byte[1024];
    int ret_read = 0;
    InputStream instr = null;
    TelnetClient tc = null;
    JLabel title = null;
    JSeparator separator = null;
    JButton logs = null;
    JTextArea result = null;
    JSeparator separatorBottom = null;
    JScrollPane scrollPane = null;
    JLabel statusProcess = null;
    JPanel buttonGroup = null;
    JButton doneProcess = null;
    JButton cancelProcess = null;
    SpringLayout layout = null;
    JSONObject scannerParams = null;
    Response resultRequest = null;
    JSONObject resultJson = null;
    JSONObject jsonArray = null;
    JSONObject jsonTruck = null;
    JSONObject jsonDriver = null;
    int sleepTime = 0;
    int delay = 0;
    Timer timer = null;
    String currentLog = "";
    String notifPath = "";
    boolean isConnected = false;
    boolean isScanDone = false;

    public ScannerIpBasedManager(FrmMain frmMain, JPanel panel, String ip, 
            int port, int indexScanner, int indexScannerShow) {
        this.frmMain = frmMain;
        this.panel = panel;
        this.ip = ip;
        this.port = port;
        this.indexScanner = indexScanner;
        this.indexScannerShow = indexScannerShow;
        this.notifPath = "./notification.mp3";
        
        new ScannerTask().execute();
    }
    
    private static class ScannerPair {
        private final String scannerInput;
        private final boolean connect;
        
        ScannerPair(String scannerInput, boolean connect) {
            this.scannerInput = scannerInput;
            this.connect = connect;
        }
    }
    
    private class ScannerTask extends SwingWorker<Void, ScannerPair> {
        @Override
        protected Void doInBackground() {
            createObjects();
            
            while (!frmMain.isRestart) {
                if (!tc.isConnected()) {
                    tryingToConnect();
                } else {
                    try {
                        tc.disconnect();
                    } catch (IOException ex) {
                        Logger.getLogger(ScannerIpBasedManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tryingToConnect();
                    instr = tc.getInputStream();
                    try {
                        ret_read = instr.read(buff);
                        if (ret_read > 0) {
                            scannerBuffer = (new String(buff, 0, ret_read)).replaceAll("\\r\\n", "");
                            if (!scannerBuffer.equals("")) {
                                scannerInput += scannerBuffer;
                            }

                            publish(new ScannerPair(scannerInput, true));
                        }
                    } catch (Exception e) {
                        if (frmMain.isDebugging.equals("true")) {
                            System.err.println("Exception while reading socket (" 
                                    + frmMain.scanner[indexScanner][1].toString() 
                                    + "):" + e.getMessage());
                        }
                    }
                }
            }
            return null;
        }
 
        @Override
        protected void process(List<ScannerPair> pairs) {
            ScannerPair pair = pairs.get(pairs.size() - 1);
            
            if (frmMain.isDebugging.equals("true") && pair.scannerInput.length() > 0) {
                try {
                    if (pair.scannerInput.length() >= Integer.parseInt(frmMain.config.get("minLengthQrCode").toString())) {
                        System.out.println("Scanner Input : " + pair.scannerInput);
                    } else {
                        System.out.println("input scanner tidak melebihi minimal qrcode character length");
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(ScannerIpBasedManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("connect : " + pair.connect);
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    panel.revalidate();
                    isConnected(pair.connect);
//                    delay
                        if (!pair.scannerInput.equals("")) {
                            if (!scannerInput.equals(prevScannerInput)) {
                                prevScannerInput = scannerInput;
                                storeInput();
                            }
                        } else {
                            prevScannerInput = "";
                        }
                }
            });
        }
        
        private void tryingToConnect() {
            try {
                tc.connect(ip, port);
                publish(new ScannerPair(scannerInput, true));
            } catch (IOException ex) {
                if (frmMain.isDebugging.equals("true")) {
                    System.err.println("Exception while connecting (" 
                            + frmMain.scanner[indexScanner][1].toString() 
                            + "):" + ex.getMessage());
                }
                publish(new ScannerPair(scannerInput, false));
            }
        }
    }
    
    public void isConnected(boolean connect) {
        if (connect) {
            frmMain.tabResult.setIconAt(this.indexScannerShow, new ImageIcon("./icons/dot_green.png"));
            if (result.getText().equals("Not Connected...")) {
                result.setForeground(Color.BLACK);
                result.setText("Ready...");
            }
            isConnected = true;
        } else {
            frmMain.tabResult.setIconAt(this.indexScannerShow, new ImageIcon("./icons/dot_red.png"));
            result.setForeground(Color.RED);
            result.setText("Not Connected...");
            isConnected = false;
        }
    }
    
    public void resetDelay() {
        try {
            delay = (int) (Double.parseDouble(frmMain.config.getString("delayScan")) * (1000 / sleepTime));
        } catch (JSONException ex) {
            Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createObjects() {
        logManager = new LogManager();
        scannerParams = new JSONObject();
        sleepTime = 100;
        tc = new TelnetClient();
        
        title = new JLabel("QR Code Scan");
        separator = new JSeparator();
        logs = new JButton("Logs");
        logs.setIcon(new ImageIcon("./icons/logs.png"));
        separatorBottom = new JSeparator();
        result = new JTextArea("Not Connected...");
        result.setForeground(Color.RED);
        scrollPane = new JScrollPane(result);
        statusProcess = new JLabel("Status Proses");
        buttonGroup = new JPanel();
        doneProcess = new JButton("OK");
        cancelProcess = new JButton("Cancel");
        
        layout = new SpringLayout();
        panel.setLayout(layout);
        
        buttonGroup.setLayout(new FlowLayout());
        
        panel.add(title);
        panel.add(separator);
        panel.add(logs);
        panel.add(scrollPane);
        panel.add(separatorBottom);
        panel.add(statusProcess);
        panel.add(buttonGroup);
        
        buttonGroup.add(doneProcess);
        buttonGroup.add(cancelProcess);
        
        panel.setBackground(Color.white);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        buttonGroup.setBackground(Color.white);
        buttonGroup.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        layout.putConstraint(SpringLayout.NORTH, title, 0, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, title, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, title, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, separator, 11, SpringLayout.SOUTH, title);
        layout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, logs, 5, SpringLayout.SOUTH, separator);
        layout.putConstraint(SpringLayout.EAST, logs, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, result, 0, SpringLayout.NORTH, scrollPane);
        layout.putConstraint(SpringLayout.EAST, result, 0, SpringLayout.EAST, scrollPane);
        layout.putConstraint(SpringLayout.WEST, result, 0, SpringLayout.WEST, scrollPane);
        layout.putConstraint(SpringLayout.SOUTH, result, 0, SpringLayout.SOUTH, scrollPane);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, logs);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, separatorBottom);
        layout.putConstraint(SpringLayout.WEST, separatorBottom, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, separatorBottom, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, separatorBottom, 0, SpringLayout.NORTH, statusProcess);
        layout.putConstraint(SpringLayout.WEST, statusProcess, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, statusProcess, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, statusProcess, 0, SpringLayout.NORTH, buttonGroup);
        layout.putConstraint(SpringLayout.WEST, buttonGroup, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, buttonGroup, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, buttonGroup, 0, SpringLayout.SOUTH, panel);
        
        title.setFont(new java.awt.Font("Monospaced", 1, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        separator.setOrientation(SwingConstants.HORIZONTAL);
        separatorBottom.setOrientation(SwingConstants.HORIZONTAL);
        
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(5, 0, 0, 0));
        scrollPane.setBackground(Color.white);
        
        result.setEditable(false);
        result.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        
        statusProcess.setHorizontalAlignment(SwingConstants.CENTER);
        
        doneProcess.setEnabled(false);
        cancelProcess.setEnabled(false);
        
        logs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FrmLogs().setVisible(true);
            }
        });
        
        frmMain.tabResult.setIconAt(this.indexScannerShow, new ImageIcon("./icons/dot_red.png"));
    }
    
    public void updateScanningUI() {
        result.setForeground(Color.BLACK);
        result.setText("Scanning...");
    }
    
    public void playSound(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            Logger.getLogger(ScannerIpBasedManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void storeInput() {
        frmMain.tabNotify(this.indexScannerShow);
        try {
            scannerParams.put("qrcode", scannerInput.replaceAll("\\r\\n", ""));
            if (!scannerInput.equals("")) {
                scannerParams.put("wb_id", frmMain.config.getString("kodeTimbangan"));
                scannerParams.put("track_name", frmMain.scanner[indexScanner][5].toString());
                try {
                    System.out.println("send post request" + scannerInput);
                    System.out.println(scannerParams);
                    System.out.println(frmMain.config.get("apiQrCode").toString());
                    System.out.println(frmMain.config.get("accessToken").toString());
                    resultRequest = frmMain.okHttpManager.sendPost(
                            frmMain.config.get("apiQrCode").toString(), 
                            scannerParams, 
                            frmMain.authManager.readAuth());
                    showResult(resultRequest);
                    scannerInput = "";
                } catch (Exception ex) {
                    Logger.getLogger(ThreadScanner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(ScannerIpBasedManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showResult(Response resultResponse) throws IOException, JSONException {
        currentLog = "";
        resultJson = new JSONObject(resultResponse.body().string());
        
        if (resultRequest.isSuccessful()) {
            result.setText("Success: QR Code Valid");
            result.append("\n\n");
            result.append("SO Number: " + resultJson.get("so_number").toString());
            result.append("\n");
            result.append("DO Number: " + resultJson.get("so_number").toString());
            result.append("\n");
            jsonArray = new JSONObject(resultJson.getJSONArray("trucks").get(0).toString());
            jsonArray = new JSONObject(jsonArray.getJSONArray("assignments").get(0).toString());
            jsonTruck = new JSONObject(jsonArray.getJSONArray("trucks").get(0).toString());
            jsonDriver = new JSONObject(jsonArray.getJSONArray("drivers").get(0).toString());
            result.append("Truk: " + String.join(" ", jsonTruck.get("number_array").toString().replace("\"", "").replace("[", "").replace("]", "").split(",")));
            result.append("\n");
            result.append("Sopir: " + jsonDriver.getString("name"));
            
            currentLog = "Success: QR Code Valid (" 
                    + "SO Number: " + resultJson.get("so_number").toString() + "-"
                    + "DO Number: " + resultJson.get("so_number").toString() + ")";

            new UpdateTrackController(scannerInput, 
                frmMain, frmMain.scanner[indexScanner][5].toString(), 
                statusProcess, doneProcess, cancelProcess);
            
        } else {
            result.setForeground(Color.RED);
            System.out.println("Gagal: QR Code tidak valid (" + this.resultJson.get("message").toString() + ")");
            result.setText("Gagal: QR Code tidak valid (" + this.resultJson.get("message").toString() + ")");
            
            currentLog = "Gagal: QR Code tidak valid (" 
                    + this.resultJson.get("message").toString() + ")";
        }
        
        logManager.updateLog(currentLog);
        if (!frmMain.isMuted) {
            playSound("notification.wav");
        }
    }
    
}
