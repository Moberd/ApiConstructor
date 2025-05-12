package edu.sfedu_mmcs.apiconstructor.result_activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import edu.sfedu_mmcs.apiconstructor.R

class ResultActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        try {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonElement = JsonParser.parseString(intent.getStringExtra("result")!!)
            val prettyJson = gson.toJson(jsonElement)
            findViewById<TextView>(R.id.showText).apply{
                text = prettyJson
                movementMethod = ScrollingMovementMethod()
            }
        } catch (e: JsonSyntaxException){
            findViewById<TextView>(R.id.showText).apply{
                text = intent.getStringExtra("result")!!
                movementMethod = ScrollingMovementMethod()
            }
        }

    }

}