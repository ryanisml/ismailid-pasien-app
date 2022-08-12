package id.ismail.pasienapps

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import id.ismail.pasienapps.lib.SharedPasien
import android.os.Bundle
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Button

class SignActivity : AppCompatActivity() {
    private var etNoTelp: EditText? = null
    private var etCode: EditText? = null
    private var btnMasuk: Button? = null
    private var sharedPref: SharedPasien? = null
    private var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        mContext = this
        sharedPref = SharedPasien(mContext as SignActivity)
        etNoTelp = findViewById(R.id.et_notelp)
        etCode = findViewById(R.id.et_code)
        btnMasuk = findViewById(R.id.btn_masuk)
        initComponents()
    }

    private fun initComponents() {
        btnMasuk!!.setOnClickListener {
            val phonenumber = etNoTelp!!.text.toString().trim { it <= ' ' }
            if (phonenumber.isEmpty()) {
                etNoTelp!!.error = "Nomor telepon wajib diisi"
                etNoTelp!!.requestFocus()
                return@setOnClickListener
            }
            closeKeyboardView()
            checkPhone(etCode!!.text.toString(), phonenumber)
        }
    }

    private fun checkPhone(code: String, phone: String) {
        val i = Intent(mContext, OtpActivity::class.java)
        val b = Bundle()
        b.putString("kCode", code)
        b.putString("kPhone", phone)
        i.putExtras(b)
        startActivity(i)
    }

    private fun closeKeyboardView() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}