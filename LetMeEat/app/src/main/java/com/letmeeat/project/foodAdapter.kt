package com.letmeeat.project

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView


class foodAdapter(var fData: ArrayList<foodData>,var map:HashMap<String,Pair<String,String>>)
    : RecyclerView.Adapter<foodAdapter.ViewHolder>(){ // will be error if no inner class(ViewHolder) defined
    var isDarr = Array(fData.size,{i->true})
    var deleteList = arrayListOf<foodData>()
    var deleteindex = arrayListOf<Int>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.listview_item,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return fData.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // define relations with recipelist(cardViewLayout) and elements of rData
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        if(!fData.get(p1).fdue.equals("유통기한 없음")) {
            p0.fDue.text=fData.get(p1).fdue+"까지"
        }
        else {
            p0.fDue.text=fData.get(p1).fdue
        }
        p0.fkind.text = fData.get(p1).fkind

        p0.fdlt.visibility = ImageButton.INVISIBLE

        val fdlt = p0.fdlt
        fdlt.setImageResource(R.drawable.ic_delete_forever_black_24dp)

        when((map.get(fData.get(p1).fkind))!!.first){
            "1"-> p0.fimg.setImageResource(R.drawable.broccoli)
            "2"-> p0.fimg.setImageResource(R.drawable.milk)
            "3"-> p0.fimg.setImageResource(R.drawable.egg)
            "4"-> p0.fimg.setImageResource(R.drawable.fish)
            "5"-> p0.fimg.setImageResource(R.drawable.turkey)
            "6"-> p0.fimg.setImageResource(R.drawable.steak)
            "7"-> p0.fimg.setImageResource(R.drawable.guitar)
            "8"-> p0.fimg.setImageResource(R.drawable.spices)
            "9"-> p0.fimg.setImageResource(R.drawable.grain)



        }

    }
    override fun onBindViewHolder(p0: ViewHolder, position: Int, payloads: MutableList<Any>) {
        Log.v("deleteindex : ", deleteindex.toString())
        if(payloads.isEmpty()) {
            onBindViewHolder(p0, position)
            Log.v("페이",p0.toString())
        }
        else {
            for (payload in payloads) {
                if (payload is String) {
                    var type = payload.toString()
                    if (TextUtils.equals(type, "clickOn")) {
                        p0.fdlt.visibility = ImageButton.VISIBLE
                    }
                    else if (TextUtils.equals(payload, "clickOff")) {
                        p0.fdlt.visibility = ImageButton.INVISIBLE
                        val fdlt = p0.fdlt
                        fdlt.setImageResource(R.drawable.ic_delete_forever_black_24dp)
                    }
                }
            }
        }
        val fdlt = p0.fdlt
        fdlt.setOnClickListener {
            if(isDarr[position]) {
                fdlt.setImageResource(R.drawable.ic_delete_forever_green_24dp)
                deleteList.add(fData.get(position))
                deleteindex.add(fData.get(position).fslotNum!!)
                Log.v("delete index add : ",fData.get(position).fslotNum!!.toString())
            }
            else {
                fdlt.setImageResource(R.drawable.ic_delete_forever_black_24dp)
                deleteList.remove(fData.get(position))
                deleteindex.remove(fData.get(position).fslotNum!!)
                Log.v("delete index deleted : ",fData.get(position).fslotNum!!.toString())
            }
            isDarr[position] = !isDarr[position]
        }

    }
    fun delete(p1 : foodData) {
        fData.remove(p1)
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var fDue: TextView
        var fkind:TextView
        var fdlt : ImageButton
        var fimg:ImageView

        init{

            fkind = itemView.findViewById(R.id.textViewKind)
            fDue= itemView.findViewById(R.id.textViewDue)
            fdlt= itemView.findViewById(R.id.deleteBtn)
            fimg = itemView.findViewById(R.id.category1_image)
        }

    }
}