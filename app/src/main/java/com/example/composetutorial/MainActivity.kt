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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose. runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import coil3.compose.rememberAsyncImagePainter


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


@Composable
fun MessageCard(msg : Message, index: Int) {
        Row(modifier = Modifier.padding(all = 8.dp)) {
            val context = LocalContext.current
            val imageUri = loadImageUri(context)
            Image(
                painter = if(imageUri != null) {
                    rememberAsyncImagePainter(imageUri)
                } else {
                    painterResource(R.drawable.ic_launcher_foreground)
                },
                contentDescription = "Profile picture",
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
    val context = LocalContext.current
    val username = remember { mutableStateOf(loadUsername(context)) }
    val authorName = username.value
    ComposeTutorialTheme {
        Conversation(SampleData.conversationSample(authorName))
    }
}
object SampleData {
    // Sample conversation data
    fun conversationSample(username: String) : List<Message> {
        return listOf(
            Message(username, "Test...Test...Test..."),
            Message(username, """List of Android versions:
                |Android KitKat (API 19)
                |Android Lollipop (API 21)
                |Android Marshmallow (API 23)
                |Android Nougat (API 24)
                |Android Oreo (API 26)
                |Android Pie (API 28)
                |Android 10 (API 29)
                |Android 11 (API 30)
                |Android 12 (API 31)""".trim()),
            Message(username, "Hey, take a look at Jetpack Compose, it's great!"),
            Message(username, "It's available from API 21+ :)"),
            Message(username,"Writing Kotlin for UI seems so natural, Compose where have you been all my life?" ),
            Message(username, "Android Studio next version's name is Arctic Fox"),
            Message(username, "Android Studio Arctic Fox tooling for Compose is top notch ^_^"),
            Message(username, "I didn't know you can now run the emulator directly from Android Studio"),
            Message(username, "Compose Previews are great to check quickly how a composable layout looks like"),
            Message(username, "Previews are also interactive after enabling the experimental setting"),
            Message(username, "Have you tried writing build.gradle with KTS?"),
            Message(username, "Adding an extra message to demonstrate scrollability"),
            Message(username, "Yet another one to add..."),
            Message(username, "And another one..."),
            Message(username, "This is the last one, at least for now...")
        )
    }
}

