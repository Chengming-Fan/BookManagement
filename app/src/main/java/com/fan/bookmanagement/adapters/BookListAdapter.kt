package com.fan.bookmanagement.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fan.bookmanagement.R
import com.fan.bookmanagement.data.Book

val diffCallback = object : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return (oldItem.title == newItem.title)
                && (oldItem.author == newItem.author)
                && (oldItem.year == newItem.year)
                && (oldItem.isbn == newItem.isbn)
    }
}
class BookListAdapter: ListAdapter<Book, BookListAdapter.ViewHolder>(diffCallback) {
    private var dataSet: List<Book> = emptyList()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun setData(dataList: List<Book>) {
        this.dataSet = dataList
        mainHandler.post { notifyDataSetChanged() }
    }

    fun getData(): List<Book> {
        return dataSet
    }
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
        val curBook = dataSet[position]
        viewHolder.textViewId.text = curBook.id.toString()
        viewHolder.textViewBookTitle.text = curBook.title
        viewHolder.textViewAuthor.text = curBook.author
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}