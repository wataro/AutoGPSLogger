package taro.app.logger.gps.auto;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

/**
 * GPSのデータを表示する。
 *
 */
public class MonitorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		/* for debug */
		Intent intent = new Intent(MonitorActivity.this, LogIntentService.class);
		intent.putExtra("filename", "taro.app.logger.gps.auto.txt");
		intent.putExtra("contents", "test");
		intent.putExtra("append", false);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}

}
