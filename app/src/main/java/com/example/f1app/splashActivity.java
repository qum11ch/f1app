package com.example.f1app;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

public class splashActivity extends Activity {

    @Override
    public void onStart() {
        super.onStart();

        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(splashActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
        }, 2500);
    }
}