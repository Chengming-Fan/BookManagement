package com.fan.bookmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fan.bookmanagement.R

class BookListAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewBookTitle: TextView
        val textViewAuthor: TextView
        val textViewId: TextView

        init {
            textViewId = view.findViewById(R.id.textview_book_id)
            textViewBookTitle = view.findViewById(R.id.textview_book_title)
            textViewAuthor = view.findViewById(R.id.textview_book_author)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.cell_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewBookTitle.text = dataSet[position]
        viewHolder.textViewId.text = position.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}