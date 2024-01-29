package id.ismail.pasienapps

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import id.ismail.pasienapps.lib.SharedPasien
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var sharedPref: SharedPasien? = null
    private var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mContext = this
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        sharedPref = SharedPasien(mContext as SplashScreenActivity)
        val mainHandler = Handler(Looper.getMainLooper())

        val splashInterval = 1500

        mainHandler.postDelayed({
            finish()
            if (sharedPref!!.spSudahLogin) {
                startActivity(Intent(mContext, MainActivity::class.java))
            } else {
                startActivity(Intent(mContext, SignActivity::class.java))
            }
        }, splashInterval.toLong())
    }
}