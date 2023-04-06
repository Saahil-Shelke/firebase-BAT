package com.example.firebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Auto_Entry extends AppCompatActivity {

    Button button1;
    ImageView imgV;
    FloatingActionButton fbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_entry);

        button1 = (Button) findViewById(R.id.add_tran);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Auto_Entry.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.submitapi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://357c-2401-4900-1af9-cd80-9c39-410f-f14b-9610.in.ngrok.io/";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });


        imgV = findViewById(R.id.imgview);
        fbtn = findViewById(R.id.fbtn);

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object mimeTypes;
                ImagePicker.with(Auto_Entry.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        imgV.setImageURI(uri);
    }



}