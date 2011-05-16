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
		return level.ordinal() <= Level.DEBUG.ordinal();
	}

	public boolean isInfo() {
		return level.ordinal() <= Level.WARN.ordinal();
	}

	public boolean isWarn() {
		return level.ordinal() <= Level.WARN.ordinal();
	}

	public boolean isError() {
		return level.ordinal() <= Level.ERROR.ordinal();
	}

	private void header(String tag) {
		System.out.print("[");
		System.out.print(tag);
		System.out.print("] ");
	}

	public void d(String tag, String msg) {
		if (isDebug()) {
			header(tag);
			System.out.println(msg);
		}
	}

	public void d(String tag, String msg, Throwable e) {
		if (isDebug()) {
			header(tag);
			System.out.println(msg);
			e.printStackTrace(System.out);
		}
	}

	public void i(String tag, String msg) {
		if (isInfo()) {
			header(tag);
			System.out.println(msg);
		}
	}

	public void i(String tag, String msg, Throwable e) {
		if (isInfo()) {
			header(tag);
			System.out.println(msg);
			e.printStackTrace(System.out);
		}
	}

	public void w(String tag, String msg) {
		if (isWarn()) {
			header(tag);
			System.out.println(msg);
		}
	}

	public void w(String tag, String msg, Throwable e) {
		if (isWarn()) {
			header(tag);
			System.out.println(msg);
			e.printStackTrace(System.out);
		}
	}

	public void e(String tag, String msg) {
		if (isError()) {
			header(tag);
			System.out.println(msg);
		}
	}

	public void e(String tag, String msg, Throwable e) {
		if (isError()) {
			header(tag);
			System.out.println(msg);
			e.printStackTrace(System.err);
		}
	}
}
