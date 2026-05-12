package com.example.laundryapp.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.databinding.ItemCustomerBinding
import com.example.laundryapp.model.Customer

class CustomerAdapter(
    private val customers: List<Customer>,
    private val onEditClick: (Customer) -> Unit,
    private val onDeleteClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(
        private val binding: ItemCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            customer: Customer,
            onEditClick: (Customer) -> Unit,
            onDeleteClick: (Customer) -> Unit
        ) {
            binding.tvCustomerName.text = customer.name
            binding.tvCustomerPhone.text = customer.phone

            binding.btnEditCustomer.setOnClickListener {
                onEditClick(customer)
            }

            binding.btnDeleteCustomer.setOnClickListener {
                onDeleteClick(customer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customers[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = customers.size
}