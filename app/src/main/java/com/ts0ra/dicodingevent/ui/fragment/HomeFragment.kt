package com.ts0ra.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.databinding.FragmentHomeBinding
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.activity.DetailActivity
import com.ts0ra.dicodingevent.ui.viewmodel.MainViewModel
import com.ts0ra.dicodingevent.ui.viewmodel.ViewModelFactory
import com.ts0ra.dicodingevent.utils.Result

class HomeFragment : Fragment(), EventAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val carouselAdapter = EventAdapter(0, this)
    private val verticalAdapter = EventAdapter(1, this)
    private val searchAdapter = EventAdapter(1, this)

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

        binding.rvUpcomingEvents.apply {
            adapter = carouselAdapter
            layoutManager = CarouselLayoutManager(HeroCarouselStrategy()).apply {
                carouselAlignment = CarouselLayoutManager.ALIGNMENT_CENTER
            }
            CarouselSnapHelper().attachToRecyclerView(binding.rvUpcomingEvents)
        }

        binding.rvFinishedEvents.apply {
            adapter = verticalAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        binding.rvSearchResult.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInformationUpcoming.visibility = View.GONE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInformationUpcoming.visibility = View.VISIBLE
                    //Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                    val error = "Error occur: ${result.error}"
                    Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("HomeFragment", "Upcoming event success")
                    carouselAdapter.submitList(result.data.take(5))
                    if (result.data.isEmpty()) {
                        binding.tvInformationUpcoming.visibility = View.VISIBLE
                    } else {
                        binding.tvInformationUpcoming.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.getFinishedEvent().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInformationFinished.visibility = View.GONE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInformationFinished.visibility = View.VISIBLE
                    //Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                    val error = "Error occur: ${result.error}"
                    Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("HomeFragment", "Finished event success")
                    verticalAdapter.submitList(result.data.take(5))
                    if (result.data.isEmpty()) {
                        binding.tvInformationFinished.visibility = View.VISIBLE
                    } else {
                        binding.tvInformationFinished.visibility = View.GONE
                    }
                }
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    val query = searchView.text.toString()
                    viewModel.searchEvent(query).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Error -> {
                                binding.searchProgressBar.visibility = View.GONE
                                val error = "Error occur: ${result.error}"
                                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
                            }

                            is Result.Loading -> {
                                binding.searchProgressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.searchProgressBar.visibility = View.GONE
                                searchAdapter.submitList(result.data)
                            }
                        }
                    }
                    true
                }

            searchView.addTransitionListener { searchView, _, newState ->
                if (!searchView.isShowing && newState == SearchView.TransitionState.HIDING) {
                    searchAdapter.submitList(emptyList())
                }
            }
        }

        // Handle back press when SearchView is open
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
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

//    private fun setSearchResult(event: List<ListEventsItem>) {
//        val adapter = EventAdapter(viewType = 1)
//        adapter.submitList(event)
//        binding.rvSearchResult.adapter = adapter
//
//        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
//            override fun onItemClicked(event: ListEventsItem) {
//                // Handle the click event to navigate to the detail activity
//                val intent = Intent(activity, DetailActivity::class.java).apply {
//                    putExtra(EVENT_ID, event.id)
//                }
//                startActivity(intent)
//            }
//        })
//    }
//
//    private fun setUpcomingEventData(event: List<ListEventsItem>) {
//        val adapter = EventAdapter(viewType = 0)
//        adapter.submitList(event.take(5))
//        binding.rvUpcomingEvents.adapter = adapter
//
//        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
//            override fun onItemClicked(event: ListEventsItem) {
//                // Handle the click event to navigate to the detail activity
//                val intent = Intent(activity, DetailActivity::class.java).apply {
//                    putExtra(EVENT_ID, event.id)
//                }
//                startActivity(intent)
//            }
//        })
//    }
//
//    private fun setFinishedEventData(event: List<ListEventsItem>) {
//        val adapter = EventAdapter(viewType = 1)
//        adapter.submitList(event.take(5))
//        binding.rvFinishedEvents.adapter = adapter
//
//        adapter.setOnItemClickCallback(object : EventAdapter.OnItemClickCallback {
//            override fun onItemClicked(event: ListEventsItem) {
//                // Handle the click event to navigate to the detail activity
//                val intent = Intent(activity, DetailActivity::class.java).apply {
//                    putExtra(EVENT_ID, event.id)
//                }
//                startActivity(intent)
//            }
//        })
//    }
//
//    private fun showSearchLoading(isLoading: Boolean) {
//        if (isLoading) {
//            binding.searchProgressBar.visibility = View.VISIBLE
//        } else {
//            binding.searchProgressBar.visibility = View.GONE
//        }
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        if (isLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            binding.progressBar.visibility = View.GONE
//        }
//    }


}