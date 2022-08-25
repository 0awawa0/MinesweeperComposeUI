import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import theme.Colors


@Composable
fun BoardView(
    board: List<List<State<BoardViewModel.Cell>>>,
    modifier: Modifier = Modifier,
    availableSize: DpSize,
    onOpenCell: (BoardViewModel.Cell) -> Unit,
    onMarkCell: (BoardViewModel.Cell) -> Unit,
    onResetBoard: () -> Unit
) {

    val cellSize = maxOf(40.dp, minOf(
        150.dp,
        minOf((availableSize.width - 30.dp) / board[0].size, (availableSize.height - 100.dp) / board.size)
    ))

    Column(
        modifier.background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                for (row in board) {
                    Row {
                        row.forEach { cell ->
                            CellView(cell, modifier = Modifier.size(cellSize), onOpenCell, onMarkCell)
                        }
                    }
                }
            }
        }

        Button(onClick = onResetBoard) {
            Text("Reset")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cellState: State<BoardViewModel.Cell>,
    modifier: Modifier = Modifier,
    onLeftClick: (BoardViewModel.Cell) -> Unit,
    onRightClick: (BoardViewModel.Cell) -> Unit
) {
    val color = when (cellState.value.visibility) {
        BoardViewModel.Cell.CellVisibility.Open -> Colors.openFieldBackground
        BoardViewModel.Cell.CellVisibility.Closed -> Colors.closedFieldBackground
        BoardViewModel.Cell.CellVisibility.MarkMine -> Colors.markedFieldBackground
    }

    val state = cellState.value.state
    val visibility = cellState.value.visibility

    Button(modifier = modifier.mouseClickable { if (buttons.isSecondaryPressed) onRightClick(cellState.value) },
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        onClick = { onLeftClick(cellState.value) }
    ) {

        if (visibility == BoardViewModel.Cell.CellVisibility.Open) {
            if (state == BoardViewModel.Cell.CellState.Mine) Text("*", fontWeight = FontWeight.Bold)
            else if (state.value > 0) Text(state.value.toString(), fontWeight = FontWeight.Bold)
        }
    }
}