package com.example.smart_gwnu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_user_home.*

class user_home : Fragment(), OnMapReadyCallback {

    private var la : Double? = null
    private var lo : Double? = null
    var uflag : Int = 3
    private lateinit var gView : MapView
    private lateinit var blueText : TextView
    private lateinit var runImage : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_user_home, container, false)
        gView = rootView.findViewById(R.id.mapView2)
        gView.onCreate(savedInstanceState)
        la = arguments?.getDouble("la")
        lo = arguments?.getDouble("lo")
        uflag = Integer.parseInt(arguments?.getInt("flag").toString())
        Log.d("플래그좀보소", "$uflag wadwad")

        blueText = rootView.findViewById(R.id.blueText)
        runImage = rootView.findViewById(R.id.runImage)

        if(uflag === 1){
            blueText.text = "블루투스가 켜져있습니다."
            runImage.setImageResource(R.drawable.user_running)
        }
        else{
            blueText.text = "블루투스를 켜주세요"
            runImage.setImageResource(R.drawable.user_notrunnig)
        }

        gView.getMapAsync(this)
        gView.onResume()
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