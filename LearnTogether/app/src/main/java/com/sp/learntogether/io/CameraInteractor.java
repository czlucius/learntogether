package com.sp.learntogether.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Facade pattern for accessing the camera.
 */
public class CameraInteractor {
    private WeakReference<Context> wctx;
    private Camera camera;
    public CameraInteractor(Context context) {
        this.wctx = new WeakReference<>(context);
    }

    public Function<Void, Void> start(UseCaseSupplier useCaseSupplier, LifecycleOwner lifecycleOwner, FailureCallback failureCallback) {
        if (wctx.get() == null) {
            throw new IllegalStateException("Context has been cleared from weak reference!");
        }
        Context ctx = wctx.get();
        Executor executor = ContextCompat.getMainExecutor(ctx);
        ListenableFuture<ProcessCameraProvider> cpf = ProcessCameraProvider.getInstance(ctx);
        UseCase[] useCases = useCaseSupplier.getUseCases(executor);
        cpf.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cpf.get();
                if (cameraProvider == null) {
                    failureCallback.onFail(new NullPointerException("Camera Provider not available."));
                    return;
                }
                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, selector, useCases[0], useCases[1]
                );
                Log.i("CameraInteractor", "init complete");

                this.camera = camera;
//                ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//                    @Override
//                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
//                        return super.onScale(detector);
//                    }
//                };
//                ScaleGestureDetector sgd = new ScaleGestureDetector(ctx, scaleGestureListener)
//                        ;
//

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                failureCallback.onFail(e);
            }

        }, executor);

        return (nothing) -> {
            ProcessCameraProvider pcp;
            try {
                pcp = cpf.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null; // the ProcessCameraProvider cannot be obtained, hence we just skip the shutdown process
            }
            pcp.unbindAll();
            return null;
        };

    }

    @FunctionalInterface
    public interface FailureCallback {
        void onFail(Exception e);
    }

    @FunctionalInterface
    public interface UseCaseSupplier {
        UseCase[] getUseCases(Executor executor);
    }
}
