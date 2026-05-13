package com.example.laundryapp.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ItemServiceBinding

class ServiceAdapter(

    private val services: List<ServiceResponse>,

    private val onEditClick: (ServiceResponse) -> Unit,

    private val onDeleteClick: (ServiceResponse) -> Unit

) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            service: ServiceResponse,
            onEditClick: (ServiceResponse) -> Unit,
            onDeleteClick: (ServiceResponse) -> Unit
        ) {

            binding.tvServiceName.text = service.serviceName

            binding.tvServicePrice.text =
                "Rp ${service.pricePerKg} / Kg"

            binding.btnEditService.setOnClickListener {
                onEditClick(service)
            }

            binding.btnDeleteService.setOnClickListener {
                onDeleteClick(service)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceViewHolder {

        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int
    ) {

        holder.bind(
            services[position],
            onEditClick,
            onDeleteClick
        )
    }

    override fun getItemCount(): Int = services.size
}