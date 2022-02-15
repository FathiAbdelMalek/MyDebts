package com.fathi.mydebts.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.*
import androidx.cardview.widget.CardView
import com.fathi.mydebts.R
import com.fathi.mydebts.models.Customer
import kotlinx.android.synthetic.main.customer.view.*
import androidx.recyclerview.widget.RecyclerView
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.activities.AddDebtActivity
import com.fathi.mydebts.activities.DebtsActivity
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import android.view.LayoutInflater
import android.widget.*


class CustomerAdapter(private val context: Context, private val customers: ArrayList<Customer>) : RecyclerView.Adapter<CustomerAdapter.Holder>() {

    private val db = DBManager(context)

    inner class Holder(val item: View) : RecyclerView.ViewHolder(item), View.OnCreateContextMenuListener {

        val card: CardView = item.customerCard
        val name: TextView = item.txtNameM
        val debt: TextView = item.txtDebtsM
        val btnAdd: ImageButton = item.btnAddDebtM

        init {
            card.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.menu_customer, menu)
            menu.setHeaderTitle("Select Action")
            menu[0].setOnMenuItemClickListener {
                editCustomer(layoutPosition)
                true
            }
            menu[1].setOnMenuItemClickListener {
                Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG).show()
                removeCustomer(layoutPosition, customers[layoutPosition].id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customer, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val customer = customers[position]
        val cursor = db.getAllWhere("debts", "customer_id", customer.id.toString())
        var sum = 0
        if(cursor!!.moveToFirst())
            do {
                sum += cursor.getInt(cursor.getColumnIndexOrThrow("value"))
            } while (cursor.moveToNext())
        holder.name.text = customer.name
        holder.debt.text = sum.toString()
        holder.btnAdd.setOnClickListener {
            context.startActivity(Intent(context, AddDebtActivity::class.java).putExtra("id", customer.id))
        }
        holder.card.setOnClickListener {
            context.startActivity(Intent(context, DebtsActivity::class.java).putExtra("id", customer.id))
        }
    }

    override fun getItemCount(): Int {
        return customers.size
    }

    fun editCustomer(position: Int) {
        val customer = customers[position]
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_edit_customer, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val edtNameCE = view.findViewById<EditText>(R.id.edtNameCE)
        val edtPhoneCE = view.findViewById<EditText>(R.id.edtPhoneCE)
        val btnEditCustomerCE = view.findViewById<Button>(R.id.btnEditCustomerCE)
        edtNameCE.setText(customer.name)
        edtPhoneCE.setText(customer.phone)
        btnEditCustomerCE.setOnClickListener {
            if (edtNameCE.text.isNotEmpty()) {
                customer.name = edtNameCE.text.toString()
                customer.phone = edtPhoneCE.text.toString()
                db.updateCustomer(Customer(customer.id, customer.name, customer.phone))
                customers[position] = customer
                notifyItemChanged(position)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "You must enter the name", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeCustomer(position: Int, id: Int) {
        db.deleteCustomer(id)
        customers.removeAt(position)
        notifyItemChanged(position)
    }
}