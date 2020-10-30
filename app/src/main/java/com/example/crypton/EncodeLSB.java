package com.example.crypton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncodeLSB extends Fragment {

    ImageView coverImage, stegoImage;
    Button btnAdd,btnSave,btnEncode;
    String currentPhotoPath;
    Uri imageUri;
    EditText etMessage;

    Bitmap stegoBitmap = null;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;

    public EncodeLSB() {
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
        View view = inflater.inflate(R.layout.fragment_encode_lsb,container,false);

        coverImage = view.findViewById(R.id.imgCover);
        stegoImage = view.findViewById(R.id.imgStego);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnSave = view.findViewById(R.id.btnSave);
        btnEncode = view.findViewById(R.id.btnEncode);
        etMessage = view.findViewById(R.id.etMessage);

        btnEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if(message.equals("") || message == ""){
                    Toast.makeText(getActivity(), "Add message first", Toast.LENGTH_SHORT).show();
                }else{
                    encodeImg(coverImage,message,stegoImage);
                }
            }
        });

        coverImage.setOnClickListener(new View.OnClickListener() {
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

        stegoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_zoom_pict,null);
                PhotoView photoView = mView.findViewById(R.id.imgZoom);
                if (stegoBitmap != null) {
                    photoView.setImageBitmap(stegoBitmap);
                    mBuilder.setView(mView);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Image not generated yet.", Toast.LENGTH_SHORT).show();
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

//      private void openCamera() {
//        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, CAMERA_REQUEST_CODE);
//      }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
//              Bitmap image = (Bitmap) data.getExtras().get("data");
//              selectedImage.setImageBitmap(image);

            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                coverImage.setImageURI(Uri.fromFile(f));
                imageUri = Uri.fromFile(f);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
//              Bitmap image = (Bitmap) data.getExtras().get("data");
//              selectedImage.setImageBitmap(image);

            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "PNG_" + timestamp + "." + getFileExt(contentUri);
                coverImage.setImageURI(contentUri);
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

    public void encodeImg(ImageView imgEncrypt, String inputMessage, ImageView imgStego){
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
        imgStego.setImageBitmap(operation);
        stegoBitmap = operation;
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