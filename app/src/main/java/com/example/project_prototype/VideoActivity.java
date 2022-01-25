package com.example.project_prototype;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoActivity extends AppCompatActivity {

    private static  int CAMERA_PERMISSION_CODE = 100;
    private static  int VIDEO_RECORD_CODE = 101;

    private Uri videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if(isCameraPresentInPhone()) {
            Log.i("VIDEO RECORD TAG", "Camera is detected");
            getCameraPermission();
        } else {
            Log.i("VIDEO RECORD TAG", "No camera is detected");
        }
    }

    public void recordVideoButtonPressed(View view) {
        recordVideo();
    }


    private boolean isCameraPresentInPhone() {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void getCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)  {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void recordVideo() {
        SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);


        startActivityForResult(intent, VIDEO_RECORD_CODE);
        Date now_start = new Date();
        System.out.println("start: " + formatter_start.format(now_start));



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);

        super.onActivityResult(requestCode, resultCode, data);
        Date now_end = new Date();
        System.out.println("end: " + formatter_end.format(now_end));

        if (requestCode == VIDEO_RECORD_CODE) {
            if(resultCode == RESULT_OK) {
                videoPath = data.getData();
                Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at path " +  videoPath);
            }
        } else if(resultCode == RESULT_CANCELED) {
            Log.i("VIDEO_RECORD_TAG", "Recording is cancelled");
        } else {
            Log.i("VIDEO_RECORD_TAG", "Recording has got some error");
        }

    }

}