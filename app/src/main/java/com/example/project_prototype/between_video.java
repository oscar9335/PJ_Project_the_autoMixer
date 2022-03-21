package com.example.project_prototype;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class between_video extends Fragment implements View.OnClickListener{

    //private NavController navController;

    private Button upload;
    public TextView file_name;
    public TextView upload_status;

    private String video_path = null;
    private String video_name = null;
    private String video_info_path = null;
    private String video_info_name = null;

    //OKhttp URL setting
    private String url = "http://" + "192.168.1.101" + ":" + 5000 + "/";

    private boolean success = false;


    private Button download;
    private String roomnumtest = "1";
    private Uri Download_Uri;

    private String roomnumber;
    private TextView roomcontainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        video_path = getArguments().getString("video_path");
        video_name = getArguments().getString("video_name");
        video_info_path = getArguments().getString("video_info_path");
        video_info_name = getArguments().getString("video_info_name");
        roomnumber = getArguments().getString("the_room_number");


        return inflater.inflate(R.layout.fragment_between_video, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //navController = Navigation.findNavController(view);

        upload = view.findViewById(R.id.upload_bt);
        upload.setOnClickListener(this);

        upload_status = view.findViewById(R.id.file_upload_status);
        file_name = view.findViewById(R.id.video_file_name);


        file_name.setText("Video: " + video_name);
        upload_status.setText("Please Click UPLOAD button");

        download = view.findViewById(R.id.downloadinvideo);
        download.setOnClickListener(this);

        roomcontainer = view.findViewById(R.id.room_container_inbetween);
        roomcontainer.setText(roomnumber);



    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_bt:
                upload_status.setText("Uploading..., please wait.");
                postRequest(url,video_path,video_name,video_info_path,video_info_name);
                break;

            case R.id.downloadinvideo:
                downloadComposed(url,roomnumtest);
                break;
        }

    }

    private void postRequest(String URL, String video_path, String video_name,String video_info_path, String video_info_name) {

        OkHttpClient okHttpClient = new OkHttpClient();


        File f = new File(video_path);
        File info = new File(video_info_path);

        String mediaType = getMimeType(video_path);
        String textType = getMimeType(video_info_path);


        RequestBody requestBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("room_number",roomnumber)
                .addFormDataPart("video", video_name, RequestBody.create(MediaType.parse(mediaType),f))
                .addFormDataPart("video_info", video_info_name , RequestBody.create(MediaType.parse(textType),info))
                .build();


        String audio_url = URL + "Video_store";
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(audio_url)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                upload_status.setText(e.getMessage());
//                upload_success = false;
                call.cancel();
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        upload_status.setText(e.getMessage());
//                        //Toast.makeText(between_video.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        call.cancel();
//                    }
//                };
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    upload_status.setText(response.body().string());
//                    between_video.upload_success = true;
                    //upload.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        //Toast.makeText(Audio_recorder.this, response.body().string(), Toast.LENGTH_LONG).show();
//                        try {
//                            upload_status.setText(response.body().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
            }
        });
        success = true;
    }

    private void downloadComposed(String url , String roomnum){

//        File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Download");
//        if (!root.exists()) {
//            root.mkdirs();
//        }
        //File mdownload = new File(root, "Masterpice.mp4");

        DownloadManager downloadManager = (DownloadManager) getActivity().getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);
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



}