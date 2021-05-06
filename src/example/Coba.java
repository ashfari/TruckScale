/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sahab
 */
public class Coba {
    public static void main(String[] args) {
        try {
            new USBComm();
        } catch (IOException ex) {
            Logger.getLogger(Coba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
