package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    private EditText et_email, et_password;
    private Button btn_login;
    private TextView forgot_password, new_user;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUiViews();

        firebaseAuth = FirebaseAuth.getInstance();


        progress_dialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(MainActivity.this, EventRegisterActivity.class));
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter email & password.", Toast.LENGTH_SHORT).show();
                } else {
                    validate(email, password);
                }
            }
        });
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

    private void setUpUiViews() {
        et_email = findViewById(R.id.login_email);
        et_password = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.btn_log_login);
        new_user = findViewById(R.id.log_register);
        forgot_password = findViewById(R.id.forgot_password);
    }

    private void validate(String email, String password) {

        progress_dialog.setMessage("Please Wait...");
        progress_dialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress_dialog.dismiss();
                    checkEmailVerification();
                } else {
                    progress_dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login failed. Check interent connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        boolean email_flag = Objects.requireNonNull(firebaseUser).isEmailVerified();
        if (email_flag) {
            SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userUid", firebaseAuth.getUid());
            editor.apply();
            Log.d("TEST", "login  :  " + firebaseAuth.getUid());
            finish();
            startActivity(new Intent(MainActivity.this, EventRegisterActivity.class));
        } else {
            Toast.makeText(this, "Verify your email.", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }


}
