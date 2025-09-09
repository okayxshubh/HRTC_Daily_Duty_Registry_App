package com.dit.hp.hrtc_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnStaffCardClickListeners;

import java.util.ArrayList;
import java.util.List;

public class StaffCardsAdapter extends RecyclerView.Adapter<StaffCardsAdapter.CardViewHolder> implements Filterable {

    private List<StaffPojo> basicList;
    private List<StaffPojo> completeList; // Backup list for filtering
    private final OnStaffCardClickListeners staffCardClickListeners;

    // Constructor
    public StaffCardsAdapter(List<StaffPojo> basicList, OnStaffCardClickListeners staffCardClickListeners) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.staffCardClickListeners = staffCardClickListeners;
    }


    // Add new items to the list
    public void addItems(List<StaffPojo> newItems) {
        basicList.addAll(newItems);  // Add the new items to the list
    }


    public void clearItems() {
        basicList.clear();
        notifyDataSetChanged();
    }



    // ViewHolder for item information
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView headTV, L2TV, officialId;
        ImageButton editBtn, deleteBtn;
        CardView cardView;
        ImageView staffTypeImageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            officialId = itemView.findViewById(R.id.L1TV);
            L2TV = itemView.findViewById(R.id.L2TV);
            staffTypeImageView = itemView.findViewById(R.id.staffTypeImageView);

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
                .inflate(R.layout.staff_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        StaffPojo selectedPojo = basicList.get(position);

        holder.headTV.setText(selectedPojo.getName());
        holder.officialId.setText("ID: " + String.valueOf(selectedPojo.getId()));
        holder.L2TV.setText("Staff Type: " + selectedPojo.getStaffType());


//        // Setting Logos According to Staff Type
//        if (selectedPojo.getStaffType().equalsIgnoreCase("Driver")){
//            holder.staffTypeImageView.setImageResource(R.drawable.driver);
//        }else if (selectedPojo.getStaffType().equalsIgnoreCase("Conductor")){
//            holder.staffTypeImageView.setImageResource(R.drawable.conductor);
//        } else {
//            holder.staffTypeImageView.setImageResource(R.drawable.staff_member);
//        }


        holder.cardView.setOnClickListener(v -> {
            if (staffCardClickListeners != null) {
                staffCardClickListeners.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (staffCardClickListeners != null) {
                staffCardClickListeners.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (staffCardClickListeners != null) {
                staffCardClickListeners.onDeleteClick(selectedPojo, holder.getAdapterPosition());
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
                for (StaffPojo item : completeList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
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
