package com.johnfneto.f45challengeserver

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private val PORT = 12345 //port that I’m using
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    private lateinit var server: HTTPServer
    private lateinit var listView: ListView
    private var adapter: ArrayAdapter<String>? = null
    var tmpClientsList: ArrayList<String> = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            server = HTTPServer()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start HTTPServer :$e")
        }

        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tmpClientsList)
        listView.adapter = adapter

        findViewById<TextView>(R.id.serverInfo).text =
            String.format("Server (phone 1) :" + Util.getLocalIpAddress(context = applicationContext))

        findViewById<Button>(R.id.startServerButton).setOnClickListener {
            if (!server.isAlive) {
                server.start()
            }
        }

        findViewById<Button>(R.id.broadcastButton).setOnClickListener {
            startBroadcast()
        }

        /**
         * We observe clientsList to know when there is a new client IP received
         *
         */
        Util.clientsList.observe(this@MainActivity, Observer {
            tmpClientsList.clear()
            // Updates @clientsList with the full list of clients' IPs and then notifies the adapter of a change
            for (item in Util.clientsList.value!!.iterator()) {
                tmpClientsList.add(item)
            }
            adapter?.run {
                notifyDataSetChanged()
            }
        })
    }

    /**
     * Function used to broadcast the device IP.
     *
     */
    private fun startBroadcast() {
        val messageStr = Util.getLocalIpAddress(context = applicationContext)
        scope.launch {
            try {
                val message = messageStr.toByteArray()
                val packet = DatagramPacket(message, messageStr.length, Util.getBroadcastAddress(context = applicationContext), PORT)
                DatagramSocket().send(packet)
            } catch (e: Exception) {
                Log.e(TAG, "Error broadcasting : $e")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}
