package Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeConverter {

    public static String DateToString(Date date)
    {
        //DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
        return df.format(date);
    }
}
