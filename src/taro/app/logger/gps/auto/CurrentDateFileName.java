package taro.app.logger.gps.auto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentDateFileName {

	public static final String FORMAT = "yyyy-MM-dd_HH-mm-ss";

	private final String mFormattedDate;
	private final String mFileName;

	public CurrentDateFileName(String prefix) {
		super();
		
		Date date = new Date();
		mFormattedDate = new SimpleDateFormat(FORMAT, Locale.JAPAN).format(date);
		mFileName = prefix + mFormattedDate + ".txt";
	}

	public String getFileName() {
		return mFileName;
	}
	
	public String getFormattedDate() {
		return mFormattedDate;
	}
}
