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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




public class between_video extends Fragment implements View.OnClickListener{

    private NavController navController;

    private Button capture;
    public TextView start_time_video;
    public TextView end_time_video;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_between_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        capture = view.findViewById(R.id.capture);
        capture.setOnClickListener(this);

        end_time_video = view.findViewById(R.id.end_time_video);
        start_time_video = view.findViewById(R.id.start_time_video);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capture:
                navController.navigate(R.id.action_between_video2_to_cameraActivity);
                break;
        }

    }

    public void start_time(Date time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);
        start_time_video.setText(formatter.format(time));
    }

    public void end_time(Date time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);
        end_time_video.setText(formatter.format(time));
    }
}