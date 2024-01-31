package id.ismail.pasienapps

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import id.ismail.pasienapps.lib.SharedPasien
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.os.Bundle
import id.ismail.pasienapps.lib.LibHelper
import android.os.CountDownTimer
import com.google.firebase.auth.FirebaseAuth
import android.text.TextWatcher
import android.text.Editable
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import android.content.DialogInterface
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private var sharedPasien: SharedPasien? = null
    private var mContext: Context? = null
    private var etSatu: EditText? = null
    private var etDua: EditText? = null
    private var etTiga: EditText? = null
    private var etEmpat: EditText? = null
    private var etLima: EditText? = null
    private var etEnam: EditText? = null
    private var btnVerifikasi: Button? = null
    private var codeprov: String? = ""
    private var notelp: String? = ""
    private var code = ""
    private var loading: Dialog? = null
    private var verifBySistem: String? = null
    private var db: FirebaseFirestore? = null
    private var tvResend: TextView? = null
    private var tvTime: TextView? = null
    private var tvPhone: TextView? = null
    private var forceResendingToken: ForceResendingToken? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        mContext = this
        sharedPasien = SharedPasien(mContext as OtpActivity)
        db = FirebaseFirestore.getInstance()
        etSatu = findViewById(R.id.et_satu)
        etDua = findViewById(R.id.et_dua)
        etTiga = findViewById(R.id.et_tiga)
        etEmpat = findViewById(R.id.et_empat)
        etLima = findViewById(R.id.et_lima)
        etEnam = findViewById(R.id.et_enam)
        btnVerifikasi = findViewById(R.id.btn_verifikasi)
        tvResend = findViewById(R.id.tv_resend)
        tvTime = findViewById(R.id.tv_time)
        tvPhone = findViewById(R.id.tvPhone)
        codeprov = intent.getStringExtra("kCode")
        notelp = intent.getStringExtra("kPhone")
        startTimerThread()
        initComponents()
        checkVerification()
        tvPhone!!.text = codeprov + "" + notelp
        loading = LibHelper.inisiasiLoading(mContext)
    }

    private fun startTimerThread() {
        tvResend!!.visibility = View.GONE
        tvTime!!.visibility = View.VISIBLE
        object : CountDownTimer(60000, 1000) {
            // adjust the milli seconds here
            @SuppressLint("SetTextI18n", "DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                tvTime!!.text = "" + String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
            }

            override fun onFinish() {
                tvTime!!.visibility = View.GONE
                tvResend!!.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun initComponents() {
        etSatu!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    etDua!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etDua!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    etTiga!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etTiga!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    etEmpat!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etEmpat!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    etLima!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etLima!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    etEnam!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etEnam!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    closeKeyboardView()
                    btnVerifikasi!!.performClick()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        btnVerifikasi!!.setOnClickListener {
            val code1 = etSatu!!.text.toString().trim { it <= ' ' }
            val code2 = etDua!!.text.toString().trim { it <= ' ' }
            val code3 = etTiga!!.text.toString().trim { it <= ' ' }
            val code4 = etEmpat!!.text.toString().trim { it <= ' ' }
            val code5 = etLima!!.text.toString().trim { it <= ' ' }
            val code6 = etEnam!!.text.toString().trim { it <= ' ' }
            code = ""
            if (code1.isEmpty()) {
                etSatu!!.error = "Enter Code"
                etSatu!!.requestFocus()
                return@setOnClickListener
            }
            if (code2.isEmpty()) {
                etDua!!.error = "Enter Code"
                etDua!!.requestFocus()
                return@setOnClickListener
            }
            if (code3.isEmpty()) {
                etTiga!!.error = "Enter Code"
                etTiga!!.requestFocus()
                return@setOnClickListener
            }
            if (code4.isEmpty()) {
                etEmpat!!.error = "Enter Code"
                etEmpat!!.requestFocus()
                return@setOnClickListener
            }
            if (code5.isEmpty()) {
                etLima!!.error = "Enter Code"
                etLima!!.requestFocus()
                return@setOnClickListener
            }
            if (code6.isEmpty()) {
                etEnam!!.error = "Enter Code"
                etEnam!!.requestFocus()
                return@setOnClickListener
            }
            code = code1 + "" + code2 + "" + code3 + "" + code4 + "" + code5 + "" + code6
            loading!!.show()
            verifyCode(code)
        }
        tvResend!!.setOnClickListener {
            tvResend!!.visibility = View.GONE
            resendVerificationCode(codeprov + "" + notelp, forceResendingToken)
            startTimerThread()
        }
    }

    private fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken?) {
        if (token != null) {
            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                    .setActivity(this)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setCallbacks(mCallbacks)
                    .setForceResendingToken(token)
                    .build()
            )
        } else {
            checkVerification()
        }
    }

    private fun checkVerification() {
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setActivity(this)
                .setPhoneNumber(codeprov + "" + notelp)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallbacks)
                .build()
        )
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verifBySistem = s
                this@OtpActivity.forceResendingToken = forceResendingToken
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val codefire = phoneAuthCredential.smsCode
                if (codefire != null) {
                    loading!!.show()
                    verifyCode(codefire)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (loading != null) {
                    loading!!.dismiss()
                }
                val alertDialog = AlertDialog.Builder(
                    mContext!!
                ).create()
                alertDialog.setMessage("Verification failed : " + e.message)
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "OK"
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                alertDialog.show()
            }

            override fun onCodeAutoRetrievalTimeOut(s: String) {
                super.onCodeAutoRetrievalTimeOut(s)
                if (loading != null) {
                    loading!!.dismiss()
                }
                val alertDialog = AlertDialog.Builder(
                    mContext!!
                ).create()
                alertDialog.setMessage("Verification timeout $s")
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "OK"
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                alertDialog.show()
            }
        }

    private fun verifyCode(codefire: String) {
        if (verifBySistem != null) {
            val credential = PhoneAuthProvider.getCredential(verifBySistem!!, codefire)
            signByCredential(credential)
        } else {
            loading!!.dismiss()
            // Handle the case where verifBySistem is null
            val alertDialog = AlertDialog.Builder(
                mContext!!
            ).create()
            alertDialog.setMessage("Verification system is null")
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            alertDialog.show()
        }
    }

    private fun signByCredential(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.user != null) {
                            val imei = UUID.randomUUID().toString()
                            db!!.collection("tb_pasien").document(task.result!!.user!!.uid).get()
                                .addOnCompleteListener { tasker: Task<DocumentSnapshot?> ->
                                    if (tasker.isSuccessful) {
                                        if (tasker.result != null) {
                                            if (tasker.result!!.data != null) {
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_IDUSER, task.result!!
                                                        .user!!.uid
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_NO_KTP, Objects.requireNonNull(
                                                        tasker.result!!["noktp"]
                                                    ).toString()
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_NAMA, Objects.requireNonNull(
                                                        tasker.result!!["nama"]
                                                    ).toString()
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_NOTELP, Objects.requireNonNull(
                                                        tasker.result!!["notelp"]
                                                    ).toString()
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_TGLLAHIR,
                                                    Objects.requireNonNull(
                                                        tasker.result!!["tanggal_lahir"]
                                                    ).toString()
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_ALAMAT, Objects.requireNonNull(
                                                        tasker.result!!["alamat"]
                                                    ).toString()
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_IMEI,
                                                    imei
                                                )
                                                sharedPasien!!.saveSPString(
                                                    SharedPasien.SP_PHONE_TYPE,
                                                    LibHelper.deviceName
                                                )
                                                sharedPasien!!.saveSPBoolean(
                                                    SharedPasien.SP_SUDAH_LOGIN,
                                                    true
                                                )
                                                FirebaseMessaging.getInstance().subscribeToTopic(
                                                    task.result!!.user!!.uid
                                                )
                                                val intent =
                                                    Intent(mContext, MainActivity::class.java)
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                insertLog("Pasien no ktp " + sharedPasien!!.spNoKtp + " melakukan login android")
                                                startActivity(intent)
                                            } else {
                                                val i =
                                                    Intent(mContext, ProfileActivity::class.java)
                                                i.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                val b = Bundle()
                                                b.putString("kPhone", codeprov + "" + notelp)
                                                b.putString("kIdUser", task.result!!.user!!.uid)
                                                i.putExtras(b)
                                                startActivity(i)
                                            }
                                        }
                                    }
                                    if (loading != null) {
                                        loading!!.dismiss()
                                    }
                                }
                        }
                    }
                } else {
//                    if (task.exception != null) {
//                        AlertDialog.Builder(mContext!!)
//                            .setMessage("Gagal sign " + task.exception!!.message)
//                            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
//                                dialog.dismiss()
//                            }
//                            .show()
//                    }
                    if (loading != null) {
                        loading!!.dismiss()
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
        datap["status_log"] = "login"
        datap["ip_log"] = sharedPasien!!.spPhoneType + " - " + sharedPasien!!.spImei
        datap["waktu_log"] = currentDateandTime
        datap["iduser_log"] = sharedPasien!!.spIduser!!
        datap["from_log"] = "android-pasien"
        db!!.collection("tb_log_aktifitas").add(datap)
    }

    private fun closeKeyboardView() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}