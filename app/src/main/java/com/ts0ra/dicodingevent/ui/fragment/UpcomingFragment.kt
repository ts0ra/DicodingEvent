package com.ts0ra.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.remote.reponse.ListEventsItem
import com.ts0ra.dicodingevent.databinding.FragmentUpcomingBinding
import com.ts0ra.dicodingevent.ui.EventAdapter
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.activity.DetailActivity
import com.ts0ra.dicodingevent.ui.viewmodel.MainViewModel
import com.ts0ra.dicodingevent.ui.viewmodel.ViewModelFactory
import com.ts0ra.dicodingevent.utils.Result

class UpcomingFragment : Fragment(), EventAdapter.OnItemClickListener {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val verticalAdapter = EventAdapter(2, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEvents.apply {
            adapter = verticalAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInformation.visibility = View.GONE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInformation.visibility = View.VISIBLE
                    //Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                    val error = "Error occur: ${result.error}"
                    Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    verticalAdapter.submitList(result.data)
                    if (result.data.isEmpty()) {
                        binding.tvInformation.visibility = View.VISIBLE
                    } else {
                        binding.tvInformation.visibility = View.GONE
                    }
                }
            }
        }

//        val layoutManager = LinearLayoutManager(requireActivity())
//        binding.rvEvents.layoutManager = layoutManager
//
//        upcomingViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
//            message?.let {
//                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
//                upcomingViewModel.clearErrorMessage()  // Clear the message after showing
//            }
//        }
//
//        upcomingViewModel.event.observe(viewLifecycleOwner) { event ->
//            setEventData(event)
//        }
//
//        upcomingViewModel.isLoading.observe(viewLifecycleOwner) {
//            showLoading(it)
//        }
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