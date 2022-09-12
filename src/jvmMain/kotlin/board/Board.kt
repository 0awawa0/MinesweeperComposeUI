package board

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.Colors
import theme.Green800


@Composable
fun BoardView(
    isOver: Boolean,
    isWon: Boolean,
    board: List<List<State<BoardViewModel.Cell>>>,
    modifier: Modifier = Modifier,
    onOpenCell: (BoardViewModel.Cell) -> Unit,
    onMarkCell: (BoardViewModel.Cell) -> Unit,
    onResetBoard: () -> Unit,
    onBackToMenu: () -> Unit
) {

    val cellSize = 50.dp
    val stateVertical = rememberScrollState(0)
    val stateHorizontal = rememberScrollState(0)

    Box(modifier.background(MaterialTheme.colors.background), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(Modifier.fillMaxHeight(0.8f).verticalScroll(stateVertical)
                .horizontalScroll(stateHorizontal),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    for (row in board) {
                        Row {
                            row.forEach { cell ->
                                CellView(
                                    cell.value.visibility == BoardViewModel.Cell.CellVisibility.Open,
                                    cell.value.visibility == BoardViewModel.Cell.CellVisibility.MarkMine,
                                    cell.value.state.value,
                                    modifier = Modifier.size(cellSize),
                                    onLeftClick = { onOpenCell(cell.value) },
                                    onRightClick = { onMarkCell(cell.value) }
                                )
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onResetBoard) {
                    Text("Reset", color = Color.White)
                }
                Spacer(Modifier.width(40.dp))
                Button(onClick = onBackToMenu) {
                    Text("Back to menu", color = Color.White)
                }
            }

        }
        if (isOver) {
            if (isWon) Text(
                "Game over. You won!",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Green800
            )
            else Text(
                "Game over. You lost :(",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.error
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    isOpen: Boolean,
    isMarked: Boolean,
    mines: Int,
    modifier: Modifier = Modifier,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered = interactionSource.collectIsHoveredAsState()
    val color = animateColorAsState(when {
        isOpen -> {
            if (mines == -2) MaterialTheme.colors.error
            else MaterialTheme.colors.onBackground
        }
        isMarked -> MaterialTheme.colors.secondary
        else -> MaterialTheme.colors.primary
    })

    val textColor = Colors.numbersColors.getOrElse(mines) { Color.Black }

    Card(
        modifier = modifier.mouseClickable {
            if (buttons.isSecondaryPressed) onRightClick()
            else onLeftClick()
        }.hoverable(interactionSource)
            .padding(2.dp),
        elevation = if (isHovered.value) 12.dp else 0.dp,
        backgroundColor = color.value,
        shape = RoundedCornerShape(10)
    ) {
        Box(contentAlignment = Alignment.Center){
            if (isHovered.value) Box(modifier.background(Color(0xff, 0xff, 0xff, 0x20)))
            if (isOpen) {
                if (mines < 0) Text("*", fontWeight = FontWeight.Bold)
                else if (mines > 0) Text(mines.toString(), fontWeight = FontWeight.Bold, color = textColor)
            }
        }

    }
}