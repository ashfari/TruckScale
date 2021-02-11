/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class JSerialManager {
    
    String message = "";
    JSONObject weight = null;
    
    public JSONObject getWeight() {
        weight = new JSONObject();
        
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }
            
            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();

                for (int i = 0; i < newData.length; ++i) {
                    if (newData[i] == 2) {
                        message = "";
                    } else if (newData[i] == 3) {
                        String messages[] = message.trim().split("\\s");
                        System.out.println("length" + messages.length);
                        System.out.println(message);
                        
                        try {
                            weight.put("value", messages[0]);
                            weight.put("unit", messages[1]);
                        } catch (JSONException ex) {
                            Logger.getLogger(JSerialManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        comPort.closePort();
                    } else {
                        message += (char) newData[i];
                    }
                }
            }
        });
        
        return weight;
    }
}
