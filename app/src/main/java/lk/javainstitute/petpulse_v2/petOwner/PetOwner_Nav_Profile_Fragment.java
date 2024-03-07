package lk.javainstitute.petpulse_v2.petOwner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class PetOwner_Nav_Profile_Fragment extends Fragment {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userMobile;
    EditText firstName,lastName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_owner__nav__profile_, container, false);

        //Display Data From Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);
        firstName = view.findViewById(R.id.f_name);
        lastName = view.findViewById(R.id.l_name);

        DocumentReference userRef = fStore.collection("petOwners").document(formattedNumber);

        userRef.addSnapshotListener(requireActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String userFirstName = documentSnapshot.getString("First Name");
                String userLastName = documentSnapshot.getString("Last Name");


                firstName.setText( userFirstName);
                lastName.setText( userLastName);

            }
        });

        //Display Data From Firestore


        Button btnUpdate = view.findViewById(R.id.profileUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstName.getText().toString();
                String newLastName = lastName.getText().toString();

                Map<String, Object> updates = new HashMap<>();
                updates.put("First Name", newFirstName);
                updates.put("Last Name", newLastName);

                //update firstore
                userRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update successful
                                Log.d(TAG, "Last name updated.");
                                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Update failed
                                Log.w(TAG, "Error updating last name.", e);
                                Toast.makeText(getContext(), "Failed to update last name.", Toast.LENGTH_SHORT).show();
                            }
                        });
                   //update firstore

            }
        });

        return view;
    }
}