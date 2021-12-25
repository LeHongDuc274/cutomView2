package com.example.balls

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.iv)
        imageView.setOnLongClickListener { v->
            val item = ClipData.Item(v.tag as? CharSequence)
            val dragData = ClipData(
                v.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item)

            // Instantiate the drag shadow builder.
            val myShadow = View.DragShadowBuilder(v)

            // Start the drag.
            v.startDragAndDrop(dragData,  // The data to be dragged
                myShadow,  // The drag shadow builder
                null,      // No need to use local data
                0          // Flags (not currently used, set to 0)
            )
            true
        }
// Set the drag event listener for the View.
        imageView.setOnDragListener { v, e ->

            // Handles each of the expected events.
            when (e.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data.
                    if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do, applies a blue color tint
                        // to the View to indicate that it can accept data.
                        (v as? ImageView)?.setColorFilter(Color.BLUE)
                        v.visibility = View.GONE
                        // Invalidate the view to force a redraw in the new tint.
                        v.invalidate()

                        // Returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false to indicate that, during the current drag and drop operation,
                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View.
                    (v as? ImageView)?.setColorFilter(Color.GREEN)

                    // Invalidates the view to force a redraw in the new tint.
                    v.invalidate()

                    // Returns true; the value is ignored.
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->{
                    // Ignore the event.
                    var mX = v.x
                    var mY = v.y
                    Log.e("Tag",mX.toString() +" " + mY.toString())
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Resets the color tint to blue.
                    (v as? ImageView)?.setColorFilter(Color.BLUE)

                    // Invalidates the view to force a redraw in the new tint.
                    v.invalidate()

                    // Returns true; the value is ignored.
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data.
                    val item: ClipData.Item = e.clipData.getItemAt(0)

                    // Gets the text data from the item.
                    val dragData = item.text

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is $dragData", Toast.LENGTH_LONG).show()

                    // Turns off any color tints.
                    (v as? ImageView)?.clearColorFilter()
                    // Invalidates the view to force a redraw.
                    v.invalidate()
                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Turns off any color tinting.
                    (v as? ImageView)?.clearColorFilter()
                    v.visibility = View.VISIBLE
                    // Invalidates the view to force a redraw.
                    v.invalidate()

                    // Does a getResult(), and displays what happened.
                    when (e.result) {
                        true ->
                            Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG)
                        else ->
                            Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG)
                    }.show()
                    var mX = v.x
                    var mY = v.y
                    Log.e("Tag2",mX.toString() +" " + mY.toString())
                    // Returns true; the value is ignored.
                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e(
                        "DragDrop Example",
                        "Unknown action type received by View.OnDragListener."
                    )
                    false
                }
            }
        }
    }
}