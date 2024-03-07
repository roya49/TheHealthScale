package Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    public static String getNetTime() {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'\n'HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,0);//获取当前时间。
            final String format = formatter.format(calendar.getTime());
            return format;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDate() {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,0);//获取当前时间。
            final String format = formatter.format(calendar.getTime());
            return format;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTime() {
        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,0);//获取当前时间。
            final String format = formatter.format(calendar.getTime());
            return format;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
