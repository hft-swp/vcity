package de.hft_stuttgart.swp2.opencl;

import static java.lang.Math.floor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 
 * @author 12bema1bif, 12riju1bif
 *
 */
public class SunPositionCalculator {

	private double azimut;
	private double hr;

	private double x, y, z;

	public static void main(String[] args) {
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utcCal.set(2006, Calendar.AUGUST, 6, 6, 0, 0);
		new SunPositionCalculator(utcCal.getTime(), 0, 0);
	}

	/**
	 * http://de.wikipedia.org/wiki/Julianisches_Datum#Berechnung
	 * calculates two angles to determine the position of the sun. This considers the location too.
	 * 
	 * @param d the time of which the sun position is to be calculated
	 * @param longitude 
	 * @param latitude
	 */
	public SunPositionCalculator(Date d, double longitude, double latitude) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(d.getTime());
		// System.out.println(d);

		// Julianische Tage
		double jdn = getJulianDate(cal);
		// Anzahl der Tage seit dem Standardäquinoktium J2000.0
		double n = jdn - 2451545.0;

		// mittlere ekliptikale Länge
		double l = (280.460 + 0.9856474 * n) % 360;
		// mittlere Anomalie
		double g = (357.528 + 0.9856003 * n) % 360;

		// ekliptikale Länge
		double v = l + 1.915 * sin(g) + 0.020 * sin(2 * g);
		// Schiefe der Ekliptik
		double epsilon = 23.439 - 0.0000004 * n;

		// Rektaszension
		double rektaszensionsNenner = cos(v);
		double alpha = atan((cos(epsilon) * sin(v)) / rektaszensionsNenner);
		// falls nenner < 0 bei der Rektaszensionsberechnung, addiere 180 grad
		// zum Ergebnis
		if (rektaszensionsNenner < 0) {
			alpha = alpha + 180;
		}
		// Deklination
		double delta = asin(sin(epsilon) * sin(v));

		// Stundenwinkel der Sonne
		double t0 = (n / 36525d);

		// Zeitpunkt in UTC mit Minuten als Nachkommastelle
		double t = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60d;
		// mittlere Sternzeit in Greenwich
		double mittlereSternzeit = (6.697376 + 2400.05134 * t0 + 1.002738 * t) % 24;

		// Stundenwinkel des Frühlingspunktes in Greenwich
		double stundenWinkelG = mittlereSternzeit * 15;
		// Stundenwinkel des Frühlingspunkts am Ort
		double stundenWinkelF = stundenWinkelG + longitude;
		// Stundenwinkel des orts
		double stundenWinkel = stundenWinkelF - alpha;

		// Azimut (nach Himmelsrichtungen orientierter Horizontalwinkel)
		double azimutNenner = cos(stundenWinkel) * sin(latitude) - tan(delta) * cos(latitude);
		azimut = atan(sin(stundenWinkel) / azimutNenner);
		// Falls der Nenner im Argument des Arcustangens einen Wert kleiner Null
		// hat,
		// sind 180° zum Ergebnis zu addieren, um den Winkel in den richtigen
		// Quadranten zu bringen.
		if (azimutNenner < 0) {
			azimut += 180;
		}
		// Bringe azimut in bereich von -180° bis 180°
//		if (azimut > 180d) {
//			azimut -= 360d;
//		} else if (azimut < -180d) {
//			azimut += 360d;
//		}
		// azimut soll von Noren aus gezählt werden
		azimut += 180;

		// Höhenwinkel
		double h = asin(cos(delta) * cos(stundenWinkel) * cos(latitude) + sin(delta)
				* sin(latitude));

		// Refraktion
		double r = 1.02 / tan(h + 10.3 / (h + 5.11));

		// Refraktionsbelastete Höhe
		hr = h + r / 60;

		// System.out.println("Azimut=" + azimut);
		// System.out.println("Höhe=" + hr);

		x = cos(hr) * cos(azimut) * 10000;
		y = sin(hr) * 10000;
		z = cos(hr) * sin(azimut) * 10000;
		// System.out.printf("SunPosition: x=%f, y=%f, z=%f%n", x, y, z);
	}

	/**
	 * 
	 * @return the azimut angle of the sun (horizontal)
	 */
	public double getAzimutAngle() {
		return azimut;
	}

	/**
	 * 
	 * @return the vertical angle of the sun
	 */
	public double getAltitude() {
		return hr;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	/**
	 * returns -1 if sun is beneath horizon, else the square number in which the
	 * sun is positioned.
	 * 
	 * @return value between -1 and 144
	 * @deprecated Please use {@link SunPositionCalculator#getSunPosition(int splitAzimuth, int splitHeight)}
	 */
	@Deprecated
	public int getSunPosition() {
		return getSunPosition(12, 12);
	}
	/**
	 * returns -1 if sun is beneath horizon, else the square number in which the
	 * sun is positioned.
	 * 
	 * @param splitAzimuth split the Azimuth angle in given parts
	 * @param splitHeight split the Height angle in given parts
	 * @return value between -1 and (splitAzimuth * splitHeight)
	 */
	public int getSunPosition(int splitAzimuth, int splitHeight) {
		if (hr < 0) {
			return -1;
		}
		int result = 0;
		result += (int) (hr / (90.0/splitHeight)) * splitAzimuth;
		result += (-1 * azimut + 270) / (360.0/splitAzimuth);
		return result;
	}

	private double getJulianDate(Calendar cal) {
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		int a = (int) floor((14 - month) / 12);
		int y = year + 4800 - a;
		int m = month + 12 * a - 3;

		double jdnDay = (day + floor((153 * m + 2) / 5) + 365 * y + floor(y / 4) - floor(y / 100)
				+ floor(y / 400) - 32045);
		double jdnSecond = (hour - 12) / 24d + minute / 1440d + second / 86400d;
		return jdnDay + jdnSecond;
	}

	private double sin(double g) {
		return Math.sin(Math.toRadians(g));
	}

	private double cos(double g) {
		return Math.cos(Math.toRadians(g));
	}

	private double asin(double g) {
		return Math.toDegrees(Math.asin(g));
	}

	private double tan(double g) {
		return Math.tan(Math.toRadians(g));
	}

	private double atan(double g) {
		return Math.toDegrees(Math.atan(g));
	}

}
