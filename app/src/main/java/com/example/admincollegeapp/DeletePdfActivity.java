package com.example.admincollegeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.notice.PdfAdapter;
import com.example.admincollegeapp.notice.PdfData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeletePdfActivity extends AppCompatActivity implements PdfAdapter.PdfItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<PdfData> list;
    private PdfAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pdf);

        recyclerView = findViewById(R.id.deletePdfRecycler);
        progressBar = findViewById(R.id.progressBar);

        reference = FirebaseDatabase.getInstance().getReference().child("pdf");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new PdfAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        getSdkData();
    }

    private void getSdkData() {
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isDestroyed()) return;

                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Mapping data manually because PdfData structure might mismatch upload structure slightly
                    String title = snapshot.child("pdfTitle").getValue(String.class);
                    String url = snapshot.child("pdfUrl").getValue(String.class);
                    String key = snapshot.getKey();

                    PdfData data = new PdfData(title, url, key);
                    list.add(data);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeletePdfActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        PdfData selectedItem = list.get(position);
        String key = selectedItem.getKey();
        reference.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DeletePdfActivity.this, "PDF Deleted Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeletePdfActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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