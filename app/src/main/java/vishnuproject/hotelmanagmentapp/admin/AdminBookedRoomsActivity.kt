package vishnuproject.hotelmanagmentapp.admin


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import vishnuproject.hotelmanagmentapp.customer.BookingModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookedRoomsScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current
    val adminEmail = UserPrefs.ADMIN_MAIL

    val bookings = remember { mutableStateListOf<BookingModel>() }
    val rooms = remember { mutableStateMapOf<String, RoomModel>() }

    var isLoading by remember { mutableStateOf(true) }

    // FORMATTER
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val today = sdf.format(Date())


    // -------------------- LOAD DATA --------------------
    LaunchedEffect(Unit) {

        val roomsRef = FirebaseDatabase.getInstance().reference
            .child("Rooms").child(adminEmail)

        val bookingsRef = FirebaseDatabase.getInstance().reference
            .child("Bookings")

        // Load all rooms (price, title, etc)
        roomsRef.get().addOnSuccessListener { snap ->
            rooms.clear()
            snap.children.forEach {
                val r = it.getValue(RoomModel::class.java)
                if (r != null) rooms[r.roomId] = r
            }
        }

        // Listen to ALL bookings
        bookingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookings.clear()

                snapshot.children.forEach { userNode ->
                    userNode.children.forEach { bookSnap ->
                        val booking = bookSnap.getValue(BookingModel::class.java)
                        if (booking != null) bookings.add(booking)
                    }
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booked Rooms", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        var selectedTab by remember { mutableIntStateOf(0) }

        val upcomingList = bookings.filter { isUpcoming(it.fromDate, today) }
        val occupiedList = bookings.filter { isTodayBetween(it.fromDate, it.toDate, today) }
        val pastList = bookings.filter { isPast(it.toDate, today) }

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Upcoming", modifier = Modifier.padding(12.dp))
                }
                Tab(selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Occupied", modifier = Modifier.padding(12.dp))
                }
                Tab(selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Past", modifier = Modifier.padding(12.dp))
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                val list = when (selectedTab) {
                    0 -> upcomingList
                    1 -> occupiedList
                    else -> pastList
                }

                items(list) { booking ->
                    BookingAdminPremiumCard(
                        booking = booking,
                        room = rooms[booking.roomId],
                        showReview = selectedTab == 2
                    )
                }
            }
        }

    }
}


@Composable
fun BookingAdminPremiumCard(
    booking: BookingModel,
    room: RoomModel?,
    showReview: Boolean
) {
    val context = LocalContext.current
//    val userEmail = booking.userEmail?.replace(".", ",")
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")

    var rating by remember { mutableIntStateOf(0) }
    var review by remember { mutableStateOf("") }

    // Load review only for past bookings
    LaunchedEffect(showReview) {
        if (showReview) {
            FirebaseDatabase.getInstance().reference
                .child("Reviews")
                .child(booking.roomId)
                .child(userEmail)
                .get()
                .addOnSuccessListener {
                    rating = it.child("rating").getValue(Int::class.java) ?: 0
                    review = it.child("review").getValue(String::class.java) ?: ""
                }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // IMAGE
            Image(
                painter = rememberAsyncImagePainter(booking.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))

            // TITLE
            Text(
                booking.roomTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            AdminInfoRow(Icons.Default.Person, "Guest: ${booking.guestName}")
            AdminInfoRow(Icons.Default.Groups, "Guests: ${booking.totalGuests}")
            AdminInfoRow(Icons.Default.DateRange, "${booking.fromDate} → ${booking.toDate}")

            if (room != null) {
                AdminInfoRow(Icons.Default.AttachMoney, "Price: ₹${room.price}")
            }

            Spacer(Modifier.height(12.dp))

            // ACTIONS (only non-past)
//            if (!showReview) {
//                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                    ActionButton("Check-In", Color(0xFF4CAF50)) {
//                        updateStatus(context, booking, "Checked-In")
//                    }
//                    ActionButton("Check-Out", Color(0xFF2196F3)) {
//                        updateStatus(context, booking, "Checked-Out")
//                    }
//                }
//            }

            // REVIEW SECTION (PAST ONLY)
            if (showReview && rating > 0) {
                Spacer(Modifier.height(14.dp))
                Divider()
                Spacer(Modifier.height(10.dp))

                Text("Customer Review", fontWeight = FontWeight.SemiBold)

                Row {
                    repeat(5) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (it < rating) Color(0xFFFFC107) else Color.LightGray
                        )
                    }
                }

                if (review.isNotBlank()) {
                    Text(
                        text = review,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun AdminInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}


/* ----------------------------- HELPER FUNCTIONS ----------------------------- */

fun isUpcoming(fromDate: String, today: String): Boolean {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.parse(fromDate)?.after(sdf.parse(today)) == true
}

fun isPast(toDate: String, today: String): Boolean {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.parse(toDate)?.before(sdf.parse(today)) == true
}

fun isTodayBetween(from: String, to: String, today: String): Boolean {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val dToday = sdf.parse(today)?.time ?: 0
    val dFrom = sdf.parse(from)?.time ?: 0
    val dTo = sdf.parse(to)?.time ?: 0
    return dToday in dFrom..dTo
}

/* ----------------------------- SECTION TITLE ----------------------------- */

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

/* ----------------------------- BOOKING CARD UI ----------------------------- */

@Composable
fun BookingAdminCard(
    booking: BookingModel,
    room: RoomModel?
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ---------- ROOM IMAGE ----------
            if (!booking.imageUrl.isNullOrEmpty()) {
                androidx.compose.foundation.Image(
                    painter = rememberAsyncImagePainter(booking.imageUrl),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                booking.roomTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text("Guest: ${booking.guestName}")
            Text("Guests Count: ${booking.totalGuests}")
            Text("From: ${booking.fromDate}")
            Text("To: ${booking.toDate}")

            if (room != null) {
                Spacer(Modifier.height(6.dp))
                Text("Room Price: £${room.price}")
            }

            Spacer(Modifier.height(14.dp))

            // ---------- ACTION BUTTONS ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // CHECK-IN
                ActionButton("Check-In", Color(0xFF4CAF50)) {
                    updateStatus(context, booking, "Checked-In")
                }

                // CHECK-OUT
                ActionButton("Check-Out", Color(0xFF2196F3)) {
                    updateStatus(context, booking, "Checked-Out")
                }
            }
        }
    }
}

@Composable
fun ActionButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(label, color = Color.White)
    }
}

/* ----------------------------- UPDATE BOOKING STATUS ----------------------------- */

fun updateStatus(context: android.content.Context, booking: BookingModel, newStatus: String) {
//    val email = booking.customerEmail.replace(".", ",")
//    FirebaseDatabase.getInstance().reference
//        .child("Bookings")
//        .child(email)
//        .child(booking.bookingId)
//        .child("bookingStatus")
//        .setValue(newStatus)
//        .addOnSuccessListener {
//            Toast.makeText(context, "Status Updated!", Toast.LENGTH_SHORT).show()
//        }
}


class AdminBookedRoomsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminBookedRoomsScreen(onBack = {
                finish()
            })
        }
    }
}