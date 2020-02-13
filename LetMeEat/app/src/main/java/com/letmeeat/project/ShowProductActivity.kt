package com.letmeeat.project

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_show_product.*
import kotlinx.android.synthetic.main.activity_show_recipe.confirm_button
import kotlinx.android.synthetic.main.activity_show_recipe.kakaoLinkBtn
import java.util.*

class ShowProductActivity : AppCompatActivity() {
//    var id:Int = 0;
    var pick_mth : String = "";
    var img : String = ""; var store_mth : String = "";
    var name : String = ""; var era : String = "";
    var kind : String = ""; var cook_mth : String = "";
    var effect : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product)
        init()
    }

    fun init(){
        val intent = getIntent()
        img = intent.getStringExtra("IMG")
        name = intent.getStringExtra("NAME")
        kind = intent.getStringExtra("KIND")
        era = intent.getStringExtra("ERA")
        effect = intent.getStringExtra("EFFECT")
        pick_mth = intent.getStringExtra("PICK_MTH")
        store_mth = intent.getStringExtra("STORE_MTH")
        cook_mth = intent.getStringExtra("COOK_MTH")

        var st = StringTokenizer(effect, "-")
        effect = ""
        while(st.hasMoreTokens()) {
            effect += "- "+st.nextToken()+"\n"
        }
        st = StringTokenizer(pick_mth, ".")
        pick_mth = ""
        while(st.hasMoreTokens()) {
            pick_mth += st.nextToken()+".\n"
        }
        st = StringTokenizer(store_mth, ".")
        store_mth = ""
        while(st.hasMoreTokens()) {
            store_mth += st.nextToken()+".\n"
        }
        st = StringTokenizer(cook_mth, ".")
        cook_mth = ""
        while(st.hasMoreTokens()) {
            cook_mth += st.nextToken()+".\n"
        }
        pick_mth = pick_mth.replace("？", "")
        store_mth = store_mth.replace("？", "")
        cook_mth = cook_mth.replace("？", "")

        pro_name.text = name
        pro_kind.text = kind
        pro_era.text = "제철 : "+era
        pro_effect.text = effect
        pro_pick.text = pick_mth
        pro_store.text = store_mth
        pro_cook.text = cook_mth
        var proimg = findViewById<ImageView>(R.id.pro_img)
        Ion.with(proimg).load(img)

        confirm_button.setOnClickListener{
            mOnClose()
        }


        kakaoLinkBtn.setOnClickListener {
            val message = setMessage()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.setPackage("com.kakao.talk")
            startActivity(intent)

//            val dataUri = Uri.parse(img)
//            val intent2 = Intent(Intent.ACTION_SEND)
//            intent2.type = "image/*"
//            intent2.putExtra(Intent.EXTRA_STREAM, dataUri)
//            intent2.setPackage("com.kakao.talk")
//            startActivity(intent)


        }

    }
    fun mOnClose(){
        finish()
    }
    fun setMessage() : String {
        var message = ""
        message += "< "+name+" >\n\n종류 : "+kind+"\n제철 : "+era+"\n\n효능 : \n"+
                effect+"\n고르는 법 : \n"+pick_mth+"\n보관 방법 : \n"+store_mth+
                "\n요리 방법 : \n"+cook_mth

        return message
    }
}
