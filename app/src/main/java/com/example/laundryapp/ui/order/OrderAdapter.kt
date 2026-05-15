package com.example.laundryapp.ui.order

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.R
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ItemOrderBinding

class OrderAdapter(
    private val orders: List<OrderResponse>,
    private val customers: List<CustomerResponse>,
    private val services: List<ServiceResponse>,
    private val isManageMode: Boolean = false,
    private val onActionClick: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            val context = binding.root.context

            val customerName = customers.find {
                it.id == order.customerId
            }?.name ?: "Customer Tidak Ditemukan"

            val serviceName = services.find {
                it.id == order.serviceId
            }?.serviceName ?: "Service Tidak Ditemukan"

            binding.tvCustomerName.text = customerName
            binding.tvOrderDetails.text = "$serviceName • ${order.weight} Kg"
            binding.tvOrderPrice.text = "Rp ${order.totalPrice}"

            val statusText = when (order.status.lowercase()) {
                "pending" -> "Pending"
                "processing" -> "Diproses"
                "done" -> "Selesai"
                "taken" -> "Diambil"
                else -> order.status.replaceFirstChar { it.uppercase() }
            }

            val statusColor = when (order.status.lowercase()) {
                "pending" -> R.color.status_pending
                "processing" -> R.color.status_processing
                "done" -> R.color.status_done
                "taken" -> R.color.status_taken
                else -> R.color.blue_secondary
            }

            binding.tvOrderStatus.text = statusText
            binding.tvOrderStatus.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, statusColor))

            // isManageMode dan onActionClick tetap ada supaya OrderHistoryActivity tidak error,
            // tapi tombol action belum dipakai dulu.
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

    override fun getItemCount(): Int = orders.size
}