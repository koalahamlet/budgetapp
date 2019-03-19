package com.example.michaelhuff.slushfund

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.LocalBroadcastManager
import android.widget.Button

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.michaelhuff.slushfund.Constants.CHANNEL_ID
import com.example.michaelhuff.slushfund.Constants.SLUSH_KEY
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.michaelhuff.slushfund.prefs"
    var prefs: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerAlarm(this)

        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        val nowButton = findViewById<Button>(R.id.nowButton)
        val subButton = findViewById<FloatingActionButton>(R.id.addExpense)
        val addButton = findViewById<FloatingActionButton>(R.id.subtractExpense)
        val editText = findViewById<EditText>(R.id.editText)
        val slushText = findViewById<TextView>(R.id.slushText)

        var slush = prefs!!.getLong(SLUSH_KEY, 0)

        setMoney(slush, slushText, editText)




        nowButton.setOnClickListener {
            var i = Intent()
            i.setAction("com.example.michaelhuff.slushfund.MONEY")
            LocalBroadcastManager.getInstance(it.context).sendBroadcast(i)
            Toast.makeText(it.context, "woweee",Toast.LENGTH_SHORT).show()
        }

        subButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            if (expense >0) {
                slush = slush + expense

                prefs!!.edit().putLong(SLUSH_KEY, slush).apply()

                setMoney(slush, slushText, editText)
            }
        }

        addButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            if (expense >0) {
                slush = slush - expense

                prefs!!.edit().putLong(SLUSH_KEY, slush).apply()

                setMoney(slush, slushText, editText)
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

    }

    private fun registerAlarm(mainActivity: MainActivity) {

        val filter = IntentFilter("com.example.michaelhuff.slushfund.MONEY")
        val receiver = AlarmReceiver()

        registerReceiver(receiver, filter)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val intent = Intent(mainActivity, AlarmReceiver::class.java)

        intent.setAction("com.example.michaelhuff.slushfund.MONEY")

        val pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
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
