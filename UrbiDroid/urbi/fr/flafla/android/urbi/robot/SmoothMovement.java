package fr.flafla.android.urbi.robot;

import static fr.flafla.android.urbi.log.LoggerFactory.logger;
import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;

import java.lang.Thread.State;

import fr.flafla.android.urbi.log.LoggerFactory;

/**
 * This class is cooked to perform less request to a robot. <br/>
 * An order is launched only at thread top
 * 
 * @author merlin
 * 
 */
public abstract class SmoothMovement {
	/** This thread iterate and call movement abstract method */
	private Thread thread = new Thread() {
		@Override
		public void run() {
			while (!interrupted) {
				movement();
				try {
					Thread.sleep(timeBetweenOrder);
				} catch (InterruptedException e) {
					// Just log error
					LoggerFactory.logger().e("SmoothMovement", "The smooth movement will not work correctly due to a thread error", e);
				}
			}
		}
	};

	/** The time between 2 order */
	private int timeBetweenOrder;
	/** Indicator of interruption */
	private boolean interrupted = true;

	/**
	 * Constructor
	 * @param timeBetweenOrder The time between 2 order
	 */
	public SmoothMovement(int timeBetweenOrder) {
		this.timeBetweenOrder = timeBetweenOrder;
	}

	/**
	 * Launch the thread
	 */
	public void launchSmooth() {
		if (interrupted) {
			interrupted = false;
			State state = thread.getState();
			if (state == RUNNABLE || state == NEW)
				thread.start();
			else
				logger().w("SmoothMovement", "The thread is not runnable");
		}
	}

	/**
	 * Stop the thread
	 */
	public void stopSmooth() {
		interrupted = true;
	}

	/**
	 * This method has to be implemented to perform the movement
	 */
	protected abstract void movement();
}
