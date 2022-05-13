package com.test.contactsexample.contacts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.contactsexample.databinding.ContactItemBinding


class MyContactsAdapter(
    private var contacts: List<Contact>,
    private val listener: RecyclerContactClickListener
) : RecyclerView.Adapter<MyContactsAdapter.MyViewHolder>() {

    fun interface RecyclerContactClickListener {
        fun onItemClicked(contact: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = contacts.size

    fun setList(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { listener.onItemClicked(contacts[adapterPosition]) }
        }

        fun bind() {
            binding.contactName.text = contacts[adapterPosition].name
            binding.contactPhone.text = contacts[adapterPosition].name
            binding.contactEmail.text = contacts[adapterPosition].name
        }

    }
}
