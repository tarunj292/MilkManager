package com.example.milkmanager

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.util.Calendar
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

        val customerList = mutableListOf<String>()

        mDbRef = FirebaseDatabase.getInstance().reference
        val databaseReference = mDbRef.child(year).child(month)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                customerList.clear()

                for (snapshot in dataSnapshot.children) {
                    val customerName = snapshot.key
                    if (customerName != null) {
                        customerList.add(customerName)
                    }
                }

                val autoComplete: AutoCompleteTextView = findViewById(R.id.autoComplete)
                val adapter = ArrayAdapter(this@CustomerMilkEntry, R.layout.list_item, customerList)
                autoComplete.setAdapter(adapter)
                autoComplete.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
                        val itemSelected = adapterView.getItemAtPosition(i)
                        getThisCustomerData(itemSelected.toString())
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
                    for(customerSnap in monthSnap.children){
                        if(customerSnap.key == "Park"){
                            val gotHashMap = customerSnap.getValue<HashMap<String, String>>()
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

        var txtDate = "" // Declare txtDate variable outside

        binding.txtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val cYear = calendar.get(Calendar.YEAR)
            val cMonth = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault()) // Change date format to exclude year
                    txtDate = dateFormat.format(selectedDate.time) // Store the formatted date in txtDate

                    binding.txtDate.text = txtDate // Update the TextView with the selected date
                },
                cYear,
                cMonth,
                dayOfMonth
            )

            // Set minimum and maximum selectable dates to current month
            val minDate = Calendar.getInstance()
            minDate.set(Calendar.DAY_OF_MONTH, 1) // First day of the month
            val maxDate = Calendar.getInstance()
            maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH)) // Last day of the month

            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

            datePickerDialog.show()
        }


        binding.btnQty.setOnClickListener {
            val txtQty = binding.txtQty.text.toString()
            if(txtQty.isNotEmpty()){
                if (txtDate.isEmpty()) {
                    doEntry(txtQty, day)
                } else {
                    doEntry(txtQty, txtDate)
                }
            }else{
                Toast.makeText(this@CustomerMilkEntry, "Enter value", Toast.LENGTH_SHORT).show()
            }
        }


        binding.goBack.setOnClickListener{
            val autoComplete: AutoCompleteTextView = findViewById(R.id.autoComplete)
            val adapter = autoComplete.adapter as ArrayAdapter<String>
            val selectedItem = autoComplete.text.toString()
            val index = adapter.getPosition(selectedItem)
            if(index>0){
                val getCustomerNameByIndex = adapter.getItem(index-1)
                Toast.makeText(this@CustomerMilkEntry,"$getCustomerNameByIndex",Toast.LENGTH_SHORT).show()
                if (getCustomerNameByIndex != null) {
                    changeCustomerNameInDropDown(getCustomerNameByIndex)
                }
            } else{
                Toast.makeText(this@CustomerMilkEntry, "You are at starting point", Toast.LENGTH_SHORT).show()
            }
//        createDataInDB(binding.txtQty.text.toString())
        }

        binding.goForward.setOnClickListener{
            val autoComplete: AutoCompleteTextView = findViewById(R.id.autoComplete)
            val adapter = autoComplete.adapter as ArrayAdapter<String>
            val selectedItem = autoComplete.text.toString()
            val index = adapter.getPosition(selectedItem)
            if(index < customerList.size-1){
                val getCustomerNameByIndex = adapter.getItem(index+1)
                if (getCustomerNameByIndex != null) {
                    changeCustomerNameInDropDown(getCustomerNameByIndex)
                }
            } else{
                Toast.makeText(this@CustomerMilkEntry, "All Done", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun changeCustomerNameInDropDown(getCustomerNameByIndex: String) {
        binding.autoComplete.isEnabled = false
        mDbRef = FirebaseDatabase.getInstance().reference
        val databaseReference = mDbRef.child("2024").child("02")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val customerList = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val customerName = snapshot.key
                    if (customerName != null) {
                        customerList.add(customerName)
                    }
                }

                val autoComplete: AutoCompleteTextView = findViewById(R.id.autoComplete)
                val adapter = ArrayAdapter(this@CustomerMilkEntry, R.layout.list_item, customerList)
                autoComplete.setText(getCustomerNameByIndex)
                autoComplete.setAdapter(adapter)
                getThisCustomerData(getCustomerNameByIndex)
                binding.autoComplete.isEnabled = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
                Toast.makeText(this@CustomerMilkEntry, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getThisCustomerData(itemSelected: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        val databaseReference = mDbRef.child("2024").child("02")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(monthSnap: DataSnapshot) {
                entriesHashMap.clear()
                for(customerSnap in monthSnap.children){
                    if(customerSnap.key == itemSelected){
                        val gotHashMap = customerSnap.getValue<HashMap<String, String>>()
                        if (gotHashMap != null) {
                            for((key,value) in gotHashMap){
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
        val entries = (1..31).associateWith { "0" }.mapKeys { "${it.key.toString().padStart(2, '0')}D" }
        //above 2 lines are converted from for loop into this
        if (text != null) {
            mDbRef.child("2024").child("02").child(text).setValue(entries)
        }
    }

    private fun doEntry(value: String, day: String) {
        val customerName = binding.autoComplete.text.toString()
        Toast.makeText(this@CustomerMilkEntry, customerName,Toast.LENGTH_SHORT).show()
        if(customerName.isNotEmpty()){
            val entryKey = "${day.padStart(2, '0')}D"

            mDbRef = FirebaseDatabase.getInstance().reference
            val entryRef = mDbRef.child("2024").child("02").child(customerName).child(entryKey)

            entryRef.setValue(value)//changing value on entryRef that is nothing but date
        }
    }


    private fun changeEntry(whereToChange:String, change: String){
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("2024").child("02").child("Park").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the HashMap from the dataSnapshot
                val entries = dataSnapshot.getValue<HashMap<String, String>>()

                // Update the value in the HashMap
                entries?.let {
                    it[whereToChange] = change // Update value at key "5"
                }

                // Set the updated HashMap back to the database
                mDbRef.child("2024").child("02").child("Park").setValue(entries)
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