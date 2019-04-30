package com.example.michaelhuff.slushfund

import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.*
import android.content.res.Configuration
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.widget.EditText
import android.widget.TextView
import com.example.michaelhuff.slushfund.Constants.ALARM_SET_KEY
import com.example.michaelhuff.slushfund.Constants.CHANNEL_ID
import com.example.michaelhuff.slushfund.Constants.DAILY_ALLOWANCE
import com.example.michaelhuff.slushfund.Constants.SLUSH_KEY
import com.example.michaelhuff.slushfund.persistance.Transaction
import com.example.michaelhuff.slushfund.persistance.TransactionViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.michaelhuff.slushfund.prefs"
    var prefs: SharedPreferences? = null
    lateinit var transactionViewModel: TransactionViewModel
    lateinit var slushText: TextView
    lateinit var etAmount: TextView
    lateinit var etType: TextView
    lateinit var adapter: TransactionAdapter
    lateinit var vm: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transactionViewModel = TransactionViewModel(application)
        initData(application)

        slushText = findViewById(R.id.slushText)
        val subButton = findViewById<FloatingActionButton>(R.id.addExpense)
        val addButton = findViewById<FloatingActionButton>(R.id.subtractExpense)
        etAmount = findViewById<EditText>(R.id.editText)
        etType = findViewById<EditText>(R.id.editText2)
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        var slush = prefs!!.getLong(SLUSH_KEY, 0)

        registerAlarm(this)
        println("look at me: regisered alarm")

        setMoney(slush, slushText)

        addButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField()
            if (expense > 0) {
                transactionViewModel.insert(Transaction(expense, etType.text.toString(),false, Date()))
                slush = slush - expense
                prefs!!.edit().putLong(SLUSH_KEY, slush).apply()
                setMoney(slush, slushText)
            }
        }

        subButton.setOnClickListener {
            var slush = prefs!!.getLong(SLUSH_KEY, 0)
            var expense = getNumberFromField()
            if (expense > 0) {
                transactionViewModel.insert(Transaction(expense, etType.text.toString(),true, Date()))

                slush = slush + expense
                prefs!!.edit().putLong(SLUSH_KEY, slush).apply()
                setMoney(slush, slushText)
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


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TransactionAdapter(transactionViewModel)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        recyclerView.adapter = adapter
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        adapter.notifyDataSetChanged()
    }

    private fun initData(application: Application) {


        transactionViewModel.deleteAllTransactions()
//        transactionViewModel.insert(Transaction(1L, "foobar", true, Date()))
//        transactionViewModel.insert(Transaction(2L,"fizzbazz", true, Date()))
//        transactionViewModel.insert(Transaction(5L,"sneederflee", true, Date()))
//        transactionViewModel.insert(Transaction(-5L,"zipzorp", false, Date()))



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
                                transactionViewModel.insert(Transaction(DAILY_ALLOWANCE, "Daily Allowance", true, Date()))
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

        // start listening to data

        val dataOfTransactions = transactionViewModel.transactionsList
        // Create the observer which updates the UI.
        val transactionListObserver = android.arch.lifecycle.Observer<List<Transaction>> { transactionList ->
            // do thing
            adapter.setTransactionList(transactionList)
            adapter.notifyDataSetChanged()
        }
        dataOfTransactions.observe(this, transactionListObserver)
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

        val pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
//                1000, // debug value
                pendingIntent)

        prefs!!.edit().putBoolean(ALARM_SET_KEY, true).apply()
    }

    private fun setMoney(slush: Long, slushText: TextView) {
        val n = NumberFormat.getCurrencyInstance(Locale.US)
        val s = n.format(slush / 100.0)
        slushText.setText(s)
        etAmount.setText("")
        etType.setText("")
    }

    fun getNumberFromField(): Long {
        if (etAmount.text.isNotBlank()) {
            return (editText.text.toString().toFloat() * 100).toLong()
        } else {
            return 0
        }
    }
}
