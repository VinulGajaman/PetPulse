package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.javainstitute.petpulse_v2.R;

public class Vet_OTP_Validation_Activity extends AppCompatActivity {

    private EditText otpEditText;
    private Button vet_validate_otp;

    private String verificationId;

   FirebaseAuth mAuth;
    FirebaseFirestore db;

    String userMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_otp_validation);

        mAuth = FirebaseAuth.getInstance();
        //
        vet_validate_otp = findViewById(R.id.vet_validate_otp);
        otpEditText = findViewById(R.id.vet_OTP);

        if (otpEditText != null) {

            vet_validate_otp.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    String otp = otpEditText.getText().toString();
                    if (!otp.isEmpty()) {
                        verifyOTP(otp);
                    } else {
                        Toast.makeText(Vet_OTP_Validation_Activity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void verifyOTP(String otp) {

        Intent intent = getIntent();
        if (intent != null) {
            verificationId = intent.getStringExtra("verificationId");
            if (verificationId != null) {



                String verificationIdIntent = verificationId;

                // Create a PhoneAuthCredential using the verification ID and the user-entered OTP
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationIdIntent, otp);

                // Sign in with the PhoneAuthCredential
                signInWithPhoneAuthCredential(credential);
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in successful
                            Toast.makeText(Vet_OTP_Validation_Activity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                            //Validation Part
                            db = FirebaseFirestore.getInstance();
                            mAuth = FirebaseAuth.getInstance();

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            userMobile = currentUser.getPhoneNumber();
                            String formattedNumber = userMobile.replace("+94", "0");
                             //        Log.d(TAG, "Phone Number: " + formattedNumber);


                            DocumentReference userRef = db.collection("vet").document(formattedNumber);

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {
                                            // Check if the "qualifications" field exists in the document
                                            if (document.contains("qualifications")) {
                                                Intent intent = new Intent(Vet_OTP_Validation_Activity.this, Doctor_Home_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(Vet_OTP_Validation_Activity.this, Vet_Details_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {
                                            // The document doesn't exist for the current user in the "vet" collection
                                            // Handle the case when the document doesn't exist
                                        }
                                    } else {
                                        // Error getting document details
                                        // Handle the error
                                    }
                                }
                            });




                        } else {
                            // If sign-in fails, display a message to the user.
                            Toast.makeText(Vet_OTP_Validation_Activity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}