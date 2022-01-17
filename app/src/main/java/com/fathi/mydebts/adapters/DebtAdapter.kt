package com.fathi.mydebts.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.activities.DebtsActivity
import com.fathi.mydebts.R
import com.fathi.mydebts.activities.MainActivity
import com.fathi.mydebts.models.Debt
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_debt.*
import kotlinx.android.synthetic.main.debt.view.*
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.collections.ArrayList

class DebtAdapter(private val context: Context, private val debts: ArrayList<Debt>) : RecyclerView.Adapter<DebtAdapter.Holder>() {

    private val db = DBManager(context)

    inner class Holder(item: View) : RecyclerView.ViewHolder(item), View.OnCreateContextMenuListener {
        val card: CardView = item.debtCard
        val date: TextView = item.txtDateD
        val debt: TextView = item.txtDebtD
        val btnAdd: ImageButton = item.btnDeleteDebtD

        override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.menu_customer, menu)
            menu.setHeaderTitle("Select Action")
            menu[0].setOnMenuItemClickListener {
                showEditDebtActivity(layoutPosition)
                true
            }
            menu[1].setOnMenuItemClickListener {
                Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG).show()
                removeDebt(layoutPosition, debts[layoutPosition].id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debt, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val debt = debts[position]
        holder.debt.text = debt.value.toString()
        holder.date.text = debt.date.toString()
        holder.btnAdd.setOnClickListener {
            db.deleteDebt(debt.id)
            context.startActivity(Intent(context, MainActivity::class.java))
            context.startActivity(Intent(context, DebtsActivity::class.java).putExtra("id", debt.customerId))
        }
        holder.card.setOnClickListener {
            Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return debts.size
    }

    fun showEditDebtActivity(position: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_edit_debt, null)
        val builder = AlertDialog.Builder(context)
        val debt = debts[position]
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val edtDebtDE = view.findViewById<EditText>(R.id.edtDebtDE)
        val edtDescDE = view.findViewById<EditText>(R.id.edtDescDE)
        val btnEditDebtDE = view.findViewById<Button>(R.id.btnEditDebtDE)
        edtDebtDE.setText(debt.value)
        edtDescDE.setText(debt.description)
        btnEditDebtDE.setOnClickListener {
            val date = debt.date.toString()
            val customerId = debt.customerId
            if (edtDebtDE.text.isNotEmpty()) {
                db.updateDebt(Debt(debt.id, edtDebtDE.text.toString().toInt(), date, edtDescDE.text.toString(), customerId))
                dialog.dismiss()

            } else {
                Toast.makeText(context, "You must enter the name", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeDebt(position: Int, id: Int) {
        db.deleteDebt(id)
        debts.removeAt(position)
        notifyItemChanged(position)
    }
}