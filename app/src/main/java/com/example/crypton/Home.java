package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnExit = findViewById(R.id.btnExit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void Cipher(View view) {
        startActivity(new Intent(Home.this, CipherText.class));
    }

    public void Transposisi(View view) {
        startActivity(new Intent(Home.this, CipherTranspose.class));
    }

    public void Super(View view) {
        startActivity(new Intent(Home.this, CipherSuper.class));
    }

    public void Vigenere(View view) {
        startActivity(new Intent(Home.this, CipherVigenere.class));
    }

    public void Playfair(View view) {
        startActivity(new Intent(Home.this, CipherPlayfair.class));
    }

    public void SteganographyLSB(View view) {
        startActivity(new Intent(Home.this, SteganographyLSB.class));
    }
}