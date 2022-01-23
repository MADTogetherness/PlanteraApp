package com.example.planteraapp.SubFragments;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.planteraapp.Mainfragments.NewPlant;
import com.example.planteraapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorTheme#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorTheme extends Fragment {
    private LinearLayout[] theme_layouts;
    private int[] colors;
    private RadioGroup group;
    private int selectedTheme;
    public ColorTheme() {/* Required empty public constructor */}
    // TODO: Rename and change types and number of parameters
    public static ColorTheme newInstance(String param1, String param2) {
        ColorTheme fragment = new ColorTheme();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTheme = convertTheme(getArguments() != null ? getArguments().getInt(NewPlant.THEME_KEY) : R.style.Theme_PlanteraApp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_color_theme, container, false);
        theme_layouts = new LinearLayout[5];
        group = v.findViewById(R.id.colorTheme_radio_group);
        colors = new int[]{R.attr.App_Container, R.attr.Section_Container, R.attr.Primary_Font, R.attr.Secondary_Font, R.attr.Button_Font, R.attr.Section_Shadow, R.attr.Button_Primary, R.attr.Button_Secondary};
        theme_layouts[0] = v.findViewById(R.id.layout_default_them);
        theme_layouts[1] = v.findViewById(R.id.layout_monochromatic_brown);
        theme_layouts[2] = v.findViewById(R.id.layout_chiffon_purple);
        theme_layouts[3] = v.findViewById(R.id.layout_accent_dark);
        theme_layouts[4] = v.findViewById(R.id.layout_drakula_light);
        for (LinearLayout theme_layout : theme_layouts) {
            for (int value : colors) {
                TypedValue typedValue = new TypedValue();
                theme_layout.getContext().getTheme().resolveAttribute(value, typedValue, true);
                @ColorInt int color = typedValue.data;
                View item = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.com_small_view_color_display, theme_layout, false);
                View viewColor = item.findViewById(R.id.view_color_item);
                viewColor.setBackgroundTintList(ColorStateList.valueOf(color));
                theme_layout.addView(item);
                theme_layout.setOnClickListener(view -> {
                    for (LinearLayout t : theme_layouts)
                        if (t.getId() == view.getId()) t.setAlpha(1f);
                        else t.setAlpha(0.5f);
                    int vi = check(view);
                    if (group.getCheckedRadioButtonId() != vi)
                        group.check(vi);
                });
            }
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        group.setOnCheckedChangeListener((radioGroup, i) -> getLayout(i).performClick());
        group.check(selectedTheme);
        view.findViewById(R.id.done).setOnClickListener(v -> saveTheme(new Bundle(), group.getCheckedRadioButtonId()));
        view.findViewById(R.id.close).setOnClickListener(v -> requireActivity().onBackPressed());
    }

    public void saveTheme(Bundle result, int theme) {
        result.putInt(NewPlant.THEME_KEY, convertTheme(theme));
        requireActivity().getSupportFragmentManager().setFragmentResult("requestKey", result);
        requireActivity().onBackPressed();
    }

    public int check(View layout) {
        switch (layout.getId()) {
            case R.id.layout_chiffon_purple:
                return R.id.apply_chiffon_purple;
            case R.id.layout_monochromatic_brown:
                return R.id.apply_monochromatic_brown;
            case R.id.layout_accent_dark:
                return R.id.apply_accent_dark;
            case R.id.layout_drakula_light:
                return R.id.apply_drakula_light;
            default:
                return R.id.apply_default_theme;
        }
    }

    public int convertTheme(int id) {
        switch (id) {
            case R.style.Theme_PlanteraApp_Accent_Dark:
                return R.id.apply_accent_dark;
            case R.style.Theme_PlanteraApp_Chiffon_Purple:
                return R.id.apply_chiffon_purple;
            case R.style.Theme_PlanteraApp_Dracula_Light:
                return R.id.apply_drakula_light;
            case R.style.Theme_PlanteraApp_Monochromatic_Brown:
                return R.id.apply_monochromatic_brown;
            case R.id.apply_chiffon_purple:
                return R.style.Theme_PlanteraApp_Chiffon_Purple;
            case R.id.apply_monochromatic_brown:
                return R.style.Theme_PlanteraApp_Monochromatic_Brown;
            case R.id.apply_accent_dark:
                return R.style.Theme_PlanteraApp_Accent_Dark;
            case R.id.apply_drakula_light:
                return R.style.Theme_PlanteraApp_Dracula_Light;
            case R.style.Theme_PlanteraApp:
                return R.id.apply_default_theme;
            case R.id.apply_default_theme:
                return R.style.Theme_PlanteraApp;
        }
        return -1;
    }

    public View getLayout(int theme) {
        switch (theme) {
            case R.id.apply_chiffon_purple:
                return theme_layouts[2];
            case R.id.apply_monochromatic_brown:
                return theme_layouts[1];
            case R.id.apply_accent_dark:
                return theme_layouts[3];
            case R.id.apply_drakula_light:
                return theme_layouts[4];
            default:
                return theme_layouts[0];
        }
    }
}