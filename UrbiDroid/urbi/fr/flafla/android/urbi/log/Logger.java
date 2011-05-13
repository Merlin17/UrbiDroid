/**
 * 
 */
package fr.flafla.android.urbi.log;

/**
 * This class is the default logger
 * 
 * @author merlin
 * 
 */
public class Logger {
	public static enum Level {
		DEBUG, INFO, WARN, ERROR, NONE
	}

	protected Level level = Level.INFO;

	public void setLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	public boolean isDebug() {
		return level.ordinal() >= Level.DEBUG.ordinal();
	}

	public boolean isInfo() {
		return level.ordinal() >= Level.WARN.ordinal();
	}

	public boolean isWarn() {
		return level.ordinal() >= Level.WARN.ordinal();
	}

	public boolean isError() {
		return level.ordinal() >= Level.ERROR.ordinal();
	}

	public void d(String tag, String msg) {
		System.out.println(msg);
	}

	public void d(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void i(String tag, String msg) {
		System.out.println(msg);
	}

	public void i(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void w(String tag, String msg) {
		System.out.println(msg);
	}

	public void w(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void e(String tag, String msg) {
		System.out.println(msg);
	}

	public void e(String tag, String msg, Throwable e) {
		System.out.println(msg);
		e.printStackTrace();
	}
}
