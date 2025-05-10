// IbanAdapter.kt
package com.example.dorel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IbanAdapter(private val ibanList: List<String>) : RecyclerView.Adapter<IbanAdapter.IbanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IbanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_iban, parent, false)
        return IbanViewHolder(view)
    }

    override fun onBindViewHolder(holder: IbanViewHolder, position: Int) {
        holder.bind(ibanList[position])
    }

    override fun getItemCount(): Int = ibanList.size

    class IbanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ibanTextView: TextView = itemView.findViewById(R.id.iban_text)

        fun bind(iban: String) {
            ibanTextView.text = iban
        }
    }
}
