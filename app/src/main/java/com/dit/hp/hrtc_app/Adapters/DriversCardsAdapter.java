package com.dit.hp.hrtc_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnDriverDeleteClickListener;
import com.dit.hp.hrtc_app.interfaces.OnDriverEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnDriverMoreInfoClickListener;

import java.util.ArrayList;
import java.util.List;

public class DriversCardsAdapter extends RecyclerView.Adapter<DriversCardsAdapter.CardViewHolder> implements Filterable {

    private List<StaffPojo> basicList;
    private List<StaffPojo> completeList; // Backup list for filtering
    private final OnDriverEditClickListener editClickListener;
    private final OnDriverDeleteClickListener onDriverDeleteClickListener;
    private final OnDriverMoreInfoClickListener onMoreInfoClickListener;

    // Constructor
    public DriversCardsAdapter(List<StaffPojo> basicList,
                               OnDriverEditClickListener editClickListener,
                               OnDriverDeleteClickListener onDriverDeleteClickListener,
                               OnDriverMoreInfoClickListener onMoreInfoClickListener) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.editClickListener = editClickListener;
        this.onDriverDeleteClickListener = onDriverDeleteClickListener;
        this.onMoreInfoClickListener = onMoreInfoClickListener;
    }

    // ViewHolder for driver information
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView headTV, L2TV, officialId;
        ImageButton editBtn, deleteBtn;
        CardView cardView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            officialId = itemView.findViewById(R.id.L1TV);
            L2TV = itemView.findViewById(R.id.L2TV);

            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    // Set specific card view
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drivers_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        StaffPojo selectedPojo = basicList.get(position);

        holder.headTV.setText(selectedPojo.getName());
        holder.officialId.setText("ID: " + String.valueOf(selectedPojo.getId()));
        holder.L2TV.setText("License: " + selectedPojo.getLicenceNo());

        holder.cardView.setOnClickListener(v -> {
            if (onMoreInfoClickListener != null) {
                onMoreInfoClickListener.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (onDriverDeleteClickListener != null) {
                onDriverDeleteClickListener.onDeleteClick(selectedPojo, holder.getAdapterPosition());
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
        return driverFilter;
    }

    // Implement filter logic to filter drivers by name
    private final Filter driverFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<StaffPojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(completeList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (StaffPojo driver : completeList) {
                    if (driver.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(driver);
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
