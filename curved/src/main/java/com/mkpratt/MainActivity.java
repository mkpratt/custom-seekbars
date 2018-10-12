package com.mkpratt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mkpratt.curvedseekbar.CurvedSeekbar;
import com.mkpratt.generalseekbar.GeneralSeekbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainLayout = findViewById(R.id.seekbarValues);

        // Curved Slider with API for customized values
        CurvedSeekbar mySeekBar = new CurvedSeekbar(this, mainLayout, 100, 2, 50f, 10f, 20, 280);

        FrameLayout layout = findViewById(R.id.seekbar_placeholder);
        layout.addView(mySeekBar);
    }
}

