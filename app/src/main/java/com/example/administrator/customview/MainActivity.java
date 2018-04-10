package com.example.administrator.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "自定义开关：";
    private SwitchView switchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchView = findViewById(R.id.switchView);
        switchView.setSwitchBackground(R.drawable.switch_background);
        switchView.setSwitchSrc(R.drawable.slide_button);
        switchView.setSwitchOpen(true);
        switchView.setOnSwitchUpdateListener(new SwitchView.OnSwitchUpdateListener() {
            @Override
            public void onSwitchUpdateListener(boolean state) {
                Log.e(TAG,state+"---->");
            }
        });
    }
}
