package taro.app.logger.gps.auto;

import android.content.Context;
import android.content.res.Resources;

/**
 * ログを保存するための情報
 * 
 *
 */
public class LogInfo {

	private static final String TAG = "LogInfo";
	
	/** Intent.putExtra(), getExtra() でファイル名を得るためのキー */
	private final String mKeyFilename;
	
	/** Intent.putExtra(), getExtra() で書き込む文字列を得るためのキー */
	private final String mKeyContents;

	/** Intent.putExtra(), getExtra() でファイルを上書き保存するか追加書き込みをするかを得るためのキー */
	private final String mKeyAppend;
	
	/** ファイル名 */
	private final String mValueFilename;

	/**
	 * コンストラクタ
	 * @param context 
	 */
	public LogInfo(Context context) {
		Resources resources = context.getResources();
		mKeyFilename = resources.getString(R.string.log_key_filename);
		mKeyContents = resources.getString(R.string.log_key_contents);
		mKeyAppend = resources.getString(R.string.log_key_append);
		mValueFilename = resources.getString(R.string.log_value_filename);
	}

	public String getKeyFilename() {
		return mKeyFilename;
	}

	public String getKeyContents() {
		return mKeyContents;
	}

	public String getKeyAppend() {
		return mKeyAppend;
	}
	
	public String getValueFilename() {
		return mValueFilename;
	}
}
