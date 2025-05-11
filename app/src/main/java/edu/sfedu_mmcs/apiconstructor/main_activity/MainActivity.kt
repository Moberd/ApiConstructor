package edu.sfedu_mmcs.apiconstructor.main_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.form_activity.FormActivity
import edu.sfedu_mmcs.apiconstructor.list_activity.ListActivity
import edu.sfedu_mmcs.apiconstructor.settings_activity.SettingsActivity
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var myViewModel: RouteViewModel
    private val routeButtonsAdapter = RouteButtonsAdapter { routeInfo -> goToActivity(routeInfo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
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
            i.putExtra("security", route.security)
            startActivity(i)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Navigate to SettingsActivity
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}