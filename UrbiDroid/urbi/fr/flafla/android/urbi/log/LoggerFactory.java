package fr.flafla.android.urbi.log;

import fr.flafla.android.urbi.log.Logger.Level;

/**
 * This factory allow to switch between default logger and another like android logger
 * 
 * @author merlin
 * 
 */
public final class LoggerFactory {
	private static Logger logger;

	/** The default level */
	private static Level defaultLevel = Level.INFO;

	public static Class<? extends Logger> loggerClass = Logger.class;

	public static Logger logger() {
		if (logger == null) {
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
			logger.setLevel(defaultLevel);
		}

		return logger;
	}
}
