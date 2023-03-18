package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var fileName: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            fileName = intent.getStringExtra(getString(R.string.intent_filename)).toString()
            status = intent.getStringExtra(getString(R.string.intent_status)).toString()
        }

        txt_filename.text = fileName
        txt_status.text = status
        if (status == getString(R.string.success)) {
            txt_status.setTextColor(getColor(R.color.green))
        } else {
            txt_status.setTextColor(getColor(R.color.red))
        }
    }

}
