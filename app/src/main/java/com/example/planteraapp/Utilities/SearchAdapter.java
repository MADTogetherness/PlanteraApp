package com.example.planteraapp.Utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.R;
import com.example.planteraapp.entities.Relations.PlantsWithEverything;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    List<PlantsWithEverything> plants;
    SearchItemClickListener listener;

    public SearchAdapter(List<PlantsWithEverything> plants, SearchItemClickListener listener) {
        this.plants = plants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.com_search_item, viewGroup, false);

        return new SearchViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        PlantsWithEverything plant = plants.get(position);

        // Build up reminder string eg. "Water & Fertilize"
        StringBuilder reminderStr = new StringBuilder();
        for (int i = 0; i < plant.Reminders.size(); i++) {
            if (i == 0) {
                reminderStr.append(plant.Reminders.get(i).name);
                continue;
            }

            reminderStr.append(" & ").append(plant.Reminders.get(i).name);
        }


        // Set text values
        holder.plantName.setText(plant.plant.plantName);
        holder.plantReminders.setText(reminderStr.toString());
        holder.plantDescription.setText(plant.plant.description);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public interface SearchItemClickListener {
        void onClick(int position);
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView plantName;
        public TextView plantReminders;
        public TextView plantDescription;

        private final SearchItemClickListener listener;

        public SearchViewHolder(@NonNull View itemView, SearchItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            plantName = itemView.findViewById(R.id.plant_name);
            plantReminders = itemView.findViewById(R.id.plant_reminders);
            plantDescription = itemView.findViewById(R.id.plant_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }

    }
}
