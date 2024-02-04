package com.example.milkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.milkmanager.databinding.ActivityCustomerMilkEntryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerMilkEntry : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding : ActivityCustomerMilkEntryBinding
    private lateinit var offersList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var entriesAdapter: EntriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMilkEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnQty = binding.btnQty
        val date = getTodayDate()

        val mDbRef = FirebaseDatabase.getInstance().reference
        val year = date.substring(6, 10)
        val month = date.substring(3, 5)
        val day = date.substring(0, 2)

        val databaseReference = mDbRef.child(year).child(month).child(day).child("SagarPark")

        offersList = arrayListOf()
        recyclerView = binding.entriesRecyclerView
        entriesAdapter = EntriesAdapter(offersList, date)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = entriesAdapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val value = dataSnapshot.getValue(String::class.java)
                    if (value != null) {
                        offersList.clear()
                        offersList.add(value)
                        entriesAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CustomerMilkEntry, "error", Toast.LENGTH_SHORT).show()
            }
        })

        binding.pav.setOnClickListener {
            doEntry("0.25", date)
        }

        binding.adha.setOnClickListener {
            doEntry("0.5", date)
        }

        binding.pona.setOnClickListener {
            doEntry("0.75", date)
        }

        binding.one.setOnClickListener {
            doEntry("1", date)
        }

        binding.dad.setOnClickListener {
            doEntry("1.5", date)
        }

        binding.two.setOnClickListener {
            doEntry("2", date)
        }

        btnQty.setOnClickListener {
            val txtQty = binding.txtQty.text.toString()
            doEntry("$txtQty", date)
        }

        binding.goBack.setOnClickListener{
            mDbRef.child(year).child(month).child(day).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    offersList.clear()
                    entriesAdapter.notifyDataSetChanged()
                }}
        }

        binding.today.text = date


    }
    private fun doEntry(value: String, date: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child(date.substring(6, 10)).child(date.substring(3, 5))
            .child(date.substring(0, 2)).child("SagarPark").setValue(value)
        mDbRef = FirebaseDatabase.getInstance().reference
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}