package com.example.milkmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class EntriesAdapter(private val entriesList: HashMap<String, String>, private val currentDate: String):
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
        // Sorting the entries of the HashMap by keys
        val sortedEntries = entriesList.entries.sortedBy { it.key }

        // Getting the entry at the current position after sorting
        val entry = sortedEntries[position]

        holder.date.text = entry.key // prints day
        holder.qty.text = entry.value
    }




    override fun getItemCount() = entriesList.size
}