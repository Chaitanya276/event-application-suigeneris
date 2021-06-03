package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.util.Objects;

public class QRCode extends AppCompatActivity {

    private TextView event_name, player_name;
    public FirebaseDatabase firebaseDatabase;
    private BottomNavigationView bottomNavigation;
    public FirebaseAuth firebaseAuth;
    private ImageView qr_code;
    String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);
        setUpUiViews();

        bottomNavigation.setSelectedItemId(R.id.event_register_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.event_register_nav:
                        return true;
                    case R.id.take_remaining_payment_nav:
                        startActivity(new Intent(getApplicationContext(), TakeRemainingPaymentAcitivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.qr_scan_nav:
                        startActivity(new Intent(getApplicationContext(), ScannedActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.upi_payment_nav:
                        startActivity(new Intent(getApplicationContext(), UpiPaymentActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user_uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference(Objects.requireNonNull(firebaseAuth.getUid()));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EventProfile eventProfile = dataSnapshot.getValue(EventProfile.class);
                event_name.setText("Event name : " + Objects.requireNonNull(eventProfile).getPlayer_event());
                player_name.setText("Name : " + eventProfile.getPlayer_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QRCode.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert windowManager != null;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(user_uid, BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        qr_code.setImageBitmap(bitmap);
    }

    private void setUpUiViews() {
        event_name = findViewById(R.id.qr_event_name);
        player_name = findViewById(R.id.qr_player_name);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        qr_code = findViewById(R.id.qr_screenshot);
    }
}










