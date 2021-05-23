/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.ThreadWeight;
import controller.ThreadScanner;
import controller.ApiManager;
import controller.AuthManager;
import controller.SerialPortManager;
import controller.ConfigManager;
import controller.OkHttpManager;
import controller.PasswordManager;
import controller.ScannerIpBasedManager;
import controller.ScannerManager;
import controller.ThreadMain;
import controller.ThreadScannerIpBased;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class FrmMain extends javax.swing.JFrame {
    
    ThreadMain threadMain = null;
    ThreadWeight threadWeight = null;
    ThreadScanner threadScanner = null;
    ThreadScannerIpBased threadScannerIpBased = null;
    public SerialPortManager serialPortManager = null;
    public ConfigManager configManager = null;
    public ScannerManager scannerManager = null;
    public JSONObject currentWeight = null;
    public JSONObject config = new JSONObject();
    public ApiManager apiManager = null;
    public OkHttpManager okHttpManager = null;
    public String scannerInput = null;
    public Logger logger = null;
    public String isDebugging = "";
    public String manualQrCode = "";
    public JSONObject auth = null;
    public AuthManager authManager = null;
    public Object[][] scanner = null;
    public Object[][] scannerFromSetting = null;
    Response result = null;
    JPopupMenu menu;
    int indexScannerShow = 0;
    public boolean isRestart = false;
    public FrmMain frmMain = null;
    public boolean isMuted = false;

    /**
     * Creates new form FrmMain
     */
    public FrmMain() {
//        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
//        this.setUndecorated(true);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("truku.png")));

        createObjects();

        this.threadMain.start();
//        this.threadScannerIpBased.start();
        this.threadWeight.start();
    }
    
    public void createObjects() {
        btnMute.setIcon(new ImageIcon("./icons/sound_unmute.png"));
        btnScannerSetting.setIcon(new ImageIcon("./icons/scanner.png"));
        btnQrcode.setIcon(new ImageIcon("./icons/qrcode.png"));
        btnConfig.setIcon(new ImageIcon("./icons/setting.png"));
        btnExit.setIcon(new ImageIcon("./icons/exit.png"));
        btnUser.setIcon(new ImageIcon("./icons/user.png"));
        menuItemLogout.setIcon(new ImageIcon("./icons/logout.png"));
        labelRefresh.setIcon(new ImageIcon("./icons/refresh.png"));
        btnMute.setText("");
        btnScannerSetting.setText("");
        btnQrcode.setText("");
        btnConfig.setText("");
        btnExit.setText("");
        labelRefresh.setText("");

        createUserPopUp();
        try {
            this.configManager = new ConfigManager();
            this.config = new JSONObject();
            this.config = this.configManager.getConfig();
            this.scannerManager = new ScannerManager();
            this.scanner = this.scannerManager.getScanner();
            this.logger = LogManager.getLogManager().getLogger("");
            setDebugging();
            this.scannerInput = "";
            
            this.apiManager = new ApiManager();
            this.okHttpManager = new OkHttpManager();
            this.currentWeight = new JSONObject();
            new PasswordManager().createPassword();
            globalKeyListener();
            this.auth = new JSONObject();
            this.authManager = new AuthManager();
            this.result = this.okHttpManager.sendGetAccountInfo(this.config.getString("apiAccountInfo"), this.authManager.readAuth());
            if (this.result.isSuccessful()) {
                this.auth = new JSONObject(result.body().string());
                btnUser.setText(this.auth.getString("name"));
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Anda tidak memiliki izin autentikasi! Apakah anda ingin Login?");
                if(dialogResult == JOptionPane.YES_OPTION){
                    this.logout();
                    return;
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.threadMain = new ThreadMain(this);
        try {
            this.threadWeight = new ThreadWeight(this);
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
//        this.threadScannerIpBased = new ThreadScannerIpBased(this, "127.0.0.1", 23);
        this.serialPortManager = new SerialPortManager();
        frmMain = this;

        this.setTabResults();
    }
    
    public void setTabResults() {
        this.indexScannerShow = 0;
        this.isRestart = false;
        tabResult.removeAll();
        if (this.scannerFromSetting != null) {
            this.scanner = this.scannerFromSetting;
            this.scannerFromSetting = null;
        }
        for (int i = 0; i < this.scanner.length; i++) {
            if (this.scanner[i][7].toString().equals("true")) {
                tabResult.addTab(this.scanner[i][1].toString(), this.createNewPanelResult(i, this.indexScannerShow));
                this.indexScannerShow++;
            }
        }
        tabResult.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabResult.getSelectedIndex() >= 0) {
                    tabResult.setForegroundAt(tabResult.getSelectedIndex(), Color.black);
                }
            }
        });
    }
    
    public void tabNotify(int indexScannerShow) {
        if (tabResult.getSelectedIndex() != indexScannerShow) {
            tabResult.setForegroundAt(indexScannerShow, Color.GREEN.darker());
        }
    }
    
    private JPanel createNewPanelResult(int indexScanner, int indexScannerShow) {
        JPanel panel = new JPanel();

        new ScannerIpBasedManager(frmMain, panel, 
                scanner[indexScanner][3].toString(), 
                Integer.parseInt(scanner[indexScanner][4].toString()),
                indexScanner, indexScannerShow
        );
        
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                
//            }
//        });
        
        return panel;
    }
    
    private void createUserPopUp() {
        // Create a JPopupMenu
        menu=new JPopupMenu();
        
        // Create JMenuItems
        menuItemLogout.setText("Logout");
        
        // Add JMenuItems to JPopupMenu
        menu.add(menuItemLogout);
    }
    
    // This method does it all!
    private void showPopup(ActionEvent ae)
    {
        // Get the event source
        Component b=(Component)ae.getSource();
        
        // Get the location of the point 'on the screen'
        Point p=b.getLocationOnScreen();
        
        // Show the JPopupMenu via program
        
        // Parameter desc
        // ----------------
        // this - represents current frame
        // 0,0 is the co ordinate where the popup
        // is shown
        menu.show(this,0,0);
        
        // Now set the location of the JPopupMenu
        // This location is relative to the screen
        menu.setLocation(p.x,p.y+b.getHeight());
    }
    
    public void globalKeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (e.getKeyCode() > 40 && e.getKeyCode() != 32) {
                            scannerInput += e.getKeyChar() + "";
                        }
                    }
                    return false;
                }
            });
    }
    
    public void setDebugging() {
        try {
            this.isDebugging = config.getString("isDebugging");
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isDebugging.equals("true")) {
            logger.setLevel(Level.INFO);
            btnQrcode.setVisible(true);
        } else {
            logger.setLevel(Level.OFF);
            btnQrcode.setVisible(false);
        }
    }
    
    public void logout() {
        if (this.authManager.deleteAuth()) {
            this.setVisible(false);
            JOptionPane.showMessageDialog(this, "Logout success!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            new FrmLogin().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Logout failed!", "Logout", JOptionPane.ERROR_MESSAGE);
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

        menuItemLogout = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        main_title = new javax.swing.JLabel();
        btnConfig = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        date_time_counter = new javax.swing.JLabel();
        dotLabel = new javax.swing.JLabel();
        refreshRateLabel = new javax.swing.JLabel();
        labelRefresh = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        splitPaneMain = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        weightValue = new javax.swing.JLabel();
        weightUnit = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        last_sent = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        UIManager.put("TabbedPane.selectedForeground", Color.black);
        tabResult = new javax.swing.JTabbedPane();
        btnQrcode = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        btnScannerSetting = new javax.swing.JButton();
        btnMute = new javax.swing.JButton();

        menuItemLogout.setText("jMenuItem1");
        menuItemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLogoutActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 519));
        setSize(new java.awt.Dimension(800, 519));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        main_title.setFont(new java.awt.Font("Monospaced", 1, 36)); // NOI18N
        main_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        main_title.setText("Main Title");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(main_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(main_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnConfig.setText("Config");
        btnConfig.setToolTipText("");
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        date_time_counter.setText("08/02/2021 05:46:58");

        refreshRateLabel.setText("1s");

        labelRefresh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelRefresh.setText("Refresh");
        labelRefresh.setAlignmentY(0.0F);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(date_time_counter, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshRateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dotLabel)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dotLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(refreshRateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
            .addComponent(date_time_counter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnExit.setText("Exit");
        btnExit.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        splitPaneMain.setDividerLocation(390);

        jPanel6.setBackground(new java.awt.Color(153, 204, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(434, 136));

        weightValue.setFont(new java.awt.Font("Monospaced", 1, 75)); // NOI18N
        weightValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        weightValue.setText("0");

        weightUnit.setFont(new java.awt.Font("Monospaced", 1, 48)); // NOI18N
        weightUnit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        weightUnit.setText("Kg");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(weightValue, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weightUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weightValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(weightUnit)))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(434, 78));

        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("WEIGHT");

        last_sent.setText("Last sent : -");
        last_sent.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addComponent(last_sent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(99, 99, 99)
                .addComponent(last_sent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPaneMain.setLeftComponent(leftPanel);

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPaneMain.setRightComponent(rightPanel);

        btnQrcode.setText("QrCode");
        btnQrcode.setToolTipText("");
        btnQrcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQrcodeActionPerformed(evt);
            }
        });

        btnUser.setText("User");
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });

        btnScannerSetting.setText("Scanner Settings");
        btnScannerSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScannerSettingActionPerformed(evt);
            }
        });

        btnMute.setText("Mute");
        btnMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(splitPaneMain, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScannerSetting)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQrcode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfig)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUser)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfig)
                    .addComponent(btnExit)
                    .addComponent(btnQrcode)
                    .addComponent(btnUser)
                    .addComponent(btnScannerSetting)
                    .addComponent(btnMute))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPaneMain)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        new FrmPassword().setVisible(true);
    }//GEN-LAST:event_btnConfigActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnQrcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQrcodeActionPerformed
        // TODO add your handling code here:
        new FrmQrCode(this).setVisible(true);
    }//GEN-LAST:event_btnQrcodeActionPerformed

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        showPopup(evt);
    }//GEN-LAST:event_btnUserActionPerformed

    private void menuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLogoutActionPerformed
        this.logout();
    }//GEN-LAST:event_menuItemLogoutActionPerformed

    private void btnScannerSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScannerSettingActionPerformed
        new FrmListScanner(this).setVisible(true);
    }//GEN-LAST:event_btnScannerSettingActionPerformed

    private void btnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuteActionPerformed
        isMuted = !isMuted;
        if (isMuted) {
            btnMute.setIcon(new ImageIcon("./icons/sound_mute.png"));
        } else {
            btnMute.setIcon(new ImageIcon("./icons/sound_unmute.png"));
        }
    }//GEN-LAST:event_btnMuteActionPerformed

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
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnQrcode;
    private javax.swing.JButton btnScannerSetting;
    private javax.swing.JButton btnUser;
    public javax.swing.JLabel date_time_counter;
    public javax.swing.JLabel dotLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel labelRefresh;
    public javax.swing.JLabel last_sent;
    public javax.swing.JPanel leftPanel;
    public javax.swing.JLabel main_title;
    private javax.swing.JMenuItem menuItemLogout;
    public javax.swing.JLabel refreshRateLabel;
    private javax.swing.JPanel rightPanel;
    public javax.swing.JSplitPane splitPaneMain;
    public javax.swing.JTabbedPane tabResult;
    public javax.swing.JLabel weightUnit;
    public javax.swing.JLabel weightValue;
    // End of variables declaration//GEN-END:variables
}
