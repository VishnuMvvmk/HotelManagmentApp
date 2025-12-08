package vishnuproject.hotelmanagmentapp.customer


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vishnuproject.hotelmanagmentapp.UserPrefs
import vishnuproject.hotelmanagmentapp.admin.RoomModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// ---------------------- BOOKING MODEL ----------------------
data class BookingModel(
    val bookingId: String = "",
    val roomId: String = "",
    val roomTitle: String = "",
    val imageUrl: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val guestName: String = "",
    val totalGuests: String = "",
    val bookingStatus: String = "", // Upcoming or Past
    val timestamp: Long = 0L
)

// ---------------------- MAIN ROOM LIST SCREEN ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRoomsScreen(
    onBack: () -> Unit = {},
    onRoomClick: (RoomModel) -> Unit
) {
    val context = LocalContext.current
    val rooms = remember { mutableStateListOf<RoomModel>() }

    // Fetch all rooms from all admins
    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance().reference.child("Rooms")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms.clear()
                snapshot.children.forEach { adminNode ->
                    adminNode.children.forEach { roomNode ->
                        roomNode.getValue(RoomModel::class.java)?.let { rooms.add(it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Available Rooms") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .background(Color(0xFFF5F5F5))
                .fillMaxSize()
        ) {
            items(rooms) { room ->
                RoomCard(room = room, onClick = { onRoomClick(room) })
            }
        }
    }
}

// ---------------------- ROOM CARD ----------------------
@Composable
fun RoomCard(
    room: RoomModel,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column {

            // ---------- ROOM IMAGE ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (room.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(room.imageUrl),
                        contentDescription = "Room Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEAEAEA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", color = Color.Gray)
                    }
                }

                // PRICE TAG
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "£${room.price}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- TITLE ----------
            Text(
                text = room.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(6.dp))

            // ---------- ICONS ----------
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoIconItem(icon = Icons.Default.Person, label = "${room.guests} Guests")
                InfoIconItem(icon = Icons.Default.Bed, label = "${room.beds} Beds")
                InfoIconItem(icon = Icons.Default.Bathtub, label = "${room.baths} Baths")
            }

            Spacer(Modifier.height(16.dp))

            // ---------- BOOK NOW BUTTON ----------
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Now", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun InfoIconItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 14.sp, color = Color.Gray)
    }
}


object CustomerSelection {
    var room = RoomModel()
}

// ---------------------- BOOK ROOM SCREEN ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRoomScreenOld(
    room: RoomModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var guestName by remember { mutableStateOf("") }
    var totalGuests by remember { mutableStateOf("") }

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    fun openDatePicker(onSelect: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                val selected = "$d-${m + 1}-$y"
                onSelect(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Room") },
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

            // ROOM SUMMARY
            Text(room.title, fontSize = 20.sp)
            Image(
                painter = rememberAsyncImagePainter(room.imageUrl),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                alignment = Alignment.Center
            )
            Spacer(Modifier.height(20.dp))

            // DATE PICKERS
            OutlinedTextField(
                value = fromDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("From Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDatePicker { fromDate = it }
                    }
            )
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = toDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("To Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDatePicker { toDate = it }
                    }
            )
            Spacer(Modifier.height(14.dp))

            // NAME
            OutlinedTextField(
                value = guestName,
                onValueChange = { guestName = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))

            // GUEST COUNT
            OutlinedTextField(
                value = totalGuests,
                onValueChange = { totalGuests = it.filter { c -> c.isDigit() } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Total Guests") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            // BOOKING BUTTON
            Button(
                onClick = {
                    if (fromDate.isBlank() || toDate.isBlank() || guestName.isBlank() || totalGuests.isBlank()) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val today = sdf.parse(sdf.format(Date()))!!.time
                    val fromMillis = sdf.parse(fromDate)!!.time

                    val status = if (fromMillis >= today) "Upcoming" else "Past"

                    val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown"
                    val db = FirebaseDatabase.getInstance().reference
                        .child("Bookings")
                        .child(userEmail)

                    val bookingId = UUID.randomUUID().toString()

                    val booking = BookingModel(
                        bookingId = bookingId,
                        roomId = room.roomId,
                        roomTitle = room.title,
                        imageUrl = room.imageUrl,
                        fromDate = fromDate,
                        toDate = toDate,
                        guestName = guestName,
                        totalGuests = totalGuests,
                        bookingStatus = status,
                        timestamp = System.currentTimeMillis()
                    )

                    db.child(bookingId).setValue(booking)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Booked Successfully!", Toast.LENGTH_LONG)
                                .show()
                            onBack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Booking Failed: ${it.message}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirm Booking")
            }
        }
    }
}

// ---------------------- PREVIEW ----------------------
@Preview(showBackground = true)
@Composable
fun PreviewRooms() {
    ViewRoomsScreen(onRoomClick = {})
}

class ViewRoomsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewRoomsScreen(
                onBack = {
                    finish()
                },
                onRoomClick = { room ->
                    CustomerSelection.room = room
                    startActivity(Intent(this, BookRoomActivity::class.java))

                })
        }
    }
}

