package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.letmeeat.project.MainActivity.Companion.conditionRef2
import com.letmeeat.project.MainActivity.Companion.conditionRef3
import com.letmeeat.project.MainActivity.Companion.conditionRef5
import com.letmeeat.project.MainActivity.Companion.ingredient
import com.letmeeat.project.MainActivity.Companion.likeCountData
import com.letmeeat.project.MainActivity.Companion.rdata
import com.letmeeat.project.MainActivity.Companion.shopOffadapter
import com.letmeeat.project.MainActivity.Companion.slotNum_Bas
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_show_recipe.*


class ShowRecipeActivity : Activity() {

    lateinit var main_adapter:AddINGRTAdapter
    lateinit var sub_adapter:AddINGRTAdapter
    var id:Int = 0
    var main:ArrayList<ingrtdata> = ArrayList<ingrtdata>()
    var sub:ArrayList<ingrtdata> = ArrayList<ingrtdata>()
    var img : String = ""
    var name : String = ""
    var rdataelement : RecipeData? = null
    var like : Boolean = false
    var pdata : ArrayList<productData> = arrayListOf()
    var main_product = arrayListOf<ingrtdata>() // 주재료에서 농산물인 것
    var sub_product = arrayListOf<ingrtdata>() // 부재료에서 농산물인 것
    var likeCount = 0
    var disgust_food_list  = ArrayList<ingrtdata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)
        init()
    }
    fun init(){
        var recipe = StringBuffer()
        val intent = getIntent()
        id = intent.getIntExtra("ID",0)
        rdataelement = intent.getSerializableExtra("recipe") as RecipeData
        pdata = intent.getSerializableExtra("product") as ArrayList<productData>
       // firebase_likeData=intent.getBooleanExtra("likeData")

        disgust_food_list = intent.getSerializableExtra("disgust") as ArrayList<ingrtdata>

        id = rdataelement!!.recipe_num
        name = rdataelement!!.rname
        img = rdataelement!!.rimg
        main = rdataelement!!.rmain
        sub = rdataelement!!.rsub
        like = rdataelement!!.rStar

        isProduct(pdata, main, sub)
        isLack(rdataelement!!.rlackmain, rdataelement!!.rlacksub)
        isDisgust(disgust_food_list, main, sub)

        rec_name.text = name
        var foodimg = findViewById<ImageView>(R.id.rec_img)
        Ion.with(foodimg).load(img)
        for(i in 1..4)init_SHOW_RECIPE(i,recipe)
        Log.v("레시피 final",recipe.toString())
        confirm_button.setOnClickListener{
            mOnClose()
        }
        if(like) {
            LikeButton.setImageResource(R.drawable.like)
        }
            else{
            LikeButton.setImageResource(R.drawable.like2)
        }
        LikeButton.setOnClickListener {
            var likeCount:Int=0
            var likeCountIndex:Int=-1
            for(i in 0..likeCountData.size-1)  //좋아요 눌린 레서피 인지 확인
            {
                if (likeCountData[i].first == rdataelement!!.recipe_num) {
                   likeCount =likeCountData[i].second // 좋아요 눌린 레서피 값을 들고 break
                    likeCountIndex=i // 좋아요 눌린 레서피의 인덱스값
                    break
                }

            }
            if(likeCountIndex == -1) // 좋아요가 눌려지지 않은 레서피였다면
            {
                likeCountData.add(Pair(rdataelement!!.recipe_num,0)) //좋아요 눌린 레서피에 등록한다. 좋아요 수는 0
                likeCountIndex = likeCountData.size-1 //좋아요 눌린 레서피의 인덱스는 size-1
            }
            Log.v("좋아요 바뀌기 전 ",likeCountIndex.toString() + "번째 데이터 :"+likeCountData[likeCountIndex].second)
            if(like) // 눌렀을 때의 상태가 좋아요였다면(좋아요를 취소한다면)
            {
                LikeButton.setImageResource(R.drawable.like2)
                conditionRef3.child(rdataelement!!.recipe_num.toString()).removeValue()
                conditionRef5.child(rdataelement!!.recipe_num.toString()).setValue(likeCount-1)
                likeCountData[likeCountIndex]=likeCountData[likeCountIndex].copy(second = likeCount-1)
            }
            else // 눌렀을 때의 상태가 안좋아요였다면(좋아요를 클릭한다면)
            {

                LikeButton.setImageResource(R.drawable.like)
                conditionRef3.child(rdataelement!!.recipe_num.toString()).setValue("true")
                conditionRef5.child(rdataelement!!.recipe_num.toString()).setValue(likeCount+1)
                likeCountData[likeCountIndex]=likeCountData[likeCountIndex].copy(second = likeCount+1)
            }
            Log.v("좋아요 바뀌기 후 ",likeCountIndex.toString() + "번째 데이터 :"+likeCountData[likeCountIndex].second)
            like = !like
            for(i in 0..rdata.size-1){
                if(rdata[i].recipe_num==rdataelement!!.recipe_num){
                    rdata[i].rStar = like
                 }

              }


        }
        initadapter()
        main_adapter.onBtnClickListener = object : AddINGRTAdapter.OnBtnClickListener{
            override fun onBtnClick(holder: AddINGRTAdapter.ViewHolder, view: View, data: String, position: Int) {
                startMyPopupActivity6(data)
            }
        }
        sub_adapter.onBtnClickListener = object : AddINGRTAdapter.OnBtnClickListener{
            override fun onBtnClick(holder: AddINGRTAdapter.ViewHolder, view: View, data: String, position: Int) {
                startMyPopupActivity6(data)
            }
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
        information_Button.setOnClickListener {
            if(info_linear.visibility == View.INVISIBLE){
                info_linear.visibility = View.VISIBLE
            }
            else{
                info_linear.visibility = View.INVISIBLE
            }
        }

    }

    override fun onDestroy() {

        super.onDestroy()

    }

    fun isProduct(pdata : ArrayList<productData>, main:ArrayList<ingrtdata>, sub:ArrayList<ingrtdata>) {
        var all_pname = arrayListOf<String>() // 전체 농산물 이름
        for (i in 0 until pdata.size) {
            all_pname.add(pdata[i].pName)
            for(j in 0 until main.size) {
                if(all_pname[i].equals(main[j].name)) {
                    main_product.add(main[j])
                    main[j].isProduct = true
                    Log.v("main_product", main[j].name)
                }
            }
        }
        for (i in 0 until pdata.size) {
            for(j in 0 until sub.size) {
                if(all_pname[i].equals(sub[j].name)) {
                    sub_product.add(sub[j])
                    sub[j].isProduct = true
                    Log.v("sub_product", sub[j].name)
                }
            }
        }
    }
    fun isLack(rlackmain:ArrayList<ingrtdata>, rlacksub:ArrayList<ingrtdata>) {
        for(i in 0 until main.size) {
            for(j in 0 until rlackmain.size) {
                if(rlackmain[j].name.equals(main[i].name)) {
                    main[i].isLack = true
                }
            }
        }
        for (i in 0 until sub.size) {
            for(j in 0 until rlacksub.size) {
                if(rlacksub[j].name.equals(sub[i].name)) {
                    sub[i].isLack = true
                }
            }
        }
    }
    fun isDisgust(disgust_food_list : ArrayList<ingrtdata>, main:ArrayList<ingrtdata>, sub:ArrayList<ingrtdata>) {
        for (i in 0 until main.size - 1) {
            if (disgust_food_list.contains(ingrtdata(main[i].name, main[i].category1, main[i].category2)) ||
                disgust_food_list.contains(ingrtdata("전체", main[i].category1, main[i].category2)) ||
                disgust_food_list.contains(ingrtdata("전체", main[i].category1, "0"))
            )
                main[i].isDisgust = true;
        }
        for (i in 0 until sub.size - 1) {
            if (disgust_food_list.contains(ingrtdata(sub[i].name, sub[i].category1, sub[i].category2)) ||
                disgust_food_list.contains(ingrtdata("전체", sub[i].category1, sub[i].category2)) ||
                disgust_food_list.contains(ingrtdata("전체", sub[i].category1, "0"))
            )
                sub[i].isDisgust = true;
        }
    }
    fun startMyPopupActivity6(ingName : String){ //  부족한 재료 추가 팝업
        var eq = false
        for (i in 0 until ingredient.size) {
            Log.v("idataaa", ingredient[i].fkind.toString())
            if (ingredient[i].fkind.toString().equals(ingName)) {
                eq = true
            }
        }
        if(eq) {
            Toast.makeText(this, "장바구니에 같은 재료가 있습니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            val intent = Intent(this, AddBasket2Activity::class.java)
            intent.putExtra("ingname", ingName)
            startActivityForResult(intent, 2)
        }
    }

    fun startMyPopupActivity9(name: String, img: String, kind : String, era : String,
                              effect : String, pick_mth : String, store_mth : String, cook_mth:String)
    //Recycler view 클릭 시
    // 농산물 상세정보 출력
    {
        val intent = Intent(this, ShowProductActivity::class.java)
        intent.putExtra("IMG",img)
        intent.putExtra("NAME",name)
        intent.putExtra("KIND",kind)
        intent.putExtra("ERA",era)
        intent.putExtra("EFFECT",effect)
        intent.putExtra("PICK_MTH",pick_mth)
        intent.putExtra("STORE_MTH",store_mth)
        intent.putExtra("COOK_MTH",cook_mth)

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==2){
            if(resultCode== Activity.RESULT_OK){
                Log.v("please", "showrecip")
                Toast.makeText(this,"등록되었습니다.", Toast.LENGTH_SHORT).show()
                val popup_food = data!!.getStringExtra("basket")
                slotNum_Bas++
                ingredient.add(foodData(popup_food,null,slotNum_Bas))
                conditionRef2.child(slotNum_Bas.toString()).setValue(popup_food)

                shopOffadapter.notifyDataSetChanged()
            }
        }
    }
    fun initadapter()
    {
        val rmainlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val rsublayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        main_adapter = AddINGRTAdapter(main)
        shortage_recycler_view_main.layoutManager = rmainlayoutManager //세번쨰 요리백과
        shortage_recycler_view_main.adapter = main_adapter
        sub_adapter = AddINGRTAdapter(sub)
        shortage_recycler_view_sub.layoutManager = rsublayoutManager
        shortage_recycler_view_sub.adapter =  sub_adapter

        main_adapter.ItemClickListener = object:AddINGRTAdapter.OnItemClickListner{
            override fun OnItemClick(holder: AddINGRTAdapter.ViewHolder, view: View, data: ingrtdata, position: Int) {
                // 재료 이름과 같은 농산물 data를 출력
                var selected_pdata : productData? = null
                for (i in 0 until pdata.size) {
                    if(pdata[i].pName.equals(data.name)) {
                        selected_pdata = pdata[i]
                    }
                }
                Log.v("selected_pdata",selected_pdata!!.pName)
                startMyPopupActivity9(selected_pdata!!.pName, selected_pdata!!.pImg, selected_pdata!!.pKind, selected_pdata!!.pEra,
                    selected_pdata!!.pEffect, selected_pdata!!.ppick_Mth, selected_pdata!!.pstore_Mth, selected_pdata!!.pcook_Mth)
            }
        }
        sub_adapter.ItemClickListener = object:AddINGRTAdapter.OnItemClickListner{
            override fun OnItemClick(holder: AddINGRTAdapter.ViewHolder, view: View, data: ingrtdata, position: Int) {
                // 재료 이름과 같은 농산물 data를 출력
                var selected_pdata : productData? = null
                for (i in 0 until pdata.size) {
                    if(pdata[i].pName.equals(data.name)) {
                        selected_pdata = pdata[i]
                    }
                }
                Log.v("selected_pdata",selected_pdata!!.pName)
                startMyPopupActivity9(selected_pdata!!.pName, selected_pdata!!.pImg, selected_pdata!!.pKind, selected_pdata!!.pEra,
                    selected_pdata!!.pEffect, selected_pdata!!.ppick_Mth, selected_pdata!!.pstore_Mth, selected_pdata!!.pcook_Mth)
            }
        }
    }
    fun parsingJsonWeb(json: JsonObject,recipe:StringBuffer){
        var value = json.get("Grid_20150827000000000228_1").asJsonObject
        val row = value.get("row").asJsonArray
        for(i in 0 until row.size()){
            value = row.get(i).asJsonObject
            val num = value.get("COOKING_NO").asString
            val method = value.get("COOKING_DC").asString
            recipe.append("\n"+ num.toString()+" "+method.toString()+"\n")
            Log.v("레시피 value",recipe.toString())


        }
        val R_text = findViewById<TextView>(R.id.recipe_text)
        R_text.text = recipe
    }
    fun init_SHOW_RECIPE(i:Int,recipe: StringBuffer){
        var first_page = ((i-1)*(1000)).toString()
        var last_page = (((i*1000))-1).toString()
        Ion.with(this).load("http://211.237.50.150:7080/openapi/" +
                "d54b81c630464f93c2eda9be92fccccc6eda4f4b65c7d23360ce262ef7170be0/" +
                "json/" + "Grid_20150827000000000228_1/" +  first_page+ "/" +last_page+"/"+"?RECIPE_ID="+id.toString())
            .asJsonObject().setCallback { e, result ->
                parsingJsonWeb(result,recipe)
            }
    }
    fun mOnClose(){
        finish()
    }
    fun setMessage() : String {
        var message = ""
        message += "[ "+name+" ]\n\n주재료 : "
        for(i in 0..main.size-1) {
            message+=main[i].name+" "
        }
        message += "\n부재료 : "
        for(i in 0..sub.size-1) {
            message+=sub[i].name + " "
        }
        message += "\n\n레시피 : \n"+recipe_text.text.toString()

        return message
    }
}
