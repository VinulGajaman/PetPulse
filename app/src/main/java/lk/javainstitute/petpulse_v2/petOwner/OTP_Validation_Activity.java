package lk.javainstitute.petpulse_v2.petOwner;

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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import lk.javainstitute.petpulse_v2.R;

public class OTP_Validation_Activity extends AppCompatActivity {

    private EditText otpEditText;
    private Button validateOTPButton;

    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);

//
        mAuth = FirebaseAuth.getInstance();

        otpEditText = findViewById(R.id.petOwner_Otp);
        validateOTPButton = findViewById(R.id.btn_validate_otp);



        validateOTPButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String otp = otpEditText.getText().toString();
                if (!otp.isEmpty()) {
                    verifyOTP(otp);
                } else {
                    Toast.makeText(OTP_Validation_Activity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                            Toast.makeText(OTP_Validation_Activity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OTP_Validation_Activity.this, PetOwner_Home_Activity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign-in fails, display a message to the user.
                            Toast.makeText(OTP_Validation_Activity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}