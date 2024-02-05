package com.sp.learntogether.kotlin

import android.content.Context
import com.sp.learntogether.astraDBHelper
import com.sp.learntogether.io.DatabaseInteractor
import com.sp.learntogether.models.Meetup
import com.sp.learntogether.models.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.function.Consumer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun getProfilesGivenUids(uids: Array<String>, dbIO: DatabaseInteractor, afterwards: (List<Profile>) -> Unit){
    CoroutineScope(Dispatchers.IO).launch {
        val profiles = arrayListOf<Profile>()
        for (uid in uids) {
            val p = suspendCoroutine<Profile> {continuation ->
                dbIO.getRow({response: JSONObject ->
                    continuation.resume(Profile.fromJsonObject(response.getJSONArray("data").getJSONObject(0)))
                }, astraDBHelper.userDetailsUrl, uid)

            }
            profiles.add(p)
        }
        afterwards(profiles)
    }

}


fun getUsernames(profiles: List<Profile>): List<String> {
    return profiles.map {
        it.username
    }
}