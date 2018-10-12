package com.mkpratt.simpleseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {//implements SimpleSeekbar.OnMySeekBarChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView seekbarValue = (TextView) findViewById(R.id.simpleSeekbarValue);

        SimpleSeekbar mySeekBar = new SimpleSeekbar(this, seekbarValue);

        FrameLayout layout = (FrameLayout) findViewById(R.id.seekbar_placeholder);
        layout.addView(mySeekBar);

        seekbarValue.setText(Integer.toString(mySeekBar.thumbValue));
    }
}

