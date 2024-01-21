package com.sp.learntogether.ui.communities;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.learntogether.R;

public class GCViewHolder extends RecyclerView.ViewHolder {
    TextView gcName;
    TextView capacity;
    TextView gcDesc;
    public GCViewHolder(@NonNull View itemView) {
        super(itemView);
        gcName = itemView.findViewById(R.id.grpName);
        capacity = itemView.findViewById(R.id.grpCapacity);
        gcDesc = itemView.findViewById(R.id.grpDesc);
    }
}
