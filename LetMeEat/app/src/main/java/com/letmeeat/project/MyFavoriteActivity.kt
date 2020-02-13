package com.letmeeat.project

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.letmeeat.project.MainActivity.Companion.disgust_food_list
import com.letmeeat.project.MainActivity.Companion.myFavoriteData
import com.letmeeat.project.MainActivity.Companion.pdata
import kotlinx.android.synthetic.main.activity_my_favorite.*

class MyFavoriteActivity : AppCompatActivity() {
    lateinit var radapter: RecommendRecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_favorite)
        init()

    }
    fun init(){
        initadapter()
}
    fun initadapter(){
        Log.v("즐겨찾기 요리 추가됨? in sub",myFavoriteData.toString())

            val rlayoutManager = GridLayoutManager(this, 2)
            radapter = RecommendRecipeAdapter(myFavoriteData!!) //두번째 요리목록
            favoriteRecyclerView.layoutManager = rlayoutManager
            favoriteRecyclerView.adapter = radapter
            radapter.ItemClickListener = object:RecommendRecipeAdapter.OnItemClickListner{
            override fun OnItemClick(holder: RecommendRecipeAdapter.ViewHolder, view: View, data: RecipeData, position: Int
            ) {
                startMyPopupActivity4(data)
            }
        }
        confirm_button.setOnClickListener{
            mOnClose()
        }

    }
    fun mOnClose(){
        finish()
    }
    fun startMyPopupActivity4(rdata : RecipeData) //Recycler view 클릭 시
    {
        val intent = Intent(this, ShowRecipeActivity::class.java)
        intent.putExtra("recipe", rdata)
        intent.putExtra("product", pdata)
        intent.putExtra("disgust", disgust_food_list)

        //파이어베이스에 저장된 원소들 중 내가 고른 아이템의 순서가 존재한다면,
        /*for(i in 0..firebase_rdata.size-1){
            if(firebase_rdata[i]==rdata.recipe_num.toString()){
                intent.putExtra("likeData",true)
                startActivityForResult(intent, 1)
                return
            }

        }
        intent.putExtra("likeData",false)*/
        startActivity(intent)


    }


}
