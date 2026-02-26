package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class TetrisView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val game = TetrisGame()
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    var onStateChanged: ((Int, Boolean) -> Unit)? = null

    fun moveLeft() {
        if (game.move(-1)) invalidate()
    }

    fun moveRight() {
        if (game.move(1)) invalidate()
    }

    fun rotate() {
        if (game.rotate()) invalidate()
    }

    fun softDrop() {
        val moved = game.softDrop()
        notifyState()
        if (moved || game.gameOver) invalidate()
    }

    fun hardDrop() {
        game.hardDrop()
        notifyState()
        invalidate()
    }

    fun tick() {
        game.tick()
        notifyState()
        invalidate()
    }

    fun reset() {
        game.reset()
        notifyState()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#151515"))

        val boardWidthPx = width.toFloat()
        val boardHeightPx = height.toFloat()
        val cellSize = min(boardWidthPx / game.width, boardHeightPx / game.height)

        val drawWidth = cellSize * game.width
        val drawHeight = cellSize * game.height
        val offsetX = (width - drawWidth) / 2f
        val offsetY = (height - drawHeight) / 2f

        for (row in 0 until game.height) {
            for (col in 0 until game.width) {
                val left = offsetX + col * cellSize
                val top = offsetY + row * cellSize
                val right = left + cellSize
                val bottom = top + cellSize

                val color = game.board[row][col]
                if (color != 0) {
                    cellPaint.style = Paint.Style.FILL
                    cellPaint.color = color
                    canvas.drawRect(left, top, right, bottom, cellPaint)
                }
                canvas.drawRect(left, top, right, bottom, gridPaint)
            }
        }

        val piece = game.activePiece
        cellPaint.style = Paint.Style.FILL
        cellPaint.color = piece.type.color
        for (cell in piece.type.cells(piece.rotation)) {
            val x = piece.x + cell.x
            val y = piece.y + cell.y
            if (y < 0) continue
            val left = offsetX + x * cellSize
            val top = offsetY + y * cellSize
            canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint)
            canvas.drawRect(left, top, left + cellSize, top + cellSize, gridPaint)
        }

        canvas.drawRect(offsetX, offsetY, offsetX + drawWidth, offsetY + drawHeight, borderPaint)
    }

    private fun notifyState() {
        onStateChanged?.invoke(game.score, game.gameOver)
    }
}
