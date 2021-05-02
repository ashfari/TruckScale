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
import controller.ScannerManager;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class FrmMain extends javax.swing.JFrame {
    
    ThreadWeight threadWeight = null;
    ThreadScanner threadScanner = null;
    ThreadScannerIpBased threadScannerIpBased = null;
    public SerialPortManager averyWeighTronix = null;
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
    Response result = null;
    JPopupMenu menu;

    /**
     * Creates new form FrmMain
     */
    public FrmMain() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("truku.png")));

        createObjects();
        
        this.auth = auth;

        threadScanner.start();
        threadScannerIpBased.start();
        threadWeight.start();
    }
    
    public void createObjects() {
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
            txtResultScan.setText("Ready...");
            this.auth = new JSONObject();
            this.authManager = new AuthManager();
            this.result = this.okHttpManager.sendGetAccountInfo(config.getString("apiAccountInfo"), this.authManager.readAuth());
            if (this.result.isSuccessful()) {
                this.auth = new JSONObject(result.body().string());
                btnUser.setText(this.auth.getString("name"));
            } else {
                this.setVisible(false);
                new FrmLogin().setVisible(true);
            }
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.threadWeight = new ThreadWeight(this);
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.threadScanner = new ThreadScanner(this);
        this.threadScannerIpBased = new ThreadScannerIpBased(this, "127.0.0.1", 23);
        this.averyWeighTronix = new SerialPortManager();
        
        this.setTabResults();
    }
    
    public void setTabResults() {
        tabResult.setTitleAt(0, this.scanner[0][1].toString());
        tabResult.setTitleAt(1, this.scanner[1][1].toString());
        for (int i = 2; i < this.scanner.length; i++) {
            try {
                tabResult.setTitleAt(i, this.scanner[i][1].toString());
            } catch (Exception e) {
                tabResult.addTab(this.scanner[i][1].toString(), new JPanel());
            }
        }
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
    
    public void resetResultScan() {
        txtResultScan.setForeground(Color.BLACK);
        txtResultScan.setText("Ready...");
    }
    
    public void setDebugging() {
        try {
            this.isDebugging = config.getString("isDebugging");
        } catch (JSONException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isDebugging.equals("true")) {
            logger.setLevel(Level.INFO);
            btn_qrcode.setVisible(true);
        } else {
            logger.setLevel(Level.OFF);
            btn_qrcode.setVisible(false);
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
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        date_time_counter = new javax.swing.JLabel();
        dotLabel = new javax.swing.JLabel();
        refreshRateLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        exit = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        weightValue = new javax.swing.JLabel();
        weightUnit = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        last_sent = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        tabResult = new javax.swing.JTabbedPane();
        panelResult = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtResultScan = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        panelResult1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtResultScan1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btn_qrcode = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

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

        jButton1.setText("Config");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        date_time_counter.setText("08/02/2021 05:46:58");

        refreshRateLabel.setText("1s");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Refresh");
        jLabel4.setAlignmentY(0.0F);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(date_time_counter, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshRateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dotLabel)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dotLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(refreshRateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
            .addComponent(date_time_counter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        exit.setText("Exit");
        exit.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(390);

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
                .addComponent(weightValue, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel7);

        panelResult.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setBorder(null);

        txtResultScan.setEditable(false);
        txtResultScan.setColumns(20);
        txtResultScan.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        txtResultScan.setLineWrap(true);
        txtResultScan.setRows(5);
        txtResultScan.setWrapStyleWord(true);
        txtResultScan.setBorder(null);
        jScrollPane2.setViewportView(txtResultScan);

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("QR Code Scan");

        javax.swing.GroupLayout panelResultLayout = new javax.swing.GroupLayout(panelResult);
        panelResult.setLayout(panelResultLayout);
        panelResultLayout.setHorizontalGroup(
            panelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        panelResultLayout.setVerticalGroup(
            panelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabResult.addTab("tab1", panelResult);

        panelResult1.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane3.setBorder(null);

        txtResultScan1.setEditable(false);
        txtResultScan1.setColumns(20);
        txtResultScan1.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        txtResultScan1.setLineWrap(true);
        txtResultScan1.setRows(5);
        txtResultScan1.setWrapStyleWord(true);
        txtResultScan1.setBorder(null);
        jScrollPane3.setViewportView(txtResultScan1);

        jLabel3.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("QR Code Scan");

        javax.swing.GroupLayout panelResult1Layout = new javax.swing.GroupLayout(panelResult1);
        panelResult1.setLayout(panelResult1Layout);
        panelResult1Layout.setHorizontalGroup(
            panelResult1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResult1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResult1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jSeparator2)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        panelResult1Layout.setVerticalGroup(
            panelResult1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResult1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabResult.addTab("tab1", panelResult1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult, javax.swing.GroupLayout.PREFERRED_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel8);

        btn_qrcode.setText("QrCode");
        btn_qrcode.setToolTipText("");
        btn_qrcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_qrcodeActionPerformed(evt);
            }
        });

        btnUser.setText("User");
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });

        jButton2.setText("Scanner Settings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_qrcode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUser)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(exit)
                    .addComponent(btn_qrcode)
                    .addComponent(btnUser)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new FrmPassword().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    private void btn_qrcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_qrcodeActionPerformed
        // TODO add your handling code here:
        new FrmQrCode(this).setVisible(true);
    }//GEN-LAST:event_btn_qrcodeActionPerformed

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        showPopup(evt);
    }//GEN-LAST:event_btnUserActionPerformed

    private void menuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLogoutActionPerformed
        if (this.authManager.deleteAuth()) {
            this.setVisible(false);
            JOptionPane.showMessageDialog(this, "Logout success!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            new FrmLogin().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Logout failed!", "Logout", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_menuItemLogoutActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new FrmListScanner().setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

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
    private javax.swing.JButton btnUser;
    private javax.swing.JButton btn_qrcode;
    public javax.swing.JLabel date_time_counter;
    public javax.swing.JLabel dotLabel;
    private javax.swing.JButton exit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    public javax.swing.JLabel last_sent;
    public javax.swing.JLabel main_title;
    private javax.swing.JMenuItem menuItemLogout;
    private javax.swing.JPanel panelResult;
    private javax.swing.JPanel panelResult1;
    public javax.swing.JLabel refreshRateLabel;
    private javax.swing.JTabbedPane tabResult;
    public javax.swing.JTextArea txtResultScan;
    public javax.swing.JTextArea txtResultScan1;
    public javax.swing.JLabel weightUnit;
    public javax.swing.JLabel weightValue;
    // End of variables declaration//GEN-END:variables
}
