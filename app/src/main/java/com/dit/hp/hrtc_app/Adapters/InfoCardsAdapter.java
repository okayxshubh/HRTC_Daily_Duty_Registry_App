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

import com.dit.hp.hrtc_app.DailyDutyRegisterCards;
import com.dit.hp.hrtc_app.Modals.DailyRegisterCardFinal;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.interfaces.OnEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnMoreInfoClickListener;

import java.util.ArrayList;
import java.util.List;

public class InfoCardsAdapter extends RecyclerView.Adapter<InfoCardsAdapter.BusViewHolder> implements Filterable {

    private List<DailyRegisterCardFinal> busList;
    private List<DailyRegisterCardFinal> fullBusList; // For backup
    private final OnEditClickListener editClickListener;
    private final OnMoreInfoClickListener onMoreInfoClickListener;
    private String selectedDate = "";

    // Constructor
    public InfoCardsAdapter(List<DailyRegisterCardFinal> busList, OnEditClickListener editClickListener, OnMoreInfoClickListener onMoreInfoClickListener) {
        this.busList = busList;
        this.fullBusList = new ArrayList<>(busList);
        this.editClickListener = editClickListener;
        this.onMoreInfoClickListener = onMoreInfoClickListener;
    }


    // Add new items to the list
    public void addItems(List<DailyRegisterCardFinal> newItems) {
        busList.addAll(newItems);  // Add the new items to the list
    }


    public void clearItems() {
        busList.clear();
        notifyDataSetChanged();
    }


    // Inflating Layout
    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_record_card_view, parent, false);
        return new BusViewHolder(view);
    }


    // Find Views Here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView routeTV, vehicleNumberTV, driverTV, conductorTV, deptTimeTV, dateTV;
        ImageButton editBtn, moreInfoBtn;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            routeTV = itemView.findViewById(R.id.headTV);
            vehicleNumberTV = itemView.findViewById(R.id.L1TV);
            driverTV = itemView.findViewById(R.id.driverTV);
            conductorTV = itemView.findViewById(R.id.conductorTV);
            deptTimeTV = itemView.findViewById(R.id.L2TV);
            dateTV = itemView.findViewById(R.id.deptTimeTV);
            editBtn = itemView.findViewById(R.id.editBtn);
            moreInfoBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    // Using the above views here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        DailyRegisterCardFinal busInfo = busList.get(position);

        holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName());

        // Normal Route Name
        if (busInfo.getActionTaken() != null && busInfo.getActionTaken().equalsIgnoreCase("none")){
            holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName());
        }

        // Extended Route Name
        else  if (busInfo.getActionTaken() != null && busInfo.getActionTaken().equalsIgnoreCase("extend")){
//            holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName() + "\n extended");
            holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName() + "\n extended \n" + busInfo.getOtherRoute().getRouteName());
        }

        // Curtailed to Stop Name
        else  if (busInfo.getActionTaken() != null && busInfo.getActionTaken().equalsIgnoreCase("curtail")){
            holder.routeTV.setText("Curtailed Route\n" + busInfo.getOriginalRoute().getRouteName());
//            holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName() + "\n curtailed to stop \n" + busInfo.getFinalStop().getStopName());
        }

        // Default Route Name
        else {
            holder.routeTV.setText(busInfo.getOriginalRoute().getRouteName());
        }

        holder.vehicleNumberTV.setText(busInfo.getAssignedVehicle().getVehicleNumber());
        holder.driverTV.setText(busInfo.getMainDriver().getName());
        holder.conductorTV.setText(busInfo.getMainConductor().getName());
        holder.deptTimeTV.setText(busInfo.getActualDepartureTime());
        holder.dateTV.setText(busInfo.getRecordDate());

        holder.editBtn.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(busInfo, holder.getAdapterPosition());
            }
        });

        holder.moreInfoBtn.setOnClickListener(v -> {
            if (onMoreInfoClickListener != null) {
                onMoreInfoClickListener.onMoreInfoClick(busInfo, holder.getAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return busList.size();
    }



    // FILTER STUFF + Search Bar   #################################################################
    @Override
    public Filter getFilter() {
        return busFilter;
    }

    private final Filter busFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DailyRegisterCardFinal> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullBusList);  // Show all if no constraint
            } else {
                // Filter by route, vehicle, or actionTaken
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DailyRegisterCardFinal bus : fullBusList) {
                    boolean matchRouteOrVehicle = bus.getOriginalRoute().getRouteName().toLowerCase().contains(filterPattern) || bus.getAssignedVehicle().getVehicleNumber().toLowerCase().contains(filterPattern);
                    boolean matchAction = bus.getActionTaken().toLowerCase().contains(filterPattern);
                    if (matchRouteOrVehicle || matchAction) {
                        filteredList.add(bus);
                    }
                }
            }

            // Add date filtering logic here
            if (!selectedDate.isEmpty()) {
                List<DailyRegisterCardFinal> dateFilteredList = new ArrayList<>();
                for (DailyRegisterCardFinal bus : filteredList) {
                    if (bus.getRecordDate().equals(selectedDate)) {
                        dateFilteredList.add(bus);
                    }
                }
                filteredList.clear();
                filteredList.addAll(dateFilteredList);  // Only keep buses with the selected date
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
