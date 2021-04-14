package com.example.harpreet.dogclassification;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Camera extends AppCompatActivity {

    private ProgressDialog dialog;
    private ProgressBar bar;
    private TextView desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        bar = findViewById(R.id.progressBarML);
        desc = findViewById(R.id.desc);
        takePic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                doSomethingWithCroppedImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void doSomethingWithCroppedImage(Uri outputUri) {

        CircleImageView view = findViewById(R.id.dogImage);
        view.setImageURI(outputUri);
        callToModel(outputUri);
    }

    private void callToModel(Uri image) {

        dialog.setTitle("Image Processing");

        ImageClassifier imageClassifier;
         try{
             imageClassifier = new ImageClassifier(this,Classifier.Device.CPU,1);
             Bitmap icon = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
             List<Classifier.Recognition> answers = imageClassifier.getResults(icon);
             callAtWikipage(answers.get(0).getTitle()+" dog");
         }
         catch (Exception e){
             desc.setText("Error "+e.toString());
             //inc count of res place
             dialog.dismiss();
             bar.setVisibility(View.INVISIBLE);
         }


    }

    private void callAtWikipage(final String name) {

        dialog.setTitle("Fetching Data");
        dialog.setMessage("Please Wait............");
        AndroidNetworking.get("https://wikifun.herokuapp.com/info")
                .setPriority(Priority.MEDIUM)
                .addQueryParameter("location",name+" goa,India")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        desc.setText(name+" : "+response);
                        //inc count of res place
                        dialog.dismiss();
                        bar.setVisibility(View.INVISIBLE);

                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(Camera.this, error.toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        bar.setVisibility(View.INVISIBLE);
                    }
                });

    }


    private void takePic()
    {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Data...");
        dialog.setTitle("Image Uploading");
        dialog.show();
        bar.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Try Again!", Toast.LENGTH_LONG).show();
                //the toast message will appear for the deny for the first time but in below line the connection is established
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
    }

    public void checkimage(View view) {

        takePic();

    }


}