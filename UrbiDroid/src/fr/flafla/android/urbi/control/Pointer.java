package fr.flafla.android.urbi.control;

import android.view.MotionEvent;

/**
 * Classe décrivant le mouvement
 * 
 * @author merlin
 * 
 */
public class Pointer extends Point {
	public float mX;
	public float mY;
	public int joystick;

	public Pointer() {
	}

	public Pointer(float x, float y) {
		super(x, y);
	}

	public Pointer(MotionEvent event) {
		x = event.getX();
		y = event.getY();
	}

}