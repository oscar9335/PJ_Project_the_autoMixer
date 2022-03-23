package com.example.project_prototype;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class home_page extends Fragment implements View.OnClickListener{

    private NavController navController;  // for the use of

    private ImageButton audio_mode_button;
    private ImageButton video_mode_button;
    private Button downloadbt;

    private TextView roomcontainer;

    //this is the Room number , needed to pass to the Audio can Camera activity
    private String roomnumber;

    private Uri Download_Uri;

    private String url = "http://" + "192.168.1.101" + ":" + 5000 + "/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //receive data(room number) from Room
        getParentFragmentManager().setFragmentResultListener("passroomnumber", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                roomnumber = bundle.getString("bundleKey");
                // Do something with the result...
                roomcontainer.setText("Room: " + roomnumber);
                System.out.println("This is the room number :" + roomnumber);
            }
        });

    }


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
        downloadbt =  view.findViewById(R.id.downloadbt);

        audio_mode_button.setOnClickListener(this);
        video_mode_button.setOnClickListener(this);
        downloadbt.setOnClickListener(this);

        roomcontainer = view.findViewById(R.id.roomcontainer);





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio_mode:

                //debug
                System.out.println("CLick room: " + roomnumber);

                Bundle bundle_audio = new Bundle();
                bundle_audio.putString("pass_room_number_toaudio", roomnumber);
                navController.navigate(R.id.action_home_page_to_audio_recorder,bundle_audio);

                break;
            case R.id.video_mode:

                Bundle bundle_video = new Bundle();
                bundle_video.putString("pass_room_number_tovideo", roomnumber);
                navController.navigate(R.id.action_home_page_to_cameraActivity,bundle_video);
                break;

            case R.id.downloadbt:
                downloadComposed(url , roomnumber);
                break;

        }
    }


    private void downloadComposed(String url , String roomnum){

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
}