package vishnuproject.hotelmanagmentapp.customer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vishnuproject.hotelmanagmentapp.UserPrefs



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val allBookings = remember { mutableStateListOf<BookingModel>() }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Upcoming, 1 = Past

    // Load bookings from Firebase
    LaunchedEffect(Unit) {
        val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_user"

        FirebaseDatabase.getInstance().reference
            .child("Bookings")
            .child(userEmail)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    allBookings.clear()
                    snapshot.children.forEach { snap ->
                        snap.getValue(BookingModel::class.java)?.let { allBookings.add(it) }
                    }

                    // Sort by upcoming first
                    allBookings.sortBy { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    val upcoming = allBookings.filter { it.bookingStatus == "Upcoming" }
    val past = allBookings.filter { it.bookingStatus == "Past" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
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
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            // ---------------- TABS ----------------
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Upcoming", modifier = Modifier.padding(14.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Past", modifier = Modifier.padding(14.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- BOOKING LIST ----------------
            val list = if (selectedTab == 0) upcoming else past

            if (list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No bookings found", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(list) { booking ->
                        BookingCard(
                            booking = booking,
                            showCancel = booking.bookingStatus == "Upcoming"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingModel,
    showCancel: Boolean,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {

            // --------- IMAGE SECTION WITH GRADIENT ---------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(booking.imageUrl),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xAA000000))
                            )
                        )
                )

                // status badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            if (booking.bookingStatus == "Upcoming") Color(0xFF4CAF50)
                            else Color.Gray,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        booking.bookingStatus,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // --------- TITLE ---------
            Text(
                booking.roomTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // --------- DATES ---------
            Text(
                "From: ${booking.fromDate}",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                "To: ${booking.toDate}",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(10.dp))

            // --------- GUEST DETAILS ---------
            Text(
                "Guest: ${booking.guestName}",
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                "Total Guests: ${booking.totalGuests}",
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(16.dp))

            // --------- CANCEL BUTTON (ONLY UPCOMING) ---------
            if (showCancel) {
                Button(
                    onClick = {
                        val email = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_user"

                        FirebaseDatabase.getInstance().reference
                            .child("Bookings")
                            .child(email)
                            .child(booking.bookingId)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel Booking", color = Color.White)
                }

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}


class MyBookingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyBookingsScreen (
                onBack = {
                    finish()
                }
            )
        }
    }
}