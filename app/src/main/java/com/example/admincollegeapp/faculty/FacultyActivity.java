package com.example.admincollegeapp.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FacultyActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    DatabaseReference databaseReference;
    // FIXED: Variable to hold the listener so we can remove it later
    private ValueEventListener teacherListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("teacher");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FacultyActivity.this, AddTeachersActivity.class));
            }
        });

        loadTeacherData();
    }

    private void loadTeacherData() {
        // FIXED: Assign listener to variable
        teacherListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // FIXED: Check if activity is still valid to prevent crashes
                if (isDestroyed() || isFinishing()) return;

                LinearLayout parentLayout = findViewById(R.id.parentLayout);
                if (parentLayout == null) return;

                parentLayout.removeAllViews(); // Clear previous views

                for (DataSnapshot departmentSnapshot : dataSnapshot.getChildren()) {
                    String departmentName = departmentSnapshot.getKey();
                    List<TeacherData> teachers = new ArrayList<>();
                    for (DataSnapshot teacherSnapshot : departmentSnapshot.getChildren()) {
                        // Convert the DataSnapshot to TeacherData object
                        String key = teacherSnapshot.getKey();
                        String name = teacherSnapshot.child("name").getValue(String.class);
                        String email = teacherSnapshot.child("email").getValue(String.class);
                        String post = teacherSnapshot.child("post").getValue(String.class);
                        String image = teacherSnapshot.child("image").getValue(String.class);
                        String category = teacherSnapshot.child("category").getValue(String.class);

                        // Create a new TeacherData object
                        TeacherData teacher = new TeacherData(name, email, post, image, category, key);

                        teachers.add(teacher);
                    }
                    displayDepartmentWithTeachers(parentLayout, departmentName, teachers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (isDestroyed() || isFinishing()) return;
                Toast.makeText(FacultyActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDepartmentWithTeachers(LinearLayout parentLayout, String departmentName, List<TeacherData> teachers) {
        // FIXED: Added check for null parent or context
        if (parentLayout == null || teachers.isEmpty()) return;

        View departmentLayout = LayoutInflater.from(this).inflate(R.layout.department_layout, parentLayout, false);

        TextView departmentTextView = departmentLayout.findViewById(R.id.departmentTextView);
        departmentTextView.setText(departmentName);

        RecyclerView recyclerView = departmentLayout.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true); // Optimization

        TeacherAdapter adapter = new TeacherAdapter(teachers, this);
        recyclerView.setAdapter(adapter);

        parentLayout.addView(departmentLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FIXED: Clean up listener to prevent memory leaks
        if (databaseReference != null && teacherListener != null) {
            databaseReference.removeEventListener(teacherListener);
        }
    }
}