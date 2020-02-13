package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.DatePicker
import com.letmeeat.project.MainActivity.Companion.slotNum_Ref
import kotlinx.android.synthetic.main.activity_buy_ingre.*

class BuyIngreActivity : Activity() {
    var ingList : ArrayList<String> = arrayListOf()
    var count = 0
    var foodList = arrayListOf<foodData>()

    var ingData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_buy_ingre)
        init()
    }
    fun init(){
        val intent = getIntent()
        ingData = intent.getStringExtra("ingData")

        BuyIngName.text = ingData
        radioBtn2.setOnCheckedChangeListener  { buttonView, isChecked ->
            //datepicker의 visiabllity를 바꿀꺼임
            if(isChecked) //체크되어있으면,
                datepicker2.visibility = View.INVISIBLE
            else
                datepicker2.visibility = View.VISIBLE
        }

        registerBtn.setOnClickListener {
            mSuccess()
        }
        registerBtn2.setOnClickListener {
            mOnClose()
        }
    }
    fun mOnClose(){
        finish()
    }
    fun mSuccess(){
        val intent = Intent()
        val mdate = findViewById<DatePicker>(R.id.datepicker2)
        lateinit var date:String
        if(mdate.visibility == View.VISIBLE){ //보일 경우는 값 넣어주기
            date= String.format(mdate.year.toString()+"/"+(mdate.month+1).toString()+"/"+mdate.dayOfMonth.toString())
        }
        else{ //안보일 경우는 임의의 값 넣어주기
            date = String.format("유통기한 없음")
        }
        if (MainActivity.shopOffadapter.ingList.isNotEmpty()) {
            //ingData
            var p1 = MainActivity.shopOffadapter.deleteList.find{
                it.fkind == ingData
            }
            // db 삭제하기
            MainActivity.conditionRef2.child(p1!!.fslotNum.toString()).removeValue()
            MainActivity.ingredient.remove(p1)
            MainActivity.shopOffadapter.deleteList.remove(p1)

        }
        intent.putExtra("result", "Close Popup")
        intent.putExtra("ingData",foodData(BuyIngName.text.toString(), date, slotNum_Ref))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
