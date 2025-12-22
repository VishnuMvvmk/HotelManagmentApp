package vishnuproject.hotelmanagmentapp.customer


import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import vishnuproject.hotelmanagmentapp.UserPrefs
import vishnuproject.hotelmanagmentapp.admin.RoomModel
import vishnuproject.hotelmanagmentapp.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class BookRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookRoomScreen(
                room = CustomerSelection.room,
                onBack = {
                finish()
            }
            )


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookRoomScreen(
    room: RoomModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var guestName by remember { mutableStateOf("") }
    var totalGuests by remember { mutableStateOf("") }

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun openDatePicker(onSelect: (String) -> Unit, minDate: Long? = null) {
        val dp = DatePickerDialog(
            context,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onSelect(dateFormat.format(c.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (minDate != null) dp.datePicker.minDate = minDate
        dp.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Room", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ---------------- HEADER IMAGE ----------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(room.imageUrl),
                    contentDescription = "Room Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Soft gradient at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xB3000000))
                            )
                        )
                )

                // Price Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .background(Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("£${room.price}", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- ROOM TITLE ----------------
            Text(
                room.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // ---------------- ROOM STATS ----------------
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconStat(Icons.Default.Person, "${room.guests} Guests")
                IconStat(Icons.Default.Bed, "${room.beds} Beds")
                IconStat(Icons.Default.Bathtub, "${room.baths} Baths")
            }

            Spacer(Modifier.height(16.dp))

            // ---------------- DESCRIPTION ----------------
            Text(
                "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                room.description,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(Modifier.height(10.dp))

            // ---------------- AMENITIES ----------------
            if (room.amenities.isNotEmpty()) {
                Text(
                    "Amenities",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    room.amenities.forEach { label ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEDEAFF), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = Color(0xFF6C63FF))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // ---------------- BOOKING FORM ----------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {

                Text("Booking Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(16.dp))

                // FROM DATE
                PremiumDateField(
                    label = "From Date",
                    value = fromDate,
                    onClick = {
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis

                        openDatePicker({ fromDate = it }, today)
                    }
                )

                Spacer(Modifier.height(12.dp))

                // TO DATE
                PremiumDateField(
                    label = "To Date",
                    value = toDate,
                    onClick = {
                        val minDate = if (fromDate.isNotBlank())
                            dateFormat.parse(fromDate)?.time
                        else Calendar.getInstance().timeInMillis

                        openDatePicker({ toDate = it }, minDate)
                    }
                )

                Spacer(Modifier.height(12.dp))

                // NAME
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = totalGuests,
                    onValueChange = { totalGuests = it.filter { c -> c.isDigit() } },
                    label = { Text("Total Guests") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------------- CONFIRM BUTTON ----------------
            Button(
                onClick = {
                    if (fromDate.isBlank() || toDate.isBlank() || guestName.isBlank() || totalGuests.isBlank()) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val sdf = dateFormat
                    val fromMillis = sdf.parse(fromDate)?.time ?: -1L
                    val toMillis = sdf.parse(toDate)?.time ?: -1L

                    if (toMillis < fromMillis) {
                        Toast.makeText(context, "To date cannot be before From date", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val todayStart = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis

                    val status = if (fromMillis >= todayStart) "Upcoming" else "Past"

                    val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_user"
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
                            Toast.makeText(context, "Booked Successfully!", Toast.LENGTH_LONG).show()
                            onBack()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Booking", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

/* ---------------- Helper Composables ---------------- */

@Composable
fun IconStat(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6C63FF))
        Spacer(Modifier.width(6.dp))
        Text(label, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun PremiumDateField(label: String, value: String, onClick: () -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))

        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select $label") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onClick() }
            )
        }
    }
}

