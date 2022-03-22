package com.example.smart_gwnu

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_manager_v.*
import kotlinx.android.synthetic.main.record_text.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [user_visit.newInstance] factory method to
 * create an instance of this fragment.
 */
class user_visit : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var vheader : String? = ""
    var api = APIS.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_user_visit, container, false)
        vheader = arguments?.getString("mheader")
        Log.d("vheader", "")

        api.get_userRecord(vheader!!).enqueue(object : Callback<Array<R_ManagerVisit>>{
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
                    /*val tv = TextView(this@user_visit.requireContext())
                    tv.text = "출입 건물 : "+response.body()!!.get(i).buildingName +
                            "\n출입자 : " + response.body()!!.get(i).username +
                            "\n 출입 시간 : " + response.body()!!.get(i).createdAt
                    tv.gravity = Gravity.CENTER
                    tv.textSize = 12f
                    list.addView(tv)*/
                    i=i+1
                }
            }

            override fun onFailure(call: Call<Array<R_ManagerVisit>>, t: Throwable) {

            }

        })



        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment user_visit.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            user_visit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}