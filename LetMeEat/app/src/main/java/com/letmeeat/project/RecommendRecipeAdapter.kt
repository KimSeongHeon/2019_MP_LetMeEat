package com.letmeeat.project

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.concurrent.CopyOnWriteArrayList

class RecommendRecipeAdapter(var rData: CopyOnWriteArrayList<RecipeData>)
    :RecyclerView.Adapter<RecommendRecipeAdapter.ViewHolder>(){ // will be error if no inner class(ViewHolder) defined
    interface OnItemClickListner{
        fun OnItemClick(holder:ViewHolder,view:View,data:RecipeData,position:Int)
    }
    lateinit var context: Context
    var ItemClickListener:OnItemClickListner? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recommend_recipe_card,p0,false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return rData.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // define relations with recipelist(cardViewLayout) and elements of rData
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Glide.with(context).load(rData.get(p1).rimg).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(p0.ad_rimg)
        p0.ad_rname.text = rData.get(p1).rname // item은 현재 CardData이다
        p0.ad_sumry.text=rData.get(p1).rsumry
        p0.ad_nation.text=rData.get(p1).rnation
        when(p0.ad_nation.text.toString()){
            "한식"-> p0.ad_img.setImageResource(R.drawable.korea2)
            "일본"->p0.ad_img.setImageResource(R.drawable.japan2)
            "중국"->p0.ad_img.setImageResource(R.drawable.china2)
            "서양"-> p0.ad_img.setImageResource(R.drawable.us)
            "퓨전"->p0.ad_img.setImageResource(R.drawable.world)
            "동남아시아"->p0.ad_img.setImageResource(R.drawable.asia2)
            "이탈리아"->p0.ad_img.setImageResource(R.drawable.italy)
        }
        if(rData.size == 0){
           // p0.ad_rimg.setImageResource(R.drawable.abc_ic_star_half_black_16dp)
        }

    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var ad_img:ImageView
        var ad_rname:TextView
        var ad_rimg:ImageView
        var ad_sumry:TextView
        var ad_nation:TextView

        init{
            ad_img = itemView.findViewById(R.id.imageView)
            ad_rname = itemView.findViewById(R.id.tv_title)
            ad_rimg = itemView.findViewById(R.id.iv_image)
            ad_sumry = itemView.findViewById(R.id.tv_content)
            ad_nation= itemView.findViewById(R.id.tv_date)

            itemView.setOnClickListener{
                val position = adapterPosition
                ItemClickListener?.OnItemClick(this,it,rData[position],position)
            }
        }
    }

}