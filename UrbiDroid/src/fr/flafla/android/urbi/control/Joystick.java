package fr.flafla.android.urbi.control;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import fr.flafla.android.urbi.R;

/**
 * Classe de description d'un joystick
 * 
 * @author merlin
 * 
 */
public class Joystick extends View implements OnTouchListener {

	private static int count = 0;
	private static List<Joystick> all;

	private Drawable joystickImg;
	private int iconSize = 100;
	private int iconMargin = 30;
	private float maxMvt = 30f;
	private int size = (int) (iconMargin * 2 + maxMvt * 2 + iconSize);

	private int internalId = ++count;

	private float x, y, mX, mY;

	public Joystick(Context context) {
		super(context);
		joystickImg = getContext().getResources().getDrawable(R.drawable.orange);
		setMinimumWidth(size);
		setMinimumHeight(size);

		setOnTouchListener(this);

		if (all == null)
			all = new ArrayList<Joystick>();
		all.add(this);

	}

	public boolean onTouch(View v, MotionEvent event) {
		// Log.i("joystick : " + this, event.toString());

		for (int id = 0; id <= event.getPointerCount(); ++id) {
			// Vérifie que l'on est dans la zone
			float xId = event.getX(id);
			float yId = event.getY(id);
			if (getLeft() <= (int) xId && (int) xId <= getRight() && getTop() <= (int) yId && (int) yId <= getBottom()) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					mX = xId - x;
					mY = y - yId;
					if (mX > maxMvt)
						mX = maxMvt;
					if (mX < -maxMvt)
						mX = -maxMvt;
					if (mY > maxMvt)
						mY = maxMvt;
					if (mY < -maxMvt)
						mY = -maxMvt;

					move();

					break;
				case MotionEvent.ACTION_POINTER_2_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_DOWN:
					// TODO création en fonction de la position sur l'écran et pas seulement du parent
					x = getLeft() + size / 2;
					y = getTop() + size / 2;
					break;
				case MotionEvent.ACTION_POINTER_2_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_UP:
					mX = 0;
					mY = 0;
					move();
					break;
				}
			}
		}

		// Notifie tous les joysticks des changements
		for (Joystick joystick : all)
			if (joystick != v && joystick != this)
				joystick.onTouch(this, event);

		return true;
	}

	private void move() {
		invalidate();
		int left = (int) (iconMargin + maxMvt - mX);
		int top = (int) (iconMargin + maxMvt - mY);
		joystickImg.setBounds(left, top, left + iconSize, top + iconSize);
		Log.i(getClass().getSimpleName(), internalId + " move to [" + mX + ", " + mY + "]");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int left = (int) (iconMargin + maxMvt - mX);
		int top = (int) (iconMargin + maxMvt - mY);
		joystickImg.setBounds(left, top, left + iconSize, top + iconSize);
		joystickImg.draw(canvas);
	}

}