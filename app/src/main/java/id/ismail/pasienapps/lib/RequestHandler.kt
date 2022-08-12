package id.ismail.pasienapps.lib

import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.HashMap
import javax.net.ssl.HttpsURLConnection
import kotlin.Throws

class RequestHandler {
    fun sendPostRequest(requestURL: String?, postDataParams: HashMap<String, String>): String {
        val url: URL
        var sb = StringBuilder()
        try {
            url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection
            //Konfigurasi koneksi
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            val os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, StandardCharsets.UTF_8))
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()
            val responseCode = conn.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                sb = StringBuilder()
                var response: String?
                //Reading server response
                while (br.readLine().also { response = it } != null) {
                    sb.append(response)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }
}