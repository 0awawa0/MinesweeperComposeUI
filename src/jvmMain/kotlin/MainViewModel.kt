import androidx.compose.runtime.mutableStateOf
import board.BoardViewModel

class MainViewModel {

    val boardViewModel = mutableStateOf<BoardViewModel?>(null)
}