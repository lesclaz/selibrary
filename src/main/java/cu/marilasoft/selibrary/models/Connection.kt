package cu.marilasoft.selibrary.models

import java.util.*

class Connection(private val _sessionInit: String, private val _sessionEnd: String, private val _time: String, private val _upLoad: String, private val _downLoad: String, private val _cost: String) {
    val cost: Float
        get() {
            return _cost.replace("$", "").replace(",", ".").toFloat()
        }

    val downLoad: String
        get() {
            return _downLoad
        }

    val sessionEnd: Date
        get() {
            val dateTime = _sessionEnd.split(" ")
            val string2 = dateTime[0]
            val string3 = dateTime[1]
            val dayMonthYear = string2.split("/")
            val day: Int = dayMonthYear[0].toInt()
            val month: Int = -1 + dayMonthYear[1].toInt()
            val year: Int = dayMonthYear[2].toInt()
            val hoursMinutesSeconds = string3.split(":")
            val hours: Int = hoursMinutesSeconds[0].toInt()
            val minutes: Int = hoursMinutesSeconds[1].toInt()
            val seconds: Int = hoursMinutesSeconds[2].toInt()
            return Date(year, month, day, hours, minutes, seconds)
        }

    val sessionInit: Date
        get() {
            val dateTime = _sessionInit.split(" ")
            val string2 = dateTime[0]
            val string3 = dateTime[1]
            val dayMonthYear = string2.split("/")
            val day: Int = dayMonthYear[0].toInt()
            val month: Int = -1 + dayMonthYear[1].toInt()
            val year: Int = dayMonthYear[2].toInt()
            val hoursMinutesSeconds = string3.split(":")
            val hours: Int = hoursMinutesSeconds[0].toInt()
            val minutes: Int = hoursMinutesSeconds[1].toInt()
            val seconds: Int = hoursMinutesSeconds[2].toInt()
            return Date(year, month, day, hours, minutes, seconds)
        }

    val time: Date
        get() {
            return Date(sessionEnd.time - sessionInit.time)
        }

    val upLoad: String
        get() {
            return _upLoad
        }

}