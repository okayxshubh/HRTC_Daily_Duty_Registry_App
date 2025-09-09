package com.dit.hp.hrtc_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.SalaryPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnSalaryCardClickListener;

import java.util.List;

public class SalaryCardsAdapter extends RecyclerView.Adapter<SalaryCardsAdapter.CardViewHolder> {

    private List<SalaryPojo> pojoList;
    private final OnSalaryCardClickListener salaryCardClickListener;

    // Constructor
    public SalaryCardsAdapter(List<SalaryPojo> pojoList, OnSalaryCardClickListener salaryCardClickListener) {
        this.pojoList = pojoList;
        this.salaryCardClickListener = salaryCardClickListener;
    }


    // Add new items to the list
    public void addItems(List<SalaryPojo> newItems) {
        pojoList.addAll(newItems);  // Add the new items to the list
    }


    public void clearItems() {
        pojoList.clear();
        notifyDataSetChanged();
    }


    // ViewHolder for item information
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView completeCardView;
        TextView headTV, secondTV, thirdTV;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            headTV = itemView.findViewById(R.id.head_tv);
            secondTV = itemView.findViewById(R.id.second_tv);
            thirdTV = itemView.findViewById(R.id.third_tv);

            completeCardView = itemView.findViewById(R.id.completeCardView);
        }
    }

    // Set specific card view
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salary_item_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        SalaryPojo selectedPojo = pojoList.get(position);

        holder.headTV.setText("Amount: ₹" +selectedPojo.getNetSalary().toString());
        holder.secondTV.setText("Month: " +selectedPojo.getMonth());
        holder.thirdTV.setText("Status: " +selectedPojo.getStatus());

        holder.completeCardView.setOnClickListener(v -> {
            if (salaryCardClickListener != null) {
                salaryCardClickListener.onCardClickListener(selectedPojo, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojoList.size();
    }


}