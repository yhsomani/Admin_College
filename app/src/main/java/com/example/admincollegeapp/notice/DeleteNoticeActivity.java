package com.example.admincollegeapp.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNoticeActivity extends AppCompatActivity implements NoticeAdapter.NoticeClickListener {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> noticeDataList;
    private NoticeAdapter noticeAdapter;
    private DatabaseReference noticeRef;
    private ValueEventListener noticeListener;
    private android.widget.TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
        progressBar = findViewById(R.id.progressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        noticeRef = com.example.admincollegeapp.utils.FirebaseConfig.getDatabaseReference().child("Notice");

        noticeDataList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(DeleteNoticeActivity.this, noticeDataList, DeleteNoticeActivity.this);
        deleteNoticeRecycler.setAdapter(noticeAdapter);

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(this));
        deleteNoticeRecycler.setHasFixedSize(true);

        loadNoticeData();
    }

    private void loadNoticeData() {
        progressBar.setVisibility(View.VISIBLE);

        // FIXED: Store the listener in a variable so it can be removed
        noticeListener = noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if activity is still valid before updating UI
                if (isDestroyed() || isFinishing()) return;

                noticeDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeData noticeData = snapshot.getValue(NoticeData.class);
                    if (noticeData != null) {
                        noticeDataList.add(noticeData);
                    }
                }
                noticeAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                if (noticeDataList.isEmpty()) {
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    deleteNoticeRecycler.setVisibility(View.GONE);
                } else {
                    emptyStateTextView.setVisibility(View.GONE);
                    deleteNoticeRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (isDestroyed() || isFinishing()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        // Safe check for index
        if (position >= 0 && position < noticeDataList.size()) {
            NoticeData noticeData = noticeDataList.get(position);
            String imageUrl = noticeData.getImage();
            String key = noticeData.getKey();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    com.google.firebase.storage.StorageReference storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storageRef.delete().addOnCompleteListener(task -> {
                        deleteNoticeFromDB(key);
                    });
                } catch (IllegalArgumentException e) {
                    deleteNoticeFromDB(key);
                }
            } else {
                deleteNoticeFromDB(key);
            }
        }
    }

    private void deleteNoticeFromDB(String key) {
        DatabaseReference noticeToDeleteRef = noticeRef.child(key);
        noticeToDeleteRef.removeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FIXED: Remove the listener to prevent memory leaks
        if (noticeRef != null && noticeListener != null) {
            noticeRef.removeEventListener(noticeListener);
        }
    }
}