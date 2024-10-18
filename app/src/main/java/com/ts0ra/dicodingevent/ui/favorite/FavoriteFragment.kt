package com.ts0ra.dicodingevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.R
import com.ts0ra.dicodingevent.data.SettingPreferences
import com.ts0ra.dicodingevent.data.dataStore
import com.ts0ra.dicodingevent.data.reponse.ListEventsItem
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.databinding.FragmentFavoriteBinding
import com.ts0ra.dicodingevent.databinding.FragmentUpcomingBinding
import com.ts0ra.dicodingevent.helper.ViewModelFactory
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.detail.DetailActivity
import com.ts0ra.dicodingevent.ui.upcoming.UpcomingViewModel

class FavoriteFragment : Fragment() {

    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelFactory.getInstance(requireActivity().application, pref)
    }
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

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

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvents.layoutManager = layoutManager

        favoriteViewModel.getAllFavoriteEvents().observe(viewLifecycleOwner) { events ->
            val items = arrayListOf<FavoriteEvent>()
            events.map {
                val item = FavoriteEvent(
                    id = it.id.toInt(),
                    name = it.name,
                    mediaCover = it.mediaCover)
                items.add(item)
            }
            val adapter = FavoriteAdapter()
            adapter.setListFavoriteEvents(items)
            binding.rvEvents.adapter = adapter

            adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
                override fun onItemClicked(event: FavoriteEvent) {
                    // Handle the click event to navigate to the detail activity
                    val intent = Intent(activity, DetailActivity::class.java).apply {
                        putExtra(EVENT_ID, event.id)
                    }
                    startActivity(intent)
                }
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}