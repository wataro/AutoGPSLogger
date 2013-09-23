package taro.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import taro.app.logger.gps.auto.LogInfo;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * 発行されたときに、渡された文字列をファイルに出力するサービス
 * 
 * インテント発行時に渡されるもの
 * - ファイル名
 * - 上書きか、追記か
 * - 内容(ファイルに出力する文字列)
 * 
 * SDカードなどの外部ストレージにファイル出力するので、パーミッション使用の明記が必要。 
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */
public class LogIntentService extends IntentService {
	
	private static final String TAG = "LogIntentService";
	
	private LogInfo mLogInfo;

	public LogIntentService(String name) {
		super(name);
	}

	/**
	 * startService() のときに呼び出されるコンストラクタ。
	 * Eclipse の quick fix では作られない。
	 */
	public LogIntentService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mLogInfo = new LogInfo(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * startService() によって実行されるタスク。
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Bundle bundle = intent.getExtras();
		
		String filename = bundle.getString(mLogInfo.getKeyFilename());
		String contents = bundle.getString(mLogInfo.getKeyContents());
		boolean append = bundle.getBoolean(mLogInfo.getKeyAppend());

		String filepath = Environment.getExternalStorageDirectory() + "/" + filename;
		try {
			FileOutputStream fos = new FileOutputStream(filepath, append);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(contents);
            osw.close();
	    } catch (IOException e) {
	        // Unable to create file, likely because external storage is
	        // not currently mounted.
	        Log.w(TAG, "Error writing " + filepath, e);
	    }
		
	}

	
}
