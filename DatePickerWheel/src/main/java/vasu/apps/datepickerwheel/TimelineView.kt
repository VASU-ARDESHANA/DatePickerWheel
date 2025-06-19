package vasu.apps.datepickerwheel

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vasu.apps.datepickerwheel.Adapter.TimeAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TimelineView : RecyclerView {
    private var adapter: TimeAdapter? = null
    private var autoDate: Boolean = true
    private var monthTextColor = 0
    private var dateTextColor = 0
    private var dayTextColor = 0
    private var disabledColor = 0
    private var numberOfDayVisible: Int = 7
    private var timelineOrientation: Int = HORIZONTAL
    private var monthTextSize = 13f
    private var dateTextSize = 25f
    private var dayTextSize = 13f
    private var selectedColor: Int = 0

    //    private float monthTextSize, dateTextSize, dayTextSize;
    var year = 0
        private set
    var month = 0
        private set
    var date = 0
        private set

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    var mode: Int = 0

    @JvmName("getMode1")
    fun getMode(): Int {
        return mode
    }

    fun initializeWithAttributes(autoDate: Boolean, mode: Int) {
        this.autoDate = autoDate
        this.mode = mode
        init()
    }

    @JvmName("getMonthTextColor1")
    fun getMonthTextColor(): Int {
        return monthTextColor
    }

    @JvmName("setMonthTextColor1")
    fun setMonthTextColor(color: Int) {
        monthTextColor = color
    }

    @JvmName("getDateTextColor1")
    fun getDateTextColor(): Int {
        return dateTextColor
    }

    @JvmName("setDateTextColor1")
    fun setDateTextColor(color: Int) {
        dateTextColor = color
    }

    @JvmName("getDayTextColor1")
    fun getDayTextColor(): Int {
        return dayTextColor
    }

    @JvmName("setDayTextColor1")
    fun setDayTextColor(color: Int) {
        dayTextColor = color
    }

    @JvmName("setDisabledDateColor1")
    fun setDisabledDateColor(color: Int) {
        this.disabledColor = color
    }

    @JvmName("getDisabledDateColor1")
    fun getDisabledDateColor(): Int {
        return disabledColor
    }

    @JvmName("getYear1")
    fun getYear(): Int {
        return year
    }

    @JvmName("getMonth1")
    fun getMonth(): Int {
        return month
    }

    @JvmName("getDate1")
    fun getDate(): Int {
        return date
    }

    @JvmName("setSelectedColor1")
    fun setSelectedColor(color: Int) {
        this.selectedColor = color
    }

    @JvmName("getSelectedColor1")
    fun getSelectedColor(): Int {
        return selectedColor
    }

    @JvmName("setMonthTextSize1")
    fun setMonthTextSize(size: Float) {
        monthTextSize = size
    }

    @JvmName("getMonthTextSize1")
    fun getMonthTextSize(): Float {
        return monthTextSize
    }

    @JvmName("setDateTextSize1")
    fun setDateTextSize(size: Float) {
        dateTextSize = size
    }

    @JvmName("getDateTextSize1")
    fun getDateTextSize(): Float {
        return dateTextSize
    }

    @JvmName("setDayTextSize1")
    fun setDayTextSize(size: Float) {
        dayTextSize = size
    }

    @JvmName("getDayTextSize1")
    fun getDayTextSize(): Float {
        return dayTextSize
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNumberOfDayVisible(days: Int) {
        this.numberOfDayVisible = days
        adapter?.setNumberOfDayVisible(days)
        adapter?.notifyDataSetChanged()
        invalidate()
    }

    fun setOnDateSelectedListener(listener: OnDateSelected?) {
        adapter!!.setDateSelectedListener(listener)
    }

    fun setInitialDate(year: Int, month: Int, date: Int) {
        this.year = year
        this.month = month - 1
        this.date = date
        invalidate()
    }

    @JvmName("setOrientation1")
    fun setOrientation(orientation: Int) {
        timelineOrientation = if (orientation == 1) VERTICAL else HORIZONTAL
        layoutManager = LinearLayoutManager(context, timelineOrientation, false)
        adapter?.notifyDataSetChanged()
    }


    fun init() {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, timelineOrientation, false)

        val calendar = Calendar.getInstance()

        if (autoDate) {
            when (mode) {
                1 -> calendar.set(Calendar.DAY_OF_MONTH, 1)
                2 -> calendar.set(Calendar.DAY_OF_YEAR, 1)
            }
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            date = calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            year = 1947
            month = 8
            date = 12
        }

        adapter = TimeAdapter(this, -1)

        setAdapter(adapter)
    }


    /**
     * Calculates the date position and set the selected background on that date
     * @param activeDate active Date
     */
    @SuppressLint("SimpleDateFormat")
    fun setActiveDate(activeDate: Calendar) {
        try {
            val initialDate = SimpleDateFormat("yyyy-MM-dd")
                .parse("$year-${month + 1}-$date")

            if (initialDate != null) {
                val diff = activeDate.time.time - initialDate.time
                val position = (diff / (1000 * 60 * 60 * 24)).toInt()

                adapter?.selectDate(activeDate)

                post {
                    val itemWidth = width / numberOfDayVisible
                    val centerOffset = (width / 2) - (itemWidth / 2)
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, centerOffset)
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


    fun deactivateDates(deactivatedDates: Array<Date?>) {
        adapter!!.disableDates(deactivatedDates)
    }

}