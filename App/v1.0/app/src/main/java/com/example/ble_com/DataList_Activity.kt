package com.example.ble_com

import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.graphics.BitmapCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.wch.ch9140lib.CH9140BluetoothManager
import cn.wch.ch9140lib.callback.CH9140MTUCallback
import cn.wch.ch9140lib.utils.LogUtil
import com.example.bloothcom.CH9140
import java.util.Timer
import java.util.TimerTask

var readData_Array = ArrayList<List<Byte>>()

class DataListActivity : AppCompatActivity() {
    lateinit var address_of_openDevice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datalist)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val database: Database = Database(this, "Sensors_Data.db", 1)
        val db = database.writableDatabase // 打开数据库

        address_of_openDevice = intent.getStringExtra("deviceAddress").toString()

        val CO2_textView = findViewById<TextView>(R.id.CO2_textView)
        val TVOC_textView = findViewById<TextView>(R.id.TVOC_textView)
        val temperature_textView = findViewById<TextView>(R.id.temperature_textView)
        val humidity_textView = findViewById<TextView>(R.id.humidity_textView)
        val pressure_textView = findViewById<TextView>(R.id.pressure_textView)

        val collection_ctrller = findViewById<Button>(R.id.collection_ctrller)

        val dataShow_timer = Timer()
        val dataShow_timerTask: TimerTask = object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    val last_data = db.query("sensor_data", null, "id = (SELECT MAX(id) FROM sensor_data)", null, null, null, null)
                    if (last_data.moveToFirst()) {
                        last_data.getColumnIndex("temperature").takeIf { it != -1 }?.let { temperatureIndex ->
                            val temperature = last_data.getFloat(temperatureIndex)
                            temperature_textView.text = String.format("%.2f", temperature)
                        }
                        last_data.getColumnIndex("humidity").takeIf { it != -1 }?.let { humidityIndex ->
                            val humidity = last_data.getFloat(humidityIndex)
                            humidity_textView.text = String.format("%.2f", humidity)
                        }
                        last_data.getColumnIndex("CO2_concentration").takeIf { it != -1 }?.let { CO2_concentrationIndex ->
                            val CO2_concentration = last_data.getInt(CO2_concentrationIndex)
                            CO2_textView.text = CO2_concentration.toString()
                        }
                        last_data.getColumnIndex("TVOC_concentration").takeIf { it != -1 }?.let { TVOC_concentrationIndex ->
                            val TVOC_concentration = last_data.getInt(TVOC_concentrationIndex)
                            TVOC_textView.text = TVOC_concentration.toString()
                        }
                        last_data.getColumnIndex("pressure").takeIf { it != -1 }?.let { pressureIndex ->
                            val pressure = last_data.getFloat(pressureIndex)
                            pressure_textView.text = String.format("%.2f", pressure)
                        }
                    }
                    last_data.close()
                }
            }
        }
        dataShow_timer.schedule(dataShow_timerTask, 0, 100)

        collection_ctrller.setOnClickListener {
            if (collection_ctrller.text == "开始监测") {
                Thread {
                    CH9140.write(("SENSORS_ONN").toByteArray(), 11)
                }.start()
                collection_ctrller.text = "停止监测"
            } else if(collection_ctrller.text == "停止监测") {
                Thread{
                    CH9140.write(("SENSORS_OFF").toByteArray(), 11)
                }.start()
                collection_ctrller.text = "开始监测"
            } else{
                showToast("未知错误")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Thread{
            CH9140.write(("SENSORS_OFF").toByteArray(), 11)
        }.start()
        CH9140.closeDevice(address_of_openDevice, false)
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}