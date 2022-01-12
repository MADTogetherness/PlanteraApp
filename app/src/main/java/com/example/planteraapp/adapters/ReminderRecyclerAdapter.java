package com.example.planteraapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.R;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ReminderViewHolder>{

    private List<Reminder> reminderList;

    public ReminderRecyclerAdapter(List<Reminder> reminderList){
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.com_calendar_item, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.textPlantName.setText(reminderList.get(position).plantName);
        holder.cbTaskDone.setChecked(reminderList.get(position).completedReminder);
        holder.textReminderName.setText(reminderList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    /**
     * ViewHolder class
     */
    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView textPlantName;
        public TextView textReminderName;
        public TextView textTime;
        public ImageView imagePlant;
        public CheckBox cbTaskDone;
        public ReminderViewHolder(View view) {
            super(view);
            textPlantName = (TextView) view.findViewById(R.id.text1);
            textReminderName = (TextView) view.findViewById(R.id.reminder_name);
            textTime = (TextView) view.findViewById(R.id.time);
            imagePlant = (ImageView) view.findViewById(R.id.item_image);
            cbTaskDone = (CheckBox) view.findViewById(R.id.task_done);

        }
    }
}
