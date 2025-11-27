package com.example.admincollegeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.admincollegeapp.faculty.FacultyActivity;
import com.example.admincollegeapp.notice.DeleteNoticeActivity;
import com.example.admincollegeapp.notice.UploadNoticeActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MaterialCardView uploadNotice, addGalleryImage, uploadEbooks, addFaculty, deleteNotice;
    MaterialCardView deleteImage, deleteEbook; // New buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadNotice = findViewById(R.id.addNotice);
        addGalleryImage = findViewById(R.id.addGalleryImage);
        uploadEbooks = findViewById(R.id.addEbook);
        addFaculty = findViewById(R.id.addFaculty);
        deleteNotice = findViewById(R.id.deleteNotice);
        deleteImage = findViewById(R.id.deleteImage);
        deleteEbook = findViewById(R.id.deleteEbook);

        uploadNotice.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        uploadEbooks.setOnClickListener(this);
        addFaculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
        deleteImage.setOnClickListener(this);
        deleteEbook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.addNotice) {
            startActivity(new Intent(MainActivity.this, UploadNoticeActivity.class));
        } else if (id == R.id.addEbook) {
            startActivity(new Intent(MainActivity.this, UploadPdfActivity.class));
        } else if (id == R.id.addFaculty) {
            startActivity(new Intent(MainActivity.this, FacultyActivity.class));
        } else if (id == R.id.addGalleryImage) {
            startActivity(new Intent(MainActivity.this, UploadImageActivity.class));
        } else if (id == R.id.deleteNotice) {
            startActivity(new Intent(MainActivity.this, DeleteNoticeActivity.class));
        } else if (id == R.id.deleteImage) {
            startActivity(new Intent(MainActivity.this, DeleteImageActivity.class));
        } else if (id == R.id.deleteEbook) {
            startActivity(new Intent(MainActivity.this, DeletePdfActivity.class));
        }
    }
}