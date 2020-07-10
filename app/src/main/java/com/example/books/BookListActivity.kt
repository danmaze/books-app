package com.example.books

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

class BookListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var mLoadingProgress: ProgressBar? = null
    private var mRecyclerViewBooks: RecyclerView? = null
    private lateinit var bookUrl: URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        mRecyclerViewBooks = findViewById(R.id.rv_books)
        mLoadingProgress = findViewById(R.id.pb_loading)
        val intent = intent
        val query = intent.getStringExtra("query")
        try {
            bookUrl = if (query == null || query.isEmpty()) {
                ApiUtil.buildUrl("cooking")!!
            } else {
                URL(query)
            }
            BooksQueryTask(this).execute(bookUrl)

        } catch (e: Exception) {
            Log.d("error", e.message!!)
        }
        val booksLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerViewBooks!!.layoutManager = booksLayoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.book_list_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        // Recent searches
        val recentList = SharedPreferencesUtil.getQueryList(applicationContext)
        val itemNum = recentList.size
        var recentMenu: MenuItem
        for (i in 0 until itemNum)
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList[i])
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.action_advanced_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                val position = item.itemId + 1
                val preferenceName = SharedPreferencesUtil.QUERY + position.toString()
                val query = SharedPreferencesUtil.getPreferenceString(applicationContext, preferenceName)
                val prefParams = query.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val queryParams = arrayOfNulls<String>(4)
                for (i in prefParams.indices)
                    queryParams[i] = prefParams[i]
                bookUrl = ApiUtil.buildUrl(
                    if (queryParams[0] == null) "" else queryParams[0]!!,
                    if (queryParams[1] == null) "" else queryParams[1]!!,
                    if (queryParams[2] == null) "" else queryParams[2]!!,
                    if (queryParams[3] == null) "" else queryParams[3]!!
                )!!
                BooksQueryTask(this).execute(bookUrl)
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        try {
            val bookUrl = ApiUtil.buildUrl(query)
            BooksQueryTask(this).execute(bookUrl)
        } catch (e: Exception) {
            Log.d("error", e.message!!)
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    private class BooksQueryTask(context: BookListActivity) : AsyncTask<URL, Void, String>() {

        private val activityReference: WeakReference<BookListActivity> = WeakReference(context)

        override fun doInBackground(vararg urls: URL): String? {
            val searchURL = urls[0]
            var result: String? = null
            try {
                result = ApiUtil.getJson(searchURL)
            } catch (e: IOException) {
                Log.e("Error", e.message!!)
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            // Get a reference to the activity if it is still there
            val activity = activityReference.get()
            if (activity == null || activity.isFinishing) return
            // Modify the activity's UI
            val textViewError = activity.findViewById<TextView>(R.id.tv_error)
            val loadingProgress = activity.findViewById<ProgressBar>(R.id.pb_loading)
            val recyclerViewBooks = activity.findViewById<RecyclerView>(R.id.rv_books)
            loadingProgress!!.visibility = View.INVISIBLE
            if (result == null) {
                recyclerViewBooks!!.visibility = View.INVISIBLE
                textViewError.visibility = View.VISIBLE
            } else {
                recyclerViewBooks!!.visibility = View.VISIBLE
                textViewError.visibility = View.INVISIBLE
                val books = ApiUtil.getBooksFromJson(result)
                val adapter = BooksAdapter(books)
                recyclerViewBooks.adapter = adapter
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()
            val loadingProgress = activity!!.findViewById<ProgressBar>(R.id.pb_loading)
            loadingProgress!!.visibility = View.VISIBLE
        }
    }
}
