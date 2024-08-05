/*
package com.sibama2024ai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MainActivitBackup1607 : AppCompatActivity(), OnMapReadyCallback {
    private var JSON_MERGE: String?=null
    private var JSON_STRING_KECAMATAN: String? = null
    private var JSON_STRING_GENANGAN: String? = null
    private val DEFAULT_LATLNG = LatLng(-7.9927011, 112.6284465)
    private lateinit var str_id_genangan: String
    private lateinit var str_id_saluran: String
    private lateinit var strnama_jalan: String
    private lateinit var status_limpasan: String
    private lateinit var strstscctv: String
    private lateinit var stralamat: String
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


    private lateinit var str_namakecamatan: String
    private lateinit var filterdata: String

    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 3000

    private lateinit var map: GoogleMap
    //private lateinit var bottomSheetBehavior: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var navigationView: NavigationView

    private lateinit var radioGroupKecamatan: RadioGroup
    private lateinit var radioGenangan: RadioButton

    private var precipitationDay1 : Double = 0.0
    private var precipitationDay2 : Double = 0.0
    private var precipitationDay3 : Double = 0.0
    private var dateprecipitationDay1: String =""
    private var dateprecipitationDay2: String =""
    private var dateprecipitationDay3: String =""

    //progress untuk map
    private lateinit var progressDialog: AlertDialog
    private val delayMillis: Long = 10000 // Delay dalam milidetik

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
    private lateinit var tstskualifikasi: TextView
    private lateinit var tstsarah: TextView
    private lateinit var tststipe: TextView
    private lateinit var tstskondisi: TextView
    private lateinit var tstskondisifisik: TextView
    private lateinit var tkondisisedimen: TextView
    private lateinit var tstsgenangan: TextView
    private lateinit var tststglupdate: TextView

    private lateinit var bfoto1: ImageButton
    private lateinit var bfoto2:ImageButton
    private val btnfoto = 0
    var bfoto1aktif: Int = 0
    var bfoto2aktif: Int = 0

    private var isTitikgenanganChecked = true
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
        val fab: FloatingActionButton = findViewById(R.id.fab_filter)
        fab.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END)
            } else {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
        // Initialize NavigationView
        navigationView = findViewById(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation item click here
            // For example, close the side sheet
            drawerLayout.closeDrawer(GravityCompat.END)
            true
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
        getJSONMeteo()

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

        //--- panggil seluruh map data
        str_namakecamatan="1"
        //filterdata = konfigurasi.URL_GET_GENANGAN


        getJSONDrainaseTitikGenangan()
        showProgressDialog()
        getJSONDrainase()

        //... kec lain nya

        //---------------- Handle CheckBox and Buttons -------------------------------
        val resetButton: Button = findViewById(R.id.reset_button)
        //val refreshButton: Button = findViewById(R.id.refresh_button)
        val progressBar: LinearProgressIndicator = findViewById(R.id.progress_bar)
        radioGroupKecamatan = findViewById(R.id.radioGroupKecamatan)
        radioGenangan = findViewById(R.id.radio_genangan)
        //radioGenangan.isChecked=true

        CheckboxDefaultState()

        radioGenangan.isChecked=true
        clearMap()
        if (this::map.isInitialized) {
            onMapReady(map)
        }

        resetButton.setOnClickListener {
            //clear all Map Kecamatan dan Tampilkan Titik Genangan saja
            //....

            CheckboxDefaultState()
            radioGenangan.isChecked=true
            clearMap()
            if (this::map.isInitialized) {
                onMapReady(map)
               //showProgressDialog()
            }

            drawerLayout.closeDrawer(GravityCompat.END)

            // ---------------------------------
        }

        refreshButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            progressBar.postDelayed({
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
            }, 3000)
            getJSONMeteo()
        }


        radioGroupKecamatan.setOnCheckedChangeListener { group, checkedId ->

            val allKecamatanStates = arrayOf(
                ::isTitikgenanganChecked,
                ::isSukunChecked,
                ::isKlojenChecked,
                ::isBlimbingChecked,
                ::isKKandangChecked,
                ::isLWaruChecked
            )

            allKecamatanStates.forEach { it.set(false) }

            when (checkedId) {
                R.id.radio_genangan -> {
                    isTitikgenanganChecked = true
                    clearMap()

                }

                R.id.radio_blimbing -> {
                    isBlimbingChecked = true
                    clearMap()
                    showToast(this, "Blimbing selected")
                }
                R.id.radio_sukun -> {
                    isSukunChecked = true

                    clearMap()

                }
                R.id.radio_lowokwaru -> {
                    isLWaruChecked = true
                    clearMap()
                    showToast(this, "Lowokwaru selected")
                }
                R.id.radio_kedkandang -> {
                    isKKandangChecked = true
                    clearMap()
                    showToast(this, "radio_kedkandang selected")
                }
                R.id.radio_klojen -> {
                    isKlojenChecked = true
                    clearMap()
                    showToast(this, "radio_klojen selected")
                }

            }
            showProgressDialog()
            if (this::map.isInitialized) {
                onMapReady(map)
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        bfoto1.setOnClickListener {
            //showToast(this,uri_foto1!!)
            val intent = Intent(this, TampilFotoActivity::class.java)
            intent.putExtra("IMAGE_URL", uri_foto1)
            startActivity(intent)
        }

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

        map.setOnMapClickListener { latLng ->
            // Sembunyikan bottom sheet saat mengklik peta
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN}


        reloadGeoJsonLayers()


    }



    fun CheckboxDefaultState(){

        isSukunChecked = false
        isKlojenChecked =false
        isBlimbingChecked =false
        isKKandangChecked = false
        isLWaruChecked =false

        hideProgressDialog()
    }


    //----- progress bar untuk map
    private fun showProgressDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null)

        progressDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        progressDialog.show()

        // Menggunakan Handler untuk menutup dialog setelah delay
        handler.postDelayed({
            hideProgressDialog()
        }, delayMillis)

    }

    private fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
    //--------------------------------------------------

    private fun checkInternetConnection() {
        val isConnected = isInternetAvailable(this)
        if (isConnected) {
            // Terhubung ke internet
            Toast.makeText(this, "Terhubung ke internet", Toast.LENGTH_LONG).show()
        } else {
            // Tidak terhubung ke internet
            AlertDialog.Builder(this)
                .setTitle("Koneksi Internet")
                .setMessage("Tidak terhubung ke internet.\nAplikasi ini membutuhkan koneksi internet untuk menampilkan data drainase.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()

            // Jadwalkan pengecekan ulang
            //handler.postDelayed({ checkInternetConnection() }, checkInterval)
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
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)
    }

    //-- untuk pembulatan bilangan desimal
    fun String.roundToDecimals(decimals: Int): Double {
        return this.toDoubleOrNull()?.let {
            "%.${decimals}f".format(it).toDouble()
        } ?: 0.0
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
            val policy = StrictMode.ThreadPolicy.Builder()
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
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun showr24(jsonString: String?) {
        try {
            // Parsing JSON string menjadi JSONObject
            val jsonObject = JSONObject(jsonString!!)

            // Mengekstrak setiap atribut dan nilai dari JSONObject
            val latitude = jsonObject.getDouble("latitude")
            val longitude = jsonObject.getDouble("longitude")
            val generationTime = jsonObject.getDouble("generationtime_ms")
            val utcOffsetSeconds = jsonObject.getInt("utc_offset_seconds")
            val timezone = jsonObject.getString("timezone")
            val timezoneAbbreviation = jsonObject.getString("timezone_abbreviation")
            val elevation = jsonObject.getDouble("elevation")

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
    */
