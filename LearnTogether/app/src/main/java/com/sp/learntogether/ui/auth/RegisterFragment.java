package com.sp.learntogether.ui.auth;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel vm;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        if (uri == null) return;
        vm.setImageUri(uri);
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this)
                .get(RegisterViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm.getImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri == null) {
                binding.profilePicRegister.setImageResource(R.drawable.baseline_add_24);
            } else {
                binding.profilePicRegister.setImageURI(uri);
                // TODO face detection
            }
        });




        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build();
        binding.profilePicRegister.setOnClickListener(v -> {
            pickMediaLauncher.launch(request);
        });
        binding.profilePicRegister.setOnLongClickListener(v -> {
            if (vm.getImageUri().getValue() != null) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.remove_profile_picture)
                        .setMessage(R.string.clear_pfp_notice)
                        .setPositiveButton(R.string.confirm, (di, which) -> {
                            vm.setImageUri(null);
                        })
                        .setNegativeButton(R.string.cancel, (di, which) -> { /* no-op */ });
                alertDialogBuilder.show();
            }
            return true;
        });




    }
}