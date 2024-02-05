package com.sp.learntogether.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentLibraryBinding;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding binding;
    private NavController navController;
    private LibraryViewModel vm;
    private final LibraryAdapter adapter = new LibraryAdapter(new ArrayList<>());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        vm = new ViewModelProvider(this).get(LibraryViewModel.class);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.scanBook.setOnClickListener(v -> {
            navController.navigate(R.id.action_libraryFragment_to_scanBookFragment);
        });
        binding.booksList.setAdapter(adapter);

        vm.allBooksLive().observe(getViewLifecycleOwner(), adapter::updateLibrary);


    }
}