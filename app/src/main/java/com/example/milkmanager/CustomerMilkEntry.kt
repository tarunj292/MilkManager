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

class CustomerMilkEntry : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var binding : ActivityCustomerMilkEntryBinding
    private lateinit var entriesHashMap: HashMap<String, String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var entriesAdapter: EntriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMilkEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = getTodayDate()
        val year = date.substring(6, 10)
        val month = date.substring(3, 5)
        val day = date.substring(0, 2)

        val mDbRef = FirebaseDatabase.getInstance().reference
        val databaseReference = mDbRef.child(year).child(month)

        entriesHashMap = hashMapOf()
        recyclerView = binding.entriesRecyclerView
        entriesAdapter = EntriesAdapter(entriesHashMap, date)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = entriesAdapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(monthSnap: DataSnapshot) {
                entriesHashMap.clear()
                    for(custSnap in monthSnap.children){
                        if(custSnap.key == "SagarPark"){
                            val gotHashMap = custSnap.getValue<HashMap<String, String>>()
                            if (gotHashMap != null) {
                                for((key,value) in gotHashMap){
//                                    Toast.makeText(this@CustomerMilkEntry,"$key",Toast.LENGTH_SHORT).show()
                                    entriesHashMap[key] = value
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
            createDataInDB()
        }

        binding.adha.setOnClickListener {
            doEntry("0.5",date)
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

        binding.btnQty.setOnClickListener {
            val txtQty = binding.txtQty.text.toString()
            doEntry(txtQty, date)
        }

        binding.goBack.setOnClickListener{
            val whereToChange = day+"D"
            changeEntry(whereToChange, "0")
        }

        binding.today.text = date


    }
    private fun createDataInDB() {
        mDbRef = FirebaseDatabase.getInstance().reference
        val entries = (1..31).associateWith { "0" }
            .mapKeys { "${it.key.toString().padStart(2, '0')}D" }
        //above 2 lines are converted from for loop into this
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

    private fun changeEntry(whereToChange:String, change: String){
        var mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("2024").child("02").child("SagarPark").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the HashMap from the dataSnapshot
                val entries = dataSnapshot.getValue<HashMap<String, String>>()

                // Update the value in the HashMap
                entries?.let {
                    it[whereToChange] = change // Update value at key "5"
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