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
import com.google.firebase.database.getValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TreeMap

class CustomerMilkEntry : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding : ActivityCustomerMilkEntryBinding
    private lateinit var offersList: HashMap<String, String>
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

        val databaseReference = mDbRef.child(year).child(month)

        offersList = hashMapOf()
        recyclerView = binding.entriesRecyclerView
        entriesAdapter = EntriesAdapter(offersList, date)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = entriesAdapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(monthSnap: DataSnapshot) {
                offersList.clear()
                    for(custSnap in monthSnap.children){
                        Toast.makeText(this@CustomerMilkEntry, "$custSnap", Toast.LENGTH_SHORT).show()
                        if(custSnap.key == "SagarPark"){
                            val gotHashMap = custSnap.getValue<HashMap<String, String>>()
                            if (gotHashMap != null) {
                                for((key,value) in gotHashMap){
                                    offersList[key] = value
                                }
                            }
                        }
                    }
                entriesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CustomerMilkEntry, "error", Toast.LENGTH_SHORT).show()
            }
        })

        binding.pav.setOnClickListener {
            changeEntry()
        }

        binding.adha.setOnClickListener {
            createDataInDB()
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
            doEntry(txtQty, date)
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
    private fun createDataInDB() {
        mDbRef = FirebaseDatabase.getInstance().reference
        var entries: HashMap<String, String> = hashMapOf()
        for(i in 1..29){
            entries["${i}D"] = "0"
        }
        mDbRef.child("2024").child("02").child("SagarPark").setValue(entries)
    }

    private fun doEntry(value: String, date: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        var entries: HashMap<Double, String> = hashMapOf()
        for(i in 1..29){
            entries[i.toDouble()] = "0"
        }
        mDbRef.child(date.substring(6, 10)).child(date.substring(3, 5)).child("SagarPark").key
        mDbRef = FirebaseDatabase.getInstance().reference
    }

    private fun changeEntry(){
        var mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("2024").child("02").child("SagarPark").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the HashMap from the dataSnapshot
                Toast.makeText(this@CustomerMilkEntry,"$dataSnapshot",Toast.LENGTH_SHORT).show()
                val entries = dataSnapshot.getValue<HashMap<String, String>>()

                // Update the value in the HashMap
                entries?.let {
                    it["10D"] = "2" // Update value at key "5"
                }

                // Set the updated HashMap back to the database
                mDbRef.child("2024").child("02").child("SagarPark").setValue(entries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
                println("Database error: $databaseError")
            }
        })

    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}