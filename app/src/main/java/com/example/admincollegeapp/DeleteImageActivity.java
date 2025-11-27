package com.example.admincollegeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.notice.GalleryAdapter;
import com.example.admincollegeapp.notice.GalleryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteImageActivity extends AppCompatActivity implements GalleryAdapter.GalleryItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<GalleryData> list;
    private GalleryAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_image);

        recyclerView = findViewById(R.id.deleteImageRecycler);
        progressBar = findViewById(R.id.progressBar);

        reference = FirebaseDatabase.getInstance().getReference().child("gallery");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new GalleryAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        getGalleryData();
    }

    private void getGalleryData() {
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isDestroyed()) return;

                list.clear();
                // Logic: Gallery -> Category -> ImageKey -> UrlString
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();

                    for (DataSnapshot imageSnapshot : categorySnapshot.getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        String key = imageSnapshot.getKey();

                        GalleryData data = new GalleryData(imageUrl, categoryName, key);
                        list.add(data);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteImageActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        GalleryData selectedItem = list.get(position);
        String category = selectedItem.getCategory();
        String key = selectedItem.getKey();

        reference.child(category).child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DeleteImageActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeleteImageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(reference != null && eventListener != null) {
            reference.removeEventListener(eventListener);
        }
    }
}