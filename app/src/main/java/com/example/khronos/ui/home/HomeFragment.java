package com.example.khronos.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.khronos.MainActivity;
import com.example.khronos.R;
import com.example.khronos.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button decide_button = binding.decideButton;
        Button motivation_button = binding.motivationButton;
        TextView decision_text = binding.textView;

        decide_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rd = new Random(); // creating Random object
                if (rd.nextBoolean())
                    decision_text.setText("Go work!!!");
                else
                    decision_text.setText("Relax and chill");

            }
        });

        motivation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rd = new Random();
                String text = "";
                switch (rd.nextInt(7)) {
                    case 0: text = "You can do it!"; break;
                    case 1: text = "Keep up working!"; break;
                    case 2: text = "Don't give up now!"; break;
                    case 3: text = "You are amazing!"; break;
                    case 4: text = "Sleep is for the weak!"; break;
                    case 5: text = "Everything is gonna be OK!"; break;
                    case 6: text = "You'll get through this!"; break;
                }

                Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });


        /*final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        // set active fragment
        MainActivity.activeFragment = "home";

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}