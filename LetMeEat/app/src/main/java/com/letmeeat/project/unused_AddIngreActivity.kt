package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.DatePicker
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_register.*

class unused_AddIngreActivity : Activity() {
    lateinit var ingreName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_ingre)
        init()
    }

    fun init(){
        val intent = getIntent()
        ingreName = intent.getStringExtra("ingName")
        val mIName = findViewById<TextView>(R.id.ingrename)
        mIName.text = ingreName

        registerButton1.setOnClickListener {
            mSuccess()
        }
        registerButton2.setOnClickListener {
            mOnClose()
        }
    }
    fun mOnClose(){
        finish()
    }
    fun mSuccess(){
        val intent = Intent()
        val mDate = findViewById<DatePicker>(R.id.datepicker)
        val date:String = String.format(mDate.year.toString()+"/"+mDate.month.toString()+"/"+mDate.dayOfMonth.toString())
        intent.putExtra("result", "Close Popup")
        intent.putExtra("Ingredient",foodData(ingreName, date))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
