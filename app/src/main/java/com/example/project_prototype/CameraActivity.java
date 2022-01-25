package com.example.project_prototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private VideoCapture videoCapture;
    private Button bRecord;

    private boolean recording = false;

    private String outputFile = null;

    public between_video for_time;

    public TextView start_time;
    public TextView end_time;

    private String outputFile_timeinfo = null;
    private FileWriter time_imformation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        bRecord = findViewById(R.id.bRecord);
        bRecord.setOnClickListener(this);

        start_time = findViewById(R.id.start_time_video);
        end_time = findViewById(R.id.end_time_video);


        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        // Video capture use case
        videoCapture = new VideoCapture.Builder()
                .build();

        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview,videoCapture);

        checkPermission();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bRecord:
                if (recording == false){
                    recording = true;
                    bRecord.setText("STOP");
                    recordVideo();
                } else {//recording == true
                    recording = false;
                    bRecord.setText("START");
                    videoCapture.stopRecording();



                    Date time2 = new Date();
                    SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);
                    try {
                        time_imformation.write(formatter_end.format(time2) + "\n");
                        time_imformation.flush();
                        time_imformation.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {

//           File file1 = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
//                    System.currentTimeMillis() + ".mp4");

            File video = new File(getExternalFilesDir("/").getAbsolutePath(), "Video");
            if (!video.exists()) {
                video.mkdirs();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
            Date now = new Date();
            File file = new File( getExternalFilesDir("Video").getAbsolutePath() + "/" + formatter.format(now) + ".mp4");

            outputFile_timeinfo = "video_time_info" + formatter.format(now) + ".txt";

            try {
                File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Video_time_info");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File filepath = new File(root, outputFile_timeinfo);
                time_imformation = new FileWriter(filepath);

                //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            VideoCapture.Metadata metadata = new VideoCapture.Metadata();
            VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).build();

//            long timestamp = System.currentTimeMillis();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }



                for_time = new between_video();

                Date time = new Date();
                SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss", Locale.TAIWAN);

                //tart_time.setText(formatter_start.format(time));
                try {
                    //time_imformation.append(sBody);
                    time_imformation.write(formatter_start.format(time)+"\n");
                    time_imformation.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                videoCapture.startRecording(
                        outputFileOptions,
                        getExecutor(),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Toast.makeText(CameraActivity.this, "Video has been saved successfully.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                Toast.makeText(CameraActivity.this, "Error saving video: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }
}