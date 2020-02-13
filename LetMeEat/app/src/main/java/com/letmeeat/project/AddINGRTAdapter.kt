package com.letmeeat.project

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView


class AddINGRTAdapter(var rData: ArrayList<ingrtdata>)
    : RecyclerView.Adapter<AddINGRTAdapter.ViewHolder>(){ // will be error if no inner class(ViewHolder) defined

    interface OnBtnClickListener {
        fun onBtnClick(holder: ViewHolder, view: View, data:String, position: Int)
    }
    var onBtnClickListener : OnBtnClickListener? = null

    interface OnItemClickListner {
        fun OnItemClick(holder: AddINGRTAdapter.ViewHolder, view: View, data: ingrtdata, position: Int)
    }

    var ItemClickListener: OnItemClickListner? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return
        val v = LayoutInflater.from(p0.context).inflate(R.layout.add_ingr_card,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return rData.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // define relations with recipelist(cardViewLayout) and elements of rData
        p0.fName.text = rData.get(p1).name

        val ingName = rData.get(p1)
        val fdlt = p0.fdlt

        if(rData.get(p1).isProduct) {
            p0.itemView.setBackgroundColor(Color.parseColor("#F2FFE9"))
            // 농산물인것만 리스너 달아줘야함
            p0.itemView.setOnClickListener {
                val position = p1
                ItemClickListener?.OnItemClick(this.ViewHolder(it), it, rData[position], position)
                Log.v("itemclick", "product")
            }
        }
        if(rData.get(p1).isLack) {
            // 재료가 부족한 재료이면 장바구니 이미지 출력
            fdlt.setImageResource(R.drawable.shopping_cart)
            fdlt.setOnClickListener{
                onBtnClickListener?.onBtnClick(p0, it, ingName.name,  p1)
                Log.v("itemclick", "lack")
            }
        }
        if(rData.get(p1).isDisgust) {
            p0.fName.setTextColor(Color.parseColor("#FF0000"))
        }
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var fName: TextView
        var fdlt : ImageButton

        init{
            fName = itemView.findViewById(R.id.add_ingr)
            fdlt= itemView.findViewById(R.id.add_Shoppinglist)
        }


    }
}