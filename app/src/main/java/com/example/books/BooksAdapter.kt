package com.example.books

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class BooksAdapter(internal var books: ArrayList<Book>) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private var tvAuthors: TextView = itemView.findViewById(R.id.tvAuthors)
        private var tvDate: TextView = itemView.findViewById(R.id.tvPublishedDate)
        private var tvPublisher: TextView = itemView.findViewById(R.id.tvPublisher)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(book: Book) {
            tvTitle.text = book.title
            tvAuthors.text = book.authors.get()
            tvDate.text = book.publishedDate.get()
            tvPublisher.text = book.publisher.get()
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val selectedBook = books[position]
            val intent = Intent(view.context, BookDetailActivity::class.java)
            intent.putExtra("Book", selectedBook)
            view.context.startActivity(intent)
        }
    }
}
