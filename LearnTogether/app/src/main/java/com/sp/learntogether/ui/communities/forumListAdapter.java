package com.sp.learntogether.ui.communities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.learntogether.R;

import java.util.List;

public class forumListAdapter extends RecyclerView.Adapter<forumListAdapter.forumViewHolder> {

    Context context;
    List<forumPostInfo> forumList;
    recycler_interface recycInt;
    public forumListAdapter(Context context, List<forumPostInfo> forumList, recycler_interface recycInt) {
        this.context = context;
        this.forumList = forumList;
        this.recycInt = recycInt;
    }

    @NonNull
    @Override
    public forumListAdapter.forumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forumitem, parent, false);
        return new forumListAdapter.forumViewHolder(view, recycInt);
    }

    @Override
    public void onBindViewHolder(@NonNull forumViewHolder holder, int position){
        holder.name.setText(forumList.get(position).getName());
        holder.forumQuestion.setText(forumList.get(position).getForumQuestion());
        holder.forumDateTime.setText(forumList.get(position).getCurrentDateTime());
    }

    @Override
    public int getItemCount() {
        return forumList.size();
    }

    public class forumViewHolder extends RecyclerView.ViewHolder {
        TextView name, uid, forumQuestion, forumDateTime;
        ImageView profileImage;
        public forumViewHolder(@NonNull View itemView, recycler_interface recycInt) {
            super(itemView);
            name = itemView.findViewById(R.id.postUser);
            forumDateTime = itemView.findViewById(R.id.postDateTime);
            profileImage = itemView.findViewById(R.id.postPic);
            forumQuestion = itemView.findViewById(R.id.postDesc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recycInt!= null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recycInt.OnItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
