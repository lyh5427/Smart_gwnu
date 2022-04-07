package com.example.smart_gwnu.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.smart_gwnu.restful.APIS
import com.example.smart_gwnu.R
import com.example.smart_gwnu.restful.R_userSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [user_set.newInstance] factory method to
 * create an instance of this fragment.
 */
class user_set : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var sheader : String? = ""
    var api = APIS.create()

    lateinit var uName : String
    lateinit var ugroup : String
    lateinit var uinside : String

    lateinit var uset_text : TextView
    lateinit var userName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_user_set, container, false)
        sheader = arguments?.getString("mheader")
        Log.d("sheader", "${sheader} qweqwe")

        uset_text = root.findViewById(R.id.uset_text)
        userName = root.findViewById(R.id.userName)

        api.get_mypage(sheader!!).enqueue(object : Callback<R_userSet>{
            override fun onResponse(call: Call<R_userSet>, response: Response<R_userSet>) {
                Log.d("userset", "${response.body()!!.toString()}")
                if(response.body()!!.role == "ROLE_USER"){
                    uName = response.body()!!.username
                    ugroup = response.body()!!.group.large
                    uinside = "${response.body()!!.group.small} \n ${response.body()!!.group.medium}"
                    uset_text.text = "그룹: ${ugroup.toString()}\n\n소속: ${uinside}\n\nID: ${uName}"
                    userName.text = uName
                }
            }

            override fun onFailure(call: Call<R_userSet>, t: Throwable) {
                TODO("Not yet implemented")
                Log.d("checking userset", "${t.message.toString()}")
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
         * @return A new instance of fragment user_set.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            user_set().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}