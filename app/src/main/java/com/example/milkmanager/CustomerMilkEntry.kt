package com.example.milkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.milkmanager.databinding.ActivityCustomerMilkEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerMilkEntry : AppCompatActivity() {

    private lateinit var binding : ActivityCustomerMilkEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMilkEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnQty = binding.btnQty

        binding.pav.setOnClickListener{
            doEntry("0.25 litres")
        }

        binding.adha.setOnClickListener{
            doEntry("0.5 litres")
        }

        binding.pona.setOnClickListener{
            doEntry("0.75 litres")
        }

        binding.one.setOnClickListener{
            doEntry("1 litres")
        }

        binding.dad.setOnClickListener{
            doEntry("1.5 litres")
        }

        binding.two.setOnClickListener{
            doEntry("2 litres")
        }

        btnQty.setOnClickListener{
            val txtQty = binding.txtQty.text.toString()
            doEntry("$txtQty litres")
        }

        binding.today.text = getCurrentDate()

    }

    private fun doEntry(value: String) {
        binding.entries.text = value
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}