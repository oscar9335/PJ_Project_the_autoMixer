package com.example.project_prototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Room extends Fragment implements View.OnClickListener ,View.OnKeyListener{

    private NavController navController;

    private EditText roomenter;
    private Button create;
    private Button join;

    private TextView roomreport;

    private String room_number = null;
    private String enter = null;
    private String ok;

    //http
    private String url = "http://" + "192.168.1.101" + ":" + 5000 + "/";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        create = view.findViewById(R.id.create);
        join = view.findViewById(R.id.join);
        create.setOnClickListener(this);
        join.setOnClickListener(this);

        roomenter = view.findViewById(R.id.roomenter);
        roomenter.setOnKeyListener(this);

        roomreport = view.findViewById(R.id.roomreport);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create:
                if(room_number != null) {
                    RequestBody formBody = new FormBody.Builder()
                            .add("action", "CREATE")
                            .add("room_number", room_number)
                            .build();
                    roomPost(url,room_number,formBody);
                }
                else{
                    roomreport.setText("Please enter room number");
                }
                break;
            case R.id.join:
                if(room_number != null) {
                    RequestBody formBody = new FormBody.Builder()
                            .add("action", "JOIN")
                            .add("room_number", room_number)
                            .build();
                    roomPost(url,room_number,formBody);
                }
                else{
                    roomreport.setText("Please enter room number");
                }
                break;
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        switch (keyEvent.getAction()) {
            case KeyEvent.ACTION_UP:
                String number = roomenter.getText().toString();
                room_number = number;
                break;
            case KeyEvent.ACTION_DOWN:
                break;

        }
        return false;
    }

    private void roomPost(String URL, String room_number, RequestBody formBody){

        OkHttpClient okHttpClient = new OkHttpClient();

        String room_URL = URL + "Room";

        Request request = new Request.Builder()
                .url(room_URL)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                roomreport.setText("Something went wrong: "+ e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    enter = response.body().string();
                    ok = enter.substring(0,2);
                    roomreport.setText(enter);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //roomreport.setText("IN THE RUN ON UI THREAD");
                            //System.out.println(ok);
                            if(ok.equals("Ok") || ok.equals("Ye")) {
                                navController.navigate(R.id.action_room_to_home_page);

                                //send data
                                Bundle result = new Bundle();
                                result.putString("bundleKey", room_number);
                                getParentFragmentManager().setFragmentResult("passroomnumber", result);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}