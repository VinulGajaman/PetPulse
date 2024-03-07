package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.petpulse_v2.R;

public class Vet_Map_Location_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_map_location);

        // Initialize FirebaseAuth and FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Inside your activity or fragment
        EditText locationEditText = findViewById(R.id.enterLocation);
        Button doneButton = findViewById(R.id.location_select);
        Button nextButton = findViewById(R.id.next);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap; // Assign GoogleMap object


                //Select Location Button Process
                doneButton.setOnClickListener(v -> {
                    String locationName = locationEditText.getText().toString().trim();
                    if (!locationName.isEmpty()) {
                        // Perform geocoding to get coordinates for the typed location
                        Geocoder geocoder = new Geocoder(this);
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                // Move camera to the typed location
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12)); // Adjust the zoom level as needed

                                // Optionally, you can add a marker at the typed location
                                mMap.clear(); // Clear existing markers
                                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                            } else {
                                // Handle case where no coordinates found for the typed location
                                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle geocoding exception
                            Toast.makeText(this, "Geocoding error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle case where the EditText is empty
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
                    }
                });

                //Done Button Process

                nextButton.setOnClickListener(v -> {
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        userMobile = currentUser.getPhoneNumber();
                        String formattedNumber = userMobile.replace("+94", "0");

                        String locationName = locationEditText.getText().toString().trim();
                        if (!locationName.isEmpty()) {
                            Geocoder geocoder = new Geocoder(this);
                            try {
                                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    double latitude = address.getLatitude();
                                    double longitude = address.getLongitude();

                                    // Create a Map object with location data
                                    Map<String, Object> locationData = new HashMap<>();
                                    locationData.put("latitude", latitude);
                                    locationData.put("longitude", longitude);

                                    // Store location data in Firestore under the user's document
                                    DocumentReference userLocationRef = db.collection("vet").document(formattedNumber);
                                    userLocationRef.update(locationData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Data successfully stored in Firestore
                                                Log.d("TAG", "Location data stored in Firestore");
                                                Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
                                                // Proceed with any necessary actions after storing data


                                                Intent intent = new Intent(Vet_Map_Location_Activity.this, Doctor_Home_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure in storing data
                                                Log.w("TAG", "Error storing location data", e);
                                            });
                                } else {
                                    // Handle case where no coordinates found for the typed location
                                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                // Handle geocoding exception
                                Toast.makeText(this, "Geocoding error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle case where the EditText is empty
                            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // Set the LatLngBounds for Sri Lanka
        LatLngBounds sriLankaBounds = new LatLngBounds(
                new LatLng(5.9167, 79.8333), // Southwest bound (bottom-left)
                new LatLng(9.8241, 81.7875) // Northeast bound (top-right)
        );

        // Zoom to the bounds of Sri Lanka
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(sriLankaBounds, 0));
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}