package com.example.smart_gwnu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_boardcreate.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.*

class boardcreate : AppCompatActivity() {
    var api = APIS.create()
    lateinit var bheader : String
    val nameList = ArrayList<Long>(0)
    var isPublic : Boolean = false

    //스몰 체크박스 생성
    private fun smallCheck(response : Response<Array<smallList>>, size : Int){
        val listener = CompoundButton.OnCheckedChangeListener{ buttonView, ischeck ->
            val ii = buttonView.id-1000
            if(ischeck){
                nameList.add(response.body()!!.get(ii).id)
            }
            else{
                nameList.remove(response.body()!!.get(ii).id)
            }
        }
        var i=0
        while(size>i){
            var check = CheckBox(this)
            check.id = i+1000
            check.text = response.body()!!.get(i).smallName
            check.setOnCheckedChangeListener(listener)
            smallNameList.addView(check)
            i = i+1
        }
    }

    //미디엄 받으면 체크박스 생성


    private fun mediunCheck(response : Response<Array<mediumList>>, size : Int, largeName : String){
        val listener = CompoundButton.OnCheckedChangeListener{ buttonView, ischeck ->
            if(ischeck){
                api.get_smallLiset(largeName, response.body()!!.get(buttonView.id).mediumName).enqueue(object : Callback<Array<smallList>>{
                    override fun onResponse(call: Call<Array<smallList>>, response: Response<Array<smallList>>) {
                        smallCheck(response, response.body()!!.size)
                    }

                    override fun onFailure(call: Call<Array<smallList>>, t: Throwable) {

                    }
                })
            }else{
                smallNameList.removeAllViews()
            }
        }
        val a = size
        var i=0
        while(a>i){
            var check = CheckBox(this)
            check.id = i
            check.text = response.body()!!.get(i).mediumName
            check.setOnCheckedChangeListener(listener)
            mediumNameList.addView(check)
            i=i+1
        }
    }

    private fun largeCheck(largeName : String){
        var largeView = TextView(this)
        largeView.text = largeName
        largeNameList.addView(largeView)
        api.get_mediumList(largeName).enqueue(object : Callback<Array<mediumList>>{
            override fun onResponse(call: Call<Array<mediumList>>, response: Response<Array<mediumList>>) {
                Log.d("bcheck", "${response.body().toString()}awdawd")
                var string : String = ""
                var a = response.body()!!.size
                mediunCheck(response, a, largeName)

            }

            override fun onFailure(call: Call<Array<mediumList>>, t: Throwable) {

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boardcreate)

        bheader = intent.getStringExtra("he").toString()
        Log.d("bheader", "${bheader}awdawdaw")
        api.get_managerGroup(bheader).enqueue(object : Callback<largeList>{
            override fun onResponse(call: Call<largeList>, response: Response<largeList>) {
                largeCheck(response.body()!!.largeName)
            }

            override fun onFailure(call: Call<largeList>, t: Throwable) {

            }
        })

        switch1.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            if (onSwitch){
                isPublic = true
            }else{
                isPublic = false
            }
        }

        creat_btn.setOnClickListener{
            var data = creatBoard(infoTitle.text.toString(),infoText.text.toString(),isPublic, nameList)
            api.creat_notice(bheader,data).enqueue(object : Callback<Any>{
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.d("check creat", " 111111111")
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

            })
            finish()
        }
    }
}