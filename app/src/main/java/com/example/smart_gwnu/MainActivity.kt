package com.example.smart_gwnu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.reflect.KParameter



class MainActivity : AppCompatActivity() {
    private val MY_GRANT : Int = 200
    var api = APIS.create()
    var h : String? =""
    var t_number : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mLogin = Intent(this, ManagerLogin::class.java)
        val uLogin = Intent(this, UserMain::class.java)
        val uJoin = Intent(this, JoinPage::class.java)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED){
            var permission = arrayOf(
                    android.Manifest.permission.READ_PHONE_NUMBERS,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.READ_SMS
            )
            ActivityCompat.requestPermissions(this, permission, MY_GRANT)
        }
        else{
            var tel : TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            loginTv.text = "등록번호 : " + tel.line1Number.toString()
            t_number = tel.line1Number.toString()
        }

        mlogTv2.setOnClickListener{
            startActivity(mLogin)
            finish()
        }

        btn_login.setOnClickListener{
            val data = UserLogin(t_number.toString(), "")
            api.login(data).enqueue(object : Callback<R_UserLogin> {
                override fun onResponse(call: Call<R_UserLogin>, response: Response<R_UserLogin>) {
                    //Log.d("logqqqqqqq", response.body().toString())
                    if(!response.body().toString().isEmpty())
                        h = response.body()!!.token
                        uLogin.putExtra("header", h.toString())
                        startActivity(uLogin)
                        finish()
                }
                override fun onFailure(call: Call<R_UserLogin>, t: Throwable) {
                    Log.d("log",t.message.toString())
                    Log.d("log","fail")
                }
            })

        }

        join.setOnClickListener{
            startActivity(uJoin)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_GRANT){
            if(grantResults.size > 0){
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        System.exit(0)
                    }
                }
                var tel : TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                loginTv.text = "등록번호 : " + tel.line1Number.toString()
                t_number = tel.line1Number.toString()
            }
        }
    }
}


