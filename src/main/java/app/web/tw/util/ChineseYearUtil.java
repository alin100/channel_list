package app.web.tw.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ChineseYearUtil {
	
	private static NumberFormat format2 = new DecimalFormat("00");
	private static NumberFormat format3 = new DecimalFormat("000");

	public static String getToday() {
		Calendar cal = GregorianCalendar.getInstance();
		int yyy = cal.get(Calendar.YEAR) - 1911;
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		StringBuffer str = new StringBuffer();
		str.append(format3.format(yyy)).append(format2.format(month)).append(format2.format(day));		
		return str.toString();
	}
}
