package lk.javainstitute.petpulse_v2.petOwner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.model.Appointment;


public class Location_Map_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    public static final String TAG = "TAG";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;


    private Location currentLocation;

    TextView vetName,distance;
    FirebaseAuth mAuth;
    FirebaseFirestore db;



    String distanceData,vetData;

    Button btnNext;

    private List<Marker> vetMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vetName = findViewById(R.id.dr_name);
        distance = findViewById(R.id.distance);

        btnNext = findViewById(R.id.btnNext);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

//        if (distanceData.equals("0 ")) {
//            btnNext.setEnabled(false); // Disable the button
//        } else {
//            btnNext.setEnabled(true); // Enable the button


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (vetData == null && distance == null||distance == null||vetData == null) {
                        Toast.makeText(getApplicationContext(), "Please Select a vet from the Map", Toast.LENGTH_SHORT).show();
                    }else if (vetData.isEmpty() || vetData.equals("Current Location")) {
                        // Show a toast if the text is empty or contains "Current Location"
                        Toast.makeText(getApplicationContext(), "Please select a valid location", Toast.LENGTH_SHORT).show();

                    }else{



                    Intent intent = new Intent(Location_Map_Activity.this, PetOwner_AppointmentConfirm_Activity.class);
                    intent.putExtra("distanceData", distanceData);
                    intent.putExtra("vetDetails", vetData);
                    startActivity(intent);
                    finish();
                    }
                }
            });

    }



    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)); // Zoom to current location
                        currentLocation = location;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;



////////////////////////////////////////////////////////////////////////////////////////////////////////////
       //Data load to map markers

        db.collection("vet")
                .whereEqualTo("available", "yes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            // Get latitude and longitude from Firestore document
                            Double latitude = documentSnapshot.getDouble("latitude");
                            Double longitude = documentSnapshot.getDouble("longitude");
                            String vetFirstName = documentSnapshot.getString("First Name");
                            String vetLastName = documentSnapshot.getString("Last Name");
                            String vetmobile = documentSnapshot.getString("mobile");

                            if (latitude != null && longitude != null  && vetFirstName != null && vetLastName != null && vetmobile != null) {
                                // Create LatLng object
                                LatLng vetLocation = new LatLng(latitude, longitude);

//                                MarkerOptions markerOptions = new MarkerOptions()
//                                        .position(vetLocation)
//                                        .title("Dr."+vetFirstName+" "+vetLastName+" Mobile:"+vetmobile)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapvet)); // Replace "green" with your custom marker image

                                // Add marker to the map
                                Marker marker = mMap.addMarker(new MarkerOptions().position(vetLocation)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.vet_location_select))
                                        .title("Dr. " + vetFirstName + " " + vetLastName + "\n" + "Mobile:" + vetmobile));
                                vetMarkers.add(marker);

                                // Move camera to the first marker (optional)
                                moveCameraToShowAllMarkers();
                            }
                        }

                    }

    });


        // Assuming you have the vetMarkers list populated with vet location markers

        mMap.setOnMarkerClickListener(marker -> {
            // Handle marker click
            String vetDetails = marker.getTitle(); // Get vet details (title) from the clicked marker
            vetName.setText(vetDetails);


            //Get VetLocation latlng
            LatLng vetLocation = marker.getPosition();

            // Calculate distance if both currentLocation and vetLocation are available
            if (currentLocation != null && vetLocation != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        currentLocation.getLatitude(), currentLocation.getLongitude(),
                        vetLocation.latitude, vetLocation.longitude,
                        results);

                // The distance will be available in results[0] in meters
                float distanceInMeters = results[0];

                // Convert meters to kilometers
                float distanceInKilometers = distanceInMeters / 1000;

                distance.setText(String.format("%.2f km", distanceInKilometers));

                distanceData = distance.getText().toString();
                vetData = vetName.getText().toString();


                //Log.d("Distance", "Distance to vet location: " + distanceInKilometers + " kilometers");
            } else {
                //Log.e("Error", "Unable to calculate distance. Location data missing.");
            }

            // Log the vet details

            return false; // Return false to indicate that we haven't consumed the event
        });


        //////////////////////////////////////////////////////////////////
    }

    private void moveCameraToShowAllMarkers() {
        // Function to move the camera to show all markers (if available)
        if (!vetMarkers.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : vetMarkers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // Padding in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}