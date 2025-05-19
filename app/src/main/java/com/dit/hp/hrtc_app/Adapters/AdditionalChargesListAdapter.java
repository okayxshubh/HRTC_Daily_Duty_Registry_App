package com.dit.hp.hrtc_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.R;

import java.util.List;

public class AdditionalChargesListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<AdditonalChargePojo> itemList;
    private List<AdditonalChargePojo> originalList;
    private int selectedPosition = -1;
    private Filter planetFilter;

    public AdditionalChargesListAdapter(Context context, List<AdditonalChargePojo> objects) {
        this.context = context;
        this.itemList = objects;
        this.originalList = objects;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public AdditonalChargePojo getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.additional_charge_cardview, parent, false);
            holder = new ViewHolder();
            holder.headTV = convertView.findViewById(R.id.head_tv);
            holder.secondTV = convertView.findViewById(R.id.second_tv);
            holder.thirdTV = convertView.findViewById(R.id.third_tv);
            holder.imageView = convertView.findViewById(R.id.imageViewEstablishment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AdditonalChargePojo item = itemList.get(position);
        holder.headTV.setText(item.getDepartmentPojo().getDepartmentName());
        holder.secondTV.setText(item.getOfficePojo().getOfficeName());
        holder.thirdTV.setText(item.getDesignationPojo().getDesignationName());

        // Set tick mark if selected
        if (position == selectedPosition) {
            holder.imageView.setImageResource(R.drawable.check);
        } else {
            holder.imageView.setImageResource(R.drawable.role_or_charges);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView headTV;
        TextView secondTV;
        TextView thirdTV;
        ImageView imageView;
    }

    public void selectSingleItem(int position) {
        if (selectedPosition == position) {
            selectedPosition = -1; // Deselect if same item clicked
        } else {
            selectedPosition = position;
        }
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void clearSelection() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null) {
            planetFilter = new PlanetFilter();
        }
        return planetFilter;
    }

    private class PlanetFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                itemList = (List<AdditonalChargePojo>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
