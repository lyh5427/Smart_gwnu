@file:Suppress("DEPRECATION")

package com.example.smart_gwnu.user

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import org.altbeacon.beacon.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.smart_gwnu.restful.APIS
import com.example.smart_gwnu.Information
import com.example.smart_gwnu.R
import com.example.smart_gwnu.restful.R_SvaeInfo
import com.example.smart_gwnu.restful.Saveinfo
import kotlinx.android.synthetic.main.activity_user_main.*
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class UserMain : AppCompatActivity(), BeaconConsumer {
    //블루투스 제어 변수
    private val REQUEST_ENABLE_BT=1
    private var bluetoothAdapter: BluetoothAdapter? = null

    //구글맵 제어 변수
    private val MY_LOCATION : Int = 100
    var locationManager : LocationManager?=null
    var latitude : Double? = 0.0
    var longitude : Double? = 0.0
    var BLUE : Int = 100

    //비콘제어 변수
    private var TAG : String? = "::MonitoringActivity::"
    private var TAG2 : String? = "::RangingActivity::"
    private var beaconManager : BeaconManager? = null
    var UUID : String? = null
    var major : String? = null
    var minor : String? = null
    val api = APIS.create()
    var flag : Int = 3 // 비콘 서비스 실행 flag 0 x 1 O
    var minorList = ArrayList<String>()
    var outsideBeaconDetected = false
    var insideBeaconDetected = false
    var inOutFlag : Boolean = false // 바깥에 있는상태 true  안쪽에 있는상태 false
    var outInFlag : Boolean = false //               false                true

    //헤더
    var uheader :String =""

    //notice관련
    var noticeIn : Intent? = null
    var pen : PendingIntent? = null
    var builder : NotificationCompat.Builder? = null
    lateinit var id : String
    var info : String? = null

    fun bluetoothOnOff(){
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }else{
            if (bluetoothAdapter?.isEnabled == false) { // 블루투스 꺼져 있으면 블루투스 활성화
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                flag = 1
            } else{ // 블루투스 켜져있으면 블루투스 비활성화
            }
        }
    }
    fun set_notice(Id : String){
        noticeIn = Intent(this, Information::class.java)
        noticeIn!!.putExtra("header", uheader)
        noticeIn!!.putExtra("building", Id)
        //noticeIn!!.putExtra("header", uheader!!.toString())

        pen = PendingIntent.getActivity(this, 0, noticeIn, PendingIntent.FLAG_UPDATE_CURRENT)
        builder = NotificationCompat.Builder(this, "MY_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("방문기록 저장")
            .setContentText("정보를 확인 하시려면 알림바를 눌러주세요")
            .setContentIntent(pen)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)
        //notice정보들
        uheader = "Bearer "+intent.getStringExtra("header").toString()//"Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2MzgxMDUzNjYsImlhdCI6MTYzODA4NzM2NiwianRpIjoiMDEwNzIzNDIwNTcifQ.Sz45tdm4sP0u__rpx6W-QxKUCfLhTTMLn7281HdSJMw7uS12aMZfwK-QHvCISwq8iLQj7FZq-f-P50u_hf82fg"// + intent.getStringExtra("header")//"Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2Mzc3NjczNTcsImlhdCI6MTYzNzc0OTM1NywianRpIjoiMDEwLTAwMDAtMDAwMCJ9.FhLh5vDzr3ATR6iWHHD0SZNXLhjJcrYFSuTJOf3u-_cE69OnIikZRMPMDKpEtpe1lVUQ2wQQuScitkc2kclWGQ"



        //블루투스 제어
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter!=null){
            if(bluetoothAdapter?.isEnabled==false){
                flag = 1
                bluetoothOnOff()
            } else{
                flag = 1
            }
        }

        fun getLatLng() : Location {
            var currentLatLng : Location? = null
            if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), this.MY_LOCATION)
                getLatLng()
            }
            else{
                val locationProvider = LocationManager.NETWORK_PROVIDER
                currentLatLng = locationManager?.getLastKnownLocation(locationProvider)
            }
            return currentLatLng!!
        }

        fun getCurrentLoc(){
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val userLocation : Location = getLatLng()
            if(userLocation != null){
                latitude = userLocation.latitude
                longitude = userLocation.longitude

                var mGeocoder = Geocoder(applicationContext, Locale.KOREAN)
                var mResultList: List<Address>? = null
                try{
                    mResultList = mGeocoder.getFromLocation(latitude!!, longitude!!, 1)
                }catch( e: IOException){
                    e.printStackTrace()
                }
            }
        }

        b_nav2.run{//네비게이션바 아이템 선택마다 다른 프래그먼트 호출
            setOnNavigationItemSelectedListener { a -> // 메뉴바 선택
                when (a.itemId) {
                    R.id.menu_home -> {
                        getCurrentLoc()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame2, user_home()
                            .apply{arguments = Bundle().
                            apply{
                                putDouble("la", latitude!!)
                                putDouble("lo", longitude!!)
                                putInt("flag", flag)
                            }
                            }).commit()
                    }
                    R.id.menu_board -> {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.frame2, user_board()
                                .apply { arguments = Bundle().apply {
                                    putString("mheader", uheader!!)
                                }
                                }).commit()
                    }
                    R.id.menu_visit -> {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.frame2, user_visit()
                                .apply { arguments = Bundle().apply {
                                    putString("mheader", uheader!!)
                                }
                                }).commit()
                    }
                    R.id.menu_set -> {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.frame2, user_set()
                                .apply { arguments = Bundle().apply {
                                    putString("mheader", uheader!!)
                                }
                                }).commit()
                    }
                }
                true
            }

                selectedItemId = R.id.menu_home
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED //권한 받기
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            var permission = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.INTERNET
            )
            //bleOF()
            ActivityCompat.requestPermissions(this, permission, this.BLUE)
        }
        else{//권한이 이미 있으면 이부분
            //bleOF()
            beaconManager = BeaconManager.getInstanceForApplication(this)
            beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
            beaconManager!!.bind(this)
        }
    }

    override fun onDestroy() {//비콘 서비스 종료
        super.onDestroy()
        beaconManager!!.unbind(this)
        flag = 0
    }

    override fun onBeaconServiceConnect() { //비콘매니저가 바인드되면 실행
        val mRegion = Region(packageName, null, null, null)



        minorList.clear()
        beaconManager!!.removeAllMonitorNotifiers()
        beaconManager!!.addRangeNotifier ( object : RangeNotifier {
            override fun didRangeBeaconsInRegion( beacons: MutableCollection<Beacon>?, region: Region?) {//startTangingBraconsInRagion메소드가 호출되면 실행됨.

                if (beacons != null) {
                    for (beacon in beacons) {
                        if (beacon.distance < 3) {
                            UUID = beacon.id1.toString()
                            major = beacon.id2.toString()
                            minor = beacon.id3.toString()
                            if (UUID == "e2c56db5-dffb-48d2-b060-d0f5a71096e0") {
                                if(beacons.size >= 2 ){
                                    minorList.clear()
                                }
                                if(minorList.size == 0 && !inOutFlag && !outInFlag) {
                                    if(minor == "1"){ //안쪽에 있는상태
                                        inOutFlag = true
                                        outInFlag = false
                                    }else if(minor == "42996"){ // 바깥에 있는상태
                                        inOutFlag = false
                                        outInFlag = true
                                    }
                                    minorList.add(minor!!)
                                }
                                else if(outInFlag){
                                    if(minor == "1"){ //바깥 안쪽 비콘 리스트에 넣음
                                        minorList.add(minor!!)
                                    }
                                }
                                else if(inOutFlag){
                                    if(minor == "42996"){ //안쪽 바깥비콘 리스트에 넣임
                                        minorList.add(minor!!)
                                    }
                                }
                            }
                        }
                    }

                    if(beacons.size >= 2 ){

                    }
                    else if(outInFlag) {
                        if(minorList.contains("1")){
                            var date: LocalDateTime = LocalDateTime.now()
                            val data = Saveinfo(UUID.toString(), major.toString(), "42996", date.toString(), true) //출입등록 요청 코드
                            postRecord(data,1)
                        }
                    }
                    else if(inOutFlag) {
                        if(minorList.contains("42996")){
                            var date: LocalDateTime = LocalDateTime.now()

                            val data = Saveinfo(UUID.toString(), major.toString(), "42996", date.toString(), false)
                            postRecord(data, 0)
                        }
                    }
                }
            }
        })


        beaconManager!!.addMonitorNotifier(object : MonitorNotifier { //모니터링메소드가 호출되면 실행

            override fun didEnterRegion(region: Region?) {//1개
                beaconManager!!.startRangingBeaconsInRegion(mRegion)


            }

            override fun didExitRegion(region: Region?) {
                beaconManager!!.stopRangingBeaconsInRegion(mRegion)
            }

            override fun didDetermineStateForRegion(state: Int, region: Region?) {
                beaconManager!!.startRangingBeaconsInRegion(mRegion)
            }
        })

        try { //예외처리
            beaconManager!!.startMonitoringBeaconsInRegion(
                mRegion
            )
        } catch (ignored: RemoteException) { }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_LOCATION){
            if(grantResults.size > 0){
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        System.exit(0)
                    }
                }
                beaconManager = BeaconManager.getInstanceForApplication(this)
                beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
                beaconManager!!.bind(this)
            }
        }
    }

    fun postRecord(data : Saveinfo, inout : Int) {
        api.userVisit(uheader!!, data).enqueue(object : Callback<R_SvaeInfo> {
            override fun onResponse(call: Call<R_SvaeInfo>, response: Response<R_SvaeInfo>) {
                 //-------------이부분
                if(outInFlag){
                    set_notice(response.body()!!.buildingid.toString())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel_id = "MY_channel" // 알림을 받을 채널 id 설정
                        val channel_name = "채널이름" // 채널 이름 설정
                        val descriptionText = "알림 채널" // 채널 설명글 설정
                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                        val channel = NotificationChannel(
                            channel_id,
                            channel_name,
                            importance
                        ).apply {
                            description = descriptionText
                        }
                        val notificationManager: NotificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)
                        notificationManager.notify(1002, builder?.build())
                        minorList.clear()
                    }
                }
                if(inout == 1){
                    inOutFlag = true
                    outInFlag = false
                }else{
                    inOutFlag = false
                    outInFlag = true
                }
            }

            override fun onFailure(call: Call<R_SvaeInfo>, t: Throwable) {

            }
        })
    }
}