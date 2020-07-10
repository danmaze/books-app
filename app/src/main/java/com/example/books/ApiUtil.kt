package com.example.books

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object ApiUtil {

    private const val BASE_API_URL = "https://www.googleapis.com/books/v1/volumes"
    private const val QUERY_PARAMETER_KEY = "q"
    private const val KEY = "key"
    private const val API_KEY = "AIzaSyCO7klgIOIMeSP3JThsZ1BRSVOjlMcg_Wg"
    private const val TITLE = "intitle:"
    private const val AUTHOR = "inauthor:"
    private const val PUBLISHER = "inpublisher:"
    private const val ISBN = "isbn:"

    fun buildUrl(title: String): URL? {
        var url: URL? = null
        val uri = Uri.parse(BASE_API_URL).buildUpon()
            .appendQueryParameter(QUERY_PARAMETER_KEY, title)
            .appendQueryParameter(KEY, API_KEY)
            .build()
        try {
            url = URL(uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }

    fun buildUrl(title: String, author: String, publisher: String, isbn: String): URL? {
        var url: URL? = null
        val sb = StringBuilder()
        if (!title.isEmpty()) sb.append("$TITLE$title+")
        if (!author.isEmpty()) sb.append("$AUTHOR$author+")
        if (!publisher.isEmpty()) sb.append("$PUBLISHER$publisher+")
        if (!isbn.isEmpty()) sb.append("$ISBN$isbn+")
        sb.setLength(sb.length - 1)     // Removes the last character
        val query = sb.toString()
        val uri = Uri.parse(BASE_API_URL).buildUpon()
            .appendQueryParameter(QUERY_PARAMETER_KEY, query)
            .appendQueryParameter(KEY, API_KEY)
            .build()
        try {
            url = URL(uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }

    @Throws(IOException::class)
    fun getJson(url: URL): String? {
        val connection = url.openConnection() as HttpURLConnection
        try {
            val stream = connection.inputStream
            val scanner = Scanner(stream)
            scanner.useDelimiter("\\A")
            val hasData = scanner.hasNext()
            return if (hasData) {
                scanner.next()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
            return null
        } finally {
            connection.disconnect()
        }
    }

    fun getBooksFromJson(json: String): ArrayList<Book> {
        val idKey = "id"
        val titleKey = "title"
        val subtitleKey = "subtitle"
        val authorsKey = "authors"
        val publisherKey = "publisher"
        val publishedDateKey = "publishedDate"
        val itemsKey = "items"
        val volumeInfoKey = "volumeInfo"
        val descriptionKey = "description"
        val imageInfoKey = "imageLinks"
        val thumbnailKey = "thumbnail"

        val books = ArrayList<Book>()
        try {
            val jsonBooks = JSONObject(json)
            val arrayBooks = jsonBooks.getJSONArray(itemsKey)
            val numberOfBooks = arrayBooks.length()
            for (i in 0 until numberOfBooks) {
                val bookJSON = arrayBooks.getJSONObject(i)
                val volumeInfoJSON = bookJSON.getJSONObject(volumeInfoKey)
                var imageLinksJSON: JSONObject? = null
                if (volumeInfoJSON.has(imageInfoKey)) {
                    imageLinksJSON = volumeInfoJSON.getJSONObject(imageInfoKey)
                }
                var authorNum: Int
                authorNum = try {
                    volumeInfoJSON.getJSONArray(authorsKey).length()
                } catch (e: Exception) {
                    0
                }
                val authors = arrayOfNulls<String>(authorNum)
                for (j in 0 until authorNum) {
                    authors[j] = volumeInfoJSON.getJSONArray(authorsKey).get(j).toString()
                }
                val book = Book(
                    bookJSON.getString(idKey),
                    volumeInfoJSON.getString(titleKey),
                    if (volumeInfoJSON.isNull(subtitleKey)) "" else volumeInfoJSON.getString(subtitleKey),
                    authors,
                    if (volumeInfoJSON.isNull(publisherKey)) "" else volumeInfoJSON.getString(publisherKey),
                    if (volumeInfoJSON.isNull(publishedDateKey)) "" else volumeInfoJSON.getString(publishedDateKey),
                    if (volumeInfoJSON.isNull(descriptionKey)) "" else volumeInfoJSON.getString(descriptionKey),
                    if (imageLinksJSON == null) "" else imageLinksJSON.getString(thumbnailKey)
                )
                books.add(book)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return books
    }
}
