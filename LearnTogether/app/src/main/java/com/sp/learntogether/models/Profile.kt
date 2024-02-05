package com.sp.learntogether.models

import android.os.Parcelable
import com.sp.learntogether.Utils
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class Profile(
    val username: String,
    val email: String,
    val isHasFace: Boolean,
    val name: String,
    val profilePicUrl: String?,
    var uid: String,
    val friends: Array<String>,
    val phone: String?,
    var fcmToken: String?
) : Parcelable {
    @Throws(JSONException::class)
    fun toJsonObject(): JSONObject {
        val obj = JSONObject()
        obj.put("username", username)
        obj.put("email", email)
        obj.put("hasface", isHasFace)
        obj.put("name", name)
        obj.put("profilepicurl", profilePicUrl)
        obj.put("uid", uid)
        obj.put("friends", JSONArray(friends))
        obj.put("phone", phone)
        obj.put("fcmtoken", fcmToken)
        return obj
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJsonObject(obj: JSONObject): Profile {
            val p = Profile(
                obj.getString("username"),
                obj.getString("email"),
                obj.getBoolean("hasface"),
                obj.getString("name"),
                obj.optString("profilepicurl"),
                obj.getString("uid"),
                Utils.strArrFromJsonArray(obj.getJSONArray("friends")),
                obj.optString("phone"),
                obj.optString("fcmtoken")

            )
            return p;
        }
    }

    override fun toString(): String {
        return username
    }
}