//Calendar.java
package com.example.planteraapp.Mainfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planteraapp.AppDatabase;
import com.example.planteraapp.R;
import com.example.planteraapp.Utilities.ReminderRecyclerAdapter;
import com.example.planteraapp.entities.DAO.PlantDAO;
import com.example.planteraapp.entities.Plant;
import com.example.planteraapp.entities.PlantLocation;
import com.example.planteraapp.entities.PlantType;
import com.example.planteraapp.entities.Relations.ReminderAndPlant;
import com.example.planteraapp.entities.Reminder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment {
    private PlantDAO DAO;


    private RecyclerView rvTodayReminder, rvTomorrowReminder;
    private ReminderRecyclerAdapter todayReminderRVA, tomorrowReminderRVA;
    private List<List<ReminderAndPlant>> allReminderList;

    public Calendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Calendar newInstance(String param1, String param2) {
        Calendar fragment = new Calendar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        allReminderList = getReminderList();
        todayReminderRVA = new ReminderRecyclerAdapter(allReminderList.get(0), DAO, requireContext());
        tomorrowReminderRVA = new ReminderRecyclerAdapter(allReminderList.get(1), DAO, requireContext());

        if (allReminderList.get(0).size() == 0)
            view.findViewById(R.id.empty_today).setVisibility(View.VISIBLE);
        if (allReminderList.get(1).size() == 0)
            view.findViewById(R.id.empty_tomorrow).setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager todayLayoutManager = new LinearLayoutManager(getContext());
        rvTodayReminder.setLayoutManager(todayLayoutManager);
        rvTodayReminder.setItemAnimator(new DefaultItemAnimator());
        rvTodayReminder.setHasFixedSize(true);
        rvTodayReminder.setAdapter(todayReminderRVA);

        RecyclerView.LayoutManager tomorrowLayoutManager = new LinearLayoutManager(getContext());
        rvTomorrowReminder.setLayoutManager(tomorrowLayoutManager);
        rvTomorrowReminder.setItemAnimator(new DefaultItemAnimator());
        rvTomorrowReminder.setHasFixedSize(true);
        rvTomorrowReminder.setAdapter(tomorrowReminderRVA);

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
}