package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import kotlinx.coroutines.*
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val db by lazy { prepareDatabase() }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    companion object {
        const val ID: Long = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val result = db.totalDao().getTotal(ID)

            withContext(Dispatchers.Main) {
                if (result != null) {
                    Toast.makeText(
                        this@MainActivity,
                        "Last Updated: ${result.total.date}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No timestamp found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) {
            updateText(it)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            this,
            TotalDatabase::class.java,
            "total_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    private fun initializeValueFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {

            val result = db.totalDao().getTotal(ID)

            if (result == null) {
                val newData = Total(
                    id = ID,
                    total = TotalObject(
                        value = 0,
                        date = Date().toString()
                    )
                )

                db.totalDao().insert(newData)

                withContext(Dispatchers.Main) {
                    viewModel.setTotal(0)
                }

            } else {
                withContext(Dispatchers.Main) {
                    viewModel.setTotal(result.total.value)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        CoroutineScope(Dispatchers.IO).launch {

            val updatedObject = TotalObject(
                value = viewModel.total.value ?: 0,
                date = Date().toString()
            )

            db.totalDao().update(
                Total(ID, updatedObject)
            )
        }
    }
}
