package com.example.crypton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecodeLSB extends Fragment {

    ImageView stegoImage;
    Button btnAdd,btnDecode;
    String currentPhotoPath;
    Uri imageUri;
    TextView txtMessage;

    Bitmap stegoBitmap = null;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;

    public DecodeLSB() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_decode_lsb,container,false);

        stegoImage = view.findViewById(R.id.imgStego);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnDecode = view.findViewById(R.id.btnDecode);
        txtMessage = view.findViewById(R.id.txtMessageExtracted);

        btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stegoImage.getDrawable() == null){
                    Toast.makeText(getActivity(), "Add image first.", Toast.LENGTH_SHORT).show();
                }else{
                    String extracted = decodeImg(stegoImage).trim();
                    extracted = extracted.replace("~_~", "");
                    if(extracted.equals("") || extracted == ""){
                        Toast.makeText(getActivity(), "Fail to generate secret message", Toast.LENGTH_SHORT).show();
                    }else{
                        txtMessage.setText(extracted);
                    }
                }
            }
        });

        stegoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_zoom_pict,null);
                PhotoView photoView = mView.findViewById(R.id.imgZoom);
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
                    photoView.setImageURI(imageUri);
                    mBuilder.setView(mView);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Image not add yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        return view;
    }

    private void addImage() {
        String[] options = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Mode Pengambilan Gambar");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0) {
                    askCameraPermissions();

                } else if (which == 1) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery,GALLERY_REQUEST_CODE);
                }
            }
        });
        builder.create().show();
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
                Toast.makeText(getActivity(), "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                stegoImage.setImageURI(Uri.fromFile(f));
                imageUri = Uri.fromFile(f);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "PNG_" + timestamp + "." + getFileExt(contentUri);
                stegoImage.setImageURI(contentUri);
                imageUri = contentUri;
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        //         File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
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

}