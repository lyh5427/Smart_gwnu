package com.example.smart_gwnu

import android.graphics.Color
import android.icu.util.LocaleData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_manager_h.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Month
import java.time.Year

class manager_h : Fragment(), OnMapReadyCallback{

    private var la : Double? = null
    private var lo : Double? = null
    private lateinit var gView : MapView
    private lateinit var dateView : TextView
    private lateinit var stick : BarChart
    private lateinit var circle2 : PieChart
    lateinit var name : Array<String>
    lateinit var ida : Array<String>
    lateinit var startYear: EditText
    lateinit var startMonth: EditText
    lateinit var startDate: EditText
    lateinit var endYear: EditText
    lateinit var endMonth: EditText
    lateinit var endDate: EditText
    lateinit var buildingName2 : TextView
    lateinit var post_btn : Button
    lateinit var dayStick : BarChart

    var index : Int = 0

    var hheader : String? = ""
    var api = APIS.create()

    //그래프
    var total : Int = 0 // 총 출입 인원
    lateinit var daily : HashMap<String, Int> //일일 출입인원 / 총 출입 인원
    lateinit var dailyDate : Array<String> //일일 날짜
    lateinit var smallGroupName : HashMap<String, Int>// 일일 그룹 /출입 인원
    lateinit var smallNameList : Array<String>//그룹이름
    lateinit var smallInOutList : Array<Int>
    lateinit var getData : Response<graph>

