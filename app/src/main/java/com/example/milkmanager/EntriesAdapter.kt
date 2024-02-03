package com.example.milkmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class EntriesAdapter(private val offersList: List<String>, private val currentDate: String):
    RecyclerView.Adapter<EntriesAdapter.MyViewHolder>()
{
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val qty: TextView
        val date: TextView
        init {
            date = itemView.findViewById(R.id.entriesLayoutDate)
            qty = itemView.findViewById(R.id.entriesTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entries_layout, parent, false)
        return MyViewHolder(view)
    }




    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.date.text = currentDate.substring(0, 2) // prints only day og month
        holder.qty.text = offersList[position]
        val context = holder.itemView.context

    }


    override fun getItemCount() = offersList.size
}