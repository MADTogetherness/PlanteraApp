//Calendar.java
package com.example.planteraapp.Mainfragments;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.AttributeConverters;
import com.example.planteraapp.Utilities.ReminderRecyclerAdapter;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;
import com.example.planteraapp.entities.Reminder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Calendar extends Fragment implements ReminderRecyclerAdapter.CalendarItemCheckListener {
    private PlantDAO DAO;
    private RecyclerView rvTodayReminder, rvTomorrowReminder;
    private ReminderRecyclerAdapter todayReminderRVA, tomorrowReminderRVA;
    private List<ReminderAndPlant> today, tomorrow;

    public Calendar() {/*Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DAO = AppDatabase.getInstance(requireContext()).plantDAO();
        initViews(view);
    }


    public void initViews(View view) {
        rvTodayReminder = view.findViewById(R.id.rv_today);
        rvTomorrowReminder = view.findViewById(R.id.rv_tomorrow);
        RecyclerView.LayoutManager todayLayoutManager = new LinearLayoutManager(getContext());
        rvTodayReminder.setLayoutManager(todayLayoutManager);
        rvTodayReminder.setItemAnimator(new DefaultItemAnimator());
        rvTodayReminder.setHasFixedSize(true);
        RecyclerView.LayoutManager tomorrowLayoutManager = new LinearLayoutManager(getContext());
        rvTomorrowReminder.setLayoutManager(tomorrowLayoutManager);
        rvTomorrowReminder.setItemAnimator(new DefaultItemAnimator());
        rvTomorrowReminder.setHasFixedSize(true);
        initialiseAdapter();
    }

    public List<List<ReminderAndPlant>> getReminderList() {
        List<ReminderAndPlant> today = new ArrayList<>();
        List<ReminderAndPlant> tomorrow = new ArrayList<>();
        for (ReminderAndPlant reminder : DAO.getRemindersWithPlant()) {
            if (Duration.between(Instant.now(), Instant.ofEpochMilli(reminder.reminder.realEpochTime)).compareTo(Duration.ofDays(1)) <= 0)
                today.add(reminder);
            else
                tomorrow.add(reminder);
        }
        return Arrays.asList(today, tomorrow);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initialiseAdapter() {
        List<List<ReminderAndPlant>> all_rem = getReminderList();
        today = all_rem.get(0);
        tomorrow = all_rem.get(1);
        todayReminderRVA = new ReminderRecyclerAdapter(rvTodayReminder.getId(), today, this, requireContext());
        tomorrowReminderRVA = new ReminderRecyclerAdapter(rvTomorrowReminder.getId(), tomorrow, this, requireContext());
        rvTodayReminder.setAdapter(todayReminderRVA);
        rvTomorrowReminder.setAdapter(tomorrowReminderRVA);
        if (today.size() == 0)
            requireView().findViewById(R.id.empty_today).setVisibility(View.VISIBLE);
        if (tomorrow.size() == 0)
            requireView().findViewById(R.id.empty_tomorrow).setVisibility(View.VISIBLE);
    }

    @Override
    public void onChecked(int recyclerID, View itemView, int position, boolean isChecked) {
        Log.d("check", isChecked + "");
        itemView.animate().translationX(1400).setDuration(600).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Reminder rem = recyclerID == rvTodayReminder.getId()
                        ? today.get(position).reminder
                        : tomorrow.get(position).reminder;
                if (isChecked) {
                    rem.lastCompleted = rem.realEpochTime;
                    rem.realEpochTime += rem.repeatInterval;
                }
                DAO.updateReminder(rem);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                initialiseAdapter();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                initialiseAdapter();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    @Override
    public void onResume() {
        initialiseAdapter();
        super.onResume();
    }
}