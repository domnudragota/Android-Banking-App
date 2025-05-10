package com.example.dorel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreditCardAdapter(private val creditCards: List<CreditCard>) :
    RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_credit_card, parent, false)
        return CreditCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val creditCard = creditCards[position]
        holder.cardNumber.text = creditCard.cardNumber
        holder.cardHolderName.text = creditCard.cardHolderName
        holder.expiryDate.text = creditCard.expirationDate
        holder.cardBalance.text = "$${"%.2f".format(creditCard.balance)}"
        holder.cardIban.text = "IBAN: ${creditCard.iban}" // Show IBAN in the UI

    }

    override fun getItemCount(): Int {
        return creditCards.size
    }

    class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardNumber: TextView = itemView.findViewById(R.id.card_number)
        val cardHolderName: TextView = itemView.findViewById(R.id.card_holder_name)
        val expiryDate: TextView = itemView.findViewById(R.id.expiry_date)
        val cardBalance: TextView = itemView.findViewById(R.id.card_balance)
        val cardIban: TextView = itemView.findViewById(R.id.card_iban)
    }
}
