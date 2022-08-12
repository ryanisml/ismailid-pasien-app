package id.ismail.pasienapps

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import id.ismail.pasienapps.lib.SharedPasien
import id.ismail.pasienapps.firestore_item.DetailItem
import id.ismail.pasienapps.firestore_item.DetailAdapter
import android.os.Bundle
import id.ismail.pasienapps.lib.LibHelper
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.WindowManager
import android.os.AsyncTask
import id.ismail.pasienapps.lib.RequestHandler
import android.content.DialogInterface
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var ivSetting: ImageView? = null
    private var tvNama: TextView? = null
    private var tvNotelp: TextView? = null
    private var tvTanggal: TextView? = null
    private var tvUpcoming: TextView? = null
    private var tvFinish: TextView? = null
    private var tvCancel: TextView? = null
    private var tvDay: TextView? = null
    private var tvMonth: TextView? = null
    private var tvYear: TextView? = null
    private var tvKeluhan: TextView? = null
    private var tvMynumber: TextView? = null
    private var tvNownumber: TextView? = null
    private var tvNoupcoming: TextView? = null
    private var tvNotoday: TextView? = null
    private var tvNohistory: TextView? = null
    private var tvViewall: TextView? = null
    private var rvHistory: RecyclerView? = null
    private var lnrUpcoming: LinearLayout? = null
    private var lnrToday: LinearLayout? = null
    private var lnrTambah: LinearLayout? = null
    private var sharedPasien: SharedPasien? = null
    private var mContext: Context? = null
    private var loading: Dialog? = null
    private var db: FirebaseFirestore? = null
    private var tgltoday = ""
    private val detailItems = ArrayList<DetailItem>()
    private var detailAdapter: DetailAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        sharedPasien = SharedPasien(mContext as MainActivity)
        db = FirebaseFirestore.getInstance()
        ivSetting = findViewById(R.id.iv_setting)
        tvNama = findViewById(R.id.tv_nama)
        tvNotelp = findViewById(R.id.tv_notelp)
        tvTanggal = findViewById(R.id.tv_tanggal)
        tvUpcoming = findViewById(R.id.tv_upcoming)
        tvFinish = findViewById(R.id.tv_finish)
        tvCancel = findViewById(R.id.tv_cancel)
        tvDay = findViewById(R.id.tv_day)
        tvMonth = findViewById(R.id.tv_month)
        tvYear = findViewById(R.id.tv_year)
        tvKeluhan = findViewById(R.id.tv_keluhan)
        tvMynumber = findViewById(R.id.tv_mynumber)
        tvNownumber = findViewById(R.id.tv_nownumber)
        tvNoupcoming = findViewById(R.id.tv_noupcoming)
        tvNotoday = findViewById(R.id.tv_notoday)
        tvNohistory = findViewById(R.id.tv_nohistory)
        tvViewall = findViewById(R.id.tv_viewall)
        rvHistory = findViewById(R.id.rv_history)
        lnrUpcoming = findViewById(R.id.lnr_upcoming)
        lnrToday = findViewById(R.id.lnr_today)
        lnrTambah = findViewById(R.id.lnr_tambah)
        loading = LibHelper.inisiasiLoading(mContext)
        initComponents()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initComponents() {
        tgltoday = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    @SuppressLint("SimpleDateFormat") val currentDateandTime =
                        SimpleDateFormat("HH:mm, dd MMMM yyyy").format(
                            Date()
                        )
                    tvTanggal!!.text = currentDateandTime
                }
            }
        }, 0, 60000)
        tvNama!!.text = sharedPasien!!.spNama
        tvNotelp!!.text = sharedPasien!!.spNotelp
        lnrTambah!!.setOnClickListener { tambahReservasi() }
        ivSetting!!.setOnClickListener {
            startActivity(
                Intent(
                    mContext,
                    PengaturanActivity::class.java
                )
            )
        }
        rvHistory!!.layoutManager = LinearLayoutManager(mContext)
        detailAdapter = DetailAdapter(detailItems)
        rvHistory!!.adapter = detailAdapter
        tvViewall!!.setOnClickListener {
            startActivity(
                Intent(
                    mContext,
                    HistoryActivity::class.java
                )
            )
        }
        my
        upcoming
        today
        data
    }

    @get:SuppressLint("NotifyDataSetChanged")
    private val data: Unit
        get() {
            rvHistory!!.visibility = View.GONE
            tvNohistory!!.visibility = View.VISIBLE
            db!!.collection("tb_reservasi").whereEqualTo("idpasien", sharedPasien!!.spIduser)
                .orderBy("tanggal_reservasi").orderBy("nomor_antrian")
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    detailItems.clear()
                    if (error != null) {
                        Toast.makeText(
                            mContext,
                            "Listen failed. when Get data reservation. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (loading != null) {
                            loading!!.dismiss()
                        }
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (value.size() > 0) {
                            rvHistory!!.visibility = View.VISIBLE
                            tvNohistory!!.visibility = View.GONE
                            for (documentSnapshot in value) {
                                val idreservasi = documentSnapshot.id
                                val idpasien =
                                    Objects.requireNonNull(documentSnapshot.data["idpasien"])
                                        .toString()
                                val keluhan =
                                    Objects.requireNonNull(documentSnapshot.data["keluhan"])
                                        .toString()
                                val nomorantrian = Objects.requireNonNull(
                                    documentSnapshot.data["nomor_antrian"]
                                ).toString()
                                val statusreservasi = Objects.requireNonNull(
                                    documentSnapshot.data["status_reservasi"]
                                ).toString().toInt()
                                val tanggalreservasi = Objects.requireNonNull(
                                    documentSnapshot.data["tanggal_reservasi"]
                                ).toString()
                                if (statusreservasi == 4) {
                                    getDetail(
                                        idreservasi,
                                        nomorantrian,
                                        statusreservasi,
                                        tanggalreservasi
                                    )
                                } else {
                                    val ri = DetailItem(
                                        idreservasi,
                                        idpasien,
                                        keluhan,
                                        nomorantrian,
                                        statusreservasi,
                                        tanggalreservasi,
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""
                                    )
                                    detailItems.add(ri)
                                    detailAdapter!!.setItemList(detailItems)
                                    detailAdapter!!.notifyDataSetChanged()
                                }
                            }
                        } else {
                            rvHistory!!.visibility = View.GONE
                            tvNohistory!!.visibility = View.VISIBLE
                        }
                    } else {
                        rvHistory!!.visibility = View.GONE
                        tvNohistory!!.visibility = View.VISIBLE
                    }
                }
        }

    private fun getDetail(
        idreservasi: String,
        nomorantrian: String,
        statusreservasi: Int,
        tanggalreservasi: String
    ) {
        db!!.collection("tb_hasil_pemeriksaan").whereEqualTo("id_reservasi", idreservasi).limit(1)
            .get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            for (documentSnapshot in task.result!!) {
                                val iddokter = Objects.requireNonNull(
                                    documentSnapshot.data["id_dokter"]
                                ).toString()
                                val keluhan = Objects.requireNonNull(
                                    documentSnapshot.data["keluhan"]
                                ).toString()
                                val obat =
                                    Objects.requireNonNull(documentSnapshot.data["obat"]).toString()
                                val saran = Objects.requireNonNull(
                                    documentSnapshot.data["saran"]
                                ).toString()
                                val tanggalpemeriksaan = Objects.requireNonNull(
                                    documentSnapshot.data["tanggal_pemeriksaan"]
                                ).toString()
                                getDokter(
                                    idreservasi,
                                    keluhan,
                                    nomorantrian,
                                    statusreservasi,
                                    tanggalreservasi,
                                    iddokter,
                                    obat,
                                    saran,
                                    tanggalpemeriksaan
                                )
                                break
                            }
                        }
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDokter(
        idreservasi: String, keluhan: String, nomorantrian: String, statusreservasi: Int,
        tanggalreservasi: String, iddokter: String, obat: String,
        saran: String, tanggal_pemeriksaan: String
    ) {
        db!!.collection("tb_dokter").document(iddokter).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val ri = DetailItem(
                            idreservasi,
                            sharedPasien!!.spIduser,
                            keluhan,
                            nomorantrian,
                            statusreservasi,
                            tanggalreservasi,
                            iddokter,
                            Objects.requireNonNull(task.result!!["nama"]).toString(),
                            obat,
                            saran,
                            tanggal_pemeriksaan
                        )
                        detailItems.add(ri)
                        detailAdapter!!.setItemList(detailItems)
                        detailAdapter!!.notifyDataSetChanged()
                    } else {
                        Toast.makeText(mContext, "Data dokter tidak ditemukan.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun tambahReservasi() {
        val dialognya = Dialog(this)
        dialognya.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialognya.setContentView(R.layout.dialog_add_reservation)
        dialognya.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialognya.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialognya.window!!.attributes = lp
        dialognya.show()
        val tvNama = dialognya.findViewById<TextView>(R.id.tv_nama)
        val tvNotelp = dialognya.findViewById<TextView>(R.id.tv_notelp)
        val tvAlamat = dialognya.findViewById<TextView>(R.id.tv_alamat)
        val tvTgllahir = dialognya.findViewById<TextView>(R.id.tv_tgllahir)
        val etKeluhan = dialognya.findViewById<EditText>(R.id.et_keluhan)
        val tvTanggaldlg = dialognya.findViewById<TextView>(R.id.tv_tanggal)
        val btnSimpan = dialognya.findViewById<Button>(R.id.btn_simpan)
        val ivClose = dialognya.findViewById<ImageView>(R.id.iv_close)
        tvNama.text = sharedPasien!!.spNama
        tvNotelp.text = sharedPasien!!.spNotelp
        tvAlamat.text = sharedPasien!!.spAlamat
        val umr =
            LibHelper.getAge(sharedPasien!!.spTgllahir!!) + " Tahun (" + sharedPasien!!.spTgllahir + ")"
        tvTgllahir.text = umr
        tvTanggaldlg.text = tgltoday
        tvTanggaldlg.setOnClickListener { chooseDate(tvTanggaldlg) }
        ivClose.setOnClickListener { dialognya.dismiss() }
        btnSimpan.setOnClickListener {
            val keluhan = etKeluhan.text.toString().trim { it <= ' ' }
            if (keluhan.isEmpty()) {
                etKeluhan.error = "Keluhan wajib diisi"
                etKeluhan.requestFocus()
                return@setOnClickListener
            }
            checkData(dialognya, keluhan, tvTanggaldlg.text.toString())
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class MyHttpRequestTask :
        AsyncTask<HashMap<String, String>?, Void?, String?>() {
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            loading!!.dismiss()
            Toast.makeText(
                mContext,
                "Berhasil menambahkan data reservasi. Silahkan cek history reservasi untuk melihat data",
                Toast.LENGTH_LONG
            ).show()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg v: HashMap<String, String>?): String? {
            val rh = RequestHandler()
            return rh.sendPostRequest(LibHelper.my_url, v[0]!!)
        }
    }

    private fun checkData(dialognya: Dialog, keluhan: String, tgl: String) {
        loading!!.show()
        db!!.collection("tb_tanggal_libur").whereEqualTo("tanggal_libur", tgl).get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            for (documentSnapshot in task.result!!) {
                                val alertDialog = AlertDialog.Builder(
                                    mContext!!
                                ).create()
                                alertDialog.setMessage(
                                    "Tidak dapat memilih tanggal $tgl karena " + Objects.requireNonNull(
                                        documentSnapshot.data["keterangan"]
                                    ) + ". " +
                                            "Silahkan pilih tanggal lainnya."
                                )
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE, "OK"
                                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                                alertDialog.show()
                                break
                            }
                            loading!!.dismiss()
                        } else {
                            checkDataKedua(dialognya, keluhan, tgl)
                        }
                    }
                } else {
                    if (task.exception != null) {
                        Toast.makeText(
                            mContext,
                            "Gagal menambahkan data " + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loading!!.dismiss()
                }
            }
    }

    private fun checkDataKedua(dialognya: Dialog, keluhan: String, tgl: String) {
        db!!.collection("tb_reservasi").whereEqualTo("idpasien", sharedPasien!!.spIduser)
            .whereEqualTo("tanggal_reservasi", tgl).get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            val alertDialog = AlertDialog.Builder(
                                mContext!!
                            ).create()
                            alertDialog.setMessage("Anda tidak dapat membuat reservasi sebanyak 2x dalam sehari. Silahkan hubungi klinik untuk informasi lebih lanjut")
                            alertDialog.setButton(
                                AlertDialog.BUTTON_POSITIVE, "OK"
                            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                            alertDialog.show()
                            loading!!.dismiss()
                        } else {
                            simpanData(dialognya, keluhan, tgl)
                        }
                    }
                } else {
                    if (task.exception != null) {
                        Toast.makeText(
                            mContext,
                            "Gagal menambahkan data " + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loading!!.dismiss()
                }
            }
    }

    private fun simpanData(dialognya: Dialog, keluhan: String, tgl: String) {
        db!!.collection("tb_reservasi").whereEqualTo("tanggal_reservasi", tgl)
            .get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    val datap: MutableMap<String, Any> = HashMap()
                    var noantri = 1
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            noantri = task.result!!.size() + 1
                        }
                    }
                    datap["nomor_antrian"] = noantri
                    datap["idpasien"] = sharedPasien!!.spIduser!!
                    datap["keluhan"] = keluhan
                    datap["noktp"] = sharedPasien!!.spNoKtp!!
                    datap["status_reservasi"] = 1
                    datap["tanggal_reservasi"] = tgl
                    val finalNoantri = noantri
                    insertLog("Reservasi baru tanggal $tgl no antrian $noantri")
                    db!!.collection("tb_reservasi").add(datap)
                        .addOnSuccessListener {
                            checkAdmin(
                                dialognya,
                                tgl,
                                finalNoantri,
                                keluhan
                            )
                        }
                } else {
                    if (task.exception != null) {
                        Toast.makeText(
                            mContext,
                            "Gagal menambahkan data " + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loading!!.dismiss()
                }
            }
    }

    private fun checkAdmin(dialognya: Dialog, tanggal: String, noantri: Int, keluhan: String) {
        db!!.collection("tb_notification").get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            dialognya.dismiss()
                            val token: MutableList<String> = ArrayList()
                            for (documentSnapshot in task.result!!) {
                                token.add(
                                    Objects.requireNonNull(documentSnapshot.data["token"])
                                        .toString()
                                )
                            }
                            val params = HashMap<String, String>()
                            for (i in token.indices) {
                                params["token[$i]"] = token[i]
                            }
                            params["tanggal"] = tanggal
                            params["noantrian"] = noantri.toString()
                            params["keluhan"] = keluhan
                            params["action"] = "add"
                            MyHttpRequestTask().execute(params)
                        } else {
                            loading!!.dismiss()
                            dialognya.dismiss()
                            Toast.makeText(
                                mContext,
                                "Berhasil menambahkan data reservasi. Silahkan cek history reservasi untuk melihat data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    if (task.exception != null) {
                        Toast.makeText(
                            mContext,
                            "Gagal menambahkan data " + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loading!!.dismiss()
                }
            }
    }

    private fun chooseDate(tv_tanggaldlg: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            DatePickerDialog(this, { _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd")
                calendar[year1, month1] = dayOfMonth
                val dateString = sdf.format(calendar.time)
                tv_tanggaldlg.text = dateString // set the date
            }, year, month, day) // set date picker to current date
        datePicker.datePicker.minDate = Date().time
        datePicker.show()
        datePicker.setOnCancelListener { obj: DialogInterface -> obj.dismiss() }
    }

    private val upcoming: Unit
        get() {
            lnrUpcoming!!.visibility = View.GONE
            tvNoupcoming!!.visibility = View.VISIBLE
            db!!.collection("tb_reservasi")
                .whereEqualTo("status_reservasi", 1)
                .whereEqualTo("idpasien", sharedPasien!!.spIduser)
                .whereGreaterThanOrEqualTo("tanggal_reservasi", tgltoday)
                .limit(1).orderBy("tanggal_reservasi", Query.Direction.ASCENDING)
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Toast.makeText(
                            mContext,
                            "Listen failed when get upcoming reservation. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (value.size() > 0) {
                            lnrUpcoming!!.visibility = View.VISIBLE
                            tvNoupcoming!!.visibility = View.GONE
                            for (documentSnapshot in value) {
                                val noantrian = Objects.requireNonNull(
                                    documentSnapshot.data["nomor_antrian"]
                                ).toString().toInt()
                                @SuppressLint("DefaultLocale") val formatted =
                                    String.format("%02d", noantrian)
                                tvMynumber!!.text = formatted
                                tvKeluhan!!.text = Objects.requireNonNull(
                                    documentSnapshot.data["keluhan"]
                                ).toString()
                                val tgl =
                                    Objects.requireNonNull(documentSnapshot.data["tanggal_reservasi"])
                                        .toString().split("-").dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                tvYear!!.text = tgl[0]
                                tvMonth!!.text = LibHelper.convertMonth(tgl[1].toInt())
                                tvDay!!.text = tgl[2]
                                break
                            }
                        } else {
                            lnrUpcoming!!.visibility = View.GONE
                            tvNoupcoming!!.visibility = View.VISIBLE
                        }
                    }
                }
        }
    private val my: Unit
        get() {
            db!!.collection("tb_reservasi")
                .whereIn("status_reservasi", listOf(3, 4, 5))
                .whereEqualTo("idpasien", sharedPasien!!.spIduser)
                .whereLessThanOrEqualTo("tanggal_reservasi", tgltoday)
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    tvFinish!!.text = "0"
                    tvCancel!!.text = "0"
                    if (error != null) {
                        Toast.makeText(
                            mContext,
                            "Listen failed when get my reservation. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (value.size() > 0) {
                            var finish = 0
                            var cancel = 0
                            for (documentSnapshot in value) {
                                val stat =
                                    Objects.requireNonNull(documentSnapshot.data["status_reservasi"])
                                        .toString().toInt()
                                when (stat) {
                                    3 -> {
                                        cancel += 1
                                    }
                                    4 -> {
                                        finish += 1
                                    }
                                    5 -> {
                                        cancel += 1
                                    }
                                }
                            }
                            tvFinish!!.text = finish.toString()
                            tvCancel!!.text = cancel.toString()
                        }
                    }
                }
            db!!.collection("tb_reservasi")
                .whereEqualTo("status_reservasi", 1)
                .whereEqualTo("idpasien", sharedPasien!!.spIduser)
                .whereGreaterThanOrEqualTo("tanggal_reservasi", tgltoday)
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Toast.makeText(
                            mContext,
                            "Listen failed when get my upcoming. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addSnapshotListener
                    }
                    tvUpcoming!!.text = "0"
                    if (value != null) {
                        if (value.size() > 0) {
                            var upcoming = 0
                            for (ignored in value) {
                                upcoming += 1
                            }
                            tvUpcoming!!.text = upcoming.toString()
                        }
                    }
                }
        }
    private val today: Unit
        get() {
            lnrToday!!.visibility = View.GONE
            tvNotoday!!.visibility = View.VISIBLE
            db!!.collection("tb_reservasi").whereEqualTo("tanggal_reservasi", tgltoday)
                .whereIn("status_reservasi", listOf(1, 2))
                .orderBy("nomor_antrian", Query.Direction.ASCENDING)
                .limit(1)
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Toast.makeText(
                            mContext,
                            "Listen failed when get today reservation. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (value.size() > 0) {
                            tvNotoday!!.visibility = View.GONE
                            lnrToday!!.visibility = View.VISIBLE
                            for (documentSnapshot in value) {
                                val noantrian =
                                    documentSnapshot.data["nomor_antrian"].toString().toInt()
                                @SuppressLint("DefaultLocale") val formatted =
                                    String.format("%02d", noantrian)
                                tvNownumber!!.text = formatted
                                break
                            }
                        } else {
                            tvNotoday!!.visibility = View.VISIBLE
                            lnrToday!!.visibility = View.GONE
                        }
                    }
                }
        }

    private fun insertLog(aktifitas: String) {
        @SuppressLint("SimpleDateFormat") val currentDateandTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                Date()
            )
        val datap: MutableMap<String, Any> = HashMap()
        datap["aktifitas_log"] = sharedPasien!!.spNama + " - " + aktifitas
        datap["status_log"] = "insert"
        datap["ip_log"] = sharedPasien!!.spPhoneType + " - " + sharedPasien!!.spImei
        datap["waktu_log"] = currentDateandTime
        datap["iduser_log"] = sharedPasien!!.spIduser!!
        datap["from_log"] = "android-pasien"
        db!!.collection("tb_log_aktifitas").add(datap)
    }
}