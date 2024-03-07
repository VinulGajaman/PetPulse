package lk.javainstitute.petpulse_v2.petOwner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lk.javainstitute.petpulse_v2.R;


public class PetOwner_LogInFragment extends Fragment {

    public static final String TAG = "tag";
    EditText mobile;

    Button goToPetOwnerHome;

    String userId;

    FirebaseAuth fAuth;

    ProgressBar progressBar;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_owner__log_in, container, false);

        goToPetOwnerHome = view.findViewById(R.id.btn_PetOwner_login);

        progressBar = view.findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mobile = view.findViewById(R.id.mobile);

        if (goToPetOwnerHome != null) {
            goToPetOwnerHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobileNum = mobile.getText().toString().trim();

                    if (TextUtils.isEmpty(mobileNum)) {
                        mobile.setError("Mobile Number is Required");
                        return;

                    } else {

                        // Query Firestore for the provided mobile number
                        fStore.collection("petOwners")
                                .whereEqualTo("mobile", mobileNum)
                                .whereEqualTo("status", "Active")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            if (!task.getResult().isEmpty()) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                sendOTP(mobileNum);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Invalid User" , Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Invalid Mobile Number " , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }


                    }

            });

        }
            return view;

        }



        private void sendOTP(String mobileNum) {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+94"+mobileNum.replaceAll("^0*", ""),        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    requireActivity(),               // Activity (for callback binding)
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                            //Toast.makeText(getContext(), "Verification ID: " + verificationId, Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.GONE);
                            // Start the new activity
                            Intent intent = new Intent(getActivity(), OTP_Validation_Activity.class);
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);

                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            // If automatic verification is successful, you can use this callback
                            // to sign in the user directly (without requiring them to enter the OTP)
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            // If the verification process fails, handle the error here
                            Toast.makeText(getContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "ERRORRRRRRR: " +  e.getMessage());
                        }
                    }
            );
        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            fAuth.signInWithCredential(credential)
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign-in successful
                                Toast.makeText(getContext(), "Verification successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), PetOwner_Home_Activity.class);
                                startActivity(intent);
                            } else {
                                // If sign-in fails, display a message to the user.
                                Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
   }