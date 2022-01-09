package com.example.planteraapp.SubFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planteraapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetReminder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetReminder.
     */
    // TODO: Rename and change types and number of parameters
    public static SetReminder newInstance(String param1, String param2) {
        SetReminder fragment = new SetReminder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_set_reminder, container, false);
        v.findViewById(R.id.button).setOnClickListener(view->{
            setReminder();
        });
        return v;
    }



    public void setReminder(){
        Bundle b = new Bundle();
        b.putLong("time", 11111);
        b.putLong("interval", 11111);
        b.putString("name", "Water");
        b.putBoolean("notification", true);
        getParentFragmentManager().setFragmentResult("requestKey", b);
        requireActivity().onBackPressed();
    }

}