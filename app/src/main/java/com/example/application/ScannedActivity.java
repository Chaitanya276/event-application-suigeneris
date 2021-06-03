package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannedActivity extends AppCompatActivity {

    private TextView player_name, event_name, event_amount, player_email;
    private BottomNavigationView bottomNavigation;
    public static String str;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        player_name = findViewById(R.id.after_scanning_player_name);
        player_email = findViewById(R.id.after_scanning_player_email);
        event_name = findViewById(R.id.after_scanning_event_name);
        event_amount = findViewById(R.id.after_scanning_event_amount);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setSelectedItemId(R.id.qr_scan_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.event_register_nav:
                        startActivity(new Intent(getApplicationContext(), EventRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.take_remaining_payment_nav:
                        startActivity(new Intent(getApplicationContext(), TakeRemainingPaymentAcitivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.qr_scan_nav:
                        return true;
                    case R.id.upi_payment_nav:
                        startActivity(new Intent(getApplicationContext(), UpiPaymentActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        IntentIntegrator intentIntegrator = new IntentIntegrator(ScannedActivity.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBarcodeImageEnabled(true);
        Toast.makeText(this, "scanning in progress...", Toast.LENGTH_SHORT).show();
        intentIntegrator.setPrompt("Place QR Code inside frame");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {

            str = result.getContents();

            firebaseDatabase = FirebaseDatabase.getInstance();

            final DatabaseReference databaseReference = firebaseDatabase.getReference().child(str)  ;

            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    EventProfile eventProfile = dataSnapshot.getValue(EventProfile.class);

                    player_name.setText("Player name : " + eventProfile.getPlayer_name());
                    player_email.setText("Player email : " + eventProfile.getPlayer_email());
                    String verify_event = eventProfile.getPlayer_event();
                    event_name.setText("Event name : " + eventProfile.getPlayer_event());
                    Toast.makeText(ScannedActivity.this, "step one", Toast.LENGTH_SHORT).show();
                    String verify_amount = eventProfile.getPlayer_amount();
                    Toast.makeText(ScannedActivity.this, "step two", Toast.LENGTH_SHORT).show();
                    if (verify_amount != null) {
                        event_amount.setText("Event amount : " + verify_amount);
                    } else {
                        Toast.makeText(ScannedActivity.this, "Event amount is empty", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ScannedActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }

            });
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}
