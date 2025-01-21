/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package laundry;

import Connection.DatabaseConnection;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import laundry.login.UserSession;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;






/**
 *
 * @author ASUS TUF
 */
public class dashboard extends javax.swing.JFrame {

    private final Connection connection;
    private javax.swing.JPanel cPanel;
    public dashboard() {
        this.connection = (Connection) new DatabaseConnection().getConnection();
        initComponents();
        initPieChart();
        initLineChart();
        loadDataTransaksiBaru(connection);
        loadDataMember();
        loadDataPendapatanHarian(); 
        
         txtBaru.setEditable(false);
         txtJmlMember.setEditable(false);
         txtPendapatan.setEditable(false);
         
         txtBaru.setFocusable(false);
         txtJmlMember.setFocusable(false);
         txtPendapatan.setFocusable(false);
         
        
        
        
        int idUser = UserSession.getIdUser(); // Mengambil id_user dari UserSession
//        txtIdUser.setText(String.valueOf(idUser)); // Menampilkan id_user di txtIdUser
//        txtIdUser.setEditable(false);
        txtUsername.setFocusable(false);
        
        String username = UserSession.getUsername();
//        txtRole.setText(String.valueOf(username));
        txtUsername.setText(String.valueOf(username));
        
        setRole();
        
    }
    
    
    
    private void initLineChart(){
        TimeSeriesCollection dataset = createDataset();  // Ambil dataset untuk grafik garis
        JFreeChart lineChart = createLineChart(dataset);  // Buat grafik garis
        ChartPanel chartPanel = new ChartPanel(lineChart); // Membuat panel untuk grafik
        chartPanel.setPreferredSize(new Dimension(400, 300));  // Ukuran grafik
        panelGrafik1.setLayout(new BorderLayout());   // Layout untuk panel
        panelGrafik1.add(chartPanel, BorderLayout.CENTER);  // Menambahkan grafik ke panel
        panelGrafik1.validate();  // Memastikan tampilan panel diperbarui
        
    }
    
    private TimeSeriesCollection createDataset() {
    TimeSeriesCollection dataset = new TimeSeriesCollection();
    TimeSeries series = new TimeSeries("Pendapatan");

    try {
        // Query SQL untuk agregasi harian
        String query = "SELECT DATE(tanggal) AS tanggal, SUM(total) AS pendapatan " +
                       "FROM transaksi " +
                       "GROUP BY DATE(tanggal) " +
                       "ORDER BY tanggal";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Date tanggal = rs.getDate("tanggal"); // Mengambil hanya tanggal
            double pendapatan = rs.getDouble("pendapatan"); // Total pendapatan per hari

            // Tambahkan data ke TimeSeries menggunakan addOrUpdate
            series.addOrUpdate(new org.jfree.data.time.Day(tanggal), pendapatan);
        }

        dataset.addSeries(series); // Tambahkan TimeSeries ke dataset
    } catch (Exception e) {
        e.printStackTrace();
    }
 
