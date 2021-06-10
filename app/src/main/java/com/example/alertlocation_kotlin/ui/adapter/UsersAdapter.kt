package com.example.alertlocation_kotlin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel


class UsersAdapter(var context: Context, users: List<User>,val viewmodel : DetailsViewModel) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    val usersList = mutableListOf<User>()
    val tempListOfSelectedUsers = mutableListOf<User>()
    companion object {
        var changeSelectionListener: ((Boolean) -> Unit)? = null
    }

    init {
        usersList.addAll(users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = usersList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, user = usersList[position],this.tempListOfSelectedUsers,viewmodel =viewmodel)
    }

    fun clear() {
        val diffCallback = UserDiffCallback(this.usersList, mutableListOf())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.usersList.clear()

        diffResult.dispatchUpdatesTo(this)
    }

    fun swap(users: List<User>) {
        val diffCallback = UserDiffCallback(this.usersList, users)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.usersList.clear()
        this.usersList.addAll(users)
        diffResult.dispatchUpdatesTo(this)
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.username)
        private val itemContainer: ConstraintLayout = itemView.findViewById(R.id.itemContainer)
        private val personselected: ImageView = itemView.findViewById(R.id.personselected)
            fun bind(context: Context, user: User, tempListOfSelectedUsers: MutableList<User>,viewmodel:DetailsViewModel) {
                name.text = user.username
                resetBackground(context,user,viewmodel)

                itemContainer.setOnClickListener {
                    changeSelection(user, context, true,tempListOfSelectedUsers,viewmodel)
                }
            }

        private fun changeSelection(user: User, context: Context, pressedItem: Boolean, tempListOfSelectedUsers: MutableList<User>,viewmodel: DetailsViewModel) {
            if ( personselected.alpha == 0.2f && !tempListOfSelectedUsers.contains(user))  {
                itemContainer.background = ContextCompat.getDrawable(context, R.drawable.background_selected)
                personselected.background = ContextCompat.getDrawable(context, R.drawable.background_selected_oval)
                personselected.alpha = 1f
                tempListOfSelectedUsers.add(user)
                changeSelectionListener?.invoke(true)

            } else {
                itemContainer.background = ContextCompat.getDrawable(context, R.drawable.background_unselected)
                personselected.background = ContextCompat.getDrawable(context, R.drawable.background_unselected_oval)
                personselected.alpha = 0.2f
                if (tempListOfSelectedUsers.contains(user))
                    tempListOfSelectedUsers.remove(user)

                if (tempListOfSelectedUsers.isEmpty()) {
                    changeSelectionListener?.invoke(false)
                }


            }
        }
        private fun resetBackground(context: Context, user: User, viewmodel:DetailsViewModel) {
            if (!viewmodel.usersToSend.value?.contains(user)!!) {
                itemContainer.background = ContextCompat.getDrawable(context, R.drawable.background_unselected)
                personselected.background = ContextCompat.getDrawable(context, R.drawable.background_unselected_oval)
                personselected.alpha = 0.2f
            }else{
                itemContainer.background = ContextCompat.getDrawable(context, R.drawable.background_selected)
                personselected.background = ContextCompat.getDrawable(context, R.drawable.background_selected_oval)
                personselected.alpha = 1f
            }

        }
    }

    class UserDiffCallback(
            private val oldList: List<User>,
            private val newList: List<User>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].username == newList[newItemPosition].username
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].username == newList[newItemPosition].username
        }

    }
}