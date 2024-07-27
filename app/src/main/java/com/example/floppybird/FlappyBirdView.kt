package com.example.floppybird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class FlappyBirdView(context: Context) : View(context) {

    private val paint = Paint()
    private lateinit var background: Bitmap
    private lateinit var gameOver: Bitmap
    private lateinit var birds: Array<Bitmap>
    private lateinit var topTube: Bitmap
    private lateinit var bottomTube: Bitmap
    private val birdCircle = Rect()
    private val font = Paint()
    private var flapState = 0
    private var birdY: Float = 0f
    private var velocity: Float = 0f
    private var score: Int = 0
    private var scoringTube: Int = 0
    private var gameState: Int = 0
    private val numberOfTubes: Int = 4
    private val screenWidth: Int = context.resources.displayMetrics.widthPixels
    private val screenHeight: Int = context.resources.displayMetrics.heightPixels
    private val topTubeWidth: Int
    private val bottomTubeWidth: Int
    private val topTubeHeight: Int
    private val bottomTubeHeight: Int
    private val tubeX = FloatArray(numberOfTubes)
    private val tubeOffset = FloatArray(numberOfTubes)
    private var distanceBetweenTubes: Float = 0.toFloat()
    private var topTubeRectangles = arrayOfNulls<Rect>(numberOfTubes)
    private var bottomTubeRectangles = arrayOfNulls<Rect>(numberOfTubes)

    init {
        background = BitmapFactory.decodeResource(resources, R.drawable.background)
        gameOver = BitmapFactory.decodeResource(resources, R.drawable.gameover)
        birds = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.bird),
            BitmapFactory.decodeResource(resources, R.drawable.bird22)
        )
        topTube = BitmapFactory.decodeResource(resources, R.drawable.toptube)
        bottomTube = BitmapFactory.decodeResource(resources, R.drawable.bottomtube)
        topTubeWidth = topTube.width
        topTubeHeight = topTube.height
        bottomTubeWidth = bottomTube.width
        bottomTubeHeight = bottomTube.height

        distanceBetweenTubes = screenWidth * 3f / 4f
        topTubeRectangles = arrayOfNulls(numberOfTubes)
        bottomTubeRectangles = arrayOfNulls(numberOfTubes)
        font.color = Color.WHITE
        font.textSize = 50f
        startGame()
    }

    private fun startGame() {
        birdY = screenHeight / 2f - birds[0].height / 2f

        for (i in 0 until numberOfTubes) {
            tubeOffset[i] = (Random.nextFloat() - 0.5f) * (screenHeight.toFloat() - GAP - 200f)
            tubeX[i] = screenWidth / 2f - topTubeWidth / 2f + screenWidth.toFloat() + i * distanceBetweenTubes
            topTubeRectangles[i] = Rect()
            bottomTubeRectangles[i] = Rect()
        }
    }


     override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(background, 0f, 0f, paint)
        if (gameState == 1) {

            if (tubeX[scoringTube] < screenWidth / 2) {
                score++
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++
                } else {
                    scoringTube = 0
                }
            }
            velocity += GRAVITY
            birdY -= velocity


            for (i in 0 until numberOfTubes) {

                if (tubeX[i] < -topTubeWidth) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes
                    tubeOffset[i] =
                        (Random.nextFloat() - 0.5f) * (screenHeight.toFloat() - GAP - 200f)
                } else {
                    tubeX[i] -= TUBE_VELOCITY
                }

                canvas.drawBitmap(
                    topTube,
                    tubeX[i],
                    screenHeight / 2f + GAP / 2 + tubeOffset[i],
                    paint
                )
                canvas.drawBitmap(
                    bottomTube,
                    tubeX[i],
                    screenHeight / 2f - GAP / 2 - bottomTubeHeight.toFloat() + tubeOffset[i],
                    paint
                )

                topTubeRectangles[i]?.set(
                    tubeX[i].toInt(),
                    (screenHeight / 2f + GAP / 2 + tubeOffset[i]).toInt(),
                    (tubeX[i] + topTubeWidth).toInt(),
                    (screenHeight / 2f + GAP / 2 + tubeOffset[i] + topTubeHeight).toInt()
                )
                bottomTubeRectangles[i]?.set(
                    tubeX[i].toInt(),
                    (screenHeight / 2f - GAP / 2 - bottomTubeHeight + tubeOffset[i]).toInt(),
                    (tubeX[i] + bottomTubeWidth).toInt(),
                    (screenHeight / 2f - GAP / 2 + tubeOffset[i]).toInt()
                )

                birdCircle.set(
                    (screenWidth / 2f - birds[flapState].width / 2).toInt(),
                    (birdY + birds[flapState].height / 2f).toInt(),
                    (screenWidth / 2f + birds[flapState].width / 2).toInt(),
                    (birdY + birds[flapState].height / 2f + birds[flapState].height).toInt()
                )

                if (birdCircle.intersect(topTubeRectangles[i]!!) || birdCircle.intersect(bottomTubeRectangles[i]!!)) {
                    gameState = 2
                }
            }

            if (birdY > 0) {
                velocity += GRAVITY
                birdY -= velocity
            } else {
                gameState = 2
            }

        } else if (gameState == 0) {

        } else if (gameState == 2) {
            canvas.drawBitmap(gameOver, screenWidth / 2f - gameOver.width / 2f, screenHeight / 2f - gameOver.height / 2f, paint)

        }

        flapState = if (flapState == 0) 1 else 0

        canvas.drawBitmap(birds[flapState], screenWidth / 2f - birds[flapState].width / 2f, birdY, paint)
        canvas.drawText(score.toString(), 100f, 200f, font)

        invalidate()
    }

    companion object {
        private const val GRAVITY = 2f
        private const val TUBE_VELOCITY = 4f
        private const val GAP = 800f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (gameState == 1) {
                    velocity = -30f
                } else if (gameState == 0) {
                    gameState = 1
                } else if (gameState == 2) {
                    startGame()
                }
            }
        }
        return true
    }

}
