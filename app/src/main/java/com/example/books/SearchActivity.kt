package com.example.books

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val button = findViewById<Button>(R.id.btnSearch)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etAuthor = findViewById<EditText>(R.id.etAuthor)
        val etPublisher = findViewById<EditText>(R.id.etPublisher)
        val etIsbn = findViewById<EditText>(R.id.etISBN)

        button.setOnClickListener {
            val title = etTitle.text.toString().trim { it <= ' ' }
            val author = etAuthor.text.toString().trim { it <= ' ' }
            val publisher = etPublisher.text.toString().trim { it <= ' ' }
            val isbn = etIsbn.text.toString().trim { it <= ' ' }
            if (title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()) {
                val message = getString(R.string.no_search_data)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            } else {
                val queryURL = ApiUtil.buildUrl(title, author, publisher, isbn)
                val context = applicationContext
                var position = SharedPreferencesUtil.getPreferenceInt(context, SharedPreferencesUtil.POSITION)
                if (position == 0 || position == 5) {
                    position = 1
                } else {
                    position++
                }
                val key = SharedPreferencesUtil.QUERY + position.toString()
                val value = "$title,$author,$publisher,$isbn"
                SharedPreferencesUtil.setPreferenceString(context, key, value)
                SharedPreferencesUtil.setPreferenceInt(context, SharedPreferencesUtil.POSITION, position)
                val intent = Intent(applicationContext, BookListActivity::class.java)
                intent.putExtra("query", queryURL!!.toString())
                startActivity(intent)
            }
        }
    }
}
