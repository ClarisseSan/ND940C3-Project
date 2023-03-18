package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var fileName: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        //delete current notification
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        //get extras from intent
        if (intent != null) {
            fileName = intent.getStringExtra(getString(R.string.intent_filename)).toString()
            status = intent.getStringExtra(getString(R.string.intent_status)).toString()
        }

        //set text
        txt_filename.text = fileName
        txt_status.text = status
        if (status == getString(R.string.success)) {
            txt_status.setTextColor(getColor(R.color.green))
        } else {
            txt_status.setTextColor(getColor(R.color.red))
        }

    }

}
