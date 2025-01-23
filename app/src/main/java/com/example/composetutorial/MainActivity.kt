package com.example.composetutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose. runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ComposeTutorialTheme {
                Navigation()
                //Conversation(SampleData.conversationSample)
                /*Surface(modifier = Modifier.fillMaxSize()) {
                    MessageCard(Message("Android", "Jetpack Composer")) */
                }
            }
        }
    }
//@Serializable
sealed class View(val route: String) {
    //@Serializable
    data object MainView : View("main_view")
    /*@Serializable
    data object SecondaryView(val message: String) : View("secondary_view?message={message}") {
        fun createRoute(message: String) = "secondary_view?message=$message"
    }*/
    data object SecondaryView : View("secondary_view")
}

    data class Message(val author: String, val body: String)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = View.MainView.route
    ) {
        composable(View.MainView.route) { MainView(navController) }
        composable(View.SecondaryView.route) { SecondaryView(navController) }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(navController : NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Row{} },
                actions = {
                    Button(
                        onClick = {
                            navController.navigate(View.SecondaryView.route) {
                                popUpTo(View.MainView.route) { inclusive = false }
                            }
                        },
                        modifier = Modifier
                            .height(42.dp)
                            .padding(end = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )

                    ) {
                        Text("Second view")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            //var index = 1
            Conversation(SampleData.conversationSample)
            /*LazyColumn {
                items(SampleData.conversationSample) { message ->
                    MessageCard(message, index)
                    index++
                }
            }*/
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryView(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Button(
                        onClick = {
                            navController.navigate(View.MainView.route) {
                                popUpTo(View.MainView.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(42.dp)
                            .padding(start = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text("Back")
                    }
                },
                title = { Row {} }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            MessageCard(Message(author = "Lexi", body = "Hello, this is the second view"),1)
        }
    }

}




@Composable
fun MessageCard(msg : Message, index: Int) {
    val picture = if (index % 2 == 0 ) {
        R.drawable.hw1pic2
    } else {
        R.drawable.hw1pic1
    }
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(picture),
                null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    //.border(1.5.dp, MaterialTheme.colorScheme.primary, RectangleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            var isExpanded by remember { mutableStateOf(false) }

            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                label = ""
            )

            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(
                    text = msg.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    color = surfaceColor,
                    modifier = Modifier.animateContentSize().padding(1.dp)
                ) {
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Hello world and something"),
                index = 2
            )
        }
    }
}
@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        var indexes = 1
        items(messages) { message ->
            MessageCard(
                message,
                index = indexes
            )
            indexes += 1
        }
    }
}
@Preview
@Composable
fun PreviewConversation() {
    ComposeTutorialTheme {
        Conversation(SampleData.conversationSample)
    }
}
object SampleData {
    // Sample conversation data
    val conversationSample = listOf(
        Message(
            "Lexi",
            "Test...Test...Test..."
        ),
        Message(
            "Lexi",
            """List of Android versions:
            |Android KitKat (API 19)
            |Android Lollipop (API 21)
            |Android Marshmallow (API 23)
            |Android Nougat (API 24)
            |Android Oreo (API 26)
            |Android Pie (API 28)
            |Android 10 (API 29)
            |Android 11 (API 30)
            |Android 12 (API 31)""".trim()
        ),
        Message(
            "Lexi",
            """I think Kotlin is my favorite programming language.
            |It's so much fun!""".trim()
        ),
        Message(
            "Lexi",
            "Searching for alternatives to XML layouts..."
        ),
        Message(
            "Lexi",
            """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
        ),
        Message(
            "Lexi",
            "It's available from API 21+ :)"
        ),
        Message(
            "Lexi",
            "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
        ),
        Message(
            "Lexi",
            "Android Studio next version's name is Arctic Fox"
        ),
        Message(
            "Lexi",
            "Android Studio Arctic Fox tooling for Compose is top notch ^_^"
        ),
        Message(
            "Lexi",
            "I didn't know you can now run the emulator directly from Android Studio"
        ),
        Message(
            "Lexi",
            "Compose Previews are great to check quickly how a composable layout looks like"
        ),
        Message(
            "Lexi",
            "Previews are also interactive after enabling the experimental setting"
        ),
        Message(
            "Lexi",
            "Have you tried writing build.gradle with KTS?"
        ),
        Message(
            author = "Lexi",
            body = "Adding an extra message to demonstrate scrolla  bility"
        ),
        Message(
            author = "Lexi",
            body = "Yet another one to add..."
        ),
        Message(
            author = "Lexi",
            body = "And another one..."
        ),
        Message(
            author = "Lexi",
            body = "This is the last one, at least for now..."
        )
    )
}

