package com.sp.learntogether.data;

import androidx.recyclerview.widget.DiffUtil;

import com.sp.learntogether.models.Meetup;

import java.util.List;
import java.util.Objects;

public class MeetupDiffCallback extends DiffUtil.Callback {
    private final List<String> oldMeetups;
    private final List<String> newMeetups;


    public MeetupDiffCallback(List<String > oldMeetups, List<String > newMeetups) {
        this.oldMeetups = oldMeetups;
        this.newMeetups = newMeetups;
    }

    @Override
    public int getOldListSize() {
        return oldMeetups.size();
    }

    @Override
    public int getNewListSize() {
        return newMeetups.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldMeetups.get(oldItemPosition), newMeetups.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldMeetups.get(oldItemPosition), newMeetups.get(newItemPosition));
    }

}
