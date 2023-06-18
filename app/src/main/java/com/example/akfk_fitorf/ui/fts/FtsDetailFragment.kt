package com.example.akfk_fitorf.ui.fts

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.akfk_fitorf.databinding.FragmentFtsDetailBinding

class FtsDetailFragment : Fragment() {
    private var _binding: FragmentFtsDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFtsDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
}