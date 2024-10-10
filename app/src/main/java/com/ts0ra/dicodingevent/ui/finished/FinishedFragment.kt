package com.ts0ra.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.data.reponse.ListEventsItem
import com.ts0ra.dicodingevent.databinding.FragmentFinishedBinding
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.detail.DetailActivity

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finishedViewModel = ViewModelProvider(this)[FinishedViewModel::class.java]

        finishedViewModel.event.observe(viewLifecycleOwner) { event ->
            setEventData(event)
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvEvents.layoutManager = layoutManager

        finishedViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        finishedViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                finishedViewModel.clearErrorMessage()  // Clear the message after showing
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEventData(event: List<ListEventsItem>) {
        val adapter = EventAdapter(viewType = 3)
        adapter.submitList(event)
        binding.rvEvents.adapter = adapter

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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}