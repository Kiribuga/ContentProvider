package com.example.content_provider.contacts.data

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.content_provider.R
import com.example.content_provider.utils.inflate

class AdapterContacts(
    private val onClickItem: (contact: Contacts) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactHolder(
            parent.inflate(R.layout.item_contact)
        )
    }

    var contacts: List<Contacts> = emptyList()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactHolder -> {
                val contact = contacts[position]
                holder.bind(contact)
                holder.itemView.setOnClickListener { onClickItem.invoke(contact) }
            }
            else -> error("Incorrect view holder = $holder")
        }
    }

    override fun getItemCount(): Int = contacts.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateListContacts(newList: List<Contacts>) {
        contacts = newList
        notifyDataSetChanged()
    }

    abstract class BaseHolder(
        private val containerView: View
    ) : RecyclerView.ViewHolder(containerView) {

        private val nameContact: TextView = itemView.findViewById(R.id.nameContact)

        @SuppressLint("SetTextI18n")
        protected fun bindMainInfo(
            name: String
        ) {
            nameContact.text = name
        }
    }

    class ContactHolder(
        containerView: View
    ) : BaseHolder(containerView) {
        fun bind(contact: Contacts) {
            bindMainInfo(contact.name)
        }
    }
}