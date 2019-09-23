package com.johnfneto.f45challengeserver

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData
import java.net.InetAddress

object Util {

    // variable used to store clients IPs
    // We use an Observer to notify the adapter when there is a new IP address
    var clientsList: MutableLiveData<List<String>?> = MutableLiveData(listOf<String>())

    /**
     * Function used to get the device's IP address
     *
     * Returns a formatted ip address
     */
    fun getLocalIpAddress(context: Context): String {
        val wifiMan = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        wifiInf.ipAddress.let { ipAddress ->
            return String.format("%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff
            )
        }
    }

    /**
     * Function used to get the broadcast address
     *
     * Returns a formatted InetAddress address
     */
    fun getBroadcastAddress(context: Context): InetAddress? {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcp = wifi.dhcpInfo ?: return null
        val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3)
            quads[k] = (broadcast shr k * 8).toByte()
        return InetAddress.getByAddress(quads)
    }
}