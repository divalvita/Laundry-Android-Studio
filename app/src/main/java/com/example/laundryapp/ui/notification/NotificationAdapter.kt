package com.example.laundryapp.ui.notification

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.R
import com.example.laundryapp.data.model.NotificationResponse
import com.example.laundryapp.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val notifications: MutableList<NotificationResponse>,
    private val onDeleteClick: (NotificationResponse) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationResponse) {
            val titleLower = notification.title.lowercase()

            val isPayment = titleLower.contains("pembayaran")
            val badge = if (isPayment) "PAYMENT" else "ORDER"
            val icon = if (isPayment) "✓" else "+"
            val iconBackground = if (isPayment) {
                R.drawable.bg_notif_icon_green
            } else {
                R.drawable.bg_notif_icon_orange
            }

            binding.tvNotifBadge.text = badge
            binding.tvNotifIcon.text = icon
            binding.tvNotifIcon.setBackgroundResource(iconBackground)
            binding.tvNotifTitle.text = notification.title
            binding.tvNotifMessage.text = notification.message
            binding.tvNotifDate.text = formatDate(notification.sentAt)

            binding.btnDeleteNotification.setOnClickListener {
                onDeleteClick(notification)
            }

            binding.root.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle(notification.title)
                    .setMessage(
                        "${notification.message}\n\n" +
                                "Tanggal: ${formatDate(notification.sentAt)}\n" +
                                "Customer ID: ${notification.customerId}"
                    )
                    .setPositiveButton("Tutup", null)
                    .show()
            }
        }

        private fun formatDate(rawDate: String): String {
            return rawDate
                .replace("T", " ")
                .substringBefore(".")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}