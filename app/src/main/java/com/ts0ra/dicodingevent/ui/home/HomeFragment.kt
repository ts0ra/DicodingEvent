package com.ts0ra.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.data.reponse.ListEventsItem
import com.ts0ra.dicodingevent.databinding.FragmentHomeBinding
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    homeViewModel.searchEvent(searchView.text.toString())
                    false
                }
        }

        homeViewModel.searchEvent.observe(viewLifecycleOwner) { event ->
            setSearchResult(event)
        }
        homeViewModel.eventSearchLoading.observe(viewLifecycleOwner) {
            showSearchLoading(it)
        }

        binding.rvSearchResult.layoutManager = LinearLayoutManager(requireActivity())

        homeViewModel.upcomingEvent.observe(viewLifecycleOwner) { event ->
            setUpcomingEventData(event)
        }

        homeViewModel.finishedEvent.observe(viewLifecycleOwner) { event ->
            setFinishedEventData(event)
        }

        val carouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carouselLayoutManager.setCarouselAlignment(CarouselLayoutManager.ALIGNMENT_CENTER)
        binding.rvUpcomingEvents.layoutManager = carouselLayoutManager
        CarouselSnapHelper().attachToRecyclerView(binding.rvUpcomingEvents)
        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireActivity())

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                homeViewModel.clearSearch()
                homeViewModel.clearErrorMessage()  // Clear the message after showing
            }
        }

        // Handle back press when SearchView is open
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                // If SearchView is open, close it
                binding.searchView.hide()
            } else {
                // Otherwise, handle the default back press
                isEnabled = false  // Disable this callback so the system back press can be processed
                requireActivity().onBackPressedDispatcher.onBackPressed()  // Trigger default back press
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSearchResult(event: List<ListEventsItem>) {
        val adapter = EventAdapter(viewType = 1)
        adapter.submitList(event)
        binding.rvSearchResult.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                // Handle the click event to navigate to the detail activity
                val intent = Intent(activity, DetailActivity::class.java).apply {
                    putExtra(EVENT_ID, event.id)
                }
                startActivity(intent)
            }
        })
    }

    private fun setUpcomingEventData(event: List<ListEventsItem>) {
        val adapter = EventAdapter(viewType = 0)
        adapter.submitList(event.take(5))
        binding.rvUpcomingEvents.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                // Handle the click event to navigate to the detail activity
                val intent = Intent(activity, DetailActivity::class.java).apply {
                    putExtra(EVENT_ID, event.id)
                }
                startActivity(intent)
            }
        })
    }

    private fun setFinishedEventData(event: List<ListEventsItem>) {
        val adapter = EventAdapter(viewType = 1)
        adapter.submitList(event.take(5))
        binding.rvFinishedEvents.adapter = adapter

        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
            override fun onItemClicked(event: ListEventsItem) {
                // Handle the click event to navigate to the detail activity
                val intent = Intent(activity, DetailActivity::class.java).apply {
                    putExtra(EVENT_ID, event.id)
                }
                startActivity(intent)
            }
        })
    }

    private fun showSearchLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.searchProgressBar.visibility = View.VISIBLE
        } else {
            binding.searchProgressBar.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}