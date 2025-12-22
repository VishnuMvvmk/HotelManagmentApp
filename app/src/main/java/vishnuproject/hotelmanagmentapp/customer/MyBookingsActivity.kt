package vishnuproject.hotelmanagmentapp.customer

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import vishnuproject.hotelmanagmentapp.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

//    val upcoming = allBookings.filter { it.bookingStatus == "Upcoming" }
//    val past = allBookings.filter { it.bookingStatus == "Past" }

    val upcoming = allBookings.filter { !isPastBooking(it.fromDate) }
    val past = allBookings.filter { isPastBooking(it.fromDate) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White) },
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
                            showCancel = !isPastBooking(booking.fromDate)
                        )
                    }
                }
            }
        }
    }
}


fun isPastBooking(fromDate: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val bookingDate = sdf.parse(fromDate)
        val today = sdf.parse(sdf.format(Date())) // today without time
        bookingDate != null && bookingDate.before(today)
    } catch (e: Exception) {
        false
    }
}

@Composable
fun BookingCard(
    booking: BookingModel,
    showCancel: Boolean,
    onWriteReview: (BookingModel) -> Unit = {}
) {
    val context = LocalContext.current
    val userEmail = UserPrefs.getEmail(context).replace(".", ",") ?: "unknown_user"

    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(false) }
    var loadingReview by remember { mutableStateOf(true) }

    // ---------- CHECK IF REVIEW EXISTS ----------
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().reference
            .child("Reviews")
            .child(booking.roomId)
            .child(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        rating = snapshot.child("rating").getValue(Int::class.java) ?: 0
                        reviewText = snapshot.child("review").getValue(String::class.java) ?: ""
                        reviewSubmitted = true
                    }
                    loadingReview = false
                }

                override fun onCancelled(error: DatabaseError) {
                    loadingReview = false
                }
            })
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {

            // ---------- IMAGE ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {

                Image(
                    painter = rememberAsyncImagePainter(booking.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xB0000000))
                            )
                        )
                )

                if(!showCancel)
                    booking.bookingStatus="Past"

                // Status badge
                StatusBadge(booking.bookingStatus)
            }

            Spacer(Modifier.height(14.dp))

            // ---------- TITLE ----------
            Text(
                booking.roomTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(10.dp))

            // ---------- DATE ----------
            InfoRow(
                icon = Icons.Default.DateRange,
                text = "From ${booking.fromDate}  •  To ${booking.toDate}"
            )

            Spacer(Modifier.height(6.dp))

            // ---------- GUEST ----------
            InfoRow(
                icon = Icons.Default.Person,
                text = "${booking.guestName} • ${booking.totalGuests} Guests"
            )

            Spacer(Modifier.height(12.dp))

            // ======================================================
            // UPCOMING BOOKING → SHOW CHECK-IN / CHECK-OUT TIME
            // ======================================================
            if (showCancel) {

                InfoRow(
                    icon = Icons.Default.Login,
                    text = "Check-in: 12:00 PM"
                )

                Spacer(Modifier.height(4.dp))

                InfoRow(
                    icon = Icons.Default.Logout,
                    text = "Check-out: 11:00 AM"
                )

                Spacer(Modifier.height(16.dp))

                // Cancel button
                Button(
                    onClick = {
                        val email =
                            UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_user"

                        FirebaseDatabase.getInstance().reference
                            .child("Bookings")
                            .child(email)
                            .child(booking.bookingId)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Booking Cancelled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Cancel Booking",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(14.dp))
            }

            // ======================================================
            // PAST BOOKING → SHOW RATE & REVIEW
            // ======================================================
            if (!showCancel && !loadingReview) {

                Spacer(Modifier.height(8.dp))

                Text(
                    "Rate your stay",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(6.dp))

                RatingStars(
                    rating = rating,
                    enabled = !reviewSubmitted,
                    onRatingChange = { rating = it }
                )

                Spacer(Modifier.height(10.dp))

                if (!reviewSubmitted) {
                    OutlinedButton(
                        onClick = {
                            if (rating == 0) {
                                Toast.makeText(context, "Please give rating", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }

                            val review = ReviewModel(
                                bookingId = booking.bookingId,
                                roomId = booking.roomId,
                                userEmail = userEmail,
                                rating = rating,
                                review = reviewText,
                                timestamp = System.currentTimeMillis()
                            )

                            FirebaseDatabase.getInstance().reference
                                .child("Reviews")
                                .child(booking.roomId)
                                .child(userEmail)
                                .setValue(review)
                                .addOnSuccessListener {
                                    reviewSubmitted = true
                                    Toast.makeText(context, "Review submitted", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Submit Review")
                    }
                } else {
                    Text(
                        "✔ Review submitted",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

data class ReviewModel(
    val bookingId: String = "",
    val roomId: String = "",
    val userEmail: String = "",
    val rating: Int = 0,
    val review: String = "",
    val timestamp: Long = 0L
)


@Composable
fun RatingStars(
    rating: Int,
    enabled: Boolean,
    onRatingChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = enabled) {
                        onRatingChange(index + 1)
                    }
            )
        }
    }
}



@Composable
fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .background(
                if (status == "Upcoming") Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            status,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}



@Composable
fun BookingCardOld(
    booking: BookingModel,
    showCancel: Boolean,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {

            // ---------- IMAGE SECTION ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {

                Image(
                    painter = rememberAsyncImagePainter(booking.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xB0000000))
                            )
                        )
                )

                if(isPastBooking(booking.fromDate))
                    booking.bookingStatus = "Past"

                // Status badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            if (booking.bookingStatus == "Upcoming")
                                Color(0xFF4CAF50)
                            else
                                Color(0xFF9E9E9E),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = booking.bookingStatus,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ---------- TITLE ----------
            Text(
                booking.roomTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(10.dp))

            // ---------- DATE ROW ----------
            InfoRow(
                icon = Icons.Default.DateRange,
                text = "From ${booking.fromDate}  •  To ${booking.toDate}"
            )

            Spacer(Modifier.height(6.dp))

            // ---------- GUEST ROW ----------
            InfoRow(
                icon = Icons.Default.Person,
                text = "${booking.guestName} • ${booking.totalGuests} Guests"
            )

            Spacer(Modifier.height(16.dp))

            // ---------- CANCEL BUTTON ----------
            if (showCancel) {
                Button(
                    onClick = {
                        val email =
                            UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_user"

                        FirebaseDatabase.getInstance().reference
                            .child("Bookings")
                            .child(email)
                            .child(booking.bookingId)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Booking Cancelled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Cancel Booking",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(14.dp))
            } else {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
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