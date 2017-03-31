package com.androidcycle.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class ScrollTextActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollTextView scrollTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_my_view);
        scrollTv = (ScrollTextView) findViewById(R.id.scroll_tv);
        final ArrayList<String[]> strs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String[] strings = new String[2];
            strings[0] = i + "-HaHaHaHa";
            strings[1] = i + "-BaLaBaLa";
            strs.add(strings);
        }
        scrollTv.setScrollText(strs);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.start:

                scrollTv.startAnim();
                break;
            case R.id.stop:
                scrollTv.stopAnim();
                break;
            default:
                break;
        }
    }
}
