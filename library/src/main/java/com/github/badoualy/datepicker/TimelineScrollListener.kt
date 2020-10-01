package com.github.badoualy.datepicker

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import java.util.*

internal class TimelineScrollListener(private val monthView: MonthView, private val timelineView: TimelineView) : RecyclerView.OnScrollListener() {
    private val calendar = Calendar.getInstance()
    private var year = -1
    private var yearStartOffset = 0
    private var yearEndOffset = 0
    private var monthCount = 12
    private val timelineItemWidth: Int
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val scrollOffset = recyclerView.computeHorizontalScrollOffset()
        val scrollOffsetCenter = scrollOffset + recyclerView.measuredWidth / 2
        val centerPosition = scrollOffsetCenter / timelineItemWidth
        if (!(scrollOffsetCenter >= yearStartOffset && scrollOffsetCenter <= yearEndOffset)) {
            calendar[timelineView.startYear, timelineView.startMonth] = timelineView.startDay
            val startDay = calendar[Calendar.DAY_OF_YEAR]
            calendar.add(Calendar.DAY_OF_YEAR, centerPosition)
            year = calendar[Calendar.YEAR]
            var yearDayCount = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            if (year != timelineView.startYear) {
                val yearDay = calendar[Calendar.DAY_OF_YEAR]
                yearStartOffset = (scrollOffsetCenter
                        - yearDay * timelineItemWidth - scrollOffsetCenter % timelineItemWidth)
                monthCount = 12
            } else {
                yearStartOffset = 0
                monthCount = 12 - timelineView.startMonth
                yearDayCount -= startDay
            }
            yearEndOffset = yearStartOffset + yearDayCount * timelineItemWidth
        }
        Log.v("TimeScrollListener", "yearStartOffset: " + yearStartOffset + ", " + "yearEndOffset: " + yearEndOffset + ", "
                + "scrollOffsetCenter: " + scrollOffsetCenter)
        val progress = (scrollOffsetCenter - yearStartOffset).toFloat() / (yearEndOffset - yearStartOffset)
        val yearOffset = ((1 - progress) * (monthCount * monthView.itemWidth)).toInt()
        Log.v("TimeScrollListener", "progress: $progress, monthOffset: $yearOffset")
        monthView.scrollToYearPosition(year, yearOffset)
    }

    init {
        timelineItemWidth = timelineView.resources.getDimensionPixelSize(R.dimen.mti_timeline_width)
    }
}