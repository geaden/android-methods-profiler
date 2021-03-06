package com.github.grishberg.profiler.chart.stages.methods

import com.github.grishberg.profiler.chart.ProfileRectangle
import com.github.grishberg.profiler.common.hexToColor
import com.github.grishberg.profiler.plugins.stages.methods.StageRelatedToMethods
import java.awt.Color
import java.awt.geom.Rectangle2D

class StageRectangle(
    val stage: StageRelatedToMethods,
    val threadTimeStart: kotlin.Double,
    val threadTimeEnd: kotlin.Double,
    val globalTimeTimeStart: kotlin.Double,
    val globalTimeTimeEnd: kotlin.Double,
    val methodRectangle: ProfileRectangle,
    newHeight: kotlin.Double,
    isThreadTimeMode: Boolean,
    val headerColor: Color
) : Rectangle2D.Double() {

    var headerTitleColor = titleColor()
        private set

    init {
        if (newHeight > 0) {
            setup(newHeight, isThreadTimeMode)
        }
    }

    private fun titleColor(): Color {
        val colorWithoutAlpha = headerColor
        val y =
            (299 * colorWithoutAlpha.red + 587 * colorWithoutAlpha.green + 114 * colorWithoutAlpha.blue) / 1000.toDouble()
        return if (y >= 128) Color.black else Color.white
    }

    /**
     * Should call after creating Bookmark.
     * @param height toolbar height
     */
    fun setup(height: kotlin.Double, isThreadTimeMode: Boolean) {
        if (isThreadTimeMode) {
            setRect(threadTimeStart, 0.0, threadTimeEnd - threadTimeStart, height)
        } else {
            setRect(globalTimeTimeStart, 0.0, globalTimeTimeEnd - globalTimeTimeStart, height)
        }
    }

    fun switchThreadTimeMode(threadTimeMode: Boolean) {
        if (threadTimeMode) {
            setRect(threadTimeStart, 0.0, threadTimeEnd - threadTimeStart, height)
            return
        }
        setRect(globalTimeTimeStart, 0.0, globalTimeTimeEnd - globalTimeTimeStart, height)
    }

    companion object {
        private val DEFAULT_ALPHA = 200
        private val DEFAULT_COLOR = Color(0x8c6057)

        fun fromMethodRectangle(
            stage: StageRelatedToMethods,
            profileRectangle: ProfileRectangle,
            height: kotlin.Double,
            isThreadTimeMode: Boolean
        ): StageRectangle {
            val color = hexToColor(stage.color, DEFAULT_COLOR)
            return StageRectangle(
                stage,
                profileRectangle.profileData.threadStartTimeInMillisecond,
                profileRectangle.profileData.threadEndTimeInMillisecond,
                profileRectangle.profileData.globalStartTimeInMillisecond,
                profileRectangle.profileData.globalEndTimeInMillisecond,
                profileRectangle,
                height,
                isThreadTimeMode,
                Color(color.red, color.green, color.blue, DEFAULT_ALPHA)
            )
        }
    }
}
