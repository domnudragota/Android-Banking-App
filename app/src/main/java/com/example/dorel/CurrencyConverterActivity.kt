package com.example.dorel

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyConverterActivity : AppCompatActivity() {

    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var convertButton: Button
    private lateinit var convertedAmountText: TextView
    private lateinit var progressBar: ProgressBar

    private val currencyList = arrayListOf("USD", "EUR", "GBP", "INR", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_converter)

        // Initialize UI components
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner)
        toCurrencySpinner = findViewById(R.id.to_currency_spinner)
        amountInput = findViewById(R.id.amount_input)
        convertButton = findViewById(R.id.convert_button)
        convertedAmountText = findViewById(R.id.converted_amount)
        progressBar = findViewById(R.id.progress_bar)

        // Set up spinners with currency list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter

        convertButton.setOnClickListener {
            val fromCurrency = fromCurrencySpinner.selectedItem.toString()
            val toCurrency = toCurrencySpinner.selectedItem.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchConversionRate(fromCurrency, toCurrency, amount)
        }
    }

    private fun fetchConversionRate(fromCurrency: String, toCurrency: String, amount: Double) {
        progressBar.visibility = View.VISIBLE

        // Make API request using Retrofit
        val apiService = RetrofitClient.instance
        apiService.getExchangeRates(fromCurrency).enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val rates = response.body()?.conversion_rates
                    val toRate = rates?.get(toCurrency)

                    if (toRate != null) {
                        val convertedAmount = amount * toRate
                        convertedAmountText.text = "Converted Amount: %.2f $toCurrency".format(convertedAmount)
                    } else {
                        Toast.makeText(this@CurrencyConverterActivity, "Currency not supported.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CurrencyConverterActivity, "Failed to fetch conversion rate.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@CurrencyConverterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
