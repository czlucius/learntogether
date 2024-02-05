package com.sp.learntogether.ui.auth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentRegisterBinding;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel vm;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
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
        NavController navController = NavHostFragment.findNavController(this);
        vm.getImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri == null) {
                binding.profilePicRegister.setImageResource(R.drawable.baseline_add_24);
            } else {
                binding.profilePicRegister.setImageURI(uri);
            }
        });

        vm.getDetectedFaces().observe(getViewLifecycleOwner(), faces -> {
            if (faces == null) {
                binding.hasFaceIndicator.setVisibility(View.GONE);
                return;
            }
            binding.hasFaceIndicator.setVisibility(View.VISIBLE);
            if (faces.isEmpty()) {
                binding.hasFaceIndicator.setText(R.string.no_face);
                binding.hasFaceIndicator.setBackgroundColor(getResources().getColor(R.color.md_theme_light_errorContainer, requireContext().getTheme()));
                Snackbar.make(view, R.string.add_face_notice, Snackbar.LENGTH_LONG).show();
            } else {
                binding.hasFaceIndicator.setBackgroundColor(getResources().getColor(R.color.success_green_background, requireContext().getTheme()));
                binding.hasFaceIndicator.setText(R.string.face_detected);
            }
        });

        vm.getStatus().observe(getViewLifecycleOwner(), status -> {
            if (status == null) {
                return;
            }
            if (status) {
                Snackbar.make(view, "Registration successful! Please check your email for a verification link.", Snackbar.LENGTH_LONG).show();

                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            } else {
                Snackbar.make(view, "Registration unsuccessful!", Snackbar.LENGTH_LONG).show();
                vm.clearStatus();
            }
        });

        binding.signupBtnRegister.setOnClickListener(v -> {
            String username = binding.usernameInputRegister.getEditText().getText().toString();
            String password = binding.passwordInputRegister.getEditText().getText().toString();
            String email = binding.emailInputRegister.getEditText().getText().toString();
            String name = binding.nameInputRegister.getEditText().getText().toString();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || vm.getImageUri().getValue() == null) {
                Snackbar.make(view, "Please fill up all fields & attach image", Snackbar.LENGTH_SHORT).show();
                return;
            }


            vm.signup(username, email, password, name);
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