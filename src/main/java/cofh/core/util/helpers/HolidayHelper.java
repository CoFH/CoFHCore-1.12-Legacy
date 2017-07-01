package cofh.core.util.helpers;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The class contains helper functions related to Holidays! The holidays intentionally begin a day before the actual holiday and end one day after it.
 *
 * Yes, they are US-centric. Feel free to suggest others!
 *
 * @author King Lemming
 */
public class HolidayHelper {

	private HolidayHelper() {

	}

	static Calendar curTime = Calendar.getInstance();

	static Calendar holidayStart = Calendar.getInstance();
	static Calendar holidayEnd = Calendar.getInstance();

	public static boolean isNewYear() {

		setDate(holidayStart, Calendar.DECEMBER, 31, false);
		setDate(holidayEnd, Calendar.JANUARY, 2, true);
		holidayEnd.set(Calendar.YEAR, Calendar.YEAR + 1);

		return dateCheck();
	}

	public static boolean isValentinesDay() {

		setDate(holidayStart, Calendar.FEBRUARY, 13, false);
		setDate(holidayEnd, Calendar.FEBRUARY, 15, true);

		return dateCheck();
	}

	public static boolean isStPatricksDay() {

		setDate(holidayStart, Calendar.MARCH, 16, false);
		setDate(holidayEnd, Calendar.MARCH, 18, true);

		return dateCheck();
	}

	public static boolean isAprilFools() {

		setDate(holidayStart, Calendar.MARCH, 31, false);
		setDate(holidayEnd, Calendar.APRIL, 2, true);

		return dateCheck();
	}

	public static boolean isEarthDay() {

		setDate(holidayStart, Calendar.APRIL, 21, false);
		setDate(holidayEnd, Calendar.APRIL, 23, true);

		return dateCheck();
	}

	/**
	 * Compute the day of the year that Easter falls on. Step names E1 E2 etc., are direct references to Knuth, Vol 1, p 155.
	 *
	 * http://en.wikipedia.org/wiki/Computus#Meeus.2FJones.2FButcher_Gregorian_algorithm
	 */
	public static boolean isEaster() {

		Calendar easterSunCal;

		int year = Calendar.getInstance().get(Calendar.YEAR);

		if (year <= 1582) {
			return false; // The calculation is based on Gregorian calendar and it's incorrect before 1582
		}
		int golden, century, x, z, d, epact, n;

		golden = (year % 19) + 1; // metonic cycle
		century = (year / 100) + 1; // Centuries are shifted by one e.g. 1984 was in 20th C
		x = (3 * century / 4) - 12; // leap year correction
		z = ((8 * century + 5) / 25) - 5; // syncing with moon's orbit

		d = (5 * year / 4) - x - 10;
		epact = (11 * golden + 20 + z - x) % 30; /* epact */

		if ((epact == 25 && golden > 11) || epact == 24) {
			epact++;
		}
		n = 44 - epact;
		n += 30 * (n < 21 ? 1 : 0);
		n += 7 - ((d + n) % 7);

		if (n > 31) {
			easterSunCal = new GregorianCalendar(year, 4 - 1, n - 31); // if April
		} else {
			easterSunCal = new GregorianCalendar(year, 3 - 1, n); // if March
		}
		setDate(holidayStart, easterSunCal.get(Calendar.MONTH), easterSunCal.get(Calendar.DAY_OF_MONTH) - 1, false);
		setDate(holidayEnd, easterSunCal.get(Calendar.MONTH), easterSunCal.get(Calendar.DAY_OF_MONTH) + 1, true);

		return dateCheck();
	}

	public static boolean isUSIndependenceDay() {

		setDate(holidayStart, Calendar.JULY, 3, false);
		setDate(holidayEnd, Calendar.JULY, 5, true);

		return dateCheck();
	}

	public static boolean isHalloween() {

		setDate(holidayStart, Calendar.OCTOBER, 30, false);
		setDate(holidayEnd, Calendar.NOVEMBER, 1, true);

		return dateCheck();
	}

	public static boolean isVeteransDay() {

		setDate(holidayStart, Calendar.NOVEMBER, 10, false);
		setDate(holidayEnd, Calendar.NOVEMBER, 12, true);

		return dateCheck();
	}

	public static boolean isCAThanksgiving() {

		Calendar thanksgivingCal = Calendar.getInstance();
		thanksgivingCal.clear();
		thanksgivingCal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		thanksgivingCal.set(Calendar.MONTH, Calendar.OCTOBER);
		thanksgivingCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		thanksgivingCal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);

		setDate(holidayStart, Calendar.OCTOBER, thanksgivingCal.get(Calendar.DAY_OF_MONTH) - 1, false);
		setDate(holidayEnd, Calendar.OCTOBER, thanksgivingCal.get(Calendar.DAY_OF_MONTH) + 1, true);

		return dateCheck();
	}

	public static boolean isUSThanksgiving() {

		Calendar thanksgivingCal = Calendar.getInstance();
		thanksgivingCal.clear();
		thanksgivingCal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		thanksgivingCal.set(Calendar.MONTH, Calendar.NOVEMBER);
		thanksgivingCal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		thanksgivingCal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);

		setDate(holidayStart, Calendar.NOVEMBER, thanksgivingCal.get(Calendar.DAY_OF_MONTH) - 1, false);
		setDate(holidayEnd, Calendar.NOVEMBER, thanksgivingCal.get(Calendar.DAY_OF_MONTH) + 1, true);

		return dateCheck();
	}

	public static boolean isChristmas() {

		setDate(holidayStart, Calendar.DECEMBER, 24, false);
		setDate(holidayEnd, Calendar.DECEMBER, 26, true);

		return dateCheck();
	}

	public static boolean isBoxingDay() {

		setDate(holidayStart, Calendar.DECEMBER, 25, false);
		setDate(holidayEnd, Calendar.DECEMBER, 27, true);

		return dateCheck();
	}

	/* HELPER FUNCTIONS */
	static void setDate(Calendar cal, int month, int date, boolean endOfDay) {

		cal.clear();

		cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, date);

		if (endOfDay) {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
	}

	static boolean dateCheck() {

		curTime = Calendar.getInstance();
		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

}
