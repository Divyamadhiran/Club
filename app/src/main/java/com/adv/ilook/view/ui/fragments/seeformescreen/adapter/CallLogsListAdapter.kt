package com.adv.ilook.view.ui.fragments.seeformescreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adv.ilook.R
import com.adv.ilook.view.ui.fragments.dataclasses.MissedCallList

class CallLogsListAdapter(private val dataMissedCallList: List<MissedCallList>): RecyclerView.Adapter<CallLogsListAdapter.MissedCallListViewHolder>()

{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissedCallListViewHolder
    {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_callogs, parent, false)
        return MissedCallListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MissedCallListViewHolder, position: Int)
    {
        val currentMissedCallList=dataMissedCallList[position]
        holder.missedName.text=currentMissedCallList.missedContactName
        holder.missedNumber.text=currentMissedCallList.missedContactNumber
        holder.missedCallDate.text=currentMissedCallList.missedCallDate
        holder.missedCallTime.text=currentMissedCallList.missedCallTime

    }

    override fun getItemCount(): Int {
        return dataMissedCallList.size
    }

    class MissedCallListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val missedName=itemView.findViewById<TextView>(R.id.contactNameTextView)
        val missedNumber=itemView.findViewById<TextView>(R.id.contactNumberTextView)
        val missedCallDate=itemView.findViewById<TextView>(R.id.missedCallDate)
        val missedCallTime=itemView.findViewById<TextView>(R.id.missedCallTime)
    }

}