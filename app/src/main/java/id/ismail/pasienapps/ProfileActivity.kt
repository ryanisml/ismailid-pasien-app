package id.ismail.pasienapps

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import id.ismail.pasienapps.lib.SharedPasien
import android.os.Bundle
import id.ismail.pasienapps.lib.LibHelper
import com.google.firebase.firestore.QuerySnapshot
import android.content.DialogInterface
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Intent
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private var etNoKtp: EditText? = null
    private var etNama: EditText? = null
    private var etAlamat: EditText? = null
    private var tvNoTelp: TextView? = null
    private var tvTanggal: TextView? = null
    private var cbVerified: CheckBox? = null
    private var btnSave: Button? = null
    private var db: FirebaseFirestore? = null
    private var mContext: Context? = null
    private var sharedPasien: SharedPasien? = null
    private var iduser: String? = ""
    private var notelp: String? = ""
    private var loading: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mContext = this
        sharedPasien = SharedPasien(mContext as ProfileActivity)
        db = FirebaseFirestore.getInstance()
        etNoKtp = findViewById(R.id.et_noktp)
        etNama = findViewById(R.id.et_nama)
        tvNoTelp = findViewById(R.id.tv_notelp)
        etAlamat = findViewById(R.id.et_alamat)
        tvTanggal = findViewById(R.id.tv_tanggal)
        cbVerified = findViewById(R.id.cb_verified)
        btnSave = findViewById(R.id.btn_save)
        iduser = intent.getStringExtra("kIdUser")
        notelp = intent.getStringExtra("kPhone")
        initComponents()
        loading = LibHelper.inisiasiLoading(mContext)
    }

    private fun initComponents() {
        tvTanggal!!.setOnClickListener { chooseDate() }
        tvNoTelp!!.text = notelp
        btnSave!!.setOnClickListener {
            if (!cbVerified!!.isChecked) {
                Toast.makeText(
                    mContext,
                    "Silahkan baca ketentuan terlebih dahulu",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val noktp = etNoKtp!!.text.toString().trim { it <= ' ' }
            val nama = etNama!!.text.toString().trim { it <= ' ' }
            val alamat = etAlamat!!.text.toString().trim { it <= ' ' }
            val tgllahir = tvTanggal!!.text.toString().trim { it <= ' ' }
            if (noktp.isEmpty()) {
                etNoKtp!!.error = "Nomor KTP wajib diisi"
                etNoKtp!!.requestFocus()
                return@setOnClickListener
            }
            if (nama.isEmpty()) {
                etNama!!.error = "Nama wajib diisi"
                etNama!!.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()) {
                etAlamat!!.error = "Alamat wajib diisi"
                etAlamat!!.requestFocus()
                return@setOnClickListener
            }
            if (tgllahir.isEmpty()) {
                tvTanggal!!.error = "Tanggal Lahir wajib diisi"
                tvTanggal!!.requestFocus()
                return@setOnClickListener
            }
            simpan()
        }
    }

    private fun simpan() {
        loading!!.show()
        db!!.collection("tb_pasien").whereEqualTo("noktp", etNoKtp!!.text.toString()).get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result!!.size() > 0) {
                        val alertDialog = AlertDialog.Builder(
                            mContext!!
                        ).create()
                        alertDialog.setTitle("Maaf")
                        alertDialog.setMessage("Nomor KTP tidak dapat digunakan kembali. Silahkan hubungi klinik jika ini adalah sebuah kesalahan.")
                        alertDialog.setButton(
                            AlertDialog.BUTTON_POSITIVE, "OK"
                        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                        alertDialog.show()
                        loading!!.dismiss()
                    } else {
                        val imei = UUID.randomUUID().toString()
                        @SuppressLint("SimpleDateFormat") val currentDateandTime =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                Date()
                            )
                        val datap: MutableMap<String, Any?> = HashMap()
                        datap["noktp"] = etNoKtp!!.text.toString()
                        datap["nama"] = etNama!!.text.toString()
                        datap["notelp"] = notelp
                        datap["alamat"] = etAlamat!!.text.toString()
                        datap["tanggal_lahir"] = tvTanggal!!.text.toString()
                        datap["has_account"] = 1
                        datap["phone_login"] = LibHelper.deviceName
                        datap["time_register"] = currentDateandTime
                        db!!.collection("tb_pasien").document(iduser!!).set(datap)
                            .addOnCompleteListener {
                                loading!!.dismiss()
                                sharedPasien!!.saveSPString(SharedPasien.SP_IDUSER, iduser)
                                sharedPasien!!.saveSPString(
                                    SharedPasien.SP_NO_KTP,
                                    etNoKtp!!.text.toString()
                                )
                                sharedPasien!!.saveSPString(
                                    SharedPasien.SP_NAMA,
                                    etNama!!.text.toString()
                                )
                                sharedPasien!!.saveSPString(
                                    SharedPasien.SP_ALAMAT,
                                    etAlamat!!.text.toString()
                                )
                                sharedPasien!!.saveSPString(SharedPasien.SP_NOTELP, notelp)
                                sharedPasien!!.saveSPString(
                                    SharedPasien.SP_TGLLAHIR,
                                    tvTanggal!!.text.toString()
                                )
                                sharedPasien!!.saveSPString(SharedPasien.SP_IMEI, imei)
                                sharedPasien!!.saveSPString(
                                    SharedPasien.SP_PHONE_TYPE,
                                    LibHelper.deviceName
                                )
                                sharedPasien!!.saveSPBoolean(SharedPasien.SP_SUDAH_LOGIN, true)
                                FirebaseMessaging.getInstance().subscribeToTopic(iduser!!)
                                val intent = Intent(mContext, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                insertLog("Pasien no ktp " + sharedPasien!!.spNoKtp + " melakukan login android")
                                startActivity(intent)
                            }
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        "Gagal menyimpan data. Silahkan coba lagi",
                        Toast.LENGTH_LONG
                    ).show()
                    loading!!.dismiss()
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
        datap["status_log"] = "login"
        datap["ip_log"] = sharedPasien!!.spPhoneType + " - " + sharedPasien!!.spImei
        datap["waktu_log"] = currentDateandTime
        datap["iduser_log"] = sharedPasien!!.spIduser!!
        datap["from_log"] = "android-pasien"
        db!!.collection("tb_log_aktifitas").add(datap)
    }

    private fun chooseDate() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            DatePickerDialog(this, { _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd")
                calendar[year1, month1] = dayOfMonth
                val dateString = sdf.format(calendar.time)
                tvTanggal!!.text = dateString // set the date
            }, year, month, day) // set date picker to current date
        datePicker.show()
        datePicker.setOnCancelListener { obj: DialogInterface -> obj.dismiss() }
    }
}