package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var contentIntent: Intent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager
    private var downloadUrl = ""
    private var downloadTitle = ""
    private var downloadDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                //download is complete, show notification
                //create an instance of notificationMAnager
                notificationManager = context?.let {
                    ContextCompat.getSystemService(
                        it,
                        NotificationManager::class.java
                    )
                } as NotificationManager


                //get download status is success or failed using DownloadManager.Query
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            notificationManager.sendNotification(
                                getString(R.string.success),
                                context
                            )
                            custom_button.buttonState = ButtonState.Completed
                        }
                        DownloadManager.STATUS_FAILED -> {
                            notificationManager.sendNotification(
                                getString(R.string.failed),
                                context
                            )
                            custom_button.buttonState = ButtonState.Completed
                        }


                    }
                }

                //create Notification Channel
                createChannel(
                    getString(CHANNEL_ID),
                    getString(CHANNEL_NAME)
                )

            }
        }
    }

    private fun download() {

        val checkedID = radio_group.checkedRadioButtonId

        if (checkedID == -1) {
            Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_LONG).show()
        } else {


            custom_button.buttonState = ButtonState.Loading

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


            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(downloadTitle)
                .setDescription(downloadDescription)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
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
        private const val CHANNEL_ID = R.string.channelId
        private const val CHANNEL_NAME = R.string.download_status
        private const val NOTIFICATION_ID = 0
    }


    private fun NotificationManager.sendNotification(
        messageBody: String,
        applicationContext: Context
    ) {

        //open activity when user clicked on notification
        contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra(getString(R.string.intent_status), messageBody)
        contentIntent.putExtra(
            getString(R.string.intent_filename),
            "$downloadTitle - $downloadDescription"
        )
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //get instance of Notification Builder
        val builder = NotificationCompat.Builder(applicationContext, getString(CHANNEL_ID))
            //set the notification details
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(downloadTitle)
            .setContentText(getString(R.string.is_downloaded, downloadDescription))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.check_status),
                pendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notify(NOTIFICATION_ID, builder.build())
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.download_status)
                setShowBadge(false)
            }


            //get instance of NotificationManager
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}
