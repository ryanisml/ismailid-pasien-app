package id.ismail.pasienapps.lib

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.TextUtils
import id.ismail.pasienapps.R
import android.view.ViewGroup
import android.view.Gravity
import android.view.Window
import java.lang.StringBuilder
import java.util.*

object LibHelper {
    var my_url = "https://klinik.ismail.id/myklinik/notification-admin"
    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else capitalize(manufacturer) + " " + model
        }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(c.uppercaseChar())
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

    fun getAge(tanggal: String): String {
        val tgl = tanggal.split("-").dropLastWhile { it.isEmpty() }.toTypedArray()
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[tgl[0].toInt(), tgl[1].toInt()] = tgl[2].toInt()
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    fun inisiasiLoading(mContext: Context?): Dialog {
        val loading = Dialog(mContext!!)
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loading.setContentView(R.layout.dialog_loading)
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)
        loading.window!!.setDimAmount(0.5f)
        val params = loading.window!!.attributes
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        return loading
    }

    fun convertMonth(month: Int): String {
        val bulan: String
        bulan = when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> "SEMUA"
        }
        return bulan
    }
}