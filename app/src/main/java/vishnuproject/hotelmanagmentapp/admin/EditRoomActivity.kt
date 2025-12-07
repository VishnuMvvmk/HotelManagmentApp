package vishnuproject.hotelmanagmentapp.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import vishnuproject.hotelmanagmentapp.UserPrefs

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


// --------------------- MAIN EDIT ROOM SCREEN ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoomScreen(
    room: RoomModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Prefilled fields
    var title by remember { mutableStateOf(room.title) }
    var description by remember { mutableStateOf(room.description) }
    var price by remember { mutableStateOf(room.price) }
    var guests by remember { mutableStateOf(room.guests) }
    var beds by remember { mutableStateOf(room.beds) }
    var baths by remember { mutableStateOf(room.baths) }

    val amenities = remember { allAmenities() }
    val selectedAmenityIds = remember { mutableStateListOf<String>().apply { addAll(room.amenities) } }

    // Image states
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var updating by remember { mutableStateOf(false) }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    // Load bitmap preview on new image
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { u ->
            val bytes = readBytesFromUri(u, context.contentResolver)
            previewBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Room") },
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
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {

            // -------- IMAGE PREVIEW --------
            Text("Room Image", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val painter = when {
                previewBitmap != null -> rememberAsyncImagePainter(previewBitmap)
                else -> rememberAsyncImagePainter(room.imageUrl)
            }

            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(10.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Change Image")
            }

            Spacer(Modifier.height(20.dp))

            // -------- TEXT FIELDS --------
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter { c -> c.isDigit() } },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = guests,
                onValueChange = { guests = it.filter { c -> c.isDigit() } },
                label = { Text("Guests") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = beds,
                onValueChange = { beds = it.filter { c -> c.isDigit() } },
                label = { Text("Beds") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = baths,
                onValueChange = { baths = it.filter { c -> c.isDigit() } },
                label = { Text("Baths") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            // -------- AMENITIES --------
            Text("Amenities", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            Column {
                amenities.forEach { amenity ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedAmenityIds.contains(amenity.id))
                                    selectedAmenityIds.remove(amenity.id)
                                else selectedAmenityIds.add(amenity.id)
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        Checkbox(
                            checked = selectedAmenityIds.contains(amenity.id),
                            onCheckedChange = {
                                if (it) selectedAmenityIds.add(amenity.id)
                                else selectedAmenityIds.remove(amenity.id)
                            }
                        )
                        Text(amenity.label)
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            // -------- UPDATE BUTTON --------
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Enter title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    updating = true

                    scope.launch {
                        var finalImageUrl = room.imageUrl

                        // If user selected new image -> upload to ImgBB
                        if (selectedImageUri != null) {
                            val bytes = readBytesFromUri(selectedImageUri!!, context.contentResolver)
                            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                            val uploaded = uploadToImgBB(base64)
                            if (uploaded == null) {
                                updating = false
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_LONG).show()
                                return@launch
                            }
                            finalImageUrl = uploaded
                        }

                        // SAVE TO FIREBASE
                        val userEmail = UserPrefs.getEmail(context)?.replace(".", ",") ?: "unknown"
                        val db = FirebaseDatabase.getInstance().reference
                            .child("Rooms")
                            .child(userEmail)
                            .child(room.roomId)

                        val updatedData = mapOf(
                            "roomId" to room.roomId,
                            "title" to title,
                            "description" to description,
                            "price" to price,
                            "guests" to guests,
                            "beds" to beds,
                            "baths" to baths,
                            "amenities" to selectedAmenityIds.toList(),
                            "imageUrl" to finalImageUrl,
                            "timestamp" to room.timestamp,
                            "ownerEmail" to room.ownerEmail
                        )

                        db.setValue(updatedData).addOnSuccessListener {
                            updating = false
                            Toast.makeText(context, "Room updated!", Toast.LENGTH_LONG).show()
                            onBack()
                        }.addOnFailureListener {
                            updating = false
                            Toast.makeText(context, "Update failed: ${it.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (updating) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Updating...")
                } else {
                    Text("Save Changes")
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

class EditRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // You must pass the RoomModel via intent or a static object
        val room = SelectedData.room

        setContent {
            EditRoomScreen(room = room) {
                finish()
            }
        }
    }
}

object SelectedData{
    var room = RoomModel()
}


