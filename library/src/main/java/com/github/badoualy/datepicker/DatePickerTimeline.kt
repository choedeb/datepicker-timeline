package com.github.badoualy.datepicker

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.badoualy.datepicker.MonthView.DateLabelAdapter
import com.github.badoualy.datepicker.MonthView.OnMonthSelectedListener
import com.github.badoualy.datepicker.Utils.getPrimaryColor
import com.github.badoualy.datepicker.Utils.getPrimaryDarkColor
import java.util.*

class DatePickerTimeline : LinearLayout, OnMonthSelectedListener {
    var monthView: MonthView? = null
        private set
    var timelineView: TimelineView? = null
        private set
    var onDateSelectedListener: OnDateSelectedListener? = null
    private var timelineScrollListener: TimelineScrollListener? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0) {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val calendar = Calendar.getInstance()
        var startYear = calendar[Calendar.YEAR]
        if (calendar[Calendar.MONTH] == Calendar.JANUARY) {
            // If we are in january, we'll probably want to have previous year :)
            startYear--
        }
        val startMonth = Calendar.JANUARY
        val startDay = 1

        // Load default values
        var primaryColor = getPrimaryColor(context)
        var primaryDarkColor = getPrimaryDarkColor(context)
        var bgTimelineColor = ContextCompat.getColor(context, R.color.mti_bg_timeline)
        var tabSelectedColor = ContextCompat.getColor(context, R.color.mti_lbl_tab_selected)
        var tabBeforeSelectionColor = ContextCompat.getColor(context, R.color.mti_lbl_tab_before_selection)
        var lblDayColor = ContextCompat.getColor(context, R.color.mti_lbl_day)
        var lblDateColor = ContextCompat.getColor(context, R.color.mti_lbl_date)
        var lblDateSelectedColor = ContextCompat.getColor(context, R.color.mti_lbl_date_selected)
        var bgLblDateSelectedColor = ContextCompat.getColor(context, R.color.mti_bg_lbl_date_selected_color)
        var ringLblDateSelectedColor = ContextCompat.getColor(context, R.color.mti_ring_lbl_date_color)
        var bgLblTodayColor = ContextCompat.getColor(context, R.color.mti_bg_lbl_today)
        var lblLabelColor = ContextCompat.getColor(context, R.color.mti_lbl_label)

        // Load xml attrs
        val a = context
                .obtainStyledAttributes(attrs, R.styleable.DatePickerTimeline, defStyleAttr, 0)
        primaryColor = a.getColor(R.styleable.DatePickerTimeline_mti_primaryColor, primaryColor)
        primaryDarkColor = a.getColor(R.styleable.DatePickerTimeline_mti_primaryDarkColor, primaryDarkColor)
        bgTimelineColor = a.getColor(R.styleable.DatePickerTimeline_mti_bgTimelineColor, bgTimelineColor)
        tabSelectedColor = a.getColor(R.styleable.DatePickerTimeline_mti_tabSelectedColor, tabSelectedColor)
        tabBeforeSelectionColor = a
                .getColor(R.styleable.DatePickerTimeline_mti_tabBeforeSelectionColor, tabBeforeSelectionColor)
        lblDayColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblDayColor, lblDayColor)
        lblDateColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblDateColor, lblDateColor)
        lblDateSelectedColor = a
                .getColor(R.styleable.DatePickerTimeline_mti_lblDateSelectedColor, lblDateSelectedColor)
        bgLblDateSelectedColor = a
                .getColor(R.styleable.DatePickerTimeline_mti_bgLblDateSelectedColor, bgLblDateSelectedColor)
        ringLblDateSelectedColor = a
                .getColor(R.styleable.DatePickerTimeline_mti_ringLblDateSelectedColor, ringLblDateSelectedColor)
        bgLblTodayColor = a.getColor(R.styleable.DatePickerTimeline_mti_bgLblTodayColor, bgLblTodayColor)
        lblLabelColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblLabelColor, lblLabelColor)
        val followScroll = a.getBoolean(R.styleable.DatePickerTimeline_mti_followScroll, true)
        val yearDigitCount = a.getInt(R.styleable.DatePickerTimeline_mti_yearDigitCount, 2)
        val yearOnNewLine = a.getBoolean(R.styleable.DatePickerTimeline_mti_yearOnNewLine, true)
        a.recycle()
        val selectedDrawable = ContextCompat
                .getDrawable(context, R.drawable.mti_bg_lbl_date_selected) as LayerDrawable?
        (selectedDrawable!!.getDrawable(0) as GradientDrawable).setColor(ringLblDateSelectedColor)
        (selectedDrawable.getDrawable(1) as GradientDrawable).setColor(bgLblDateSelectedColor)
        val todayDrawable = ContextCompat
                .getDrawable(context, R.drawable.mti_bg_lbl_date_today) as LayerDrawable?
        (todayDrawable!!.getDrawable(1) as GradientDrawable).setColor(bgLblTodayColor)
        orientation = VERTICAL
        val view = View.inflate(context, R.layout.mti_datepicker_timeline, this)
        monthView = view.findViewById<View>(R.id.mti_month_view) as MonthView
        timelineView = view.findViewById<View>(R.id.mti_timeline) as TimelineView
        monthView!!.setBackgroundColor(primaryColor)
        monthView!!.setFirstDate(startYear, startMonth)
        monthView!!.defaultColor = primaryDarkColor
        monthView!!.colorSelected = tabSelectedColor
        monthView!!.colorBeforeSelection = tabBeforeSelectionColor
        monthView!!.setYearDigitCount(yearDigitCount)
        monthView!!.isYearOnNewLine = yearOnNewLine
        monthView!!.onMonthSelectedListener = this
        timelineView!!.setBackgroundColor(bgTimelineColor)
        timelineView!!.setFirstDate(startYear, startMonth, startDay)
        timelineView!!.setDayLabelColor(lblDayColor)
        timelineView!!.setDateLabelColor(lblDateColor)
        timelineView!!.setDateLabelSelectedColor(lblDateSelectedColor)
        timelineView!!.setLabelColor(lblLabelColor)
        timelineView!!.onDateSelectedListener = object : OnDateSelectedListener {
            override fun onDateSelected(year: Int, month: Int, day: Int, index: Int) {
                monthView!!.setSelectedMonth(year, month, false, timelineScrollListener == null)
                if (onDateSelectedListener != null) {
                    onDateSelectedListener!!.onDateSelected(year, month, day, index)
                }
            }
        }
        timelineView!!.setSelectedDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])
        if (followScroll) {
            timelineScrollListener = TimelineScrollListener(monthView!!, timelineView!!)
            timelineView!!.addOnScrollListener(timelineScrollListener!!)
        }
    }

    val selectedYear: Int
        get() = timelineView!!.selectedYear

    val selectedMonth: Int
        get() = timelineView!!.selectedMonth

    val selectedDay: Int
        get() = timelineView!!.selectedDay

    fun setDateLabelAdapter(dateLabelAdapter: DateLabelAdapter?) {
        timelineView!!.setDateLabelAdapter(dateLabelAdapter)
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        timelineView!!.setSelectedDate(year, month, day)
    }

    override fun onMonthSelected(year: Int, month: Int, index: Int) {
        timelineView!!.setSelectedDate(year, month, 1)
    }

    fun setFirstVisibleDate(year: Int, month: Int, day: Int) {
        monthView!!.setFirstDate(year, month)
        timelineView!!.setFirstDate(year, month, day)
    }

    fun setLastVisibleDate(year: Int, month: Int, day: Int) {
        monthView!!.setLastDate(year, month)
        timelineView!!.setLastDate(year, month, day)
    }

    fun centerOnSelection() {
        monthView!!.centerOnSelection()
        timelineView!!.centerOnSelection()
    }

    val monthSelectedPosition: Int
        get() = monthView!!.selectedPosition

    val timelineSelectedPosition: Int
        get() = timelineView!!.getSelectedPosition()

    fun setFollowScroll(followScroll: Boolean) {
        if (!followScroll && timelineScrollListener != null) {
            timelineView!!.removeOnScrollListener(timelineScrollListener!!)
            timelineScrollListener = null
        } else if (followScroll && timelineScrollListener == null) {
            timelineScrollListener = TimelineScrollListener(monthView!!, timelineView!!)
            timelineView!!.addOnScrollListener(timelineScrollListener!!)
        }
    }

    interface OnDateSelectedListener {
        fun onDateSelected(year: Int, month: Int, day: Int, index: Int)
    }
}