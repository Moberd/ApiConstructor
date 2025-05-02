package edu.sfedu_mmcs.apiconstructor.main_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.form_activity.FormActivity
import edu.sfedu_mmcs.apiconstructor.list_activity.ListActivity
import edu.sfedu_mmcs.apiconstructor.models.RouteViewModel
import edu.sfedu_mmcs.apiconstructor.models.RouteViewModelFactory
import edu.sfedu_mmcs.apiconstructor.models.UrlViewModelFactory
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var myViewModel: RouteViewModel
    private val routeButtonsAdapter = RouteButtonsAdapter { routeInfo -> goToActivity(routeInfo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myViewModel = ViewModelProvider(
            this,
             RouteViewModelFactory(getSharedPreferences("UrlPrefs", MODE_PRIVATE))
        )[RouteViewModel::class.java]
        val recyclerView = findViewById<RecyclerView>(R.id.routesRecycle)

        recyclerView.adapter = routeButtonsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        myViewModel.routeList.observe(this, Observer {
            Log.d(TAG, "onCreate: $it")
            routeButtonsAdapter.setRoutes(it)
        })
        myViewModel.getRoutes()

    }

    private fun goToActivity(route: RouteInfo){
        if (route.type == "list"){
            val i = Intent(this, ListActivity::class.java)
            i.putExtra("route", route.route)
            startActivity(i)
        } else if (route.type == "form"){
            val i = Intent(this, FormActivity::class.java)
            i.putExtra("route", route.route)
            i.putExtra("fields", route.fields)
            i.putExtra("method", route.method)
            i.putExtra("content", route.content)
            startActivity(i)
        }

    }
}