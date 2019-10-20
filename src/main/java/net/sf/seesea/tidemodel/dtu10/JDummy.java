package net.sf.seesea.tidemodel.dtu10;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class JDummy {

	static {
		System.loadLibrary("tidemodel"); //$NON-NLS-1$
	}
	
	/** tide in cm */
	public native double getTideHeight(double lat, double lon, double time);

	
//	@Override
	public double getTideHeight(double lat, double lon, Date time) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
		calendar.setTime(time);
		double hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour += ((double)calendar.get(Calendar.MINUTE)) / 60;
		hour += ((double)calendar.get(Calendar.SECOND)) / 3600;
		
		double modifiedJulian = getModifiedJulian(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH + 1), calendar.get(Calendar.DAY_OF_MONTH), hour); 
		try {
			double tideOffset = getTideHeight(lat, lon, modifiedJulian);
			return tideOffset;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	
	private double getModifiedJulian(int year, int month, int day, double Hour)
	{	/*
			convert a normal date into Julian date (with given hour as 0-24 if minutes specified as decimal)
		*/
		double A=((year*10000)+(month*100)+day);
		int B;
		if (month < 3)
		{	month = month+12;
			year=year-1;
		}	
		if (A<15821004.1)
		{	B =((year+4716)/4)-1181;
		}
		else 	
		{	B = ((int)(year/400))-((int)(year/100))+((int)(year/4));
		}
		A = (365 * year) - 679004;
		double modifiedJulianDate = A+B+((int)(30.6001*(month+1)))+day+((Hour/24));
	return modifiedJulianDate;
	}

	
}
