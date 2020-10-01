package com.github.badoualy.datepicker.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.badoualy.datepicker.DatePickerTimeline
import com.github.badoualy.datepicker.MonthView
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        setSupportActionBar(toolbar)

        timeline.setDateLabelAdapter(object: MonthView.DateLabelAdapter {
            override fun getLabel(calendar: Calendar?, index: Int): CharSequence? =
                (calendar!!.get(Calendar.MONTH) + 1).toString() + "/" + (calendar.get(Calendar.YEAR) % 2000);
        })

        timeline.onDateSelectedListener = object: DatePickerTimeline.OnDateSelectedListener {
            override fun onDateSelected(year: Int, month: Int, day: Int, index: Int) {

            }
        }

        timeline.setFirstVisibleDate(2016, Calendar.JULY, 19)
        timeline.setLastVisibleDate(2020, Calendar.JULY, 19)
        //timeline.setFollowScroll(false);
    }
}