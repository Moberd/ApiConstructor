package edu.sfedu_mmcs.apiconstructor.list_activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.models.ListViewModel
import edu.sfedu_mmcs.apiconstructor.models.ListViewModelFactory

class ListActivity: AppCompatActivity() {

    private val TAG = "ListActivity"
    private lateinit var myViewModel: ListViewModel
    private val listAdapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myViewModel = ViewModelProvider(
            this,
            ListViewModelFactory(
                intent.getStringExtra("route")!!,
                getSharedPreferences("UrlPrefs", MODE_PRIVATE)
            )
        )[ListViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.routesRecycle)

        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        myViewModel.dataList.observe(this, Observer {
            Log.d(TAG, "onCreate: $it")
            listAdapter.setItems(it)
        })
        myViewModel.getData()

    }
}