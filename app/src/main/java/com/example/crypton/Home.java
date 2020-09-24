package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    ConstraintLayout containerCipher, containerTranspose, containerSuper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        containerCipher = findViewById(R.id.containerCipher);
        containerSuper = findViewById(R.id.containerSuper);
        containerTranspose = findViewById(R.id.containerTranspose);

        containerCipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherText.class));
                finish();
            }
        });

        containerTranspose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherTranspose.class));
                finish();
            }
        });

        containerSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherSuper.class));
                finish();
            }
        });
    }
}