/* ---- end data meteo*//*


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
            JSON_STRING_GENANGAN = responseString //panggil di OnMapReady

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
            val obj = JSONObject(JSON_STRING_GENANGAN)
            layerGenangan = GeoJsonLayer(map, obj)

            // Set the style of the feature
            addLayerTitikGenangan(layerGenangan!!)

            // Set a listener for feature click
            layerGenangan?.setOnFeatureClickListener { featureGenangan ->
                strnama_jalan = featureGenangan.getProperty("nama_jalan")
                str_id_genangan = featureGenangan.getProperty("id")
                stream_id = featureGenangan.getProperty("stream_id")

                if (stream_id.isNullOrEmpty() || stream_id == "-") {
                    strstscctv="Not Available"

                } else {
                    strstscctv="Available"
                    showToast(this, "$strstscctv\n$stream_id")

                    // Call WebView CCTV
                       Intent(this, TitikGenangan::class.java).apply {
                           putExtra(konfigurasi.GET_ID_GENANGAN, str_id_genangan)
                           putExtra(konfigurasi.GET_STREAM_ID, stream_id)
                           startActivity(this)
                       }
                }
                //showToast(this,"CCTV : " + strstscctv)
            }
            layerGenangan?.addLayerToMap()


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun clearMapGenangan() {
        layerGenangan?.removeLayerFromMap()
        lastClickedFeature = null // Reset the last clicked feature
    }

    private fun reloadGeoJsonLayerGenangan() {

        runTitikGenangan()
    }
    //------------------ end of Titik Genangan -------------------------------


    //vvvvvvvvvvvvvvv start of JSON Drainase SUKUN vvvvvvvvvvvvvvv
    private fun runMapKecamatan(){
        try {
            if (JSON_STRING_KECAMATAN != null) {
                val obj = JSONObject(JSON_STRING_KECAMATAN!!)

                layerKecamatan = GeoJsonLayer(map, obj)

                // Set the style of the feature
                layerKecamatan?.defaultLineStringStyle
                addLayerMapKecamatan(layerKecamatan!!)


                // Set a listener for feature click
                layerKecamatan?.setOnFeatureClickListener { feature ->
                    strnama_jalan = feature.getProperty("nama_jalan")
                    str_id_genangan = feature.getProperty("id")

                    feature?.let {
                        if (feature.geometry.geometryType == "LineString") {
                            val lineStringStyle = GeoJsonLineStringStyle().apply {
                                color = Color.rgb(0, 100, 249)
                            }

                            (feature as GeoJsonFeature).apply {
                                // Reset previous clicked feature color
                                lastClickedFeatureKecamatan?.let {
                                    val resetStyle = GeoJsonLineStringStyle().apply {
                                        color = Color.rgb(0, 202, 249) // Original color
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
                        panjang = feature.getProperty("panjang").roundToDecimals(1).toString()
                        str_id_saluran = feature.getProperty("kode_saluran")
                        arah = feature.getProperty("arah")
                        tipe = feature.getProperty("tipe")
                        kondisi_fisik = feature.getProperty("kondisi_fisik")
                        created_at = feature.getProperty("tahun_survey")
                        foto = feature.getProperty("foto")
                        dimensi = feature.getProperty("dimensi")

                        // Tampilkan di bottomsheet
                        tstsnamajalan.text = "$strnama_jalan ($str_id_saluran)"
                        tstslajur.text = sisi
                        tstspanjang.text = "$panjang meter"
                        if (feature.getProperty("arah") == null) {
                            arah="-"
                        }
                        tstsarah.text = arah

                        //
                        tststipe.text = tipe
                        tstskondisifisik.text = kondisi_fisik
                        tstsgenangan.text = status_limpasan
                        tststglupdate.text = created_at

                        // URI Foto 1
                        */
