package com.sp.learntogether.objects;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.function.Consumer;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "BarcodeAnalyzer";
    private Consumer<List<Barcode>> barcodeConsumer;

    public BarcodeAnalyzer(Consumer<List<Barcode>> barcodeConsumer) {
        this.barcodeConsumer = barcodeConsumer;
    }

    private BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                    Barcode.FORMAT_ALL_FORMATS
            )
            .build();

    private BarcodeScanner scanner = BarcodeScanning.getClient(options);
    @OptIn(markerClass = ExperimentalGetImage.class) @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
//        Log.i(TAG, "analyze" + image);
        if (image != null) {
            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());
            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        barcodeConsumer.accept(barcodes);
                        imageProxy.close();
                    });
        }

    }
}
