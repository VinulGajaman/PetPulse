package lk.javainstitute.petpulse_v2.petOwner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lk.javainstitute.petpulse_v2.R;

public class Checkout_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String userMobile;
    String VetmobileNumber, ImageURL, petDes, petType;


    ProgressBar progressBar;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

// Create a SimpleDateFormat to format the date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

// Format the date and time
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        CheckBox checkBox = findViewById(R.id.checkBoxTandC);

        Button btngoToPaid = findViewById(R.id.goToPaidPage);
        btngoToPaid.setEnabled(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable/disable the button based on checkbox state
                if (!isChecked) {
                    btngoToPaid.setEnabled(false);
                } else {
                    btngoToPaid.setEnabled(true);


                    btngoToPaid.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {


                            progressBar.setVisibility(View.VISIBLE);
                            intent = getIntent();
                            if (intent != null) {
                                VetmobileNumber = intent.getStringExtra("Vet_Mobile");
                                ImageURL = intent.getStringExtra("ImgURL");
                                petDes = intent.getStringExtra("petDes");
                                petType = intent.getStringExtra("petType");

                                //Current User
                                FirebaseUser currentUser = mAuth.getCurrentUser();

//        Log.d(TAG, "Phone Number: " + formattedNumber);


                                ///// Checking the Appoinments ((VetMobile+ current Date)count + 1) ///////////////////////////////////////
                                if (currentUser != null) {
                                    userMobile = currentUser.getPhoneNumber();
                                    String formattedNumber = userMobile.replace("+94", "0");

                                    db.collection("appointments")
                                            .whereEqualTo("Vet Mobile", VetmobileNumber)
                                            .whereEqualTo("PetOwner Mobile", formattedNumber)
                                            .whereEqualTo("status", "Pending")
                                            .whereEqualTo("date", currentDate)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    // Documents exist for the specified query
                                                    Toast.makeText(Checkout_Activity.this, "You already have an appointment to this Veterinarian", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    /////////////////////If not Make an Appointment////////////////////////////////////////

                                                    db.collection("appointments")
                                                            .whereEqualTo("Vet Mobile", VetmobileNumber)
                                                            .whereEqualTo("date", currentDate)
                                                            .get()
                                                            .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                                                int appointmentNumber = queryDocumentSnapshots2.size() + 1;

                                                                // Update the documents with a new field "appointment Number"


                                                                //Register Firestore

                                                                Map<String, Object> appointment = new HashMap<>();
                                                                appointment.put("Vet Mobile", VetmobileNumber);
                                                                appointment.put("PetOwner Mobile", formattedNumber);
                                                                appointment.put("date", currentDate);
                                                                appointment.put("time", currentTime);
                                                                appointment.put("status", "Pending");
                                                                appointment.put("note", "None");
                                                                appointment.put("pet", petType);
                                                                appointment.put("pet description", petDes);
                                                                appointment.put("petImageUrl", ImageURL);
                                                                appointment.put("appointment Number", appointmentNumber);

                                                                // Set the document ID as mobileNum
                                                                db.collection("appointments")
                                                                        .add(appointment)
                                                                        .addOnSuccessListener(aVoid -> {


                                                                            //////////////////////////////////////////////////////////////////////////////////////////


                                                                            // Handle success, such as navigating to another activity or initiating OTP verification
                                                                            Log.d("TAG", "DocumentSnapshot successfully written!");
                                                                            progressBar.setVisibility(View.GONE);
                                                                            Toast.makeText(Checkout_Activity.this, "Appointment Registered", Toast.LENGTH_SHORT).show();

                                                                            String appointmentNumberAsString = String.valueOf(appointmentNumber);

                                                                            intent = new Intent(Checkout_Activity.this, Successfully_Paid_Activity.class);
                                                                            intent.putExtra("appointmentNum", appointmentNumberAsString);
                                                                            intent.putExtra("date", currentDate);
                                                                            intent.putExtra("time", currentTime);
                                                                            startActivity(intent);
                                                                            finish();

                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            // Handle any errors
                                                                            progressBar.setVisibility(View.GONE);
                                                                            Toast.makeText(Checkout_Activity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                                            Log.w("TAG", "Error writing document", e);
                                                                        });


                                                            }).addOnFailureListener(e -> {
                                                                // Handle query failure
                                                                Log.e("Firestore", "Error querying appointments", e);
                                                            });




                                                    ////////////////////////////////////////////////////////////////////////////////////////////
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle errors
                                            });



                                }
                            }
                        }

                        //////////////////////////// Checking the Appoinments ///////////////////////////////////////
//             Start the new activity


                    });


                }

            }
        });

    }
}