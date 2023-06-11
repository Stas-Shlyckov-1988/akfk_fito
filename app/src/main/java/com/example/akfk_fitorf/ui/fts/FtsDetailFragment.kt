package com.example.akfk_fitorf.ui.fts

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import com.example.akfk_fitorf.databinding.FragmentFtsBinding


class FtsDetailFragment : DialogFragment() {

    private var title: String? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            title = it.getString(ARG_TITLE)
//            message = it.getString(ARG_MESSAGE)
//        }
    }


    @Override
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val arg = arguments
            // Use the parameters by accessing the key from variable "arg"
            val myBuilder = AlertDialog.Builder(it)
            myBuilder
                .setTitle("test")
                .setMessage("test")
                .setPositiveButton("OK") { _, _ -> }
            myBuilder.create()

        }
    }

    fun show(fragment: FragmentContainerView, tag: Any) {
        println(2222)
    }


    companion object {
        val TAG: Any
            get() {
                return "FtsDetailDialog"
            }

        fun newInstance(s: String) {
            val TAG = "FtsDetailDialog"
            val ARG_TITLE = "argTitle"
            val ARG_MESSAGE = "argMessage"

            fun newInstance(title: String, message: String) = FtsDetailFragment().apply {
                var arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }

}