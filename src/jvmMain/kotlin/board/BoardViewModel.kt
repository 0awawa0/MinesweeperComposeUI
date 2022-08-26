package board

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class BoardViewModel(width: Int, height: Int, val minesCount: Int) {

    data class GameState(val isOver: Boolean = false, val isWon: Boolean = false, val board: List<List<Cell.CellState>>)

    data class Cell(
        val x: Int,
        val y: Int,
        val state: CellState,
        val visibility: CellVisibility
    ) {
        enum class CellVisibility {
            Open,
            Closed,
            MarkMine
        }

        sealed class CellState(val value: Int) {
            object Mine: CellState(-1)
            class Free(value: Int): CellState(value)
        }
    }

    private val board = MutableList(height) {x ->
        MutableList(width) { y ->
            mutableStateOf(Cell(x, y, Cell.CellState.Free(0), Cell.CellVisibility.Closed))
        }
    }

    private val mines = HashSet<Pair<Int, Int>>()
    private val cellsToOpen = HashSet<Pair<Int, Int>>()

    private val mGameOver = mutableStateOf(false)
    val gameOver: State<Boolean>
        get() = mGameOver

    private val mWon = mutableStateOf(false)
    val won: State<Boolean>
        get() = mWon

    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    val boardState: List<List<State<Cell>>>
        get() = board.map { it.toList() }

    fun openCell(cell: Cell) {
        if (cell.visibility == Cell.CellVisibility.Open) return
        if (mines.isEmpty()) placeMines(cell)
        if (mGameOver.value) return

        val x = cell.x
        val y = cell.y
        if (cell.state == Cell.CellState.Mine) {
            mGameOver.value = true
            mWon.value = false
            for (mine in mines) {
                board[mine.first][mine.second].value = board[mine.first][mine.second].value.copy(
                    visibility = Cell.CellVisibility.Open
                )
            }
        }

        suspend fun dfs(x: Int, y: Int) {
            if (x < 0 || x > board.lastIndex) return
            if (y < 0 || y > board[x].lastIndex) return
            if (board[x][y].value.state == Cell.CellState.Mine) return
            if (board[x][y].value.visibility == Cell.CellVisibility.Open) return

            delay(50)
            board[x][y].value = board[x][y].value.copy(visibility = Cell.CellVisibility.Open)
            cellsToOpen.remove(x to y)

            if (board[x][y].value.state.value == 0) {
                dfs(x - 1, y - 1)
                dfs(x - 1, y)
                dfs(x - 1, y + 1)
                dfs(x, y - 1)
                dfs(x, y + 1)
                dfs(x + 1, y - 1)
                dfs(x + 1, y)
                dfs(x + 1, y + 1)
            }
        }

        viewModelScope.launch {
            dfs(x, y)
            if (cellsToOpen.isEmpty()) {
                mGameOver.value = true
                mWon.value = true
            }
        }
    }

    fun markCell(cell: Cell) {
        val x = cell.x
        val y = cell.y
        val newVisibility = when(board[x][y].value.visibility) {
            Cell.CellVisibility.Open -> Cell.CellVisibility.Open
            Cell.CellVisibility.Closed -> Cell.CellVisibility.MarkMine
            Cell.CellVisibility.MarkMine -> Cell.CellVisibility.Closed
        }
        board[x][y].value = board[x][y].value.copy(visibility = newVisibility)
    }

    fun resetBoard() {
        for (x in board.indices) {
            for (y in board[x].indices) {
                board[x][y].value = Cell(x, y, Cell.CellState.Free(0), Cell.CellVisibility.Closed)
            }
        }
        mines.clear()
        cellsToOpen.clear()
        mGameOver.value = false
        mWon.value = false
    }

    private fun placeMines(ignore: Cell) {
        var minesLeft = minesCount
        while (minesLeft > 0) {
            val x = Random.nextInt(0, board.size)
            val y = Random.nextInt(0, board[x].size)
            if (x == ignore.x && y == ignore.y || board[x][y].value.state == Cell.CellState.Mine) continue
            board[x][y].value = board[x][y].value.copy(state = Cell.CellState.Mine)
            mines.add(x to y)
            minesLeft--
            increaseMineCount(x - 1, y - 1)
            increaseMineCount(x - 1, y)
            increaseMineCount(x - 1, y + 1)
            increaseMineCount(x, y - 1)
            increaseMineCount(x, y + 1)
            increaseMineCount(x + 1, y - 1)
            increaseMineCount(x + 1, y)
            increaseMineCount(x + 1, y + 1)
        }

        for (x in board.indices) {
            for (y in board[x].indices) {
                if (board[x][y].value.state != Cell.CellState.Mine) cellsToOpen.add(x to y)
            }
        }
    }

    private fun increaseMineCount(x: Int, y: Int) {
        if (x < 0 || x > board.lastIndex) return
        if (y < 0 || y > board[x].lastIndex) return
        if (board[x][y].value.state == Cell.CellState.Mine) return
        val count = board[x][y].value.state.value
        board[x][y].value = board[x][y].value.copy(state = Cell.CellState.Free(count + 1))
    }
}