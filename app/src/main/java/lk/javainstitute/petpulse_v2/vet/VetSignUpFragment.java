package lk.javainstitute.petpulse_v2.vet;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lk.javainstitute.petpulse_v2.R;


public class VetSignUpFragment extends Fragment {


    public static final String TAG = "tag";
    EditText fname,lname,mobile;
    Button goToOTP;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vet_sign_up, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        fname = view.findViewById(R.id.vet_fname);
        lname = view.findViewById(R.id.vet_lname);
        mobile = view.findViewById(R.id.vet_mobile);

        goToOTP = view.findViewById(R.id.goToVet_OTP);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        goToOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate part
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String mobileNum = mobile.getText().toString().trim();



                if(TextUtils.isEmpty(firstName)){
                    fname.setError("First Name is Required");
                    return;

                } else if (TextUtils.isEmpty(lastName)) {
                    lname.setError("Last Name is Required");
                    return;

                }else if (TextUtils.isEmpty(mobileNum)) {
                    mobile.setError("Mobile Number is Required");
                    return;

                 }else{

                    fStore.collection("vet")
                            .whereEqualTo("mobile", mobileNum)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        if (!task.getResult().isEmpty()) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Mobile Number is Already in used. " , Toast.LENGTH_SHORT).show();


                                        } else {

                                            progressBar.setVisibility(View.VISIBLE);
                                            sendOTP(firstName,lastName,mobileNum);




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

        return view;
    }

    private void sendOTP(String vetFirstName,String vetLastName,String mobileNum) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+94"+mobileNum.replaceAll("^0*", ""),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                requireActivity(),               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(getContext(), "Verification ID: " + verificationId, Toast.LENGTH_SHORT).show();

                        //Register Firestore

                        Map<String, Object> user = new HashMap<>();
                        user.put("First Name", vetFirstName);
                        user.put("Last Name", vetLastName);
                        user.put("mobile", mobileNum);
                        user.put("status", "Active");
                        user.put("available", "no");

                       // Set the document ID as mobileNum
                        fStore.collection("vet")
                                .document(mobileNum)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Handle success, such as navigating to another activity or initiating OTP verification
                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                    Log.w("TAG", "Error writing document", e);
                                });
                        progressBar.setVisibility(View.GONE);
                        // Start the new activity
                        Intent intent = new Intent(getActivity(), Vet_OTP_Validation_Activity.class);
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
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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

                            Intent intent = new Intent(getActivity(), Vet_Details_Activity.class);
                            startActivity(intent);
                        } else {
                            // If sign-in fails, display a message to the user.
                            Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}