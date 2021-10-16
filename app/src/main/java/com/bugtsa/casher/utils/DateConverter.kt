package com.bugtsa.casher.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateConverter {

    private const val FORMAT_SIMPLE_DATE = "dd.MM.yyyy"
    private const val FORMAT_SHORT_DATE = "dd.MM.yy"
    private const val FORMAT_DATE_CREATE = "yyyy-MM-dd"
    private const val FORMAT_FULL_TIME = "HH:mm:ss"
    private const val FORMAT_SIMPLE_TIME = "HH:mm"
    private const val FORMAT_HOURS = "HH"
    private const val FORMAT_MINUTES = "mm"
    private const val FORMAT_DAY = "dd"
    private const val FORMAT_MONTH = "MM"
    private const val FORMAT_YEAR = "yyyy"
    private const val FORMAT_WORDS_DATE = "dd MMMM"

    /**
     * 10:20
     */
    fun Date.toTimeString(): String = format(FORMAT_SIMPLE_TIME, this)

    /**
     * 10:20:55
     */
    fun Date.toFullTimeString(): String = format(FORMAT_FULL_TIME, this)

    /**
     * 27.04.2020
     */
    fun Date.toSimpleDate(): String = format(FORMAT_SIMPLE_DATE, this)

    /**
     * 27.04.20
     */
    fun Date.toShortDate(): String = format(FORMAT_SHORT_DATE, this)

    /**
     * 1 января
     */
    fun Date.toWordsDate(): String = format(FORMAT_WORDS_DATE, this)

    /**
     * Преобразует дату формата 27.04.2020 10:20 в longTime
     */
    fun String.fullDateToLongTime(): Long =
        getFullDateFromString(this).time

    /**
     * Преобразует дату формата 27.04.2020T10:20:55 в longTime
     */
    fun String.dateCreateToLongTime(): Long =
        getDateCreateFromString(this).time

    /**
     * Преобразует дату формата 27.04.2020 10:20 в Date
     */
    fun String.fullDate(): Date =
        getFullDateFromString(this)

    /**
     * Преобразует дату формата 27.04.2020 10:20:55 в longTime
     */
    fun String.fullDateWithSeconds(): Long =
        getFullDateWithSeconds(this).time

    /**
     * Преобразует дату формата 10:20:55 в longTime
     */
    fun String.fullTimeToLongTime(): Long =
        getFullTimeFromString(this).time

    /**
     * Преобразует longTime в дату формата 27.04.2020 10:20
     */
    fun Long.toFullDateString(): String = Date(this).toFullDate()

    /**
     * Преобразует longTime в дату формата 27.04.20, 10:20
     */
    fun Long.toShortDateString(): String = Date(this).toFullShortYearDate()

    /**
     * Преобразует longTime в дату формата 27.04.2020 10:20:55
     */
    fun Long.toFullDateWithSecondsString(): String = Date(this).toFullDateWithSeconds()

    /**
     * Преобразует longTime в дату формата 27.04.2020
     */
    fun Long.toSimpleDateString(): String = Date(this).toSimpleDate()

    /**
     * Преобразует longTime в дату формата 10:20
     */
    fun Long.toTimeString(): String = Date(this).toTimeString()

    /**
     * Преобразует longTime в дату формата 10:20:55
     */
    fun Long.secondsToFullTime(): String {
        val s = this % 60
        val m = this / 60 % 60
        val h = this / (60 * 60) % 24
        return String.format("%d:%02d:%02d", h, m, s)
    }

    /**
     * Преобразует longTime в дату формата 10, где 10 это значение часов
     */
    fun Long.toHoursValue(): Int = format(FORMAT_HOURS, Date(this)).toInt()

    /**
     * Преобразует longTime в дату формата 20, где 20 это значение минут
     */
    fun Long.toMinutesValue(): Int = format(FORMAT_MINUTES, Date(this)).toInt()

    fun Long.toDayValue(): Int = format(FORMAT_DAY, Date(this)).toInt()

    fun Long.toMonthValue(): Int = format(FORMAT_MONTH, Date(this)).toInt()

    fun Long.toYearValue(): Int = format(FORMAT_YEAR, Date(this)).toInt()

    /**
     * Устанавливает для текущей [Date] даты новое время
     * Дату формата 27.04.2020 10:20 с hours 22 и minutes 55 преобразует
     * в дату формата 27.04.2020 22:55
     *
     * @param hours часы
     * @param minutes минуты
     * @return текущая дата с новым значение часов и минут
     */
    fun Date.setupNewTime(hours: Int, minutes: Int): Date =
        "${this.toSimpleDate()} ${hours}:${minutes}".fullDate()

    /**
     * 1 января
     */
    fun Long.toWordsDate(): String = format(FORMAT_WORDS_DATE, Date(this))



    private const val TIME_DELIMITER = "-"
    private const val STRING_DEFAULT = ""

    /**
     * 27.04.2020 10:20
     */
    private fun Date.toFullDate(): String = format("$FORMAT_SIMPLE_DATE $FORMAT_SIMPLE_TIME", this)

    /**
     * 27.04.20, 10:20
     */
    private fun Date.toFullShortYearDate(): String = format("$FORMAT_SHORT_DATE, $FORMAT_SIMPLE_TIME", this)

    /**
     * 27.04.2020 10:20:55
     */
    private fun Date.toFullDateWithSeconds(): String = format("$FORMAT_SIMPLE_DATE $FORMAT_FULL_TIME", this)

    private fun getFullDateFromString(stringDate: String?): Date = runCatching {
        SimpleDateFormat(
            "$FORMAT_SIMPLE_DATE $FORMAT_SIMPLE_TIME",
            Locale.forLanguageTag(Constants.DEFAULT_LOCALE)
        ).parse(stringDate ?: "")
    }.getOrElse {
        Date()
    }

    private fun getDateCreateFromString(stringDate: String?): Date =
        getFullDateWithSeconds(stringDate?.replace("T", " "))

    private fun getFullDateWithSeconds(stringDate: String?): Date = runCatching {
        SimpleDateFormat(
            "$FORMAT_DATE_CREATE $FORMAT_FULL_TIME",
            Locale.forLanguageTag(Constants.DEFAULT_LOCALE)
        ).parse(stringDate ?: "")
    }.getOrElse {
        Date()
    }

    private fun getFullTimeFromString(stringDate: String?): Date = runCatching {
        SimpleDateFormat(
            FORMAT_FULL_TIME,
            Locale.forLanguageTag(Constants.DEFAULT_LOCALE)
        ).parse(stringDate ?: "")
    }.getOrElse {
        Date()
    }

    private fun format(pattern: String, date: Date) =
        SimpleDateFormat(pattern, Locale.forLanguageTag(Constants.DEFAULT_LOCALE)).format(date)
}
