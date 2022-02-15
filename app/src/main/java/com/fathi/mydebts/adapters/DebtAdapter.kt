package com.fathi.mydebts.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.models.Debt
import kotlinx.android.synthetic.main.debt.view.*
import kotlin.collections.ArrayList


class DebtAdapter(private val context: Context, private val debts: ArrayList<Debt>) : RecyclerView.Adapter<DebtAdapter.Holder>() {

    private val db = DBManager(context)

    inner class Holder(val item: View) : RecyclerView.ViewHolder(item) {

        val card: CardView = item.debtCard
        val date: TextView = item.txtDateD
        val debt: TextView = item.txtDebtD
        val btnDelete: ImageButton = item.btnDeleteDebtD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debt, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val debt = debts[position]
        holder.debt.text = debt.value.toString()
        holder.date.text = debt.date.toString()
        holder.btnDelete.setOnClickListener {
            removeDebt(position, debt.id)
        }
        holder.card.setOnClickListener {
            editDebt(position)
        }
    }

    override fun getItemCount(): Int {
        return debts.size
    }

    fun editDebt(position: Int) {
        val debt = debts[position]
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_edit_debt, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val edtDebtDE = view.findViewById<EditText>(R.id.edtDebtDE)
        val edtDescDE = view.findViewById<EditText>(R.id.edtDescDE)
        val btnEditDebtDE = view.findViewById<Button>(R.id.btnEditDebtDE)
        edtDebtDE.setText(debt.value.toString())
        edtDescDE.setText(debt.description)
        btnEditDebtDE.setOnClickListener {
            val date = debt.date.toString()
            val customerId = debt.customerId
            if (edtDebtDE.text.isNotEmpty()) {
                debt.value = edtDebtDE.text.toString().toInt()
                debt.description = edtDescDE.text.toString()
                db.updateDebt(Debt(debt.id, debt.value, date, debt.description, customerId))
                debts[position] = debt
                notifyItemChanged(position)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "You must enter the debt", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeDebt(position: Int, id: Int) {
        db.deleteDebt(id)
        debts.removeAt(position)
        notifyItemChanged(position)
    }
}