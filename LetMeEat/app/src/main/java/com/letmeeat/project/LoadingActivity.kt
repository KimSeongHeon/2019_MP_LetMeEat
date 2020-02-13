package com.letmeeat.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.gson.JsonParser
import java.util.*


class LoadingActivity : AppCompatActivity() {

    lateinit var type:String
    lateinit var id:String
    lateinit var nickname:String
    lateinit var imageURL: String
    lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        init()
        initView();

        var hd = Handler()
        thread_recipe.start()
    }
    fun init(){
        type = intent.getStringExtra("type")
        nickname = intent.getStringExtra("nickname")
        id = intent.getStringExtra("id")
        imageURL = intent.getStringExtra("imageURL")
        email = intent.getStringExtra("email")
    }
    val thread_recipe = Thread(Runnable {
        getxmlData_INDRT()
        if(MainActivity.ingredient_set.size>=746){
            LoadMain()
            Thread.interrupted()
        }
    })
    @SuppressLint("ResourceType")
    fun initView(){
        var imgAndroid = findViewById<ImageView>(R.id.img_android)
        var anim = AnimationUtils.loadAnimation(this,R.anim.loading)
        imgAndroid.animation = anim
    }

    fun getxmlData_INDRT() //xml 파싱. xml에서 재료 종류만 뽑아옴.
    {
        try {
            MainActivity.ingrtArray.clear()
            val scan = Scanner(resources.openRawResource(R.raw.ingrt_set))
            var page = ""
            while (scan.hasNextLine()) {
                val line = scan.nextLine()
                page += line
            }
            Log.v("page",page)
            val parser = JsonParser()
            val json = parser.parse(page).asJsonObject
            val jarr = json.getAsJsonArray("product").asJsonArray
            for(i in 0 until jarr.size()){
                val name = jarr[i].asJsonObject.get("name").asString
                val category1 = jarr[i].asJsonObject.get("category1").asString
                val category2 = jarr[i].asJsonObject.get("category2").asString
                MainActivity.ingredient_set.put(name,Pair(category1,category2))
                MainActivity.ingrtArray.add(ingrtdata(name,category1,category2,false,false))
            }
        }
        finally {
            var e: Exception? = null
            e?.printStackTrace()
        }
    }
    fun LoadMain(){
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("type", "kakao")
        intent.putExtra("nickname",nickname)
        intent.putExtra("id",id)
        intent.putExtra("imageURL",imageURL)
        intent.putExtra("email",email)
        startActivity(intent)
        finish()
    }
}
