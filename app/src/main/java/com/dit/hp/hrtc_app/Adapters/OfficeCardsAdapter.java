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

import com.dit.hp.hrtc_app.Modals.OfficePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnOfficeCardClickListeners;
import com.dit.hp.hrtc_app.utilities.Econstants;

import java.util.ArrayList;
import java.util.List;

public class OfficeCardsAdapter extends RecyclerView.Adapter<OfficeCardsAdapter.CardViewHolder> {

    private List<OfficePojo> basicList;
    private List<OfficePojo> completeList; // Backup list for filtering
    private final OnOfficeCardClickListeners onOfficeCardClickListeners;

    // Constructor
    public OfficeCardsAdapter(List<OfficePojo> basicList, OnOfficeCardClickListeners onOfficeCardClickListeners) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.onOfficeCardClickListeners = onOfficeCardClickListeners;
    }


    // Add new items to the list
    public void addItems(List<OfficePojo> newItems) {
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
        ImageView mainImageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.headTV);
            officialId = itemView.findViewById(R.id.L1TV);
            L2TV = itemView.findViewById(R.id.L2TV);
            mainImageView = itemView.findViewById(R.id.staffTypeImageView);

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
        OfficePojo selectedPojo = basicList.get(position);

        holder.headTV.setText(selectedPojo.getOfficeName());

        holder.officialId.setText("ID: " + String.valueOf(selectedPojo.getOfficeParentId()));

        holder.L2TV.setText("Staff Type: " + selectedPojo.getDepartmentPojo().getDepartmentName());


        // Setting Logos According to Office types
//        if (Econstants.isNotEmpty(String.valueOf(selectedPojo.getOfficeParentId()))) {
//            holder.mainImageView.setImageResource(R.drawable.driver);
//        } else {
//            holder.mainImageView.setImageResource(R.drawable.staff_member);
//        }


        holder.cardView.setOnClickListener(v -> {
            if (onOfficeCardClickListeners != null) {
                onOfficeCardClickListeners.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (onOfficeCardClickListeners != null) {
                onOfficeCardClickListeners.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (onOfficeCardClickListeners != null) {
                onOfficeCardClickListeners.onDeleteClick(selectedPojo, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return basicList.size();
    }


}
