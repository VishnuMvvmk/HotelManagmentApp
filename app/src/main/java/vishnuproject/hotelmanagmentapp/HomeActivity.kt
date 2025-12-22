package vishnuproject.hotelmanagmentapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import vishnuproject.hotelmanagmentapp.admin.AddRoomActivity
import vishnuproject.hotelmanagmentapp.admin.AdminBookedRoomsActivity
import vishnuproject.hotelmanagmentapp.admin.AdminServicesRequestActivity
import vishnuproject.hotelmanagmentapp.admin.ManageRoomsActivity
import vishnuproject.hotelmanagmentapp.admin.RoomModel
import vishnuproject.hotelmanagmentapp.admin.SetFoodMenuActivity
import vishnuproject.hotelmanagmentapp.admin.SetHotelContactDetailsActivity
import vishnuproject.hotelmanagmentapp.customer.BookingModel
import vishnuproject.hotelmanagmentapp.customer.ProfileActivity
import vishnuproject.hotelmanagmentapp.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HotelHomeScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelHomeScreen() {

    val context = LocalContext.current
    val adminEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown_admin"

    var totalRooms by remember { mutableStateOf(0) }
    var occupiedRooms by remember { mutableStateOf(0) }
    var availableRooms by remember { mutableStateOf(0) }
    var todaysBookings by remember { mutableStateOf(0) }
    var totalRevenue by remember { mutableStateOf(0) }

    // ---------------------------------------------------
    // LOAD REALTIME STATS FROM FIREBASE
    // ---------------------------------------------------
    LaunchedEffect(Unit) {

        val roomsRef = FirebaseDatabase.getInstance().reference
            .child("Rooms").child(adminEmail)

        val bookingsRef = FirebaseDatabase.getInstance().reference
            .child("Bookings")

        // ---------- LOAD TOTAL ROOMS ----------
        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalRooms = snapshot.childrenCount.toInt()
                availableRooms = totalRooms - occupiedRooms
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // ---------- LOAD BOOKINGS ----------
        bookingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                occupiedRooms = 0
                todaysBookings = 0
                totalRevenue = 0

                val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                val bookingsList = mutableListOf<BookingModel>()
                val roomsMap = mutableMapOf<String, RoomModel>()

                // Load all room prices first
                roomsRef.get().addOnSuccessListener { roomSnap ->
                    roomSnap.children.forEach { roomNode ->
                        val room = roomNode.getValue(RoomModel::class.java)
                        if (room != null) roomsMap[room.roomId] = room
                    }

                    // Iterate through all users' bookings
                    snapshot.children.forEach { userNode ->
                        userNode.children.forEach { bookSnap ->
                            val booking = bookSnap.getValue(BookingModel::class.java) ?: return@forEach
                            bookingsList.add(booking)
                        }
                    }

                    for (b in bookingsList) {

                        // Today's bookings
                        if (b.fromDate == today) todaysBookings++

                        // Calculate occupied rooms (if today falls between fromDate & toDate)
                        try {
                            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val todayMillis = sdf.parse(today)?.time ?: 0
                            val fromMillis = sdf.parse(b.fromDate)?.time ?: 0
                            val toMillis = sdf.parse(b.toDate)?.time ?: 0

                            if (todayMillis in fromMillis..toMillis) {
                                occupiedRooms++
                            }
                        } catch (_: Exception) {}

                        // Revenue calculation
                        val room = roomsMap[b.roomId]
                        val price = room?.price?.toIntOrNull() ?: 0
                        totalRevenue += price
                    }

                    availableRooms = totalRooms - occupiedRooms
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---------------------------------------------------
    // UI STARTS HERE
    // ---------------------------------------------------

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hotel Manager", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, ProfileActivity::class.java))

                    }) {
                        Icon(Icons.Default.AccountBox, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryColor
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            item {
                DashboardSection(
                    occupiedRooms,
                    availableRooms,
                    todaysBookings,
                    "£$totalRevenue"
                )
            }

            item { QuickActionsSection() }
        }
    }
}

// ------------------------ DASHBOARD ------------------------

@Composable
fun DashboardSection(
    occupiedRooms: Int,
    availableRooms: Int,
    todaysBookings: Int,
    totalRevenue: String
) {

    Text(
        "Dashboard",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column {

        Row(Modifier.fillMaxWidth()) {
            DashboardCard("Occupied Rooms", occupiedRooms.toString(), Icons.Default.MeetingRoom, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            DashboardCard("Available Rooms", availableRooms.toString(), Icons.Default.DoorBack, Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            DashboardCard("Today's Bookings", todaysBookings.toString(), Icons.Default.Event, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            DashboardCard("Total Revenue", totalRevenue, Icons.Default.CurrencyPound, Modifier.weight(1f))
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null)
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(value, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }
        }
    }
}

// ------------------------ QUICK ACTIONS ------------------------

@Composable
fun QuickActionsSection() {

    val context = LocalContext.current

    Text(
        "Quick Actions",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Row(Modifier.fillMaxWidth()) {
        QuickActionCard("Add Room", Icons.Default.Add, Modifier.weight(1f)) {
            context.startActivity(Intent(context, AddRoomActivity::class.java))
        }
        Spacer(Modifier.width(12.dp))
        QuickActionCard("Manage Rooms", Icons.Default.Person, Modifier.weight(1f)) {
            context.startActivity(Intent(context, ManageRoomsActivity::class.java))
        }
    }

    Spacer(Modifier.height(12.dp))

    Row(Modifier.fillMaxWidth()) {
        QuickActionCard("Food Menu", Icons.Default.Fastfood, Modifier.weight(1f)) {
            context.startActivity(Intent(context, SetFoodMenuActivity::class.java))
        }
        Spacer(Modifier.width(12.dp))
        QuickActionCard("Contact Details", Icons.Default.Phone, Modifier.weight(1f)) {
            context.startActivity(Intent(context, SetHotelContactDetailsActivity::class.java))
        }

    }

    Spacer(Modifier.height(12.dp))

    Row(Modifier.fillMaxWidth()) {
        QuickActionCard("Service Request", Icons.Default.CleaningServices, Modifier.weight(1f)) {
            context.startActivity(Intent(context, AdminServicesRequestActivity::class.java))
        }
        Spacer(Modifier.width(12.dp))
        QuickActionCard("Bookings", Icons.Default.Bookmark, Modifier.weight(1f)) {
            context.startActivity(Intent(context, AdminBookedRoomsActivity::class.java))
        }

    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(95.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    HotelHomeScreen()
}
