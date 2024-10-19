package com.ts0ra.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ts0ra.dicodingevent.R
import com.ts0ra.dicodingevent.data.database.remote.retrofit.ApiConfig
import com.ts0ra.dicodingevent.data.di.Injection
import com.ts0ra.dicodingevent.data.repository.EventRepository
import com.ts0ra.dicodingevent.ui.activity.DetailActivity
import com.ts0ra.dicodingevent.ui.activity.DetailActivity.Companion.EVENT_ID
import com.ts0ra.dicodingevent.utils.Result as Res

class MyWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val eventRepository: EventRepository by lazy {
        Injection.provideEventRepository(context)
    }

    override suspend fun doWork(): Result {
        Log.e(TAG, "Work is executed")
        return getLatestEvent()
    }

    private suspend fun getLatestEvent(): Result {
        return try {
            val result = eventRepository.getLastUpcomingEvent(1, null, 1)
            if (result is Res.Success) {
                val eventId = result.data.id
                val title = result.data.name
                val eventTime = "Event will begin at ${result.data.beginTime}"
                showNotification(eventId, title, eventTime)
                Result.success()
            } else {
                Log.e(TAG, "Failed to fetch events")
                Result.failure()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

//    private fun getLatestEvent(): Result {
//        val client = ApiConfig.getApiService().getLatestEvent()
//        client.enqueue(object : Callback<EventResponse> {
//            override fun onResponse(
//                call: Call<EventResponse>,
//                response: Response<EventResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        Log.e(TAG, "onSuccess: ${response.message()}")
//                        val eventId = responseBody.listEvents.first().id
//                        val title = responseBody.listEvents.first().name
//                        val eventTime = responseBody.listEvents.first().beginTime
//                        showNotification(eventId, title, eventTime)
//                    }
//                    resultStatus = Result.success()
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                    resultStatus = Result.failure()
//                }
//            }
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e(TAG, "onFailure: ${t.message}")
//                resultStatus = Result.failure()
//            }
//        })
//        return resultStatus as Result
//    }

//    private fun getLatestEvent(): Result {
//        return try {
//            // This is a synchronous network request
//            val response = ApiConfig.getApiService().getLatestEvent().execute()
//            if (response.isSuccessful) {
//                val responseBody = response.body()
//                if (responseBody != null) {
//                    Log.e(TAG, "onSuccess: ${response.message()}")
//                    val eventId = responseBody.listEvents.first().id
//                    val title = responseBody.listEvents.first().name
//                    val eventTime = "Event will begin at ${responseBody.listEvents.first().beginTime}"
//                    showNotification(eventId, title, eventTime)
//                }
//                Result.success()
//            } else {
//                Log.e(TAG, "onFailure: ${response.message()}")
//                Result.failure()
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "onFailure: ${e.message}")
//            Result.failure()
//        }
//    }

    private fun showNotification(eventId: Int, title: String, eventTime: String) {
        val notifDetailIntent = Intent(applicationContext, DetailActivity::class.java)
        notifDetailIntent.putExtra(EVENT_ID, eventId)

        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(notifDetailIntent)
            getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText(eventTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object{
        private const val TAG = "MyWorker"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "event channel"
    }
}