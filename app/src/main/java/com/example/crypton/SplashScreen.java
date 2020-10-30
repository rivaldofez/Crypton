package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = findViewById(R.id.imgLogo);

        //inisiasi view
        final ProgressBar progressBar = findViewById(R.id.progressSplash);
        progressBar.setVisibility(View.VISIBLE);

        setContentView(R.layout.activity_splash_screen);
        //waktu splash screen
        int splashscreen_time = 3500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setelah waktu splashscreen habis, maka langsung berpindah ke Halaman Login
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(SplashScreen.this, Home.class));
                finish();
            }
        }, splashscreen_time);

    }
}