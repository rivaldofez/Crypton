  package com.example.crypton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

  public class SteganographyLSB extends AppCompatActivity {
    ImageView selectedImage, stegoImage;
    String currentPhotoPath;
    Button btnCamera, btnGallery, btnEncode, btnDecode;
    TextView txtSecret;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steganography_lsb);

//        int a=10;
//        char b=(char)a;
//        Log.d("Hasil", " "+b);

        selectedImage = findViewById(R.id.imgCover);
        stegoImage = findViewById(R.id.imgStego);
        btnEncode = findViewById(R.id.btnEncode);
        btnDecode = findViewById(R.id.btnDecode);
        btnCamera = findViewById(R.id.btnCamera);
        txtSecret = findViewById(R.id.txtSecret);
        btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,GALLERY_REQUEST_CODE);
            }
        });

        btnEncode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                encodeImg(selectedImage, "Rivaldo Fernandes~_~");
            }
        });

        btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               txtSecret.setText(decodeImg(stegoImage));
            }
        });
    }

      private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else{
            dispatchTakePictureIntent();
        }
      }

      @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          if(requestCode == CAMERA_PERMISSION_CODE){
              if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  dispatchTakePictureIntent();
              }else{
                  Toast.makeText(this, "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
              }
          }
      }

//      private void openCamera() {
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//      }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == CAMERA_REQUEST_CODE) {
//              Bitmap image = (Bitmap) data.getExtras().get("data");
//              selectedImage.setImageBitmap(image);

              if(resultCode == Activity.RESULT_OK){
                  File f = new File(currentPhotoPath);
                  selectedImage.setImageURI(Uri.fromFile(f));

                  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                   Uri contentUri = Uri.fromFile(f);
                  mediaScanIntent.setData(contentUri);
                  this.sendBroadcast(mediaScanIntent);
              }
          }

          if (requestCode == GALLERY_REQUEST_CODE) {
//              Bitmap image = (Bitmap) data.getExtras().get("data");
//              selectedImage.setImageBitmap(image);

              if(resultCode == Activity.RESULT_OK){
                  Uri contentUri = data.getData();
                  String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                  String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                  selectedImage.setImageURI(contentUri);
              }
          }
      }

      private String getFileExt(Uri contentUri) {
          ContentResolver c = getContentResolver();
          MimeTypeMap mime = MimeTypeMap.getSingleton();
          return mime.getExtensionFromMimeType(c.getType(contentUri));
      }

      private File createImageFile() throws IOException {
          // Create an image file name
          String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
          String imageFileName = "JPEG_" + timeStamp + "_";
 //         File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
          File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
          File image = File.createTempFile(
                  imageFileName,  /* prefix */
                  ".bmp",         /* suffix */
                  storageDir      /* directory */
          );

          // Save a file: path for use with ACTION_VIEW intents
          currentPhotoPath = image.getAbsolutePath();
          return image;
      }

      private void dispatchTakePictureIntent() {
          Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          // Ensure that there's a camera activity to handle the intent
          if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
              // Create the File where the photo should go
              File photoFile = null;
              try {
                  photoFile = createImageFile();
              } catch (IOException ex) {

              }
              // Continue only if the File was successfully created
              if (photoFile != null) {
                  Uri photoURI = FileProvider.getUriForFile(this,
                          "com.example.android.fileprovider",
                          photoFile);
                  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                  startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
              }
          }
      }

      public void encodeImg(ImageView imgEncrypt, String inputMessage){
          char[] message = toBinary(inputMessage).toCharArray();

          Bitmap bmap;
          BitmapDrawable bmapD = (BitmapDrawable)imgEncrypt.getDrawable();
          bmap = bmapD.getBitmap();

          Bitmap operation = Bitmap.createBitmap(bmap.getWidth(),bmap.getHeight(),bmap.getConfig());

          Integer idMessage = 0;
          for(int i=0; i<bmap.getWidth(); i++){
              for(int j=0; j<bmap.getHeight();j++){

                  Integer p = bmap.getPixel(i, j);
                  Integer r = Color.red(p);
                  Integer g = Color.green(p);
                  Integer b = Color.blue(p);
                  Integer alpha = Color.alpha(p);

                  for(int k = 0; k < 3; k++){
                      if(idMessage < message.length){
                          if(k == 0){
                              if((r % 2 == 0 && message[idMessage] == '1') || (r % 2 == 1 && message[idMessage] == '0')){
                                  r = r ^ 1;
                              }
                          }else if(k == 1){
                              if((g % 2 == 0 && message[idMessage] == '1') || (g % 2 == 1 && message[idMessage] == '0')){
                                  g = g ^ 1;
                              }
                          }else if(k==2) {
                              if((b % 2 == 0 && message[idMessage] == '1') || (b % 2 == 1 && message[idMessage] == '0')){
                                  b = b ^ 1;
                              }
                          }
                      }
                      idMessage++;
                  }
                  operation.setPixel(i, j, Color.argb(alpha, r, g, b));
              }
          }
          Log.d("Test","Okeee");
          stegoImage.setImageBitmap(operation);
      }

      public String decodeImg(ImageView imgEncrypt){
          String message = "";
          String hasil ="";

          Bitmap bmap;
          BitmapDrawable bmapD = (BitmapDrawable)imgEncrypt.getDrawable();
          bmap = bmapD.getBitmap();

          Integer idMessage = 1;
          int idsearch = 0;
          for(int i=0; i<bmap.getWidth(); i++){
              for(int j=0; j<bmap.getHeight();j++){

                  Integer p = bmap.getPixel(i, j);
                  Integer r = Color.red(p);
                  Integer g = Color.green(p);
                  Integer b = Color.blue(p);
                  Integer alpha = Color.alpha(p);


                  for(int k = 0; k < 3; k++){
                        if(k == 0){
                            if(r % 2 == 0){
                                message = message + '0';
                            }else {
                                message = message + '1';
                            }
                        }else if(k == 1){
                            if(g % 2 == 0){
                                message = message + '0';
                            }else {
                                message = message + '1';
                            }
                        }else if(k == 2){
                            if(b % 2 == 0){
                                message = message + '0';
                            }else {
                                message = message + '1';
                            }
                        }

                        if(idMessage == 8){
                            int num = Integer.parseInt(message,2);

                            if(num >= 32 && num<127){
                                char letter = (char)num;
                                Log.d("Num", ""+letter);

                                if(letter == '~'){
                                    idsearch ++;
                                }else if(letter == '_' && idsearch == 1){
                                    idsearch++;
                                }else{
                                    idsearch = 0;
                                }

                                hasil = hasil + letter;

                                if(idsearch == 3){
                                    i = bmap.getWidth();
                                    j = bmap.getHeight();
                                    k = 3;
                                    Log.d("Test", "Ketemu");
                                }
                            }
                            message="";
                            idMessage = 0;
                        }
                        idMessage++;
                  }
              }
          }
          return hasil;
      }

      public String toBinary(String input){
        String hasil="";
        char[] inputChar = input.toCharArray();
        for(int i = 0; i< inputChar.length; i++){
            hasil += String.format("%1$8s", Integer.toBinaryString( ((int)inputChar[i]) )).replace(' ','0');
        }
        return hasil;
      }
  }