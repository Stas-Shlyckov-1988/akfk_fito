package com.example.akfk_fitorf.ui.material

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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.whenCreated
import com.example.akfk_fitorf.databinding.FragmentMaterialBinding
import com.google.gson.JsonObject
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

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var thisMaterial = this

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date_to: String = sdf.format(c.time)

        c.add(Calendar.MONTH, -1)
        val date_from: String = sdf.format(c.time)

        val dateFrom : EditText = binding.editTextDate3
        dateFrom.setText(date_from)

        val dateTo : EditText = binding.editTextDate4
        dateTo.setText(date_to)

        val informer: TextView = binding.informer

        val materialList: ListView = binding.materialList
        val pageBtn: Button = binding.nextBtn
        var step: Int = 1
        pageBtn.setOnClickListener {
            if (pageBtn.text == "назад") {
                materialList.visibility = View.VISIBLE
                informer.visibility = View.INVISIBLE
                pageBtn.visibility = View.VISIBLE
                pageBtn.text = "Вперед"
                dateFrom.visibility = View.VISIBLE
                dateTo.visibility = View.VISIBLE
                return@setOnClickListener
            }
            val dataItems = arrayListOf<String>()
            GlobalScope.launch(Dispatchers.IO) {
                val url = URL("https://akfk.fitorf.ru/api/obrashchenie_insp?sort=&date_from=" + dateFrom.text + "&date_to=" + dateTo.text + "&page=" + step)
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
                        materialList.setOnItemClickListener { parent, view, position, id ->
                            dateFrom.visibility = View.INVISIBLE
                            dateTo.visibility = View.INVISIBLE
                            pageBtn.text = "назад"
                            materialList.visibility = View.INVISIBLE

                            informer.visibility = View.VISIBLE
                            thisMaterial.getMaterialInfo(prettyJson.asJsonArray[id.toInt()].asJsonObject, informer)


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
        pageBtn.callOnClick()

        return root
    }

    fun getMaterialInfo(obj: JsonObject, detail: TextView) {
        var data: String = ""
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://akfk.fitorf.ru/api/obrashchenie_ca/" + obj.get("id").toString())
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

                    // Convert raw JSON to pretty JSON using GSON library
                    val prettyJson = JsonParser.parseString(response)
                    data += "Питомник: " + obj.get("pitomnik").toString() + "\n"
                    data += "Страна: " + obj.get("dep_country").toString() + "\n"
                    data += "Место отгрузки: " + obj.get("mesto_otgruzki").toString() + "\n"
                    data += "Пункт пропуска: " + obj.get("punkt_propusk").toString() + "\n"
                    data += "Место оформления: " + obj.get("mesto_oforml").toString() + "\n\n"


                    data += "Дата: " + prettyJson.asJsonObject.get("date").toString() + "\n\n"
                    data += "Заявитель \n"
                    data += "Имя: " + prettyJson.asJsonObject.get("zayavitel").asJsonObject.get("name").toString() + "\n"
                    data += "Email: " + prettyJson.asJsonObject.get("zayavitel").asJsonObject.get("email").toString() + "\n"
                    data += "Phone: " + prettyJson.asJsonObject.get("zayavitel").asJsonObject.get("phone").toString() + "\n"
                    data += "Inn: " + prettyJson.asJsonObject.get("zayavitel").asJsonObject.get("inn").toString() + "\n\n"

                    if (prettyJson.asJsonObject.get("mesto_poseva").toString() == "null")
                        data += "Место посева: неуказано\n"
                    else
                        data += "Место посева: " + prettyJson.asJsonObject.get("mesto_poseva").toString() + "\n"
                    when (prettyJson.asJsonObject.get("contract").toString()) {
                        "false" -> data += "Контакт: нет\n"
                        "true" -> data += "Контакт: да\n"
                        else -> { // Note the block
                            data += "Контакт: неуказано\n"
                        }
                    }
                    when (prettyJson.asJsonObject.get("plan_zavoza").toString()) {
                        "false" -> data += "План завоза: нет\n"
                        "true" -> data += "План завоза: да\n"
                        else -> { // Note the block
                            data += "План завоза: неуказано\n"
                        }
                    }
                    if (prettyJson.asJsonObject.get("contract_date").toString() == "null")
                        data += "Номер контакта: неуказано\n"
                    else
                        data += "Номер контакта: " + prettyJson.asJsonObject.get("contract_num").toString() + "\n"
                    if (prettyJson.asJsonObject.get("contract_date").toString() == "null")
                        data += "Дата контакта: неуказано\n"
                    else
                        data += "Дата контакта: " + prettyJson.asJsonObject.get("contract_date").toString() + "\n"
                    if (prettyJson.asJsonObject.get("plan_zavoza_date").toString() == "null")
                        data += "Номер план завоза: неуказано\n"
                    else
                        data += "Номер план завоза: " + prettyJson.asJsonObject.get("plan_zavoza_date").toString() + "\n"
                    data += "Статус: " + prettyJson.asJsonObject.get("status").toString() + "\n"

                    detail.text = data

                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}