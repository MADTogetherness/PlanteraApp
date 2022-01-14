package com.example.planteraapp.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.AppLaunchChecker;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;

import java.util.List;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ReminderViewHolder> {

    private final List<ReminderAndPlant> reminderList;
    private final PlantDAO DAO;
    private final Context context;

    public ReminderRecyclerAdapter(List<ReminderAndPlant> reminderList, PlantDAO DAO, Context context) {
        this.reminderList = reminderList;
        this.DAO = DAO;
        this.context = context;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.com_calendar_item, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReminderAndPlant item = reminderList.get(position);
        holder.textPlantName.setText(item.reminder.plantName);
        holder.cbTaskDone.setChecked(item.reminder.completedReminder);
        holder.textReminderName.setText(item.reminder.name);
        holder.imagePlant.setImageBitmap(AttributeConverters.StringToBitMap(item.plant.profile_image));
        if (item.reminder.notify) {
            holder.reminderType.setBackgroundTintList(ContextCompat.getColorStateList(context, LauncherActivity.getColour(item.reminder.name)));
            holder.textTime.setText(AttributeConverters.getRemainingTime(item.reminder.realEpochTime));
        } else {
            holder.reminderType.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Secondary_Font));
            holder.textTime.setText("Notification is Off");
            holder.cbTaskDone.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Secondary_Font));
        }
        holder.cbTaskDone.setEnabled(item.reminder.notify);
        holder.cbTaskDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                item.reminder.lastCompleted = item.reminder.realEpochTime;
                item.reminder.realEpochTime += item.reminder.repeatInterval;
            } else {
                if (System.currentTimeMillis() <= item.reminder.realEpochTime - item.reminder.repeatInterval) {
                    item.reminder.realEpochTime = item.reminder.lastCompleted;
                    item.reminder.lastCompleted = item.reminder.realEpochTime - item.reminder.repeatInterval;
                } else {
                    item.reminder.lastCompleted = item.reminder.realEpochTime;
                    item.reminder.realEpochTime += item.reminder.repeatInterval;
                }
            }
            DAO.updateReminder(item.reminder);
            holder.textTime.setText(AttributeConverters.getRemainingTime(item.reminder.realEpochTime));
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    /**
     * ViewHolder class
     */
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView textPlantName;
        public TextView textReminderName;
        public TextView textTime;
        public ImageView imagePlant;
        public CheckBox cbTaskDone;
        public View reminderType;

        public ReminderViewHolder(View view) {
            super(view);
            textPlantName = view.findViewById(R.id.text1);
            textReminderName = view.findViewById(R.id.reminder_name);
            textTime = view.findViewById(R.id.time);
            imagePlant = view.findViewById(R.id.item_image);
            cbTaskDone = view.findViewById(R.id.task_done);
            reminderType = view.findViewById(R.id.type_of_reminder);
        }
    }
}
