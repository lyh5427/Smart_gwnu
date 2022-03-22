package com.example.smart_gwnu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_user_set.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [manager_s.newInstance] factory method to
 * create an instance of this fragment.
 */
class manager_s : Fragment() {
    // TODO: Rename and change types of parameters

    var sheader : String? = ""
    var api = APIS.create()

    lateinit var mName : String
    lateinit var mgroup : String
    lateinit var minside : String
    lateinit var mset_text : TextView
    lateinit var mname : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_manager_s, container, false)

        mset_text = root.findViewById(R.id.mset_text)
        mname = root.findViewById(R.id.mName)

        sheader = arguments?.getString("mheader")
        api.get_mypage(sheader!!).enqueue(object : Callback<R_userSet> {
            override fun onResponse(call: Call<R_userSet>, response: Response<R_userSet>) {
                Log.d("userset", "${response.body()!!.toString()}")
                if(response.body()!!.role == "ROLE_MANAGER"){
                    mName = response.body()!!.username
                    mgroup = response.body()!!.group.large
                    minside = response.body()!!.group.small + "/n   " +response.body()!!.group.medium
                    mset_text.text = "그룹: ${mgroup}\n\n소속: ${mgroup}\n\nID: ${mName}"
                    mname.text = mName
                }
            }

            override fun onFailure(call: Call<R_userSet>, t: Throwable) {
                TODO("Not yet implemented")
                Log.d("checking userset", "${t.message.toString()}")
            }
        })
        return root
    }


}