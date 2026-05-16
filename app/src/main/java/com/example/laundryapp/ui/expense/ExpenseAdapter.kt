package com.example.laundryapp.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.data.model.ExpenseResponse
import com.example.laundryapp.databinding.ItemExpenseBinding
import java.text.NumberFormat
import java.util.Locale

class ExpenseAdapter(

    private val expenses: List<ExpenseResponse>,

    private val onEditClick: (ExpenseResponse) -> Unit,

    private val onDeleteClick: (ExpenseResponse) -> Unit

) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            expense: ExpenseResponse,
            onEditClick: (ExpenseResponse) -> Unit,
            onDeleteClick: (ExpenseResponse) -> Unit
        ) {
            binding.tvExpenseName.text = expense.itemName

            binding.tvExpenseCategory.text =
                "Kategori: ${expense.category}"

            binding.tvExpenseDate.text =
                "Tanggal: ${expense.date}"

            binding.tvExpenseAmount.text =
                formatRupiah(expense.amount)

            binding.btnEditExpense.setOnClickListener {
                onEditClick(expense)
            }

            binding.btnDeleteExpense.setOnClickListener {
                onDeleteClick(expense)
            }
        }

        private fun formatRupiah(amount: Int): String {
            val formatter = NumberFormat.getCurrencyInstance(
                Locale("id", "ID")
            )

            return formatter.format(amount)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {

        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {
        holder.bind(
            expenses[position],
            onEditClick,
            onDeleteClick
        )
    }

    override fun getItemCount(): Int = expenses.size
}