package com.example.hiking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gunakan layout splash atau biarkan kosong sekejap sementara check logic
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("users_location");

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            sendLocationAndNavigate();
        }
    }

    private void sendLocationAndNavigate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null && mAuth.getCurrentUser() != null) {
                    String userId = mAuth.getCurrentUser().getUid();

                    // TUKAR ALAMAT: Guna "Users" supaya sama dengan SignUp & Home
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("lat", location.getLatitude());
                    updates.put("lng", location.getLongitude());
                    // Kita tukar perkataan "Belum ditetapkan" kepada string koordinat
                    updates.put("location", location.getLatitude() + ", " + location.getLongitude());
                    updates.put("userAgent", WebSettings.getDefaultUserAgent(this));
                    updates.put("model", Build.MODEL);

                    // Guna updateChildren supaya data "name" dan "email" sedia ada TAK HILANG
                    userRef.updateChildren(updates);
                }
                navigateToNextScreen();
            });
        } else {
            navigateToNextScreen();
        }
    }

    private void navigateToNextScreen() {
        if (mAuth.getCurrentUser() != null) {
            // Kalau dah login, pergi Home
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // Kalau belum, pergi Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish(); // Tutup MainActivity supaya user tak boleh back ke sini
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            // Tak kira user bagi permission atau tak, kita tetap move forward
            sendLocationAndNavigate();
        }
    }
}