package com.example.ble_com

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bloothcom.BleDeviceInformation
import com.example.bloothcom.BluetoothCtrl

class MainActivity(): AppCompatActivity() {
    private lateinit var bluetoothCtrl: BluetoothCtrl // 蓝牙控制器
    private val device_information: ArrayList<BleDeviceInformation> = ArrayList() // 蓝牙设备信息列表
    val database: Database = Database(this, "Sensors_Data.db", 1)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_TurnOnBluetooth = findViewById<Button>(R.id.Button_TurnOnBluetooth)
        val button_CloseBluetooth = findViewById<Button>(R.id.Button_TurnOffBluetooth)
        val button_SearchBluetooth = findViewById<Button>(R.id.Button_FindBluetoothDevice)
        val bluetoothAdapter_ListView = findViewById<ListView>(R.id.bluetooth_adapter_list)

        bluetoothCtrl = BluetoothCtrl(
            this,
            application,
            device_information,
            bluetoothAdapter_ListView
        )

        button_TurnOnBluetooth.setOnClickListener {
            bluetoothCtrl.turnOnBluetooth(this, 1)
        }
        button_CloseBluetooth.setOnClickListener {
            bluetoothCtrl.turnOffBluetooth(this)
        }
        button_SearchBluetooth.setOnClickListener {
            bluetoothCtrl.searchDevices()
            database.writableDatabase
        }
        bluetoothAdapter_ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DataListActivity::class.java)
            intent.putExtra("deviceAddress", device_information[position].device.address)
            startActivity(intent)
            bluetoothCtrl.connectDevice(device_information[position].device.address)
        }
    }

    fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}