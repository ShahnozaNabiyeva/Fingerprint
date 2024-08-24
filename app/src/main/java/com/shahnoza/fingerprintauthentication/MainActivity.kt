package com.shahnoza.fingerprintauthentication

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var btn:Button
       private var cancellationSignal:CancellationSignal?=null

    private val authenticationCallBack:BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
    object :BiometricPrompt.AuthenticationCallback(){

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)

            notifyUser("Barmoq izini skanerlash xatosi")

        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            notifyUser("Barmoq izi mos keldi!")
            val intent=Intent(this@MainActivity,Private::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    btn=findViewById(R.id.btnAuth)
   checkBiometricSupport()
btn.setOnClickListener {
    val biometricPrompt=BiometricPrompt.Builder(this)
        .setTitle("Indetifikatsiya uchun")
        .setSubtitle("Shaxsni tasdiqlash uchun barmoq identifikatsiyasi")
        .setDescription("Bu dastur ma'lumotlaringizni yaxshi himoyalsh uchun barmoq izizi tekshirishi kerak")
        .setNegativeButton("Bekor qilish",this.mainExecutor,
            DialogInterface.OnClickListener{ dialog, which ->
            notifyUser("Identifikattsiya bekir qilindi!")
        }).build()
    biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallBack)

}


    }
    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal=CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Foydalanuvchi barmoq skanerini bekor qildi!")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport():Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Barmoq izi yoqilmagan!")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Barmoq izi skaneridan foydalanishga ruxsat berilmagan!")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true
    }


    private fun notifyUser(message:String){
        Toast.makeText(this, "message", Toast.LENGTH_SHORT).show()
    }
}