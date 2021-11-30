package com.example.project_prototype;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class audio_recoder extends Fragment implements View.OnClickListener {

    private int PERMISSION_CODE = 21 ;  //random individual number
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    private ImageButton audio_record_button;
    private Chronometer timer;
    private String recordFile;
    private TextView textView3;

    private Button for8k;
    private Button for16k;
    private Button for22_05k;
    private Button for44_1k;
    private Button for48k;


    private boolean isRecording = false;

    private MediaRecorder mediaRecorder;

    private int samplerate = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_recoder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView3 = view.findViewById(R.id.textView3);

        timer = view.findViewById(R.id.record_timer);

        audio_record_button = view.findViewById(R.id.audio_record_button);
        audio_record_button.setOnClickListener(this);

        for8k = view.findViewById(R.id.for8k);
        for8k.setOnClickListener(this);

        for16k = view.findViewById(R.id.for16k);
        for16k.setOnClickListener(this);

        for22_05k = view.findViewById(R.id.for22_05k);
        for22_05k.setOnClickListener(this);

        for44_1k = view.findViewById(R.id.for44_1k);
        for44_1k.setOnClickListener(this);

        for48k = view.findViewById(R.id.for48k);
        for48k.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio_record_button:

                if(isRecording){
                    //stop
                    stopRecording();
                    audio_record_button.setImageDrawable(getResources().getDrawable(R.drawable.recordbutton));
                    for8k.setBackgroundColor(0xFF00FFFF);
                    for16k.setBackgroundColor(0xFF00FFFF);
                    for22_05k.setBackgroundColor(0xFF00FFFF);
                    for44_1k.setBackgroundColor(0xFF00FFFF);
                    for48k.setBackgroundColor(0xFF00FFFF);
                    isRecording = false;
                }
                else{
                    if(checkPermission()) {
                        //if isRecording is false then we can call  startRecording();
                        //start
                        //textView3.setText("debug");
                        startRecording();
                        audio_record_button.setImageDrawable(getResources().getDrawable(R.drawable.recordingpng));
                        isRecording = true;
                    }
                }
                break;
            case R.id.for8k:
                if(isRecording == false) {
                    //can change the frame rate
                    samplerate = 8000;

                    for8k.setBackgroundColor(0xFF00FFFF);

                    for16k.setBackgroundColor(0xFFCCCCCC);
                    for22_05k.setBackgroundColor(0xFFCCCCCC);
                    for44_1k.setBackgroundColor(0xFFCCCCCC);
                    for48k.setBackgroundColor(0xFFCCCCCC);
                }
                break;
            case R.id.for16k:
                if(isRecording == false) {
                    //can change the frame rate
                    samplerate = 16000;

                    for16k.setBackgroundColor(0xFF00FFFF);

                    for8k.setBackgroundColor(0xFFCCCCCC);
                    for22_05k.setBackgroundColor(0xFFCCCCCC);
                    for44_1k.setBackgroundColor(0xFFCCCCCC);
                    for48k.setBackgroundColor(0xFFCCCCCC);
                }
                break;
            case R.id.for22_05k:
                if(isRecording == false) {
                    //can change the frame rate
                    samplerate = 22050;

                    for22_05k.setBackgroundColor(0xFF00FFFF);

                    for8k.setBackgroundColor(0xFFCCCCCC);
                    for16k.setBackgroundColor(0xFFCCCCCC);
                    for44_1k.setBackgroundColor(0xFFCCCCCC);
                    for48k.setBackgroundColor(0xFFCCCCCC);
                }
                break;
            case R.id.for44_1k:
                if(isRecording == false) {
                    //can change the frame rate
                    samplerate = 44100;


                    for44_1k.setBackgroundColor(0xFF00FFFF);

                    for8k.setBackgroundColor(0xFFCCCCCC);
                    for16k.setBackgroundColor(0xFFCCCCCC);
                    for22_05k.setBackgroundColor(0xFFCCCCCC);
                    for48k.setBackgroundColor(0xFFCCCCCC);
                }
                break;
            case R.id.for48k:
                if(isRecording == false) {
                    //can change the frame rate
                    samplerate = 48000;

                    for48k.setBackgroundColor(0xFF00FFFF);

                    for8k.setBackgroundColor(0xFFCCCCCC);
                    for16k.setBackgroundColor(0xFFCCCCCC);
                    for22_05k.setBackgroundColor(0xFFCCCCCC);
                    for44_1k.setBackgroundColor(0xFFCCCCCC);
                }
                break;
        }
    }



    private void startRecording(){

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
        Date now = new Date();
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //AAC more flexible

        mediaRecorder.setAudioSamplingRate(samplerate);




        //public void setAudioSamplingRate (int samplingRate)
        //sampling rate really depends on the format for the audio recording as well as the capabilities of the platform
        //AAC 8k to 96kHz

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(), recordPermission)  == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE );
            return false;
        }
    }


}