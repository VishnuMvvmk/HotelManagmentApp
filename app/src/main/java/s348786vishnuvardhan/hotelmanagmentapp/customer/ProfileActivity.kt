package s348786vishnuvardhan.hotelmanagmentapp.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import s348786vishnuvardhan.hotelmanagmentapp.SignInActivity
import s348786vishnuvardhan.hotelmanagmentapp.HotelAccountData
import s348786vishnuvardhan.hotelmanagmentapp.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    val email = HotelAccountData.getEmail(context) ?: ""
    val emailKey = email.replace(".", ",")

    val userRole = HotelAccountData.getRole(context)

    var name by remember { mutableStateOf(HotelAccountData.getName(context) ?: "") }
    var password by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryColor
                )
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF2F3F7))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(PrimaryColor),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Text(
                        email,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    if (userRole == "customer") {
                        PremiumField(
                            label = "Full Name",
                            value = name,
                            icon = Icons.Default.Person,
                            onValueChange = { name = it },
                            enabled = true
                        )
                    } else {
                        PremiumField(
                            label = "Full Name",
                            value = name,
                            icon = Icons.Default.Person,
                            onValueChange = { name = it }, enabled = false
                        )
                    }



                    Spacer(Modifier.height(14.dp))

                    PremiumField(
                        label = "Email Address",
                        value = email,
                        icon = Icons.Default.Email,
                        enabled = false
                    )

                    Spacer(Modifier.height(14.dp))


                    if (userRole == "customer") {

                        PremiumField(
                            label = "New Password",
                            value = password,
                            icon = Icons.Default.Lock,
                            placeholder = "Leave blank to keep same",
                            onValueChange = { password = it }
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Name cannot be empty",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return@Button
                                }

                                isSaving = true

                                val updates = mutableMapOf<String, Any>("name" to name)
                                if (password.isNotBlank()) updates["password"] = password

                                FirebaseDatabase.getInstance()
                                    .getReference("HotelUsers")
                                    .child(emailKey)
                                    .updateChildren(updates)
                                    .addOnSuccessListener {
                                        HotelAccountData.saveName(context, name)
                                        password = ""
                                        isSaving = false
                                        Toast.makeText(
                                            context,
                                            "Profile updated",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    .addOnFailureListener {
                                        isSaving = false
                                        Toast.makeText(
                                            context,
                                            "Failed: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isSaving
                        ) {
                            Text(if (isSaving) "Saving..." else "Save Changes")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Logout, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PremiumField(
    label: String,
    value: String,
    icon: ImageVector,
    enabled: Boolean = true,
    placeholder: String = "",
    onValueChange: (String) -> Unit = {}
) {
    Column {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(icon, contentDescription = null)
            },
            shape = RoundedCornerShape(14.dp)
        )
    }
}


@Composable
fun ProfileItem(title: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))

        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(16.dp))

        Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))
        Spacer(Modifier.height(12.dp))
    }
}

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                onBack = {
                    finish()
                },
                onLogout = {
                    HotelAccountData.markLoginStatus(this, false)

                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

            )
        }
    }
}
