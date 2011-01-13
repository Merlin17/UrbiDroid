package fr.flafla.android.urbi.control;


/**
 * Classe décrivant un point.
 * 
 * @author merlin
 */
public class Point {

	public float x;
	public float y;

	public Point() {
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float distance(float x, float y) {
		float dX = x - this.x;
		float dY = y - this.y;
		return dX * dX + dY * dY;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}

}