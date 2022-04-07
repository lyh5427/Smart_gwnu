@file:Suppress("DEPRECATION")

package com.example.smart_gwnu.manager

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.smart_gwnu.R
import kotlinx.android.synthetic.main.activity_manger_main.*
import java.io.IOException
import java.util.*

class mangerMain : AppCompatActivity(){

    private val MY_GRANT : Int = 200
    val MY_LOCATION : Int = 100
    var locationManager : LocationManager?=null
    var currentLocation : String =""
    var latitude : Double? = 0.0
    var longitude : Double? = 0.0
    var mheader : String? = "Bearer "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manger_main)

        mheader += intent.getStringExtra("mheader").toString()
        Log.d("managermain", "${mheader}")
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
                Log.d("check", "현재 내위치 $latitude, $longitude")

                var mGeocoder = Geocoder(applicationContext, Locale.KOREAN)
                var mResultList: List<Address>? = null
                try{
                    mResultList = mGeocoder.getFromLocation(latitude!!, longitude!!, 1)
                }catch( e:IOException){
                    e.printStackTrace()
                }
            }
        }

        b_nav.run{
            setOnNavigationItemSelectedListener { a -> // 메뉴바 선택
                when(a.itemId){
                    R.id.menu_home ->{
                        getCurrentLoc()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame, manager_h()
                            .apply{arguments = Bundle().
                                apply{
                                    putDouble("la", latitude!!)
                                    putDouble("lo", longitude!!)
                                    putString("mheader", mheader!!.toString())
                                }
                            }).commit()
                    }
                    R.id.menu_board ->{
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame, manager_b().
                        apply{arguments = Bundle().
                        apply{
                            putString("mheader", mheader!!.toString())
                        }
                        }).commit()
                    }
                    R.id.menu_visit ->{
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame, manager_v().
                        apply{arguments = Bundle().
                        apply{
                            putString("mheader", mheader!!.toString())
                        }
                        }).commit()
                    }
                    R.id.menu_set ->{
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame, manager_s().
                        apply{arguments = Bundle().
                        apply{
                            putString("mheader", mheader!!.toString())
                        }
                        }).commit()
                    }
                }
                true
            }
            selectedItemId= R.id.menu_home
        }
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
            }
        }
    }
}