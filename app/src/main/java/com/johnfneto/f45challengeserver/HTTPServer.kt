package com.johnfneto.f45challengeserver

import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class HTTPServer @Throws(IOException::class)
constructor() : NanoHTTPD(PORT) {
    private val TAG = javaClass.simpleName

    override fun serve(session: IHTTPSession): Response? {
        val queryParameter = session.queryParameterString

        // Checks if it received a call with a query parameter 'ip='
        if (queryParameter.contains("ip=")) {
            val ipReceived = queryParameter.substringAfterLast("ip=")
            var tmpClientsList: ArrayList<String> = arrayListOf<String>()
            if (Util.clientsList.value!!.isNotEmpty()) {
                tmpClientsList = Util.clientsList.value as ArrayList<String>
            }
            // Updates clientsList with the full clients' IPs
            if (!tmpClientsList.contains(ipReceived)) {
                tmpClientsList.add(ipReceived)
                Util.clientsList.postValue(tmpClientsList)
            }

            val response = ("<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<body>\n"
                    + "\n"
                    + "<p>You sent :"
                    + ipReceived
                    + "</p></body></html>")

            return newFixedLengthResponse(response)
        }
        return null
    }

    companion object {
        val PORT = 12345
    }
}
