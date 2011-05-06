package fr.flafla.android.urbi.log;

/**
 * This factory allow to switch between default logger and another like android logger
 * 
 * @author merlin
 * 
 */
public final class LoggerFactory {
	private static Logger logger;

	public static Class<? extends Logger> loggerClass = Logger.class;

	public static Logger logger() {
		if (logger == null)
			try {
				logger = loggerClass.newInstance();
			} catch (IllegalAccessException e) {
				// On error create a default logger
				e.printStackTrace();
				logger = new Logger();
			} catch (InstantiationException e) {
				// On error create a default logger
				e.printStackTrace();
				logger = new Logger();
			}

		return logger;
	}
}
