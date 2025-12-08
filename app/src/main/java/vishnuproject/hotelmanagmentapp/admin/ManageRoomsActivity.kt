package vishnuproject.hotelmanagmentapp.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vishnuproject.hotelmanagmentapp.UserPrefs

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.*

data class RoomModel(
    val roomId: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val guests: String = "",
    val beds: String = "",
    val baths: String = "",
    val amenities: List<String> = emptyList(),
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val ownerEmail: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRoomsScreen(
    onBack: () -> Unit = {},
    onEditRoom: (RoomModel) -> Unit = {} // pass room to edit screen
) {
    val context = LocalContext.current
    val rooms = remember { mutableStateListOf<RoomModel>() }
    var loading by remember { mutableStateOf(true) }

    /// ----------- FETCH ROOMS UNDER LOGGED-IN USER -----------
    LaunchedEffect(Unit) {
        val userEmail = try {
            UserPrefs.getEmail(context) ?: ""
        } catch (e: Exception) {
            ""
        }
        val sanitized = userEmail.replace(".", ",")

        val dbRef = FirebaseDatabase.getInstance().reference
            .child("Rooms")
            .child(sanitized)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rooms.clear()
                for (roomSnap in snapshot.children) {
                    roomSnap.getValue(RoomModel::class.java)?.let { rooms.add(it) }
                }
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                loading = false
                Toast.makeText(context, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Rooms", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (rooms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No rooms added yet", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .background(Color(0xFFF5F5F5))
                        .fillMaxSize()
                ) {
                    items(rooms) { room ->
                        RoomManageCard(
                            room = room,
                            onEdit = { onEditRoom(room) },
                            onDelete = {
                                deleteRoom(room, context)
                            }
                        )
                    }
                }
            }
        }
    }
}

/// ----------- ROOM CARD UI -----------
@Composable
fun RoomManageCard(
    room: RoomModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // IMAGE
            if (room.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(room.imageUrl),
                    contentDescription = "Room Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TITLE
            Text(room.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // PRICE
            Text("£${room.price}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))

            // AMENITIES
            Text(
                "Amenities: ${room.amenities.joinToString()}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // EDIT / DELETE BUTTONS
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(Modifier.width(6.dp))
                    Text("Edit")
                }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        }
    }
}

/// ----------- DELETE ROOM FUNCTION -----------
fun deleteRoom(room: RoomModel, context: android.content.Context) {
    val userEmail = try {
        UserPrefs.getEmail(context) ?: ""
    } catch (e: Exception) {
        ""
    }
    val sanitized = userEmail.replace(".", ",")

    val dbRef = FirebaseDatabase.getInstance().reference
        .child("Rooms")
        .child(sanitized)
        .child(room.roomId)

    dbRef.removeValue().addOnSuccessListener {
        Toast.makeText(context, "Room deleted", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Delete failed: ${it.message}", Toast.LENGTH_LONG).show()
    }
}

// ---------------- PREVIEW ----------------
@Preview(showBackground = true)
@Composable
fun PreviewManageRooms() {
    ManageRoomsScreen()
}



class ManageRoomsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManageRoomsScreen(
                onBack = {
                    finish()
                },
                onEditRoom = {
                    RoomModel ->
                            SelectedData.room=RoomModel
                            startActivity(
                                android.content.Intent(
                                    this,
                                    EditRoomActivity::class.java
                                )
                            )
                }
            )
        }
    }
}