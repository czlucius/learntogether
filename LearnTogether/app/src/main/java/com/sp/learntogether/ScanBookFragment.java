package com.sp.learntogether;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sp.learntogether.databinding.FragmentScanBookBinding;

public class ScanBookFragment extends Fragment {

    private ScanBookViewModel mViewModel;

    public static ScanBookFragment newInstance() {
        return new ScanBookFragment();
    }

    private FragmentScanBookBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // If the user comes here and do not grant permissions, gracefully degrade by only allowing manual entry.
    }
}