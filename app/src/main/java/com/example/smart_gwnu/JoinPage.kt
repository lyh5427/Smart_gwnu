package com.example.smart_gwnu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_boardcreate.*
import kotlinx.android.synthetic.main.activity_join_page.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinPage : AppCompatActivity() {
    private val MY_GRANT : Int = 100
    lateinit var largelist : Array<String>
    lateinit var mediumList : Array<String>
    lateinit var smallList: Array<String>
    val api = APIS.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_page)

        fun largeDial( r : Response<Array<largeList>>, size : Int){
            var i =0
            largelist = Array(r.body()!!.size, {"0"})
            while(i<size){
                largelist.set(i,r.body()!!.get(i).largeName)
                i=i+1
            }
            var dlg = AlertDialog.Builder(this)
            dlg.setIcon(R.drawable.gwnu_logo)
            dlg.setItems(largelist){ dialog, which->
                group1.text = largelist[which]
            }
            dlg.setPositiveButton("닫기", null)
            dlg.show()
        }

        fun mediumDial( r : Response<Array<mediumList>>, size : Int){
            var i = 0
            mediumList = Array(r.body()!!.size, {"0"})
            while(i<r.body()!!.size){
                mediumList.set(i,r.body()!!.get(i).mediumName)
                i=i+1
            }
            var dlg = AlertDialog.Builder(this)
            dlg.setIcon(R.drawable.ic_launcher_background)
            dlg.setItems(mediumList){ dialog, which->
                group2.text = mediumList[which]
            }
            dlg.setPositiveButton("닫기", null)
            dlg.show()
        }

        fun smallDial( r : Response<Array<smallList>>, size : Int){
            var i = 0
            smallList = Array(r.body()!!.size, {"0"})
            while(i<r.body()!!.size){
                smallList.set(i,r.body()!!.get(i).smallName)
                i=i+1
            }
            var dlg = AlertDialog.Builder(this)
            dlg.setIcon(R.drawable.ic_launcher_background)
            dlg.setItems(smallList) { dialog, which ->
                group3.text = smallList[which]
            }
            dlg.setPositiveButton("닫기", null)
            dlg.show()
        }

        val intent = Intent(this, MainActivity::class.java)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            var permission = arrayOf(
                android.Manifest.permission.READ_PHONE_NUMBERS,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_SMS
            )
            ActivityCompat.requestPermissions(this, permission, MY_GRANT)
        }
        else{

            var tel : TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            join_id_text.text = tel.line1Number.toString()

        }

        group1.setOnClickListener{
            api.get_largeList().enqueue(object : Callback<Array<largeList>>{
                override fun onResponse(call: Call<Array<largeList>>, response: Response<Array<largeList>>) {
                    largeDial(response, response.body()!!.size)
                }
                override fun onFailure(call: Call<Array<largeList>>, t: Throwable) {

                }
            })

        }
        group2.setOnClickListener{
            api.get_mediumList(group1.text.toString()).enqueue(object : Callback<Array<mediumList>>{
                override fun onResponse(call: Call<Array<mediumList>>, response: Response<Array<mediumList>>) {
                    mediumDial(response, response.body()!!.size)

                }

                override fun onFailure(call: Call<Array<mediumList>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        }

        group3.setOnClickListener {
            api.get_smallLiset(group1.text.toString(), group2.text.toString()).enqueue(object : Callback<Array<smallList>>{
                override fun onResponse(call: Call<Array<smallList>>, response: Response<Array<smallList>>) {
                    smallDial(response, response.body()!!.size)

                }

                override fun onFailure(call: Call<Array<smallList>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }

        btn_join.setOnClickListener{
            if(join_id_text.text != null && group1.text !=null && group2.text !=null&& group3.text !=null){
                val data = UserJoin(join_id_text.text.toString(), group1.text.toString(), group2.text.toString(), group3.text.toString() )
                api.userJoin(data).enqueue(object : Callback<R_UserJoin>{
                    override fun onResponse(call: Call<R_UserJoin>, response: Response<R_UserJoin>) {
                        Toast.makeText(applicationContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call<R_UserJoin>, t: Throwable) {
                        Toast.makeText(applicationContext, "다시한번 시도해 주세요", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(applicationContext, "그룹을 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
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
                join_id_text.text = tel.line1Number.toString()
            }
        }
    }
}