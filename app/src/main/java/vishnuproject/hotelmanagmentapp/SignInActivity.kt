package vishnuproject.hotelmanagmentapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.jvm.java

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInScreen()
        }
    }
}


@Composable
fun SignInScreen() {

    var guestBookingMail by remember { mutableStateOf("") }
    var guestBookingPassword by remember { mutableStateOf("") }

    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .size(150.dp),
            painter = painterResource(id = R.drawable.ic_hotel_management),
            contentDescription = "Hotel Management"
        )

        Text(
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 24.dp),
            text = "Hotel Management",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )

        Image(
            painter = painterResource(id = R.drawable.wave_up), // Replace with your actual SVG drawable
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.background(
                color = colorResource(id = R.color.firsthome_color),
            )
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = guestBookingMail,
                onValueChange = { guestBookingMail = it },
                label = { Text("Enter Email") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(32.dp),
                trailingIcon = {
                    Image(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.ic_email), // Replace with your actual drawable resource
                        contentDescription = "Email Icon"
                    )

                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = guestBookingPassword,
                onValueChange = { guestBookingPassword = it },
                label = { Text("Enter Password") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(32.dp),
                trailingIcon = {
                    Image(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.ic_password), // Replace with your actual drawable resource
                        contentDescription = "Password Icon"
                    )

                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row {

                Text(
                    modifier = Modifier
                        .clickable {
                            when{


                                guestBookingMail.isBlank() -> {
                                    Toast.makeText(context, "MailID missing", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                guestBookingPassword.isBlank() -> {
                                    Toast.makeText(context, "Password missing", Toast.LENGTH_SHORT)
                                        .show()

                                }
                                else -> {


                                }
                            }
                        }
                        .padding(start = 24.dp)
                        .background(
                            color = colorResource(id = R.color.white),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.white),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    text = "SignIn",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.firsthome_color),
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 24.dp)
                        .clickable {
                            context.startActivity(Intent(context, SignupActivity::class.java))
                            context.finish()
                        },
                    text = "Signup For An Account",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black,
                    )
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

        }

        Image(
            painter = painterResource(id = R.drawable.wave_down), // Replace with your actual SVG drawable
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))


    }

}