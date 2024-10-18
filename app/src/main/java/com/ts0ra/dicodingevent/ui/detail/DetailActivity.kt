package com.ts0ra.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.R
import com.ts0ra.dicodingevent.data.SettingPreferences
import com.ts0ra.dicodingevent.data.dataStore
import com.ts0ra.dicodingevent.data.reponse.Event
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.databinding.ActivityDetailBinding
import com.ts0ra.dicodingevent.helper.ViewModelFactory
import com.ts0ra.dicodingevent.ui.EventAdapter.Companion.EVENT_ID
import com.ts0ra.dicodingevent.ui.setting.SettingViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        val pref = SettingPreferences.getInstance(this.dataStore)
        ViewModelFactory.getInstance(this.application, pref)
    }
    private val settingViewModel by viewModels<SettingViewModel> {
        val pref = SettingPreferences.getInstance(this.dataStore)
        ViewModelFactory.getInstance(this.application, pref)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.topAppBar.navigationIcon?.setTint(getColor(R.color.navigation_icon_color))

        val eventId = intent.getIntExtra(EVENT_ID, -1)

        //val detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        detailViewModel.getDetailEvent(eventId.toString())

        detailViewModel.event.observe(this) { event ->
            setDetailEvent(event)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                binding.scrollView.visibility = View.GONE
                detailViewModel.clearErrorMessage()  // Clear the message after showing
            }
        }

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setDetailEvent(event: Event) {
        Glide.with(binding.root.context).load(event.mediaCover).into(binding.imgDetailMediaCover)
        binding.tvDetailName.text = event.name
        binding.tvDetailOwnerName.text = event.ownerName
        binding.tvDetailCategory.text = getString(R.string.detail_category, event.category)
        binding.tvDetailRemainingQuota.text = getString(R.string.detail_remaining_quota, getRemainingQuota(event).toString())
        binding.tvDetailEventTime.text = getString(R.string.detail_event_time, event.beginTime)
        binding.tvDetailDescription.text = HtmlCompat.fromHtml(
            event.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.btnRegister.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(event.link)
            }
            startActivity(intent)
        }

        detailViewModel.getFavoriteEventById(event.id).observe(this) { favEvent ->
            if (favEvent == null) {
                binding.fabFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fav))
                binding.fabFav.setOnClickListener {
                    val newFavEvent = FavoriteEvent(event.id, event.name, event.mediaCover)
                    detailViewModel.insert(newFavEvent)
                }
            } else {
                binding.fabFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fav_filled))
                binding.fabFav.setOnClickListener {
                    detailViewModel.delete(favEvent)
                }
            }
        }
    }

    private fun getRemainingQuota(event: Event): Int {
        return event.quota - event.registrants
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        }
    }

    companion object {
        const val EVENT_ID = "event_id"
    }
}