package com.letmeeat.project

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class dueAdapter(var dData: ArrayList<dueData>)
    : RecyclerView.Adapter<dueAdapter.ViewHolder>(){ // will be error if no inner class(ViewHolder) defined

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder { // inflate layout and return

        val v = LayoutInflater.from(p0.context).inflate(R.layout.due_item, p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dData.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // define relations with recipelist(cardViewLayout) and elements of rData

        p0.fName.text = dData.get(p1).fname
        p0.fDue.text=dData.get(p1).fdue

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var fName: TextView = itemView.findViewById(R.id.textViewName)
        var fDue: TextView = itemView.findViewById(R.id.textViewDue)

    }
}