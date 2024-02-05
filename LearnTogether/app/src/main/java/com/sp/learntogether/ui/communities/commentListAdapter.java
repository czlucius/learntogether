package com.sp.learntogether.ui.communities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sp.learntogether.R;

import java.util.ArrayList;
import java.util.List;

public class commentListAdapter extends ArrayAdapter<commentInfo> {

    private ArrayList<commentInfo> commentInfos;
    public commentListAdapter(@NonNull Context context, ArrayList<commentInfo> commentInfoArrayList) {
        super(context, 0, commentInfoArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.commentitem, parent, false);
        }

        commentInfo comment = getItem(position);
        TextView commentUser = listitemView.findViewById(R.id.commentUser);
        TextView commentDateTime = listitemView.findViewById(R.id.commentDateTime);
        TextView commentDesc = listitemView.findViewById(R.id.commentDesc);

        commentUser.setText(comment.getUsername());
        commentDateTime.setText(comment.getDateTime());
        commentDesc.setText(comment.getCommentDesc());
        return listitemView;
    }
}
