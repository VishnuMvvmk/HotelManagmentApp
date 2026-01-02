package s348786vishnuvardhan.hotelmanagmentapp.customer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import s348786vishnuvardhan.hotelmanagmentapp.ui.theme.PrimaryColor


class CustomerHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomerHomeScreen(
                onViewRooms = {
                    startActivity(Intent(this, ViewRoomsActivity::class.java))
                },
                onMyBookings = {
                    startActivity(Intent(this, MyBookingsActivity::class.java))

                },
                onFoodMenu = {
                    startActivity(Intent(this, FoodMenuActivity::class.java))
                },
                onHotelContact = {
                    startActivity(Intent(this, ContactHotelActivity::class.java))
                },
                onServiceRequest = {
                    startActivity(Intent(this, ServiceRequestActivity::class.java))
                },
                onProfile = {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onViewRooms: () -> Unit = {},
    onMyBookings: () -> Unit = {},
    onFoodMenu: () -> Unit = {},
    onHotelContact: () -> Unit = {},
    onServiceRequest: () -> Unit = {},
    onProfile: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Welcome!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryColor
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Manage your stay!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    HomeCard(
                        title = "View Rooms",
                        icon = Icons.Default.Hotel,
                        bgColor = Color(0xFF6C63FF),
                        onClick = onViewRooms,
                        modifier = Modifier.weight(1f)
                    )

                    HomeCard(
                        title = "My Bookings",
                        icon = Icons.Default.Book,
                        bgColor = Color(0xFF4CAF50),
                        onClick = onMyBookings,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    HomeCard(
                        title = "Food Menu",
                        icon = Icons.Default.Fastfood,
                        bgColor = Color(0xFFFF9800),
                        onClick = onFoodMenu,
                        modifier = Modifier.weight(1f)
                    )

                    HomeCard(
                        title = "Contact",
                        icon = Icons.Default.Call,
                        bgColor = Color(0xFFE91E63),
                        onClick = onHotelContact,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    HomeCard(
                        title = "Service Request",
                        icon = Icons.Default.CleaningServices,
                        bgColor = Color(0xFF3F51B5),
                        onClick = onServiceRequest,
                        modifier = Modifier.weight(1f)
                    )

                    HomeCard(
                        title = "Profile",
                        icon = Icons.Default.Person,
                        bgColor = Color(0xFF009688),
                        onClick = onProfile,
                        modifier = Modifier.weight(1f)
                    )
                }

                AboutAndContactSection()
            }
        }
    }
}


@Composable
fun HomeCard(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(bgColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = bgColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomerHome() {
    CustomerHomeScreen()
}


@Composable
fun AboutAndContactSection() {

    Spacer(modifier = Modifier.height(6.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F3F7))
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "About App",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "The Hotel Management System simplifies the business of the hotel like the booking of rooms, customer management, and billing in one online platform.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(14.dp))
                Divider()

                Spacer(Modifier.height(10.dp))

                Text("Developed By", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("Student ID: 00:3787000046", fontSize = 14.sp)
                Text("Student Number: S348786", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Contact Us",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "For any queries or support, reach us at:",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "vishnuvardhanmadineni@gmail.com",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "© 2025 Hotel Management System",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
