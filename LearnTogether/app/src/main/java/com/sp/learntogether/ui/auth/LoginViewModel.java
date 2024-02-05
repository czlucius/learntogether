package com.sp.learntogether.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


enum LoginState {
    LOGGED_IN, NOT_VERIFIED, VERIFY_SENT, NETWORK_ERROR, AUTH_FAILED
}
public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginState> state = new MutableLiveData<>(null);
    private final FirebaseAuth auth;

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<LoginState> getState() {
        return state;
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            state.setValue(LoginState.LOGGED_IN);
                        } else {
                            state.setValue(LoginState.NOT_VERIFIED);
                        }
                    } else {
                        if (task.getException() != null) {task.getException().printStackTrace();}
                        state.setValue(LoginState.AUTH_FAILED);
                    }
                });
    }

    public void sendVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return; // no-op
        }
        user.sendEmailVerification().addOnSuccessListener(command -> {
            state.setValue(LoginState.VERIFY_SENT);
        });
    }

    public void resetLoginState() {
        state.setValue(null);
    }
}
