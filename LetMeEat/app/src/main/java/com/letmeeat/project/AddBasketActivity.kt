package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import com.letmeeat.project.MainActivity.Companion.ingredient
import kotlinx.android.synthetic.main.activity_register.*
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class AddBasketActivity : Activity() {

    lateinit var adapter:ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_basket)
        init()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return if (event!!.getAction() == MotionEvent.ACTION_OUTSIDE) {
            false
        }
        else true

    }

    fun init(){
        val intent = getIntent()
        val data = intent.getStringExtra("data")

        val INDRT_SET:HashSet<String> = HashSet((intent.getSerializableExtra("INDRT_SET") as HashMap<String, Pair<String, String>>).keys)
        var INDRT_LIST = ArrayList(INDRT_SET)
        adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,INDRT_LIST)
        autoCompleteTextViewkind.setAdapter(adapter)
        autoCompleteTextViewkind.setOnItemClickListener { parent, view, position, id ->
            autoCompleteTextViewkind.isEnabled= false
        }
        registerButton1.setOnClickListener {
            var eq = false
            if (!autoCompleteTextViewkind.isEnabled) {
                for (i in 0 until ingredient.size) {
                    if (ingredient[i].fkind.toString().trim().equals(autoCompleteTextViewkind.text.toString().trim())) {
                        eq = true
                    }
                }
                if(eq) {
                    Toast.makeText(this, "장바구니에 같은 재료가 있습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    mSuccess()
                }
            }
            else {
                Toast.makeText(this,"재료를 드롭다운 메뉴에서 선택하여 주십시오.", Toast.LENGTH_SHORT).show()
            }
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
        intent.putExtra("result", "Close Popup")
       intent.putExtra("basket",autoCompleteTextViewkind.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
