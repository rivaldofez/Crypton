package com.example.crypton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CipherText extends AppCompatActivity {

    TextView txtOutput;
    EditText txtInput, txtKey;
    Button btnEncrypt, btnDecrypt, btnUpload, btnSave;
    Integer key;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher_text);

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
                    Toast.makeText(CipherText.this, "Cipher belum digenerate..", Toast.LENGTH_SHORT).show();
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        //check permission
                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                            //show pop up untuk permission
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        }else{
                            //permission sudah diizinkan
                            saveToTxtFile(cipherText);
                        }
                    }else{
                        //OS dibawah marshmellow
                        saveToTxtFile(cipherText);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE: {
                //jika request batal, hasil array kosong
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission diizinkan, save ke txt
                    saveToTxtFile(txtOutput.getText().toString());
                }else{
                    Toast.makeText(this,"Storage permission is required to save file",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveToTxtFile(String cipherText) {
        //get current time untuk penamaan
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        try {
            //path tp storage
            File path = Environment.getExternalStorageDirectory();

            //create folder
            File dir = new File(path + "/Crypton/");
            dir.mkdirs();

            //filename
            String fileName = "Crypton_CipherText_" + timeStamp + ".txt";

            File file = new File(dir, fileName);

            //File writer untuk membentuk karakter kedalam file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(cipherText);
            bw.close();

            Toast.makeText(this, fileName+ "is saved to\n" +dir , Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            //jika gagal
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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