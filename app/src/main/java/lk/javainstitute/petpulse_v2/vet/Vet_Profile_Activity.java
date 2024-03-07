package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Vet_Profile_Activity extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userMobile;
    EditText firstName,lastName,specailistIn;

    TextInputEditText qualifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_profile);

        //Display Data From Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);
        firstName = findViewById(R.id.f_name);
        lastName = findViewById(R.id.l_name);
        specailistIn = findViewById(R.id.specialist_in);
        qualifications = findViewById(R.id.vet_qualifications);


        DocumentReference userRef = fStore.collection("vet").document(formattedNumber);

        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String userFirstName = documentSnapshot.getString("First Name");
                String userLastName = documentSnapshot.getString("Last Name");
                String userSpecialistIn = documentSnapshot.getString("specialist in");
                String userQualifications = documentSnapshot.getString("qualifications");


                firstName.setText( userFirstName);
                lastName.setText( userLastName);
                specailistIn.setText( userSpecialistIn);
                qualifications.setText( userQualifications);

            }
        });

        //Display Data From Firestore


        Button btnUpdate = findViewById(R.id.updateProfile);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstName.getText().toString().trim();
                String newLastName = lastName.getText().toString().trim();
                String newSpecialistIn = specailistIn.getText().toString().trim();
                String newQualifications = qualifications.getText().toString().trim();

                if (TextUtils.isEmpty(newFirstName)) {
                    firstName.setError("First Name is Required");
                    return;
                }else if(TextUtils.isEmpty(newLastName)) {
                    lastName.setError("Last Name is Required");
                    return;
                }else if(TextUtils.isEmpty(newSpecialistIn)) {
                    specailistIn.setError("specailist In is Required");
                    return;
                }else if(TextUtils.isEmpty(newQualifications)) {
                    qualifications.setError("Qualification is Required");
                    return;
                }else {



                Map<String, Object> updates = new HashMap<>();
                updates.put("First Name", newFirstName);
                updates.put("Last Name", newLastName);
                updates.put("specialist in", newSpecialistIn);
                updates.put("qualifications", newQualifications);

                //update firstore
                userRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update successful
                                Log.d(TAG, "Last name updated.");
                                Toast.makeText(Vet_Profile_Activity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Update failed
                                Log.w(TAG, "Error updating last name.", e);
                                Toast.makeText(Vet_Profile_Activity.this, "Failed to update last name.", Toast.LENGTH_SHORT).show();
                            }
                        });
                //update firstore
                }
            }
        });

    }
}