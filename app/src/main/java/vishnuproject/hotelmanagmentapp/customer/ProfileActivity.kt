package vishnuproject.hotelmanagmentapp.customer

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vishnuproject.hotelmanagmentapp.SignInActivity
import vishnuproject.hotelmanagmentapp.UserPrefs
import kotlin.jvm.java

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    val userName = UserPrefs.getName(context) ?: "Customer"
    val userEmail = UserPrefs.getEmail(context) ?: "Not Available"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(30.dp))

            // ------------------- PROFILE AVATAR -------------------
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // ------------------- USER NAME -------------------
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            // ------------------- USER EMAIL -------------------
            Text(
                text = userEmail,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(40.dp))

            // ------------------- INFORMATION CARD -------------------
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Profile Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))

                    ProfileItem(title = "Name", value = userName)
                    ProfileItem(title = "Email", value = userEmail)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------------------- LOGOUT BUTTON -------------------
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
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
                    UserPrefs.markLoginStatus(this, false)

                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

            )
        }
    }
}
