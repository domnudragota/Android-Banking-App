package com.example.dorel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.transactionName.text = transaction.name
        holder.transactionAmount.text = "$${"%.2f".format(transaction.amount)}"
        holder.transactionCategory.text = transaction.category // Bind the category
        holder.transactionDate.text = transaction.date // Bind the date
        holder.transactionDescription.text = transaction.description // Bind the description
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionName: TextView = itemView.findViewById(R.id.transaction_name)
        val transactionAmount: TextView = itemView.findViewById(R.id.transaction_amount)
        val transactionCategory: TextView = itemView.findViewById(R.id.transaction_category)
        val transactionDate: TextView = itemView.findViewById(R.id.transaction_date)
        val transactionDescription: TextView = itemView.findViewById(R.id.transaction_description) // Add description field
    }
}
