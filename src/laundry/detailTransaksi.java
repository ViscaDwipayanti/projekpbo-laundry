/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package laundry;

import Connection.DatabaseConnection;
import static Connection.DatabaseConnection.getConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ASUS TUF
 */
public class detailTransaksi extends javax.swing.JFrame {

    private int idTransaksi;
    private transaksi transaksiForm;
    private static Connection connection ;
    
    
    public detailTransaksi() {
        initComponents();
        this.connection = (Connection) new DatabaseConnection().getConnection();
        
        txtNama.setEditable(false);
        txtNoTelepon.setEditable(false);
        txtAlamat.setEditable(false);
        
        txtNama.setFocusable(false);
        txtNoTelepon.setFocusable(false);
        txtAlamat.setFocusable(false);  
        
        txtIdTransaksi.setEditable(false);
        txtIdTransaksi.setFocusable(false);
              
        
    }
    
    public void setTransaksiForm(transaksi transaksiForm, int idTransaksi) {
        this.transaksiForm = transaksiForm;
        this.idTransaksi = idTransaksi;
    }


    
public void showDetailTransaksi(int idTransaksi) {
    try {
        String sql = "SELECT transaksi.id_transaksi, member.nama_member, member.no_telepon, member.alamat " +
                     "FROM detail_transaksi " +
                     "INNER JOIN member ON detail_transaksi.transaksi_member_id_member = member.id_member " +
                     "INNER JOIN transaksi ON detail_transaksi.transaksi_id_transaksi = transaksi.id_transaksi " +
                     "WHERE detail_transaksi.transaksi_id_transaksi = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, idTransaksi);  // Menggunakan idTransaksi sebagai parameter

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            // Print values for debugging
            System.out.println("id_transaksi: " + rs.getString("id_transaksi"));
            System.out.println("nama_member: " + rs.getString("nama_member"));
            System.out.println("no_telepon: " + rs.getString("no_telepon"));
            System.out.println("alamat: " + rs.getString("alamat"));
            
            txtIdTransaksi.setText(rs.getString("id_transaksi"));
            txtNama.setText(rs.getString("nama_member"));
            txtNoTelepon.setText(rs.getString("no_telepon"));
            txtAlamat.setText(rs.getString("alamat"));
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}


    
public void showTotal(int idTransaksi) {
    try {
        String sql = "SELECT transaksi.total " +
                     "FROM transaksi " + // Tambahkan spasi
                     "INNER JOIN member ON transaksi.member_id_member = member.id_member " +
                     "WHERE transaksi.id_transaksi = ?";
        
        // Debugging query
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, idTransaksi);
        
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            // Ambil nilai total
            int total = rs.getInt("total");
            txtTotal.setText(String.format("Rp.%d", total)); 
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error Database: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error Umum: " + e.getMessage());
    }
}


    
    public void showDetailTabelTransaksi(int idTransaksi) {
        try {
            String sql = "SELECT transaksi.tanggal, layanan.waktu_pengerjaan, layanan.satuan_waktu, layanan.jenis_layanan, transaksi.berat, layanan.harga, transaksi.total " +
                     "FROM detail_transaksi " +
                     "INNER JOIN transaksi ON detail_transaksi.transaksi_id_transaksi = transaksi.id_transaksi " +
                     "INNER JOIN layanan ON layanan.id_layanan = transaksi.layanan_id_layanan " +
                     "WHERE detail_transaksi.transaksi_id_transaksi = ?";

            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, idTransaksi);

            ResultSet rs = st.executeQuery();

            // Menyiapkan tabel untuk menampilkan hasil
            DefaultTableModel model = (DefaultTableModel) tblData.getModel();
            model.setRowCount(0);

            // Menambahkan data ke dalam tabel
            while (rs.next()) {
                Timestamp tanggalTransaksi = rs.getTimestamp("tanggal");
                int waktuPengerjaan = rs.getInt("waktu_pengerjaan");
                String satuanWaktu = rs.getString("satuan_waktu");
                String jenisLayanan = rs.getString("jenis_layanan");
                double berat = rs.getDouble("berat");
                int harga = rs.getInt("harga");

                // Menghitung tanggal_ambil berdasarkan tanggal_transaksi, waktu_pengerjaan, dan satuan_waktu
                Calendar calendar = Calendar.getInstance();
                Timestamp tanggalAmbil = null;

                if (tanggalTransaksi != null) {
                    calendar.setTimeInMillis(tanggalTransaksi.getTime()); // Set tanggal transaksi

                    if (satuanWaktu.equalsIgnoreCase("jam")) {
                        calendar.add(Calendar.HOUR, waktuPengerjaan); // Menambah jam
                    } else if (satuanWaktu.equalsIgnoreCase("hari")) {
                        calendar.add(Calendar.DATE, waktuPengerjaan); // Menambah hari
                    }

                    tanggalAmbil = new Timestamp(calendar.getTimeInMillis()); // Hitung tanggal ambil
                    String formattedTanggal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tanggalAmbil);

                    // Simpan ke dalam tabel database
                    saveTanggalAmbil(idTransaksi, tanggalAmbil);

                    // Tambahkan data ke dalam tabel
                    model.addRow(new Object[]{formattedTanggal, jenisLayanan, berat, harga});
                } else {
                    System.out.println("Tanggal transaksi null, tidak dapat diproses.");
                }
            }

