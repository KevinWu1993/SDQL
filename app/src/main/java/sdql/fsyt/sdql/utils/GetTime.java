package sdql.fsyt.sdql.utils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KevinWu on 2015/10/28.
 */
public class GetTime {
    String year;
    String month;
    String day;
    int dayOfWeek;//注意0时表示周日
    public GetTime(){
        set();
    }
public void set(){
    Calendar calendar = Calendar.getInstance();
    Date date = new Date();
    calendar.setTime(date);
    dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
    DecimalFormat df = new DecimalFormat("00");
    day=df.format(calendar.get(Calendar.DAY_OF_MONTH)+1);
    month=df.format(calendar.get(Calendar.MONTH));
    year=df.format(calendar.get(Calendar.YEAR));
}

    public int getDayOfWeek(){
        return dayOfWeek;
    }
    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getDay() {
        return day;
    }
}
