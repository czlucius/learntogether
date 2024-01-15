package com.sp.learntogether.ui.communities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentCommunitiesBinding;
import android.widget.GridView;

import java.util.ArrayList;

public class CommunitiesFragment extends Fragment {
    GridView communityGV;
    private FragmentCommunitiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CommunitiesViewModel communitiesViewModel =
                new ViewModelProvider(this).get(CommunitiesViewModel.class);

        binding = FragmentCommunitiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        communityGV = binding.Communities;
        ArrayList<Communities> communitiesModelArrayList = new ArrayList<Communities>();
        communitiesModelArrayList.add(new Communities("Mathematics", R.drawable.math));
        communitiesModelArrayList.add(new Communities("English", R.drawable.english));
        communitiesModelArrayList.add(new Communities("Science", R.drawable.science));
        communitiesGVadapter adapter = new communitiesGVadapter(getContext(), communitiesModelArrayList);
        communityGV.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}