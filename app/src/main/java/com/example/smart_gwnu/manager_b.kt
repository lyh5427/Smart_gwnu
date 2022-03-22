package com.example.smart_gwnu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_manager_b.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [manager_b.newInstance] factory method to
 * create an instance of this fragment.
 */
class manager_b : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var bheader : String? = ""
    var api = APIS.create()

    lateinit var t : TextView
    lateinit var t2 : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bheader = arguments?.getString("mheader")

        var root = inflater.inflate(R.layout.fragment_manager_b, container, false)
        var creat = root.findViewById<Button>(R.id.boardcreat)

        var intent = Intent(requireContext(), boardcreate::class.java)

        var titleList = arrayListOf(R.id.mtitle1, R.id.mtitle2, R.id.mtitle3, R.id.mtitle4, R.id.mtitle5
            , R.id.mtitle6, R.id.mtitle7, R.id.mtitle8, R.id.mtitle9, R.id.mtitle10)
        var textList = arrayListOf(R.id.mtext1, R.id.mtext2, R.id.mtext3, R.id.mtext4, R.id.mtext5
            , R.id.mtext6, R.id.mtext7, R.id.mtext8, R.id.mtext9, R.id.mtext10)

        fun setText(res : Response<Array<notice>>){
            var i = 0

            var a = res.body()!!.size
            if(a>10){
                a=10
            }
            while(i<a){
                t = root.findViewById(titleList[i])
                t2 = root.findViewById(textList[i])
                t.text = res.body()!!.get(i).title
                t2.text = res.body()!!.get(i).content
                i=i+1
            }
        }

        creat.setOnClickListener{
            intent.putExtra("he", bheader)
            startActivity(intent)
        }

        api.get_notice(bheader!!).enqueue(object:Callback<Array<notice>>{
            override fun onResponse(call: Call<Array<notice>>, response: Response<Array<notice>>) {
                Log.d("바디체크", "${response.body()!!.toString()}")
                setText(response)
            }

            override fun onFailure(call: Call<Array<notice>>, t: Throwable) {

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
         * @return A new instance of fragment manager_b.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            manager_b().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}