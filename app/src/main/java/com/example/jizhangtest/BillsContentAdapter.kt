package com.example.jizhangtest

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_content.view.*
import org.w3c.dom.Text
import kotlin.math.log

class BillsContentAdapter(val context: Context, val BillsContentList: List<BillsContent>) :
        RecyclerView.Adapter<BillsContentAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val billsContentTheme: TextView     = view.findViewById(R.id.LeftThemeContent)  // 类型
        val billsContentPrice: TextView     = view.findViewById(R.id.RightPriceContent) // 金额
        val billsContentHint: TextView      = view.findViewById(R.id.hintContent) // 备注
        val billsContentDate: TextView      = view.findViewById(R.id.dateContent) // 年月日
        val billsContentHMS: TextView       = view.findViewById(R.id.HMSContent) // 时分秒
        val dot: TextView                   = view.findViewById(R.id.dotThemeContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bills_content_item, parent, false)
        // 添加点击事件
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {

            // 设置 Dialog 弹窗
            var button1Dialog: Dialog = Dialog(parent.context, R.style.AppTheme)
            var view: View = LayoutInflater.from(parent.context).inflate(R.layout.dialog_content,null)
            var win = button1Dialog.window
            win?.setGravity(Gravity.BOTTOM)
            button1Dialog.setContentView(view)
            button1Dialog.setCanceledOnTouchOutside(true);
            var params = win?.attributes
            params?.width = WindowManager.LayoutParams.MATCH_PARENT
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT

            view.priceTextView.text = viewHolder.billsContentPrice.text // 金额
            view.themeTextView.text = viewHolder.billsContentTheme.text // 类型
            view.dateTextView.text = viewHolder.billsContentDate.text // 年月日
            view.HMSTextView.text = viewHolder.billsContentHMS.text // 时分秒
            if(viewHolder.billsContentHint.text.toString() == "null") { // 备注
                view.hintTextView.text = ""
            }
            else {
                view.hintTextView.text = viewHolder.billsContentHint.text
            }

            if(view.themeTextView.text.toString() == "支出") {
                view.priceTextView.setTextColor(Color.rgb(229, 105, 105))
                view.themeTextView.setTextColor(Color.rgb(229, 105, 105))
            }
            else {
                view.priceTextView.setTextColor(Color.rgb(83, 189, 140))
                view.themeTextView.setTextColor(Color.rgb(83, 189, 140))
            }
            button1Dialog.show()


            // 得到数据
            // 年月日
            val num = view.dateTextView.text.toString().split('-')
            val yearNum = num[0].toInt()
            val monthNum = num[1].toInt()
            val dayNum = num[2].toInt()
            // 时分秒
            val num2 = view.HMSTextView.text.toString().split('-')
            val hourNum = num2[0].toInt()
            val minuteNum = num2[1].toInt()
            val secondNum = num2[2].toInt()
            // 金额
            val priceNum = view.priceTextView.text.toString()
            // 类型
            val themeNum = view.themeTextView.text.toString()
            // 备注
            val hintNum = view.hintTextView.text.toString()

            // 删除按钮
            view.deleteButton.setOnClickListener {
                // 点击删除按钮时退出 Dialog
                button1Dialog.onBackPressed()

                val alertdialog = AlertDialog.Builder(parent.context)
                alertdialog.setTitle("删除")
                alertdialog.setMessage("确定要删除这条记录吗？")
                alertdialog.setCancelable(false)
                alertdialog.setPositiveButton("确定") {
                    _, _ ->
                    val dbHelper = MyDatabaseHelper(parent.context, "Bills.db", 1)
                    val db = dbHelper.writableDatabase
                    db.execSQL("delete from Bills " +
                            "where date_year=${yearNum} and date_month=${monthNum} and date_day=${dayNum} " +
                            "and date_hour=${hourNum} and date_minute=${minuteNum} and date_second=${secondNum} " +
                            "and theme=\"${themeNum}\" and price=\"${priceNum}\"")
                    Toast.makeText(parent.context, "请下拉刷新查看删除结果", Toast.LENGTH_SHORT).show()
                }

                alertdialog.setNegativeButton("取消") {
                    _, _ ->
                }
                alertdialog.show()
            }

            // 修改按钮
            view.changeButton.setOnClickListener {
                val intent = Intent(parent.context, CountActivity::class.java)
                intent.putExtra("theme", themeNum)
                intent.putExtra("year", yearNum)
                intent.putExtra("month", monthNum)
                intent.putExtra("day", dayNum)
                intent.putExtra("hour", hourNum)
                intent.putExtra("minute", minuteNum)
                intent.putExtra("second", secondNum)
                intent.putExtra("price", priceNum)
                intent.putExtra("hint", hintNum)
                intent.putExtra("flag", "1")
                parent.context.startActivity(intent)
                Toast.makeText(parent.context, "修改", Toast.LENGTH_SHORT).show()

                button1Dialog.onBackPressed()
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bills = BillsContentList[position]
        holder.billsContentTheme.text = bills.theme // 给 ThemeTextView 赋值
        holder.billsContentPrice.text = bills.price.toString() // 给 PriceTextView 赋值
        holder.billsContentHint.text = bills.hint // 给 hint 赋值
        holder.billsContentDate.text = "${bills.year}-${bills.month}-${bills.day}" // 给 年月日 赋值
        holder.billsContentHMS.text = "${bills.hour}-${bills.minute}-${bills.second}" // 给 时分秒 赋值

        if(bills.theme.toString() == "支出") {
            holder.dot.setTextColor(Color.rgb(229, 105, 105))
            holder.billsContentPrice.setTextColor(Color.rgb(229, 105, 105))
        }
        else {
            holder.dot.setTextColor(Color.rgb(83, 189, 140))
            holder.billsContentPrice.setTextColor(Color.rgb(83, 189, 140))
        }
    }

    override fun getItemCount(): Int  = BillsContentList.size
}