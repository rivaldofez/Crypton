package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CipherText extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput;
    Button btnEncrypt, btnDecrypt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_text);

        txtOutput = findViewById(R.id.txtOutput);
        txtInput = findViewById(R.id.etInput);
        btnDecrypt = findViewById(R.id.btnDecrypt);
        btnEncrypt = findViewById(R.id.btnEncrypt);

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtOutput.setText(encrypt(256,txtInput.getText().toString()));
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtOutput.setText(decrypt(256,txtInput.getText().toString()));
            }
        });
    }

    String encrypt(int key, String input){
        char[] chars = input.toCharArray();
        key = key % 255;

        String output = "";

        for(char c : chars){
            c += key;
            output = output + c;
        }

        return output;
    }

    String decrypt(int key, String input){
        char[] chars = input.toCharArray();
        key = key % 255;

        String output = "";

        for(char c : chars){
            c -= key;
            output = output + c;
        }

        return output;
    }
}