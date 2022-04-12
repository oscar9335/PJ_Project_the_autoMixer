package com.example.project_prototype;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private PreviewView previewView;
    private VideoCapture videoCapture;
    private Button bRecord;

    private int rotation;

    private boolean recording = false;

    public between_video for_time;

    public String start_time = null;
    public String end_time = null;

    private String outputFile = null;
    private String video_name = null;

    private String outputFile_timeinfo = null;
    private String video_info_path;

    private FileWriter time_imformation;

    //private ProcessCameraProvider mCameraProvider;

    private String roomnumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);



        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        bRecord = findViewById(R.id.bRecord);
        bRecord.setOnClickListener(this);





        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {

                e.printStackTrace();
            }
        }, getExecutor());

        //high project name section
        getSupportActionBar().hide();
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //receive data from hoom_fragment using SafeArg
        roomnumber = getIntent().getExtras().getString("pass_room_number_tovideo");
        System.out.println("Kill me " + roomnumber);




    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        rotation = previewView.getDisplay().getRotation();


//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .build();


//        Preview preview = new Preview.Builder()
//                .setTargetRotation(rotation)
//                .build();

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        // Video capture use case
//        videoCapture = new VideoCapture.Builder()
//                .setTargetRotation(rotation)
//                .build();


        videoCapture = new VideoCapture.Builder()
                .setTargetResolution(new Size(1280, 720))
                .build();




        cameraProvider.unbindAll();
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
                    SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);
                    try {
                        time_imformation.write(formatter_end.format(time2) + "\n");
                        time_imformation.flush();
                        time_imformation.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //this place ##important##
                    //send the video_file_name & video_info_name to the front fragment and let the fragment do the upload file job
                    Bundle bundle = new Bundle();
                    bundle.putString("video_path",outputFile);
                    bundle.putString("video_name",video_name);
                    bundle.putString("video_info_path",video_info_path);
                    bundle.putString("video_info_name",outputFile_timeinfo);
                    bundle.putString("the_room_number",roomnumber);

                    between_video fragment_object = new between_video();
                    fragment_object.setArguments(bundle);


//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,fragment_object).commit();
                    bRecord.setVisibility(View.GONE);
                    previewView.setVisibility(View.GONE);



                }
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {

            File video = new File(getExternalFilesDir("/").getAbsolutePath(), "Video");
            if (!video.exists()) {
                video.mkdirs();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
            Date now = new Date();
            File file = new File( getExternalFilesDir("Video").getAbsolutePath() + "/" + formatter.format(now) + ".mp4");
            outputFile = file.getPath();
            video_name = formatter.format(now) + ".mp4";

            outputFile_timeinfo = "video_time_info" + formatter.format(now) + ".txt";

            try {
                File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Video_time_info");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File filepath = new File(root, outputFile_timeinfo);
                video_info_path = filepath.getPath();

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
                SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);

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