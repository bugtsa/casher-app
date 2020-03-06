package com.bugtsa.casher.presentation.chart

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.models.charts.ChooseChartsModel
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.monthCalendarValue
import com.bugtsa.casher.presentation.optional.RxViewModel
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import com.bugtsa.casher.ui.screens.charts.MonthYearPickerDialog.Companion.MIN_MONTH
import com.bugtsa.casher.ui.screens.charts.MonthYearPickerDialog.Companion.MIN_YEAR
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY
import com.bugtsa.casher.utils.getMonthName
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import toothpick.Toothpick
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChooseChartsViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            Toothpick.openScope(app).getInstance(modelClass) as T
}

class ChooseChartsViewModel @Inject constructor(chooseChartsModel: ChooseChartsModel) : RxViewModel() {

    private val dialogsRangeMonthLiveData = MutableLiveData<Pair<UiDateRange, UiDateRange>>()
    fun observeDialogsRangeMonth() = dialogsRangeMonthLiveData as LiveData<Pair<UiDateRange, UiDateRange>>

    //    private var defaultStartDate = DateRange(1, 1)
    private lateinit var startRange: DateRange

    private lateinit var endRange: DateRange
    private var sortType = SORT_MODE_DEFAULT

    init {
        chooseChartsModel.getRangeMonths()
                .map { it.map { payment -> payment.date } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    startRange = processAndCheckDate(list.first() ?: EMPTY)
                    endRange = processAndCheckDate(list.last() ?: EMPTY)
                    dialogsRangeMonthLiveData.value = UiDateRange(startRange) to UiDateRange(endRange)
                }, ErrorHandler::handle)
                .also(::addDispose)
    }

    fun setupTypeSort(sortType: Int) {
        this.sortType = sortType
    }

    fun getPreference(): ChartPreference {
        return ChartPreference(startRange, endRange, sortType)
    }

    fun setupStartRange(changedStartRange: ChangedDateRange) {
        startRange = changedStartRange.dateRange
    }

    fun setupEndRange(changedDateRange: ChangedDateRange) {
        endRange = changedDateRange.dateRange
    }

    private fun processAndCheckDate(date: String): DateRange {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            processLocalDate(date)
        } else {
            processLocalTime(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processLocalDate(date: String): DateRange {
        val formatter = DateTimeFormatter.ofPattern(SHORT_DATE_FORMAT)
        val shortFormatter = DateTimeFormatter.ofPattern(LESS_TEN_DAYS_SHORT_DATE_FORMAT)
        val fullFormatter = DateTimeFormatter.ofPattern(FULL_DATE_FORMAT)
        return when (date.length) {
            LESS_TEN_DAY_SHORT_DATE_FORMAT_LENGTH ->
                LocalDate.parse(date, formatter)
            SHORT_DATE_FORMAT_LENGTH, SHORT_DATE_FORMAT_DELIMITER_LENGTH ->
                LocalDate.parse(date, shortFormatter)
            else -> LocalDate.parse(date, fullFormatter)
        }.let { localDate ->
            DateRange(localDate.monthValue, localDate.year)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun processLocalTime(date: String): DateRange {
        val formatter = SimpleDateFormat(SHORT_DATE_FORMAT)
        val shortFormatter = SimpleDateFormat(LESS_TEN_DAYS_SHORT_DATE_FORMAT)
        val fullFormatter = SimpleDateFormat(FULL_DATE_FORMAT)

        val parseDate = when (date.length) {
            LESS_TEN_DAY_SHORT_DATE_FORMAT_LENGTH -> formatter.parse(date)
            SHORT_DATE_FORMAT_LENGTH, SHORT_DATE_FORMAT_DELIMITER_LENGTH -> shortFormatter.parse(date)
            else -> fullFormatter.parse(date)
        }

        return parseDate?.let { niceDate ->
            val cal: Calendar = Calendar.getInstance()
            cal.time = niceDate
            DateRange(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        } ?: DateRange(MIN_MONTH, MIN_YEAR)
    }

    companion object {
        private const val LESS_TEN_DAY_SHORT_DATE_FORMAT_LENGTH = 6
        private const val SHORT_DATE_FORMAT_LENGTH = 7
        private const val SHORT_DATE_FORMAT_DELIMITER_LENGTH = 8
        private const val LESS_TEN_DAYS_SHORT_DATE_FORMAT = "d.MM.yy"
        private const val SHORT_DATE_FORMAT = "dd.MM.yy"
        private const val FULL_DATE_FORMAT = "dd.MM.yy, HH:mm"

        const val SORT_ASC = 1
        const val SORT_DESC = 2
        const val SORT_UNSORTED = 0
        const val SORT_MODE_DEFAULT = 2

        fun monthCalendarValue(monthValue: Int): Int = monthValue - 1
    }
}

data class DateRange(val month: Int,
                     val year: Int)

data class ChangedDateRange(val dateRange: DateRange)

class UiDateRange(dateRange: DateRange) {

    val month: Int = monthCalendarValue(dateRange.month)
    val year: Int = dateRange.year
}

class UiStringDateRange {
    val month: String
    val year: String

    constructor(dateRange: UiDateRange) {
      month  = dateRange.month.getMonthName(Locale.getDefault(), false)
      year  = dateRange.year.toString()
    }

    constructor(default: DefaultDateRange) {
        month = default.value.month.getMonthName(Locale.getDefault(), false)
        year = default.value.year.toString()
    }
}

class DefaultDateRange {
    val value: DateRange get() {
        val cal: Calendar = Calendar.getInstance()
        return DateRange(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    }
}