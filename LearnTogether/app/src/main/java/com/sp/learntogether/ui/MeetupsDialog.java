package com.sp.learntogether.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sp.learntogether.R;
import com.sp.learntogether.Utils;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.databinding.DialogMeetupBinding;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.kotlin.EditTextUtilsKt;
import com.sp.learntogether.models.Meetup;
import com.sp.learntogether.models.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MeetupsDialog extends BottomSheetDialogFragment {
    private static final String TAG = "MeetupsDialog";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final ArrayList<String> participantUids = new ArrayList<>();


    private DialogMeetupBinding binding;
    private Consumer<Meetup> createCallback;

    public MeetupsDialog(Consumer<Meetup> createCallback) {
        super(R.layout.dialog_meetup);
        this.createCallback = createCallback;
    }

    private String getText(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString();
    }

    private boolean isEmpty(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString().isEmpty();
    }


    private Place place = null;



    private Set<Profile> added = new HashSet<>() {
        @Override
        public boolean add(Profile profile) {
            boolean b = super.add(profile);
            if (b) {
                binding.recyclerView.setText(binding.recyclerView.getText() + ", " + profile.getUsername());
            }
            return  b;
        }

    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DialogMeetupBinding.bind(view);
        DatabaseInteractor dbInteract = DatabaseInteractor.getInstance(requireContext());


        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), "AIzaSyAn0xSyeQLz7E9x3xWDKDl8UCe-3UM9MQU");
        }


        EditTextUtilsKt.transformIntoDatePicker(binding.meetupInputDate, null);
        EditTextUtilsKt.transformIntoTimePicker(binding.meetupInputTime);


        AutocompleteSupportFragment autocompletePlaces =
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.meetup_place_input);
        ArrayList<Place.Field> fields = new ArrayList<>();
        fields.add(Place.Field.ID);
        fields.add(Place.Field.NAME);
        fields.add(Place.Field.LAT_LNG);
        fields.add(Place.Field.ADDRESS);

        autocompletePlaces.setPlaceFields(fields);
        autocompletePlaces.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(Status status) {
                Log.e(TAG, "onError: An error occurred: $status");
            }

            @Override
            public void onPlaceSelected(Place place) {
                MeetupsDialog.this.place = place;
            }
        });


        binding.getRoot().findViewById(R.id.meetup_place_input);
        ArrayAdapter<Profile> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        AtomicReference<Profile> mine = new AtomicReference<>();
        dbInteract.getRow(response -> {
            String[] friendUids;
            try {
                mine.set(Profile.Companion.fromJsonObject(response.getJSONArray("data").getJSONObject(0)));
                friendUids = Utils.strArrFromJsonArray(response.getJSONArray("data").getJSONObject(0).getJSONArray("friends"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for (String friendUid : friendUids) {
                dbInteract.getRow(response2 -> {
                    try {
                        aa.add(Profile.Companion.fromJsonObject(response2.getJSONArray("data").getJSONObject(0)));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, astraDBHelper.userDetailsUrl, friendUid);
            }
        }, astraDBHelper.userDetailsUrl, auth.getUid());

        binding.imageButton.setOnClickListener(v -> {
            Spinner spinner = new Spinner(requireContext());
            spinner.setAdapter(aa);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select friend")
                    .setView(spinner)
                    .setPositiveButton(R.string.add, (dialog, which) -> {
                        Profile fw = (Profile) spinner.getSelectedItem();
                        added.add(fw);
                    })
                    .setNegativeButton(R.string.cancel, (di, which) -> {
                    })
                    .show();
        });


        binding.scheduleConfirm.setOnClickListener(v -> {
            String name = getText(binding.meetupInputName);
            if (name.isEmpty() || isEmpty(binding.meetupInputDate) || isEmpty(binding.meetupInputTime) || isEmpty(binding.meetupInputDuration) || place == null) {
                Snackbar.make(binding.getRoot(), R.string.please_fill_in_all_fields, Snackbar.LENGTH_SHORT).show();
                return;
            }
            // guaranteed that cast will succeed since transformIntoDatePicker is called
            Calendar calendar = (Calendar) binding.meetupInputDate.getTag();
            Calendar time = (Calendar) binding.meetupInputTime.getTag();
            calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", UUID.randomUUID().toString());
                JSONArray participants = new JSONArray(participantUids);
                obj.put("invitees", participants);
                JSONArray a = new JSONArray();
                a.put(auth.getUid());
                for (Profile pf : added) {
                    a.put(pf.getUid());
                }
                obj.put("participantuids", a);
                obj.put("meet_time", calendar.getTimeInMillis());
                obj.put("name", name);
                obj.put("duration", binding.meetupInputDuration.getEditText().getText().toString());
                obj.put("initiatoruid", auth.getUid());
                obj.put("latitude", place.getLatLng().latitude);
                obj.put("longitude", place.getLatLng().longitude);

                Geocoder geocoder = new Geocoder(requireContext());

                try {
                    // Blocking call
                    List<Address> address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                    if (address != null && address.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        Address myAddress = address.get(0);
                        for (int i = 0; i <= myAddress.getMaxAddressLineIndex(); i++) {
                            sb.append(myAddress.getAddressLine(i));
                        }
                        obj.put("locationgeocoded", sb.toString());
                    }
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), "JSON exception!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "onViewCreated: JSON to be sent to AstraDB is " + obj);
            dbInteract.post(obj, astraDBHelper.meetupsUrl, response -> {
                try {
                    Meetup meetup = Meetup.fromJsonObject(obj);
                    dbInteract.addToList(astraDBHelper.userDetailsUrl + auth.getUid(), "meetups", meetup.getId(), response1 -> {
                        createCallback.accept(meetup);
                        Toast.makeText(getContext(), "Meetup successfully created.", Toast.LENGTH_SHORT).show();

                        dismiss();
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }, err -> {
                err.printStackTrace();
                Log.e(TAG, "onViewCreated: " + Arrays.toString(err.networkResponse.data));
            });
        });

    }

    private FirebaseMessaging messaging = FirebaseMessaging.getInstance();
}