    return dataset;
}

     private JFreeChart createLineChart(TimeSeriesCollection dataset) {
        return ChartFactory.createTimeSeriesChart(
            "Grafik Pendapatan",  // Judul grafik
            "Tanggal",            // Label sumbu X 
            "Pendapatan (Rp)",    // Label sumbu Y
            dataset,              // Dataset untuk grafik
            false,                // Tidak menampilkan legenda
            true,                 // Menampilkan tooltips
            false                 // Tidak menampilkan URL
        );
    }
    
    

    
    private void initPieChart() {
        DefaultPieDataset dataset = createPieDataset(); // Menggunakan dataset untuk Pie Chart
        JFreeChart chart = createPieChart(dataset);     // Membuat Pie Chart dari dataset
        ChartPanel chartPanel = new ChartPanel(chart);  // Panel untuk menampilkan chart
        chartPanel.setPreferredSize(new Dimension(400, 300)); // Ukuran grafik
        panelGrafik.setLayout(new BorderLayout());
        panelGrafik.add(chartPanel, BorderLayout.CENTER);
        panelGrafik.validate();
}

    private DefaultPieDataset createPieDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
    
        try {
            String query = "SELECT l.jenis_layanan, COUNT(t.id_transaksi) AS jumlah_transaksi " +
                       "FROM transaksi t " +
                       "JOIN layanan l ON t.layanan_id_layanan = l.id_layanan " +
                       "GROUP BY l.jenis_layanan " +
                       "ORDER BY jumlah_transaksi DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Menambahkan data ke dataset
            while (rs.next()) {
                String jenisLayanan = rs.getString("jenis_layanan");
                int jumlahTransaksi = rs.getInt("jumlah_transaksi");
                dataset.setValue(jenisLayanan, jumlahTransaksi); // Menambahkan kategori dan nilai
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private JFreeChart createPieChart(DefaultPieDataset dataset) {
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Layanan Terlaris",  // Judul chart
            dataset,                        // Dataset untuk chart
            true,                           // Menampilkan legenda
            true,                           // Menampilkan tooltip
            false                           // Tidak menggunakan URL
        );

        pieChart.setTitle(new TextTitle("Layanan Terlaris", new Font("SanSerif", Font.BOLD, 20)));
        return pieChart;
       

    }
    
    
    public void setRole () {
        String role = UserSession.getRole();
        
        
        if (role.equals("admin")) {
            txtTransaksi.setEnabled(true);
            txtMember.setEnabled(true);
            txtLayanan.setEnabled(true);
            txtLaporan.setEnabled(true);
    
        } else if (role.equals("kasir")) {
            txtTransaksi.setEnabled(true);
            txtMember.setEnabled(true);
            txtLayanan.setEnabled(false);
            txtLaporan.setEnabled(false);
        }
    }
    
    
    private void loadDataTransaksiBaru(Connection connection) {
    try {
        // Ambil waktu sekarang dalam zona waktu UTC
        Date nowUTC = new Date(); // Waktu sistem lokal
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfUTC.setTimeZone(utcTimeZone);
        String waktuUTC = sdfUTC.format(nowUTC);

        // Mengonversi UTC ke zona waktu Asia/Jakarta
        TimeZone jakartaTimeZone = TimeZone.getTimeZone("Asia/Jakarta");
        SimpleDateFormat sdfJakarta = new SimpleDateFormat("yyyy-MM-dd");
        sdfJakarta.setTimeZone(jakartaTimeZone); // Menggunakan format hanya tanggal
        Date jakartaDate = new Date(sdfUTC.parse(waktuUTC).getTime());
        String waktuJakarta = sdfJakarta.format(jakartaDate);

        System.out.println("Waktu Jakarta: " + waktuJakarta); // Output waktu Jakarta

        // Query menggunakan waktu Jakarta
        String sql = "SELECT COUNT(*) AS jumlah " +
                     "FROM transaksi " +
                     "JOIN detail_transaksi ON transaksi.id_transaksi = detail_transaksi.transaksi_id_transaksi " +
                     "WHERE detail_transaksi.status_transaksi = 'Baru' " +
                     "AND DATE(transaksi.tanggal) = ?";
                         
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, waktuJakarta); // Menggunakan langsung waktuJakarta

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int jumlahTransaksiBaru = rs.getInt("jumlah");
            System.out.println("Jumlah Transaksi Baru: " + jumlahTransaksiBaru);
            txtBaru.setText(String.valueOf(jumlahTransaksiBaru)); // Set jumlah ke textfield
        }

        rs.close();
        st.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

    
    private void loadDataMember() {
        try {
        // Koneksi ke database
        
            String sql = "SELECT COUNT(*) AS jumlah " +
                         "FROM member ";
                       
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
        
            if (rs.next()) {
                int jumlahMember = rs.getInt("jumlah");
                System.out.println("Jumlah Member Saat Ini: " + jumlahMember);

                txtJmlMember.setText(String.valueOf(jumlahMember)); // Set jumlah ke textfield
            }

            rs.close();
            st.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage());
        }
    }
    
    public void loadDataPendapatanHarian() {
    try {
        // Ambil waktu sekarang dalam zona waktu UTC
        Date nowUTC = new Date(); // Waktu sistem lokal
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfUTC.setTimeZone(utcTimeZone);
        String waktuUTC = sdfUTC.format(nowUTC);

        // Mengonversi UTC ke zona waktu Asia/Jakarta
        TimeZone jakartaTimeZone = TimeZone.getTimeZone("Asia/Jakarta");
        SimpleDateFormat sdfJakarta = new SimpleDateFormat("yyyy-MM-dd");
        sdfJakarta.setTimeZone(jakartaTimeZone); // Menggunakan format hanya tanggal
        Date jakartaDate = new Date(sdfUTC.parse(waktuUTC).getTime());
        String waktuJakarta = sdfJakarta.format(jakartaDate);

        System.out.println("Waktu Jakarta: " + waktuJakarta); // Output waktu Jakarta

        // Query menggunakan waktu Jakarta
        String sql = "SELECT SUM(total) AS pendapatan " +
                     "FROM transaksi " +
                     "WHERE DATE(transaksi.tanggal) = ?";
                         
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, waktuJakarta); // Menggunakan tanggal Jakarta

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int dataPendapatanHarian = rs.getInt("pendapatan");
            txtPendapatan.setText("Rp. " + String.valueOf(dataPendapatanHarian)); // Set jumlah ke textfield
        }

        rs.close();
        st.close();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage());
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

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnUser = new javax.swing.JLabel();
        txtUsername = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtLayanan = new javax.swing.JButton();
        txtLaporan = new javax.swing.JButton();
        txtTransaksi = new javax.swing.JButton();
        txtMember = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtLogout = new javax.swing.JButton();
        panelGrafik = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBaru = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtJmlMember = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPendapatan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        panelGrafik1 = new javax.swing.JPanel();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(55, 67, 117));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Male User_9.png"))); // NOI18N
        btnUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUserMouseClicked(evt);
            }
        });

        txtUsername.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("EZWash");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUser)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtLayanan.setBackground(new java.awt.Color(55, 67, 117));
        txtLayanan.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtLayanan.setForeground(new java.awt.Color(255, 255, 255));
        txtLayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Full Shopping Basket_1.png"))); // NOI18N
        txtLayanan.setText("Layanan");
        txtLayanan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtLayanan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLayananActionPerformed(evt);
            }
        });

        txtLaporan.setBackground(new java.awt.Color(55, 67, 117));
        txtLaporan.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtLaporan.setForeground(new java.awt.Color(255, 255, 255));
        txtLaporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Graph Report.png"))); // NOI18N
        txtLaporan.setText("Laporan");
        txtLaporan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtLaporan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtLaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLaporanActionPerformed(evt);
            }
        });

        txtTransaksi.setBackground(new java.awt.Color(55, 67, 117));
        txtTransaksi.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        txtTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Wallet.png"))); // NOI18N
        txtTransaksi.setText("Transaksi");
        txtTransaksi.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtTransaksi.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransaksiActionPerformed(evt);
            }
        });

        txtMember.setBackground(new java.awt.Color(55, 67, 117));
        txtMember.setFont(new java.awt.Font("Swis721 BT", 1, 18)); // NOI18N
        txtMember.setForeground(new java.awt.Color(255, 255, 255));
        txtMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/People_1.png"))); // NOI18N
        txtMember.setText("Member");
        txtMember.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtMember.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMemberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtMember, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(txtLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(txtTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(txtMember, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(txtLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(txtLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(238, Short.MAX_VALUE)))
        );

        txtLogout.setBackground(new java.awt.Color(55, 67, 117));
        txtLogout.setFont(new java.awt.Font("Swis721 BT", 1, 14)); // NOI18N
        txtLogout.setForeground(new java.awt.Color(255, 255, 255));
        txtLogout.setText("Logout");
        txtLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(771, Short.MAX_VALUE)
                .addComponent(txtLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelGrafik.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelGrafikLayout = new javax.swing.GroupLayout(panelGrafik);
        panelGrafik.setLayout(panelGrafikLayout);
        panelGrafikLayout.setHorizontalGroup(
            panelGrafikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 335, Short.MAX_VALUE)
        );
        panelGrafikLayout.setVerticalGroup(
            panelGrafikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Transaksi Baru (Harian)");

        txtBaru.setBackground(new java.awt.Color(212, 220, 220));
        txtBaru.setFont(new java.awt.Font("Swis721 BT", 1, 36)); // NOI18N
        txtBaru.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtBaru.setBorder(null);
        txtBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBaruActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(txtBaru))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBaru)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Swis721 BT", 1, 20)); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Jumlah Member");

        txtJmlMember.setBackground(new java.awt.Color(212, 220, 220));
        txtJmlMember.setFont(new java.awt.Font("Swis721 BT", 1, 36)); // NOI18N
        txtJmlMember.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtJmlMember.setBorder(null);
        txtJmlMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJmlMemberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(txtJmlMember))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtJmlMember)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Swis721 BT", 1, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Pendapatan Harian");

        txtPendapatan.setBackground(new java.awt.Color(212, 220, 220));
        txtPendapatan.setFont(new java.awt.Font("Swis721 BT", 1, 30)); // NOI18N
        txtPendapatan.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPendapatan.setBorder(null);
        txtPendapatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPendapatanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(txtPendapatan))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPendapatan, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel7.setFont(new java.awt.Font("Swis721 BT", 1, 24)); // NOI18N
        jLabel7.setText("Dashboard");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        panelGrafik1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelGrafik1Layout = new javax.swing.GroupLayout(panelGrafik1);
        panelGrafik1.setLayout(panelGrafik1Layout);
        panelGrafik1Layout.setHorizontalGroup(
            panelGrafik1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );
        panelGrafik1Layout.setVerticalGroup(
            panelGrafik1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelGrafik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(panelGrafik1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelGrafik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelGrafik1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLogoutActionPerformed
        // TODO add your handling code here:
//        login l = new login();
//        l.setVisible(true);
//        this.setVisible(false);
        
        this.dispose(); 
        
    }//GEN-LAST:event_txtLogoutActionPerformed

    private void txtTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransaksiActionPerformed
        // TODO add your handling code here:
        transaksi transaksi = new transaksi();  
                transaksi.setVisible(true);
                
        this.dispose(); 
    }//GEN-LAST:event_txtTransaksiActionPerformed

    private void txtMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMemberActionPerformed
        // TODO add your handling code here:
        member member = new member();
                member.setVisible(true);
        
        this.dispose(); 
        
    }//GEN-LAST:event_txtMemberActionPerformed

    private void txtLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLayananActionPerformed
        // TODO add your handling code here:
        layanan layanan = new layanan();
            layanan.setVisible(true);
         
        this.dispose(); 
    }//GEN-LAST:event_txtLayananActionPerformed

    private void txtLaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLaporanActionPerformed
        // TODO add your handling code here:
        laporan laporan = new laporan();
        laporan.setVisible(true);
        this.dispose();
        
    }//GEN-LAST:event_txtLaporanActionPerformed

    private void btnUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUserMouseClicked
        // TODO add your handling code here:
        user userDetail = new user();
        userDetail.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUserMouseClicked

    private void txtBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBaruActionPerformed
        // TODO add your handling code here:
        loadDataTransaksiBaru(connection);
    }//GEN-LAST:event_txtBaruActionPerformed

    private void txtJmlMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJmlMemberActionPerformed
        // TODO add your handling code here:
        loadDataMember();
    }//GEN-LAST:event_txtJmlMemberActionPerformed

    private void txtPendapatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPendapatanActionPerformed
        // TODO add your handling code here:
        loadDataPendapatanHarian();
    }//GEN-LAST:event_txtPendapatanActionPerformed

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
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel panelGrafik;
    private javax.swing.JPanel panelGrafik1;
    private javax.swing.JTextField txtBaru;
    private javax.swing.JTextField txtJmlMember;
    private javax.swing.JButton txtLaporan;
    private javax.swing.JButton txtLayanan;
    private javax.swing.JButton txtLogout;
    private javax.swing.JButton txtMember;
    private javax.swing.JTextField txtPendapatan;
    private javax.swing.JButton txtTransaksi;
    private javax.swing.JLabel txtUsername;
    // End of variables declaration//GEN-END:variables

    
}
