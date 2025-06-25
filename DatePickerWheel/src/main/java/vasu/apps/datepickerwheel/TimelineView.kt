package vasu.apps.datepickerwheel

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import vasu.apps.datepickerwheel.Adapter.TimeAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.sqrt

class TimelineView : RecyclerView {
    private var adapter: TimeAdapter? = null
    private var autoDate: Boolean = true
    private var monthTextColor = 0
    private var dateTextColor = 0
    private var dayTextColor = 0
    private var disabledColor = 0
    private var normalDate = 0
    private var numberOfDayVisible: Int = 7
    private var timelineOrientation: Int = HORIZONTAL
    private var monthTextSize = 13f
    private var dateTextSize = 25f
    private var dayTextSize = 13f
    private var selectedColor: Int = 0
    private var isMonthEnable: Boolean = true
    private var isDateEnable: Boolean = true
    private var isDayEnable: Boolean = true
    private var deactivatedDates: List<Date> = emptyList()
    private var scrollSpeedFactor = 100f

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

    @JvmName("setNormalBackground1")
    fun setNormalBackground(color: Int) {
        this.normalDate = color
    }

    @JvmName("getNormalBackground1")
    fun getNormalBackground(): Int {
        return normalDate
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

    @JvmName("setScrollSpeedFactor1")
    fun setScrollSpeedFactor(factor: Int) {
        scrollSpeedFactor = factor.toFloat()
    }

    @JvmName("getScrollSpeedFactor1")
    fun getScrollSpeedFactor(): Float {
        return scrollSpeedFactor
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNumberOfDayVisible(days: Int) {
        this.numberOfDayVisible = days
        adapter?.setNumberOfDayVisible(days)
        adapter?.notifyDataSetChanged()
        invalidate()
    }

    fun setMonthEnabled(enabled: Boolean) {
        isMonthEnable = enabled
    }

    fun isMonthEnabled(): Boolean {
        return isMonthEnable
    }

    fun setDateEnabled(enabled: Boolean) {
        isDateEnable = enabled
    }

    fun isDateEnabled(): Boolean {
        return isDateEnable
    }

    fun setDayEnabled(enabled: Boolean) {
        isDayEnable = enabled
    }

    fun isDayEnabled(): Boolean {
        return isDayEnable
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

    @SuppressLint("NotifyDataSetChanged")
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
                    val linearLayoutManager = layoutManager as LinearLayoutManager

                    val smoothScroller = object : LinearSmoothScroller(context) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return scrollSpeedFactor / displayMetrics.densityDpi
                        }

                        override fun getHorizontalSnapPreference(): Int {
                            return SNAP_TO_START
                        }

                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_START
                        }

                        override fun calculateDxToMakeVisible(
                            view: View,
                            snapPreference: Int
                        ): Int {
                            val itemWidth =
                                if (numberOfDayVisible > 0) width / numberOfDayVisible else width
                            val centerOffset = (width / 2) - (itemWidth / 2)
                            val left = linearLayoutManager.getDecoratedLeft(view)
                            return left - centerOffset
                        }

                        override fun calculateDyToMakeVisible(
                            view: View,
                            snapPreference: Int
                        ): Int {
                            val itemHeight =
                                if (numberOfDayVisible > 0) height / numberOfDayVisible else height
                            val centerOffset = (height / 2) - (itemHeight / 2)
                            val top = linearLayoutManager.getDecoratedTop(view)
                            return top - centerOffset
                        }

                        override fun onTargetFound(targetView: View, state: State, action: Action) {
                            val dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference)
                            val dy = calculateDyToMakeVisible(targetView, verticalSnapPreference)
                            val distance = sqrt((dx * dx + dy * dy).toDouble()).toInt()
                            val time = calculateTimeForDeceleration(distance)

                            if (time > 0) {
                                action.update(dx, dy, time, mDecelerateInterpolator)
                            }
                        }
                    }

                    smoothScroller.targetPosition = position
                    linearLayoutManager.startSmoothScroll(smoothScroller)
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    fun getDeactivatedDates(): List<Date> {
        return deactivatedDates ?: emptyList()
    }

    fun deactivateDates(dates: Array<Date?>) {
        this.deactivatedDates = dates.filterNotNull()
        adapter?.disableDates(dates)
    }

}