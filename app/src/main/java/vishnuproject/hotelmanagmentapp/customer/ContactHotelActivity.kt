package vishnuproject.hotelmanagmentapp.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import vishnuproject.hotelmanagmentapp.UserPrefs
import vishnuproject.hotelmanagmentapp.ui.theme.PrimaryColor

data class HotelContactModel(
    val email: String = "",
    val phone: String = "",
    val emergency: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelContactScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var contact by remember { mutableStateOf<HotelContactModel?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Load from Firebase
    LaunchedEffect(Unit) {
        val adminEmail = try {
            UserPrefs.ADMIN_MAIL
        } catch (e: Exception) {
            "unknown_hotel"
        }

        FirebaseDatabase.getInstance().reference
            .child("HotelContact")
            .child(adminEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contact = snapshot.getValue(HotelContactModel::class.java)
                    loading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    loading = false
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Contact Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryColor
                )
            )
        }
    ) { pad ->

        if (loading) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (contact == null) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No contact details available", fontSize = 16.sp, color = Color.Gray)
            }
            return@Scaffold
        }

        val data = contact!!

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            ContactCard(
                icon = Icons.Default.Email,
                title = "Email",
                value = data.email
            )

            ContactCard(
                icon = Icons.Default.Phone,
                title = "Phone Number",
                value = data.phone,
                showCallButton = true
            )

            ContactCard(
                icon = Icons.Default.Warning,
                title = "Emergency Contact",
                value = data.emergency,
                showCallButton = true
            )
        }
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    value: String,
    showCallButton: Boolean = false
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = "", tint = Color(0xFF6C63FF), modifier = Modifier.size(28.dp))

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(value, fontSize = 14.sp, color = Color.Gray)
                }
            }

            if (showCallButton) {
                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (value.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$value"))
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No number available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                ) {
                    Text("Call", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


class ContactHotelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelContactScreen(
                onBack = {
                    finish()
                }
            )
        }
    }
}