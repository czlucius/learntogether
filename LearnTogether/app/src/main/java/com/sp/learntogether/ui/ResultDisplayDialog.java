package com.sp.learntogether.ui;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.sp.learntogether.databinding.ResultsPageBinding;

import java.text.DateFormat;

public class ResultDisplayDialog extends BottomSheetDialog {
    private ResultsPageBinding binding;
    private Barcode code;

    public ResultDisplayDialog(@NonNull Context context, Barcode code) {
        super(context);
        this.code = code;
        initialize();
        populateLayout();
    }

    private void initialize() {
        setCancelable(true);
        binding = ResultsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    protected void onStart() {
        super.onStart();
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void populateLayout() {
        new Thread(() -> {

            
        }).start();
//        binding.bookDetails.loadUrl();
//        DateFormat dateFormat = DateFormat.getDateTimeInstance();
//        binding.dateContents.setText(dateFormat.format(code.getTimeScanned()));
//        binding.typeContents.setText(code.getDataType().getTypeName());
//        binding.formatContents.setText(code.getFormatName(getContext()));
//        binding.codeContentsText.setText(code.getData().getStringRepresentation());
//
//
//        binding.actionsGroup.removeAllViews();
//        Chip chip;
//        for (Action obj : code.getDataType().getActions()) {
//            chip = (Chip) getLayoutInflater()
//                    .inflate(R.layout.template_chip, binding.actionsGroup, false);
//            if (obj.getActionIcon() != null) {
//                chip.setChipIconResource(obj.getActionIcon());
//            }
//            chip.setText(obj.getActionText());
//            chip.setOnClickListener((v) -> {
//                obj.performAction(getContext(), code.getData());
//            });
//            binding.actionsGroup.addView(chip);
//        }

    }
}
