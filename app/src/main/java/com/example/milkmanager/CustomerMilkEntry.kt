package com.example.milkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.milkmanager.databinding.ActivityCustomerMilkEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerMilkEntry : AppCompatActivity() {

    private lateinit var binding : ActivityCustomerMilkEntryBinding
    private lateinit var offersList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var entriesadapter: EntriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMilkEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnQty = binding.btnQty

        binding.pav.setOnClickListener{
            doEntry("0.25")
        }

        binding.adha.setOnClickListener{
            doEntry("0.5")
        }

        binding.pona.setOnClickListener{
            doEntry("0.75")
        }

        binding.one.setOnClickListener{
            doEntry("1")
        }

        binding.dad.setOnClickListener{
            doEntry("1.5")
        }

        binding.two.setOnClickListener{
            doEntry("2")
        }

        btnQty.setOnClickListener{
            val txtQty = binding.txtQty.text.toString()
            doEntry("$txtQty")
        }
        val date = getCurrentDate()
        binding.today.text = date

        //        Recycler View for entries
        offersList = arrayListOf()
        recyclerView = binding.entriesRecyclerView
        entriesadapter = EntriesAdapter(offersList, date)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = entriesadapter

    }
    private fun doEntry(value: String) {
        offersList.add(value)
        entriesadapter.notifyDataSetChanged()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}