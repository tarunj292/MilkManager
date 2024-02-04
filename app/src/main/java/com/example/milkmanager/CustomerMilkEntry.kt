package com.example.milkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val custList = mutableListOf<String>()


                for (snapshot in dataSnapshot.children) {
                    val custName = snapshot.key
                    if (custName != null) {
                        custList.add(custName)
                    }
                }

                val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_complete)
                val adapter = ArrayAdapter(this@CustomerMilkEntry, R.layout.list_item, custList)
                autoComplete.setAdapter(adapter)
                autoComplete.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view2: View, i: Int, l: Long ->
                        val itemselected = adapterView.getItemAtPosition(i)
                        getThisCustData(itemselected.toString())
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
                Toast.makeText(this@CustomerMilkEntry, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show()
            }
        })


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
            doEntry("0.25", day)
        }

        binding.adha.setOnClickListener {
            doEntry("0.5",day)
        }

        binding.pona.setOnClickListener {
            doEntry("0.75", day)
        }

        binding.one.setOnClickListener {
            doEntry("1", day)
        }

        binding.dad.setOnClickListener {
            doEntry("1.5", day)
        }

        binding.two.setOnClickListener {
            doEntry("2", day)
        }

        binding.btnQty.setOnClickListener {
            val txtQty = binding.txtQty.text.toString()
            doEntry(txtQty, day)
        }

        binding.goBack.setOnClickListener{

        }

        binding.goForward.setOnClickListener{

        }

    }

    private fun getThisCustData(itemselected: String) {
        val mDbRef = FirebaseDatabase.getInstance().reference
        val databaseReference = mDbRef.child("2024").child("02")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(monthSnap: DataSnapshot) {
                entriesHashMap.clear()
                for(custSnap in monthSnap.children){
                    if(custSnap.key == itemselected){
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
    }

    private fun createDataInDB(text: String?) {
        mDbRef = FirebaseDatabase.getInstance().reference
        val entries = (1..31).associateWith { "0" }
            .mapKeys { "${it.key.toString().padStart(2, '0')}D" }
        //above 2 lines are converted from for loop into this
        if (text != null) {
            mDbRef.child("2024").child("02").child(text).setValue(entries)
        }
    }

    private fun doEntry(value: String, day: String) {
        val custName = binding.autoComplete.text.toString()
        Toast.makeText(this@CustomerMilkEntry, "$custName",Toast.LENGTH_SHORT).show()
        val entryKey = "${day.padStart(2, '0')}D"
        val entryValue = value

        val mDbRef = FirebaseDatabase.getInstance().reference
        val entryRef = mDbRef.child("2024").child("02").child(custName).child(entryKey)

        entryRef.setValue(entryValue)
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