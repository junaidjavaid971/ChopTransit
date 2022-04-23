package app.com.choptransit.extentions

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import app.com.choptransit.ChopApplication

fun dpToPx(dp: Float): Int {
    return dpToPx(dp, ChopApplication.instance.resources)
}

private fun dpToPx(dp: Float, resources: Resources): Int {
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    return px.toInt()
}

fun getColorCompat(resId: Int) = ContextCompat.getColor(ChopApplication.instance, resId)

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}
