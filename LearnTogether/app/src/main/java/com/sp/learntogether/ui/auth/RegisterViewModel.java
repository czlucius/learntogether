package com.sp.learntogether.ui.auth;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.sp.learntogether.data.RegistrationHelper;
import com.sp.learntogether.models.Profile;
import com.sp.learntogether.objects.RegistrationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<Uri> imageUri = new MutableLiveData<>(null);
    private MutableLiveData<List<Face>> detectedFaces = new MutableLiveData<>(null);

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private MutableLiveData<Boolean> status = new MutableLiveData<>(null);

    private FaceDetectorOptions detectorOptions = new FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build();
    private FaceDetector detector = FaceDetection.getClient(detectorOptions);

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    /**
     * Gets detected faces as LiveData.
     * @return a list of faces.
     * - null: do not show relevant UI
     * - empty list: no faces
     * - list of faces: faces detected
     */
    public LiveData<List<Face>> getDetectedFaces() {
        return detectedFaces;
    }

    public LiveData<Boolean> getStatus() {
        return status;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri.setValue(imageUri);
        try {
            analyze(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            detectedFaces.setValue(null);
        }
    }

    public void clearStatus() {
        status.setValue(null);
    }


    public void analyze(Uri imageUri) throws IOException {
        if (imageUri == null) {
            return;
        }
        InputImage inputImage = InputImage.fromFilePath(getApplication(), imageUri);
        Task<List<Face>> result = detector.process(inputImage)
                .addOnSuccessListener(faces -> {
                     detectedFaces.setValue(faces);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    detectedFaces.setValue(null);
                });
    }


    private RegistrationHelper helper = new RegistrationHelper();

    public void signup(String username, String email, String password, String name) {
        boolean hasFace = detectedFaces.getValue() != null && detectedFaces.getValue().size() > 0;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = Objects.requireNonNull(authResult.getUser());
                    user.sendEmailVerification(); // Send email verification
                    Profile profile = new Profile(
                            username,
                            email,
                            hasFace,
                            name,
                            null,
                            Objects.requireNonNull(authResult.getUser()).getUid(),
                            new String[]{}, null,
                            null
                    );



                    FirebaseMessaging.getInstance().getToken()
                        .addOnSuccessListener(fcmToken -> {
                            profile.setFcmToken(fcmToken);
                            user.getIdToken(false).addOnSuccessListener(idToken -> {
                                try {
                                helper.signup(
                                        getApplication(), profile, imageUri.getValue(), _unused -> {
                                            status.setValue(true);
                                            return null;
                                        }, fcmToken, idToken.getToken()
                                );

                                } catch (RegistrationException e) {
                                    status.setValue(false);
                                }
                            });
                        });



                });

    }


}
