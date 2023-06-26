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
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.akfk_fitorf.databinding.FragmentSurBinding
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SurFragment : Fragment() {

    private var _binding: FragmentSurBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var thisSur = this

        val informer: TextView = binding.informer

        val surlList: ListView = binding.surlList
        val pageBtn: Button = binding.nextBtn
        var step: Int = 1
        pageBtn.setOnClickListener {
            if (pageBtn.text == "назад") {
                surlList.visibility = View.VISIBLE
                informer.visibility = View.INVISIBLE
                pageBtn.visibility = View.VISIBLE
                pageBtn.text = "Вперед"
                return@setOnClickListener
            }
            val dataItems = arrayListOf<String>()
            GlobalScope.launch(Dispatchers.IO) {
                val url = URL("https://akfk.fitorf.ru/akfk/api/sur/list?page=" + step)
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
                            dataItems.add(obj.asJsonObject.get("c_name").toString() + "\n" + obj.asJsonObject.get("p_name").toString())
                        }
                        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_list_item_1, dataItems)
                        surlList.adapter = adapter
                        surlList.setOnItemClickListener { parent, view, position, id ->
                            pageBtn.text = "назад"
                            surlList.visibility = View.INVISIBLE

                            informer.visibility = View.VISIBLE
                            thisSur.getSurInfo(prettyJson.asJsonArray[id.toInt()].asJsonObject, informer)


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

    fun getSurInfo(obj: JsonObject, detail: TextView) {
        var data: String = ""
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://akfk.fitorf.ru/api/sur/" + obj.get("id").toString())
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
                    data += "Продукт: " + obj.get("p_name").toString() + "\n"
                    data += "Страна: " + obj.get("c_name").toString() + "\n"
                    data += "Базовый риск: " + obj.get("basic_risk").toString() + "\n"
                    data += "Индекс риск: " + obj.get("index_risk").toString() + "\n"
                    when (prettyJson.asJsonObject.get("vvoz_zapr").toString()) {
                        "false" -> data += "Запрет ввоза: нет\n"
                        "true" -> data += "Запрет ввоза: да\n"
                        else -> { // Note the block
                            data += "Запрет ввоза: неуказано\n"
                        }
                    }
                    when (prettyJson.asJsonObject.get("high_risk").toString()) {
                        "false" -> data += "Большой риск: нет\n\n"
                        "true" -> data += "Большой риск: да\n\n"
                        else -> { // Note the block
                            data += "Большой риск: неуказано\n\n"
                        }
                    }

                    data += "Зима: " + prettyJson.asJsonObject.get("zima").toString() + "\n"
                    data += "Весна: " + prettyJson.asJsonObject.get("vesna").toString() + "\n"
                    data += "Лето: " + prettyJson.asJsonObject.get("leto").toString() + "\n"
                    data += "Осень: " + prettyJson.asJsonObject.get("osen").toString() + "\n\n"

                    data += "Объект карантин: " + prettyJson.asJsonObject.get("quarantine_obj").toString() + "\n"
                    data += "Вредные организмы: " + prettyJson.asJsonObject.get("vred_org").toString() + "\n"
                    data += "Нотификация КВО: " + prettyJson.asJsonObject.get("notifications_kvo").toString() + "\n"
                    data += "Введ. фитосаниторийные меры: " + prettyJson.asJsonObject.get("vved_fitosanit_mery").toString() + "\n"
                    data += "Нарушение учета требований ведомства: " + prettyJson.asJsonObject.get("narushen_treb_uch_ved").toString() + "\n"
                    data += "Нарушение требований производства: " + prettyJson.asJsonObject.get("narushen_treb_proizv").toString() + "\n"
                    //data += "nokzr_mery: " + prettyJson.asJsonObject.get("nokzr_mery").toString() + "\n"
                    data += "Ограничение карантийных объектов: " + prettyJson.asJsonObject.get("ochagi_quarantine_obj").toString() + "\n"
                    data += "Сумма риска: " + prettyJson.asJsonObject.get("sum_risk").toString() + "\n"

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