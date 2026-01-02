package s348786vishnuvardhan.hotelmanagmentapp.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import s348786vishnuvardhan.hotelmanagmentapp.HotelAccountData
import s348786vishnuvardhan.hotelmanagmentapp.customer.ServiceRequestModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceRequestScreen(
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    val adminEmail = HotelAccountData.ADMIN_MAIL

    val requestList = remember { mutableStateListOf<ServiceRequestModel>() }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().reference
            .child("ServiceRequests")
            .child(adminEmail)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    requestList.clear()
                    snapshot.children.forEach {
                        val req = it.getValue(ServiceRequestModel::class.java)
                        if (req != null) requestList.add(req)
                    }
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
                title = { Text("Service Requests", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        if(requestList.isEmpty())
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) { Text("No Requests Received", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)}
            return@Scaffold
        }

        val pendingList = requestList.filter { it.status == "Pending" }
        val progressList = requestList.filter { it.status == "In Progress" }
        val completedList = requestList.filter { it.status == "Completed" }

        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp)
        ) {

            if (pendingList.isNotEmpty())
                item { SectionLabel("Pending Requests") }
            items(pendingList) { req ->
                ServiceRequestCard(req, adminEmail)
            }

            if (progressList.isNotEmpty())
                item { SectionLabel("In Progress") }
            items(progressList) { req ->
                ServiceRequestCard(req, adminEmail)
            }

            if (completedList.isNotEmpty())
                item { SectionLabel("Completed") }
            items(completedList) { req ->
                ServiceRequestCard(req, adminEmail)
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}


@Composable
fun ServiceRequestCard(
    req: ServiceRequestModel,
    adminEmail: String
) {

    val context = LocalContext.current
    val dbRef = FirebaseDatabase.getInstance().reference
        .child("ServiceRequests")
        .child(adminEmail)
        .child(req.requestId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    when (req.category) {
                        "Room Cleaning" -> Icons.Default.CleaningServices
                        "Food Service" -> Icons.Default.Fastfood
                        "Technical Issue" -> Icons.Default.Build
                        else -> Icons.Default.Info
                    },
                    contentDescription = "",
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(req.category, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Room #${req.roomNumber}", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(req.description, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            Text("Urgency: ${req.urgency}", fontWeight = FontWeight.Medium)
            Text("Customer: ${req.customerEmail}", color = Color.Gray)

            Spacer(Modifier.height(14.dp))

            when (req.status) {

                "Pending" -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                        ActionButtonCustom("Accept", Color(0xFF4CAF50)) {
                            dbRef.child("status").setValue("In Progress")
                            Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show()
                        }

                        ActionButtonCustom("Reject", Color.Red) {
                            dbRef.removeValue()
                            Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                "In Progress" -> {
                    ActionButtonCustom("Mark Completed", Color(0xFF2196F3)) {
                        dbRef.child("status").setValue("Completed")
                        Toast.makeText(context, "Marked Completed", Toast.LENGTH_SHORT).show()
                    }
                }

                "Completed" -> {
                    Text(
                        "Completed",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}


@Composable
fun ActionButtonCustom(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, color = Color.White)
    }
}


class AdminServicesRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminServiceRequestScreen(onBack = {
                finish()
            })
        }
    }
}