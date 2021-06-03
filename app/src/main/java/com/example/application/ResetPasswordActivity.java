package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText reset_password_email;
    private Button reset_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        reset_password_email = findViewById(R.id.reset_email);
        reset_button = findViewById(R.id.btn_reset_password);
        firebaseAuth = FirebaseAuth.getInstance();

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reset_password_email.getText().toString().trim();
                if(reset_password_email.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this,"Please enter email",Toast.LENGTH_SHORT ).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this,"Passwrod reset link has been sent to your email",Toast.LENGTH_SHORT ).show();
                                finish();
                                startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class));
                                firebaseAuth.signOut();
                            }
                            else {
                                Toast.makeText(ResetPasswordActivity.this,"Password reset link could not been sent. Check your email and internet",Toast.LENGTH_LONG ).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
