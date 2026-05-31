package com.example.test_draw

import android.content.Context
import android.graphics.Color
import com.example.ble_com.Database
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class DynamicChart(context: Context, mlineChart: LineChart){
    val database: Database = Database(context, "Sensors_Data.db", 1)
    val db = database.writableDatabase // 打开数据库
    private var lineChart: LineChart = mlineChart
    private var leftAxis: YAxis? = null
    private var xAxis: XAxis
    private var yAxis: YAxis
    private var lineData: LineData? = null
    private var lineDataSet_CO2: LineDataSet = LineDataSet(null, "CO2")
    private var lineDataSet_TVOC: LineDataSet = LineDataSet(null, "TVOC")
    private var lineDataSet_tem: LineDataSet = LineDataSet(null, "tem")
    private var lineDataSet_hum: LineDataSet = LineDataSet(null, "hum")
    private var lineDataSet_pre: LineDataSet = LineDataSet(null, "pre")

    init {
        leftAxis = lineChart.axisLeft

        xAxis = lineChart.xAxis
        yAxis = lineChart.axisLeft
        //设置在曲线图中显示的最大数量
        xAxis.setAxisMinimum(0f)
        xAxis.setAxisMaximum(40f)
        yAxis.setAxisMinimum(0f)
        yAxis.setAxisMaximum(100f)

        initLineChart()

        initLineDataSet(lineDataSet_CO2, Color.RED)
        initLineDataSet(lineDataSet_TVOC, Color.GREEN)
        initLineDataSet(lineDataSet_tem, Color.BLUE)
        initLineDataSet(lineDataSet_hum, Color.YELLOW)
        initLineDataSet(lineDataSet_pre, Color.CYAN)
    }

    fun initLineChart(){
        lineChart.isDoubleTapToZoomEnabled = false
        // 不显示数据描述
        lineChart.description.isEnabled = false
        // 没有数据的时候，显示“暂无数据”
        lineChart.setNoDataText("暂无数据")
        //禁止x轴y轴同时进行缩放
        lineChart.setPinchZoom(false)
        //启用/禁用缩放图表上的两个轴。
        lineChart.setScaleEnabled(false)
        //设置为false以禁止通过在其上双击缩放图表。
        lineChart.axisRight.isEnabled = false //关闭右侧Y轴
        lineChart.setDrawGridBackground(false)
        //显示边界
        lineChart.setDrawBorders(true)
        //折线图例 标签 设置 这里不显示图例
        val legend: Legend = lineChart.legend
        legend.isEnabled = false

        //X轴设置显示位置在底部
        xAxis.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setGranularity(1f) // 间隔1
        xAxis.setLabelCount(3000) // 显示3000个
        xAxis.setDrawGridLines(false) // 不绘制网格线
        //保证Y轴从0开始，不然会上移一点
        leftAxis?.setAxisMinimum(0f)
        leftAxis?.setDrawGridLines(false) // 不绘制网格线
    }

    fun initLineDataSet(lineDataSet: LineDataSet , color: Int){
        lineDataSet.setLineWidth(1.5f) // 线宽
        lineDataSet.setDrawCircles(false) // 不绘制圆点
        lineDataSet.setColor(color) // 颜色
        lineDataSet.highLightColor = color // 点击交点后显示线颜色
        //设置曲线填充
        lineDataSet.setDrawFilled(false)//填充底部颜色
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT // Y轴
        lineDataSet.valueTextSize = 0f // 折线图上字体大小
        lineDataSet.setMode(LineDataSet.Mode.LINEAR)//CUBIC_BEZIER
        //添加一个空的 LineData
        lineData = LineData() // LineData对象，用于存放LineDataSet的列表
        lineChart.setData(lineData) // 将数据描述添加到图表中

        lineChart.invalidate()
    }

    fun addEntry(){
        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet_hum.entryCount == 0) {
            lineData?.addDataSet(lineDataSet_CO2)
            lineData?.addDataSet(lineDataSet_TVOC)
            lineData?.addDataSet(lineDataSet_tem)
            lineData?.addDataSet(lineDataSet_hum)
            lineData?.addDataSet(lineDataSet_pre)
        }

        // humidity
        var dataSet: LineDataSet = lineData?.getDataSetByIndex(0) as (LineDataSet)
        dataSet.values = getDataFromDatabase("CO2_concentration")
        dataSet = lineData?.getDataSetByIndex(1) as (LineDataSet)
        dataSet.values = getDataFromDatabase("TVOC_concentration")
        dataSet = lineData?.getDataSetByIndex(2) as (LineDataSet)
        dataSet.values = getDataFromDatabase("temperature")
        dataSet = lineData?.getDataSetByIndex(3) as (LineDataSet)
        dataSet.values = getDataFromDatabase("humidity")
        dataSet = lineData?.getDataSetByIndex(4) as (LineDataSet)
        dataSet.values = getDataFromDatabase("pressure")

        lineChart.notifyDataSetChanged();
        lineChart.setData(lineData)
    }

    fun getDataFromDatabase(dataName: String): ArrayList<Entry> {
        val dataList = ArrayList<Entry>()
        val databaseCursor = db.query(
            "sensor_data",
            null,
            "id >= (SELECT MAX(id) - 119 FROM sensor_data)",
            null, null, null, null)

        val timeIndex = databaseCursor.getColumnIndex("time")
        val dataIndex = databaseCursor.getColumnIndex(dataName)
        val firsTime = databaseCursor.getInt(timeIndex)

        while (databaseCursor.moveToNext()) {
            val time = databaseCursor.getInt(timeIndex) - firsTime
            val data = when (dataName) {
                "CO2_concentration", "TVOC_concentration" -> (databaseCursor.getInt(dataIndex) / 600).toFloat()
                "humidity" -> databaseCursor.getFloat(dataIndex)
                "temperature" -> (databaseCursor.getFloat(dataIndex) -25) * 5.toFloat()
                "pressure" -> databaseCursor.getFloat(dataIndex)
                else -> 0f
            }
            dataList.add(Entry(time.toFloat(), data))
        }

        databaseCursor.close()

        return dataList
    }
}