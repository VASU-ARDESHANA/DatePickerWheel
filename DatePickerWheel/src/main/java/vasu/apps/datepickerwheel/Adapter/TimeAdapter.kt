package vasu.apps.datepickerwheel.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vasu.apps.datepickerwheel.OnDateSelected
import vasu.apps.datepickerwheel.R
import vasu.apps.datepickerwheel.TimelineView
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date

class TimeAdapter(
    private val timelineView: TimelineView,
    private var selectedPosition: Int
) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

    private val calendar: Calendar = Calendar.getInstance()
    private var deactivatedDates: Array<Date?>? = null
    private var listener: OnDateSelected? = null
    private var selectedView: View? = null
    private var numberOfDayVisible: Int = 7

    fun setNumberOfDayVisible(days: Int) {
        numberOfDayVisible = days
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_timeline, parent, false)

        val layoutParams =
            if ((timelineView.layoutManager as LinearLayoutManager).orientation == RecyclerView.HORIZONTAL) {
                val screenWidth = parent.resources.displayMetrics.widthPixels
                val itemWidth = screenWidth / numberOfDayVisible
                RecyclerView.LayoutParams(itemWidth, RecyclerView.LayoutParams.WRAP_CONTENT)
            } else {
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        view.layoutParams = layoutParams

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        resetCalendar(position)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val isDisabledDate = holder.bind(month, day, dayOfWeek, year, position)

        holder.rootView.setOnClickListener { v ->
            if (!isDisabledDate) {
                selectedView?.background = null

                val drawable = ContextCompat.getDrawable(v.context, R.drawable.background)?.mutate()
                drawable?.setTint(timelineView.getSelectedColor())
                v.background = drawable

                selectedPosition = position
                selectedView = v

                listener?.onDateSelected(year, month, day, dayOfWeek)
            } else {
                listener?.onDisabledDateSelected(year, month, day, dayOfWeek, true)
            }
        }

    }


    private fun resetCalendar(position: Int) {

        calendar.set(
            timelineView.getYear(),
            timelineView.getMonth(),
            timelineView.getDate(),
            0,
            0,
            0
        )

        when (timelineView.getMode()) {
            1 -> {
                calendar.add(Calendar.DAY_OF_YEAR, position)
            }
            2 -> {
                calendar.add(Calendar.DAY_OF_YEAR, position)
            }
        }
    }


    /**
     * Set the position of selected date
     * @param selectedPosition active date Position
     */
    fun setSelectedPosition(selectedPosition: Int) {
        this.selectedPosition = selectedPosition
    }

    fun selectDate(date: Calendar) {
        val baseCalendar = Calendar.getInstance()
        baseCalendar.set(
            timelineView.getYear(),
            timelineView.getMonth(),
            timelineView.getDate(),
            0, 0, 0
        )

        val diffDays =
            ((date.timeInMillis - baseCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        selectedPosition = diffDays
        notifyDataSetChanged()
        timelineView.scrollToPosition(selectedPosition)
    }

    override fun getItemCount(): Int {
        return when (timelineView.getMode()) {
            1 -> { // month
                val calendar = Calendar.getInstance()
                calendar.set(timelineView.getYear(), timelineView.getMonth(), 1)
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

            2 -> { // year
                val calendar = Calendar.getInstance()
                calendar.set(timelineView.getYear(), 0, 1)
                calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            }

            else -> 0
        }
    }


    fun disableDates(dates: Array<Date?>?) {
        dates?.let { deactivatedDates = it }
        notifyDataSetChanged()
    }

    fun setDateSelectedListener(listener: OnDateSelected?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val monthView: TextView = itemView.findViewById(R.id.monthView)
        private val dateView: TextView = itemView.findViewById(R.id.dateView)
        private val dayView: TextView = itemView.findViewById(R.id.dayView)
        val rootView: View = itemView.findViewById(R.id.rootView)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(month: Int, day: Int, dayOfWeek: Int, year: Int, position: Int): Boolean {
            monthView.setTextColor(timelineView.getMonthTextColor())
            dateView.setTextColor(timelineView.getDateTextColor())
            dayView.setTextColor(timelineView.getDayTextColor())

            monthView.textSize = timelineView.getMonthTextSize()
            dateView.textSize = timelineView.getDateTextSize()
            dayView.textSize = timelineView.getDayTextSize()

            monthView.text = MONTH_NAME[month]
            dateView.text = day.toString()
            dayView.text = WEEK_DAYS[dayOfWeek]

            if (selectedPosition == position) {
                val drawable =
                    ContextCompat.getDrawable(rootView.context, R.drawable.background)?.mutate()
                drawable?.setTint(timelineView.getSelectedColor())
                rootView.background = drawable
                selectedView = rootView
            } else {
                rootView.background = null
            }

            val todayCal = Calendar.getInstance()
            val isToday = todayCal.get(Calendar.DAY_OF_MONTH) == day &&
                    todayCal.get(Calendar.MONTH) == month &&
                    todayCal.get(Calendar.YEAR) == year

            deactivatedDates?.forEach { date ->
                val tempCal = Calendar.getInstance()
                if (date != null) {
                    tempCal.time = date
                }
                if (tempCal.get(Calendar.DAY_OF_MONTH) == day &&
                    tempCal.get(Calendar.MONTH) == month &&
                    tempCal.get(Calendar.YEAR) == year &&
                    !isToday
                ) {
                    val disabledColor = timelineView.getDisabledDateColor()
                    monthView.setTextColor(disabledColor)
                    dateView.setTextColor(disabledColor)
                    dayView.setTextColor(disabledColor)
                    rootView.background = null
                    return true
                }
            }

            return false
        }
    }

    companion object {
        private val WEEK_DAYS: Array<String> = DateFormatSymbols().shortWeekdays
        private val MONTH_NAME: Array<String> = DateFormatSymbols().shortMonths
    }
}