package id.ismail.pasienapps

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import id.ismail.pasienapps.lib.SharedPasien
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import id.ismail.pasienapps.lib.LibHelper
import com.google.firebase.messaging.FirebaseMessaging
import android.content.pm.PackageManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.SystemClock
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class PengaturanActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var sharedPasien: SharedPasien? = null
    private var db: FirebaseFirestore? = null
    private var tvEdit: TextView? = null
    private var tvNama: TextView? = null
    private var tvNoTelp: TextView? = null
    private var tvUmur: TextView? = null
    private var frameAbout: FrameLayout? = null
    private var frameRating: FrameLayout? = null
    private var frameKeluar: FrameLayout? = null
    private var loading: Dialog? = null
    private var mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)
        mContext = this
        sharedPasien = SharedPasien(mContext as PengaturanActivity)
        db = FirebaseFirestore.getInstance()
        initToolbar()
        tvEdit = findViewById(R.id.tv_edit)
        tvNama = findViewById(R.id.tv_nama)
        tvNoTelp = findViewById(R.id.tv_notelp)
        tvUmur = findViewById(R.id.tv_umur)
        frameAbout = findViewById(R.id.frame_about)
        frameRating = findViewById(R.id.frame_rating)
        frameKeluar = findViewById(R.id.frame_keluar)
        initComponents()
        loading = LibHelper.inisiasiLoading(mContext)
    }

    @SuppressLint("SetTextI18n")
    private fun initComponents() {
        tvNama!!.text = sharedPasien!!.spNama
        tvNoTelp!!.text = sharedPasien!!.spNotelp
        tvUmur!!.text = LibHelper.getAge(sharedPasien!!.spTgllahir!!) + " Tahun"
        frameKeluar!!.setOnClickListener {
            insertLog("Pasien telah logout akun android", "logout")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPasien!!.spIduser!!)
            val settings = mContext!!.getSharedPreferences(SharedPasien.SP_APP, MODE_PRIVATE)
            settings.edit().clear().apply()
            val intent = Intent(mContext, SignActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        frameAbout!!.setOnClickListener { showAbout() }
        frameRating!!.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)
                    )
                )
            }
        }
        tvEdit!!.setOnClickListener { editData() }
    }

    @SuppressLint("SetTextI18n")
    private fun showAbout() {
        val dialog = Dialog(mContext!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_about)
        dialog.window!!.setDimAmount(0f)
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        dialog.show()
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvVersion = dialog.findViewById<TextView>(R.id.tv_version)
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            tvVersion.text = "Versi $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            tvVersion.text = "Versi Aplikasi Tidak Ditemukan"
        }
        ivClose.setOnClickListener { dialog.dismiss() }
    }

    private fun editData() {
        val dialog = Dialog(mContext!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_edit_profile)
        dialog.window!!.setDimAmount(0f)
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
        dialog.show()
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvNoKtp = dialog.findViewById<TextView>(R.id.tv_noktp)
        val etNama = dialog.findViewById<EditText>(R.id.et_nama)
        val tvNoTelp = dialog.findViewById<TextView>(R.id.tv_notelp)
        val etAlamat = dialog.findViewById<EditText>(R.id.et_alamat)
        val lnrTgl = dialog.findViewById<LinearLayout>(R.id.lnr_tgl)
        val tvTanggal = dialog.findViewById<TextView>(R.id.tv_tanggal)
        val btnSimpan = dialog.findViewById<Button>(R.id.btn_simpan)
        tvNoKtp.text = sharedPasien!!.spNoKtp
        etNama.setText(sharedPasien!!.spNama)
        tvNoTelp.text = sharedPasien!!.spNotelp
        etAlamat.setText(sharedPasien!!.spAlamat)
        tvTanggal.text = sharedPasien!!.spTgllahir
        lnrTgl.setOnClickListener { showUmur(tvTanggal) }
        ivClose.setOnClickListener { dialog.dismiss() }
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim { it <= ' ' }
            val alamat = etAlamat.text.toString().trim { it <= ' ' }
            val tgllahir = tvTanggal.text.toString().trim { it <= ' ' }
            if (nama.isEmpty()) {
                etNama.error = "Nama wajib diisi"
                etNama.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()) {
                etAlamat.error = "Alamat wajib diisi"
                etAlamat.requestFocus()
                return@setOnClickListener
            }
            if (tgllahir.isEmpty()) {
                tvTanggal.error = "Tanggal lahir wajib diisi"
                tvTanggal.requestFocus()
                return@setOnClickListener
            }
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            simpanData(dialog, nama, alamat, tgllahir)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun simpanData(dialog: Dialog, nama: String, alamat: String, tgllahir: String) {
        loading!!.show()
        val datap: MutableMap<String, Any> = HashMap()
        datap["nama"] = nama
        datap["alamat"] = alamat
        datap["tanggal_lahir"] = tgllahir
        db!!.collection("tb_pasien").document(sharedPasien!!.spIduser!!).update(datap)
            .addOnSuccessListener {
                insertLog(
                    "Pasien no ktp " + sharedPasien!!.spNoKtp + " merubah data pribadi",
                    "update"
                )
                sharedPasien!!.saveSPString(SharedPasien.SP_NAMA, nama)
                sharedPasien!!.saveSPString(SharedPasien.SP_ALAMAT, alamat)
                sharedPasien!!.saveSPString(SharedPasien.SP_TGLLAHIR, tgllahir)
                tvNama!!.text = nama
                tvUmur!!.text = LibHelper.getAge(tgllahir) + " Tahun"
                loading!!.dismiss()
                dialog.dismiss()
                Toast.makeText(mContext, "Berhasil merubah profile", Toast.LENGTH_LONG).show()
            }
    }

    private fun showUmur(tv_tanggal: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            DatePickerDialog(this, { _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd")
                calendar[year1, month1] = dayOfMonth
                val dateString = sdf.format(calendar.time)
                tv_tanggal.text = dateString // set the date
            }, year, month, day) // set date picker to current date
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
        datePicker.setOnCancelListener { obj: DialogInterface -> obj.dismiss() }
    }

    private fun insertLog(aktifitas: String, status: String) {
        @SuppressLint("SimpleDateFormat") val currentDateandTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                Date()
            )
        val datap: MutableMap<String, Any> = HashMap()
        datap["aktifitas_log"] = sharedPasien!!.spNama + " - " + aktifitas
        datap["status_log"] = status
        datap["ip_log"] = sharedPasien!!.spPhoneType + " - " + sharedPasien!!.spImei
        datap["waktu_log"] = currentDateandTime
        datap["iduser_log"] = sharedPasien!!.spIduser!!
        datap["from_log"] = "android-pasien"
        db!!.collection("tb_log_aktifitas").add(datap)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ivBack = toolbar.findViewById<ImageView>(R.id.iv_back)
        val ivRefresh = toolbar.findViewById<ImageView>(R.id.iv_refresh)
        val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
        ivBack.setOnClickListener { onBackPressed() }
        tvTitle.text = "Pengaturan".uppercase(Locale.getDefault())
        ivRefresh.visibility = View.GONE
    }
}