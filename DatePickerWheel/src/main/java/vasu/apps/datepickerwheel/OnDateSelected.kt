package vasu.apps.datepickerwheel

interface OnDateSelected {
    fun onDateSelected(year: Int, month: Int, day: Int, dayOfWeek: Int)
    fun onDisabledDateSelected(year: Int, month: Int, day: Int, dayOfWeek: Int, isDisabled: Boolean)
}
