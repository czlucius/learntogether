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

public class communitiesGVadapter extends ArrayAdapter<Communities> {

    public communitiesGVadapter(@NonNull Context context, ArrayList<Communities> communitiesModelArrayList) {
        super(context, 0, communitiesModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView. 
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.community_item, parent, false);
        }

        Communities courseModel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.communityName);
        ImageView courseIV = listitemView.findViewById(R.id.communityImage);

        courseTV.setText(courseModel.getCommunity_type());
        courseIV.setImageResource(courseModel.getImgid());
        return listitemView;
    }
}