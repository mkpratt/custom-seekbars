package com.mkpratt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mkpratt.generalseekbar.GeneralSeekbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainLayout = findViewById(R.id.seekbarValues);

        // Generalized Slider with API for customized values
        // Started to add functionality for thumb width and padding. It mostly works, just not bulletproof. 50f as the thumbWidth is best.
        GeneralSeekbar mySeekBar = new GeneralSeekbar(this, mainLayout, 75, 3, 50f, 10f, 10, 90);

        FrameLayout layout = findViewById(R.id.seekbar_placeholder);
        layout.addView(mySeekBar);
    }
}

