package com.github.badoualy.datepicker

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormatSymbols
import java.util.*

class MonthView : RecyclerView {
    private var adapter: MonthAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var onMonthSelectedListener: OnMonthSelectedListener? = null

    /**
     * Default indicator and text color
     */
    var defaultColor = 0

    /**
     * Color when month is selected
     */
    var colorSelected = 0

    /**
     * Color when month is before the current selected month
     */
    var colorBeforeSelection = 0
    private var startYear = 1970
    private var startMonth = 0
    private var yearDigitCount = 2
    var isYearOnNewLine = false
    var selectedYear = 0
        private set
    var selectedMonth = 0
        private set
    var selectedPosition = -1
        private set
    private var monthCount = Int.MAX_VALUE

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
        val calendar = Calendar.getInstance()
        setSelectedMonth(calendar[Calendar.YEAR], calendar[Calendar.MONTH], false)
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = MonthAdapter()
        setLayoutManager(layoutManager)
        setAdapter(adapter)
    }

    fun setSelectedMonth(year: Int, month: Int) {
        setSelectedMonth(year, month, true, true)
    }

    fun setSelectedMonth(year: Int, month: Int, callListener: Boolean) {
        setSelectedMonth(year, month, callListener, true)
    }

    fun setSelectedMonth(year: Int, month: Int, callListener: Boolean, centerOnPosition: Boolean) {
        onMonthSelected(year, month, callListener, centerOnPosition)
    }

    private fun onMonthSelected(year: Int, month: Int, callListener: Boolean, centerOnPosition: Boolean) {
        val oldPosition = selectedPosition
        selectedPosition = getPositionForDate(year, month)
        selectedYear = year
        selectedMonth = month
        if (selectedPosition == oldPosition) {
            if (centerOnPosition) {
                centerOnPosition(selectedPosition)
            }
            return
        }
        if (adapter != null && layoutManager != null) {
            val rangeStart = Math.min(oldPosition, selectedPosition)
            val rangeEnd = Math.max(oldPosition, selectedPosition)
            adapter!!.notifyItemRangeChanged(rangeStart, rangeEnd - rangeStart + 1)

            // Animate scroll
            if (centerOnPosition) {
                centerOnPosition(selectedPosition)
            }
            if (callListener && onMonthSelectedListener != null) {
                onMonthSelectedListener!!.onMonthSelected(year, month, selectedPosition)
            }
        } else if (centerOnPosition) {
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
        val offset = measuredWidth / 2 - itemWidth / 2
        layoutManager!!.scrollToPositionWithOffset(position, offset)
    }

    fun centerOnDate(year: Int, month: Int) {
        centerOnPosition(getPositionForDate(year, month))
    }

    fun centerOnSelection() {
        centerOnPosition(selectedPosition)
    }

    fun scrollToYearPosition(year: Int, offsetYear: Int) {
        if (childCount == 0) {
            return
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut) {
                return
            }
        }
        // Animate scroll
        layoutManager!!.scrollToPositionWithOffset(getPositionForDate(year + 1, 0),
                offsetYear + measuredWidth / 2 - itemWidth / 2)
    }

    val itemWidth: Int
        get() = getChildAt(0).measuredWidth

    val yearWidth: Int
        get() = itemWidth * 12

    private fun getYearForPosition(position: Int): Int {
        return (position + startMonth) / 12 + startYear
    }

    private fun getMonthForPosition(position: Int): Int {
        return (startMonth + position) % 12
    }

    private fun getPositionForDate(year: Int, month: Int): Int {
        return 12 * (year - startYear) + month - startMonth
    }

    fun getMonthCount(): Int {
        return monthCount
    }

    fun setYearDigitCount(yearDigitCount: Int) {
        require(!(yearDigitCount < 0 || yearDigitCount > 4)) { "yearDigitCount cannot be smaller than 0 or greater than 4" }
        this.yearDigitCount = yearDigitCount
    }

    fun getYearDigitCount(): Int {
        return yearDigitCount
    }

    fun setFirstDate(startYear: Int, startMonth: Int) {
        this.startYear = startYear
        this.startMonth = startMonth
        selectedYear = startYear
        selectedMonth = startMonth
        selectedPosition = 0
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun setMonthCount(monthCount: Int) {
        if (this.monthCount == monthCount) {
            return
        }
        this.monthCount = monthCount
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun setLastDate(endYear: Int, endMonth: Int) {
        require(!(endYear < startYear || endYear == startYear && endMonth < startMonth)) { "Last visible date cannot be before first visible date" }
        val firstDate = Calendar.getInstance()
        firstDate[startYear, startMonth] = 1
        val lastDate = Calendar.getInstance()
        lastDate[endYear, endMonth] = 1
        val diffYear = lastDate[Calendar.YEAR] - firstDate[Calendar.YEAR]
        val diffMonth = diffYear * 12 + lastDate[Calendar.MONTH] - firstDate[Calendar.MONTH]
        setMonthCount(diffMonth + 1)
    }

    private inner class MonthAdapter internal constructor() : Adapter<MonthViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.mti_item_month, parent, false)
            return MonthViewHolder(view)
        }

        override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
            val year = getYearForPosition(position)
            val month = getMonthForPosition(position)
            holder.bind(year, month, position == selectedPosition, position < selectedPosition)
        }

        override fun getItemCount(): Int {
            return monthCount
        }
    }

    private inner class MonthViewHolder internal constructor(root: View) : ViewHolder(root) {
        private val lbl: TextView
        private val indicator: DotView
        private var year = 0
        private var month = 0
        fun bind(year: Int, month: Int, selected: Boolean, beforeSelection: Boolean) {
            this.year = year
            this.month = month
            var text = MONTHS[month]
            text = text.substring(0, Math.min(text.length, 3)).toUpperCase(Locale.US)
            if (yearDigitCount > 0) {
                text += if (isYearOnNewLine) "\n" else " "
                text += year % Math.pow(10.0, yearDigitCount.toDouble()).toInt()
            }
            lbl.text = text
            val color = if (selected) colorSelected else if (beforeSelection) colorBeforeSelection else defaultColor
            lbl.setTextColor(color)
            indicator.setColor(color)
            indicator.setCircleSizeDp(if (selected) 12 else 5)
        }

        init {
            indicator = root.findViewById<View>(R.id.mti_view_indicator) as DotView
            lbl = root.findViewById<View>(R.id.mti_month_lbl) as TextView
            root.setOnClickListener { onMonthSelected(year, month, true, true) }
        }
    }

    interface OnMonthSelectedListener {
        fun onMonthSelected(year: Int, month: Int, index: Int)
    }

    interface DateLabelAdapter {
        fun getLabel(calendar: Calendar?, index: Int): CharSequence?
    }

    companion object {
        private val MONTHS = DateFormatSymbols.getInstance().shortMonths
    }
}