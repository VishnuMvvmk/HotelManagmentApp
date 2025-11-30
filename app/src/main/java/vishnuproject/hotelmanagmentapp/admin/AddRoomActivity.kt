package vishnuproject.hotelmanagmentapp.admin

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen() {
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



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Room",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
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


            Button(
                onClick = {
                    when {
                        roomName.isBlank() -> Toast.makeText(context, "Please enter room name", Toast.LENGTH_SHORT).show()
                        price.isBlank() -> Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                        guests.isBlank() -> Toast.makeText(context, "Please enter guests", Toast.LENGTH_SHORT).show()
                        else -> {
                            Toast.makeText(context, "Selected amenities: ${selectedAmenityIds.joinToString()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Post", color = Color.White)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun AddRoomWithAmenitiesPreview() {
    MaterialTheme {
        AddRoomScreen()
    }
}


class AddRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AddRoomScreen()
        }
    }
}