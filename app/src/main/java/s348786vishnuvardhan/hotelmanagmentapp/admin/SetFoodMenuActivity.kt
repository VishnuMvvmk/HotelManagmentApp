package s348786vishnuvardhan.hotelmanagmentapp.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import s348786vishnuvardhan.hotelmanagmentapp.HotelAccountData
import java.util.UUID

data class FoodItem(
    val itemId: String = "",
    val name: String = "",
    val price: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetFoodMenuScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current
    val foodItems = remember { mutableStateListOf<FoodItem>() }

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userEmail = HotelAccountData.getEmail(context)?.replace(".", ",") ?: "unknown"
        val dbRef = FirebaseDatabase.getInstance().reference
            .child("HotelMenu")
            .child(userEmail)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodItems.clear()
                snapshot.children.forEach {
                    it.getValue(FoodItem::class.java)?.let { f -> foodItems.add(f) }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Food Menu") },
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
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Food Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = itemPrice,
                onValueChange = { itemPrice = it.filter { c -> c.isDigit() } },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(15.dp))

            Button(
                onClick = {
                    if (itemName.isBlank() || itemPrice.isBlank()) {
                        Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userEmail = HotelAccountData.getEmail(context)?.replace(".", ",") ?: "unknown"
                    val dbRef = FirebaseDatabase.getInstance().reference
                        .child("HotelMenu")
                        .child(userEmail)

                    val id = UUID.randomUUID().toString()
                    val data = FoodItem(id, itemName, itemPrice)

                    dbRef.child(id).setValue(data).addOnSuccessListener {
                        itemName = ""
                        itemPrice = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
                Spacer(Modifier.width(5.dp))
                Text("Add Item")
            }

            Spacer(Modifier.height(20.dp))

            Text("Food Menu", fontSize = 20.sp)

            Spacer(Modifier.height(10.dp))

            LazyColumn {
                items(foodItems) { item ->
                    FoodItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.name, fontSize = 18.sp)
                Text("£${item.price}", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }

            IconButton(
                onClick = {
                    val userEmail =
                        HotelAccountData.getEmail(context)?.replace(".", ",") ?: "unknown"
                    FirebaseDatabase.getInstance().reference
                        .child("HotelMenu")
                        .child(userEmail)
                        .child(item.itemId)
                        .removeValue()
                }
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevMenu() {
    SetFoodMenuScreen()
}


class SetFoodMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetFoodMenuScreen(onBack = { finish() })
        }
    }
}