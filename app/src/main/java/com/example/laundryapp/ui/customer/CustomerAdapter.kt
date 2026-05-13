package com.example.laundryapp.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.databinding.ItemCustomerBinding

class CustomerAdapter(

    private val customers: List<CustomerResponse>,

    private val onEditClick: (CustomerResponse) -> Unit,

    private val onDeleteClick: (CustomerResponse) -> Unit

) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(
        private val binding: ItemCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            customer: CustomerResponse,
            onEditClick: (CustomerResponse) -> Unit,
            onDeleteClick: (CustomerResponse) -> Unit
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerViewHolder {

        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomerViewHolder,
        position: Int
    ) {

        holder.bind(
            customers[position],
            onEditClick,
            onDeleteClick
        )
    }

    override fun getItemCount(): Int = customers.size
}