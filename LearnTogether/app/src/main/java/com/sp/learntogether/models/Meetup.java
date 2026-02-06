package com.sp.learntogether.models;

import static com.sp.learntogether.Utils.strArrFromJsonArray;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.io.DatabaseInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class Meetup implements Parcelable {
    private String id;
    private String initiatorUid;
    private String[] invitees;
    private String[] participantUids;
    private double latitude;
    private double longitide;
    private long meetTime;
    private String name;
    private int duration;
    private String locationGeocoded;

    protected Meetup(Parcel in) {
        id = in.readString();
        initiatorUid = in.readString();
        invitees = in.createStringArray();
        participantUids = in.createStringArray();
        latitude = in.readDouble();
        longitide = in.readDouble();
        meetTime = in.readLong();
        name = in.readString();
        duration = in.readInt();
        locationGeocoded = in.readString();
    }

    public static final Creator<Meetup> CREATOR = new Creator<Meetup>() {
        @Override
        public Meetup createFromParcel(Parcel in) {
            return new Meetup(in);
        }

        @Override
        public Meetup[] newArray(int size) {
            return new Meetup[size];
        }
    };

    public String[] getInvitees() {
        return invitees;
    }

    public String getInitiatorUid() {
        return initiatorUid;
    }

    public static Meetup fromJsonObject(JSONObject obj) throws JSONException {
        Meetup meetup = new Meetup(
                obj.getString("id"),
                obj.getString("initiatoruid"),
                strArrFromJsonArray(obj.optJSONArray("invitees")),
                strArrFromJsonArray(obj.optJSONArray("participantuids")),
                obj.getDouble("latitude"),
                obj.getDouble("longitude"),
                obj.getLong("meet_time"),
                obj.getString("name"),
                obj.getInt("duration"),
                obj.optString("locationgeocoded")
        );
        return meetup;
    }

    public String getId() {
        return id;
    }

    public Meetup(String id, String initiatorUid, String[] invitees, String[] participantUids, double latitude, double longitide, long meetTime, String name, int duration, String locationGeocoded) {
        this.id = id;
        this.initiatorUid = initiatorUid;
        this.invitees = invitees;
        this.participantUids = participantUids;
        this.latitude = latitude;
        this.longitide = longitide;
        this.meetTime = meetTime;
        this.name = name;
        this.duration = duration;
        this.locationGeocoded = locationGeocoded;
    }

    public String getLocationGeocoded() {
        return locationGeocoded;
    }

    public void setLocationGeocoded(String locationGeocoded) {
        this.locationGeocoded = locationGeocoded;
    }



    public int getDuration() {
        return duration;
    }

    public String[] getParticipantUids() {
        return participantUids;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitide() {
        return longitide;
    }

    public long getMeetTime() {
        return meetTime;
    }
    public String getFormattedDateTime() {
        DateFormat df = SimpleDateFormat.getDateTimeInstance();
        return df.format(new Date(meetTime));
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meetup meetup = (Meetup) o;
        return Double.compare(meetup.latitude, latitude) == 0 && Double.compare(meetup.longitide, longitide) == 0 && meetTime == meetup.meetTime && id.equals(meetup.id) && initiatorUid.equals(meetup.initiatorUid) && Arrays.equals(invitees, meetup.invitees) && Arrays.equals(participantUids, meetup.participantUids) && name.equals(meetup.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, initiatorUid, latitude, longitide, meetTime, name);
        result = 31 * result + Arrays.hashCode(invitees);
        result = 31 * result + Arrays.hashCode(participantUids);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(initiatorUid);
        dest.writeStringArray(invitees);
        dest.writeStringArray(participantUids);
        dest.writeDouble(latitude);
        dest.writeDouble(longitide);
        dest.writeLong(meetTime);
        dest.writeString(name);
        dest.writeInt(duration);
        dest.writeString(locationGeocoded);
    }
}
