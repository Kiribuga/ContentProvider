package com.example.content_provider.contacts.data

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.content_provider.R
import com.example.content_provider.utils.inflate

class AdapterInfoContact() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var detailContact: List<Contacts> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DetailContactHolder(
            parent.inflate(R.layout.item_contact_info_detail)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailContactHolder -> {
                val contact = detailContact[position]
                holder.bind(contact)
            }
            else -> error("Incorrect view holder = $holder")
        }
    }

    override fun getItemCount(): Int = detailContact.size

    fun updateListContacts(newList: List<Contacts>) {
        detailContact = newList
    }

    abstract class BaseHolder(
        private val containerView: View
    ) : RecyclerView.ViewHolder(containerView) {
        protected fun bindMainInfo(
            name: String,
            phone: List<String>,
            email: List<String>
        ) {
            val nameContact: TextView = itemView.findViewById(R.id.nameContactTextView)
            val phoneContact: TextView = itemView.findViewById(R.id.phonesContactTextView)
            val emailContact: TextView = itemView.findViewById(R.id.emailsContactTextView)

            nameContact.text = name
            phoneContact.text = phone.joinToString ( separator = "\n")
            emailContact.text = email.joinToString ( separator = "\n")
        }
    }

    class DetailContactHolder(containerView: View) : BaseHolder(containerView) {
        fun bind(contact: Contacts) {
            bindMainInfo(contact.name, contact.phones, contact.email)
        }
    }
}