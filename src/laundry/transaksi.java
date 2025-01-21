/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package laundry;

import laundry.login.UserSession;
import Connection.DatabaseConnection;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Timestamp;

/**
 *
 * @author ASUS TUF
 */
public final class transaksi extends javax.swing.JFrame {

    
    public transaksi() {
        initComponents();
        this.connection = (Connection) new DatabaseConnection().getConnection();
        
        int idUser = UserSession.getIdUser(); 
        txtIdUser.setText(String.valueOf(idUser)); 
        txtIdUser.setEditable(false);
        txtIdUser.setFocusable(false);
        
        txtCari.setText("Masukkan id member...");
        txtCari.setForeground(Color.GRAY);
        
        setRole();
        
        refreshComboMember();
        refreshComboLayanan();
        
        
        
        getData();
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
    
    
    
      private void resetForm() {
        txtBerat.setText("");
        txtTotal.setText("");
        txtMember.setSelectedIndex(0);
        txtLayanan.setSelectedIndex(0);
        txtTunai.setText("");
        txtKembali.setText("");
  
    }
    
    private void getData() {
        DefaultTableModel model = (DefaultTableModel) tblData.getModel(); //mengambil data dari database dan menampilkan di tabel tblData
        model.setRowCount(0);
    
        try {
            String sql = "SELECT transaksi.id_transaksi, transaksi.user_id_user, transaksi.member_id_member, transaksi.tanggal, transaksi.total, detail_transaksi.status_transaksi FROM transaksi INNER JOIN detail_transaksi ON transaksi.id_transaksi = detail_transaksi.transaksi_id_transaksi";

            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
        
            while (rs.next()) {
                int id_transaksi = rs.getInt("id_transaksi");
                int id_user = rs.getInt("user_id_user");
                int id_member = rs.getInt("member_id_member");
                Timestamp tanggal = (Timestamp) rs.getTimestamp("tanggal");
                String formattedTanggal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tanggal);
                int total = rs.getInt("total"); 
                String statusTransaksi = rs.getString("status_transaksi");
    

                Object[] rowData = {id_transaksi,id_user,id_member, formattedTanggal, total, statusTransaksi }; // 
                model.addRow(rowData); //menambahkan rowData ke DefaultTabelModel untuk ditampilkan ke tabel GUI
            }
        
            rs.close();
            st.close();
        } catch (Exception e){
            Logger.getLogger(member.class.getName()).log(Level.SEVERE,null, e);
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
            
            String sql = "SELECT id_layanan, jenis_layanan, harga, waktu_pengerjaan, satuan_waktu FROM layanan";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            

            while (rs.next()) {
                String idLayanan = rs.getString("id_layanan");
                String jenisLayanan = rs.getString("jenis_layanan");
                String harga = rs.getString("harga");
                String waktuPengerjaan = rs.getString("waktu_pengerjaan"); 
                String satuanWaktu = rs.getString("satuan_waktu");

                txtLayanan.addItem(idLayanan + " | " +jenisLayanan + " | " + harga + " | " + waktuPengerjaan + " " + satuanWaktu); 
            }
    } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    
    private void hitungDiskon(int idMember){
        try {
        // Hitung total transaksi untuk mendapatkan poin
        String sql = "SELECT poin FROM member WHERE id_member=?";
        PreparedStatement stPoin = connection.prepareStatement(sql);
        stPoin.setInt(1, idMember);
        ResultSet rsPoin = stPoin.executeQuery();

        int poin = 0;
        if (rsPoin.next()) {
            poin = rsPoin.getInt("poin");
        }

        // Hitung diskon
        double diskon = 0;
        if (poin >= 3) {
            diskon = 0.30; // Diskon 30%
            System.out.println("Diskon 30% diterapkan!");
            
            // Setelah diskon diterapkan, set poin menjadi 0
            String updatePoinSql = "UPDATE member SET poin = 0 WHERE id_member=?";
            PreparedStatement stUpdatePoin = connection.prepareStatement(updatePoinSql);
            stUpdatePoin.setInt(1, idMember);
            stUpdatePoin.executeUpdate();
            System.out.println("Poin member telah direset menjadi 0.");
        } else {
            System.out.println("Tidak ada diskon karena poin kurang dari 3.");
        }

        // Tampilkan diskon di txtDiskon
        txtDiskon.setText(String.valueOf(diskon));

        // Tutup resource
        rsPoin.close();
        stPoin.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void hitungTotal(){
        try {
                
            double berat = Double.parseDouble(txtBerat.getText());

            String selectedLayanan = (String) txtLayanan.getSelectedItem();
                if (!selectedLayanan.equals("Pilih Layanan") && selectedLayanan != null) {
                    String[] layananParts = selectedLayanan.split(" \\| ");
                    
                    if (layananParts.length > 1) {
                        int harga = Integer.parseInt(layananParts[2].trim());
                        int totalHarga = (int) (berat * harga);
                        
                        double diskon = Double.parseDouble(txtDiskon.getText());
                        int hargaAkhir = (int) (totalHarga - (totalHarga * diskon));
                        txtTotal.setText("Rp. " + String.valueOf(hargaAkhir));
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
    
    
    
  
    public void refreshTableData() {
    try {
        String sql = "SELECT detail_transaksi.id_detail_transaksi, transaksi.tanggal, member.nama_member, detail_transaksi.status_transaksi " +
                     "FROM detail_transaksi " +
                     "INNER JOIN transaksi ON detail_transaksi.transaksi_id_transaksi = transaksi.id_transaksi " +
                     "INNER JOIN member ON transaksi.member_id_member = member.id_member";
        PreparedStatement st = connection.prepareStatement(sql);
        ResultSet rs = st.executeQuery();

        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id_detail_transaksi"),
                rs.getTimestamp("tanggal"),
                rs.getString("nama_member"),
                rs.getString("status_transaksi")
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel18 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
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
        jLabel17 = new javax.swing.JLabel();
        txtStatusTransaksi = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();

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

        tblData.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Transaksi", "ID User", "Member", "Tanggal Transaksi", "Total", "Status Transaksi"
            }
        ));
        tblData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblData);

        jLabel18.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        jLabel18.setText("Cari");

        txtCari.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCariFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCariFocusLost(evt);
            }
        });
        txtCari.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCariMouseClicked(evt);
            }
        });
        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCariKeyTyped(evt);
            }
        });

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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnKembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        jLabel9.setText("Tanggal Transaksi");

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
        txtMember.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                txtMemberItemStateChanged(evt);
            }
        });
        txtMember.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMemberMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtMemberMousePressed(evt);
            }
        });
        txtMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMemberActionPerformed(evt);
            }
        });

        txtLayanan.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel17.setText("Status Transaksi");

        txtStatusTransaksi.setFont(new java.awt.Font("Swis721 BT", 0, 14)); // NOI18N
        txtStatusTransaksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baru", "Diproses", "Selesai", "Diambil" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(70, 70, 70)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMember, 0, 263, Short.MAX_VALUE)
                            .addComponent(txtIdTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtDiskon, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(53, 53, 53)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(67, 67, 67)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtStatusTransaksi, 0, 281, Short.MAX_VALUE)
                            .addComponent(txtIdUser, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(txtBerat))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(txtLayanan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(37, 37, 37))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStatusTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMember, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(99, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Tambah ", jPanel4);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 65, 950, 530));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 960, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 590, 960, 20));

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
        double berat = Double.parseDouble(txtBerat.getText());  
        int id_user = Integer.parseInt(txtIdUser.getText());
        double diskon = Double.parseDouble(txtDiskon.getText());
        String statusTransaksi = txtStatusTransaksi.getSelectedItem().toString();
        
        String selectedMember = txtMember.getSelectedItem().toString();
        String[] parts = selectedMember.split(" \\| "); 
        int id_member = Integer.parseInt(parts[0].trim());
        
        
        
        String selectedLayanan = txtLayanan.getSelectedItem().toString();
        String[] parts2 = selectedLayanan.split(" \\| ");
        int idLayanan = Integer.parseInt(parts2[0].trim()); 
        String jenisLayanan = parts2[1].trim();  
        int harga = Integer.parseInt(parts2[2].trim());  
        String waktuPengerjaan = parts2[3].trim();  
        
        int total = 0;
        try {
             // Menghapus simbol mata uang "Rp." dan karakter non-angka lainnya
             String totalText = txtTotal.getText().replaceAll("[^0-9]", ""); // Menghapus semua karakter non-angka
             total = Integer.parseInt(totalText);  // Mengonversi string yang sudah bersih menjadi angka
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Total harus berupa angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
          return;  // Menghentikan eksekusi jika input tidak valid
}
        
        
        
        try {
    // Query untuk insert ke tabel transaksi
            String sqlTransaksi = "INSERT INTO transaksi (tanggal, berat, diskon, total, member_id_member, layanan_id_layanan, user_id_user) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement stTransaksi = connection.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS);
            Timestamp tanggal = new Timestamp(System.currentTimeMillis());
    
                stTransaksi.setTimestamp(1, tanggal);
                stTransaksi.setDouble(2, berat);
                stTransaksi.setDouble(3, diskon);
                stTransaksi.setInt(4, total);
                stTransaksi.setInt(5, id_member);
                stTransaksi.setInt(6, idLayanan);
                stTransaksi.setInt(7, id_user);

            int rowInserted = stTransaksi.executeUpdate();
    
            // Mendapatkan ID transaksi yang baru saja disimpan
            int id_transaksi = 0;
            ResultSet generatedKeys = stTransaksi.getGeneratedKeys();
            if (generatedKeys.next()) {
                id_transaksi = generatedKeys.getInt(1);
            }
            generatedKeys.close();
            stTransaksi.close();
    
    // Jika transaksi berhasil disimpan, insert ke detail_transaksi
            if (rowInserted > 0 && id_transaksi > 0) {
                String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id_transaksi, status_transaksi, transaksi_member_id_member, transaksi_layanan_id_layanan) VALUES (?, ?, ?, ?)";

                PreparedStatement stDetail = connection.prepareStatement(sqlDetail);
        
                stDetail.setInt(1, id_transaksi); // Set transaksi_id dari ID transaksi yang baru
                stDetail.setString(2, statusTransaksi);
                stDetail.setInt(3, id_member);
                stDetail.setInt(4, idLayanan);
                
                // Set status transaksi
        
                int detailRowInserted = stDetail.executeUpdate();
                stDetail.close();
        
            if (detailRowInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    
        resetForm();
        getData();
    
        } catch (Exception e) {
            Logger.getLogger(transaksi.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        member memberService = new member();
        memberService.tambahPoin(id_member);
        
       
        
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
        hitungTotal();
    }//GEN-LAST:event_txtTotalActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

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
        laporan laporan = new laporan();
        laporan.setVisible(true);
        this.dispose();
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
    
    private void tblDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDataMouseClicked
        // TODO add your handling code here:
        int row = tblData.getSelectedRow();
        if (row != -1) {
            try {
                // Ambil idTransaksi dan idMember
                int idTransaksi = Integer.parseInt(tblData.getValueAt(row, 0).toString());
                int idMember = Integer.parseInt(tblData.getValueAt(row, 2).toString());

                // Navigasi ke detailTransaksi
                detailTransaksi dt = new detailTransaksi();
                dt.showDetailTransaksi(idTransaksi);
                dt.showTotal(idTransaksi);
                dt.showDetailTabelTransaksi(idTransaksi);
                dt.setTransaksiForm(this, idTransaksi);
                dt.setVisible(true);

                this.dispose();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Kesalahan format data: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris untuk melihat detail transaksi.");
    }
            
    }//GEN-LAST:event_tblDataMouseClicked

    private void txtCariMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCariMouseClicked
        // TODO add your handling code here:
        txtCari.setText("");
    }//GEN-LAST:event_txtCariMouseClicked

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    private void txtCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyTyped
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtCariKeyTyped

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
        model.setRowCount(0);  // Clear tabel sebelum menambahkan data baru
    
        String cari = txtCari.getText();

         try {
                String sql = "SELECT * FROM transaksi JOIN detail_transaksi ON transaksi.id_transaksi = detail_transaksi.transaksi_id_transaksi WHERE member_id_member LIKE ?";
                PreparedStatement st = connection.prepareStatement(sql);
                    st.setString(1, "%" + cari + "%");
                ResultSet rs = st.executeQuery();
                   
                        while (rs.next()) {
                            int idTransaksi = rs.getInt("id_transaksi");
                            int idUser = rs.getInt("user_id_user");
                            int idMember = rs.getInt("member_id_member");
                            Timestamp tanggal = rs.getTimestamp("tanggal");
                            String formattedTanggal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tanggal);
                            int total = rs.getInt("total"); 
                            String statusTransaksi = rs.getString("status_transaksi");

                            Object[] rowData = {idTransaksi, idUser, idMember, formattedTanggal, total, statusTransaksi};
                            model.addRow(rowData);
                        }
                        
                rs.close();
                st.close();

            } catch (Exception e) {
                Logger.getLogger(transaksi.class.getName()).log(Level.SEVERE, null, e);
            }   
        
    }//GEN-LAST:event_txtCariKeyReleased

    private void txtMemberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMemberMouseClicked
        // TODO add your handling code here:
   
        
    }//GEN-LAST:event_txtMemberMouseClicked

    private void txtMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMemberActionPerformed
        // TODO add your handling code here:
         String selectedMember = (String) txtMember.getSelectedItem();
    
        // Periksa apakah item yang dipilih adalah "- Pilih Member -"
        if (selectedMember != null && !selectedMember.equals("- Pilih Member -")) {
            try {

                String[] memberParts = selectedMember.split(" \\| ");
                int idMember = Integer.parseInt(memberParts[0].trim());
            
                hitungDiskon(idMember);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Format ID Member tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        
        
    }//GEN-LAST:event_txtMemberActionPerformed

    private void txtMemberItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_txtMemberItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMemberItemStateChanged

    private void txtMemberMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMemberMousePressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtMemberMousePressed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        dashboard dashboard = new dashboard();
        dashboard.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void txtCariFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCariFocusGained
        // TODO add your handling code here:
        if (txtCari.getText().equals("Masukkan id member...")) {
        txtCari.setText("");
        txtCari.setForeground(Color.BLACK); // Ubah warna teks ke normal
    }
    }//GEN-LAST:event_txtCariFocusGained

    private void txtCariFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCariFocusLost
        // TODO add your handling code here:
        if (txtCari.getText().isEmpty()) {
        txtCari.setText("Masukkan id member...");
        txtCari.setForeground(Color.GRAY); // Ubah warna teks ke warna placeholder
    }
    }//GEN-LAST:event_txtCariFocusLost

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
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtBerat;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtIdTransaksi;
    private javax.swing.JTextField txtIdUser;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JComboBox<String> txtLayanan;
    private javax.swing.JComboBox<String> txtMember;
    private javax.swing.JComboBox<String> txtStatusTransaksi;
    private com.toedter.calendar.JDateChooser txtTanggal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables

    

    

    

    
}
