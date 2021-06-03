package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText username, user_password, user_email, confirm_user_password;
    private TextView reg_login;
    int sum;
    public Button register_register;
    public Spinner position;
    private FirebaseAuth firebaseAuth;
    public FirebaseStorage firebaseStorage;
    String name, email, password, confirm_password, post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpuIViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_post, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        position.setAdapter(adapter);
        position.setOnItemSelectedListener(this);

        reg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                    //Already Login
                firebaseAuth.signOut();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                    // Register Button
                if (validate()) {                                                                             // Calling...
                    //Upload data to the database.
                    String email = user_email.getText().toString().trim();
                    String password = user_password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserData();                                                                             // Calling...
                                send_email_verification();
                                Toast.makeText(RegisterActivity.this, "Registration successful, Data uploaded.", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Sorry, Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void setUpuIViews() {
        username = findViewById(R.id.register_username);
        user_email = findViewById(R.id.register_email);
        position = findViewById(R.id.register_spinner);
        user_password = findViewById(R.id.register_password);
        confirm_user_password = findViewById(R.id.register_confirm_password);
        reg_login = findViewById(R.id.reg_login);
        register_register = findViewById(R.id.btn_register);

    }

    private boolean validate() {                                                                    // getting text from all fields and checking whether one of them are
        boolean res = false;                                                                     //empty or not.
        name = username.getText().toString();
        password = user_password.getText().toString();
        confirm_password = confirm_user_password.getText().toString();
        email = user_email.getText().toString();
        post = position.getSelectedItem().toString().trim();
        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || confirm_password.isEmpty() || post.isEmpty()) {
            Toast.makeText(this, "Please enter all the details.", Toast.LENGTH_SHORT).show();
        } else if (password.equals(confirm_password)) {
            res = true;
        } else {
            Toast.makeText(RegisterActivity.this, "Check password and confirm password.", Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    private void sendUserData() {                                                                   //Sending user data to the database section in firebase.
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myref = firebaseDatabase.getReference(Objects.requireNonNull(firebaseAuth.getUid()));
        UserProfile userProfile = new UserProfile(name, email, post, 0);
        myref.setValue(userProfile);
    }

    private void send_email_verification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "verification mail has been sent.", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        Log.d("TEST", "Register : "+ firebaseAuth.getUid());
                        editor.putString("userUid", firebaseAuth.getUid());
                        editor.apply();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}





