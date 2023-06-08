package com.example.akfk_fitorf.ui.material

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.akfk_fitorf.databinding.FragmentMaterialBinding
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar

class MaterialFragment : Fragment() {

    private var _binding: FragmentMaterialBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date_to: String = sdf.format(c.time)

        c.add(Calendar.MONTH, -1)
        val date_from: String = sdf.format(c.time)

        val dateFrom : EditText = binding.editTextDate3
        dateFrom.setText(date_from)

        val dateTo : EditText = binding.editTextDate4
        dateTo.setText(date_to)

        val dataItems = arrayListOf<String>()
        val materialList: ListView = binding.materialList
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://akfk.fitorf.ru/api/obrashchenie_insp?sort=&date_from=" + date_from + "&date_to=" + date_to + "&page=1")
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
                        dataItems.add(obj.asJsonObject.get("pitomnik").toString())
                    }
                    val adapter = ArrayAdapter(requireActivity(), R.layout.simple_list_item_1, dataItems)
                    materialList.adapter = adapter

                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}