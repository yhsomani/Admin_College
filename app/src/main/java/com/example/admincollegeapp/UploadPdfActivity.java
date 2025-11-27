package com.example.admincollegeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
public class UploadPdfActivity extends AppCompatActivity {

    private Uri pdfData;
    private EditText pdfTitle;
    private TextView pdfTextView;
    private MaterialButton uploadPdfBTN, pdfPreviewBtn;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    // New ActivityResultLauncher for picking PDF
    private final ActivityResultLauncher<Intent> pdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pdfData = result.getData().getData();
                        if (pdfData != null) {
                            pdfTextView.setText(getPdfName(pdfData));
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pdfTitle = findViewById(R.id.pdfTitleTextView);
        pdfTextView = findViewById(R.id.pdfTextView);
        uploadPdfBTN = findViewById(R.id.uploadPdfButton);
        pdfPreviewBtn = findViewById(R.id.pdfPreview); // Added mapping
        progressDialog = new ProgressDialog(this);

        MaterialCardView addPdfCardView = findViewById(R.id.addPdf);
        addPdfCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfPicker();
            }
        });

        uploadPdfBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pdfTitle.getText().toString().trim();
                if (title.isEmpty()) {
                    pdfTitle.setError("Required");
                    pdfTitle.requestFocus();
                } else if (pdfData == null) {
                    Toast.makeText(UploadPdfActivity.this, "Please Select PDF", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf(title);
                }
            }
        });

        // Fixed Preview Button - Opens in external PDF Viewer
        pdfPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfData != null) {
                    Intent viewerIntent = new Intent(Intent.ACTION_VIEW);
                    viewerIntent.setDataAndType(pdfData, "application/pdf");
                    viewerIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(viewerIntent, "Open PDF with..."));
                    } catch (Exception e) {
                        Toast.makeText(UploadPdfActivity.this, "No PDF Viewer found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadPdfActivity.this, "Select a PDF first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadPdf(String title) {
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading PDF...");
        progressDialog.show();

        String pdfName = title + "-" + System.currentTimeMillis() + ".pdf";
        StorageReference reference = storageReference.child("pdf").child(pdfName);

        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadData(title, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadPdfActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String title, String downloadUrl) {
        String uniqueKey = databaseReference.child("pdf").push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("pdfTitle", title);
        data.put("pdfUrl", downloadUrl);

        if (uniqueKey != null) {
            databaseReference.child("pdf").child(uniqueKey).setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadPdfActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            clearComponents();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadPdfActivity.this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        pdfLauncher.launch(Intent.createChooser(intent, "Select Pdf File"));
    }

    private String getPdfName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    private void clearComponents() {
        pdfTitle.setText("");
        pdfTextView.setText("No File Selected");
        pdfData = null;
    }
}