
package com.letmeeat.fridgeproj

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.letmeeat.project.R
import com.letmeeat.project.foodData
import java.util.*


class MyShopOffAdapter(var items: ArrayList<foodData>)
    : RecyclerView.Adapter<MyShopOffAdapter.ViewHolder>(){

    var deleteList = arrayListOf<foodData>()
    var deleteindex = arrayListOf<Int>()
    var ingList = arrayListOf<String>()

    interface ListBtnClickListener {
        fun onListBtnClick(holder: MyShopOffAdapter.ViewHolder, view: View, data:foodData, position: Int)
    }
    var listBtnClickListener : ListBtnClickListener? = null


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(com.letmeeat.project.R.layout.offlineshop_card, p0, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ingr: CheckBox
        var ingr_t : TextView
        init{
            ingr = itemView.findViewById(R.id.c_ingre)
            ingr_t = itemView.findViewById(R.id.c_text)
            ingr.isChecked = false
            itemView.setOnClickListener{
                val position = adapterPosition
                listBtnClickListener?.onListBtnClick(this, it, items.get(position),  position)
            }
        }
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.ingr_t.text = items.get(p1).fkind

        p0.ingr.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                deleteList.add(items.get(p1))
                deleteindex.add(items.get(p1).fslotNum!!)
                ingList.add(items.get(p1).fkind!!)
                Log.v("delete index 추가(장바구니): ",items.get(p1).fkind!!.toString()+"를 추가하였습니다.")
            }
            else
            {
                deleteList.remove(items.get(p1))
                deleteindex.remove(items.get(p1).fslotNum!!)
                ingList.remove(items.get(p1).fkind)
                Log.v("delete index 삭제(장바구니): ",items.get(p1).fslotNum!!.toString()+"를 삭제하였습니다.")
            }
        }

        p0.ingr.isChecked = false
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
        {

            onBindViewHolder(p0, p1)
            Log.v("페이", p0.toString())
        }
        Log.v("asd p1 : ",p1.toString())
        Log.v("asd items :",items.toString())

    }
    fun delete(p1 : foodData) {
        items.remove(p1)
    }
}

