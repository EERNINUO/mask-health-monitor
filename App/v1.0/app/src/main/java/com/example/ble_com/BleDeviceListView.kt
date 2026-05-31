package com.example.bloothcom

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.ble_com.R

class BleDeviceInformation( // 蓝牙设备信息类
    val device: BluetoothDevice,
    val rssi: Int
)

class BleDevice_ListView_Adapter(activity: Activity, val resourceID: Int,data: ArrayList<BleDeviceInformation>)
    : ArrayAdapter<BleDeviceInformation>(activity, resourceID, data){ // 蓝牙设备列表适配器

       override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
           val view: View
           if (convertView == null) {
               view = LayoutInflater.from(context).inflate(resourceID, parent, false)
           } else {
               view = convertView
           }
           val DeviceName: TextView = view.findViewById(R.id.name)
           val DeviceAddress: TextView = view.findViewById(R.id.address)
           val DeviceRSSI: TextView = view.findViewById(R.id.rssi)
           val deviceInfo= getItem(position)

            if (deviceInfo != null){
                DeviceName.text = " ${deviceInfo.device.name}"
                DeviceAddress.text = " 设备地址:${deviceInfo.device.address}"
                DeviceRSSI.text = "${deviceInfo.rssi} db "
            }

           return view
       }
   }

// 位置权限
val LocationPermission = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

// 蓝牙权限
@RequiresApi(Build.VERSION_CODES.S)
val BluetoothPermission = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_ADVERTISE,
    Manifest.permission.BLUETOOTH_PRIVILEGED
)