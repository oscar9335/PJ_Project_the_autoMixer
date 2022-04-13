package com.example.project_prototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class roomHolderSetting extends Fragment implements View.OnClickListener{
    private NavController navController;

    private TextView roominfo;
    private TextView setting_view;
    private Button for8k;
    private Button for16k;
    private Button for44k;
    private Button confirmbt;

    private String roomnumber = "****";
    private String audioFramerate = "44100";

//    private String url = "http://" + "192.168.1.101" + ":" + 5000 + "/";
    private String url = "http://" + "140.116.82.135" + ":" + 5000 + "/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_holder_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);


        for8k = view.findViewById(R.id.for8k);
        for16k = view.findViewById(R.id.for16k);
        for44k = view.findViewById(R.id.for44_1k);
        confirmbt = view.findViewById(R.id.sendsetting); //confirm


        roominfo = view.findViewById(R.id.roominfo);
        //roominfo.setText("get form intent");

        setting_view = view.findViewById(R.id.settingView);

        for8k.setOnClickListener(this);
        for16k.setOnClickListener(this);
        for44k.setOnClickListener(this);
        confirmbt.setOnClickListener(this);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("passtosetting", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                roomnumber = bundle.getString("sendtosetting");
                // Do something with the result...
                roominfo.setText(" Setting Room: " + roomnumber);
                System.out.println("This is the room number :" + roomnumber);
            }
        });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.for8k:
                setting_view.setText("You have set the Audio frameRate to:\n    8000 HZ");
                for8k.setTextColor(0xFF9C1111);
                for16k.setTextColor(0xFF000000);
                for44k.setTextColor(0xFF000000);
                audioFramerate = "8000";
                break;
            case R.id.for16k:
                setting_view.setText("You have set the Audio frameRate to:\n    16000 HZ");
                for16k.setTextColor(0xFF9C1111);
                for8k.setTextColor(0xFF000000);
                for44k.setTextColor(0xFF000000);
                audioFramerate = "16000";
                break;
            case R.id.for44_1k:
                setting_view.setText("You have set the Audio frameRate to:\n    44100 HZ");
                for44k.setTextColor(0xFF9C1111);
                for16k.setTextColor(0xFF000000);
                for8k.setTextColor(0xFF000000);
                audioFramerate = "44100";
                break;
            case R.id.sendsetting:

                postRequest(url, audioFramerate, roomnumber);
                // make it enable until response onfailure
                confirmbt.setEnabled(false);

                break;

            }
        }

    private void postRequest(String URL, String audioframerate, String roomnumber) {

        OkHttpClient okHttpClient = new OkHttpClient();

        String setting_URL = URL + "Setting";

        //if needed setting video resoultion is also there
        RequestBody formBody = new FormBody.Builder()
                .add("audioSetting", audioframerate)
                .add("roomnumbersetting", roomnumber)
                .build();


        Request request = new Request.Builder()
                .url(setting_URL)
                .post(formBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Please try it again.", Toast.LENGTH_SHORT).show();
                        confirmbt.setEnabled(false);
                        call.cancel();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //get the response to check to navigate
                            String ok = response.body().string().substring(0,3);
                            if(ok.equals("SUC")) {
                                //intent send data to home_page fragmnet

                                //send data
                                Bundle result = new Bundle();
                                result.putString("bundleKey", roomnumber);
                                result.putString("idKey", "Host");
                                getParentFragmentManager().setFragmentResult("passroomnumber", result);

                                navController.navigate(R.id.action_roomHolderSetting_to_home_page);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

}

