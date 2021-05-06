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
import controller.ThreadMain;
import controller.ThreadScannerIpBased;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
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
                this.authManager.deleteAuth();
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
        
        this.setTabResults();
    }
    
    public void setTabResults() {
        tabResult.removeAll();
        if (this.scannerFromSetting != null) {
            this.scanner = this.scannerFromSetting;
            this.scannerFromSetting = null;
        }
        for (int i = 0; i < this.scanner.length; i++) {
            if (this.scanner[i][7].toString().equals("true")) {
                tabResult.addTab(this.scanner[i][1].toString(), this.createNewPanelResult(i));
            }
        }
    }
    
    public void tabNotify(int indexScanner) {
        tabResult.setForegroundAt(indexScanner, Color.RED);
    }
    
    private JPanel createNewPanelResult(int indexScanner) {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("QR Code Scan");
        JSeparator separator = new JSeparator();
        JSeparator separatorBottom = new JSeparator();
        JTextArea result = new JTextArea("");
        JScrollPane scrollPane = new JScrollPane(result);
        JLabel statusProcess = new JLabel("Status Proses");
        JPanel buttonGroup = new JPanel();
        JButton doneProcess = new JButton("OK");
        JButton cancelProcess = new JButton("Cancel");
        
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        
        buttonGroup.setLayout(new FlowLayout());
        
        panel.add(title);
        panel.add(separator);
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
        layout.putConstraint(SpringLayout.NORTH, result, 0, SpringLayout.NORTH, scrollPane);
        layout.putConstraint(SpringLayout.EAST, result, 0, SpringLayout.EAST, scrollPane);
        layout.putConstraint(SpringLayout.WEST, result, 0, SpringLayout.WEST, scrollPane);
        layout.putConstraint(SpringLayout.SOUTH, result, 0, SpringLayout.SOUTH, scrollPane);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, separator);
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
        scrollPane.setBorder(new EmptyBorder(10, 0, 0, 0));
        scrollPane.setBackground(Color.white);
        
        result.setEditable(false);
        result.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        
        statusProcess.setHorizontalAlignment(SwingConstants.CENTER);
        
        doneProcess.setEnabled(false);
        cancelProcess.setEnabled(false);
        
//        Create result thread
        if (this.scanner[indexScanner][2].toString().equals("IP Based")) {
            this.threadScannerIpBased = new ThreadScannerIpBased(this, 
                title, result, statusProcess, doneProcess, cancelProcess, indexScanner);
            this.threadScannerIpBased.start();
        } else {
            this.threadScanner = new ThreadScanner(this, title, result, indexScanner);
            this.threadScanner.start();
        }
        
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabResult, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
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
                .addContainerGap())
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
        new FrmListScanner(this).setVisible(true);
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSplitPane jSplitPane1;
    public javax.swing.JLabel last_sent;
    public javax.swing.JLabel main_title;
    private javax.swing.JMenuItem menuItemLogout;
    public javax.swing.JLabel refreshRateLabel;
    private javax.swing.JTabbedPane tabResult;
    public javax.swing.JLabel weightUnit;
    public javax.swing.JLabel weightValue;
    // End of variables declaration//GEN-END:variables
}
