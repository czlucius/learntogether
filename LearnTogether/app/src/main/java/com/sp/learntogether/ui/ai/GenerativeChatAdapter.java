package com.sp.learntogether.ui.ai;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.learntogether.databinding.SingleChatBinding;
import com.sp.learntogether.models.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class GenerativeChatAdapter extends RecyclerView.Adapter<GenerativeChatAdapter.VH> {
    private static final String TAG = "GenerativeChatAdapter";
    private ArrayList<Message> messages;
    public GenerativeChatAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void update(ArrayList<Message> messages) {
        Log.i(TAG, "update: Messages changed" + messages.toString());
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        SingleChatBinding binding = SingleChatBinding.inflate(inflater, parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Context context = holder.itemView.getContext();
        Message message = messages.get(position);
        Log.i(TAG, "onBindViewHolder: Message @ " + position + " with desc " + message.getDescription());
        holder.binding.setMessage(message.getDescription());
        holder.binding.setFrom(message.getSenderName(context));
        holder.binding.profileAiChat.setImageURI(message.getImageLocation());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        SingleChatBinding binding;
        public VH(SingleChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
