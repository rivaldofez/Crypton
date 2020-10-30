package com.example.crypton;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class EncodeLSB extends Fragment {

    ImageView coverImage, stegoImage;
    Button btnAdd,btnSave,btnEncode;

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
        btnAdd = view.findViewById(R.id.btnAdd)

        return view;
    }
}