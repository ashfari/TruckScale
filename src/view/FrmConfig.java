/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.AuthManager;
import controller.ConfigManager;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class FrmConfig extends javax.swing.JFrame {

    ConfigManager configManager = null;
    JSONObject config = null;
    String isDebugging = "";
    AuthManager authManager = null;
    
    /**
     * Creates new form FrmConfig
     */
    public FrmConfig() {
        this.setUndecorated(true);
        initComponents();
        this.setLocationRelativeTo(null);
        
        createObjects();
        
        setForm();
    }
    
    private void createObjects() {
        this.configManager = new ConfigManager();
        this.config = new JSONObject();
        try {
            config = configManager.getConfig();
            isDebugging = config.getString("isDebugging");
        } catch (JSONException ex) {
            Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.authManager = new AuthManager();
    }
    
    private void setDebugging() {
        if (isDebugging.equals("true")) {
            btnOn.setSelected(true);
            btnOff.setSelected(false);
        } else {
            btnOn.setSelected(false);
            btnOff.setSelected(true);
        }
    }
    
    private void setForm() {
//        try {
//            cbComPort.setSelectedIndex(Integer.parseInt(config.get("comPort").toString()));
//        } catch (Exception e) {
//        }
        try {
            txtTitleAplikasi.setText(config.getString("title"));
            txtKodeTimbangan.setText(config.getString("kodeTimbangan"));
            txtAccessToken.setText(authManager.readAuth());
            txtApiNilaiTimbangan.setText(config.getString("apiTimbangan"));
            txtApiQrCode.setText(config.getString("apiQrCode"));
            txtApiUpdateTrack.setText(config.getString("apiUpdateTrack"));
            txtMinLengthQrCode.setText(config.getString("minLengthQrCode"));
            txtMinWeight.setText(config.getString("minStepWeight"));
            txtRefreshRate.setText(config.getString("refreshRate"));
            setDebugging();
            txtDelayScan.setText(config.getString("delayScan"));
            txtDelayOk.setText(config.getString("delayOk"));
            txtClientId.setText(config.getString("clientId") != null ? config.getString("clientId") : "");
            txtClientSecret.setText(config.getString("clientSecret") != null ? config.getString("clientSecret") : "");
            txtApiRequestToken.setText(config.getString("apiRequestToken") != null ? config.getString("apiRequestToken") : "");
            txtApiAccountInfo.setText(config.getString("apiAccountInfo") != null ? config.getString("apiAccountInfo") : "");
        } catch (Exception e) {
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        Aplikasi = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        txtTitleAplikasi = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAccessToken = new javax.swing.JTextArea();
        jLabel24 = new javax.swing.JLabel();
        btnOn = new javax.swing.JToggleButton();
        btnOff = new javax.swing.JToggleButton();
        jLabel32 = new javax.swing.JLabel();
        txtRefreshRate = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtDelayScan = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtDelayOk = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtMinLengthQrCode = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMinWeight = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtKodeTimbangan = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        txtApiRequestToken = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txtApiAccountInfo = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtClientId = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtClientSecret = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtApiQrCode = new javax.swing.JTextField();
        txtApiUpdateTrack = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtApiNilaiTimbangan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 300));
        setSize(new java.awt.Dimension(400, 300));

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        Aplikasi.setDividerLocation(370);

        jPanel4.setPreferredSize(new java.awt.Dimension(380, 380));

        jLabel22.setText("Nama Aplikasi");

        jLabel23.setText("Access Token");

        txtAccessToken.setEditable(false);
        txtAccessToken.setColumns(20);
        txtAccessToken.setRows(5);
        jScrollPane2.setViewportView(txtAccessToken);

        jLabel24.setText("Debugging");

        btnOn.setText("ON");
        btnOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnActionPerformed(evt);
            }
        });

        btnOff.setText("OFF");
        btnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOffActionPerformed(evt);
            }
        });

        jLabel32.setText("Refresh Rate (detik)");

        jLabel33.setText("Delay Scan (detik)");

        jLabel34.setText("Delay OK (detik)");

        jLabel30.setText("Jumlah Minimal Karakter QR Code");

        jLabel10.setText("Jarak Minimum Weight (kg)");

        jLabel13.setText("Kode Timbangan");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTitleAplikasi)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMinLengthQrCode, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                            .addComponent(txtDelayScan)
                            .addComponent(jLabel30)
                            .addComponent(jLabel33)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnOn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOff))
                            .addComponent(jLabel32)
                            .addComponent(txtRefreshRate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDelayOk, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addComponent(txtMinWeight))
                            .addComponent(jLabel34)
                            .addComponent(jLabel10)
                            .addComponent(jLabel13)
                            .addComponent(txtKodeTimbangan, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTitleAplikasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOn)
                    .addComponent(btnOff))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDelayOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDelayScan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMinLengthQrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMinWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKodeTimbangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRefreshRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Aplikasi.setLeftComponent(jPanel4);

        jPanel5.setMaximumSize(new java.awt.Dimension(380, 380));
        jPanel5.setPreferredSize(new java.awt.Dimension(380, 380));

        jLabel35.setText("URL API Request Token");

        jLabel36.setText("URL API Login");

        jLabel25.setText("Client ID");

        jLabel26.setText("Client Secret");

        jLabel6.setText("URL API Post Data QR Code Scanner");

        jLabel12.setText("URL API Post Update Track");

        jLabel9.setText("URL API Post Data Nilai Timbangan");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtApiUpdateTrack, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApiNilaiTimbangan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApiQrCode, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtClientId, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtClientSecret, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApiRequestToken, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApiAccountInfo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 167, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClientId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClientSecret, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApiRequestToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApiAccountInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApiQrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApiNilaiTimbangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApiUpdateTrack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        Aplikasi.setRightComponent(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(632, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Aplikasi, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(405, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Aplikasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(46, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        // TODO add your handling code here:
        try {
            configManager.updateConfig(getNewConfig());
            this.setVisible(false);
        } catch (JSONException ex) {
            Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnOkActionPerformed

    private JSONObject getNewConfig() throws JSONException{
        JSONObject newConfig = new JSONObject();
        try {
            newConfig.put("title", txtTitleAplikasi.getText());
            newConfig.put("kodeTimbangan", txtKodeTimbangan.getText());
            newConfig.put("accessToken", txtAccessToken.getText());
            newConfig.put("apiTimbangan", txtApiNilaiTimbangan.getText());
            newConfig.put("apiQrCode", txtApiQrCode.getText());
            newConfig.put("apiUpdateTrack", txtApiUpdateTrack.getText());
            newConfig.put("minLengthQrCode", txtMinLengthQrCode.getText());
            newConfig.put("minStepWeight", txtMinWeight.getText());
            newConfig.put("refreshRate", txtRefreshRate.getText());
            newConfig.put("isDebugging", isDebugging);
            newConfig.put("delayScan", txtDelayScan.getText());
            newConfig.put("delayOk", txtDelayOk.getText());
            newConfig.put("clientId", txtClientId.getText());
            newConfig.put("clientSecret", txtClientSecret.getText());
            newConfig.put("apiRequestToken", txtApiRequestToken.getText());
            newConfig.put("apiAccountInfo", txtApiAccountInfo.getText());
        } catch (Exception e) {
        }
        
        return newConfig;
    }
    
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnActionPerformed
        if (btnOn.isSelected()) {
            isDebugging = "true";
            btnOff.setSelected(false);
        } else if (!btnOn.isSelected()) {
            isDebugging = "false";
            btnOff.setSelected(true);
        }
    }//GEN-LAST:event_btnOnActionPerformed

    private void btnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOffActionPerformed
        if (btnOff.isSelected()) {
            isDebugging = "false";
            btnOn.setSelected(false);
        } else if (!btnOff.isSelected()) {
            isDebugging = "true";
            btnOn.setSelected(true);
        }
    }//GEN-LAST:event_btnOffActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPassword().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane Aplikasi;
    private javax.swing.JButton btnCancel;
    private javax.swing.JToggleButton btnOff;
    private javax.swing.JButton btnOk;
    private javax.swing.JToggleButton btnOn;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtAccessToken;
    private javax.swing.JTextField txtApiAccountInfo;
    private javax.swing.JTextField txtApiNilaiTimbangan;
    private javax.swing.JTextField txtApiQrCode;
    private javax.swing.JTextField txtApiRequestToken;
    private javax.swing.JTextField txtApiUpdateTrack;
    private javax.swing.JTextField txtClientId;
    private javax.swing.JTextField txtClientSecret;
    private javax.swing.JTextField txtDelayOk;
    private javax.swing.JTextField txtDelayScan;
    private javax.swing.JTextField txtKodeTimbangan;
    private javax.swing.JTextField txtMinLengthQrCode;
    private javax.swing.JTextField txtMinWeight;
    private javax.swing.JTextField txtRefreshRate;
    private javax.swing.JTextField txtTitleAplikasi;
    // End of variables declaration//GEN-END:variables
}
