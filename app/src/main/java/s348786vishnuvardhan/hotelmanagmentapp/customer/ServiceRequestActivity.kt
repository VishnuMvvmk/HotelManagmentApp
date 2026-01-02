package s348786vishnuvardhan.hotelmanagmentapp.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.FirebaseDatabase
import s348786vishnuvardhan.hotelmanagmentapp.HotelAccountData
import s348786vishnuvardhan.hotelmanagmentapp.ui.theme.PrimaryColor
import java.util.UUID

data class ServiceRequestModel(
    val requestId: String = "",
    val roomNumber: String = "",
    val category: String = "",
    val description: String = "",
    val urgency: String = "",
    val customerEmail: String = "",
    val timestamp: Long = 0L,
    val status: String = "Pending"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var roomNumber by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf("Normal") }

    val categories = listOf(
        "Room Cleaning",
        "Food Service",
        "Laundry",
        "Water/Towels",
        "Technical Issue",
        "Other"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Request", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(20.dp)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                "Need something? We're here to help!",
                fontSize = 16.sp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = roomNumber,
                onValueChange = { roomNumber = it.filter { c -> c.isDigit() } },
                label = { Text("Room Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Select Category", fontWeight = FontWeight.SemiBold)

            categories.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = category }
                        .background(
                            if (selectedCategory == category) Color(0xFFEDEAFF)
                            else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        when (category) {
                            "Room Cleaning" -> Icons.Default.CleaningServices
                            "Food Service" -> Icons.Default.Fastfood
                            "Technical Issue" -> Icons.Default.Build
                            else -> Icons.Default.Info
                        },
                        contentDescription = "",
                        tint = Color(0xFF6C63FF)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(category, fontSize = 16.sp)
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe your request") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Text("Urgency", fontWeight = FontWeight.SemiBold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                UrgencyChip(
                    label = "Normal",
                    selected = urgencyLevel == "Normal",
                    onClick = { urgencyLevel = "Normal" }
                )

                UrgencyChip(
                    label = "High",
                    selected = urgencyLevel == "High",
                    onClick = { urgencyLevel = "High" }
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {

                    if (selectedCategory.isBlank() || description.isBlank() || roomNumber.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val requestId = UUID.randomUUID().toString()
                    val adminEmail = HotelAccountData.ADMIN_MAIL

                    val customerEmail =
                        HotelAccountData.getEmail(context)?.replace(".", ",") ?: "unknown_customer"

                    val request = ServiceRequestModel(
                        requestId = requestId,
                        roomNumber = roomNumber,
                        category = selectedCategory,
                        description = description,
                        urgency = urgencyLevel,
                        customerEmail = customerEmail,
                        timestamp = System.currentTimeMillis(),
                        status = "Pending"
                    )

                    FirebaseDatabase.getInstance().reference
                        .child("ServiceRequests")
                        .child(adminEmail)
                        .child(requestId)
                        .setValue(request)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Request Submitted!", Toast.LENGTH_LONG).show()
                            onBack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_LONG)
                                .show()
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Submit Request", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

        }
    }
}


@Composable
fun UrgencyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color(0xFF6C63FF) else Color(0xFFE0E0E0),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            color = if (selected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}


class ServiceRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiceRequestScreen(
                onBack = {
                    finish()
                }
            )
        }
    }
}