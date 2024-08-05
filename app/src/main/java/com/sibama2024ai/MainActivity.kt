package com.sibama2024ai

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.google.android.gms.location.FusedLocationProviderClient
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineString
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private val originalColor = mutableMapOf<String, Int>()

    private val apiUrl = "https://sibama2022.griyasolusiindonesia.com/api/drainase"
    private val fileNamealldrainases = "alldrainases.json"
    private val fileNameblimbing = "jsonblimbing.json"
    private val fileNamekkandang = "jsonkkandang.json"
    private val fileNameklojen = "jsonklojen.json"
    private val fileNamelowokwaru = "jsonlowokwaru.json"
    private val fileNamesukun = "jsonsukun.json"
    private val fileNameGenangan = "jsongenangan.json"


    private lateinit var strfilejsonkecamatan: String
    private var JSON_STRING_KECAMATAN: String? = null
    private var JSON_STRING_GENANGAN: String? = null
    private val DEFAULT_LATLNG = LatLng(-7.9927011, 112.6284465)
    private lateinit var str_id_genangan: String
    private lateinit var str_id_saluran: String
    private lateinit var strnama_jalan: String
    private lateinit var status_limpasan: String
    private lateinit var strstscctv: String
    private lateinit var stskecamatan: String
    private lateinit var strlatitude: String
    private lateinit var strlongitude: String
    private lateinit var stream_id: String
    private lateinit var str_r24: String

    private var lastClickedFeatureKecamatan: GeoJsonFeature? = null
    private var lastClickedFeature: GeoJsonFeature? = null

    private var layerKecamatan: GeoJsonLayer? = null
    private var layerGenangan: GeoJsonLayer? = null


    //... tambahkan layer map kecamatan lain
    private var layerMerge: GeoJsonLayer? = null
    private var lastClickedFeatureMerge: GeoJsonFeature? = null


    private lateinit var str_namakecamatan: String //

    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 3000

    private lateinit var map: GoogleMap
    //private lateinit var bottomSheetBehavior: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var navigationView: NavigationView

    private lateinit var radioGroupKecamatan: RadioGroup

    private lateinit var radio_blimbing: RadioButton
    private lateinit var radio_sukun: RadioButton
    private lateinit var radio_lowokwaru: RadioButton
    private lateinit var radio_kedkandang: RadioButton
    private lateinit var radio_klojen: RadioButton


    private var precipitationDay1 : Double = 0.0
    private var precipitationDay2 : Double = 0.0
    private var precipitationDay3 : Double = 0.0
    private var dateprecipitationDay1: String =""
    private var dateprecipitationDay2: String =""
    private var dateprecipitationDay3: String =""

    //progress untuk map
    private lateinit var progressDialog2: ProgressDialog
    private lateinit var progressDialog: AlertDialog
    private val delayMillis: Long =5000 // Delay dalam milidetik

    //property BOTTOM SHEET
    private var nama_jalan: String? = null
    private var sisi:String? = null
    private lateinit var panjang:String
    private var arah: String? = null
    private var tipe: String? = null
    private var kondisi_fisik:String? = null
    private var dimensi: String? = null
    private var foto: String? = null
    //private var status_limpasan: String? = null
    private var created_at: String? = null

    private var uri_foto1: String? = null
    private var uri_foto2: String? = null

    private lateinit var tstsnamajalan: TextView
    private lateinit var tstslajur: TextView
    private lateinit var tstspanjang: TextView
    private lateinit var tstskecamatan: TextView
    private lateinit var tstsarah: TextView
    private lateinit var tststipe: TextView
    private lateinit var tstskondisifisik: TextView
    private lateinit var tstsgenangan: TextView
    private lateinit var tststglupdate: TextView

    private lateinit var bfoto1: ImageButton
    private lateinit var bfoto2:ImageButton
    private lateinit var btnOpenWebSibama:ImageView

    private var isSukunChecked = false
    private var isBlimbingChecked = false
    private var isKlojenChecked = false
    private var isLWaruChecked = false
    private var isKKandangChecked = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permisiall()

        setContentView(R.layout.activity_main)

        //--- cek internet ----------------------
        checkInternetConnection()


        //------- Navigation Drawer (Side Sheet) -----------------------------------
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.openDrawer(GravityCompat.END)


        val fab: FloatingActionButton = findViewById(R.id.fab_filter)

        // Initialize NavigationView
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener {
            // Handle navigation item click here
            // For example, close the side sheet
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }


        fab.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)

        }
        //--------------------------------------------------------
        tstsnamajalan = findViewById(R.id.stsnamajalan)
        tstslajur = findViewById(R.id.stslajur)
        tstspanjang = findViewById(R.id.stspanjang)
        tstsarah = findViewById(R.id.stsarah)
        tststipe = findViewById(R.id.ststipe)
        tstskondisifisik = findViewById(R.id.stskondisifisik)
        tststglupdate = findViewById(R.id.ststglupdate)
        tstsgenangan = findViewById(R.id.stsgenangan)
        tstskecamatan = findViewById(R.id.stskecamatan)
        btnOpenWebSibama= findViewById(R.id.floatingImageView)

        bfoto1 = findViewById(R.id.btn_foto1)
        bfoto2 = findViewById(R.id.btn_foto2)


        // Initialize Map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize BottomSheet
        //val bottomSheet = findViewById(R.id.info_bottom_sheet_layout)
        val bottomSheet: LinearLayout = findViewById(R.id.info_bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //----------- PRECIPITATION DATA ---------------------------------
        //sinkrondata()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val precipitationData = listOf(
            PrecipitationData(dateprecipitationDay1, precipitationDay1),
            PrecipitationData(dateprecipitationDay2, precipitationDay2),
            PrecipitationData(dateprecipitationDay3, precipitationDay3)
            // Tambahkan data lainnya di sini
        )

        val adapter = PrecipitationAdapter(precipitationData)
        recyclerView.adapter = adapter

        //----------- END OF PRECIPITATION DATA ---------------------------------


        //---------------- Handle CheckBox and Buttons -------------------------------
        val sinkronbutton: Button = findViewById(R.id.sinkron_button)
        val resetButton: Button = findViewById(R.id.reset_button)
