package com.ts0ra.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import com.ts0ra.dicodingevent.data.database.preference.dataStore
import com.ts0ra.dicodingevent.data.database.local.entity.FavoriteEvent
import com.ts0ra.dicodingevent.databinding.FragmentFavoriteBinding
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.viewmodel.ViewModelFactory
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.activity.DetailActivity

import com.ts0ra.dicodingevent.ui.viewmodel.MainViewModel
import com.ts0ra.dicodingevent.utils.Result

class FavoriteFragment : Fragment(), EventAdapter.OnItemClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val verticalAdapter = EventAdapter(4, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEvents.apply {
            adapter = verticalAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        viewModel.getFavoriteEvent().observe(viewLifecycleOwner) { result ->
            verticalAdapter.submitList(result)
            if (result.isEmpty()) {
                binding.tvInformation.visibility = View.VISIBLE
            } else {
                binding.tvInformation.visibility = View.GONE
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(event: EventEntity) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra(EVENT_ID, event.id)
        startActivity(intent)
    }

}