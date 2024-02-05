package com.sp.learntogether.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentLoginBinding;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth auth;
    private NavController nc;

    private LoginViewModel vm;



    private void navigateHome() {
        nc.navigate(R.id.action_loginFragment_to_navigation_home);

    }
    private void navigateSignUp() {
        nc.navigate(R.id.action_loginFragment_to_registerFragment);
    }


    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        nc = NavHostFragment.findNavController(this);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            navigateHome();
            return null;
        }
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(LoginViewModel.class);
        return binding.getRoot();
    }

    private void notVerifiedSnackbar() {
        Snackbar.make(requireView(), R.string.email_not_verified, Snackbar.LENGTH_LONG)
                .setAction(R.string.verify, v1 -> {
                    vm.sendVerification();
                })
                .show();
    }

    // not verified, auth failure, network failure, success,
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm.getState().observe(getViewLifecycleOwner(), loginState -> {
            if (loginState == null) {
                return;
            }
            switch (loginState) {
                case LOGGED_IN:
                    navigateHome();
                    break;
                case NOT_VERIFIED:
                    notVerifiedSnackbar();
                    break;
                case VERIFY_SENT:
                    Snackbar.make(requireView(), R.string.verification_email_sent, Snackbar.LENGTH_LONG).show();
                    break;
                case NETWORK_ERROR:
                    Snackbar.make(requireView(), R.string.network_error, Snackbar.LENGTH_LONG).show();
                    break;
                case AUTH_FAILED:
                    Snackbar.make(requireView(), R.string.auth_failure, Snackbar.LENGTH_LONG).show();
                    break;
            }
            vm.resetLoginState();
        });


        binding.newUserBtn.setOnClickListener(v -> {
            navigateSignUp();
        });
        binding.signInBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailField.getEditText()).getText().toString();
            String password = Objects.requireNonNull(binding.passwordField.getEditText()).getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(view, R.string.email_password_cannot_be_empty, Snackbar.LENGTH_SHORT).show();
                return;
            }
            vm.login(email, password);
        });
    }
}