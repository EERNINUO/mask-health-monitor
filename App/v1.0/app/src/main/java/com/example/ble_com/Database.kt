package com.example.ble_com

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class Database(val context: Context, name: String, version: Int): SQLiteOpenHelper(context, name, null, version) {
    private val createTable = "CREATE TABLE sensor_data (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "time INTE, " +
            "CO2_concentration INTE, " +
            "TVOC_concentration INTE," +
            "temperature REAL," +
            "humidity REAL," +
            "pressure REAL" +
            ");"

    override fun onCreate(db: android.database.sqlite.SQLiteDatabase?) {
        db?.execSQL(createTable)
        Toast.makeText(context, "Table Created", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
