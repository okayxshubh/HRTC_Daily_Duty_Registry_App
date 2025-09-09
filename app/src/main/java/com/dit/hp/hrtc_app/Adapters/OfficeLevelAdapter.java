package com.dit.hp.hrtc_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.OfficeLevel;
import com.dit.hp.hrtc_app.Modals.StopPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnOfficeLevelItemsClickListener;

import java.util.Collections;
import java.util.List;


public class OfficeLevelAdapter extends RecyclerView.Adapter<OfficeLevelAdapter.StopViewHolder> {

    private List<OfficeLevel> officeLevelList;
    private Context context;
    private final OnOfficeLevelItemsClickListener onOfficeLevelItemsClickListener;

    private int editingPosition = -1;

    public OfficeLevelAdapter(List<OfficeLevel> officeLevelList, OnOfficeLevelItemsClickListener onOfficeLevelItemsClickListener) {
        this.officeLevelList = officeLevelList;
        this.onOfficeLevelItemsClickListener = onOfficeLevelItemsClickListener;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.office_level_item, parent, false);
        return new StopViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StopViewHolder holder, int position) {
        OfficeLevel listItem = officeLevelList.get(position);
        holder.officeLevelNameTV.setText(listItem.getOfficeLevelName());

        // Highlight the background if this is the editing position
        if (position == editingPosition) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Set up remove button click listener
        holder.removeStopButton.setOnClickListener(v -> {
            if (onOfficeLevelItemsClickListener != null) {
                // Notify listener of listItem removal
                onOfficeLevelItemsClickListener.onOfficeLevelRemoveClick(listItem, holder.getAdapterPosition());
            }
        });

        // Set up edit button click listener.. Method inside same Interface
        holder.editStopButton.setOnClickListener(v -> {
            if (onOfficeLevelItemsClickListener != null) {
                // Notify listener of listItem removal
                onOfficeLevelItemsClickListener.onOfficeLevelEditClick(listItem, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return officeLevelList.size();
    }


    public static class StopViewHolder extends RecyclerView.ViewHolder {

        TextView officeLevelNameTV;
        ImageButton removeStopButton;
        ImageButton editStopButton;

        public StopViewHolder(View itemView) {
            super(itemView);

            officeLevelNameTV = itemView.findViewById(R.id.officeLevelNameTV);
            removeStopButton = itemView.findViewById(R.id.removeStopBtn);
            editStopButton = itemView.findViewById(R.id.editStopBtn);
        }
    }
}
