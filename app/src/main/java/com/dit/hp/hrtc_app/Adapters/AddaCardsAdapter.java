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

import com.dit.hp.hrtc_app.Modals.AddaPojo;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnAddaCardClickListeners;

import java.util.ArrayList;
import java.util.List;

public class AddaCardsAdapter extends RecyclerView.Adapter<AddaCardsAdapter.CardViewHolder> implements Filterable {

    private List<AddaPojo> basicList;
    private List<AddaPojo> completeList; // Backup list for filtering
    private final OnAddaCardClickListeners cardClickListeners;

    // Constructor
    public AddaCardsAdapter(List<AddaPojo> basicList, OnAddaCardClickListeners onAddaCardClickListeners) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.cardClickListeners = onAddaCardClickListeners;
    }

    // ViewHolder
    public class CardViewHolder extends RecyclerView.ViewHolder {
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

    public void updateData(List<AddaPojo> newData) {
        basicList.clear();
        basicList.addAll(newData);
        notifyDataSetChanged();
    }

    public void addData(List<AddaPojo> moreData) {
        basicList.addAll(moreData);
        notifyItemRangeInserted(basicList.size() - moreData.size(), moreData.size());
    }

    public void clearItems() {
        basicList.clear();
        notifyDataSetChanged();
    }


    // Add new items to the list
    public void addItems(List<AddaPojo> newItems) {
        basicList.addAll(newItems);  // Add the new items to the list
    }



    // Set specific card view
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adda_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        AddaPojo selectedPojo = basicList.get(position);

        holder.headTV.setText(selectedPojo.getAddaName());
        holder.L1TV.setText("Adda ID: " + String.valueOf(selectedPojo.getId()));
        holder.L2TV.setText("Location: " + selectedPojo.getLocation().getName());


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
            List<AddaPojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(completeList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AddaPojo item : completeList) {
                    if (item.getAddaName().toLowerCase().contains(filterPattern)) {
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
