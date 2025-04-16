package com.jayanthr.spendisense

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jayanthr.spendisense.feature.sms.SmsReceiver
import com.jayanthr.spendisense.ui.theme.SpendiSenseTheme

class MainActivity : ComponentActivity() {


    private lateinit var smsReceiver: SmsReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!hasSmsPermissions()) requestSmsPermissions()

        enableEdgeToEdge()
        setContent {
            SpendiSenseTheme {

                Surface(modifier =
                Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background)
                {
                    NavHostScreen()
                }
            }
        }
    }

    private fun hasSmsPermissions(): Boolean {
        // Check if the permissions are already granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermissions() {
        // Request permissions from the user
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver) // Clean up to avoid memory leaks
    }

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 100
    }
}

