import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adv.ilook.R
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList

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

    class ContactListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val contactName=itemView.findViewById<TextView>(R.id.contactNameTextView)
        val contactNumber=itemView.findViewById<TextView>(R.id.contactNumberTextView)
        fun bind(contactList: ContactList, clickListener:OnItemClickListener)
        {
            itemView.setOnClickListener {
                clickListener.onItemClick(contactList)
            }

        }
    }
    private var filteredContacts = dataContactList
    fun filter(query: String) {
        filteredContacts = if (query.isEmpty()) {
            dataContactList
        } else {
            dataContactList.filter {
                it.contactName.contains(query, ignoreCase = true) || it.contactNumber.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}