package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import kotlinx.android.synthetic.main.activity_add_basket2.*

class AddBasket2Activity : Activity() {

    var ingname = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_basket2)
        init()
    }

    fun init(){
        val intent = getIntent()
        ingname = intent.getStringExtra("ingname")
        //txtText.setText(data)
        iName.text = ingname
//        adingtext.text = "장바구니에 "+ingname+" 추가하기"
        yesBtn.setOnClickListener {
            mSuccess()
        }
        noBtn.setOnClickListener {
            mOnClose()
        }
    }
    fun mOnClose(){
        finish()
    }
    fun mSuccess(){
        val intent = Intent()
        intent.putExtra("result", "Close Popup")
        intent.putExtra("basket",ingname)
        Log.v("please", ingname)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
