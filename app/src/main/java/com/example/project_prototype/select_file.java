package com.example.project_prototype;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class select_file extends AppCompatActivity {

    private Button audio_select;
    private Button video_select;

    private String patt;



    int requestcode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        audio_select = (Button) findViewById(R.id.audio_select);
        //video_select = (Button) findViewById(R.id.video_select);


        audio_select.setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            Intent intent1 = intent.setType("*/*");
            startActivityForResult(intent,1);
        });


    }



    @Override
    public void onActivityResult(int requestcode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestcode, resultCode, data);

        Context context = getApplicationContext();

        if(requestcode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String[] uripath = uri.getPath().split("raw");

            if(uri != null){
                try{
                    Toast.makeText(context,uripath[1],Toast.LENGTH_LONG).show();
                }
                catch(Exception message){
                    Toast.makeText(context,uri.getPath(),Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(context, "無效的檔案路徑!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(context, "取消選取檔案!", Toast.LENGTH_LONG).show();
        }
    }
}