package taro.app.logger.gps.auto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDateFileName {

	private final String mFileName;

	public CurrentDateFileName() {
		super();
		
		Date date = new Date();
		String format = "yyyy-MM-dd_HH-mm-ss";
		mFileName = new SimpleDateFormat(format).format(date) + ".txt";
	}

	public String getFileName() {
		return mFileName;
	}
}
