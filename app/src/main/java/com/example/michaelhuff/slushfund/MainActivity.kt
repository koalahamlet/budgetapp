package com.example.michaelhuff.slushfund

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton

import android.widget.EditText
import android.widget.TextView
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.michaelhuff.slushfund.prefs"
    var prefs: SharedPreferences? = null
    val SLUSH_KEY = "slush"
//    val editText: EditText? = null
//    val slushText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        val subButton = findViewById<FloatingActionButton>(R.id.addExpense)
        val addButton = findViewById<FloatingActionButton>(R.id.subtractExpense)
        val editText = findViewById<EditText>(R.id.editText)
        val slushText = findViewById<TextView>(R.id.slushText)

        var slush = prefs!!.getLong(SLUSH_KEY, 0)

        setMoney(slush, slushText, editText)


        subButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            slush = slush + expense

            prefs!!.edit().putLong(SLUSH_KEY, slush).apply()

            setMoney(slush, slushText, editText)
            val price = """
                        ${'$'}9.99
                        """
        }

        addButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            slush = slush - expense

            prefs!!.edit().putLong(SLUSH_KEY, slush).apply()

            setMoney(slush, slushText, editText)
        }
    }

    private fun setMoney(slush: Long, slushText: TextView, editText: EditText) {
        val n = NumberFormat.getCurrencyInstance(Locale.US)
        val s = n.format(slush / 100.0)
        slushText.setText(s)
        editText.setText("")
    }

    fun getNumberFromField(editText: EditText) : Long {
        return (editText.text.toString().toFloat()*100).toLong()
    }
}