//        val refreshButton: Button = findViewById(R.id.refresh_button)
        val progressBar: LinearProgressIndicator = findViewById(R.id.progress_bar)
        radioGroupKecamatan = findViewById(R.id.radioGroupKecamatan)


        //getJSONDrainaseTitikGenangan()


        // Initialize the fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        btnOpenWebSibama.setOnClickListener {
            val url = "https://sibama2022.griyasolusiindonesia.com/peta-drainase"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        resetButton.setOnClickListener {
            //clear all Map Kecamatan dan Tampilkan Titik Genangan saja

            drawerLayout.closeDrawer(GravityCompat.END)
            CheckboxDefaultState()
            radioGroupKecamatan.clearCheck()
            clearMap()
            if (this::map.isInitialized) {
                onMapReady(map)

            }

        }

        sinkronbutton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            progressBar.postDelayed({
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Data Synchronized", Toast.LENGTH_SHORT).show()
            }, 4000)


            //fetchAndSaveGeoJsonData()
            fetchAndSaveAllGeoJsonDataKecamatan()
            getJSONMeteo()

        }





        radioGroupKecamatan.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {

                // Get the selected RadioButton
                val selectedRadioButton = findViewById<RadioButton>(checkedId)

                str_namakecamatan = selectedRadioButton.text.toString()
                //showToast(this@MainActivity, str_namakecamatan)
                drawerLayout.closeDrawer(GravityCompat.END)
                clearMap()
                showProgressDialogWithDelay()

                val jsonFileName = when (str_namakecamatan) {
                    "Kec. Blimbing" -> fileNameblimbing
                    "Kec. Sukun" -> fileNamesukun
                    "Kec. Klojen" -> fileNameklojen
                    "Kec. Lowokwaru" -> fileNamelowokwaru
                    "Kec. Kedungkandang" -> fileNamekkandang
                    else -> null
                }

                jsonFileName?.let {
                    strfilejsonkecamatan = it
                    //getJSONDrainaseTes()
                    getJSONDrainaseFromFile()
                    runMapKecamatan(map)
                    getcurrentuserloc()
                }

            }
        }


        bfoto1.setOnClickListener {
            //showToast(this,uri_foto1!!)
            val intent = Intent(this, TampilFotoActivity::class.java)
            intent.putExtra("IMAGE_URL", uri_foto1)
            startActivity(intent)
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out)
        }

    }

    fun sinkrondata(){
        val progressBar: LinearProgressIndicator = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        progressBar.postDelayed({
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Data Synchronized", Toast.LENGTH_SHORT).show()
        }, 4000)

        fetchAndSaveAllGeoJsonDataKecamatan()
        //fetchAndSaveGeoJsonData()
        getJSONGenanganFile()
        getJSONMeteo()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)

        // Add a marker and move the camera
        map.addMarker(MarkerOptions().position(DEFAULT_LATLNG).title("Dinas PUPRPKP"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG, 13.53f))

        // Tambahkan marker pada peta
        val markerOptions = MarkerOptions().position(DEFAULT_LATLNG).title("Dinas PUPRPKP")
        val marker = map.addMarker(markerOptions)



        if (!JSON_STRING_GENANGAN.isNullOrEmpty()) {
            // JSON_STRING_GENANGAN contains valid data, proceed with processing
            runTitikGenangan()
        } else {
            // JSON_STRING_GENANGAN is empty or null, handle accordingly
            Toast.makeText(this, "No data available for Titik Genangan", Toast.LENGTH_LONG).show()
        }


        map.setOnMapClickListener { latLng ->
            // Sembunyikan bottom sheet saat mengklik peta
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN}


        //reloadGeoJsonLayers()
        getcurrentuserloc()

    }

    private fun getcurrentuserloc(){

        // Enable My Location layer
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        val pointIcon = BitmapDescriptorFactory.fromResource(R.drawable.userloc2)

        //val customMarker = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.custom_marker))

        map.isMyLocationEnabled = true
        // Get the current location of the device and set the position of the map
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.53f))
                map.addMarker(MarkerOptions().position(currentLatLng).icon(pointIcon).title("Posisi Anda"))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, call onMapReady again to enable location layer
                onMapReady(map)
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission required to show your location on the map", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun CheckboxDefaultState(){
        isSukunChecked = false
        isKlojenChecked =false
        isBlimbingChecked =false
        isKKandangChecked = false
        isLWaruChecked =false


    }

    //----- progress bar untuk map

    private fun showProgressDialogWithDelay() {
        // Create and show the ProgressDialog
        progressDialog2 =
            ProgressDialog.show(this, "Proses Ambil Data", "Mohon tunggu...", true, false)

        // Delay for 10 seconds
        val delayMillis = 4000 // 10 seconds
        Handler().postDelayed({ // Dismiss the ProgressDialog after the delay
            if (progressDialog2 != null && progressDialog2.isShowing) {
                progressDialog2.dismiss()
            }
        }, delayMillis.toLong())
    }

    //--------------------------------------------------

    private fun checkInternetConnection() {
        val isConnected = isInternetAvailable(this)
        if (isConnected) {
            // Terhubung ke internet
            Toast.makeText(this, "Telah Terhubung ke internet", Toast.LENGTH_LONG).show()
            sinkrondata()


        } else {
            // Tidak terhubung ke internet
            AlertDialog.Builder(this)
                .setTitle("Cek Internet")
                .setMessage("Tidak terhubung ke internet.\nAplikasi ini membutuhkan koneksi internet untuk menampilkan data drainase.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()

            // Jadwalkan pengecekan ulang
            handler.postDelayed({ checkInternetConnection() }, checkInterval)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private fun permisiall() {
        val ALL_PERMISSIONS = 101
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)
    }

    //-- untuk pembulatan bilangan desimal
 /*   fun String.roundToDecimals(decimals: Int): Double {
        return this.toDoubleOrNull()?.let {
            "%.${decimals}f".format(it).toDouble()
        } ?: 0.0
    }*/

    fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToInt() / factor
    }

    // Fungsi untuk memuat gambar menggunakan Glide
    private fun loadImage(uri: String, imageView: ImageView) {
        Glide.with(this)
            .load(uri)
            .apply(RequestOptions().placeholder(R.drawable.imagekosong).error(R.drawable.imagekosong))
            .into(imageView)
    }


    //-------------  Data Meteo -----------
    //ekstrak value JSON API Meteo
    private fun getJSONMeteo() {
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(5, TimeUnit.SECONDS)
        builder.readTimeout(5, TimeUnit.SECONDS)
        builder.writeTimeout(5, TimeUnit.SECONDS)
        val client = builder.build()
        val request = Request.Builder()
            .url(konfigurasi.URL_GET_24METEO)
            .build()
        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string()
            //showToast(this, responseString.toString());
            showr24(responseString)

        } catch (e: IOException) {
            //Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            showToast(this,"MOHON CEK KONEKSI INTERNET")
            e.printStackTrace()
        }
    }

    private fun showr24(jsonString: String?) {
        try {
            // Parsing JSON string menjadi JSONObject
            val jsonObject = JSONObject(jsonString!!)

            val daily = jsonObject.getJSONObject("daily")
            val dailyTimeArray = daily.getJSONArray("time")
            val dailyPrecipitationSumArray = daily.getJSONArray("precipitation_sum")

            //showToast(str_r24);

            //ekstrak nilai precip sum 3 hari ke depan
            val precipitationSumList = mutableListOf<Double>()
            for (i in 0 until dailyPrecipitationSumArray.length()) {
                val value = dailyPrecipitationSumArray.getDouble(i)
                precipitationSumList.add(value)
            }

            val percDay1 = precipitationSumList[0].toString()
            val percDay2 = precipitationSumList[1].toString()
            val percDay3 = precipitationSumList[2].toString()

            precipitationDay1 = try {
                percDay1.toDouble()
            } catch (e: NumberFormatException) {
                0.0 // Nilai default jika konversi gagal
            }

            precipitationDay2 = try {
                percDay2.toDouble()
            } catch (e: NumberFormatException) {
                0.0 // Nilai default jika konversi gagal
            }
            precipitationDay3 = try {
                percDay3.toDouble()
            } catch (e: NumberFormatException) {
                0.0 // Nilai default jika konversi gagal
            }

            //ekstrak nilai array tanggal (time)
            val timeList = mutableListOf<String>()
            for (i in 0 until dailyTimeArray.length()) {
                val value = dailyTimeArray.getString(i)
                timeList.add(value)
            }
            val tgl1 = timeList[0]
            val tgl2 = timeList[1]
            val tgl3 = timeList[2]

            //ubah format tanggal ke ddd, dd-mm-yyy
            val formatterInput = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formatterOutput = DateTimeFormatter.ofPattern("EEE, dd-MM-yyyy")
            //simpan di variabel masing2 tgl
            val date1 = LocalDate.parse(tgl1, formatterInput)
            val formattedDate1 = date1.format(formatterOutput)
            val date2 = LocalDate.parse(tgl2, formatterInput)
            val formattedDate2 = date2.format(formatterOutput)
            val date3 = LocalDate.parse(tgl3, formatterInput)
            val formattedDate3 = date3.format(formatterOutput)
            //tampilkan
            dateprecipitationDay1 =formattedDate1
            dateprecipitationDay2 =formattedDate2
            dateprecipitationDay3 =formattedDate3

        } catch (e: JSONException) {
            //showToast(e.getMessage().toString());
            showToast(this,"Gagal Ambil Data Percipitation")
        }
    }
    /* ---- end data meteo*/

    //------------ start of JSON Titik Genangan ---------------------------
    private fun getJSONDrainaseTitikGenangan() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(10, TimeUnit.SECONDS)

        val client = builder.build()
        val request = Request.Builder()
            .url(konfigurasi.URL_GET_GENANGAN)
            .addHeader("x-api-key", konfigurasi.x_api_key)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string()

            // Cek apakah responseString tidak null dan tidak kosong
            if (!responseString.isNullOrEmpty()) {

                JSON_STRING_GENANGAN = responseString
            } else {
                Toast.makeText(this, "Response is empty or null", Toast.LENGTH_LONG).show()
            }

            //showToast(this,"tes")
        } catch (e: IOException) {
            Toast.makeText(this, "Error Get GeoJSON Titik Genangan: " + e.message  + "\nInternet tidak stabil", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun addLayerTitikGenangan(layer: GeoJsonLayer) {

        layer.features.forEach { feature ->
            // Check if the necessary properties exist
            val id = feature.getProperty("id")
            val namaJalan = feature.getProperty("nama_jalan")

            if (id != null && namaJalan != null) {
                strnama_jalan = namaJalan

                // Get the icon for the feature
                val pointIcon = BitmapDescriptorFactory.fromResource(R.drawable.alert2)

                // Create a new point style
                val pointStyle = GeoJsonPointStyle().apply {
                    icon = pointIcon
                    title = "Lokasi: $strnama_jalan"
                    snippet = "CCTV: ${if (feature.getProperty("stream_id").isNullOrEmpty() || feature.getProperty("stream_id") == "-") "Not Available" else "Available"}"
                }

                // Assign the point style to the feature
                feature.pointStyle = pointStyle
            }
        }

    }

    private fun runTitikGenangan(){
        try {
            // val obj = JSONObject(JSON_STRING_GENANGAN!!)

            val jsonString = JSON_STRING_GENANGAN ?: ""
            val obj = JSONObject(JSON_STRING_GENANGAN!!)
            layerGenangan = GeoJsonLayer(map, obj)

            // Set the style of the feature
            addLayerTitikGenangan(layerGenangan!!)

            layerGenangan?.addLayerToMap()
            // Set a listener for feature click
            layerGenangan?.setOnFeatureClickListener { featureGenangan ->
                strnama_jalan = featureGenangan.getProperty("nama_jalan")
                str_id_genangan = featureGenangan.getProperty("id")
                stream_id = featureGenangan.getProperty("stream_id")

                if (stream_id.isNullOrEmpty() || stream_id == "-") {
                    strstscctv="Not Available"

                } else {
                    strstscctv="Available"
                    //showToast(this, "$strstscctv\n$stream_id")

                    // Call WebView CCTV
                    Intent(this, TitikGenangan::class.java).apply {
                        putExtra(konfigurasi.GET_ID_GENANGAN, str_id_genangan)
                        putExtra(konfigurasi.GET_STREAM_ID, stream_id)
                        startActivity(this)
                    }
                }
                //showToast(this,"CCTV : " + strstscctv)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun clearMapGenangan() {
        layerGenangan?.removeLayerFromMap()
        lastClickedFeature = null // Reset the last clicked feature
    }


    //------------------ end of Titik Genangan -------------------------------


    //vvvvvvvvvvvvvvv start of JSON Drainase SUKUN vvvvvvvvvvvvvvv
    private fun runMapKecamatan(@NonNull googleMap: GoogleMap){
        map = googleMap

        try {
            val jsonStringKecamatan = JSON_STRING_KECAMATAN ?: ""

            if (JSON_STRING_KECAMATAN != null) {
                val obj = JSONObject(jsonStringKecamatan)

                layerKecamatan = GeoJsonLayer(map, obj)

                // Set the style of the feature
                layerKecamatan?.defaultLineStringStyle

                addLayerMapKecamatan(layerKecamatan!!)

                layerKecamatan?.addLayerToMap()

                // Set a listener for feature click
                layerKecamatan?.setOnFeatureClickListener { feature ->
                    strnama_jalan = feature.getProperty("nama_jalan")
                    str_id_genangan = feature.getProperty("id")
                    val stslimpasan:String = feature.getProperty("status_limpasan")
                    feature?.let {
                        if (feature.geometry.geometryType == "LineString") {
                            val lineStringStyle = GeoJsonLineStringStyle().apply {
                                color = Color.rgb(0, 100, 249)
                            }

                            (feature as GeoJsonFeature).apply {
                                // Reset previous clicked feature color
                                lastClickedFeatureKecamatan?.let {
                                    val lastFeatureId = it.getProperty("id") ?: return@let
                                    val resetStyle = GeoJsonLineStringStyle().apply {
                                        color = originalColor[lastFeatureId] ?: Color.rgb(0, 202, 249)  // Warna asli kondisi sebelum diklik
                                        width = 3.0f
                                        //color = Color.rgb(0, 202, 249) // Original color
                                    }
                                    it.lineStringStyle = resetStyle
                                }

                                // Set new clicked feature color
                                lineStringStyle.let {
                                    this.lineStringStyle = it
                                    lastClickedFeatureKecamatan = this

                                }

                                showBottomSheet(this)
                            }
                        }

                        // Ambil data feature drainase
                        sisi = feature.getProperty("sisi")

                        panjang = feature.getProperty("panjang")//.roundToDecimals(1).toString()
                        val panjangString = panjang?.toDoubleOrNull()?.roundToDecimals(1)?.toString() ?: "0.0"

                        str_id_saluran = feature.getProperty("kode_saluran")
                        arah = feature.getProperty("arah")
                        tipe = feature.getProperty("tipe")
                        kondisi_fisik = feature.getProperty("kondisi_fisik")
                        created_at = feature.getProperty("tahun_survey")
                        foto = feature.getProperty("foto")
                        dimensi = feature.getProperty("dimensi")
                        stskecamatan = feature.getProperty("kecamatan")

                        // Tampilkan di bottomsheet
                        tstsnamajalan.text = "$strnama_jalan ($str_id_saluran)"
                        tstslajur.text = sisi
                        tstspanjang.text = "$panjangString meter"
                        tstsarah.text = arah

                        //
                        tststipe.text = tipe
                        tstskondisifisik.text = kondisi_fisik
                        tstsgenangan.text = stslimpasan
                        tststglupdate.text = created_at
                        tstskecamatan.text = stskecamatan

                        // URI Foto 1
                        uri_foto1 = "${konfigurasi.URL_ASSET_FOTO}2023/foto/$foto"
                        uri_foto1 = foto
                        loadImage(uri_foto1!!, bfoto1)

                        // URI Foto 2
                        uri_foto2 = "${konfigurasi.URL_ASSET_FOTO}2023/dimensi/$dimensi"
                        uri_foto2 = dimensi
                        loadImage(uri_foto2!!, bfoto2)



                    } ?: showToast(this, "Data tidak ada")
                }

            } else {
                // Tangani kasus di mana JSON_STRING_KECAMATAN null
                //Log.e("Error", "JSON_STRING_KECAMATAN is null")
                showToast(this,"JSON_STRING_KECAMATAN is null")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun addLayerMapKecamatan(layer: GeoJsonLayer) {
        // Iterate over all the features stored in the layer
        status_limpasan=""
        val boundsBuilder = LatLngBounds.Builder()

        for (feature in layer.features) {
            // Check if the object exist
            if (feature.getProperty("id") != null) {

                strnama_jalan = feature.getProperty("nama_jalan")
                status_limpasan = feature.getProperty("status_limpasan")
                //--------------- hitung limpasan
                /* val dblpanjang: Double = feature.getProperty("panjang")?.toDoubleOrNull() ?: 0.0
                 val dblkeliling_penampung: Double = feature.getProperty("keliling_penampung")?.toDoubleOrNull() ?: 0.0
                 val dblluas_penampung: Double = feature.getProperty("luas_penampung")?.toDoubleOrNull() ?: 0.0
                 val dblslope: Double = feature.getProperty("slope")?.toDoubleOrNull() ?: 0.0
                 val dblsum_ca_m2: Double = feature.getProperty("sum_ca_m2")?.toDoubleOrNull() ?: 0.0

                 val drainase = Drainase(slope = dblslope, luas_penampung = dblluas_penampung,
                     keliling_penampung = dblkeliling_penampung, panjang = dblpanjang, sum_ca_m2 = dblsum_ca_m2)

                 val statusLimpasan = runBlocking {
                     fetchAndCalculate(drainase)
                 }
                 status_limpasan = statusLimpasan*/
                //-------------------------------------------------------------------------------


                // Create a new point style
                val lineStringStyle = GeoJsonLineStringStyle().apply {
                    color = when (status_limpasan) {
                        "MELIMPAH" ->  Color.rgb(255, 0, 50)
                        "RAWAN MELIMPAH" -> Color.rgb(255, 165, 0)
                        else -> Color.rgb(0, 202, 249) // default color
                    }
                    width = if (status_limpasan == "-") 3.0f else 3.0f
                    isClickable = true
                    isGeodesic = true
                }
                // Simpan warna asli sebelum mengubahnya menggunakan ID atau properti unik dari fitur
                val featureId = feature.getProperty("id") ?: UUID.randomUUID().toString() // Contoh menggunakan ID atau UUID
                originalColor[featureId] = lineStringStyle.color


                // Assign the point style to the feature
                feature.lineStringStyle = lineStringStyle

                val geometry = feature.geometry
                if (geometry is GeoJsonLineString) {
                    for (latLng in geometry.coordinates) {
                        boundsBuilder.include(latLng)
                    }
                }
            }
        }
        val bounds = boundsBuilder.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun clearMapKecamatan() {

        layerKecamatan?.removeLayerFromMap()
        lastClickedFeatureKecamatan = null // Reset the last clicked feature
    }



    private fun showBottomSheet(feature: GeoJsonFeature) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    // ^^^^^^^^^^^^ end of Sukun ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    private fun clearMap() {
        if (map != null) {
            map.clear()
        }
        //clearMapGenangan()
        clearMapKecamatan()
    }


    //---- tes

    private fun getJSONGenanganFile() {
        try {
            val jsonString = loadJSONFromAsset(this@MainActivity, "jsongenangan.json")
                JSON_STRING_GENANGAN = jsonString
                    //showToast(this, jsonString.toString());
        } catch (e: IOException) {
            showToast(this@MainActivity, "Error: " + e.message + "\nGagal Load Kecamatan")
            e.printStackTrace()
        }
    }





    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }


    fun filterGeoJSON(jsonString: String, tahunSurvey: String): String {
        val jsonObject = JSONObject(jsonString)
        val features = jsonObject.getJSONArray("features")
        val filteredFeatures = JSONArray()

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val properties = feature.getJSONObject("properties")

            if (properties.getString("tahun_survey") == tahunSurvey) {
                filteredFeatures.put(feature)
            }

        }

        val filteredGeoJSON = JSONObject()
        filteredGeoJSON.put("type", "FeatureCollection")
        filteredGeoJSON.put("features", filteredFeatures)

        return filteredGeoJSON.toString()
    }


    //-------------- Menyimpan Data GeoJSON ke Storage

    fun saveGeoJsonToFile(context: Context, data: String, fileName: String) {
        try {
            val file = File(context.filesDir, fileName)
            val fos = FileOutputStream(file)
            fos.write(data.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadGeoJsonFromFile(context: Context, fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            val inputStream = file.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }






    private fun getJSONDrainaseFromFile() {
        var filteredJsonString=""
        JSON_STRING_KECAMATAN = ""
        try {
            val jsonString = loadGeoJsonFromFile(this@MainActivity, strfilejsonkecamatan)

            val yearMap = mapOf(
                fileNameblimbing to "2024",
                fileNamekkandang to "2021",
                fileNameklojen to "2022",
                fileNamelowokwaru to "2021",
                fileNamesukun to "2023"
            )

            if (jsonString != null) {
                filteredJsonString = yearMap[strfilejsonkecamatan]?.let { year ->
                    filterGeoJSON(jsonString, year)
                } ?: run {
                    println("Failed to match file name with year")
                    ""
                }

                JSON_STRING_KECAMATAN = filteredJsonString
            } else {
                println("Failed load Data")
                JSON_STRING_KECAMATAN = ""
            }



        } catch (e: IOException) {
            showToast(this@MainActivity, "Error: " + e.message + "\nGagal Load Kecamatan")
            e.printStackTrace()
        }
    }




    fun fetchGeoJsonData(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("x_api_key", "5113411142023")
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun fetchAndSaveGeoJsonData() {
        CoroutineScope(Dispatchers.IO).launch {
            val kecamatanMap = mapOf(
                konfigurasi.ID_KEC_Blimbing to fileNameblimbing,
                konfigurasi.ID_KEC_Kedungkandang to fileNamekkandang,
                konfigurasi.ID_KEC_Klojen to fileNameklojen,
                konfigurasi.ID_KEC_Lowokwaru to fileNamelowokwaru,
                konfigurasi.ID_KEC_Sukun to fileNamesukun
            )

            kecamatanMap.forEach { (idKecamatan, fileName) ->
                val geoJsonData = fetchGeoJsonData("${konfigurasi.URL_GET_DRAINKECAMATAN_2024}$idKecamatan")
                if (geoJsonData != null) {
                    saveGeoJsonToFile(this@MainActivity, geoJsonData, fileName)
                }
            }

        }
    }

    //save json all kecamatan
    private fun fetchAndSaveAllGeoJsonDataKecamatan() {
        CoroutineScope(Dispatchers.IO).launch {
            val kecamatanMap = mapOf(
                konfigurasi.ID_KEC_Lowokwaru to fileNamelowokwaru,
                konfigurasi.ID_KEC_Sukun to fileNamesukun,
                konfigurasi.ID_KEC_Blimbing to fileNameblimbing,
                konfigurasi.ID_KEC_Kedungkandang to fileNamekkandang,
                konfigurasi.ID_KEC_Klojen to fileNameklojen
            )

            kecamatanMap.forEach { (idKecamatan, fileName) ->
                val geoJsonData = fetchGeoJsonData("${konfigurasi.URL_GET_DRAINKECAMATAN_2024}$idKecamatan")
                geoJsonData?.let {
                    saveGeoJsonToFile(this@MainActivity, it, fileName)
                }
            }
        }
    }



}
