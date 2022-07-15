package com.vishal.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.vishal.bookhub.R
import com.vishal.bookhub.adapter.FavouriteRecyclerAdaptor
import com.vishal.bookhub.database.BookDatabase
import com.vishal.bookhub.database.BookEntity

@Suppress("DEPRECATION")
class FavouriteFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: FavouriteRecyclerAdaptor
    var dbBookList = listOf<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.RecyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity as Context, 2)
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        if (activity!=null){
            progressBar.visibility = View.GONE
            recyclerAdaptor = FavouriteRecyclerAdaptor(activity as Context, dbBookList)
            recyclerFavourite.adapter = recyclerAdaptor
            recyclerFavourite.layoutManager = layoutManager

        }
        return view
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

            return db.bookDao().getAllBooks()
        }

    }

}




