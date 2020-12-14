package com.example.jizhangtest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
        SQLiteOpenHelper(context, name, null, version){

    private val createBills = "create table Bills (" +
            "theme text," +
            "date_year integer," +
            "date_month integer," +
            "date_day integer," +
            "date_hour integer," +
            "date_minute integer," +
            "date_second integer," +
            "hint text," +
            "price text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createBills)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}