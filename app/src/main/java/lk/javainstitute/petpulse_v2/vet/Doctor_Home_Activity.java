package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import lk.javainstitute.petpulse_v2.LogInActivity;
import lk.javainstitute.petpulse_v2.R;

public class Doctor_Home_Activity extends AppCompatActivity {

    String userMobile;
    TextView Fullname;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        //Display Data From Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);

        // Find the TextView
        Fullname = findViewById(R.id.vetFullname);

        DocumentReference userRef = fStore.collection("vet").document(formattedNumber);

        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String userFirstName = documentSnapshot.getString("First Name");
                String userLastName = documentSnapshot.getString("Last Name");

                Fullname.setText( userFirstName +" "+userLastName);

            }
        });

        //Display Data From Firestore




        ToggleButton toggleButton = findViewById(R.id.btnStatus);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String availability = documentSnapshot.getString("available");

                // Check the availability status and update the ToggleButton and text
                if ("yes".equals(availability)) {
                    // Set ToggleButton to green and text to "AVAILABLE"
                    toggleButton.setBackgroundResource(R.color.green);
                    toggleButton.setText("AVAILABLE");
                } else {
                    // Set ToggleButton to red and text to "NOT AVAILABLE"
                    toggleButton.setBackgroundResource(R.color.red);
                    toggleButton.setText("NOT AVAILABLE");
                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure to fetch the document
        });



///////////////////////////////////////////////////////////////////////////////

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String availability = isChecked ? "yes" : "no";

                // Update the "available" field in Firestore
                userRef.update("available", availability)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), isChecked ? "Available" : "Not Available", Toast.LENGTH_SHORT).show();

                                // Change background color based on isChecked
                                int color = isChecked ? Color.GREEN : Color.RED;
                                toggleButton.setBackgroundColor(color);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        Button button = findViewById(R.id.goToVet_Profile);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(Doctor_Home_Activity.this, Vet_Profile_Activity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });

        Button btnViewApp = findViewById(R.id.btnViewAppointments);

        btnViewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(Doctor_Home_Activity.this, Vet_All_Appointments_Activity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });


        Button btnChange_Location = findViewById(R.id.btnChange_Location);
        btnChange_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(Doctor_Home_Activity.this, Vet_Map_Location_Activity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });


        Button btnLogOut = findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Doctor_Home_Activity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}