package com.example.michaelhuff.slushfund

import android.app.*
import android.content.*
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton

import android.widget.EditText
import android.widget.TextView
import com.example.michaelhuff.slushfund.Constants.ALARM_SET_KEY
import com.example.michaelhuff.slushfund.Constants.CHANNEL_ID
import com.example.michaelhuff.slushfund.Constants.DAILY_ALLOWANCE
import com.example.michaelhuff.slushfund.Constants.SLUSH_KEY
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.michaelhuff.slushfund.prefs"
    var prefs: SharedPreferences? = null
    lateinit var slushText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slushText = findViewById(R.id.slushText)
        val subButton = findViewById<FloatingActionButton>(R.id.addExpense)
        val addButton = findViewById<FloatingActionButton>(R.id.subtractExpense)
        val editText = findViewById<EditText>(R.id.editText)
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        var slush = prefs!!.getLong(SLUSH_KEY, 0)

        registerAlarm(this)
        println("look at me: regisered alarm")

        setMoney(slush, slushText, editText)

        subButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            if (expense > 0) {
                slush = slush + expense
                prefs!!.edit().putLong(SLUSH_KEY, slush).apply()
                setMoney(slush, slushText, editText)
            }
        }

        addButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField(editText)
            if (expense > 0) {
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


    override fun onResume() {
        super.onResume()
        if (intent.action == "deposit.money") {
            println("look at me: do something from notification")
            val builder = AlertDialog.Builder(this)
            val n = NumberFormat.getCurrencyInstance(Locale.US)
            val money = n.format(DAILY_ALLOWANCE / 100.0)
            builder.setMessage("Another $money has been deposited to your slush fund")
                    .setPositiveButton(R.string.deposit,
                            DialogInterface.OnClickListener { dialog, id ->
                                var prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
                                var slush = prefs.getLong(SLUSH_KEY, 0)
                                slush += DAILY_ALLOWANCE
                                prefs.edit().putLong(SLUSH_KEY, slush).apply()
                                val n = NumberFormat.getCurrencyInstance(Locale.US)
                                val s = n.format(slush / 100.0)
                                slushText.setText(s)
                                dialog.dismiss()
                            })
            builder.create().show()
        }
    }
    private fun registerAlarm(mainActivity: MainActivity) {

        val filter = IntentFilter()
        val receiver = AlarmReceiver()

        registerReceiver(receiver, filter)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val intent = Intent(mainActivity, AlarmReceiver::class.java)

        intent.setAction("com.example.michaelhuff.slushfund.MONEY")

        val pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
//                1000, // debug value
                pendingIntent)

        prefs!!.edit().putBoolean(ALARM_SET_KEY, true).apply()
    }

    private fun setMoney(slush: Long, slushText: TextView, editText: EditText) {
        val n = NumberFormat.getCurrencyInstance(Locale.US)
        val s = n.format(slush / 100.0)
        slushText.setText(s)
        editText.setText("")
    }

    fun getNumberFromField(editText: EditText): Long {
        if (editText.text.isNotBlank()) {
            return (editText.text.toString().toFloat() * 100).toLong()
        } else {
            return 0
        }
    }
}
