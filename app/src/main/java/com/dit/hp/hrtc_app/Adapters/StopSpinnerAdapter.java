package com.dit.hp.hrtc_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dit.hp.hrtc_app.Modals.StopPojo;

import java.util.List;

public class StopSpinnerAdapter extends ArrayAdapter<StopPojo> {
    private final Context context;
    private final List<StopPojo> stops;


    public StopSpinnerAdapter(Context context, int textViewResourceId, List<StopPojo> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.stops = values;
    }


    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(stops.get(position).getStopName());
        // Set text size, color, and padding for the TextView
        textView.setTextSize(18);
        textView.setTextColor(Color.parseColor("#13914f"));
        textView.setPadding(30, 0, 30, 0);
        return convertView;
        

    }



    public int getPositionForItem(String nameNId) {
        for (int i = 0; i < stops.size(); i++) {
            StopPojo item = stops.get(i);
            // Check if both the name and ID match
            if ((item.getStopName() + item.getStopId()).equals(nameNId)) {
                return i; // Return the position of the matching driver
            }
        }
        return -1; // Return -1 if no match is found
    }

    public int getPositionForItem(int Id) {
        for (int i = 0; i < stops.size(); i++) {
            StopPojo item = stops.get(i);
            // Check if both the name and ID match
            if (item.getStopId() == Id) {
                return i; // Return the position of the matching driver
            }
        }
        return -1; // Return -1 if no match is found
    }

    @Override
    public @NonNull View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(stops.get(position).getStopName());
        return convertView;
    }


}