/*uri_foto1 = "${konfigurasi.URL_ASSET_FOTO}2023/foto/$foto"*//*

                        uri_foto1 = foto
                        loadImage(uri_foto1!!, bfoto1)

                        // URI Foto 2
                        */
/*uri_foto2 = "${konfigurasi.URL_ASSET_FOTO}2023/dimensi/$dimensi"*//*

                        uri_foto2 = dimensi
                        loadImage(uri_foto2!!, bfoto2)
                    } ?: showToast(this, "Data tidak ada")
                }

                layerKecamatan?.addLayerToMap()
            } else {
                // Tangani kasus di mana JSON_STRING_KECAMATAN null
                //Log.e("Error", "JSON_STRING_KECAMATAN is null")
                showToast(this,"JSON_STRING_KECAMATAN is null")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun getJSONDrainase() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(10, TimeUnit.SECONDS)

        val client = builder.build()
        val request = Request.Builder()
            */
/*.url(filterdata)*//*

            //.url(konfigurasi.URL_GET_DRAINKECAMATAN_2024+str_namakecamatan)
            .url("https://sibama2022.griyasolusiindonesia.com/api/drainase?idKec=$str_namakecamatan")
            //.url("https://sibama2022.griyasolusiindonesia.com/api/drainase?idKec=1")
            .addHeader("x-api-key", konfigurasi.x_api_key)
            .build()
        showToast(this,konfigurasi.URL_GET_DRAINKECAMATAN_2024+str_namakecamatan)
        try {

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()
            JSON_STRING_KECAMATAN = responseString

        } catch (e: IOException) {
            Toast.makeText(this, "Error Get GeoJSON Kecamatan: " + e.message + "\nInternet tidak stabil", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun addLayerMapKecamatan(layer: GeoJsonLayer) {
        // Iterate over all the features stored in the layer
        for (feature in layer.features) {
            // Check if the object exist
            if (feature.getProperty("id") != null) {

                strnama_jalan = feature.getProperty("nama_jalan")
                status_limpasan = feature.getProperty("status_limpasan")

                // Create a new point style
                val lineStringStyle = GeoJsonLineStringStyle().apply {
                    color = when (status_limpasan) {
                        "MELIMPAH" -> Color.rgb(255, 0, 50)
                        "RAWAN MELIMPAH" -> Color.rgb(255, 165, 0)
                        else -> Color.rgb(0, 202, 249) // default color
                    }
                    width = if (status_limpasan == "-") 5.5f else 5f
                    isClickable = true
                    isGeodesic = true
                }

                // Assign the point style to the feature
                feature.lineStringStyle = lineStringStyle

            }
        }
    }

    private fun clearMapKecamatan() {
        layerKecamatan?.removeLayerFromMap()
        lastClickedFeatureKecamatan = null // Reset the last clicked feature
    }

    private fun reloadGeoJsonLayerKecamatan() {
        runMapKecamatan()
    }

    private fun showBottomSheet(feature: GeoJsonFeature) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    // ^^^^^^^^^^^^ end of Sukun ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

*/
/*
    implementasi
    val combinedJsonString = combineGeoJSON(jsonString1, jsonString2)
    println(combinedJsonString)
*//*


    private fun clearMap() {
        clearMapGenangan()
        clearMapKecamatan()

        //tambahkan kec lain

    }

    private fun reloadGeoJsonLayers() {
        if (isSukunChecked) {
            //
            str_namakecamatan =konfigurasi.ID_KEC_Sukun
            getJSONDrainase()
            reloadGeoJsonLayerKecamatan()
        } else {
            clearMapKecamatan()
        }

        if (isTitikgenanganChecked) {
            getJSONDrainaseTitikGenangan()
            reloadGeoJsonLayerGenangan()
        }else{
            clearMapGenangan()
        }

        //tambahkan kec lain
        if (isKlojenChecked) {
            str_namakecamatan =konfigurasi.ID_KEC_Klojen
            //getJSONDrainaseTitikGenangan()
            getJSONDrainase()
            reloadGeoJsonLayerKecamatan()
        }else{
            clearMapKecamatan()
        }
        if (isBlimbingChecked) {
            str_namakecamatan =konfigurasi.ID_KEC_Blimbing
            getJSONDrainase()
            reloadGeoJsonLayerKecamatan()
        }else{
            clearMapKecamatan()
        }
        if (isLWaruChecked) {

            str_namakecamatan = konfigurasi.ID_KEC_Lowokwaru
            getJSONDrainase()
            reloadGeoJsonLayerKecamatan()
        }else{
            clearMapKecamatan()
        }
        if (isKKandangChecked) {
            str_namakecamatan = konfigurasi.ID_KEC_Kedungkandang
            getJSONDrainase()
            reloadGeoJsonLayerKecamatan()
        }else{
            clearMapKecamatan()
        }


    }

    fun getUri_foto1(): String {
        return uri_foto1!!
    }

    fun getUri_foto2(): String {
        return uri_foto2!!
    }

    */
/*fun getId_foto1(): String {
        return id_foto1
    }

    fun getId_foto2(): String {
        return id_foto2
    }*//*


    fun getbtn_foto(): Int {
        return btnfoto
    }


}
*/
