package board

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine
import theme.ApplicationTheme

@Composable
fun CanvasBoard(
    rows: Int,
    columns: Int,
    squareSize: DpSize,
    squarePadding: Dp,
    availableSize: DpSize,
    focusedColor: Color = MaterialTheme.colors.secondary,
    unfocusedColor: Color = MaterialTheme.colors.primary,
    modifier: Modifier = Modifier
) {

    var shift by remember { mutableStateOf(Offset.Zero) }
    var pointerPosition by remember { mutableStateOf(Offset.Zero) }

    var focusedId by remember { mutableStateOf(-1) }
    var prevFocusedId by remember { mutableStateOf(-1) }

    val animatedFocusedColor = remember(focusedId) { Animatable(unfocusedColor) }
    val animatedUnfocusedColor = remember(prevFocusedId) { Animatable(focusedColor) }

    LaunchedEffect(focusedId) {
        animatedFocusedColor.animateTo(targetValue = focusedColor, animationSpec = tween(250))
    }

    LaunchedEffect(prevFocusedId) {
        animatedUnfocusedColor.animateTo(targetValue = unfocusedColor, animationSpec = tween(250))
    }

    val requiredWidth = (squareSize.width + squarePadding).value * rows + squarePadding.value
    val requiredHeight = (squareSize.height + squarePadding).value * columns + squarePadding.value

    val actualWindowHeight = availableSize.height.value - 36
    val actualWindowWidth = availableSize.width.value

    Canvas(modifier
        .height(availableSize.height)
        .width(availableSize.width)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Move) pointerPosition = event.changes.first().position
                }
            }
        }
        .pointerInput(availableSize) {
            detectDragGestures { change, dragAmount ->
                change.consumeAllChanges()
                val minShiftY = if (requiredHeight > actualWindowHeight) -(requiredHeight - actualWindowHeight) else 0f
                val minShiftX = if (requiredWidth > availableSize.width.value) -(requiredWidth - availableSize.width.value)else 0f
                val shiftX = maxOf(minOf(0f, shift.x + dragAmount.x), minShiftX)
                val shiftY = maxOf(minOf(0f, shift.y + dragAmount.y), minShiftY)
                shift = Offset(shiftX, shiftY)
            }
        }
        .background(MaterialTheme.colors.background)
    ) {
        var newFocusedId = -1
        for (row in 0 until rows) {
            val offsetY = squarePadding.value * (row + 1) + squareSize.height.value * row + shift.y
            if (offsetY > actualWindowHeight) break
            if (offsetY + squareSize.height.value < 0) continue
            for (col in 0 until columns) {
                val offsetX = squarePadding.value * (col + 1) + squareSize.width.value * col + shift.x
                if (offsetX > actualWindowWidth) break
                if (offsetX + squareSize.width.value < 0) continue
                val xWithinBorder =
                    pointerPosition.x >= offsetX && pointerPosition.x <= offsetX + squareSize.width.value
                val yWithinBorder =
                    pointerPosition.y >= offsetY && pointerPosition.y <= offsetY + squareSize.height.value
                val pointerWithinSquare = xWithinBorder && yWithinBorder

                val squareId = row * columns + col

                if (pointerWithinSquare) newFocusedId = squareId

                val squareColor = when (squareId) {
                    focusedId -> animatedFocusedColor.value
                    prevFocusedId -> animatedUnfocusedColor.value
                    else -> unfocusedColor
                }

                drawRoundRect(
                    color = squareColor,
                    topLeft = Offset(
                        x = offsetX,
                        y = offsetY
                    ),
                    size = Size(width = squareSize.width.value, height = squareSize.height.value),
                    cornerRadius = CornerRadius(10f)
                )

                drawContext.canvas.nativeCanvas.apply {
                    drawTextLine(
                        TextLine.make(squareId.toString(), Font()),
                        offsetX + squareSize.width.value / 2,
                        offsetY + squareSize.height.value / 2,
                        Paint()
                    )
                }
            }
        }

        if (newFocusedId != focusedId) {
            if (focusedId != -1) prevFocusedId = focusedId
            focusedId = newFocusedId
        }
    }
}


fun main() = application {
    val windowState = rememberWindowState()
    Window(onCloseRequest = ::exitApplication, state = windowState) {
        ApplicationTheme {
            CanvasBoard(
                100_000,
                100_000,
                DpSize(50.dp, 50.dp),
                2.dp,
                windowState.size
            )
        }
    }
}
