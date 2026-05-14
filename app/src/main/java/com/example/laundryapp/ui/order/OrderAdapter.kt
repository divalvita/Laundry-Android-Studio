package com.example.laundryapp.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderResponse) {
            // Cari data relasi
            val customer = customers.find { it.id == order.customerId }
            val service = services.find { it.id == order.serviceId }

            // Tampilkan data sesuai UI kamu
            binding.tvCustomerName.text = customer?.name ?: "Customer Tidak Ditemukan"
            binding.tvOrderDetails.text = "${service?.serviceName ?: "Service Tidak Ditemukan"} • ${order.weight} Kg"
            binding.tvOrderPrice.text = "Rp ${order.totalPrice}"

            // Logika Tombol Aksi agar tidak "nyaru"
            if (isManageMode) {
                binding.btnAction.visibility = View.VISIBLE
                // Ganti teks tombol berdasarkan status saat ini
                binding.btnAction.text = if (order.status == "done") "Tandai Diambil" else "Tandai Selesai"

                binding.btnAction.setOnClickListener {
                    onActionClick?.invoke(order.id)
                }
            } else {
                // Sembunyikan tombol jika hanya melihat riwayat
                binding.btnAction.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}