package com.bugtsa.casher.ui.screens.charts

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bugtsa.casher.R

/**
 * Base class of all Activities of the Demo Application.
 *
 * @author Philipp Jahoda
 */
abstract class DemoBase : AppCompatActivity() {
    protected val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )
    protected var tfRegular: Typeface? = null
    @JvmField
    protected var tfLight: Typeface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
    }

}