/**
 * 
 */
package fr.flafla.android.urbi.control;

/**
 * 
 * 
 * @author merlin
 * 
 */
public class Axes {
	public static class Axis {
		/** Axe min value */
		public final int minValue;
		/** Axe max value */
		public final int maxValue;

		/** Value (editable) */
		public int value = 0;

		/**
		 * Construct an axis with min and max values
		 * @param minValue
		 * @param maxValue
		 */
		public Axis(int minValue, int maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
	}

	/** X axis (nullable) */
	public final Axis x;
	/** Y axis (nullable) */
	public final Axis y;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public Axes(Axis x, Axis y) {
		this.x = x;
		this.y = y;
	}

}
