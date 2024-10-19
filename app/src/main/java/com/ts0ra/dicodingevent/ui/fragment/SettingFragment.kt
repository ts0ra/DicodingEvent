package com.ts0ra.dicodingevent.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ts0ra.dicodingevent.R
import com.ts0ra.dicodingevent.data.database.preference.SettingPreferences
import com.ts0ra.dicodingevent.data.database.preference.dataStore
import com.ts0ra.dicodingevent.databinding.FragmentSettingBinding
import com.ts0ra.dicodingevent.utils.MyWorker
import com.ts0ra.dicodingevent.ui.viewmodel.ViewModelFactory
import com.ts0ra.dicodingevent.ui.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit

class SettingFragment : PreferenceFragmentCompat() {

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(requireActivity())
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentSettingBinding.inflate(inflater, container, false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = findPreference<SwitchPreferenceCompat>("theme")
        val switchReminder = findPreference<SwitchPreferenceCompat>("reminder")

        viewModel.getThemeSetting().observe(viewLifecycleOwner) {
            switchTheme?.isChecked = it
        }

        viewModel.getReminderSetting().observe(viewLifecycleOwner) {
            switchReminder?.isChecked = it
        }

        switchTheme?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean
            viewModel.saveThemeSetting(isDarkModeEnabled)
            if (isDarkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }

        switchReminder?.setOnPreferenceChangeListener { _, newValue ->
            val isReminderEnabled = newValue as Boolean
            viewModel.saveReminderSetting(isReminderEnabled)
            if (isReminderEnabled) {
                startPeriodicTask()
            } else {
                cancelPeriodicTask()
            }
            true
        }

//        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
//            if (isDarkModeActive) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                binding.switchTheme.isChecked = true
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                binding.switchTheme.isChecked = false
//            }
//        }
//
//        settingViewModel.getReminderSettings().observe(viewLifecycleOwner) { isReminderActive: Boolean ->
//            binding.reminderSwitch.isChecked = isReminderActive
//        }
//
//        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
//            settingViewModel.saveThemeSetting(isChecked)
//        }
//
//        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
//            settingViewModel.saveReminderSetting(isChecked)
//            if (isChecked) {
//                startPeriodicTask()
//            } else {
//                cancelPeriodicTask()
//            }
//        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("daily_reminder")
    }
}