package fr.flafla.android.urbi.test;

/**
 * This class is a counter used for tests
 */
public class Counter {
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