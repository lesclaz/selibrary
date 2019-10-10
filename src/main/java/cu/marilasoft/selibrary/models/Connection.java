package cu.marilasoft.selibrary.models;

import cu.marilasoft.selibrary.Utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Connection {
    private String _sessionInit;
    private String _sessionEnd;
    private String _time;
    private String _upLoad;
    private String _downLoad;
    private String _cost;

    public Connection(String sessionInit, String sessionEnd, String time, String upLoad, String downLoad, String cost) {
        this._sessionInit = sessionInit;
        this._sessionEnd = sessionEnd;
        this._time = time;
        this._upLoad = upLoad;
        this._downLoad = downLoad;
        this._cost = cost;
    }

    public Calendar sessionInit() {
        String date, time;
        int day, month, year, hours, minutes, seconds;
        // Separa la fecha de la hora
        String[] date_time = Utils.splitString(_sessionInit, " ");
        date = date_time[0];
        time = date_time[1];

        // Separa el dia, mes y annio
        String[] day_month_year = Utils.splitString(date, "/");
        day = Integer.parseInt(day_month_year[0]);
        month = Integer.parseInt(day_month_year[1]) - 1;
        year = Integer.parseInt(day_month_year[2]);

        // Separa horas, minutos y segundos
        String[] hours_minutes_seconds = Utils.splitString(time, ":");
        hours = Integer.parseInt(hours_minutes_seconds[0]);
        minutes = Integer.parseInt(hours_minutes_seconds[1]);
        seconds = Integer.parseInt(hours_minutes_seconds[2]);
        Calendar sessionInit = new GregorianCalendar();
        sessionInit.set(year, month, day, hours, minutes, seconds);

        return sessionInit;
    }

    public Calendar sessionEnd() {
        String date, time;
        int day, month, year, hours, minutes, seconds;
        // Separa la fecha de la hora
        String[] date_time = Utils.splitString(_sessionEnd, " ");
        date = date_time[0];
        time = date_time[1];

        // Separa el dia, mes y annio
        String[] day_month_year = Utils.splitString(date, "/");
        day = Integer.parseInt(day_month_year[0]);
        month = Integer.parseInt(day_month_year[1]) - 1;
        year = Integer.parseInt(day_month_year[2]);

        // Separa horas, minutos y segundos
        String[] hours_minutes_seconds = Utils.splitString(time, ":");
        hours = Integer.parseInt(hours_minutes_seconds[0]);
        minutes = Integer.parseInt(hours_minutes_seconds[1]);
        seconds = Integer.parseInt(hours_minutes_seconds[2]);
        Calendar sessionEnd = new GregorianCalendar();
        sessionEnd.set(year, month, day, hours, minutes, seconds);

        return sessionEnd;
    }

    public Calendar time() {
        // Separa horas, minutos y segundos
        String[] hours_minutes_seconds = Utils.splitString(_time, ":");
        int hours = Integer.parseInt(hours_minutes_seconds[0]);
        int minutes = Integer.parseInt(hours_minutes_seconds[1]);
        int seconds = Integer.parseInt(hours_minutes_seconds[2]);
        Calendar time = new GregorianCalendar();
        time.set(0, 0, 0, hours, minutes, seconds);

        return time;
    }

    public String upLoad() {
        return _upLoad;
    }

    public String downLoad() {
        return _downLoad;
    }

    public float cost() {
        return Float.parseFloat(_cost.replace("$", "")
                .replace(",", "."));
    }

}
