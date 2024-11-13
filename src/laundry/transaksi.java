/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package laundry;

import laundry.login.UserSession;
import Connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS TUF
 */
public class transaksi extends javax.swing.JFrame {

    
    public transaksi() {
        initComponents();
        this.connection = (Connection) new DatabaseConnection().getConnection();
        
        int idUser = UserSession.getIdUser(); 
        txtIdUser.setText(String.valueOf(idUser)); 
        txtIdUser.setEditable(false);
        txtIdUser.setFocusable(false);
        
        setRole();
        
        refreshComboMember();
        refreshComboLayanan();
    }
    
    
    private final Connection connection;
    
    public void setRole () {
        String role = login.UserSession.getRole();
        
        if (role.equals("admin")) {
            btnTransaksi.setEnabled(true);
            btnMember.setEnabled(true);
            btnLayanan.setEnabled(true);
            btnLaporan.setEnabled(true);
    
        } else if (role.equals("kasir")) {
            btnTransaksi.setEnabled(true);
            btnMember.setEnabled(true);
            btnLayanan.setEnabled(false);
            btnLaporan.setEnabled(false);
        }
    }
    
    public void refreshComboMember(){
        try {
            
            txtMember.addItem("- Pilih Member -"); 
            
            String sql = "SELECT id_member, nama_member FROM member";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery(); 
            
            
            while(rs.next()){
                String idMember = rs.getString("id_member");
                String namaMember = rs.getString("nama_member");
                txtMember.addItem(idMember + " | " + namaMember);
                
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    public void refreshComboLayanan() {
        try {
            
            txtLayanan.addItem("- Pilih Layanan -");
            
            String sql = "SELECT jenis_layanan, harga, waktu_pengerjaan, satuan_waktu FROM layanan";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            

            while (rs.next()) {
                String jenisLayanan = rs.getString("jenis_layanan");
                String harga = rs.getString("harga");
                String waktuPengerjaan = rs.getString("waktu_pengerjaan"); 
                String satuanWaktu = rs.getString("satuan_waktu");

                txtLayanan.addItem(jenisLayanan + " | " + harga + " | " + waktuPengerjaan + " " + satuanWaktu); 
            }
    } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private void hitungTotal(){
        try {
                
            int berat = Integer.parseInt(txtBerat.getText());

            String selectedLayanan = (String) txtLayanan.getSelectedItem();
                if (selectedLayanan != null && !selectedLayanan.equals("Pilih")) {
                    String[] layananParts = selectedLayanan.split(" \\| ");
                    
                    if (layananParts.length > 1) {
                        int harga = Integer.parseInt(layananParts[1].trim());
                        int total = berat * harga;
                        txtTotal.setText("Rp." + String.valueOf(total));
                    } else {
                        JOptionPane.showMessageDialog(this, "Layanan masih kosong", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        }catch(NumberFormatException  e){
            JOptionPane.showMessageDialog(this, "Masukkan berat yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void hitungKembalian(){   
        try {
             
            String totalText = txtTotal.getText();
            String tunaiText = txtTunai.getText();

            if (totalText.isEmpty() || tunaiText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Total dan tunai harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
                return;  
            }
            
            totalText = totalText.replace("Rp.", "").trim();
            tunaiText = tunaiText.replace("Rp.", "").trim();
 
            int total = Integer.parseInt(totalText);
            int tunai = Integer.parseInt(tunaiText);

            if (tunai >= total) {
               int kembalian = tunai - total; 
               txtKembali.setText("Rp." + String.valueOf(kembalian));
            } 

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan nilai yang valid untuk total dan tunai.", "Error", JOptionPane.ERROR_MESSAGE);
    
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

        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnTransaksi = new javax.swing.JButton();
        btnMember = new javax.swing.JButton();
        btnLayanan = new javax.swing.JButton();
        btnLaporan = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        btnKembali = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtIdTransaksi = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtBerat = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtKembali = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTunai = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        txtIdUser = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTanggal = new com.toedter.calendar.JDateChooser();
        txtMember = new javax.swing.JComboBox<>();
        txtLayanan = new javax.swing.JComboBox<>();

        jButton5.setBackground(new java.awt.Color(55, 67, 117));
        jButton5.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Kembali");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(55, 67, 117));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setPreferredSize(new java.awt.Dimension(610, 55));

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Transaksi");

        btnDashboard.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Home_11.png"))); // NOI18N
        btnDashboard.setText("Dashboard");
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });

        btnTransaksi.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Wallet.png"))); // NOI18N
        btnTransaksi.setText("Transaksi");
        btnTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransaksiActionPerformed(evt);
            }
        });

        btnMember.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/People_1.png"))); // NOI18N
        btnMember.setText("Member");
        btnMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMemberActionPerformed(evt);
            }
        });

        btnLayanan.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnLayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Full Shopping Basket_1.png"))); // NOI18N
        btnLayanan.setText("Layanan");
        btnLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLayananActionPerformed(evt);
            }
        });

