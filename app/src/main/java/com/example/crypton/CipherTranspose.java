package com.example.crypton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CipherTranspose extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt, btnUpload, btnSave;
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
        btnUpload = findViewById(R.id.btnUpload);
        btnSave = findViewById(R.id.btnSave);

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cipherText = txtOutput.getText().toString();
                if(cipherText.isEmpty()) {
                    Toast.makeText(CipherTranspose.this, "Cipher belum digenerate..", Toast.LENGTH_SHORT).show();
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        //check permission
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            performSaveFile();
                        }else{
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE_CODE);
                        }
                    }else{
                        //OS dibawah marshmellow
                        performSaveFile();
                    }
                }
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //check permission
                    if(getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        performFileSearch();
                    }else{
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_CODE);
                    }
                }else{
                    //OS dibawah marshmellow
                    performFileSearch();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode == WRITE_EXTERNAL_STORAGE_CODE){
            //jika request batal, hasil array kosong
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                performSaveFile();
            }else{
                Toast.makeText(this,"Storage permission is required to save file",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == READ_EXTERNAL_STORAGE_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Storage permission is required to read file",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readText(String input){
        File file = new File(Environment.getExternalStorageDirectory(), input);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null){
                text.append(line);
            }
            br.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        return text.toString();
    }

    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":") + 1);
                if(path.contains("emulated")){
                    path = path.substring(path.indexOf("0") + 1);
                }
                Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
                txtInput.setText(readText(path));
            }
        }else if(requestCode == WRITE_REQUEST_CODE){
            if(resultCode == RESULT_OK){

                try{
                    Uri uri = data.getData();
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.write(txtOutput.getText().toString().getBytes());
                    outputStream.close();

                    Toast.makeText(this, "File berhasil disimpan", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(this, "File gagal disimpan", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "File gagal disimpan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performSaveFile(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String fileName = "Crypton_Transposition_" + timeStamp + ".txt";

        intent.putExtra(intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
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