package com.example.jizhangtest

import android.annotation.SuppressLint
import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_content.*
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    var cal = Calendar.getInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    var time_year = cal.get(Calendar.YEAR)
    @RequiresApi(Build.VERSION_CODES.N)
    var time_month = cal.get(Calendar.MONTH) + 1 // 月份要加一
    @RequiresApi(Build.VERSION_CODES.N)
    var time_day = cal.get(Calendar.DAY_OF_MONTH)
    @RequiresApi(Build.VERSION_CODES.N)
    var time_hour = cal.get(Calendar.HOUR_OF_DAY)
    @RequiresApi(Build.VERSION_CODES.N)
    var time_minute = cal.get(Calendar.MINUTE)
    @RequiresApi(Build.VERSION_CODES.N)
    var time_second = cal.get(Calendar.SECOND)

    // 创建数据库对象
    val dbHelper = MyDatabaseHelper(this, "Bills.db", 1)

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Log.d("TimeTest", "${time_year}-${time_month}-${time_day}-${time_hour}-${time_minute}-${time_second}")
        // 时期选择按钮：要根据时间来显示账单信息
        queryDatabase()
        timePickerButton.text = "${time_year}-${time_month}-${time_day}"
        timePickerButton.setOnClickListener {
            var listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                time_year = cal.get(Calendar.YEAR)
                time_month = cal.get(Calendar.MONTH) + 1 // 月份要加一
                time_day = cal.get(Calendar.DAY_OF_MONTH)
                timePickerButton.text = "${time_year}-${time_month}-${time_day}"
                queryDatabase()
            }

            DatePickerDialog(this,
                listener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // FloatingButton 上划时消失
        mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.getVisibility() === VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.getVisibility() !== VISIBLE) {
                    fab.show()
                }
            }
        })

        // 浮窗按钮
        fab.setOnClickListener {
            val intent = Intent(this, CountActivity::class.java)
            intent.putExtra("flag", "0")
            startActivity(intent)
            // 设置转场动画
            overridePendingTransition(R.anim.in_from_up, R.anim.no)
        }

        // 下拉刷新
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            queryDatabase()
            swipeRefresh.isRefreshing = false
        }
    }

    // 从数据库中取当月的内容
    fun queryDatabase() {
        val billsList = ArrayList<BillsContent>()
        var paytot: BigDecimal = BigDecimal(0)
        var incometot: BigDecimal = BigDecimal(0)
        val db = dbHelper.writableDatabase
        // 查询当前月的所有数据
        val cursor = db.rawQuery(
            "select * from Bills " +
                    "where date_year=${time_year} " +
                    "and date_month=${time_month} " +
                    "and date_day=${time_day}",
            null
        )

        if (cursor.moveToNext()) {
            do {
                // 遍历 cursor 对象，取出数据并打印
                val theme = cursor.getString(cursor.getColumnIndex("theme"))
                val price = cursor.getString(cursor.getColumnIndex("price"))
                val year = cursor.getInt(cursor.getColumnIndex("date_year"))
                val month = cursor.getInt(cursor.getColumnIndex("date_month"))
                val day = cursor.getInt(cursor.getColumnIndex("date_day"))
                val hour = cursor.getInt(cursor.getColumnIndex("date_hour"))
                val minute = cursor.getInt(cursor.getColumnIndex("date_minute"))
                val second = cursor.getInt(cursor.getColumnIndex("date_second"))
                val hint = cursor.getString(cursor.getColumnIndex("hint"))
                val bills = BillsContent(theme, year, month, day, hour, minute, second, hint, price.toBigDecimal())
                if(theme == "支出") {
                    Log.d("Main", "支出 ${paytot}+${price.toBigDecimal()}")
                    paytot = paytot.add(price.toBigDecimal())
                }
                else {
                    Log.d("Main", "收入 ${incometot}+${price.toBigDecimal()}")
                    incometot = incometot.add(price.toBigDecimal())
                }
                billsList.add(bills)
        //                Log.d(
        //                    "BillsTest",
        //                    "主题:${theme} 时间:${year}+${month}+${day} 备注:${hint} 价格:${price}"
        //                )
            } while (cursor.moveToNext())
        }

        val fil = ArrayList<BillsContent>()
        for(i in billsList.size - 1 downTo 0) {
            fil.add(billsList[i])
        }

        if(fil.size == 0) {
            hintLL.visibility = View.VISIBLE
        }
        else {
            hintLL.visibility = View.GONE
        }
        // 传入适配器
//        Log.d("Here", "this context is ${baseContext.toString()}")
        val layoutManager = GridLayoutManager(this, 1)
        mainRecyclerView.layoutManager = layoutManager
//        Log.d("Here", "next")
        val adapter = BillsContentAdapter(this, fil)
        mainRecyclerView.adapter = adapter

        payTotalShowTextView.text = paytot.toString()
        incomeTotalShowTextView.text = incometot.toString()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Test", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        queryDatabase()
        Log.d("Test", "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Test", "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Test", "OnStop()")
    }
}