    lateinit var charArray : ArrayList<PieEntry>

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
            buildingName2.text = name[which]
            index = which

        }
        dlg.setPositiveButton("닫기", null)
        dlg.show()
    }

    fun drawChart(){
        charArray = ArrayList(smallNameList.size)
        var i=0
        while(i < smallNameList.size){
            charArray.add(PieEntry(smallGroupName.get(smallNameList[i])!!.toFloat(), smallNameList[i]))
            i=i+1
        }
        circle2.animateY(1000, Easing.EasingOption.EaseInOutCubic)
        var dateSet = PieDataSet(charArray,"smallName")
        dateSet.sliceSpace = 3f
        dateSet.selectionShift=5f
        dateSet.colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)

        var data = PieData(dateSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.YELLOW)

        var des = Description()
        des.text = "학과별 출입인원"

        circle2.data = data
        circle2.description = des

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var rootView = inflater.inflate(R.layout.fragment_manager_h, container, false)
        stick = rootView.findViewById(R.id.stick)
        circle2 = rootView.findViewById(R.id.circle2)
        dateView = rootView.findViewById(R.id.dateView)
        gView = rootView.findViewById(R.id.mapView)
        startYear = rootView.findViewById(R.id.startYear)
        startMonth = rootView.findViewById(R.id.startMonth)
        startDate = rootView.findViewById(R.id.startDate)
        endYear = rootView.findViewById(R.id.endYear)
        endMonth = rootView.findViewById(R.id.endMonth)
        endDate = rootView.findViewById(R.id.endDate)
        buildingName2 = rootView.findViewById(R.id.buildingName2)
        post_btn = rootView.findViewById(R.id.post_btn)
        dayStick = rootView.findViewById(R.id.dayStick)

        dateView.text = LocalDate.now().toString()

        gView.onCreate(savedInstanceState)
        la = arguments?.getDouble("la")
        lo = arguments?.getDouble("lo")
        hheader = arguments?.getString("mheader")
        Log.d("hheader", "${hheader}q3qrq3r")
        gView.getMapAsync(this)
        gView.onResume()

        /*stick.clearChart()
        stick.addBar(BarModel("12", 12f, 0xFF56B7F1.toInt()))
        stick.addBar(BarModel("14", 14f, 0xFF56B7F1.toInt()))
        stick.addBar(BarModel("15", 15f, 0xFF56B7F1.toInt()))
        stick.addBar(BarModel("16", 16f, 0xFF56B7F1.toInt()))
        stick.addBar(BarModel("13", 13f, 0xFF56B7F1.toInt()))
        stick.startAnimation()*/


        fun getSmallInOut(res: Response<graph>, smallName : String){
            var i = 0
            var j = 0
            var size1 = res.body()!!.dayList.size
            var size2 = res.body()!!.dayList.get(0).dayGroupList.size
            smallInOutList = Array(size1, {0})

            while(i < size1){
                j=0
                while(j<size2){
                    if(res.body()!!.dayList.get(i).dayGroupList.get(j).group.smallName == smallName){
                        smallInOutList[i] = res.body()!!.dayList.get(i).dayGroupList.get(j).dayGroupTotal
                        break
                    }
                    j=j+1
                }
                i=i+1
            }
            i=0

            stick.clearChart()
            while(i<size1){
                j=0
                while(j<size2){
                    if(res.body()!!.dayList.get(i).dayGroupList.get(j).group.smallName == smallName){
                        stick.addBar(BarModel(dailyDate[i], smallInOutList[i].toFloat()/*res.body()!!.dayList.get(i).dayGroupList.get(j).dayGroupTotal.toFloat()*/, Color.BLACK))
                        break
                    }
                    j = j+1
                }
                i=i+1
            }
            stick.startAnimation()
        }

        fun DrawGraph(res : Response<graph>){
            //lateinit var daily : HashMap<String, Int> //일일 출입인원 / 총 출입 인원
            //lateinit var dailyDate : Array<String> //일일 날짜
            //lateinit var smallGroupName : HashMap<String, Int>// 일일 그룹 /출입 인원
            //lateinit var smallNameList : Array<String>//그룹이름

            var size2 = res.body()!!.dayList.get(0).dayGroupList.size
            var size1 = res.body()!!.dayList.size
            var i = 0
            var j = 0

            dailyDate = Array(size1,{"0"})
            daily = HashMap(size1)

            smallGroupName = HashMap(size2)// 일일 그룹 /출입 인원
            smallNameList = Array(size2,{"0"})


            while(i<size2){
                smallGroupName.put(res.body()!!.dayList.get(0).dayGroupList.get(i).group.smallName, 0)
                smallNameList[i] = res.body()!!.dayList.get(0).dayGroupList.get(i).group.smallName
                i=i+1
            }
            i=0
            while(i < size1){
                j=0
                dailyDate[i] = res.body()!!.dayList.get(i).day
                daily.put(dailyDate[i], res.body()!!.dayList.get(i).daytotal)
                while(j<size2){
                    var name = res.body()!!.dayList.get(i).dayGroupList.get(j).group.smallName
                    var size = res.body()!!.dayList.get(i).dayGroupList.get(j).dayGroupTotal
                    var number = smallGroupName.get(name)
                    number = number!! + size
                    smallGroupName.replace(name, number!!)
                    j=j+1
                }
                i=i+1
            }

            i=0
            dayStick.clearChart()
            while(i<size1){
                dayStick.addBar(BarModel(dailyDate[i], daily.get(dailyDate[i])!!.toFloat(), Color.YELLOW ))
                Log.d("daw", "${res.body()!!.dayList.get(i).daytotal}aadad")
                i=i+1
            }
            drawChart()
        }
        buildingName2.setOnClickListener{
            api.get_buildingID(hheader!!).enqueue(object : Callback<Array<A>> {
                override fun onResponse(call: Call<Array<A>>, response: Response<Array<A>>) {
                    if(!response.body().toString().isEmpty()){
                        dial(response!!)
                    }
                }

                override fun onFailure(call: Call<Array<A>>, t: Throwable) {

                }
            })
        }
        post_btn.setOnClickListener{
            if(startYear.text.toString() != "" && startMonth.text.toString() != "" && startDate.text.toString() != ""
                && endYear.text.toString() != "" && endMonth.text.toString() != "" && endDate.text.toString() != ""){
                    var start = LocalDate.of(Integer.parseInt(startYear.text.toString()), Integer.parseInt(startMonth.text.toString()), Integer.parseInt(startDate.text.toString()))
                    var end = LocalDate.of(Integer.parseInt(endYear.text.toString()), Integer.parseInt(endMonth.text.toString()), Integer.parseInt(endDate.text.toString()))

                    var data = post_Graph(ida[index].toLong() , start.toString(), end.toString())
                    api.postGraph(hheader!!, data).enqueue(object : Callback<graph>{
                        override fun onResponse(call: Call<graph>, response: Response<graph>) {
                            DrawGraph(response!!)
                            getData = response!!
                        }

                        override fun onFailure(call: Call<graph>, t: Throwable) {
                            Log.d("안옴", "${t.message.toString()}awdawd")
                        }
                    })

            }
        }

        circle2.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                getSmallInOut(getData,smallNameList[h?.x?.toInt()!!])
            }

            override fun onNothingSelected() {}
        })

        return rootView

    }

    override fun onMapReady(p0: GoogleMap) {
        val myL = LatLng(la!!,lo!!)

        val marker = MarkerOptions().position(myL).title("위치").snippet("내위치")
        p0.addMarker(marker)
        p0.moveCamera(CameraUpdateFactory.newLatLng(myL))
        p0.moveCamera(CameraUpdateFactory.zoomTo(15f))
        p0.animateCamera(CameraUpdateFactory.newLatLng(myL))
    }
    override fun onStart() {
        super.onStart()
        gView.onStart()
    }
    override fun onStop() {
        super.onStop()
        gView.onStop()
    }
    override fun onResume() {
        super.onResume()
        gView.onResume()
    }
    override fun onPause() {
        super.onPause()
        gView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        gView.onLowMemory()
    }
    override fun onDestroy() {
        gView.onDestroy()
        super.onDestroy()
    }

}