package com.example.harpreet.dogclassification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageClassifier imageClassifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //senor orientation  bitmap
        try {
            imageClassifier = new ImageClassifier(this,Classifier.Device.CPU,1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        List<Classifier.Recognition> answer = imageClassifier.getResults(Icon);
        Toast.makeText(this, "Answer="+answer.get(0).getConfidence()+" "+answer.get(0).getTitle(), Toast.LENGTH_LONG).show();
    }
}
