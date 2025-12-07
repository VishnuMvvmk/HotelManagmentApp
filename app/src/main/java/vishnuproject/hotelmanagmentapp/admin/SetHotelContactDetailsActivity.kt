package vishnuproject.hotelmanagmentapp.admin

import android.os.Bundle
import vishnuproject.hotelmanagmentapp.UserPrefs


import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.*

data class HotelContact(
    val email: String = "",
    val phone: String = "",
    val emergency: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetHotelContactDetailsScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var emergency by remember { mutableStateOf("") }

    // Load existing data
    LaunchedEffect(Unit) {
        val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown"
        val dbRef = FirebaseDatabase.getInstance().reference
            .child("HotelContact")
            .child(userEmail)

        dbRef.get().addOnSuccessListener {
            val data = it.getValue(HotelContact::class.java)
            if (data != null) {
                email = data.email
                phone = data.phone
                emergency = data.emergency
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Contact Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Hotel Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { c -> c.isDigit() } },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = emergency,
                onValueChange = { emergency = it.filter { c -> c.isDigit() } },
                label = { Text("Emergency Contact") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank() || phone.isBlank() || emergency.isBlank()) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown"

                    val data = HotelContact(email, phone, emergency)

                    FirebaseDatabase.getInstance().reference
                        .child("HotelContact")
                        .child(userEmail)
                        .setValue(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Save failed: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Details", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevContact() {
    SetHotelContactDetailsScreen()
}


class SetHotelContactDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetHotelContactDetailsScreen(onBack = { finish() })
        }
    }
}