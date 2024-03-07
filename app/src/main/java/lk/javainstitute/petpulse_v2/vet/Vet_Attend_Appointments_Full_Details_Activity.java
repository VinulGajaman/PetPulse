package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.model.Appointment;

public class Vet_Attend_Appointments_Full_Details_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String userMobile;
    FirebaseStorage storage;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_attend_appointments_full_details);

        storage =  FirebaseStorage.getInstance();
        mAuth =  FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        if (intent != null) {
            String petOwnerMobile = intent.getStringExtra("petOwner_Mobile");
            String date = intent.getStringExtra("date");
            int appointmentNum = intent.getIntExtra("appointmentNum", 0); // Use the appropriate default value

            FirebaseUser currentUser = mAuth.getCurrentUser();
            userMobile = currentUser.getPhoneNumber();
            String formattedNumber = userMobile.replace("+94", "0");

            ImageButton backbtn = findViewById(R.id.backbtn2);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the new activity
                    Intent intent = new Intent(Vet_Attend_Appointments_Full_Details_Activity.this,Vet_Attend_Appointments_Activity.class); // Replace NextActivity.class with your target activity
                    startActivity(intent);
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            db.collection("appointments")
                    .whereEqualTo("Vet Mobile", formattedNumber)
                    .whereEqualTo("appointment Number", appointmentNum)
                    .whereEqualTo("date", date)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                String imageUid = documentSnapshot.getString("petImageUrl");
                                String petType = documentSnapshot.getString("pet");
                                String petDesc = documentSnapshot.getString("pet description");

                                TextView pType = findViewById(R.id.pType2);
                                TextView pDecs = findViewById(R.id.pDecs2);
                                ImageView petMianImg = findViewById(R.id.petMianImg2);

                                pType.setText(petType);
                                pDecs.setText(petDesc);


                                Picasso.get()
                                        .load(imageUid)
                                        .resize(450, 450)
                                        .centerCrop()
                                        .into(petMianImg);

//


                            }


                        }
                    });


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////




            // Use the retrieved data as needed (e.g., set it to TextViews)

            TextView appointmentNumTextView = findViewById(R.id.vetAppNum2);


            appointmentNumTextView.setText("0"+String.valueOf(appointmentNum));

            DocumentReference userRef = db.collection("petOwners").document(petOwnerMobile);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("First Name");
                    String lastName = documentSnapshot.getString("Last Name");


                    TextView poName = findViewById(R.id.poName2);

                    poName.setText(firstName+" "+lastName);



                } else {
                    // Document does not exist
                }
            }).addOnFailureListener(e -> {
                // Handle failure to fetch data from Firestore
            });

            // Add more code to display the retrieved data

            ///////////////////////////////////////////////////////////////////////////////////////////////////


            EditText vetNote = findViewById(R.id.vetNote2);



            db.collection("appointments")
                    .whereEqualTo("Vet Mobile", formattedNumber)
                    .whereEqualTo("appointment Number", appointmentNum)
                    .whereEqualTo("date", date)
                    .whereEqualTo("status", "done")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                String note = documentSnapshot.getString("note");


                                vetNote.setText(note);
                                vetNote.setEnabled(false);
                                // Use the values as needed
                                Log.d("AppointmentDetails", "Note: " + note);

//                        Appointment appointment = documentSnapshot.toObject(Appointment.class);
//                        appointments.add(appointment);


                            }


                        }
                    });






        }

        ;
    }
}