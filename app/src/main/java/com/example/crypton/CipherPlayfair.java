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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CipherPlayfair extends AppCompatActivity {

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
        setContentView(R.layout.activity_cipher_playfair);

        Log.d("Test", removeChar("rivaldoaldo",'a'));
        Log.d("Test", removeChar(removeChar(removeDuplicate(generateKey("jalan ganesha sepuluh")),'j'),' '));
        Log.d("Test", cleanPlain("temui ibu nanti malam"));

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
                    key = txtKey.getText().toString();
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                Log.d("Test", encrypt(key,txtInput.getText().toString()));
                txtOutput.setText(addSeparator(encrypt(key,txtInput.getText().toString().toLowerCase()),' '));
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keys = txtKey.getText().toString();
                if(!keys.isEmpty()){
                    key = txtKey.getText().toString();
                }else{
                    Toast.makeText(getApplicationContext(), "Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

                Log.d("Test", encrypt(key,txtInput.getText().toString()));
                txtOutput.setText(addSeparator(decrypt(key,txtInput.getText().toString().toLowerCase()),' '));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cipherText = txtOutput.getText().toString();
                if(cipherText.isEmpty()) {
                    Toast.makeText(CipherPlayfair.this, "Cipher belum digenerate..", Toast.LENGTH_SHORT).show();
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
        String fileName = "Crypton_Playfair_" + timeStamp + ".txt";

        intent.putExtra(intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    String removeDuplicate(String input){

        char[] chars = input.toCharArray();
        Set<Character> present = new HashSet<>();
        int len = 0;
        for (char c : chars)
            if (present.add(c))
                chars[len++] = c;

        return (new String(chars, 0, len));
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


    String generateKey(String input){
        String alphabet = "abcdefghiklmnopqrstuvwxyz";
        input = input + alphabet;
        char[] chars = input.toCharArray();
        Set<Character> present = new HashSet<>();
        int len = 0;
        for (char c : chars)
            if (present.add(c))
                chars[len++] = c;

        return (new String(chars, 0, len));
    }

    char[][] matriksKey(String input){
        char key[][] = new char[5][5];
        int n=0;
            for(int i=0 ; i<5 ; i++){
                for(int j=0 ; j<5 ; j++ ){
                    key[i][j] = input.charAt(n);
                    n++;
                }
            }
        return key;
    }

    String cleanPlain(String input){
        input = removeChar(input.replaceAll("j","i"),' ');
        char[] chars = input.toCharArray();
        int enums = 1;

        String hasil = "";
        hasil += chars[0];

        for(int i=1 ; i<input.length(); i++){
            if(chars[i] == hasil.charAt(enums-1)){
                hasil += 'x';
                enums ++;
            }
            hasil += chars[i];
            enums++;
        }

        if(enums%2 == 1)
            hasil += 'x';

        return hasil;
    }

    int[] searchElement(char find, char[][] matrix){
        int[] indeks = new int[2];
        boolean state = false;

        for(int i=0 ;i<5 ;i++){
            for(int j=0;j<5 ;j++){
                if(find == matrix[i][j]) {
                    indeks[0] = i;
                    indeks[1] = j;
                    state = true;
                }
            }
        }
        return indeks;
    }

    String addSeparator(String input, char sep){
        char[] chars = input.toCharArray();
        String output ="";
        for(int i=0 ; i<input.length(); i++){
            output += chars[i];
            if(i%2==1){
                output += sep;
            }
        }
        return output;
    }


    String encrypt(String key, String input){
        input = cleanPlain(input);
        char[] chars = input.toCharArray();
        key = removeChar(removeChar(removeDuplicate(generateKey(key)),'j'),' ');
        char matriksKey[][] = matriksKey(key);

        Log.d("Key", key);
        Log.d("Plain", input);

        String hasil = "";
        int[] index1 = new int[2];
        int[] index2 = new int[2];

        for(int i=0 ; i<input.length() ; i+=2){
            index1 = searchElement(chars[i],matriksKey);
            index2 = searchElement(chars[i+1],matriksKey);

            if(index1[1] == index2[1]){
                if(index1[0] == 4) {
                    hasil += matriksKey[0][index1[1]];
                }else{
                    hasil+= matriksKey[index1[0]+1][index1[1]];
                }

                if(index2[0] == 4) {
                    hasil += matriksKey[0][index2[1]];
                }else{
                    hasil+= matriksKey[index2[0]+1][index2[1]];
                }
            }

            if(index1[0] == index2[0]){
                if(index1[1] == 4) {
                    hasil += matriksKey[index1[0]][0];
                }else{
                    hasil+= matriksKey[index1[0]][index1[1]+1];
                }

                if(index2[1] == 4) {
                    hasil += matriksKey[index2[0]][0];
                }else{
                    hasil+= matriksKey[index2[0]][index2[1]+1];
                }
            }

            if((index1[0] != index2[0])&&(index1[1] != index2[1])){
                if(index1[0] < index2[0]){
                    if(index1[1]<index2[1]){
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }else{
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }
                }else{
                    if(index1[1]<index2[1]){
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }else{
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }
                }
            }
        }

        return hasil.toUpperCase();
    }

    String decrypt(String key, String input){
        input = cleanPlain(input);
        char[] chars = input.toCharArray();
        key = removeChar(removeChar(removeDuplicate(generateKey(key)),'j'),' ');
        char matriksKey[][] = matriksKey(key);

        Log.d("Key", key);
        Log.d("Plain", input);

        String hasil = "";
        int[] index1 = new int[2];
        int[] index2 = new int[2];

        for(int i=0 ; i<input.length() ; i+=2){
            index1 = searchElement(chars[i],matriksKey);
            index2 = searchElement(chars[i+1],matriksKey);

            if(index1[1] == index2[1]){
                if(index1[0] == 0) {
                    hasil += matriksKey[4][index1[1]];
                }else{
                    hasil+= matriksKey[index1[0]-1][index1[1]];
                }

                if(index2[0] == 0) {
                    hasil += matriksKey[4][index2[1]];
                }else{
                    hasil+= matriksKey[index2[0]-1][index2[1]];
                }
            }

            if(index1[0] == index2[0]){
                if(index1[1] == 0) {
                    hasil += matriksKey[index1[0]][4];
                }else{
                    hasil+= matriksKey[index1[0]][index1[1]-1];
                }

                if(index2[1] == 0) {
                    hasil += matriksKey[index2[0]][4];
                }else{
                    hasil+= matriksKey[index2[0]][index2[1]-1];
                }
            }

            if((index1[0] != index2[0])&&(index1[1] != index2[1])){
                if(index1[0] > index2[0]){
                    if(index1[1]<index2[1]){
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }else{
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }
                }else{
                    if(index1[1]<index2[1]){
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }else{
                        hasil += matriksKey[index1[0]][index2[1]];
                        hasil += matriksKey[index2[0]][index1[1]];
                    }
                }
            }
        }

        return removeChar(hasil,'x').toUpperCase();
    }
}