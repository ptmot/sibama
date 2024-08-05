package com.sibama2024ai
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso

class TampilFotoActivity : AppCompatActivity(), View.OnTouchListener {

    private lateinit var imageView: ImageView
    // Matriks ini akan digunakan untuk memindahkan dan memperbesar gambar
    private val matrix = Matrix()
    private val savedMatrix = Matrix()

    // Kita bisa berada dalam salah satu dari 3 status ini
    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
    private var mode = NONE

    // Mengingat beberapa hal untuk memperbesar
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private val TAG = "Touch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampil_foto)

        imageView = findViewById(R.id.imageView)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val imageUrl = intent.getStringExtra("IMAGE_URL")

        //imageView.setOnTouchListener(this)

        // Load image into ImageView using Picasso
        Picasso.get().load(imageUrl).into(imageView)
        centerImage()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val view = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        var scale: Float

        dumpEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                Log.d(TAG, "mode=DRAG")
                mode = DRAG
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                Log.d(TAG, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                Log.d(TAG, "oldDist=$oldDist")
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                    Log.d(TAG, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    if (view.left >= -392) {
                        matrix.postTranslate(event.x - start.x, event.y - start.y)
                    }
                } else if (mode == ZOOM) {
                    val newDist = spacing(event)
                    Log.d(TAG, "newDist=$newDist")
                    if (newDist > 5f) {
                        matrix.set(savedMatrix)
                        scale = newDist / oldDist
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }
            }
        }
        view.imageMatrix = matrix
        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf(
            "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"
        )
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount) sb.append(";")
        }
        sb.append("]")
        Log.d(TAG, sb.toString())
    }

    private fun centerImage() {
        // Get dimensions of the image
        val drawable = imageView.drawable ?: return
        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()

        // Get dimensions of ImageView
        val imageViewWidth = imageView.width.toFloat()
        val imageViewHeight = imageView.height.toFloat()

        // Calculate scale
        val scale = if (imageWidth > imageHeight) {
            imageViewWidth / imageWidth
        } else {
            imageViewHeight / imageHeight
        }

        // Calculate translation to center image
        val dx = (imageViewWidth - imageWidth * scale) / 2
        val dy = (imageViewHeight - imageHeight * scale) / 2

        // Set initial matrix to center image
        matrix.reset()
        matrix.postScale(scale, scale)
        matrix.postTranslate(dx, dy)

        // Apply matrix to ImageView
        imageView.imageMatrix = matrix
    }

}
