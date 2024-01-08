package com.sp.learntogether.ui.communities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sp.learntogether.databinding.FragmentCommunitiesBinding;

public class CommunitiesFragment extends Fragment {

    private FragmentCommunitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CommunitiesViewModel communitiesViewModel =
                new ViewModelProvider(this).get(CommunitiesViewModel.class);

        binding = FragmentCommunitiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        communitiesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}