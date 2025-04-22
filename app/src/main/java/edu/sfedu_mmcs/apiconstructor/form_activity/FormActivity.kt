package edu.sfedu_mmcs.apiconstructor.form_activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.models.FormViewModel
import edu.sfedu_mmcs.apiconstructor.models.FormViewModelFactory

class FormActivity: AppCompatActivity() {

    private val TAG = "FormActivity"
    private lateinit var myViewModel: FormViewModel
    private val listAdapter = FormAdapter()
    private val contentAdapter = FormAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        myViewModel = ViewModelProvider(
            this,
            FormViewModelFactory(
                intent.getStringExtra("route")!!,
                intent.getStringExtra("method")!!,
                getSharedPreferences("UrlPrefs", MODE_PRIVATE)
            )
        )[FormViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.formsRecycle)
        val recyclerContentView = findViewById<RecyclerView>(R.id.formsContentRecycle)

        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter.setItems(intent.getStringArrayListExtra("fields")!!)

        recyclerContentView.adapter = contentAdapter
        recyclerContentView.layoutManager = LinearLayoutManager(this)
        contentAdapter.setItems(intent.getStringArrayListExtra("content")!!)

        myViewModel.responseRes.observe(this, Observer {
            Log.d(TAG, "onResponse: $it")
            showResultDialog(it)
        })
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        sendBtn.text = intent.getStringExtra("route")
        sendBtn.setOnClickListener {
            myViewModel.sendData(collectData(recyclerView), collectData(recyclerContentView))
        }

    }

    private fun collectData(recyclerView: RecyclerView): MutableMap<String, Any> {
        val data = mutableMapOf<String, Any>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as FormAdapter.ViewHolder
            data[viewHolder.editText.hint.toString()] = viewHolder.editText.text.toString()
        }
        return data
    }

    private fun showResultDialog(text: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Response")
        builder.setMessage(text)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}