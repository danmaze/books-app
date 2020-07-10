package com.example.books

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.books.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityBookDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_book_detail)
        val book = intent.getParcelableExtra<Book>("Book")
        binding.book = book
    }

}
