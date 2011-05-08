/**
 * 
 */
package fr.flafla.android.urbi.test;

import junit.framework.Assert;

import org.junit.Test;

import fr.flafla.android.urbi.robot.SmoothMovement;

/**
 * This test class testt the {@link SmoothMovement} class and assert that the behavior is conform.
 * 
 * @author merlin
 * 
 */
public class SmoothMovementTest {
	/**
	 * This class is a counter used for tests
	 */
	static final class Counter {
		private int value = 0;

		public final void reset() {
			value = 0;
		}

		public final synchronized void inc() {
			value++;
		}

		public final synchronized int value() {
			return value;
		}
	}

	/**
	 * This method test the {@link SmoothMovement} class
	 * @throws InterruptedException
	 */
	@Test
	public void testMovement() throws InterruptedException {
		final Counter count = new Counter();
		SmoothMovement mvt = new SmoothMovement(500) {
			@Override
			public void movement() {
				System.out.println("movement");
				count.inc();
			}
		};
		mvt.launchSmooth();

		// Test on 3 times if the value is correct
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(i, count.value());
			Thread.sleep(400);
		}
		mvt.stopSmooth();

		Assert.assertEquals(3, count.value());

		// Test if the thread is correctly stopped
		Thread.sleep(1000);
		Assert.assertEquals(3, count.value());
	}
}
