package com.jeddi.myquote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListQuotesActivity : AppCompatActivity() {

    companion object {
        private val TAG = ListQuotesActivity::class.java.simpleName
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var listQuotes: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_quotes)

        supportActionBar?.title = "List of Quotes"

        init()

        getListQuotes()
    }

    private fun init() {
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        listQuotes = findViewById<ListView>(R.id.listQuotes)
    }

    private fun getListQuotes() {
        progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://programming-quotes-api.herokuapp.com/quotes"
        client.get(url, object : AsyncHttpResponseHandler() {

            override fun onSuccess(
                statusCode: Int,
                headers:
                Array<Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                progressBar.visibility = View.INVISIBLE

                // Parsing JSON
                val listQuote = ArrayList<String>()
                val result = String(responseBody)

                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n — $author\n")
                    }

                    val adapter = ArrayAdapter(this@ListQuotesActivity, android.R.layout.simple_list_item_1, listQuote)

                    listQuotes.adapter = adapter

                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message,
                        Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable,
            ) {
                // Jika koneksi gagal
                progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                Toast.makeText(this@ListQuotesActivity, errorMessage,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}