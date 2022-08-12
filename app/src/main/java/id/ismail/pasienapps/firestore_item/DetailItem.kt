package id.ismail.pasienapps.firestore_item

class DetailItem {
    var id_reservasi: String? = null
        private set
    var id_pasien: String? = null
        private set
    var keluhan: String? = null
        private set
    var nomor_antrian: String? = null
        private set
    var status_reservasi = 0
        private set
    var tanggal_reservasi: String? = null
        private set
    var id_dokter: String? = null
        private set
    var nama_dokter: String? = null
        private set
    var obat: String? = null
        private set
    var saran: String? = null
        private set
    var tanggal_pemeriksaan: String? = null
        private set

    constructor(
        id_reservasi: String?,
        id_pasien: String?,
        keluhan: String?,
        nomor_antrian: String?,
        status_reservasi: Int,
        tanggal_reservasi: String?,
        id_dokter: String?,
        nama_dokter: String?,
        obat: String?,
        saran: String?,
        tanggal_pemeriksaan: String?
    ) {
        this.id_reservasi = id_reservasi
        this.id_pasien = id_pasien
        this.keluhan = keluhan
        this.nomor_antrian = nomor_antrian
        this.status_reservasi = status_reservasi
        this.tanggal_reservasi = tanggal_reservasi
        this.id_dokter = id_dokter
        this.nama_dokter = nama_dokter
        this.obat = obat
        this.saran = saran
        this.tanggal_pemeriksaan = tanggal_pemeriksaan
    }

    constructor() {}
}