package com.dit.hp.hrtc_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dit.hp.hrtc_app.Modals.RoutePojo;

import java.util.List;

public class RouteSpinnerAdapter extends ArrayAdapter<RoutePojo> {

    private Context context;
    private List<RoutePojo> values;

    public RouteSpinnerAdapter(Context context, int textViewResourceId, List<RoutePojo> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public RouteSpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public int getCount() {
        return values.size();
    }

    public RoutePojo getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public int getPositionForRoute(String name, int id) {
        for (int i = 0; i < values.size(); i++) {
            RoutePojo routePojo = values.get(i);
            // Check if both the name and ID match
            if (routePojo.getRouteName().equals(name) && routePojo.getRouteId() == (id)) {
                return i; // Return the position of the matching driver
            }
        }
        return -1; // Return -1 if no match is found
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        //label.setTextColor(Color.BLACK);
        label.setTextSize(18);
        label.setTextColor(Color.parseColor("#13914f"));
        label.setPadding(30, 0, 30, 0);

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getRouteName());
        // label.setTypeface(Econstants.getTypefaceRegular(context));

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextColor(Color.parseColor("#585566"));
        label.setTextSize(18);
        label.setPadding(15, 15, 15, 15);
        label.setText(values.get(position).getRouteName());
        // label.setTypeface(Econstants.getTypefaceRegular(context));

        return label;
    }


}