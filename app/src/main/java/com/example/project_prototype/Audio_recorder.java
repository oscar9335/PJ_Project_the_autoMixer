package com.example.project_prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//http
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class Audio_recorder extends AppCompatActivity {

    private static final String TAG = "Tag test";
    private int PERMISSION_CODE = 21 ;  //random individual number
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    private Context context;

    private MediaRecorder myAudioRecorder;

    private String outputFile = null;


    private ImageButton recorder_button;
    private TextView textView4;

    private boolean isRecording = false;

    private Chronometer timer;

    private Button for8K;
    private Button for16K;
    private Button for44K;

    private int samplerate = 8000;
    private String samplerate_show = "8000\n";

    private TextView see_time_start;
    private TextView see_time_end;

    private String outputFile_timeinfo = null;
    private String info_path = null;
    private FileWriter time_imformation;

    String thefilename;

    //OKhttp URL setting
    private String url = "http://" + "192.168.1.101" + ":" + 5000 + "/";
    private TextView audio_upload_txt;

    private Button download;
    private String download_check = "NOT";

    private String download_filename;

    private ProgressBar uploadProgress;

    private String roomnumtest = "1";
    private Uri Download_Uri;

    private String roomnumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        context = this;
        //activity = this;

        recorder_button = (ImageButton) findViewById(R.id.recorder_button);
        textView4 = (TextView) findViewById(R.id.textView4);

        audio_upload_txt = (TextView) findViewById(R.id.audio_upload_txt);

        see_time_start = (TextView) findViewById(R.id.see_time_start);
        see_time_end = (TextView) findViewById(R.id.see_time_end);

        timer = (Chronometer) findViewById(R.id.record_timer2);
        for8K = (Button) findViewById((R.id.for8k2)) ;
        for16K = (Button) findViewById((R.id.for16k2)) ;
        for44K = (Button) findViewById((R.id.for44_1k2)) ;

        recorder_button.setOnClickListener(click);
        for8K.setOnClickListener(click);
        for16K.setOnClickListener(click);
        for44K.setOnClickListener(click);

        download = (Button) findViewById(R.id.downloadinaudio);
        download.setOnClickListener(click);

        // I need to obtain the room number here to navigate to the specific route


        askForPermissions();
        checkPermission();

        //receive data from hoom_fragment using SafeArg
        roomnumber = getIntent().getExtras().getString("pass_room_number_toaudio");
        System.out.println("Kill me " + roomnumber);



    }

    private View.OnClickListener click = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recorder_button:
                    if(isRecording){

                        stopRecording();
                        isRecording = false;
                    }
                    else{
                        record();
                        isRecording = true;
                    }
                    break;
                case R.id.for8k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 8000; //default
                        samplerate_show = "8000\n";
                        textView4.setText("Select 8KHZ");
                    }
                    break;
                case R.id.for16k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 16000; //default
                        samplerate_show = "16000\n";
                        textView4.setText("Select 16KHZ");
                    }
                    break;
                case R.id.for44_1k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 44100; //default
                        samplerate_show = "44100\n";
                        textView4.setText("Select 44.1KHZ");
                    }
                    break;

                case R.id.downloadinaudio:
//                    downloadPost(url);
                    downloadComposed(url,roomnumtest);
                    break;
            }
        }
    };
    private void record() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
        Date now = new Date();

        System.out.println(now);

        File audio = new File(getExternalFilesDir("/").getAbsolutePath(), "Audio");
        if (!audio.exists()) {
            audio.mkdirs();
        }

        outputFile = getExternalFilesDir("/Audio").getAbsolutePath() + "/" + formatter.format(now) + ".3gp";
        //the file name
        outputFile_timeinfo = "audio_time_info" + formatter.format(now) + ".txt";
        //outputFile_timeinfo = getExternalFilesDir("/").getAbsolutePath() + "/" + formatter.format(now) + ".txt";
        thefilename = formatter.format(now) + ".3gp";

        try {
            File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Audio_time_info");
            if (!root.exists()) {
                root.mkdirs();
            }
            File filepath = new File(root, outputFile_timeinfo);
            info_path = filepath.getPath();
            time_imformation = new FileWriter(filepath);

            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + formatter.format(now) + ".3gp";
        //outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + formatter.format(now) + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setOutputFile(outputFile);
        //Toast.makeText(context, outputFile, Toast.LENGTH_SHORT).show();

        if(samplerate == 16000){
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioSamplingRate(samplerate);
        }
        else if(samplerate == 44100){
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioEncodingBitRate(384000);
            myAudioRecorder.setAudioSamplingRate(samplerate);
        }
        else{
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioSamplingRate(8000);

        }

        try {
            myAudioRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);

        try {
            Date now_start = new Date();
            see_time_start.setText(formatter_start.format(now_start));

            try {
                //time_imformation.append(sBody);
                time_imformation.write(formatter_start.format(now_start)+"\n");
                time_imformation.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            myAudioRecorder.start();

        } catch(IllegalStateException e){
            e.printStackTrace();
        }

        isRecording = true;
        textView4.setText(samplerate_show + "recording");
    }

    private void stopRecording() {
        audio_upload_txt.setText("Uploading..., please do not exit!!!");

        if(myAudioRecorder != null) {
            timer.stop();
            SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);
            try {
                myAudioRecorder.stop();
                Date now_end = new Date();
                see_time_end.setText(formatter_end.format(now_end));

                try {
                    time_imformation.write(formatter_end.format(now_end) + "\n");
                    time_imformation.flush();
                    time_imformation.close();

                    //hgttp
                    postRequest(url,outputFile,thefilename,info_path,outputFile_timeinfo);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IllegalStateException e) {
                // handle cleanup here
                e.printStackTrace();
            }
            //myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;
            textView4.setText("stop! SAVE IN " + outputFile);
        }
    }

    private void postRequest(String URL, String audio_file_path, String audio_filename,String audio_info_path, String info_filename) {

        OkHttpClient okHttpClient = new OkHttpClient();




        File f = new File(audio_file_path);
        File info = new File(audio_info_path);

        String mediaType = getMimeType(audio_file_path);
        String textType = getMimeType(audio_info_path);


        RequestBody requestBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("room_number",roomnumber)
                .addFormDataPart("audio", audio_filename, RequestBody.create(MediaType.parse(mediaType),f))
                .addFormDataPart("audio_info", info_filename , RequestBody.create(MediaType.parse(textType),info))
                .build();


        String audio_url = URL + "Audio_store";
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(audio_url)
                .build();

        //這邊是接收回傳(確認是否連線成功，不需要改動的部分)
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Audio_recorder.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(Audio_recorder.this, response.body().string(), Toast.LENGTH_LONG).show();
                            audio_upload_txt.setText("Upload Finished!!!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void downloadComposed(String url , String roomnum){

        File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Download");
                if (!root.exists()) {
                    root.mkdirs();
                }
        //File mdownload = new File(root, "Masterpice.mp4");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Download_Uri = Uri.parse(url + "Download" + roomnum);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //request.setAllowedOverRoaming(false);
        request.setTitle("Download Result");
        request.setDescription("Downloading your masterpice");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES,"Download.mp4");

        //String mediaType = getMimeType(audio_file_path);
        request.setMimeType("*/*");
        downloadManager.enqueue(request);

    }

    private String getMimeType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                //no permission than go to permission manager to change
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }
}