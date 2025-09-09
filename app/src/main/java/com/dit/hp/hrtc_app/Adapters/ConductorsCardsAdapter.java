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
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnConductorEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnConductorMoreInfoClickListener;

import java.util.ArrayList;
import java.util.List;

public class ConductorsCardsAdapter extends RecyclerView.Adapter<ConductorsCardsAdapter.CardViewHolder> implements Filterable {

    private List<StaffPojo> basicList;
    private List<StaffPojo> completeList; // Backup list for filtering
    private final OnConductorEditClickListener editClickListener;
    private final OnConductorMoreInfoClickListener onMoreInfoClickListener;

    // Constructor
    public ConductorsCardsAdapter(List<StaffPojo> basicList,
                                  OnConductorEditClickListener editClickListener,
                                  OnConductorMoreInfoClickListener onMoreInfoClickListener) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.editClickListener = editClickListener;
        this.onMoreInfoClickListener = onMoreInfoClickListener;
    }

    // ViewHolder for conductor information
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView routeTV, headTV, L2TV, empType, officialId, statusVehicleTV;
        ImageButton editBtn, moreInfoBtn;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            L2TV = itemView.findViewById(R.id.L2TV);
            officialId = itemView.findViewById(R.id.L1TV);

            editBtn = itemView.findViewById(R.id.editBtn);
            moreInfoBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    // Set specific card view
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conductors_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        StaffPojo selectedPojo = basicList.get(position);

        holder.headTV.setText(selectedPojo.getName());
        holder.officialId.setText("ID: " + String.valueOf(selectedPojo.getId()));
        holder.L2TV.setText("License: " + selectedPojo.getLicenceNo());

        holder.editBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onMoreInfoClickListener != null) {
                onMoreInfoClickListener.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.moreInfoBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onRemoveClick(selectedPojo, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return basicList.size();
    }

    // Filterable interface method
    @Override
    public Filter getFilter() {
        return conductorFilter;
    }

    // Implement filter logic to filter conductors by name
    private final Filter conductorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<StaffPojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(completeList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (StaffPojo conductor : completeList) {
                    if (conductor.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(conductor);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            basicList.clear();
            if (results.values != null) {
                basicList.addAll((List) results.values);
            }
            notifyDataSetChanged();
        }
    };
}