            // Jika tidak ada data, tampilkan pesan
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Metode tambahan untuk menyimpan tanggal_ambil ke database
    private void saveTanggalAmbil(int idTransaksi, Timestamp tanggalAmbil) {
        try {
            String updateSql = "UPDATE detail_transaksi SET tanggal_ambil = ? WHERE id_detail_transaksi = ?";
            PreparedStatement pst = connection.prepareStatement(updateSql);
            pst.setTimestamp(1, tanggalAmbil);
            pst.setInt(2, idTransaksi);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Tanggal ambil berhasil disimpan untuk transaksi ID: " + idTransaksi);
            } else {
                System.out.println("Gagal menyimpan tanggal ambil untuk transaksi ID: " + idTransaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error menyimpan tanggal ambil: " + e.getMessage());
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

        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNoTelepon = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtStatusTransaksi = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtIdTransaksi = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        txtTotal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel4.setBackground(new java.awt.Color(55, 67, 117));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setPreferredSize(new java.awt.Dimension(610, 55));

        jLabel8.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Detail Transaksi");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel8)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel1.setText("Nama");

        txtNama.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel2.setText("No Telepon");

        txtNoTelepon.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel3.setText("Status Transaksi");

        txtAlamat.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N

        txtStatusTransaksi.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        txtStatusTransaksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baru", "Diproses", "Selesai", "Diambil" }));

        jLabel4.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel4.setText("Alamat");

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        jLabel6.setText("ID Transaksi");

        txtIdTransaksi.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStatusTransaksi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNoTelepon)
                    .addComponent(txtNama)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdTransaksi))
                .addGap(34, 34, 34))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNoTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatusTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(15, 15, 15))
        );

        tblData.setFont(new java.awt.Font("Swis721 BT", 0, 15)); // NOI18N
        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Tanggal Ambil", "Layanan", "Berat", "Harga / Kg"
            }
        ));
        tblData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDataMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblData);

        txtTotal.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel5.setText("Total");

        btnSimpan.setBackground(new java.awt.Color(55, 67, 117));
        btnSimpan.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnCetak.setBackground(new java.awt.Color(55, 67, 117));
        btnCetak.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnCetak.setText("Cetak Invoice");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        btnKeluar.setBackground(new java.awt.Color(55, 67, 117));
        btnKeluar.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        btnKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 972, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(513, 513, 513)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1014, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCetak)
                .addGap(43, 43, 43))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(26, 26, 26))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tblDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDataMouseClicked
       
    }//GEN-LAST:event_tblDataMouseClicked

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:       
        try {
            // Ambil nilai status baru
            String statusBaru = txtStatusTransaksi.getSelectedItem().toString();

            // Validasi input
            if (idTransaksi <= 0 || statusBaru == null || statusBaru.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak valid! Pilih transaksi dan status yang benar.");
                return;
            }

            // Query update status
            String sql = "UPDATE detail_transaksi SET status_transaksi = ? WHERE transaksi_id_transaksi = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, statusBaru);
            pst.setInt(2, idTransaksi);

            int updatedRows = pst.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Status transaksi berhasil diupdate.");
                
                // Perbarui tabel di form transaksi
                if (transaksiForm != null) {
                    transaksiForm.refreshTableData();
                }
                
                transaksi transaksi = new transaksi();
                transaksi.setVisible(true);
                this.dispose(); // Tutup form
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status transaksi.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
                
           
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
       
        try {
        String reportPath = "src/report/invoice.jasper";
        Connection connection = getConnection();

        String id_transaksi = txtIdTransaksi.getText();
    
        // Menyiapkan parameter
        HashMap<String, Object> parameters = new HashMap<>();

        // Jika id_transaksi harus berupa angka di laporan Jasper, konversikan ke Integer
        try {
            Integer idTransaksiInt = Integer.parseInt(id_transaksi);
            parameters.put("id_transaksi", idTransaksiInt);
    } catch (NumberFormatException e) {
        // Menampilkan pesan error jika id_transaksi bukan angka
        JOptionPane.showMessageDialog(this, "Error: id_transaksi harus berupa angka", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Keluar dari blok try-catch jika terjadi kesalahan format angka
    }
    
    System.out.println("id Transaksi: " + id_transaksi);

    // Mengisi laporan dengan data
    JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, connection);

    // Menampilkan laporan
    JasperViewer viewer = new JasperViewer(print, false);
    viewer.setVisible(true);      
} catch (Exception e) {
    // Menampilkan pesan error jika terjadi exception
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

        
        
        
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        // TODO add your handling code here:
        transaksi transaksi = new transaksi ();
        transaksi.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

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
            java.util.logging.Logger.getLogger(detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new detailTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtIdTransaksi;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoTelepon;
    private javax.swing.JComboBox<String> txtStatusTransaksi;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
