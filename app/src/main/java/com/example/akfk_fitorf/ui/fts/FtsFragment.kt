package com.example.akfk_fitorf.ui.fts

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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

        var detail: TextView = binding.textView3

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

                        mListView.setOnItemClickListener { parent, view, position, id ->

                            mListView.visibility = View.INVISIBLE
                            detail.visibility = View.VISIBLE
                            page.visibility = View.INVISIBLE
                            dateFrom.visibility = View.INVISIBLE
                            dateTo.visibility = View.INVISIBLE

                            var info : String = ""
                            info = "Номер акта: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("akt_num").toString() + "\n"
                            info += "Регистрация: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("cert_date").toString() + "\n"
                            info += "Сертификат: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("cert_num").toString() + "\n"
                            info += "Машина: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("transp_num").toString() + "\n"
                            info += "Документ на перевозку: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("transp_doc").toString() + "\n"
                            info += "Экспортер: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("exporter").toString() + "\n"
                            info += "Импортер: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("importer").toString() + "\n"
                            info += "ФТС: " + prettyJson.asJsonArray[id.toInt()].asJsonObject.get("fts").toString() + "\n"
                            detail.text = info

                        }

                    }
                } else {
                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                }

                if (dataItems.size < 10) {
                    step = 1
                }
                else {
                    step++
                }
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

