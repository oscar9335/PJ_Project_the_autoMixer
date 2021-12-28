package com.example.project_prototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class home_page extends Fragment implements View.OnClickListener{

    private NavController navController;  // for the use of

    private ImageButton audio_mode_button;
    private ImageButton video_mode_button;
    private Button file_select;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        audio_mode_button =  view.findViewById(R.id.audio_mode);
        video_mode_button =  view.findViewById(R.id.video_mode);
        file_select =  view.findViewById(R.id.file);

        audio_mode_button.setOnClickListener(this);
        video_mode_button.setOnClickListener(this);
        file_select.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio_mode:
                navController.navigate(R.id.action_home_page_to_audio_recorder);
                break;
            case R.id.video_mode:
                navController.navigate(R.id.action_home_page_to_videoActivity);
                break;
            case R.id.file:
                navController.navigate(R.id.action_home_page_to_select_file2);
                break;
        }
    }
}