package com.bugtsa.casher.utls

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SoftwareUtils {

    companion object {

        fun getCurrentDate(): Date {
            val calendar = Calendar.getInstance()
            return calendar.time
        }

        fun serverStringToDate(): Date {
            val dateFormat = SimpleDateFormat("dd.MM.yy, HH:mm")
            var convertedDate = getCurrentDate()
            try {
                convertedDate = dateFormat.parse(getCurrentDate().toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return convertedDate
        }

        fun getCurrentTimeStamp(): Long {
            val timestamp = Timestamp(System.currentTimeMillis())
            return timestamp.time / 1000
        }

        fun timeStampToString(timeStamp: Long, locale: Locale): String {
            val dateFormat = SimpleDateFormat("dd.MM.yy, HH:mm", locale)
            val date = Date(timeStamp * 1000)
            return dateFormat.format(date)
        }
    }




}