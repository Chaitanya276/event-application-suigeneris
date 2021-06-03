package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.Random;

public class EventRegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner event;
    private EditText p_name, p_contact, p_email, p_amount;
    private Button btn_generate_qr_code;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout refresh_layout;
    public FirebaseStorage firebaseStorage;
    boolean res = false;
    String event_name, name, email, contact, amount;
    String uPost, uEmail, uName;
    String TAG = "TEST";
    String userUid;
    int eventCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setUpUiViews();

        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(new Intent(EventRegisterActivity.this, EventRegisterActivity.class));
            }
        });

        btn_generate_qr_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    Random random = new Random();
                    String c_upercase_email = p_name.getText().toString();
                    String c_email = c_upercase_email.toLowerCase();
                    c_email = c_email + random.nextInt(100000) + "@gmail.com";
                    String c_password = p_contact.getText().toString();

                    firebaseAuth.createUserWithEmailAndPassword(c_email, c_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserData();
                                updateUserData();
                                Toast.makeText(EventRegisterActivity.this, "Event registered.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EventRegisterActivity.this, QRCode.class));
                            } else {
                                Toast.makeText(EventRegisterActivity.this, "Event not registered.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EventRegisterActivity.this, "Enter correct details.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.event_register_acitivity);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
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

    }

    private void setUpUiViews() {
        event = findViewById(R.id.spinner);
        p_name = findViewById(R.id.ply_name);
        p_contact = findViewById(R.id.ply_contact);
        p_email = findViewById(R.id.ply_email);
        p_amount = findViewById(R.id.ply_amount);
        btn_generate_qr_code = findViewById(R.id.btn_gererate_qr_code);
        refresh_layout = findViewById(R.id.event_register_acitivity);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(EventRegisterActivity.this);
        String myRef = preferences.getString("userUid", "");
        Log.d(TAG, "Event : " + myRef);


        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_event, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        event.setAdapter(adapter);
        event.setOnItemSelectedListener(this);
        foundDetails();


    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference player_ref = firebaseDatabase.getReference(Objects.requireNonNull(firebaseAuth.getUid()));
        String uid = firebaseAuth.getUid();
        EventProfile eventProfile = new EventProfile(event_name, name, email, contact, amount, uid);
        player_ref.setValue(eventProfile);
    }

    private boolean validate() {

        event_name = event.getSelectedItem().toString();
        name = p_name.getText().toString().trim();
        email = p_email.getText().toString().trim();
        contact = p_contact.getText().toString().trim();
        amount = p_amount.getText().toString().trim();

        res = true;

        if (event_name.isEmpty() || name.isEmpty() || contact.isEmpty() || email.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please enter all the details of Event.", Toast.LENGTH_SHORT).show();
            res = false;
        } else {
            int compare_amount = Integer.parseInt(amount);
            switch (event_name) {
                case "Cs Go":
                    if (compare_amount == 20) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 20) {
                        btn_generate_qr_code.setEnabled(true);
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 20) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
                case "PUBG (Duo)":
                    if (compare_amount == 60) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 60) {
                        Toast.makeText(this, "Didn't paid complecase \"DBugging\":\n" +
                                "                case \"Protovision\":\n" +
                                "                case \"Unravel\":\n" +
                                "                    if (compare_amount == 80) {\n" +
                                "                        Toast.makeText(this, \"Paid comlete amount.\", Toast.LENGTH_SHORT).show();\n" +
                                "                        res = true;\n" +
                                "                    } else if (compare_amount < 80) {\n" +
                                "                        Toast.makeText(this, \"Didn't paid complete amount.\", Toast.LENGTH_SHORT).show();\n" +
                                "                        res = true;\n" +
                                "                    } else if (compare_amount > 80) {\n" +
                                "                        Toast.makeText(this, \"Enter correct amount.\", Toast.LENGTH_SHORT).show();\n" +
                                "                        res = false;\n" +
                                "                    }\n" +
                                "                    break;te amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 60) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
                case "PUBG (Squad)":
                    if (compare_amount == 120) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 120) {
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 120) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
                case "Carrom":
                    if (compare_amount == 20) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 20) {
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 20) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
                case "Search In the Dark":
                case "Rugby":
                case "Dead Shot":
                    if (compare_amount == 90) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 90) {
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 90) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
                case "VR":
                case "Knock It Down":
                    if (compare_amount == 100) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 100) {
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 100) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;

                case "IPL":
                    if (compare_amount == 200) {
                        Toast.makeText(this, "Paid comlete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount < 200) {
                        Toast.makeText(this, "Didn't paid complete amount.", Toast.LENGTH_SHORT).show();
                        res = true;
                    } else if (compare_amount > 200) {
                        Toast.makeText(this, "Enter correct amount.", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                    break;
            }
        }


        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


     private UserProfile foundDetails(){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(EventRegisterActivity.this);
        userUid = preferences.getString("userUid", "");
        Log.d(TAG, "Event : " + userUid);
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(userUid);
         final UserProfile[] userProfile = {new UserProfile()};

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userProfile[0] = snapshot.getValue(UserProfile.class);
                uPost = userProfile[0].getUser_post();
                uName = userProfile[0].getUser_name();
                uEmail = userProfile[0].getUser_email();
                eventCount = userProfile[0].getEventCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventRegisterActivity.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        return userProfile[0];
    }

    private void updateUserData() {
        eventCount += 1;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
         DatabaseReference userDatabaseReference = firebaseDatabase.getReference(userUid);
         UserProfile updatedUserProfile = new UserProfile(uName, uEmail,uPost, eventCount);
        userDatabaseReference.setValue(updatedUserProfile);
    }
}