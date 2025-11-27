package com.example.admincollegeapp.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admincollegeapp.R;
import com.example.admincollegeapp.utils.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeachersActivity extends AppCompatActivity {

    private CircleImageView teacherImage;
    private TextInputEditText teacherName, teacherEmail, teacherPost;
    private Spinner teacherCategory;
    private MaterialButton addTeacherButton;
    private Bitmap bitmap = null;
    private String downloadUrl = "";
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, dbRef;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            // FIXED: Using ImageUtils for API compatibility
                            bitmap = ImageUtils.getBitmap(AddTeachersActivity.this, uri);
                            teacherImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(AddTeachersActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teachers);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        teacherImage = findViewById(R.id.addTeacherImage);
        teacherName = findViewById(R.id.addTeacherName);
        teacherEmail = findViewById(R.id.addTeacherEmail);
        teacherPost = findViewById(R.id.addTeacherPost);
        teacherCategory = findViewById(R.id.addTeacherCategory);
        addTeacherButton = findViewById(R.id.addTeacherButton);

        progressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.teacher_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherCategory.setAdapter(adapter);

        teacherImage.setOnClickListener(v -> openGallery());

        addTeacherButton.setOnClickListener(v -> checkValidation());

        teacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void checkValidation() {
        String name = teacherName.getText().toString().trim();
        String email = teacherEmail.getText().toString().trim();
        String post = teacherPost.getText().toString().trim();
        String category = teacherCategory.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || post.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (bitmap == null) {
            insertData(name, email, post, category);
        } else {
            insertImage(name, email, post, category);
        }
    }

    private void insertImage(final String name, final String email, final String post, final String category) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teachers").child(System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = filePath.putBytes(finalimg);

        uploadTask.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();
                    insertData(name, email, post, category);
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(AddTeachersActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }

    private void insertData(String name, String email, String post, String category) {
        dbRef = databaseReference.child(category);
        String uniqueKey = dbRef.push().getKey();

        if (uniqueKey != null) {
            TeacherData teacherData = new TeacherData(name, email, post, downloadUrl, category, uniqueKey);

            dbRef.child(uniqueKey).setValue(teacherData)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddTeachersActivity.this, "Teacher Added", Toast.LENGTH_SHORT).show();
                        clearComponents();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddTeachersActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearComponents() {
        teacherName.setText("");
        teacherEmail.setText("");
        teacherPost.setText("");
        teacherCategory.setSelection(0);
        teacherImage.setImageResource(R.drawable.man_user_icon);
        bitmap = null;
        downloadUrl = "";
    }
}