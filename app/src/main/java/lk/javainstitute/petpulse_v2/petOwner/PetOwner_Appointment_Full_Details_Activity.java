package lk.javainstitute.petpulse_v2.petOwner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.vet.Vet_Appointments_Full_Details_Activity;
import lk.javainstitute.petpulse_v2.vet.Vet_Pending_Appointments_Activity;

public class PetOwner_Appointment_Full_Details_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String userMobile;
    FirebaseStorage storage;

    Intent intent;

    EditText vetNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_owner_appointment_full_details);


        storage =  FirebaseStorage.getInstance();
        mAuth =  FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        vetNote = findViewById(R.id.vetNote2);
        vetNote.setEnabled(false);

        Intent intent = getIntent();
        if (intent != null) {
            String petOwnerMobile = intent.getStringExtra("petOwner_Mobile");
            String vetMobile = intent.getStringExtra("vet_Mobile");
            String currentDate = intent.getStringExtra("date");

            int appointmentNum = intent.getIntExtra("appointmentNum", 0); // Use the appropriate default value

            TextView mobileNumberTextView = findViewById(R.id.mobileNumberTextView);
            mobileNumberTextView.setText(vetMobile);
            // Use the retrieved data as needed (e.g., set it to TextViews)

            TextView appointmentNumTextView = findViewById(R.id.vetAppNum2);


            appointmentNumTextView.setText("0"+String.valueOf(appointmentNum));

            db.collection("appointments")
                    .whereEqualTo("Vet Mobile", vetMobile)
                    .whereEqualTo("PetOwner Mobile", petOwnerMobile)
                    .whereEqualTo("appointment Number", appointmentNum)
                    .whereEqualTo("date", currentDate)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                String imageUid = documentSnapshot.getString("petImageUrl");

                                ImageView petMianImg = findViewById(R.id.petMianImg2);


                                Picasso.get()
                                        .load(imageUid)
                                        .resize(450, 450)
                                        .centerCrop()
                                        .into(petMianImg);

                            }


                        }
                    });


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            DocumentReference vetRef = db.collection("vet").document(vetMobile);

            vetRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String vetFname = documentSnapshot.getString("First Name");
                    String vetLname = documentSnapshot.getString("Last Name");
                    String vetQualifications = documentSnapshot.getString("qualifications");
                    String vetSpecialistIn = documentSnapshot.getString("specialist in");


                    TextView poName = findViewById(R.id.poName2);
                    TextView pType = findViewById(R.id.pType2);
                    TextView pDecs = findViewById(R.id.pDecs2);

                    poName.setText(vetFname+" "+vetLname);
                    pType.setText(vetSpecialistIn);
                    pDecs.setText(vetQualifications);


                } else {
                    // Document does not exist
                }
            }).addOnFailureListener(e -> {
                // Handle failure to fetch data from Firestore
            });

            ////////////////////////////////////////////////////////////////////////////////////////////////////


            FirebaseUser currentUser = mAuth.getCurrentUser();
            userMobile = currentUser.getPhoneNumber();
            String formattedNumber = userMobile.replace("+94", "0");


            db.collection("appointments")
                    .whereEqualTo("appointment Number", appointmentNum)
                    .whereEqualTo("Vet Mobile", vetMobile)
                    .whereEqualTo("PetOwner Mobile", formattedNumber)
                    .whereEqualTo("date", currentDate)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String note = documentSnapshot.getString("note");


                            vetNote.setText(note);
                            // Use the 'note' value as needed (e.g., display it, store it in a variable)
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failures
                    });


            /////////////////////////////////////////////////////////////////////////////////////////////////////


            mobileNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the phone number from the TextView
                    String phoneNumber = mobileNumberTextView.getText().toString();

                    // Create an intent to open the call log with the phone number
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));

                    // Verify if there's an app to handle the intent before starting the activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        // Handle case where no app can handle the action
                        Toast.makeText(PetOwner_Appointment_Full_Details_Activity.this, "No app found to handle this action", Toast.LENGTH_SHORT).show();
                    }
                }
            });



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Button btnDone = findViewById(R.id.btnDirection);

            if (btnDone != null) {

                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference vetRef2 = db.collection("vet").document(vetMobile);

                        vetRef2.get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Double latitude = documentSnapshot.getDouble("latitude");
                                        Double longitude = documentSnapshot.getDouble("longitude");

                                        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                        intent.setPackage("com.google.android.apps.maps");

                                        if (intent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(intent);
                                        } else {
                                            // Handle the case where there's no app to handle the Intent
                                        }
                                    } else {
                                        // Handle the case where the document doesn't exist
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failures
                                });



                    }
                });


            }
        }

        ;
    }
}