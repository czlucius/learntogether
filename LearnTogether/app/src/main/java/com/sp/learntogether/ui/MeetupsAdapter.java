package com.sp.learntogether.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sp.learntogether.R;
import com.sp.learntogether.Utils;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.data.MeetupDiffCallback;
import com.sp.learntogether.databinding.SingleMeetupBinding;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.kotlin.MeetupKt;
import com.sp.learntogether.models.Meetup;
import com.sp.learntogether.models.Profile;
import com.sp.learntogether.objects.Location;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class MeetupsAdapter extends RecyclerView.Adapter<MeetupsAdapter.VH> {

    private List<String> meetups;
    private Consumer<Meetup> onLocate;

    public MeetupsAdapter(List<String> meetups, Consumer<Meetup> onLocate) {
        this.meetups = new ArrayList<>(meetups);
        this.onLocate = onLocate;
    }

    private DatabaseInteractor dbIO;

    FirebaseDatabase database = FirebaseDatabase.getInstance(Utils.FIREBASE_RTDB_URL);
    DatabaseReference myRef = database.getReference("meetups");


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        SingleMeetupBinding binding = SingleMeetupBinding.inflate(inflater, parent, false);
        VH vh = new VH(binding);
        if (dbIO == null) {
            dbIO = DatabaseInteractor.getInstance(context);
        }
        return vh;
    }

    private static final String TAG = "MeetupsAdapter";

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Context context = holder.itemView.getContext();
        SingleMeetupBinding binding = holder.binding;
        String meetupId = meetups.get(position);
        dbIO.getRow(response -> {
            Log.i(TAG, "onBindViewHolder: " + response);
            Meetup meetup;
            try {
                meetup = Meetup.fromJsonObject(response.getJSONArray("data").getJSONObject(0));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            binding.meetupLocate.setVisibility(View.VISIBLE);
            binding.meetupLocate.setOnClickListener(v -> {
                Log.i(TAG, "onBindViewHolder: locate clicked");
                onLocate.accept(meetup);
            });
            binding.setName(meetup.getName());
            binding.setDateTime(meetup.getFormattedDateTime());
            binding.setDuration(meetup.getDuration());
            String loc;
            if (meetup.getLocationGeocoded() != null) {
                loc = meetup.getLocationGeocoded();
            } else {
                loc = context.getString(R.string.lat_lng_template, meetup.getLatitude(), meetup.getLongitide());
            }
            binding.setLocation(loc);

            binding.meetupShare.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String text = context.getString(R.string.meetup_share_template, loc, meetup.getFormattedDateTime());

                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.setType("text/*");
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_upcoming_meetup)));
            });

            binding.meetupNavigate.setOnClickListener(v -> {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", meetup.getLatitude(), meetup.getLongitide());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            });


            binding.notifyChange();
            dbIO.getRow(userResponse -> {
                Log.i(TAG, "onBindViewHolder: JSON get user " + userResponse);
                int count;

                try {
                    count = userResponse.getInt("count");
                    if (count > 0) {
                        binding.peopleIcon.setVisibility(View.VISIBLE);
                        binding.meetupPeople.setVisibility(View.VISIBLE);
                        binding.meetupPeople.setText(userResponse.getJSONArray("data").getJSONObject(0).getString("username"));
                    } else {
                        binding.peopleIcon.setVisibility(View.GONE);
                        binding.meetupPeople.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }, astraDBHelper.userDetailsUrl, meetup.getInitiatorUid() + "?fields=username");
        }, astraDBHelper.meetupsUrl, meetupId);

    }

    public void updateLibrary(List<String> newMeetups) {
        MeetupDiffCallback callback = new MeetupDiffCallback(meetups, newMeetups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        meetups = newMeetups;
        result.dispatchUpdatesTo(this);
    }


    public void add(Meetup meetup) {
        meetups.add(meetup.getId());
        notifyItemInserted(meetups.size());
    }


    @Override
    public int getItemCount() {
        return meetups.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final SingleMeetupBinding binding;

        public VH(@NonNull SingleMeetupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
