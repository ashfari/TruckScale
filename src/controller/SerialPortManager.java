/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import view.FrmConfig;
import view.FrmListScannerAdd;
import view.FrmMain;

/**
 *
 * @author Sahab
 */
public class SerialPortManager {
    
    FrmMain window = null;
    FrmListScannerAdd frmListScannerAdd = null;
    String message = "";
    String messages[] = null;
    JSONObject weight = null;
    SerialPort comPort = null;
    SerialPort[] comPorts = null;
    ScannerManager scannerManager = null;
    public Object[][] scanner = null;

    public SerialPortManager() {
    }

    public SerialPortManager(FrmListScannerAdd frmListScannerAdd) {
        this.frmListScannerAdd = frmListScannerAdd;
    }
    
    public SerialPortManager(FrmMain window) throws JSONException, FileNotFoundException {
        this.window = window;
        this.scannerManager = new ScannerManager();
        this.scanner = scannerManager.getScanner();
    }
    
    public void getWeight() throws JSONException {
        comPort = SerialPort.getCommPorts()[Integer.parseInt(this.scanner[1][4].toString())];
        comPort.openPort();
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }
            
            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();
                
                if (window.isDebugging.equals("true")) {
                    System.out.println("data from port : " + newData);
                }
                
                for (int i = 0; i < newData.length; ++i) {
                    if (newData[i] == 2) {
                        message = "";
                    } else if (newData[i] == 3) {
                        messages = message.trim().split("\\s");
                        try {
                            window.currentWeight.put("value", messages[0]);
                            window.currentWeight.put("unit", messages[1]);
//                            window.txtLog.append(message + "\n");
                        } catch (JSONException ex) {
                            Logger.getLogger(SerialPort.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        message += (char) newData[i];
                    }
                }
            }
        });
    }
    
    public void getPorts() {
        comPorts = SerialPort.getCommPorts();
        
        for (int i = 0; i < comPorts.length; i++) {
            frmListScannerAdd.cbComPort.addItem(comPorts[i].getDescriptivePortName());
        }
    }
}
