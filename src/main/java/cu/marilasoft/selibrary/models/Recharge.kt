package cu.marilasoft.selibrary.models

import java.util.*

class Recharge(private val _date: String, private val _import: String, private val _channel: String, private val _type: String) {
    val date: Date
        get() {
            // Separa la fecha de la hora
            val dateTime = _date.split(" ")
            val date = dateTime[0]
            val time = dateTime[1]
            // Separa el dia, mes y annio
            val dayMonthYear = date.split("/")
            val day = dayMonthYear[0].toInt()
            val month = dayMonthYear[1].toInt() - 1
            val year = dayMonthYear[2].toInt()
            // Separa horas, minutos y segundos
            val hoursMinutesSeconds = time.split(":")
            val hours = hoursMinutesSeconds[0].toInt()
            val minutes = hoursMinutesSeconds[1].toInt()
            val seconds = hoursMinutesSeconds[2].toInt()
            return Date(year, month, day, hours, minutes, seconds)
        }

    val import_: Float
        get() {
            return _import.replace("$", "").replace(",", ".").toFloat()
        }

    val channel: String
        get() {
            return _channel
        }

    val type: String
        get() {
            return _type
        }

}