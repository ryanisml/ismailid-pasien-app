package id.ismail.pasienapps

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import id.ismail.pasienapps.lib.SharedPasien
import android.os.Bundle
import android.content.Intent
import android.os.Handler

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var sharedPref: SharedPasien? = null
    private var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mContext = this
        sharedPref = SharedPasien(mContext as SplashScreenActivity)
        val splashInterval = 1500
        Handler().postDelayed({
            finish()
            if (sharedPref!!.spSudahLogin) {
                startActivity(Intent(mContext, MainActivity::class.java))
            } else {
                startActivity(Intent(mContext, SignActivity::class.java))
            }
        }, splashInterval.toLong())
    }
}