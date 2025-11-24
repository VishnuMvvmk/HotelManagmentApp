package vishnuproject.hotelmanagmentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotelHomeScreen()
        }
    }
}

data class Booking(
    val id: Int,
    val customerName: String,
    val roomType: String,
    val checkInDate: String
)

// ------------------------ HOME SCREEN ------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelHomeScreen() {

    val recentBookings = remember {
        mutableStateOf(
            listOf(
                Booking(1, "John Doe", "Deluxe Room", "Today"),
                Booking(2, "Emma Brown", "Suite Room", "Tomorrow"),
                Booking(3, "Michael Smith", "Standard Room", "Today")
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hotel Manager") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // ---------- Dashboard Boxes ----------
            item { DashboardSection() }

            // ---------- Quick Actions ----------
            item { QuickActionsSection() }

            // ---------- Recent Bookings ----------
            item {
                Text(
                    "Recent Bookings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            items(recentBookings.value) { booking ->
                BookingListItem(booking)
            }
        }
    }
}

// ------------------------ DASHBOARD ------------------------

@Composable
fun DashboardSection() {

    Text(
        "Dashboard",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column {

        Row(Modifier.fillMaxWidth()) {
            DashboardCard("Occupied Rooms", "42", Icons.Default.Home, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            DashboardCard("Available Rooms", "18", Icons.Default.Home, Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            DashboardCard("Today's Bookings", "12", Icons.Default.Home, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            DashboardCard("Total Revenue", "₹86,000", Icons.Default.DateRange, Modifier.weight(1f))
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(110.dp),
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

    Text(
        "Quick Actions",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Row(Modifier.fillMaxWidth()) {
        QuickActionCard("Add Booking", Icons.Default.Add, Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        QuickActionCard("Add Customer", Icons.Default.Person, Modifier.weight(1f))
    }

    Spacer(Modifier.height(12.dp))

    Row(Modifier.fillMaxWidth()) {
        QuickActionCard("View Rooms", Icons.Default.Home, Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        QuickActionCard("Manage Staff", Icons.Default.Person, Modifier.weight(1f))
    }
}

@Composable
fun QuickActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {

    Card(
        modifier = modifier
            .height(95.dp)
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

// ------------------------ BOOKING ITEM ------------------------

@Composable
fun BookingListItem(booking: Booking) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp)
    ) {

        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(booking.customerName, fontWeight = FontWeight.Bold)
                Text(booking.roomType, color = Color.Gray)
                Text("Check-in: ${booking.checkInDate}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotelHomeScreenPreview() {
    HotelHomeScreen()
}