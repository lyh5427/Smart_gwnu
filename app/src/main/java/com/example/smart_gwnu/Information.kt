package com.example.smart_gwnu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_information.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class Information : AppCompatActivity() {
    var api = APIS.create()
    lateinit var noticeheader : String
    lateinit var id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        noticeheader = intent.getStringExtra("header").toString()
        id = intent.getStringExtra("id").toString()
        Log.d("noticeheader", "${noticeheader}awdawda")

        api.get_noticeInfo(noticeheader, "1").enqueue(object : Callback<Array<R_noticeInfo>>{
            override fun onResponse(call: Call<Array<R_noticeInfo>>, response: Response<Array<R_noticeInfo>>) {
                Log.d("notice information", "${response.body()!!.toString()}")
                infoTitle.text = response.body()!!.get(0).title
                infoText.text = response.body()!!.get(0).content
            }

            override fun onFailure(call: Call<Array<R_noticeInfo>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


    }


}