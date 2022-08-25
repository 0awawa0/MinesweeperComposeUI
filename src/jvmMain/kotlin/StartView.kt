
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StartView(modifier: Modifier, onStartGame: (Int, Int, Int) -> Unit) {
    Column(modifier.background(Color.Gray), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Row {
            GamePropertiesView(
                5,
                5,
                5,
                modifier = Modifier.padding(25.dp),
                onClick = { onStartGame(5, 5, 5) }
            )
            GamePropertiesView(
                8,
                8,
                10,
                modifier = Modifier.padding(25.dp),
                onClick = { onStartGame(8, 8, 10) }
            )
        }

        Row {
            GamePropertiesView(
                16,
                16,
                40,
                modifier = Modifier.padding(25.dp),
                onClick = { onStartGame(16, 16, 40) }
            )
            GamePropertiesView(
                30,
                16,
                99,
                modifier = Modifier.padding(25.dp),
                onClick = { onStartGame(30, 16, 99) }
            )
        }
    }
}

@Composable
fun GamePropertiesView(
    width: Int,
    height: Int,
    minesCount: Int,
    onClick: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .width(250.dp)
            .height(250.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$width x $height", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White)
            Text("$minesCount mines", textAlign = TextAlign.Center, color = Color.White)
        }
    }
}