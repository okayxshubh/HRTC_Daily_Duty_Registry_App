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

import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnDepotCardClickListeners;

import java.util.ArrayList;
import java.util.List;
public class DepotsCardsAdapter extends RecyclerView.Adapter<DepotsCardsAdapter.CardViewHolder> implements Filterable {

    private List<DepotPojo> basicList;
    private final List<DepotPojo> completeList; // Backup list for filtering
    private final OnDepotCardClickListeners cardClickListeners;

    // Constructor
    public DepotsCardsAdapter(List<DepotPojo> basicList, OnDepotCardClickListeners onDepotCardClickListeners) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList); // Backup list initialized here
        this.cardClickListeners = onDepotCardClickListeners;
    }

    // ViewHolder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView headTV, L1TV, L2TV;
        ImageButton editBtn, deleteBtn;
        CardView cardView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            L1TV = itemView.findViewById(R.id.L1TV);
            L2TV = itemView.findViewById(R.id.L2TV);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.depots_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        DepotPojo selectedPojo = basicList.get(position);

        // Set TextViews
        holder.headTV.setText(selectedPojo.getDepotName());
        holder.L1TV.setText("Depot ID: " + selectedPojo.getId());
        holder.L2TV.setText("Depot Code: " + selectedPojo.getDepotCode());

        // Set Click Listeners
        holder.cardView.setOnClickListener(v -> {
            if (cardClickListeners != null) {
                cardClickListeners.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (cardClickListeners != null) {
                cardClickListeners.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (cardClickListeners != null) {
                cardClickListeners.onDeleteClick(selectedPojo, holder.getAdapterPosition());
            }
        });
    }

    // Update entire list (e.g., Search results)
    public void updateData(List<DepotPojo> newList) {
        basicList.clear();
        basicList.addAll(newList);
        notifyDataSetChanged();
    }

    // Append data for "Load More"
    public void appendData(List<DepotPojo> newList) {
        int oldSize = basicList.size();
        basicList.addAll(newList);
        notifyItemRangeInserted(oldSize, newList.size());
    }


    @Override
    public int getItemCount() {
        return basicList.size();
    }

    // Filterable interface method
    @Override
    public Filter getFilter() {
        return depotFilter;
    }

    // Implement filter logic to filter depots by name
    private final Filter depotFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DepotPojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(completeList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DepotPojo item : completeList) {
                    if (item.getDepotName().toLowerCase().contains(filterPattern)) {
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
                // Safely cast results to List<DepotPojo>
                @SuppressWarnings("unchecked")
                List<DepotPojo> resultList = (List<DepotPojo>) results.values;
                basicList.addAll(resultList);
            }
            notifyDataSetChanged();
        }
    };
}
