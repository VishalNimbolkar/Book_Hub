package com.vishal.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vishal.bookhub.R
import com.vishal.bookhub.adapter.DashboardRecyclerAdapter
import com.vishal.bookhub.model.Book
import com.vishal.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    val bookInfoList = arrayListOf<Book>()

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    val ratingComparator = Comparator<Book>{book1, book2 ->
        if(book1.bookRating.compareTo(book2.bookRating,true)==0){
            book1.bookName.compareTo(book2.bookName,true)
        }
        else{
            book1.bookRating.compareTo(book2.bookRating, true)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.RecyclerDashboard)


        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)
1
        val url = "http://13.235.250.119/v1/book/fetch_books"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {
                    //code for handle the response

                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")

                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJSONObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJSONObject.getString("book_id"),
                                    bookJSONObject.getString("name"),
                                    bookJSONObject.getString("author"),
                                    bookJSONObject.getString("rating"),
                                    bookJSONObject.getString("price"),
                                    bookJSONObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager

                                recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error is occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }, Response.ErrorListener {
                    //code for handle the error

                    if (activity != null){
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred..",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val Headers = HashMap<String, String>()
                    Headers["Content-type"] = "application/json"
                    Headers["token"] = "0a7d1c35f05b4d"
                    return Headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Active")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_sort){
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}

