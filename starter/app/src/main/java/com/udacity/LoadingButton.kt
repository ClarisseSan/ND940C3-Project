package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.renderscript.Sampler.Value
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates


private const val BUTTON_LABEL_OFFSET = 30

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private lateinit var frame: Rect

    private var progress = 0.0

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    //background and text colors
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val loadingColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val arcColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)


    // Set up the paint with which to draw.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55f
        typeface = Typeface.create("", Typeface.BOLD)
        color = backgroundColor
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
    }


    init {

        // Setting the view's isClickable property to true enables that view to accept user input.
        isClickable = true
    }

    override fun performClick(): Boolean {
        super.performClick()

        buttonState = ButtonState.Loading

        if (buttonState == ButtonState.Loading) {
            showAnimation()
        }


        //invalidates the entire view, forcing a call to onDraw() to redraw the view
        invalidate()

        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //draw Button
        paint.color = backgroundColor
        canvas?.drawRect(frame, paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = loadingColor
            canvas?.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )


            //save the state the canvas is in
            canvas?.save()
            //position of circle
            canvas?.translate((width / 2 + 220).toFloat(), 40f)
            //draw loading circle
            paint.color = arcColor
            canvas?.drawArc(
                RectF(0f, 0f, 100f, 100f),
                0f,
                (360 * (progress / 100)).toFloat(),
                true,
                paint
            )
            //remove all modifications to the matrix/clip state since the last save call
            canvas?.restore()
        }

        //draw label
        paint.color = textColor
        val label = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.button_loading)
            ButtonState.Loading -> context.getString(R.string.button_loading)
            ButtonState.Completed -> context.getString(R.string.download)
        }
        canvas?.drawText(
            label, (width / 2).toFloat(), ((height + BUTTON_LABEL_OFFSET) / 2).toFloat(),
            paint
        )


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        frame = Rect(0, 0, width, height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun showAnimation() {
        val valueAnimator = ValueAnimator.ofFloat(0f, width.toFloat())

        valueAnimator.duration = 5000
        valueAnimator.addUpdateListener { animation ->
            progress = (animation.animatedValue as Float).toDouble()
            invalidate()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.Completed
                isEnabled = true
            }
        })
        valueAnimator.start()
    }


}