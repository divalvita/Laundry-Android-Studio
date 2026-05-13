package com.example.laundryapp.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ItemOrderBinding

class OrderAdapter(

    private val orders: List<OrderResponse>,

    private val customers: List<CustomerResponse>,

    private val services: List<ServiceResponse>

) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {

            // Cari nama customer berdasarkan customerId
            val customerName = customers.find {
                it.id == order.customerId
            }?.name ?: "Customer Tidak Ditemukan"

            // Cari nama service berdasarkan serviceId
            val serviceName = services.find {
                it.id == order.serviceId
            }?.serviceName ?: "Service Tidak Ditemukan"

            // Tampilkan ke card
            binding.tvCustomerName.text = customerName

            binding.tvOrderDetails.text =
                "$serviceName • ${order.weight} Kg"

            binding.tvOrderPrice.text =
                "Rp ${order.totalPrice}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {

        holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}