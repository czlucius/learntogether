package com.sp.learntogether.objects


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sp.learntogether.R
import com.sp.learntogether.astraDBHelper
import com.sp.learntogether.io.DatabaseInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ActionsReceiver() : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val auth = Firebase.auth

    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent?.extras
        val action = data?.getString("action")
        val notificationId = data?.getInt("notificationId")
        Log.i("ActionsReceiver", "onReceive: $action $notificationId $data")
        val dbInteractor = DatabaseInteractor.getInstance(context)

        when (action) {
            "MEETUP_REQUEST"-> {

                // The server will add the friends for us (2-way, mutually)
                context.cancelNotification(notificationId)

                val meetupId = data.getString("meetupId")!!
                scope.launch(Dispatchers.IO) {
                    dbInteractor.addToList(astraDBHelper.meetupsUrl + meetupId, "participantuids", auth.uid!!) { _ ->
                        Toast.makeText(context, R.string.accepted_meetup_request, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "dismiss" -> {
                context.cancelNotification(notificationId)
            }
            else -> {} // No-op
        }
    }

    private fun Context?.cancelNotification(notificationId: Int?) {
        if (notificationId != null && notificationId != 0) {
            val notificationManager = this?.let { NotificationManagerCompat.from(it) }
            notificationManager?.cancel(notificationId)
        }
    }

    companion object {
        const val DENY_ACTION_FRIEND_REQ = "deny_action"
    }
}