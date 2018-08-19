package com.example.sandy.kotlin_google_places

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.sandy.kotlin_google_places.beans.PlacesBean
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.indiview.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    var lati: Double? = null
    var longi: Double? = null

    var mLoginProgress: ProgressDialog? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoginProgress = ProgressDialog(this)

       /* getting current location by network provider*/
        var lManager = getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
        lManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000.toLong(), 1.toFloat(), object : LocationListener {
            override fun onLocationChanged(location: Location?) {

                lati = location!!.latitude
                longi = location!!.longitude
                tv_lati.text = lati.toString()
                tv_longi.text = longi.toString()
                lManager.removeUpdates(this) //for stop updating location


            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        /* location picker (manually) */
        loc_pin.setOnClickListener {

            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this@MainActivity),
                    1)
        }

        /* Initializing the Retrofit object to hit Google Places */
        get_places.setOnClickListener {

            mLoginProgress!!.setTitle("Gathering information")
            mLoginProgress!!.setMessage("Please wait while we check")
            mLoginProgress!!.setCanceledOnTouchOutside(false)
            mLoginProgress!!.show()

            var r = Retrofit.Builder().
                    baseUrl("https://maps.googleapis.com/").
                    addConverterFactory(GsonConverterFactory.create()).
                    build()

            var api = r.create(PlacesAPI::class.java)

            var call = api.getPlaces("$lati,$longi",sp1.selectedItem.toString())

            call.enqueue(object : Callback<PlacesBean> {
                override fun onResponse(call: Call<PlacesBean>?,
                                        response: Response<PlacesBean>?) {

                    var bean = response!!.body()

                    var list = bean!!.results

                  /*  var temp_list = mutableListOf<String>()

                    for(item in list!!){
                        temp_list.add(item.name+"\n"+item.vicinity)
                    }

                    var adapter = ArrayAdapter<String>(this@MainActivity,
                            android.R.layout.simple_list_item_1, temp_list)*/

                    lview.adapter = object : BaseAdapter() {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                            var inflater = LayoutInflater.from(this@MainActivity)
                            var view = inflater.inflate(R.layout.indiview,null)
                            view.name.text = list!!.get(position).name
                            view.address.text = list!!.get(position).vicinity

                            return view
                        }

                        override fun getItem(position: Int): Any {

                            return 0
                        }

                        override fun getItemId(position: Int): Long {

                            return 0
                        }

                        override fun getCount(): Int {

                            return list.count()
                        }


                    }

                    mLoginProgress!!.dismiss()
                }

                override fun onFailure(call: Call<PlacesBean>?, t: Throwable?) {

                    mLoginProgress!!.dismiss()

                    Toast.makeText(this@MainActivity,"Exception is Raised...",
                            Toast.LENGTH_LONG).show()
                }
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val place = PlacePicker.getPlace(data!!, this)
        lati = place.latLng.latitude
        longi = place.latLng.longitude
        tv_lati.text = lati.toString()
        tv_longi.text = longi.toString()

    }
}
