package id.ismail.pasienapps.lib

import android.content.Context
import android.content.SharedPreferences

class SharedPasien(context: Context) {
    private var sp: SharedPreferences
    private var spEditor: SharedPreferences.Editor
    fun saveSPString(keySP: String?, value: String?) {
        spEditor.putString(keySP, value)
        spEditor.commit()
    }

    fun saveSPBoolean(keySP: String?, value: Boolean) {
        spEditor.putBoolean(keySP, value)
        spEditor.commit()
    }

    val spNama: String?
        get() = sp.getString(SP_NAMA, "")
    val spNoKtp: String?
        get() = sp.getString(SP_NO_KTP, "")
    val spNotelp: String?
        get() = sp.getString(SP_NOTELP, "")
    val spImei: String?
        get() = sp.getString(SP_IMEI, "")
    val spPhoneType: String?
        get() = sp.getString(SP_PHONE_TYPE, "")
    val spTgllahir: String?
        get() = sp.getString(SP_TGLLAHIR, "")
    val spAlamat: String?
        get() = sp.getString(SP_ALAMAT, "")
    val spIduser: String?
        get() = sp.getString(SP_IDUSER, "")
    val spSudahLogin: Boolean
        get() = sp.getBoolean(SP_SUDAH_LOGIN, false)

    companion object {
        const val SP_APP = "pasien_ismailid"
        const val SP_IDUSER = "sp_iduser"
        const val SP_NAMA = "sp_nama"
        const val SP_NO_KTP = "sp_noktp"
        const val SP_NOTELP = "sp_notelp"
        const val SP_TGLLAHIR = "sp_tgllahir"
        const val SP_ALAMAT = "sp_alamat"
        const val SP_IMEI = "sp_imei"
        const val SP_PHONE_TYPE = "sp_phone_type"
        const val SP_SUDAH_LOGIN = "sp_is_login"
    }

    init {
        sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE)
        spEditor = sp.edit()
    }
}