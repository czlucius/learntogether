package com.sp.learntogether.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sp.learntogether.App;
import com.sp.learntogether.MeetupsViewModel;
import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentMeetupsBinding;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Meetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MeetupsFragment extends Fragment {

    private MeetupsViewModel vm;
    private FragmentMeetupsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMeetupsBinding.inflate(inflater, container, false);
        dbIO = DatabaseInteractor.getInstance(requireContext());
        vm = new ViewModelProvider(this).get(MeetupsViewModel.class);

        return binding.getRoot();

    }

    private DatabaseInteractor dbIO;
    private MeetupsAdapter adapter;


    private static final String TAG = "MeetupsFragment";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController nc = NavHostFragment.findNavController(this);
        adapter = new MeetupsAdapter(new ArrayList<>(), meetupLocated -> {
            Log.i(TAG, "onViewCreated: Locate callback called 1");

            vm.getMeetupInitiatorLocation(meetupLocated.getId(), location -> {
                Log.i(TAG, "onViewCreated: Locate callback called");
                getActivity().runOnUiThread(() -> {

                    nc.navigate(MeetupsFragmentDirections.actionMeetupsFragmentToMeetupMapsFragment(
                            location, meetupLocated
                    ));
                });
            });
        });
        binding.meetupsList.setAdapter(adapter);
        vm.getMeetups().observe(getViewLifecycleOwner(), meetups -> {
            adapter.updateLibrary(meetups);
        });
//        binding.meetupsList.setAdapter();
        Consumer<Meetup> refreshCallback = m -> {
            adapter.add(m);
            vm.addMeetupToTrackingPrefs(m);
        };
        binding.scheduleMeetup.setOnClickListener(v -> {
            MeetupsDialog dialog = new MeetupsDialog(refreshCallback);
            dialog.show(getChildFragmentManager(), null);
        });

    }
}