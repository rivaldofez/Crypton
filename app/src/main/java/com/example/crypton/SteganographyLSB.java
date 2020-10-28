  package com.example.crypton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

  public class SteganographyLSB extends AppCompatActivity {
    ImageView selectedImage;
    String currentPhotoPath;
    Button btnCamera, btnGallery;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steganography_lsb);

        selectedImage = findViewById(R.id.imgCover);
        btnCamera = findViewById(R.id.btnCamera);
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
                Toast.makeText(SteganographyLSB.this, "Button Gallery pressed", Toast.LENGTH_SHORT).show();
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

  }