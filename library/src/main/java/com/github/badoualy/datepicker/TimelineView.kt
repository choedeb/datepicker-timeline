package com.github.badoualy.datepicker

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.badoualy.datepicker.DatePickerTimeline.OnDateSelectedListener
import com.github.badoualy.datepicker.MonthView.DateLabelAdapter
import java.text.DateFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit

class TimelineView : RecyclerView {
    private val calendar = Calendar.getInstance(Locale.getDefault())
    private var adapter: TimelineAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var onDateSelectedListener: OnDateSelectedListener? = null
    private var dateLabelAdapter: DateLabelAdapter? = null
    var startYear = 1970
        private set
    var startMonth = 0
        private set
    var startDay = 1
        private set
    var selectedYear = 0
        private set
    var selectedMonth = 0
        private set
    var selectedDay = 0
        private set
    private var selectedPosition = 1
    private var dayCount = Int.MAX_VALUE

    // Day letter
    var lblDayColor = 0

    // Day number label
    var lblDateColor = 0
    var lblDateSelectedColor = 0

    // Label
    var lblLabelColor = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }

    private fun init() {
        calendar.timeInMillis = System.currentTimeMillis()
        setSelectedDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        resetCalendar()
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = TimelineAdapter()
        setLayoutManager(layoutManager)
        setAdapter(adapter)
    }

    private fun resetCalendar() {
        calendar[startYear, startMonth, startDay, 1, 0] = 0
    }

    private fun onDateSelected(position: Int, year: Int, month: Int, day: Int) {
        if (position == selectedPosition) {
            centerOnPosition(selectedPosition)
            return
        }
        val oldPosition = selectedPosition
        selectedPosition = position
        selectedYear = year
        selectedMonth = month
        selectedDay = day
        if (adapter != null && layoutManager != null) {
            adapter!!.notifyItemChanged(oldPosition)
            adapter!!.notifyItemChanged(position)
            centerOnPosition(selectedPosition)
            if (onDateSelectedListener != null) {
                onDateSelectedListener!!.onDateSelected(selectedYear, selectedMonth, selectedDay, selectedPosition)
            }
        } else {
            post { centerOnPosition(selectedPosition) }
        }
    }

    fun centerOnPosition(position: Int) {
        if (childCount == 0) {
            return
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut) {
                return
            }
        }
        // Animate scroll
        val offset = measuredWidth / 2 - getChildAt(0).measuredWidth / 2
        layoutManager!!.scrollToPositionWithOffset(position, offset)
    }

    fun centerOnSelection() {
        centerOnPosition(selectedPosition)
    }

    fun setSelectedPosition(position: Int) {
        resetCalendar()
        calendar.add(Calendar.DAY_OF_YEAR, position)
        onDateSelected(position, calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        var day = day
        if (year == startYear && month == startMonth && day < startDay) {
            day = startDay
        }

        // Get new selected dayOfYear
        calendar[year, month, day, 1, 0] = 0
        val newDayOfYear = calendar[Calendar.DAY_OF_YEAR]
        val newTimestamp = calendar.timeInMillis

        // Get current selected dayOfYear
        calendar[selectedYear, selectedMonth, selectedDay, 1, 0] = 0
        val oldDayOfYear = calendar[Calendar.DAY_OF_YEAR]
        val oldTimestamp = calendar.timeInMillis
        val dayDifference: Int
        dayDifference = if (year == selectedYear) {
            newDayOfYear - oldDayOfYear
        } else {
            // Lazy...
            val dayDifferenceApprox = ((newTimestamp - oldTimestamp) / TimeUnit.DAYS.toMillis(1)).toInt()
            calendar.add(Calendar.DAY_OF_YEAR, dayDifferenceApprox)
            dayDifferenceApprox + (newDayOfYear - calendar[Calendar.DAY_OF_YEAR])
        }
        onDateSelected(selectedPosition + dayDifference, year, month, day)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun setDateLabelAdapter(dateLabelAdapter: DateLabelAdapter?) {
        this.dateLabelAdapter = dateLabelAdapter
    }

    fun setDayLabelColor(lblDayColor: Int) {
        this.lblDayColor = lblDayColor
    }

    fun setDateLabelColor(lblDateColor: Int) {
        this.lblDateColor = lblDateColor
    }

    fun setDateLabelSelectedColor(lblDateSelectedColor: Int) {
        this.lblDateSelectedColor = lblDateSelectedColor
    }

    fun setLabelColor(lblLabelColor: Int) {
        this.lblLabelColor = lblLabelColor
    }

    fun setFirstDate(startYear: Int, startMonth: Int, startDay: Int) {
        this.startYear = startYear
        this.startMonth = startMonth
        this.startDay = startDay
        selectedYear = startYear
        selectedMonth = startMonth
        selectedDay = startDay
        selectedPosition = 0
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun setLastDate(endYear: Int, endMonth: Int, endDay: Int) {
        val firstDate = Calendar.getInstance()
        firstDate[startYear, startMonth] = startDay
        val lastDate = Calendar.getInstance()
        lastDate[endYear, endMonth] = endDay

        // TODO: might now work for summer time...
        val dayDiff = TimeUnit.DAYS.convert(lastDate.timeInMillis - firstDate.timeInMillis,
                TimeUnit.MILLISECONDS).toInt()
        setDayCount(dayDiff + 1)
    }

    fun setDayCount(dayCount: Int) {
        if (this.dayCount == dayCount) {
            return
        }
        this.dayCount = dayCount
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    private inner class TimelineAdapter internal constructor() : Adapter<TimelineViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.mti_item_day, parent, false)
            return TimelineViewHolder(view)
        }

        override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
            // Set calendar
            resetCalendar()
            calendar.add(Calendar.DAY_OF_YEAR, position)

            // Get values
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val isToday = DateUtils.isToday(calendar.timeInMillis)
            holder.bind(position, year, month, day, dayOfWeek,
                    if (dateLabelAdapter != null) dateLabelAdapter!!.getLabel(calendar, position) else "",
                    position == selectedPosition, isToday)
        }

        override fun getItemCount(): Int {
            return dayCount
        }
    }

    private inner class TimelineViewHolder internal constructor(root: View) : ViewHolder(root) {
        private val lblDay: TextView
        private val lblDate: TextView
        private val lblValue: TextView
        private var position: Int? = 0
        private var year = 0
        private var month = 0
        private var day = 0

        fun bind(position: Int, year: Int, month: Int, day: Int, dayOfWeek: Int, label: CharSequence?, selected: Boolean, isToday: Boolean) {
            this.position = position
            this.year = year
            this.month = month
            this.day = day
            lblDay.text = WEEK_DAYS[dayOfWeek].toUpperCase(Locale.US)
            lblDate.text = day.toString()
            lblValue.text = label
            lblDate.setBackgroundResource(if (selected) R.drawable.mti_bg_lbl_date_selected else if (isToday) R.drawable.mti_bg_lbl_date_today else 0)
            lblDate.setTextColor(if (selected || isToday) lblDateSelectedColor else lblDateColor)
        }

        init {
            lblDay = root.findViewById<View>(R.id.mti_timeline_lbl_day) as TextView
            lblDate = root.findViewById<View>(R.id.mti_timeline_lbl_date) as TextView
            lblValue = root.findViewById<View>(R.id.mti_timeline_lbl_value) as TextView
            lblDay.setTextColor(lblDayColor)
            lblDate.setTextColor(lblDateColor)
            lblValue.setTextColor(lblLabelColor)
            root.setOnClickListener { onDateSelected(position!!, year, month, day) }
        }
    }

    companion object {
        private const val TAG = "TimelineView"
        private val WEEK_DAYS = DateFormatSymbols.getInstance().shortWeekdays
    }
}