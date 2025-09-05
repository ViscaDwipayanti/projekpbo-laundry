SELECT 
    t.id_transaksi,
    t.tanggal,
    t.berat,
    t.diskon,
    t.total,
    m.nama_member,
    l.jenis_layanan,
    t.user_id_user
FROM 
    transaksi t
JOIN 
    member m ON t.member_id_member = m.id_member
JOIN 
    layanan l ON t.layanan_id_layanan = l.id_layanan
WHERE 
MONTH(t.tanggal) = $P{bulan} 
AND YEAR(t.tanggal) = $P{tahun}
ORDER BY 
    t.id_transaksi ASC;