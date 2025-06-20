package vasu.apps.datepickerwheel

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatePickerWheel : LinearLayout {
    private var timelineView: TimelineView? = null
    private var btnToday: TextView? = null
    private var dateText: TextView? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr)
    }

    fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val view: View = inflate(context, R.layout.date_picker_timeline, this)
        timelineView = view.findViewById(R.id.timelineView)
        btnToday = view.findViewById(R.id.btnToday)
        dateText = view.findViewById(R.id.txtSelectedDate)

        val a = context.obtainStyledAttributes(attrs, R.styleable.DatePickerWheel, defStyleAttr, 0)

        val autoDate = a.getBoolean(R.styleable.DatePickerWheel_autoDate, true)
        val mode = a.getInt(R.styleable.DatePickerWheel_mode, 2)
        val isTodayButtonEnabled =
            a.getBoolean(R.styleable.DatePickerWheel_isTodayButtonEnable, false)
        val dateToText = a.getBoolean(R.styleable.DatePickerWheel_dateToText, false)


        btnToday?.visibility = if (isTodayButtonEnabled) VISIBLE else GONE
        dateText?.visibility = if (dateToText) VISIBLE else GONE

        btnToday?.setOnClickListener {
            val today = Calendar.getInstance()
            setActiveDate(today)
            updateDateText(today)
        }

        timelineView!!.setDayTextColor(
            a.getColor(
                R.styleable.DatePickerWheel_dayTextColor,
                ContextCompat.getColor(context, R.color.date_text)
            )
        )
        timelineView!!.setDateTextColor(
            a.getColor(
                R.styleable.DatePickerWheel_dateTextColor,
                ContextCompat.getColor(context, R.color.date_text)
            )
        )
        timelineView!!.setMonthTextColor(
            a.getColor(
                R.styleable.DatePickerWheel_monthTextColor,
                ContextCompat.getColor(context, R.color.date_text)
            )
        )
        timelineView!!.setDisabledDateColor(
            a.getColor(
                R.styleable.DatePickerWheel_disabledColor,
                ContextCompat.getColor(context, R.color.disabled_date)
            )
        )
        timelineView!!.setSelectedColor(
            a.getColor(
                R.styleable.DatePickerWheel_selectedColor,
                ContextCompat.getColor(context, R.color.selected_background)
            )
        )
        timelineView!!.setNormalBackground(
            a.getColor(
                R.styleable.DatePickerWheel_normalDateBackground,
                ContextCompat.getColor(context, R.color.normal_background)
            )
        )

        val numberOfDayVisible = a.getInt(R.styleable.DatePickerWheel_numberOfDayVisible, 7)
        val monthTextSize = a.getDimension(R.styleable.DatePickerWheel_monthTextSize, 13f)
        val dateTextSize = a.getDimension(R.styleable.DatePickerWheel_dateTextSize, 25f)
        val dayTextSize = a.getDimension(R.styleable.DatePickerWheel_dayTextSize, 13f)
        val orientation = a.getInt(R.styleable.DatePickerWheel_datePickerOrientation, 0)

        a.recycle()

        timelineView!!.initializeWithAttributes(autoDate, mode)
        timelineView!!.setNumberOfDayVisible(numberOfDayVisible)
        timelineView!!.setMonthTextSize(monthTextSize)
        timelineView!!.setDateTextSize(dateTextSize)
        timelineView!!.setDayTextSize(dayTextSize)
        timelineView!!.setOrientation(orientation)
    }


    private fun updateDateText(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        dateText?.text = formattedDate
    }

    fun setOnDateSelectedListener(listener: OnDateSelected) {
        timelineView?.setOnDateSelectedListener(object : OnDateSelected {
            override fun onDateSelected(year: Int, month: Int, day: Int, dayOfWeek: Int) {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                updateDateText(calendar)
                listener.onDateSelected(year, month, day, dayOfWeek)
            }

            override fun onDisabledDateSelected(
                year: Int,
                month: Int,
                day: Int,
                dayOfWeek: Int,
                isDisabled: Boolean
            ) {
                listener.onDisabledDateSelected(year, month, day, dayOfWeek, isDisabled)
            }
        })
    }

    /**
     * Set a Start date for the calendar (Default, 1 Jan 1970)
     * @param year start year
     * @param month start month
     * @param date start date
     */
    fun setInitialDate(year: Int, month: Int, date: Int) {
        timelineView!!.setInitialDate(year, month, date)
    }

    /**
     * Set selected background to active date
     * @param date Active Date
     */
    fun setActiveDate(date: Calendar?) {
        timelineView!!.setActiveDate(date!!)
        updateDateText(date)
    }

    /**
     * Deactivate dates from the calendar. User won't be able to select
     * the deactivated date.
     * @param dates Array of Dates
     */
    fun deactivateDates(dates: Array<Date?>?) {
        dates?.let { timelineView!!.deactivateDates(it) }
    }
}