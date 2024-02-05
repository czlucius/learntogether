package com.sp.learntogether;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sp.learntogether.databinding.FragmentScanBookBinding;
import com.sp.learntogether.io.CameraInteractor;
import com.sp.learntogether.objects.ScanWrapper;
import com.sp.learntogether.ui.BookDisplayDialog;

import java.io.IOException;

public class ScanBookFragment extends Fragment {
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    private ScanBookViewModel vm;

    public static ScanBookFragment newInstance() {
        return new ScanBookFragment();
    }
    private FragmentScanBookBinding binding;
    private CameraInteractor cameraInteractor;


    private ActivityResultLauncher<String> permissionRequestLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        if (granted) {
            startCamera();
        } else {
            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), CAMERA_PERMISSION);
            if (showRationale) {
                showPermissionsRationale();
            } else {
                // Permanent denial!
                Snackbar sb = Snackbar.make(binding.getRoot(), R.string.permanent_denial_promt, Snackbar.LENGTH_SHORT);
                sb.setAction(R.string.grant, v -> {
                    launchAppInfo();
                });
            }
        }
    });

    private void launchAppInfo() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri pkg = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(pkg);
        startActivity(intent);
    }

    private static final String TAG = "ScanBookFragment";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBookBinding.inflate(inflater, container, false);
        cameraInteractor = new CameraInteractor(requireContext());

        vm = new ViewModelProvider(this).get(ScanBookViewModel.class);

        vm.getDisplayedBook().observe(getViewLifecycleOwner(), book -> {
            if (book != null) {
                Log.i(TAG, "onCreateView: getDisplayedBook updated with non null value");
                new BookDisplayDialog(requireContext(), book, newBook -> {
                    try {
                        vm.addBook(newBook);
                    } catch (IOException e) {
                        Snackbar.make(requireView(), R.string.error_adding_book, Snackbar.LENGTH_LONG).show();
                    }
                })
                        .show();
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // If the user comes here and do not grant permissions, gracefully degrade by only allowing manual entry.
        reqPm();

        vm.getScanned().observe(getViewLifecycleOwner(), scanWrappers -> {
            for (ScanWrapper scanWrapper: scanWrappers) {
                scanWrapper.display(view, isbn -> {
                    if (isbn != null) {
                        vm.loadBook(isbn);
                    }
                });
            }
        });

        binding.enterIsbn.setOnClickListener(v -> {
            EditText et = new EditText(requireContext());
            et.setHint(R.string.isbn);
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.enter_isbn)
                    .setView(et)
                    .setPositiveButton(R.string.check_info, (di, which) -> {
                        vm.loadBook(et.getText().toString());
                    }).setNegativeButton(R.string.cancel, (di, which) -> {})
                    .show();

        });


    }

    private void reqPm() {
        int granted = ContextCompat.checkSelfPermission(requireContext(), CAMERA_PERMISSION);
        if (granted == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), CAMERA_PERMISSION)) {
            // Show the rationale to the user
            showPermissionsRationale();
        } else {
            requestCamera();
        }
    }

    private CameraInteractor.UseCaseSupplier useCaseSupplier = (executor) -> {
        // Use Case 1: Preview
        Preview preview = new Preview.Builder().build();
        if (binding != null) {
            preview.setSurfaceProvider(binding.scanArea.getSurfaceProvider());
        }

        // Use case 2: Barcode analysis
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executor, vm.analyzer);

        return new UseCase[]{preview, imageAnalysis};
    };

    private void startCamera() {
        // Start the camera as all permissions have been successfully granted
        cameraInteractor.start(useCaseSupplier, getViewLifecycleOwner(), e -> {
            Snackbar.make(requireView(), R.string.failure_to_start_camera, Snackbar.LENGTH_LONG).show();
        });
    }

    private void showPermissionsRationale() {
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.permissions_required)
                .setMessage(R.string.camera_book_rationale)
                .setPositiveButton(R.string.grant, (di, which) -> {
                    requestCamera();
                })
                .setNegativeButton(R.string.cancel, (di, which) -> {
                    // Degrade to manual entry
                })
                .create();
        alertDialog.show();
    }

    private void requestCamera() {
        permissionRequestLauncher.launch(CAMERA_PERMISSION);
    }


}