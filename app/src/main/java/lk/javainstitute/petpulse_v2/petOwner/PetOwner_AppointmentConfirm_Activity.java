package lk.javainstitute.petpulse_v2.petOwner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.javainstitute.petpulse_v2.R;

public class PetOwner_AppointmentConfirm_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String userMobile;
    String VetmobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_owner_appointment_confirm);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        if (intent != null) {
            String distanceData = intent.getStringExtra("distanceData");
            String vetName = intent.getStringExtra("vetDetails");

            // Split the vetInfo string based on the "Mobile:" delimiter
            String[] splitInfo = vetName.split("Mobile:");
            if (splitInfo.length > 1) {
                // Extract the mobile number part after "Mobile:" and remove any leading/trailing whitespace
               VetmobileNumber = splitInfo[1].trim();
                // Now mobileNumber will contain the extracted mobile number
                Log.d("Mobile Number", "Mobile Number: " + VetmobileNumber);
            }

            /////////////////////////////////////////////////////////////////////////////////

            if (distanceData != null && vetName != null ) {
                // Use the received data as needed in the second activity
                TextView vetNameTextView = findViewById(R.id.vet_full_name); // Replace with your actual TextView
                vetNameTextView.setText(vetName);

                TextView distanceextView = findViewById(R.id.distance); // Replace with your actual TextView
                distanceextView.setText(distanceData);

                ///////////////////////////////////////////////////////////////////////////////

                db.collection("vet").document(VetmobileNumber)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String specialistIn = documentSnapshot.getString("specialist in");
                                TextView vetspecialistIn = findViewById(R.id.vetSpecialist); // Replace with your actual TextView
                                vetspecialistIn.setText(specialistIn);
                            } else {
                                // Document doesn't exist
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors
                        });

            }


            //Button Process (Appointment Confiramtion)

            Button sumbit = findViewById(R.id.btnSumbitAppointment);

            sumbit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //data Insert into firestroe


                    Toast.makeText(PetOwner_AppointmentConfirm_Activity.this, "Appoinment Confirmed", Toast.LENGTH_SHORT).show();
                    // Start the new activity
                    Intent intent = new Intent(PetOwner_AppointmentConfirm_Activity.this, Appointment_Details_Activity.class);
                    intent.putExtra("Vet_Mobile", VetmobileNumber);
                    startActivity(intent);
                    finish();
                    //data Insert into firestroe
                }
            });



        }

    }
}