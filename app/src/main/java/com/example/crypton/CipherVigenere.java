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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CipherVigenere extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt, btnUpload, btnSave;
    String key;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_CODE = 2;
    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_vigenere);

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
                String key = removeChar(generateKey(txtInput.getText().toString(), txtKey.getText().toString().toUpperCase()),' ');
                if(!key.isEmpty()){
                    txtOutput.setText(removeChar(encrypt(txtInput.getText().toString().toUpperCase(), key),' '));
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = removeChar(generateKey(txtInput.getText().toString(), txtKey.getText().toString().toUpperCase()),' ');
                if(!key.isEmpty()){
                    txtOutput.setText(removeChar(decrypt(txtInput.getText().toString().toUpperCase(), key),' '));
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cipherText = txtOutput.getText().toString();
                if(cipherText.isEmpty()) {
                    Toast.makeText(CipherVigenere.this, "Cipher belum digenerate..", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
        String fileName = "Crypton_Vigenere_" + timeStamp + ".txt";

        intent.putExtra(intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    String removeChar(String input, char c) {
        if (input == null || input.length() <= 1)
            return input;
        char[] inputArray = input.toCharArray();
        char[] outputArray = new char[inputArray.length];
        int outputArrayIndex = 0;
        for (int i = 0; i < inputArray.length; i++) {
            char p = inputArray[i];
            if (p != c) {
                outputArray[outputArrayIndex] = p;
                outputArrayIndex++;
            }
        }
        return new String(outputArray, 0, outputArrayIndex);
    }

    String generateKey(String input, String key)
    {
        key = removeChar(key,' ');
        int x = input.length();
        for (int i = 0; ; i++)
        {
            if (x == i)
                i = 0;
            if (key.length() == input.length())
                break;
            key+=(key.charAt(i));
        }
        return key;
    }

    String encrypt(String input, String key)
    {
        String hasil = "";
        for(int i = 0; i < input.length(); i++) {
            int x = (input.charAt(i) + key.charAt(i)) %26;

            x += 'A';
            hasil += (char)(x);
        }
        return hasil;
    }

    String decrypt(String input, String key)
    {
        String hasil = "";

        for(int i = 0 ; i < input.length() && i < key.length(); i++) {
            int x = (input.charAt(i) - key.charAt(i) + 26) %26;

            x += 'A';
            hasil += (char)(x);
        }
        return hasil;
    }

}