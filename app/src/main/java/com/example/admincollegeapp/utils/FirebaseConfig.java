package com.example.admincollegeapp.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseStorage firebaseStorage;

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

    // Get Firebase Storage reference
    public static synchronized StorageReference getStorageReference() {
        if (firebaseStorage == null) {
            firebaseStorage = FirebaseStorage.getInstance();
        }
        return firebaseStorage.getReference();
    }
}