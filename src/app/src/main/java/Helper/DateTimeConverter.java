package Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Steffen on 23.06.2017.
 */

public class DateTimeConverter {

    public static String DateToString(Date date)
    {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String returnValue = df.format(date);
        return returnValue;
    }
}
