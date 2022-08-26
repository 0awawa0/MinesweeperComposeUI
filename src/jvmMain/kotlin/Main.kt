// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import board.BoardView
import board.BoardViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import theme.ApplicationTheme

@Composable
@Preview
fun App(windowState: WindowState) {

    val viewModel = remember { MainViewModel() }
    val boardViewModel = viewModel.boardViewModel.value
    val availableSize = mutableStateOf(windowState.size)

    LaunchedEffect(windowState) {
        snapshotFlow { windowState.size }.onEach {
            availableSize.value = it
        }.launchIn(this)
    }

    ApplicationTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val stateVertical = rememberScrollState(0)
            val stateHorizontal = rememberScrollState(0)
            if (boardViewModel == null) {
                StartView(
                    Modifier.fillMaxSize()
                        .verticalScroll(stateVertical)
                        .horizontalScroll(stateHorizontal),
                    onStartGame = { width, height, minesCount ->
                        viewModel.boardViewModel.value = BoardViewModel(width, height, minesCount)

                    }
                )
            } else {
                BoardView(
                    boardViewModel.gameOver.value,
                    boardViewModel.won.value,
                    boardViewModel.boardState,
                    modifier = Modifier.fillMaxSize()
                        .align(Alignment.Center),
                    availableSize.value,
                    onOpenCell = boardViewModel::openCell,
                    onMarkCell = boardViewModel::markCell,
                    onResetBoard = boardViewModel::resetBoard,
                    onBackToMenu = { viewModel.boardViewModel.value = null }
                )
            }

            if (viewModel.boardViewModel.value == null) {
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    adapter = rememberScrollbarAdapter(stateHorizontal)
                )
            }
        }
    }
}

fun main() = application {
    val windowState = rememberWindowState()
    Window(onCloseRequest = ::exitApplication, windowState, title = "Minesweeper") {
        App(windowState)
    }
}
