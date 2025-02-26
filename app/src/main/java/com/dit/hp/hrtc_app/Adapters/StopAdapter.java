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

import com.dit.hp.hrtc_app.Modals.StopPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnStopRemoveClickListener;

import java.util.Collections;
import java.util.List;


public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private List<StopPojo> stopList;
    private Context context;
    private final OnStopRemoveClickListener onStopRemoveClickListener;

    private int editingPosition = -1;

    public StopAdapter(List<StopPojo> stopList, OnStopRemoveClickListener onStopRemoveClickListener) {
        this.stopList = stopList;
        this.onStopRemoveClickListener = onStopRemoveClickListener;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stops_item, parent, false);
        return new StopViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(StopViewHolder holder, int position) {
        StopPojo stop = stopList.get(position);
        holder.stopSequenceTextView.setText(String.valueOf(stop.getStopSequence()));
        holder.stopNameTextView.setText(stop.getStopName());

        // Highlight the background if this is the editing position
        if (position == editingPosition) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Set up remove button click listener
        holder.removeStopButton.setOnClickListener(v -> {
            if (onStopRemoveClickListener != null) {
                // Notify listener of stop removal
                onStopRemoveClickListener.onStopRemoveClick(stop, holder.getAdapterPosition());
            }
        });

        // Set up edit button click listener.. Method inside same Interface
        holder.editStopButton.setOnClickListener(v -> {
            if (onStopRemoveClickListener != null) {
                // Notify listener of stop removal
                onStopRemoveClickListener.onStopEditClick(stop, holder.getAdapterPosition());
            }
        });
    }


    public void setEditingPosition(int position) {
        int previousPosition = editingPosition;
        editingPosition = position;
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(position);
    }


    public void clearEditingPosition() {
        Log.d("EditingPosition", "Clearing editing position");
        editingPosition = -1;
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return stopList.size();
    }

    // Method to swap items for drag-and-drop functionality
    public void swapItems(int fromPosition, int toPosition) {
        Collections.swap(stopList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    // Update stop sequences after reordering
    public void updateSequences() {
        for (int i = 0; i < stopList.size(); i++) {
            stopList.get(i).setStopSequence(i + 1); // Update sequence based on position
        }
        notifyDataSetChanged();
    }


    public static class StopViewHolder extends RecyclerView.ViewHolder {

        TextView stopSequenceTextView;
        TextView stopNameTextView;
        ImageButton removeStopButton;
        ImageButton editStopButton;

        public StopViewHolder(View itemView) {
            super(itemView);
            stopSequenceTextView = itemView.findViewById(R.id.stopSequenceTextView);
            stopNameTextView = itemView.findViewById(R.id.stopNameTextView);
            removeStopButton = itemView.findViewById(R.id.removeStopBtn);
            editStopButton = itemView.findViewById(R.id.editStopBtn);
        }
    }
}
