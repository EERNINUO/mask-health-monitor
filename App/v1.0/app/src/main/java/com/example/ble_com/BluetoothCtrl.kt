package com.example.bloothcom

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.ListView
import androidx.annotation.RequiresApi
import cn.wch.ch9140lib.CH9140BluetoothManager
import cn.wch.ch9140lib.callback.ConnectStatus
import cn.wch.ch9140lib.utils.FormatUtil
import cn.wch.ch9140lib.utils.LogUtil
import com.example.ble_com.Database
import com.example.ble_com.MainActivity
import com.example.ble_com.R
import com.example.ble_com.readData_Array
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Timer
import java.util.TimerTask

val CH9140: CH9140BluetoothManager = CH9140BluetoothManager.getInstance() // 获取CH9140蓝牙管理器
var readData_Buffer = ArrayList<Byte>()

@RequiresApi(Build.VERSION_CODES.S)
class BluetoothCtrl(val activity: MainActivity, val application: Application, val device_information: ArrayList<BleDeviceInformation>, val listView: ListView){
    var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter() // 获取蓝牙适配器
    val database: Database = Database(activity, "Sensors_Data.db", 1) // 获取数据库
    var bluetoothName: String = ""
    var leSearching = false

    init {
        // 权限申请
        applyPermission(LocationPermission)
        applyPermission(BluetoothPermission)

        CH9140.init(application) // 初始化CH9140蓝牙管理器

        bluetoothName = bluetoothAdapter.name
    }

    fun applyPermission(permission: Array<String>){ // 申请权限
        if (permission.any {
                ContextCompat.checkSelfPermission(activity, it) !=
                        PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(activity, permission, 200)
        }
    }

    // 打开蓝牙
    fun turnOnBluetooth(activity: MainActivity, requestCode: Int) {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, requestCode)
        }
        Toast.makeText(activity, "蓝牙已打开", Toast.LENGTH_SHORT).show()
    }

    fun turnOffBluetooth(activity: MainActivity) {
        if (bluetoothAdapter.isEnabled) {
            bluetoothAdapter.disable()
        }
        Toast.makeText(activity, "蓝牙已关闭", Toast.LENGTH_SHORT).show()
    }

    fun connectDevice(address: String) {
        CH9140.openDevice(address, 5000, connectStatus)
//        while (!CH9140.isDeviceOpened(address))
//        showToast(CH9140.mtu.toString())

    }

    fun disconnectDevice(mac: String) {
        CH9140.closeDevice(mac, false)
    }

    fun searchDevices() {
        if (!leSearching) {
            bluetoothAdapter.startLeScan(leScanCallback) // 开始搜索设备
            showToast("开始搜索设备")
            LogUtil.d("开始搜索设备")
            leSearching = true

            // 5秒后停止搜索
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    if (leSearching) {
                        bluetoothAdapter.stopLeScan(leScanCallback)
                        LogUtil.d("停止搜索设备")
                        leSearching = false
                    }
                }
            }

            timer.schedule(task, 5000)
        }
    }

    fun send_data(data: ByteArray) {
        CH9140.write(data, data.size)
    }

    // 搜索设备回调
    private val leScanCallback =
        BluetoothAdapter.LeScanCallback { bluetoothDevice: BluetoothDevice, rssi: Int, bytes: ByteArray? ->
            try {
//                Log.e("bytes",bytesToHexString(bytes))
//                if(bytes!=null) {
//                    val name2 = getBluetoothName(bytes)
//                }

                @SuppressLint("MissingPermission")
                val name = bluetoothDevice.getName()
                if (name == null || name.isEmpty()) return@LeScanCallback
                var mac = bluetoothDevice.getAddress()
                if (mac == null || mac.isEmpty()) return@LeScanCallback
                mac = mac.replace(":", "")

//                Log.e("bleDiscovery", name + "|" + mac +"|"+ rssi)
                // 判断是否已经存在
                var isExist = false
                for (tempDevice in device_information) {
                    if (tempDevice.device.getAddress().replace(":", "") == mac) {
                        isExist = true
                        break
                    }
                }
                // 如果不存在，则添加到列表中
                if (!isExist) {
                    val searchDevice = BleDeviceInformation(bluetoothDevice, rssi)
                    device_information.add(searchDevice)
                    val adapter = BleDevice_ListView_Adapter(
                        activity,
                        R.layout.bledevicelistview_layout,
                        device_information
                    )
                    listView.adapter = adapter
                }
            } catch (e: Throwable) {
                Log.e("LeScanCallback", "Throwable")
            }
        }

    @OptIn(ExperimentalStdlibApi::class)
    val connectStatus: ConnectStatus = object: ConnectStatus { // 连接状态回调
        override fun OnError(t: Throwable){
            LogUtil.d("连接失败")
        }

        override fun OnConnecting(){
            LogUtil.d("正在连接")
        }

        override fun OnConnectSuccess(var1:String){
            LogUtil.d("连接成功")
        }

        override fun onInvalidDevice(var1:String){
            TODO()
        }

        override fun OnConnectTimeout(var1:String){
            LogUtil.d("连接超时")
        }

        override fun OnDisconnect(var1:String, var2:Int){
            LogUtil.d("连接断开")
        }

        override fun onSerialReadData(data: ByteArray) { // 读取数据回调
            if (data.size == 0) { // 如果数据为空，则不处理
                LogUtil.d("数据为空")
            } else{
                // val stringData = data.toHexString()
                LogUtil.d("收到数据：" + FormatUtil.bytesToHexString(data))
                for (i in 0 until 20){
                    readData_Buffer.add(data[i])
                }
                while (readData_Buffer.size > 12){
                    val onceReadData = (readData_Buffer.slice(0 until 12))
                    readData_Buffer.subList(0, 12).clear()
                    val time = System.currentTimeMillis()
                    val CO2_PPM = (onceReadData[0].toInt() and 0xff) + ((onceReadData[1].toInt() and 0xff) * 256)
                    val TVOC_PPB = (onceReadData[2].toInt() and 0xff) + ((onceReadData[3].toInt() and 0xff) * 256)
                    var temperature = ((onceReadData[4].toInt() and 0xff) + (onceReadData[5].toInt() and 0xff) * 256).toFloat()
                    temperature = ((175.0 * temperature / 65536.0) - 45.0).toFloat()
                    var humidity = (onceReadData[6].toInt() and 0xff) + ((onceReadData[7].toInt() and 0xff) * 256).toFloat()
                    humidity = ((125.0 * humidity / 65536.0) - 6.0).toFloat()
                    var pressure = ((onceReadData[8].toInt() and 0xff) + (onceReadData[9].toInt() and 0xff) * 256 + (onceReadData[10].toInt() and 0xff) * 256 * 256).toDouble() // + onceReadData[11] * 256.0 * 256.0 * 256.0 (空字节)
                    pressure /= 40.96

                    val values: ContentValues = ContentValues();
                    val db = database.writableDatabase // 打开数据库

                    values.put("time", time)
                    values.put("CO2_concentration", CO2_PPM)
                    values.put("TVOC_concentration", TVOC_PPB)
                    values.put("temperature", temperature)
                    values.put("humidity", humidity)
                    values.put("pressure", pressure)
                    db.insert("sensor_data", null, values); // 插入第一条数据
                    values.clear()
                    readData_Array.add(onceReadData)
                }
            }
        }
    }

    fun showToast(s: String) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }
}
