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
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnRouteEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnRouteMoreInfoClickListener;

import java.util.ArrayList;
import java.util.List;

public class RouteCardsAdapter extends RecyclerView.Adapter<RouteCardsAdapter.CardViewHolder> implements Filterable {
    private List<RoutePojo> basicList;
    private List<RoutePojo> completeList; // Backup list for filtering
    private final OnRouteEditClickListener editClickListener;
    private final OnRouteMoreInfoClickListener onMoreInfoClickListener;

    // Constructor
    public RouteCardsAdapter(List<RoutePojo> basicList,
                             OnRouteEditClickListener editClickListener,
                             OnRouteMoreInfoClickListener onMoreInfoClickListener) {
        this.basicList = basicList;
        this.completeList = new ArrayList<>(basicList);
        this.editClickListener = editClickListener;
        this.onMoreInfoClickListener = onMoreInfoClickListener;
    }

    // ViewHolder for route information
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView routeNameTextView, startLocationTextView, routeTimeTextView, endLocationTextView;
        ImageView imageView;
        ImageButton editButton, deleteButton, stopsBtn;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            routeNameTextView = itemView.findViewById(R.id.routeName); // Route Name
            startLocationTextView = itemView.findViewById(R.id.L1TV); // From Location
            endLocationTextView = itemView.findViewById(R.id.L2TV); // Route Time
            routeTimeTextView = itemView.findViewById(R.id.L3TV); //
            imageView = itemView.findViewById(R.id.staffTypeImageView);

            stopsBtn = itemView.findViewById(R.id.stopsBtn);
            editButton = itemView.findViewById(R.id.editBtn); // Edit Button
            deleteButton = itemView.findViewById(R.id.deleteBtn); // Delete Button
        }
    }

    // Set specific card view
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routes_card_view, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        RoutePojo selectedPojo = basicList.get(position);

        // Bind data to views
        holder.routeNameTextView.setText(selectedPojo.getRouteName());
        holder.startLocationTextView.setText("From: " + selectedPojo.getStartLocationPojo().getName());
        holder.endLocationTextView.setText("To: " + selectedPojo.getEndLocationPojo().getName());
        holder.routeTimeTextView.setText(selectedPojo.getStartTime());


        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onRemoveClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        // Handle stops button click
        holder.stopsBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onStopsClick(selectedPojo, holder.getAdapterPosition());
            }
        });

        // Handle item click for more info
        holder.itemView.setOnClickListener(v -> {
            if (onMoreInfoClickListener != null) {
                onMoreInfoClickListener.onMoreInfoClick(selectedPojo, holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return basicList.size();
    }

    // Add new items to the list
    public void addItems(List<RoutePojo> newItems) {
        basicList.addAll(newItems);  // Add the new items to the list
    }

    public void clearItems() {
        basicList.clear();
        notifyDataSetChanged();
    }

    // Filterable interface method
    @Override
    public Filter getFilter() {
        return routeFilter;
    }

    // Implement filter logic to filter routes by route name
    private final Filter routeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RoutePojo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(completeList); // Show all if no constraint
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RoutePojo route : completeList) {
                    if (route.getRouteName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(route);
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
                basicList.addAll((List<RoutePojo>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}
