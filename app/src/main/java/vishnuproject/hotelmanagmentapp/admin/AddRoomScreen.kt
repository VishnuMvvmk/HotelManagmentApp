package vishnuproject.hotelmanagmentapp.admin

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import vishnuproject.hotelmanagmentapp.UserPrefs
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

// ----------------- Amenity model & list -----------------
data class AmenityItem(val id: String, val label: String)

fun allAmenities(): List<AmenityItem> = listOf(
    AmenityItem("wifi", "Wi-Fi"),
    AmenityItem("ac", "AC"),
    AmenityItem("pool", "Pool"),
    AmenityItem("tv", "TV"),
    AmenityItem("parking", "Parking"),
    AmenityItem("breakfast", "Breakfast"),
    AmenityItem("bathtub", "Bathtub"),
    AmenityItem("gym", "Gym"),
    AmenityItem("kitchen", "Kitchen"),
    AmenityItem("elevator", "Elevator"),
    AmenityItem("heating", "Heating"),
    AmenityItem("workspace", "Workspace"),
    AmenityItem("pet_friendly", "Pet Friendly"),
    AmenityItem("iron", "Iron"),
    AmenityItem("laundry", "Laundry"),
    AmenityItem("fireplace", "Fireplace"),
    AmenityItem("pool_table", "Pool Table"),
    AmenityItem("spa", "Spa"),
    AmenityItem("shuttle", "Shuttle"),
    AmenityItem("child_friendly", "Child Friendly")
)

