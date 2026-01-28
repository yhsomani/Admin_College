package com.example.admincollegeapp.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {

    private static FirebaseDatabase firebaseDatabase;

    // Private constructor to prevent instantiation
    private FirebaseConfig() {
    }

    // Get Firebase Database reference
    public static synchronized DatabaseReference getDatabaseReference() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase.getReference();
    }
}