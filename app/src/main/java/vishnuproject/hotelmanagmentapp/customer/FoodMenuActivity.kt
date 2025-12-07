package vishnuproject.hotelmanagmentapp.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.*
import vishnuproject.hotelmanagmentapp.UserPrefs

data class FoodItem(
    val itemId: String = "",
    val name: String = "",
    val price: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodMenuScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val foodItems = remember { mutableStateListOf<FoodItem>() }
    var loading by remember { mutableStateOf(true) }

    // -------- Fetch menu from Firebase --------
    LaunchedEffect(Unit) {
        val adminEmail = try {
            UserPrefs.ADMIN_MAIL
        } catch (e: Exception) {
            "unknown_hotel"
        }

        FirebaseDatabase.getInstance().reference
            .child("HotelMenu")
            .child(adminEmail)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    foodItems.clear()
                    snapshot.children.forEach { snap ->
                        snap.getValue(FoodItem::class.java)?.let { foodItems.add(it) }
                    }
                    loading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    loading = false
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Menu", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->

        if (loading) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (foodItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No menu items available", fontSize = 16.sp, color = Color.Gray)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(foodItems) { item ->
                FoodMenuCard(item)
            }
        }
    }
}

@Composable
fun FoodMenuCard(item: FoodItem) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("Freshly Prepared", fontSize = 12.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("£${item.price}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenu() {
    val dummy = listOf(
        FoodItem("1", "Chicken Biryani", "220"),
        FoodItem("2", "Veg Fried Rice", "180")
    )
    Column {
        dummy.forEach { FoodMenuCard(it) }
    }
}


class FoodMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodMenuScreen(
                onBack = {
                    finish()
                }
            )
        }
    }
}