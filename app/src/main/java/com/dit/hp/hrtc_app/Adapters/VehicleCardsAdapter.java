package com.dit.hp.hrtc_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnBusEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnBusMoreInfoClickListener;

import java.util.ArrayList;
import java.util.List;

public class VehicleCardsAdapter extends RecyclerView.Adapter<VehicleCardsAdapter.BusViewHolder> implements Filterable {

    private List<VehiclePojo> busList;
    private List<VehiclePojo> fullBusList; // Backup list for filtering
    private final OnBusEditClickListener editClickListener;
    private final OnBusMoreInfoClickListener onBusMoreInfoClickListener;

    // Constructor
    public VehicleCardsAdapter(List<VehiclePojo> busList,
                               OnBusEditClickListener editClickListener,
                               OnBusMoreInfoClickListener onBusMoreInfoClickListener) {
        this.busList = busList;
        this.fullBusList = new ArrayList<>(busList);
        this.editClickListener = editClickListener;
        this.onBusMoreInfoClickListener = onBusMoreInfoClickListener;
    }

    // ViewHolder for bus information
    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView headTV, L2TV, L1TV;
        ImageButton editBtn, moreInfoBtn;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            L2TV = itemView.findViewById(R.id.L2TV);
            L1TV = itemView.findViewById(R.id.L1TV);

            editBtn = itemView.findViewById(R.id.editBtn);
            moreInfoBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicles_card_view, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        VehiclePojo busInfo = busList.get(position);

        holder.headTV.setText(busInfo.getVehicleNumber());
        holder.L1TV.setText("Type: " +busInfo.getVehicleType());
        holder.L2TV.setText("Model: " +busInfo.getIotFirm());

        holder.editBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(busInfo, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onBusMoreInfoClickListener != null) {
                onBusMoreInfoClickListener.onMoreInfoClick(busInfo, holder.getAdapterPosition());
            }
        });
        holder.moreInfoBtn.setOnClickListener(v -> {
            if (onBusMoreInfoClickListener != null) {
                editClickListener.onRemoveClick(busInfo, holder.getAdapterPosition());
            }
        });
    }

    public void updateData(List<VehiclePojo> newData) {
        busList.clear();
        busList.addAll(newData);
        notifyDataSetChanged();
    }

    public void addData(List<VehiclePojo> moreData) {
        busList.addAll(moreData);
        notifyItemRangeInserted(busList.size() - moreData.size(), moreData.size());
    }

    public void clearItems() {
        busList.clear();
        notifyDataSetChanged();
    }


    // Add new items to the list
    public void addItems(List<VehiclePojo> newItems) {
        busList.addAll(newItems);  // Add the new items to the list
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    @Override
    public Filter getFilter() {
        return busFilter;
    }

    private final Filter busFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<VehiclePojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullBusList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (VehiclePojo bus : fullBusList) {
                    // Filter only by vehicle number
                    if (bus.getVehicleNumber().toLowerCase().contains(filterPattern)) {
                        filteredList.add(bus);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            busList.clear();
            busList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
