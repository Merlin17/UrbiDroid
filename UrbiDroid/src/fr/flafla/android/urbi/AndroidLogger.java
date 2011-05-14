package fr.flafla.android.urbi;

import android.util.Log;
import fr.flafla.android.urbi.log.Logger;

/**
 * This class is a wrapper of the android logger
 * 
 * @author merlin
 * 
 */
public class AndroidLogger extends Logger {
	public void d(String tag, String msg) {
		if (isDebug())
			Log.d(tag, msg);
	}

	public void d(String tag, String msg, Throwable e) {
		if (isDebug())
			Log.d(tag, msg, e);
	}

	public void i(String tag, String msg) {
		if (isInfo())
			Log.i(tag, msg);
	}

	public void i(String tag, String msg, Throwable e) {
		if (isInfo())
			Log.i(tag, msg, e);
	}

	public void w(String tag, String msg) {
		if (isWarn())
			Log.w(tag, msg);
	}

	public void w(String tag, String msg, Throwable e) {
		if (isWarn())
			Log.w(tag, msg, e);
	}

	public void e(String tag, String msg) {
		if (isError())
			Log.e(tag, msg);
	}

	public void e(String tag, String msg, Throwable e) {
		if (isError())
			Log.e(tag, msg, e);
	}
}
