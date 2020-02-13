package com.letmeeat.project

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koushikdutta.ion.Ion
import java.util.concurrent.CopyOnWriteArrayList

class productAdapter (var pData: CopyOnWriteArrayList<productData>)
    : RecyclerView.Adapter<productAdapter.ViewHolder>() { // will be error if no inner class(ViewHolder) defined
    interface OnItemClickListner {
        fun OnItemClick(holder: ViewHolder, view: View, data: productData, position: Int)
    }

    var ItemClickListener: OnItemClickListner? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.productlist, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return pData.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Ion.with(p0.ad_pimg).load(pData.get(p1).pImg)
        p0.ad_pname.text = pData.get(p1).pName
        p0.ad_pkind.text = pData.get(p1).pKind
        p0.ad_pera.text = "제철 : " + pData.get(p1).pEra

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ad_pimg: ImageView
        var ad_pname: TextView
        var ad_pkind: TextView
        var ad_pera: TextView

        init {
            ad_pimg = itemView.findViewById(R.id.plisticon)
            ad_pname = itemView.findViewById(R.id.product_name)
            ad_pkind = itemView.findViewById(R.id.product_kind)
            ad_pera = itemView.findViewById(R.id.product_era)

            itemView.setOnClickListener {
                val position = adapterPosition
                ItemClickListener?.OnItemClick(this, it, pData[position], position)
            }
        }
    }
}

