package com.example.smart_gwnu

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_manager_login.*
import kotlinx.android.synthetic.main.fragment_manager_h.*
import kotlinx.android.synthetic.main.fragment_manager_v.*
import kotlinx.android.synthetic.main.record_text.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class manager_v : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var api = APIS.create()
    var header :String? =""//"Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2MzgzODc1OTMsImlhdCI6MTYzODM2OTU5MywianRpIjoibWFuYWdlciJ9.IObD9aIxeboA0KjhI9HvHQbylJOVF3k3OwbNTbaExAAzOXgsIf81i4uoCG0QMdg5a6ep4xsDWdTm3BqIZHQfpQ"
    lateinit var name : Array<String>
    lateinit var ida : Array<String>
    var select : String? = null

    private fun dial(response: Response<Array<A>>){
        var a = 0
        name = Array(response.body()!!.size,{"0"})
        ida = Array(response.body()!!.size,{"0"})
        while(a < response.body()!!.size){
            name.set(a,response.body()!!.get(a).name)
            ida.set(a,response.body()!!.get(a).id.toString())
            a=a+1
        }
        var dlg = AlertDialog.Builder(requireContext())
        dlg.setIcon(R.drawable.gwnu_logo)
        dlg.setItems(name){ dialog, which->
            buildingName.text = name[which]
            seeLog(ida[which])
        }
        dlg.setPositiveButton("닫기", null)
        dlg.show()
    }

    private fun seeLog(id : String){
        api.get_buidingVisit(header!!, id).enqueue((object : Callback<Array<R_ManagerVisit>>{
            override fun onResponse(call: Call<Array<R_ManagerVisit>>, response: Response<Array<R_ManagerVisit>>) {
                var a = response.body()!!.size - 1
                Log.d("방문기록", "${response.body()!!.toString()}")
                var i = 0
                while( i <= a){
                    val mInflater : LayoutInflater = layoutInflater
                    val v : View = mInflater.inflate(R.layout.record_text, list, false)
                    v.record_username.setText(response.body()!!.get(i).username)
                    v.record_building.setText(response.body()!!.get(i).buildingName)
                    v.record_time.setText(response.body()!!.get(i).createdAt)
                    if(!response.body()!!.get(i).isEnter) {
                        v.arrow_navigate.rotation = 180F
                    }
                    list.addView(v)
                    /*val tv = TextView(this@manager_v.requireContext())
                    tv.text = "출입 건물 : "+response.body()!!.get(i).buildingName +
                            "\n출입자 : " + response.body()!!.get(i).username +
                            "\n 출입 시간 : " + response.body()!!.get(i).createdAt
                    tv.gravity = Gravity.CENTER
                    tv.textSize = 12f*/
                    i=i+1
                 }
            }

            override fun onFailure(call: Call<Array<R_ManagerVisit>>, t: Throwable) {
                Log.d("checking building visited", " 시발")
            }
        }))

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        header = arguments?.getString("mheader")
        api.get_buildingID(header!!).enqueue(object : Callback<Array<A>> {
            override fun onResponse(call: Call<Array<A>>, response: Response<Array<A>>) {
                if(!response.body().toString().isEmpty()){
                    dial(response!!)
                }
            }

            override fun onFailure(call: Call<Array<A>>, t: Throwable) {
                Log.d("log",t.message.toString())
                Log.d("log","fail")
            }
        })

        return inflater.inflate(R.layout.fragment_manager_v, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            manager_v().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}