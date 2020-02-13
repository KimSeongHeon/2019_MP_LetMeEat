package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import com.letmeeat.project.MainActivity.Companion.m_fdata
import kotlinx.android.synthetic.main.activity_register.*

class registerActivity : Activity() {
    lateinit var imm : InputMethodManager
    lateinit var et : EditText
    var slotNum_Ref : Int = -5959
    lateinit var adapter:ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_register)
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
        slotNum_Ref = intent.getIntExtra("slotNum",-5959)
        val INDRT_SET = (intent.getSerializableExtra("INDRT_SET") as HashMap<String,Pair<String,String>>).keys
        var INDRT_LIST = ArrayList<String>()
        val it: Iterator<String> = INDRT_SET.iterator()
        while(it.hasNext()){
            var str = it.next().toString()
            INDRT_LIST.add(str)
        }
        var drawerlayout =findViewById<DrawerLayout>(R.id.drawerLayout)
        var drawerView = findViewById<LinearLayout>(R.id.drawer)

        adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,INDRT_LIST)
        autoCompleteTextViewkind.setAdapter(adapter)

        ingreList.setOnItemClickListener { parent, view, position, id ->
            autoCompleteTextViewkind.setText(parent.getItemAtPosition(position).toString())
            autoCompleteTextViewkind.isEnabled = false
            drawerlayout.closeDrawer(drawerView)

        }
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        registerButton1.setOnClickListener {
            var eq = false
            if (!autoCompleteTextViewkind.isEnabled) {
                for (i in 0 until m_fdata.size) {
                    Log.v("fdataaa", m_fdata[i].fkind.toString())
                    if (m_fdata[i].fkind.toString().trim().equals(autoCompleteTextViewkind.text.toString().trim())) {
                        eq = true
                    }
                }
                if(eq) {
                    Toast.makeText(this, "냉장고에 같은 재료가 있습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    mSuccess()
                }
                Log.v("fdataaaauto", autoCompleteTextViewkind.text.toString())
            }
            else {
                Toast.makeText(this,"재료를 드롭다운 메뉴에서 선택하여 주십시오.", Toast.LENGTH_SHORT).show()
            }
        }
        registerButton2.setOnClickListener {
            mOnClose()
        }
        radioBtn.setOnCheckedChangeListener  { buttonView, isChecked ->
            //datepicker의 visiabllity를 바꿀꺼임
            if(isChecked) //체크되어있으면,
                datepicker.visibility = View.INVISIBLE
            else
                datepicker.visibility = View.VISIBLE
        }
        findKindButton.setOnClickListener {
                drawerlayout.openDrawer(drawerView)
            ingreList.adapter = adapter
        }
    }
    fun LinearOnClick(view : View) {
        imm.hideSoftInputFromWindow(et.windowToken, 0)
    }
    fun mOnClose(){
        finish()
    }
    fun mSuccess(){
        val intent = Intent()
        val mdate = findViewById<DatePicker>(R.id.datepicker)
        lateinit var date:String
        if(mdate.visibility == View.VISIBLE){ //보일 경우는 값 넣어주기
            date= String.format(mdate.year.toString()+"/"+(mdate.month+1).toString()+"/"+mdate.dayOfMonth.toString())
        }
        else{ //안보일 경우는 임의의 값 넣어주기
            date = String.format("유통기한 없음")
        }
        slotNum_Ref++
        intent.putExtra("result", "Close Popup")

        intent.putExtra("Ingredient",foodData(autoCompleteTextViewkind.text.toString(),date,slotNum_Ref))
        setResult(RESULT_OK, intent)
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult", "onActivityResult: .")
        if (resultCode == Activity.RESULT_OK) {
            val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val re = scanResult.contents
            Log.d("onActivityResult", "onActivityResult: .$re")


        }
    }

}
