package com.example.jizhangtest

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_count.*
import java.math.BigDecimal

class CountActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    var cal = Calendar.getInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    var time_year = cal.get(Calendar.YEAR) // 得到年份
    @RequiresApi(Build.VERSION_CODES.N)
    var time_month = cal.get(Calendar.MONTH) + 1 // 月份要加一
    @RequiresApi(Build.VERSION_CODES.N)
    var time_day = cal.get(Calendar.DAY_OF_MONTH) // 得到日期
    @RequiresApi(Build.VERSION_CODES.N)
    var time_hour = cal.get(Calendar.HOUR_OF_DAY) // 得到小时
    @RequiresApi(Build.VERSION_CODES.N)
    var time_minute = cal.get(Calendar.MINUTE) // 得到分
    @RequiresApi(Build.VERSION_CODES.N)
    var time_second = cal.get(Calendar.SECOND) // 得到秒

    val MAX:Double = 100000000.0
    var price = BigDecimal.valueOf(0.0)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count)

        // flagGet 用于判断是走添加还是修改
        val flagGet = intent.getStringExtra("flag")

            numEvent() // 键盘
            themeInit() // 类型

        if(flagGet == "0") {
            dateInit() // 日期
            // 保存键
            Log.d("add", "添加 flag:${flagGet}")
            saveNum.setOnClickListener {

                var hint = psEditText.text.toString()
                var tmp = textNum.text.toString().toBigDecimal().add(price) // 浮点数大数类

                if (tmp.compareTo(BigDecimal.valueOf(MAX)) > 0) {
                    Toast.makeText(this, "计算范围过大", Toast.LENGTH_SHORT).show()
                } else if (tmp.compareTo(BigDecimal(0.0)) <= 0) {
                    Toast.makeText(this, "保存的数据必须大于0", Toast.LENGTH_SHORT).show()
                } else {
                    val theme: String = if (payRadioButton.isChecked) "支出" else "收入"
                    price = tmp
                    if (hint == "") hint = "null"
                    Log.d(
                        "TAGGG",
                        "theme:${theme}\n" +
                                "year:${time_year}\n" +
                                "month:${time_month}\n" +
                                "day:${time_day}\n" +
                                "hour:${time_hour}\n" +
                                "minute:${time_minute}\n" +
                                "second:${time_second}\n" +
                                "hint:${hint}\n" +
                                "price:${price}\n"
                    )
                    // 进行数据存储工作
                    val dbHelper = MyDatabaseHelper(this, "Bills.db", 1) // 创建一个数据库
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put("theme", theme)
                        put("date_year", time_year)
                        put("date_month", time_month)
                        put("date_day", time_day)
                        put("date_hour", time_hour)
                        put("date_minute", time_minute)
                        put("date_second", time_second)
                        put("hint", hint)
                        put("price", price.toString())
                    }
                    db.insert("Bills", null, values) // 插入数据
                    // 数据存储工作结束

                    // 把数据初始化
                    textNum.text = "0"
                    countTextView.text = ""
                    totalNameTextView.text = ""
                    totalTextView.text = ""

                }
                if (price > BigDecimal(0)) {
                    price = BigDecimal(0.0)
                    finish()
                }
            }
        }
        else {
            // 修改数据
            val themeGet = intent.getStringExtra("theme").toString()
            val priceGet = intent.getStringExtra("price")?.toBigDecimal()
            val yearGet = intent.getIntExtra("year", 0)
            val monthGet = intent.getIntExtra("month", 0)
            val dayGet = intent.getIntExtra("day", 0)
            val hourGet = intent.getIntExtra("hour", 0)
            val minuteGet = intent.getIntExtra("minute", 0)
            val secondGet = intent.getIntExtra("second", 0)
            val hintGet = intent.getStringExtra("hint").toString()
            Log.d(
                "change",
                "修改 flag:${flagGet}\n ${themeGet}-${priceGet}-${yearGet}-${monthGet}-${dayGet}-${hourGet}-${minuteGet}-${secondGet}-${hintGet}"
            )
//            setTimeButton.text = "${yearGet}-${monthGet}-${dayGet}"

            // 时间
            time_year = yearGet
            time_month = monthGet
            time_day = dayGet
            dateInit()

            // 类型
            if(themeGet == "支出") {
                payRadioButton.toggle()
            }
            else {
                incomeRadioButton.toggle()
            }
            // 备注
            psEditText.setText(hintGet)
            // 金额
            textNum.text = priceGet.toString()

            saveNum.setOnClickListener {

                var hint = psEditText.text.toString()
                var tmp = textNum.text.toString().toBigDecimal().add(price) // 浮点数大数类
                val theme: String = if (payRadioButton.isChecked) "支出" else "收入"

                if (tmp.compareTo(BigDecimal.valueOf(MAX)) > 0) {
                    Toast.makeText(this, "计算范围过大", Toast.LENGTH_SHORT).show()
                } else if (tmp.compareTo(BigDecimal(0.0)) <= 0) {
                    Toast.makeText(this, "保存的数据必须大于0", Toast.LENGTH_SHORT).show()
                } else {

                    price = tmp
                    if (hint == "") hint = "null"
//                    Log.d(
//                        "TAGGG",
//                        "theme:${theme}\n" +
//                                "year:${time_year}\n" +
//                                "month:${time_month}\n" +
//                                "day:${time_day}\n" +
//                                "hour:${time_hour}\n" +
//                                "minute:${time_minute}\n" +
//                                "second:${time_second}\n" +
//                                "hint:${hint}\n" +
//                                "price:${price}\n"
//                    )
                    // 进行数据修改工作
                    val dbHelper = MyDatabaseHelper(this, "Bills.db", 1) // 创建一个数据库
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put("theme", theme)
                        put("date_year", time_year)
                        put("date_month", time_month)
                        put("date_day", time_day)
                        put("date_hour", time_hour)
                        put("date_minute", time_minute)
                        put("date_second", time_second)
                        put("hint", hint)
                        put("price", price.toString())
                    }
                    db.update("Bills", values, "theme=? " +
                            "and date_year=? " +
                            "and date_month=? " +
                            "and date_day=? " +
                            "and date_hour=? " +
                            "and date_minute=? " +
                            "and date_second=? " +
                            "and price=?",
                    arrayOf(themeGet,
                        yearGet.toString(),
                        monthGet.toString(),
                        dayGet.toString(),
                        hourGet.toString(),
                        minuteGet.toString(),
                        secondGet.toString(),
                        priceGet.toString())
                    )
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                    // 数据存储工作结束

                    // 把数据初始化
                    textNum.text = "0"
                    countTextView.text = ""
                    totalNameTextView.text = ""
                    totalTextView.text = ""

                }
                if (price > BigDecimal(0)) {
                    price = BigDecimal(0.0)
                    finish()
                }
            }

        }
    }

    // 设置退场动画
    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.out_to_down)
    }

    // 根据类型选择修改颜色
    private fun themeInit() {
        payRadioButton.toggle() // 默认数据类型为 支出

        radioGroup.setOnCheckedChangeListener {
            _, _ ->
            if(payRadioButton.isChecked) { // 当前类型为支出时，整体颜色为红色
                textNum.setTextColor(Color.rgb(229, 105, 105))
                totalNameTextView.setTextColor(Color.rgb(229, 105, 105))
                totalTextView.setTextColor(Color.rgb(229, 105, 105))
                countTextView.setTextColor(Color.rgb(229, 105, 105))
                countTextView.setTextColor(Color.rgb(229, 105, 105))
                saveNum.setBackgroundColor(Color.rgb(229,105,105))
            }
            else { // 当前类型为收入时，整体颜色为绿色
                textNum.setTextColor(Color.rgb(83, 189, 140))
                totalNameTextView.setTextColor(Color.rgb(83, 189, 140))
                totalTextView.setTextColor(Color.rgb(83, 189, 140))
                countTextView.setTextColor(Color.rgb(83, 189, 140))
                textNum.setTextColor(Color.rgb(83, 189, 140))
                saveNum.setBackgroundColor(Color.rgb(83, 189, 140))
            }
        }
    }

    // 日期
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dateInit() {
        setTimeButton.text = "${time_year}-${time_month}-${time_day}"
        // 获得当前时间
        setTimeButton.setOnClickListener {

            var listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                time_year = cal.get(Calendar.YEAR)
                time_month = cal.get(Calendar.MONTH) + 1 // 月份要加一
                time_day = cal.get(Calendar.DAY_OF_MONTH)
                time_hour = cal.get(Calendar.HOUR_OF_DAY)
                time_minute = cal.get(Calendar.MINUTE)
                time_second = cal.get(Calendar.SECOND)
                Log.d("MainDate", "" +
                        "year${time_year}\n" +
                        "month:${time_month}\n" +
                        "day:${time_day}\n" +
                        "hour:${time_hour}\n" +
                        "second:${time_second}\n")
                setTimeButton.text = "${time_year}-${time_month}-${time_day}"
            }
            DatePickerDialog(this,
                listener,
                time_year,
                time_month - 1,
                time_day
            ).show()

        }
    }

    // 数字盘按钮
    private fun numEvent() {
        // 若整数部分最长 8 位
        // 小数点最多 2 位
        // 九个数字键
        oneNum.setOnClickListener { add_opr(1) }
        twoNum.setOnClickListener { add_opr(2) }
        threeNum.setOnClickListener { add_opr(3) }
        fourNum.setOnClickListener { add_opr(4) }
        fiveNum.setOnClickListener { add_opr(5) }
        sixNum.setOnClickListener { add_opr(6) }
        sevenNum.setOnClickListener { add_opr(7) }
        eightNum.setOnClickListener { add_opr(8) }
        nineNum.setOnClickListener { add_opr(9) }
        zeroNum.setOnClickListener { add_opr(0) }
        floatNum.setOnClickListener { add_opr(-1) }
        // 加号键
        addNum.setOnClickListener { num_opr(1) }
        // 减号键
        subNum.setOnClickListener { num_opr(0) }
        // 返回键
        backNum.setOnClickListener {
            var strNum = textNum.text.toString()
            if(strNum.length > 1)
                strNum = strNum.substring(0, strNum.length - 1)
            else
                strNum = "0"
            textNum.text = strNum
        }
        // 清零键
        clearNum.setOnClickListener {
            price = BigDecimal.valueOf(0.0)
            textNum.text = "0"
            countTextView.text = ""
            totalTextView.text = ""
            totalNameTextView.text = ""
        }
    }

    // 0~9 的操作函数
    private fun add_opr(num: Int) {
        var strNum = textNum.text.toString()
        var flag:Boolean = false // 检测是否有小数点
        var zero:Boolean = false // 检测是否是 0 开头
        var longLength: Int = 1 // 整数部分的长度
        var doubleLength: Int = 0 // 浮点数部分长度

        for(i in 0 until strNum.length) {
            if(i == 0 && strNum[i] == '0') {
                zero = true
            }
            if(!flag) longLength++
            else doubleLength++
            if(strNum[i] == '.') flag = true
        }
    //        println("flag:${flag} long:${longLength} double:${doubleLength}")
        if(zero && !flag) {
            if(num == -1) strNum = "0"
            else strNum = ""
        }

        if(longLength == 9 && num != -1) {
            Toast.makeText(this, "输入金额过大！", Toast.LENGTH_SHORT).show()
        }
        else if(doubleLength == 2) {
            Toast.makeText(this, "金额只保留小数点后两位！", Toast.LENGTH_SHORT).show()
        }
        else {
            when(num) {
                1 -> textNum.text = strNum + "1"
                2 -> textNum.text = strNum + "2"
                3 -> textNum.text = strNum + "3"
                4 -> textNum.text = strNum + "4"
                5 -> textNum.text = strNum + "5"
                6 -> textNum.text = strNum + "6"
                7 -> textNum.text = strNum + "7"
                8 -> textNum.text = strNum + "8"
                9 -> textNum.text = strNum + "9"
                0 -> textNum.text = strNum + "0"
                -1 -> if(!flag) textNum.text = strNum + "."
            }
        }
    }

    // 加、减号的操作函数
    private fun num_opr(num: Int) {
        var strOpr = countTextView.text.toString()
        var strNum = textNum.text.toString()

        var doubleLength: Int = 0
        var flag: Boolean = false
        for(i in 0 until strNum.length) {
            if(flag) doubleLength++
            if(strNum[i] == '.') flag = true
        }

        if(!flag) strNum += "."
        for(i in 0 until 2 - doubleLength) {
            strNum += "0"
        }

        if(strNum.toBigDecimal().compareTo(BigDecimal(0)) != 0)
        when(num) {
            1 -> { // 加号的操作
                var tmp = price + strNum.toBigDecimal()
                if(tmp.compareTo(BigDecimal.valueOf(MAX)) > 0){
                    Toast.makeText(this, "计算范围过大", Toast.LENGTH_SHORT).show()
                }
                else {
                    price = tmp
                    priceFunction()
                    if(strOpr == "") strOpr = strNum
                    else strOpr += " + " + strNum
                }
            }
            0 -> { // 减号的操作
                var tmp = price - strNum.toBigDecimal()
                if(tmp.compareTo(BigDecimal(-MAX)) < 0) {
                    Toast.makeText(this, "计算范围过小", Toast.LENGTH_SHORT).show()
                }
                else {
                    price = tmp
                    priceFunction()
                    if (strOpr == "") strOpr = "-" + strNum
                    else strOpr += " - " + strNum
                }
            }
        }
        textNum.text = "0"
        countTextView.text = strOpr
    }

    // 加、减号的操作函数 2
    private fun priceFunction() {
        var str = price.toString()
        var doubleLength: Int = 0
        var flag: Boolean = false

        for(i in 0 until str.length) {
            if(flag) doubleLength++
            if(str[i] == '.') flag = true
        }
        if(!flag) str += "."
        for(i in 0 until 2 - doubleLength) {
            str += "0"
        }

        for(i in 0 until str.length) {
            if(flag) doubleLength++
            if(str[i] == '.') flag = true
        }

        if(totalNameTextView.text.toString() == "") {
            totalNameTextView.text = "总计"
            totalTextView.text = str
        }
        else {
            totalTextView.text = str
        }
    }

}