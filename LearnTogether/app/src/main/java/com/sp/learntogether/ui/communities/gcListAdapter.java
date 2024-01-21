package com.sp.learntogether.ui.communities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.learntogether.R;

import java.util.List;

public class gcListAdapter extends RecyclerView.Adapter<GCViewHolder> {

    Context context;
    List<groupchatInfo> gcList;

    public gcListAdapter(Context context, List<groupchatInfo> gcList) {
        this.context = context;
        this.gcList = gcList;
    }

    @NonNull
    @Override
    public GCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GCViewHolder(LayoutInflater.from(context).inflate(R.layout.groupchatitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GCViewHolder holder, int position){
        holder.gcName.setText(gcList.get(position).getName());
        holder.gcDesc.setText(gcList.get(position).getDescription());
        holder.capacity.setText(gcList.get(position).getCapacity());
    }

    @Override
    public int getItemCount() {
        return gcList.size();
    }
}
