package com.sp.learntogether.ui.auth;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<Uri> imageUri = new MutableLiveData<>(null);
    private MutableLiveData<List<Face>> detectedFaces = new MutableLiveData<>(null);

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

    public void setImageUri(Uri imageUri) {
        this.imageUri.setValue(imageUri);
        try {
            analyze(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            detectedFaces.setValue(null);
        }
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



}
