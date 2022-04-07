package com.example.smart_gwnu.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smart_gwnu.*
import com.example.smart_gwnu.restful.APIS
import com.example.smart_gwnu.restful.R_UserLogin
import com.example.smart_gwnu.restful.UserLogin
import kotlinx.android.synthetic.main.activity_manager_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerLogin : AppCompatActivity() {
    var api = APIS.create()
    var header : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_login)

        val uLogin = Intent(this, MainActivity::class.java)
        val mLogin = Intent(this, mangerMain::class.java)

        ulogTv2.setOnClickListener{
            startActivity(uLogin)
            finish()
        }

        btn_mlogin.setOnClickListener{
            var data = UserLogin(id_text.text.toString(), pw_text.text.toString())
            api.login(data).enqueue(object : Callback<R_UserLogin> {
                override fun onResponse(call: Call<R_UserLogin>, response: Response<R_UserLogin>) {
                    Log.d("jjjjjjj", "${response.body()!!.token.toString()}wedad")
                    header = response.body()!!.token
                    Log.d("HEADER", header.toString())
                    mLogin.putExtra("mheader", header.toString())
                    startActivity(mLogin)
                    finish()
                }

                override fun onFailure(call: Call<R_UserLogin>, t: Throwable) {
                    Log.d("log",t.message.toString())
                    Log.d("log","fail")
                }
            })
        }

    }


}