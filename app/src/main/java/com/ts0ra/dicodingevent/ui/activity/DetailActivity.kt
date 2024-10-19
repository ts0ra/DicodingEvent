package com.ts0ra.dicodingevent.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.ts0ra.dicodingevent.R
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import com.ts0ra.dicodingevent.data.database.preference.dataStore
import com.ts0ra.dicodingevent.data.database.remote.reponse.Event
import com.ts0ra.dicodingevent.data.database.local.entity.FavoriteEvent
import com.ts0ra.dicodingevent.databinding.ActivityDetailBinding
import com.ts0ra.dicodingevent.ui.viewmodel.ViewModelFactory
import com.ts0ra.dicodingevent.ui.viewmodel.MainViewModel
import com.ts0ra.dicodingevent.utils.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
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

        viewModel.getDetailEvent(eventId).observe(this) { result ->
            when (result) {
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.GONE
                    binding.tvInformation.visibility = View.VISIBLE
                    val error = "Error occur: ${result.error}"
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                }

                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                    binding.tvInformation.visibility = View.GONE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    binding.tvInformation.visibility = View.GONE
                    val event = result.data
                    binding.fabFav.setImageResource(
                        if (event.isFavorite) R.drawable.ic_fav_filled else R.drawable.ic_fav
                    )
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

                    binding.fabFav.setOnClickListener {
                        event.isFavorite = !event.isFavorite
                        if (event.isFavorite) {
                            binding.fabFav.setImageResource(R.drawable.ic_fav_filled)
                            viewModel.addFavoriteEvent(event)
                        } else {
                            binding.fabFav.setImageResource(R.drawable.ic_fav)
                            viewModel.deleteFavoriteEvent(event)
                        }
                    }
                }
            }
        }

        viewModel.getThemeSetting().observe(this) { isDarkModeEnable: Boolean ->
            if (isDarkModeEnable) {
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

    private fun getRemainingQuota(event: EventEntity): Int {
        return event.quota - event.registrants
    }

    companion object {
        const val EVENT_ID = "event_id"
    }
}