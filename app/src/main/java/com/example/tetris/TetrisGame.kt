package com.example.tetris

import kotlin.random.Random

private const val BOARD_WIDTH = 10
private const val BOARD_HEIGHT = 20

class TetrisGame {
    data class ActivePiece(
        val type: Tetromino,
        var rotation: Int,
        var x: Int,
        var y: Int
    )

    enum class Tetromino(val color: Int, private val rotations: Array<Array<Cell>>) {
        I(
            0xFF00BCD4.toInt(),
            arrayOf(
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(2, 0)),
                arrayOf(Cell(1, -1), Cell(1, 0), Cell(1, 1), Cell(1, 2)),
                arrayOf(Cell(-1, 1), Cell(0, 1), Cell(1, 1), Cell(2, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(0, 1), Cell(0, 2))
            )
        ),
        O(
            0xFFFFC107.toInt(),
            arrayOf(
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1))
            )
        ),
        T(
            0xFF9C27B0.toInt(),
            arrayOf(
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(0, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(1, 0), Cell(0, 1)),
                arrayOf(Cell(0, -1), Cell(-1, 0), Cell(0, 0), Cell(1, 0)),
                arrayOf(Cell(0, -1), Cell(-1, 0), Cell(0, 0), Cell(0, 1))
            )
        ),
        S(
            0xFF4CAF50.toInt(),
            arrayOf(
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(-1, 1), Cell(0, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(1, 0), Cell(1, 1)),
                arrayOf(Cell(0, 0), Cell(1, 0), Cell(-1, 1), Cell(0, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(1, 0), Cell(1, 1))
            )
        ),
        Z(
            0xFFF44336.toInt(),
            arrayOf(
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(1, -1), Cell(0, 0), Cell(1, 0), Cell(0, 1)),
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(1, -1), Cell(0, 0), Cell(1, 0), Cell(0, 1))
            )
        ),
        J(
            0xFF3F51B5.toInt(),
            arrayOf(
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(-1, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(0, 1), Cell(1, 1)),
                arrayOf(Cell(1, -1), Cell(-1, 0), Cell(0, 0), Cell(1, 0)),
                arrayOf(Cell(-1, -1), Cell(0, -1), Cell(0, 0), Cell(0, 1))
            )
        ),
        L(
            0xFFFF9800.toInt(),
            arrayOf(
                arrayOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(1, 1)),
                arrayOf(Cell(0, -1), Cell(0, 0), Cell(0, 1), Cell(1, -1)),
                arrayOf(Cell(-1, -1), Cell(-1, 0), Cell(0, 0), Cell(1, 0)),
                arrayOf(Cell(-1, 1), Cell(0, -1), Cell(0, 0), Cell(0, 1))
            )
        );

        fun cells(rotation: Int): Array<Cell> = rotations[rotation % 4]
    }

    data class Cell(val x: Int, val y: Int)

    val width: Int = BOARD_WIDTH
    val height: Int = BOARD_HEIGHT
    val board: Array<IntArray> = Array(BOARD_HEIGHT) { IntArray(BOARD_WIDTH) }

    var activePiece: ActivePiece = spawnPiece()
        private set

    var score: Int = 0
        private set

    var gameOver: Boolean = false
        private set

    fun reset() {
        for (row in board) {
            row.fill(0)
        }
        score = 0
        gameOver = false
        activePiece = spawnPiece()
    }

    fun move(dx: Int): Boolean {
        if (gameOver) return false
        if (isValid(activePiece, activePiece.rotation, activePiece.x + dx, activePiece.y)) {
            activePiece.x += dx
            return true
        }
        return false
    }

    fun rotate(): Boolean {
        if (gameOver) return false
        val nextRotation = (activePiece.rotation + 1) % 4
        val kicks = intArrayOf(0, -1, 1, -2, 2)
        for (kick in kicks) {
            val newX = activePiece.x + kick
            if (isValid(activePiece, nextRotation, newX, activePiece.y)) {
                activePiece.rotation = nextRotation
                activePiece.x = newX
                return true
            }
        }
        return false
    }

    fun softDrop(): Boolean {
        if (gameOver) return false
        if (isValid(activePiece, activePiece.rotation, activePiece.x, activePiece.y + 1)) {
            activePiece.y += 1
            return true
        }
        lockPiece()
        return false
    }

    fun hardDrop() {
        if (gameOver) return
        while (softDrop()) {
            // keep dropping
        }
    }

    fun tick() {
        softDrop()
    }

    private fun lockPiece() {
        val cells = activePiece.type.cells(activePiece.rotation)
        for (cell in cells) {
            val x = activePiece.x + cell.x
            val y = activePiece.y + cell.y
            if (y in 0 until BOARD_HEIGHT && x in 0 until BOARD_WIDTH) {
                board[y][x] = activePiece.type.color
            }
        }

        clearLines()
        val next = spawnPiece()
        if (!isValid(next, next.rotation, next.x, next.y)) {
            gameOver = true
        }
        activePiece = next
    }

    private fun clearLines() {
        var linesCleared = 0
        var writeRow = BOARD_HEIGHT - 1

        for (readRow in BOARD_HEIGHT - 1 downTo 0) {
            if (board[readRow].all { it != 0 }) {
                linesCleared++
                continue
            }
            if (writeRow != readRow) {
                board[writeRow] = board[readRow].copyOf()
            }
            writeRow--
        }

        while (writeRow >= 0) {
            board[writeRow] = IntArray(BOARD_WIDTH)
            writeRow--
        }

        if (linesCleared > 0) {
            score += when (linesCleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                else -> 800
            }
        }
    }

    private fun spawnPiece(): ActivePiece {
        val type = Tetromino.entries[Random.nextInt(Tetromino.entries.size)]
        return ActivePiece(type = type, rotation = 0, x = BOARD_WIDTH / 2, y = 0)
    }

    private fun isValid(piece: ActivePiece, rotation: Int, x: Int, y: Int): Boolean {
        for (cell in piece.type.cells(rotation)) {
            val px = x + cell.x
            val py = y + cell.y

            if (px !in 0 until BOARD_WIDTH || py >= BOARD_HEIGHT) return false
            if (py >= 0 && board[py][px] != 0) return false
        }
        return true
    }
}