        btnLaporan.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnLaporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Graph Report.png"))); // NOI18N
        btnLaporan.setText("Laporan");
        btnLaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnTransaksi)
                .addGap(18, 18, 18)
                .addComponent(btnMember)
                .addGap(18, 18, 18)
                .addComponent(btnLayanan)
                .addGap(18, 18, 18)
                .addComponent(btnLaporan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDashboard)
                    .addComponent(btnTransaksi)
                    .addComponent(btnMember)
                    .addComponent(btnLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(16, 16, 16))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 943, 53));

        jTabbedPane1.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N

        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Transaksi", "ID User", "Member", "Tanggal Transaksi", "Status Transaksi", "Total", "Detail"
            }
        ));
        jScrollPane1.setViewportView(tblData);

        btnKembali.setBackground(new java.awt.Color(55, 67, 117));
        btnKembali.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Transaksi", jPanel3);

        jLabel7.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel7.setText("ID Transaksi");

        txtIdTransaksi.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtIdTransaksi.setEnabled(false);
        txtIdTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdTransaksiActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel9.setText("Tanggal");

        jLabel8.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel8.setText("Member");

        jLabel10.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel10.setText("Layanan");

        jLabel11.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel11.setText("Diskon");

        txtDiskon.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiskonActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel12.setText("Berat");

        txtBerat.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtBerat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBeratActionPerformed(evt);
            }
        });
        txtBerat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBeratKeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        jLabel13.setText("Total");

        jLabel14.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel14.setText("Kembali");

        txtKembali.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKembaliActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel15.setText("Tunai");

        txtTunai.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtTunai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTunaiActionPerformed(evt);
            }
        });
        txtTunai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTunaiKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTunaiKeyReleased(evt);
            }
        });

        btnSimpan.setBackground(new java.awt.Color(55, 67, 117));
        btnSimpan.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        txtTotal.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });

        txtIdUser.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtIdUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdUserActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel16.setText("ID User");

        txtMember.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N

        txtLayanan.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(txtMember, 0, 263, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(53, 53, 53)))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTotal)
                                    .addComponent(txtDiskon, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(65, 65, 65)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtLayanan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel15)
                                            .addGap(67, 67, 67)))
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel14))))
                .addGap(62, 62, 62))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMember))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Tambah ", jPanel4);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 65, -1, 500));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdTransaksiActionPerformed

    private void txtDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiskonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiskonActionPerformed

    private void txtBeratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeratActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBeratActionPerformed

    private void txtKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKembaliActionPerformed

    private void txtTunaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTunaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTunaiActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
        hitungTotal();
    }//GEN-LAST:event_txtTotalActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void txtIdUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdUserActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // TODO add your handling code here:
        dashboard dashboard = new dashboard();  
        dashboard.setVisible(true);
        
        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransaksiActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_btnTransaksiActionPerformed

    private void btnMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMemberActionPerformed
        // TODO add your handling code here:
        member member = new member();
        member.setVisible(true);
        this.dispose();
                
    }//GEN-LAST:event_btnMemberActionPerformed

    private void btnLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLayananActionPerformed
        // TODO add your handling code here:
        layanan layanan = new layanan();
        layanan.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLayananActionPerformed

    private void btnLaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaporanActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnLaporanActionPerformed

    private void txtBeratKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBeratKeyReleased
        // TODO add your handling code here:
        hitungTotal();
    }//GEN-LAST:event_txtBeratKeyReleased

    private void txtTunaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTunaiKeyReleased

        String input = txtTunai.getText().replace("Rp.", "").trim();  
            try {
                int value = Integer.parseInt(input); 
                txtTunai.setText("Rp. " + value);  
            } catch (NumberFormatException e) {
                
            }
        
    
        hitungKembalian();
    }//GEN-LAST:event_txtTunaiKeyReleased

    private void txtTunaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTunaiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTunaiKeyPressed

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
            java.util.logging.Logger.getLogger(transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new transaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnLayanan;
    private javax.swing.JButton btnMember;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTransaksi;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtBerat;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtIdTransaksi;
    private javax.swing.JTextField txtIdUser;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JComboBox<String> txtLayanan;
    private javax.swing.JComboBox<String> txtMember;
    private com.toedter.calendar.JDateChooser txtTanggal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables

    
}
