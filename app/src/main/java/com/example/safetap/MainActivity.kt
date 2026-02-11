package com.example.safetap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.net.Uri
import android.provider.Telephony




class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // CHANGE THIS TO YOUR NUMBER
    private val emergencyNumber = "7447843165"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) // THIS LINE IS MUST

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.btnSOS).setOnClickListener {
            sendSOS()
        }
    }

    private fun autoCall() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                200
            )
            return
        }

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:7447843165") // India Emergency 112

        startActivity(intent)
    }

    private fun sendSOS() {

        if (!hasPermissions()) {
            requestPermissions()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                val lat = location?.latitude
                val lon = location?.longitude

                val link =
                    "https://maps.google.com/?q=$lat,$lon"

                val msg = """
                    🚨 EMERGENCY 🚨
                    I need help!
                    
                    Location:
                    $link
                """.trimIndent()

                sendSMS(msg)
            }
        autoCall()

    }

    private fun sendSMS(msg: String) {

        try {

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$emergencyNumber")
            intent.putExtra("sms_body", msg)

            startActivity(intent)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "No SMS app found",
                Toast.LENGTH_LONG
            ).show()
        }
    }



    private fun hasPermissions(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            ),
            101
        )
    }
}
