package com.example.application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.application.TakeRemainingPaymentAcitivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.annotations.Nullable
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*

class TakeRemainingPaymentAcitivity : AppCompatActivity() {
    private var btn_pay: Button? = null
    private var u_payment: EditText? = null
    private var u_remaing_payment: EditText? = null
    private var u_name: EditText? = null
    private var u_email: EditText? = null
    private var u_contact: EditText? = null
    private var u_event: EditText? = null
    private var bottomNavigationView: BottomNavigationView? = null
    var name: String? = null
    var email: String? = null
    var payment: String? = null
    var contact: String? = null
    var event: String? = null
    var amount: String? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_remaining_payment_acitivity)
        setUpUiViews()
        btn_pay!!.setOnClickListener {
            val firebaseDatabase = FirebaseDatabase.getInstance().getReference(str!!)
            val p_event = u_event!!.text.toString()
            val p_name = u_name!!.text.toString()
            val p_email = u_email!!.text.toString()
            val p_contact = u_contact!!.text.toString()
            val p_old_pay = u_payment!!.text.toString()
            val p_new_pay = u_remaing_payment!!.text.toString()
            if (p_contact.isEmpty() || p_email.isEmpty() || p_event.isEmpty() || p_name.isEmpty() || p_old_pay.isEmpty() || p_new_pay.isEmpty()) {
                Toast.makeText(this@TakeRemainingPaymentAcitivity, "Enter all details", Toast.LENGTH_SHORT).show()
            } else {
                val old: Int
                val new_pay: Int
                val sum_pay: Int
                old = p_old_pay.toInt()
                new_pay = p_new_pay.toInt()
                sum_pay = old + new_pay
                val p_pay = sum_pay.toString()
                val eventProfile = EventProfile(p_event, p_name, p_email, p_contact, p_pay, str)
                firebaseDatabase.setValue(eventProfile)
                Toast.makeText(this@TakeRemainingPaymentAcitivity, "Payment updated successfully.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@TakeRemainingPaymentAcitivity, TakeRemainingPaymentAcitivity::class.java))
            }
        }
        bottomNavigationView!!.selectedItemId = R.id.take_remaining_payment_nav
        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.event_register_nav -> {
                    startActivity(Intent(applicationContext, EventRegisterActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.take_remaining_payment_nav -> return@OnNavigationItemSelectedListener true
                R.id.qr_scan_nav -> {
                    startActivity(Intent(applicationContext, ScannedActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.upi_payment_nav -> {
                    startActivity(Intent(applicationContext, UpiPaymentActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        val intentIntegrator = IntentIntegrator(this@TakeRemainingPaymentAcitivity)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.setBarcodeImageEnabled(true)
        Toast.makeText(this, "scanning in progress...", Toast.LENGTH_SHORT).show()
        intentIntegrator.setPrompt("Place QR Code inside frame")
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.initiateScan()
    }

    private fun setUpUiViews() {
        btn_pay = findViewById(R.id.btn_payment)
        u_event = findViewById(R.id.update_event)
        u_name = findViewById(R.id.update_name)
        u_email = findViewById(R.id.update_email)
        u_contact = findViewById(R.id.update_contact)
        u_payment = findViewById(R.id.update_old_amount)
        u_remaing_payment = findViewById(R.id.update_new_amount)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            var str: String?
            str = result.contents
            firebaseDatabase = FirebaseDatabase.getInstance()
            val databaseReference = firebaseDatabase!!.reference.child(str)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val eventProfile = dataSnapshot.getValue(EventProfile::class.java)
                    u_event!!.setText(Objects.requireNonNull(eventProfile)?.getPlayer_event())
                    u_name!!.setText(eventProfile!!.getPlayer_name())
                    u_email!!.setText(eventProfile.getPlayer_email())
                    u_contact!!.setText(eventProfile.getPlayer_contact())
                    u_payment!!.setText(eventProfile.getPlayer_amount())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@TakeRemainingPaymentAcitivity, databaseError.code, Toast.LENGTH_SHORT).show()
                }
            })
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        var str: String? = null
    }
}