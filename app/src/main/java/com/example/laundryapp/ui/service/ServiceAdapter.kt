package com.example.laundryapp.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.databinding.ItemServiceBinding
import com.example.laundryapp.model.Service

class ServiceAdapter(
    private val services: List<Service>,
    private val onEditClick: (Service) -> Unit,
    private val onDeleteClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            service: Service,
            onEditClick: (Service) -> Unit,
            onDeleteClick: (Service) -> Unit
        ) {
            binding.tvServiceName.text = service.service_name
            binding.tvServicePrice.text = "Rp ${service.price_per_kg} / Kg"

            binding.btnEditService.setOnClickListener {
                onEditClick(service)
            }

            binding.btnDeleteService.setOnClickListener {
                onDeleteClick(service)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = services.size
}