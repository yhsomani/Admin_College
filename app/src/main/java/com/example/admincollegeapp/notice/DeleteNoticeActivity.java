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
    private DatabaseReference noticeRef;
    private ValueEventListener noticeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
        progressBar = findViewById(R.id.progressBar);

        noticeRef = FirebaseDatabase.getInstance().getReference("Notice");

        noticeDataList = new ArrayList<>();

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
                NoticeAdapter noticeAdapter = new NoticeAdapter(DeleteNoticeActivity.this, noticeDataList, DeleteNoticeActivity.this);
                deleteNoticeRecycler.setAdapter(noticeAdapter);

                progressBar.setVisibility(View.GONE);
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
            DatabaseReference noticeToDeleteRef = noticeRef.child(noticeDataList.get(position).getKey());
            noticeToDeleteRef.removeValue();
        }
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