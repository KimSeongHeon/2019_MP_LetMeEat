package com.letmeeat.project

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.koushikdutta.ion.Ion
import java.util.concurrent.CopyOnWriteArrayList

class RecipeAdapter(var rData: CopyOnWriteArrayList<RecipeData>, var firebaseDatabase: DatabaseReference, var firebase_rdata:ArrayList<String>)
    :RecyclerView.Adapter<RecipeAdapter.ViewHolder>(){ // will be error if no inner class(ViewHolder) defined
    interface OnItemClickListner{
        fun OnItemClick(holder:ViewHolder,view:View,data:RecipeData,position:Int)
    }
    var ItemClickListener:OnItemClickListner? = null
    lateinit var bitmap:Bitmap
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recipelist,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return rData.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // define relations with recipelist(cardViewLayout) and elements of rData
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Ion.with(p0.ad_img).load(rData.get(p1).rimg)
        p0.ad_rname.text = rData.get(p1).rname // item은 현재 CardData이다
        p0.ad_rsumry.text=rData.get(p1).rsumry
        p0.ad_rnation.text=rData.get(p1).rnation
        p0.ad_rtime.text=rData.get(p1).rtime
        p0.ad_rlevel.text=rData.get(p1).rlevel
        p0.ad_rcal.text=rData.get(p1).rcal


        p0.ad_num.text = "주재료 :" + rData.get(p1).rlackmain.size.toString()+" 개 부족!!"+
                "부재료 :"+ rData.get(p1).rlacksub.size.toString()+" 개 부족!!"
       /* if(rData.get(p1).rStar)
            p0.ad_star.setImageResource(R.drawable.ic_star_black_24dp)
        else
            p0.ad_star.setImageResource(R.drawable.ic_star_border_black_24dp)*/
       /* p0.ad_star.setOnClickListener {

            if(rData.get(p1).rStar) {
                p0.ad_star.setImageResource(R.drawable.ic_star_border_black_24dp)
                firebaseDatabase.child(rData.get(p1).recipe_num.toString()).removeValue()
            } else {
                p0.ad_star.setImageResource(R.drawable.ic_star_black_24dp)
                firebaseDatabase.child(rData.get(p1).recipe_num.toString()).setValue("true")
            }
            rData.get(p1).rStar = !rData.get(p1).rStar
            firebase_rdata.add(rData.get(p1).recipe_num.toString())
        }*/

    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var ad_img:ImageView
        var ad_rname:TextView
        var ad_rsumry:TextView
        var ad_rnation:TextView
        var ad_rtime:TextView
        var ad_rlevel:TextView
        var ad_rcal:TextView
        var ad_num:TextView
      //  var ad_star:ImageView

        init{
            ad_img = itemView.findViewById(R.id.listicon)
            ad_rname = itemView.findViewById(R.id.recipe_name)
            ad_rsumry= itemView.findViewById(R.id.recipe_sumry)
            ad_rnation = itemView.findViewById(R.id.recipe_nation)
            ad_rtime = itemView.findViewById(R.id.recipe_time)
            ad_rlevel = itemView.findViewById(R.id.recipe_level)
            ad_rcal = itemView.findViewById(R.id.recipe_cal)
            ad_num = itemView.findViewById(R.id.lack_Ingredient)
           // ad_star = itemView.findViewById(R.id.starBtn)

            itemView.setOnClickListener{
                val position = adapterPosition
                ItemClickListener?.OnItemClick(this,it,rData[position],position)
            }
        }
    }

}