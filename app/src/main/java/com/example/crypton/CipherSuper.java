package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CipherSuper extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt;
    Integer key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_super);
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
                    key = (Integer.parseInt(txtKey.getText().toString())) % 255;
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                txtOutput.setText(encryptCipher(key,encryptTranspose(key,txtInput.getText().toString())));
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keys = txtKey.getText().toString();
                if(!keys.isEmpty()){
                    key = (Integer.parseInt(txtKey.getText().toString())) % 255;
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                txtOutput.setText(decryptTranspose(key,decryptCipher(key,txtInput.getText().toString())));
            }
        });
    }

    String encryptTranspose(int key, String input){
        char[] chars = input.toCharArray();

        int num_row = 0;
        int id_plain = 0;
        if(input.length() % key == 0){
            num_row = input.length() / key;
        }else{
            num_row = (input.length() / key) + 1;
        }

        char plain[][] = new char[num_row][key];
        for(int i=0 ; i<num_row ; i++){
            for(int j=0 ; j<key ; j++){
                if(id_plain < input.length()){
                    plain[i][j] = chars[id_plain];
                    id_plain++;
                }else{
                    plain[i][j] = '.';
                }
            }
        }

        String output = "";
        for(int i=0 ; i<key ; i++){
            for(int j=0 ; j<num_row ; j++){
                output = output + plain[j][i];
            }
        }

        return output;
    }

    String decryptTranspose(int key, String input){
        char[] chars = input.toCharArray();
        key = input.length() / key;
        int num_row = 0;
        int id_plain = 0;
        if(input.length() % key == 0){
            num_row = input.length() / key;
        }else{
            num_row = (input.length() / key) + 1;
        }

        char plain[][] = new char[num_row][key];
        for(int i=0 ; i<num_row ; i++){
            for(int j=0 ; j<key ; j++){
                if(id_plain < input.length()){
                    plain[i][j] = chars[id_plain];
                    id_plain++;
                }else{
                    plain[i][j] = '.';
                }
            }
        }

        String output = "";
        for(int i=0 ; i<key ; i++){
            for(int j=0 ; j<num_row ; j++){
                output = output + plain[j][i];
            }
        }

        return output;
    }

    String encryptCipher(int key, String input){
        char[] chars = input.toCharArray();

        String output = "";

        for(char c : chars){
            c += key;
            output = output + c;
        }

        return output;
    }

    String decryptCipher(int key, String input){
        char[] chars = input.toCharArray();

        String output = "";

        for(char c : chars){
            c -= key;
            output = output + c;
        }

        return output;
    }
}