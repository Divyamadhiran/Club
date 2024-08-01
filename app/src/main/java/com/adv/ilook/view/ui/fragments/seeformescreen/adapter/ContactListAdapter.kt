import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.adv.ilook.R
import com.adv.ilook.view.ui.fragments.seeformescreen.dataclasses.ContactList
import com.google.android.material.button.MaterialButton


class ContactListAdapter(private val dataContactList: List<ContactList>,
                         private val itemClickListener:OnItemClickListener):
    RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>()


{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contactlist, parent, false)
        return ContactListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        val currentContactList=filteredContacts[position]
        holder.contactName.text="Call: ${currentContactList.contactName}"
        holder.contactNumber.text=currentContactList.contactNumber
        holder.bind(currentContactList,itemClickListener)
    }

    override fun getItemCount(): Int {
        return filteredContacts.size
    }
    interface OnItemClickListener{
        fun onItemClick(contactList: ContactList)
    }

    class ContactListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName_TV)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber_TV)
        val deletedContacts: ImageView = itemView.findViewById(R.id.deleteContact_IMV)

        fun bind(contactList: ContactList, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClick(contactList)
            }
            deletedContacts.setOnClickListener {
                showDeleteConfirmationDialog(itemView.context,contactList)
            }
        }
        fun showDeleteConfirmationDialog(context: Context, contactList: ContactList) {
            val builder = AlertDialog.Builder(context)
            val deleteDialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_deletecontacts, null)
            builder.setView(deleteDialogView)

            val dialog = builder.create()

            val deleteOkBtn: MaterialButton = deleteDialogView.findViewById(R.id.deleteOk_Btn)
            val deleteCancelBtn: MaterialButton =
                deleteDialogView.findViewById(R.id.deleteCancel_Btn)
            deleteOkBtn.setOnClickListener {
                Toast.makeText(
                    context,
                    "Contact deleted ${contactList.contactName}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            deleteCancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    private var filteredContacts = dataContactList
    fun filter(query: String) {
        filteredContacts = if (query.isEmpty()) {
            dataContactList//no filter is applied so dataContact list is remains the same
        } else {
            dataContactList.filter {
                it.contactName.contains(query, ignoreCase = true) || it.contactNumber.contains(
                    query,
                    ignoreCase = true
                )
            }
        }
        notifyDataSetChanged()
    }
}