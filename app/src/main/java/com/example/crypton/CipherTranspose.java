package com.example.crypton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CipherTranspose extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt;
    Integer key;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_CODE = 2;
    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_transpose);
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

        int num_row = 0;
        int id_plain = 0;
        if(input.length() % key == 0){
            num_row = input.length() / key;
            Log.d("Row", Integer.toString(num_row));
            Log.d("Input Length", Integer.toString(input.length()));
            Log.d("Key", Integer.toString(key));
        }else{
            num_row = (input.length() / key) + 1;
            Log.d("Row", Integer.toString(num_row));
            Log.d("Input Length", Integer.toString(input.length()));
            Log.d("Key", Integer.toString(key));
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

    String decrypt(int key, String input){
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
}