package com.example.project_prototype;

import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class audio_recoder extends Fragment implements View.OnClickListener {

    private ImageButton audio_record_button;
    private String recordFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_recoder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        audio_record_button = view.findViewById(R.id.audio_record_button);

        audio_record_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio_record_button:
                /*
                if(isRecording){
                    //stop
                    stopRecording();
                    recordbutton.setImageDrawable(getResources().getDrawable(R.drawable.record_button));
                    isRecording = false;
                }
                else{
                    if(checkPermission()) {
                        //start
                        startRecording();
                        recordbutton.setImageDrawable(getResources().getDrawable(R.drawable.stop_button));
                        isRecording = true;
                    }
                }
                */
                break;
        }
    }

    private void startRecording(){

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
        Date now = new Date();
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }
}