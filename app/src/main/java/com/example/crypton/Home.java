package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    ConstraintLayout containerCipher, containerTranspose, containerSuper, containerVigenere, containerPlayfair;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        containerCipher = findViewById(R.id.containerCipher);
        containerSuper = findViewById(R.id.containerSuper);
        containerTranspose = findViewById(R.id.containerTranspose);
        containerVigenere = findViewById(R.id.containerVigenere);
        containerPlayfair = findViewById(R.id.containerPlayfair);
        btnExit = findViewById(R.id.btnExit);

        containerCipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherText.class));
            }
        });

        containerTranspose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherTranspose.class));
            }
        });

        containerSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherSuper.class));
            }
        });

        containerVigenere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherVigenere.class));
            }
        });


        containerPlayfair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CipherPlayfair.class));
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}