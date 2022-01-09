package com.example.planteraapp.Mainfragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.planteraapp.LauncherActivity;
import com.example.planteraapp.R;
import com.example.planteraapp.SubFragments.ColorTheme;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPlant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPlant extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewPlant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPlant.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPlant newInstance(String param1, String param2) {
        NewPlant fragment = new NewPlant();
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
        return inflater.inflate(R.layout.fragment_new_plant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        view.findViewById(R.id.click).setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
                int result = bundle.getInt("bundleKey");
                Log.d("receive", String.valueOf(result));
                Toast.makeText(requireContext(), LauncherActivity.getThemeName(result), Toast.LENGTH_SHORT).show();
            });
            Bundle b = new Bundle();
            b.putInt("theme", R.style.Theme_PlanteraApp_Accent_Dark);
            requireActivity().findViewById(R.id.coordinator_layout).setVisibility(View.GONE);
            Navigation.findNavController(view).navigate(R.id.action_newPlant_fragment_to_colorTheme, b,
                    LauncherActivity.slide_in_out_fragment_options.build());
        });
    }

}