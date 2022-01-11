package com.example.planteraapp.Utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.R;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    List<PlantsWithEverything> plants;

    public SearchAdapter(List<PlantsWithEverything> plants) {
        this.plants = plants;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.com_search_item, viewGroup, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        PlantsWithEverything plant = plants.get(position);

        holder.plantName.setText(plant.plant.plantName);

        StringBuilder reminderStr = new StringBuilder();
        for (int i = 0; i < plant.Reminders.size(); i++) {
            if(i == 0) {
                reminderStr.append(plant.Reminders.get(i));
                continue;
            }

            reminderStr.append(" & ").append(plant.Reminders.get(i));
        }


        holder.plantReminders.setText(reminderStr.toString());
        holder.plantDescription.setText(plant.plant.description);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }
}

class SearchViewHolder extends RecyclerView.ViewHolder {

    TextView plantName;
    TextView plantReminders;
    TextView plantDescription;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        plantName = itemView.findViewById(R.id.plant_name);
        plantReminders = itemView.findViewById(R.id.plant_reminders);
        plantDescription = itemView.findViewById(R.id.plant_description);
    }

}