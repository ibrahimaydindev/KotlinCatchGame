package com.example.kotlincatchthekennygame

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

private const val REQUEST_ENABLE_BLUETOOTH = 1
private const val RUNTIME_PERMISSION_REQUEST_CODE = 2

class MainActivity : AppCompatActivity() {
    var score = 0
    var imageArray = ArrayList<ImageView>()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    // From the previous section:
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            with(result.device) {
                if (result.device.address == DEVICE_ADDRESS) {
                    CoroutineScope(Dispatchers.Main).launch {
                        stopBleScan()
                        connectGatt(
                            this@MainActivity,
                            true,
                            gattCallback,
                            BluetoothDevice.TRANSPORT_LE
                        )
                        Log.d(
                            ContentValues.TAG,
                            "Found BLE device! Name: ${result.device.name}, address: ${result.device.address}"
                        )
                    }
                }

            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    gatt.discoverServices()

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w(
                    "BluetoothGattCallback",
                    "Error $status encountered for $deviceAddress! Disconnecting..."
                )
                gatt.close()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.services?.forEach { service ->
                if (service.uuid == SERVICE_ADDRESS) {
                    val characteristic = service.getCharacteristic(CHARACTERISTIC_ADDRESS)
                    gatt.readCharacteristic(characteristic)

                } else {
                    Log.d("SERVİSLER:", "HATA")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic?.value?.toString(Charsets.UTF_8)

               deger(value)
            } else {
                runOnUiThread { Log.d("OKUNAN VERİ:", "HATA") }
            }
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        imageArray.add(imageView)
        imageArray.add(imageView2)
        imageArray.add(imageView3)
        imageArray.add(imageView4)
        imageArray.add(imageView5)
        imageArray.add(imageView6)
        imageArray.add(imageView7)
        imageArray.add(imageView8)
        imageArray.add(imageView9)

        hideImages()

            object : CountDownTimer(600000, 1000) {
                override fun onFinish() {

                    timeText.text = "Süre: 0"

                    for (image in imageArray) {
                        image.visibility = View.INVISIBLE
                    }

                    val alert = AlertDialog.Builder(this@MainActivity)
                    alert.setTitle("Oyun Bitti")
                    alert.setMessage("Yeniden Oyna?")
                    alert.setPositiveButton("Evet") { dialog, which ->
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }
                    alert.setNegativeButton("Hayır") { dialog, which ->
                        Toast.makeText(this@MainActivity, "Oyun Bitti", Toast.LENGTH_LONG).show()
                    }
                    alert.show()
                }

                override fun onTick(millisUntilFinished: Long) {
                    timeText.text = "Süre: " + millisUntilFinished / 1000
                }
            }.start()
        }

        fun increaseScore(view: View) {
            score += 1
            scoreText.text = "Skor: $score"
        }

        fun hareket(deger: String) {
            when (deger) {
                "1" -> {
                    imageView.visibility = View.INVISIBLE
                    imageView2.visibility = View.INVISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.INVISIBLE
                    imageView5.visibility = View.INVISIBLE
                    imageView6.visibility = View.INVISIBLE
                    imageView7.visibility = View.VISIBLE
                    imageView8.visibility = View.INVISIBLE

                }
                "2" -> {
                    imageView.visibility = View.INVISIBLE
                    imageView2.visibility = View.INVISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.INVISIBLE
                    imageView5.visibility = View.INVISIBLE
                    imageView6.visibility = View.VISIBLE
                    imageView7.visibility = View.INVISIBLE
                    imageView8.visibility = View.INVISIBLE
                }
                "3" -> {
                    imageView.visibility = View.INVISIBLE
                    imageView2.visibility = View.INVISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.VISIBLE
                    imageView5.visibility = View.INVISIBLE
                    imageView6.visibility = View.INVISIBLE
                    imageView7.visibility = View.INVISIBLE
                    imageView8.visibility = View.INVISIBLE
                }
                "4" -> {
                    imageView.visibility = View.INVISIBLE
                    imageView2.visibility = View.INVISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.INVISIBLE
                    imageView5.visibility = View.INVISIBLE
                    imageView6.visibility = View.VISIBLE
                    imageView7.visibility = View.INVISIBLE
                    imageView8.visibility = View.INVISIBLE
                }
                "5" -> {
                    imageView.visibility = View.INVISIBLE
                    imageView2.visibility = View.INVISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.INVISIBLE
                    imageView5.visibility = View.INVISIBLE
                    imageView6.visibility = View.INVISIBLE
                    imageView7.visibility = View.INVISIBLE
                    imageView8.visibility = View.INVISIBLE

                }
            }
        }

        fun hideImages() {
            imageView.visibility = View.INVISIBLE
            imageView2.visibility = View.INVISIBLE
            imageView3.visibility = View.VISIBLE
            imageView4.visibility = View.INVISIBLE
            imageView5.visibility = View.INVISIBLE
            imageView6.visibility = View.INVISIBLE
            imageView7.visibility = View.INVISIBLE
            imageView8.visibility = View.VISIBLE
            imageView9.visibility = View.INVISIBLE
        }

        @SuppressLint("MissingPermission")
        private fun promptEnableBluetooth() {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }


        override fun onResume() {
            super.onResume()
            if (!bluetoothAdapter.isEnabled) {
                promptEnableBluetooth()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            when (requestCode) {
                REQUEST_ENABLE_BLUETOOTH -> {
                    if (resultCode != Activity.RESULT_OK) {
                        promptEnableBluetooth()
                    }
                }
            }
        }

        fun Context.hasPermission(permissionType: String): Boolean {
            return ContextCompat.checkSelfPermission(this, permissionType) ==
                    PackageManager.PERMISSION_GRANTED
        }

        fun Context.hasRequiredRuntimePermissions(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasPermission(android.Manifest.permission.BLUETOOTH_SCAN) &&
                        hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        @SuppressLint("MissingPermission")
        private suspend fun startBleScan() {
            if (!hasRequiredRuntimePermissions()) {
                requestRelevantRuntimePermissions()
            } else {
                bleScanner.startScan(scanCallback)
            }
        }

        private fun Activity.requestRelevantRuntimePermissions() {
            if (hasRequiredRuntimePermissions()) {
                return
            }
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                    requestLocationPermission()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    requestBluetoothPermissions()
                }
            }
        }

        private fun requestLocationPermission() {
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle("Lokasyon İzni Gerekli")
                    .setMessage(
                        "Starting from Android M (6.0), the system requires apps to be granted " +
                                "location access in order to scan for BLE devices."
                    )
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            RUNTIME_PERMISSION_REQUEST_CODE
                        )
                    }
                    .show()
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun requestBluetoothPermissions() {
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle("Bluetooth İzni Gerekli")
                    .setMessage(
                        "Starting from Android S (12.0), the system requires apps to be granted " +
                                "Bluetooth permissions in order to scan for BLE devices."
                    )
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                android.Manifest.permission.BLUETOOTH_SCAN,
                                android.Manifest.permission.BLUETOOTH_CONNECT
                            ),
                            RUNTIME_PERMISSION_REQUEST_CODE
                        )
                    }
                    .show()
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                RUNTIME_PERMISSION_REQUEST_CODE -> {
                    val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
                        it.second == PackageManager.PERMISSION_DENIED &&
                                !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
                    }
                    val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                    val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                    when {
                        containsPermanentDenial -> {
                            val buidler = AlertDialog.Builder(this)
                            buidler.setTitle("İzin Gerekli")
                            buidler.setMessage("Uygulamayı kullanabilmek için izin vermeniz gerekmektedir.")
                            buidler.setPositiveButton("Ayarları Aç") { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            buidler.setNegativeButton("İptal") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog = buidler.create()
                            dialog.show()
                        }
                        containsDenial -> {
                            requestRelevantRuntimePermissions()
                        }
                        allGranted && hasRequiredRuntimePermissions() -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                startBleScan()
                            }

                        }
                        else -> {
                            // Unexpected scenario encountered when handling permissions
                            recreate()
                        }
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        private suspend fun stopBleScan() {
            bleScanner.stopScan(scanCallback)
        }

        companion object {
            private const val DEVICE_ADDRESS = "C0:49:EF:F9:BB:16"
            private val SERVICE_ADDRESS = UUID.fromString("FB349B5F-8000-0080-0010-0000E1FE0000")
            private val CHARACTERISTIC_ADDRESS =
                UUID.fromString("000710af-0900-1821-1235-000009000000")
            private val DESCRIPTOR_ADDRESS = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        }

        fun deger (text:String?) {
            if (text == "1") {
                hareket("1")
            } else if (text == "2") {
                hareket("2")
            } else if (text == "3") {
                hareket("3")
            } else if (text == "4") {
                hareket("4")
            } else if (text == "5") {
                hareket("5")
                val alert = AlertDialog.Builder(this@MainActivity)
                alert.setTitle("MARİO KAÇMAYI BAŞARDI ! TEBRİKLER")
                alert.setMessage("Yeniden Oyna?")
                alert.setPositiveButton("Evet") { dialog, which ->
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
                alert.setNegativeButton("Hayır") { dialog, which ->
                    Toast.makeText(this@MainActivity, "Oyun Bitti", Toast.LENGTH_LONG).show()
                }
                alert.show()

            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Lütfen 1-5 arası bir değer giriniz",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

}




