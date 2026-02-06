package com.sp.learntogether.ui.ai;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentGenerativeChatBinding;

import org.json.JSONException;

public class GenerativeChatFragment extends Fragment {

    private FragmentGenerativeChatBinding binding;
    private GenerativeChatViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenerativeChatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GenerativeChatViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.sendToAi.setOnClickListener(v -> {
            String req = binding.editText.getText().toString();
            if (req.trim().isEmpty()) {
                return;
            }
            binding.editText.setText("");
            try {
                viewModel.ask(req);
            } catch (JSONException exception) {
                Snackbar.make(view, getString(R.string.parsing_errors), Snackbar.LENGTH_LONG).show();
            }
        });
        GenerativeChatAdapter adapter = new GenerativeChatAdapter(viewModel.getMessages().getValue());
        binding.genChatList.setAdapter(adapter);
        viewModel.getMessages().observe(getViewLifecycleOwner(), adapter::update);
    }
}