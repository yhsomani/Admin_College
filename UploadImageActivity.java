// Import necessary Firebase libraries
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadImageActivity {

    private DatabaseReference databaseReference;

    public UploadImageActivity() {
        // Initialize FirebaseConfig utility to get the database reference
        this.databaseReference = FirebaseConfig.getDatabaseReference();
    }

    // Your existing methods and logic
}