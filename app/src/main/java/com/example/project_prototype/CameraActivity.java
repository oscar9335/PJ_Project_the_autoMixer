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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

    private String date_now_gotfromrequest = null;

    private String url = "http://" + "140.116.82.135" + ":" + 5000 + "/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //high project name section
        getSupportActionBar().hide();
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        bRecord = findViewById(R.id.bRecord);
        bRecord.setOnClickListener(this);

        checkPermission();

        //receive data from hoom_fragment using SafeArg
        roomnumber = getIntent().getExtras().getString("pass_room_number_tovideo");
        System.out.println("Kill me " + roomnumber);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {

                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


    }

//    Executor getExecutor() {
//        return ContextCompat.getMainExecutor(this);
//    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        rotation = previewView.getDisplay().getRotation();


//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();



        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        videoCapture = new VideoCapture.Builder()
                .setCameraSelector(cameraSelector)
                .setTargetResolution(new Size(1280, 720))
                .setBitRate(5000000)
                .setVideoFrameRate(30)
                .build();



        //bind to lifecycle:
        cameraProvider.bindToLifecycle(this, cameraSelector, preview,videoCapture);

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

//                    Date time2 = new Date();
//                    SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);

                    date_now_gotfromrequest = null;
                    date_postRequest(url);
                    int toresendrequest = 0;
                    while(date_now_gotfromrequest == null){

                        if(toresendrequest > 6000){
                            date_postRequest(url);
                        }
//                        System.out.println("IN WHILE");
                        toresendrequest++;
                    }
                    System.out.println("TIMES IN WHILE");
                    System.out.println(toresendrequest);

                    if(date_now_gotfromrequest != null){
                        try {
//                        time_imformation.write(formatter_end.format(time2) + "\n");
                            time_imformation.write(date_now_gotfromrequest + "\n");
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

            File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Video_time_info");
            if (!root.exists()) {
                root.mkdirs();
            }

            //file name
            // format yyyy_MM_dd_hh_mm_ss

            date_postRequest(url);

            int toresendrequest = 0;
            while(date_now_gotfromrequest == null){
                if(toresendrequest > 6000){
                    date_postRequest(url);
                }
//                System.out.println("IN WHILE");
                toresendrequest++;
            }
            System.out.println("TIMES IN WHILE");
            System.out.println(toresendrequest);

            if(date_now_gotfromrequest != null){
                String date_now_filename = date_now_gotfromrequest.substring(0,19);

//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
//            Date now = new Date();

//            File file = new File( getExternalFilesDir("Video").getAbsolutePath() + "/" + formatter.format(now) + ".mp4");


//            outputFile = file.getPath();

                video_name = date_now_filename + ".mp4";
                String videovideoname = date_now_filename;

                outputFile_timeinfo = "video_time_info" + date_now_filename + ".txt";

                try {
                    File filepath = new File(root, outputFile_timeinfo);
                    video_info_path = filepath.getPath();
                    time_imformation = new FileWriter(filepath);

                } catch (IOException e) {
                    e.printStackTrace();
                }

//            VideoCapture.Metadata metadata = new VideoCapture.Metadata();
//            VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build();



                try {
                    for_time = new between_video();

//                Date time = new Date();
//                SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);

                    try {
                        time_imformation.write(date_now_gotfromrequest + "\n");
                        time_imformation.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, videovideoname);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");    // this is significant WTF

                outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath() + "/" + videovideoname + ".mp4";
                System.out.println(outputFile);


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

                videoCapture.startRecording(
                        new VideoCapture.OutputFileOptions.Builder(
                                getContentResolver(),
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                        ).build(),
                        ContextCompat.getMainExecutor(this),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Toast.makeText(CameraActivity.this, outputFile, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                Toast.makeText(CameraActivity.this, "Error saving video: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }
    }

    private void date_postRequest(String URL){

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .connectTimeout(100,TimeUnit.MICROSECONDS)
//            .writeTimeout(100,TimeUnit.MICROSECONDS)
//            .readTimeout(100,TimeUnit.MICROSECONDS).build();

        OkHttpClient okHttpClient = new OkHttpClient();

        String date_obtain_url = URL + "timesynchronize";

        RequestBody formBody = new FormBody.Builder()
                .add("date_request", "date_request_camera")
                .build();

        Request request = new Request.Builder()
                .url(date_obtain_url)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

//                date_now_gotfromrequest = "TTTTTTTTTTTTTTTTTTTT";
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                date_now_gotfromrequest = response.body().string();
//                System.out.println(date_now_gotfromrequest);
            }
        });


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