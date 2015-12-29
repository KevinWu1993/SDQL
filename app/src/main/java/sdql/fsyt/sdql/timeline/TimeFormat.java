package sdql.fsyt.sdql.timeline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KevinWu on 2015/12/22.
 */
public class TimeFormat {
    public static String format(String format, String time) {
        String result = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(time);
            SimpleDateFormat df1 = new SimpleDateFormat(format);
            result = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
