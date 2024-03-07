package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import lk.javainstitute.petpulse_v2.R;

public class Vet_Details_Activity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private DrawerLayout drawerLayout;

    private TextView Fullname;

    private Button btnNext;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private TextInputEditText vet_qualifications;

    private EditText specialist_in;

    String userMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_details);


        //Display Data From Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);


        DocumentReference userRef = fStore.collection("vet").document(formattedNumber);

        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String userFirstName = documentSnapshot.getString("First Name");
                String userLastName = documentSnapshot.getString("Last Name");


                // Find the TextView within the header layout
                Fullname = findViewById(R.id.vet_Name);

                Fullname.setText( userFirstName +" "+userLastName);

            }
        });

        //Display Data From Firestore

        specialist_in = findViewById(R.id.specialist_in);
        vet_qualifications = findViewById(R.id.vet_qualifications);

        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Data Update in Firestore

                String vet_specialist_in = specialist_in.getText().toString().trim();
                String vetQualifications = vet_qualifications.getText().toString().trim();

                if (TextUtils.isEmpty(vet_specialist_in)) {
                    Toast.makeText(getApplicationContext(), "Please Fill the Form.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(vetQualifications)) {
                    Toast.makeText(getApplicationContext(), "Please Fill the Form.", Toast.LENGTH_SHORT).show();
                }else {



                Map<String, Object> updates = new HashMap<>();
                updates.put("specialist in", vet_specialist_in);
                updates.put("qualifications", vetQualifications);

                userRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            // Handle successful update
                            Log.d("TAG", "DocumentSnapshot successfully updated with new fields!");
                            Toast.makeText(Vet_Details_Activity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Create an Intent to navigate to the SecondActivity
                            Intent intent = new Intent(Vet_Details_Activity.this, Vet_Map_Location_Activity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors
                            Log.w("TAG", "Error updating document", e);
                        });

                //Data Update in Firestore

                }

            }
        });
    }
}