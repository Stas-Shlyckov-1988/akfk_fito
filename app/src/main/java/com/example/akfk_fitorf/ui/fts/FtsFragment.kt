package com.example.akfk_fitorf.ui.fts

import android.R
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.akfk_fitorf.databinding.FragmentFtsBinding
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar

class FtsFragment : Fragment() {

    private var _binding: FragmentFtsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFtsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date_to: String = sdf.format(c.time)

        c.add(Calendar.MONTH, -1)
        val date_from: String = sdf.format(c.time)

        val dateFrom : EditText = binding.editTextDate
        dateFrom.setText(date_from)

        val dateTo : EditText = binding.editTextDate2
        dateTo.setText(date_to)

        var mListView: ListView = binding.ftsList
        var page: Button = binding.pageBtn
        var step: Int = 1
        page.setOnClickListener {
            val dataItems = arrayListOf<String>()
            GlobalScope.launch(Dispatchers.IO) {
                val url = URL("https://akfk.fitorf.ru/api/fts?date_from=" + dateFrom.text + "&date_to=" + dateTo.text + "&page=" + step)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.doInput = true
                httpURLConnection.doOutput = false
                // Check if the connection is successful
                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                    withContext(Dispatchers.Main) {

                        val prettyJson = JsonParser.parseString(response)
                        for (obj in prettyJson.asJsonArray) {
                            dataItems.add(obj.asJsonObject.get("akt_num").toString())
                        }
                        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_list_item_1, dataItems)
                        mListView.adapter = adapter

                    }
                } else {
                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                }
                step++
            }

        }
        page.callOnClick()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

