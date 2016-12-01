package com.meterview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MeterView mv_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mv_view = (MeterView) findViewById(R.id.mv_view);
        mv_view.setCurrentValue(701);
        mv_view.startAnimation();
    }
}
