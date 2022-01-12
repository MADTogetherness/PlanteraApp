package com.example.planteraapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planteraapp.R;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;
import com.example.planteraapp.entities.Reminder;

import java.util.List;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ReminderViewHolder>{

    private List<ReminderAndPlant> reminderList;

    public ReminderRecyclerAdapter(List<ReminderAndPlant> reminderList){
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
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textPlantName.setText(reminderList.get(position).reminder.plantName);
        holder.cbTaskDone.setChecked(reminderList.get(position).reminder.completedReminder);
        holder.textReminderName.setText(reminderList.get(position).reminder.name);
//        holder.imagePlant.setImageBitmap();
//        holder.reminderType.setBackground();
//        holder.textTime.setText();
        holder.cbTaskDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                // checkbox pressed
                reminderList.remove(reminderList.get(position));
                notifyItemRemoved(holder.getAdapterPosition());
                notifyDataSetChanged();
                // update the database
            }
        });
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
        public View reminderType;
        public ReminderViewHolder(View view) {
            super(view);
            textPlantName = (TextView) view.findViewById(R.id.text1);
            textReminderName = (TextView) view.findViewById(R.id.reminder_name);
            textTime = (TextView) view.findViewById(R.id.time);
            imagePlant = (ImageView) view.findViewById(R.id.item_image);
            cbTaskDone = (CheckBox) view.findViewById(R.id.task_done);
            reminderType = view.findViewById(R.id.type_of_reminder);
        }
    }
}
