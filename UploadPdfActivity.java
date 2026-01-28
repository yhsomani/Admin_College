// Updated UploadPdfActivity.java to use FirebaseConfig

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadPdfActivity {

    private DatabaseReference eBookDatabase;

    public UploadPdfActivity() {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        eBookDatabase = database.getReference("eBooks"); // Consistent path for E-books
    }

    // Add other existing code and modifications as needed
}