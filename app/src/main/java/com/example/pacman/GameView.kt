package com.example.pacman

import android.content.Context
import com.example.pacman.Globals.Companion.instance
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.content.Intent
import android.graphics.*
import android.view.MotionEvent
import android.util.Log
import kotlin.experimental.xor
import android.os.CountDownTimer
import androidx.core.content.ContextCompat.startActivity


class GameView(context: Context?) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private var thread: Thread? = null
    //private var holder: SurfaceHolder
    private var canDraw = true
    private val paint: Paint
    private lateinit var pacmanRight: Array<Bitmap?>
    private lateinit var pacmanDown: Array<Bitmap?>
    private lateinit var pacmanLeft: Array<Bitmap?>
    private lateinit var pacmanUp: Array<Bitmap?>
    private lateinit var arrowRight: Array<Bitmap?>
    private lateinit var arrowDown: Array<Bitmap?>
    private lateinit var arrowLeft: Array<Bitmap?>
    private lateinit var arrowUp: Array<Bitmap?>
    private var ghostBitmap: Bitmap? = null
    private val totalFrame = 4 // Total amount of frames fo each direction
    private var currentPacmanFrame = 0 // Current Pacman frame to draw
    private var currentArrowFrame = 0 // Current arrow frame to draw
    private var frameTicker // Current time since last frame has been drawn
            : Long
    private var xPosPacman // x-axis position of pacman
            : Int
    private var yPosPacman // y-axis position of pacman
            : Int
    private var xPosGhost1 // x-axis position of ghost1
            : Int
    private var yPosGhost1 // y-axis position of ghost1
            : Int
    private var xPosGhost2 :Int
    private var yPosGhost2 :Int

    var xDistance1 = 0
    var yDistance1 = 0
    var xDistance2 = 0
    var yDistance2 = 0
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var t1=30
    private var y2 // Initial/Final positions of swipe
            = 0f
    private var direction = 4 // Direction of the swipe, initial direction is right
    private var nextDirection = 4 // Buffer for the next direction you choose
    private var viewDirection = 2 // Direction that pacman is facing
    private var ghostDirection1: Int
    private var ghostDirection2: Int
    private val arrowDirection = 4
    private val screenWidth // Width of the phone screen
            : Int
    private var blockSize // Size of a block on the map
            : Int
    private var currentScore = 0 //Current game score
    override fun run() {
        Log.i("info", "Run")
        val t=System.currentTimeMillis();
        while (canDraw) {
            if (!holder.surface.isValid) {
                continue
            }
            val canvas = holder.lockCanvas()
            // Set background color to Transparent
            if (canvas != null) {
                canvas.drawColor(Color.BLACK)
                drawMap(canvas)
                drawArrowIndicators(canvas)
                updateFrame(System.currentTimeMillis(),canvas)
                moveGhost1(canvas)
                moveGhost2(canvas);
                // Moves the pacman based on his direction
                movePacman(canvas)
                timer(canvas,t1-(System.currentTimeMillis()-t)/1000)
                if((t1-(System.currentTimeMillis()-t)/1000).toInt()==0)
                {
                    PlayActivity.instance?.gameOver()
                }
                if(((xDistance1>=0&&xDistance1<=10)&&(yDistance1>=0&&yDistance1<=10))||((xDistance2>=0&&xDistance2<=10)&&(yDistance2>=0&&yDistance2<=10)))
                {
                    PlayActivity.instance?.gameOver()
                }
                // Draw the pellets
                drawPellets(canvas)
                //Update current and high scores
                updateScores(canvas)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
    fun timer(canvas: Canvas, t: Long)
    {
        paint.textSize=blockSize.toFloat()
        val time= "00:$t"
        canvas.drawText(time,0f,(20 * blockSize - 10).toFloat(),paint)
    }
    fun updateScores(canvas: Canvas) {
        paint.textSize = blockSize.toFloat()
        val g = instance
        val highScore = g!!.highScore
        if (currentScore > highScore) {
            g.highScore = currentScore
        }
        val formattedHighScore = String.format("%05d", highScore)
        val hScore = "High Score : $formattedHighScore"
        canvas.drawText(hScore, 0f, (2 * blockSize - 10).toFloat(), paint)
        val formattedScore = String.format("%05d", currentScore)
        val score = "Score : $formattedScore"
        Globals.instance!!.score = currentScore
        canvas.drawText(score, (11 * blockSize).toFloat(), (2 * blockSize - 10).toFloat(), paint)
    }
    fun moveGhost2(canvas:Canvas)
    {
        val ch: Short
        xDistance2 = xPosPacman - xPosGhost2
        yDistance2 = yPosPacman - yPosGhost2
        if (xPosGhost2 % blockSize == 0 && yPosGhost2 % blockSize == 0) {
            ch = leveldata1[yPosGhost2 / blockSize][xPosGhost2 / blockSize]
            if (xPosGhost2 >= blockSize * 17) {
                xPosGhost2 = 0
            }
            if (xPosGhost2 < 0) {
                xPosGhost2 = blockSize * 17
            }
            if (xDistance2 >= 0 && yDistance2 >= 0) { // Move right and down
                ghostDirection2 = if (ch.toInt() and 4 == 0 && ch.toInt() and 8 == 0) {
                    if (Math.abs(xDistance1) > Math.abs(yDistance2)) {
                        1
                    } else {
                        2
                    }
                } else if (ch.toInt() and 4 == 0) {
                    1
                } else if (ch.toInt() and 8 == 0) {
                    2
                } else 3
            }
            if (xDistance2 >= 0 && yDistance2 <= 0) { // Move right and up
                ghostDirection2 = if (ch.toInt() and 4 == 0 && ch.toInt() and 2 == 0) {
                    if (Math.abs(xDistance2) > Math.abs(yDistance2)) {
                        1
                    } else {
                        0
                    }
                } else if (ch.toInt() and 4 == 0) {
                    1
                } else if (ch.toInt() and 2 == 0) {
                    0
                } else 2
            }
            if (xDistance2 <= 0 && yDistance2 >= 0) { // Move left and down
                ghostDirection2 = if (ch.toInt() and 1 == 0 && ch.toInt() and 8 == 0) {
                    if (Math.abs(xDistance2) > Math.abs(yDistance2)) {
                        3
                    } else {
                        2
                    }
                } else if (ch.toInt() and 1 == 0) {
                    3
                } else if (ch.toInt() and 8 == 0) {
                    2
                } else 1
            }
            if (xDistance2 <= 0 && yDistance2 <= 0) { // Move left and up
                ghostDirection2 = if (ch.toInt() and 1 == 0 && ch.toInt() and 2 == 0) {
                    if (Math.abs(xDistance2) > Math.abs(yDistance2)) {
                        3
                    } else {
                        0
                    }
                } else if (ch.toInt() and 1 == 0) {
                    3
                } else if (ch.toInt() and 2 == 0) {
                    0
                } else 2
            }
            // Handles wall collisions
            if (ghostDirection2 == 3 && ch.toInt() and 1 != 0 ||
                ghostDirection2 == 1 && ch.toInt() and 4 != 0 ||
                ghostDirection2 == 0 && ch.toInt() and 2 != 0 ||
                ghostDirection2 == 2 && ch.toInt() and 8 != 0
            ) {
                ghostDirection2 = 4
            }
        }
        if (ghostDirection2 == 0) {
            yPosGhost2 += -blockSize / 20
        } else if (ghostDirection2 == 1) {
            xPosGhost2 += blockSize / 20
        } else if (ghostDirection2 == 2) {
            yPosGhost2 += blockSize / 20
        } else if (ghostDirection2 == 3) {
            xPosGhost2 += -blockSize / 20
        }
        canvas.drawBitmap(ghostBitmap!!, xPosGhost2.toFloat(), yPosGhost2.toFloat(), paint)
    }
    fun moveGhost1(canvas: Canvas) {
        val ch: Short
        xDistance1 = xPosPacman - xPosGhost1
        yDistance1 = yPosPacman - yPosGhost1
        if (xPosGhost1 % blockSize == 0 && yPosGhost1 % blockSize == 0) {
            ch = leveldata1[yPosGhost1 / blockSize][xPosGhost1 / blockSize]
            if (xPosGhost1 >= blockSize * 17) {
                xPosGhost1 = 0
            }
            if (xPosGhost1 < 0) {
                xPosGhost1 = blockSize * 17
            }
            if (xDistance1 >= 0 && yDistance1 >= 0) { // Move right and down
                ghostDirection1 = if (ch.toInt() and 4 == 0 && ch.toInt() and 8 == 0) {
                    if (Math.abs(xDistance1) > Math.abs(yDistance1)) {
                        1
                    } else {
                        2
                    }
                } else if (ch.toInt() and 4 == 0) {
                    1
                } else if (ch.toInt() and 8 == 0) {
                    2
                } else 3
            }
            if (xDistance1 >= 0 && yDistance1 <= 0) { // Move right and up
                ghostDirection1 = if (ch.toInt() and 4 == 0 && ch.toInt() and 2 == 0) {
                    if (Math.abs(xDistance1) > Math.abs(yDistance1)) {
                        1
                    } else {
                        0
                    }
                } else if (ch.toInt() and 4 == 0) {
                    1
                } else if (ch.toInt() and 2 == 0) {
                    0
                } else 2
            }
            if (xDistance1 <= 0 && yDistance1 >= 0) { // Move left and down
                ghostDirection1 = if (ch.toInt() and 1 == 0 && ch.toInt() and 8 == 0) {
                    if (Math.abs(xDistance1) > Math.abs(yDistance1)) {
                        3
                    } else {
                        2
                    }
                } else if (ch.toInt() and 1 == 0) {
                    3
                } else if (ch.toInt() and 8 == 0) {
                    2
                } else 1
            }
            if (xDistance1 <= 0 && yDistance1 <= 0) { // Move left and up
                ghostDirection1 = if (ch.toInt() and 1 == 0 && ch.toInt() and 2 == 0) {
                    if (Math.abs(xDistance1) > Math.abs(yDistance1)) {
                        3
                    } else {
                        0
                    }
                } else if (ch.toInt() and 1 == 0) {
                    3
                } else if (ch.toInt() and 2 == 0) {
                    0
                } else 2
            }
            // Handles wall collisions
            if (ghostDirection1 == 3 && ch.toInt() and 1 != 0 ||
                ghostDirection1 == 1 && ch.toInt() and 4 != 0 ||
                ghostDirection1 == 0 && ch.toInt() and 2 != 0 ||
                ghostDirection1 == 2 && ch.toInt() and 8 != 0
            ) {
                ghostDirection1 = 4
            }
        }
        if (ghostDirection1 == 0) {
            yPosGhost1 += -blockSize / 20
        } else if (ghostDirection1 == 1) {
            xPosGhost1 += blockSize / 20
        } else if (ghostDirection1 == 2) {
            yPosGhost1 += blockSize / 20
        } else if (ghostDirection1 == 3) {
            xPosGhost1 += -blockSize / 20
        }
        canvas.drawBitmap(ghostBitmap!!, xPosGhost1.toFloat(), yPosGhost1.toFloat(), paint)
    }

    // Updates the character sprite and handles collisions
    fun movePacman(canvas: Canvas) {
        val ch: Short

        // Check if xPos and yPos of pacman is both a multiple of block size
        if (xPosPacman % blockSize == 0 && yPosPacman % blockSize == 0) {

            // When pacman goes through tunnel on
            // the right reappear at left tunnel
            if (xPosPacman >= blockSize * 17) {
                xPosPacman = 0
            }

            // Is used to find the number in the level array in order to
            // check wall placement, pellet placement, and candy placement
            ch = leveldata1[yPosPacman / blockSize][xPosPacman / blockSize]

            // If there is a pellet, eat it
            if (ch.toInt() and 16 != 0) {
                // Toggle pellet so it won't be drawn anymore
                leveldata1[yPosPacman / blockSize][xPosPacman / blockSize] = (ch xor 16) as Short
                currentScore += 10
            }

            // Checks for direction buffering
            if (!(nextDirection == 3 && ch.toInt() and 1 != 0 ||
                        nextDirection == 1 && ch.toInt() and 4 != 0 ||
                        nextDirection == 0 && ch.toInt() and 2 != 0 ||
                        nextDirection == 2 && ch.toInt() and 8 != 0)
            ) {
                direction = nextDirection
                viewDirection = direction
            }

            // Checks for wall collisions
            if (direction == 3 && ch.toInt() and 1 != 0 ||
                direction == 1 && ch.toInt() and 4 != 0 ||
                direction == 0 && ch.toInt() and 2 != 0 ||
                direction == 2 && ch.toInt() and 8 != 0
            ) {
                direction = 4
            }
        }

        // When pacman goes through tunnel on
        // the left reappear at right tunnel
        if (xPosPacman < 0) {
            xPosPacman = blockSize * 17
        }
        drawPacman(canvas)

        // Depending on the direction move the position of pacman
        if (direction == 0) {
            yPosPacman += -blockSize / 15
        } else if (direction == 1) {
            xPosPacman += blockSize / 15
        } else if (direction == 2) {
            yPosPacman += blockSize / 15
        } else if (direction == 3) {
            xPosPacman += -blockSize / 15
        }
    }

    private fun drawArrowIndicators(canvas: Canvas) {
        when (nextDirection) {
            0 -> canvas.drawBitmap(
                arrowUp[currentArrowFrame]!!,
                (5 * blockSize).toFloat(),
                (20 * blockSize).toFloat(),
                paint
            )
            1 -> canvas.drawBitmap(
                arrowRight[currentArrowFrame]!!,
                (5 * blockSize).toFloat(),
                (20 * blockSize).toFloat(),
                paint
            )
            2 -> canvas.drawBitmap(
                arrowDown[currentArrowFrame]!!,
                (5 * blockSize).toFloat(),
                (20 * blockSize).toFloat(),
                paint
            )
            3 -> canvas.drawBitmap(
                arrowLeft[currentArrowFrame]!!,
                (5 * blockSize).toFloat(),
                (20 * blockSize).toFloat(),
                paint
            )
            else -> {
            }
        }
    }

    // Method that draws pacman based on his viewDirection
    fun drawPacman(canvas: Canvas) {
        when (viewDirection) {
            0 -> canvas.drawBitmap(
                pacmanUp[currentPacmanFrame]!!,
                xPosPacman.toFloat(),
                yPosPacman.toFloat(),
                paint
            )
            1 -> canvas.drawBitmap(
                pacmanRight[currentPacmanFrame]!!,
                xPosPacman.toFloat(),
                yPosPacman.toFloat(),
                paint
            )
            3 -> canvas.drawBitmap(
                pacmanLeft[currentPacmanFrame]!!,
                xPosPacman.toFloat(),
                yPosPacman.toFloat(),
                paint
            )
            else -> canvas.drawBitmap(
                pacmanDown[currentPacmanFrame]!!,
                xPosPacman.toFloat(),
                yPosPacman.toFloat(),
                paint
            )
        }
    }

    // Method that draws pellets and updates them when eaten
    fun drawPellets(canvas: Canvas) {
        var x: Float
        var y: Float
        for (i in 0..17) {
            for (j in 0..16) {
                x = (j * blockSize).toFloat()
                y = (i * blockSize).toFloat()
                // Draws pellet in the middle of a block
                if (leveldata1[i][j].toInt() and 16 != 0) canvas.drawCircle(
                    x + blockSize / 2,
                    y + blockSize / 2,
                    (blockSize / 10).toFloat(),
                    paint
                )
            }
        }
    }

    // Method to draw map layout
    fun drawMap(canvas: Canvas) {
        paint.color = Color.BLUE
        paint.strokeWidth = 2.5f
        var x: Int
        var y: Int
        for (i in 0..17) {
            for (j in 0..16) {
                x = j * blockSize
                y = i * blockSize
                if (leveldata1[i][j].toInt() and 1 != 0) // draws left
                    canvas.drawLine(
                        x.toFloat(),
                        y.toFloat(),
                        x.toFloat(),
                        (y + blockSize - 1).toFloat(),
                        paint
                    )
                if (leveldata1[i][j].toInt() and 2 != 0) // draws top
                    canvas.drawLine(
                        x.toFloat(),
                        y.toFloat(),
                        (x + blockSize - 1).toFloat(),
                        y.toFloat(),
                        paint
                    )
                if (leveldata1[i][j].toInt() and 4 != 0) // draws right
                    canvas.drawLine(
                        (
                                x + blockSize).toFloat(),
                        y.toFloat(),
                        (x + blockSize).toFloat(),
                        (y + blockSize - 1).toFloat(),
                        paint
                    )
                if (leveldata1[i][j].toInt() and 8 != 0) // draws bottom
                    canvas.drawLine(
                        x.toFloat(),
                        (y + blockSize).toFloat(),
                        (x + blockSize - 1).toFloat(),
                        (y + blockSize).toFloat(),
                        paint
                    )
            }
        }
        paint.color = Color.WHITE
    }

    var longPressed = Runnable {
        Log.i("info", "LongPress")
        val pauseIntent = Intent(getContext(), PauseActivity::class.java)
        getContext().startActivity(pauseIntent)
    }

    // Method to get touch events
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
                handler.postDelayed(longPressed, LONG_PRESS_TIME.toLong())
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y
                calculateSwipeDirection()
                handler.removeCallbacks(longPressed)
            }
        }
        return true
    }

    // Calculates which direction the user swipes
    // based on calculating the differences in
    // initial position vs final position of the swipe
    private fun calculateSwipeDirection() {
        val xDiff = x2 - x1
        val yDiff = y2 - y1

        // Directions
        // 0 means going up
        // 1 means going right
        // 2 means going down
        // 3 means going left
        // 4 means stop moving, look at move function

        // Checks which axis has the greater distance
        // in order to see which direction the swipe is
        // going to be (buffering of direction)
        if (Math.abs(yDiff) > Math.abs(xDiff)) {
            if (yDiff < 0) {
                nextDirection = 0
            } else if (yDiff > 0) {
                nextDirection = 2
            }
        } else {
            if (xDiff < 0) {
                nextDirection = 3
            } else if (xDiff > 0) {
                nextDirection = 1
            }
        }
    }

    // Check to see if we should update the current frame
    // based on time passed so the animation won't be too
    // quick and look bad
    private fun updateFrame(gameTime: Long,canvas:Canvas) {

        // If enough time has passed go to next frame
        if (gameTime > frameTicker + totalFrame * 30) {
            frameTicker = gameTime
            // Increment the frame
            currentPacmanFrame++
            // Loop back the frame when you have gone through all the frames
            if (currentPacmanFrame >= totalFrame) {
                currentPacmanFrame = 0
            }
        }
        if (gameTime > frameTicker + 50) {
            currentArrowFrame++
            if (currentArrowFrame >= 7) {
                currentArrowFrame = 0
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i("info", "Surface Created")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.i("info", "Surface Changed")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i("info", "Surface Destroyed")
    }

    fun pause() {
        Log.i("info", "pause")
        canDraw = false
        thread = null
    }

    fun resume() {
        Log.i("info", "resume")
        if (thread != null) {
            thread!!.start()
        }
        if (thread == null) {
            thread = Thread(this)
            thread!!.start()
            Log.i("info", "resume thread")
        }
        canDraw = true
    }

    private fun loadBitmapImages() {
        // Scales the sprites based on screen
        var spriteSize = screenWidth / 17 // Size of Pacman & Ghost
        spriteSize = spriteSize / 5 * 5 // Keep it a multiple of 5
        val arrowSize = 7 * blockSize // Size of arrow indicators

        // Add bitmap images of right arrow indicators
        arrowRight = arrayOfNulls(7) // 7 image frames for right direction
        arrowRight[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame1
            ), arrowSize, arrowSize, false
        )
        arrowRight[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame2
            ), arrowSize, arrowSize, false
        )
        arrowRight[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame3
            ), arrowSize, arrowSize, false
        )
        arrowRight[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame4
            ), arrowSize, arrowSize, false
        )
        arrowRight[4] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame5
            ), arrowSize, arrowSize, false
        )
        arrowRight[5] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame6
            ), arrowSize, arrowSize, false
        )
        arrowRight[6] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.right_arrow_frame7
            ), arrowSize, arrowSize, false
        )
        arrowDown = arrayOfNulls(7) // 7 images frames for down direction
        arrowDown[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame1
            ), arrowSize, arrowSize, false
        )
        arrowDown[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame2
            ), arrowSize, arrowSize, false
        )
        arrowDown[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame3
            ), arrowSize, arrowSize, false
        )
        arrowDown[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame4
            ), arrowSize, arrowSize, false
        )
        arrowDown[4] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame5
            ), arrowSize, arrowSize, false
        )
        arrowDown[5] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame6
            ), arrowSize, arrowSize, false
        )
        arrowDown[6] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.down_arrow_frame7
            ), arrowSize, arrowSize, false
        )
        arrowUp = arrayOfNulls(7) // 7 frames for each direction
        arrowUp[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame1
            ), arrowSize, arrowSize, false
        )
        arrowUp[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame2
            ), arrowSize, arrowSize, false
        )
        arrowUp[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame3
            ), arrowSize, arrowSize, false
        )
        arrowUp[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame4
            ), arrowSize, arrowSize, false
        )
        arrowUp[4] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame5
            ), arrowSize, arrowSize, false
        )
        arrowUp[5] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame6
            ), arrowSize, arrowSize, false
        )
        arrowUp[6] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.up_arrow_frame7
            ), arrowSize, arrowSize, false
        )
        arrowLeft = arrayOfNulls(7) // 7 images frames for left direction
        arrowLeft[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame1
            ), arrowSize, arrowSize, false
        )
        arrowLeft[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame2
            ), arrowSize, arrowSize, false
        )
        arrowLeft[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame3
            ), arrowSize, arrowSize, false
        )
        arrowLeft[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame4
            ), arrowSize, arrowSize, false
        )
        arrowLeft[4] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame5
            ), arrowSize, arrowSize, false
        )
        arrowLeft[5] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame6
            ), arrowSize, arrowSize, false
        )
        arrowLeft[6] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.left_arrow_frame7
            ), arrowSize, arrowSize, false
        )


        // Add bitmap images of pacman facing right
        pacmanRight = arrayOfNulls(totalFrame)
        pacmanRight[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_right1
            ), spriteSize, spriteSize, false
        )
        pacmanRight[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_right2
            ), spriteSize, spriteSize, false
        )
        pacmanRight[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_right3
            ), spriteSize, spriteSize, false
        )
        pacmanRight[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_right
            ), spriteSize, spriteSize, false
        )
        // Add bitmap images of pacman facing down
        pacmanDown = arrayOfNulls(totalFrame)
        pacmanDown[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_down1
            ), spriteSize, spriteSize, false
        )
        pacmanDown[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_down2
            ), spriteSize, spriteSize, false
        )
        pacmanDown[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_down3
            ), spriteSize, spriteSize, false
        )
        pacmanDown[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_down
            ), spriteSize, spriteSize, false
        )
        // Add bitmap images of pacman facing left
        pacmanLeft = arrayOfNulls(totalFrame)
        pacmanLeft[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_left1
            ), spriteSize, spriteSize, false
        )
        pacmanLeft[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_left2
            ), spriteSize, spriteSize, false
        )
        pacmanLeft[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_left3
            ), spriteSize, spriteSize, false
        )
        pacmanLeft[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_left
            ), spriteSize, spriteSize, false
        )
        // Add bitmap images of pacman facing up
        pacmanUp = arrayOfNulls(totalFrame)
        pacmanUp[0] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_up1
            ), spriteSize, spriteSize, false
        )
        pacmanUp[1] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_up2
            ), spriteSize, spriteSize, false
        )
        pacmanUp[2] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_up3
            ), spriteSize, spriteSize, false
        )
        pacmanUp[3] = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.pacman_up
            ), spriteSize, spriteSize, false
        )
        ghostBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ghost
            ), spriteSize, spriteSize, false
        )
    }

    val leveldata1 = arrayOf(
        shortArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        shortArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        shortArrayOf(19, 26, 26, 18, 26, 26, 26, 22, 0, 19, 26, 26, 26, 18, 26, 26, 22),
        shortArrayOf(21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21),
        shortArrayOf(17, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 20),
        shortArrayOf(25, 26, 26, 20, 0, 25, 26, 22, 0, 19, 26, 28, 0, 17, 26, 26, 28),
        shortArrayOf(0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0),
        shortArrayOf(0, 0, 0, 21, 0, 19, 26, 24, 26, 24, 26, 22, 0, 21, 0, 0, 0),
        shortArrayOf(26, 26, 26, 16, 26, 20, 0, 0, 0, 0, 0, 17, 26, 16, 26, 26, 26),
        shortArrayOf(0, 0, 0, 21, 0, 17, 26, 26, 26, 26, 26, 20, 0, 21, 0, 0, 0),
        shortArrayOf(0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0),
        shortArrayOf(19, 26, 26, 16, 26, 24, 26, 22, 0, 19, 26, 24, 26, 16, 26, 26, 22),
        shortArrayOf(21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21),
        shortArrayOf(25, 22, 0, 21, 0, 0, 0, 17, 2, 20, 0, 0, 0, 21, 0, 19, 28),
        shortArrayOf(0, 21, 0, 17, 26, 26, 18, 24, 24, 24, 18, 26, 26, 20, 0, 21, 0),
        shortArrayOf(19, 24, 26, 28, 0, 0, 25, 18, 26, 18, 28, 0, 0, 25, 26, 24, 22),
        shortArrayOf(21, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 21),
        shortArrayOf(25, 26, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 26, 28)
    )

    companion object {
        var LONG_PRESS_TIME = 750 // Time in milliseconds
    }

    init {
        holder.addCallback(this)
        frameTicker = (1000 / totalFrame).toLong()
        paint = Paint()
        paint.color = Color.WHITE
        val metrics = resources.displayMetrics
        screenWidth = metrics.widthPixels
        blockSize = screenWidth / 17
        blockSize = blockSize / 5 * 5
        xPosGhost1 = 8 * blockSize
        xPosGhost2 = 12 * blockSize
        ghostDirection1 = 4
        ghostDirection2 = 3
        yPosGhost1 = 4 * blockSize
        xPosPacman = 8 * blockSize
        yPosPacman = 13 * blockSize
        yPosGhost2 = 15 * blockSize
        loadBitmapImages()
        Log.i("info", "Constructor")
    }
}