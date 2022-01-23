package com.example.planteraapp.Utilities.Adapters;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.Activites.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.Other.AttributeConverters;
import com.example.planteraapp.Model.Relations.ReminderAndPlant;

import java.util.List;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ReminderViewHolder> {
    private final List<ReminderAndPlant> reminderList;
    private final Context context;
    private final CalendarItemCheckListener listener;
    public final int id;

    public ReminderRecyclerAdapter(int id, List<ReminderAndPlant> reminderList, CalendarItemCheckListener listener, Context context) {
        this.reminderList = reminderList;
        this.context = context;
        this.listener = listener;
        this.id = id;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.com_calendar_item, parent, false);
        return new ReminderViewHolder(id, itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReminderAndPlant item = reminderList.get(position);
        holder.textPlantName.setText(item.reminder.plantName);
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
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public interface CalendarItemCheckListener {
        void onChecked(int id, View v, int position, boolean isChecked);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        public TextView textPlantName;
        public TextView textReminderName;
        public TextView textTime;
        public ImageView imagePlant;
        public CheckBox cbTaskDone;
        public View reminderType;
        private final CalendarItemCheckListener listener;
        private final int id;

        public ReminderViewHolder(int recycler, View view, CalendarItemCheckListener listener) {
            super(view);
            this.id = recycler;
            textPlantName = view.findViewById(R.id.text1);
            textReminderName = view.findViewById(R.id.reminder_name);
            textTime = view.findViewById(R.id.time);
            imagePlant = view.findViewById(R.id.item_image);
            cbTaskDone = view.findViewById(R.id.task_done);
            reminderType = view.findViewById(R.id.type_of_reminder);
            this.listener = listener;
            cbTaskDone.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            listener.onChecked(id, itemView, getAdapterPosition(), b);
        }
    }
}
