package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UpiPaymentActivity extends AppCompatActivity {

    private EditText upi_name, upi_amount, upi_note, upi_reciever_id;
    private Button upi_payment;
    private BottomNavigationView bottomNavigation;
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_payment);
        setUpUiViews();

        bottomNavigation.setSelectedItemId(R.id.upi_payment_nav);
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
                        startActivity(new Intent(getApplicationContext(), ScannedActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.upi_payment_nav:

                        return true;
                }
                return false;
            }
        });

        upi_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = upi_amount.getText().toString();
                String name = upi_name.getText().toString();
                String note = upi_note.getText().toString();
                String id = upi_reciever_id.getText().toString();
                payUsingUpi(amount, id, name, note);
            }
        });

    }

    private void payUsingUpi(String amount, String id, String name, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", id)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPaymentIntent = new Intent(Intent.ACTION_VIEW);
        upiPaymentIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPaymentIntent, "Pay with...");

        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, "No UPI app found, please install one to complete payment.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("responnse");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add(trxt);
                        upiPaymentDataOperation(datalist);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add("nothing");
                        upiPaymentDataOperation(datalist);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> datalist = new ArrayList<>();
                    datalist.add("nothing");
                    upiPaymentDataOperation(datalist);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(UpiPaymentActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation:" + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(UpiPaymentActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(UpiPaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UpiPaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UpiPaymentActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }

    }

    private static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && ((NetworkInfo) netInfo).isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void setUpUiViews() {
        upi_amount = findViewById(R.id.upi_amount);
        upi_reciever_id = findViewById(R.id.upi_id);
        upi_name = findViewById(R.id.upi_name);
        upi_note = findViewById(R.id.upi_note);
        upi_payment = findViewById(R.id.btn_upi_payment);
        bottomNavigation = findViewById(R.id.bottom_navigation);

    }
}
