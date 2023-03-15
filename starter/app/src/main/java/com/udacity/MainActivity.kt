package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        radio_group.setOnCheckedChangeListener { group, checkedID ->
            //Toast.makeText(this, checkedID.toString(), Toast.LENGTH_SHORT).show()

        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {

        val checkedID = radio_group.checkedRadioButtonId

        if (checkedID == -1) {
            Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_LONG).show()
        } else {
            var downloadUrl = ""
            var downloadTitle = ""
            var downloadDescription = ""

            val selectedRadio: RadioButton = findViewById(checkedID)
            when (selectedRadio.getId()) {
                R.id.radio_glide -> {
                    downloadUrl = URL_GLIDE
                    downloadTitle = getString(R.string.glide_title)
                    downloadDescription = getString(R.string.glide_description)
                }

                R.id.radio_loadapp -> {
                    downloadUrl = URL_LOADAPP
                    downloadTitle = getString(R.string.loadapp_title)
                    downloadDescription = getString(R.string.loadapp_description)
                }
                R.id.radio_retrofit -> {
                    downloadUrl = URL_RETROFIT
                    downloadTitle = getString(R.string.retrofit_title)
                    downloadDescription = getString(R.string.retrofit_description)
                }
            }

            Toast.makeText(this, downloadUrl, Toast.LENGTH_LONG).show()

            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(downloadTitle)
                .setDescription(downloadDescription)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.


        }


    }

    companion object {
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
