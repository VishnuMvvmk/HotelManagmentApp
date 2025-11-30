package vishnuproject.hotelmanagmentapp.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vishnuproject.hotelmanagmentapp.R

data class RoomItem(
    val title: String,
    val location: String,
    val price: String
)

@Preview(showBackground = true, heightDp = 900)
@Composable
fun RoomsListScreenPreview() {
    MaterialTheme {
        RoomsListScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsListScreen(
    onBack: () -> Unit = {},
    onBookClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {

    val rooms = remember {
        listOf(
            RoomItem("Deluxe King Suite", "Birmingham, UK", "£120.00"),
            RoomItem("Executive Queen Room", "London, UK", "£145.00"),
            RoomItem("Ocean View Room", "Brighton, UK", "£180.00"),
            RoomItem("Premium Twin Bed", "Manchester, UK", "£110.00"),
            RoomItem("Luxury Penthouse", "Newcastle, UK", "£250.00"),
            RoomItem("Studio Apartment", "Glasgow, UK", "£95.00"),
            RoomItem("Superior King Room", "Leeds, UK", "£130.00"),
            RoomItem("Family Suite", "Liverpool, UK", "£160.00"),
            RoomItem("Garden View Deluxe", "Sheffield, UK", "£105.00"),
            RoomItem("Royal Heritage Room", "York, UK", "£170.00")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Rooms",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            items(rooms) { room ->
                RoomCard(
                    room = room,
                    onBookClick = onBookClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
fun RoomCard(
    room: RoomItem,
    onBookClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.sample_hotel_image),
                    contentDescription = room.title,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "ROOM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )

                    Text(
                        text = room.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = room.location,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = room.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                IconButton(
                    onClick = { onDeleteClick(room.title) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.DarkGray
                    )
                }
            }

            Button(
                onClick = { onBookClick(room.title) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("View")
            }
        }
    }
}


class ViewRoomsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomsListScreen(onBack = {
                finish()
            })
        }
    }
}