// ----------------- UI Chips -----------------
@Composable
fun AmenityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.White
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray

    Surface(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = background,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SelectedAmenityChip(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ----------------- ImgBB Key -----------------
private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b" // <- your key (from user)

// ----------------- Helper: Read URI to ByteArray -----------------
suspend fun readBytesFromUri(uri: Uri, contentResolver: ContentResolver): ByteArray =
    withContext(Dispatchers.IO) {
        val input: InputStream? = contentResolver.openInputStream(uri)
        val baos = ByteArrayOutputStream()
        input?.use { stream ->
            val buffer = ByteArray(4096)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
        }
        baos.toByteArray()
    }

// ----------------- Helper: Upload to ImgBB (returns image URL) -----------------
suspend fun uploadToImgBB(base64Image: String): String? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        // ImgBB accepts 'image' param as base64 string
        val form = FormBody.Builder()
            .add("key", IMGBB_API_KEY)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(form)
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return@withContext null
            if (!response.isSuccessful) return@withContext null

            val json = JSONObject(body)
            // Look for data -> url or display_url
            val data = json.optJSONObject("data")
            return@withContext data?.optString("url") ?: data?.optString("display_url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

// ----------------- Main Composable Screen -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreenWithAmenitiesHorizontalWithImageUpload(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var roomName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var beds by remember { mutableStateOf("") }
    var baths by remember { mutableStateOf("") }

    // Amenity data & selection state
    val amenities = remember { allAmenities() }
    val selectedAmenityIds = remember { mutableStateListOf<String>() } // stores selected amenity ids

    // Image selection / preview state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // Activity result launcher to pick image
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

// Load bitmap preview when URI changes
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            val bytes = readBytesFromUri(uri, context.contentResolver)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            selectedImageBitmap = bmp
        }
    }


    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Room", fontWeight = FontWeight.SemiBold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Room title
            Text("Room Title", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = roomName,
                onValueChange = { roomName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter room title") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))

            // Description
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                placeholder = { Text("Enter description") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))

            // Price & Guests
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Price", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { input -> price = input.filter { it.isDigit() || it == '.' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 2500") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Guests", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = guests,
                        onValueChange = { input -> guests = input.filter { it.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 4") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Beds & Baths
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Beds", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = beds,
                        onValueChange = { input -> beds = input.filter { it.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 2") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Baths", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = baths,
                        onValueChange = { input -> baths = input.filter { it.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 1") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Image picker
            Text("Room Image", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedImageBitmap != null) {
                    Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    // placeholder box
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFEFEF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", textAlign = TextAlign.Center)
                    }
                }

                Column {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Select Image")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Selected image will be upload",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Select Amenities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(8.dp))

            val rowState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rowState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                amenities.forEach { amenity ->
                    val selected = amenity.id in selectedAmenityIds
                    AmenityChip(
                        label = amenity.label,
                        selected = selected,
                        onClick = {
                            if (selected) selectedAmenityIds.remove(amenity.id)
                            else selectedAmenityIds.add(amenity.id)
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Show selected amenities as chips/text below the row
            if (selectedAmenityIds.isNotEmpty()) {
                Text("Selected:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedAmenityIds.forEach { id ->
                        val label = amenities.firstOrNull { it.id == id }?.label ?: id
                        SelectedAmenityChip(label = label)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
            } else {
                Spacer(Modifier.height(8.dp))
            }

            // Post button - upload image then save data
            Button(
                onClick = {
                    // launch coroutine to upload & save
                    if (roomName.isBlank()) {
                        Toast.makeText(context, "Please enter room name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (price.isBlank()) {
                        Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (guests.isBlank()) {
                        Toast.makeText(context, "Please enter guests", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedImageUri == null) {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // start upload
                    uploading = true

                    // Use coroutine scope in Compose
                    scope.launch {
                        try {
                            // Read image bytes
                            val bytes = readBytesFromUri(selectedImageUri!!, context.contentResolver)
                            // Convert to Base64 (ImgBB expects base64 without data URI prefix)
                            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                            // Upload to ImgBB
                            val imgUrl = uploadToImgBB(base64)

                            if (imgUrl == null) {
                                uploading = false
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            // Prepare data map
                            val amenitiesForSave = selectedAmenityIds.toList()
                            val roomId = UUID.randomUUID().toString()
                            val timestamp = System.currentTimeMillis()

                            // get user email from UserPrefs
                            val userEmail = try {
                                UserPrefs.getEmail(context) ?: "unknown_user"
                            } catch (e: Exception) {
                                "unknown_user"
                            }

                            // sanitize email for Firebase key (replace '.' with ',')
                            val sanitizedEmail = userEmail.replace(".", ",")

                            val roomData = mapOf(
                                "roomId" to roomId,
                                "title" to roomName,
                                "description" to description,
                                "price" to price,
                                "guests" to guests,
                                "beds" to beds,
                                "baths" to baths,
                                "amenities" to amenitiesForSave,
                                "imageUrl" to imgUrl,
                                "timestamp" to timestamp,
                                "ownerEmail" to userEmail
                            )

                            // Save to Firebase Realtime Database under Rooms/<sanitizedEmail>/<roomId>
                            val db = FirebaseDatabase.getInstance().reference
                            db.child("Rooms")
                                .child(sanitizedEmail)
                                .child(roomId)
                                .setValue(roomData)
                                .addOnSuccessListener {
                                    uploading = false
                                    Toast.makeText(context, "Room posted successfully", Toast.LENGTH_LONG).show()
                                    // reset form
                                    roomName = ""
                                    description = ""
                                    price = ""
                                    guests = ""
                                    beds = ""
                                    baths = ""
                                    selectedAmenityIds.clear()
                                    selectedImageUri = null
                                    selectedImageBitmap = null
                                }
                                .addOnFailureListener { ex ->
                                    uploading = false
                                    Toast.makeText(context, "Failed to save room: ${ex.message}", Toast.LENGTH_LONG).show()
                                }

                        } catch (e: Exception) {
                            uploading = false
                            e.printStackTrace()
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uploading
            ) {
                if (uploading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Posting...")
                } else {
                    Text("Post", color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ----------------- Preview / Activity -----------------
@Preview(showBackground = true, heightDp = 900)
@Composable
fun AddRoomWithAmenitiesPreview() {
    MaterialTheme {
        AddRoomScreenWithAmenitiesHorizontalWithImageUpload {}
    }
}

class AddRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddRoomScreenWithAmenitiesHorizontalWithImageUpload(onBack = { finish() })
        }
    }
}
