package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CipherText extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt;
    Integer key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_text);

        txtOutput = findViewById(R.id.txtOutput);
        txtInput = findViewById(R.id.etInput);
        txtKey = findViewById(R.id.etKey);
        btnDecrypt = findViewById(R.id.btnDecrypt);
        btnEncrypt = findViewById(R.id.btnEncrypt);

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keys = txtKey.getText().toString();
                if(!keys.isEmpty()){
                    key = Integer.parseInt(txtKey.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                txtOutput.setText(encrypt(key,txtInput.getText().toString()));
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keys = txtKey.getText().toString();
                if(!keys.isEmpty()){
                    key = Integer.parseInt(txtKey.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                txtOutput.setText(decrypt(key,txtInput.getText().toString()));
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