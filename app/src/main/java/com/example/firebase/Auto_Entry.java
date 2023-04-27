package com.example.firebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class Auto_Entry extends AppCompatActivity {

    Button button1;
    ImageView imgV;
    Uri uri1;
    TextView img_textview;
    FloatingActionButton fbtn;
    ActivityResultLauncher<Intent> activityResultLauncher;

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
//                String url = "https://357c-2401-4900-1af9-cd80-9c39-410f-f14b-9610.in.ngrok.io/";
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                activityResultLauncher.launch(uri1);
            }
        });


        imgV = findViewById(R.id.imgview);
        img_textview = findViewById(R.id.img_text);
        fbtn = findViewById(R.id.fbtn);

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Object mimeTypes;
//                ImagePicker.with(Auto_Entry.this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
                Intent capimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(capimg);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bundle extras = result.getData().getExtras();
//                Uri imageUri;
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(
                        imageBitmap,imageBitmap.getHeight(), imageBitmap.getWidth(), false).copy(
                        Bitmap.Config.RGB_565, true)
                );

                Bitmap bm = result1.get();
//                imageUri = saveImage(bm,HomePage.this);
//                img_textview.setText(""+imageUri);
                imgV.setImageBitmap(bm);

                InputImage fnimg = InputImage.fromBitmap(bm, 0);
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                Task<Text> result2 = recognizer.process(fnimg).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        StringBuilder result2 = new StringBuilder();
                        for(Text.TextBlock block: text.getTextBlocks()){
                            String blockText = block.getText();
                            Point[] blockcornerPoint = block.getCornerPoints();
                            Rect blockFrame = block.getBoundingBox();
                            for(Text.Line line: block.getLines()){
                                String lineText = line.getText();
                                Point[] lineCornerPoint = line.getCornerPoints();
                                Rect lineRect = line.getBoundingBox();
                                for(Text.Element element:line.getElements()){
                                    String elementText = element.getText();
                                    result2.append(elementText);
                                }
                                img_textview.setText(blockText);
                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Auto_Entry.this, "Fail to detect text from image.."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uri1 = data.getData();
        imgV.setImageURI(uri1);

    }

//    private Uri saveImage(Bitmap image, Context context) {
//        File imagesFolder = new File(context.getCacheDir(),"images");
//        Uri uri = null;
//        try{
//            imagesFolder.mkdirs();
//            File file = new File(imagesFolder,"captured_image.jpg");
//            FileOutputStream stream = new FileOutputStream(file);
//            image.compress(Bitmap.CompressFormat.JPEG,100,stream);
//            stream.flush();
//            stream.close();
//            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.bat"+".provider", file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return uri;
//
////        PyObject pyobj = py.getModule("eocr");
//
//    }




}